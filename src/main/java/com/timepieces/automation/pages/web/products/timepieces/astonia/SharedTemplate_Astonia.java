package com.timepieces.automation.pages.web.products.timepieces.astonia;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The Shared Template page exists solely to be inherited by the Astonia
 * timepiece PDP pages as most of the pages share the same design templates.
 * For those with a unique or missing feature, the individual pages will
 * have the feature but will have the same methods with the same name to
 * allow for method chaining in step definitions.
 *
 * <p>
 * LOCATOR SOURCE / data-testid ROLLOUT GAP: the real selectors below were
 * captured from a live production scrape (2026-07-10, see
 * {@code docs/scrape-artifacts/phase0/}). The Astonia PDP core elements do NOT
 * yet carry {@code data-testid} attributes, so the mappings fall back (per the
 * framework's locator-fallback strategy) to aria-labels, then semantic/utility
 * classes, then text anchors. Every such locator is tagged
 * {@code // ROLLOUT-GAP} and should be swapped for a {@code data-testid} once the
 * site's rollout reaches the PDP. Only the shared site chrome (header/footer)
 * carries real {@code data-testid}s and is already mapped in
 * {@code HeaderComponent}/{@code FooterComponent}.
 *
 * FLUENT INTERFACE: All methods that remain on this page
 * return {@code this} (SharedTemplate_Astonia) to allow method chaining in
 * step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_Astonia"
* @since 2026-07-03
 */

@Log4j2
public abstract class SharedTemplate_Astonia<SELF extends SharedTemplate_Astonia<SELF>> extends StoreFrontBasePage<SELF> {

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_Astonia(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // --- Core PDP identity (ROLLOUT-GAP: no data-testid yet) ---
    // Product title <h1> uses a Tailwind arbitrary-value class `text-[32px]`.
    private final By productTitle = By.cssSelector("h1.text-\\[32px\\]"); // ROLLOUT-GAP: semantic-class
    private final By productPrice = By.cssSelector(".product-price span"); // ROLLOUT-GAP: semantic-class
    private final By imageGalleryContainer = By.cssSelector("div.w-full.aspect-square.bg-white"); // ROLLOUT-GAP: semantic-class

    // Primary call-to-action in the sticky sidebar. Products are currently 100/100
    // allocated, so this renders DISABLED with the label "Interest submitted".
    // The wishlist button is excluded by its title attribute so this resolves to
    // the add-to-cart / interest CTA whether it is enabled or disabled.
    private final By primaryCtaButton = By
            .cssSelector(".desktop-sticky-sidebar button:not([title='Add to wishlist']):not([title='Remove from wishlist'])"); // ROLLOUT-GAP: structural

    // Wishlist toggle — title flips between the two states on click.
    private final By wishlistButton = By.cssSelector(".desktop-sticky-sidebar button[title='Add to wishlist']"); // ROLLOUT-GAP: aria/title
    private final By wishlistButtonActive = By.cssSelector(".desktop-sticky-sidebar button[title='Remove from wishlist']"); // ROLLOUT-GAP: aria/title

    // --- "Loom" light/night (lume) image toggle ---
    private final By lumeToggleButton = By.cssSelector("button[aria-label='Show lume view']"); // ROLLOUT-GAP: aria

    // --- 3D viewer (present on every Astonia variant EXCEPT Trophy Gold) ---
    private final By threeDViewerToggle = By.cssSelector("button[aria-label='Open 3D View']"); // ROLLOUT-GAP: aria
    private final By threeDViewerCanvas = By.cssSelector("canvas[aria-label='Astonia']"); // ROLLOUT-GAP: aria
    private final By threeDViewerCloseButton = By.cssSelector("button[aria-label='Close 3D view']"); // ROLLOUT-GAP: aria

    // --- Sibling-variant navigation cards (jump between Astonia variants) ---
    private final By siblingVariantContainer = By.cssSelector("div.hidden.md\\:flex.gap-3.flex-wrap"); // ROLLOUT-GAP: semantic-class
    private final By siblingVariantLinks = By.cssSelector("div.hidden.md\\:flex.gap-3.flex-wrap a"); // ROLLOUT-GAP: semantic-class

    // --- Specification / details accordion (Case / Strap / Dial / Movement) ---
    private final By specificationItems = By.cssSelector("div.specification-item"); // ROLLOUT-GAP: semantic-class
    private final By specificationToggles = By.cssSelector("button[aria-controls^='spec-content-']"); // ROLLOUT-GAP: aria

    // --- Extras ---
    private final By seeAllOwnersButton = By.xpath("//button[contains(normalize-space(),'See all Astonia owners')]"); // ROLLOUT-GAP: text-anchor

    // Dynamic locator templates (consumed by formatLocator / helper builders).
    private final By specificationToggleTemplate = By.xpath("//button[@aria-controls='spec-content-%s']"); // ROLLOUT-GAP: aria
    private final By faqQuestionTemplate = By.xpath("//button[.//h3[contains(normalize-space(),'%s')]]"); // ROLLOUT-GAP: text-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * P.1 Returns {@code this} typed as the concrete subclass ({@code SELF}) so
     * inherited business methods can stay fluent without every Astonia variant
     * re-declaring them. Safe by the CRTP contract — each subclass binds
     * {@code SELF} to itself.
     *
     * @return this page instance, typed as the concrete subclass.
     */
    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }

    /**
     * P.2 Builds a locator for a single sibling-variant navigation card by the
     * product handle it links to (e.g. {@code astonia-trophy-gold}). Handles are
     * stable across rebuilds, unlike the surrounding utility classes.
     *
     * @param handle the target product handle the sibling card links to.
     * @return a By locator for the sibling card anchor.
     */
    private By siblingVariantLink(String handle) {
        return By.cssSelector("a[href*='" + handle + "']");
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Toggles the wishlist state for this product. Clicking flips the button
     * title between "Add to wishlist" and "Remove from wishlist" and swaps the
     * heart icon fill.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickWishlist() {
        log.info("Toggling wishlist state on the Astonia PDP.");
        clickElement(wishlistButton);
        return self();
    }

    /**
     * B.2 Clicks the "Show lume view" toggle, which overlays the night/lume image
     * of the watch over the standard product image.
     *
     * @return this page instance for method chaining.
     */
    public SELF showLumeView() {
        log.info("Activating the lume (light/night) image toggle.");
        clickElement(lumeToggleButton);
        return self();
    }

    /**
     * B.3 Opens the 3D viewer canvas from the top-most product image.
     * <p>
     * NOTE: absent on Astonia Trophy Gold — calling this there will not find the
     * toggle. Guard with {@link #has3DViewer()} in shared flows.
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
     * B.4 Closes the 3D viewer canvas.
     *
     * @return this page instance for method chaining.
     */
    public SELF close3DViewer() {
        log.info("Closing the 3D viewer.");
        clickElement(threeDViewerCloseButton);
        waitForInvisibility(threeDViewerCanvas);
        return self();
    }

    /**
     * B.5 Expands a specification accordion section by its name (one of
     * {@code Case}, {@code Strap}, {@code Dial}, {@code Movement}).
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
     * B.6 Expands a FAQ item whose question header contains the given text.
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
     * B.7 Clicks the "See all Astonia owners" button.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickSeeAllOwners() {
        log.info("Clicking 'See all Astonia owners'.");
        clickElement(seeAllOwnersButton);
        return self();
    }

    /**
     * B.8 Navigates to a sibling Astonia variant via its in-page navigation card
     * (e.g. jump from Dusk Gold to {@code astonia-trophy-gold}) without returning
     * to the collection page.
     * <p>
     * Returns {@code void}: the caller should instantiate the concrete target page
     * object (e.g. {@code new AstoniaTrophyGold(driver)}) to continue asserting,
     * since the destination type differs from the current page type.
     *
     * @param targetHandle the product handle of the sibling variant to open.
     */
    public void navigateToSiblingVariant(String targetHandle) {
        log.info("Navigating to sibling Astonia variant: '{}'.", targetHandle);
        clickElement(siblingVariantLink(targetHandle));
    }

    /**
     * B.9 Returns the product title text as displayed on the PDP (observed as the
     * family name, e.g. "Astonia").
     *
     * @return the trimmed product title text.
     */
    public String getProductTitleText() {
        return getText(productTitle);
    }

    /**
     * B.10 Returns the displayed price text (e.g. "&#8377;59,000").
     *
     * @return the trimmed price text.
     */
    public String getPriceText() {
        return getText(productPrice);
    }

    /**
     * B.11 Returns the primary CTA button label. Currently observed as
     * "Interest submitted" because every Astonia variant is fully allocated
     * (100/100 PCS) on production.
     *
     * @return the trimmed CTA button text.
     */
    public String getPrimaryCtaText() {
        return getText(primaryCtaButton);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Reports whether this variant exposes the 3D viewer toggle. Overridden to
     * {@code false} by {@code AstoniaTrophyGold}, which has no 3D viewer.
     *
     * @return true if the 3D viewer is expected on this variant.
     */
    public boolean has3DViewer() {
        return true;
    }

    /**
     * V.2 Verifies the current browser title matches this variant's expected
     * {@code PAGE_TITLE} (each subclass supplies it via {@link #getPageTitle()}).
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
     * V.3 Verifies the product title element is displayed.
     *
     * @return true if the product title is displayed.
     */
    public boolean isProductTitleDisplayed() {
        return isDisplayed(productTitle);
    }

    /**
     * V.4 Verifies the product price element is displayed.
     *
     * @return true if the price is displayed.
     */
    public boolean isPriceDisplayed() {
        return isDisplayed(productPrice);
    }

    /**
     * V.5 Verifies the primary image gallery container is displayed.
     *
     * @return true if the image gallery is displayed.
     */
    public boolean isImageGalleryDisplayed() {
        return isDisplayed(imageGalleryContainer);
    }

    /**
     * V.6 Verifies the primary CTA (add-to-cart / "Interest submitted") button is
     * present on the page.
     *
     * @return true if the primary CTA button is displayed.
     */
    public boolean isPrimaryCtaDisplayed() {
        return isDisplayed(primaryCtaButton);
    }

    /**
     * V.7 Verifies the product is in the sold-out "Interest submitted" state — the
     * primary CTA is disabled and labelled accordingly. Uses a no-wait
     * {@code findElements} check so it is fast when the element is absent.
     *
     * @return true if the CTA is disabled and reads "Interest submitted".
     */
    public boolean isInterestSubmittedState() {
        return driver.findElements(primaryCtaButton).stream()
                .findFirst()
                .map(btn -> {
                    String text = btn.getText() == null ? "" : btn.getText().trim();
                    boolean disabled = btn.getAttribute("disabled") != null || !btn.isEnabled();
                    return disabled && text.toLowerCase().contains("interest");
                })
                .orElse(false);
    }

    /**
     * V.8 Verifies the wishlist button is displayed (in either add or remove
     * state).
     *
     * @return true if a wishlist toggle is displayed.
     */
    public boolean isWishlistButtonDisplayed() {
        return isDisplayed(wishlistButton) || isDisplayed(wishlistButtonActive);
    }

    /**
     * V.9 Verifies the wishlist is currently in the ACTIVE ("Remove from wishlist")
     * state — used to confirm a preceding {@link #clickWishlist()} took effect.
     * No-wait check.
     *
     * @return true if the product is wishlisted.
     */
    public boolean isWishlistActive() {
        return !driver.findElements(wishlistButtonActive).isEmpty();
    }

    /**
     * V.10 Verifies the lume (light/night) toggle button is displayed.
     *
     * @return true if the lume toggle is displayed.
     */
    public boolean isLumeToggleDisplayed() {
        return isDisplayed(lumeToggleButton);
    }

    /**
     * V.11 Verifies the 3D viewer toggle is displayed. No-wait check so it returns
     * promptly on Trophy Gold, which does not render it.
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
     * V.14 Counts the sibling-variant navigation cards shown (observed: 4).
     *
     * @return the number of sibling-variant cards, or 0 if none are present.
     */
    public int getSiblingVariantCount() {
        List<org.openqa.selenium.WebElement> links = driver.findElements(siblingVariantLinks);
        log.info("Sibling-variant cards found: {}", links.size());
        return links.size();
    }

    /**
     * V.15 Counts the specification accordion sections (observed: Case, Strap,
     * Dial, Movement).
     *
     * @return the number of specification sections present.
     */
    public int getSpecificationSectionCount() {
        List<org.openqa.selenium.WebElement> items = driver.findElements(specificationToggles);
        if (items.isEmpty()) {
            items = driver.findElements(specificationItems);
        }
        log.info("Specification sections found: {}", items.size());
        return items.size();
    }
}
