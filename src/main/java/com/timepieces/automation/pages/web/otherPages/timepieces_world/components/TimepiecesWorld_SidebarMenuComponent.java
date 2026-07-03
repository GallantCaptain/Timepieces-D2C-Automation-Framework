package com.timepieces.automation.pages.web.otherPages.timepieces_world.components;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * TimepiecesWorld_SidebarMenuComponent contains all the Timepieces World Product Page
 * Showcase links presented as Product cards. The product cards are
 * presentation cards of the 5 products that the user can further explore by
 * clicking on the card which redirects them to the respective
 * product showcase pages (Arvion, Monarch, Auriqua, Astonia, Manifesta).
 * This SidebarMenu component is different from the main website's sidebar menu.
 * This sidebar menu is only visible on the Timepieces World pages and is not
 * present on the main website.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (TimepiecesWorld_SidebarMenuComponent) to allow method chaining in step
 * definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class TimepiecesWorld_SidebarMenuComponent extends BasePage<TimepiecesWorld_SidebarMenuComponent> {

    // ==========================================
    // Constructor
    // ==========================================
    public TimepiecesWorld_SidebarMenuComponent(WebDriver driver) {
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
