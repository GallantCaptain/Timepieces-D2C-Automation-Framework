package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.pages.web.payments.CheckoutPage;
import com.timepieces.automation.utils.ConfigReader;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

/**
 * CheckoutSteps
 * 
 * @Description "Description of CheckoutSteps"
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class CheckoutSteps {

    private CheckoutPage checkoutPage;

    @Given("the user is on the final payment verification checkout module")
    public void the_user_is_on_the_final_payment_verification_checkout_module() {
        WebDriver driver = DriverFactory.getDriver();
        String baseUrl = ConfigReader.get("base.url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://staging.timepieces-d2c.example";
        }

        driver.get(baseUrl + "/checkout");
        new com.timepieces.automation.pages.web.StagingGatePage(driver).unlockIfPresent();
        checkoutPage = new CheckoutPage(driver);

        // /checkout REDIRECTS to the homepage when the cart is empty (verified 2026-07-17:
        // of the 126 data-testids on the landed page, every one is home-*/header-*/footer-*
        // and there are ZERO checkout testids). Without this guard the scenario carried on
        // asserting against the HOMEPAGE and reported a confusing "processing is not
        // active" failure that had nothing to do with idempotency. Fail HERE, where the
        // real problem is, instead of three steps later somewhere misleading.
        if (!checkoutPage.isCheckoutPageLoaded()) {
            throw new PendingException(
                    "Cannot reach checkout: /checkout redirects to the homepage with an empty cart. "
                            + "This scenario needs bag pre-seeding (API cart injection or add-to-cart) — the same "
                            + "gap already scaffolded across the checkout-form scenarios. "
                            + "NOTE FOR WHOEVER PICKS THIS UP: seeding alone is not enough to finish this "
                            + "scenario. There is no 'Place Order' button on the Timepieces checkout page — "
                            + "'Proceed to Payment' only LAUNCHES the Razorpay modal, and the actual order "
                            + "submission happens inside Razorpay's third-party iframe, which Selenium cannot "
                            + "reach and which we are not authorised to drive. Double-clicking it against "
                            + "production would also create real orders in the payment gateway. The meaningful "
                            + "double-submit guarantee is SERVER-side and is already covered by "
                            + "IdempotencyApiTest (duplicate Idempotency-Key -> de-duplicated). If this UI "
                            + "scenario is ever completed, it must run against STAGING only.");
        }
    }

    @When("they execute a rapid double-click on the {string} trigger element")
    public void they_execute_a_rapid_double_click_on_the_trigger_element(String buttonText) {
        WebDriver driver = DriverFactory.getDriver();
        log.info("Simulating rapid double-click on button: {}", buttonText);

        // The try/catch that used to wrap this swallowed the NoSuchElementException thrown
        // when the button is not there ("expected on mock/empty state"), so the scenario
        // sailed on as though it had clicked and failed later on an unrelated assertion.
        // A step that cannot do its job must say so where it happens. The @Given above now
        // stops the scenario before this point when checkout is unreachable, so anything
        // thrown here is a real, informative failure.
        new Actions(driver).doubleClick(checkoutPage.getPlaceOrderButton()).perform();
    }

    @Then("the system should process exactly one transaction pipeline")
    public void the_system_should_process_exactly_one_transaction_pipeline() {
        // Assert that either the processing indicator is active or we did not raise
        // double processing
        boolean isProcessing = checkoutPage.isProcessingIndicatorActive();
        log.info("Is checkout processing active: {}", isProcessing);
        // For a POC we can log or perform soft assertion
        assertThat(isProcessing)
                .as("Processing cycle should be active to indicate order submission initiated.")
                .isTrue();
    }

    @Then("the {string} button should transform to a disabled state")
    public void the_button_should_transform_to_a_disabled_state(String targetElement) {
        boolean isDisabled = checkoutPage.isPlaceOrderButtonDisabled();
        log.info("Is place order button disabled: {}", isDisabled);
        assertThat(isDisabled)
                .as("The Place Order button must be disabled after double-clicking to prevent multiple submissions.")
                .isTrue();
    }
}
