package com.timepieces.automation.utils;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import lombok.extern.log4j.Log4j2;

/**
 * ActionUtils class provides utility methods for performing complex user
 * interactions using Selenium WebDriver's Actions class.
 * It includes methods for hovering over elements, dragging and dropping, and
 * sending keys with modifiers
 * Scope: This class handles multistep and gesture based browser interactions,
 * that goes beyond a simple .click() or .sendKeys().
 * All the methods here are stateless, and receive a driver instance explicitly
 * for thread safety in parallel runs.
 */
@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public class ActionsUtils {
    // ==========================================
    // 1. MOUSE ACTIONS
    // ==========================================
    /**
     * 1.1 Moves the mouse pointer to the center of the specified element,
     * simulating a hover action.
     * Triggers CSS: hover effects, tooltips, and any JavaScript events bound to
     * mouseover, i.e., any content rendered conditionally on mouse-over.
     * Critical for Timepieces product cards, nav menus, and 3D viewer controls that
     * reveal overlaid action buttons only on hover.
     * 
     * @param driver  The WebDriver instance to perform the action on.
     * @param element The WebElement to hover over.
     */
    public static void hoverOver(WebDriver driver, WebElement element) {
        log.info("Hovering over element: {}", element);
        new Actions(driver).moveToElement(element).perform();
        log.info("Hovered over element: {}", element);
    }

    /**
     * 1.2 Drags an element from a source location and drops it onto a target
     * element.
     * Simulates the user action of clicking and holding the source element, moving
     * it to the target, and releasing it.
     * Useful for testing drag-and-drop interfaces, such as rearranging items in a
     * list or moving files into folders , or any drag-and-drop feature in the
     * Timepieces product configurator.
     * Note: Some complex drag-and-drop implementations (e.g., those using HTML5
     * drag-and-drop API) may require additional handling, such as using JavaScript
     * to trigger the events properly.
     * 
     * @param driver The WebDriver instance to perform the action on.
     * @param source The WebElement to be dragged.
     * @param target The WebElement where the source should be dropped.
     */
    public static void dragAndDrop(WebDriver driver, WebElement source, WebElement target) {
        log.info("Dragging element: {} to target: {}", source, target);
        new Actions(driver).dragAndDrop(source, target).perform();
        log.info("Dragged element: {} to target: {}", source, target);
    }

    /**
     * 1.3 Drags a source element by its center and drops it onto a target element.
     * This is an alternative to the standard dragAndDrop method, which can be more
     * reliable in certain scenarios where the default implementation may not
     * trigger the necessary events on the target element.
     * It is a compound action composed of clickAndHold, moveToElement, and release
     * actions to ensure the drag-and-drop interaction is performed as a user would
     * do it.
     * Use when the standard dragAndDrop method fails to work due to complex DOM
     * structures, custom drag-and-drop implementations, or when precise control
     * over the drag-and-drop sequence is required.
     * 
     * @param driver The WebDriver instance to perform the action on.
     * @param source The WebElement to be dragged.
     * @param target The WebElement where the source should be dropped.
     */
    public static void dragAndDropBy(WebDriver driver, WebElement source, WebElement target) {
        log.info("Dragging element: {} to target: {}", source, target);
        new Actions(driver).clickAndHold(source).moveToElement(target).release().perform();
        log.info("Dragged element: {} to target: {}", source, target);
    }

    /**
     * 1.4 Drags a source element by an offset and drops it at the new location.
     * Use when a drop target is defined by coordinates rather than an
     * element (e.g., positioning a part on a 3D canvas or design board).
     * 
     * @param driver  The WebDriver instance to perform the action on.
     * @param source  The WebElement to be dragged.
     * @param xOffset The horizontal offset in pixels.
     * @param yOffset The vertical offset in pixels.
     */
    public static void dragAndDropByOffset(WebDriver driver, WebElement source, int xOffset, int yOffset) {
        log.info("Dragging element: {} by offset: ({}, {})", source, xOffset, yOffset);
        new Actions(driver).clickAndHold(source).moveByOffset(xOffset, yOffset).release().perform();
        log.info("Dragged element: {} by offset: ({}, {})", source, xOffset, yOffset);
    }

    /**
     * 1.5 Performs a right-click (context click) on the specified element, which
     * typically opens a context menu.
     * Use when testing context menu interactions, such as right-clicking on a
     * product card to reveal additional options or actions in the Timepieces
     * interface.
     * 
     * @param driver  The WebDriver instance to perform the action on.
     * @param element The WebElement to right-click on.
     */
    public static void rightClick(WebDriver driver, WebElement element) {
        log.info("Performing right-click on element: {}", element);
        new Actions(driver).contextClick(element).perform();
        log.info("Right-click performed on element: {}", element);
    }

    /**
     * 1.6 Performs a double-click action on the specified element, which can
     * trigger events bound to double-clicks, such as opening a detailed view or
     * activating an edit mode in the Timepieces product configurator, or model
     * viewers, and any component bound to the double click event.
     * 
     * @param driver  The WebDriver instance to perform the action on.
     * @param element The WebElement to double-click on.
     */
    public static void doubleClick(WebDriver driver, WebElement element) {
        log.debug("Double clicking on element: {}", element);
        new Actions(driver).doubleClick(element).perform();
        log.info("Double clicked on element: {}", element);
    }

    /**
     * 1.7 Performs a click-and-hold action on the specified element without
     * releasing, which simulates the user clicking and holding down the mouse
     * button on an element. This can be used to trigger events that occur on mouse
     * down, such as starting a drag operation or revealing hidden content in the
     * Timepieces interface.
     * 
     * @param driver  The WebDriver instance to perform the action on.
     * @param element The WebElement to click and hold on.
     */
    public static void clickAndHold(WebDriver driver, WebElement element) {
        log.debug("Clicking and holding element: {}", element);
        new Actions(driver).clickAndHold(element).perform();
        log.info("Clicked and holding element: {}", element);
    }

    /**
     * 1.8 Releases a previously held click on the specified element, simulating the
     * user releasing the mouse button after a click-and-hold action. This can be
     * used to complete a drag operation or trigger events that occur on mouse up in
     * the Timepieces interface.
     * Always paired with the clickAndHold() method to ensure the full
     * click-and-hold interaction is performed correctly.
     * Releases wherever the drag sequence ends — call after moving to the desired
     * target.
     * 
     * @param driver  The WebDriver instance to perform the action on.
     * @param element The WebElement to release the click on.
     */
    public static void release(WebDriver driver, WebElement element) {
        log.debug("Releasing click on element: {}", element);
        new Actions(driver).release(element).perform();
        log.info("Released click on element: {}", element);
    }

    /**
     * 1.9 Moves the mouse pointer by a specified offset (x, y) coordinate relative
     * to the viewport, which can be used to simulate dragging an element by a
     * certain distance or moving the cursor to a specific location on the page.
     * This is useful for testing interactions that depend on precise cursor
     * movements, such as dragging a part on a 3D canvas in the Timepieces product
     * configurator.
     * Use when interacting with canvas-based elements (e.g., WebGL 3D model
     * viewers, heat-map interactions) that respond to raw pointer coordinates
     * rather than DOM elements.
     * 
     * @param driver  The WebDriver instance to perform the action on.
     * @param xOffset The horizontal offset in pixels to move the mouse pointer.
     * @param yOffset The vertical offset in pixels to move the mouse pointer.
     */
    public static void moveToCoordinates(WebDriver driver, WebElement element, int xOffset, int yOffset) {
        log.debug("Moving to coordinates relative to element: {}", element);
        new Actions(driver).moveToElement(element).moveByOffset(xOffset, yOffset).perform();
        log.info("Moved to coordinates relative to element: {}", element);
    }

    // ==========================================
    // 2. KEYBOARD ACTIONS
    // ==========================================
    /**
     * 2.1 Sends a keyboard key to the given element (e.g., Keys.ENTER, Keys.TAB).
     * Use for triggering form submissions, dismissing autocomplete dropdowns,
     * or navigating focus order in forms without clicking.
     * 
     * @param driver  The WebDriver instance to perform the action on.
     * @param element The WebElement to send the key to.
     * @param key     The keyboard key to send (e.g., Keys.ENTER, Keys.TAB).
     */
    public static void sendKey(WebDriver driver, WebElement element, Keys key) {
        log.debug("Sending key: {} to element: {}", key, element);
        new Actions(driver).sendKeys(element, key).perform();
        log.info("Sent key: {} to element: {}", key, element);
    }

    /**
     * 2.2 Sends a keyboard shortcut chord to the active browser window. A chord is
     * a key combination held simultaneously (e.g., CTRL+A, CTRL+Z).
     * Use for clipboard operations, undo/redo, or keyboard shortcuts in the Timepieces
     * product configuration editor.
     * Note: The keys are sent in a sequence of keyDown for each key, followed by
     * keyUp for each key, to ensure the correct chord is performed.
     * 
     * @param driver The WebDriver instance to perform the action on.
     * @param keys   The keyboard keys to send as a chord (e.g., Keys.CONTROL,
     *               Keys.A).
     */

    public static void sendKeyboardShortcut(WebDriver driver, Keys modifier, String key) {
        log.debug("Sending keyboard shortcut: {} + {}", modifier.name(), key);
        new Actions(driver).keyDown(modifier).sendKeys(key).keyUp(modifier).perform();
        log.info("Keyboard shortcut sent: {} + {}", modifier.name(), key);
    }

    /**
     * 2.3 Sends a sequence of keys to the given element, simulating typing. This
     * can be used for entering text into input fields, filling out forms, or
     * simulating user input in the Timepieces product configurator.
     * The method sends each character in the input string as a separate key event,
     * which can trigger events like onKeyPress or onInput for each character,
     * allowing for more realistic typing simulations.
     * 
     * @param driver  The WebDriver instance to perform the action on.
     * @param element The WebElement to send the keys to.
     * @param keys    The string of keys to send (e.g., "Hello World").
     */
    public static void sendKeys(WebDriver driver, WebElement element, String keys) {
        log.debug("Sending keys: '{}' to element: {}", keys, element);
        new Actions(driver).sendKeys(element, keys).perform();
        log.info("Sent keys: '{}' to element: {}", keys, element);
    }

    /**
     * 2.4 Clears an input field and types the given text using keyboard simulation.
     * Unlike element.sendKeys(), this goes through the Actions chain, which
     * fires all keyboard events (keydown, keypress, keyup) reliably. Use for
     * React/NextJS controlled inputs where synthetic events can cause the
     * element.sendKeys() approach to silently drop characters.
     * 
     * @param driver  The WebDriver instance to perform the action on.
     * @param element The WebElement to send the keys to.
     * @param keys    The string of keys to send (e.g., "Hello World").
     */
    public static void typeWithKeyboard(WebDriver driver, WebElement element, String text) {
        log.debug("Typing text into element via keyboard Actions: {}", element);
        new Actions(driver)
                .click(element) // focus the field
                .keyDown(Keys.CONTROL).sendKeys("a") // select all existing text
                .keyUp(Keys.CONTROL)
                .sendKeys(text) // type new text
                .perform();
        log.info("Typed text into element via keyboard: '{}'", text);
    }

    // ==========================================
    // 3. COMBINED GESTURE ACTIONS
    // ==========================================
    /**
     * 3.1 Hovers over a parent element and then clicks a child element that
     * becomes visible only after hovering (e.g., a dropdown item, a tooltip
     * button, or a contextual action appearing on card hover).
     * Very common in Timepieces product listing cards and nav menus.
     *
     * @param driver The WebDriver instance.
     * @param parent The WebElement to hover over first.
     * @param child  The WebElement to click after hover reveals it.
     */
    public static void hoverAndClick(WebDriver driver, WebElement parent, WebElement child) {
        log.debug("Hovering over element: {} and clicking child element: {}", parent, child);
        new Actions(driver).moveToElement(parent).click(child).perform();
        log.info("Hovered over element: {} and clicked child element: {}", parent, child);
    }

    /**
     * 3.2 Scrolls horizontally within a scrollable container element by a pixel
     * offset.
     * Standard window.scrollTo only scrolls the body. For horizontally scrollable
     * carousels, product galleries, or spec tables in Timepieces, you need to scroll
     * within the container element itself.
     *
     * @param driver      The WebDriver instance.
     * @param container   The scrollable container WebElement.
     * @param pixelsDelta Pixels to scroll: positive = right, negative = left.
     */
    public static void horizontalScroll(WebDriver driver, WebElement container, int pixelsDelta) {
        log.debug("Scrolling container {} horizontally by {} pixel", container, pixelsDelta);
        new Actions(driver).moveToElement(container).click().moveByOffset(pixelsDelta, 0).perform();
        log.info("Scrolled container {} horizontally by {} pixel", container, pixelsDelta);
    }

    /**
     * 3.3 Scrolls vertically within a scrollable container element by a pixel
     * offset.
     * Use for scrolling inside modals, drawers, or fixed-height panels —
     * common in Timepieces configuration panels and specification sidebars — where
     * the scrollable area is a child container, not the main document body.
     *
     * @param driver      The WebDriver instance.
     * @param container   The scrollable container WebElement.
     * @param pixelsDelta Pixels to scroll: positive = down, negative = up.
     */
    public static void verticalScroll(WebDriver driver, WebElement container, int pixelsDelta) {
        log.debug("Scrolling container {} vertically by {} pixels", container, pixelsDelta);
        new Actions(driver).moveToElement(container).click().moveByOffset(0, pixelsDelta).perform();
        log.info("Scrolled container {} vertically by {} pixels", container, pixelsDelta);
    }

    /**
     * 3.4 Performs a drag-and-drop action by dragging a source element to a target
     * element and then sending a keyboard shortcut (e.g., CTRL+S) to save the
     * changes. This simulates a common user workflow of rearranging items and then
     * saving them.
     * Use for testing complex interactions in the Timepieces product configurator
     * where
     * users drag parts into a design area and then save their configuration with a
     * keyboard shortcut.
     * Note: The method combines mouse and keyboard actions in a single sequence to
     * ensure the entire workflow is executed as a cohesive unit.
     * 
     * @param driver The WebDriver instance to perform the action on.
     * @param source The WebElement to be dragged.
     * @param target The WebElement to drag the source to.
     */
    public static void dragAndDropWithSave(WebDriver driver, WebElement source, WebElement target) {
        log.debug("Dragging element: {} to target: {} and then saving with CTRL+S", source, target);
        new Actions(driver)
                .dragAndDrop(source, target)
                .keyDown(Keys.CONTROL).sendKeys("s").keyUp(Keys.CONTROL)
                .perform();
        log.info("Dragged element: {} to target: {} and saved with CTRL+S", source, target);
    }

    /**
     * 3.5 Hovers over a parent element, clicks a child element that appears on
     * hover, and then sends an ENTER key to confirm an action (e.g., confirming a
     * delete or save action in a tooltip).
     * Use for testing interactions where a user hovers to reveal an action button,
     * clicks it, and then confirms the action with a keyboard shortcut, which is
     * common in Timepieces product cards and configuration panels.
     * 
     * @param driver     The WebDriver instance to perform the action on.
     * @param parent     The WebElement to hover over first.
     * @param child      The WebElement to click after hovering.
     * @param confirmKey The keyboard key to send for confirmation (e.g.,
     *                   Keys.ENTER).
     */
    public static void hoverClickAndConfirm(WebDriver driver, WebElement parent, WebElement child, Keys confirmKey) {
        log.debug("Hovering over element: {}, clicking child element: {}, and confirming with key: {}", parent, child,
                confirmKey);
        new Actions(driver)
                .moveToElement(parent)
                .click(child)
                .sendKeys(confirmKey)
                .perform();
        log.info("Hovered over element: {}, clicked child element: {}, and confirmed with key: {}", parent, child,
                confirmKey);
    }

    /**
     * 3.6 Performs a right-click on an element to open a context menu, then sends a
     * keyboard shortcut to select an option from the context menu (e.g., pressing
     * 'D' to select "Delete" from the menu).
     * Use for testing context menu interactions where users right-click to open a
     * menu and then use keyboard shortcuts to select options, which is common in
     * Timepieces product cards and configuration interfaces.
     * 
     * @param driver        The WebDriver instance to perform the action on.
     * @param element       The WebElement to right-click on.
     * @param menuOptionKey The keyboard key to select the context menu option
     *                      (e.g., 'D' for Delete).
     *                      Note: The method assumes that the context menu is
     *                      already open after the right-click action and that the
     *                      specified key corresponds to a valid menu option.
     */
    public static void rightClickAndSelectOption(WebDriver driver, WebElement element, String menuOptionKey) {
        log.debug("Right-clicking on element: {} and selecting context menu option with key: {}", element,
                menuOptionKey);
        new Actions(driver)
                .contextClick(element)
                .sendKeys(menuOptionKey)
                .perform();
        log.info("Right-clicked on element: {} and selected context menu option with key: {}", element, menuOptionKey);
    }
}
