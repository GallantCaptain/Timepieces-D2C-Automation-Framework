package com.timepieces.automation.tdd.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.pages.web.StagingGatePage;
import com.timepieces.automation.pages.web.account.Onboarding_BecomeMore;

import lombok.extern.log4j.Log4j2;

/**
 * BecomeMoreSmokeTest — a TDD (TestNG) smoke that drives the new
 * {@link Onboarding_BecomeMore} page object against the live storefront: the
 * intro + method dialog, the step-1 validation gate, the interests
 * multi-select gate, and the photo-step consent gate. The final "Submit
 * Profile" is NEVER clicked (it creates a real profile).
 *
 * <p>
 * OPT-IN: this class is NOT part of the default {@code testng.xml} suite (it
 * needs a real browser and reaches the live site). Run it explicitly via its own
 * suite:
 * {@code mvnw -o test "-Dsurefire.suite=testng-becomemore-smoke.xml" "-Denv=production"}.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of BecomeMoreSmokeTest"
* @since 2026-07-16
 */

@Log4j2
public class BecomeMoreSmokeTest {

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
    /** Opens a Become More section, unlocking the staging gate when present. */
    private Onboarding_BecomeMore openSection(String sectionKey) {
        Onboarding_BecomeMore page = new Onboarding_BecomeMore(driver);
        page.openSection(sectionKey);
        if (new StagingGatePage(driver).unlockIfPresent()) {
            page.openSection(sectionKey);
        }
        return page;
    }

    // ==========================================
    // Tests
    // ==========================================
    @Test(groups = { "ui-smoke", "becomemore" })
    public void introOpensTheMethodDialog() {
        Onboarding_BecomeMore page = new Onboarding_BecomeMore(driver).open();
        if (new StagingGatePage(driver).unlockIfPresent()) {
            page.open();
        }
        assertThat(page.isIntroDisplayed())
                .as("Intro: 'Become More' + begin button should render").isTrue();
        page.clickBeginProfile();
        assertThat(page.isMethodDialogDisplayed())
                .as("Method dialog: LinkedIn import + Start Manually should render").isTrue();
    }

    @Test(groups = { "ui-smoke", "becomemore" })
    public void personalInfoStepGatesUntilValid() {
        Onboarding_BecomeMore page = openSection(Onboarding_BecomeMore.SECTION_NAME);
        assertThat(page.getStepIndicatorText())
                .as("Step 1 indicator").contains("Step 01 of 07");
        assertThat(page.isNextEnabled())
                .as("Next should start DISABLED").isFalse();
        page.fillPersonalInfo("Test User", "Mr", "01/01/1990", "Professional");
        assertThat(page.isNextEnabled())
                .as("Next should ENABLE once all four fields validate").isTrue();
    }

    @Test(groups = { "ui-smoke", "becomemore" })
    public void interestsMultiSelectGatesProgression() {
        Onboarding_BecomeMore page = openSection(Onboarding_BecomeMore.SECTION_INTEREST);
        assertThat(page.isNextEnabled())
                .as("Next should start DISABLED with no interests picked").isFalse();
        page.selectChip("Watches");
        assertThat(page.isChipSelected("Watches"))
                .as("'Watches' chip should render selected").isTrue();
        assertThat(page.isNextEnabled())
                .as("Next should ENABLE after at least one interest").isTrue();
    }

    @Test(groups = { "ui-smoke", "becomemore" })
    public void photoConsentGatesTheSubmit() {
        Onboarding_BecomeMore page = openSection(Onboarding_BecomeMore.SECTION_PHOTO);
        assertThat(page.isPhotoStepDisplayed())
                .as("Photo step: avatar + consent should render").isTrue();
        assertThat(page.isSubmitProfileEnabled())
                .as("'Submit Profile' should start DISABLED").isFalse();
        page.tickConsent();
        // Assert-only: NEVER click Submit Profile (it creates a real profile).
        assertThat(page.isSubmitProfileEnabled())
                .as("'Submit Profile' should ENABLE once consent is ticked").isTrue();
    }
}
