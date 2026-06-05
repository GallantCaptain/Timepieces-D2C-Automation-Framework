package com.timepieces.automation.pages.web.account;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.HeaderOnlyBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * ProfilePage: /account/profile
 * This page is for profile edits after account creation, and is gated behind
 * OTP verification, where the user is taken to the Account Details page after
 * clicking on the profile icon on the global header.
 * After that there is an option to edit profile details, which takes the user
 * to the profile edit page with the URL /account/profile.
 * This page has a different URL and UI from the onboarding page that is shown
 * for first-time users after login, which is gated behind OTP verification.
 * Hence, we are modeling this page separately in the ProfilePage class, while
 * the onboarding flow is modeled in the OnboardingPage class.
 */
@Log4j2
public class ProfilePage extends HeaderOnlyBasePage {
    /* Constructor */
    public ProfilePage(WebDriver driver) {
        super(driver);
    }

    // /********* Locators *********/
    


    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /*
     * These return boolean so step definitions can pass them to assertion libraries
     * (AssertJ, TestNG, JUnit) without the page object having a dependency on any
     * assertion framework.
     */
}
