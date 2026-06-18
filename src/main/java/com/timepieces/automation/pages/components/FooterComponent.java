package com.timepieces.automation.pages.components;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * FooterComponent class represents the footer section of the web application.
 * It extends the BasePage class, which provides common functionalities for all
 * page components. The FooterComponent class contains a WebDriver instance to
 * interact with the web elements in the footer section. The constructor
 * initializes the WebDriver instance when an object of FooterComponent is
 * created.
 */
@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public class FooterComponent extends BasePage {

    /// ==========================================
    // Constructor
    // ==========================================
    public FooterComponent(WebDriver driver) {
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
