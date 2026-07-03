package com.timepieces.automation.pages.components.cards;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * AstoniaCardComponent — Component Object Model for the Astonia Product Drop
 * Card Component. It is for the current Astonia drop till August 2026. It will
 * be replaced by the next drop card. Card components are a temporary and will
 * always need to be archived when it's function is over.
 * 
 * Current function of the card:
 * Appears within 15 seconds if the the drop page button in the landing/homepage
 * is not selected.
 * Can be dismissed once, then reappears after 1 minute, provided the user has
 * not gone to the associated drop page that the drop product card refers to=,
 * in this case, the upcoming Astonia product drop.
 * If the user dismisses the card a second time, then it is permanently
 * dismissed and cannot reappear even with navigation actions like page
 * refresh/back/forward, etc.
 * Also, the card is permanently dismissed if the user clicks on the card and is
 * navigated to the concerned drop page (Astonia for now, not Manifesta).
 * It is also permanently dismissed if the user navigates to the drop page
 * without clicking directly on the card but using any of the other navigation
 * links in the website.
 * Clicking on the card redirects the user to: URL:
 * https://www.timepieces-d2c.example/new-release/astonia (AstoniaDropPage)
 */

@Log4j2
public class AstoniaDropCardComponent extends BasePage<AstoniaDropCardComponent> {

    // ==========================================
    // Constructor
    // ==========================================
    public AstoniaDropCardComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Selector for any Astonia product drop link card on the catalog:
    private final By astoniaDropCard = By.cssSelector("div[data-testid='astonia-popup-card']");
    // The Card Label Message
    private final By dropCardName = By.cssSelector("h3[data-testid='astonia-popup-title']");
    // Label Card Message Text
    private final By dropCardText = By.cssSelector("h3[data-testid='astonia-popup-title'] + p");
    // The Astonia Drop Card Dismiss Button
    private final By dismissAstoniaCardButton = By.cssSelector("button[data-testid='astonia-popup-close']");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * Polls up to {@code timeoutSeconds} for the Astonia card to become visible.
     * Non-throwing:
     * returns false if the card never appears within the budget. Shared by the
     * timed
     * appearance/reappearance checks so the polling logic lives in one place.
     *
     * @param timeoutSeconds maximum time to wait for the card to appear.
     * @return true if the card became visible within the timeout, false otherwise.
     */
    private boolean becomesVisibleWithin(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(astoniaDropCard));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Waits for the Astonia drop card to be visible, then clicks it. Clicking
     * navigates the
     * user to the Astonia drop page (https://www.timepieces-d2c.example/new-release/astonia)
     * and permanently
     * dismisses the card.
     *
     * @return this AstoniaCardComponent for method chaining.
     */
    public AstoniaDropCardComponent clickAstoniaDropCard() {
        log.info("Waiting for the Astonia drop card, then clicking it.");
        waitForVisibility(astoniaDropCard);
        clickElement(astoniaDropCard);
        log.info("Astonia drop card clicked.");
        return this;
    }

    /**
     * B.2 Dismisses the Astonia drop card via its close button. The first dismissal
     * is temporary —
     * the card reappears after ~1 minute (unless the user has visited the drop
     * page); a second
     * dismissal is permanent.
     *
     * @return this AstoniaCardComponent for method chaining.
     */
    public AstoniaDropCardComponent dismissAstoniaCard() {
        log.info("Dismissing the Astonia drop card via its close button.");
        clickElement(dismissAstoniaCardButton);
        log.info("Astonia drop card dismissed.");
        return this;
    }

    /**
     * B.3 Waits up to {@code timeoutSeconds} for the card to (re)appear, then
     * clicks it. Use after
     * a first dismissal to interact with the reappeared instance (allow ~60s plus
     * buffer for the
     * reappearance window).
     *
     * @param timeoutSeconds maximum time to wait for the card to (re)appear.
     * @return this AstoniaCardComponent for method chaining.
     * @throws NoSuchElementException if the card does not appear within the
     *                                timeout.
     */
    public AstoniaDropCardComponent clickAstoniaDropCardWhenVisible(int timeoutSeconds) {
        log.info("Waiting up to {}s for the Astonia card to (re)appear, then clicking it.", timeoutSeconds);
        if (!becomesVisibleWithin(timeoutSeconds)) {
            throw new NoSuchElementException(
                    "Astonia drop card did not appear within " + timeoutSeconds + "s.");
        }
        clickElement(astoniaDropCard);
        log.info("Astonia drop card clicked after appearing within {}s.", timeoutSeconds);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /*
     * These return boolean so step definitions can pass them to assertion libraries
     * (AssertJ/TestNG) without the component depending on any assertion framework.
     */

    /**
     * V.1 Verifies if the Astonia product card is currently displayed (within the
     * standard explicit
     * wait). Use to assert the card auto-appears (~15s) when the drop page has not
     * been visited.
     *
     * @return true if displayed, false otherwise.
     */
    public boolean isAstoniaDropCardDisplayed() {
        boolean displayed = isDisplayed(astoniaDropCard);
        log.info("Astonia card presence check: {}", displayed);
        return displayed;
    }

    /**
     * V.2 Verifies the card (re)appears within {@code timeoutSeconds}. After a
     * first dismissal the
     * card should reappear at ~1 minute, so a positive reappearance check would
     * pass a value of
     * ~65s here. To assert the card does NOT reappear too early, call this with the
     * lower bound
     * (e.g. 55s) and expect {@code false}.
     *
     * @param timeoutSeconds the appearance window to poll.
     * @return true if the card became visible within the window, false otherwise.
     */
    public boolean isAstoniaDropCardDisplayedWithin(int timeoutSeconds) {
        boolean displayed = becomesVisibleWithin(timeoutSeconds);
        log.info("Astonia card visible within {}s check: {}", timeoutSeconds, displayed);
        return displayed;
    }

}
