package com.timepieces.automation.pages.web.products.timepieces;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;
import com.timepieces.automation.pages.components.PdpOwnersModalComponent;
import com.timepieces.automation.pages.components.PdpRegisterInterestModalComponent;

import lombok.extern.log4j.Log4j2;

/**
 * SharedTimepiecePdp — the common base for EVERY timepiece PDP family template
 * (Astonia, Monarch, Arvion, Astonia Sports, Auriqua, Manifesta).
 *
 * <p>
 * All six families render the same PDP: product title + nested price, an image
 * gallery, a "Register Interest" CTA (which opens the shared register-interest
 * form) with a floating variant of the same bar, a wishlist toggle, a share
 * button, an optional lume toggle, an optional 3D viewer, a spec accordion,
 * a sibling-variant strip, the "See all {family} owners" modal, a Book
 * Consultation link, FAQ accordions, and autoplay background videos. This base
 * maps all of that ONCE; the per-family templates only supply the small
 * variations via the hooks below.
 *
 * <p>
 * FAMILY HOOKS (override in the family template):
 * <ul>
 * <li>{@link #familyName()} — the family label used for the 3D canvas
 * {@code aria-label} (and log context). REQUIRED.</li>
 * <li>{@link #hasLume()} — false for Manifesta (no lume). Default true.</li>
 * <li>{@link #has3DViewer()} — false for Auriqua (no 3D) and Astonia Trophy Gold.
 * Default true.</li>
 * <li>{@link #productTitleLocator()} / {@link #imageGalleryLocator()} — Astonia
 * (the Phase-0 pilot) uses different markup and overrides these.</li>
 * </ul>
 *
 * <p>
 * LOCATOR SOURCE / data-testid ROLLOUT GAP: selectors captured from the
 * 2026-07-10/15 production scrapes ({@code docs/scrape-artifacts/}). The PDP core
 * carries NO {@code data-testid}s, so locators fall back to aria-labels /
 * semantic classes / text anchors, tagged {@code // ROLLOUT-GAP}. The
 * register-interest form fields and the share button were confirmed by the
 * targeted verify scrape (TASK-PDP-005, {@code verify-register-share.md}).
 *
 * FLUENT INTERFACE: business methods that remain on the page return {@code this}
 * (typed as the concrete subclass via CRTP) for chaining.
 *
 * @param <SELF> the concrete page/component subtype, for fluent return typing.
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTimepiecePdp"
* @since 2026-07-15
 */

@Log4j2
public abstract class SharedTimepiecePdp<SELF extends SharedTimepiecePdp<SELF>> extends StoreFrontBasePage<SELF> {

    /** Shared owners filter/search modal, identical across all timepiece PDPs. */
    public final PdpOwnersModalComponent ownersModal;
    /** Shared "Register your interest" form modal, identical across timepiece PDPs. */
    public final PdpRegisterInterestModalComponent registerInterestModal;

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTimepiecePdp(WebDriver driver) {
        super(driver);
        this.ownersModal = new PdpOwnersModalComponent(driver);
        this.registerInterestModal = new PdpRegisterInterestModalComponent(driver);
    }

    // ==========================================
    // FAMILY HOOKS (overridable per family)
    // ==========================================
    /**
     * The family label used for the 3D canvas {@code aria-label} (e.g. "Monarch")
     * and log context.
     *
     * @return the family display name.
     */
    protected abstract String familyName();

    /**
     * Whether this family has the lume (light/night) image toggle. Manifesta
     * overrides this to false.
     *
     * @return true if the family supports lume.
     */
    public boolean hasLume() {
        return true;
    }

    /**
     * Whether this family exposes the 3D viewer. Auriqua overrides this to false;
     * Astonia Trophy Gold (variant level) also overrides it to false.
     *
     * @return true if the family has a 3D viewer.
     */
    public boolean has3DViewer() {
        return true;
    }

    /**
     * The product-title locator. Default {@code h1}; Astonia overrides it to its
     * Tailwind arbitrary-value class.
     *
     * @return the product-title By locator.
     */
    protected By productTitleLocator() {
        return By.cssSelector("h1"); // ROLLOUT-GAP: semantic (price nested — see getProductTitleText)
    }

    /**
     * The image-gallery container locator. Default is the standard desktop/mobile
     * gallery; Astonia overrides it.
     *
     * @return the image-gallery By locator.
     */
    protected By imageGalleryLocator() {
        return By.cssSelector("div.desktop-image-gallery, div.product-media-carousel"); // ROLLOUT-GAP: structural
    }

    /**
     * The 3D viewer canvas locator, built from {@link #familyName()}
     * (e.g. {@code canvas[aria-label='Monarch']}).
     *
     * @return the 3D canvas By locator.
     */
    protected By threeDViewerCanvasLocator() {
        return By.cssSelector("canvas[aria-label='" + familyName() + "']"); // ROLLOUT-GAP: aria
    }

    // ==========================================
    // LOCATORS (invariant across families)
    // ==========================================
    private final By productPrice = By.cssSelector("div.product-price span"); // ROLLOUT-GAP: structural

    // Primary CTA — "Register Interest" (title-case), enabled even at FULLY
    // ALLOCATED. Present and enabled on ALL six families (the verify scrape
    // confirmed Astonia is NOT sold out — it shows the same enabled CTA).
    private final By registerInterestButton = By.xpath(
            "//div[contains(@class,'desktop-sticky-sidebar')]//button[contains(normalize-space(),'Register Interest')]"); // ROLLOUT-GAP: text-anchor
    private final By floatingRegisterInterestButton = By.xpath(
            "//div[contains(@class,'fixed') and contains(@class,'left-0') and contains(@class,'right-0') "
                    + "and contains(@class,'md:block')]//button[contains(normalize-space(),'Register Interest')]"); // ROLLOUT-GAP: text-anchor

    private final By wishlistButton = By.cssSelector(".desktop-sticky-sidebar button[title='Add to wishlist']"); // ROLLOUT-GAP: aria/title
    private final By wishlistButtonActive = By.cssSelector(".desktop-sticky-sidebar button[title='Remove from wishlist']"); // ROLLOUT-GAP: aria/title

    // Share button — opens the native OS share sheet via navigator.share (only the
    // trigger is automatable; no in-page fallback). Confirmed by the verify scrape.
    private final By sharePdpButton = By.cssSelector("button[aria-label='Share product']"); // ROLLOUT-GAP: aria

    // Lume (light/night) toggle — present on every family EXCEPT Manifesta.
    private final By lumeToggleButton = By.cssSelector("button[aria-label='Show lume view']"); // ROLLOUT-GAP: aria

    // 3D viewer — present on every family EXCEPT Auriqua (and Astonia Trophy Gold).
    private final By threeDViewerToggle = By.cssSelector("button[aria-label='Open 3D View']"); // ROLLOUT-GAP: aria
    private final By threeDViewerContainer = By.cssSelector("div.fixed.inset-0.z-\\[9999\\]"); // ROLLOUT-GAP: structural
    private final By threeDViewerCloseButton = By.cssSelector("button[aria-label='Close 3D view']"); // ROLLOUT-GAP: aria

    private final By backgroundVideos = By.cssSelector("video.object-cover"); // ROLLOUT-GAP: semantic-class

    private final By siblingVariantContainer = By.cssSelector("div.hidden.md\\:flex.gap-3.flex-wrap"); // ROLLOUT-GAP: semantic-class
    private final By siblingVariantLinks = By.cssSelector("div.hidden.md\\:flex.gap-3.flex-wrap a"); // ROLLOUT-GAP: semantic-class

    private final By specificationItems = By.cssSelector("div.physical-specification"); // ROLLOUT-GAP: semantic-class
    private final By specificationToggles = By.cssSelector("button[aria-controls^='spec-content-']"); // ROLLOUT-GAP: aria

    private final By bookConsultationLink = By.cssSelector("a[href*='/consultation']"); // ROLLOUT-GAP: href-anchor

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
     * links to (e.g. {@code monarch-silver-black}).
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
     * B.1 Clicks the primary "Register Interest" CTA in the sticky sidebar (raw
     * click; use {@link #openRegisterInterest()} to get the returned form modal).
     *
     * @return this page instance for method chaining.
     */
    public SELF clickRegisterInterest() {
        log.info("[{}] Clicking the sticky 'Register Interest' CTA.", familyName());
        clickElement(registerInterestButton);
        return self();
    }

    /**
     * B.2 Opens the shared "Register your interest" form modal from this PDP.
     *
     * @return the opened {@link PdpRegisterInterestModalComponent} for chaining.
     */
    public PdpRegisterInterestModalComponent openRegisterInterest() {
        log.info("[{}] Opening the register-interest form.", familyName());
        return registerInterestModal.open();
    }

    /**
     * B.3 Clicks the floating "Register Interest" bar that appears on scroll.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickFloatingRegisterInterest() {
        log.info("[{}] Clicking the floating 'Register Interest' CTA.", familyName());
        clickElement(floatingRegisterInterestButton);
        return self();
    }

    /**
     * B.4 Clicks the share button, which opens the browser/OS share sheet. The
     * native sheet itself cannot be automated further — this only triggers it.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickShare() {
        log.info("[{}] Clicking the PDP share button (opens the native share sheet).", familyName());
        clickElement(sharePdpButton);
        return self();
    }

    /**
     * B.5 Toggles the wishlist state for this product (title flips between
     * "Add to wishlist" and "Remove from wishlist").
     *
     * @return this page instance for method chaining.
     */
    public SELF clickWishlist() {
        log.info("[{}] Toggling wishlist state.", familyName());
        clickElement(wishlistButton);
        return self();
    }

    /**
     * B.6 Activates the lume (light/night) image toggle. Only meaningful when
     * {@link #hasLume()} is true.
     *
     * @return this page instance for method chaining.
     */
    public SELF showLumeView() {
        log.info("[{}] Activating the lume image toggle.", familyName());
        clickElement(lumeToggleButton);
        return self();
    }

    /**
     * B.7 Opens the 3D viewer canvas. Only meaningful when {@link #has3DViewer()}
     * is true.
     *
     * @return this page instance for method chaining.
     */
    public SELF open3DViewer() {
        log.info("[{}] Opening the 3D viewer.", familyName());
        clickElement(threeDViewerToggle);
        waitForVisibility(threeDViewerCanvasLocator());
        return self();
    }

    /**
     * B.8 Closes the 3D viewer canvas.
     *
     * @return this page instance for method chaining.
     */
    public SELF close3DViewer() {
        log.info("[{}] Closing the 3D viewer.", familyName());
        clickElement(threeDViewerCloseButton);
        waitForInvisibility(threeDViewerContainer);
        return self();
    }

    /**
     * B.9 Expands a specification accordion section by name
     * ({@code Case}/{@code Strap}/{@code Dial}/{@code Movement}).
     *
     * @param sectionName the spec section to expand.
     * @return this page instance for method chaining.
     */
    public SELF expandSpecificationSection(String sectionName) {
        log.info("[{}] Expanding specification section: '{}'.", familyName(), sectionName);
        clickElement(formatLocator(specificationToggleTemplate, sectionName));
        return self();
    }

    /**
     * B.10 Expands a FAQ item whose question header contains the given text.
     *
     * @param questionSubstring a distinguishing substring of the FAQ question.
     * @return this page instance for method chaining.
     */
    public SELF expandFaq(String questionSubstring) {
        log.info("[{}] Expanding FAQ item containing: '{}'.", familyName(), questionSubstring);
        clickElement(formatLocator(faqQuestionTemplate, questionSubstring));
        return self();
    }

    /**
     * B.11 Navigates to a sibling variant via its in-page card (by handle).
     * Returns {@code void}: the caller should instantiate the concrete target page
     * object to continue asserting.
     *
     * @param targetHandle the product handle of the sibling variant to open.
     */
    public void navigateToSiblingVariant(String targetHandle) {
        log.info("[{}] Navigating to sibling variant: '{}'.", familyName(), targetHandle);
        clickElement(siblingVariantLink(targetHandle));
    }

    /**
     * B.12 Opens the shared "See all {family} owners" filter/search modal.
     *
     * @return the opened {@link PdpOwnersModalComponent} for chaining.
     */
    public PdpOwnersModalComponent openOwnersModal() {
        log.info("[{}] Opening the owners modal.", familyName());
        return ownersModal.open();
    }

    /**
     * B.13 Clicks the "Book Consultation" virtual-consultation link.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickBookConsultation() {
        log.info("[{}] Clicking 'Book Consultation'.", familyName());
        clickElement(bookConsultationLink);
        return self();
    }

    /**
     * B.14 Returns the product title text with the nested price stripped out
     * (the price span is rendered inside the {@code <h1>}).
     *
     * @return the trimmed product title (e.g. "Monarch").
     */
    public String getProductTitleText() {
        String full = getText(productTitleLocator());
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
     * B.15 Returns the displayed price text (e.g. "₹59,000").
     *
     * @return the trimmed price text.
     */
    public String getPriceText() {
        return getText(productPrice);
    }

    /**
     * B.16 Returns the primary CTA label (observed: "Register Interest" for the
     * active families).
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
     * {@code PAGE_TITLE} (variants supply it via {@link #getPageTitle()}).
     *
     * @return true if the document title contains the expected page title.
     */
    public boolean isPageTitleCorrect() {
        String expected = getPageTitle();
        String actual = driver.getTitle();
        boolean match = expected != null && !expected.isBlank() && actual != null && actual.contains(expected);
        log.info("[{}] PDP title check — expected contains: '{}', actual: '{}', match: {}",
                familyName(), expected, actual, match);
        return match;
    }

    /**
     * V.2 Verifies the product title element is displayed.
     *
     * @return true if the product title is displayed.
     */
    public boolean isProductTitleDisplayed() {
        return isDisplayed(productTitleLocator());
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
        return isDisplayed(imageGalleryLocator());
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
     * V.6 Verifies the primary "Register Interest" CTA is present and enabled. All
     * six families keep it clickable even when fully allocated (the verify scrape
     * confirmed Astonia included). No-wait check.
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
     * V.7 Verifies the share button is displayed.
     *
     * @return true if the share button is displayed.
     */
    public boolean isShareButtonDisplayed() {
        return isDisplayed(sharePdpButton);
    }

    /**
     * V.8 Verifies the floating Register-Interest bar is present (no-wait — it only
     * renders after scroll).
     *
     * @return true if the floating CTA is present in the DOM.
     */
    public boolean isFloatingCtaPresent() {
        return !driver.findElements(floatingRegisterInterestButton).isEmpty();
    }

    /**
     * V.9 Verifies a wishlist toggle is displayed (either state).
     *
     * @return true if a wishlist toggle is displayed.
     */
    public boolean isWishlistButtonDisplayed() {
        return isDisplayed(wishlistButton) || isDisplayed(wishlistButtonActive);
    }

    /**
     * V.10 Verifies the wishlist is in the ACTIVE ("Remove from wishlist") state.
     * No-wait check.
     *
     * @return true if the product is wishlisted.
     */
    public boolean isWishlistActive() {
        return !driver.findElements(wishlistButtonActive).isEmpty();
    }

    /**
     * V.11 Verifies the lume toggle button is present (no-wait). Naturally false
     * for families without lume ({@link #hasLume()} == false).
     *
     * @return true if the lume toggle is present.
     */
    public boolean isLumeToggleDisplayed() {
        return !driver.findElements(lumeToggleButton).isEmpty();
    }

    /**
     * V.12 Verifies the 3D viewer toggle is present (no-wait). Naturally false for
     * families/variants without 3D ({@link #has3DViewer()} == false).
     *
     * @return true if the 3D viewer toggle is present.
     */
    public boolean is3DViewerToggleDisplayed() {
        return !driver.findElements(threeDViewerToggle).isEmpty();
    }

    /**
     * V.13 Verifies the 3D viewer canvas is displayed (call after
     * {@link #open3DViewer()}).
     *
     * @return true if the 3D canvas is displayed.
     */
    public boolean is3DViewerCanvasDisplayed() {
        return isDisplayed(threeDViewerCanvasLocator());
    }

    /**
     * V.14 Verifies the sibling-variant navigation strip is displayed.
     *
     * @return true if the sibling-variant container is displayed.
     */
    public boolean isSiblingVariantNavigationDisplayed() {
        return isDisplayed(siblingVariantContainer);
    }

    /**
     * V.15 Counts the sibling-variant navigation cards.
     *
     * @return the number of sibling-variant cards.
     */
    public int getSiblingVariantCount() {
        return driver.findElements(siblingVariantLinks).size();
    }

    /**
     * V.16 Counts the specification accordion sections (observed: Case, Strap,
     * Dial, Movement).
     *
     * @return the number of specification sections.
     */
    public int getSpecificationSectionCount() {
        int toggles = driver.findElements(specificationToggles).size();
        return toggles > 0 ? toggles : driver.findElements(specificationItems).size();
    }

    /**
     * V.17 Counts the autoplay background videos on the page.
     *
     * @return the number of background videos.
     */
    public int getBackgroundVideoCount() {
        return driver.findElements(backgroundVideos).size();
    }

    /**
     * V.18 Verifies the "Book Consultation" link is displayed.
     *
     * @return true if the consultation link is displayed.
     */
    public boolean isBookConsultationDisplayed() {
        return isDisplayed(bookConsultationLink);
    }

    /**
     * V.19 Verifies the "See all {family} owners" modal trigger is present.
     *
     * @return true if the owners-modal trigger is displayed.
     */
    public boolean isOwnersModalTriggerDisplayed() {
        return ownersModal.isTriggerDisplayed();
    }
}
