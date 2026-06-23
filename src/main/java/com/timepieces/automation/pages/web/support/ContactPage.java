package com.timepieces.automation.pages.web.support;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The ContactPage has links for Phone Support (removed for now), WhatsApp
 * Support, Email link, and also a form to directly send an email to the Support
 * team. The support team normally replies within 1 Business day after receiving
 * a support request. A request number is raised and displayed on the page after
 * submitting the form. For email, a support number is given with the
 * auto-generated reply that the support team will get back within a business
 * day.
 */
@Log4j2
public class ContactPage extends StoreFrontBasePage {
    // ==========================================
    // Constructor
    // ==========================================
    public ContactPage(WebDriver driver) {
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
