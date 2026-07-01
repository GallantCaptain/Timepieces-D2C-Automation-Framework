package com.timepieces.automation.pages.components;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The Razorpay Modal Component is called for the payment step for both domestic
 * and in international payments, although the prior steps to get to this
 * differ. Also, for international payments only Apple Pay in USD, Euro, Dirham
 * are supported currently, while INR is the default currency for domestic
 * customers.
 * 
 * @version 0.1
 * @author Avinash Rath
 */

@Log4j2
public class RazorpayModalComponent extends BasePage {

    // ==========================================
    // Constructor
    // ==========================================
    public RazorpayModalComponent(WebDriver driver) {
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
