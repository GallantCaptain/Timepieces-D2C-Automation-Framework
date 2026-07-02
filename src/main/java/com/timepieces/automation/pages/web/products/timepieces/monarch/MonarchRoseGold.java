package com.timepieces.automation.pages.web.products.timepieces.monarch;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Monarch Rose Gold: https://www.timepieces-d2c.example/products/monarch-rose-gold
 * The Monarch Rose Gold PDP page. This page contains details about the Monarch
 * Rose Gold watch. It is a shared template for the Monarch family of
 * timepieces. The other watches can be navigated to in the PDP page with a
 * card to toggle between each. The page also has a sticky header and footer
 * and scroll-to-section functionality. The videos are on autoplay and for
 * those that have audio they are muted by default. The AR functionality is not
 * currently working. The user will be redirected to a page in the future when
 * the feature is ready to be implemented.
 * The page can be navigated to from the PDP Page with a side navigation bar and
 * top navigation bar. FLUENT INTERFACE: All methods that remain on this page
 * return {@code this} (MonarchRoseGold) to allow method chaining in step
 * definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class MonarchRoseGold extends SharedTemplate_Monarch {

    // ==========================================
    // Constructor
    // ==========================================
    public MonarchRoseGold(WebDriver driver) {
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
