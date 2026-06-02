package com.timepieces.automation.stepdefinitions;

import org.testng.Assert;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.pages.web.account.LoginPage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

// Assertions belong in Step Definitions, NEVER in Page Objects
public class LoginSteps {
    // Declare the Page Object. We do not initialize it until the steps are called.
    private LoginPage loginPage;

    @Given("a user navigates to the Timepieces login page")
    public void a_user_navigates_to_the_timepieces_login_page() {
        // Initialize the page object using the ThreadLocal driver instantiated by
        // Hooks.java
        loginPage = new LoginPage(DriverFactory.getDriver());

        // Use the fluent POM to navigate
        loginPage.open();

        Assert.assertTrue(loginPage.isWelcomeMessageDisplayed(), "Welcome message was not displayed upon loading.");
    }

    @When("the user selects the country {string} from the region dropdown")
    public void the_user_selects_the_country_from_the_region_dropdown(String country) {
        loginPage.configureCountryCodeByName(country);
    }

    @And("the user enters the phone number {string} in the phone number input field")
    public void the_user_enters_the_phone_number(String phoneNumber) {
        loginPage.enterPhoneNumber(phoneNumber);
    }

    // This step assumes that staging has CAPTCHA bypassed or a Turnstile test key is active, otherwsie need to create one in test bed. 
    @And("clicks the send OTP button")
    public void clicks_the_send_otp_button() {
        loginPage.clickSendOtpButton();
    }

    @Then("the OTP verification screen should be displayed")
    public void the_otp_verification_screen_should_be_displayed() {
        Assert.assertTrue(loginPage.isOtpEntryScreenDisplayed(), "OTP entry screen was not displayed.");
    }
}
