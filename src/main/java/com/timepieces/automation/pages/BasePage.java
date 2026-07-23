package com.timepieces.automation.pages;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.timepieces.automation.pages.components.SiteOverlayHandler;
import com.timepieces.automation.utils.ConfigReader;

import lombok.extern.log4j.Log4j2;

/**
 * BasePage — Abstract foundation for all Page Object Model (POM) classes.
 *
 * Every page class in the framework MUST extend this class.
 *
 * RESPONSIBILITIES: 1. Injects the WebDriver instance into the page so all page
 * objects use the same ThreadLocal-safe driver instance from DriverFactory. 2.
 * Initialises a shared WebDriverWait that all page methods inherit. Timeout is
 * read from config (explicit.wait.timeout), defaulting to 10s, so it can be
 * overridden per environment without touching code. 3. Runs
 * By Plain Objects,
 * e.g. private final By webElementName = By.cssSelector("button[id='clear']");
 *
 * WHAT DOES NOT BELONG HERE: - Business logic (belongs in individual page
 * objects) - Assertions (belong in step definitions or test classes) - Driver
 * initialisation or teardown (belongs in DriverFactory + Hooks)
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (Appropriate Page
 * Object) to allow method chaining in step definitions.
 *
 * GENERIC SELF-TYPE (CRTP):
 * The class is parameterised with a recursive self-type {@code SELF} — the
 * "Curiously Recurring Template Pattern". Every concrete page declares itself as
 * the type argument (e.g. {@code class UpdatesPage extends StoreFrontBasePage<UpdatesPage>}).
 * This lets inherited fluent methods such as {@link #open()} return the CONCRETE
 * page type instead of a bare {@code BasePage}, so chains like
 * {@code new UpdatesPage(driver).open().verifyUpdatesLoaded()} compile WITHOUT
 * every subclass having to override {@code open()} purely to narrow the return type.
 *
 * @param <SELF> the concrete page/component subtype, used for fluent return typing
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of BasePage"
 */

@Log4j2
public abstract class BasePage<SELF extends BasePage<SELF>> {

    // ==========================================
    // GLOBAL VARIABLES
    // ==========================================
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    // ==========================================
    // CONSTRUCTORS
    // ==========================================
    /**
     * Constructs a BasePage, wiring up the driver, the explicit wait, and
     * PageFactory element proxies for the concrete subclass.
     *
     * @param driver The WebDriver instance managed by DriverFactory (ThreadLocal).
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;

        // Read explicit wait timeout from config; fall back to 10 seconds if unset.
        // This allows per-environment tuning via staging.properties / prod.properties.
        int timeoutSeconds = Optional.ofNullable(ConfigReader.get("explicit.wait.timeout"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .orElse(10);

        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

        log.debug("Initialised page object: [{}] with explicit wait timeout: [{}s]", this.getClass().getSimpleName(),
                timeoutSeconds);
    }

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * P.1 Provides clean structural support for dynamic locators (e.g.: selecting
     * collections dynamically) Accepts a locator template with placeholders and
     * a replacement value to generate a specific locator instance at runtime,
     * enabling flexible and reusable locator strategies for elements that share
     * common patterns but differ in specific identifiers (e.g., product names,
     * user IDs).
     *
     * @param xPathTemplate    The By locator template containing placeholders for
     *                         dynamic values.
     * @param replacementValue The value to replace the placeholders in the
     *                         locator template.
     * @return A By locator instance with the placeholders replaced by the
     *         specified value.
     */
    protected By formatLocator(By xPathTemplate, String replacementValue) {
        return By.xpath(String.format(xPathTemplate.toString().replace("By.xpath: ", ""), replacementValue));
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Missing #1: presence (not visibility) — needed for hidden iframes like
     * Cloudflare
     *
     * @param locator The By locator used to identify the target element on the
     *                page.
     * @return The WebElement that matches the locator.
     */
    protected WebElement getElementPresence(By locator) {
        log.debug("Awaiting DOM presence (not visibility) for locator: [{}]", locator);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * B.2 Missing #2: pure "wait for visible" without returning the element —
     * used as a navigation confirmation guard (e.g. after clickSendOtpButton)
     *
     * @param locator The By locator used to identify the target element on the
     *                page.
     */
    protected void waitForVisibility(By locator) {
        log.debug("Awaiting visibility confirmation for locator: [{}]", locator);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * B.3 Missing #3 (optional but clean): wait for invisibility/disappearance —
     * you'll need this soon for loading spinners
     *
     * @param locator The By locator used to identify the target element on the
     *                page.
     */
    protected void waitForInvisibility(By locator) {
        log.debug("Awaiting invisibility/disappearance of locator: [{}]", locator);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * B.4 Synchronizes and isolates element retrieval using explicit visibility
     * constraints. Guardrails agains lazy loading elements on heavy animated
     * matrices and ensures that interactions are performed on elements that are
     * present and visible in the DOM.
     *
     * @param locator The By locator used to identify the target element on the
     *                page.
     * @return The WebElement that matches the locator and is visible on the
     *         page.
     */
    protected WebElement getElement(By locator) {
        log.debug("Synchronizing element visibility tracking for locator target: [{}]", locator);
        // visibilityOfANYElementsLocated, deliberately — NOT visibilityOfElementLocated.
        //
        // The Timepieces storefront renders RESPONSIVE DUPLICATES: the same logical control
        // exists twice in the DOM, once for mobile and once for desktop, and CSS hides
        // whichever does not apply. visibilityOfElementLocated waits for the FIRST match
        // in document order to become visible — and that is very often the hidden mobile
        // node, so it waits the full timeout and then throws, against a page that is
        // working perfectly.
        //
        // Measured on the production accessory PDP (2026-07-17): the gallery selector
        // matched product-media-carousel (mobile, hidden) at document position ~31,940
        // BEFORE desktop-image-gallery (visible) at ~36,688. Every one of the five
        // accessory PDP smoke tests failed "image gallery should be displayed" for that
        // reason alone. Same shape for header-stories-toggle (2 nodes, first hidden) and
        // the cookie consent buttons.
        //
        // Taking the first VISIBLE match is both correct and faster: no timeout burned
        // waiting on a node CSS will never show.
        return waitForFirstVisible(locator);
    }

    /**
     * B.4a Waits until at least one element matching {@code locator} is VISIBLE and
     * returns it.
     *
     * <p>Selenium ships no "any of these is visible" condition — {@code
     * visibilityOfElementLocated} only ever considers the first match, and {@code
     * visibilityOfAllElementsLocatedBy} demands every match be visible, which is exactly
     * wrong when the page deliberately hides the mobile or desktop twin. So the condition
     * is spelled out here.
     *
     * <p>Returning {@code null} from the lambda is not a bug: {@link WebDriverWait} treats
     * null/false as "not ready yet" and keeps polling until the timeout.
     *
     * @param locator the By locator, which may legitimately match several nodes
     * @return the first visible matching element
     */
    protected WebElement waitForFirstVisible(By locator) {
        return wait.until(driver -> {
            WebElement offScreenFallback = null;
            for (WebElement candidate : driver.findElements(locator)) {
                try {
                    if (!candidate.isDisplayed()) {
                        continue;
                    }
                    // PREFER a candidate that is actually within the page's horizontal
                    // bounds. isDisplayed() only asks about CSS visibility and box size —
                    // it does NOT ask whether the element is anywhere near the viewport.
                    // This storefront renders carousels twice (desktop + mobile tracks),
                    // and a card parked far off to the right in the inactive track still
                    // reports displayed=true. Picking it produces the puzzling
                    // "not clickable at point (2324, 492)" on a 1904px window.
                    if (isWithinHorizontalBounds(candidate)) {
                        return candidate;
                    }
                    // Remember it, but keep looking for a better one.
                    if (offScreenFallback == null) {
                        offScreenFallback = candidate;
                    }
                } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                    // Re-rendered between find and check (React does this constantly).
                    // Skip it; the next poll re-finds a fresh reference.
                }
            }
            // Fall back to an off-screen-but-displayed match rather than failing outright:
            // scrollIntoView can often still bring it in, and a wrong-looking element beats
            // a timeout when no better candidate exists.
            return offScreenFallback;
        });
    }

    /**
     * Is the element within the document's horizontal bounds, i.e. not parked far off to
     * the side in an inactive carousel track?
     *
     * <p>Deliberately generous — it only rejects elements clearly outside the page width,
     * so a normally-positioned element below the fold is never rejected. Returns true on
     * any error: this is a preference, not a gate, and it must never be the reason a valid
     * element is skipped.
     */
    private boolean isWithinHorizontalBounds(WebElement element) {
        try {
            Object result = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "var r = arguments[0].getBoundingClientRect();"
                            + "return r.left < window.innerWidth && r.right > 0;",
                    element);
            return !Boolean.FALSE.equals(result);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * B.5 Synchronizes and isolates multiple elements retrieval using explicit
     * visibility constraints. Retrieves a list of elements based on the
     * provided locator. Ensures that all matching elements are visible and
     * interactable before returning them. Useful for scenarios where multiple
     * elements share the same locator, such as lists or tables, and allows for
     * batch operations on those elements.
     *
     * @param locator The By locator used to identify the target elements on the
     *                page.
     * @return A List of WebElement objects that match the locator and are
     *         visible on the page.
     */
    protected List<WebElement> getElements(By locator) {
        log.debug("Synchronizing multiple element visibility tracking for locator target: [{}]", locator);
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    /**
     * B.6 Centralized click wrapper with proactive clickability verification.
     * Ensures that the element is not only visible but also interactable (i.e.,
     * enabled and not obscured) before performing the click action, reducing
     * the likelihood of ElementNotInteractableException and improving test
     * stability.
     *
     * @param locator The By locator used to identify the target element on the
     *                page.
     */
    protected void clickElement(By locator) {
        log.info("Executing safe click transaction on target element: [{}]", locator);
        try {
            // Resolve the VISIBLE twin first (see getElement/waitForFirstVisible for why
            // the first DOM match is so often the hidden responsive duplicate), then wait
            // for that specific element to be clickable. Passing the By straight to
            // elementToBeClickable would re-introduce the first-match trap and time out
            // on a node CSS will never show.
            WebElement visible = waitForFirstVisible(locator);

            // SCROLL THE TARGET TO THE MIDDLE OF THE VIEWPORT BEFORE CLICKING.
            //
            // The storefront pins a cookie-consent bar to the BOTTOM of the window
            // (z-index 9999, roughly y=896..986 at 1920x1080). Anything the test clicks
            // low on the page — footer links, the guarantee/returns tiles, the cart
            // drawer's checkout button — lands underneath it and Selenium reports
            // ElementClickInterceptedException. Six such failures in the 2026-07-23 run
            // were all in that band; the partner-profile one was a footer link at
            // y=974 intercepted by the bar's inner card.
            //
            // SiteOverlayHandler already dismisses that bar on open(), but a page reached
            // by CLICKING a link never goes through open(), and the bar can also re-render
            // as React hydrates. Centring the element removes the dependency on the bar
            // being gone at all: a centred element cannot be under a bottom-pinned overlay.
            //
            // This is also just how a human clicks — you scroll to a control before
            // pressing it — so it makes the automation more faithful, not less.
            scrollElementToCentre(visible);

            try {
                wait.until(ExpectedConditions.elementToBeClickable(visible)).click();
            } catch (org.openqa.selenium.ElementClickInterceptedException intercepted) {
                // LAST RESORT, and it announces itself.
                //
                // Some carousels here use scroll-snap: we scroll the card to the centre,
                // the track snaps back while elementToBeClickable is still waiting, and the
                // click lands on whatever is now in that spot. Retrying a native click is
                // the honest first move; only if that fails too do we dispatch the click
                // via JS, which ignores what is painted on top.
                //
                // Deliberately WARN rather than debug: a JS click bypasses the very check
                // that catches real blocking-overlay bugs, so if this starts appearing in
                // the logs for a control that ought to be plainly clickable, that is a
                // finding — not something to be quietly grateful for.
                log.warn("Native click on [{}] was intercepted; re-scrolling and retrying.", locator);
                scrollElementToCentre(visible);
                try {
                    visible.click();
                } catch (org.openqa.selenium.ElementClickInterceptedException stillIntercepted) {
                    log.warn("Still intercepted after re-scroll; falling back to a JS click on [{}]. "
                            + "If this element is not inside a snap-scrolling carousel, investigate "
                            + "what is covering it rather than accepting this.", locator);
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", visible);
                }
            }
        } catch (Exception e) {
            log.error("Click transaction aborted. Element was un-interactable: [{}]", locator, e);
            throw e;
        }
    }

    /**
     * B.15 Hovers the mouse over an element, then clicks it.
     *
     * <p>For controls that only EXIST visually on hover — the Tailwind
     * {@code group}/{@code group-hover:opacity-100} pattern this storefront uses for its
     * carousel arrows. Such a button is present in the DOM but reports
     * {@code isDisplayed() == false} (or zero opacity) until the pointer is over its
     * container, so a plain click waits the full timeout and then fails with "element not
     * visible" — which reads like a broken locator when the locator is perfectly correct.
     *
     * <p>Hovering first is also simply what a user does: the arrows are meant to appear
     * when you move onto the carousel.
     *
     * @param hoverTarget the container that reveals the control (e.g. the carousel)
     * @param clickTarget the control to click once it appears
     * @return this page instance, typed as the concrete subclass, for chaining
     */
    @SuppressWarnings("unchecked")
    protected SELF hoverAndClick(By hoverTarget, By clickTarget) {
        try {
            WebElement hover = getElementPresence(hoverTarget);
            scrollElementToCentre(hover);
            new org.openqa.selenium.interactions.Actions(driver).moveToElement(hover).perform();
        } catch (Exception e) {
            log.debug("Could not hover [{}] before clicking [{}]: {}", hoverTarget, clickTarget, e.getMessage());
        }
        clickElement(clickTarget);
        return (SELF) this;
    }

    /**
     * Centres an already-resolved element in the viewport. Never throws — a failed scroll
     * must not fail the click that follows, because the click may well have worked anyway.
     */
    private void scrollElementToCentre(WebElement element) {
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", element);
        } catch (Exception e) {
            log.debug("Could not centre the element before clicking: {}", e.getMessage());
        }
    }

    /**
     * B.7 Centralized input wrapper. Flushes the existing text caches before
     * injecting new input values to ensure clean state and prevent input
     * anomalies.
     *
     * @param locator The By locator used to identify the target element on the
     *                page.
     * @param value   The text value to be injected into the target element.
     */
    protected void sendKeys(By locator, String value) {
        if (value == null) {
            log.warn("Skipping text injection for null value on target element: [{}]", locator);
            return;
        }
        log.info("Injecting text value into target element: [{}]: [****]", locator);
        try {
            WebElement element = getElement(locator);
            element.clear();
            element.sendKeys(value);
        } catch (Exception e) {
            log.error("Text injection transaction failed for target element: [{}]", locator, e);
            throw e;
        }
    }

    /**
     * B.8 Safely pulls inner text generator from validated DOM nodes. Guards
     * against stale element references and ensures that the text content is
     * accurately captured from the current state of the page.
     *
     * @param locator The By locator used to identify the target element on the
     *                page.
     * @return The text content of the target element.
     */
    protected String getText(By locator) {
        log.debug("Extracting text content from target element: [{}]", locator);
        try {
            return getElement(locator).getText().trim();
        } catch (Exception e) {
            log.error("Text extraction failed for target element: [{}]", locator, e);
            throw e;
        }
    }

    /**
     * B.9 Centralized method for retrieving the attribute value of a target
     * element.
     *
     * @param locator       The By locator used to identify the target element the
     *                      page.
     * @param attributeName The name of the attribute to retrieve.
     * @return The value of the specified attribute.
     */
    protected String getAttribute(By locator, String attributeName) {
        log.debug("Extracting attribute [{}] from target element: [{}]", attributeName, locator);
        try {
            return getElement(locator).getAttribute(attributeName);
        } catch (Exception e) {
            log.error("Attribute extraction failed for target element: [{}]", locator, e);
            throw e;
        }
    }

    /**
     * B.10 Returns the base url for navigation.
     * Defaults to the base.url property from configuration, but can be
     * overridden in child classes if needed.
     * 
     * @return The base url of the page
     */
    public String getBaseUrl() {
        return ConfigReader.get("base.url");
    }

    /**
     * B.11 Returns the page path (appended to the base URL by {@link #open()}).
     * <p>
     * Non-abstract on purpose: defaults to an empty string so components and
     * scaffold pages that have no dedicated endpoint compile without ceremony.
     * Pages that map to a specific endpoint override this to return their
     * {@code PAGE_PATH} constant.
     *
     * @return The path of the page (empty by default)
     */
    protected String getPagePath() {
        return "";
    }

    /**
     * B.12 Returns the page title for assertions.
     * <p>
     * Non-abstract on purpose (see {@link #getPagePath()}): defaults to an empty
     * string; pages with a known title override this to return their
     * {@code PAGE_TITLE} constant.
     *
     * @return The title of the page (empty by default)
     */
    protected String getPageTitle() {
        return "";
    }

    /**
     * B.13 Opens the page by navigating to {@code baseUrl + pagePath}.
     * <p>
     * Returns the concrete subtype ({@code SELF}) rather than {@code BasePage},
     * so callers can keep chaining page-specific methods without any subclass
     * having to override this method just to fix the return type. The cast is
     * safe by the CRTP contract: every subclass binds {@code SELF} to itself.
     *
     * @return this page instance, typed as the concrete subclass, for chaining
     */
    @SuppressWarnings("unchecked")
    public SELF open() {
        driver.get(getBaseUrl() + getPagePath());
        // Clear the site-wide overlays before handing control back. The cookie consent
        // bar is a real modal pinned to the bottom of the viewport at z-index 9999, and
        // it intercepted clicks in the 2026-07-17 run; the Astonia drop card does the
        // same intermittently. Doing this here — the single navigation funnel every page
        // object goes through — means no test has to remember to. It costs one click on
        // the first page of a scenario and then no-ops, because the site persists the
        // choice in the timepieces_cookie_consent cookie. See SiteOverlayHandler.
        SiteOverlayHandler.dismissBlockingOverlays(driver);
        return (SELF) this;
    }

    /**
     * B.13a Clears the cookie bar / drop card on the CURRENT page.
     *
     * <p>{@link #open()} already does this. Call it directly only when a page was reached
     * without {@code open()} — a link click, a redirect, or a browser back — and an
     * overlay may have re-rendered.
     *
     * @return this page instance, typed as the concrete subclass, for chaining
     */
    @SuppressWarnings("unchecked")
    public SELF dismissOverlays() {
        SiteOverlayHandler.dismissBlockingOverlays(driver);
        return (SELF) this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Checks if the current browser window is sized as a mobile viewport.
     * 
     * @return true if the window width is less than 768 pixels, false otherwise.
     */
    public boolean isMobileViewport() {
        try {
            int width = driver.manage().window().getSize().getWidth();
            log.debug("Current viewport width: {}px", width);
            return width < 768; // Tailwind md breakpoint
        } catch (Exception e) {
            log.error("Failed to determine viewport size: {}", e.getMessage());
            return false;
        }
    }

    /**
     * V.2 Boolean Verification wrapper asserting active component status
     *
     * @param locator The By locator used to identify the target element on the
     *                page.
     * @return true if the element is displayed, false otherwise.
     */
    protected Boolean isDisplayed(By locator) {
        log.debug("Verifying display status of target element: [{}]", locator);
        // WAITS, and checks EVERY match — both halves matter, for different reasons.
        //
        // The original implementation was getElement(locator).isDisplayed(), which waited
        // up to the explicit timeout but only ever considered the FIRST DOM match. On this
        // storefront that is frequently the hidden mobile twin of a responsive pair, so it
        // burned the whole timeout and then reported false about a section plainly on
        // screen — the reason all five accessory PDP tests claimed the image gallery was
        // missing.
        //
        // The first fix for that checked every match but dropped the WAIT, and that broke a
        // different set of tests: any assertion made straight after a click or navigation
        // now raced the render and reported false before the page had settled (caught on
        // the Friends & Partners directory, whose h1 is present and visible but not
        // instantly). Losing the wait was a regression, not a simplification.
        //
        // waitForFirstVisible does both: poll until ANY match is visible, so it neither
        // fixates on the hidden twin nor returns before the page is ready. It still costs
        // the full timeout when the answer is genuinely "no" — same as the original — which
        // is the right trade for an assertion helper.
        return isEventuallyDisplayed(locator);
    }

    /**
     * V.2c "Is anything matching this visible RIGHT NOW?" — no waiting.
     *
     * <p>Use this only where the page is already known to be settled and the expected
     * answer may legitimately be false, so paying the full timeout would be pure delay
     * (e.g. polling for a transient state, or asserting absence). Everywhere else prefer
     * {@link #isDisplayed(By)}, which waits.
     *
     * @param locator the By locator, which may match several nodes
     * @return true if any match is visible at this instant
     */
    protected boolean isDisplayedNow(By locator) {
        try {
            for (WebElement candidate : driver.findElements(locator)) {
                try {
                    if (candidate.isDisplayed()) {
                        return true;
                    }
                } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                    // Re-rendered mid-check; the remaining candidates still get a look.
                }
            }
        } catch (Exception e) {
            log.debug("Instant display check failed for [{}]: {}", locator, e.getMessage());
        }
        return false;
    }

    /**
     * V.2a Waits for an element matching {@code locator} to become visible, then reports
     * whether it did.
     *
     * <p>{@link #isDisplayed(By)} answers "is it visible RIGHT NOW" with no waiting, which
     * is the correct question once a page has settled but the wrong one immediately after
     * navigation: React mounts sections after hydration, so an instant check can race the
     * render and report a false negative. Use this when asserting a section exists shortly
     * after landing on a page.
     *
     * @param locator the By locator to look for
     * @return true if an element became visible within the configured timeout
     */
    protected boolean isEventuallyDisplayed(By locator) {
        try {
            waitForFirstVisible(locator);
            return true;
        } catch (Exception e) {
            log.debug("No visible element matched [{}] within the timeout.", locator);
            return false;
        }
    }

    /**
     * B.14 Scrolls the first matching element to the centre of the viewport.
     *
     * <p>The storefront reveals its lower sections on scroll (IntersectionObserver), so a
     * section that sits ~2000px below the fold may not be painted until the user — or the
     * test — actually scrolls to it. Asserting on such a section without scrolling is a
     * coin flip: it passes when the render happens to have run and fails when it has not,
     * which is precisely the kind of intermittent red that makes a suite untrustworthy.
     *
     * <p>Uses presence rather than visibility to find the target, because the whole point
     * is that it may not be visible yet. Never throws: a scroll is an optimisation for the
     * assertion that follows, not the assertion itself.
     *
     * @param locator the By locator to bring into view
     * @return this page instance, typed as the concrete subclass, for chaining
     */
    @SuppressWarnings("unchecked")
    protected SELF scrollIntoView(By locator) {
        try {
            WebElement target = getElementPresence(locator);
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block:'center', behavior:'instant'});", target);
        } catch (Exception e) {
            log.debug("Could not scroll [{}] into view: {}", locator, e.getMessage());
        }
        return (SELF) this;
    }

    /**
     * V.2b Waits for every element matching {@code locator} to become invisible or leave
     * the DOM.
     *
     * <p>The counterpart to {@link #isEventuallyDisplayed(By)}, and the correct way to
     * assert a modal/drawer has CLOSED. Overlays close with a CSS transition, so checking
     * the instant after clicking "close" catches the element mid-animation and reports it
     * as still open — a failure that says "the app is broken" when the app is merely
     * animating.
     *
     * @param locator the By locator expected to disappear
     * @return true if nothing matching is visible within the configured timeout
     */
    protected boolean isEventuallyHidden(By locator) {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (Exception e) {
            log.debug("Element [{}] was still visible when the timeout expired.", locator);
            return false;
        }
    }
}
