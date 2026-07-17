package com.timepieces.automation.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * StoriesModalComponent — the Instagram-style stories dialog opened from the
 * header's {@code header-stories-toggle} button (separate desktop/mobile
 * trigger nodes exist; only the visible viewport node is interactable).
 *
 * <p>
 * SCRAPE NOTE (2026-07-16): the modal is a {@code div[role='dialog']}
 * ({@code aria-modal='true'}) over a blurred backdrop. A per-story progress bar
 * strip sits on top; the ACTIVE segment's fill animates its inline
 * {@code style="width: X%"} — stories AUTO-ADVANCE when the fill reaches 100%,
 * and the modal AUTO-CLOSES after the final story completes (verified). A
 * pause/play button flips its {@code aria-label} between "Pause" and "Play".
 * Tap-navigation: invisible hotspots cover the left 25% ("Previous story") and
 * right 25% ("Next story") of the story panel; desktop also renders an outside
 * previous-arrow with the same aria-label. Close is
 * {@code button[aria-label='Close stories']}. Only the trigger carries a
 * {@code data-testid} — the dialog internals are aria/structural fallbacks
 * tagged {@code // ROLLOUT-GAP}.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this component return {@code this}
 * (StoriesModalComponent) to allow method chaining in step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of StoriesModalComponent"
 * @since 2026-07-16
 */

@Log4j2
public class StoriesModalComponent extends BasePage<StoriesModalComponent> {

    // ==========================================
    // Constructor
    // ==========================================
    public StoriesModalComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // The header trigger that opens the stories modal (real testid).
    private final By storiesToggle = By.cssSelector("button[data-testid='header-stories-toggle']");
    // The dialog container (aria-modal over the blurred backdrop).
    // Scoped with :not(...) deliberately. The bare div[role='dialog'][aria-modal='true']
    // ALSO matches the site-wide cookie consent bar, which is markup-identical at that
    // level (it is only distinguishable by aria-labelledby='cookie-consent-title'). With
    // the bare selector this component would happily report "the stories modal is open"
    // while looking at the cookie banner — a false pass, which is worse than a failure.
    private final By dialog = By.cssSelector(
            "div[role='dialog'][aria-modal='true']:not([aria-labelledby='cookie-consent-title'])");
    // The per-story progress bar segments.
    private final By progressSegments = By.cssSelector("div[role='dialog'] div.flex-1.h-\\[3px\\]"); // ROLLOUT-GAP: structural
    // The ACTIVE segment's animated fill (inline width % advances over time).
    private final By activeProgressFill = By.cssSelector("div[role='dialog'] div.h-full.bg-white.transition-\\[width\\]"); // ROLLOUT-GAP: structural
    // The pause/play toggle (aria-label flips "Pause" <-> "Play").
    private final By pausePlayButton = By.cssSelector("button[aria-label='Pause'], button[aria-label='Play']"); // ROLLOUT-GAP: aria
    // The close button.
    private final By closeButton = By.cssSelector("button[aria-label='Close stories']"); // ROLLOUT-GAP: aria
    // The tap-previous / tap-next hotspots (also the desktop outside arrow for previous).
    private final By previousHotspot = By.cssSelector("button[aria-label='Previous story']"); // ROLLOUT-GAP: aria
    private final By nextHotspot = By.cssSelector("button[aria-label='Next story']"); // ROLLOUT-GAP: aria
    // The current story image.
    private final By storyImage = By.cssSelector("div[role='dialog'] div.absolute.h-\\[90\\%\\] img"); // ROLLOUT-GAP: structural

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Opens the stories modal via the header trigger.
     *
     * @return this component instance for method chaining.
     */
    public StoriesModalComponent openStories() {
        log.info("Opening the stories modal via the header trigger.");
        clickElement(storiesToggle);
        return this;
    }

    /**
     * B.2 Advances to the next story via the right tap-hotspot.
     *
     * @return this component instance for method chaining.
     */
    public StoriesModalComponent nextStory() {
        log.info("Tapping to the next story.");
        clickElement(nextHotspot);
        return this;
    }

    /**
     * B.3 Goes back to the previous story via the left tap-hotspot / desktop
     * arrow.
     *
     * @return this component instance for method chaining.
     */
    public StoriesModalComponent previousStory() {
        log.info("Tapping to the previous story.");
        clickElement(previousHotspot);
        return this;
    }

    /**
     * B.4 Toggles pause/play on the auto-advance.
     *
     * @return this component instance for method chaining.
     */
    public StoriesModalComponent togglePause() {
        log.info("Toggling the stories pause/play state.");
        clickElement(pausePlayButton);
        return this;
    }

    /**
     * B.5 Closes the stories modal.
     *
     * @return this component instance for method chaining.
     */
    public StoriesModalComponent closeStories() {
        log.info("Closing the stories modal.");
        clickElement(closeButton);
        // Wait for the close to actually finish. The modal fades out, so returning the
        // instant the click lands leaves the caller asserting against an element that is
        // still mid-animation and still "displayed" — which reads as "the modal will not
        // close" when it is closing perfectly well.
        isEventuallyHidden(dialog);
        return this;
    }

    /**
     * B.6 Returns the active segment's fill width (the inline "width: X%" style),
     * useful for asserting auto-advance progression between two reads.
     *
     * @return the raw style attribute of the active fill (e.g. "width: 42%").
     */
    public String getActiveFillStyle() {
        return getAttribute(activeProgressFill, "style");
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Reports whether the stories dialog is open.
     *
     * @return true if the dialog is present.
     */
    public boolean isOpen() {
        return !driver.findElements(dialog).isEmpty();
    }

    /**
     * V.2 Counts the story progress segments (one per story).
     *
     * @return the number of stories.
     */
    public int getStoryCount() {
        return driver.findElements(progressSegments).size();
    }

    /**
     * V.3 Reports whether the auto-advance is currently PAUSED (the toggle
     * offers "Play").
     *
     * @return true if paused.
     */
    public boolean isPaused() {
        try {
            String label = getAttribute(pausePlayButton, "aria-label");
            return "Play".equals(label);
        } catch (Exception e) {
            log.warn("Could not read the stories pause/play state.", e);
            return false;
        }
    }

    /**
     * V.4 Verifies the current story image is displayed.
     *
     * @return true if the story image is displayed.
     */
    public boolean isStoryImageDisplayed() {
        return isDisplayed(storyImage);
    }
}
