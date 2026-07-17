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
        // Entered via the contact screen, NOT openSection(SECTION_NAME): the name
        // screen does not deep-link — it redirects to contact (verified
        // 2026-07-17), and contact has no step indicator, so the old entry sat in
        // a 15s wait for "Step 01 of 07" on a screen that never shows one. That
        // was this test's only failure; the gate itself works.
        // openSection() first purely to clear the staging gate on this fresh
        // driver; enterJourneyAtPersonalInfo() then walks contact -> Step 01.
        Onboarding_BecomeMore page = openSection(Onboarding_BecomeMore.SECTION_CONTACT);
        page.enterJourneyAtPersonalInfo();

        // isEqualTo, not contains: the step indicator now resolves to the single
        // <span> that holds the text, so the exact string is assertable. The old
        // locator matched 6 ancestor containers and returned the whole page text,
        // where a contains() check would pass for ANY step.
        assertThat(page.getStepIndicatorText())
                .as("Step 1 indicator").isEqualTo("Step 01 of 07");

        // The gate is asserted from a state this test CREATES, not from the one it
        // happens to arrive in. The screen restores a saved draft (see the page
        // object's DRAFT-RESTORE note), so on arrival the form is already valid and
        // Next is legitimately enabled — asserting "starts disabled" here would be
        // asserting that nobody has ever used the form, which production cannot
        // promise. Emptying a required field re-arms the real gate.
        page.clearFullName();
        assertThat(page.isNextEnabled())
                .as("Next should be DISABLED while a required field is empty").isFalse();
        page.fillPersonalInfo("Test User", "Mr", "01/01/1990", "Professional");
        assertThat(page.isNextEnabled())
                .as("Next should ENABLE once all four fields validate").isTrue();
    }

    @Test(groups = { "ui-smoke", "becomemore" })
    public void interestsMultiSelectGatesProgression() {
        Onboarding_BecomeMore page = openSection(Onboarding_BecomeMore.SECTION_INTEREST);
        // Clear first: this screen also arrives with chips selected from the
        // restored draft, which opens the gate AND makes the click below toggle
        // "Watches" OFF rather than on.
        page.clearAllSelectedChips();
        assertThat(page.isNextEnabled())
                .as("Next should be DISABLED with no interests picked").isFalse();
        page.selectChip("Watches");
        assertThat(page.isChipSelected("Watches"))
                .as("'Watches' chip should render selected").isTrue();
        assertThat(page.isNextEnabled())
                .as("Next should ENABLE after at least one interest").isTrue();
    }

    @Test(groups = { "ui-smoke", "becomemore" })
    public void photoConsentGatesTheSubmit() {
        // The photo screen CANNOT be deep-linked — ?section=photo redirects to
        // contact, as does every alias tried on 2026-07-17 (avatar, profilephoto,
        // picture, image, submit, review, final). The old
        // openSection(SECTION_PHOTO) therefore asserted against the contact
        // screen, which has no avatar and no consent box, and reported the
        // consent gate as missing. The gate is fine; the entry was impossible.
        // Walking all 9 screens is the only way in.
        openSection(Onboarding_BecomeMore.SECTION_CONTACT); // Clears the staging gate first.
        Onboarding_BecomeMore page = new Onboarding_BecomeMore(driver).walkToPhotoStep();

        assertThat(page.isOnSection(Onboarding_BecomeMore.SECTION_PHOTO))
                .as("The walk should land on the photo screen").isTrue();
        assertThat(page.isPhotoStepDisplayed())
                .as("Photo step: avatar + consent should render").isTrue();
        assertThat(page.isSubmitProfileEnabled())
                .as("'Submit Profile' should start DISABLED").isFalse();

        page.tickConsent();

        // Assert-only: NEVER click Submit Profile (it creates a real profile).
        // The whole test is the disabled -> enabled TRANSITION.
        assertThat(page.waitUntilSubmitProfileEnabled())
                .as("'Submit Profile' should ENABLE once consent is ticked").isTrue();
    }
}
