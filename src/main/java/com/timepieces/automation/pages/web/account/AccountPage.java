package com.timepieces.automation.pages.web.account;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.HeaderOnlyBasePage;

import lombok.extern.log4j.Log4j2;
/**
 * AccountPage — Page Object Repository for the Timepieces Account Page.
 *
 * Covers the full OTP-based login flow:
 * 1. Country code selection (by name or by dial code)
 * 2. Phone number entry
 * 3. Cloudflare CAPTCHA iframe awareness
 * 4. OTP dispatch (Send OTP button)
 * 5. OTP entry across multiple input fields
 * 6. Login completion (Continue button)
 *
 * CLOUDFLARE NOTE:
 * The Cloudflare CAPTCHA challenge cannot be solved by WebDriver in a real
 * production environment. For automated smoke tests, ensure the staging
 * environment has CAPTCHA disabled, or use a Cloudflare Turnstile test key
 * (0x4AAAAAAAAAA... always-passes key) configured in the staging build.
 * The iframe locator is present for environments where it must be waited on.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return `this` (LoginPage) to allow
 * method chaining in step definitions. The final step (completeLogin) returns
 * the next page object once navigation is confirmed.
 */
@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public class AccountPage extends HeaderOnlyBasePage {
    // /********* Constructor *********/
    public AccountPage(WebDriver driver) {
        super(driver);
    }

    // /********* Locators *********/
    //When the profile is incomplete
    //Profile Image Container:
private final By profileImageContainer = By.xpath("xpath: //span[contains(@class,'text-white')]/parent::div[contains(@class,'border')]");

//Incomplete Profile Message:
private final By incompleteProfileMessage = By.xpath("xpath: //div[@class='mt-6 p-4 rounded-xl border border-[#3a3a1a] bg-gradient-to-r from-[#2a2a0a] to-[#1a1a0a]']");

//When the profile is complete
//Profile Image Container [Uploaded Photo]:
private final By accountPageProfileImageContainer = By.cssSelector("img[alt='Profile']");

/*Image State Assertion in Profile Page can be done by checking the presence of the profile image element with the alt text 'Profile' and its src attribute to confirm that a photo has been uploaded, as opposed to the default placeholder image which would have a different src or might not be present at all.)*/
private final By accountPageProfileImage = By.xpath("xpath: //div[contains(@class,'rounded-full')]//img");








   

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
