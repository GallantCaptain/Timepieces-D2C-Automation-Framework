package com.timepieces.automation.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.exceptions.OtpRateLimitException;
import com.timepieces.automation.pages.web.account.LoginPage;
import com.timepieces.automation.utils.ConfigReader;
import com.timepieces.automation.utils.TwilioOtpClient;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

// Assertions belong in Step Definitions, NEVER in Page Objects
@Log4j2
public class LoginSteps {
    private final TestContext testContext;
    private LoginPage loginPage;

    // PicoContainer will inject the TestContext instance here
    public LoginSteps(TestContext testContext) {
        this.testContext = testContext;
        this.loginPage = new LoginPage(DriverFactory.getDriver());
    }

    @Given("a user navigates to the Timepieces login page")
    public void a_user_navigates_to_the_timepieces_login_page() {
        /* Initialize the page object using the ThreadLocal driver instantiated by Hooks.java */
        loginPage = new LoginPage(DriverFactory.getDriver());

        // Use the fluent POM to navigate
        loginPage.open();

        assertThat(loginPage.isWelcomeMessageDisplayed())
                .as("Welcome message was not displayed upon loading.")
                .isTrue();
    }

    @When("the user selects the country {string} from the region dropdown")
    public void the_user_selects_the_country_from_the_region_dropdown(String country) {
        loginPage.configureCountryCodeByName(country);
    }

    @And("the user enters the phone number {string} in the phone number input field")
    public void the_user_enters_the_phone_number(String phoneNumber) {
        testContext.setCurrentPhoneNumber(phoneNumber); // Save to context
        loginPage.enterPhoneNumber(phoneNumber);
    }

    // This step assumes that staging has CAPTCHA bypassed or a Turnstile test key is active, otherwise need to create one in test bed.
    @And("clicks the send OTP button")
    public void clicks_the_send_otp_button() {
        try {
            loginPage.clickSendOtpButton();
        } catch (OtpRateLimitException e) {
            // Parallel scenarios and back-to-back automation runs reuse the same login number, so
            // the ~90s "Too many attempts" cooldown (login-cooldown) can be active when this step
            // runs. That is an environment timing artifact, not a product defect — so instead of
            // failing, wait the cooldown out (its duration + a small buffer) and retry the send
            // ONCE so the scenario can proceed. A second cooldown means the number is genuinely
            // stuck, and we let it surface.
            int waitSeconds = (e.getWaitSeconds() >= 0 ? e.getWaitSeconds() : 90) + 10;
            log.warn("Send OTP hit the ~{}s cooldown (parallel/automation churn). Waiting it out and "
                    + "retrying once.", e.getWaitSeconds());
            loginPage.waitOutOtpCooldown(waitSeconds);
            loginPage.clickSendOtpButton();
            log.info("Send OTP succeeded on retry after the cooldown elapsed.");
        }
    }

    @Then("the OTP verification screen should be displayed")
    public void the_otp_verification_screen_should_be_displayed() {
        assertThat(loginPage.isOtpEntryScreenDisplayed())
                .as("OTP entry screen was not displayed.")
                .isTrue();
    }

    @And("the user enters the OTP {string}")
    public void the_user_enters_the_otp(String otp) {
        loginPage.enterOtp(otp);
    }

    // --- End-to-end OTP read-back via Twilio (tagged @OtpE2E; excluded from default runs) ---

    @When("the user enters their Twilio-registered phone number")
    public void the_user_enters_their_twilio_registered_phone_number() {
        // Country name + national digits for the Twilio number, supplied via env/local config so
        // the real number is never committed. e.g. TWILIO_TEST_COUNTRY="United States".
        String country = resolve("TWILIO_TEST_COUNTRY", "twilio.test.country");
        String phone = resolve("TWILIO_TEST_PHONE", "twilio.test.phone");
        testContext.setCurrentPhoneNumber(phone);
        loginPage.configureCountryCodeByName(country);
        loginPage.enterPhoneNumber(phone);
    }

    @When("the user enters the OTP retrieved from Twilio")
    public void the_user_enters_the_otp_retrieved_from_twilio() {
        // Wait up to 60s for the SMS to land, polling every 5s — OTP delivery has real latency.
        String otp = new TwilioOtpClient().fetchLatestOtp(60, 5);
        loginPage.enterOtp(otp);
    }

    /** Reads an env var first, then a ConfigReader key; fails clearly if neither is set. */
    private String resolve(String envName, String configKey) {
        String value = System.getenv(envName);
        if (value == null || value.isBlank()) {
            value = ConfigReader.get(configKey);
        }
        if (value == null || value.isBlank() || value.startsWith("${")) {
            throw new IllegalStateException("Set " + envName + " (or " + configKey
                    + " in a gitignored local properties file) to run the @OtpE2E scenario.");
        }
        return value.trim();
    }

    @And("clicks the continue button")
    public void clicks_the_continue_button() {
        loginPage.clickContinueButton();
    }

    // --- OTP brute-force security probe (tagged @OtpBruteForce; opt-in, not in smoke/regression) ---

    @When("the user submits {int} incorrect OTP codes in a row")
    public void the_user_submits_incorrect_otp_codes_in_a_row(int attempts) {
        int allowed = loginPage.probeUnlimitedOtpAttempts(attempts);
        testContext.setContextData("otpBruteForceAttempts", attempts);
        testContext.setContextData("otpBruteForceAllowed", allowed);
    }

    @Then("the OTP entry is never locked or rate limited")
    public void the_otp_entry_is_never_locked_or_rate_limited() {
        int attempts = (int) testContext.getContextData("otpBruteForceAttempts");
        int allowed = (int) testContext.getContextData("otpBruteForceAllowed");
        // This assertion PASSES today because the verify step has no attempt limit — i.e. it
        // documents a real vulnerability. Once a server-side cap/lockout is added (the fix),
        // `allowed` will be < attempts and this scenario will START FAILING, signalling that the
        // exposure is closed and the test should be inverted to assert the lockout.
        assertThat(allowed)
                .as("SECURITY: expected all %s wrong OTP submissions to be accepted with no lockout "
                        + "(demonstrating the missing attempt limit), but only %s were allowed.",
                        attempts, allowed)
                .isEqualTo(attempts);
    }
}