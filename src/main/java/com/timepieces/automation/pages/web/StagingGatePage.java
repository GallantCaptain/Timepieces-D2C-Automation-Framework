package com.timepieces.automation.pages.web;

import java.time.Duration;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.timepieces.automation.pages.BasePage;
import com.timepieces.automation.utils.ConfigReader;

import lombok.extern.log4j.Log4j2;

/**
 * Handles the standalone password gate that fronts non-production environments.
 *
 * Selectors and the password are resolved through ConfigReader so the same code
 * works in staging today and can be repointed at another env-gated environment
 * later without touching this class:
 * - staging.password (required when the gate is present)
 * - staging.gate.passwordField (defaults to input[type='password'])
 * - staging.gate.submitButton (defaults to button[type='submit'])
 * - staging.gate.detection.timeout (defaults to 4 seconds; should be long
 * enough to allow the gate to appear but short enough to fail fast if it's not
 * there)
 *
 * Detection is intentionally fast and non-fatal: unlockIfPresent() probes for
 * the password input with a short wait; if absent (cookie already set, or env
 * has no gate at all) it returns false and the caller continues normally.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (StagingGatePage)
 * to allow method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of StagingGatePage"
* @since 2026-07-03
 */

@Log4j2
public class StagingGatePage extends BasePage<StagingGatePage> {

    private final By passwordField;
    private final By submitButton;
    private final int detectionTimeoutSeconds;

    // ==========================================
    // Constructor
    // ==========================================
    public StagingGatePage(WebDriver driver) {
        super(driver);
        this.passwordField = By.cssSelector(
                Optional.ofNullable(ConfigReader.get("staging.gate.passwordField"))
                        .orElse("input[type='password']"));
        this.submitButton = By.cssSelector(
                Optional.ofNullable(ConfigReader.get("staging.gate.submitButton"))
                        .orElse("button[type='submit']"));
        this.detectionTimeoutSeconds = Optional.ofNullable(ConfigReader.get("staging.gate.detection.timeout"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .orElse(4);
    }

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * P.1 If the gate is on the current page, fill the password and submit.
     * Returns true if the gate was detected and unlocked, false if no gate
     * was present (so the caller can decide whether to re-navigate).
     */
    public boolean unlockIfPresent() {
        WebElement input;
        try {
            input = new WebDriverWait(driver, Duration.ofSeconds(detectionTimeoutSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        } catch (TimeoutException | NoSuchElementException e) {
            log.debug(
                    "No staging gate password field detected within {}s — assuming gate is absent or already cleared.",
                    detectionTimeoutSeconds);
            return false;
        }

        String password = ConfigReader.get("staging.password");
        if (password == null || password.isEmpty()) {
            throw new IllegalStateException(
                    "Staging password gate detected but staging.password is unset. "
                            + "Set it in src/test/resources/config/staging.local.properties "
                            + "or pass -Dstaging.password=... on the command line.");
        }

        log.info("Staging gate detected. Submitting password to unlock.");
        try {
            input.clear();
            input.sendKeys(password);
            clickElement(submitButton);
            // Wait until the password field is no longer visible — proxy for "gate
            // dismissed".
            waitForInvisibility(passwordField);
            log.info("Staging gate unlocked.");
            return true;
        } catch (WebDriverException e) {
            log.error("Failed to submit staging gate password.", e);
            throw e;
        }
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Enters the password into the gate password field.
     *
     * @param password The gatekeeper password to enter.
     * @return this StagingGatePage for method chaining.
     */
    public StagingGatePage enterPassword(String password) {
        log.info("Entering staging gate password.");
        sendKeys(passwordField, password);
        return this;
    }

    /**
     * B.2 Clicks the submit button on the password gate.
     *
     * @return this StagingGatePage for method chaining.
     */
    public StagingGatePage clickSubmitButton() {
        log.info("Clicking staging gate submit button.");
        clickElement(submitButton);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies if the password wall has cleared successfully by checking for
     * the invisibility of the password field.
     *
     * @return true if cleared, false if still visible.
     */
    public boolean isPasswordWallCleared() {
        log.info("Verifying if staging gate is cleared.");
        try {
            waitForInvisibility(passwordField);
            return true;
        } catch (TimeoutException e) {
            log.warn("Staging gate password field is still visible.");
            return false;
        }
    }

    /**
     * V.2 Checks if the password gate field is present on the page.
     *
     * @return true if the password field is visible, false otherwise.
     */
    public boolean isPasswordGatePresent() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(detectionTimeoutSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(passwordField));
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }
}
