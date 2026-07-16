package com.timepieces.automation.pages.web.otherPages.timepieces_world.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * TimepiecesWorld_SidebarMenuComponent — the slide-in navigation drawer on the
 * Timepieces World pages, opened via the header hamburger
 * ({@code TimepiecesWorld_HeaderComponent#toggleMenu()}).
 *
 * <p>
 * SCRAPE NOTE (2026-07-16): the drawer is a fixed left panel (full-width on
 * mobile, 500px on desktop) whose open/closed state is encoded in its transform
 * classes — {@code translate-x-0} when OPEN, {@code -translate-x-full} when
 * CLOSED ({@link #isOpen()} reads that). A blurred backdrop overlay closes the
 * drawer on click, as does toggling the hamburger again. The menu holds exactly
 * the 5 family showcase links (ARVION / MONARCH / AURIQUA / ASTONIA /
 * MANIFESTA), all relative to the {@code world.timepieces-d2c.example} subdomain
 * ({@code /products/<family>}). NO {@code data-testid}s exist on the Timepieces
 * World chrome — all locators are fallbacks tagged {@code // ROLLOUT-GAP}.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (TimepiecesWorld_SidebarMenuComponent) to allow method chaining in step
 * definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TimepiecesWorld_SidebarMenuComponent"
* @since 2026-07-03
 */

@Log4j2
public class TimepiecesWorld_SidebarMenuComponent extends BasePage<TimepiecesWorld_SidebarMenuComponent> {

    // ==========================================
    // Constructor
    // ==========================================
    public TimepiecesWorld_SidebarMenuComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // The slide-in drawer panel (translate-x-0 = open, -translate-x-full = closed).
    private final By drawerPanel = By.cssSelector("header div.fixed.top-0.left-0.h-screen"); // ROLLOUT-GAP: structural
    // The blurred backdrop overlay (click closes the drawer).
    private final By backdropOverlay = By.cssSelector("header div.fixed.inset-0.bg-black\\/30"); // ROLLOUT-GAP: utility-class
    // All family navigation links inside the drawer.
    private final By familyLinks = By.cssSelector("header div.fixed.top-0.left-0 a[href^='/products/']"); // ROLLOUT-GAP: structural
    // Dynamic: a family link by its lowercase handle (e.g. "astonia").
    private final By familyLinkTemplate = By.xpath("//header//div[contains(@class,'fixed')]//a[@href='/products/%s']"); // ROLLOUT-GAP: href-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Clicks a family showcase link by its lowercase handle (e.g. "astonia"),
     * navigating to that family's Timepieces World showcase page.
     *
     * @param familyHandle the lowercase family handle.
     * @return this component instance for method chaining.
     */
    public TimepiecesWorld_SidebarMenuComponent clickFamily(String familyHandle) {
        log.info("Clicking the RW sidebar family link: [{}]", familyHandle);
        clickElement(formatLocator(familyLinkTemplate, familyHandle));
        return this;
    }

    /**
     * B.2 Closes the drawer by clicking the backdrop overlay.
     *
     * @return this component instance for method chaining.
     */
    public TimepiecesWorld_SidebarMenuComponent closeViaBackdrop() {
        log.info("Closing the RW sidebar via the backdrop.");
        clickElement(backdropOverlay);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Reports whether the drawer is OPEN (its transform class is
     * {@code translate-x-0} rather than {@code -translate-x-full}).
     *
     * @return true if the drawer is open.
     */
    public boolean isOpen() {
        if (driver.findElements(drawerPanel).isEmpty()) {
            return false;
        }
        String cls = getAttribute(drawerPanel, "class");
        return cls != null && cls.contains("translate-x-0") && !cls.contains("-translate-x-full");
    }

    /**
     * V.2 Verifies a family link with the given lowercase handle exists in the
     * drawer.
     *
     * @param familyHandle the lowercase family handle (e.g. "monarch").
     * @return true if the link is present.
     */
    public boolean hasFamilyLink(String familyHandle) {
        return !driver.findElements(formatLocator(familyLinkTemplate, familyHandle)).isEmpty();
    }

    /**
     * V.3 Counts the family links in the drawer (5 expected).
     *
     * @return the number of family links.
     */
    public int getFamilyLinkCount() {
        return driver.findElements(familyLinks).size();
    }

    /**
     * V.4 Verifies the backdrop overlay is present (open state only).
     *
     * @return true if the backdrop is present.
     */
    public boolean isBackdropPresent() {
        return !driver.findElements(backdropOverlay).isEmpty();
    }
}
