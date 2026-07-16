package com.timepieces.automation.pages.web.orders;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.HeaderOnlyBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * OrderDetailsPage: https://www.timepieces-d2c.example/account/orders/Timepieces{Order
 * Number}
 * This is the Page Object Repository for the Timepieces Order Details Page
 * (/account/orders/Timepieces{Order Number}).
 *
 * This page is displayed to a loggedin user via the order history page from
 * the account page.
 *
 * All details of the order, delivery status, payment status, receipt
 * if delivered are displayed here. The order details include the order number,
 * order date, order status, order total, order items, shipping address, billing
 * address.
 *
 * This page only contains the Header.
 *
 * <p>
 * SCRAPE NOTE (2026-07-16, authenticated session): FULLY
 * {@code data-testid}-instrumented (`order-detail-page`, `order-detail-back`,
 * `order-detail-title`, `order-detail-variant`, `order-detail-status(-label)`,
 * `order-detail-price-toggle`, `order-detail-item-price`, `order-detail-total`,
 * `order-detail-view-breakup`, `order-detail-order-id`,
 * `order-detail-payment-status`, `order-detail-shipping-address`,
 * `order-detail-billing-address`, `order-detail-invoice` [rendered only when
 * DELIVERED], `order-detail-help`). The old Javadoc flagged Contact Support as
 * "currently bugged" — the scrape DISPROVED that: the "Help" button now opens a
 * Support Request sheet, itself fully testid'd with separate desktop/mobile
 * variants (`support-sheet-desktop`/`-mobile`, close buttons, subject toggle,
 * note textarea, file input, submit — submit starts disabled). This page object
 * picks the right variant via the inherited {@code isMobileViewport()}. The
 * document title is the generic storefront title (NOT unique). Only status-date
 * / sold-by / address inner texts lack testids ({@code ROLLOUT-GAP}).
 *
 * <p>
 * The path is per-order, so {@code open()} is not used directly; callers open a
 * specific order via {@link #openOrder(String)} (or by clicking a card on
 * {@link OrderHistoryPage}).
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (OrderDetailsPage)
 * to allow method chaining in step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of OrderDetailsPage"
* @since 2026-07-03
 */

@Log4j2
public class OrderDetailsPage extends HeaderOnlyBasePage<OrderDetailsPage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The path prefix for an order-details page (Timepieces-prefixed order number appended).
    private static final String ORDER_PATH_PREFIX = "/account/orders/";

    // ==========================================
    // Constructor
    // ==========================================
    public OrderDetailsPage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // The page root container.
    private final By pageRoot = By.cssSelector("div[data-testid='order-detail-page']");
    // The "Back" link (→ /account/orders).
    private final By backLink = By.cssSelector("a[data-testid='order-detail-back']");
    // The order item title.
    private final By orderTitle = By.cssSelector("h1[data-testid='order-detail-title']");
    // The item variant line.
    private final By orderVariant = By.cssSelector("p[data-testid='order-detail-variant']");
    // The order-status wrapper + label (colored by status).
    private final By statusContainer = By.cssSelector("div[data-testid='order-detail-status']");
    private final By statusLabel = By.cssSelector("p[data-testid='order-detail-status-label']");
    // The status date line ("on <day>, <date>") — no testid.
    private final By statusDate = By.cssSelector("div[data-testid='order-detail-status'] p.text-white"); // ROLLOUT-GAP: structural

    // --- Pricing ---
    // The item-price collapse toggle.
    private final By priceToggle = By.cssSelector("button[data-testid='order-detail-price-toggle']");
    // The item total price.
    private final By itemPrice = By.cssSelector("span[data-testid='order-detail-item-price']");
    // The order total paid.
    private final By orderTotal = By.cssSelector("[data-testid='order-detail-total']");
    // The "View Breakup" button.
    private final By viewBreakupButton = By.cssSelector("button[data-testid='order-detail-view-breakup']");

    // --- IDs & payment ---
    // The internal order id.
    private final By orderId = By.cssSelector("span[data-testid='order-detail-order-id']");
    // The payment status (e.g. "refunded").
    private final By paymentStatus = By.cssSelector("span[data-testid='order-detail-payment-status']");

    // --- Addresses ---
    // The shipping / billing address blocks (inner texts hold personal data).
    private final By shippingAddress = By.cssSelector("div[data-testid='order-detail-shipping-address']");
    private final By billingAddress = By.cssSelector("div[data-testid='order-detail-billing-address']");

    // --- Invoice (delivered orders only) ---
    private final By invoiceButton = By.cssSelector("button[data-testid='order-detail-invoice']");

    // --- Help / Support Request sheet ---
    // The "Help" trigger button.
    private final By helpButton = By.cssSelector("button[data-testid='order-detail-help']");
    // The sheet backdrop.
    private final By supportBackdrop = By.cssSelector("div[data-testid='support-sheet-backdrop']");
    // Desktop sheet + controls.
    private final By supportSheetDesktop = By.cssSelector("div[data-testid='support-sheet-desktop']");
    private final By supportCloseDesktop = By.cssSelector("button[data-testid='support-sheet-close-desktop']");
    private final By supportSubjectDesktop = By.cssSelector("button[data-testid='support-subject-toggle-desktop']");
    private final By supportNoteDesktop = By.cssSelector("textarea[data-testid='support-note-desktop']");
    private final By supportSubmitDesktop = By.cssSelector("button[data-testid='support-submit-desktop']");
    // Mobile sheet + controls.
    private final By supportSheetMobile = By.cssSelector("div[data-testid='support-sheet-mobile']");
    private final By supportCloseMobile = By.cssSelector("button[data-testid='support-sheet-close-mobile']");
    private final By supportSubjectMobile = By.cssSelector("button[data-testid='support-subject-toggle-mobile']");
    private final By supportNoteMobile = By.cssSelector("textarea[data-testid='support-note-mobile']");
    private final By supportSubmitMobile = By.cssSelector("button[data-testid='support-submit-mobile']");
    // File-upload inputs (type=file; hidden — target with sendKeys, not click).
    private final By supportFileDesktop = By.cssSelector("input[data-testid='support-file-input-desktop']");
    private final By supportFileMobile = By.cssSelector("input[data-testid='support-file-input-mobile']");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * P.1 Returns the support-sheet container for the current viewport.
     *
     * @return the mobile or desktop sheet locator.
     */
    private By supportSheet() {
        return isMobileViewport() ? supportSheetMobile : supportSheetDesktop;
    }

    /**
     * P.2 Returns the support-sheet close button for the current viewport.
     *
     * @return the mobile or desktop close-button locator.
     */
    private By supportClose() {
        return isMobileViewport() ? supportCloseMobile : supportCloseDesktop;
    }

    /**
     * P.3 Returns the support-note textarea for the current viewport.
     *
     * @return the mobile or desktop note locator.
     */
    private By supportNote() {
        return isMobileViewport() ? supportNoteMobile : supportNoteDesktop;
    }

    /**
     * P.4 Returns the support submit button for the current viewport.
     *
     * @return the mobile or desktop submit locator.
     */
    private By supportSubmit() {
        return isMobileViewport() ? supportSubmitMobile : supportSubmitDesktop;
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Opens a specific order-details page by its Timepieces-prefixed order
     * number (e.g. "Timepieces3306").
     *
     * @param orderNumber the full Timepieces-prefixed order number.
     * @return this page instance for method chaining.
     */
    public OrderDetailsPage openOrder(String orderNumber) {
        String url = getBaseUrl() + ORDER_PATH_PREFIX + orderNumber;
        log.info("Opening order details URL: [{}]", url);
        driver.get(url);
        return this;
    }

    /**
     * B.2 Clicks the "Back" link (→ {@code /account/orders}).
     *
     * @return this page instance for method chaining.
     */
    public OrderDetailsPage clickBack() {
        log.info("Navigating back to Order History.");
        clickElement(backLink);
        return this;
    }

    /**
     * B.3 Returns the order item title text.
     *
     * @return the trimmed title text.
     */
    public String getOrderTitleText() {
        return getText(orderTitle);
    }

    /**
     * B.4 Returns the order status label text (e.g. "Cancelled", "Delivered").
     *
     * @return the trimmed status text.
     */
    public String getStatusText() {
        return getText(statusLabel);
    }

    /**
     * B.5 Returns the status date line ("on <day>, <date>").
     *
     * @return the trimmed status-date text.
     */
    public String getStatusDateText() {
        return getText(statusDate);
    }

    /**
     * B.6 Returns the internal order id text.
     *
     * @return the trimmed order id.
     */
    public String getOrderIdText() {
        return getText(orderId);
    }

    /**
     * B.7 Returns the payment status text (e.g. "refunded", "paid").
     *
     * @return the trimmed payment-status text.
     */
    public String getPaymentStatusText() {
        return getText(paymentStatus);
    }

    /**
     * B.8 Returns the order total text (e.g. "₹8,250").
     *
     * @return the trimmed order total.
     */
    public String getOrderTotalText() {
        return getText(orderTotal);
    }

    /**
     * B.9 Returns the item total price text.
     *
     * @return the trimmed item price.
     */
    public String getItemPriceText() {
        return getText(itemPrice);
    }

    /**
     * B.10 Toggles the item-price collapse section.
     *
     * @return this page instance for method chaining.
     */
    public OrderDetailsPage togglePriceSection() {
        log.info("Toggling the item-price section.");
        clickElement(priceToggle);
        return this;
    }

    /**
     * B.11 Clicks "View Breakup" for the detailed price breakdown.
     *
     * @return this page instance for method chaining.
     */
    public OrderDetailsPage clickViewBreakup() {
        log.info("Opening the price breakup.");
        clickElement(viewBreakupButton);
        return this;
    }

    /**
     * B.12 Clicks "Get Invoice" (rendered only when the order is DELIVERED).
     *
     * @return this page instance for method chaining.
     */
    public OrderDetailsPage clickGetInvoice() {
        log.info("Clicking 'Get Invoice'.");
        clickElement(invoiceButton);
        return this;
    }

    /**
     * B.13 Clicks "Help", opening the Support Request sheet.
     *
     * @return this page instance for method chaining.
     */
    public OrderDetailsPage clickHelp() {
        log.info("Opening the Support Request sheet via 'Help'.");
        clickElement(helpButton);
        return this;
    }

    /**
     * B.14 Closes the Support Request sheet via its viewport-appropriate close
     * button.
     *
     * @return this page instance for method chaining.
     */
    public OrderDetailsPage closeSupportSheet() {
        log.info("Closing the Support Request sheet.");
        clickElement(supportClose());
        return this;
    }

    /**
     * B.15 Opens the subject selector inside the Support Request sheet.
     *
     * @return this page instance for method chaining.
     */
    public OrderDetailsPage clickSupportSubjectToggle() {
        log.info("Opening the support subject selector.");
        clickElement(isMobileViewport() ? supportSubjectMobile : supportSubjectDesktop);
        return this;
    }

    /**
     * B.16 Types a note into the Support Request sheet's textarea.
     *
     * @param note the support note text.
     * @return this page instance for method chaining.
     */
    public OrderDetailsPage enterSupportNote(String note) {
        log.info("Entering a support note.");
        sendKeys(supportNote(), note);
        return this;
    }

    /**
     * B.17 Submits the Support Request sheet.
     *
     * @return this page instance for method chaining.
     */
    public OrderDetailsPage submitSupportRequest() {
        log.info("Submitting the support request.");
        clickElement(supportSubmit());
        return this;
    }

    /**
     * B.18 Attaches a file to the Support Request via the hidden file input
     * (sendKeys with an absolute path — the input is not clickable).
     *
     * @param absoluteFilePath the absolute path of the image/video to attach.
     * @return this page instance for method chaining.
     */
    public OrderDetailsPage attachSupportFile(String absoluteFilePath) {
        log.info("Attaching a file to the support request.");
        By fileInput = isMobileViewport() ? supportFileMobile : supportFileDesktop;
        getElementPresence(fileInput).sendKeys(absoluteFilePath);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the order-details page root is displayed.
     *
     * @return true if the page root is displayed.
     */
    public boolean isPageLoaded() {
        return isDisplayed(pageRoot);
    }

    /**
     * V.2 Verifies the "Back" link is displayed.
     *
     * @return true if the back link is displayed.
     */
    public boolean isBackLinkDisplayed() {
        return isDisplayed(backLink);
    }

    /**
     * V.3 Verifies the order status wrapper + label are displayed.
     *
     * @return true if the status is displayed.
     */
    public boolean isStatusDisplayed() {
        return isDisplayed(statusContainer) && isDisplayed(statusLabel);
    }

    /**
     * V.4 Verifies the item variant line is present.
     *
     * @return true if the variant line is present.
     */
    public boolean isVariantPresent() {
        return !driver.findElements(orderVariant).isEmpty();
    }

    /**
     * V.5 Verifies the shipping address block is displayed.
     *
     * @return true if the shipping address is displayed.
     */
    public boolean isShippingAddressDisplayed() {
        return isDisplayed(shippingAddress);
    }

    /**
     * V.6 Verifies the billing address block is displayed.
     *
     * @return true if the billing address is displayed.
     */
    public boolean isBillingAddressDisplayed() {
        return isDisplayed(billingAddress);
    }

    /**
     * V.7 Reports whether the "Get Invoice" button is present (rendered only for
     * DELIVERED orders).
     *
     * @return true if the invoice button is present.
     */
    public boolean isInvoiceAvailable() {
        return !driver.findElements(invoiceButton).isEmpty();
    }

    /**
     * V.8 Verifies the "Help" button is displayed.
     *
     * @return true if the help button is displayed.
     */
    public boolean isHelpButtonDisplayed() {
        return isDisplayed(helpButton);
    }

    /**
     * V.9 Reports whether the Support Request sheet is open (backdrop + the
     * viewport-appropriate sheet rendered).
     *
     * @return true if the support sheet is open.
     */
    public boolean isSupportSheetOpen() {
        return !driver.findElements(supportBackdrop).isEmpty()
                && !driver.findElements(supportSheet()).isEmpty();
    }

    /**
     * V.10 Reports whether the support submit button is enabled (it starts
     * disabled until the form is filled).
     *
     * @return true if the submit button is enabled.
     */
    public boolean isSupportSubmitEnabled() {
        try {
            var button = getElement(supportSubmit());
            String cls = button.getAttribute("class");
            boolean styledDisabled = cls != null && cls.contains("cursor-not-allowed");
            return button.isEnabled() && !styledDisabled;
        } catch (Exception e) {
            log.warn("Could not evaluate the support submit state.", e);
            return false;
        }
    }
}
