package com.timepieces.automation.pages.components;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.timepieces.automation.utils.ConfigReader;

import lombok.extern.log4j.Log4j2;

/**
 * Clears the site-wide overlays that sit on top of every page and silently break tests:
 * the cookie consent bar and the Astonia drop-card promo popup.
 *
 * <h2>Why this exists — measured, not guessed</h2>
 * The cookie bar is a real modal:
 *
 * <pre>
 * &lt;div role="dialog" aria-modal="true" aria-labelledby="cookie-consent-title"
 *      class="... fixed inset-x-0 bottom-0 z-[9999] pointer-events-none"&gt;
 * </pre>
 *
 * The wrapper is {@code pointer-events:none}, but its inner card is
 * {@code pointer-events:auto} at {@code z-index:9999}, and it occupies roughly
 * <b>y=896..986</b> in the framework's 1920x1080 window. Every
 * {@code ElementClickInterceptedException} in the 2026-07-17 run reported a click point
 * inside that band — e.g. <b>(740, 929)</b>. It was not the drop card and not the app: it
 * was the consent bar eating clicks on anything near the bottom of the viewport.
 *
 * <h2>How the choice persists (verified against production)</h2>
 * Clicking "Reject Cookies" sets:
 * <ul>
 *   <li>cookie {@code timepieces_cookie_consent=rejected}</li>
 *   <li>localStorage {@code timepieces_consent_preferences} =
 *       {@code {"analytics_storage":false,"ad_storage":false,...}}</li>
 * </ul>
 * and the bar does not come back after a reload. So this costs ONE click on the first
 * page of a scenario; every later navigation finds nothing and no-ops in milliseconds.
 *
 * <h2>Reject, not accept</h2>
 * Rejecting is the default because it is the choice that keeps the run closest to a clean
 * room: no analytics_storage, no ad_storage, no marketing tags firing during a test. It
 * also means the suite is not silently feeding synthetic traffic into the site's
 * analytics. Override with {@code -Dcookie.consent.mode=accept} if a scenario genuinely
 * needs the accepted state, or {@code ignore} to leave the bar alone.
 *
 * <p>All methods are static and take the driver explicitly, matching the framework's
 * stateless-utility convention (see ConfigReader) so they are safe in parallel runs.
 *
 * @since 2026-07-17
 * @version 1.0
 * @author Avinash Rath
 */
@Log4j2
public final class SiteOverlayHandler {

    /** The consent bar's wrapper. aria-labelledby is the stable hook - it has no testid. */
    private static final By COOKIE_DIALOG =
            By.cssSelector("div[role='dialog'][aria-labelledby='cookie-consent-title']");

    /**
     * Note: the site renders the consent buttons TWICE (a desktop row and a mobile
     * column); only one is displayed at a time, so these must always be resolved through
     * {@link #firstDisplayed} rather than findElement's blind first-match.
     */
    private static final By REJECT_COOKIES = By.xpath("//button[normalize-space()='Reject Cookies']");
    private static final By ACCEPT_COOKIES = By.xpath("//button[normalize-space()='Accept Cookies']");

    /** The Astonia drop promo. Confirmed testids; may or may not render on a given visit. */
    private static final By DROP_CARD = By.cssSelector("div[data-testid='astonia-popup']");
    private static final By DROP_CARD_CLOSE = By.cssSelector("button[data-testid='astonia-popup-close']");

    /**
     * Deliberately short. These overlays render with the page; waiting 15s for one that
     * is not coming would add 15s to EVERY navigation in the suite.
     */
    private static final Duration OVERLAY_POLL = Duration.ofMillis(1200);

    private SiteOverlayHandler() {
        // Static utility.
    }

    /**
     * Clears any blocking overlay on the current page. Safe to call after every
     * navigation: it never throws, and it no-ops in milliseconds once consent is stored.
     */
    public static void dismissBlockingOverlays(WebDriver driver) {
        if (driver == null) {
            return;
        }
        handleCookieConsent(driver);
        dismissDropCard(driver);
    }

    /**
     * Answers the consent bar according to {@code cookie.consent.mode}
     * (reject | accept | ignore; default reject).
     */
    private static void handleCookieConsent(WebDriver driver) {
        String mode = ConfigReader.get("cookie.consent.mode");
        mode = (mode == null || mode.isBlank()) ? "reject" : mode.trim().toLowerCase();

        if ("ignore".equals(mode)) {
            return;
        }
        if (!isPresent(driver, COOKIE_DIALOG)) {
            return; // Already answered this session, or not shown on this page.
        }

        By target = "accept".equals(mode) ? ACCEPT_COOKIES : REJECT_COOKIES;
        WebElement button = firstDisplayed(driver, target);
        if (button == null) {
            // The bar is up but neither button is visible (mid-animation, or the markup
            // moved). Fall back to hiding it so it cannot intercept clicks, and say so.
            log.warn("Cookie consent bar is present but no visible '{}' button was found; "
                    + "neutralising it via JS so it cannot intercept clicks.", mode);
            neutraliseCookieBar(driver);
            return;
        }
        try {
            button.click();
            log.debug("Cookie consent: chose '{}'. This persists for the session "
                    + "(cookie timepieces_cookie_consent).", mode);
        } catch (Exception e) {
            log.warn("Could not click the '{}' consent button ({}); neutralising the bar instead.",
                    mode, e.getClass().getSimpleName());
            neutraliseCookieBar(driver);
        }
    }

    /**
     * Last-resort: removes the consent bar from the DOM.
     *
     * <p>Only used when the bar is up but unclickable. Hiding a real overlay is normally
     * the wrong instinct — it papers over a genuine app problem — so it is deliberately
     * NOT the primary path and it logs a warning when it fires.
     */
    private static void neutraliseCookieBar(WebDriver driver) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "var d=document.querySelector(\"div[role='dialog'][aria-labelledby='cookie-consent-title']\");"
                            + "if(d){d.style.display='none';d.style.pointerEvents='none';}");
        } catch (Exception e) {
            log.debug("JS neutralisation of the cookie bar failed: {}", e.getMessage());
        }
    }

    /**
     * Closes the Astonia drop-card promo if it is showing.
     *
     * <p>The popup is throttled by the app via localStorage
     * ({@code astonia_popup_visited} / {@code astonia_popup_show_count}), so it appears on
     * some visits and not others — which is exactly what makes it such an unpleasant
     * source of intermittent failures. Dismissing unconditionally is the point.
     */
    private static void dismissDropCard(WebDriver driver) {
        if (!isPresent(driver, DROP_CARD)) {
            return;
        }
        WebElement close = firstDisplayed(driver, DROP_CARD_CLOSE);
        if (close == null) {
            return;
        }
        try {
            close.click();
            log.debug("Dismissed the Astonia drop-card popup.");
        } catch (Exception e) {
            log.debug("Drop-card close click failed ({}); continuing.", e.getClass().getSimpleName());
        }
    }

    /** Cheap presence check that never throws and never waits the global implicit timeout. */
    private static boolean isPresent(WebDriver driver, By locator) {
        try {
            driver.manage().timeouts().implicitlyWait(OVERLAY_POLL);
            return !driver.findElements(locator).isEmpty();
        } catch (Exception e) {
            return false;
        } finally {
            restoreImplicitWait(driver);
        }
    }

    /**
     * Returns the first DISPLAYED match, or null.
     *
     * <p>This matters more than it looks. The site renders responsive duplicates (a mobile
     * node and a desktop node) for the consent buttons, the header stories toggle, and
     * others. Selenium's {@code findElement} returns the first in DOM order, which is
     * frequently the HIDDEN one — producing "element not visible" / "not interactable"
     * failures against a page that is working perfectly.
     */
    public static WebElement firstDisplayed(WebDriver driver, By locator) {
        try {
            List<WebElement> candidates = driver.findElements(locator);
            for (WebElement candidate : candidates) {
                try {
                    if (candidate.isDisplayed()) {
                        return candidate;
                    }
                } catch (Exception ignored) {
                    // Stale between find and check: skip it rather than fail the caller.
                }
            }
        } catch (Exception e) {
            log.debug("firstDisplayed({}) failed: {}", locator, e.getMessage());
        }
        return null;
    }

    /** Puts the framework's configured implicit wait back after a short-poll lookup. */
    private static void restoreImplicitWait(WebDriver driver) {
        try {
            String configured = ConfigReader.get("timeout.implicit");
            long seconds = (configured == null || configured.isBlank()) ? 10 : Long.parseLong(configured.trim());
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
        } catch (Exception e) {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
    }
}
