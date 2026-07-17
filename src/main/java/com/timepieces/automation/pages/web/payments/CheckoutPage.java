package com.timepieces.automation.pages.web.payments;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.HeaderOnlyBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * www.timepieces-d2c.example/checkout is the checkout page for international deliveries,
 * which involves the Razorpay payment gateway. It is important to note that the
 * checkout page is only relevant for international deliveries, as domestic
 * deliveries within India do not utilize the Razorpay payment gateway.
 * The RazorpayModalComponent class represents the Razorpay payment modal that
 * appears during the checkout process.
 * It provides methods to interact with the modal, such as entering payment
 * details and confirming the payment.
 * This class is designed to be used within the CheckoutPage class, allowing for
 * a clean separation of concerns and better organization of page objects.
 * The checkout page is called when the user selects delivery outside of India
 * (domestic). It is mainly concerned with the international payment flow, which
 * involves the Razorpay payment gateway.
 * The RazorpayModalComponent encapsulates the interactions with the Razorpay
 * modal, making it easier to manage and maintain the code related to the
 * payment process.
 * After the user has filled the relevant details on the checkout page and
 * proceeds to payment, the Razorpay modal will be triggered, and the methods in
 * this class can be used to handle the payment process within the tests.
 * If there is at least 1 item, the user can skip the RazorpayModal and go to
 * the checkout page by pasting the url. However, for payment, the
 * RazorpayModalComponent is triggered again.
 * The RazorpayModalComponent is the final gateway to make any purchases from
 * the website (domestic and international).
* @Description "Description of CheckoutPage"
* @since 2026-07-03
* @version 1.0
* @author Avinash Rath
 */
@Log4j2
public class CheckoutPage extends HeaderOnlyBasePage<CheckoutPage> {

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    /*
     * Wait for the checkout page to load - This method should be implemented to
     * ensure that the page is fully loaded before any interactions are attempted.
     * You can use WebDriver's wait mechanisms to wait for specific elements that
     * indicate the page has loaded, such as a unique element on the checkout page.
     * For example, you might wait for a form or a specific button that is only
     * present on the checkout page.
     * The actual implementation will depend on the structure of your checkout page
     * and the elements it contains.
     * Make sure to replace "checkout-page-identifier" with the actual identifier of
     * an element that confirms the page has loaded.
     */
    /*
     * Still need to implement the waitForPageLoad method to ensure the checkout
     * page is fully loaded before interactions.
     * This is crucial for the stability of the tests, as it prevents actions from
     * being attempted on elements that are not yet available.
     * 
     * @Override
     * protected void waitForPageLoad() {
     * // Implement logic to wait for the checkout page to load, e.g., wait for a
     * specific element
     * int pageLoadTimeout = 30; // seconds to wait for the page to load
     * //waitForVisibility("checkout-page-identifier", pageLoadTimeout); // Will
     * Replace "checkout-page-identifier" with the actual identifier of an element
     * that confirms the page has loaded
     * }
     */

    // ==========================================
    // LOCATORS
    // ==========================================
    // Product & Price Summary
    // ---------Mobile Viewport-----------//
    // Section - Summary Section - Overflow only
    private final By summarySectionOverflowMobile = By.cssSelector("div[data-testid='checkout-order-summary-mobile']");

    // Button - Checkout - Show Order Summary Toggle
    private final By showSummaryOrderMobile = By
            .cssSelector("button[data-testid='checkout-order-summary-mobile-toggle']");

    // Label - Checkout - Show Order Summary Label {Toggle - On}
    private final By showOrderSummaryLabelMobile = By.xpath("//span[normalize-space()='Show order summary']");

    // Checkout mobile - Hide Order Summary Label {Toggle - Off}
    private final By hideOrderSummaryMobile = By.xpath("//span[normalize-space()='Hide order summary']");

    // Checkout mobile - Show Order Label Summary Price Label
    private final By priceSummaryShowOrderLabel = By
            .cssSelector("span[data-testid='checkout-order-summary-mobile-total']");

    // Checkout mobile - Product Information with Item Counter {Dynamic} Label
    private final By productInformationWithItemCounterLabelMobile = By.xpath(
            "//div[@data-testid='checkout-order-summary-mobile']//h2[contains(normalize-space(),'Product Information')]");

    // Checkout Mobile - Order Summary Item Container
    /*
     * As the locators in the container are not unique, use a list to return all the
     * matching elements and then use value or index
     */
    private final By orderSummaryItemContainerMobile = By
            .cssSelector("div[data-testid='checkout-order-summary-mobile-item']");

    // Checkout Mobile - Item Title
    // Not unique - use index or value
    private final By itemTitleLabelOrderSummaryContainerMobile = By
            .cssSelector("h4[data-testid='checkout-order-summary-mobile-item-title']");

    // Checkout mobile - Item Only Price
    // Not unique - use index or value
    private final By itemPriceLabelOrderSummaryContainerMobile = By
            .cssSelector("p[data-testid='checkout-order-summary-mobile-item-price']");

    // Checkout mobile - Item Only Remove Button
    /*
     * Removes all quantities of the product and removes it from the checkout
     * container
     */
    private final By itemRemoveButtonOrderSummaryContainerMobile = By
            .cssSelector("button[data-testid='checkout-order-summary-mobile-item-remove']");

    // Checkout mobile - Item Only quantity counter {dynamic}
    // Not unique - use index or value
    private final By itemQuantityCounterLabelOrderSummaryContainerMobile = By
            .cssSelector("span[data-testid='checkout-order-summary-mobile-item-quantity']");

    // Checkout mobile - Item quantity increment
    // Not unique - use index or value
    private final By itemQuantityIncrementButtonOrderSummaryContainerMobile = By
            .cssSelector("button[data-testid='checkout-order-summary-mobile-item-increase']");

    // Checkout mobile - Total Price Label
    private final By totalPriceLabelMobile = By.xpath(
            "//p[contains(normalize-space(),'Total Price') and following-sibling::p[@data-testid='checkout-order-summary-mobile-total']]");

    // Checkout mobile - Total Price Summary
    private final By totalPriceSummaryMobile = By.cssSelector("p[data-testid='checkout-order-summary-mobile-total']");

    // Checkout mobile - Subtotal and Price Container
    private final By subtotalPriceLabelContainerMobile = By.xpath(
            "//div[span[normalize-space()='Subtotal'] and span[@data-testid='checkout-order-summary-mobile-subtotal']]");

    // Checkout mobile - Subtotal Label
    private final By subtotalLabelMobile = By.xpath(
            "//span[normalize-space()='Subtotal' and following-sibling::span[@data-testid='checkout-order-summary-mobile-subtotal']]");

    // Checkout mobile - Subtotal Price Label:
    private final By subtotalPriceLabelMobile = By
            .cssSelector("span[data-testid='checkout-order-summary-mobile-subtotal']");

    // Checkout mobile - Delivery Price Label and Total Label Container:
    private final By deliveryPriceAndTotalPriceLabelContainerMobile = By.xpath(
            "//div[span[normalize-space()='Subtotal'] and span[@data-testid='checkout-order-summary-mobile-subtotal']]");

    // Checkout mobile - Add Promo Code Toggle
    private final By promoCodeToggleMobile = By
            .cssSelector("button[data-testid='checkout-order-summary-mobile-promo-toggle']");

    // Checkout mobile - Add Promo Code Field {After Toggling}
    private final By promoCodeFieldMobile = By
            .cssSelector("input[data-testid='checkout-order-summary-mobile-promo-input']");

    // Checkout mobile - Apply Promo Code Button {After Toggling}
    private final By applyPromoCodeButtonMobile = By
            .cssSelector("button[data-testid='checkout-order-summary-mobile-promo-apply']");

    // Checkout mobile - Cancel Promo Code Button {After Toggling}
    private final By cancelPromoCodeButtonMobile = By
            .cssSelector("button[data-testid='checkout-order-summary-mobile-promo-cancel']");

    // Checkout mobile - Cancel Promo Code Button - After Toggling - Failed to apply
    // code label
    private final By failedpromoCodeLabelMobile = By
            .cssSelector("p[data-testid='checkout-order-summary-mobile-discount-error']");

    // Checkout Mobile - Proceed to payment button
    private final By proceedToPaymentButtonMobile = By
            .cssSelector("button[data-testid='checkout-mobile-proceed-to-payment']");

    // ----------------Desktop Viewport ------------------//
    // Section - Desktop - Checkout Section
    private final By checkoutSectionDesktop = By.cssSelector("div[data-testid='checkout-order-summary-desktop']");

    // Label - Checkout - Product Information Label
    /*
     * As the locators in the container are not unique, use a list to return all the
     * matching elements and then use value or index
     */
    private final By productInformationLabelDesktop = By.xpath(
            "//div[@data-testid='checkout-order-summary-desktop']//h2[contains(normalize-space(),'Product Information')]");

    // Container - Checkout - Product Item
    // Not unique - use index or value
    private final By itemContainerCheckoutContainerDesktop = By
            .cssSelector("div[data-testid='checkout-order-summary-desktop-item']");

    // Label - Checkout Container - Item only Title
    // Not unique - use index or value
    private final By itemTitleLabelCheckoutContainerDesktop = By
            .cssSelector("4[data-testid='checkout-order-summary-desktop-item-title']");

    // Label - Checkout Container - Item Only Price
    // Not unique - use index or value
    private final By itemPriceLabelCheckoutContainerDesktop = By
            .cssSelector("p[data-testid='checkout-order-summary-desktop-item-price']");

    // Button - Checkout Container - Item Only Remove Button
    /*
     * Removes all quantities of the product and removes it from the checkout
     * container
     */
    // Not unique - use index or value
    private final By itemRemoveButtonCheckoutContainerDesktop = By
            .cssSelector("button[data-testid='checkout-order-summary-desktop-item-remove']");

    // Label - Checkout Container - Item Only quantity counter {Dynamic}
    // Not unique - use index or value
    private final By itemQuantityCounterLabelCheckoutContainerDesktop = By
            .cssSelector("span[data-testid='checkout-order-summary-desktop-item-quantity']");

    // Button - Checkout Desktop - Item Only quantity increment {Dynamic}
    // Not unique - use index or value
    private final By itemQuantityIncrementButtonCheckoutContainerDesktop = By
            .cssSelector("button[data-testid='checkout-order-summary-desktop-item-increase']");

    /* Rest are all unique - Outside the checkout container */
    // Checkout Desktop - Total Price Label
    private final By totalPriceLabelDesktop = By.xpath(
            "//p[contains(normalize-space(),'Total Price') and following-sibling::p[@data-testid='checkout-order-summary-desktop-total']]");

    // Checkout Desktop - Total Price Summary
    private final By totalPriceSummaryDesktop = By.cssSelector("p[data-testid='checkout-order-summary-desktop-total']");

    // Checkout Desktop - Subtotal and Price Container
    private final By subtotalPriceLabelContainerDesktop = By.xpath(
            "//div[span[normalize-space()='Subtotal'] and span[@data-testid='checkout-order-summary-desktop-subtotal']]");

    // Checkout Desktop - Subtotal Label
    private final By subtotalLabelDesktop = By.xpath(
            "//span[normalize-space()='Subtotal' and following-sibling::span[@data-testid='checkout-order-summary-desktop-subtotal']]");

    // Checkout Desktop - Subtotal Price Label:
    private final By subtotalPriceLabelDesktop = By
            .cssSelector("span[data-testid='checkout-order-summary-desktop-subtotal']");

    // Checkout Desktop - Delivery and Price Label Container:
    private final By deliveryPriceLabelContainerDesktop = By.xpath(
            "//div[span[normalize-space()='Subtotal'] and span[@data-testid='checkout-order-summary-desktop-subtotal']]");

    // Checkout Desktop - Add Promo Code Toggle
    private final By promoCodeToggleButtonDesktop = By
            .cssSelector("button[data-testid='checkout-order-summary-desktop-promo-toggle']");

    // Checkout Desktop - Add Promo Code Field {After Toggling}
    private final By promoCodeFieldDesktop = By
            .cssSelector("input[data-testid='checkout-order-summary-desktop-promo-input']");

    // Checkout Desktop - Apply Promo Code Button - After Toggling
    private final By applyPromoCodeButtonDesktop = By
            .cssSelector("button[data-testid='checkout-order-summary-desktop-promo-apply']");

    // Checkout Desktop - Cancel Promo Code Button - After Toggling
    private final By cancelPromoCodeButtonDesktop = By
            .cssSelector("button[data-testid='checkout-order-summary-desktop-promo-cancel']");

    // Label - Checkout - Failed Promo Code Label {After Toggling} - Failed to apply
    // code label or Invalid Code
    private final By failedPromoCodeLabelDesktop = By
            .cssSelector("p[data-testid='checkout-order-summary-desktop-discount-error']");

    // Checkout desktop - Proceed to Payment Button
    private final By proceedToPaymentButtonDesktop = By
            .cssSelector("button[data-testid='checkout-order-summary-desktop-proceed-to-payment']");

    // -----------Viewport Agnostic - User Information Form---------------//
    // Label - Information Label
    private final By checkoutPageInformationLabel = By.xpath("//h1[normalize-space()='Delivery Information']");

    // Label - Fill out Information Message Label
    private final By checkoutPageFillOutInformationMessageLabel = By.xpath(
            "//p[contains(normalize-space(),'Please fill out delivery details to finish the checkout process.')]");

    // Section - Entire Main Form Section
    private final By checkoutPageMainFormSection = By.cssSelector("div[data-testid='checkout-delivery-section']");

    // -----------------Shipping Form--------------------//
    // Section - Shipping - General Information Section
    private final By shippingGeneralInformationSection = By
            .cssSelector("section[data-testid='checkout-general-information']");

    // Label - Shipping - General Information Label
    private final By shippingGeneralInformationLabel = By.xpath(
            "//section[@data-testid='checkout-general-information']//h2[contains(normalize-space(),'General Information')]");

    // Shipping - First Name Label
    private final By shippingFirstNameLabel = By.xpath(
            "//section[@data-testid='checkout-general-information']//label[contains(normalize-space(),'First Name')]");

    // Shipping - First Name Field
    private final By shippingFirstNameField = By.cssSelector("input[data-testid='checkout-delivery-first-name']");

    // Label Mandatory - Required First Name Label
    private final By shippingFirstNameErrorLabel = By
            .cssSelector("p[data-testid='checkout-delivery-first-name-error']");

    // Shipping - Last Name Label
    private final By shippingLastNameLabel = By.xpath(
            "//section[@data-testid='checkout-general-information']//label[contains(normalize-space(),'Last Name')]");

    // Shipping - Last Name Field
    private final By shippingLastNameField = By.cssSelector("input[data-testid='checkout-delivery-last-name']");

    // Label Mandatory - Required Last Name Label
    private final By shippingLastNameErrorLabel = By.cssSelector("p[data-testid='checkout-delivery-last-name-error']");

    // Label - Shipping - Phone Label
    private final By shippingPhoneLabel = By.xpath(
            "//section[@data-testid='checkout-general-information']//label[contains(normalize-space(),'Phone')]");

    // Shipping - Country Code Dropdown Box Container
    private final By shippingCountryCodeDropdown = By
            .cssSelector("button[data-testid='checkout-delivery-phone-country-button']");

    // Shipping - Search Container
    /*
     * [Exact matches only] {Only after clicking on the dropdown button and opening
     * the country code dropdown container}
     */
    private final By shippingCountryCodeDropdownSearchField = By
            .cssSelector("input[data-testid='checkout-delivery-phone-country-search']");

    // Shipping - Country Code Dropdown Scroll Container
    /*
     * {Use list to retrieve and choose the country. Match the country name and
     * country code. Eg: 'India' will return British Indian Ocean Territory first
     * and then India second. To ensure we get the correct country match code and
     * name. Similarly just searching for 91 or +91 will return Bolivia (+591)m,
     * Eritrea (+291), India (+91), and Micronesia (+691) respectively. Only
     * available after clicking on dropdown button
     */
    private final By shippingCountryCodeDropdownScrollContainer = By.cssSelector("div[class='phone-dropdown-scroll']");

    // Shipping - Individual Country Options - [Use index or value]
    /*
     * css: button[data-testid='checkout-delivery-phone-country-option-{Country
     * Initials}']
     * \\---Commented out: Might not be required---
     */

    // Shipping - Phone Field
    private final By shippingPhoneField = By.cssSelector("input[data-testid='checkout-delivery-phone']");

    // Label Mandatory - Shipping - Required Phone Label
    private final By shippingPhoneErrorLabel = By.cssSelector("p[data-testid='checkout-delivery-phone-error']");

    // Label - Shipping - Email Label
    private final By shippingEmailLabel = By.xpath(
            "//section[@data-testid='checkout-general-information']//label[contains(normalize-space(),'Email')]");

    // Shipping - Email Field
    private final By shippingEmailField = By.cssSelector("input[data-testid='checkout-delivery-email']");

    // Label Mandatory - Required Email Label
    private final By shippingEmailErrorLabel = By.cssSelector("p[data-testid='checkout-delivery-email-error']");

    // Section - Shipping - [Bold] Address Section
    private final By shippingAddressSection = By.xpath(
            "//section[h2[contains(normalize-space(),'Address')] and not(@data-testid='checkout-billing-section')]");

    // Label - Shipping - [Bold] Address Section Label
    private final By shippingAddressSectionLabel = By.xpath(
            "//section[h2[contains(normalize-space(),'Address')] and not(@data-testid='checkout-billing-section')]//h2");

    // Shipping - Country Label
    private final By shippingCountryLabel = By.xpath(
            "//section[h2[contains(normalize-space(),'Address')] and not(@data-testid='checkout-billing-section')]//label[contains(normalize-space(),'Country')]");

    // Shipping - Country Dropdown
    /*
     * The country dropdown affects both phone country code dropdown fields
     * initially, i.e., if they have not been changed. If they have been altered by
     * user, then it won't change when country dropdown is selected
     */
    private final By shippingCountryDropdown = By.cssSelector("select[data-testid='checkout-delivery-country']");

    /*
     * Label Mandatory - Required Country Dropdown Label is not applicable as a
     * default Country is always selected. Also, a bug has been found, where the
     * billing & shipping country are always same and cannot be different Working on
     * fix by v1.0.1
     */

    // Shipping - State Label
    private final By shippingStateLabel = By.xpath(
            "//section[h2[contains(normalize-space(),'Address')] and not(@data-testid='checkout-billing-section')]//label[contains(normalize-space(),'State')]");

    // Dropdown - Shipping - State Dropdown (Only for India)
    /*
     * Checkout page is not normally accessible to domestic customers unless they
     * deliberately go to {base_url}/checkout with atleast 1 item
     */
    private final By shippingStateDropdown = By.cssSelector("select[data-testid='checkout-delivery-state']");

    // Field - Shipping - State Field (All except India)
    /*
     * It will be state field 99.99% of the times as most users will be taken to the
     * RazorpayModalComponent for all domestic purchases. Only for international
     * cases are customers redirected to checkout page
     */
    private final By shippingStateField = By.cssSelector("input[data-testid='checkout-delivery-state']");

    // Label Mandatory - Required State Dropdown Label
    private final By shippingStateErrorLabel = By.cssSelector("p[data-testid='checkout-delivery-state-error']");

    // Shipping - Street Address Label
    private final By shippingAddressLabel = By.xpath(
            "//section[h2[contains(normalize-space(),'Address')] and not(@data-testid='checkout-billing-section')]//label[contains(normalize-space(),'Street Address')]");

    // Shipping - Street Address Field
    private final By shippingAddressField = By.cssSelector("input[data-testid='checkout-delivery-address']");

    // Label Mandatory - Required Street Address Label
    private final By shippingAddressErrorLabel = By.cssSelector("p[data-testid='checkout-delivery-address-error']");

    // Shipping - City Label
    private final By shippingCityLabel = By.xpath(
            "//section[h2[contains(normalize-space(),'Address')] and not(@data-testid='checkout-billing-section')]//label[contains(normalize-space(),'City')]");

    // Shipping - City Field
    private final By shippingCityField = By.cssSelector("input[data-testid='checkout-delivery-city']");

    // Label Mandatory - Required City Label
    private final By shippingCityErrorLabel = By.cssSelector("p[data-testid='checkout-delivery-city-error']");

    // Shipping - Pincode Label
    private final By shippingPincodeLabel = By.xpath(
            "//section[h2[contains(normalize-space(),'Address')] and not(@data-testid='checkout-billing-section')]//label[contains(normalize-space(),'Pin code')]");

    // Shipping - Pincode Field
    private final By shippingPincodeField = By.cssSelector("input[data-testid='checkout-delivery-pincode']");

    // Label Mandatory - Required Pincode Label
    private final By shippingPincodeErrorLabel = By.cssSelector("p[data-testid='checkout-delivery-pincode-error']");

    // Label Alert for Page - Mandatory Fields not Filled
    /*
     * It also triggers for wrong inputs in the phone field and other incorrect data
     * in other fields
     */
    private final By pageIncompleteFormErrorLabel = By.cssSelector("div[data-testid='checkout-payment-error']");

    /* This toggles the billing form if checkbox not checked for same as shipping */
    // Checkbox - Shipping - Shipping same as billing
    private final By checkboxShippingSameAsBilling = By
            .cssSelector("input[data-testid='checkout-billing-same-as-delivery']");

    // Label - Shipping - Shipping same as billing label
    private final By shippingSameAsBillingLabel = By.xpath(
            "//label[@data-testid='checkout-billing-same-as-delivery-label']//span[contains(normalize-space(),'My billing address is the same as delivery address')]");

    // ------------Billing Form--------------------//
    // Section - Billing Section
    private final By billingSection = By.cssSelector("section[data-testid='checkout-billing-section']");

    // Label - Billing Address Section Label
    private final By billingAddressSectionLabel = By.xpath("//h2[contains(normalize-space(),'Billing Address')]");

    // Billing - First Name Label
    private final By billingFirstNameLabel = By.xpath(
            "//section[@data-testid='checkout-billing-section']//label[contains(normalize-space(),'First Name')]");

    // Billing - First Name Field
    private final By billingFirstNameField = By.cssSelector("input[data-testid='checkout-billing-first-name']");

    // Label Mandatory - Required First Name Label
    private final By billingFirstNameErrorLabel = By.cssSelector("p[data-testid='checkout-billing-first-name-error']");

    // Billing - Last Name Label
    private final By billingLastNameLabel = By.xpath(
            "//section[@data-testid='checkout-billing-section']//label[contains(normalize-space(),'Last Name')]");

    // Billing - Last Name Field
    private final By billingLastNameField = By.cssSelector("input[data-testid='checkout-billing-last-name']");

    // Label Mandatory - Required Last Name Label
    private final By billingLastNameErrorLabel = By.cssSelector("p[data-testid='checkout-billing-last-name-error']");

    // Billing - Country Label
    private final By billingCountryLabel = By
            .xpath("//section[@data-testid='checkout-billing-section']//label[contains(normalize-space(),'Country')]");

    // Billing - Country Dropdown
    private final By billingCountryDropdown = By.cssSelector("select[data-testid='checkout-billing-country']");

    /*
     * Required country label error message not required as country is auto-selected
     * from start -> From the website region settings or location auto-detection
     */

    // Billing - State Label
    private final By billingStateLabel = By
            .xpath("//section[@data-testid='checkout-billing-section']//label[contains(normalize-space(),'State')]");

    // Billing - State Dropdown (Only India supported)
    /*
     * Checkout page is not normally accessible to domestic customers unless they
     * directly add /checkout to the base url if they have at least 1 item
     */
    private final By billingStateDropdown = By.cssSelector("select[data-testid='checkout-billing-state']");

    // Field - Billing - State Field (All except India)
    private final By billingStateField = By.cssSelector("input[data-testid='checkout-billing-state']");

    // Label Mandatory - Required State Label
    private final By billingStateErrorLabel = By.cssSelector("p[data-testid='checkout-billing-state-error']");

    // Billing - Street Address Label
    private final By billingAddressLabel = By.xpath(
            "//section[@data-testid='checkout-billing-section']//label[contains(normalize-space(),'Street Address')]");

    // Billing - Street Address Field
    private final By billingAddressField = By.cssSelector("input[data-testid='checkout-billing-address']");

    // Label Mandatory - Required Street Address Label
    private final By billingAddressErrorLabel = By.cssSelector("p[data-testid='checkout-billing-address-error']");

    // Billing - City Label
    private final By billingCityLabel = By
            .xpath("//section[@data-testid='checkout-billing-section']//label[contains(normalize-space(),'City')]");

    // Billing - City Field
    private final By billingCityField = By.cssSelector("input[data-testid='checkout-billing-city']");

    // Label Mandatory - Required City Label
    private final By billingCityErrorLabel = By.cssSelector("p[data-testid='checkout-billing-city-error']");

    // Billing - Pincode Label
    private final By billingPincodeLabel = By
            .xpath("//section[@data-testid='checkout-billing-section']//label[contains(normalize-space(),'Pin code')]");

    // Billing - Pincode Field
    private final By billingPincodeField = By.cssSelector("input[data-testid='checkout-billing-pincode']");

    // Label Mandatory - Billing - Required Pincode Label
    private final By billingPincodeErrorLabel = By.cssSelector("p[data-testid='checkout-billing-pincode-error']");

    // Billing - Phone Label
    private final By billingPhoneLabel = By
            .xpath("//section[@data-testid='checkout-billing-section']//label[contains(normalize-space(),'Phone')]");

    // Billing - Phone Country Code Dropdown
    private final By billingCountryCodeDropdown = By
            .cssSelector("button[data-testid='checkout-billing-phone-country-button']");

    // Input - Billing - Search in Phone Country Dropdown
    private final By billingCountryCodeDropdownSearchField = By
            .cssSelector("input[data-testid='checkout-billing-phone-country-search']");

    // Scroll Container - Billing - Phone Dropdown Scroll Container
    /*
     * use list to retrieve elements and then select country. Ensure that the
     * intended country has country code and name matching to ensure success.
     */
    private final By billingCountryCodeDropdownScrollContainer = By.cssSelector("div[class='phone-dropdown-scroll']");

    // Billing - Phone Field
    private final By billingPhoneField = By.cssSelector("input[data-testid='checkout-billing-phone']");

    /*
     * No mandatory phone required label message for billing as it defaults to
     * Please fill in all required fields - page alert message
     */

    /*
     * After successful form validation -> RazorpayModalComponent iFrame is called.
     * It goes to div[data-testid='checkout-v2-container']. This is for all
     * international payment routes. The default mode of payment in this is only
     * Apple Pay for supported countries - UAE, US/Canada, UK, Europ (maybe?)
     */

    // Unsupported-country block (2026-07-16 Razorpay capture): selecting an
    // unsupported country (e.g. KP, CU) and clicking Proceed to Payment halts
    // with a validation alert — "Country/Region in the shipping address isn't
    // supported" (and the billing twin).
    // Anchored on the text BEFORE the apostrophe, deliberately.
    //
    // The old form hard-coded "isn't supported" with a STRAIGHT apostrophe ('). Web copy is
    // routinely authored with a TYPOGRAPHIC apostrophe (’ U+2019) — designers, CMSes and
    // smart-quote editors all produce it — and the two are different characters, so an
    // XPath containing one will never match text containing the other. It is the same
    // class of trap as matching across a <br>: the string a human reads is not the string
    // the DOM holds.
    //
    // Matching on "in the shipping address" / "in the billing address" sidesteps the
    // apostrophe entirely and still cannot collide with anything else on a checkout page.
    private final By unsupportedCountryAlert = By.xpath(
            "//*[contains(normalize-space(),'Country/Region in the shipping address')"
                    + " or contains(normalize-space(),'Country/Region in the billing address')]"); // ROLLOUT-GAP: text-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * Fills the full Delivery Information form (international /checkout flow) in
     * one call — name, contact, and address. The state control renders as a
     * dropdown for some countries and a free-text input for others, so both are
     * handled. Does NOT submit — pair with {@link #clickProceedToPayment()}.
     *
     * @param firstName first name.
     * @param lastName  last name.
     * @param phone     phone number (country code preset by the region).
     * @param email     email address.
     * @param country   the country VALUE in the country dropdown (e.g. "US").
     * @param state     state/province.
     * @param street    street address.
     * @param city      city.
     * @param pincode   pin/zip code.
     * @return this CheckoutPage for method chaining.
     */
    public CheckoutPage fillDeliveryInformation(String firstName, String lastName, String phone, String email,
            String country, String state, String street, String city, String pincode) {
        log.info("Filling the checkout Delivery Information form.");
        sendKeys(shippingFirstNameField, firstName);
        sendKeys(shippingLastNameField, lastName);
        sendKeys(shippingPhoneField, phone);
        sendKeys(shippingEmailField, email);
        new org.openqa.selenium.support.ui.Select(getElement(shippingCountryDropdown)).selectByValue(country);
        if (!driver.findElements(shippingStateDropdown).isEmpty()) {
            new org.openqa.selenium.support.ui.Select(getElement(shippingStateDropdown)).selectByVisibleText(state);
        } else {
            sendKeys(shippingStateField, state);
        }
        sendKeys(shippingAddressField, street);
        sendKeys(shippingCityField, city);
        sendKeys(shippingPincodeField, pincode);
        return this;
    }

    /**
     * Clicks the desktop "Proceed to Payment" button — with a VALID international
     * address this launches the Razorpay modal in its "Payment Options" mode;
     * with an unsupported country it halts with the unsupported-country alert.
     *
     * @return this CheckoutPage for method chaining.
     */
    public CheckoutPage clickProceedToPayment() {
        log.info("Clicking Proceed to Payment.");
        clickElement(proceedToPaymentButtonDesktop);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================

    /**
     * Returns the "Proceed to Payment" button.
     *
     * <p><b>THERE IS NO "PLACE ORDER" BUTTON ON THIS PAGE.</b> The name is a leftover and
     * it is actively misleading: "Proceed to Payment" only LAUNCHES the Razorpay modal
     * (see {@link #clickProceedToPayment()}); the actual order submission happens inside
     * Razorpay's third-party iframe, which Selenium cannot reach and which we are not
     * authorised to drive. Any test reasoning about "the order submit button" on this page
     * is reasoning about a control that does not exist.
     *
     * @return the Proceed to Payment element
     */
    public org.openqa.selenium.WebElement getPlaceOrderButton() {
        return driver.findElement(proceedToPaymentButtonDesktop);
    }

    public boolean isPlaceOrderButtonDisabled() {
        try {
            String disabledAttr = getPlaceOrderButton().getAttribute("disabled");
            return disabledAttr != null && (disabledAttr.equalsIgnoreCase("true") || disabledAttr.equals("disabled"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reports whether a submission-in-progress indicator is showing.
     *
     * <p>REMOVED 2026-07-17: this used to {@code catch} and fall back to
     * {@link #isPlaceOrderButtonDisabled()} — which is precisely what the NEXT step of the
     * idempotency scenario asserts. That made the two assertions a tautology: the first
     * could only pass by measuring the second. Worse, the {@code catch} also swallowed the
     * {@link org.openqa.selenium.NoSuchElementException} thrown when the page is not the
     * checkout page at all, so a test that had silently landed on the homepage reported
     * "processing is not active" instead of "I am not where I think I am".
     *
     * <p>It now answers only its own question and lets an absent page surface as an error.
     *
     * <p>CAVEAT for whoever picks this up: the selectors below are UNVERIFIED. Reaching a
     * real submitting state needs a seeded cart (see the scenario in
     * {@code Checkout_Idempotency.feature}), which no test can currently do, so nobody has
     * ever observed this indicator. Treat them as a hypothesis, not a contract.
     *
     * @return true if a processing indicator is visible
     */
    public boolean isProcessingIndicatorActive() {
        return isDisplayed(By.cssSelector(
                "div.processing, .spinner-active, [data-testid='checkout-processing'], div.razorpay-loading"));
    }

    /**
     * Reports whether the checkout page actually rendered, rather than bouncing away.
     *
     * <p>VERIFIED 2026-07-17: {@code /checkout} REDIRECTS to the homepage when the cart is
     * empty. Of the 126 data-testids on the landed page, every one is {@code home-*} /
     * {@code header-*} / {@code footer-*} — there are ZERO checkout testids. ({@code /cart}
     * is a 404; the cart is a drawer, not a page.) A test that navigates straight to
     * /checkout without seeding a cart is therefore asserting against the HOMEPAGE, and
     * every failure it reports is meaningless.
     *
     * @return true if the browser is still on a checkout URL after the redirect settles
     */
    public boolean isCheckoutPageLoaded() {
        return driver.getCurrentUrl().contains("/checkout");
    }

    /**
     * Verifies the unsupported-country validation alert rendered — purchases
     * from unsupported countries (e.g. KP, CU, Afghanistan) must be BLOCKED at
     * Proceed to Payment with "Country/Region in the shipping address isn't
     * supported" (2026-07-16 Razorpay capture).
     *
     * @return true if the unsupported-country alert is displayed.
     */
    public boolean isUnsupportedCountryAlertDisplayed() {
        // isEventuallyDisplayed, not findElements(...).isEmpty(): the alert appears AFTER
        // the Proceed-to-Payment round-trip, so a presence check the instant after the
        // click races the response. Presence also can't tell a rendered alert from one
        // sitting hidden in the DOM.
        return isEventuallyDisplayed(unsupportedCountryAlert);
    }
}
