package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.components.RazorpayModalComponent;
import com.timepieces.automation.pages.web.payments.CheckoutPage;

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

    @And("the user has filled in all required delivery information on the checkout page")
    public void the_user_has_filled_in_all_required_delivery_information_on_the_checkout_page() {
        log.info("STEP: fill all required delivery information on the checkout page.");
        checkoutPage = new CheckoutPage(driver);
        checkoutPage.fillDeliveryInformation("Timepieces", "Tester", "3063069876", "qa@timepieces-d2c.example",
                "US", "New York", "123 Test Street", "New York", "10001");
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
