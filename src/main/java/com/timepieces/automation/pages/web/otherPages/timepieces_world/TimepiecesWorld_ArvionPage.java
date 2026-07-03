package com.timepieces.automation.pages.web.otherPages.timepieces_world;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * 
 * TimepiecesWorld_ArvionPage: https://world.timepieces-d2c.example/products/arvion
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (FriendsAndPartnersPage) to allow method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class TimepiecesWorld_ArvionPage extends BasePage<TimepiecesWorld_ArvionPage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Page Path Variable
    private static final String PAGE_PATH = "/products/arvion";

    // The Page Title Variable {Keep whether Unique or Not}
    // Decide if it needs to be added to assertions if not unique
    private static final String PAGE_TITLE = "Timepieces Watches | Early Access to Limited Edition Pieces";

    // ==========================================
    // Constructor
    // ==========================================
    public TimepiecesWorld_ArvionPage(WebDriver driver) {
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