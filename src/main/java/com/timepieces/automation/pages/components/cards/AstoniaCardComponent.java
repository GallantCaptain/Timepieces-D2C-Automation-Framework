package com.timepieces.automation.pages.components.cards;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * AstoniaCardComponent — Component Object Model for the Astonia Card Component.
 */
@Log4j2
public class AstoniaCardComponent extends BasePage {

    // ==========================================
    // Constructor
    // ==========================================
    public AstoniaCardComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Selector for any Astonia product link card on the catalog:
    private final By anyAstoniaProductCard = By.cssSelector("a[href*='/products/astonia-']");

    // Dynamic XPath template to target a specific Astonia product card by its URL handle:
    private final By specificAstoniaProductCardTemplate = By.xpath("//a[contains(@href, '/products/astonia-%s')]");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * Clicks on the first Astonia product card found.
     * @return this AstoniaCardComponent for method chaining.
     */
    public AstoniaCardComponent clickAnyAstoniaCard() {
        log.info("Clicking the first available Astonia product card.");
        clickElement(anyAstoniaProductCard);
        return this;
    }

    /**
     * Clicks on a specific Astonia product card variant (e.g. "dusk-gold", "stealth-silver").
     * @param variantHandle the handle string of the variant (e.g. "dusk-gold").
     * @return this AstoniaCardComponent for method chaining.
     */
    public AstoniaCardComponent clickSpecificAstoniaCard(String variantHandle) {
        log.info("Clicking Astonia product card with variant handle: {}", variantHandle);
        By formattedLocator = formatLocator(specificAstoniaProductCardTemplate, variantHandle);
        clickElement(formattedLocator);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * Verifies if any Astonia product card is displayed on the page.
     * @return true if displayed, false otherwise.
     */
    public boolean isAnyAstoniaCardDisplayed() {
        boolean displayed = isDisplayed(anyAstoniaProductCard);
        log.info("Astonia card presence check: {}", displayed);
        return displayed;
    }
}
