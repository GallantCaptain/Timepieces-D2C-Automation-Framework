package com.timepieces.automation.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * PdpRegisterInterestModalComponent — the "Register your interest" form modal
 * shared by every timepiece PDP.
 *
 * <p>
 * Every timepiece PDP's primary "Register Interest" CTA opens the SAME modal form
 * (Full name, Email, Phone with a country toggle, an agree checkbox, and a
 * "Register interest" submit). Because it is identical across all timepiece PDPs,
 * it is mapped ONCE here as a Component Object (like {@code HeaderComponent} /
 * {@code PdpOwnersModalComponent}) and referenced by each family template rather
 * than duplicated per family.
 *
 * <p>
 * LOCATOR SOURCE: mapped from the 2026-07-15 live PDP screenshots. No element
 * carries a {@code data-testid}, so every locator is a best-effort fallback
 * (placeholder / text-anchor) tagged {@code // ROLLOUT-GAP · VERIFY} — these
 * should be confirmed by a targeted scrape that actually opens the form (the
 * chunk-1E scrape captured the CTA but not the form internals).
 *
 * <p>
 * SUBMISSION WARNING: {@link #submit()} registers REAL interest against the
 * backend (name/email/phone). Automated tests should exercise field entry and
 * validation only and must NOT submit against production.
 *
 * FLUENT INTERFACE: methods that stay in the modal return {@code this}.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of PdpRegisterInterestModalComponent"
* @since 2026-07-15
 */

@Log4j2
public class PdpRegisterInterestModalComponent extends BasePage<PdpRegisterInterestModalComponent> {

    // ==========================================
    // Constructor
    // ==========================================
    public PdpRegisterInterestModalComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Trigger = the sticky "Register Interest" CTA on the PDP (title-case).
    private final By ctaTrigger = By.xpath(
            "//div[contains(@class,'desktop-sticky-sidebar')]//button[contains(normalize-space(),'Register Interest')]"); // ROLLOUT-GAP: text-anchor
    // Modal heading confirms the form opened.
    private final By modalHeading = By.xpath("//*[normalize-space()='Register your interest']"); // ROLLOUT-GAP · VERIFY: text-anchor
    private final By fullNameInput = By.cssSelector("input[placeholder='Enter full name']"); // ROLLOUT-GAP · VERIFY: placeholder
    private final By emailInput = By.cssSelector("input[placeholder='Enter email address']"); // ROLLOUT-GAP · VERIFY: placeholder
    private final By phoneInput = By.cssSelector("input[placeholder='Enter phone number']"); // ROLLOUT-GAP · VERIFY: placeholder
    private final By phoneCountryToggle = By.xpath(
            "//*[normalize-space()='Register your interest']/following::button[contains(normalize-space(),'+')][1]"); // ROLLOUT-GAP · VERIFY: structural
    private final By agreeCheckbox = By.xpath(
            "//*[normalize-space()='Register your interest']/following::input[@type='checkbox'][1]"); // ROLLOUT-GAP · VERIFY: structural
    // Submit is sentence-case ("Register interest") — distinct from the title-case CTA.
    private final By submitButton = By.xpath(
            "//button[normalize-space()='Register interest']"); // ROLLOUT-GAP · VERIFY: text-anchor
    private final By closeButton = By.cssSelector("button[aria-label='Close']"); // ROLLOUT-GAP: aria

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Opens the register-interest form by clicking the PDP's "Register
     * Interest" CTA, then waits for the form heading to confirm it is interactive.
     *
     * @return this component for method chaining.
     */
    public PdpRegisterInterestModalComponent open() {
        log.info("Opening the register-interest form.");
        clickElement(ctaTrigger);
        waitForVisibility(modalHeading);
        return this;
    }

    /**
     * B.2 Enters the full name.
     *
     * @param fullName the full name to enter.
     * @return this component for method chaining.
     */
    public PdpRegisterInterestModalComponent enterFullName(String fullName) {
        sendKeys(fullNameInput, fullName);
        return this;
    }

    /**
     * B.3 Enters the email address.
     *
     * @param email the email address to enter.
     * @return this component for method chaining.
     */
    public PdpRegisterInterestModalComponent enterEmail(String email) {
        sendKeys(emailInput, email);
        return this;
    }

    /**
     * B.4 Enters the phone number (country code is set via the country toggle).
     *
     * @param phone the phone number digits.
     * @return this component for method chaining.
     */
    public PdpRegisterInterestModalComponent enterPhone(String phone) {
        sendKeys(phoneInput, phone);
        return this;
    }

    /**
     * B.5 Opens the phone country-code selector.
     *
     * @return this component for method chaining.
     */
    public PdpRegisterInterestModalComponent openCountrySelector() {
        clickElement(phoneCountryToggle);
        return this;
    }

    /**
     * B.6 Ticks the "I agree to receive communication…" consent checkbox.
     *
     * @return this component for method chaining.
     */
    public PdpRegisterInterestModalComponent acceptConsent() {
        clickElement(agreeCheckbox);
        return this;
    }

    /**
     * B.7 Convenience: fills every text field of the form in one call. Does NOT
     * submit — see the class-level submission warning.
     *
     * @param fullName full name.
     * @param email    email address.
     * @param phone    phone number.
     * @return this component for method chaining.
     */
    public PdpRegisterInterestModalComponent fillForm(String fullName, String email, String phone) {
        log.info("Filling the register-interest form (no submit).");
        enterFullName(fullName);
        enterEmail(email);
        enterPhone(phone);
        return this;
    }

    /**
     * B.8 Submits the register-interest form.
     * <p>
     * WARNING: this registers REAL interest against the backend. Do NOT call
     * against production in automated runs — see the class-level warning.
     *
     * @return this component for method chaining.
     */
    public PdpRegisterInterestModalComponent submit() {
        log.warn("Submitting the register-interest form — this registers REAL interest.");
        clickElement(submitButton);
        return this;
    }

    /**
     * B.9 Closes the register-interest modal and waits for it to disappear.
     *
     * @return this component for method chaining.
     */
    public PdpRegisterInterestModalComponent close() {
        log.info("Closing the register-interest modal.");
        clickElement(closeButton);
        waitForInvisibility(modalHeading);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the register-interest form modal is open (heading visible).
     *
     * @return true if the form is open.
     */
    public boolean isModalDisplayed() {
        return isDisplayed(modalHeading);
    }

    /**
     * V.2 Verifies the core form fields (name, email, phone) are displayed.
     *
     * @return true if the form fields are displayed.
     */
    public boolean isFormDisplayed() {
        return isDisplayed(fullNameInput) && isDisplayed(emailInput) && isDisplayed(phoneInput);
    }

    /**
     * V.3 Verifies the "Register interest" submit button is displayed.
     *
     * @return true if the submit button is displayed.
     */
    public boolean isSubmitButtonDisplayed() {
        return isDisplayed(submitButton);
    }
}
