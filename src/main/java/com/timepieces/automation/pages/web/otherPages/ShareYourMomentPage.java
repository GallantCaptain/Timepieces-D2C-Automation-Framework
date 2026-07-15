package com.timepieces.automation.pages.web.otherPages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.HeaderOnlyBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The ShareYourMomentsPage: https://www.timepieces-d2c.example/timepieces-wall/share is a
 * page where the customers can share their moments with their Timepieces
 * timepieces and have a chance to be featured on the page. Only genuine
 * customers who have bought the numbered watches will be able to share
 * their testimonials and photos. The testimonials and photos are vetted by
 * the Timepieces team before it goes live on the Timepieces Wall (TimepiecesWallPage).
 *
 * There is a submit your moment button that first checks if the user is logged
 * in. If logged in, then if the user is a registered owner, only then they may
 * be able to proceed and upload their testimonials. There is also a go to the
 * Timepieces Wall link below the button. There is a carousel with images of
 * Timepieces watches worn by different people on the side or top depending on
 * the viewport. This page contains only Header and no footer.
 *
 * <p>
 * SCRAPE NOTE (2026-07-15): the page heading is {@code h1} "Show your Timepieces
 * moment". A logged-out visitor sees the story {@code <textarea>}, the hidden
 * {@code <input type=file>}, a "Go to Timepieces Wall" button and a Google-review
 * cross-sell link, but the primary CTA ("Share your moment") is GATED — clicking
 * it opens a login modal ({@code div.fixed.inset-0.z-50}) headed "Sign in to
 * share your Timepieces moment" with the subtext "Only Timepieces owners can submit
 * reviews and be featured on the Timepieces Wall." and a "Sign in" CTA; the actual
 * "Submit your moment" button is rendered {@code disabled} until authenticated.
 * The watch-photo carousel is a {@code div.carousel-track} that auto-scrolls
 * ({@code pointer-events-none}, no prev/next controls) — horizontal on mobile,
 * vertical on desktop. No element carries a {@code data-testid} (only the shared
 * header does), so every locator below is a fallback tagged {@code // ROLLOUT-GAP}.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (ShareYourMomentsPage) to allow method chaining in step definitions.
 *
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of ShareYourMomentPage"
 */

@Log4j2
public class ShareYourMomentPage extends HeaderOnlyBasePage<ShareYourMomentPage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Page Path Variable
    private static final String PAGE_PATH = "/timepieces-wall/share";

    // The Page Title Variable {Unique or Not}
    private static final String PAGE_TITLE = "Timepieces Watches | Early Access to Limited Edition Pieces";

    // ==========================================
    // Constructor
    // ==========================================
    public ShareYourMomentPage(WebDriver driver) {
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
    // The page heading — "Show your Timepieces moment".
    private final By pageHeading = By.xpath("//h1[contains(normalize-space(),'Show your Timepieces moment')]"); // ROLLOUT-GAP: text-anchor
    // The story textarea (visible pre-auth).
    private final By storyTextarea = By.cssSelector("textarea"); // ROLLOUT-GAP: semantic
    // The hidden file/media upload input (visible pre-auth in DOM).
    private final By fileUploadInput = By.cssSelector("input[type='file']"); // ROLLOUT-GAP: semantic
    // The gated primary CTA that triggers the login modal when logged out.
    private final By shareYourMomentCta = By.xpath("//button[contains(normalize-space(),'Share your moment')]"); // ROLLOUT-GAP: text-anchor
    // The "Submit your moment" button (rendered disabled until authenticated).
    private final By submitMomentButton = By.xpath("//button[contains(normalize-space(),'Submit your moment')]"); // ROLLOUT-GAP: text-anchor
    // The "Go to Timepieces Wall" button (→ /timepieces-wall).
    private final By goToTimepiecesWallButton = By.xpath("//button[contains(normalize-space(),'Go to Timepieces Wall')]"); // ROLLOUT-GAP: text-anchor
    // The Google-review cross-sell link.
    private final By googleReviewLink = By.cssSelector("a[href*='g.page']"); // ROLLOUT-GAP: href-anchor
    // The auto-scrolling watch-photo carousel track (horizontal mobile / vertical desktop).
    private final By carouselTrack = By.cssSelector("div.carousel-track"); // ROLLOUT-GAP: utility-class
    // A single watch photo item within the carousel track.
    private final By carouselItem = By.cssSelector("div.carousel-track div.shrink-0"); // ROLLOUT-GAP: structural

    // --- Login gate modal (shown when a logged-out visitor clicks the CTA) ---
    // The gate modal wrapper.
    private final By gateModal = By.cssSelector("div.fixed.inset-0.z-50"); // ROLLOUT-GAP: utility-class
    // The gate modal heading — "Sign in to share your Timepieces moment".
    private final By gateModalHeading = By.xpath("//h2[contains(normalize-space(),'Sign in to share')]"); // ROLLOUT-GAP: text-anchor
    // The gate modal owner-only message.
    private final By gateModalMessage = By.xpath("//p[contains(normalize-space(),'Only Timepieces owners')]"); // ROLLOUT-GAP: text-anchor
    // The gate modal "Sign in" CTA.
    private final By gateSignInButton = By.xpath("//div[contains(@class,'fixed')]//button[normalize-space()='Sign in']"); // ROLLOUT-GAP: text-anchor
    // The gate modal close (X) button.
    private final By gateCloseButton = By.cssSelector("button.absolute.top-4.right-4"); // ROLLOUT-GAP: utility-class

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Clicks the gated "Share your moment" CTA. When logged out this opens the
     * sign-in gate modal.
     *
     * @return this page instance for method chaining.
     */
    public ShareYourMomentPage clickShareYourMoment() {
        log.info("Clicking the gated 'Share your moment' CTA.");
        clickElement(shareYourMomentCta);
        return this;
    }

    /**
     * B.2 Clicks the "Go to Timepieces Wall" button (navigates to {@code /timepieces-wall}).
     *
     * @return this page instance for method chaining.
     */
    public ShareYourMomentPage clickGoToTimepiecesWall() {
        log.info("Clicking 'Go to Timepieces Wall'.");
        clickElement(goToTimepiecesWallButton);
        return this;
    }

    /**
     * B.3 Clicks the "Sign in" CTA inside the login gate modal.
     *
     * @return this page instance for method chaining.
     */
    public ShareYourMomentPage clickGateSignIn() {
        log.info("Clicking 'Sign in' inside the gate modal.");
        clickElement(gateSignInButton);
        return this;
    }

    /**
     * B.4 Closes the login gate modal via its (X) button.
     *
     * @return this page instance for method chaining.
     */
    public ShareYourMomentPage closeGateModal() {
        log.info("Closing the gate modal.");
        clickElement(gateCloseButton);
        return this;
    }

    /**
     * B.5 Returns the page heading text ("Show your Timepieces moment").
     *
     * @return the trimmed heading text.
     */
    public String getPageHeadingText() {
        return getText(pageHeading);
    }

    /**
     * B.6 Returns the login gate modal heading text.
     *
     * @return the trimmed gate heading text.
     */
    public String getGateHeadingText() {
        return getText(gateModalHeading);
    }

    /**
     * B.7 Returns the login gate modal owner-only message text.
     *
     * @return the trimmed gate message text.
     */
    public String getGateMessageText() {
        return getText(gateModalMessage);
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
     * V.2 Verifies the page heading is displayed.
     *
     * @return true if the heading is displayed.
     */
    public boolean isPageHeadingDisplayed() {
        return isDisplayed(pageHeading);
    }

    /**
     * V.3 Verifies the story {@code <textarea>} is present pre-authentication.
     *
     * @return true if the story textarea is present.
     */
    public boolean isStoryTextareaPresent() {
        return !driver.findElements(storyTextarea).isEmpty();
    }

    /**
     * V.4 Verifies the file/media upload {@code <input>} is present in the DOM.
     *
     * @return true if the file upload input is present.
     */
    public boolean isFileUploadInputPresent() {
        return !driver.findElements(fileUploadInput).isEmpty();
    }

    /**
     * V.5 Verifies the "Submit your moment" button is present, and reports whether
     * it is disabled (the expected logged-out / non-owner state).
     *
     * @return true if the submit button is disabled (or absent), false if enabled.
     */
    public boolean isSubmitMomentDisabled() {
        if (driver.findElements(submitMomentButton).isEmpty()) {
            return true;
        }
        String disabled = getAttribute(submitMomentButton, "disabled");
        boolean enabled = driver.findElement(submitMomentButton).isEnabled();
        return disabled != null || !enabled;
    }

    /**
     * V.6 Verifies the "Go to Timepieces Wall" button is displayed.
     *
     * @return true if the button is displayed.
     */
    public boolean isGoToTimepiecesWallDisplayed() {
        return isDisplayed(goToTimepiecesWallButton);
    }

    /**
     * V.7 Verifies the watch-photo carousel track is displayed.
     *
     * @return true if the carousel track is displayed.
     */
    public boolean isCarouselDisplayed() {
        return isDisplayed(carouselTrack);
    }

    /**
     * V.8 Counts the watch photos currently in the carousel track.
     *
     * @return the number of carousel items.
     */
    public int getCarouselItemCount() {
        return driver.findElements(carouselItem).size();
    }

    /**
     * V.9 Verifies the Google-review cross-sell link is present.
     *
     * @return true if the review link is present.
     */
    public boolean isGoogleReviewLinkPresent() {
        return !driver.findElements(googleReviewLink).isEmpty();
    }

    /**
     * V.10 Verifies the login gate modal is displayed (after clicking the gated
     * CTA while logged out).
     *
     * @return true if the gate modal is displayed.
     */
    public boolean isGateModalDisplayed() {
        return isDisplayed(gateModal);
    }

    /**
     * V.11 Verifies the gate modal "Sign in" CTA is displayed.
     *
     * @return true if the gate sign-in button is displayed.
     */
    public boolean isGateSignInDisplayed() {
        return isDisplayed(gateSignInButton);
    }
}
