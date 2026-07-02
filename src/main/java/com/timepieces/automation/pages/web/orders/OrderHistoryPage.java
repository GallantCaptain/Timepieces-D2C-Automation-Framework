package com.timepieces.automation.pages.web.orders;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.HeaderOnlyBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * OrderHistoryPage: https://www.timepieces-d2c.example/account/orders
 * This is the Page Object Repository for the Timepieces Order History Page
 * (/account/orders).
 *
 * This page is displayed after a successful login (returning users).
 *
 * Contains links to all the orders placed by the user. In this page all
 * order are displayed in a card format with their final status, order number
 * and the date on which the order was placed. When the order card is clicked
 * it navigates the user to the corresponding {@code OrderDetailsPage}.
 * 
 * This page only contains the Header.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (OrderHistoryPage)
 * to
 * allow
 * method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class OrderHistoryPage extends HeaderOnlyBasePage {

    // ==========================================
    // Constructor
    // ==========================================
    public OrderHistoryPage(WebDriver driver) {
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
