package com.timepieces.automation.pages.components.cards;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * AstoniaCardComponent — Component Object Model for the Astonia Product Drop
 * Card Component. It is for the current Astonia drop till August 2026. It will
 * be replaced by the next drop card. Card components are a temporary and will
 * always need to be archived when it's function is over.
 * 
 * Current function of the card:
 * Appears within 15 seconds if the the drop page button in the landing/homepage is not selected. 
 * Can be dismissed once, then reappears after 1 minute, provided the user has not gone to the associated drop page that the drop product card refers to=, in this case, the upcoming Astonia product drop. 
 * If the user dismisses the card a second time, then it is permanently dismissed and cannot reappear even with navigation actions like page refresh/back/forward, etc. 
 * Also, the card is permanently dismissed if the user clicks on the card and is navigated to the concerned drop page (Astonia for now, not Manifesta).
 * It is also permanently dismissed if the user navigates to the drop page without clicking directly on the card but using any of the other navigation links in the website. 
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
    // Selector for any Astonia product drop link card on the catalog:
    private final By astoniaDropCard = By.cssSelector("div[data-testid='astonia-popup-card']");
    // The Card Label Message
    private final By dropCardName = By.cssSelector("h3[data-testid='astonia-popup-title']");
    //Label Card Message Text
    private final By dropCardText = By.cssSelector("h3[data-testid='astonia-popup-title'] + p");
    //The Astonia Drop Card Dismiss Button 
    private final By dismissAstoniaCardButton = By.cssSelector("button[data-testid='astonia-popup-close']");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * Clicks on the Astonia drop card found.
     * 
     * @return this AstoniaCardComponent for method chaining.
     */
    public AstoniaCardComponent clickAnyAstoniaCard() {
        log.info("Clicking the Astonia drop product card.");
        try {
            waitForVisibility(astoniaDropCard);
        }
        catch (Exception e) {
            log.info("TBD-Need to retry-Incompelete method")
        }
        clickElement(astoniaDropCard);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * Verifies if any Astonia product card is displayed on the page.
     * 
     * @return true if displayed, false otherwise.
     */
    public boolean isAnyAstoniaCardDisplayed() {
        boolean displayed = isDisplayed(astoniaDropCard);
        log.info("Astonia card presence check: {}", displayed);
        return displayed;
    }
}
