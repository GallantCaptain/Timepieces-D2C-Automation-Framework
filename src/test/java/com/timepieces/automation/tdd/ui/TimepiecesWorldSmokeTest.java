package com.timepieces.automation.tdd.ui;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.pages.web.otherPages.timepieces_world.TimepiecesWorld_AstoniaPage;
import com.timepieces.automation.pages.web.otherPages.timepieces_world.TimepiecesWorld_ExperiencePage;
import com.timepieces.automation.pages.web.otherPages.timepieces_world.TimepiecesWorld_MonarchPage;
import com.timepieces.automation.pages.web.otherPages.timepieces_world.components.TimepiecesWorld_HeaderComponent;
import com.timepieces.automation.pages.web.otherPages.timepieces_world.components.TimepiecesWorld_SidebarMenuComponent;

import lombok.extern.log4j.Log4j2;

/**
 * TimepiecesWorldSmokeTest — a TDD (TestNG) smoke that drives the Timepieces World
 * page objects against the live microsite ({@code world.timepieces-d2c.example}, a fixed
 * public origin independent of the configured {@code base.url}): the experience
 * welcome gate + canvas + progress rail, a representative showcase's shared
 * template (logo/tagline/specs/footer), the waitlist disabled-until-valid gate
 * (NEVER submitted — real signup), the sidebar's 5 family links, and the
 * header logo's dual navigation from a showcase back to the experience.
 *
 * <p>
 * OPT-IN: this class is NOT part of the default {@code testng.xml} suite (it
 * needs a real browser and reaches the live site). Run it explicitly via its own
 * suite:
 * {@code mvnw -o test "-Dsurefire.suite=testng-timepieces-world-smoke.xml" "-Denv=production"}.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TimepiecesWorldSmokeTest"
* @since 2026-07-16
 */

@Log4j2
public class TimepiecesWorldSmokeTest {

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
    // Tests
    // ==========================================
    @Test(groups = { "ui-smoke", "timepieces-world" })
    public void experienceGateStartsTheCanvas() {
        TimepiecesWorld_ExperiencePage page = new TimepiecesWorld_ExperiencePage(driver).open();
        assertThat(page.isEnterExperienceDisplayed())
                .as("Experience: the 'Enter Experience' gate should be displayed").isTrue();
        page.enterExperience();
        assertThat(page.isCanvasDisplayed())
                .as("Experience: the canvas should be displayed after entering").isTrue();
        assertThat(page.isProgressRailDisplayed())
                .as("Experience: the progress rail should be displayed").isTrue();
        assertThat(page.getProgressSegmentCount())
                .as("Experience: the rail should show the 7 scenes").isEqualTo(7);
    }

    @Test(groups = { "ui-smoke", "timepieces-world" })
    public void canvasNudgesToAFamilySceneWithDiscover() {
        TimepiecesWorld_ExperiencePage page = new TimepiecesWorld_ExperiencePage(driver).open();
        page.enterExperience();
        new WebDriverWait(driver, Duration.ofSeconds(90)).until(d -> {
            if (page.isDiscoverCtaDisplayed()) {
                return true;
            }
            page.advanceCanvas(3000);
            return false;
        });
        assertThat(page.getActiveFamilyHeading())
                .as("Experience: a family heading should accompany the DISCOVER CTA").isNotBlank();
    }

    @Test(groups = { "ui-smoke", "timepieces-world" })
    public void showcaseRendersTheSharedTemplate() {
        TimepiecesWorld_AstoniaPage page = new TimepiecesWorld_AstoniaPage(driver).open();
        assertThat(page.isPageTitleCorrect())
                .as("Showcase: title should match '<Family> | <Tagline> | Timepieces Watches'").isTrue();
        assertThat(page.isFamilyLogoDisplayed())
                .as("Showcase: the ASTONIA logo should be displayed").isTrue();
        assertThat(page.hasSpec("BASE CALIBRE"))
                .as("Showcase: the specs grid should list BASE CALIBRE").isTrue();
        assertThat(page.getLearnMoreButtonCount())
                .as("Showcase: material learn-more buttons should be offered").isGreaterThan(0);
        assertThat(page.areFooterLinksPresent())
                .as("Showcase: the footer support links should be present").isTrue();
    }

    @Test(groups = { "ui-smoke", "timepieces-world" })
    public void waitlistSubmitIsGatedUntilValid() {
        TimepiecesWorld_AstoniaPage page = new TimepiecesWorld_AstoniaPage(driver).open();
        assertThat(page.isWaitlistFormDisplayed())
                .as("Waitlist: the signup form should be present").isTrue();
        assertThat(page.isWaitlistSubmitEnabled())
                .as("Waitlist: submit should start DISABLED (never submitted — real signup)").isFalse();
    }

    @Test(groups = { "ui-smoke", "timepieces-world" })
    public void sidebarListsAllFiveFamilies() {
        new TimepiecesWorld_ExperiencePage(driver).open();
        TimepiecesWorld_HeaderComponent header = new TimepiecesWorld_HeaderComponent(driver);
        TimepiecesWorld_SidebarMenuComponent sidebar = new TimepiecesWorld_SidebarMenuComponent(driver);
        header.toggleMenu();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> sidebar.isOpen());
        assertThat(sidebar.getFamilyLinkCount())
                .as("Sidebar: all 5 family links should be listed").isEqualTo(5);
    }

    @Test(groups = { "ui-smoke", "timepieces-world" })
    public void headerLogoReturnsToExperienceFromAShowcase() {
        new TimepiecesWorld_MonarchPage(driver).open();
        new TimepiecesWorld_HeaderComponent(driver).clickLogo();
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> d.getCurrentUrl().contains("world.timepieces-d2c.example")
                        && !d.getCurrentUrl().contains("/products/"));
        assertThat(driver.getCurrentUrl())
                .as("Header logo: from a showcase it should return to the experience page")
                .contains("world.timepieces-d2c.example").doesNotContain("/products/");
    }
}
