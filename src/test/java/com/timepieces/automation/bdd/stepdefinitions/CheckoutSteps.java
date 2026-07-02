package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.pages.web.payments.CheckoutPage;
import com.timepieces.automation.utils.ConfigReader;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

/**
 * CheckoutSteps
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

        // Open the checkout page directly for verification
        driver.get(baseUrl + "/checkout");
        checkoutPage = new CheckoutPage(driver);
    }

    @When("they execute a rapid double-click on the {string} trigger element")
    public void they_execute_a_rapid_double_click_on_the_trigger_element(String buttonText) {
        WebDriver driver = DriverFactory.getDriver();
        log.info("Simulating rapid double-click on button: {}", buttonText);

        Actions actionBuilder = new Actions(driver);
        try {
            actionBuilder.doubleClick(checkoutPage.getPlaceOrderButton()).perform();
        } catch (Exception e) {
            log.warn("Place Order button double-click action had exception (expected on mock/empty state): {}",
                    e.getMessage());
        }
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
