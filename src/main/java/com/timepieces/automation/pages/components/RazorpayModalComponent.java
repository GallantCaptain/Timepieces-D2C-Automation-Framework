package com.timepieces.automation.pages.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The Razorpay Modal Component is called for the payment step for both domestic
 * and in international payments, although the prior steps to get to this
 * differ. Also, for international payments only Apple Pay in USD, Euro, Dirham
 * are supported currently, while INR is the default currency for domestic
 * customers. The Razorpay Modal already has data-testid as there is nothing for
 * Timepieces to implement. However, for automation, the locators will be used to
 * ensure that the modal is displayed and closed properly. The modal cannot be
 * closed using the esc key or the cancel button.
 *
 * Furthermore, payments can be verified for domestic and international
 * customers using the test credit card details provided by Razorpay. The
 * payment should be verified only for the test customers, not for the
 * actual customers. Also, for automation purposes, the payment is only made
 * for the amount ₹1, which is the minimum amount that can be paid.
 *
 * This payment is only for verification purposes. The actual payment is
 * handled by Razorpay.
 *
 * <p>
 * BEHAVIOR SPEC (product owner, 2026-07-16 + checkout screenshots):
 * <ul>
 * <li><b>Precondition:</b> the modal only opens when the cart holds AT LEAST 1
 * item.</li>
 * <li><b>DOMESTIC (India, INR):</b> cart drawer "Slide to Pay" → the Razorpay
 * MAGIC CHECKOUT modal opens with the step rail Contact → Address → Payment.
 * Logged-in / saved customers proceed straight to payment. GUESTS enter
 * mobile+email on the Contact step, which triggers the Razorpay MAGIC-LINK
 * OTP prompt ("We can help fill your address" / "Enter OTP sent to +91…", 4
 * boxes, Skip OTP, Resend); completing it surfaces the addresses saved
 * against the magic-link identity (Home/Office cards, Standard FREE
 * shipping). Domestic payment methods: UPI (incl. QR), Cards, EMI,
 * Netbanking, Wallet + offers + recommended saved card.</li>
 * <li><b>INTERNATIONAL:</b> "Slide to Pay" instead routes to the Timepieces
 * {@code /checkout} Delivery Information page; "Proceed to Payment" there
 * opens this modal in its compact "Payment Options" mode with ONLY two
 * method families: CREDIT CARD (saved cards / add a new card) and APPLE
 * PAY.</li>
 * <li><b>PRICING:</b> international prices are 1.5× the domestic price for ALL
 * products; currencies: EUR (European countries), AED (UAE), USD (all other
 * supported countries). Unsupported countries (e.g. Afghanistan) must NOT
 * allow purchases.</li>
 * <li><b>IFRAME:</b> the checkout renders inside the Razorpay iframe
 * ({@code razorpay-checkout-frame} within {@code div.razorpay-container}) —
 * call {@link #switchToCheckoutFrame()} before interacting with modal
 * internals and {@link #switchBackToStorefront()} after.</li>
 * </ul>
 *
 * <p>
 * LOCATOR TIER — VERIFY: the modal DOM is Razorpay-owned (their data-testids
 * exist but were captured from screenshots, not a live DOM dump), so the inner
 * locators below are best-effort text/type anchors tagged {@code // VERIFY}.
 * TASK-PDP-010 tracks the one-time live capture (open the modal with 1 cart
 * item, dump the iframe DOM) to swap these for Razorpay's real testids.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (RazorpayModalComponent) to allow method chaining in step definitions.
 *
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of RazorpayModalComponent"
 */

@Log4j2
public class RazorpayModalComponent extends BasePage<RazorpayModalComponent> {

    // ==========================================
    // Constructor
    // ==========================================
    public RazorpayModalComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // --- Top document (storefront context — do NOT switch frames for these) ---
    // The Razorpay overlay container injected into the storefront DOM.
    private final By razorpayContainer = By.cssSelector("div.razorpay-container"); // VERIFY: Razorpay-owned
    // The checkout iframe that hosts the whole modal UI.
    private final By checkoutIframe = By.cssSelector("iframe[class*='razorpay-checkout-frame'], div.razorpay-container iframe"); // VERIFY: Razorpay-owned

    // --- Inside the checkout iframe: shared chrome ---
    // The Contact → Address → Payment step rail entries.
    private final By stepTabTemplate = By.xpath("//*[normalize-space()='%s'][self::button or self::a or self::span or self::div]"); // VERIFY: text-anchor
    // The modal close (X). NOTE: esc and any cancel affordance do NOT close it.
    private final By closeButton = By.xpath("//button[@aria-label='Close' or normalize-space()='×' or normalize-space()='✕']"); // VERIFY: aria/text
    // The back arrow (appears from the Address step onward).
    private final By backButton = By.xpath("//button[@aria-label='Back']"); // VERIFY: aria
    // The left brand panel rows.
    private final By orderSummaryRow = By.xpath("//*[contains(normalize-space(),'Order summary')]"); // VERIFY: text-anchor
    private final By couponsRow = By.xpath("//*[contains(normalize-space(),'Coupons and offers')]"); // VERIFY: text-anchor
    private final By usingAsChip = By.xpath("//*[contains(normalize-space(),'Using as +91')]"); // VERIFY: text-anchor
    private final By securedByRazorpay = By.xpath("//*[contains(normalize-space(),'Secured by')]"); // VERIFY: text-anchor
    // The primary Continue CTA (shared across Contact/OTP/Address steps).
    private final By continueButton = By.xpath("//button[normalize-space()='Continue']"); // VERIFY: text-anchor

    // --- Contact step (domestic guests) ---
    private final By contactHeading = By.xpath("//*[normalize-space()='Contact details']"); // VERIFY: text-anchor
    private final By mobileInput = By.cssSelector("input[type='tel']"); // VERIFY: semantic
    private final By emailInput = By.cssSelector("input[type='email']"); // VERIFY: semantic
    private final By offersCheckbox = By.cssSelector("input[type='checkbox']"); // VERIFY: semantic

    // --- Magic-link OTP prompt ---
    private final By otpPromptHeading = By.xpath("//*[contains(normalize-space(),'Enter OTP sent to')]"); // VERIFY: text-anchor
    private final By addressesFoundBanner = By.xpath("//*[contains(normalize-space(),'addresses found')]"); // VERIFY: text-anchor
    // The 4 single-digit OTP boxes.
    private final By otpDigitInputs = By.xpath("//*[contains(normalize-space(),'Enter OTP sent to')]/following::input[@type='tel' or @type='text' or @type='number'][position()<=4]"); // VERIFY: structural
    private final By skipOtpButton = By.xpath("//*[normalize-space()='Skip OTP'][self::button or self::span or self::div]"); // VERIFY: text-anchor
    private final By resendOtpButton = By.xpath("//*[contains(normalize-space(),'Resend OTP')]"); // VERIFY: text-anchor

    // --- Address step (magic-link saved addresses) ---
    // Dynamic: a saved-address card by its label (e.g. "Home", "Office").
    private final By addressCardTemplate = By.xpath("//*[normalize-space()='%s']/ancestor::*[contains(@class,'card') or (self::div and .//input) or self::label][1]"); // VERIFY: structural
    private final By addressCardLabels = By.xpath("//*[normalize-space()='Home' or normalize-space()='Office']"); // VERIFY: text-anchor
    private final By standardShippingRow = By.xpath("//*[contains(normalize-space(),'Standard')]"); // VERIFY: text-anchor

    // --- Payment step (domestic full method rail) ---
    // Dynamic: a payment-method rail entry by label (UPI / Cards / EMI / Netbanking / Wallet).
    private final By paymentMethodTemplate = By.xpath("//*[normalize-space()='%s'][self::button or self::div or self::span or self::a]"); // VERIFY: text-anchor
    private final By upiQrBlock = By.xpath("//*[contains(normalize-space(),'UPI QR') or contains(normalize-space(),'Show QR')]"); // VERIFY: text-anchor
    private final By availableOffers = By.xpath("//*[contains(normalize-space(),'Available Offers')]"); // VERIFY: text-anchor

    // --- "Payment Options" mode (INTERNATIONAL: cards + Apple Pay ONLY) ---
    private final By paymentOptionsTitle = By.xpath("//*[normalize-space()='Payment Options']"); // VERIFY: text-anchor
    private final By savedCardsSection = By.xpath("//*[normalize-space()='Saved Cards']"); // VERIFY: text-anchor
    private final By applePayRow = By.xpath("//*[contains(normalize-space(),'Apple Pay')]"); // VERIFY: text-anchor
    private final By addNewCardRow = By.xpath("//*[contains(normalize-space(),'Add a new card')]"); // VERIFY: text-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Waits for the Razorpay overlay + iframe to appear in the storefront DOM
     * (call from the DEFAULT content, right after "Slide to Pay" / "Proceed to
     * Payment"). Remember: the modal only opens when the cart holds ≥1 item.
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent waitForModalOpen() {
        log.info("Waiting for the Razorpay checkout modal (container + iframe).");
        waitForVisibility(checkoutIframe);
        return this;
    }

    /**
     * B.2 Switches the driver INTO the Razorpay checkout iframe. Every
     * modal-internal method below assumes this has been called.
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent switchToCheckoutFrame() {
        log.info("Switching into the Razorpay checkout iframe.");
        driver.switchTo().frame(getElementPresence(checkoutIframe));
        return this;
    }

    /**
     * B.3 Switches the driver BACK to the storefront document.
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent switchBackToStorefront() {
        log.info("Switching back to the storefront document.");
        driver.switchTo().defaultContent();
        return this;
    }

    /**
     * B.4 Closes the modal via its X control (esc and cancel do NOT close it).
     * Assumes the frame context; restores the storefront context afterwards.
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent closeModal() {
        log.info("Closing the Razorpay modal via its X control.");
        clickElement(closeButton);
        switchBackToStorefront();
        return this;
    }

    /**
     * B.5 Fills the guest Contact step (mobile + email). The offers checkbox is
     * left in its default state.
     *
     * @param mobile the 10-digit mobile number (the +91 prefix is preset).
     * @param email  the email address.
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent enterContactDetails(String mobile, String email) {
        log.info("Filling the Razorpay Contact step.");
        sendKeys(mobileInput, mobile);
        sendKeys(emailInput, email);
        return this;
    }

    /**
     * B.6 Toggles the "Send me offers and order updates" checkbox.
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent toggleOffersCheckbox() {
        clickElement(offersCheckbox);
        return this;
    }

    /**
     * B.7 Clicks the primary Continue CTA (Contact / OTP / Address steps).
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent clickContinue() {
        log.info("Clicking Continue in the Razorpay modal.");
        clickElement(continueButton);
        return this;
    }

    /**
     * B.8 Enters the 4-digit magic-link OTP (one digit per box).
     *
     * @param otp the 4-digit OTP.
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent enterMagicLinkOtp(String otp) {
        log.info("Entering the Razorpay magic-link OTP.");
        List<WebElement> boxes = getElements(otpDigitInputs);
        for (int i = 0; i < otp.length() && i < boxes.size(); i++) {
            boxes.get(i).sendKeys(String.valueOf(otp.charAt(i)));
        }
        return this;
    }

    /**
     * B.9 Skips the magic-link OTP (guest continues without the saved-address
     * fill).
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent clickSkipOtp() {
        log.info("Skipping the Razorpay magic-link OTP.");
        clickElement(skipOtpButton);
        return this;
    }

    /**
     * B.10 Selects a magic-link saved-address card by its label (e.g. "Home",
     * "Office").
     *
     * @param label the address card label.
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent selectAddressCard(String label) {
        log.info("Selecting the Razorpay saved-address card: [{}]", label);
        clickElement(formatLocator(addressCardTemplate, label));
        return this;
    }

    /**
     * B.11 Opens a domestic payment-method rail entry by label (UPI / Cards /
     * EMI / Netbanking / Wallet).
     * <p>
     * PAYMENT SAFETY: no method in this component ever completes a payment.
     * Verification payments are made ONLY with Razorpay test cards, for ₹1, for
     * test customers — see the class-level warning.
     *
     * @param methodLabel the payment-method rail label.
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent selectPaymentMethod(String methodLabel) {
        log.info("Selecting the Razorpay payment method: [{}]", methodLabel);
        clickElement(formatLocator(paymentMethodTemplate, methodLabel));
        return this;
    }

    /**
     * B.12 Returns the order-summary line text from the left brand panel (e.g.
     * "Order summary • 2 items").
     *
     * @return the trimmed order-summary text.
     */
    public String getOrderSummaryText() {
        return getText(orderSummaryRow);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Reports whether the Razorpay modal is open (container + iframe present
     * in the storefront DOM). Call from the DEFAULT content.
     *
     * @return true if the modal overlay is present.
     */
    public boolean isModalOpen() {
        return !driver.findElements(razorpayContainer).isEmpty()
                && !driver.findElements(checkoutIframe).isEmpty();
    }

    /**
     * V.2 Verifies a step-rail entry ("Contact" / "Address" / "Payment") is
     * displayed (frame context).
     *
     * @param stepLabel the step label.
     * @return true if the step tab is present.
     */
    public boolean isStepDisplayed(String stepLabel) {
        return !driver.findElements(formatLocator(stepTabTemplate, stepLabel)).isEmpty();
    }

    /**
     * V.3 Verifies the guest Contact step is displayed (heading + mobile + email).
     *
     * @return true if the Contact step is displayed.
     */
    public boolean isContactStepDisplayed() {
        return !driver.findElements(contactHeading).isEmpty()
                && !driver.findElements(mobileInput).isEmpty()
                && !driver.findElements(emailInput).isEmpty();
    }

    /**
     * V.4 Verifies the magic-link OTP prompt is displayed (with Skip + Resend).
     *
     * @return true if the OTP prompt is displayed.
     */
    public boolean isOtpPromptDisplayed() {
        return !driver.findElements(otpPromptHeading).isEmpty()
                && !driver.findElements(skipOtpButton).isEmpty()
                && !driver.findElements(resendOtpButton).isEmpty();
    }

    /**
     * V.5 Verifies the magic-link "N addresses found" banner rendered.
     *
     * @return true if the addresses-found banner is present.
     */
    public boolean isAddressesFoundBannerDisplayed() {
        return !driver.findElements(addressesFoundBanner).isEmpty();
    }

    /**
     * V.6 Counts the saved-address card labels (Home/Office) on the Address step.
     *
     * @return the number of labelled address cards.
     */
    public int getAddressCardCount() {
        return driver.findElements(addressCardLabels).size();
    }

    /**
     * V.7 Verifies the Standard (FREE) shipping row is present on the Address
     * step.
     *
     * @return true if the standard-shipping row is present.
     */
    public boolean isStandardShippingDisplayed() {
        return !driver.findElements(standardShippingRow).isEmpty();
    }

    /**
     * V.8 Verifies a domestic payment method is offered on the Payment rail.
     *
     * @param methodLabel the rail label (UPI / Cards / EMI / Netbanking / Wallet).
     * @return true if the method is offered.
     */
    public boolean isPaymentMethodOffered(String methodLabel) {
        return !driver.findElements(formatLocator(paymentMethodTemplate, methodLabel)).isEmpty();
    }

    /**
     * V.9 Verifies the domestic Payment step shows the full method rail
     * (UPI + Cards + EMI + Netbanking + Wallet) plus the UPI QR block.
     *
     * @return true if the full domestic rail is offered.
     */
    public boolean isDomesticPaymentRailComplete() {
        return isPaymentMethodOffered("UPI")
                && isPaymentMethodOffered("Cards")
                && isPaymentMethodOffered("EMI")
                && isPaymentMethodOffered("Netbanking")
                && isPaymentMethodOffered("Wallet")
                && !driver.findElements(upiQrBlock).isEmpty();
    }

    /**
     * V.10 Verifies the INTERNATIONAL "Payment Options" mode: Saved Cards +
     * Apple Pay + Add-a-new-card are offered, and NONE of the domestic-only
     * families (UPI / EMI / Netbanking / Wallet) appear. This is the
     * two-method-family rule for international purchases.
     *
     * @return true if only the card + Apple Pay families are offered.
     */
    public boolean isInternationalPaymentOptionsMode() {
        boolean intlSurface = !driver.findElements(paymentOptionsTitle).isEmpty()
                && !driver.findElements(savedCardsSection).isEmpty()
                && !driver.findElements(applePayRow).isEmpty()
                && !driver.findElements(addNewCardRow).isEmpty();
        boolean noDomesticOnly = driver.findElements(formatLocator(paymentMethodTemplate, "UPI")).isEmpty()
                && driver.findElements(formatLocator(paymentMethodTemplate, "EMI")).isEmpty()
                && driver.findElements(formatLocator(paymentMethodTemplate, "Netbanking")).isEmpty()
                && driver.findElements(formatLocator(paymentMethodTemplate, "Wallet")).isEmpty();
        return intlSurface && noDomesticOnly;
    }

    /**
     * V.11 Verifies the offers block is present on the domestic Payment step.
     *
     * @return true if "Available Offers" is present.
     */
    public boolean areOffersDisplayed() {
        return !driver.findElements(availableOffers).isEmpty();
    }

    /**
     * V.12 Verifies the left panel chrome (order summary + coupons + Secured by
     * Razorpay).
     *
     * @return true if the brand-panel rows are present.
     */
    public boolean isBrandPanelDisplayed() {
        return !driver.findElements(orderSummaryRow).isEmpty()
                && !driver.findElements(couponsRow).isEmpty()
                && !driver.findElements(securedByRazorpay).isEmpty();
    }

    /**
     * V.13 Verifies the "Using as +91…" identity chip is shown (the shopper is
     * recognized by the magic-link identity).
     *
     * @return true if the identity chip is present.
     */
    public boolean isIdentityChipDisplayed() {
        return !driver.findElements(usingAsChip).isEmpty();
    }

    /**
     * V.14 Reports whether the back arrow is available (Address step onward).
     *
     * @return true if the back control is present.
     */
    public boolean isBackAvailable() {
        return !driver.findElements(backButton).isEmpty();
    }
}
