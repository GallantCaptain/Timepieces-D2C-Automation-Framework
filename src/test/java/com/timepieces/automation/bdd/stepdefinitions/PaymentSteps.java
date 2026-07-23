package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.components.CartDrawerComponent;
import com.timepieces.automation.pages.components.HeaderComponent;
import com.timepieces.automation.pages.components.RazorpayModalComponent;
import com.timepieces.automation.pages.components.SidebarMenuComponent;
import com.timepieces.automation.pages.web.payments.CheckoutPage;
import com.timepieces.automation.pages.web.products.accessories.AllAccessoriesPage;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

/**
 * PaymentSteps — glue for the payment integration and security scenarios
 * (Phase 05): card-format validation, declined-card handling, secure token
 * transmission and the successful order-confirmation route.
 *
 * <p>
 * WIRED (2026-07-16, from the live Razorpay capture): delivery-form
 * population ({@link CheckoutPage#fillDeliveryInformation}), navigation into
 * the Razorpay "Payment Options" iframe (Cards → Add a new card), card entry
 * via the {@code card.*} inputs, submission via {@code add-card-cta}
 * (Razorpay TEST cards only — the declined-card scenarios exist precisely for
 * the sanctioned test-card declines), decline-message assertion inside the
 * frame, and the order-not-placed check (modal still open, no confirmation
 * route). STILL {@link PendingException}: inline card-FORMAT error selectors
 * (not in the capture), the network-level token/PAN assertions (need CDP
 * capture), and the order-confirmation page (needs a completed ₹1 test
 * payment + confirmation mapping).
 *
 * @Description "Description of PaymentSteps"
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class PaymentSteps {

    // Razorpay test-card companions (declines are driven by the card NUMBER).
    private static final String DEFAULT_EXPIRY = "12/29";
    private static final String EXPIRED_EXPIRY = "01/20";
    private static final String DEFAULT_CVV = "123";
    private static final String WRONG_CVV = "000";
    private static final String CARDHOLDER = "Timepieces Test";

    // Typed into the Razorpay guest contact field and never submitted, so no SMS is ever
    // sent to it. A repeating-digit number is used rather than a random one precisely
    // because it is recognisable as synthetic if it ever surfaces in a log or a screenshot:
    // a plausible-looking random 10-digit Indian mobile would be indistinguishable from a
    // real customer's, and somebody would eventually try to call it. India has no reserved
    // test range (there is no equivalent of the US 555 block), so "obviously fake" is the
    // best available protection. NEVER replace this with a real number.
    private static final String SYNTHETIC_MOBILE = "9999999999";

    private final TestContext testContext;
    private final WebDriver driver;
    private final RazorpayModalComponent razorpay;
    private CheckoutPage checkoutPage;
    private boolean inRazorpayFrame;

    public PaymentSteps(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverFactory.getDriver();
        this.razorpay = new RazorpayModalComponent(driver);
    }

    // ==========================================
    // Background
    // ==========================================

    /**
     * PRECONDITION, NOT A NAVIGATION STEP. This fills a form that is already on screen; it
     * does not get you to one. It was the second line of this feature's Background until
     * 2026-07-23, where the browser was still on the homepage — so it asked a homepage for
     * a delivery form and every scenario in the file died here. Only usable once the bag
     * holds an item AND the international funnel has actually routed to /checkout.
     */
    @And("the user has filled in all required delivery information on the checkout page")
    public void the_user_has_filled_in_all_required_delivery_information_on_the_checkout_page() {
        log.info("STEP: fill all required delivery information on the checkout page.");
        checkoutPage = new CheckoutPage(driver);
        checkoutPage.fillDeliveryInformation("Timepieces", "Tester", "3063069876", "qa@timepieces-d2c.example",
                "US", "New York", "123 Test Street", "New York", "10001");
    }

    // ==========================================
    // The real storefront funnel (2026-07-23 rework)
    // ==========================================

    @When("the user opens the Accessories collection from the sidebar")
    public void the_user_opens_the_accessories_collection_from_the_sidebar() {
        log.info("STEP: homepage -> hamburger -> Accessories -> All Accessories.");
        new HeaderComponent(driver).clickMenuToggle();
        new SidebarMenuComponent(driver).openAccessoriesCollection();
    }

    @Then("the accessories collection page is displayed")
    public void the_accessories_collection_page_is_displayed() {
        log.info("STEP: assert the All Accessories collection page rendered.");
        AllAccessoriesPage accessories = new AllAccessoriesPage(driver);
        assertThat(driver.getCurrentUrl())
                .as("The sidebar Accessories route should land on the collection page")
                .contains("/accessories/all");
        assertThat(accessories.isCategoryTabsDisplayed())
                .as("The accessories collection should render its category tabs").isTrue();
    }

    @And("the bag contains {int} line items")
    public void the_bag_contains_line_items(int expected) {
        log.info("STEP: assert the bag holds {} line items.", expected);
        assertThat(new CartDrawerComponent(driver).getLineItemCount())
                .as("The bag should hold %d distinct line items (a strap, a clasp and a watch case)", expected)
                .isEqualTo(expected);
    }

    @And("the bag total is displayed")
    public void the_bag_total_is_displayed() {
        CartDrawerComponent cart = new CartDrawerComponent(driver);
        assertThat(cart.isOrderTotalDisplayed())
                .as("The drawer should price the bag before offering Secure Checkout").isTrue();
        log.info("STEP: bag total rendered as [{}].", cart.getOrderTotalText());
    }

    @When("the guest types contact details into the Razorpay contact step")
    public void the_guest_types_contact_details_into_the_razorpay_contact_step() {
        log.info("STEP: type a synthetic mobile + email into the Razorpay contact step.");
        // Deliberately NOT submitted — see RazorpayModalComponent.typeGuestContactDetails.
        // Pressing Continue would send a verification SMS to whoever really owns this
        // number, on every pipeline run, with nothing tying the messages back to us.
        String email = "timepieces-suite-" + System.currentTimeMillis() + "@example.invalid";
        testContext.setContextData("GUEST_CONTACT_MOBILE", SYNTHETIC_MOBILE);
        testContext.setContextData("GUEST_CONTACT_EMAIL", email);

        razorpay.switchToCheckoutFrame().typeGuestContactDetails(SYNTHETIC_MOBILE, email);
        inRazorpayFrame = true;
    }

    @Then("the Razorpay contact step accepts the mobile and the email")
    public void the_razorpay_contact_step_accepts_the_mobile_and_the_email() {
        log.info("STEP: read the contact fields back and assert they held what was typed.");
        String expectedMobile = String.valueOf(testContext.getContextData("GUEST_CONTACT_MOBILE"));
        String expectedEmail = String.valueOf(testContext.getContextData("GUEST_CONTACT_EMAIL"));

        assertThat(razorpay.isGuestContactStepDisplayed())
                .as("A signed-out shopper should meet the contact step (mobile + email) first").isTrue();
        // Read back rather than trusting sendKeys: a masked or reformatting input can drop
        // or rewrite what was typed, and that is exactly the kind of defect worth catching.
        assertThat(razorpay.getContactNumberValue())
                .as("The mobile input should hold the digits that were typed").contains(expectedMobile);
        assertThat(razorpay.getContactEmailValue())
                .as("The email input should hold the address that was typed").isEqualTo(expectedEmail);

        razorpay.switchBackToStorefront();
        inRazorpayFrame = false;
    }

    @Then("the shopper is returned to the storefront with the Razorpay modal closed")
    public void the_shopper_is_returned_to_the_storefront_with_the_razorpay_modal_closed() {
        log.info("STEP: assert the modal is gone and the storefront is interactive again.");
        // Asks whether the iframe is still VISIBLE, not whether it still exists. Razorpay
        // hides its overlay rather than removing it, so isModalOpen() (a presence check)
        // reports "open" forever once the modal has been shown even once.
        assertThat(razorpay.isModalDismissed())
                .as("Confirming 'Yes, exit' should hide the Razorpay checkout iframe").isTrue();
    }

    @And("has navigated to the payment section")
    public void has_navigated_to_the_payment_section() {
        log.info("STEP: advance to the payment section (Razorpay Payment Options → Cards → new card).");
        checkoutPage.clickProceedToPayment();
        razorpay.waitForModalOpen()
                .switchToCheckoutFrame()
                .selectPaymentMethod("Cards")
                .openAddNewCardForm();
        inRazorpayFrame = true;
    }

    // ==========================================
    // Card-format validation
    // ==========================================

    @When("the user enters invalid card credentials:")
    public void the_user_enters_invalid_card_credentials(DataTable dataTable) {
        Map<String, String> card = dataTable.asMap(String.class, String.class);
        testContext.setContextData("CARD_CREDENTIALS", card);
        log.info("STEP: enter invalid card credentials: {}", card);
        razorpay.fillNewCardForm(
                card.getOrDefault("number", "1234"),
                card.getOrDefault("expiry", "13/99"),
                card.getOrDefault("cvv", "1"),
                card.getOrDefault("name", CARDHOLDER));
    }

    @Then("the credit card form displays inline errors for number, expiry, and CVV formats")
    public void the_credit_card_form_displays_inline_errors() {
        log.info("STEP: assert inline errors for number, expiry and CVV.");
        // The 2026-07-16 capture did not include Razorpay's inline format-error
        // selectors (submitting malformed values was out of the safety rails).
        throw new PendingException("Razorpay inline card-format error selectors not captured yet.");
    }

    // ==========================================
    // Declined-card handling (Razorpay TEST cards — sanctioned declines)
    // ==========================================

    @When("the user enters card credentials for insufficient funds {string}")
    public void the_user_enters_card_credentials_for_insufficient_funds(String cardNumber) {
        testContext.setContextData("CARD_NUMBER", cardNumber);
        log.info("STEP: enter insufficient-funds test card [{}].", cardNumber);
        razorpay.fillNewCardForm(cardNumber, DEFAULT_EXPIRY, DEFAULT_CVV, CARDHOLDER);
    }

    @When("the user enters card credentials for an expired card {string}")
    public void the_user_enters_card_credentials_for_an_expired_card(String cardNumber) {
        testContext.setContextData("CARD_NUMBER", cardNumber);
        log.info("STEP: enter expired test card [{}].", cardNumber);
        razorpay.fillNewCardForm(cardNumber, EXPIRED_EXPIRY, DEFAULT_CVV, CARDHOLDER);
    }

    @When("the user enters card credentials for incorrect CVV {string}")
    public void the_user_enters_card_credentials_for_incorrect_cvv(String cardNumber) {
        testContext.setContextData("CARD_NUMBER", cardNumber);
        log.info("STEP: enter incorrect-CVV test card [{}].", cardNumber);
        razorpay.fillNewCardForm(cardNumber, DEFAULT_EXPIRY, WRONG_CVV, CARDHOLDER);
    }

    @And("clicks the place order button")
    public void clicks_the_place_order_button() {
        if (inRazorpayFrame) {
            log.info("STEP: submit the Razorpay card form (test card — sanctioned decline path).");
            razorpay.submitNewCardForm();
        } else {
            log.info("STEP: click the checkout Proceed to Payment button.");
            checkoutPage.clickProceedToPayment();
        }
    }

    @Then("the payment system displays an error {string}")
    public void the_payment_system_displays_an_error(String message) {
        log.info("STEP: assert payment error [{}].", message);
        assertThat(razorpay.isMessageDisplayed(message))
                .as("The Razorpay checkout should display the decline reason '%s'", message).isTrue();
    }

    @And("the order is not placed")
    public void the_order_is_not_placed() {
        log.info("STEP: assert the order was not placed (modal still open, no confirmation route).");
        razorpay.switchBackToStorefront();
        inRazorpayFrame = false;
        assertThat(razorpay.isModalOpen())
                .as("The Razorpay modal should still be open after a decline").isTrue();
        assertThat(driver.getCurrentUrl())
                .as("The browser should not reach an order-confirmation route")
                .doesNotContain("confirmation").doesNotContain("order-success");
    }

    // ==========================================
    // Secure token transmission
    // ==========================================

    @When("the user submits valid card credentials")
    public void the_user_submits_valid_card_credentials() {
        log.info("STEP: enter + submit a valid Razorpay TEST card.");
        razorpay.fillNewCardForm("4111111111111111", DEFAULT_EXPIRY, DEFAULT_CVV, CARDHOLDER)
                .submitNewCardForm();
    }

    @Then("the checkout submission transmits a secure one-time payment token")
    public void the_checkout_submission_transmits_a_secure_one_time_payment_token() {
        log.info("STEP: assert a secure one-time payment token is transmitted.");
        // Needs CDP/proxy network capture — not implementable at the DOM level.
        throw new PendingException("Implement secure-token transmission assertion (network capture).");
    }

    @And("does not send raw credit card numbers directly to storefront APIs")
    public void does_not_send_raw_credit_card_numbers_directly_to_storefront_apis() {
        log.info("STEP: assert raw PAN is never sent to storefront APIs.");
        throw new PendingException("Implement raw-PAN-absence assertion (network capture).");
    }

    // ==========================================
    // Successful order confirmation
    // ==========================================

    @When("the user enters a valid test card number {string}")
    public void the_user_enters_a_valid_test_card_number(String cardNumber) {
        testContext.setContextData("CARD_NUMBER", cardNumber);
        log.info("STEP: enter valid test card [{}].", cardNumber);
        razorpay.fillNewCardForm(cardNumber, DEFAULT_EXPIRY, DEFAULT_CVV, CARDHOLDER);
    }

    @Then("the user is redirected to the Order Confirmation success page")
    public void the_user_is_redirected_to_the_order_confirmation_success_page() {
        log.info("STEP: assert redirect to the Order Confirmation success page.");
        // Needs a COMPLETED ₹1 test payment + the confirmation page mapping.
        throw new PendingException("Implement order-confirmation redirect assertion (needs a completed ₹1 test payment).");
    }

    @And("the page displays a unique order ID and purchase summary")
    public void the_page_displays_a_unique_order_id_and_purchase_summary() {
        log.info("STEP: assert a unique order id and purchase summary render.");
        throw new PendingException("Implement order-id + purchase-summary assertion.");
    }
}
