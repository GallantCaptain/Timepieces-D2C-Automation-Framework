package com.timepieces.automation.pages.web.otherPages.timepieces_world.components;

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
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (TimepiecesWorld_HeaderComponent) to allow method chaining in step
 * definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class TimepiecesWorld_HeaderComponent extends BasePage {

    // ==========================================
    // Constructor
    // ==========================================
    public TimepiecesWorld_HeaderComponent(WebDriver driver) {
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
