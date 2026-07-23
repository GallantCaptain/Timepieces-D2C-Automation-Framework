package com.timepieces.automation.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * CartDrawerComponent — Component Object Model for the Cart Drawer Component.
* @Description "Description of CartDrawerComponent"
* @since 2026-07-03
* @version 1.0
* @author Avinash Rath
 */
@Log4j2
public class CartDrawerComponent extends BasePage<CartDrawerComponent> {

    // ==========================================
    // Constructor
    // ==========================================
    public CartDrawerComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Cart Drawer Overlay:
    private final By cartDrawerOverlay = By.cssSelector("div[data-testid='cart-drawer-overlay']");

    // Cart Drawer Container:
    private final By cartDrawerContainer = By.cssSelector("aside[data-testid='cart-drawer']");

    // Cart Drawer Title:
    private final By cartDrawerTitle = By.cssSelector("h2[data-testid='cart-drawer-title']");

    // Close Button:
    // Cart can also be dismissed by clicking anywhere outside of the
    // cartDrawerContainer in the cartDrawerOverlay
    private final By closeCartButton = By.cssSelector("button[data-testid='cart-drawer-close']");

    // Cart Drawer Content Area:
    private final By contentArea = By.cssSelector("div[data-testid='cart-drawer-content']");

    // Empty Cart State:
    private final By emptyCartState = By.cssSelector("[data-testid='cart-drawer-empty']");

    // Empty State CTA Link:
    private final By emptyStateCtaLink = By.cssSelector("[data-testid='cart-drawer-empty-cta']");

    /*
     * Item container locators are not unique. Use list and then index or value as
     * per requirement
     */
    // Cart Item Container: [Use List]
    private final By buyItemContainer = By.cssSelector("div[data-testid='cart-line-item']");

    // Image of the item in Cart
    private final By buyItemImage = By.cssSelector("img[data-testid='cart-line-item-image-link']");

    // Item Title of the item in Cart
    private final By buyItemTitle = By.cssSelector("h4[data-testid='cart-line-item-title']");

    // Item size variants for accessories (clasps & straps so far)
    private final By buyItem_VariantLabel = By.cssSelector("p[data-testid='cart-line-item-variant']");

    /*
     * Will dynamically reflect prices based on the region selected/detected, as
     * well as the final checkout process, where the user has to select the delivery
     * area -> domestic or international. If international, the prices will increase
     * 1.5 times or 50% increase. We will use this knowledge to check for dynamic
     * price changes.
     */
    // Item Price label in the Item Price Container {Currency}{Price}
    private final By buyItem_PriceLabel = By.cssSelector("p[data-testid='cart-line-item-price']");

    // Item Quantity Controls Container
    private final By buyItem_QuantityControls_Container = By
            .cssSelector("div[data-testid='cart-line-item-quantity-controls']");

    // Helpful Comment
    private final By removeBuyItem_Button = By.cssSelector("button[data-testid='cart-line-item-remove']");

    // Helpful Comment
    private final By buyItem_QuanityCounter_Label = By
            .cssSelector("span[data-testid='cart-line-item-quantity']");

    // Helpful Comment
    private final By incrementBuyItem_Button = By
            .cssSelector("button[data-testid='cart-line-item-increase']");

    /*
     * Addon items in upsell container locators are not unique. Use list and then
     * index or value as per requirement. The addons are scrollable, with a hidden
     * scrollbar
     */
    // Recommended Add-Ons Upsell Container
    private final By upsellItemSection = By.cssSelector("div[data-testid='cart-upsell']");

    // Recommended Add-ons Label in Upsell Container
    private final By recommendedAddonsLabel = By.cssSelector("div[data-testid='cart-upsell'] > h3");

    // Addons Item Container
    private final By addonItemContainer = By.cssSelector("div[data-testid='cart-upsell-item']");

    // Helpful Comment
    private final By addonItemTitle = By.cssSelector("h4[data-testid='cart-upsell-item-title']]");

    // Addon Price Label [Adjusts dynamically based on region selected]
    private final By addonItemPrice = By.cssSelector("span[data-testid='cart-upsell-item-price']");

    /*
     * Add addon to the Buy Item List in Cart. The item is removed from the
     * Recommended Addon list container when we click on the add button. If we
     * remove the addon item from the Buy item list in the cart, then it will
     * return to the recommended addon list, as long as it is a compatible clasp
     * with the strap in the cart.
     * If there are different straps with their own compatible clasps addons, then
     * as long as one of the straps is in the buy container, then the addons will be
     * in the recommendations container. If the compatible strap is removed from the
     * buy container, and then it's compatiable clasp is also removed, then that
     * claps won't return to the recommended addons. If a compatible strap is added
     * to the cart, then the compatible clasps auto-populate the addon container.
     * If all the available addons are already in cart when a compatible strap is
     * added later, then there will not be any new recommendations. The addons
     * container is populated if there are any unique addons not added to the buy
     * container in the cart.
     */
    private final By buyAddonButton = By.cssSelector("button[data-testid='cart-upsell-item-add']");

    // Cart Order Summary Section in Cart
    private final By cartOrderSummarySection = By.cssSelector("div[data-testid='cart-order-summary']");

    // Order Summary Label
    private final By orderSummaryLabel = By.cssSelector("div[data-testid='cart-order-summary'] > h3");

    // The {Dynamic} Price Value Subtotal excluding delivery charges label
    private final By subtotalValue = By.cssSelector("span[data-testid='cart-summary-subtotal']");

    // The Delivery Charges Value Label
    private final By deliveryChargesValue = By.cssSelector("span[data-testid='cart-summary-delivery']");

    // Helpful Comment
    private final By totalPriceValue = By.cssSelector("span[data-testid='cart-summary-total']");

    // T & C, Policy Agreement Label
    private final By policyAgreementLabel = By
            .xpath("//p[contains(normalize-space(),'By continuing, I confirm that I have read and accept the')]");

    // Helpful Comment
    private final By termsAndConditionsLink = By.cssSelector("a[href='/policies/terms-of-service']");

    // Helpful Comment
    private final By privacyPolicyLink = By.cssSelector("a[href='/policies/privacy-policy']");

    // ---------------------------------------------------------------------------
    // Checkout CTA — "Slide to Pay" was REMOVED by the production build of 2026-07-17.
    //
    // The drawer used to end in a drag-to-confirm track: you had to press the handle and
    // drag it the full width to proceed, which the suite drove with an Actions
    // click-and-hold + moveByOffset. That control no longer exists. Verified against the
    // new build on 2026-07-17 with a seeded cart (1 x Premium Watch Case):
    //     div[class='drag-button-track']              -> 0 nodes
    //     [data-testid='slide-to-pay-text']           -> 0 nodes
    //     [data-testid='slide-to-pay-handle']         -> 0 nodes
    //     [class*='drag']                             -> 0 nodes (nothing drag-like left)
    // It is now an ordinary button:
    //     <button data-testid="cart-secure-checkout">Secure Checkout | ₹4,500</button>
    //
    // That is a straight win for the suite: a real click replaces a synthetic drag, which
    // was the single most fragile interaction we had (it depended on pixel offsets and
    // broke whenever the drawer width changed).
    // ---------------------------------------------------------------------------
    private final By secureCheckoutButton = By.cssSelector("button[data-testid='cart-secure-checkout']");

    // Also confirmed present on the same build, for whoever extends this component next:
    //   cart-line-item · cart-line-item-image-link · cart-line-item-title-link
    //   cart-line-item-title · cart-line-item-variant · cart-line-item-price
    //   cart-line-item-quantity-controls · cart-line-item-quantity
    //   cart-line-item-increase · cart-line-item-remove
    //   cart-order-summary · cart-summary-subtotal
    // (cart-drawer-overlay / cart-drawer / cart-drawer-title / cart-drawer-close /
    //  cart-drawer-content are already mapped at the top of this class.)

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * Clicks the close button to dismiss the cart drawer.
     * 
     * @return this CartDrawerComponent for method chaining.
     */
    public CartDrawerComponent clickCloseButton() {
        log.info("Clicking cart drawer close button.");
        clickElement(closeCartButton);
        // Wait for the drawer to finish sliding out before handing control back.
        //
        // The drawer covers the right-hand side of the viewport (it opens automatically
        // on Add to cart), so anything the next step wants to click over there is behind
        // it until the animation completes. Verified on production 2026-07-23: after
        // adding a strap, the "+ Add" clasp-upgrade button sits at x=1736 OUTSIDE the
        // drawer and elementFromPoint at its centre returned the drawer, not the button —
        // which is the ElementClickInterceptedException the strap+clasp scenario hit.
        isEventuallyHidden(cartDrawerContainer);
        return this;
    }

    /**
     * Clicks the CTA link inside the empty cart drawer to return to shopping.
     * 
     * @return this CartDrawerComponent for method chaining.
     */
    public CartDrawerComponent clickEmptyStateCta() {
        log.info("Clicking cart drawer empty state CTA link.");
        clickElement(emptyStateCtaLink);
        return this;
    }

    /**
     * Performs the "Slide to Pay" gesture: click-and-hold the handle, drag it
     * across the full track width, release. DOMESTIC (INR) regions get the
     * Razorpay modal in-viewport; INTERNATIONAL regions are routed to the
     * Timepieces /checkout page instead (2026-07-16 behavior spec). Requires ≥1
     * cart item.
     *
     * @return this CartDrawerComponent for method chaining.
     */
    public CartDrawerComponent performSlideToPay() {
        // NO LONGER A DRAG. The 2026-07-17 production build replaced the slide-to-confirm
        // track with an ordinary <button data-testid="cart-secure-checkout">. The old
        // implementation did an Actions clickAndHold + moveByOffset across the measured
        // track width; every part of that is now gone (drag-button-track, the handle and
        // the label all match 0 nodes).
        //
        // The method name is kept so the existing step definitions and their Gherkin keep
        // reading naturally, and because "the shopper confirms payment" is the behaviour
        // under test — the gesture was only ever the implementation of it.
        log.info("Clicking Secure Checkout (the build of 2026-07-17 replaced the slide-to-pay drag).");
        clickElement(secureCheckoutButton);
        return this;
    }

    /**
     * Verifies the Secure Checkout CTA is displayed.
     *
     * @return true if the checkout button is visible in the drawer.
     */
    public boolean isSecureCheckoutDisplayed() {
        return isDisplayed(secureCheckoutButton);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * Verifies if the cart drawer container is displayed.
     * 
     * @return true if displayed, false otherwise.
     */
    public boolean isCartDrawerDisplayed() {
        boolean displayed = isDisplayed(cartDrawerContainer);
        log.info("Cart Drawer displayed check: {}", displayed);
        return displayed;
    }

    /**
     * Verifies if the cart is empty.
     * 
     * @return true if empty cart state is displayed, false otherwise.
     */
    public boolean isCartEmpty() {
        boolean empty = isDisplayed(emptyCartState);
        log.info("Cart empty check: {}", empty);
        return empty;
    }
}
