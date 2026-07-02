package com.timepieces.automation.utils;

//import com.timepieces.automation.base.DriverFactory;

//import java.time.Duration;
import org.openqa.selenium.TimeoutException;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.extern.log4j.Log4j2;

/**
 * BrowserUtils class provides utility methods for performing complex user
 * interactions using Selenium WebDriver's Advanced Synchronization 'Fluent
 * Waits',
 * complex browser related actions like switching between windows/tabs, iframes,
 * etc.
 * Also there are methods using Javascript Executor or Direct WebDriver methods
 * to perform actions,
 * as well as Screenshot Utility methods to capture screenshots of the browser
 * at different stages of the test execution.
 * All the methods here are stateless, and receive a driver instance explicitly
 * for thread safety in parallel runs.
 * 
 * FLUENT INTERFACE:
 * All methods return {@code Object} to allow method
 * chaining in step definitions, as this is used to perform actions on
 * browser windows, iframes, tabs, etc. when chaining is desired
 * in other cases just return void (example screenshot methods).
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class BrowserUtils {

    // ==========================================
    // 1. ADVANCED SYNCHRONIZATION UTILITIES
    // ==========================================
    /**
     * 1.1 Dynamically pauses execution until the page's document.readyState is
     * 'complete', ensuring that all resources have finished loading before
     * proceeding.
     * This prevents timing flakiness in tests by synchronizing actions with the
     * actual state of the page, rather than relying on fixed wait times.
     * 
     * @param driver           The WebDriver instance to use for executing
     *                         JavaScript.
     * @param timeoutInSeconds The maximum time to wait for the page to load, in
     *                         seconds.
     * @return true if the page loads within the timeout, false otherwise.
     */
    public static boolean waitForPageLoad(WebDriver driver, int timeoutInSeconds) {
        log.debug("Verifying full DOM initialization via document.readyState state sync.");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                    .until(webDriver -> "complete".equals(((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState")));
            log.info("Page has fully loaded with document.readyState: complete");
            return true;
        } catch (Exception e) {
            log.error("Timed out after {} seconds waiting for page to load with document.readyState: complete",
                    timeoutInSeconds, e);
            return false;
        }
    }

    /**
     * 1.2 Dynamically pauses execution until the element is visible, ensuring that
     * the element is present and interactable before proceeding.
     * This prevents timing flakiness in tests by synchronizing actions with the
     * actual state of the element, rather than relying on fixed wait times.
     * 
     * @param timeoutInSeconds The maximum time to wait for the element to be
     *                         visible, in seconds.
     * @param driver           The WebDriver instance to use for executing
     *                         JavaScript.
     * @param element          The WebElement to wait for visibility.
     * @return true if the element becomes visible within the timeout, false
     *         otherwise.
     */
    public static boolean waitForVisibility(WebDriver driver, WebElement element, int timeoutInSeconds) {
        log.debug("Waiting for element visibility: {}", element);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                    .until(ExpectedConditions.visibilityOf(element));
            log.info("Element is now visible: {}", element);
            return true;
        } catch (TimeoutException e) {
            log.error("Timed out after {} seconds waiting for element to become visible: {}", timeoutInSeconds, element,
                    e);
            return false;
        }
    }

    /**
     * 1.3 Dynamically wait until the element is visible and enabled, ensuring that
     * the element is present and interactable before proceeding.
     * This prevents timing flakiness in tests by synchronizing actions with the
     * actual state of the element, rather than relying on fixed wait times.
     * Distinct from waitForVisibility — an element can be visible but disabled.
     * This method ensures the element is both visible and enabled, making it truly
     * interactable before proceeding.
     * 
     * @param timeoutInSeconds The maximum time to wait for the element to be
     *                         visible, in seconds.
     * @param driver           The WebDriver instance to use for executing
     *                         JavaScript.
     * @param element          The WebElement to wait for visibility.
     * @return true if the element becomes visible within the timeout, false
     *         otherwise.
     */
    public static boolean waitForClickability(WebDriver driver, WebElement element, int timeoutInSeconds) {
        log.debug("Waiting for element clickability: {}", element);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                    .until(ExpectedConditions.elementToBeClickable(element));
            log.info("Element is now clickable: {}", element);
            return true;
        } catch (TimeoutException e) {
            log.error("Timed out after {} seconds waiting for element to become clickable: {}", timeoutInSeconds,
                    element, e);
            return false;
        }
    }

    /**
     * 1.4 Waits until the element is no longer visible or present in the DOM,
     * ensuring that the element has been removed or hidden before proceeding.
     * This prevents timing flakiness in tests by synchronizing actions with the
     * actual state of the element, rather than relying on fixed wait times.
     * This is particularly useful for waiting for loading spinners, modals, or any
     * transient elements to disappear before interacting with the underlying page.
     * 
     * @param timeoutInSeconds The maximum time to wait for the element to become
     *                         invisible, in seconds.
     * @param driver           The WebDriver instance to use for executing
     *                         JavaScript.
     * @param element          The WebElement to wait for invisibility.
     * @return true if the element becomes invisible within the timeout, false
     *         otherwise.
     */
    public static boolean waitForInvisibility(WebDriver driver, WebElement element, int timeoutInSeconds) {
        log.debug("Waiting for element invisibility: {}", element);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                    .until(ExpectedConditions.invisibilityOf(element));
            log.info("Element is now invisible: {}", element);
            return true;
        } catch (TimeoutException e) {
            log.error("Timed out after {} seconds waiting for element to become invisible: {}", timeoutInSeconds,
                    element, e);
            return false;
        }
    }

    // ==========================================
    // 2. JAVASCRIPT INTERACTION UTILITIES
    // ==========================================
    /**
     * 2.1 Clicks an element via JavaScript, bypassing potential issues with
     * WebDriver's native click method (e.g.,
     * elements that are not in view or obscured by other elements).
     * Useful when an element is obscured by an overlay or not reachable by a
     * standard click.
     * 
     * @param driver  The WebDriver instance to use for executing JavaScript.
     * @param element The WebElement to click.
     */
    public static void clickElementViaJavaScript(WebDriver driver, WebElement element) {
        log.debug("Performing Javascript click on WebElement: {}", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        log.info("Clicked element via JavaScript: {}", element);
    }

    /**
     * 2.2 Scrolls the page to bring the specified element into view using
     * JavaScript, ensuring that the element is visible and interactable.
     * Scrolls the page until the given element is in the viewport. Prevents
     * ElementNotInteractableException caused by elements outside the visible area.
     * 
     * @param driver  The WebDriver instance to use for executing JavaScript.
     * @param element The WebElement to scroll into view.
     */
    public static void scrollToElement(WebDriver driver, WebElement element) {
        log.debug("Scrolling to WebElement: {}", element);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        log.info("Scrolled to element: {}", element);
    }

    /**
     * 2.3 Scrolls the page to the top using JavaScript, ensuring that the top of
     * the page is visible and interactable.
     * Scrolls the page to the top. Useful for resetting the scroll position before
     * performing actions that require the top of the page to be visible.
     * 
     * @param driver The WebDriver instance to use for executing JavaScript.
     */
    public static void scrollToTop(WebDriver driver) {
        log.debug("Scrolling to the top of the page.");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo({top: 0, behavior: 'smooth'});");
        log.info("Scrolled to the top of the page.");
    }

    /**
     * 2.4 Scrolls the page to the bottom using JavaScript, ensuring that the bottom
     * of the page is visible and interactable.
     * Scrolls the page to the bottom. Useful for loading dynamic content that
     * appears as you scroll or for interacting with elements located at the bottom
     * of the page.
     * 
     * @param driver The WebDriver instance to use for executing JavaScript.
     */
    public static void scrollToBottom(WebDriver driver) {
        log.debug("Scrolling to the bottom of the page");
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});");
        log.info("Scrolled to the bottom of the page.");
    }

    /**
     * 2.5 Sets the value of an input field directly via JavaScript.
     * Useful when sendKeys() is intercepted or blocked (e.g., date/time pickers).
     * 
     * @param driver  The WebDriver instance to use for executing JavaScript.
     * @param element The WebElement representing the input field.
     * @param value   The value to set.
     */
    public static void jsSetValue(WebDriver driver, WebElement element, String value) {
        log.debug("Setting value of WebElement via JavaScript: {} to '{}'", element, value);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", element, value);
        log.info("Set value of element via JavaScript: {} to '{}'", element, value);
    }

    /**
     * 2.6 Retrieves the value of an input field directly via JavaScript.
     * Useful when getText() does not return the expected value (e.g., for certain
     * input types).
     * 
     * @param driver  The WebDriver instance to use for executing JavaScript.
     * @param element The WebElement representing the input field.
     * @return The value of the input field as retrieved via JavaScript.
     */
    public static String jsGetValue(WebDriver driver, WebElement element) {
        log.debug("Getting value of WebElement via JavaScript: {}", element);
        String value = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].value;", element);
        log.info("Got value of element via JavaScript: {} is '{}'", element, value);
        return value;
    }

    /**
     * 2.7 Highlights an element by applying a temporary CSS style via JavaScript.
     * Useful for debugging purposes to visually identify elements during test
     * execution.
     * Applies a red border to the element for a short duration to make it stand out
     * on the page.
     * Intended for debugging and visual test reporting only; do not use in
     * production runs.
     * 
     * @param driver  The WebDriver instance to use for executing JavaScript.
     * @param element The WebElement to highlight.
     */
    public static void highlightElement(WebDriver driver, WebElement element) {
        log.debug("Highlighting WebElement: {}", element);
        String originalStyle = element.getAttribute("style");
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].setAttribute('style', arguments[1]);",
                element, "border: 2px solid red; background-color: yellow; border-style: dashed;");
        // Revert to original style after a short delay
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Highlight duration
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].setAttribute('style', arguments[1]);",
                        element, originalStyle);
                log.info("Reverted highlight for element: {}", element);
            } catch (InterruptedException e) {
                log.error("Error while reverting highlight for element: {}", element, e);
                Thread.currentThread().interrupt();
            }
        }).start();
        log.info("Highlighted element: {}", element);
    }

    // ==========================================
    // 3. BROWSER NAVIGATION UTILITIES
    // ==========================================
    /**
     * 3.1 Navigates to a specified URL and waits for the page to fully load using
     * the waitForPageLoad utility.
     * Combines navigation and synchronization into a single method for convenience
     * and reliability.
     * 
     * @param driver The WebDriver instance to use for navigating.
     * @param url    The URL to navigate to.
     */
    public static void navigateTo(WebDriver driver, String url, int timeoutInSeconds) {
        log.debug("Navigating to URL: {}", url);
        driver.get(url);
        waitForPageLoad(driver, timeoutInSeconds);
        log.info("Navigated to: {}", url);
    }

    /**
     * 3.2 Refreshes the current page and waits for it to fully load using the
     * waitForPageLoad utility.
     * Combines refresh and synchronization into a single method for convenience and
     * reliability.
     * 
     * @param driver           The WebDriver instance to use for refreshing.
     * @param timeoutInSeconds The maximum time to wait for the page to load.
     */
    public static void refreshPage(WebDriver driver, int timeoutInSeconds) {
        log.debug("Refreshing the current page.");
        driver.navigate().refresh();
        waitForPageLoad(driver, timeoutInSeconds);
        log.info("Page refreshed successfully.");
    }

    /**
     * 3.3 Navigates back in the browser history and waits for the page to fully
     * load using the waitForPageLoad utility.
     * Combines back navigation and synchronization into a single method for
     * convenience and reliability.
     * 
     * @param driver           The WebDriver instance to use for navigating back.
     * @param expectedUrl      The expected URL to assert against.
     * @param timeoutInSeconds The maximum time to wait for the page to load.
     */
    public static void navigateBack(WebDriver driver, String expectedUrl, int timeoutInSeconds) {
        log.debug("Navigating back in browser history.");
        driver.navigate().back();
        waitForPageLoad(driver, timeoutInSeconds);
        log.info("Navigated back successfully. Current URL: {}", driver.getCurrentUrl());
    }

    /**
     * 3.4 Navigates forward in the browser history and waits for the page to fully
     * load using the waitForPageLoad utility.
     * Combines forward navigation and synchronization into a single method for
     * convenience and reliability.
     * 
     * @param driver           The WebDriver instance to use for navigating forward.
     * @param timeoutInSeconds The maximum time to wait for the page to load.
     */
    public static void navigateForward(WebDriver driver, String expectedUrl, int timeoutInSeconds) {
        log.debug("Navigating forward in browser history.");
        driver.navigate().forward();
        waitForPageLoad(driver, timeoutInSeconds);
        log.info("Navigated forward successfully. Current URL: {}", driver.getCurrentUrl());
    }

    /**
     * 3.5 Returns the current URL of the page to assert against the expected URL.
     * Useful for assertions in tests to verify that navigation has occurred as
     * expected.
     * 
     * @param driver The WebDriver instance to use for retrieving the URL.
     * @return The current URL of the page.
     */
    public static String getExpectedCurrentUrl(String expectedUrl, WebDriver driver) {
        String currentUrl = driver.getCurrentUrl();
        try {
            log.debug("Current URL: {}", currentUrl);
            if (currentUrl.equals(expectedUrl)) {
                log.info("Current URL matches expected URL: {}", expectedUrl);
            } else {
                log.warn("Current URL does not match expected URL. Expected: {}, Actual: {}", expectedUrl, currentUrl);
            }
            return currentUrl;
        } catch (Exception e) {
            log.error("Error occurred while retrieving current URL.", e);
            return null;
        }
    }

    /**
     * 3.6 Only returns the URL of the currently loaded page.
     * 
     * @param driver The WebDriver instance.
     * @return The current URL as a String.
     */
    public static String getCurrentUrl(WebDriver driver) {
        String url = driver.getCurrentUrl();
        log.debug("Current URL: {}", url);
        return url;
    }

    /**
     * 3.7 Retrieves the page title to assert against the expected title.
     * Useful for assertions in tests to verify that the correct page has loaded.
     * 
     * @param driver The WebDriver instance.
     * @return The title of the current page.
     */
    public static String getPageTitle(WebDriver driver) {
        String title = driver.getTitle();
        log.debug("Current page title: {}", title);
        return title;
    }

    // ==========================================
    // 4. SCREENSHOT UTILITIES
    // ==========================================
    /**
     * 4.1 Captures a screenshot of the current browser window and saves it to a
     * specified directory with a timestamped filename.
     * Useful for debugging and reporting purposes to visually capture the state of
     * the application at specific points during test execution.
     * The file is named using the provided label and a timestamp to avoid
     * overwrites.
     * 
     * @param driver    The WebDriver instance to use for taking the screenshot.
     * @param label     A descriptive label to include in the screenshot filename
     *                  for easier identification.
     * @param directory The directory where the screenshot will be saved. If the
     *                  directory does not exist, it will be created.
     * @return The file path of the saved screenshot.
     */
    public static String takeScreenshot(WebDriver driver, String label, String directory) {
        try {
            // Ensure the directory exists
            Path dirPath = Paths.get(directory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                log.info("Created screenshot directory: {}", directory);
            }

            // Generate timestamped filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = String.format("%s_%s.png", label, timestamp);
            Path filePath = dirPath.resolve(filename);

            // Take screenshot and save to file
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), filePath);
            log.info("Screenshot saved: {}", filePath.toString());
            return filePath.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("Failed to take screenshot with label: {}", label, e);
            return null;
        }
    }

}