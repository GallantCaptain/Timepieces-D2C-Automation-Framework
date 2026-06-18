package com.timepieces.automation.pages.web.account;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.HeaderOnlyBasePage;
import com.timepieces.automation.pages.web.StagingGatePage;
import com.timepieces.automation.utils.BrowserUtils;
import com.timepieces.automation.utils.ConfigReader;

import lombok.extern.log4j.Log4j2;

/**
 * AccountPage — Page Object Repository for the Timepieces Account Page (/account).
 *
 * This page is displayed after a successful login (returning users) or after
 * completing the onboarding flow (new users). It shows the user's profile
 * summary, profile completion status, and provides navigation to the
 * profile edit page.
 *
 * KEY STATES:
 * 1. Profile Incomplete — shows a banner prompting the user to complete their profile
 * 2. Profile Complete — shows the user's uploaded profile image and account details
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (AccountPage) to allow
 * method chaining in step definitions.
 */
@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public class AccountPage extends HeaderOnlyBasePage {
    // /********* Constructor *********/
    public AccountPage(WebDriver driver) {
        super(driver);
    }

    // /********* Locators *********/
    // When the profile is incomplete
    // Profile Image Container:
    private final By profileImageContainer = By
            .xpath("//span[contains(@class,'text-white')]/parent::div[contains(@class,'border')]");

    // Incomplete Profile Message:
    private final By incompleteProfileMessage = By.xpath(
            "//div[@class='mt-6 p-4 rounded-xl border border-[#3a3a1a] bg-gradient-to-r from-[#2a2a0a] to-[#1a1a0a]']");

    // When the profile is complete
    // Profile Image Container [Uploaded Photo]:
    private final By accountPageProfileImageContainer = By.cssSelector("img[alt='Profile']");

    /*
     * Image State Assertion in Profile Page can be done by checking the presence of
     * the profile image element with the alt text 'Profile' and its src attribute
     * to confirm that a photo has been uploaded, as opposed to the default
     * placeholder image which would have a different src or might not be present at
     * all.)
     */
    private final By accountPageProfileImage = By.xpath("//div[contains(@class,'rounded-full')]//img");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================

    /**
     * B.1 Navigates to the Timepieces account page and waits for the profile image
     * container to confirm it has fully loaded. The base URL is sourced from config
     * so it works across environments (staging, prod) without touching test code.
     *
     * @return this AccountPage for method chaining.
     */
    public AccountPage open() {
        String baseUrl = ConfigReader.get("base.url");
        String accountUrl = baseUrl + "/account";
        log.info("Navigating to account page: {}", accountUrl);
        BrowserUtils.navigateTo(driver, accountUrl, 15);

        // Non-prod environments may sit behind a password gate. Unlock transparently
        // and re-navigate so the load-confirmation wait below behaves identically
        // across envs.
        if (new StagingGatePage(driver).unlockIfPresent()) {
            BrowserUtils.navigateTo(driver, accountUrl, 15);
        }

        waitForVisibility(profileImageContainer);
        log.info("Account page loaded and profile image container confirmed visible.");
        return this;
    }

    /**
     * B.2 Clicks the incomplete-profile message banner to navigate to the
     * profile edit page. The banner acts as a link to {@code /account/profile}.
     *
     * @return a new {@link ProfilePage} representing the edit-profile destination.
     */
    public ProfilePage clickEditProfile() {
        log.info("Clicking incomplete profile message banner to navigate to Edit Profile.");
        clickElement(incompleteProfileMessage);
        log.info("Navigated to Profile page.");
        return new ProfilePage(driver);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /*
     * These return boolean so step definitions can pass them to assertion libraries
     * (AssertJ, TestNG, JUnit) without the page object having a dependency on any
     * assertion framework.
     */

    /**
     * V.1 Verifies that the account page has loaded by checking whether
     * the profile image container (incomplete state) or the uploaded profile
     * image container (complete state) is displayed.
     *
     * @return true if the account page is loaded, false otherwise.
     */
    public boolean isAccountPageLoaded() {
        boolean loaded = isDisplayed(profileImageContainer) || isDisplayed(accountPageProfileImageContainer);
        if (loaded) {
            log.info("Verified: Account page is loaded.");
        } else {
            log.warn("Account page does not appear to be loaded — neither profile container is displayed.");
        }
        return loaded;
    }

    /**
     * V.2 Verifies whether the incomplete-profile message banner is displayed,
     * indicating the user has not yet completed their profile.
     *
     * @return true if the incomplete profile message is displayed, false otherwise.
     */
    public boolean isProfileIncomplete() {
        boolean incomplete = isDisplayed(incompleteProfileMessage);
        if (incomplete) {
            log.info("Verified: Incomplete profile message is displayed.");
        } else {
            log.warn("Incomplete profile message is not displayed.");
        }
        return incomplete;
    }

    /**
     * V.3 Verifies whether a user-uploaded profile image is displayed on the
     * account page (the {@code img[alt='Profile']} element).
     *
     * @return true if the uploaded profile image is displayed, false otherwise.
     */
    public boolean isProfileImageUploaded() {
        boolean uploaded = isDisplayed(accountPageProfileImageContainer);
        if (uploaded) {
            log.info("Verified: Uploaded profile image is displayed.");
        } else {
            log.warn("Uploaded profile image is not displayed.");
        }
        return uploaded;
    }
}
