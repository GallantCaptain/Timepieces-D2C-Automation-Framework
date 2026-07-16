package com.timepieces.automation.pages.web.orders;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * WishlistPage: https://www.timepieces-d2c.example/wishlist
 * This is the Page Object Repository for the Timepieces Wishlist Page
 * ({@code /wishlist} — confirmed by the 2026-07-16 scrape; NOT
 * {@code /account/wishlist}).
 *
 * This page is displayed to both guest users and logged in users.
 *
 * Contains links to all the items added to the user's wishlist. In this page
 * all wishlist items are displayed in a card format with their product name,
 * image, price and stock status.
 * The wishlist counter is global and reflects whenever the user clicks on the
 * heart icon of any product. The counter in the header and wishlist page
 * should always be the same. The counter in the header is visibly dynamic
 * when there is an increment/decrement to the wishlist. The wishlist is noth
 * guests and loggedin users. Guest users can access the wishlist via the
 * {@code SidebarMenuComponent} and logged in users can access it via both
 * {@code WishlistIconComponent} and {@code AccountPage}.
 * This page only contains the Header & Footer.
 *
 * <p>
 * SCRAPE NOTE (2026-07-16): guest wishlist state persists in browser
 * {@code localStorage} under the key {@code timepieces_wishlist} (a JSON array of
 * product objects with id/variantId/title/handle/price/image/availableForSale/
 * addedAt/watchMovement/productShortInfo/productType) — step definitions can
 * SEED the wishlist via a JavascriptExecutor write to that key instead of
 * clicking PDP hearts. The page heading carries a real
 * {@code data-testid="account-page-title"} (shared account-section template) and
 * breadcrumbs carry {@code data-testid="account-breadcrumbs"}. Empty state: an
 * {@code h2} "Your Wishlist is Empty" + a "Continue Shopping" link
 * (→ {@code /collections/timepieces}). Populated state: an item-count line
 * ("2 items"), a "Clear All" button (clears instantly, NO confirm dialog), and
 * item cards ({@code div.bg-[#141414].rounded-xl}) each holding an absolute
 * remove button ({@code button[title='Remove from wishlist']}, instant, no
 * confirm), the product link (→ {@code /products/<handle>} or
 * {@code /accessories/<type>/<handle>}), a category label, an {@code h3} title,
 * variant details, a price, and a "Sold Out" overlay when unavailable. Guest
 * cards have NO add-to-cart / move-to-cart — purchase goes through the PDP.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (WishlistPage)
 * to
 * allow
 * method chaining in step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of WishlistPage"
* @since 2026-07-03
 */

@Log4j2
public class WishlistPage extends StoreFrontBasePage<WishlistPage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Page Path Variable (confirmed /wishlist by the 2026-07-16 scrape).
    private static final String PAGE_PATH = "/wishlist";

    // The localStorage key holding the guest wishlist JSON (for test seeding).
    public static final String WISHLIST_STORAGE_KEY = "timepieces_wishlist";

    // ==========================================
    // Constructor
    // ==========================================
    public WishlistPage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // PAGE METADATA (drives the inherited fluent open())
    // ==========================================
    @Override
    protected String getPagePath() {
        return PAGE_PATH;
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // The "Wishlist" page heading (shared account-section template testid).
    private final By pageTitle = By.cssSelector("h1[data-testid='account-page-title']");
    // The account breadcrumbs (home link + current location).
    private final By breadcrumbs = By.cssSelector("nav[data-testid='account-breadcrumbs']");

    // --- Empty state ---
    // The empty-wishlist heading.
    private final By emptyHeading = By.xpath("//h2[contains(normalize-space(),'Your Wishlist is Empty')]"); // ROLLOUT-GAP: text-anchor
    // The "Continue Shopping" CTA (→ /collections/timepieces).
    private final By continueShoppingLink = By.xpath("//a[contains(normalize-space(),'Continue Shopping')]"); // ROLLOUT-GAP: text-anchor

    // --- Populated state ---
    // The dynamic item-count line ("1 item" / "2 items").
    private final By itemCountText = By.xpath("//p[contains(@class,'text-gray-400')][contains(normalize-space(),'item')]"); // ROLLOUT-GAP: text-anchor
    // The "Clear All" button (clears instantly, no confirm dialog).
    private final By clearAllButton = By.xpath("//button[normalize-space()='Clear All']"); // ROLLOUT-GAP: text-anchor
    // A wishlist item card.
    private final By itemCards = By.cssSelector("div.bg-\\[\\#141414\\].rounded-xl.overflow-hidden.group.relative"); // ROLLOUT-GAP: utility-class
    // The per-card instant remove button (absolute top-right, no confirm dialog).
    private final By removeButtons = By.cssSelector("button[title='Remove from wishlist']");
    // The per-card product link (timepiece PDP or accessory PDP).
    private final By cardProductLinks = By.cssSelector("div.bg-\\[\\#141414\\].rounded-xl a[href*='/products/'], div.bg-\\[\\#141414\\].rounded-xl a[href*='/accessories/']"); // ROLLOUT-GAP: structural
    // The card product-name heading.
    private final By cardTitles = By.cssSelector("div.bg-\\[\\#141414\\].rounded-xl h3"); // ROLLOUT-GAP: structural
    // The "Sold Out" overlay badge on unavailable timepieces.
    private final By soldOutBadges = By.xpath("//div[contains(@class,'rounded-xl')]//*[normalize-space()='Sold Out']"); // ROLLOUT-GAP: text-anchor
    // Dynamic: a wishlist card by its product name (h3).
    private final By cardByNameTemplate = By.xpath("//div[contains(@class,'rounded-xl')][.//h3[normalize-space()='%s']]"); // ROLLOUT-GAP: text-anchor
    // Dynamic: the remove button inside the card of a named product.
    private final By removeByNameTemplate = By.xpath("//div[contains(@class,'rounded-xl')][.//h3[normalize-space()='%s']]//button[@title='Remove from wishlist']"); // ROLLOUT-GAP: text-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Returns the page heading text ("Wishlist").
     *
     * @return the trimmed page heading text.
     */
    public String getPageHeadingText() {
        return getText(pageTitle);
    }

    /**
     * B.2 Returns the item-count line text (e.g. "2 items").
     *
     * @return the trimmed item-count text.
     */
    public String getItemCountText() {
        return getText(itemCountText);
    }

    /**
     * B.3 Parses the on-page item-count line into a number (e.g. "2 items" → 2).
     *
     * @return the declared item count, or 0 if the line is absent/unparseable.
     */
    public int getDeclaredItemCount() {
        try {
            String text = getText(itemCountText);
            return Integer.parseInt(text.replaceAll("\\D+", ""));
        } catch (Exception e) {
            log.warn("Could not parse the wishlist item-count line; returning 0.", e);
            return 0;
        }
    }

    /**
     * B.4 Clicks "Clear All", removing every wishlist item (instant — the site
     * shows NO confirmation dialog).
     *
     * @return this page instance for method chaining.
     */
    public WishlistPage clickClearAll() {
        log.info("Clearing the entire wishlist via 'Clear All'.");
        clickElement(clearAllButton);
        return this;
    }

    /**
     * B.5 Removes a single item by its product name via the card's remove button
     * (instant — no confirmation dialog).
     *
     * @param productName the exact card title (h3) text.
     * @return this page instance for method chaining.
     */
    public WishlistPage removeItem(String productName) {
        log.info("Removing wishlist item: [{}]", productName);
        clickElement(formatLocator(removeByNameTemplate, productName));
        return this;
    }

    /**
     * B.6 Clicks a wishlist card's product link by name, navigating to its PDP.
     *
     * @param productName the exact card title (h3) text.
     * @return this page instance for method chaining.
     */
    public WishlistPage clickItem(String productName) {
        log.info("Opening wishlist item PDP: [{}]", productName);
        clickElement(formatLocator(cardByNameTemplate, productName));
        return this;
    }

    /**
     * B.7 Clicks the empty-state "Continue Shopping" CTA
     * (→ {@code /collections/timepieces}).
     *
     * @return this page instance for method chaining.
     */
    public WishlistPage clickContinueShopping() {
        log.info("Clicking 'Continue Shopping' from the empty wishlist.");
        clickElement(continueShoppingLink);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the "Wishlist" page heading is displayed.
     *
     * @return true if the page heading is displayed.
     */
    public boolean isPageHeadingDisplayed() {
        return isDisplayed(pageTitle);
    }

    /**
     * V.2 Verifies the account breadcrumbs are displayed.
     *
     * @return true if the breadcrumbs are displayed.
     */
    public boolean isBreadcrumbsDisplayed() {
        return isDisplayed(breadcrumbs);
    }

    /**
     * V.3 Reports whether the wishlist is in its EMPTY state.
     *
     * @return true if the empty-wishlist heading is present.
     */
    public boolean isEmptyStateDisplayed() {
        return !driver.findElements(emptyHeading).isEmpty();
    }

    /**
     * V.4 Counts the wishlist item cards currently rendered.
     *
     * @return the number of item cards.
     */
    public int getItemCardCount() {
        return driver.findElements(itemCards).size();
    }

    /**
     * V.5 Verifies a wishlist card for the given product name is present.
     *
     * @param productName the exact card title (h3) text.
     * @return true if the card is present.
     */
    public boolean hasItem(String productName) {
        return !driver.findElements(formatLocator(cardByNameTemplate, productName)).isEmpty();
    }

    /**
     * V.6 Verifies the on-page declared count matches the number of rendered
     * cards (the counter is expected to stay in sync).
     *
     * @return true if the declared count equals the card count.
     */
    public boolean isItemCountInSync() {
        return getDeclaredItemCount() == getItemCardCount();
    }

    /**
     * V.7 Verifies the "Clear All" button is displayed (populated state only).
     *
     * @return true if "Clear All" is displayed.
     */
    public boolean isClearAllDisplayed() {
        return !driver.findElements(clearAllButton).isEmpty();
    }

    /**
     * V.8 Counts the "Sold Out" overlay badges among the item cards.
     *
     * @return the number of sold-out badges.
     */
    public int getSoldOutBadgeCount() {
        return driver.findElements(soldOutBadges).size();
    }

    /**
     * V.9 Verifies every item card links to a product or accessory PDP (and at
     * least one card exists).
     *
     * @return true if all card links target PDPs.
     */
    public boolean allCardsLinkToPdps() {
        var links = driver.findElements(cardProductLinks);
        if (links.isEmpty()) {
            return false;
        }
        return links.stream()
                .map(e -> e.getAttribute("href"))
                .allMatch(h -> h != null && (h.contains("/products/") || h.contains("/accessories/")));
    }

    /**
     * V.10 Verifies per-card remove buttons are present (populated state).
     *
     * @return true if at least one remove button is present.
     */
    public boolean areRemoveButtonsPresent() {
        return !driver.findElements(removeButtons).isEmpty();
    }
}
