package com.timepieces.automation.pages.web.orders;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.HeaderOnlyBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * OrderHistoryPage: https://www.timepieces-d2c.example/account/orders
 * This is the Page Object Repository for the Timepieces Order History Page
 * (/account/orders).
 *
 * This page is displayed after a successful login (returning users).
 *
 * Contains links to all the orders placed by the user. In this page all
 * order are displayed in a card format with their final status, order number
 * and the date on which the order was placed. When the order card is clicked
 * it navigates the user to the corresponding {@code OrderDetailsPage}.
 *
 * This page only contains the Header.
 *
 * <p>
 * SCRAPE NOTE (2026-07-16, authenticated session): this page is FULLY
 * {@code data-testid}-instrumented — `orders-page`, `account-page-title`,
 * `account-breadcrumbs`, `orders-error`, `orders-empty`, `orders-empty-cta`,
 * `orders-list`, `order-card` (carrying a
 * {@code data-order-number="Timepieces<No>"} attribute), `order-card-link`
 * (href {@code /account/orders/Timepieces<No>}), `order-card-status`,
 * `order-card-title`. The document title is the generic storefront title (NOT
 * unique). Empty state: "This space awaits your first Timepieces." + a Continue
 * Shopping CTA (→ {@code /collections/timepieces}). Only the order-date /
 * internal-order-id / card-icon inner texts lack testids ({@code ROLLOUT-GAP}).
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (OrderHistoryPage)
 * to
 * allow
 * method chaining in step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of OrderHistoryPage"
* @since 2026-07-03
 */

@Log4j2
public class OrderHistoryPage extends HeaderOnlyBasePage<OrderHistoryPage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Page Path Variable (auth-gated).
    private static final String PAGE_PATH = "/account/orders";

    // ==========================================
    // Constructor
    // ==========================================
    public OrderHistoryPage(WebDriver driver) {
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
    // The page root container.
    private final By pageRoot = By.cssSelector("div[data-testid='orders-page']");
    // The "Orders" page heading (shared account-section template testid).
    private final By pageTitle = By.cssSelector("h1[data-testid='account-page-title']");
    // The account breadcrumbs ("My Profile" link + current "Orders").
    private final By breadcrumbs = By.cssSelector("nav[data-testid='account-breadcrumbs']");
    // The fetch-failure error box.
    private final By errorBox = By.cssSelector("div[data-testid='orders-error']");

    // --- Empty state ---
    // The empty-state wrapper ("This space awaits your first Timepieces.").
    private final By emptyState = By.cssSelector("div[data-testid='orders-empty']");
    // The empty-state Continue Shopping CTA (→ /collections/timepieces).
    private final By emptyStateCta = By.cssSelector("a[data-testid='orders-empty-cta']");

    // --- Populated state ---
    // The order-cards list wrapper.
    private final By ordersList = By.cssSelector("div[data-testid='orders-list']");
    // An individual order card (carries data-order-number="Timepieces<No>").
    private final By orderCards = By.cssSelector("div[data-testid='order-card']");
    // The card's link to its OrderDetailsPage (/account/orders/Timepieces<No>).
    private final By orderCardLinks = By.cssSelector("a[data-testid='order-card-link']");
    // The card's final-status label (e.g. "Cancelled", colored by status).
    private final By orderCardStatuses = By.cssSelector("p[data-testid='order-card-status']");
    // The card's item title.
    private final By orderCardTitles = By.cssSelector("h3[data-testid='order-card-title']");
    // The card's status date ("on <date>") — no testid.
    private final By orderCardDates = By.cssSelector("div[data-testid='order-card'] p.text-gray-400"); // ROLLOUT-GAP: structural
    // Dynamic: an order card by its Timepieces order number (e.g. "Timepieces3306").
    private final By cardByOrderNumberTemplate = By.xpath("//div[@data-testid='order-card'][@data-order-number='%s']"); // testid + attribute
    // Dynamic: the details link inside a numbered card.
    private final By linkByOrderNumberTemplate = By.xpath("//div[@data-testid='order-card'][@data-order-number='%s']//a[@data-testid='order-card-link']"); // testid + attribute

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Returns the page heading text ("Orders").
     *
     * @return the trimmed page heading text.
     */
    public String getPageHeadingText() {
        return getText(pageTitle);
    }

    /**
     * B.2 Opens the order card with the given Timepieces order number, navigating to
     * its {@code OrderDetailsPage}.
     *
     * @param orderNumber the full Timepieces-prefixed order number (e.g.
     *                    "Timepieces3306").
     * @return this page instance for method chaining.
     */
    public OrderHistoryPage openOrder(String orderNumber) {
        log.info("Opening order card: [{}]", orderNumber);
        clickElement(formatLocator(linkByOrderNumberTemplate, orderNumber));
        return this;
    }

    /**
     * B.3 Clicks the first order card's details link.
     *
     * @return this page instance for method chaining.
     */
    public OrderHistoryPage openFirstOrder() {
        log.info("Opening the first order card.");
        clickElement(orderCardLinks);
        return this;
    }

    /**
     * B.4 Clicks the empty-state Continue Shopping CTA
     * (→ {@code /collections/timepieces}).
     *
     * @return this page instance for method chaining.
     */
    public OrderHistoryPage clickContinueShopping() {
        log.info("Clicking the empty-state Continue Shopping CTA.");
        clickElement(emptyStateCta);
        return this;
    }

    /**
     * B.5 Returns the status label text of the order card with the given number.
     *
     * @param orderNumber the full Timepieces-prefixed order number.
     * @return the trimmed status text (e.g. "Cancelled").
     */
    public String getOrderStatus(String orderNumber) {
        By statusInCard = By.xpath(String.format(
                "//div[@data-testid='order-card'][@data-order-number='%s']//p[@data-testid='order-card-status']",
                orderNumber));
        return getText(statusInCard);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the orders page root container is displayed.
     *
     * @return true if the page root is displayed.
     */
    public boolean isPageLoaded() {
        return isDisplayed(pageRoot);
    }

    /**
     * V.2 Verifies the "Orders" page heading is displayed.
     *
     * @return true if the heading is displayed.
     */
    public boolean isPageHeadingDisplayed() {
        return isDisplayed(pageTitle);
    }

    /**
     * V.3 Verifies the account breadcrumbs are displayed.
     *
     * @return true if the breadcrumbs are displayed.
     */
    public boolean isBreadcrumbsDisplayed() {
        return isDisplayed(breadcrumbs);
    }

    /**
     * V.4 Reports whether the order history is in its EMPTY state.
     *
     * @return true if the empty-state wrapper is present.
     */
    public boolean isEmptyStateDisplayed() {
        return !driver.findElements(emptyState).isEmpty();
    }

    /**
     * V.5 Reports whether the fetch-failure error box is displayed.
     *
     * @return true if the error box is present.
     */
    public boolean isErrorDisplayed() {
        return !driver.findElements(errorBox).isEmpty();
    }

    /**
     * V.6 Verifies the order-cards list wrapper is displayed (populated state).
     *
     * @return true if the list wrapper is displayed.
     */
    public boolean isOrdersListDisplayed() {
        return !driver.findElements(ordersList).isEmpty();
    }

    /**
     * V.7 Counts the order cards currently rendered.
     *
     * @return the number of order cards.
     */
    public int getOrderCardCount() {
        return driver.findElements(orderCards).size();
    }

    /**
     * V.8 Verifies an order card with the given Timepieces order number exists.
     *
     * @param orderNumber the full Timepieces-prefixed order number.
     * @return true if the card is present.
     */
    public boolean hasOrder(String orderNumber) {
        return !driver.findElements(formatLocator(cardByOrderNumberTemplate, orderNumber)).isEmpty();
    }

    /**
     * V.9 Verifies every order card links to a Timepieces-prefixed details page
     * ({@code /account/orders/Timepieces<No>}), and at least one card exists.
     *
     * @return true if all card links target order-details pages.
     */
    public boolean allCardsLinkToOrderDetails() {
        var links = driver.findElements(orderCardLinks);
        if (links.isEmpty()) {
            return false;
        }
        return links.stream()
                .map(e -> e.getAttribute("href"))
                .allMatch(h -> h != null && h.contains("/account/orders/Timepieces"));
    }

    /**
     * V.10 Verifies each order card shows a status label and a status date.
     *
     * @return true if statuses and dates are present for the rendered cards.
     */
    public boolean areCardStatusesDisplayed() {
        int cards = driver.findElements(orderCards).size();
        return cards > 0
                && driver.findElements(orderCardStatuses).size() == cards
                && !driver.findElements(orderCardDates).isEmpty();
    }

    /**
     * V.11 Verifies each order card shows an item title.
     *
     * @return true if every rendered card carries a title.
     */
    public boolean areCardTitlesDisplayed() {
        int cards = driver.findElements(orderCards).size();
        return cards > 0 && driver.findElements(orderCardTitles).size() == cards;
    }
}
