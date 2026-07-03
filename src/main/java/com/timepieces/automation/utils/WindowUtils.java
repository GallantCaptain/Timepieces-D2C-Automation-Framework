package com.timepieces.automation.utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.extern.log4j.Log4j2;

/**
 * WindowUtils - Frame switching, window/tab management, and alert handling.
 *
 * Scope Decisions for the Timepieces application:
 * NextJS modals (MUI Dialog, Headless UI, Radix, custom) are DOM portals
 * rendered via createPortal() into <body>. They live in the SAME browsing
 * context as the host page. You do NOT need switchTo() for these — use
 * BrowserUtils.waitForVisibility() and BrowserUtils.waitForClickability().
 *
 * Tailwind overlays, drawers, and sidebars are plain DOM elements.
 * Same rule applies — no switchTo() needed.
 *
 * Where switchTo() Is actually required in Timepieces:
 * - Embedded 3D model viewers (Sketchfab, custom WebGL) loaded in <iframe>
 * - OAuth / SSO login popups opening in new windows
 * - PDF preview iframes
 * - Native browser alert() / confirm() / prompt() dialogs
 * - Any third-party widget (chat, payment) loaded in a sandboxed iframe
 *
 * NOT IN SCOPE (see sibling classes):
 * - Page load / element waits → BrowserUtils.java
 * - JS interactions / scrolling → BrowserUtils.java
 * - Hover / drag / keyboard → ActionUtils.java
 * 
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of WindowUtils"
* @since 2026-07-03
 */

@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public class WindowUtils {

    // ==========================================
    // 1. FRAME / IFRAME SWITCHING
    // ==========================================
    /**
     * 1.1 Switches WebDriver context into an iframe identified by index.
     * Use when iframes lack a name or id attribute (e.g., anonymous third-party
     * widgets, auto-generated payment iframes). Index is zero-based and ordered
     * by DOM appearance.
     *
     * @param driver     The WebDriver instance.
     * @param frameIndex Zero-based index of the iframe in the current DOM.
     */
    public static void switchToFrame(WebDriver driver, int frameIndex) {
        log.debug("Switching to frame at index: {}", frameIndex);
        try {
            driver.switchTo().frame(frameIndex);
            log.info("Successfully switched to frame at index: {}", frameIndex);
        } catch (NoSuchFrameException e) {
            log.error("No frame found at index {}: {}", frameIndex, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 1.2 Switches WebDriver context into an iframe identified by its name or id
     * attribute.
     * Prefer this over index-based switching — it is resistant to DOM reordering
     * and is more readable in test code.
     *
     * @param driver   The WebDriver instance.
     * @param nameOrId The name or id attribute value of the target iframe.
     */
    public static void switchToFrameByNameOrId(WebDriver driver, String nameOrId) {
        log.debug("Switching to frame by name/id: {}", nameOrId);
        try {
            driver.switchTo().frame(nameOrId);
            log.info("Successfully switched to frame with name/id: {}", nameOrId);
        } catch (NoSuchFrameException e) {
            log.error("No frame found with name/id '{}': {}", nameOrId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 1.3 Switches WebDriver context into an iframe represented as a WebElement.
     * This is the most robust approach: PageFactory locates the iframe element
     * via @FindBy, so the switch is tied to the same locator strategy as the
     * rest of the page object. Use this for all known Timepieces iframes.
     *
     * @param driver        The WebDriver instance.
     * @param iframeElement The WebElement representing the <iframe> tag.
     */
    public static void switchToFrameByElement(WebDriver driver, WebElement iFrameElement) {
        log.debug("Switching to frame by WebElement: {}", iFrameElement);
        try {
            driver.switchTo().frame(iFrameElement);
            log.info("Successfully switched to frame using WebElement: {}", iFrameElement);
        } catch (NoSuchFrameException e) {
            log.error("No frame found for WebElement {}: {}", iFrameElement, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 1.4 Waits for an iframe to be available and then switches into it.
     * Use when the iframe is injected into the DOM after initial page load
     * (e.g., a lazily loaded 3D viewer, a chat widget, or a payment form
     * that appears after clicking a button).
     *
     * @param driver           The WebDriver instance.
     * @param iframeElement    The WebElement representing the <iframe> tag.
     * @param timeoutInSeconds Maximum time to wait for the frame to be available.
     */
    public static void waitAndSwitchToFrame(WebDriver driver, WebElement iFrameElement, int timeoutInSeconds) {
        log.debug("Waiting for frame to be available and switching: {}", iFrameElement);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                    .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iFrameElement));
            log.info("Successfully switched to frame after waiting: {}", iFrameElement);
        } catch (TimeoutException e) {
            log.error("Timed out after {} seconds waiting for frame {}: {}", timeoutInSeconds, iFrameElement,
                    e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 1.5 Returns the WebDriver context to the main document from within any frame.
     * ALWAYS call this after you have finished interacting inside an iframe.
     * Forgetting to switch back is one of the most common causes of
     * NoSuchElementException in multi-frame test flows.
     *
     * @param driver The WebDriver instance.
     */
    public static void switchToDefaultContent(WebDriver driver) {
        log.debug("Switching back to default content (main document)");
        driver.switchTo().defaultContent();
        log.info("Successfully switched back to default content");
    }

    /**
     * 1.6 Steps one level up from a nested iframe to its parent frame.
     * Use when iframes are nested (parent iframe contains a child iframe).
     * Distinct from switchToDefaultContent() — this goes up one level,
     * not all the way back to the top-level document.
     *
     * @param driver The WebDriver instance.
     */
    public static void switchToParentFrame(WebDriver driver) {
        log.debug("Switching to parent frame");
        driver.switchTo().parentFrame();
        log.info("Successfully switched to parent frame");
    }

    /**
     * 1.7 Retrieves the current frame's name or id attribute for verification.
     * Use for debugging or assertions to confirm you are in the expected frame.
     *
     * @param driver The WebDriver instance.
     * @return The name or id attribute of the current frame.
     */
    public static String getCurrentFrameNameOrId(WebDriver driver) {
        log.debug("Getting current frame name/id");
        String frameNameOrId = driver.switchTo().activeElement().getAttribute("name");
        if (frameNameOrId == null || frameNameOrId.isEmpty()) {
            frameNameOrId = driver.switchTo().activeElement().getAttribute("id");
        }
        log.info("Current frame name/id: {}", frameNameOrId);
        return frameNameOrId;
    }

    // ==========================================
    // 2. WINDOW / TAB MANAGEMENT
    // ==========================================
    /**
     * 2.1 Switches WebDriver context to a window or tab identified by its title.
     * Use when a new window/tab opens with a unique title (e.g., an OAuth
     * login popup, a PDF preview, or an external link). This is more reliable
     * than index-based switching since the order of windows can change.
     *
     * @param driver      The WebDriver instance.
     * @param windowTitle The exact title of the target window/tab.
     */
    public static String switchToWindowByTitle(WebDriver driver, String windowTitle) {
        log.debug("Switching to window by title: {}", windowTitle);
        Set<String> windowHandles = driver.getWindowHandles();
        for (String handle : windowHandles) {
            driver.switchTo().window(handle);
            if (driver.getTitle().equals(windowTitle)) {
                log.info("Successfully switched to window with title: {}", windowTitle);
                return handle;
            }
        }
        log.error("No window found with title '{}': {}", windowTitle);
        throw new NoSuchFrameException("No window found with title: " + windowTitle);
    }

    /**
     * 2.2 Switches WebDriver context to a window or tab identified by its URL.
     * Use when the new window/tab has a unique URL (e.g., an external link,
     * a payment gateway, or a third-party widget). This is an alternative to
     * title-based switching when titles are dynamic or non-unique.
     *
     * @param driver      The WebDriver instance.
     * @param urlFragment A unique substring of the target window's URL.
     */
    public static String switchToWindowByUrlFragment(WebDriver driver, String urlFragment) {
        log.debug("Switching to window by URL fragment: {}", urlFragment);
        Set<String> windowHandles = driver.getWindowHandles();
        for (String handle : windowHandles) {
            driver.switchTo().window(handle);
            if (driver.getCurrentUrl().contains(urlFragment)) {
                log.info("Successfully switched to window with URL containing: {}", urlFragment);
                return handle;
            }
        }
        log.error("No window found with URL containing '{}': {}", urlFragment);
        throw new NoSuchFrameException("No window found with URL containing: " + urlFragment);
    }

    /**
     * 2.3 Switches WebDriver context to a window or tab identified by its index.
     * Use when multiple windows/tabs are open and you want to switch based on
     * the order they were opened (e.g., the second window that opens after a
     * button click). Index is zero-based and ordered by the sequence of
     * window handles returned by getWindowHandles().
     *
     * @param driver      The WebDriver instance.
     * @param windowIndex Zero-based index of the target window/tab.
     */
    public static void switchToWindowByIndex(WebDriver driver, int windowIndex) {
        log.debug("Switching to window by index: {}", windowIndex);
        List<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
        if (windowIndex < 0 || windowIndex >= windowHandles.size()) {
            log.error("Invalid window index {}: only {} windows available", windowIndex, windowHandles.size());
            throw new NoSuchFrameException("Invalid window index: " + windowIndex);
        }
        driver.switchTo().window(windowHandles.get(windowIndex));
        log.info("Successfully switched to window at index: {}", windowIndex);
    }

    /**
     * 2.4 Closes the current window or tab and switches back to the original one.
     * Use when you have finished interacting with a popup window/tab and want to
     * return to the main application context. Always call this after switching to
     * a new window/tab to avoid leaving orphaned windows open.
     *
     * @param driver The WebDriver instance.
     */
    public static void closeCurrentWindowAndSwitchBack(WebDriver driver) {
        log.debug("Closing current window and switching back to original");
        String originalHandle = driver.getWindowHandle();
        driver.close();
        Set<String> remainingHandles = driver.getWindowHandles();
        if (remainingHandles.contains(originalHandle)) {
            driver.switchTo().window(originalHandle);
            log.info("Successfully closed current window and switched back to original");
        } else if (!remainingHandles.isEmpty()) {
            driver.switchTo().window(remainingHandles.iterator().next());
            log.warn("Original window handle not found; switched to another remaining window");
        } else {
            log.warn("No remaining windows after closing current one");
        }
    }

    /**
     * 2.5 Returns the window handle of the currently focused browser window.
     * Capture this before triggering any action that opens a new tab or window
     * (e.g., clicking an external link, a share button, or an OAuth login popup)
     * so you can return to it later via switchToWindow().
     *
     * @param driver The WebDriver instance.
     * @return The window handle String of the current window.
     */
    public static String getCurrentWindowHandle(WebDriver driver) {
        log.debug("Getting current window handle");
        String currentHandle = driver.getWindowHandle();
        log.info("Current window handle: {}", currentHandle);
        return currentHandle;
    }

    /**
     * 2.6 Switches WebDriver focus to a window identified by its handle.
     * Pair this with getCurrentWindowHandle() — store the original handle
     * before the action, switch to new window for assertions, then switch
     * back using the stored handle.
     *
     * @param driver       The WebDriver instance.
     * @param windowHandle The handle String of the window to switch to.
     */
    public static void switchToWindow(WebDriver driver, String windowHandle) {
        log.debug("Switch to window handle: {}", windowHandle);
        try {
            driver.switchTo().window(windowHandle);
            log.info("Successfully switched to window with handle: {}", windowHandle);
        } catch (NoSuchFrameException e) {
            log.error("No window found with handle '{}': {}", windowHandle, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 2.7 Waits for a new window or tab to open and then switches into it.
     * Use after clicking any element that opens a new browser context
     * (e.g., target="_blank" links, OAuth / SSO login popups, share windows).
     * Returns the handle of the newly opened window so the caller can
     * switch back to the original window after test assertions.
     *
     * @param driver           The WebDriver instance.
     * @param originalHandle   The handle of the window that was active before.
     * @param timeoutInSeconds Maximum time to wait for the new window to appear.
     * @return The window handle of the newly opened window.
     */
    public static String waitAndSwitchToNewWindow(WebDriver driver, String originalHandle, int timeoutInSeconds) {
        log.debug("Waiting for new window and switching: {}", originalHandle);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                    .until(d -> d.getWindowHandles().size() > 1);
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(originalHandle)) {
                    driver.switchTo().window(handle);
                    log.info("Successfully switched to new window: {}", handle);
                    return handle;
                }
            }
        } catch (TimeoutException e) {
            log.error("Timed out after {} seconds waiting for new window: {}", timeoutInSeconds, originalHandle,
                    e.getMessage(), e);
            throw e;
        }
        throw new IllegalStateException("New window did not appear within timeout");
    }

    /**
     * 2.8 Closes the currently focused window and switches focus back to the
     * specified window handle. Use to clean up a popup or new tab after
     * assertions are done and resume the main test flow.
     *
     * @param driver         The WebDriver instance.
     * @param returnToHandle The handle of the window to return focus to after
     *                       closing.
     */
    public static void closeCurrentWindowAndReturn(WebDriver driver, String returnToHandle) {
        log.debug("Closing current window and returning to handle: {}", returnToHandle);
        driver.close();
        switchToWindow(driver, returnToHandle);
        log.info("Successfully closed current window and returned to handle: {}", returnToHandle);
    }

    /**
     * 2.9 Returns all currently open window handles as an ordered List.
     * Ordered by window open sequence (index 0 = first opened). Useful for
     * asserting the number of open tabs or iterating over all open windows.
     *
     * @param driver The WebDriver instance.
     * @return Ordered list of all open window handle Strings.
     */
    public static List<String> getAllWindowHandles(WebDriver driver) {
        log.debug("Getting all window handles");
        List<String> handles = new ArrayList<>(driver.getWindowHandles());
        log.info("Current open window handles: {}", handles);
        return handles;
    }

    // ==========================================
    // 3. ALERT / CONFIRM / PROMPT HANDLING
    // ==========================================
    /**
     * 3.1 Waits for a native browser alert to appear and accepts it (clicks OK).
     * Native alert() dialogs are outside the DOM — only switchTo().alert()
     * can interact with them. These are rarely used in NextJS apps but can
     * appear in legacy components, error handlers, or unsaved-changes warnings.
     *
     * @param driver           The WebDriver instance.
     * @param timeoutInSeconds Maximum time to wait for the alert.
     */
    public static void acceptAlert(WebDriver driver, int timeoutInSeconds) {
        log.debug("Waiting for alert to appear before accepting it.");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                    .until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept();
            log.info("Alert accepted.");
        } catch (TimeoutException e) {
            log.error("Timed out after {} seconds waiting for alert.", timeoutInSeconds, e);
            throw e;
        }
    }

    /**
     * 3.2 Waits for a native browser confirm dialog to appear and dismisses it
     * (clicks Cancel). Use to test Cancel / negative paths in confirm dialogs
     * (e.g., "Are you sure you want to delete this item?").
     *
     * @param driver           The WebDriver instance.
     * @param timeoutInSeconds Maximum time to wait for the alert.
     */
    public static void dismissAlert(WebDriver driver, int timeoutInSeconds) {
        log.debug("Waiting for the alert to appear before dismissing it.");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                    .until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.dismiss();
            log.info("Alert dismissed.");
        } catch (TimeoutException e) {
            log.error("Timed out after {} seconds waiting for alert.", timeoutInSeconds, e);
            throw e;
        }
    }

    /**
     * 3.3 Retrieves the text displayed in the current native browser alert.
     * Capture the alert text before accepting or dismissing it for use in
     * assertions (e.g., verifying the correct warning message is shown).
     *
     * @param driver           The WebDriver instance.
     * @param timeoutInSeconds Maximum time to wait for the alert.
     * @return The text content of the alert dialog.
     */
    public static String getAlertText(WebDriver driver, int timeoutInSeconds) {
        log.debug("Retrieving alert text after waiting for it to appear.");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                    .until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            log.info("Alert text: {}", alertText);
            return alertText;
        } catch (TimeoutException e) {
            log.error("Timed out after {} seconds waiting for alert.", timeoutInSeconds, e);
            throw e;
        }
    }

    /**
     * 3.4 Types text into a native browser prompt() dialog and accepts it.
     * prompt() dialogs are exceedingly rare in modern NextJS apps but may
     * appear in admin tools or legacy integration pages in the Timepieces platform.
     *
     * @param driver           The WebDriver instance.
     * @param text             The text to type into the prompt input field.
     * @param timeoutInSeconds Maximum time to wait for the prompt.
     */
    public static void sendTextToAlert(WebDriver driver, String text, int timeoutInSeconds) {
        log.debug("Typing into the prompt dialog: '{}'", text);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                    .until(ExpectedConditions.alertIsPresent());
            Alert alertPrompt = driver.switchTo().alert();
            alertPrompt.sendKeys(text);
            alertPrompt.accept();
            log.info("Text sent to prompt and accepted.");
        } catch (TimeoutException e) {
            log.error("Timed out after {} seconds waiting for prompt.", timeoutInSeconds, e);
            throw e;
        }
    }

    /**
     * 3.5 Checks whether a native browser alert is currently present on the page.
     * Use this as a guard before calling any other alert method when the presence
     * of the alert is not guaranteed (e.g., conditionally shown error dialogs).
     *
     * @param driver The WebDriver instance.
     * @return true if an alert is present, false otherwise.
     */
    public static boolean isAlertPresent(WebDriver driver, int timeoutInSeconds) {
        log.debug("Checking for presence of alert.");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                    .until(ExpectedConditions.alertIsPresent());
            log.info("Alert is present.");
            return true;
        } catch (TimeoutException e) {
            log.info("No alert present after waiting for {} seconds.", timeoutInSeconds);
            return false;
        }
    }

    /**
     * 3.6 Waits for a native browser alert to appear, retrieves its text, and then
     * accepts it.
     * Use this when you want to assert the content of an alert and then close it in
     * one step (e.g., verifying an error message in an alert dialog).
     * 
     * @param driver           The WebDriver instance.
     * @param timeoutInSeconds Maximum time to wait for the alert.
     */
    public static String acceptAndRetrieveAlertText(WebDriver driver, int timeoutInSeconds) {
        log.debug("Waiting for alert, retrieving text, and accepting it.");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds))
                    .until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            alert.accept();
            log.info("Alert accepted. Text was: {}", alertText);
            return alertText;
        } catch (TimeoutException e) {
            log.error("Timed out after {} seconds waiting for alert.", timeoutInSeconds, e);
            throw e;
        }
    }
}
