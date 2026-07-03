package com.timepieces.automation.pages.components;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.timepieces.automation.pages.BasePage;
import com.timepieces.automation.utils.ActionsUtils;

import lombok.extern.log4j.Log4j2;

/**
 * StoriesComponent class is a representation of the Stories Page, which is a
 * fullscreen modal component that appears when the user clicks the Stories
 * toggle button in the site header.
 * This class extends the BasePage class, which provides commonalities for all
 * page components, such as the {@code driver}, {@code wait}, and various helper
 * methods for interacting with web elements. This class inherits the
 * {@code clickElement(By locator)}, {@code isDisplayed(By locator)}, and
 * {@code waitUntilDisplayed(By locator)} methods, which are crucial for
 * performing actions and validations on the page. Additionally, this class uses
 * the {@code log4j2} annotation to automatically generate a logger instance,
 * which is used for logging information about the test execution.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (StoriesComponent) to allow method chaining in step definitions.
 * 
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class StoriesComponent extends BasePage<StoriesComponent> {

    // ==========================================
    // Constructor
    // ==========================================
    public StoriesComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    /*
     * Placeholder values added for now. Will be replacing them with the
     * data-testid() attributes for the whole Stories component once they are
     * available.
     */
    // Stories Page Container: (Viewport Agnostic)
    private final By storiesPageContainer = By.cssSelector("div[role='dialog'][aria-modal='true']");

    // Stories Play/Pause Button: (Mobile viewport)
    private final By storiesPlay_PauseButton_Mobile = By.cssSelector("button[data-testid='stories-close']");

    // Stories Play/Pause Button: (Desktop viewport)
    private final By storiesPlay_PauseButton_Desktop = By.cssSelector("button[aria-label='Close']");

    // Stories Close Button: (Mobile viewport)
    private final By storiesCloseButton_Mobile = By.cssSelector("button[data-testid='stories-close']");

    // Stories Close Button: (Desktop viewport)
    private final By storiesCloseButton_Desktop = By.cssSelector("button[aria-label='Close']");

    // Stories Previous Button: (Mobile Viewport)
    private final By storiesPreviousButton_Mobile = By.cssSelector("button[data-testid='stories-previous']");

    // Stories Previous Button: (Desktop viewport)
    private final By storiesPreviousButton_Desktop = By.cssSelector("button[data-testid='stories-previous']");

    // Stories Next Button: (Mobile viewport)
    private final By storiesNextButton_Mobile = By.cssSelector("button[data-testid='stories-next']");

    // Stories Next Button: (Desktop viewport)
    private final By storiesNextButton_Desktop = By.cssSelector("button[data-testid='stories-next']");

    // Stories Window Card: (Mobile viewport)
    private final By storiesWindowCard_Mobile = By.cssSelector("div[data-testid='stories-window']");

    // Stories Window Card: (Desktop viewport)
    private final By storiesWindowCard_Desktop = By.cssSelector("div[data-testid='stories-window']");

    // Stories Progress Bar: (Mobile viewport)
    private final By storiesProgressBar_Mobile = By.cssSelector("div[data-testid='stories-progress-bar']");

    // Stories Progress Bar: (Desktop viewport)
    private final By storiesProgressBar_Desktop = By.cssSelector("div[data-testid='stories-progress-bar']");

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================

    /**
     * B.1 Closes the StoriesPage by clicking the close button.
     *
     * @return The previous page (HeaderComponent in this case) for method chaining.
     */
    public HeaderComponent closeStories() {
        log.info("Closing Stories page.");
        if (isMobileViewport()) {
            clickElement(storiesCloseButton_Mobile);
        } else {
            clickElement(storiesCloseButton_Desktop);
        }
        return new HeaderComponent(driver);
    }

    /**
     * B.2 Plays/Pauses the StoriesPage Window Cards, when clicked on.
     *
     * @return this StoriesComponent for method chaining.
     */
    public StoriesComponent clickOnStoriesWindowCard() {
        log.info("Clicking on the Stories page window card.");
        if (isMobileViewport()) {
            clickElement(storiesPlay_PauseButton_Mobile);
        } else {
            clickElement(storiesPlay_PauseButton_Desktop);
        }
        return this;
    }

    /**
     * B.3 Navigates to the previous StoriesCard by clicking the previous button.
     *
     * @return this StoriesComponent for method chaining.
     */
    public StoriesComponent clickStoriesPreviousButton() {
        log.info("Clicking on the Stories page previous button.");
        if (isMobileViewport()) {
            clickElement(storiesPreviousButton_Mobile);
        } else {
            clickElement(storiesPreviousButton_Desktop);
        }
        return this;
    }

    /**
     * B.4 Navigates to the next Stories Card by clicking on the next button.
     * If on the last card, this action dismisses the Stories modal.
     *
     * @return BasePage (either StoriesComponent, or HeaderComponent if dismissed).
     */
    public BasePage clickStoriesNextButton() {
        log.info("Clicking on the Stories page next button.");
        boolean isLast = isLastCard();
        if (isMobileViewport()) {
            clickElement(storiesNextButton_Mobile);
        } else {
            clickElement(storiesNextButton_Desktop);
        }
        if (isLast) {
            log.info("Last card reached via next button. Stories modal dismissed.");
            waitForInvisibility(storiesPageContainer);
            return new HeaderComponent(driver);
        }
        return this;
    }

    /**
     * B.5 Skips to the next StoriesCard by pressing the Spacebar key.
     *
     * @return this StoriesComponent for method chaining.
     */
    public StoriesComponent pressSpacebarToPauseStoriesCards() {
        log.info("Pressing spacebar to pause StoriesCards.");
        ActionsUtils.sendKey(driver, getElement(storiesPageContainer), Keys.SPACE);
        return this;
    }

    /**
     * B.6 Resume Stories Cards by pressing the Spacebar key.
     *
     * @return this StoriesComponent for method chaining.
     */
    public StoriesComponent pressSpacebarToResumeStoriesCards() {
        log.info("Pressing spacebar to resume StoriesCards.");
        ActionsUtils.sendKey(driver, getElement(storiesPageContainer), Keys.SPACE);
        return this;
    }

    /**
     * B.7 Goes to the next StoriesCards by pressing the Right Arrow key.
     * If on the last card, this action dismisses the Stories modal.
     *
     * @return BasePage (either StoriesComponent, or HeaderComponent if dismissed).
     */
    public BasePage pressRightArrowKeyToNextStoriesCards() {
        log.info("Pressing right arrow key to go to the next StoriesCards.");
        boolean isLast = isLastCard();
        ActionsUtils.sendKey(driver, getElement(storiesPageContainer), Keys.RIGHT);
        if (isLast) {
            log.info("Last card reached via right arrow key. Stories modal dismissed.");
            waitForInvisibility(storiesPageContainer);
            return new HeaderComponent(driver);
        }
        return this;
    }

    /**
     * B.8 Goes to the previous StoriesCards by pressing the Left Arrow key.
     *
     * @return this StoriesComponent for method chaining.
     */
    public StoriesComponent pressLeftArrowKeyToPreviousStoriesCards() {
        log.info("Pressing left arrow key to go to the previous StoriesCards.");
        ActionsUtils.sendKey(driver, getElement(storiesPageContainer), Keys.LEFT);
        return this;
    }

    /**
     * B.9 Waits for the last card's story timer to run out and the Stories modal to
     * be dismissed automatically.
     *
     * @return HeaderComponent representing the page behind the dismissed modal.
     */
    public HeaderComponent waitForLastCardTimerToRunOut() {
        log.info("Waiting for the last card story timer to run out and dismiss the Stories modal.");
        waitForInvisibility(storiesPageContainer);
        return new HeaderComponent(driver);
    }

    // ==========================================
    // PRIVATE HELPERS (FOR STORY TRACKING)
    // ==========================================
    /**
     * H.1 Helper method to get the index of the currently active story card.
     * It scans the progress bars and returns the index of the one with the 'active'
     * state.
     *
     * @return index of the active card, or -1 if not found.
     */
    private int getActiveCardIndex() {
        By progressBarLocator = isMobileViewport() ? storiesProgressBar_Mobile : storiesProgressBar_Desktop;
        List<WebElement> progressBars = getElements(progressBarLocator);
        for (int i = 0; i < progressBars.size(); i++) {
            String className = progressBars.get(i).getAttribute("class");
            String dataStatus = progressBars.get(i).getAttribute("data-status");
            if ((className != null && className.contains("active"))
                    || (dataStatus != null && "active".equalsIgnoreCase(dataStatus))) {
                return i;
            }
        }
        return -1;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================

    /**
     * V.1 Verifies if the StoriesPage is displayed on the page.
     *
     * @return true if displayed, false otherwise.
     */
    public boolean isStoriesDisplayed() {
        boolean displayed = isDisplayed(storiesPageContainer);
        log.info("StoriesPage displayed check: {}", displayed);
        return displayed;
    }

    /**
     * V.2 Verifies if the currently displayed card is the last card in the Stories
     * component.
     *
     * @return true if it is the last card, false otherwise.
     */
    public boolean isLastCard() {
        By progressBarLocator = isMobileViewport() ? storiesProgressBar_Mobile : storiesProgressBar_Desktop;
        try {
            List<WebElement> progressBars = getElements(progressBarLocator);
            if (progressBars == null || progressBars.isEmpty()) {
                return true;
            }
            int activeIndex = getActiveCardIndex();
            return activeIndex == progressBars.size() - 1;
        } catch (Exception e) {
            log.warn("Failed to determine if last card: {}", e.getMessage());
            return false;
        }
    }
}
