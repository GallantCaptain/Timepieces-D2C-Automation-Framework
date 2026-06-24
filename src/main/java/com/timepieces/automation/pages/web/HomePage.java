package com.timepieces.automation.pages.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;
import com.timepieces.automation.pages.web.products.upcomingDrops.AstoniaDropPage;

import lombok.extern.log4j.Log4j2;

/**
 * This is the Page Object Repository of all the WebElements in the Timepieces
 * HomePage.
 */
@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public class HomePage extends BasePage {
    // ==========================================
    // Constructor
    // ==========================================
    public HomePage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Homepage the full page container
    private final By homepageFullPageContaniner = By.cssSelector("main[data-testid='home-main']");

    // Homepage Hero Section Container
    private final By homepageHeroSectionContainer = By.cssSelector("section[data-testid='home-hero']");

    // Use this to locate the homepage
    // Timepieces Homepage Hero Section
    private final By heroSection = By.cssSelector("section[data-testid='home-hero-overlay']");

    // Hero mute/un-mute toggle for the background showreel video.
    private final By muteButton_In_HeroSection = By.cssSelector("button[data-testid='home-hero-mute-toggle']");

    // Hero "Discover" CTA — navigates to the current Astonia drop page.
    private final By redirectButton_To_AstoniaDropPage = By.cssSelector("button[data-testid='home-hero-discover-cta']");

    // TODO(locators): the remaining homepage sections (featured collections, drop strip,
    // brand story, footer CTA, etc.) still need their real data-testid selectors added here.
    // The 'varNameN' placeholder stubs were removed because duplicate field names / "value"
    // selectors broke compilation. Add one `private final By <descriptiveName> = By.cssSelector(...)`
    // per section, with a real data-testid, as the storefront markup is confirmed.

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Waits until the homepage has rendered, using the root {@code <main>} container as the
     * readiness signal. Use as a navigation guard after landing on the homepage before asserting
     * on any child section.
     *
     * @return this HomePage for method chaining.
     */
    public HomePage waitUntilLoaded() {
        log.info("Waiting for the homepage root container to render.");
        waitForVisibility(homepageFullPageContaniner);
        log.info("Homepage root container is visible.");
        return this;
    }

    /**
     * B.2 Toggles the hero showreel mute button (mute ↔ un-mute).
     *
     * @return this HomePage for method chaining.
     */
    public HomePage toggleHeroMute() {
        log.info("Toggling the hero section mute button.");
        clickElement(muteButton_In_HeroSection);
        return this;
    }

    /**
     * B.3 Clicks the hero "Discover" CTA, which redirects to the current Astonia drop page.
     * Returns an {@link AstoniaDropPage} so callers can continue the flow fluently.
     *
     * @return a new AstoniaDropPage for the destination view.
     */
    public AstoniaDropPage clickDiscoverCta() {
        log.info("Clicking the hero 'Discover' CTA to navigate to the Astonia drop page.");
        clickElement(redirectButton_To_AstoniaDropPage);
        return new AstoniaDropPage(driver);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /*
     * These return boolean so step definitions can pass them to assertion libraries
     * (AssertJ/TestNG) without the page object depending on any assertion framework.
     */

    /**
     * V.1 Verifies the homepage root container is displayed — a reliable "page loaded" check.
     *
     * @return true if the homepage root container is displayed, false otherwise.
     */
    public boolean isHomePageLoaded() {
        boolean displayed = isDisplayed(homepageFullPageContaniner);
        log.info("Homepage loaded (root container) check: {}", displayed);
        return displayed;
    }

    /**
     * V.2 Verifies the homepage hero section is displayed.
     *
     * @return true if the hero section is displayed, false otherwise.
     */
    public boolean isHeroSectionDisplayed() {
        boolean displayed = isDisplayed(homepageHeroSectionContainer);
        log.info("Homepage hero section displayed check: {}", displayed);
        return displayed;
    }

    /**
     * V.3 Verifies the hero overlay layer (headline/CTA band) is displayed.
     *
     * @return true if the hero overlay is displayed, false otherwise.
     */
    public boolean isHeroOverlayDisplayed() {
        boolean displayed = isDisplayed(heroSection);
        log.info("Homepage hero overlay displayed check: {}", displayed);
        return displayed;
    }

}
