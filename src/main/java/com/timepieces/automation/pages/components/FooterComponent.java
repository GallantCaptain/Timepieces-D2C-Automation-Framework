package com.timepieces.automation.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * FooterComponent class represents the footer section of the web application.
 * It extends the BasePage class, which provides common functionalities for all
 * page components. The FooterComponent class contains a WebDriver instance to
 * interact with the web elements in the footer section. The constructor
 * initializes the WebDriver instance when an object of FooterComponent is
 * created.
 */
@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public class FooterComponent extends BasePage {

    // ==========================================
    // Constructor
    // ==========================================
    public FooterComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Site Footer Container:
    private final By siteFooterContainer = By.cssSelector("[data-testid='site-footer']");
    
    // Footer Desktop Container:
    private final By footerDesktopContainer = By.cssSelector("[data-testid='footer-desktop']");
    
    // Footer Mobile Container:
    private final By footerMobileContainer = By.cssSelector("[data-testid='footer-mobile']");
    
    // Footer Accordion Columns:
    private final By footerAccordionColumns = By.cssSelector("[data-testid='footer-accordion']");
    
    // Footer Accordion Toggle Buttons:
    private final By footerAccordionToggles = By.cssSelector("[data-testid='footer-accordion-toggle']");
    
    // Footer Accordion Links:
    private final By footerAccordionLinks = By.cssSelector("[data-testid='footer-accordion-link']");
    
    // Footer Socials Section:
    private final By footerSocialsSection = By.cssSelector("[data-testid='footer-socials']");
    
    // Footer Social Link:
    private final By footerSocialLinks = By.cssSelector("[data-testid='footer-social-link']");
    
    // Country Selector Wrapper:
    private final By countrySelectorWrapper = By.cssSelector("[data-testid='country-selector']");
    
    // Country Selector Button / Toggle:
    private final By countrySelectorToggle = By.cssSelector("[data-testid='country-selector-toggle']");
    
    // Footer Legal Links:
    private final By footerLegalLinks = By.cssSelector("[data-testid='footer-legal']");
    
    // Footer Sitemap Link:
    private final By footerSitemapLink = By.cssSelector("[data-testid='footer-sitemap']");
    
    // Footer Copyright Text:
    private final By footerCopyrightText = By.cssSelector("[data-testid='footer-copyright']");
    
    // Footer Menu Columns (Desktop):
    private final By footerMenuColumns = By.cssSelector("[data-testid='footer-menu-column']");
    
    // Footer Menu Links (Desktop):
    private final By footerMenuLinks = By.cssSelector("[data-testid='footer-menu-link']");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * Clicks on the country selector toggle in the footer.
     * @return this FooterComponent for method chaining.
     */
    public FooterComponent clickCountrySelector() {
        log.info("Clicking footer country selector toggle.");
        clickElement(countrySelectorToggle);
        return this;
    }

    /**
     * Retrieves the text displayed in the copyright element of the footer.
     * @return copyright text as String.
     */
    public String getCopyrightText() {
        log.info("Retrieving copyright text from footer.");
        return getText(footerCopyrightText);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * Verifies if the footer container is displayed on the page.
     * @return true if displayed, false otherwise.
     */
    public boolean isFooterDisplayed() {
        boolean displayed = isDisplayed(siteFooterContainer);
        log.info("Site Footer displayed check: {}", displayed);
        return displayed;
    }

    /**
     * Verifies if the country selector toggle is displayed.
     * @return true if displayed, false otherwise.
     */
    public boolean isCountrySelectorDisplayed() {
        boolean displayed = isDisplayed(countrySelectorToggle);
        log.info("Country selector toggle displayed check: {}", displayed);
        return displayed;
    }
}
