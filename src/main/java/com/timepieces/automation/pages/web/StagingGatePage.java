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
 *   - staging.password                  (required when the gate is present)
 *   - staging.gate.passwordField        (defaults to input[type='password'])
 *   - staging.gate.submitButton         (defaults to button[type='submit'])
 *   - staging.gate.detection.timeout    (defaults to 4 seconds; should be long enough to allow the gate to appear but short enough to fail fast if it's not there)
 *
 * Detection is intentionally fast and non-fatal: unlockIfPresent() probes for
 * the password input with a short wait; if absent (cookie already set, or env
 * has no gate at all) it returns false and the caller continues normally.
 */
@Log4j2
public class StagingGatePage extends BasePage {

    private final By passwordField;
    private final By submitButton;
    private final int detectionTimeoutSeconds;

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

    /**
     * If the gate is on the current page, fill the password and submit.
     * Returns true if the gate was detected and unlocked, false if no gate
     * was present (so the caller can decide whether to re-navigate).
     */
    public boolean unlockIfPresent() {
        WebElement input;
        try {
            input = new WebDriverWait(driver, Duration.ofSeconds(detectionTimeoutSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        } catch (TimeoutException | NoSuchElementException e) {
            log.debug("No staging gate password field detected within {}s — assuming gate is absent or already cleared.",
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
            // Wait until the password field is no longer visible — proxy for "gate dismissed".
            waitForInvisibility(passwordField);
            log.info("Staging gate unlocked.");
            return true;
        } catch (WebDriverException e) {
            log.error("Failed to submit staging gate password.", e);
            throw e;
        }
    }
}
