package com.timepieces.automation.pages.web.products.timepieces.astoniaSports;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;
import com.timepieces.automation.pages.components.PdpOwnersModalComponent;

import lombok.extern.log4j.Log4j2;

/**
 * The Shared Template page exists solely to be inherited by the Astonia Sports
 * timepiece PDP pages as most of the pages share the same design templates.
 * For those with a unique or missing feature, the individual pages will
 * have the feature but will have the same methods with the same name to
 * allow for method chaining in step definitions.
 *
 * <p>
 * LOCATOR SOURCE / data-testid ROLLOUT GAP: selectors captured from the
 * 2026-07-15 production scrape ({@code docs/scrape-artifacts/phase1/}). Like the
 * Astonia/Monarch/Arvion PDPs, the Astonia Sports PDP core carries NO
 * {@code data-testid}s, so locators fall back to aria-labels / semantic classes /
 * text anchors and are tagged {@code // ROLLOUT-GAP}.
 *
 * <p>
 * ASTONIA SPORTS SHAPE (confirmed by the scrape): identical to Monarch/Arvion —
 * all three variants have the lume toggle AND the 3D viewer (canvas
 * {@code aria-label='Astonia Sports'}); the primary CTA is "Register Interest"
 * and is <b>enabled</b> even when fully allocated; there is NO band-color
 * selector, NO video unmute control, and NO side/top scroll nav. The shared
 * "See all Astonia Sports owners" filter/search modal is modelled by
 * {@link PdpOwnersModalComponent}.
 *
 * FLUENT INTERFACE: All methods that remain on this page
 * return {@code this} (SharedTemplate_AstoniaSports) to allow method chaining
 * in step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_AstoniaSports"
* @since 2026-07-03
 */

@Log4j2
public abstract class SharedTemplate_AstoniaSports<SELF extends SharedTemplate_AstoniaSports<SELF>> extends StoreFrontBasePage<SELF> {

    /** Shared owners filter/search modal, identical across all timepiece PDPs. */
    public final PdpOwnersModalComponent ownersModal;

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_AstoniaSports(WebDriver driver) {
        super(driver);
        this.ownersModal = new PdpOwnersModalComponent(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // --- Core PDP identity (ROLLOUT-GAP: no data-testid) ---
    private final By productTitle = By.cssSelector("h1"); // ROLLOUT-GAP: semantic (price nested — see getProductTitleText)
    private final By productPrice = By.cssSelector("div.product-price span"); // ROLLOUT-GAP: structural
    private final By imageGalleryContainer = By
            .cssSelector("div.desktop-image-gallery, div.product-media-carousel"); // ROLLOUT-GAP: structural

    // Primary CTA — "Register Interest", ENABLED even at FULLY ALLOCATED.
    private final By registerInterestButton = By.xpath(
            "//div[contains(@class,'desktop-sticky-sidebar')]//button[contains(normalize-space(),'Register Interest')]"); // ROLLOUT-GAP: text-anchor
    // Floating Register-Interest bar that appears on scroll (desktop bottom bar).
    private final By floatingRegisterInterestButton = By.xpath(
            "//div[contains(@class,'fixed') and contains(@class,'left-0') and contains(@class,'right-0') "
                    + "and contains(@class,'md:block')]//button[contains(normalize-space(),'Register Interest')]"); // ROLLOUT-GAP: text-anchor

    private final By wishlistButton = By.cssSelector(".desktop-sticky-sidebar button[title='Add to wishlist']"); // ROLLOUT-GAP: aria/title
    private final By wishlistButtonActive = By.cssSelector(".desktop-sticky-sidebar button[title='Remove from wishlist']"); // ROLLOUT-GAP: aria/title

    // --- Lume (light/night) image toggle ---
    private final By lumeToggleButton = By.cssSelector("button[aria-label='Show lume view']"); // ROLLOUT-GAP: aria

    // --- 3D viewer (present on every Astonia Sports variant) ---
    private final By threeDViewerToggle = By.cssSelector("button[aria-label='Open 3D View']"); // ROLLOUT-GAP: aria
    private final By threeDViewerContainer = By.cssSelector("div.fixed.inset-0.z-\\[9999\\]"); // ROLLOUT-GAP: structural
    private final By threeDViewerCanvas = By.cssSelector("canvas[aria-label='Astonia Sports']"); // ROLLOUT-GAP: aria
    private final By threeDViewerCloseButton = By.cssSelector("button[aria-label='Close 3D view']"); // ROLLOUT-GAP: aria

    // --- Background videos (autoplay/muted/loop, no controls) ---
    private final By backgroundVideos = By.cssSelector("video.object-cover"); // ROLLOUT-GAP: semantic-class

    // --- Sibling-variant navigation cards ---
    private final By siblingVariantContainer = By.cssSelector("div.hidden.md\\:flex.gap-3.flex-wrap"); // ROLLOUT-GAP: semantic-class
    private final By siblingVariantLinks = By.cssSelector("div.hidden.md\\:flex.gap-3.flex-wrap a"); // ROLLOUT-GAP: semantic-class

    // --- Specification accordion (Case / Strap / Dial / Movement) ---
    private final By specificationItems = By.cssSelector("div.physical-specification"); // ROLLOUT-GAP: semantic-class
    private final By specificationToggles = By.cssSelector("button[aria-controls^='spec-content-']"); // ROLLOUT-GAP: aria

    // --- Extras ---
    private final By bookConsultationLink = By.cssSelector("a[href*='/consultation']"); // ROLLOUT-GAP: href-anchor

    // Dynamic templates.
    private final By specificationToggleTemplate = By.xpath("//button[@aria-controls='spec-content-%s']"); // ROLLOUT-GAP: aria
    private final By faqQuestionTemplate = By
            .xpath("//button[@aria-expanded][contains(normalize-space(),'%s')]"); // ROLLOUT-GAP: text-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * P.1 Returns {@code this} typed as the concrete subclass ({@code SELF}) so
     * inherited business methods stay fluent (CRTP).
     *
     * @return this page instance, typed as the concrete subclass.
     */
    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }

    /**
     * P.2 Builds a locator for a sibling-variant card by the product handle it
     * links to (e.g. {@code astonia-sports-phantom-black}).
     *
     * @param handle the target product handle.
     * @return a By locator for the sibling card anchor.
     */
    private By siblingVariantLink(String handle) {
        return By.cssSelector("a[href*='" + handle + "']");
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Clicks the primary "Register Interest" CTA in the sticky sidebar.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickRegisterInterest() {
        log.info("Clicking the sticky 'Register Interest' CTA.");
        clickElement(registerInterestButton);
        return self();
    }

    /**
     * B.2 Clicks the floating "Register Interest" bar that appears on scroll.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickFloatingRegisterInterest() {
        log.info("Clicking the floating 'Register Interest' CTA.");
        clickElement(floatingRegisterInterestButton);
        return self();
    }

    /**
     * B.3 Toggles the wishlist state for this product (title flips between
     * "Add to wishlist" and "Remove from wishlist").
     *
     * @return this page instance for method chaining.
     */
    public SELF clickWishlist() {
        log.info("Toggling wishlist state on the Astonia Sports PDP.");
        clickElement(wishlistButton);
        return self();
    }

    /**
     * B.4 Activates the lume (light/night) image toggle.
     *
     * @return this page instance for method chaining.
     */
    public SELF showLumeView() {
        log.info("Activating the lume image toggle.");
        clickElement(lumeToggleButton);
        return self();
    }

    /**
     * B.5 Opens the 3D viewer canvas.
     *
     * @return this page instance for method chaining.
     */
    public SELF open3DViewer() {
        log.info("Opening the 3D viewer.");
        clickElement(threeDViewerToggle);
        waitForVisibility(threeDViewerCanvas);
        return self();
    }

    /**
     * B.6 Closes the 3D viewer canvas.
     *
     * @return this page instance for method chaining.
     */
    public SELF close3DViewer() {
        log.info("Closing the 3D viewer.");
        clickElement(threeDViewerCloseButton);
        waitForInvisibility(threeDViewerContainer);
        return self();
    }

    /**
     * B.7 Expands a specification accordion section by name
     * ({@code Case}/{@code Strap}/{@code Dial}/{@code Movement}).
     *
     * @param sectionName the spec section to expand.
     * @return this page instance for method chaining.
     */
    public SELF expandSpecificationSection(String sectionName) {
        log.info("Expanding specification section: '{}'.", sectionName);
        clickElement(formatLocator(specificationToggleTemplate, sectionName));
        return self();
    }

    /**
     * B.8 Expands a FAQ item whose question header contains the given text.
     *
     * @param questionSubstring a distinguishing substring of the FAQ question.
     * @return this page instance for method chaining.
     */
    public SELF expandFaq(String questionSubstring) {
        log.info("Expanding FAQ item containing: '{}'.", questionSubstring);
        clickElement(formatLocator(faqQuestionTemplate, questionSubstring));
        return self();
    }

    /**
     * B.9 Navigates to a sibling Astonia Sports variant via its in-page card (by
     * handle). Returns {@code void}: the caller should instantiate the concrete
     * target page object to continue asserting.
     *
     * @param targetHandle the product handle of the sibling variant to open.
     */
    public void navigateToSiblingVariant(String targetHandle) {
        log.info("Navigating to sibling Astonia Sports variant: '{}'.", targetHandle);
        clickElement(siblingVariantLink(targetHandle));
    }

    /**
     * B.10 Opens the shared "See all Astonia Sports owners" filter/search modal.
     *
     * @return the opened {@link PdpOwnersModalComponent} for chaining.
     */
    public PdpOwnersModalComponent openOwnersModal() {
        log.info("Opening the owners modal from the Astonia Sports PDP.");
        return ownersModal.open();
    }

    /**
     * B.11 Clicks the "Book Consultation" virtual-consultation link.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickBookConsultation() {
        log.info("Clicking 'Book Consultation'.");
        clickElement(bookConsultationLink);
        return self();
    }

    /**
     * B.12 Returns the product title text with the nested price stripped out
     * (the price span is rendered inside the {@code <h1>}).
     *
     * @return the trimmed product title (e.g. "Astonia Sports").
     */
    public String getProductTitleText() {
        String full = getText(productTitle);
        try {
            String price = getText(productPrice);
            if (price != null && !price.isBlank()) {
                full = full.replace(price, "");
            }
        } catch (Exception ignored) {
            // price not present / not nested — return the raw title
        }
        return full.trim();
    }

    /**
     * B.13 Returns the displayed price text (e.g. "₹71,000").
     *
     * @return the trimmed price text.
     */
    public String getPriceText() {
        return getText(productPrice);
    }

    /**
     * B.14 Returns the primary CTA label (observed: "Register Interest").
     *
     * @return the trimmed CTA button text.
     */
    public String getRegisterInterestText() {
        return getText(registerInterestButton);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the browser title contains this variant's expected
     * {@code PAGE_TITLE} (the variants store the reliable distinguishing prefix).
     *
     * @return true if the document title contains the expected page title.
     */
    public boolean isPageTitleCorrect() {
        String expected = getPageTitle();
        String actual = driver.getTitle();
        boolean match = expected != null && !expected.isBlank() && actual != null && actual.contains(expected);
        log.info("PDP title check — expected contains: '{}', actual: '{}', match: {}", expected, actual, match);
        return match;
    }

    /**
     * V.2 Verifies the product title element is displayed.
     *
     * @return true if the product title is displayed.
     */
    public boolean isProductTitleDisplayed() {
        return isDisplayed(productTitle);
    }

    /**
     * V.3 Verifies the product price element is displayed.
     *
     * @return true if the price is displayed.
     */
    public boolean isPriceDisplayed() {
        return isDisplayed(productPrice);
    }

    /**
     * V.4 Verifies the image gallery container is displayed.
     *
     * @return true if the image gallery is displayed.
     */
    public boolean isImageGalleryDisplayed() {
        return isDisplayed(imageGalleryContainer);
    }

    /**
     * V.5 Verifies the primary "Register Interest" CTA is displayed.
     *
     * @return true if the CTA is displayed.
     */
    public boolean isRegisterInterestDisplayed() {
        return isDisplayed(registerInterestButton);
    }

    /**
     * V.6 Verifies the primary "Register Interest" CTA is enabled (Astonia Sports
     * keeps it clickable even when fully allocated). No-wait check.
     *
     * @return true if the CTA is present and enabled.
     */
    public boolean isRegisterInterestEnabled() {
        return driver.findElements(registerInterestButton).stream()
                .findFirst()
                .map(btn -> btn.isEnabled() && btn.getAttribute("disabled") == null)
                .orElse(false);
    }

    /**
     * V.7 Verifies the floating Register-Interest bar is present (no-wait — it only
     * renders after scroll).
     *
     * @return true if the floating CTA is present in the DOM.
     */
    public boolean isFloatingCtaPresent() {
        return !driver.findElements(floatingRegisterInterestButton).isEmpty();
    }

    /**
     * V.8 Verifies a wishlist toggle is displayed (either state).
     *
     * @return true if a wishlist toggle is displayed.
     */
    public boolean isWishlistButtonDisplayed() {
        return isDisplayed(wishlistButton) || isDisplayed(wishlistButtonActive);
    }

    /**
     * V.9 Verifies the wishlist is in the ACTIVE ("Remove from wishlist") state.
     * No-wait check.
     *
     * @return true if the product is wishlisted.
     */
    public boolean isWishlistActive() {
        return !driver.findElements(wishlistButtonActive).isEmpty();
    }

    /**
     * V.10 Verifies the lume toggle button is displayed.
     *
     * @return true if the lume toggle is displayed.
     */
    public boolean isLumeToggleDisplayed() {
        return isDisplayed(lumeToggleButton);
    }

    /**
     * V.11 Verifies the 3D viewer toggle is present (no-wait).
     *
     * @return true if the 3D viewer toggle is present.
     */
    public boolean is3DViewerToggleDisplayed() {
        return !driver.findElements(threeDViewerToggle).isEmpty();
    }

    /**
     * V.12 Verifies the 3D viewer canvas is displayed (call after
     * {@link #open3DViewer()}).
     *
     * @return true if the 3D canvas is displayed.
     */
    public boolean is3DViewerCanvasDisplayed() {
        return isDisplayed(threeDViewerCanvas);
    }

    /**
     * V.13 Verifies the sibling-variant navigation strip is displayed.
     *
     * @return true if the sibling-variant container is displayed.
     */
    public boolean isSiblingVariantNavigationDisplayed() {
        return isDisplayed(siblingVariantContainer);
    }

    /**
     * V.14 Counts the sibling-variant navigation cards (observed: 3).
     *
     * @return the number of sibling-variant cards.
     */
    public int getSiblingVariantCount() {
        return driver.findElements(siblingVariantLinks).size();
    }

    /**
     * V.15 Counts the specification accordion sections (observed: Case, Strap,
     * Dial, Movement).
     *
     * @return the number of specification sections.
     */
    public int getSpecificationSectionCount() {
        int toggles = driver.findElements(specificationToggles).size();
        return toggles > 0 ? toggles : driver.findElements(specificationItems).size();
    }

    /**
     * V.16 Counts the autoplay background videos on the page (observed: 4,
     * all muted/looping with no controls).
     *
     * @return the number of background videos.
     */
    public int getBackgroundVideoCount() {
        return driver.findElements(backgroundVideos).size();
    }

    /**
     * V.17 Verifies the "Book Consultation" link is displayed.
     *
     * @return true if the consultation link is displayed.
     */
    public boolean isBookConsultationDisplayed() {
        return isDisplayed(bookConsultationLink);
    }

    /**
     * V.18 Verifies the "See all Astonia Sports owners" modal trigger is present.
     *
     * @return true if the owners-modal trigger is displayed.
     */
    public boolean isOwnersModalTriggerDisplayed() {
        return ownersModal.isTriggerDisplayed();
    }
}
