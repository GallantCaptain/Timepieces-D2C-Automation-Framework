package com.timepieces.automation.pages.web.orders;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.HeaderOnlyBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * OrderDetailsPage: https://www.timepieces-d2c.example/account/orders/Timepieces{Order
 * Number}
 * This is the Page Object Repository for the Timepieces Order Details Page
 * (/account/orders/Timepieces{Order Number}).
 *
 * This page is displayed to a loggedin user via the order history page from
 * the account page. The is a contact support button that the user can use to
 * contact the Timepieces Support team regarding the order. "Currently bugged"
 * 
 * All details of the order, delivery status, payment status, receipt
 * if delivered are displayed here. The order details include the order number,
 * order date, order status, order total, order items, shipping address, billing
 * address.
 * 
 * This page only contains the Header.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (OrderDetailsPage)
 * to allow method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class OrderDetailsPage extends HeaderOnlyBasePage<OrderDetailsPage> {

    // ==========================================
    // Constructor
    // ==========================================
    public OrderDetailsPage(WebDriver driver) {
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
