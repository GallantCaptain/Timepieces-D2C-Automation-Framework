package com.timepieces.automation.pages.web.products.timepieces.astonia;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Astonia Trophy Gold: https://www.timepieces-d2c.example/products/astonia-trophy-gold
 * The Astonia Trophy Gold PDP page. This page contains details about the
 * Astonia Trophy Gold watch. It is a variant of the Astonia family of
 * timepieces. The other watches can be navigated to in the PDP page with a card
 * to toggle between each. The page also has a sticky header and footer and
 * scroll-to-section functionality. The videos are on autoplay and for those
 * that support 'loom' functionality like Arvion, Astonia, Astonia Sports, and
 * Monarch, i.e., all except Manifesta. There is a 3D view toggle on the
 * top-most product image showcase that opens the 3D viewer canvas and is used
 * to view the product models in better detail in 3D. All product except
 * Astonia Trophy Gold has the 3D viewer toggle. There is also a planned
 * AR functionality 'tbd' sometime around the end of the year. This page does
 * not contain the 3D viewer toggle.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (AstoniaTrophyGold)
 * to allow method chaining in step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of AstoniaTrophyGold"
* @since 2026-07-03
 */

@Log4j2
public class AstoniaTrophyGold extends SharedTemplate_Astonia<AstoniaTrophyGold> {

    // ==========================================
    // PAGE CONSTANTS
    // ==========================================
    private static final String PAGE_PATH = "/products/astonia-trophy-gold";
    private static final String PAGE_TITLE = "Astonia Trophy Gold, Astonia Watch - Timepieces World";

    // ==========================================
    // Constructor
    // ==========================================
    public AstoniaTrophyGold(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // PAGE METADATA (drives the inherited fluent open())
    // ==========================================
    @Override
    protected String getPagePath() {
        return PAGE_PATH;
    }

    @Override
    protected String getPageTitle() {
        return PAGE_TITLE;
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Inherited from SharedTemplate_Astonia. Trophy Gold is the ONLY Astonia
    // variant WITHOUT a 3D viewer (see the 3D overrides below).

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 (override) Trophy Gold does NOT ship a 3D viewer; this variant reflects
     * that absence rather than the shared default of {@code true}.
     *
     * @return always false for Astonia Trophy Gold.
     */
    @Override
    public boolean has3DViewer() {
        return false;
    }

    /**
     * V.16 Confirms the 3D viewer toggle is genuinely ABSENT on this variant (the
     * distinguishing feature of Trophy Gold vs. the other Astonia PDPs).
     *
     * @return true if no 3D viewer toggle is present on the page.
     */
    public boolean is3DViewerAbsent() {
        return !is3DViewerToggleDisplayed();
    }
}
