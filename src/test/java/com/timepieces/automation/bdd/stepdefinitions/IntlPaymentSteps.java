package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.components.FooterComponent;
import com.timepieces.automation.pages.components.RazorpayModalComponent;
import com.timepieces.automation.pages.web.payments.CheckoutPage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

/**
 * IntlPaymentSteps — glue for International_Payment_Rules.feature: region
 * switching (footer country selector), the international Slide-to-Pay routing
 * to {@code /checkout}, the unsupported-country block, and the international
 * Razorpay "Payment Options" two-method-family rule (cards + Apple Pay only).
 *
 * <p>
 * The slide gesture, cart seeding and staging-gate Background steps are owned
 * by {@code CartCheckoutSteps}/{@code AccessorySteps}/{@code StagingBypassSteps}.
 * No step here ever completes a payment.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of IntlPaymentSteps"
* @since 2026-07-16
 */

@Log4j2
public class IntlPaymentSteps {

    @SuppressWarnings("unused")
    private final TestContext testContext;
    private final WebDriver driver;

    private CheckoutPage checkoutPage;

    public IntlPaymentSteps(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverFactory.getDriver();
    }

    // ==========================================
    // Region switching
    // ==========================================
    @And("the user switches the storefront region to {string}")
    public void the_user_switches_the_storefront_region_to(String isoCode) {
        log.info("STEP: switch the storefront region to [{}] via the footer country selector.", isoCode);
        FooterComponent footer = new FooterComponent(driver);
        footer.clickCountrySelector();
        footer.selectCountryByCode(isoCode);
    }

    // ==========================================
    // /checkout routing
    // ==========================================
    @Then("the user is routed to the delivery information checkout page")
    public void the_user_is_routed_to_the_delivery_information_checkout_page() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> d.getCurrentUrl().contains("/checkout"));
        assertThat(driver.getCurrentUrl())
                .as("International Slide-to-Pay should route to /checkout").contains("/checkout");
        checkoutPage = new CheckoutPage(driver);
    }

    @When("the user fills the delivery information with the country {string}")
    public void the_user_fills_the_delivery_information_with_the_country(String country) {
        log.info("STEP: fill the delivery information (country [{}]).", country);
        checkoutPage.fillDeliveryInformation("Timepieces", "Tester", "3063069876", "qa@timepieces-d2c.example",
                country, "Test State", "123 Test Street", "Test City", "10001");
    }

    @And("the user proceeds to payment from the checkout page")
    public void the_user_proceeds_to_payment_from_the_checkout_page() {
        checkoutPage.clickProceedToPayment();
    }

    // ==========================================
    // Blocking + international modal rules
    // ==========================================
    @Then("the unsupported-country alert is displayed")
    public void the_unsupported_country_alert_is_displayed() {
        assertThat(checkoutPage.isUnsupportedCountryAlertDisplayed())
                .as("An unsupported country must be BLOCKED with the isn't-supported alert").isTrue();
    }

    @Then("the Razorpay payment options offer only cards and Apple Pay")
    public void the_razorpay_payment_options_offer_only_cards_and_apple_pay() {
        RazorpayModalComponent razorpay = new RazorpayModalComponent(driver);
        razorpay.waitForModalOpen().switchToCheckoutFrame().selectPaymentMethod("Cards");
        boolean intlMode = razorpay.isInternationalPaymentOptionsMode();
        razorpay.switchBackToStorefront();
        assertThat(intlMode)
                .as("International Payment Options must offer ONLY cards + Apple Pay "
                        + "(no UPI/EMI/Netbanking/Wallet)").isTrue();
    }

    @When("the user exits the Razorpay modal via the exit confirmation")
    public void the_user_exits_the_razorpay_modal_via_the_exit_confirmation() {
        RazorpayModalComponent razorpay = new RazorpayModalComponent(driver);
        razorpay.switchToCheckoutFrame();
        razorpay.closeModal();
    }
}
