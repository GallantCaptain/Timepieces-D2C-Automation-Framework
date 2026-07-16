package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.web.StagingGatePage;
import com.timepieces.automation.pages.web.account.Onboarding_BecomeMore;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

/**
 * BecomeMoreSteps — glue for BecomeMore_Onboarding.feature (Phase 7A mapping),
 * driving the new {@link Onboarding_BecomeMore} page object.
 *
 * <p>
 * The /becomemore journey is PUBLIC (no login gate). The final "Submit Profile"
 * creates a REAL profile, so NO step here ever clicks it — the consent-gate
 * scenario only asserts the button becomes enabled. Single-select chips
 * auto-advance the journey, so the section assertions wait on the URL's
 * {@code section=} query value.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of BecomeMoreSteps"
* @since 2026-07-16
 */

@Log4j2
public class BecomeMoreSteps {

    @SuppressWarnings("unused")
    private final TestContext testContext;
    private final WebDriver driver;

    private Onboarding_BecomeMore becomeMore;

    public BecomeMoreSteps(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverFactory.getDriver();
    }

    // ==========================================
    // Navigation
    // ==========================================
    @Given("the user navigates to the Become More onboarding")
    public void the_user_navigates_to_the_become_more_onboarding() {
        log.info("STEP: open /becomemore.");
        becomeMore = new Onboarding_BecomeMore(driver).open();
        if (new StagingGatePage(driver).unlockIfPresent()) {
            becomeMore.open();
        }
    }

    @Given("the user opens the Become More section {string}")
    public void the_user_opens_the_become_more_section(String sectionKey) {
        log.info("STEP: deep-link to the Become More section '{}'.", sectionKey);
        becomeMore = new Onboarding_BecomeMore(driver);
        becomeMore.openSection(sectionKey);
        if (new StagingGatePage(driver).unlockIfPresent()) {
            becomeMore.openSection(sectionKey);
        }
    }

    // ==========================================
    // Intro + method dialog
    // ==========================================
    @Then("the Become More intro screen is displayed")
    public void the_become_more_intro_screen_is_displayed() {
        assertThat(becomeMore.isIntroDisplayed())
                .as("The Become More intro screen should be displayed").isTrue();
    }

    @When("the user begins their profile")
    public void the_user_begins_their_profile() {
        becomeMore.clickBeginProfile();
    }

    @Then("the profile method dialog is displayed with both entry options")
    public void the_profile_method_dialog_is_displayed_with_both_entry_options() {
        assertThat(becomeMore.isMethodDialogDisplayed())
                .as("The method dialog should offer LinkedIn import AND manual entry").isTrue();
    }

    @When("the user closes the profile method dialog")
    public void the_user_closes_the_profile_method_dialog() {
        becomeMore.closeMethodDialog();
    }

    // ==========================================
    // Steps + progression
    // ==========================================
    @Then("the step indicator reads {string}")
    public void the_step_indicator_reads(String expected) {
        assertThat(becomeMore.getStepIndicatorText())
                .as("The step indicator should read '%s'", expected).contains(expected);
    }

    @And("the next step control is disabled")
    public void the_next_step_control_is_disabled() {
        assertThat(becomeMore.isNextEnabled())
                .as("The Next control should be DISABLED until the step validates").isFalse();
    }

    @Then("the next step control is enabled")
    public void the_next_step_control_is_enabled() {
        assertThat(becomeMore.isNextEnabled())
                .as("The Next control should be ENABLED once the step validates").isTrue();
    }

    @When("the user fills the personal info with {string}, {string}, {string} and {string}")
    public void the_user_fills_the_personal_info_with_and(String name, String salutation, String dob, String persona) {
        becomeMore.fillPersonalInfo(name, salutation, dob, persona);
    }

    @Then("the browser is on the Become More section {string}")
    public void the_browser_is_on_the_become_more_section(String sectionKey) {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> becomeMore.isOnSection(sectionKey));
        assertThat(becomeMore.isOnSection(sectionKey))
                .as("The browser should sit on section '%s'", sectionKey).isTrue();
    }

    @And("the step heading contains {string}")
    public void the_step_heading_contains(String fragment) {
        assertThat(becomeMore.getStepHeadingText())
                .as("The step heading should contain '%s'", fragment).contains(fragment);
    }

    // ==========================================
    // Chips
    // ==========================================
    @When("the user selects the chip {string}")
    public void the_user_selects_the_chip(String chipText) {
        becomeMore.selectChip(chipText);
    }

    @Then("the chip {string} is selected")
    public void the_chip_is_selected(String chipText) {
        assertThat(becomeMore.isChipSelected(chipText))
                .as("The chip '%s' should render selected", chipText).isTrue();
    }

    // ==========================================
    // Timepieces House
    // ==========================================
    @When("the user opens the curated inspirations")
    public void the_user_opens_the_curated_inspirations() {
        becomeMore.clickNeedInspiration();
    }

    @Then("the curated inspirations are displayed")
    public void the_curated_inspirations_are_displayed() {
        assertThat(becomeMore.isCuratedInspirationsDisplayed())
                .as("The curated inspirations should be displayed").isTrue();
    }

    @When("the user skips the house step")
    public void the_user_skips_the_house_step() {
        becomeMore.skipHouseStep();
    }

    // ==========================================
    // Photo + consent gate (never submits)
    // ==========================================
    @Then("the photo step is displayed")
    public void the_photo_step_is_displayed() {
        assertThat(becomeMore.isPhotoStepDisplayed())
                .as("The photo step (avatar + consent) should be displayed").isTrue();
    }

    @And("the submit profile button is disabled")
    public void the_submit_profile_button_is_disabled() {
        assertThat(becomeMore.isSubmitProfileEnabled())
                .as("'Submit Profile' should be DISABLED before consent").isFalse();
    }

    @When("the user ticks the profile consent")
    public void the_user_ticks_the_profile_consent() {
        becomeMore.tickConsent();
    }

    @Then("the submit profile button is enabled")
    public void the_submit_profile_button_is_enabled() {
        // Assert-only: the button is NEVER clicked (a real profile would be created).
        assertThat(becomeMore.isSubmitProfileEnabled())
                .as("'Submit Profile' should ENABLE once consent is ticked").isTrue();
    }
}
