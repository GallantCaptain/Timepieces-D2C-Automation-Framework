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
 * LoginPage — Page Object Repository for the Timepieces Login Page.
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
public class LoginPage extends HeaderOnlyBasePage {
    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Already Created Profile & Welcome Message Elements for Login Page
    // Timepieces Image - Before/After Entering Phone Number
    private final By timepiecesLoginPageImage = By.cssSelector("img[alt='Timepieces']");

    // --- Validation & Feedback: Pre-OTP submission After Entering Phone Number ---
    // Access Your Timepieces Message:
    private final By timepiecesWelcomeMessage = By.xpath("//h1[normalize-space()='Access your Timepieces']");
    // Access your profile, application status, and timepieces message
    private final By timepiecesWelcomeMessageDetails = By
            .xpath("//h1[normalize-space()='Access your Timepieces']/following-sibling::p");

    // --- Labels and Fields for Identity Entry & Region Configuration - Pre-OTP submission ---
    /* Phone Label [For Ui Assertion Only, Not For Element Interaction - The actual input field doesn't have a label element, so we will use the label text as an anchor for locating the input field]: */
    private final By phoneNumberLabel = By.xpath("//label[contains(normalize-space(),'Phone')]");

    // Identity Entry & Region Configuration Elements - Before Entering Phone Number    // Country Dropdown Box Container (Clickable):
    private final By countryCodeDropdownBox = By.cssSelector(
            "button[data-testid='login-phone-input-country-button']");
    // Country Dropdown Button Icon (Clickable):
    private final By countryDropdownButton = By.xpath("//button[@type='button']//*[name()='svg']");
    // Country Dropdown List: [Only after opening]
    private final By countryDropdownScrollArea = By.cssSelector("div.phone-dropdown-scroll");
    // Search Input Field: [Only after opening]
    private final By countrySearchField = By.cssSelector("input[placeholder='Search country...']");
    // No countries found message when searching in country code dropdown:
    private final By searchFieldCountryNotFoundMessage = By.xpath("//div[contains(text(),'No countries found')]");
    // The Phone Number field is located using data-testid:
    private final By phoneNumberField = By.cssSelector("input[data-testid='login-phone-input']");
 
    // Process Dispatch & Phase States - Disabled before country & number selected,
    /* captcha verified, Then Submit button is Enabled & Clickable after all conditions met: */
    private final By sendOtpButton = By.cssSelector("button[data-testid='login-send-otp']");

    /* After submitting any otp (valid/invalid) as long as all the input fields are filled. */
    private final By continueButton = By.xpath("//button[@type='submit' and normalize-space()='Continue']");

    /* Security Constraints & Security Firewalls (CloudFlare) - Before OTP ubmission To verify bots and prevent abuse */
    /*
     * Cloudflare CAPTCHA iframe [shadow-root='closed' - WebDriver cannot access its
     * internals, but we can wait for the iframe element itself to confirm it has
     * loaded.
     */
    private final By cloudflareChallengeFrame = By
            .cssSelector("iframe[title*='Widget containing a Cloudflare security challenge']");
    // Captcha Failed Message:
    private final By captchaChallengeFailedMessage = By
            .xpath("//div[contains(text(),'CAPTCHA verification failed. Please try again.')]");

    /* Additional Messages & Feedback - Pre and Post OTP submission Before Phone Number Submission */
    private final By additionalWhatsAppMessage = By
            .xpath("//p[contains(text(),'Your OTP will also be delivered on WhatsApp for faster access.')]");
    // Unsupported Country Message when selecting a country not enabled for OTP:
    private final By unsupportedCountryMessage = By
            .xpath("//div[contains(text(), 'This country is not supported for OTP. Please contact support.')]");

    /* Too many attempts message happens after user changes their phone number after submitting the otp */
    private final By tooManyAttemptsMessageLockout = By.xpath(
            "//div[@class='flex items-center justify-center gap-3 py-3 px-4 rounded-xl bg-amber-900/30 border border-amber-700/40']");
    // Timepieces Data Secure Message [Ui Assertion Only, Always Visible on Login Page]:
    private final By timepiecesSecureAssuranceMessage = By.xpath(
            "//p[contains(normalize-space(), 'Your details are secure and used only for your Timepieces experience.')]");

    // --- Validation & Feedback: Post-OTP submission ---
    // Verify Access Heading on OTP entry screen:
    private final By verifyAccessMessage = By
            .cssSelector("h1[class='text-white text-[24px] md:text-[24px] font-bold ']");
    /* The text label under the "Verify Access" heading that says "Enter the 6-digit code sent to +{Country Code} {First 2 digits}******{Last 2 digits}" */
    private final By enterCodeMessageUnderVerifyAccess = By.cssSelector("p[class='text-gray-400 text-[16px] ']");
    /* Resend OTP link: [Only visible after submitting phone number and landing on OTP entry screen] */
    private final By resendOtpLink = By.xpath("//button[contains(text(),\"Didn't receive it? Resend code\")]");

    /* Otp Input Fields Container and Individual Fields (Only visible after clicking
     * Send OTP and navigating to OTP entry screen):*/
    //OTP Input Field Container: 
    private final By otpInputFieldContainer = By.xpath("//input[@inputmode='numeric']/parent::div");

    // OTP input field [0 - 5]:
    /* In case we want to target individual fields instead of the container, We can use these locators to interact with each individual OTP field. It can be used for more granular control over the OTP input fields. For verifcation and validation purposes */
    private final By otpInputFieldIndex0 = By.xpath("//input[inputmode='numeric' and value='0']");
    private final By otpInputFieldIndex1 = By.xpath("//input[inputmode='numeric' and value='1']");
    private final By otpInputFieldIndex2 = By.xpath("//input[inputmode='numeric' and value='2']");
    private final By otpInputFieldIndex3 = By.xpath("//input[inputmode='numeric' and value='3']");
    private final By otpInputFieldIndex4 = By.xpath("//input[inputmode='numeric' and value='4']");
    private final By otpInputFieldIndex5 = By.xpath("//input[inputmode='numeric' and value='5']");

    // Change Phone Number Link on OTP entry screen (In case user wants to change
    // the phone number after submitting and before entering the OTP):
    private final By changePhoneNumber = By.xpath("//button[contains(text(),'Change phone number')]");

    /* 
    * Login Page {First Time Setup}: https://www.timepieces-d2c.example/login
    * This page is only there for the first time. For profile edits after account creation, the user is taken to the Account Details page which has a different URL and UI.
    * The profile edit page is not gated behind OTP verification, so it does not have the phone number, country code dropdown, or OTP submission elements that are present on the login page. Hence, this LoginPage class only models the first-time profile completion page that is gated behind OTP verification.
    */ 
    // Timepieces Image
    private final By timepiecesImage = By.cssSelector("img[alt='Timepieces']");

    // Complete your profile Message: (Unique for this viewport)
    private final By completeYourProfileMessage = By.xpath(
            "//p[contains(normalize-space(),'Add your details to begin your Timepieces journey.')]/parent::div/h1[@class='text-white text-[24px] font-bold mb-2']");

    // Add your details to begin your Timepieces journey Message:
    private final By addYourDetailsMessage = By.xpath(
            "//p[contains(normalize-space(), 'Add your details to begin your Timepieces journey.')]/parent::div/p[@class='text-gray-400 text-[16px]']");

    // First Name Label: [English]
    private final By firstNameLabel = By.xpath("//label[contains(normalize-space(.), 'First Name')]");

    // First Name Field:
    private final By firstNameField = By
            .xpath("//label[contains(normalize-space(), 'First Name')]/following-sibling::input");

    // First Name Field Placeholder [English]:
    private final By firstNamePlaceholder = By.xpath(
            "//label[contains(normalize-space(), 'First Name')]/following-sibling::input[@placeholder='Eg. John']");

    // Last Name Label: [English]
    private final By lastNameLabel = By.xpath("//label[contains(normalize-space(.), 'Last Name')]");

    // Last Name Field:
    private final By lastNameField = By
            .xpath("//label[contains(normalize-space(), 'Last Name')]/following-sibling::input");

    // Last Name Field Placeholder: [English]
    private final By lastNamePlaceholder = By.xpath(
            "//label[contains(normalize-space(), 'Last Name')]/following-sibling::input[@placeholder='Eg. Doe']");

    // Email Label: [English]
    private final By emailLabel = By.xpath("//label[text()='Email']");

    // Email Field:
    private final By emailField = By.xpath("//label[contains(normalize-space(), 'Email')]/following-sibling::input");

    // Email Field Placeholder: [English]
    private final By emailPlaceholder = By.xpath(
            "//label[contains(normalize-space(), 'Email')]/following-sibling::input[@placeholder='Enter your email']");

    // Phone Label:
    private final By phoneLabel = By.xpath("//label[text()='Phone']");

    // Phone Country Code Dropdown Menu:
    private final By phoneCountryCodeDropdown = By.xpath("//div[@class='relative']//button[@type='button']");

    // Phone Number Field:
    private final By phone_numberField = By.xpath("//input[@inputmode='numeric']");

    // Gender Label: [English]
    private final By genderLabel = By.xpath("//label[contains(normalize-space(.), 'Gender')]");

    // Gender Dropdown Field: Use List to fetch
    private final By genderDropdown = By.xpath("//div[@class='relative']/child::select");

    // Gender List: [Male, Female, Other, Prefer Not To Say]
    private final By maleOption = By.xpath("//option[contains(@value,'Male')]");
    private final By femaleOption = By.xpath("//option[contains(@value,'Female')]");
    private final By otherOption = By.xpath("//option[contains(@value,'Other')]");
    private final By preferNotToSayOption = By.xpath("//option[contains(normalize-space(@value),'Prefer not to say')]");

    // DOB Label: [English]
    private final By dobLabel = By.xpath("//label[contains(normalize-space(), 'Date of Birth')]");

    // DOB Field [mm/dd/yyyy]:
    private final By dobField = By.cssSelector("input[type='date']");

    // DOB field placeholder:
    private final By dobPlaceholder = By.cssSelector("input[placeholder='DD/MM/YYYY']");

    // Profession Label: [English]
    private final By professionLabel = By.xpath("//label[contains(normalize-space(), 'Profession (optional)')]");

    // Profession Field: [Text Field]
    private final By professionField = By
            .xpath("//label[contains(normalize-space(), 'Profession (optional)')]/following-sibling::input");
    // Profession Field Placeholder: [Same as above/ Assert the placeholder]

    // Description Label: [English]
    private final By descriptionLabel = By.xpath("//label[text()='Email']");

    // Description Field:
    private final By descriptionField = By.cssSelector("textarea[placeholder='Write something about yourself']");
    // Description Field Placeholder: [Same as above/ Assert the placeholder]

    // Agreement Checkbox:
    private final By agreementCheckbox = By.cssSelector("input[type='checkbox']");

    // Agreement Message Assertion: [English]
    private final By agreementMessage = By.xpath("//span[contains(normalize-space(.), 'By creating an account')]");

    // Agreement Message Privacy Policy Link:
    private final By privacyPolicyLink = By.cssSelector("a[href='/policies/privacy-policy']");

    // Privacy Policy Link:
    private final By termsOfServiceLink = By.cssSelector("a[href='/policies/terms-of-service']");

    // Enter Timepieces Button (Disabled until form filled and checkbox selected):
    private final By enterTimepiecesButton = By.cssSelector("button[type='submit']");

    // Timepieces Data Secure Message:
    private final By secureMessage = By.xpath(
            "//p[contains(normalize-space(), 'Your details are secure and used only for your Timepieces experience.')]");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * Opens the country code dropdown, types the search term, and clicks the
     * first matching option. Extracted to eliminate the duplication that previously
     * existed across configureCountryCodeByName, configureCountryCodeByNumber,
     * and enterMobileNumber.
     *
     * @param searchTerm The text to type into the search field (country name or
     *                   dial code).
     * @throws RuntimeException if no matching option is found in the dropdown.
     */
    private void openCountryDropdownAndSearch(String searchTerm) {
        log.debug("Opening country code dropdown and searching for: '{}'", searchTerm);

        waitForVisibility(countryDropdownButton);
        clickElement(countryCodeDropdownBox);
        log.debug("Country code dropdown opened.");

        waitForVisibility(countryDropdownScrollArea);
        sendKeys(countrySearchField, searchTerm);
        log.debug("Search term '{}' entered into country search field.", searchTerm);

        /*
         * The previous loop iterated the scroll container itself and called .click() on
         * it, which Chrome rejects with "element click intercepted" because the
         * styled-jsx wrapper inside is the real hit target. Target the leaf-most descendant
         * whose normalized text contains the search term — that's the actual option item.
         * Substring contains() picks up false positives: searching "India" also matched
         * "British Indian Ocean Territory" and document order returned +246 first. Use
         * a word-boundary check by wrapping both the haystack and the needle in spaces —
         * so "India" only matches when surrounded by whitespace (start/end of string
         * counts via the space-wrap), not when embedded inside "Indian" or similar.
         */
        String literal = xpathLiteral(searchTerm);
        String wordMatch = "contains(concat(' ', normalize-space(.), ' '), concat(' ', "
                + literal + ", ' '))";
        By countryOption = By.xpath(
                "(//div[contains(@class,'phone-dropdown-scroll')]" +
                        "//*[" + wordMatch + " and not(.//*[" + wordMatch + "])])[1]");
        try {
            clickElement(countryOption);
            log.debug("Country option selected for search term: '{}'", searchTerm);
        } catch (Exception e) {
            log.error("No clickable country option matched search term: '{}'", searchTerm);
            throw new RuntimeException("Country not found in dropdown for search term: " + searchTerm, e);
        }
    }

    /*
     * This is to ensure proper XPath literalization. It works for strings
     * containing single or double quotes.
     */
    private static String xpathLiteral(String value) {
        if (!value.contains("'"))
            return "'" + value + "'";
        if (!value.contains("\""))
            return "\"" + value + "\"";
        StringBuilder sb = new StringBuilder("concat(");
        String[] parts = value.split("'", -1);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0)
                sb.append(", \"'\", ");
            sb.append("'").append(parts[i]).append("'");
        }
        sb.append(")");
        return sb.toString();
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Navigates to the Timepieces login page and waits for the welcome message to
     * confirm it has fully loaded. The base URL is sourced from config so it works
     * across
     * environments (staging, prod) without touching test code.
     *
     * @return this LoginPage for method chaining.
     */
    public LoginPage open() {
        String baseUrl = ConfigReader.get("base.url");
        String loginPath = ConfigReader.get("login.path");
        String loginUrl = baseUrl + loginPath;

        String env = System.getProperty("env");
        if ("dev".equalsIgnoreCase(env)) {
            String bypassToken = System.getenv("AUTOMATION_TOKEN");
            if (bypassToken == null || bypassToken.isEmpty()) {
                bypassToken = System.getProperty("AUTOMATION_TOKEN");
            }
            if (bypassToken != null && !bypassToken.isEmpty()) {
                String devLoginUrl = baseUrl + "/dev-login?token=" + bypassToken;
                log.info("Dev environment detected. Navigating via Google OAuth token bypass URL: {}", devLoginUrl);
                BrowserUtils.navigateTo(driver, devLoginUrl, 15);
                
                // Navigate back to loginUrl to initialize session
                BrowserUtils.navigateTo(driver, loginUrl, 15);
            } else {
                log.warn("AUTOMATION_TOKEN environment variable not set, navigating normally.");
                BrowserUtils.navigateTo(driver, loginUrl, 15);
            }
        } else {
            log.info("Navigating to login page: {}", loginUrl);
            BrowserUtils.navigateTo(driver, loginUrl, 15);

            // Non-prod environments may sit behind a password gate. Unlock transparently
            // and re-navigate so the welcome message wait below behaves identically.
            if (new StagingGatePage(driver).unlockIfPresent()) {
                BrowserUtils.navigateTo(driver, loginUrl, 15);
            }
        }

        try {
            waitForVisibility(timepiecesWelcomeMessage);
            log.info("Login page loaded and welcome message confirmed visible.");
        } catch (TimeoutException e) {
            log.info("Welcome message wait timed out. User might be authenticated via bypass.");
        }
        return this;
    }

    /**
     * B.2 Selects a country from the phone code dropdown by searching for its name.
     * Opens the dropdown, types the country name in the search field, then clicks
     * the matching option. Extracted as a public method for cases where only the
     * country needs to change without re-entering the phone number.
     *
     * @param countryName The full country name to search for (e.g., "India").
     * @return this LoginPage for method chaining.
     */
    public LoginPage configureCountryCodeByName(String countryName) {
        log.info("Configuring country code by name: {}", countryName);
        openCountryDropdownAndSearch(countryName);
        return this;
    }

    /**
     * B.3 Selects a country from the phone code dropdown by searching for its dial
     * code.
     * Prepends '+' to the integer code before searching (e.g., 91 → "+91").
     *
     * @param countryCode The international dial code as an integer (e.g., 91 for
     *                    India).
     * @return this LoginPage for method chaining.
     */
    public LoginPage configureCountryCodeByNumber(int countryCode) {
        log.info("Configuring country code by dial code: +{}", countryCode);
        openCountryDropdownAndSearch("+" + countryCode);
        return this;
    }

    /**
     * B.4 Enters the phone number into the phone number input field.
     * Does NOT include the country code — configure that separately with
     * configureCountryCodeByName() or configureCountryCodeByNumber().
     *
     * @param phoneNumber The phone number digits without the country code prefix.
     * @return this LoginPage for method chaining.
     */
    public LoginPage enterPhoneNumber(String phoneNumber) {
        log.info("Entering phone number: {}", phoneNumber);
        sendKeys(phoneNumberField, phoneNumber);
        log.info("Phone Number {} entered successfully", phoneNumber);
        return this;
    }

    /**
     * B.5 This is a convenience method — it configures country code by name AND
     * enters
     * the phone number
     * in a single call. Use in test flows where both need to be set from scratch.
     *
     * @param countryName The full country name (e.g., "India").
     * @param phoneNumber The phone number digits without the country code prefix.
     * @return this LoginPage for method chaining.
     */
    public LoginPage enterTotalMobileNumber(String countryName, String phoneNumber) {
        log.info("Configuring country '{}' and entering the phone number: {}", countryName, phoneNumber);
        configureCountryCodeByName(countryName);
        enterPhoneNumber(phoneNumber);
        return this;
    }

    /**
     * B.6 Waits for the Cloudflare CAPTCHA iframe to be present in the DOM.
     * Call this after entering the phone number, before clicking Send OTP,
     * only in environments where CAPTCHA is enabled. The Send OTP button
     * remains disabled until CAPTCHA resolves.
     *
     * NOTE: WebDriver cannot solve the CAPTCHA. This method only waits for the
     * iframe to confirm it has loaded. In staging, use a Turnstile test key
     * (always-passes key) so CAPTCHA auto-resolves without manual intervention.
     *
     * @param timeoutInSeconds Maximum time to wait for the CAPTCHA iframe.
     * @return this LoginPage for method chaining.
     */
    public LoginPage waitForCloudFlareCaptcha(int timeoutInSeconds) {
        log.info("Waiting for CloudFlare CAPTCHA iframe to load (max {}s)...", timeoutInSeconds);
        WindowUtils.waitAndSwitchToFrame(
                driver,
                getElementPresence(cloudflareChallengeFrame),
                timeoutInSeconds);
        WindowUtils.switchToDefaultContent(driver);
        log.info("CloudFlare CAPTCHA iframe loaded. Switched back to default content.");
        return this;
    }

    /**
     * B.7 Clicks the Send OTP button to dispatch the OTP to the configured phone
     * number.
     * Waits for the button to be clickable first — it is disabled until both
     * country
     * and phone number are entered and (in environments where it applies) CAPTCHA
     * is resolved.
     *
     * @return this LoginPage for method chaining.
     */
    public LoginPage clickSendOtpButton() {
        // Send OTP stays disabled until Cloudflare Turnstile resolves (managed
        // challenges
        // can take 5-30s). The default 15s wait is too short and asks the wrong
        // question
        // (clickable vs. enabled). Poll the disabled attribute with a CAPTCHA-aware
        // budget.
        int captchaWaitSeconds = Optional.ofNullable(ConfigReader.get("captcha.wait.timeout"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .orElse(45);
        log.info("Waiting up to {}s for Send OTP button to become enabled (CAPTCHA resolution)...",
                captchaWaitSeconds);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(captchaWaitSeconds))
                    .until(d -> {
                        WebElement btn = d.findElement(sendOtpButton);
                        return btn.isEnabled() && btn.getAttribute("disabled") == null;
                    });
        } catch (TimeoutException e) {
            log.error("Send OTP button did not become enabled within {}s — CAPTCHA likely unresolved "
                    + "(headless detection or interactive challenge served).", captchaWaitSeconds);
            throw e;
        }
        log.info("Send OTP button is enabled. Clicking.");
        clickElement(sendOtpButton);
        try {
            waitForVisibility(verifyAccessMessage);
        } catch (TimeoutException e) {
            log.error("Timeout waiting for verifyAccessMessage. Page source: \n{}", driver.getPageSource());
            throw e;
        }
        log.info("OTP dispatched. OTP entry screen is now visible.");
        return this;
    }

    /**
     * B.8 Enters the OTP across the individual digit input fields.
     * The OTP page renders one input per digit (otpInputField). This method
     * distributes each character of the OTP string to its corresponding field.
     * Assumes the OTP length matches the number of input fields on the page.
     *
     * @param otp The full OTP string (e.g., "123456").
     * @return this LoginPage for method chaining.
     * @throws IllegalArgumentException if OTP length does not match the field
     *                                  count.
     */
    public LoginPage enterOtp(String otp) {
        log.info("Entering OTP (length: {} digits).", otp.length());
        List<WebElement> fields = getElements(otpInputFieldContainer).stream()
                .flatMap(container -> container.findElements(By.cssSelector("input.otp-input")).stream())
                .toList();

        if (otp.length() != fields.size()) {
            throw new IllegalArgumentException(
                    String.format("OTP length mismatch: provided OTP has %d digits but page has %d input fields.",
                            otp.length(), fields.size()));
        }

        for (int i = 0; i < fields.size(); i++) {
            fields.get(i).sendKeys(String.valueOf(otp.charAt(i)));
        }
        log.info("OTP entered across {} digit fields", fields.size());
        return this;
    }

    /**
     * B.9 Clicks the Continue button to submit the entered OTP and complete login.
     * This is the final action on the LoginPage. After a successful login,
     * the application redirects to the homepage (new users see OnboardingPage first).
     *
     * @return this LoginPage for now. Return type will be updated once the
     *         full E2E login-to-account flow is wired and tested.
     */
    public LoginPage clickContinueButton() {
        log.info("Clicking Continue button to submit OTP.");
        clickElement(continueButton);
        log.info("Continue clicked. Login flow submitted.");
        // TODO: Replace return type with AccountPage once AccountPage POM is created:
        // return new AccountPage(driver);
        return this;
    }

    /**
     * B.10 Clicks the "Change phone number" link on the OTP entry screen.
     * Returns the user to the phone number entry view.
     *
     * @return this LoginPage for method chaining.
     */
    public LoginPage clickChangePhoneNumber() {
        log.info("Clicking 'Change phone number' link.");
        clickElement(changePhoneNumber);
        waitForVisibility(phoneNumberField);
        log.info("Returned to phone number entry view.");
        return this;
    }

    /**
     * B.11 Clicks the "Resend OTP" link to request a new OTP code.
     *
     * @return this LoginPage for method chaining.
     */
    public LoginPage clickResendOtp() {
        log.info("Clicking 'Resend OTP' link.");
        clickElement(resendOtpLink);
        log.info("Resend OTP requested.");
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
     * V.1 Verifies that the login page welcome message is visible.
     * Use in smoke tests to confirm the page loaded correctly before proceeding.
     *
     * @return true if the welcome message is displayed, false otherwise.
     */
    public boolean isWelcomeMessageDisplayed() {
        if (isDisplayed(timepiecesWelcomeMessage) && isDisplayed(timepiecesWelcomeMessageDetails)) {
            log.info("verified: Welcome message and details are displayed");
            return true;
        }
        log.warn("Welcome message not displayed.");
        return false;
    }

    /**
     * V.2 Verifies that the OTP entry screen is displayed after submitting the
     * phone
     * number.
     * Checks for the 'Verify Access' heading that appears when the OTP section
     * loads.
     *
     * @return true if the OTP entry screen is displayed, false otherwise.
     */
    public boolean isOtpEntryScreenDisplayed() {
        if (isDisplayed(verifyAccessMessage) && isDisplayed(enterCodeMessageUnderVerifyAccess)) {
            log.info("Verified: OTP entry screen messages are displayed.");
            return true;
        }
        log.warn("OTP entry screen messages not displayed.");
        return false;
    }

    /**
     * V.3 Verifies whether the 'unsupported country' message is visible.
     * Use in negative test cases when selecting a country not enabled for OTP.
     *
     * @return true if the unsupported country message is displayed, false
     *         otherwise.
     */
    public boolean isUnsupportedCountryMessageDisplayed() {
        if (isDisplayed(unsupportedCountryMessage)) {
            log.info("Verified: unsupported country message is displayed.");
            return true;
        }
        log.warn("Unsupported country message not displayed.");
        return false;
    }

    /**
     * V.4 Verifies whether the CAPTCHA verification failed message is visible.
     * Use in negative test cases to confirm correct error feedback on CAPTCHA
     * failure.
     *
     * @return true if the CAPTCHA failed message is displayed, false otherwise.
     */
    public boolean isCaptchaFailedMessageDisplayed() {
        if (isDisplayed(captchaChallengeFailedMessage)) {
            log.info("Verified: CAPTCHA failed message is displayed.");
            return true;
        }
        log.warn("CAPTCHA failed message not displayed.");
        return false;
    }

    /**
     * V.5 Verifies that the WhatsApp OTP delivery notice is visible on the login
     * page.
     * Use to confirm the correct supporting message is shown to the user.
     *
     * @return true if the WhatsApp message is displayed, false otherwise.
     */
    public boolean isAdditionalWhatsAppMessageDisplayed() {
        if (isDisplayed(additionalWhatsAppMessage)) {
            log.info("Verified: WhatsApp OTP delivery message is displayed.");
            return true;
        }
        log.warn("WhatsApp OTP delivery message not displayed.");
        return false;
    }

    /**
     * V.6 Verifies that the 'No countries found' message appears when searching for
     * a
     * non-existent country in the country code dropdown.
     * Use in negative test cases to confirm correct feedback when dropdown search
     * yields no results.
     * 
     * @return true if the 'No countries found' message is displayed, false
     *         otherwise.
     */
    public boolean isNoCountriesFoundMessageDisplayed() {
        if (isDisplayed(searchFieldCountryNotFoundMessage)) {
            log.info("Verified: 'No countries found' message is displayed.");
            return true;
        }
        log.warn("'No countries found' message not displayed.");
        return false;
    }

    /**
     * V.7 Verifies that the 'Too many attempts' lockout message is displayed after
     * multiple failed OTP submissions.
     * Use in negative test cases to confirm correct feedback when user is
     * temporarily locked out due to too many failed attempts.
     * For now, the Too Many Attempts triggers after the user changes their phone
     * with or without submitting the OTP,
     * but this may evolve to also trigger after a certain number of failed OTP
     * entries. This method can be used to verify the message in either scenario.
     * 
     * @return true if the 'Too many attempts' lockout message is displayed, false
     *         otherwise.
     */
    public boolean isTooManyAttemptsLockoutMessageDisplayed() {
        if (isDisplayed(tooManyAttemptsMessageLockout)) {
            log.info("Verified: 'Too many attempts' lockout message is displayed.");
            return true;
        }
        log.warn("'Too many attempts' lockout message not displayed.");
        return false;
    }

    /**
     * V.8 Verifies that the 'Your details are secure' assurance message is visible.
     * This message is always present on the login page as a trust signal.
     *
     * @return true if the secure assurance message is displayed, false otherwise.
     */
    public boolean isSecureAssuranceMessageDisplayed() {
        if (isDisplayed(timepiecesSecureAssuranceMessage)) {
            log.info("Verified: Secure assurance message is displayed.");
            return true;
        }
        log.warn("Secure assurance message not displayed.");
        return false;
    }

    /**
     * V.9 Verifies that the Timepieces brand image is visible on the login page.
     * Use in smoke tests to confirm visual branding elements loaded correctly.
     *
     * @return true if the Timepieces image is displayed, false otherwise.
     */
    public boolean isLoginPageImageDisplayed() {
        if (isDisplayed(timepiecesLoginPageImage)) {
            log.info("Verified: Timepieces login page image is displayed.");
            return true;
        }
        log.warn("Timepieces login page image not displayed.");
        return false;
    }

    /**
     * V.10 Verifies that the phone number label is visible on the login page.
     * Use for UI assertion to confirm the form label rendered correctly.
     *
     * @return true if the phone label is displayed, false otherwise.
     */
    public boolean isPhoneLabelDisplayed() {
        if (isDisplayed(phoneNumberLabel)) {
            log.info("Verified: Phone number label is displayed.");
            return true;
        }
        log.warn("Phone number label not displayed.");
        return false;
    }

    /**
     * V.11 Verifies that the 'Resend OTP' link is visible on the OTP entry screen.
     * Use to confirm the resend option is available before attempting to click it.
     *
     * @return true if the resend OTP link is displayed, false otherwise.
     */
    public boolean isResendOtpLinkDisplayed() {
        if (isDisplayed(resendOtpLink)) {
            log.info("Verified: Resend OTP link is displayed.");
            return true;
        }
        log.warn("Resend OTP link not displayed.");
        return false;
    }
}