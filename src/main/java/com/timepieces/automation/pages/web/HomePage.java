package com.timepieces.automation.pages.web;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.timepieces.automation.pages.BasePage;
import com.timepieces.automation.utils.BrowserUtils;
import com.timepieces.automation.utils.ConfigReader;
import com.timepieces.automation.utils.WindowUtils;

import lombok.extern.log4j.Log4j2;

/**
 * This is the Page Object Repository of all the WebElements in the Timepieces HomePage.
 */
@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public class HomePage extends BasePage {
    /********* Constructor *********/
    public HomePage(WebDriver driver) {
        super(driver);
    }

    /********* Locators *********/

    /**
     * Checks if the brand logo is visible on the homepage/storefront.
     *
     * @param logoSelector The CSS selector for the logo element.
     * @return true if visible, false otherwise.
     */
    public boolean isLogoVisible(String logoSelector) {
        log.info("Checking visibility of logo using selector: {}", logoSelector);
        try {
            By locator = By.cssSelector(logoSelector);
            return isDisplayed(locator);
        } catch (Exception e) {
            log.error("Logo with selector {} was not visible", logoSelector, e);
            return false;
        }
    }
}
