package com.timepieces.automation.pages.components.cards;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * ManifestaCardComponent — Component Object Model for the Manifesta Product
 * Drop Card Component. It is for the current Manifesta drop till August 2026.
 * It will be replaced by the next drop card. Card components are a temporary
 * and will always need to be archived when it's function is over.
 * 
 * Current function of the card:
 * Appears within 15 seconds if the the drop page button in the landing/homepage
 * is not selected. Can be dismissed once, then reappears after 1 minute,
 * provided the user has not gone to the associated drop page that the drop
 * product card refers to, in this case, the upcoming Manifesta product drop.
 * If the user dismisses the card a second time, then it is permanently
 * dismissed and cannot reappear even with navigation actions like page
 * refresh/back/forward, etc.
 * Also, the card is permanently dismissed if the user clicks on the card and is
 * navigated to the concerned drop page (Astonia for now, not Manifesta).
 * It is also permanently dismissed if the user navigates to the drop page
 * without clicking directly on the card but using any of the other navigation
 * links in the website.
 * Clicking on the card redirects the user to: URL:
 * https://www.timepieces-d2c.example/new-release/manifesta (ManifestaDropPage)
 * 
 * This is yet to be implemented. Will happen around end of July or beginning of
 * August.
 */

@Log4j2
public class ManifestaDropCardComponent extends BasePage<ManifestaDropCardComponent> {

    // ==========================================
    // Constructor
    // ==========================================
    public ManifestaDropCardComponent(WebDriver driver) {
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
