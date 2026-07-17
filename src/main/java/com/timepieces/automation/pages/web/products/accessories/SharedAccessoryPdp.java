package com.timepieces.automation.pages.web.products.accessories;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * SharedAccessoryPdp — the common base for EVERY accessory PDP family template
 * (watch cases, clasps, straps). The accessory analogue of
 * {@code SharedTimepiecePdp}.
 *
 * <p>
 * All accessory PDPs render the same template (confirmed by the 2026-07-15
 * discovery scrape): an {@code h1} title, a price, a compatibility caption, a
 * real ENABLED <b>"Add to cart"</b> CTA (accessories are purchasable — unlike the
 * sold-out timepieces), a wishlist toggle, an image gallery, a color/size
 * <b>card-button switcher</b> (each card NAVIGATES to a distinct PDP URL), a
 * Description/Technical-details accordion, "Return &amp; Refund" / "Guarantee"
 * side drawers, and a bottom "recommended models" carousel. Straps add a
 * dial-try carousel + clasp-upgrade cross-sell — those live in
 * {@code SharedStrapPdp}, not here.
 *
 * <p>
 * URL PATTERN: accessory PDPs live at {@code /accessories/<type>/<handle>} (NOT
 * {@code /products/…}); variants supply the exact {@code PAGE_PATH}. Because the
 * color/size switcher navigates, every color/size is its own URL/class.
 *
 * <p>
 * LOCATOR SOURCE / data-testid ROLLOUT GAP: the accessory PDP core carries NO
 * {@code data-testid}s, so locators fall back to aria-labels / text anchors /
 * semantic classes, tagged {@code // ROLLOUT-GAP}. The price and compatibility
 * caption have no stable selector at all (text-anchored) and are the weakest
 * ({@code // ROLLOUT-GAP · WEAK}).
 *
 * FLUENT INTERFACE: business methods that remain on the page return {@code this}
 * (typed as the concrete subclass via CRTP) for chaining.
 *
 * @param <SELF> the concrete page subtype, for fluent return typing.
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedAccessoryPdp"
* @since 2026-07-15
 */

@Log4j2
public abstract class SharedAccessoryPdp<SELF extends SharedAccessoryPdp<SELF>> extends StoreFrontBasePage<SELF> {

    // ==========================================
    // Constructor
    // ==========================================
    public SharedAccessoryPdp(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS (invariant across accessory families)
    // ==========================================
    private final By productTitle = By.cssSelector("h1"); // ROLLOUT-GAP: semantic
    // No stable price/compat selector — text-anchored to the first ₹ / caption after the title.
    private final By productPrice = By.xpath("//h1/following::*[contains(normalize-space(),'₹')][1]"); // ROLLOUT-GAP · WEAK: text-anchor
    private final By compatibilityCaption = By.xpath(
            "//h1/following::*[contains(normalize-space(),'compatible') or starts-with(normalize-space(),'For ') "
                    + "or contains(normalize-space(),'Suitable for')][1]"); // ROLLOUT-GAP · WEAK: text-anchor

    // Primary CTA — "Add to cart" (desktop) / "Add to Cart" (sticky bar), enabled.
    private final By addToCartButton = By.xpath(
            "//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                    + "'abcdefghijklmnopqrstuvwxyz'),'add to cart')]"); // ROLLOUT-GAP: text-anchor

    private final By wishlistButton = By.cssSelector("button[title='Add to wishlist']"); // ROLLOUT-GAP: aria/title
    private final By wishlistButtonActive = By.cssSelector("button[title='Remove from wishlist']"); // ROLLOUT-GAP: aria/title

    // The gallery is rendered TWICE: a mobile product-media-carousel and a desktop
    // desktop-image-gallery, with CSS hiding whichever does not apply. Both are kept
    // here so the suite works at either viewport — BasePage.isDisplayed/getElement now
    // resolve the first VISIBLE match rather than the first DOM match, which is what
    // makes this safe. Verified on production 2026-07-17: document order puts the HIDDEN
    // mobile carousel (~31,940) BEFORE the visible desktop gallery (~36,688), so the old
    // first-match behaviour reported "gallery not displayed" on all five accessory PDPs
    // while the gallery was plainly on screen.
    private final By imageGalleryContainer = By
            .cssSelector("div.desktop-image-gallery, div.product-media-carousel"); // ROLLOUT-GAP: structural

    // Color/size card-button switcher. Color cards share a title (they differ by
    // background-colour) so are not individually selectable by locator — navigate by
    // handle via the model carousel instead. Size cards carry visible text.
    //
    // Verified on production 2026-07-17 (single-deployment-clasp-rose-gold-20mm):
    //   colour swatches : <button class="w-6 h-6 rounded-full transition-all"
    //                             aria-label="Single Deployment Clasp"> x4  @y=352
    //   size chips      : <button>20mm</button>/<button>22mm</button>       @y=438
    //                     parent   div.flex.gap-3.mt-2
    //                     ancestor div.size-selector
    // The previous locator (div.flex.flex-wrap.gap-2) matched ZERO nodes — there is no
    // flex-wrap container on the page at all — which is why "colour card-button switcher
    // should be displayed" failed on both clasp PDPs.
    private final By variantSwitcherContainer = By.cssSelector(
            "div.size-selector, button[aria-label][class*='rounded-full'][class*='w-6']");
    private final By sizeButtonTemplate = By.xpath(
            "//div[contains(@class,'size-selector')]//button[normalize-space()='%s']");

    // Description / Technical-details accordions.
    //
    // The old container locator (div.w-full.border-t) matched ZERO nodes. The real
    // toggles are buttons carrying aria-expanded, which is a far better hook than a
    // Tailwind class chain: it is the accessibility contract, it states the OPEN/CLOSED
    // state directly, and it survives restyling. Verified: 12 button[aria-expanded] on a
    // clasp PDP (Description=true, Technical details=false, plus the footer accordions).
    //
    // NOTE for whoever writes the next accordion test: "Description" ships EXPANDED
    // (aria-expanded="true") by default. A test that clicks it and then asserts expanded
    // will actually COLLAPSE it and fail. Assert the state, or toggle deliberately.
    private final By accordionContainer = By.cssSelector("button[aria-expanded]");
    private final By accordionToggleTemplate = By.xpath("//button[@aria-expanded][contains(normalize-space(),'%s')]");

    // Return & Refund / Guarantee side drawers (share the same overlay container).
    private final By guaranteeTrigger = By.cssSelector("button[aria-label='Open guarantee details']"); // ROLLOUT-GAP: aria
    private final By returnRefundTrigger = By.cssSelector("button[aria-label='Open return and refunds details']"); // ROLLOUT-GAP: aria
    private final By drawerContainer = By.cssSelector("div.fixed.inset-0.z-\\[9999\\]"); // ROLLOUT-GAP: structural
    private final By drawerCloseButton = By.cssSelector("button[aria-label='Close']"); // ROLLOUT-GAP: aria
    private final By drawerBackdrop = By.cssSelector("div.absolute.inset-0.bg-black\\/50.backdrop-blur-sm"); // ROLLOUT-GAP: structural

    // Bottom recommended-models carousel — cards link to /accessories/<type>/<handle>.
    //
    // The site calls this section "Similar Products" (H2) and each card carries the
    // class similar-product-card. Verified on production 2026-07-17: 6 cards, all
    // displayed, at y~2314 under the H2 at y~2245.
    //
    // The old locator was div.relative.group — a bare Tailwind pair that matched 19
    // unrelated elements page-wide, the first of which was hidden. That is the whole
    // story behind "the recommended-models carousel should be displayed = false": the
    // carousel was there, the locator just resolved to something else entirely.
    private final By modelCarouselContainer = By.cssSelector("div.similar-product-card");
    private final By modelCarouselCards = By.cssSelector("div.similar-product-card a[href*='/accessories/']");

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
     * P.2 Builds a locator for a recommended-model carousel card by the accessory
     * handle it links to (e.g. {@code fkm-rubber-strap-yellow}).
     *
     * @param handle the target accessory handle.
     * @return a By locator for that model card anchor.
     */
    private By modelCardByHandle(String handle) {
        return By.cssSelector("a[href*='" + handle + "']");
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Clicks the "Add to cart" CTA. Adding to cart opens the site cart drawer
     * (mapped by {@code CartDrawerComponent}, reachable via the header) — this
     * method only performs the add.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickAddToCart() {
        log.info("Clicking 'Add to cart'.");
        clickElement(addToCartButton);
        return self();
    }

    /**
     * B.2 Toggles the wishlist state for this accessory.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickWishlist() {
        log.info("Toggling wishlist state on the accessory PDP.");
        clickElement(wishlistButton);
        return self();
    }

    /**
     * B.3 Selects a size card-button (e.g. "20mm" / "22mm"). This NAVIGATES to the
     * distinct size PDP, so the caller should instantiate the target page object to
     * continue asserting.
     *
     * @param size the size label to select.
     */
    public void selectSize(String size) {
        log.info("Selecting size card-button: '{}'.", size);
        clickElement(formatLocator(sizeButtonTemplate, size));
    }

    /**
     * B.4 Navigates to another model/variant via the bottom recommended-models
     * carousel, identified by its accessory handle. Navigates to a distinct PDP.
     *
     * @param targetHandle the accessory handle of the model to open.
     */
    public void navigateToModel(String targetHandle) {
        log.info("Navigating to model: '{}'.", targetHandle);
        clickElement(modelCardByHandle(targetHandle));
    }

    /**
     * B.5 Expands an accordion section by name ({@code Description} /
     * {@code Technical details}).
     *
     * @param sectionName the accordion section to expand.
     * @return this page instance for method chaining.
     */
    public SELF expandAccordion(String sectionName) {
        log.info("Expanding accordion section: '{}'.", sectionName);
        clickElement(formatLocator(accordionToggleTemplate, sectionName));
        return self();
    }

    /**
     * B.6 Opens the "Guarantee" side drawer.
     *
     * @return this page instance for method chaining.
     */
    public SELF openGuaranteeDrawer() {
        log.info("Opening the guarantee side drawer.");
        clickElement(guaranteeTrigger);
        waitForVisibility(drawerContainer);
        return self();
    }

    /**
     * B.7 Opens the "Return &amp; Refund" side drawer.
     *
     * @return this page instance for method chaining.
     */
    public SELF openReturnRefundDrawer() {
        log.info("Opening the return & refund side drawer.");
        clickElement(returnRefundTrigger);
        waitForVisibility(drawerContainer);
        return self();
    }

    /**
     * B.8 Closes the open side drawer via its close ('X') button.
     *
     * @return this page instance for method chaining.
     */
    public SELF closeDrawer() {
        log.info("Closing the side drawer.");
        clickElement(drawerCloseButton);
        waitForInvisibility(drawerContainer);
        return self();
    }

    /**
     * B.9 Closes the open side drawer by clicking the backdrop (outside-click).
     *
     * @return this page instance for method chaining.
     */
    public SELF closeDrawerViaBackdrop() {
        log.info("Closing the side drawer via backdrop.");
        clickElement(drawerBackdrop);
        waitForInvisibility(drawerContainer);
        return self();
    }

    /**
     * B.10 Returns the product title text (e.g. "FKM Rubber Strap - Black").
     *
     * @return the trimmed product title.
     */
    public String getProductTitleText() {
        return getText(productTitle);
    }

    /**
     * B.11 Returns the displayed price text (e.g. "₹6,500").
     *
     * @return the trimmed price text.
     */
    public String getPriceText() {
        return getText(productPrice);
    }

    /**
     * B.12 Returns the compatibility caption text (e.g. "For Astonia").
     *
     * @return the trimmed compatibility caption.
     */
    public String getCompatibilityText() {
        return getText(compatibilityCaption);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the browser title contains this variant's expected
     * {@code PAGE_TITLE}. NOTE: size variants (e.g. Rose Gold 20mm vs 22mm) share
     * the same title — only the URL distinguishes them.
     *
     * @return true if the document title contains the expected page title.
     */
    public boolean isPageTitleCorrect() {
        String expected = getPageTitle();
        String actual = driver.getTitle();
        boolean match = expected != null && !expected.isBlank() && actual != null && actual.contains(expected);
        log.info("Accessory PDP title check — expected contains: '{}', actual: '{}', match: {}",
                expected, actual, match);
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
     * V.3 Verifies the price element is displayed.
     *
     * @return true if the price is displayed.
     */
    public boolean isPriceDisplayed() {
        return isDisplayed(productPrice);
    }

    /**
     * V.4 Verifies the compatibility caption is displayed.
     *
     * @return true if the compatibility caption is displayed.
     */
    public boolean isCompatibilityCaptionDisplayed() {
        return isDisplayed(compatibilityCaption);
    }

    /**
     * V.5 Verifies the "Add to cart" CTA is displayed.
     *
     * @return true if the add-to-cart button is displayed.
     */
    public boolean isAddToCartDisplayed() {
        return isDisplayed(addToCartButton);
    }

    /**
     * V.6 Verifies the "Add to cart" CTA is present and enabled (accessories are
     * purchasable). No-wait check.
     *
     * @return true if the add-to-cart button is present and enabled.
     */
    public boolean isAddToCartEnabled() {
        return driver.findElements(addToCartButton).stream()
                .findFirst()
                .map(btn -> btn.isEnabled() && btn.getAttribute("disabled") == null)
                .orElse(false);
    }

    /**
     * V.7 Verifies a wishlist toggle is displayed (either state).
     *
     * @return true if a wishlist toggle is displayed.
     */
    public boolean isWishlistButtonDisplayed() {
        return isDisplayed(wishlistButton) || isDisplayed(wishlistButtonActive);
    }

    /**
     * V.8 Verifies the wishlist is in the ACTIVE ("Remove from wishlist") state.
     * No-wait check.
     *
     * @return true if the accessory is wishlisted.
     */
    public boolean isWishlistActive() {
        return !driver.findElements(wishlistButtonActive).isEmpty();
    }

    /**
     * V.9 Verifies the image gallery container is displayed.
     *
     * @return true if the image gallery is displayed.
     */
    public boolean isImageGalleryDisplayed() {
        return isDisplayed(imageGalleryContainer);
    }

    /**
     * V.10 Verifies the color/size variant switcher is displayed.
     *
     * @return true if the variant switcher is displayed.
     */
    public boolean isVariantSwitcherDisplayed() {
        return isDisplayed(variantSwitcherContainer);
    }

    /**
     * V.11 Verifies the accordion panel container is displayed.
     *
     * @return true if the accordion container is displayed.
     */
    public boolean isAccordionDisplayed() {
        return isDisplayed(accordionContainer);
    }

    /**
     * V.12 Verifies the "Guarantee" drawer trigger is displayed.
     *
     * @return true if the guarantee trigger is displayed.
     */
    public boolean isGuaranteeTriggerDisplayed() {
        return isDisplayed(guaranteeTrigger);
    }

    /**
     * V.13 Verifies the "Return & Refund" drawer trigger is displayed.
     *
     * @return true if the return & refund trigger is displayed.
     */
    public boolean isReturnRefundTriggerDisplayed() {
        return isDisplayed(returnRefundTrigger);
    }

    /**
     * V.14 Verifies a side drawer is currently open.
     *
     * @return true if the drawer container is displayed.
     */
    public boolean isDrawerDisplayed() {
        return isDisplayed(drawerContainer);
    }

    /**
     * V.15 Verifies the bottom recommended-models carousel is displayed.
     *
     * @return true if the model carousel is displayed.
     */
    public boolean isModelCarouselDisplayed() {
        return isDisplayed(modelCarouselContainer);
    }

    /**
     * V.16 Counts the recommended-model cards in the bottom carousel.
     *
     * @return the number of model cards.
     */
    public int getModelCarouselCardCount() {
        return driver.findElements(modelCarouselCards).size();
    }

    /**
     * V.17 Verifies that every recommended-model card links to an
     * {@code /accessories/} product page (and that at least one card exists).
     *
     * @return true if all model cards' hrefs contain "/accessories/".
     */
    public boolean allModelCardsLinkToAccessories() {
        List<org.openqa.selenium.WebElement> cards = getElements(modelCarouselCards);
        if (cards.isEmpty()) {
            return false;
        }
        return cards.stream().allMatch(card -> {
            String href = card.getAttribute("href");
            return href != null && href.contains("/accessories/");
        });
    }
}
