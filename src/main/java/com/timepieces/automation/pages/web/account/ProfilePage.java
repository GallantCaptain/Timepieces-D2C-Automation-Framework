package com.timepieces.automation.pages.web.account;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.timepieces.automation.pages.HeaderOnlyBasePage;
import com.timepieces.automation.pages.web.StagingGatePage;
import com.timepieces.automation.utils.BrowserUtils;
import com.timepieces.automation.utils.ConfigReader;
import com.timepieces.automation.utils.WindowUtils;

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
    // ==========================================
    // Constructor
    // ==========================================
    public ProfilePage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // My Account Link At Top (My Account -> Edit Profile)
    private final By accountLinkTopLeftProfilePage = By.cssSelector("a[href='/account']");

    // Edit Profile (Label) next to My Account Link at top:
    private final By profileLabel = By.xpath("//span[normalize-space(text())='Edit Profile']");

    // LinkedIn Ai Icon in the LinkedIn Container:
    private final By linkedInAiIcon = By.xpath("//img[@alt='LinkedIn AI']");

    // Import data from linkedin text (label)
    private final By importDataFromLinkedInLabel = By.xpath("//span[normalize-space()='Import data from LinkedIn']");

    // Import Data From LinkedIn URL Field [Container-Top] (Assertion):
    private final By importDataFromLinkedInUrlField = By
            .cssSelector("input[placeholder='Paste your LinkedIn profile here...']");

    // Import Data From LinkedIn Upload Button
    private final By importDataFromLinkedInUploadButton = By.xpath(
            "//input[normalize-space(@placeholder)='Paste your LinkedIn profile here...']/following-sibling::button");

    // Invalid LinkedIn URL text validation message [Wrong linkedin url pasted and
    // submitted]:
    private final By invalidLinkedInUrlMessage = By
            .xpath("//p[normalize-space()='Please enter a valid LinkedIn profile URL first']");

    // Import Data from LinkedIn container
    private final By importDataFromLinkedInContainer = By
            .xpath("//img[@alt='LinkedIn AI']/ancestor::div[contains(@class,'border')]");

    // Photo Container (No Photo):
    private final By photoContainer = By
            .xpath("//span[contains(@class,'text-white')]/parent::div[contains(@class,'border')]");

    // Photo Container - Placeholder Text (No Photo) [Assertion match Initials]:
    private final By photoPlaceholderText = By
            .xpath("//*[local-name()='svg']/parent::label[contains(@class,'absolute')]/preceding-sibling::div/span");

    // Add Photo Icon/Button:
    private final By addPhotoIconButton = By
            .xpath("//*[local-name()='svg']/parent::label[contains(@class,'absolute')]");

    // Remove Photo Button: [After adding a photo only]
    private final By removePhotoButton = By.xpath("//button[normalize-space()='Remove photo']");

    // Basic Details Text Label
    private final By basicDetailsLabel = By.xpath("//h2[normalize-space()='Basic details']");

    // Name Label:
    private final By nameLabel = By.xpath("//label[contains(normalize-space(),'Name')]");

    // Name Field:
    private final By nameField = By.xpath("//label[contains(normalize-space(),'Name')]/following-sibling::input");

    // Enter Full Name *Required Placeholder [English Assertion]:
    private final By fullNamePlaceholder = By.xpath("//input[normalize-space(@placeholder)='Enter your full name']");

    // Designation Label:
    private final By designationLabel = By.xpath("//label[contains(normalize-space(),'Designation')]");

    // Designation Field:
    private final By designationField = By
            .xpath("//label[contains(normalize-space(),'Designation')]/following-sibling::input");

    // LinkedIn Profile URL label:
    private final By linkedInProfileUrlLabel = By.xpath("//label[normalize-space()='LinkedIn Profile URL']");

    // LinkedIn url Field input:
    private final By linkedInUrlField = By
            .xpath("//label[normalize-space()='LinkedIn Profile URL']/following-sibling::input");

    // About Label:
    private final By aboutLabel = By.xpath("//label[contains(normalize-space(),'About')]");

    // About TextArea Field:
    private final By aboutTextArea = By
            .xpath("//label[contains(normalize-space(),'About')]/following-sibling::textarea");

    // Contact Details Label:
    private final By contactDetailsLabel = By.xpath("//h2[contains(normalize-space(),'Contact details')]");

    // Phone Label:
    private final By phoneLabel = By.xpath("//label[contains(normalize-space(), 'Phone')]");

    // Phone Country Dropdown [Disabled]:
    private final By phoneCountryDropdown = By.xpath("//select[normalize-space(@aria-label)='Phone number country']");

    // Phone Country Flag Icon [Disabled Click/Select]:
    private final By phoneCountryFlagIcon = By.xpath("//img[@class='PhoneInputCountryIconImg']");

    // Phone Number Field: [Disabled Read-Only]
    private final By phoneField = By.xpath("//input[@autocomplete='tel']");

    // Email Label:
    private final By emailLabel = By.xpath("//label[contains(normalize-space(), 'Email')]");

    // Email Field:
    private final By emailField = By.xpath("//label[contains(normalize-space(), 'Email')]/following-sibling::input");

    // Note Message (Small, Above Save Button) Label:
    private final By noteMessageLabel = By.xpath("//span[contains(normalize-space(), 'Note')]/parent::p");

    // Save Changes Button:
    private final By saveChangesButton = By.xpath("//button[normalize-space()='Save Changes']");

    // Cancel Button:
    private final By cancelButton = By.xpath("//button[normalize-space()='Cancel']");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Navigates to the Timepieces profile page and waits for the basic details
     * label to confirm the page has fully loaded. The base URL is sourced from
     * config so it works across environments (staging, prod) without touching
     * test code.
     *
     * @return this ProfilePage for method chaining.
     */
    public ProfilePage open() {
        String profileUrl = ConfigReader.get("base.url") + "/account/profile";
        log.info("Navigating to profile page: {}", profileUrl);
        BrowserUtils.navigateTo(driver, profileUrl, 15);

        // Non-prod environments may sit behind a password gate. Unlock transparently
        // and re-navigate so the load confirmation wait below behaves identically
        // across envs.
        if (new StagingGatePage(driver).unlockIfPresent()) {
            BrowserUtils.navigateTo(driver, profileUrl, 15);
        }

        waitForVisibility(basicDetailsLabel);
        log.info("Profile page loaded and basic details label confirmed visible.");
        return this;
    }

    /**
     * B.2 Updates the name field with the provided value.
     * BasePage.sendKeys already clears the field before typing.
     *
     * @param name The full name to enter.
     * @return this ProfilePage for method chaining.
     */
    public ProfilePage updateName(String name) {
        log.info("Updating name field.");
        sendKeys(nameField, name);
        return this;
    }

    /**
     * B.3 Updates the designation field with the provided value.
     *
     * @param designation The designation/title to enter.
     * @return this ProfilePage for method chaining.
     */
    public ProfilePage updateDesignation(String designation) {
        log.info("Updating designation field.");
        sendKeys(designationField, designation);
        return this;
    }

    /**
     * B.4 Updates the LinkedIn profile URL field with the provided value.
     *
     * @param url The LinkedIn profile URL to enter.
     * @return this ProfilePage for method chaining.
     */
    public ProfilePage updateLinkedInUrl(String url) {
        log.info("Updating LinkedIn URL field.");
        sendKeys(linkedInUrlField, url);
        return this;
    }

    /**
     * B.5 Updates the about/bio text area with the provided value.
     *
     * @param about The about/bio text to enter.
     * @return this ProfilePage for method chaining.
     */
    public ProfilePage updateAbout(String about) {
        log.info("Updating about text area.");
        sendKeys(aboutTextArea, about);
        return this;
    }

    /**
     * B.6 Updates the email field with the provided value.
     *
     * @param email The email address to enter.
     * @return this ProfilePage for method chaining.
     */
    public ProfilePage updateEmail(String email) {
        log.info("Updating email field.");
        sendKeys(emailField, email);
        return this;
    }

    /**
     * B.7 Clicks the add photo icon/button to initiate photo upload.
     * Note: the actual file upload dialog interaction would need to be handled
     * by the step definition or a separate utility.
     *
     * @return this ProfilePage for method chaining.
     */
    public ProfilePage clickAddPhotoButton() {
        log.info("Clicking add photo icon button.");
        clickElement(addPhotoIconButton);
        return this;
    }

    /**
     * B.8 Clicks the remove photo button to remove the currently uploaded photo.
     * This button is only visible after a photo has been added.
     *
     * @return this ProfilePage for method chaining.
     */
    public ProfilePage clickRemovePhotoButton() {
        log.info("Clicking remove photo button.");
        clickElement(removePhotoButton);
        return this;
    }

    /**
     * B.9 Pastes a LinkedIn profile URL into the import field and clicks the
     * upload button to trigger data import from LinkedIn.
     *
     * @param linkedInProfileUrl The LinkedIn profile URL to import data from.
     * @return this ProfilePage for method chaining.
     */
    public ProfilePage importFromLinkedIn(String linkedInProfileUrl) {
        log.info("Importing data from LinkedIn profile.");
        sendKeys(importDataFromLinkedInUrlField, linkedInProfileUrl);
        clickElement(importDataFromLinkedInUploadButton);
        return this;
    }

    /**
     * B.10 Clicks the Save Changes button to persist the profile edits.
     * The page shows a success state but does not navigate away.
     *
     * @return this ProfilePage for method chaining.
     */
    public ProfilePage clickSaveChanges() {
        log.info("Clicking Save Changes button.");
        clickElement(saveChangesButton);
        log.info("Save Changes clicked. Profile update submitted.");
        return this;
    }

    /**
     * B.11 Clicks the Cancel button to discard profile edits and navigate back
     * to the account page.
     *
     * @return AccountPage since cancel navigates back to the account page.
     */
    public AccountPage clickCancel() {
        log.info("Clicking Cancel button.");
        clickElement(cancelButton);
        log.info("Cancel clicked. Navigating back to account page.");
        return new AccountPage(driver);
    }

    /**
     * B.12 Clicks the 'My Account' breadcrumb link at the top of the profile
     * page to navigate back to the account page.
     *
     * @return AccountPage since the breadcrumb navigates to the account page.
     */
    public AccountPage clickAccountLink() {
        log.info("Clicking 'My Account' breadcrumb link.");
        clickElement(accountLinkTopLeftProfilePage);
        return new AccountPage(driver);
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
     * V.1 Verifies that the profile page has fully loaded by checking both
     * the basic details label and the 'Edit Profile' breadcrumb label are
     * displayed.
     *
     * @return true if the profile page is loaded, false otherwise.
     */
    public boolean isProfilePageLoaded() {
        if (isDisplayed(basicDetailsLabel) && isDisplayed(profileLabel)) {
            log.info("Verified: Profile page is loaded — basic details and edit profile labels are displayed.");
            return true;
        }
        log.warn("Profile page load verification failed — one or both labels not displayed.");
        return false;
    }

    /**
     * V.2 Retrieves the current value of the name input field.
     *
     * @return the value attribute of the name field.
     */
    public String getNameValue() {
        log.debug("Retrieving name field value.");
        return getAttribute(nameField, "value");
    }

    /**
     * V.3 Retrieves the current value of the designation input field.
     *
     * @return the value attribute of the designation field.
     */
    public String getDesignationValue() {
        log.debug("Retrieving designation field value.");
        return getAttribute(designationField, "value");
    }

    /**
     * V.4 Retrieves the current value of the LinkedIn URL input field.
     *
     * @return the value attribute of the LinkedIn URL field.
     */
    public String getLinkedInUrlValue() {
        log.debug("Retrieving LinkedIn URL field value.");
        return getAttribute(linkedInUrlField, "value");
    }

    /**
     * V.5 Retrieves the current value of the about text area.
     *
     * @return the value attribute of the about text area.
     */
    public String getAboutValue() {
        log.debug("Retrieving about text area value.");
        return getAttribute(aboutTextArea, "value");
    }

    /**
     * V.6 Retrieves the current value of the phone number field.
     * Note: the phone field is read-only; this returns the pre-populated value.
     *
     * @return the value attribute of the phone field.
     */
    public String getPhoneNumberValue() {
        log.debug("Retrieving phone number field value.");
        return getAttribute(phoneField, "value");
    }

    /**
     * V.7 Retrieves the current value of the email input field.
     *
     * @return the value attribute of the email field.
     */
    public String getEmailValue() {
        log.debug("Retrieving email field value.");
        return getAttribute(emailField, "value");
    }

    /**
     * V.8 Verifies whether the phone field is read-only by checking for the
     * presence of a 'disabled' or 'readonly' attribute.
     *
     * @return true if the phone field is read-only or disabled, false otherwise.
     */
    public boolean isPhoneFieldReadOnly() {
        log.debug("Checking if phone field is read-only.");
        String disabled = getAttribute(phoneField, "disabled");
        String readOnly = getAttribute(phoneField, "readonly");
        if (disabled != null || readOnly != null) {
            log.info("Verified: Phone field is read-only (disabled={}, readonly={}).", disabled, readOnly);
            return true;
        }
        log.warn("Phone field is not read-only — neither disabled nor readonly attribute found.");
        return false;
    }

    /**
     * V.9 Verifies whether the Save Changes button is displayed and enabled.
     *
     * @return true if the Save Changes button is displayed and enabled, false
     *         otherwise.
     */
    public boolean isSaveChangesButtonEnabled() {
        log.debug("Checking if Save Changes button is displayed and enabled.");
        try {
            WebElement button = getElement(saveChangesButton);
            boolean result = button.isDisplayed() && button.isEnabled();
            if (result) {
                log.info("Verified: Save Changes button is displayed and enabled.");
            } else {
                log.warn("Save Changes button is visible but not enabled.");
            }
            return result;
        } catch (Exception e) {
            log.warn("Save Changes button verification failed — element may not be present.", e);
            return false;
        }
    }

    /**
     * V.10 Verifies whether the invalid LinkedIn URL validation message is
     * displayed.
     *
     * @return true if the invalid LinkedIn URL message is displayed, false
     *         otherwise.
     */
    public boolean isInvalidLinkedInUrlMessageDisplayed() {
        if (isDisplayed(invalidLinkedInUrlMessage)) {
            log.info("Verified: Invalid LinkedIn URL message is displayed.");
            return true;
        }
        log.warn("Invalid LinkedIn URL message not displayed.");
        return false;
    }

    /**
     * V.11 Verifies whether the LinkedIn import section is displayed by checking
     * for both the LinkedIn AI icon and the import label.
     *
     * @return true if the LinkedIn import section is displayed, false otherwise.
     */
    public boolean isLinkedInImportSectionDisplayed() {
        if (isDisplayed(linkedInAiIcon) && isDisplayed(importDataFromLinkedInLabel)) {
            log.info("Verified: LinkedIn import section is displayed.");
            return true;
        }
        log.warn("LinkedIn import section not fully displayed.");
        return false;
    }

    /**
     * V.12 Verifies whether the photo placeholder (showing user initials) is
     * displayed.
     *
     * @return true if the photo placeholder text is displayed, false otherwise.
     */
    public boolean isPhotoPlaceholderDisplayed() {
        if (isDisplayed(photoPlaceholderText)) {
            log.info("Verified: Photo placeholder is displayed.");
            return true;
        }
        log.warn("Photo placeholder not displayed.");
        return false;
    }

    /**
     * V.13 Retrieves the initials text shown in the photo placeholder when no
     * photo has been uploaded.
     *
     * @return the initials text from the photo placeholder.
     */
    public String getPhotoPlaceholderInitials() {
        log.debug("Retrieving photo placeholder initials.");
        return getText(photoPlaceholderText);
    }
}
