package com.timepieces.automation.pages.components;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * HeaderComponent class represents the header section of the web application.
 * It extends the BasePage class, which provides common functionalities for all
 * page components. The HeaderComponent class contains a WebDriver instance to
 * interact with the web elements in the header section. The constructor
 * initializes the WebDriver instance when an object of HeaderComponent is
 * created.
 */
@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public class HeaderComponent extends BasePage {
    // ==========================================
    // Constructor
    // ==========================================
    public HeaderComponent(WebDriver driver) {
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
