package com.timepieces.automation.pages.web.orders;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * Track Orders Page: https://www.timepieces-d2c.example/track-order
 * 
 * The difference between OrderHistoryPage and TrackOrdersPage is that the user
 * has to be logged in for the former and a user can browse as a guest and track
 * the order using the order number when they click on Track Order Button from
 * the SidebarMenuComponent
 * 
 * Only details of the order like delivery status, order number and order date
 * are mentioned here. There is a contact support button that takes the user to
 * {@code ContactPage}
 * 
 * This page only contains the Header.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (OrderDetailsPage)
 * to allow method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TrackOrdersPage"
* @since 2026-07-03
 */

@Log4j2
public class TrackOrdersPage extends StoreFrontBasePage<TrackOrdersPage> {

    // ==========================================
    // Constructor
    // ==========================================
    public TrackOrdersPage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Order Details Container
    private final By orderDetailsContainer = By.xpath("//div[@data-testid='order-details-card']");

    // Order Number Text - Should contain Timepieces prefix
    private final By orderNumberText = By.xpath("//div[@data-testid='order-number']");

    // Order Date Text
    private final By orderDateText = By.xpath("//div[@data-testid='order-date']");

    // Order Status Badge
    private final By orderStatusBadge = By
            .xpath("//div[contains(@class, 'text-sm') and contains(@class, 'text-right')]");

    // Contact Support Button
    private final By contactSupportButton = By.xpath("//button[contains(text(), 'Contact Support')]");

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
