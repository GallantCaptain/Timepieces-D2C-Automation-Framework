package com.timepieces.automation.pages.web.orders;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The difference between OrderHistoryPage and TrackOrdersPage is that the user
 * has to be logged in for the former and a user can browse as a guest and track
 * the order using the order number when they click on Track Order Button from
 * the SidebarMenuComponent
 */
@Log4j2
public class TrackOrdersPage extends StoreFrontBasePage {
    // ==========================================
    // Constructor
    // ==========================================
    public TrackOrdersPage(WebDriver driver) {
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
