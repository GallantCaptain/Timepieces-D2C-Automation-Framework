package com.timepieces.automation.pages.web.otherPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.HeaderOnlyBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The TimepiecesHousePage: https://www.timepieces-d2c.example/experience-center is meant to
 * direct the users to the physical stores of Timepieces in 'Delhi' & 'Gurugram'
 * only for Q3 2026. It provides the google maps links and address for the
 * users to be able to visit the stores and have a direct experience and
 * consultations with the Timepieces Official Concierge. They will be able to
 * touch and see the timepieces in person. It is meant to the elevate the
 * premium watch experience.
 *
 * <p>
 * LOCATOR SOURCE: 2026-07-10 scrape
 * ({@code docs/scrape-artifacts/phase0/timepieces-house-page.md}). This page has
 * almost NO {@code data-testid}s (only the shared {@code site-header}), so the
 * store cards and CTAs fall back to text-anchor XPath / structural selectors —
 * every one is tagged {@code // ROLLOUT-GAP}. Store cards are matched via XPath
 * because Selenium's CSS engine supports neither {@code :has()} nor
 * {@code :contains()}.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (TimepiecesHousePage)
 * to allow method chaining in step definitions.
 *
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TimepiecesHousePage"
 */

@Log4j2
public class TimepiecesHousePage extends HeaderOnlyBasePage<TimepiecesHousePage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Page Path Variable
    private static final String PAGE_PATH = "/experience-center";

    // The Page Title Variable {Keep whether Unique or Not}
    // Confirmed by the 2026-07-10 scrape as the real experience-center title.
    private static final String PAGE_TITLE = "Timepieces Watches | Early Access to Limited Edition Pieces";

    // ==========================================
    // Constructor
    // ==========================================
    public TimepiecesHousePage(WebDriver driver) {
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
    private final By pageHeading = By.xpath("//h1[contains(normalize-space(),'Experience')]"); // ROLLOUT-GAP: text-anchor

    // --- Event / video section ---
    private final By eventVideo = By.cssSelector("video.object-cover"); // ROLLOUT-GAP: structural
    private final By eventAudioToggle = By.cssSelector("button[aria-label='Unmute'], button[aria-label='Mute']"); // ROLLOUT-GAP: aria
    private final By eventSectionHeader = By.xpath("//h2[normalize-space()='Timepieces House']"); // ROLLOUT-GAP: text-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * P.1 Builds a locator for a store card container scoped by its city, matched
     * on the card's {@code <h2>} heading (e.g. "New Delhi" / "Gurugram").
     *
     * @param city the city named in the store card heading.
     * @return a By locator for that store's card container.
     */
    private By storeCard(String city) {
        return By.xpath("//div[contains(@class,'rounded-2xl')][.//h2[contains(normalize-space(),'" + city + "')]]");
    }

    /**
     * P.2 Builds a locator for the address paragraph inside a city's store card.
     *
     * @param city the city named in the store card heading.
     * @return a By locator for the store's address text.
     */
    private By storeAddress(String city) {
        return By.xpath("(//div[contains(@class,'rounded-2xl')][.//h2[contains(normalize-space(),'" + city
                + "')]]//p)[1]");
    }

    /**
     * P.3 Builds a locator for a text-labelled button inside a city's store card
     * (e.g. "Get Directions" / "Share Location").
     *
     * @param city       the city named in the store card heading.
     * @param buttonText the visible button label.
     * @return a By locator for that button within the store card.
     */
    private By storeCardButton(String city, String buttonText) {
        return By.xpath("//div[contains(@class,'rounded-2xl')][.//h2[contains(normalize-space(),'" + city
                + "')]]//button[contains(normalize-space(),'" + buttonText + "')]");
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Clicks the "Get Directions" button for the given city's store.
     *
     * @param city the store city (e.g. "New Delhi").
     * @return this page for method chaining.
     */
    public TimepiecesHousePage clickGetDirections(String city) {
        log.info("Clicking 'Get Directions' for the {} store.", city);
        clickElement(storeCardButton(city, "Get Directions"));
        return this;
    }

    /**
     * B.2 Clicks the "Share Location" button for the given city's store.
     *
     * @param city the store city (e.g. "Gurugram").
     * @return this page for method chaining.
     */
    public TimepiecesHousePage clickShareLocation(String city) {
        log.info("Clicking 'Share Location' for the {} store.", city);
        clickElement(storeCardButton(city, "Share Location"));
        return this;
    }

    /**
     * B.3 Toggles the event video's audio (Unmute &harr; Mute).
     *
     * @return this page for method chaining.
     */
    public TimepiecesHousePage toggleEventAudio() {
        log.info("Toggling the event video audio.");
        clickElement(eventAudioToggle);
        return this;
    }

    /**
     * B.4 Returns the address text for the given city's store.
     *
     * @param city the store city.
     * @return the trimmed store address text.
     */
    public String getStoreAddress(String city) {
        return getText(storeAddress(city));
    }

    /**
     * B.5 Returns the main page heading text (observed: "Timepieces Experience
     * centers").
     *
     * @return the trimmed page heading text.
     */
    public String getPageHeadingText() {
        return getText(pageHeading);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the browser title matches the expected {@code PAGE_TITLE}.
     *
     * @return true if the document title contains the expected page title.
     */
    public boolean isPageTitleCorrect() {
        String actual = driver.getTitle();
        return actual != null && actual.contains(PAGE_TITLE);
    }

    /**
     * V.2 Verifies the main page heading is displayed.
     *
     * @return true if the page heading is displayed.
     */
    public boolean isPageHeadingDisplayed() {
        return isDisplayed(pageHeading);
    }

    /**
     * V.3 Verifies the store card for the given city is displayed.
     *
     * @param city the store city (e.g. "New Delhi" / "Gurugram").
     * @return true if the store card is displayed.
     */
    public boolean isStoreCardDisplayed(String city) {
        return isDisplayed(storeCard(city));
    }

    /**
     * V.4 Verifies the event promo video is displayed.
     *
     * @return true if the event video is displayed.
     */
    public boolean isEventVideoDisplayed() {
        return isDisplayed(eventVideo);
    }

    /**
     * V.5 Verifies the event section header ("Timepieces House") is displayed.
     *
     * @return true if the event section header is displayed.
     */
    public boolean isEventSectionDisplayed() {
        return isDisplayed(eventSectionHeader);
    }
}
