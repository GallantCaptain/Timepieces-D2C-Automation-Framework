package com.timepieces.automation.pages.web.friendsAndPartners;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * PartnerProfilePage — a single eminent-personality profile under
 * {@code /friends-and-partners/<slug>}, reached by clicking a card on
 * {@link FriendsAndPartners}. It is a deliberately "highlighted / standout"
 * layout (distinct from a regular customer profile) with header + footer.
 *
 * <p>
 * SCRAPE NOTE (2026-07-16, slugs {@code kush-maini} + {@code vivek-oberoi}): the
 * document title is {@code <Name> | Friends & Partners | Timepieces Watches}. The
 * hero has the person's name as an {@code h1}, a second {@code h1} tagline
 * "A Timepieces Man", a role/bio {@code <p>}, and a portrait set as a CSS
 * {@code background-image} on the hero wrapper. There is NO "Play Video" button —
 * profiles that feature a video embed an inline, autoplaying, muted, looping
 * YouTube {@code <iframe>} (only some profiles have it). The "numbered watches
 * bought" live in a FIXED sticky bottom bar showing the highlighted watch (link
 * to its PDP + a serial number like {@code #009}); partners who bought multiple
 * watches also get a "See More" button that expands a drawer
 * ({@code max-h-0} → {@code max-h-[60vh]}) listing the rest, each linking to a
 * {@code /products/<handle>} PDP. A "Friends & Partners" link navigates back.
 *
 * <p>
 * The path is per-profile, so this class does NOT bind a {@code PAGE_PATH};
 * callers open a specific profile via {@link #openProfile(String)}.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (PartnerProfilePage)
 * to allow method chaining in step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of PartnerProfilePage"
 * @since 2026-07-16
 */

@Log4j2
public class PartnerProfilePage extends StoreFrontBasePage<PartnerProfilePage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The profile hero tagline shown on every partner profile.
    private static final String TAGLINE = "A Timepieces Man";
    // The path prefix for a partner profile (slug appended by openProfile()).
    private static final String PROFILE_PATH_PREFIX = "/friends-and-partners/";

    // ==========================================
    // Constructor
    // ==========================================
    public PartnerProfilePage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // The tagline heading ("A Timepieces Man").
    private final By taglineHeading = By.xpath("//h1[normalize-space()='A Timepieces Man']"); // ROLLOUT-GAP: text-anchor
    // The person's name heading (the hero h1 that is NOT the tagline).
    private final By profileName = By.xpath("(//h1[normalize-space()!='A Timepieces Man'])[1]"); // ROLLOUT-GAP: structural
    // The portrait hero wrapper (portrait is a CSS background-image on it).
    private final By heroPortrait = By.cssSelector("div.flex.flex-col.justify-end.relative.overflow-hidden"); // ROLLOUT-GAP: utility-class
    // The role/bio paragraph inside the hero.
    private final By roleText = By.cssSelector("div.flex.flex-col.justify-end.relative.overflow-hidden p"); // ROLLOUT-GAP: structural
    // The inline autoplaying YouTube video iframe (only some profiles have one).
    private final By videoIframe = By.cssSelector("iframe[src*='youtube.com/embed']"); // ROLLOUT-GAP: href-anchor
    // The fixed sticky bottom "numbered watches" bar.
    private final By stickyWatchBar = By.cssSelector("div.fixed.left-0.right-0.z-20"); // ROLLOUT-GAP: utility-class
    // The highlighted watch link inside the sticky bar (→ its PDP).
    private final By highlightedWatchLink = By.cssSelector("div.fixed.left-0.right-0.z-20 a[href*='/products/']"); // ROLLOUT-GAP: structural
    // The "See More" button that expands the multiple-watches drawer.
    private final By seeMoreButton = By.xpath("//button[normalize-space()='See More']"); // ROLLOUT-GAP: text-anchor
    // The expandable drawer listing the remaining purchased watches.
    private final By watchesDrawer = By.cssSelector("div.overflow-y-auto.max-h-\\[60vh\\]"); // ROLLOUT-GAP: utility-class
    // The watch links inside the drawer (each → a PDP).
    private final By drawerWatchLinks = By.cssSelector("div.overflow-y-auto.max-h-\\[60vh\\] a[href*='/products/']"); // ROLLOUT-GAP: structural
    // Every product PDP link on the page (sticky bar + drawer combined).
    private final By allProductLinks = By.cssSelector("a[href*='/products/']"); // ROLLOUT-GAP: structural
    // The "Friends & Partners" back navigation link.
    private final By backNavLink = By.xpath("//a[normalize-space()='Friends & Partners']"); // ROLLOUT-GAP: text-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Opens a specific partner profile by its slug (e.g. {@code kush-maini}).
     *
     * @param slug the profile slug (the part after {@code /friends-and-partners/}).
     * @return this page instance for method chaining.
     */
    public PartnerProfilePage openProfile(String slug) {
        String url = getBaseUrl() + PROFILE_PATH_PREFIX + slug;
        log.info("Opening partner profile URL: [{}]", url);
        driver.get(url);
        return this;
    }

    /**
     * B.2 Returns the person's name from the hero heading.
     *
     * @return the trimmed profile name.
     */
    public String getProfileName() {
        return getText(profileName);
    }

    /**
     * B.3 Returns the hero tagline text ("A Timepieces Man").
     *
     * @return the trimmed tagline text.
     */
    public String getTaglineText() {
        return getText(taglineHeading);
    }

    /**
     * B.4 Returns the role/bio text from the hero.
     *
     * @return the trimmed role text.
     */
    public String getRoleText() {
        return getText(roleText);
    }

    /**
     * B.5 Returns the highlighted (sticky-bar) watch's PDP href.
     *
     * @return the product href of the highlighted watch.
     */
    public String getHighlightedWatchHref() {
        return getAttribute(highlightedWatchLink, "href");
    }

    /**
     * B.6 Clicks the "See More" button to expand the multiple-watches drawer.
     *
     * @return this page instance for method chaining.
     */
    public PartnerProfilePage clickSeeMore() {
        log.info("Expanding the 'See More' watches drawer.");
        clickElement(seeMoreButton);
        return this;
    }

    /**
     * B.7 Navigates back to the Friends & Partners landing page.
     *
     * @return this page instance for method chaining.
     */
    public PartnerProfilePage clickBackToFriendsAndPartners() {
        log.info("Navigating back to Friends & Partners.");
        clickElement(backNavLink);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the browser title contains the given person name (the title
     * pattern is {@code <Name> | Friends & Partners | Timepieces Watches}).
     *
     * @param name the person name expected in the title.
     * @return true if the document title contains the name.
     */
    public boolean isTitleForPartner(String name) {
        String actual = driver.getTitle();
        return actual != null && actual.contains(name) && actual.contains("Friends & Partners");
    }

    /**
     * V.2 Verifies the profile name heading is displayed.
     *
     * @return true if the profile name is displayed.
     */
    public boolean isProfileNameDisplayed() {
        return isDisplayed(profileName);
    }

    /**
     * V.3 Verifies the "A Timepieces Man" tagline is displayed.
     *
     * @return true if the tagline is displayed.
     */
    public boolean isTaglineDisplayed() {
        return isDisplayed(taglineHeading);
    }

    /**
     * V.4 Verifies the tagline text equals the expected constant.
     *
     * @return true if the tagline matches "A Timepieces Man".
     */
    public boolean isTaglineCorrect() {
        return TAGLINE.equals(getText(taglineHeading));
    }

    /**
     * V.5 Verifies the portrait hero wrapper is displayed.
     *
     * @return true if the hero portrait is displayed.
     */
    public boolean isHeroPortraitDisplayed() {
        return isDisplayed(heroPortrait);
    }

    /**
     * V.6 Reports whether this profile embeds a video (inline YouTube iframe).
     * Not all profiles have one.
     *
     * @return true if a video iframe is present.
     */
    public boolean isVideoPresent() {
        return !driver.findElements(videoIframe).isEmpty();
    }

    /**
     * V.7 Verifies the fixed sticky "numbered watches" bar is displayed.
     *
     * @return true if the sticky watch bar is displayed.
     */
    public boolean isStickyWatchBarDisplayed() {
        return isDisplayed(stickyWatchBar);
    }

    /**
     * V.8 Reports whether this profile has a "See More" watches drawer (present
     * only for partners who bought multiple watches).
     *
     * @return true if the "See More" button is present.
     */
    public boolean hasSeeMoreDrawer() {
        return !driver.findElements(seeMoreButton).isEmpty();
    }

    /**
     * V.9 Reports whether the watches drawer is currently expanded (its wrapper
     * carries the {@code max-h-[60vh]} class rather than {@code max-h-0}).
     *
     * @return true if the drawer is expanded.
     */
    public boolean isWatchesDrawerExpanded() {
        if (driver.findElements(watchesDrawer).isEmpty()) {
            return false;
        }
        String cls = getAttribute(watchesDrawer, "class");
        return cls != null && cls.contains("max-h-[60vh]");
    }

    /**
     * V.10 Counts the watch links inside the expanded drawer.
     *
     * @return the number of drawer watch links.
     */
    public int getDrawerWatchCount() {
        return driver.findElements(drawerWatchLinks).size();
    }

    /**
     * V.11 Verifies every product link on the page targets a {@code /products/}
     * PDP (the numbered watches all link to product detail pages).
     *
     * @return true if all product links are PDP links (and at least one exists).
     */
    public boolean allWatchLinksArePdps() {
        var links = driver.findElements(allProductLinks);
        if (links.isEmpty()) {
            return false;
        }
        return links.stream()
                .map(e -> e.getAttribute("href"))
                .allMatch(h -> h != null && h.contains("/products/"));
    }
}
