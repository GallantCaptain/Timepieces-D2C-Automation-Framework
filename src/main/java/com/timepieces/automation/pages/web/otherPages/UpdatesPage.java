package com.timepieces.automation.pages.web.otherPages;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The UpdatesPage: https://www.timepieces-d2c.example/updates provides users
 * with news updates and announcements on this page. Users will be able to
 * view older news and announcements pertaining to the brand.
 * The news/announcements can be filtered by clicking on any of the
 * content filter buttons, like 'All', 'Drops', etc.
 * There is a header and a footer on this page.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (UpdatesPage)
 * to allow method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class UpdatesPage extends StoreFrontBasePage {

    // ==========================================
    // Constructor
    // ==========================================
    public UpdatesPage(WebDriver driver) {
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
