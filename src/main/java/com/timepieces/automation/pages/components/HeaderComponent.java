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
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (HeaderComponent) to allow method chaining in step definitions.
 * 
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of HeaderComponent"
 */

@Log4j2
public class HeaderComponent extends BasePage<HeaderComponent> {

    // ==========================================
    // Constructor
    // ==========================================
    public HeaderComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Site Header Container: [Represents the entire Header section]
    private final By siteHeaderContainer = By.cssSelector("header[data-testid='site-header']");

    // Header Menu Toggle Button: [Opens the SidebarMenuComponent]
    private final By headerMenuToggle = By.cssSelector("button[data-testid='header-menu-toggle']");

    // Header Logo Link: [Navigates to the Home Page]
    private final By headerLogo = By.cssSelector("a[data-testid='header-logo']");

    // Header Stories Toggle Button: [Opens the StoriesPage]
    private final By headerStoriesToggle = By.cssSelector("button[data-testid='header-stories-toggle']");

    // Header Search Toggle Button: [Opens the Global Search Bar]
    private final By headerSearchToggle = By.cssSelector("button[data-testid='header-search-toggle']");

    // Header Account Toggle Button: [Opens the Account Page/Login Page]
    private final By headerAccountToggle = By.cssSelector("button[data-testid='header-account-toggle']");

    // Header Cart Toggle Button: [Opens the CartDrawerComponent]
    private final By headerCartToggle = By.cssSelector("button[data-testid='header-cart-toggle']");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Clicks on the Menu toggle button to open the sidebar navigation menu.
     * 
     * @return this HeaderComponent for method chaining.
     */
    public HeaderComponent clickMenuToggle() {
        log.info("Clicking header menu toggle.");
        clickElement(headerMenuToggle);
        return this;
    }

    /**
     * B.2 Clicks on the Stories toggle button.
     * 
     * @return this HeaderComponent for method chaining.
     */
    public HeaderComponent clickStoriesToggle() {
        log.info("Clicking header stories toggle.");
        clickElement(headerStoriesToggle);
        return this;
    }

    /**
     * B.3 Clicks on the brand logo link to navigate back to home.
     * 
     * @return this HeaderComponent for method chaining.
     */
    public HeaderComponent clickLogo() {
        log.info("Clicking brand logo link in header.");
        clickElement(headerLogo);
        return this;
    }

    /**
     * B.4 Clicks on the Search toggle button.
     * 
     * @return this HeaderComponent for method chaining.
     */
    public HeaderComponent clickSearchToggle() {
        log.info("Clicking search toggle button.");
        clickElement(headerSearchToggle);
        return this;
    }

    /**
     * B.5 Clicks on the Account toggle button to open the account page/login.
     * 
     * @return this HeaderComponent for method chaining.
     */
    public HeaderComponent clickAccountToggle() {
        log.info("Clicking account toggle button.");
        clickElement(headerAccountToggle);
        return this;
    }

    /**
     * B.6 Clicks on the Cart toggle button to open the cart drawer.
     * 
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
     * V.1 Verifies if the header container is displayed on the page.
     * 
     * @return true if displayed, false otherwise.
     */
    public boolean isHeaderDisplayed() {
        boolean displayed = isDisplayed(siteHeaderContainer);
        log.info("Site Header displayed check: {}", displayed);
        return displayed;
    }
}
