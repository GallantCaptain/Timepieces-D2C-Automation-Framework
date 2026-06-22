package com.timepieces.automation.pages.components;

import org.openqa.selenium.By;
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
    // Site Header Container:
    private final By siteHeaderContainer = By.cssSelector("[data-testid='site-header']");
    
    // Header Menu Toggle Button:
    private final By headerMenuToggle = By.cssSelector("[data-testid='header-menu-toggle']");
    
    // Header Stories Toggle Button:
    private final By headerStoriesToggle = By.cssSelector("[data-testid='header-stories-toggle']");
    
    // Header Logo Link:
    private final By headerLogo = By.cssSelector("[data-testid='header-logo']");
    
    // Header Search Toggle Button:
    private final By headerSearchToggle = By.cssSelector("[data-testid='header-search-toggle']");
    
    // Header Account Toggle Button:
    private final By headerAccountToggle = By.cssSelector("[data-testid='header-account-toggle']");
    
    // Header Cart Toggle Button:
    private final By headerCartToggle = By.cssSelector("[data-testid='header-cart-toggle']");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * Clicks on the Menu toggle button to open the sidebar navigation menu.
     * @return this HeaderComponent for method chaining.
     */
    public HeaderComponent clickMenuToggle() {
        log.info("Clicking header menu toggle.");
        clickElement(headerMenuToggle);
        return this;
    }

    /**
     * Clicks on the Stories toggle button.
     * @return this HeaderComponent for method chaining.
     */
    public HeaderComponent clickStoriesToggle() {
        log.info("Clicking header stories toggle.");
        clickElement(headerStoriesToggle);
        return this;
    }

    /**
     * Clicks on the brand logo link to navigate back to home.
     * @return this HeaderComponent for method chaining.
     */
    public HeaderComponent clickLogo() {
        log.info("Clicking brand logo link in header.");
        clickElement(headerLogo);
        return this;
    }

    /**
     * Clicks on the Search toggle button.
     * @return this HeaderComponent for method chaining.
     */
    public HeaderComponent clickSearchToggle() {
        log.info("Clicking search toggle button.");
        clickElement(headerSearchToggle);
        return this;
    }

    /**
     * Clicks on the Account toggle button to open the account page/login.
     * @return this HeaderComponent for method chaining.
     */
    public HeaderComponent clickAccountToggle() {
        log.info("Clicking account toggle button.");
        clickElement(headerAccountToggle);
        return this;
    }

    /**
     * Clicks on the Cart toggle button to open the cart drawer.
     * @return this HeaderComponent for method chaining.
     */
    public HeaderComponent clickCartToggle() {
        log.info("Clicking cart toggle button.");
        clickElement(headerCartToggle);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * Verifies if the header container is displayed on the page.
     * @return true if displayed, false otherwise.
     */
    public boolean isHeaderDisplayed() {
        boolean displayed = isDisplayed(siteHeaderContainer);
        log.info("Site Header displayed check: {}", displayed);
        return displayed;
    }
}
