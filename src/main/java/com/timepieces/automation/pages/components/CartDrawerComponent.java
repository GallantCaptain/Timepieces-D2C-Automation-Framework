package com.timepieces.automation.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * CartDrawerComponent — Component Object Model for the Cart Drawer Component.
 */
@Log4j2
public class CartDrawerComponent extends BasePage {

    // ==========================================
    // Constructor
    // ==========================================
    public CartDrawerComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Cart Drawer Overlay:
    private final By cartDrawerOverlay = By.cssSelector("[data-testid='cart-drawer-overlay']");
    
    // Cart Drawer Container:
    private final By cartDrawerContainer = By.cssSelector("[data-testid='cart-drawer']");
    
    // Cart Drawer Title:
    private final By cartDrawerTitle = By.cssSelector("[data-testid='cart-drawer-title']");
    
    // Close Button:
    private final By closeButton = By.cssSelector("[data-testid='cart-drawer-close']");
    
    // Cart Drawer Content Area:
    private final By contentArea = By.cssSelector("[data-testid='cart-drawer-content']");
    
    // Empty Cart State:
    private final By emptyCartState = By.cssSelector("[data-testid='cart-drawer-empty']");
    
    // Empty State CTA Link:
    private final By emptyStateCtaLink = By.cssSelector("[data-testid='cart-drawer-empty-cta']");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * Clicks the close button to dismiss the cart drawer.
     * @return this CartDrawerComponent for method chaining.
     */
    public CartDrawerComponent clickCloseButton() {
        log.info("Clicking cart drawer close button.");
        clickElement(closeButton);
        return this;
    }

    /**
     * Clicks the CTA link inside the empty cart drawer to return to shopping.
     * @return this CartDrawerComponent for method chaining.
     */
    public CartDrawerComponent clickEmptyStateCta() {
        log.info("Clicking cart drawer empty state CTA link.");
        clickElement(emptyStateCtaLink);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * Verifies if the cart drawer container is displayed.
     * @return true if displayed, false otherwise.
     */
    public boolean isCartDrawerDisplayed() {
        boolean displayed = isDisplayed(cartDrawerContainer);
        log.info("Cart Drawer displayed check: {}", displayed);
        return displayed;
    }

    /**
     * Verifies if the cart is empty.
     * @return true if empty cart state is displayed, false otherwise.
     */
    public boolean isCartEmpty() {
        boolean empty = isDisplayed(emptyCartState);
        log.info("Cart empty check: {}", empty);
        return empty;
    }
}