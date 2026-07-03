package com.timepieces.automation.pages.web.products.timepieces.auriqua;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Auriqua Noir Rose: https://www.timepieces-d2c.example/products/auriqua-noir-rose
 * The Auriqua Noir Rose Gold PDP page. This page contains details about the
 * Auriqua Noir Rose watch. It is a shared template for the Auriqua family of
 * timepieces. The other watches can be navigated to in the PDP page with a card
 * to toggle between each. The page also has a sticky header and footer and
 * scroll-to-section functionality. The videos are on autoplay and for those
 * that support 'loom' functionality like Arvion, Astonia, Astonia Sports, and
 * Monarch, i.e., all except Manifesta. There is a 3D view toggle on the
 * top-most product image showcase that opens the 3D viewer canvas and is used
 * to view the product models in better detail in 3D. All product except
 * Astonia Trophy Gold has the 3D viewer toggle. There is also a planned
 * AR functionality 'tbd' sometime around the end of the year.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (AuriquaNoirRose) to
 * allow method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class AuriquaNoirRose extends SharedTemplate_Auriqua<AuriquaNoirRose> {

    // ==========================================
    // Constructor
    // ==========================================
    public AuriquaNoirRose(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================

}
