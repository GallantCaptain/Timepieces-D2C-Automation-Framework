package com.timepieces.automation.bdd.stepdefinitions;

import java.util.List;

import com.timepieces.automation.context.TestContext;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

/**
 * CheckoutFormSteps — glue for delivery/billing form validation, promo-code
 * handling and dynamic total recalculation on the international checkout page
 * (Phase 04: Checkout Funnel and Validation).
 *
 * SCAFFOLD: expressions (including the mandatory-fields DataTable) are wired and
 * captured into {@link TestContext} where reused, but interactions throw
 * {@link PendingException} until the {@code CheckoutPage} business methods exist.
 * The generic {@code clicks the {string} button} step lives here and is shared
 * across features (e.g. the wishlist flow), so it is intentionally not redefined
 * elsewhere.
 *
 * @Description "Description of CheckoutFormSteps"
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class CheckoutFormSteps {

    private final TestContext testContext;

    public CheckoutFormSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    // ==========================================
    // Background
    // ==========================================

    @And("the user has at least one active product in the shopping bag")
    public void the_user_has_at_least_one_active_product_in_the_shopping_bag() {
        log.info("STEP: ensure at least one active product is in the shopping bag.");
        // TODO: seed the bag (API inject or add-to-cart) so /checkout is reachable.
        throw new PendingException("Implement bag pre-seeding for the checkout form flow.");
    }

    @And("the user initiates international checkout to navigate to the checkout page")
    public void the_user_initiates_international_checkout_to_navigate_to_the_checkout_page() {
        log.info("STEP: navigate to the international /checkout page.");
        // TODO: drive the international checkout entry and land on CheckoutPage.
        throw new PendingException("Implement navigation to the international checkout page (CheckoutPage).");
    }

    // ==========================================
    // Shared UI actions
    // ==========================================

    @And("clicks the {string} button")
    public void clicks_the_button(String buttonLabel) {
        testContext.setContextData("LAST_CLICKED_BUTTON", buttonLabel);
        log.info("STEP: click the [{}] button.", buttonLabel);
        if ("Wishlist".equalsIgnoreCase(buttonLabel)) {
            // The wishlist heart on the active PDP (shared across timepieces).
            new com.timepieces.automation.pages.web.products.timepieces.GenericTimepiecePdp(
                    com.timepieces.automation.base.DriverFactory.getDriver()).clickWishlist();
            return;
        }
        // TODO: resolve and click other labelled buttons on the active page.
        throw new PendingException("Implement labelled button click for: " + buttonLabel);
    }

    // ==========================================
    // Mandatory field validation
    // ==========================================

    @When("the user leaves all delivery details blank")
    public void the_user_leaves_all_delivery_details_blank() {
        log.info("STEP: leave all delivery detail fields blank.");
        // TODO: ensure the delivery form fields are empty before submitting.
        throw new PendingException("Implement blank delivery-form precondition.");
    }

    @Then("the checkout form displays mandatory error messages under:")
    public void the_checkout_form_displays_mandatory_error_messages_under(DataTable dataTable) {
        List<String> fields = dataTable.asList();
        log.info("STEP: assert mandatory error messages under fields: {}", fields);
        // TODO: for each field, assert its inline mandatory error label is displayed.
        throw new PendingException("Implement per-field mandatory error assertions (CheckoutPage error labels).");
    }

    @And("a global alert message {string} is displayed")
    public void a_global_alert_message_is_displayed(String message) {
        log.info("STEP: assert the global alert message [{}] is displayed.", message);
        // TODO: assert the page-level incomplete-form alert text.
        throw new PendingException("Implement global alert assertion for: " + message);
    }

    // ==========================================
    // Field-format validation
    // ==========================================

    @When("the user enters an invalid email {string}")
    public void the_user_enters_an_invalid_email(String email) {
        log.info("STEP: enter invalid email [{}].", email);
        // TODO: type the invalid email into the delivery email field.
        throw new PendingException("Implement invalid-email entry for: " + email);
    }

    @And("fills out all other mandatory delivery details")
    public void fills_out_all_other_mandatory_delivery_details() {
        log.info("STEP: fill all other mandatory delivery fields with valid data.");
        // TODO: populate first/last name, phone, address, city, state, pincode.
        throw new PendingException("Implement valid population of remaining mandatory delivery fields.");
    }

    @Then("the email field error message {string} is displayed")
    public void the_email_field_error_message_is_displayed(String message) {
        log.info("STEP: assert email field error [{}].", message);
        throw new PendingException("Implement email-format error assertion for: " + message);
    }

    @When("the user enters an invalid pincode {string}")
    public void the_user_enters_an_invalid_pincode(String pincode) {
        log.info("STEP: enter invalid pincode [{}].", pincode);
        throw new PendingException("Implement invalid-pincode entry for: " + pincode);
    }

    @Then("the pincode field error message {string} is displayed")
    public void the_pincode_field_error_message_is_displayed(String message) {
        log.info("STEP: assert pincode field error [{}].", message);
        throw new PendingException("Implement pincode-format error assertion for: " + message);
    }

    // ==========================================
    // Billing address expansion
    // ==========================================

    @Given("the billing address same as delivery checkbox is checked")
    public void the_billing_address_same_as_delivery_checkbox_is_checked() {
        log.info("STEP: assert billing-same-as-delivery checkbox starts checked.");
        throw new PendingException("Implement billing-same-as-delivery checked-state precondition.");
    }

    @When("the user unchecks the billing address same as delivery checkbox")
    public void the_user_unchecks_the_billing_address_same_as_delivery_checkbox() {
        log.info("STEP: uncheck the billing-same-as-delivery checkbox.");
        throw new PendingException("Implement billing-same-as-delivery uncheck action.");
    }

    @Then("the separate Billing Address form section expands")
    public void the_separate_billing_address_form_section_expands() {
        log.info("STEP: assert the billing address section expands.");
        throw new PendingException("Implement billing-section expansion assertion (CheckoutPage billing section).");
    }

    @And("the billing form displays mandatory input fields for name, address, and phone")
    public void the_billing_form_displays_mandatory_input_fields() {
        log.info("STEP: assert billing form exposes name/address/phone fields.");
        throw new PendingException("Implement billing mandatory-field presence assertion.");
    }

    // ==========================================
    // Promo code handling
    // ==========================================

    @When("the user enters a promo code {string} in the discount field")
    public void the_user_enters_a_promo_code_in_the_discount_field(String promoCode) {
        testContext.setContextData("PROMO_CODE", promoCode);
        log.info("STEP: enter promo code [{}] into the discount field.", promoCode);
        throw new PendingException("Implement promo-code entry for: " + promoCode);
    }

    @And("clicks the apply promo code button")
    public void clicks_the_apply_promo_code_button() {
        log.info("STEP: click the apply promo-code button.");
        throw new PendingException("Implement apply-promo-code click (CheckoutPage apply button).");
    }

    @Then("the promo field displays an error message {string}")
    public void the_promo_field_displays_an_error_message(String message) {
        log.info("STEP: assert promo field error [{}].", message);
        throw new PendingException("Implement promo-error assertion for: " + message);
    }

    @And("the subtotal and order total pricing structure remains unchanged")
    public void the_subtotal_and_order_total_pricing_structure_remains_unchanged() {
        log.info("STEP: assert subtotal/total unchanged after invalid promo.");
        throw new PendingException("Implement pre/post pricing-unchanged assertion.");
    }

    @Given("a valid promo code {string} provides a 10% discount")
    public void a_valid_promo_code_provides_a_10_percent_discount(String promoCode) {
        testContext.setContextData("VALID_PROMO_CODE", promoCode);
        log.info("STEP: register valid promo code [{}] worth a 10% discount.", promoCode);
        throw new PendingException("Implement valid-promo precondition/registration for: " + promoCode);
    }

    @Then("the discount is successfully applied to the order summary")
    public void the_discount_is_successfully_applied_to_the_order_summary() {
        log.info("STEP: assert the discount is applied to the order summary.");
        throw new PendingException("Implement discount-applied assertion on the order summary.");
    }

    @And("the order total is dynamically updated to reflect the 10% reduction")
    public void the_order_total_is_dynamically_updated_to_reflect_the_10_percent_reduction() {
        log.info("STEP: assert order total drops by 10%.");
        throw new PendingException("Implement dynamic-total 10%-reduction assertion.");
    }
}
