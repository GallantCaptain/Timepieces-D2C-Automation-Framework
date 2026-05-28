package com.timepieces.automation.pages.web.account;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.timepieces.automation.pages.BasePage;
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
public class LoginPage extends BasePage {
    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Identity Entry & Region Configuration Elements
    private final By phoneNumberField = By.cssSelector("input[placeholder='Enter your phone number']");
    private final By countryCodeDropdownBox = By.xpath(
            "(//button[@class='flex items-center gap-1 py-3 pr-2 text-[14px] hover:bg-white/10 transition-colors rounded'])");
    private final By countryDropdownButton = By.xpath("//button[@type='button']//*[name()='svg']");
    private final By countryDropdownScrollArea = By.cssSelector("div.phone-dropdown-scroll");
    private final By countrySearchField = By.cssSelector("input[placeholder='Search country...']");

    // Process Dispatch & Phase States
    private final By sendOtpButton = By.cssSelector("button[type='submit']"); // Disabled before country & number
                                                                              // selected, captcha verified
    private final By otpInputFields = By.cssSelector("input.otp-input");
    private final By continueButton = By.cssSelector("button.continue-button");

    // Security Constraints & Security Firewalls (CloudFlare) - Before OTP
    // submission
    private final By cloudflareChallengeFrame = By
            .cssSelector("iframe[title*='Widget containing a Cloudflare security challenge']");

    // --- Validation & Feedback: Pre-OTP submission ---
    private final By welcomeMessage = By.xpath("//h1[normalize-space()='Access your Timepieces']");
    private final By welcomeMessageDetails = By.xpath("//p[contains(@class,'text-gray-400 text-[16px]')]");
    private final By additionalWhatsAppMessage = By
            .xpath("//p[contains(text(),'Your OTP will also be delivered on WhatsApp for faster access.')]");
    private final By unsupportedCountryMessage = By
            .xpath("//div[contains(text(), 'This country is not supported for OTP. Please contact support.')]");
    private final By captchaChallengeFailedMessage = By
            .xpath("//div[contains(text(),'CAPTCHA verification failed. Please try again.')]");

    // --- Validation & Feedback: Post-OTP submission ---
    // XPath string containing an apostrophe must use double-quote delimiters to
    // avoid malformed XPath (the apostrophe in "Didn't" would close a single-quoted
    // string early).
    private final By verifyAccessMessage = By
            .cssSelector("h1[class='text-white text-[24px] md:text-[24px] font-bold ']");
    private final By enterCodeMessageUnderVerifyAccess = By.cssSelector("p[class='text-gray-400 text-[16px] ']");
    private final By resendOtpLink = By.xpath("//button[contains(text(),\"Didn't receive it? Resend code\")]");
    private final By changePhoneNumber = By.xpath("//button[contains(text(),'Change phone number')]");

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

        // The previous loop iterated the scroll container itself and called .click() on it,
        // which Chrome rejects with "element click intercepted" because the styled-jsx wrapper
        // inside is the real hit target. Target the leaf-most descendant whose normalized text
        // contains the search term — that's the actual option item.
        // Substring contains() picks up false positives: searching "India" also matched
        // "British Indian Ocean Territory" and document order returned +246 first. Use a
        // word-boundary check by wrapping both the haystack and the needle in spaces — so
        // "India" only matches when surrounded by whitespace (start/end of string counts via
        // the space-wrap), not when embedded inside "Indian" or similar.
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

    private static String xpathLiteral(String value) {
        if (!value.contains("'")) return "'" + value + "'";
        if (!value.contains("\"")) return "\"" + value + "\"";
        StringBuilder sb = new StringBuilder("concat(");
        String[] parts = value.split("'", -1);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(", \"'\", ");
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
        log.info("Navigating to login page: {}", loginUrl);
        BrowserUtils.navigateTo(driver, loginUrl, 15);

        // Non-prod environments may sit behind a password gate. Unlock transparently
        // and re-navigate so the welcome message wait below behaves identically across envs.
        if (new StagingGatePage(driver).unlockIfPresent()) {
            BrowserUtils.navigateTo(driver, loginUrl, 15);
        }

        waitForVisibility(welcomeMessage);
        log.info("Login page loaded and welcome message confirmed visible.");
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
        log.info("CloudFlare CAPTCHA ifrae loaded. Switched back to default content.");
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
        // Send OTP stays disabled until Cloudflare Turnstile resolves (managed challenges
        // can take 5-30s). The default 15s wait is too short and asks the wrong question
        // (clickable vs. enabled). Poll the disabled attribute with a CAPTCHA-aware budget.
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
        waitForVisibility(verifyAccessMessage);
        log.info("OTP dispatched. OTP entry screen is now visible.");
        return this;
    }

    /**
     * B.8 Enters the OTP across the individual digit input fields.
     * The OTP page renders one input per digit (input.otp-input). This method
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
        List<WebElement> fields = getElements(otpInputFields);

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
     * the application navigates away from this page to the dashboard or home.
     * Return type is void here — replace with `return new DashboardPage(driver)`
     * once the DashboardPage POM is created.
     *
     * @return this LoginPage (update to DashboardPage once that POM is ready).
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
        if (isDisplayed(welcomeMessage) && isDisplayed(welcomeMessageDetails)) {
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

}