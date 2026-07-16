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
 * BEHAVIOR SPEC (product owner + live iframe capture, 2026-07-16):
 * <ul>
 * <li><b>Precondition:</b> the modal only opens when the cart holds AT LEAST 1
 * item.</li>
 * <li><b>DOMESTIC (India, INR):</b> cart "Slide to Pay" → the modal opens on
 * the <b>Review Order</b> step (selected address block + delivery options +
 * Continue {@code proceed-to-pay}) → <b>Payment Options</b> with the full
 * method rail (Recommended / UPI / Cards / EMI / Netbanking / Wallet + UPI QR
 * + offers). Contact details live in a POPUP opened from the "Using as
 * +91…" identity chip; the address switcher is opened from the address
 * block. Guests without a recognized identity get the Razorpay MAGIC-LINK
 * OTP prompt first (4 boxes, Skip/Resend — those locators remain VERIFY-tier;
 * the live capture ran with a recognized identity).</li>
 * <li><b>INTERNATIONAL:</b> "Slide to Pay" routes to the Timepieces
 * {@code /checkout} Delivery Information page; "Proceed to Payment" opens
 * this modal in a streamlined "Payment Options" mode whose rail holds ONLY
 * Recommended + Cards (UPI/EMI/Netbanking/Wallet are absent — verified);
 * the Cards pane offers Saved Cards, <b>Apple Pay</b> and "Add a new card"
 * (card form inputs carry {@code name="card.*"} attributes, no testids).</li>
 * <li><b>CLOSE SEMANTICS (verified):</b> esc/cancel do not close it; the X
 * ({@code checkout-close}) opens an EXIT-CONFIRMATION dialog — "Yes, exit"
 * ({@code confirm-positive}) closes the modal, "Continue" ({@code
 * confirm-negative}) stays in checkout.</li>
 * <li><b>PRICING:</b> international prices are 1.5× the domestic price for ALL
 * products; currencies: EUR (European countries), AED (UAE), USD (all other
 * supported countries). Unsupported countries are blocked on the /checkout
 * form ("Country/Region in the shipping address isn't supported").</li>
 * <li><b>IFRAME:</b> the checkout renders inside the CROSS-ORIGIN Razorpay
 * iframe ({@code iframe.razorpay-checkout-frame} within
 * {@code div.razorpay-container}, plus a {@code div.razorpay-backdrop}) —
 * call {@link #switchToCheckoutFrame()} before interacting with modal
 * internals and {@link #switchBackToStorefront()} after.</li>
 * </ul>
 *
 * <p>
 * LOCATOR SOURCE: Razorpay's REAL {@code data-testid}s captured live on
 * 2026-07-16 ({@code docs/scrape-artifacts/razorpay/}). Razorpay quirks kept
 * verbatim: the merchant logo testid is misspelled ({@code merchnatLogoImage})
 * and two controls use hyphenated {@code data-test-id}
 * ({@code country-code-selector}, {@code add-card-cta}). Only the magic-link
 * OTP block remains {@code VERIFY}-tier (not reachable in the captured
 * session).
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
    // The Razorpay overlay container + backdrop injected into the storefront DOM.
    private final By razorpayContainer = By.cssSelector("div.razorpay-container");
    private final By razorpayBackdrop = By.cssSelector("div.razorpay-backdrop");
    // The cross-origin checkout iframe hosting the whole modal UI.
    private final By checkoutIframe = By.cssSelector("iframe.razorpay-checkout-frame");

    // --- Inside the iframe: root + left brand panel ---
    private final By checkoutRoot = By.cssSelector("div[data-testid='checkout-v2-container']");
    // Razorpay's own misspelled testid — kept verbatim.
    private final By merchantLogo = By.cssSelector("img[data-testid='merchnatLogoImage']");
    private final By merchantName = By.cssSelector("div[data-testid='merchantName']");
    private final By orderSummaryButton = By.cssSelector("button[data-testid='order-summary-widget-multiple']");
    // The live amount (rolling digits) + its plain-text fallback.
    private final By orderAmount = By.cssSelector("div[data-testid='order-amount']");
    private final By orderAmountFallback = By.cssSelector("div[data-testid='order-amount-fallback']");
    private final By gstinButton = By.cssSelector("button[data-testid='gstin-button']");
    private final By orderInstructionsButton = By.cssSelector("button[data-testid='order-instructions-button']");
    private final By offersSubtext = By.cssSelector("span[data-testid='summary-offer-coupon-subtext']");
    private final By offersChevronButton = By.cssSelector("button[data-testid='summary-chevron-button']");
    // The identity chip ("Using as +91 …") — domestic; the intl variant differs.
    private final By identityChip = By.cssSelector("button[data-testid='overview-contact-cta']");
    private final By identityChipIntl = By.cssSelector("button[data-testid='contact-sidebar-widget']");
    private final By securedByRazorpay = By.cssSelector("div[data-testid='secured-by-razorpay']");

    // --- Step header chrome ---
    private final By stepTitle = By.cssSelector("span[data-testid='title']");
    private final By backButton = By.cssSelector("button[data-testid='nav-back']");
    private final By moreOptionsButton = By.cssSelector("button[data-testid='checkout-more-options']");
    private final By closeButton = By.cssSelector("button[data-testid='checkout-close']");

    // --- Exit-confirmation dialog (the X never closes directly) ---
    private final By exitDialogTitle = By.xpath("//h3[contains(normalize-space(),'Are you sure you want to exit?')]"); // text-anchor (no testid)
    private final By exitConfirmButton = By.cssSelector("button[data-testid='confirm-positive']");
    private final By exitStayButton = By.cssSelector("button[data-testid='confirm-negative']");

    // --- Review Order step (domestic step 1) ---
    private final By addressBlock = By.cssSelector("div[data-testid='address-block']");
    private final By deliveryCard = By.cssSelector("div[data-testid='delivery-card']");
    private final By deliveryOptionsList = By.cssSelector("div[data-testid='delivery-options-list']");
    private final By deliveryOptionItems = By.cssSelector("div[data-testid='delivery-options-listitem']");
    private final By proceedToPayButton = By.cssSelector("button[data-testid='proceed-to-pay']");

    // --- Address switcher ---
    private final By savedAddressItems = By.cssSelector("div[data-testid='saved-address-listitem']");
    private final By addNewAddressButton = By.cssSelector("button[name='add_new_address_cta']"); // name attr (no testid)
    private final By editAddressButton = By.cssSelector("button[data-testid='edit-address-btn']");
    private final By billingSameAsShipping = By.cssSelector("input#billing-same-as-shipping");
    private final By continueSelectedAddress = By.cssSelector("button[data-testid='continue-selected-address']");

    // --- Contact-details popup (identity chip) ---
    private final By contactPopupTitle = By.xpath("//h3[normalize-space()='Contact details']"); // text-anchor (no testid)
    // Razorpay uses the hyphenated data-test-id attribute here — kept verbatim.
    private final By countryCodeSelector = By.cssSelector("button[data-test-id='country-code-selector']");
    private final By contactNumberInput = By.cssSelector("input[data-testid='contactNumber']");
    private final By contactEmailInput = By.cssSelector("input[data-testid='email']");
    private final By contactContinueButton = By.cssSelector("button[validateform='true']"); // attribute (no testid)

    // --- Magic-link OTP prompt (guest identity; NOT reached in the live
    // capture — still VERIFY-tier from the checkout screenshots) ---
    private final By otpPromptHeading = By.xpath("//*[contains(normalize-space(),'Enter OTP sent to')]"); // VERIFY: text-anchor
    private final By otpDigitInputs = By.xpath("//*[contains(normalize-space(),'Enter OTP sent to')]/following::input[@type='tel' or @type='text' or @type='number'][position()<=4]"); // VERIFY: structural
    private final By skipOtpButton = By.xpath("//*[normalize-space()='Skip OTP'][self::button or self::span or self::div]"); // VERIFY: text-anchor
    private final By resendOtpButton = By.xpath("//*[contains(normalize-space(),'Resend OTP')]"); // VERIFY: text-anchor

    // --- Payment Options: the method rail ---
    private final By methodsRail = By.cssSelector("div[data-testid='nav-sidebar']");
    private final By railRecommended = By.cssSelector("div[data-testid='recommended']");
    private final By railUpi = By.cssSelector("div[data-testid='upi']");
    private final By railCards = By.cssSelector("div[data-testid='card']");
    private final By railEmi = By.cssSelector("div[data-testid='emi']");
    private final By railNetbanking = By.cssSelector("div[data-testid='netbanking']");
    private final By railWallet = By.cssSelector("div[data-testid='wallet']");
    private final By inlineOfferBadges = By.cssSelector("div[data-testid='inline-offer-text']");

    // --- Payment Options: right-pane blocks ---
    private final By offersContainer = By.cssSelector("div[data-testid='offer-container']");
    private final By offerCards = By.cssSelector("div[data-testid^='offer-card-offer_']");
    private final By viewAllOffersButton = By.cssSelector("button[data-testid='view-all-offers']");
    private final By upiQrContainer = By.cssSelector("div[data-testid='upi-qr-container']");

    // --- International Cards pane: Apple Pay + add-a-new-card ---
    private final By applePayButton = By.xpath("//button[contains(normalize-space(),'Apple Pay')]"); // text-anchor (no testid)
    private final By addNewCardButton = By.xpath("//button[contains(normalize-space(),'Add a new card')]"); // text-anchor (no testid)
    private final By cardNumberInput = By.cssSelector("input[name='card.number']"); // name attr (no testid)
    private final By cardExpiryInput = By.cssSelector("input[name='card.expiry']"); // name attr (no testid)
    private final By cardCvvInput = By.cssSelector("input[name='card.cvv']"); // name attr (no testid)
    private final By cardNameInput = By.cssSelector("input[name='card.name']"); // name attr (no testid)
    private final By saveCardCheckbox = By.cssSelector("input[data-testid='save-card-checkbox']");
    // Razorpay's hyphenated data-test-id again — kept verbatim.
    private final By addCardContinueButton = By.cssSelector("button[data-test-id='add-card-cta']");

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
     * B.2 Switches the driver INTO the (cross-origin) Razorpay checkout iframe.
     * Every modal-internal method below assumes this has been called.
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
     * B.4 Closes the modal COMPLETELY: clicks the X ({@code checkout-close}),
     * confirms "Yes, exit" in the exit dialog, then restores the storefront
     * context. (esc/cancel never close it; the X alone only opens the dialog.)
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent closeModal() {
        log.info("Closing the Razorpay modal (X → exit confirmation → Yes, exit).");
        clickElement(closeButton);
        clickElement(exitConfirmButton);
        switchBackToStorefront();
        return this;
    }

    /**
     * B.5 Dismisses the exit-confirmation dialog and STAYS in checkout
     * ("Continue shopping" / negative option).
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent stayInCheckout() {
        log.info("Dismissing the exit dialog — staying in checkout.");
        clickElement(exitStayButton);
        return this;
    }

    /**
     * B.6 Clicks the X only (opens the exit-confirmation dialog without
     * confirming).
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent clickClose() {
        log.info("Clicking the Razorpay X (opens the exit confirmation).");
        clickElement(closeButton);
        return this;
    }

    /**
     * B.7 Continues from the Review Order step to Payment Options
     * ({@code proceed-to-pay}).
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent clickReviewOrderContinue() {
        log.info("Continuing from Review Order to Payment Options.");
        clickElement(proceedToPayButton);
        return this;
    }

    /**
     * B.8 Opens the address switcher by clicking the selected-address block.
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent openAddressSwitcher() {
        log.info("Opening the Razorpay address switcher.");
        clickElement(addressBlock);
        return this;
    }

    /**
     * B.9 Confirms the currently selected saved address
     * ({@code continue-selected-address}) and returns to Review Order.
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent continueWithSelectedAddress() {
        log.info("Continuing with the selected saved address.");
        clickElement(continueSelectedAddress);
        return this;
    }

    /**
     * B.10 Opens the contact-details popup from the "Using as +91…" identity
     * chip and fills mobile + email.
     *
     * @param mobile the mobile number (country code preset via its selector).
     * @param email  the email address.
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent updateContactDetails(String mobile, String email) {
        log.info("Updating contact details via the identity chip popup.");
        clickElement(identityChip);
        sendKeys(contactNumberInput, mobile);
        sendKeys(contactEmailInput, email);
        clickElement(contactContinueButton);
        return this;
    }

    /**
     * B.11 Enters the 4-digit magic-link OTP (one digit per box; guest identity
     * path — VERIFY-tier locators).
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
     * B.12 Skips the magic-link OTP (guest continues without the saved-address
     * fill; VERIFY-tier locator).
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent clickSkipOtp() {
        log.info("Skipping the Razorpay magic-link OTP.");
        clickElement(skipOtpButton);
        return this;
    }

    /**
     * B.13 Selects a payment-method rail entry.
     * <p>
     * PAYMENT SAFETY: no method in this component ever completes a payment.
     * Verification payments are made ONLY with Razorpay test cards, for ₹1, for
     * test customers — see the class-level warning.
     *
     * @param method one of: Recommended, UPI, Cards, EMI, Netbanking, Wallet.
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent selectPaymentMethod(String method) {
        log.info("Selecting the Razorpay payment rail entry: [{}]", method);
        clickElement(railFor(method));
        return this;
    }

    /**
     * B.14 Opens the "Add a new card" form (international Cards pane). Card
     * fields are filled via the {@code card.*} name-attribute inputs; the form
     * is submitted with {@code add-card-cta} — NEVER call that against a real
     * card (Razorpay test cards only).
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent openAddNewCardForm() {
        log.info("Opening the 'Add a new card' form.");
        clickElement(addNewCardButton);
        return this;
    }

    /**
     * B.15 Fills the add-a-new-card form (Razorpay TEST cards only — see the
     * class-level payment-safety warning). Does not submit.
     *
     * @param number the card number (Razorpay test card).
     * @param expiry the MM/YY expiry.
     * @param cvv    the CVV.
     * @param name   the cardholder name.
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent fillNewCardForm(String number, String expiry, String cvv, String name) {
        log.info("Filling the add-a-new-card form (test card).");
        sendKeys(cardNumberInput, number);
        sendKeys(cardExpiryInput, expiry);
        sendKeys(cardCvvInput, cvv);
        sendKeys(cardNameInput, name);
        return this;
    }

    /**
     * B.15b Submits the add-a-new-card form via the {@code add-card-cta}.
     * <p>
     * WARNING: this ATTEMPTS the payment/tokenization. Only ever call it with
     * Razorpay TEST cards for the sanctioned decline/₹1 verification scenarios —
     * see the class-level payment-safety warning.
     *
     * @return this component instance for method chaining.
     */
    public RazorpayModalComponent submitNewCardForm() {
        log.warn("Submitting the Razorpay card form — TEST cards only (sanctioned verification).");
        clickElement(addCardContinueButton);
        return this;
    }

    /**
     * B.15c Verifies a message text renders anywhere inside the checkout iframe
     * (e.g. a decline reason like "Insufficient Funds"). Frame context required.
     *
     * @param message the message text (or a distinctive fragment).
     * @return true if the message is displayed.
     */
    public boolean isMessageDisplayed(String message) {
        By text = By.xpath(String.format("//*[contains(normalize-space(),'%s')]", message));
        return !driver.findElements(text).isEmpty();
    }

    /**
     * B.16 Returns the order amount (prefers the plain-text fallback node; the
     * primary node renders rolling digit animations).
     *
     * @return the trimmed order-amount text (e.g. "₹11,000").
     */
    public String getOrderAmountText() {
        if (!driver.findElements(orderAmountFallback).isEmpty()) {
            return getText(orderAmountFallback);
        }
        return getText(orderAmount);
    }

    /**
     * B.17 Returns the offers subtext from the left panel (e.g. "7 offers
     * available").
     *
     * @return the trimmed offers subtext.
     */
    public String getOffersSubtext() {
        return getText(offersSubtext);
    }

    /**
     * B.18 Returns the current step title (e.g. "Review Order", "Payment
     * Options").
     *
     * @return the trimmed step title.
     */
    public String getStepTitle() {
        return getText(stepTitle);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Reports whether the Razorpay modal is open (container + backdrop +
     * iframe present in the storefront DOM). Call from the DEFAULT content.
     *
     * @return true if the modal overlay is present.
     */
    public boolean isModalOpen() {
        return !driver.findElements(razorpayContainer).isEmpty()
                && !driver.findElements(checkoutIframe).isEmpty()
                && !driver.findElements(razorpayBackdrop).isEmpty();
    }

    /**
     * V.2 Verifies the checkout root + merchant branding rendered inside the
     * iframe (Timepieces logo/name + Secured by Razorpay).
     *
     * @return true if the branded checkout shell is displayed.
     */
    public boolean isCheckoutShellDisplayed() {
        return !driver.findElements(checkoutRoot).isEmpty()
                && !driver.findElements(merchantName).isEmpty()
                && !driver.findElements(merchantLogo).isEmpty()
                && !driver.findElements(securedByRazorpay).isEmpty();
    }

    /**
     * V.3 Verifies the domestic left panel extras (order summary + GST + order
     * instructions + offers row).
     *
     * @return true if the domestic brand-panel rows are present.
     */
    public boolean isDomesticBrandPanelDisplayed() {
        return !driver.findElements(orderSummaryButton).isEmpty()
                && !driver.findElements(gstinButton).isEmpty()
                && !driver.findElements(orderInstructionsButton).isEmpty()
                && !driver.findElements(offersChevronButton).isEmpty();
    }

    /**
     * V.4 Verifies the Review Order step is displayed (address block + delivery
     * options + Continue).
     *
     * @return true if the Review Order step is displayed.
     */
    public boolean isReviewOrderDisplayed() {
        return !driver.findElements(addressBlock).isEmpty()
                && !driver.findElements(deliveryCard).isEmpty()
                && !driver.findElements(deliveryOptionsList).isEmpty()
                && !driver.findElements(proceedToPayButton).isEmpty();
    }

    /**
     * V.5 Counts the delivery options offered on Review Order.
     *
     * @return the number of delivery option rows.
     */
    public int getDeliveryOptionCount() {
        return driver.findElements(deliveryOptionItems).size();
    }

    /**
     * V.6 Counts the saved addresses in the address switcher.
     *
     * @return the number of saved-address items.
     */
    public int getSavedAddressCount() {
        return driver.findElements(savedAddressItems).size();
    }

    /**
     * V.7 Verifies the address switcher chrome (add-new + edit + billing-same
     * checkbox) is present.
     *
     * @return true if the switcher controls are present.
     */
    public boolean isAddressSwitcherDisplayed() {
        return !driver.findElements(savedAddressItems).isEmpty()
                && !driver.findElements(addNewAddressButton).isEmpty()
                && !driver.findElements(editAddressButton).isEmpty()
                && !driver.findElements(billingSameAsShipping).isEmpty();
    }

    /**
     * V.8 Verifies the contact-details popup is displayed (title + country code
     * + number + email).
     *
     * @return true if the contact popup is displayed.
     */
    public boolean isContactPopupDisplayed() {
        return !driver.findElements(contactPopupTitle).isEmpty()
                && !driver.findElements(countryCodeSelector).isEmpty()
                && !driver.findElements(contactNumberInput).isEmpty()
                && !driver.findElements(contactEmailInput).isEmpty();
    }

    /**
     * V.9 Verifies the guest magic-link OTP prompt is displayed (VERIFY-tier
     * locators — see the class Javadoc).
     *
     * @return true if the OTP prompt is displayed.
     */
    public boolean isOtpPromptDisplayed() {
        return !driver.findElements(otpPromptHeading).isEmpty()
                && !driver.findElements(skipOtpButton).isEmpty()
                && !driver.findElements(resendOtpButton).isEmpty();
    }

    /**
     * V.10 Verifies a payment rail entry is offered.
     *
     * @param method one of: Recommended, UPI, Cards, EMI, Netbanking, Wallet.
     * @return true if the rail entry is present.
     */
    public boolean isPaymentMethodOffered(String method) {
        return !driver.findElements(railFor(method)).isEmpty();
    }

    /**
     * V.11 Verifies the DOMESTIC Payment Options rail is complete
     * (Recommended + UPI + Cards + EMI + Netbanking + Wallet + the UPI QR
     * block).
     *
     * @return true if the full domestic rail is offered.
     */
    public boolean isDomesticPaymentRailComplete() {
        return !driver.findElements(methodsRail).isEmpty()
                && isPaymentMethodOffered("Recommended")
                && isPaymentMethodOffered("UPI")
                && isPaymentMethodOffered("Cards")
                && isPaymentMethodOffered("EMI")
                && isPaymentMethodOffered("Netbanking")
                && isPaymentMethodOffered("Wallet")
                && !driver.findElements(upiQrContainer).isEmpty();
    }

    /**
     * V.12 Verifies the INTERNATIONAL "Payment Options" mode: the rail holds
     * Recommended + Cards ONLY (UPI/EMI/Netbanking/Wallet absent — the
     * two-method-family rule), with Apple Pay + Add-a-new-card in the Cards
     * pane and the streamlined identity widget.
     *
     * @return true if the international mode renders correctly.
     */
    public boolean isInternationalPaymentOptionsMode() {
        boolean railLimited = !driver.findElements(methodsRail).isEmpty()
                && isPaymentMethodOffered("Recommended")
                && isPaymentMethodOffered("Cards")
                && driver.findElements(railUpi).isEmpty()
                && driver.findElements(railEmi).isEmpty()
                && driver.findElements(railNetbanking).isEmpty()
                && driver.findElements(railWallet).isEmpty();
        boolean cardsPane = !driver.findElements(applePayButton).isEmpty()
                && !driver.findElements(addNewCardButton).isEmpty();
        return railLimited && cardsPane;
    }

    /**
     * V.13 Verifies the offers panel is present (container + at least one offer
     * card + view-all).
     *
     * @return true if offers render.
     */
    public boolean areOffersDisplayed() {
        return !driver.findElements(offersContainer).isEmpty()
                && !driver.findElements(offerCards).isEmpty()
                && !driver.findElements(viewAllOffersButton).isEmpty();
    }

    /**
     * V.14 Counts the inline offer badges on the method rail.
     *
     * @return the number of inline offer badges.
     */
    public int getInlineOfferBadgeCount() {
        return driver.findElements(inlineOfferBadges).size();
    }

    /**
     * V.15 Verifies the identity chip is shown ("Using as +91…" — domestic or
     * the international sidebar variant).
     *
     * @return true if an identity chip is present.
     */
    public boolean isIdentityChipDisplayed() {
        return !driver.findElements(identityChip).isEmpty()
                || !driver.findElements(identityChipIntl).isEmpty();
    }

    /**
     * V.16 Verifies the exit-confirmation dialog is displayed (after clicking
     * the X).
     *
     * @return true if the exit dialog is displayed.
     */
    public boolean isExitDialogDisplayed() {
        return !driver.findElements(exitDialogTitle).isEmpty()
                && !driver.findElements(exitConfirmButton).isEmpty()
                && !driver.findElements(exitStayButton).isEmpty();
    }

    /**
     * V.17 Verifies the add-a-new-card form rendered (all four card.* inputs +
     * save-card checkbox + the add-card CTA).
     *
     * @return true if the new-card form is displayed.
     */
    public boolean isNewCardFormDisplayed() {
        return !driver.findElements(cardNumberInput).isEmpty()
                && !driver.findElements(cardExpiryInput).isEmpty()
                && !driver.findElements(cardCvvInput).isEmpty()
                && !driver.findElements(cardNameInput).isEmpty()
                && !driver.findElements(saveCardCheckbox).isEmpty()
                && !driver.findElements(addCardContinueButton).isEmpty();
    }

    /**
     * V.18 Reports whether the back control is available in the step header.
     *
     * @return true if the back control is present.
     */
    public boolean isBackAvailable() {
        return !driver.findElements(backButton).isEmpty();
    }

    /**
     * V.19 Reports whether the more-options (three dots) control is present.
     *
     * @return true if the more-options control is present.
     */
    public boolean isMoreOptionsAvailable() {
        return !driver.findElements(moreOptionsButton).isEmpty();
    }

    // ==========================================
    // PRIVATE HELPERS (rail lookup)
    // ==========================================
    /**
     * P.1 Maps a rail label to its Razorpay testid locator.
     *
     * @param method the rail label.
     * @return the rail entry locator.
     */
    private By railFor(String method) {
        switch (method.toLowerCase()) {
            case "recommended":
                return railRecommended;
            case "upi":
                return railUpi;
            case "cards":
            case "card":
                return railCards;
            case "emi":
                return railEmi;
            case "netbanking":
                return railNetbanking;
            case "wallet":
                return railWallet;
            default:
                throw new IllegalArgumentException("Unknown Razorpay payment rail: " + method);
        }
    }
}
