package com.timepieces.automation.pages.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;
import com.timepieces.automation.pages.web.products.upcomingDrops.AstoniaDropPage;

import lombok.extern.log4j.Log4j2;

/**
 * This is the Page Object Repository of all the WebElements in the Timepieces
 * HomePage. The Homepage in production: https://www.timepieces-d2c.example/,
 * The Homepage in staging: https://staging.timepieces-d2c.example/
 * 
 * The homepage is filled with videos, animations, and modals to entice the
 * users with a premium experience. The hero section of the homepage acts as
 * the landing page to generate excitement and funnel conversion of the
 * upcoming timepiece drop. As this is a D2C business, this is the primary
 * conversion funnel.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (HomePage) to allow
 * method chaining in step definitions. The chained step returns
 * the next page object once navigation is confirmed (for all the linked pages).
 * 
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of HomePage"
* @since 2026-07-03
 */

@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public class HomePage extends BasePage<HomePage> {

    // ==========================================
    // Constructor
    // ==========================================
    public HomePage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Homepage the full page container
    private final By homepageFullPageContaniner = By.cssSelector("main[data-testid='home-main']");

    // Homepage Hero Section Container
    private final By homepageHeroSectionContainer = By.cssSelector("section[data-testid='home-hero']");

    // Use this to locate the homepage
    // Timepieces Homepage Hero Section
    // The overlay is a <div>, NOT a <section>. Verified against the rendered production
    // DOM on 2026-07-17: section[data-testid='home-hero-overlay'] matches 0 nodes, while
    // div[data-testid='home-hero-overlay'] matches 1 and is displayed. The tag-qualified
    // selector is what made "the hero overlay should be displayed" fail — the element was
    // there all along. Tag names are the most brittle half of a testid selector; the
    // testid is the contract, the tag is an implementation detail that can change with a
    // refactor. Matching on the testid alone is the safer form.
    private final By heroSection = By.cssSelector("[data-testid='home-hero-overlay']");

    // Hero mute/un-mute toggle for the background showreel video.
    private final By muteButton_In_HeroSection = By.cssSelector("button[data-testid='home-hero-mute-toggle']");

    // Hero "Discover" CTA — navigates to the current Astonia drop page.
    private final By redirectButton_To_AstoniaDropPage = By.cssSelector("button[data-testid='home-hero-discover-cta']");

    // --- Hero extras (2026-07-16 scrape; all real testids) ---
    // The hero background showreel videos (desktop/mobile variants).
    private final By heroVideoDesktop = By.cssSelector("video[data-testid='home-hero-video-desktop']");
    private final By heroVideoMobile = By.cssSelector("video[data-testid='home-hero-video-mobile']");

    // --- "Two Stories, One Philosophy" section (release preview cards) ---
    private final By twoStoriesSection = By.cssSelector("div[data-testid='home-two-stories']");
    // Tag-free on purpose. Probed headless (1904x985) on 2026-07-17:
    // p[data-testid='home-two-stories-title'] matched ZERO nodes while the section and
    // both story cards were displayed — the title element is not a <p> at that viewport.
    // Pinning the tag is what broke this; the testid is the contract the app owns, the
    // tag is an implementation detail that changes with the responsive variant.
    private final By twoStoriesTitle = By.cssSelector("[data-testid='home-two-stories-title']");
    // The Astonia release card (→ /new-release/astonia) + its arrow CTA.
    private final By astoniaStoryCard = By.cssSelector("a[data-testid='home-discover-astonia']");
    private final By astoniaStoryCta = By.cssSelector("button[data-testid='home-discover-astonia-cta']");
    // The Manifesta release card (→ /new-release/manifesta) + its LOCKED badge ("SEP, 2026").
    private final By manifestaStoryCard = By.cssSelector("a[data-testid='home-discover-manifesta']");
    private final By manifestaLockedBadge = By.cssSelector("div[data-testid='home-discover-manifesta-locked']");

    // --- Brand story / parallax quote section (desktop + mobile variants) ---
    private final By parallaxDesktop = By.cssSelector("section[data-testid='home-parallax-desktop']");
    private final By parallaxMobile = By.cssSelector("section[data-testid='home-parallax-mobile']");
    // The main brand quote inside the viewport-appropriate parallax wrap.
    private final By brandQuote = By.cssSelector(
            "section[data-testid='home-parallax-desktop'] h2, section[data-testid='home-parallax-mobile'] h2"); // ROLLOUT-GAP: structural

    // --- Friends & Partners section ---
    private final By friendsPartnersSection = By.cssSelector("section[data-testid='home-friends-partners']");
    private final By friendsPartnersTitle = By.cssSelector("h2[data-testid='home-friends-partners-title']");
    private final By friendsPartnersViewAll = By.cssSelector("a[data-testid='home-friends-partners-view-all']");
    private final By partnerCards = By.cssSelector("a[data-testid='home-partner-card']");
    // Scroll-next arrow (no prev control exists — the carousel scrolls forward only).
    private final By friendsPartnersNext = By.cssSelector("button[data-testid='home-friends-partners-carousel-next']");

    // --- "Last Release" product slider ---
    private final By productSliderSection = By.cssSelector("section[data-testid='home-product-slider']");
    private final By productSliderTitle = By.cssSelector("h2[data-testid='home-product-slider-title']");
    private final By productSliderSubtitle = By.cssSelector("p[data-testid='home-product-slider-subtitle']");
    private final By productSliderViewAll = By.cssSelector("a[data-testid='home-product-slider-view-all']");
    private final By productSliderCards = By.cssSelector("a[data-testid='home-product-slider-card']");
    private final By productSliderNext = By.cssSelector("button[data-testid='home-product-slider-carousel-next']");

    // --- "Crafted With Intent" scroll-sequence section (desktop + mobile) ---
    private final By imageSequenceDesktop = By.cssSelector("section[data-testid='home-image-sequence-desktop']");
    private final By imageSequenceMobile = By.cssSelector("section[data-testid='home-image-sequence-mobile']");
    private final By imageSequenceCanvas = By.cssSelector(
            "section[data-testid='home-image-sequence-desktop'] canvas, section[data-testid='home-image-sequence-mobile'] canvas"); // ROLLOUT-GAP: structural

    // --- "FEATURED IN" USP marquee ---
    private final By uspSection = By.cssSelector("section[data-testid='home-usp']");
    private final By uspMarquee = By.cssSelector("div[data-testid='home-usp-marquee']");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Waits until the homepage has rendered, using the root {@code <main>}
     * container as the
     * readiness signal. Use as a navigation guard after landing on the homepage
     * before asserting
     * on any child section.
     *
     * @return this HomePage for method chaining.
     */
    public HomePage waitUntilLoaded() {
        log.info("Waiting for the homepage root container to render.");
        waitForVisibility(homepageFullPageContaniner);
        log.info("Homepage root container is visible.");
        return this;
    }

    /**
     * B.2 Toggles the hero showreel mute button (mute ↔ un-mute).
     *
     * @return this HomePage for method chaining.
     */
    public HomePage toggleHeroMute() {
        log.info("Toggling the hero section mute button.");
        clickElement(muteButton_In_HeroSection);
        return this;
    }

    /**
     * B.3 Clicks the hero "Discover" CTA, which redirects to the current Astonia
     * drop page.
     * Returns an {@link AstoniaDropPage} so callers can continue the flow fluently.
     *
     * @return a new AstoniaDropPage for the destination view.
     */
    public AstoniaDropPage clickDiscoverCta() {
        log.info("Clicking the hero 'Discover' CTA to navigate to the Astonia drop page.");
        clickElement(redirectButton_To_AstoniaDropPage);
        return new AstoniaDropPage(driver);
    }

    /**
     * B.4 Clicks the "Two Stories" Astonia release card (→ the Astonia drop page).
     *
     * @return a new AstoniaDropPage for the destination view.
     */
    public AstoniaDropPage clickAstoniaStoryCard() {
        log.info("Clicking the Two-Stories Astonia release card.");
        clickElement(astoniaStoryCard);
        return new AstoniaDropPage(driver);
    }

    /**
     * B.5 Clicks the "Two Stories" Manifesta release card (→ the Manifesta drop
     * page; the card carries a LOCKED badge until the drop opens).
     *
     * @return this HomePage for method chaining.
     */
    public HomePage clickManifestaStoryCard() {
        log.info("Clicking the Two-Stories Manifesta release card.");
        clickElement(manifestaStoryCard);
        return this;
    }

    /**
     * B.6 Returns the Manifesta card's LOCKED badge text (e.g. "SEP, 2026").
     *
     * @return the trimmed locked-badge text.
     */
    public String getManifestaLockedText() {
        return getText(manifestaLockedBadge);
    }

    /**
     * B.7 Returns the brand-story quote text from the viewport-appropriate
     * parallax section.
     *
     * @return the trimmed brand quote.
     */
    public String getBrandQuoteText() {
        return getText(brandQuote);
    }

    /**
     * B.8 Clicks the Friends & Partners "View all" CTA (→ /friends-and-partners).
     *
     * @return this HomePage for method chaining.
     */
    public HomePage clickFriendsPartnersViewAll() {
        log.info("Clicking the Friends & Partners 'View all' CTA.");
        clickElement(friendsPartnersViewAll);
        return this;
    }

    /**
     * B.9 Advances the Friends & Partners carousel (forward-only — no prev
     * control exists).
     *
     * @return this HomePage for method chaining.
     */
    public HomePage advanceFriendsPartnersCarousel() {
        log.info("Advancing the Friends & Partners carousel.");
        clickElement(friendsPartnersNext);
        return this;
    }

    /**
     * B.10 Clicks the "Last Release" slider's "View all" CTA
     * (→ /collections/timepieces).
     *
     * @return this HomePage for method chaining.
     */
    public HomePage clickLastReleaseViewAll() {
        log.info("Clicking the Last Release 'View all' CTA.");
        clickElement(productSliderViewAll);
        return this;
    }

    /**
     * B.11 Advances the "Last Release" product slider (forward-only).
     *
     * @return this HomePage for method chaining.
     */
    public HomePage advanceLastReleaseSlider() {
        log.info("Advancing the Last Release product slider.");
        clickElement(productSliderNext);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /*
     * These return boolean so step definitions can pass them to assertion libraries
     * (AssertJ/TestNG) without the page object depending on any assertion
     * framework.
     */

    /**
     * V.1 Verifies the homepage root container is displayed — a reliable "page
     * loaded" check.
     *
     * @return true if the homepage root container is displayed, false otherwise.
     */
    public boolean isHomePageLoaded() {
        boolean displayed = isDisplayed(homepageFullPageContaniner);
        log.info("Homepage loaded (root container) check: {}", displayed);
        return displayed;
    }

    /**
     * V.2 Verifies the homepage hero section is displayed.
     *
     * @return true if the hero section is displayed, false otherwise.
     */
    public boolean isHeroSectionDisplayed() {
        boolean displayed = isDisplayed(homepageHeroSectionContainer);
        log.info("Homepage hero section displayed check: {}", displayed);
        return displayed;
    }

    /**
     * V.3 Verifies the hero overlay layer (headline/CTA band) is displayed.
     *
     * @return true if the hero overlay is displayed, false otherwise.
     */
    public boolean isHeroOverlayDisplayed() {
        boolean displayed = isDisplayed(heroSection);
        log.info("Homepage hero overlay displayed check: {}", displayed);
        return displayed;
    }

    /**
     * V.4 Verifies the hero background showreel video (viewport-appropriate
     * variant) is present.
     *
     * @return true if the hero video is present.
     */
    public boolean isHeroVideoPresent() {
        By variant = isMobileViewport() ? heroVideoMobile : heroVideoDesktop;
        return !driver.findElements(variant).isEmpty();
    }

    /**
     * V.5 Verifies the "Two Stories, One Philosophy" section is displayed with
     * both release cards.
     *
     * @return true if the section, title and both cards are displayed.
     */
    public boolean isTwoStoriesSectionDisplayed() {
        // Scroll first, then wait. This section sits ~2000px below the fold and the
        // storefront reveals lower sections on scroll, so asserting from the top of the
        // page is a race: it passed on one run and failed on the next against an
        // unchanged site. Probing production on 2026-07-17 found the section, its title
        // and both cards all present and displayed — the section was never the problem,
        // the test just never looked at it properly.
        scrollIntoView(twoStoriesSection);
        return isEventuallyDisplayed(twoStoriesSection)
                && isEventuallyDisplayed(twoStoriesTitle)
                && !driver.findElements(astoniaStoryCard).isEmpty()
                && !driver.findElements(manifestaStoryCard).isEmpty();
    }

    /**
     * V.6 Verifies the Astonia story card carries its arrow CTA.
     *
     * @return true if the Astonia arrow CTA is present.
     */
    public boolean isAstoniaStoryCtaPresent() {
        return !driver.findElements(astoniaStoryCta).isEmpty();
    }

    /**
     * V.7 Reports whether the Manifesta story card is LOCKED (pre-drop badge
     * displayed).
     *
     * @return true if the locked badge is present.
     */
    public boolean isManifestaStoryLocked() {
        return !driver.findElements(manifestaLockedBadge).isEmpty();
    }

    /**
     * V.8 Verifies the brand-story parallax section is present
     * (viewport-appropriate variant).
     *
     * @return true if the parallax section is present.
     */
    public boolean isBrandStoryDisplayed() {
        By variant = isMobileViewport() ? parallaxMobile : parallaxDesktop;
        return !driver.findElements(variant).isEmpty();
    }

    /**
     * V.9 Verifies the Friends & Partners section is displayed (title + View all
     * + forward arrow).
     *
     * @return true if the section chrome is displayed.
     */
    public boolean isFriendsPartnersSectionDisplayed() {
        return isDisplayed(friendsPartnersSection)
                && isDisplayed(friendsPartnersTitle)
                && !driver.findElements(friendsPartnersViewAll).isEmpty()
                && !driver.findElements(friendsPartnersNext).isEmpty();
    }

    /**
     * V.10 Counts the partner cards in the Friends & Partners carousel.
     *
     * @return the number of partner cards.
     */
    public int getPartnerCardCount() {
        return driver.findElements(partnerCards).size();
    }

    /**
     * V.11 Verifies every partner card links to a partner profile
     * ({@code /friends-and-partners/<handle>}), and at least one exists.
     *
     * @return true if all partner cards link to profiles.
     */
    public boolean allPartnerCardsLinkToProfiles() {
        var cards = driver.findElements(partnerCards);
        if (cards.isEmpty()) {
            return false;
        }
        return cards.stream()
                .map(e -> e.getAttribute("href"))
                .allMatch(h -> h != null && h.contains("/friends-and-partners/"));
    }

    /**
     * V.12 Verifies the "Last Release" slider section is displayed (title +
     * subtitle + View all + forward arrow).
     *
     * @return true if the slider chrome is displayed.
     */
    public boolean isLastReleaseSliderDisplayed() {
        return isDisplayed(productSliderSection)
                && isDisplayed(productSliderTitle)
                && !driver.findElements(productSliderSubtitle).isEmpty()
                && !driver.findElements(productSliderViewAll).isEmpty()
                && !driver.findElements(productSliderNext).isEmpty();
    }

    /**
     * V.13 Counts the product cards in the "Last Release" slider.
     *
     * @return the number of product cards.
     */
    public int getLastReleaseCardCount() {
        return driver.findElements(productSliderCards).size();
    }

    /**
     * V.14 Verifies every "Last Release" card links to a timepiece PDP
     * ({@code /products/<handle>}), and at least one exists.
     *
     * @return true if all slider cards link to PDPs.
     */
    public boolean allLastReleaseCardsLinkToPdps() {
        var cards = driver.findElements(productSliderCards);
        if (cards.isEmpty()) {
            return false;
        }
        return cards.stream()
                .map(e -> e.getAttribute("href"))
                .allMatch(h -> h != null && h.contains("/products/"));
    }

    /**
     * V.15 Verifies the "Crafted With Intent" scroll-sequence section is present
     * (viewport-appropriate variant) with its frame canvas.
     *
     * @return true if the sequence section + canvas are present.
     */
    public boolean isCraftedWithIntentDisplayed() {
        By variant = isMobileViewport() ? imageSequenceMobile : imageSequenceDesktop;
        return !driver.findElements(variant).isEmpty()
                && !driver.findElements(imageSequenceCanvas).isEmpty();
    }

    /**
     * V.16 Verifies the "FEATURED IN" USP marquee is present.
     *
     * @return true if the USP section + marquee track are present.
     */
    public boolean isUspMarqueeDisplayed() {
        return !driver.findElements(uspSection).isEmpty()
                && !driver.findElements(uspMarquee).isEmpty();
    }

}
