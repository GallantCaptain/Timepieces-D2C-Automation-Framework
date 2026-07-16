package com.timepieces.automation.pages.web.otherPages.timepieces_world;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * ExperiencePage: https://world.timepieces-d2c.example/
 * The experience page is the first page the user lands upon when clicking on
 * Timepieces World. It scrolls through each of the 5 timepieces and has a CTA
 * button for each of the them that takes them to their respecitve Timepieces World
 * product showcase page. The canvas here is full of static images that is
 * controlled by scripts. The images load forward giving the appearance of a
 * video when scrolling down. When scrolling up it gives the appearance of a
 * video playing in reverse. The speed of the canvas cannot be controlled and
 * can only be nudged to go forward or back. The progress bars at the side are
 * not interactable and showcase the progress of the RW canvas both forwards and
 * backwards. The order of the Products in the canvas sections are in the order:
 * Arvion, Monarch, Auriqua, Astonia, Manifesta, and then all the products in
 * the product card containers together in a static carousel at the last
 * section.
 *
 * <p>
 * SCRAPE NOTE (2026-07-16): 7 scenes total (Opening → the 5 families → End
 * Scene, a STATIC card grid — no prev/next controls). A welcome screen with an
 * "Enter Experience" button gates the canvas. Native scrolling is DISABLED
 * ({@code html/body overflow:hidden}, {@code scrollTop} stays 0) — the canvas
 * advances on {@code wheel}/touch events, so {@link #advanceCanvas(int)}
 * dispatches a synthetic {@code WheelEvent} via JS. Progress is read from the
 * right-side pill rail: the ACTIVE section renders a tall pill ({@code h-6})
 * while inactive sections are dots ({@code h-1.5}); the hover/active tooltip
 * text is "&lt;Section&gt;&lt;NN&gt;%". Each family scene shows an {@code h2}
 * family heading + a "DISCOVER" CTA →
 * {@code /products/<family>?from=scene&frame=<n>}. NO {@code data-testid}s on
 * the page — every locator is a fallback tagged {@code // ROLLOUT-GAP}. The
 * document title is the generic storefront title (not unique). This page lives
 * on {@code https://world.timepieces-d2c.example} — {@link #getBaseUrl()} is overridden.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (TimepiecesWorld_ExperiencePage) to allow method chaining in step
 * definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TimepiecesWorld_ExperiencePage"
* @since 2026-07-03
 */

@Log4j2
public class TimepiecesWorld_ExperiencePage extends BasePage<TimepiecesWorld_ExperiencePage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Timepieces World origin (distinct from the storefront base.url).
    private static final String WORLD_BASE_URL = "https://world.timepieces-d2c.example";
    // The Page Path Variable.
    private static final String PAGE_PATH = "/";

    // ==========================================
    // Constructor
    // ==========================================
    public TimepiecesWorld_ExperiencePage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // PAGE METADATA (drives the inherited fluent open())
    // ==========================================
    @Override
    public String getBaseUrl() {
        return WORLD_BASE_URL;
    }

    @Override
    protected String getPagePath() {
        return PAGE_PATH;
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // The welcome-screen "Enter Experience" gate button.
    private final By enterExperienceButton = By.xpath("//button[contains(normalize-space(),'Enter Experience')]"); // ROLLOUT-GAP: text-anchor
    // The full-screen canvas wrapper + the frame canvas itself.
    private final By canvasWrapper = By.cssSelector("div.fixed.inset-0.w-full.h-screen.overflow-hidden"); // ROLLOUT-GAP: utility-class
    private final By experienceCanvas = By.cssSelector("canvas.w-full.h-full"); // ROLLOUT-GAP: structural
    // The active scene's family heading (h2 inside the pointer-events-auto overlay).
    private final By familyHeading = By.cssSelector("div.pointer-events-auto h2"); // ROLLOUT-GAP: structural
    // The active scene's "DISCOVER" CTA (→ /products/<family>?from=scene&frame=<n>).
    private final By discoverButton = By.xpath("//div[contains(@class,'pointer-events-auto')]//button[normalize-space()='DISCOVER']"); // ROLLOUT-GAP: text-anchor
    // The right-side progress rail; the ACTIVE section is a tall pill (h-6), inactive are dots (h-1.5).
    private final By progressRail = By.cssSelector("div.fixed.right-3"); // ROLLOUT-GAP: utility-class
    private final By progressSegments = By.cssSelector("div.fixed.right-3 div.relative.group"); // ROLLOUT-GAP: structural
    private final By activeProgressPill = By.cssSelector("div.fixed.right-3 div.h-6"); // ROLLOUT-GAP: utility-class
    // The end-scene static product grid (desktop) / list (mobile).
    private final By endGridDesktop = By.cssSelector("div.hidden.md\\:grid"); // ROLLOUT-GAP: utility-class
    private final By endListMobile = By.cssSelector("div.flex-col.md\\:hidden"); // ROLLOUT-GAP: utility-class
    // The end-scene product cards (each links to /products/<family>).
    private final By endGridCards = By.cssSelector("a[href^='/products/']"); // ROLLOUT-GAP: structural

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Clicks the welcome-screen "Enter Experience" button, starting the
     * canvas experience.
     *
     * @return this page instance for method chaining.
     */
    public TimepiecesWorld_ExperiencePage enterExperience() {
        log.info("Entering the Timepieces World experience.");
        clickElement(enterExperienceButton);
        return this;
    }

    /**
     * B.2 Nudges the canvas forward (positive delta) or backward (negative
     * delta) by dispatching a synthetic {@code WheelEvent} — native scrolling is
     * disabled on this page, so a plain scroll cannot advance the scene.
     *
     * @param deltaY the wheel delta (e.g. 5000 to advance, -5000 to reverse).
     * @return this page instance for method chaining.
     */
    public TimepiecesWorld_ExperiencePage advanceCanvas(int deltaY) {
        log.info("Dispatching a wheel event (deltaY={}) to nudge the canvas.", deltaY);
        ((JavascriptExecutor) driver).executeScript(
                "window.dispatchEvent(new WheelEvent('wheel', { deltaY: arguments[0], bubbles: true }));", deltaY);
        return this;
    }

    /**
     * B.3 Returns the currently displayed scene's family heading text (e.g.
     * "Arvion").
     *
     * @return the trimmed family heading text.
     */
    public String getActiveFamilyHeading() {
        return getText(familyHeading);
    }

    /**
     * B.4 Clicks the active scene's "DISCOVER" CTA, navigating to that family's
     * Timepieces World showcase page.
     *
     * @return this page instance for method chaining.
     */
    public TimepiecesWorld_ExperiencePage clickDiscover() {
        log.info("Clicking the active scene's DISCOVER CTA.");
        clickElement(discoverButton);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the welcome "Enter Experience" gate is displayed (pre-entry
     * state).
     *
     * @return true if the enter button is displayed.
     */
    public boolean isEnterExperienceDisplayed() {
        return isDisplayed(enterExperienceButton);
    }

    /**
     * V.2 Verifies the experience canvas is displayed.
     *
     * @return true if the canvas is displayed.
     */
    public boolean isCanvasDisplayed() {
        return isDisplayed(canvasWrapper) && !driver.findElements(experienceCanvas).isEmpty();
    }

    /**
     * V.3 Verifies the right-side progress rail is present.
     *
     * @return true if the progress rail is present.
     */
    public boolean isProgressRailDisplayed() {
        return !driver.findElements(progressRail).isEmpty();
    }

    /**
     * V.4 Counts the progress-rail segments (one per scene — 7 expected:
     * Opening + 5 families + End Scene).
     *
     * @return the number of progress segments.
     */
    public int getProgressSegmentCount() {
        return driver.findElements(progressSegments).size();
    }

    /**
     * V.5 Reports whether an ACTIVE (tall pill) progress segment is currently
     * rendered — the signal that the canvas experience is running.
     *
     * @return true if an active pill is present.
     */
    public boolean hasActiveProgressPill() {
        return !driver.findElements(activeProgressPill).isEmpty();
    }

    /**
     * V.6 Verifies the active scene shows a "DISCOVER" CTA (family scenes only —
     * not the opening/end scenes).
     *
     * @return true if the DISCOVER CTA is present.
     */
    public boolean isDiscoverCtaDisplayed() {
        return !driver.findElements(discoverButton).isEmpty();
    }

    /**
     * V.7 Verifies the end-scene static product grid/list is displayed
     * (viewport-appropriate variant).
     *
     * @return true if the end grid (desktop) or list (mobile) is present.
     */
    public boolean isEndGridDisplayed() {
        By variant = isMobileViewport() ? endListMobile : endGridDesktop;
        return !driver.findElements(variant).isEmpty();
    }

    /**
     * V.8 Counts the end-scene product cards.
     *
     * @return the number of product cards (5 families expected).
     */
    public int getEndGridCardCount() {
        return driver.findElements(endGridCards).size();
    }

    /**
     * V.9 Verifies every end-scene card links to a Timepieces World showcase page
     * ({@code /products/<family>}), and at least one card exists.
     *
     * @return true if all cards link to showcase pages.
     */
    public boolean allEndGridCardsLinkToShowcases() {
        var cards = driver.findElements(endGridCards);
        if (cards.isEmpty()) {
            return false;
        }
        return cards.stream()
                .map(e -> e.getAttribute("href"))
                .allMatch(h -> h != null && h.contains("/products/"));
    }
}
