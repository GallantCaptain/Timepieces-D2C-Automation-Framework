package com.timepieces.automation.pages.web.otherPages.timepieces_world.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * TimepiecesWorld_HeaderComponent is a global header for all the Timepieces World
 * pages. The Timepieces Logo in the Timepieces World header has 2 navigation
 * functions, as long as the user is in the Timepieces World experience page, the
 * Timepieces logo navigates back to the main website: https://www.timepieces-d2c.example,
 * but after clicking on any of the products 'Discover' cta button,
 * and navigating to the product showcase page, then the timepieces logo changes
 * to navigate back to the Timepieces World Experience page:
 * https://world.timepieces-d2c.example
 * instead of the main website: https://www.timepieces-d2c.example
 *
 * <p>
 * SCRAPE NOTE (2026-07-16): the dual-navigation behavior above was VERIFIED
 * live. The header is a fixed {@code z-[1000]} bar with a hamburger toggle
 * ({@code button[aria-label='Toggle menu']}) whose inner {@code img[alt='Menu']}
 * swaps between {@code hamburger_collapased.svg} (closed) and
 * {@code hamburger_expanded.svg} (open) — {@link #isMenuExpanded()} reads that.
 * The logo is {@code img[alt='Timepieces Logo']}. NO {@code data-testid}s exist on
 * the Timepieces World chrome — all locators are fallbacks tagged
 * {@code // ROLLOUT-GAP}.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (TimepiecesWorld_HeaderComponent) to allow method chaining in step
 * definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TimepiecesWorld_HeaderComponent"
* @since 2026-07-03
 */

@Log4j2
public class TimepiecesWorld_HeaderComponent extends BasePage<TimepiecesWorld_HeaderComponent> {

    // ==========================================
    // Constructor
    // ==========================================
    public TimepiecesWorld_HeaderComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // The fixed Timepieces World header bar.
    private final By headerContainer = By.cssSelector("header.fixed.top-0"); // ROLLOUT-GAP: utility-class
    // The hamburger menu toggle (opens/closes the RW sidebar).
    private final By menuToggle = By.cssSelector("header button[aria-label='Toggle menu']"); // ROLLOUT-GAP: aria
    // The hamburger state icon (src swaps collapsed <-> expanded).
    private final By menuIcon = By.cssSelector("header button[aria-label='Toggle menu'] img[alt='Menu']"); // ROLLOUT-GAP: structural
    // The dual-navigation Timepieces logo.
    private final By timepiecesLogo = By.cssSelector("header img[alt='Timepieces Logo']"); // ROLLOUT-GAP: alt-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Clicks the hamburger toggle (opens the sidebar when closed, closes it
     * when open).
     *
     * @return this component instance for method chaining.
     */
    public TimepiecesWorld_HeaderComponent toggleMenu() {
        log.info("Toggling the Timepieces World sidebar menu.");
        clickElement(menuToggle);
        return this;
    }

    /**
     * B.2 Clicks the Timepieces logo. DUAL NAVIGATION: from the experience page this
     * leaves for {@code https://www.timepieces-d2c.example}; from a product showcase page it
     * returns to the experience page ({@code https://world.timepieces-d2c.example}).
     *
     * @return this component instance for method chaining.
     */
    public TimepiecesWorld_HeaderComponent clickLogo() {
        log.info("Clicking the Timepieces World logo (dual-navigation).");
        clickElement(timepiecesLogo);
        return this;
    }

    /**
     * B.3 Returns the hamburger icon's current image source (encodes the
     * open/closed state).
     *
     * @return the menu icon src.
     */
    public String getMenuIconSrc() {
        return getAttribute(menuIcon, "src");
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the Timepieces World header bar is displayed.
     *
     * @return true if the header is displayed.
     */
    public boolean isHeaderDisplayed() {
        return isDisplayed(headerContainer);
    }

    /**
     * V.2 Verifies the hamburger toggle is displayed.
     *
     * @return true if the menu toggle is displayed.
     */
    public boolean isMenuToggleDisplayed() {
        return isDisplayed(menuToggle);
    }

    /**
     * V.3 Verifies the Timepieces logo is displayed.
     *
     * @return true if the logo is displayed.
     */
    public boolean isLogoDisplayed() {
        return isDisplayed(timepiecesLogo);
    }

    /**
     * V.4 Reports whether the sidebar menu is currently OPEN, read from the
     * hamburger icon source ({@code hamburger_expanded.svg} when open).
     *
     * @return true if the menu icon shows the expanded state.
     */
    public boolean isMenuExpanded() {
        String src = getMenuIconSrc();
        return src != null && src.contains("hamburger_expanded");
    }
}
