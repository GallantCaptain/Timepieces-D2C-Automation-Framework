package com.timepieces.automation.pages.web.account;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.HeaderOnlyBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * OnboardingPage — Page Object Repository for the Timepieces Onboarding Page.
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
 * 
 * For UI Language Testing (UI Verification Only, No Test Logic in Page Object):
 * The file en.properties will contain the English words for Language Assertion testing for the onboarding page, and the file es.properties will contain the Spanish Words for the onboarding page. We will use the same locator keys in both files, but with values in different languages. This way, we can easily switch between languages in our tests by loading the appropriate properties file based on the test configuration.
 */
@Log4j2
public class OnboardingPage extends HeaderOnlyBasePage {
        // /********* Constructor *********/
        public OnboardingPage(WebDriver driver) {
                super(driver);
        }
        /**
         * Onboarding User Journey [Shared state - LoginPage]:
         * https://www.timepieces-d2c.example/login
         * This page is only there for the first time. For profile edits after account
         * creation, the user is taken to the Account Details page which has a different
         * URL and UI.
         * The profile edit page is gated behind OTP verification, so it requires the
         * phone number, country code dropdown, or OTP submission elements that are
         * present on the login page.
         * Hence, this OnboardingPage class only models the first-time profile
         * completion page that is gated behind OTP verification.
         * The Account Details page for profile edits after account creation is modeled
         * separately in the ProfilePage class.
         * The form copies the same locators as the onboarding page, but without the OTP
         * verification elements.
         * Hence, we will reuse the form field locators in the ProfilePage class and
         * only keep the unique locators for the onboarding flow in this OnboardingPage
         * class.
         */

        // Login Page {First Time Setup}: https://www.timepieces-d2c.example/login
        // Timepieces Image (Left Side): timepiecesLoginPageImage [Shared with LoginPage]

        // Complete your profile Message: (Unique for this viewport)
        private final By completeYourProfileMessage = By.xpath(
                        "//h1[contains(@class,'text-white text-[24px] font-bold mb-2') and contains(normalize-space(text()),'Complete your profile')]");
        // Add your details to begin your Timepieces journey Message:
        private final By addYourDetailsMessage = By.xpath(
                        "//p[contains(@class,'text-gray-400 text-[16px]') and contains(normalize-space(), 'Add your details to begin your Timepieces journey.')]");

        // First Name Label: [English]
        private final By firstNameLabel = By.xpath("//label[contains(normalize-space(.), 'First Name')]");
        // First Name Field:
        private final By firstNameField = By
                        .xpath("//label[contains(normalize-space(), 'First Name')]/following-sibling::input");

        // Last Name Label: [English]
        private final By lastNameLabel = By.xpath("//label[contains(normalize-space(.), 'Last Name')]");
        // Last Name Field:
        private final By lastNameField = By
                        .xpath("//label[contains(normalize-space(), 'Last Name')]/following-sibling::input");

        // Email Label: [English]
        private final By emailLabel = By.xpath("//label[text()='Email']");
        // Email Field:
        private final By emailField = By
                        .xpath("//label[contains(normalize-space(), 'Email')]/following-sibling::input");

        // Phone Label:
        private final By phoneLabel = By.xpath("//label[text()='Phone']");
        // Phone Country Code Dropdown Menu:
        private final By phoneCountryCodeDropdown = By.xpath("//div[@class='relative']//button[@type='button']");
        // Phone Number Field:
        private final By phoneNumberField = By.xpath("//input[@inputmode='numeric']");

        // Gender Label: [English]
        private final By genderLabel = By.xpath("//label[contains(normalize-space(.), 'Gender')]");
        // Gender Dropdown Field: Use List to fetch
        private final By genderDropdown = By.xpath("//div[@class='relative']/child::select");
        // Gender List: [Male, Female, Other, Prefer Not To Say]
        private final By maleOption = By.xpath("//option[contains(@value,'Male')]");
        private final By femaleOption = By.xpath("//option[contains(@value,'Female')]");
        private final By otherOption = By.xpath("//option[contains(@value,'Other')]");
        private final By preferNotToSayOption = By
                        .xpath("//option[contains(normalize-space(@value),'Prefer not to say')]");

        // DOB Label: [English]
        private final By dobLabel = By.xpath("//label[contains(normalize-space(), 'Date of Birth')]");
        // DOB Field [mm/dd/yyyy]:
        private final By dobField = By.cssSelector("input[type='date']");

        // Profession Label: [English]
        private final By professionLabel = By.xpath("//label[contains(normalize-space(), 'Profession (optional)')]");
        // Profession Field: [Text Field]
        private final By professionField = By
                        .xpath("//label[contains(normalize-space(), 'Profession (optional)')]/following-sibling::input");

        // Description Label: [English]
        private final By descriptionLabel = By.xpath("//label[text()='Email']");
        // Description Field:
        private final By descriptionField = By.cssSelector("textarea[placeholder='Write something about yourself']");

        // Agreement Checkbox:
        private final By agreementCheckbox = By.cssSelector("input[type='checkbox']");
        // Agreement Message Assertion: [English]
        private final By agreementMessage = By.xpath("//span[contains(normalize-space(.), 'By creating an account')]");
        // Agreement Message Embedded Privacy Policy Link:
        private final By privacyPolicyLink = By.cssSelector("a[href='/policies/privacy-policy']");
        // Agreement Message Embedded Terms of Service Link:
        private final By termsOfServiceLink = By.cssSelector("a[href='/policies/terms-of-service']");

        // Enter Timepieces Button (Disabled until form filled and checkbox selected):
        private final By enterTimepiecesButton = By.cssSelector("button[type='submit']");

        // Timepieces Data Secure Message:
        private final By timepiecesDataSecureMessage = By.xpath(
                        "//p[contains(normalize-space(), 'Your details are secure and used only for your Timepieces experience.')]");

        // ==========================================
        // PRIVATE HELPERS
        // ==========================================

        // ==========================================
        // BUSINESS LIBRARY
        // ==========================================
        /** 
         * 
         */


        // ==========================================
        // VERIFICATION METHODS
        // ==========================================
        /*
         * These return boolean so step definitions can pass them to assertion libraries
         * (AssertJ, TestNG, JUnit) without the page object having a dependency on any
         * assertion framework.
         */
}
