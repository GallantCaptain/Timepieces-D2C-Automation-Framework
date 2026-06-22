package com.timepieces.automation.pages.components.cards;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * SidebarMenuComponent represents the navigation sidebar menu card drawer.
 */
@Log4j2
public class SidebarMenuComponent extends BasePage {

    // ==========================================
    // Constructor
    // ==========================================
    public SidebarMenuComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Sidebar Overlay:
    private final By sidebarOverlay = By.cssSelector("[data-testid='nav-sidebar-overlay']");
    
    // Sidebar Container:
    private final By sidebarContainer = By.cssSelector("[data-testid='nav-sidebar']");
    
    // Close Button:
    private final By closeButton = By.cssSelector("[data-testid='nav-sidebar-close']");
    
    // Logo inside Sidebar:
    private final By sidebarLogo = By.cssSelector("[data-testid='nav-sidebar-logo']");
    
    // Menu Links List:
    private final By menuLinksList = By.cssSelector("[data-testid='nav-sidebar-menu']");
    
    // Menu List Items:
    private final By menuListItems = By.cssSelector("[data-testid='nav-sidebar-menu-item']");
    
    // Menu Item Button:
    private final By menuItemButton = By.cssSelector("[data-testid='nav-sidebar-menu-button']");
    
    // Menu Item Link:
    private final By menuItemLink = By.cssSelector("[data-testid='nav-sidebar-menu-link']");
    
    // Sign In Button:
    private final By signInButton = By.cssSelector("[data-testid='nav-sidebar-signin']");
    
    // Contact Link:
    private final By contactLink = By.cssSelector("[data-testid='nav-sidebar-contact']");
    
    // Wishlist Link:
    private final By wishlistLink = By.cssSelector("[data-testid='nav-sidebar-wishlist']");
    
    // Track Orders Link:
    private final By trackOrdersLink = By.cssSelector("[data-testid='nav-sidebar-track-orders']");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * Clicks the close button to dismiss the sidebar.
     * @return this SidebarMenuComponent for method chaining.
     */
    public SidebarMenuComponent clickCloseButton() {
        log.info("Clicking sidebar close button.");
        clickElement(closeButton);
        return this;
    }

    /**
     * Clicks on the Sign In button inside the sidebar.
     * @return this SidebarMenuComponent for method chaining.
     */
    public SidebarMenuComponent clickSignIn() {
        log.info("Clicking sidebar sign in button.");
        clickElement(signInButton);
        return this;
    }

    /**
     * Clicks on the Contact Us link inside the sidebar.
     * @return this SidebarMenuComponent for method chaining.
     */
    public SidebarMenuComponent clickContact() {
        log.info("Clicking sidebar contact link.");
        clickElement(contactLink);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * Verifies if the sidebar container is displayed.
     * @return true if displayed, false otherwise.
     */
    public boolean isSidebarDisplayed() {
        boolean displayed = isDisplayed(sidebarContainer);
        log.info("Sidebar Container displayed check: {}", displayed);
        return displayed;
    }

    /**
     * Verifies if the close button is displayed.
     * @return true if displayed, false otherwise.
     */
    public boolean isCloseButtonDisplayed() {
        boolean displayed = isDisplayed(closeButton);
        log.info("Sidebar Close Button displayed check: {}", displayed);
        return displayed;
    }
}
