package com.timepieces.automation.pages.web.otherPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.HeaderOnlyBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * TimepiecesWallPage: https://www.timepieces-d2c.example/timepieces-wall/ showcases the
 * various user testimonials and photos in an infinite scroll canvas
 * with most of the testimonials and photos in a polaroid style container
 * and some photos without containers if they have been shared without any
 * testimonials. The background is black with a black rotating globe with
 * white lines of longitude and latitude giving the impression of a
 * rotating globe animation.
 * There is a button to toggle the music on/off. This page only contains
 * the Header.
 *
 * <p>
 * SCRAPE NOTE (2026-07-15): the testimonial "polaroid" cards and photos are
 * NOT HTML DOM nodes — they are rendered directly inside a WebGL {@code <canvas>}
 * via Three.js, with the data fetched from {@code /api/timepieces-wall}. There is
 * therefore no per-card locator to assert on; the page is verified via the
 * draggable wall container, the WebGL canvas, the background {@code <audio>}
 * element, and the music on/off toggle (which flips its {@code aria-label}
 * between "Play background music" and "Mute background music"). Infinite scroll
 * happens inside the canvas, so there is no DOM load-more trigger. No element on
 * the wall body carries a {@code data-testid} (only the shared header does), so
 * every locator below is a fallback tagged {@code // ROLLOUT-GAP}.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (TimepiecesWallPage)
 * to allow method chaining in step definitions.
 *
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TimepiecesWallPage"
 */

@Log4j2
public class TimepiecesWallPage extends HeaderOnlyBasePage<TimepiecesWallPage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Page Path Variable
    private static final String PAGE_PATH = "/timepieces-wall";

    // The Page Title Variable {Unique or Not}
    private static final String PAGE_TITLE = "Timepieces Watches | Early Access to Limited Edition Pieces";

    // The two aria-label states of the music toggle button.
    private static final String MUSIC_LABEL_OFF = "Play background music"; // currently muted → click to play
    private static final String MUSIC_LABEL_ON = "Mute background music"; // currently playing → click to mute

    // ==========================================
    // Constructor
    // ==========================================
    public TimepiecesWallPage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // PAGE METADATA (drives the inherited fluent open())
    // ==========================================
    @Override
    protected String getPagePath() {
        return PAGE_PATH;
    }

    @Override
    protected String getPageTitle() {
        return PAGE_TITLE;
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // The full-screen draggable wall canvas wrapper (cursor-grab / active:cursor-grabbing).
    private final By wallContainer = By.cssSelector("div.cursor-grab"); // ROLLOUT-GAP: utility-class
    // The WebGL canvas that renders the polaroids + rotating globe animation.
    private final By wallCanvas = By.cssSelector("div.cursor-grab canvas"); // ROLLOUT-GAP: structural
    // The background music <audio> element.
    private final By audioPlayer = By.cssSelector("audio"); // ROLLOUT-GAP: semantic
    // The fixed bottom-left music on/off toggle button.
    private final By musicToggleButton = By.cssSelector("button.fixed.bottom-6.left-6"); // ROLLOUT-GAP: utility-class
    // The equalizer GIF inside the music toggle (music_equalizer.gif ↔ stop_music_equalizer.gif).
    private final By musicEqualizerImage = By.cssSelector("button.fixed.bottom-6.left-6 img"); // ROLLOUT-GAP: structural
    // The "LOADING MOMENTS" loader shown while the canvas hydrates.
    private final By loaderText = By.xpath("//div[contains(normalize-space(),'LOADING MOMENTS')]"); // ROLLOUT-GAP: text-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * P.1 Reads the current {@code aria-label} of the music toggle button, which
     * encodes the play/mute state.
     *
     * @return the toggle's aria-label, or an empty string if unreadable.
     */
    private String musicToggleLabel() {
        try {
            return getAttribute(musicToggleButton, "aria-label");
        } catch (Exception e) {
            log.warn("Could not read music toggle aria-label.", e);
            return "";
        }
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Clicks the music on/off toggle button, flipping the background audio
     * between playing and muted.
     *
     * @return this page instance for method chaining.
     */
    public TimepiecesWallPage toggleMusic() {
        log.info("Toggling Timepieces Wall background music.");
        clickElement(musicToggleButton);
        return this;
    }

    /**
     * B.2 Returns the music toggle's current {@code aria-label} ("Play background
     * music" when muted, "Mute background music" when playing).
     *
     * @return the toggle aria-label.
     */
    public String getMusicToggleAriaLabel() {
        return musicToggleLabel();
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the browser title matches this page's expected
     * {@code PAGE_TITLE}.
     *
     * @return true if the document title contains the expected page title.
     */
    public boolean isPageTitleCorrect() {
        String actual = driver.getTitle();
        return actual != null && actual.contains(PAGE_TITLE);
    }

    /**
     * V.2 Verifies the draggable wall container is displayed.
     *
     * @return true if the wall container is displayed.
     */
    public boolean isWallContainerDisplayed() {
        return isDisplayed(wallContainer);
    }

    /**
     * V.3 Verifies the WebGL canvas (which renders the polaroids + globe) is
     * present in the DOM.
     *
     * @return true if the wall canvas is present.
     */
    public boolean isWallCanvasDisplayed() {
        return !driver.findElements(wallCanvas).isEmpty();
    }

    /**
     * V.4 Verifies the background {@code <audio>} element is present in the DOM.
     *
     * @return true if the audio player is present.
     */
    public boolean isAudioPlayerPresent() {
        return !driver.findElements(audioPlayer).isEmpty();
    }

    /**
     * V.5 Verifies the music on/off toggle button is displayed.
     *
     * @return true if the music toggle is displayed.
     */
    public boolean isMusicToggleDisplayed() {
        return isDisplayed(musicToggleButton);
    }

    /**
     * V.6 Verifies the equalizer image inside the music toggle is present.
     *
     * @return true if the equalizer image is present.
     */
    public boolean isMusicEqualizerImagePresent() {
        return !driver.findElements(musicEqualizerImage).isEmpty();
    }

    /**
     * V.7 Reports whether the background music is currently playing, based on the
     * toggle's aria-label ("Mute background music" ⇒ playing).
     *
     * @return true if music is playing, false if muted.
     */
    public boolean isMusicPlaying() {
        return MUSIC_LABEL_ON.equals(musicToggleLabel());
    }

    /**
     * V.8 Reports whether the background music is currently muted, based on the
     * toggle's aria-label ("Play background music" ⇒ muted).
     *
     * @return true if music is muted, false if playing.
     */
    public boolean isMusicMuted() {
        return MUSIC_LABEL_OFF.equals(musicToggleLabel());
    }

    /**
     * V.9 Reports whether the "LOADING MOMENTS" loader is still visible (the
     * canvas is hydrating).
     *
     * @return true if the loader is present.
     */
    public boolean isLoaderVisible() {
        return !driver.findElements(loaderText).isEmpty();
    }
}
