package com.timepieces.automation.pages.web.account;

import java.time.Duration;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.timepieces.automation.pages.HeaderOnlyBasePage;
import com.timepieces.automation.utils.ConfigReader;

import lombok.extern.log4j.Log4j2;

/**
 * OnboardingPage: https://www.timepieces-d2c.example/becomemore
 * The Page Object Repository for the Timepieces Onboarding Page.
 *
 * Displayed once for first-time users immediately after successful OTP
 * verification on the LoginPage. Collects the user's personal details
 * (name, email, gender, DOB, profession, description) and copies them
 * to the Account and Profile pages upon submission.
 *
 * The 'new' onboarding experience is triggered though two different ways. A
 * first time user can signup via any of the wishlist/register interest forms
 * and is then prompted to go to the onboarding or they can directly signup via
 * the login page and have their number verified via otp, after which the new
 * onbaording process will trigger.
 * Currently there are 7-8 steps for the user to fill when they get to the
 * onboarding journey. They will need to the need to fill the mandatory steps to
 * have the profile completed. There are progress bars for each step that turn
 * green when filled. There is an llm option to help fill the description and
 * other details. The last step before submitting/completing the onboarding
 * journey is to upload a profile photo. There are two ways a user can go on
 * the onboarding journey. They can choose the manual path to fill all the
 * details manually or they may choose the import from LinkedIn path that
 * automatically fills in most of the fields when the user confirms the valid
 * linkedin url profile link that they paste is theirs.
 * 
 * 
 * -- Only Happy Path testing done so far. Need to do negative and edge testing
 * with profanities and other cases to check the llm restrictions. --
 * "There is still a major defect with the new onboarding for first time users,
 * and it is being worked on."
 * All the locators in this page are now useless, although the methods can
 * be reworked a bit.
 * -- Will update this part with version 1.0 --
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (OnboardingPage)
 * to allow method chaining in step definitions.
 *
 * LANGUAGE TESTING:
 * For UI language verification, locale-specific .properties files
 * (en.properties, es.properties) contain expected label text keyed
 * by the same locator identifiers.
 * 
 * The onboarding experience has been completely revamped, so this page is
 * outdated and will be replaced for the revamp.
 * 
 * @version 0.1
 * @author Avinash Rath
 */

@Log4j2
public class OnboardingPage extends HeaderOnlyBasePage<OnboardingPage> {

	// ==========================================
	// Constructor
	// ==========================================
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

	// ==========================================
	// LOCATORS
	// ==========================================
	/*
	 * These locators are redundant and will be replaced with the new onboarding
	 * page ones.
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
	private final By descriptionLabel = By.xpath("//label[normalize-space(text())='Description (optional)']");
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

	/**
	 * Selects a gender option from the gender dropdown using Selenium's
	 * {@link Select} class.
	 *
	 * @param gender The visible text of the gender option to select
	 *               (e.g., "Male", "Female", "Other", "Prefer not to say").
	 */
	private void selectGenderOption(String gender) {
		log.debug("Selecting gender option: '{}'", gender);
		WebElement dropdownElement = getElement(genderDropdown);
		Select select = new Select(dropdownElement);
		select.selectByVisibleText(gender);
		log.debug("Gender option '{}' selected.", gender);
	}

	// ==========================================
	// BUSINESS LIBRARY
	// ==========================================

	/**
	 * B.1 Enters the first name into the first name input field.
	 *
	 * @param firstName The first name to enter.
	 * @return this OnboardingPage for method chaining.
	 */
	public OnboardingPage enterFirstName(String firstName) {
		log.info("Entering first name: '{}'", firstName);
		sendKeys(firstNameField, firstName);
		log.info("First name '{}' entered successfully.", firstName);
		return this;
	}

	/**
	 * B.2 Enters the last name into the last name input field.
	 *
	 * @param lastName The last name to enter.
	 * @return this OnboardingPage for method chaining.
	 */
	public OnboardingPage enterLastName(String lastName) {
		log.info("Entering last name: '{}'", lastName);
		sendKeys(lastNameField, lastName);
		log.info("Last name '{}' entered successfully.", lastName);
		return this;
	}

	/**
	 * B.3 Enters the email address into the email input field.
	 *
	 * @param email The email address to enter.
	 * @return this OnboardingPage for method chaining.
	 */
	public OnboardingPage enterEmail(String email) {
		log.info("Entering email: '{}'", email);
		sendKeys(emailField, email);
		log.info("Email '{}' entered successfully.", email);
		return this;
	}

	/**
	 * B.4 Selects a gender from the gender dropdown.
	 *
	 * @param gender The visible text of the gender option to select
	 *               (e.g., "Male", "Female", "Other", "Prefer not to say").
	 * @return this OnboardingPage for method chaining.
	 */
	public OnboardingPage selectGender(String gender) {
		log.info("Selecting gender: '{}'", gender);
		selectGenderOption(gender);
		log.info("Gender '{}' selected successfully.", gender);
		return this;
	}

	/**
	 * B.5 Enters the date of birth into the DOB input field using
	 * {@link JavascriptExecutor}. Date inputs resist standard
	 * {@code sendKeys} on many browsers, so JavaScript is used to
	 * set the value directly on the element.
	 *
	 * @param dob The date of birth in yyyy-MM-dd format (HTML5 date input format).
	 * @return this OnboardingPage for method chaining.
	 */
	public OnboardingPage enterDateOfBirth(String dob) {
		log.info("Entering date of birth: '{}'", dob);
		WebElement dobElement = getElement(dobField);
		((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", dobElement, dob);
		log.info("Date of birth '{}' entered successfully via JavaScript.", dob);
		return this;
	}

	/**
	 * B.6 Enters the profession into the profession input field.
	 *
	 * @param profession The profession to enter.
	 * @return this OnboardingPage for method chaining.
	 */
	public OnboardingPage enterProfession(String profession) {
		log.info("Entering profession: '{}'", profession);
		sendKeys(professionField, profession);
		log.info("Profession '{}' entered successfully.", profession);
		return this;
	}

	/**
	 * B.7 Enters the description into the description textarea field.
	 *
	 * @param description The description text to enter.
	 * @return this OnboardingPage for method chaining.
	 */
	public OnboardingPage enterDescription(String description) {
		log.info("Entering description.");
		sendKeys(descriptionField, description);
		log.info("Description entered successfully.");
		return this;
	}

	/**
	 * B.8 Checks the agreement checkbox to accept the privacy policy
	 * and terms of service. The "Enter Timepieces" submit button remains
	 * disabled until this checkbox is checked along with all required
	 * form fields being filled.
	 *
	 * @return this OnboardingPage for method chaining.
	 */
	public OnboardingPage checkAgreementCheckbox() {
		log.info("Checking agreement checkbox.");
		clickElement(agreementCheckbox);
		log.info("Agreement checkbox checked.");
		return this;
	}

	/**
	 * B.9 Clicks the "Enter Timepieces" submit button to complete onboarding.
	 * Waits for the button to become enabled first — it is disabled until
	 * all required form fields are filled and the agreement checkbox is
	 * checked. After clicking, the user is redirected to the homepage.
	 */
	public void clickEnterTimepiecesButton() {
		int formWaitSeconds = Optional.ofNullable(ConfigReader.get("onboarding.submit.wait.timeout"))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.map(Integer::parseInt)
				.orElse(30);
		log.info("Waiting up to {}s for 'Enter Timepieces' button to become enabled...", formWaitSeconds);
		try {
			new WebDriverWait(driver, Duration.ofSeconds(formWaitSeconds))
					.until(d -> {
						WebElement btn = d.findElement(enterTimepiecesButton);
						return btn.isEnabled() && btn.getAttribute("disabled") == null;
					});
		} catch (TimeoutException e) {
			log.error("'Enter Timepieces' button did not become enabled within {}s — "
					+ "form may be incomplete or agreement checkbox unchecked.", formWaitSeconds);
			throw e;
		}
		log.info("'Enter Timepieces' button is enabled. Clicking.");
		clickElement(enterTimepiecesButton);
		log.info("'Enter Timepieces' clicked. Onboarding submitted — redirecting to homepage.");
	}

	/**
	 * B.10 Convenience method that fills the core profile fields in sequence:
	 * first name, last name, email, gender, and date of birth.
	 *
	 * @param firstName The first name to enter.
	 * @param lastName  The last name to enter.
	 * @param email     The email address to enter.
	 * @param gender    The visible text of the gender option to select.
	 * @param dob       The date of birth in yyyy-MM-dd format.
	 * @return this OnboardingPage for method chaining.
	 */
	public OnboardingPage fillCompleteProfile(String firstName, String lastName, String email,
			String gender, String dob) {
		log.info("Filling complete profile: firstName='{}', lastName='{}', email='{}', gender='{}', dob='{}'",
				firstName, lastName, email, gender, dob);
		enterFirstName(firstName);
		enterLastName(lastName);
		enterEmail(email);
		selectGender(gender);
		enterDateOfBirth(dob);
		log.info("Complete profile filled successfully.");
		return this;
	}

	/**
	 * B.11 Clicks the Privacy Policy link embedded in the agreement message.
	 *
	 * @return this OnboardingPage for method chaining.
	 */
	public OnboardingPage clickPrivacyPolicyLink() {
		log.info("Clicking Privacy Policy link.");
		clickElement(privacyPolicyLink);
		log.info("Privacy Policy link clicked.");
		return this;
	}

	/**
	 * B.12 Clicks the Terms of Service link embedded in the agreement message.
	 *
	 * @return this OnboardingPage for method chaining.
	 */
	public OnboardingPage clickTermsOfServiceLink() {
		log.info("Clicking Terms of Service link.");
		clickElement(termsOfServiceLink);
		log.info("Terms of Service link clicked.");
		return this;
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
	 * V.1 Verifies that the onboarding page is displayed by checking both
	 * the "Complete your profile" heading and the "Add your details" subtitle.
	 *
	 * @return true if both onboarding messages are displayed, false otherwise.
	 */
	public boolean isOnboardingPageDisplayed() {
		if (isDisplayed(completeYourProfileMessage) && isDisplayed(addYourDetailsMessage)) {
			log.info("Verified: Onboarding page is displayed — profile and details messages visible.");
			return true;
		}
		log.warn("Onboarding page not displayed — one or both heading messages missing.");
		return false;
	}

	/**
	 * V.2 Verifies that the first name label and input field are displayed.
	 *
	 * @return true if both the label and field are displayed, false otherwise.
	 */
	public boolean isFirstNameFieldDisplayed() {
		if (isDisplayed(firstNameLabel) && isDisplayed(firstNameField)) {
			log.info("Verified: First name label and field are displayed.");
			return true;
		}
		log.warn("First name label or field not displayed.");
		return false;
	}

	/**
	 * V.3 Verifies that the last name label and input field are displayed.
	 *
	 * @return true if both the label and field are displayed, false otherwise.
	 */
	public boolean isLastNameFieldDisplayed() {
		if (isDisplayed(lastNameLabel) && isDisplayed(lastNameField)) {
			log.info("Verified: Last name label and field are displayed.");
			return true;
		}
		log.warn("Last name label or field not displayed.");
		return false;
	}

	/**
	 * V.4 Verifies that the email label and input field are displayed.
	 *
	 * @return true if both the label and field are displayed, false otherwise.
	 */
	public boolean isEmailFieldDisplayed() {
		if (isDisplayed(emailLabel) && isDisplayed(emailField)) {
			log.info("Verified: Email label and field are displayed.");
			return true;
		}
		log.warn("Email label or field not displayed.");
		return false;
	}

	/**
	 * V.5 Verifies that the gender label and dropdown are displayed.
	 *
	 * @return true if both the label and dropdown are displayed, false otherwise.
	 */
	public boolean isGenderDropdownDisplayed() {
		if (isDisplayed(genderLabel) && isDisplayed(genderDropdown)) {
			log.info("Verified: Gender label and dropdown are displayed.");
			return true;
		}
		log.warn("Gender label or dropdown not displayed.");
		return false;
	}

	/**
	 * V.6 Verifies that the date of birth label and input field are displayed.
	 *
	 * @return true if both the label and field are displayed, false otherwise.
	 */
	public boolean isDobFieldDisplayed() {
		if (isDisplayed(dobLabel) && isDisplayed(dobField)) {
			log.info("Verified: Date of birth label and field are displayed.");
			return true;
		}
		log.warn("Date of birth label or field not displayed.");
		return false;
	}

	/**
	 * V.7 Verifies that the profession label and input field are displayed.
	 *
	 * @return true if both the label and field are displayed, false otherwise.
	 */
	public boolean isProfessionFieldDisplayed() {
		if (isDisplayed(professionLabel) && isDisplayed(professionField)) {
			log.info("Verified: Profession label and field are displayed.");
			return true;
		}
		log.warn("Profession label or field not displayed.");
		return false;
	}

	/**
	 * V.8 Verifies that the description label and textarea field are displayed.
	 *
	 * @return true if both the label and field are displayed, false otherwise.
	 */
	public boolean isDescriptionFieldDisplayed() {
		if (isDisplayed(descriptionLabel) && isDisplayed(descriptionField)) {
			log.info("Verified: Description label and field are displayed.");
			return true;
		}
		log.warn("Description label or field not displayed.");
		return false;
	}

	/**
	 * V.9 Verifies that the agreement checkbox and its accompanying message
	 * are displayed.
	 *
	 * @return true if both the checkbox and message are displayed, false otherwise.
	 */
	public boolean isAgreementCheckboxDisplayed() {
		if (isDisplayed(agreementCheckbox) && isDisplayed(agreementMessage)) {
			log.info("Verified: Agreement checkbox and message are displayed.");
			return true;
		}
		log.warn("Agreement checkbox or message not displayed.");
		return false;
	}

	/**
	 * V.10 Verifies that the "Enter Timepieces" submit button is both displayed
	 * and enabled. Useful for asserting the button state after filling all
	 * required form fields and checking the agreement checkbox.
	 *
	 * @return true if the button is displayed and enabled, false otherwise.
	 */
	public boolean isEnterTimepiecesButtonEnabled() {
		try {
			WebElement btn = getElement(enterTimepiecesButton);
			if (btn.isDisplayed() && btn.isEnabled()) {
				log.info("Verified: 'Enter Timepieces' button is displayed and enabled.");
				return true;
			}
			log.warn("'Enter Timepieces' button is displayed but not enabled.");
			return false;
		} catch (Exception e) {
			log.warn("'Enter Timepieces' button not found or not displayed.");
			return false;
		}
	}

	/**
	 * V.11 Verifies that the secure assurance message ("Your details are
	 * secure...")
	 * is displayed on the onboarding page.
	 *
	 * @return true if the secure assurance message is displayed, false otherwise.
	 */
	public boolean isSecureAssuranceMessageDisplayed() {
		if (isDisplayed(timepiecesDataSecureMessage)) {
			log.info("Verified: Secure assurance message is displayed.");
			return true;
		}
		log.warn("Secure assurance message not displayed.");
		return false;
	}

	/**
	 * V.12 Retrieves the current value of the first name input field.
	 * Useful for verifying pre-populated values on the onboarding form.
	 *
	 * @return the value attribute of the first name field, or null if not present.
	 */
	public String getFirstNameValue() {
		log.debug("Retrieving first name field value.");
		return getAttribute(firstNameField, "value");
	}

	/**
	 * V.13 Retrieves the current value of the email input field.
	 * Useful for verifying pre-populated values on the onboarding form.
	 *
	 * @return the value attribute of the email field, or null if not present.
	 */
	public String getEmailValue() {
		log.debug("Retrieving email field value.");
		return getAttribute(emailField, "value");
	}
}
