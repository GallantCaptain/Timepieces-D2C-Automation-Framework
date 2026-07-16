package com.timepieces.automation.pages.web.orders;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * Track Orders Page: https://www.timepieces-d2c.example/track-order
 *
 * The difference between OrderHistoryPage and TrackOrdersPage is that the user
 * has to be logged in for the former and a user can browse as a guest and track
 * the order using the order number when they click on Track Order Button from
 * the SidebarMenuComponent
 *
 * Only details of the order like delivery status, order number and order date
 * are mentioned here. There is a contact support button that takes the user to
 * {@code ContactPage}
 *
 * <p>
 * SCRAPE NOTE (2026-07-16): this page has BOTH header and footer (the earlier
 * "header only" note was wrong — the {@code StoreFrontBasePage} base is
 * correct). Navigating to {@code /track-order?orderId=<id>} auto-fills the
 * input and auto-submits on mount. The previous scaffold locators
 * ({@code data-testid='order-details-card'/'order-number'/'order-date'} and a
 * "Contact Support" button) were NOT found in the live DOM and were replaced:
 * the result view renders an {@code h1} "Order #&lt;id&gt;", a "Placed on
 * &lt;date&gt; • &lt;time&gt;" line, a "Timepieces in this order" list (with a
 * dropdown selector when the order holds &gt;1 item; each item shows image,
 * name, and "SN : &lt;serial&gt;"), a vertical status-stepper timeline (Order
 * confirmed → In Assembly → In Testing → Ready for pickup/Shipped → Out for
 * delivery → Delivered; or Order cancelled), a shipment Tracking section when
 * shipped (carrier, "AWB: &lt;n&gt;", a target=_blank Track-Shipment link), and
 * a Contact-support {@code a[href='/contact']}. An unknown order id renders
 * "Order not found. Please check your order ID and try again." The submit
 * button is disabled while the input is empty and reads "Tracking..." while
 * loading. No {@code data-testid}s on the body — fallbacks tagged
 * {@code ROLLOUT-GAP}.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (TrackOrdersPage)
 * to allow method chaining in step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TrackOrdersPage"
* @since 2026-07-03
 */

@Log4j2
public class TrackOrdersPage extends StoreFrontBasePage<TrackOrdersPage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Page Path Variable.
    private static final String PAGE_PATH = "/track-order";

    // ==========================================
    // Constructor
    // ==========================================
    public TrackOrdersPage(WebDriver driver) {
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
    // --- Entry form (unsearched state) ---
    // The "Track your order" heading.
    private final By mainHeading = By.xpath("//h1[contains(normalize-space(),'Track your order')]"); // ROLLOUT-GAP: text-anchor
    // The subheading line.
    private final By subHeading = By.xpath("//p[contains(normalize-space(),'Monitor the progress of your Timepieces timepiece')]"); // ROLLOUT-GAP: text-anchor
    // The order-id input.
    private final By orderIdInput = By.cssSelector("input[placeholder='Enter order Id']"); // ROLLOUT-GAP: placeholder-anchor
    // The submit button ("Track order", or "Tracking..." while loading).
    private final By trackButton = By.xpath("//button[contains(normalize-space(),'Track order') or contains(normalize-space(),'Tracking')]"); // ROLLOUT-GAP: text-anchor

    // --- Error state ---
    // The invalid-order error message.
    private final By errorMessage = By.cssSelector("div.bg-red-900\\/30 p"); // ROLLOUT-GAP: utility-class

    // --- Success state (order details view) ---
    // The "Order #<id>" heading.
    private final By orderNumberHeading = By.xpath("//h1[starts-with(normalize-space(),'Order #')]"); // ROLLOUT-GAP: text-anchor
    // The "Placed on <date> • <time>" line.
    private final By placedOnText = By.xpath("//p[contains(normalize-space(),'Placed on')]"); // ROLLOUT-GAP: text-anchor
    // The "Timepieces in this order" list label.
    private final By timepiecesListLabel = By.xpath("//p[contains(normalize-space(),'Timepieces in this order')]"); // ROLLOUT-GAP: text-anchor
    // The item serial-number line(s) ("SN : <serialNumber>").
    private final By serialNumberText = By.xpath("//p[contains(normalize-space(),'SN :')]"); // ROLLOUT-GAP: text-anchor
    // The multi-item dropdown toggle (present only when the order has >1 item).
    private final By itemDropdownToggle = By.cssSelector("button.w-full.flex.items-center.gap-3.rounded-2xl"); // ROLLOUT-GAP: utility-class
    // A status-stepper step row.
    private final By stepperSteps = By.cssSelector("div.flex.gap-4"); // ROLLOUT-GAP: structural
    // Dynamic: a stepper step title by its exact text (e.g. "Order confirmed").
    private final By stepByTitleTemplate = By.xpath("//div[contains(@class,'flex')]//p[normalize-space()='%s']"); // ROLLOUT-GAP: text-anchor

    // --- Shipment tracking section (present when shipped) ---
    // The "Tracking" section label.
    private final By trackingSectionLabel = By.xpath("//p[normalize-space()='Tracking']"); // ROLLOUT-GAP: text-anchor
    // The "AWB: <awbNumber>" line.
    private final By awbText = By.xpath("//p[contains(normalize-space(),'AWB:')]"); // ROLLOUT-GAP: text-anchor
    // The external Track-Shipment link (opens in a new tab).
    private final By trackShipmentLink = By.xpath("//a[@target='_blank'][contains(@class,'rounded-full')]"); // ROLLOUT-GAP: structural

    // --- Support CTA ---
    // The "Contact support" link (→ /contact). NOTE: an <a>, not a <button>.
    private final By contactSupportLink = By.cssSelector("a[href='/contact']"); // ROLLOUT-GAP: href-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Enters an order id and submits the tracking form.
     *
     * @param orderId the order id to track.
     * @return this page instance for method chaining.
     */
    public TrackOrdersPage trackOrder(String orderId) {
        log.info("Tracking order id: [{}]", orderId);
        sendKeys(orderIdInput, orderId);
        clickElement(trackButton);
        return this;
    }

    /**
     * B.2 Opens the page with an {@code ?orderId=} query parameter, which
     * auto-fills the input and auto-submits the search on mount.
     *
     * @param orderId the order id to track.
     * @return this page instance for method chaining.
     */
    public TrackOrdersPage openWithOrderId(String orderId) {
        String url = getBaseUrl() + PAGE_PATH + "?orderId=" + orderId;
        log.info("Opening track-order with auto-submit URL: [{}]", url);
        driver.get(url);
        return this;
    }

    /**
     * B.3 Returns the invalid-order error message text.
     *
     * @return the trimmed error text.
     */
    public String getErrorMessage() {
        return getText(errorMessage);
    }

    /**
     * B.4 Returns the "Order #<id>" heading text from the result view.
     *
     * @return the trimmed order-number heading text.
     */
    public String getOrderNumberText() {
        return getText(orderNumberHeading);
    }

    /**
     * B.5 Returns the "Placed on <date> • <time>" line text.
     *
     * @return the trimmed placed-on text.
     */
    public String getPlacedOnText() {
        return getText(placedOnText);
    }

    /**
     * B.6 Returns the serial-number lines ("SN : <n>") of the items in the order.
     *
     * @return the list of trimmed serial-number texts.
     */
    public List<String> getSerialNumbers() {
        return driver.findElements(serialNumberText).stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());
    }

    /**
     * B.7 Returns the "AWB: <n>" line from the shipment tracking section.
     *
     * @return the trimmed AWB text.
     */
    public String getAwbText() {
        return getText(awbText);
    }

    /**
     * B.8 Clicks the external Track-Shipment link (opens the carrier's tracking
     * page in a new tab).
     *
     * @return this page instance for method chaining.
     */
    public TrackOrdersPage clickTrackShipment() {
        log.info("Clicking the external Track-Shipment link.");
        clickElement(trackShipmentLink);
        return this;
    }

    /**
     * B.9 Clicks the "Contact support" link, navigating to the Contact page.
     *
     * @return this page instance for method chaining.
     */
    public TrackOrdersPage clickContactSupport() {
        log.info("Clicking 'Contact support' (→ /contact).");
        clickElement(contactSupportLink);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the "Track your order" heading is displayed (entry form).
     *
     * @return true if the main heading is displayed.
     */
    public boolean isMainHeadingDisplayed() {
        return isDisplayed(mainHeading);
    }

    /**
     * V.2 Verifies the subheading line is displayed.
     *
     * @return true if the subheading is displayed.
     */
    public boolean isSubHeadingDisplayed() {
        return isDisplayed(subHeading);
    }

    /**
     * V.3 Verifies the order-id input is displayed.
     *
     * @return true if the input is displayed.
     */
    public boolean isOrderIdInputDisplayed() {
        return isDisplayed(orderIdInput);
    }

    /**
     * V.4 Reports whether the submit button is enabled (it is disabled while the
     * input is empty/whitespace).
     *
     * @return true if the track button is enabled.
     */
    public boolean isTrackButtonEnabled() {
        try {
            var button = getElement(trackButton);
            String cls = button.getAttribute("class");
            boolean styledDisabled = cls != null && cls.contains("cursor-not-allowed");
            return button.isEnabled() && !styledDisabled;
        } catch (Exception e) {
            log.warn("Could not evaluate the track button state.", e);
            return false;
        }
    }

    /**
     * V.5 Reports whether the invalid-order error message is displayed.
     *
     * @return true if the error is present.
     */
    public boolean isErrorDisplayed() {
        return !driver.findElements(errorMessage).isEmpty();
    }

    /**
     * V.6 Reports whether the order-details result view is displayed (the
     * "Order #" heading rendered).
     *
     * @return true if the result view is present.
     */
    public boolean isOrderDetailsDisplayed() {
        return !driver.findElements(orderNumberHeading).isEmpty();
    }

    /**
     * V.7 Verifies the "Timepieces in this order" list label is displayed.
     *
     * @return true if the list label is displayed.
     */
    public boolean isTimepiecesListDisplayed() {
        return !driver.findElements(timepiecesListLabel).isEmpty();
    }

    /**
     * V.8 Reports whether the multi-item dropdown selector is present (only when
     * the order contains more than one item).
     *
     * @return true if the dropdown toggle is present.
     */
    public boolean hasItemDropdown() {
        return !driver.findElements(itemDropdownToggle).isEmpty();
    }

    /**
     * V.9 Verifies a status step with the given title exists in the timeline
     * (e.g. "Order confirmed", "In Assembly", "Delivered", "Order cancelled").
     *
     * @param stepTitle the exact step title.
     * @return true if the step is present.
     */
    public boolean hasStep(String stepTitle) {
        return !driver.findElements(formatLocator(stepByTitleTemplate, stepTitle)).isEmpty();
    }

    /**
     * V.10 Counts the status-stepper step rows in the timeline.
     *
     * @return the number of step rows.
     */
    public int getStepCount() {
        return driver.findElements(stepperSteps).size();
    }

    /**
     * V.11 Reports whether the shipment Tracking section is displayed (present
     * only once the order has shipped).
     *
     * @return true if the tracking section is present.
     */
    public boolean isTrackingSectionDisplayed() {
        return !driver.findElements(trackingSectionLabel).isEmpty();
    }

    /**
     * V.12 Verifies the "Contact support" link is present.
     *
     * @return true if the contact-support link is present.
     */
    public boolean isContactSupportPresent() {
        return !driver.findElements(contactSupportLink).isEmpty();
    }
}
