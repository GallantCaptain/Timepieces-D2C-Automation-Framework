package com.timepieces.automation.pages.web.account;

import java.time.Duration;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.timepieces.automation.exceptions.OtpRateLimitException;
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
 * SECURITY NOTE — current OTP controls (observed on staging):
 * 1. OTP VERIFICATION HAS NO ATTEMPT LIMIT. Continue can be pressed unlimited
 * times, and the
 * OTP fields can be cleared and re-tried with different combinations
 * indefinitely — no
 * lockout, no escalating backoff, no CAPTCHA on the verify step. The 6-digit
 * (1,000,000
 * value) space is therefore fully brute-forceable. See
 * {@link #probeUnlimitedOtpAttempts(int)}
 * and the @OtpBruteForce security scenario, which document this exposure.
 * 2. RESEND OTP is gated only by a fixed ~90s countdown, then re-enabled — with
 * NO cap on the
 * total number of resends as long as each 90s window is waited out. See
 * {@link #clickResendOtp()}.
 * 3. A ~90s "Too many attempts" COOLDOWN ({@code login-cooldown}) is the only
 * lockout, and it is
 * triggered by phone-number churn (submit a wrong number then the correct one,
 * or submit the
 * correct number then go back to change it) — NOT by wrong OTP guesses. See
 * {@link #clickChangePhoneNumber()} / {@link #waitOutOtpCooldown(int)}.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (LoginPage) to allow
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
    // Login Phone Form Container:
    private final By loginPhoneFormSection = By.cssSelector("[data-testid='login-phone-form']");

    // Already Created Profile & Welcome Message Elements for Login Page
    // Timepieces Image - Before/After Entering Phone Number
    private final By timepiecesLoginPageImage = By.cssSelector("img[alt='Timepieces']");

    // --- Validation & Feedback: Pre-OTP submission After Entering Phone Number ---
    // Access Your Timepieces Message:
    private final By timepiecesWelcomeMessage = By.cssSelector("h1[data-testid='login-phone-heading']");
    // Access your profile, application status, and timepieces message
    private final By timepiecesWelcomeMessageDetails = By.cssSelector("h1[data-testid='login-phone-heading'] + p");

    // --- Labels and Fields for Identity Entry & Region Configuration - Pre-OTP
    // submission ---
    /*
     * Phone Label [For Ui Assertion Only, Not For Element Interaction - The actual
     * input field doesn't have a label element, so we will use the label text as an
     * anchor for locating the input field]:
     */
    private final By phoneNumberLabel = By.xpath("//label[contains(normalize-space(),'Phone')]");

    // Identity Entry & Region Configuration Elements - Before Entering Phone Number
    // // Country Dropdown Box Container (Clickable):
    private final By countryCodeDropdownBox = By.cssSelector(
            "button[data-testid='login-phone-input-country-button']");
    // Country Dropdown List: [Only after opening]
    private final By countryDropdownScrollArea = By.cssSelector("div.phone-dropdown-scroll");
    // Search Input Field: [Only after opening]
    private final By countrySearchField = By.cssSelector("input[data-testid='login-phone-input-country-search']");
    // No countries found message when searching in country code dropdown:
    private final By searchFieldCountryNotFoundMessage = By.xpath("//div[contains(text(),'No countries found')]");
    // The Phone Number field is located using data-testid:
    private final By phoneNumberField = By.cssSelector("input[data-testid='login-phone-input']");

    // Process Dispatch & Phase States - Disabled before country & number selected,
    /*
     * captcha verified, Then Submit button is Enabled & Clickable after all
     * conditions met:
     */
    private final By sendOtpButton = By.cssSelector("button[data-testid='login-send-otp']");

    /*
     * After submitting any otp (valid/invalid) as long as all the input fields are
     * filled.
     */
    private final By continueButton = By.cssSelector("button[data-testid='login-verify-otp']");

    /*
     * Security Constraints & Security Firewalls (CloudFlare) - Before OTP ubmission
     * To verify bots and prevent abuse
     */
    /*
     * Cloudflare CAPTCHA iframe [shadow-root='closed' - WebDriver cannot access its
     * internals, but we can wait for the iframe element itself to confirm it has
     * loaded.
     */
    private final By cloudflareChallengeFrame = By
            .cssSelector("iframe[title*='Widget containing a Cloudflare security challenge']");

    /*
     * Additional Messages & Feedback - Pre and Post OTP submission Before Phone
     * Number Submission
     */
    private final By additionalWhatsAppMessage = By.cssSelector("button[data-testid='login-send-otp'] + p");
    // Invalid Phone Number [Only supported countries get the check - India, USA,
    // UK, UAE]
    private final By invalidPhoneNumberMessage = By.cssSelector("div[data-testid='login-error']");
    // Unsupported Country Message when selecting a country not enabled for OTP:
    private final By unsupportedCountryMessage = By.cssSelector("div[data-testid='login-error']");
    // Invalid OTP Message - Wrong OTP Submission
    /* The invalid OTP button has unlimited attempts */
    private final By invalidOtpMessage = By.cssSelector("div[data-testid='login-otp-error']");

    /*
     * Too many attempts message happens after user changes their phone number after
     * submitting the otp
     */
    private final By tooManyAttemptsMessageLockout = By.cssSelector("div[data-testid='login-cooldown']");

    // NOTE: an OTP-SEND rate-limit backoff toast ("Too many attempts / Please wait
    // Ns") used to be
    // mapped here, but that throttle is disabled on staging (sending is effectively
    // unlimited with a
    // short reset). The only real lockout is the post-OTP change-phone cooldown,
    // mapped above as
    // tooManyAttemptsMessageLockout (login-cooldown). Locators intentionally
    // removed — see the
    // class-level SECURITY NOTE.

    // Timepieces Data Secure Message [Ui Assertion Only, Always Visible on Login
    // Page]:
    private final By timepiecesSecureAssuranceMessage = By.cssSelector("button[data-testid='login-verify-otp'] + p");

    // --- Validation & Feedback: Post-OTP submission ---
    // Verify Access Heading on OTP entry screen:
    private final By verifyAccessMessage = By
            .cssSelector("h1[data-testid='login-otp-heading']");
    /*
     * The text label under the "Verify Access" heading that says
     * "Enter the 6-digit code sent to +{Country Code} {First 2 digits}******{Last 2 digits}"
     */
    private final By enterCodeMessageUnderVerifyAccess = By.cssSelector("h1[data-testid='login-otp-heading'] + p");
    /*
     * Resend OTP link: [Only visible after submitting phone number and landing on
     * OTP entry screen]
     */
    private final By resendOtpLink = By.cssSelector("button[data-testid='login-resend-otp']");

    /*
     * Otp Input Fields Container and Individual Fields (Only visible after clicking
     * Send OTP and navigating to OTP entry screen):
     */
    // OTP Input Field Container:
    private final By otpInputFieldContainer = By.cssSelector("div[data-testid='login-otp-inputs']");

    // OTP input field [0 - 5]:
    /*
     * In case we want to target individual fields instead of the container, We can
     * use these locators to interact with each individual OTP field. It can be used
     * for more granular control over the OTP input fields. For verifcation and
     * validation purposes
     */
    private final By otpInputFieldIndex0 = By.cssSelector("input[data-testid='login-otp-input-0']");
    private final By otpInputFieldIndex1 = By.cssSelector("input[data-testid='login-otp-input-1']");
    private final By otpInputFieldIndex2 = By.cssSelector("input[data-testid='login-otp-input-2']");
    private final By otpInputFieldIndex3 = By.cssSelector("input[data-testid='login-otp-input-3']");
    private final By otpInputFieldIndex4 = By.cssSelector("input[data-testid='login-otp-input-4']");
    private final By otpInputFieldIndex5 = By.cssSelector("input[data-testid='login-otp-input-5']");

    // Change Phone Number Link on OTP entry screen (In case user wants to change
    // the phone number after submitting and before entering the OTP):
    private final By changePhoneNumber = By.cssSelector("button[data-testid='login-change-phone']");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * P.1 Opens the country code dropdown, types the search term, and clicks the
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

        waitForVisibility(countryCodeDropdownBox);
        clickElement(countryCodeDropdownBox);
        log.debug("Country code dropdown opened.");

        waitForVisibility(countryDropdownScrollArea);
        sendKeys(countrySearchField, searchTerm);
        log.debug("Search term '{}' entered into country search field.", searchTerm);

        /*
         * The previous loop iterated the scroll container itself and called .click() on
         * it, which Chrome rejects with "element click intercepted" because the
         * styled-jsx wrapper inside is the real hit target. Target the leaf-most
         * descendant whose normalized text contains the search term — that's the
         * actual option item.
         * Substring contains() picks up false positives: searching "India" also matched
         * "British Indian Ocean Territory" and document order returned +246 first. Use
         * a word-boundary check by wrapping both the haystack and the needle in spaces
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
     * P.2 This is to ensure proper XPath literalization. It works for strings
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

    /**
     * P.3 Polls until the element is present, enabled, and has no {@code disabled}
     * attribute,
     * or the timeout elapses. Used for buttons gated by a countdown (e.g. Resend
     * OTP).
     */
    private void waitForElementEnabled(By locator, int timeoutSeconds) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(d -> {
            try {
                WebElement el = d.findElement(locator);
                return el.isEnabled() && el.getAttribute("disabled") == null;
            } catch (org.openqa.selenium.NoSuchElementException ex) {
                return false;
            }
        });
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
        // and re-navigate so the welcome message wait below behaves identically.
        if (new StagingGatePage(driver).unlockIfPresent()) {
            BrowserUtils.navigateTo(driver, loginUrl, 15);
        }

        try {
            waitForVisibility(timepiecesWelcomeMessage);
            log.info("Login page loaded and welcome message confirmed visible.");
        } catch (TimeoutException e) {
            log.info("Welcome message wait timed out.");
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
            // The OTP-send "Too many attempts, wait Ns" backoff toast is DISABLED (sending
            // is
            // effectively unlimited with a short reset), so it is no longer the suspect
            // here.
            // The only remaining send-OTP blocker is the ~90s "Too many attempts" COOLDOWN
            // (login-cooldown), tripped by phone-number churn. Surface that distinctly so
            // it is
            // reported as a known cooldown and NOT blindly retried (RetryAnalyzer skips
            // it).
            if (isOtpCooldownActive()) {
                int cooldown = configuredOtpCooldownSeconds();
                log.error("OTP send blocked by the ~{}s 'Too many attempts' cooldown (login-cooldown).", cooldown);
                throw new OtpRateLimitException(cooldown);
            }
            log.error("Timeout waiting for verifyAccessMessage. Page source: \n{}", driver.getPageSource());
            throw e;
        }
        log.info("OTP dispatched. OTP entry screen is now visible.");
        return this;
    }

    /**
     * B. 8 Fast, non-blocking check for the ~90s "Too many attempts" cooldown
     * lockout
     * (the {@code login-cooldown} element). Uses {@code findElements} so it adds no
     * wait.
     *
     * @return true if the cooldown lockout is currently on the page.
     */
    public boolean isOtpCooldownActive() {
        return !driver.findElements(tooManyAttemptsMessageLockout).isEmpty();
    }

    /** Configured OTP cooldown length in seconds (default 90). */
    private int configuredOtpCooldownSeconds() {
        return Optional.ofNullable(ConfigReader.get("otp.cooldown.seconds"))
                .map(String::trim).filter(s -> !s.isEmpty())
                .map(Integer::parseInt).orElse(90);
    }

    /**
     * B.9 Enters the OTP across the individual digit input fields.
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
        By[] fieldsLocators = {
                otpInputFieldIndex0,
                otpInputFieldIndex1,
                otpInputFieldIndex2,
                otpInputFieldIndex3,
                otpInputFieldIndex4,
                otpInputFieldIndex5
        };

        if (otp.length() != fieldsLocators.length) {
            throw new IllegalArgumentException(
                    String.format("OTP length mismatch: provided OTP has %d digits but page has %d input fields.",
                            otp.length(), fieldsLocators.length));
        }

        for (int i = 0; i < fieldsLocators.length; i++) {
            sendKeys(fieldsLocators[i], String.valueOf(otp.charAt(i)));
        }
        log.info("OTP entered across {} digit fields", fieldsLocators.length);
        return this;
    }

    /**
     * B.10 Clicks the Continue button to submit the entered OTP and complete login.
     * This is the final action on the LoginPage. After a successful login,
     * the application redirects to the homepage (new users see OnboardingPage
     * first).
     *
     * @return this LoginPage for now. Return type will be updated once the
     *         full E2E login-to-account flow is wired and tested.
     */
    public LoginPage clickContinueButton() {
        log.info("Clicking Continue button to submit OTP.");
        clickElement(continueButton);
        log.info("Continue clicked. Login flow submitted.");
        // SECURITY: there is NO limit on how many times this can be pressed with wrong
        // codes,
        // and no lockout/backoff on the verify step — see
        // probeUnlimitedOtpAttempts(int) and the
        // @OtpBruteForce scenario. The only lockout (login-cooldown) comes from
        // phone-number
        // churn, not from wrong OTP guesses.
        // TODO: Replace return type with AccountPage once AccountPage POM is created:
        // return new AccountPage(driver);
        return this;
    }

    /**
     * B.11 Clicks the "Change phone number" link on the OTP entry screen.
     * Returns the user to the phone number entry view.
     *
     * SECURITY/REALITY: changing the number after submitting an OTP — or submitting
     * a wrong
     * number then the correct one — trips the ~90s "Too many attempts" cooldown
     * ({@code login-cooldown}), during which Send OTP is blocked. Call
     * {@link #waitOutOtpCooldown(int)} if a test must continue through it.
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
     * B.12 Clicks the "Resend OTP" link to request a new OTP code.
     *
     * REALITY/SECURITY: resend is gated only by a fixed ~90s countdown after each
     * dispatch —
     * the button is disabled until it elapses, then re-enabled, with NO cap on the
     * total number
     * of resends as long as each window is waited out. This method waits for the
     * button to
     * become enabled (cooldown + small buffer) before clicking.
     *
     * @return this LoginPage for method chaining.
     */
    public LoginPage clickResendOtp() {
        int cooldown = Optional.ofNullable(ConfigReader.get("otp.resend.cooldown.seconds"))
                .map(String::trim).filter(s -> !s.isEmpty())
                .map(Integer::parseInt).orElse(90);
        log.info("Waiting up to {}s for the Resend OTP {}s cooldown to elapse...", cooldown + 5, cooldown);
        waitForElementEnabled(resendOtpLink, cooldown + 5);
        clickElement(resendOtpLink);
        log.info("Resend OTP requested; a fresh {}s cooldown now begins.", cooldown);
        return this;
    }

    /**
     * B.13 SECURITY PROBE — demonstrates the missing OTP attempt limit.
     * Submits {@code attempts} deliberately-WRONG 6-digit codes back to back,
     * clearing the
     * fields between each (exactly what an attacker's script does). The verify step
     * has no
     * server-side cap, so each wrong code is merely rejected and the OTP screen
     * stays usable.
     *
     * @param attempts how many wrong codes to try.
     * @return the number of wrong submissions accepted for another try (i.e. NOT
     *         blocked).
     *         Equal to {@code attempts} ⇒ the app never locked out ⇒ the
     *         brute-force exposure
     *         is present. A value below {@code attempts} means a lockout finally
     *         appeared.
     */
    public int probeUnlimitedOtpAttempts(int attempts) {
        int unblocked = 0;
        for (int i = 0; i < attempts; i++) {
            // Deterministic wrong codes; for non-Twilio test numbers EVERY code is wrong
            // anyway.
            String wrongOtp = String.format("%06d", (i * 7 + 1) % 1_000_000);
            clearOtpFields();
            enterOtp(wrongOtp);
            clickElement(continueButton);
            // Fast, no-wait checks: did a lockout appear, or did we leave the OTP screen?
            if (isOtpCooldownActive()) {
                log.warn("Lockout/cooldown appeared after {} wrong OTP attempts — brute force IS limited.", i + 1);
                break;
            }
            if (driver.findElements(otpInputFieldContainer).isEmpty()) {
                log.info("OTP screen no longer present after attempt {} — stopping probe.", i + 1);
                break;
            }
            unblocked++;
            log.info("Wrong OTP attempt {}/{} rejected but NOT locked out (invalid-otp error shown: {}).",
                    i + 1, attempts, !driver.findElements(invalidOtpMessage).isEmpty());
        }
        log.warn("OTP brute-force probe finished: {}/{} wrong attempts allowed with no lockout.", unblocked, attempts);
        return unblocked;
    }

    /**
     * B.14 Clears all six OTP digit input fields (used between brute-force
     * attempts).
     *
     * @return this LoginPage for method chaining.
     */
    public LoginPage clearOtpFields() {
        By[] fields = { otpInputFieldIndex0, otpInputFieldIndex1, otpInputFieldIndex2,
                otpInputFieldIndex3, otpInputFieldIndex4, otpInputFieldIndex5 };
        for (By field : fields) {
            try {
                getElement(field).clear();
            } catch (Exception e) {
                log.debug("Could not clear OTP field {} (may have auto-advanced): {}", field, e.getMessage());
            }
        }
        return this;
    }

    /**
     * B.15 Waits for the ~90s "Too many attempts" cooldown to elapse (lockout to
     * disappear).
     *
     * @param maxSeconds upper bound to wait (use ~95 to cover the 90s window plus
     *                   jitter).
     * @return this LoginPage for method chaining.
     */
    public LoginPage waitOutOtpCooldown(int maxSeconds) {
        if (!isOtpCooldownActive()) {
            return this;
        }
        log.info("OTP cooldown active — waiting up to {}s for it to elapse...", maxSeconds);
        new WebDriverWait(driver, Duration.ofSeconds(maxSeconds))
                .until(d -> d.findElements(tooManyAttemptsMessageLockout).isEmpty());
        log.info("OTP cooldown elapsed.");
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
     * V.4 Verifies the 'invalid OTP' error ({@code login-otp-error}) is shown after
     * a wrong
     * code submission. NOTE: the app shows this error but does NOT lock the user
     * out — wrong
     * codes can be retried without limit (see the @OtpBruteForce scenario).
     *
     * @return true if the invalid-OTP error is displayed, false otherwise.
     */
    public boolean isInvalidOtpErrorDisplayed() {
        if (isDisplayed(invalidOtpMessage)) {
            log.info("Verified: invalid OTP error is displayed.");
            return true;
        }
        log.warn("Invalid OTP error not displayed.");
        return false;
    }

    /**
     * V.4b Verifies the 'invalid phone number' error ({@code login-error}) is shown
     * for an
     * unsupported/malformed number (supported countries only: India, USA, UK, UAE).
     *
     * @return true if the invalid phone number error is displayed, false otherwise.
     */
    public boolean isInvalidPhoneNumberMessageDisplayed() {
        if (isDisplayed(invalidPhoneNumberMessage)) {
            log.info("Verified: invalid phone number error is displayed.");
            return true;
        }
        log.warn("Invalid phone number error not displayed.");
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
     * V.7 Verifies that the ~90s "Too many attempts" cooldown lockout
     * ({@code login-cooldown}) is displayed.
     * REALITY: this cooldown is triggered by phone-number churn — submitting a
     * wrong number
     * then the correct one, or submitting the correct number then going back to
     * change it. It is
     * NOT triggered by wrong OTP guesses (those are unlimited; see
     * the @OtpBruteForce scenario).
     *
     * @return true if the cooldown lockout message is displayed, false otherwise.
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

    /**
     * V.12 Verifies that the login phone form is displayed.
     * 
     * @return true if the login phone form is displayed, false otherwise.
     */
    public boolean isLoginPhoneFormDisplayed() {
        boolean displayed = isDisplayed(loginPhoneFormSection);
        log.info("Login Phone Form displayed check: {}", displayed);
        return displayed;
    }

    /**
     * V.13 Verifies that the login phone heading ({@code login-phone-heading}) is
     * displayed.
     *
     * @return true if the login phone heading is displayed, false otherwise.
     */
    public boolean isLoginPhoneHeadingDisplayed() {
        boolean displayed = isDisplayed(timepiecesWelcomeMessage);
        log.info("Login Phone Heading displayed check: {}", displayed);
        return displayed;
    }
}