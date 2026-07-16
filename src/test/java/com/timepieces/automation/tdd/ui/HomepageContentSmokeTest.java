package com.timepieces.automation.tdd.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.pages.components.FooterComponent;
import com.timepieces.automation.pages.components.StoriesModalComponent;
import com.timepieces.automation.pages.components.cards.AstoniaDropCardComponent;
import com.timepieces.automation.pages.web.HomePage;
import com.timepieces.automation.pages.web.StagingGatePage;

import lombok.extern.log4j.Log4j2;

/**
 * HomepageContentSmokeTest — a TDD (TestNG) smoke that drives the completed
 * homepage Page/Component Objects against the live storefront: the full
 * section sweep (hero video → Two Stories → parallax → F&P → Last Release →
 * sequence → USP), the stories modal open/pause/close, the drop-popup
 * sessionStorage suppression, and the footer country-selector dropdown.
 *
 * <p>
 * OPT-IN: this class is NOT part of the default {@code testng.xml} suite (it
 * needs a real browser and reaches the live site). Run it explicitly via its own
 * suite:
 * {@code mvnw -o test "-Dsurefire.suite=testng-homepage-smoke.xml" "-Denv=production"}.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of HomepageContentSmokeTest"
* @since 2026-07-16
 */

@Log4j2
public class HomepageContentSmokeTest {

    private WebDriver driver;

    // ==========================================
    // Lifecycle
    // ==========================================
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
        this.driver = DriverFactory.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            DriverFactory.removeDriver();
        }
    }

    // ==========================================
    // Private helpers
    // ==========================================
    /** Opens the homepage, unlocking the staging gate when present. */
    private HomePage openHomepage() {
        HomePage page = new HomePage(driver);
        driver.get(page.getBaseUrl());
        if (new StagingGatePage(driver).unlockIfPresent()) {
            driver.get(page.getBaseUrl());
        }
        return page.waitUntilLoaded();
    }

    // ==========================================
    // Tests
    // ==========================================
    @Test(groups = { "ui-smoke", "homepage" })
    public void homepageSectionSweepRenders() {
        HomePage page = openHomepage();
        assertThat(page.isHeroSectionDisplayed()).as("Hero section").isTrue();
        assertThat(page.isHeroVideoPresent()).as("Hero showreel video").isTrue();
        assertThat(page.isTwoStoriesSectionDisplayed()).as("Two Stories section").isTrue();
        assertThat(page.isManifestaStoryLocked()).as("Manifesta LOCKED badge").isTrue();
        assertThat(page.isBrandStoryDisplayed()).as("Brand-story parallax").isTrue();
        assertThat(page.isFriendsPartnersSectionDisplayed()).as("Friends & Partners section").isTrue();
        assertThat(page.allPartnerCardsLinkToProfiles()).as("Partner cards link to profiles").isTrue();
        assertThat(page.isLastReleaseSliderDisplayed()).as("Last Release slider").isTrue();
        assertThat(page.allLastReleaseCardsLinkToPdps()).as("Last Release cards link to PDPs").isTrue();
        assertThat(page.isCraftedWithIntentDisplayed()).as("Crafted With Intent sequence").isTrue();
        assertThat(page.isUspMarqueeDisplayed()).as("FEATURED IN marquee").isTrue();
    }

    @Test(groups = { "ui-smoke", "homepage" })
    public void storiesModalOpensPausesAndCloses() {
        openHomepage();
        StoriesModalComponent stories = new StoriesModalComponent(driver);
        stories.openStories();
        assertThat(stories.isOpen()).as("Stories modal should open").isTrue();
        assertThat(stories.getStoryCount()).as("At least one story").isGreaterThan(0);
        stories.togglePause();
        assertThat(stories.isPaused()).as("Stories should pause").isTrue();
        stories.closeStories();
        assertThat(stories.isOpen()).as("Stories modal should close").isFalse();
    }

    @Test(groups = { "ui-smoke", "homepage" })
    public void dropPopupIsSuppressibleViaSessionState() {
        openHomepage();
        // Suppress BEFORE the ~15s mount fires, then reload — the popup must not mount.
        ((JavascriptExecutor) driver).executeScript(
                String.format("window.sessionStorage.setItem('%s','1');",
                        AstoniaDropCardComponent.POPUP_VISITED_KEY));
        driver.navigate().refresh();
        new HomePage(driver).waitUntilLoaded();
        assertThat(new AstoniaDropCardComponent(driver).isAstoniaDropCardDisplayedWithin(20))
                .as("The suppressed drop popup should NOT mount").isFalse();
    }

    @Test(groups = { "ui-smoke", "homepage" })
    public void countrySelectorOpensAndFilters() {
        openHomepage();
        FooterComponent footer = new FooterComponent(driver);
        footer.clickCountrySelector();
        assertThat(footer.isCountryDropdownOpen()).as("Country dropdown should open").isTrue();
        int before = footer.getCountryOptionCount();
        footer.searchCountry("United");
        int after = footer.getCountryOptionCount();
        assertThat(after).as("Search should filter the options (before=%s)", before)
                .isGreaterThan(0).isLessThan(before);
    }
}
