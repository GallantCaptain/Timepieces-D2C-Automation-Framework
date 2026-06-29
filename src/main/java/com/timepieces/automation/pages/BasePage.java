package com.timepieces.automation.pages;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.timepieces.automation.utils.ConfigReader;

import lombok.extern.log4j.Log4j2;

/**
 * BasePage — Abstract foundation for all Page Object Model (POM) classes.
 *
 * Every page class in the framework MUST extend this class.
 *
 * RESPONSIBILITIES: 1. Injects the WebDriver instance into the page so all page
 * objects use the same ThreadLocal-safe driver instance from DriverFactory. 2.
 * Initialises a shared WebDriverWait that all page methods inherit. Timeout is
 * read from config (explicit.wait.timeout), defaulting to 10s, so it can be
 * overridden per environment without touching code. 3. Runs
 * BY Plain Objects, e.g. private final By webElementName = By.....
 *
 * WHAT DOES NOT BELONG HERE: - Business logic (belongs in individual page
 * objects) - Assertions (belong in step definitions or test classes) - Driver
 * initialisation or teardown (belongs in DriverFactory + Hooks)
 */
@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    // ==========================================
    // CONSTRUCTORS
    // ==========================================
    /**
     * Constructs a BasePage, wiring up the driver, the explicit wait, and
     * PageFactory element proxies for the concrete subclass.
     *
     * @param driver The WebDriver instance managed by DriverFactory (ThreadLocal).
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;

        // Read explicit wait timeout from config; fall back to 10 seconds if unset.
        // This allows per-environment tuning via staging.properties / prod.properties.
        int timeoutSeconds = Optional.ofNullable(ConfigReader.get("explicit.wait.timeout"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .orElse(10);

        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

        log.debug("Initialised page object: [{}] with explicit wait timeout: [{}s]", this.getClass().getSimpleName(),
                timeoutSeconds);
    }

    // Missing #1: presence (not visibility) — needed for hidden iframes like
    // Cloudflare
    protected WebElement getElementPresence(By locator) {
        log.debug("Awaiting DOM presence (not visibility) for locator: [{}]", locator);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // Missing #2: pure "wait for visible" without returning the element —
    // used as a navigation confirmation guard (e.g. after clickSendOtpButton)
    protected void waitForVisibility(By locator) {
        log.debug("Awaiting visibility confirmation for locator: [{}]", locator);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // Missing #3 (optional but clean): wait for invisibility/disappearance —
    // you'll need this soon for loading spinners
    protected void waitForInvisibility(By locator) {
        log.debug("Awaiting invisibility/disappearance of locator: [{}]", locator);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Synchronizes and isolates element retrieval using explicit visibility
     * constraints. Guardrails agains lazy loading elements on heavy animated
     * matrices and ensures that interactions are performed on elements that are
     * present and visible in the DOM.
     *
     * @param locator The By locator used to identify the target element on the
     *                page.
     * @return The WebElement that matches the locator and is visible on the
     *         page.
     */
    protected WebElement getElement(By locator) {
        log.debug("Synchronizing element visibility tracking for locator target: [{}]", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Synchronizes and isolates multiple elements retrieval using explicit
     * visibility constraints. Retrieves a list of elements based on the
     * provided locator. Ensures that all matching elements are visible and
     * interactable before returning them. Useful for scenarios where multiple
     * elements share the same locator, such as lists or tables, and allows for
     * batch operations on those elements.
     *
     * @param locator The By locator used to identify the target elements on the
     *                page.
     * @return A List of WebElement objects that match the locator and are
     *         visible on the page.
     */
    protected List<WebElement> getElements(By locator) {
        log.debug("Synchronizing multiple element visibility tracking for locator target: [{}]", locator);
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    /**
     * Centralized click wrapper with proactive clickability verification.
     * Ensures that the element is not only visible but also interactable (i.e.,
     * enabled and not obscured) before performing the click action, reducing
     * the likelihood of ElementNotInteractableException and improving test
     * stability.
     *
     * @param locator The By locator used to identify the target element on the
     *                page.
     */
    protected void clickElement(By locator) {
        log.info("Executing safe click transaction on target element: [{}]", locator);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (Exception e) {
            log.error("Click transaction aborted. Element was un-interactable: [{}]", locator, e);
            throw e;
        }
    }

    /**
     * Centralized input wrapper. Flushes the existing text caches before
     * injecting new input values to ensure clean state and prevent input
     * anomalies.
     *
     * @param locator The By locator used to identify the target element on the
     *                page.
     * @param value   The text value to be injected into the target element.
     */
    protected void sendKeys(By locator, String value) {
        if (value == null) {
            log.warn("Skipping text injection for null value on target element: [{}]", locator);
            return;
        }
        log.info("Injecting text value into target element: [{}]: [****]", locator);
        try {
            WebElement element = getElement(locator);
            element.clear();
            element.sendKeys(value);
        } catch (Exception e) {
            log.error("Text injection transaction failed for target element: [{}]", locator, e);
            throw e;
        }
    }

    /**
     * Safely pulls inner text generator from validated DOM nodes. Guards
     * against stale element references and ensures that the text content is
     * accurately captured from the current state of the page.
     *
     * @param locator The By locator used to identify the target element on the
     *                page.
     * @return The text content of the target element.
     */
    protected String getText(By locator) {
        log.debug("Extracting text content from target element: [{}]", locator);
        try {
            return getElement(locator).getText().trim();
        } catch (Exception e) {
            log.error("Text extraction failed for target element: [{}]", locator, e);
            throw e;
        }
    }

    /**
     * Boolean Verification wrapper asserting active component status
     *
     * @param locator The By locator used to identify the target element on the
     *                page.
     * @return true if the element is displayed, false otherwise.
     */
    protected Boolean isDisplayed(By locator) {
        log.debug("Verifying display status of target element: [{}]", locator);
        try {
            return getElement(locator).isDisplayed();
        } catch (Exception e) {
            log.warn("Display verification failed for target element: [{}]. Element may not be present or visible.",
                    locator, e);
            return false;
        }
    }

    /**
     * Centralized method for retrieving the attribute value of a target
     * element.
     *
     * @param locator       The By locator used to identify the target element the
     *                      page.
     * @param attributeName The name of the attribute to retrieve.
     * @return The value of the specified attribute.
     */
    protected String getAttribute(By locator, String attributeName) {
        log.debug("Extracting attribute [{}] from target element: [{}]", attributeName, locator);
        try {
            return getElement(locator).getAttribute(attributeName);
        } catch (Exception e) {
            log.error("Attribute extraction failed for target element: [{}]", locator, e);
            throw e;
        }
    }

    /**
     * Provides clean structural support for dynamic locators (e.g.: selecting
     * collections dynamically) Accepts a locator template with placeholders and
     * a replacement value to generate a specific locator instance at runtime,
     * enabling flexible and reusable locator strategies for elements that share
     * common patterns but differ in specific identifiers (e.g., product names,
     * user IDs).
     *
     * @param xPathTemplate    The By locator template containing placeholders for
     *                         dynamic values.
     * @param replacementValue The value to replace the placeholders in the
     *                         locator template.
     * @return A By locator instance with the placeholders replaced by the
     *         specified value.
     */
    protected By formatLocator(By xPathTemplate, String replacementValue) {
        return By.xpath(String.format(xPathTemplate.toString().replace("By.xpath: ", ""), replacementValue));
    }

    /**
     * Checks if the current browser window is sized as a mobile viewport.
     * 
     * @return true if the window width is less than 768 pixels, false otherwise.
     */
    public boolean isMobileViewport() {
        try {
            int width = driver.manage().window().getSize().getWidth();
            log.debug("Current viewport width: {}px", width);
            return width < 768; // Tailwind md breakpoint
        } catch (Exception e) {
            log.error("Failed to determine viewport size: {}", e.getMessage());
            return false;
        }
    }
}
