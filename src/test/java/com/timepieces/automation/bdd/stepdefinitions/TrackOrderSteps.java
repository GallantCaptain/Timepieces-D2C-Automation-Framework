package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.web.StagingGatePage;
import com.timepieces.automation.pages.web.orders.TrackOrdersPage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

/**
 * TrackOrderSteps — glue for the guest order-tracking feature, driving the
 * {@link TrackOrdersPage} page object (Phase-3 chunk 3B-guest mapping: entry
 * form with disabled-while-empty submit, {@code ?orderId=} auto-submit deep
 * link, order-not-found error, contact-support link).
 *
 * <p>
 * The shared {@code the {string} heading is displayed} assertion lives in
 * {@link ContentPagesSteps}.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TrackOrderSteps"
* @since 2026-07-16
 */

@Log4j2
public class TrackOrderSteps {

    @SuppressWarnings("unused")
    private final TestContext testContext;
    private final WebDriver driver;

    private TrackOrdersPage trackPage;

    public TrackOrderSteps(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverFactory.getDriver();
    }

    // ==========================================
    // Navigation
    // ==========================================
    @Given("the user navigates to the Track Order page")
    public void the_user_navigates_to_the_track_order_page() {
        log.info("STEP: navigate to /track-order.");
        trackPage = new TrackOrdersPage(driver).open();
        if (new StagingGatePage(driver).unlockIfPresent()) {
            trackPage.open();
        }
    }

    @Given("the user opens the Track Order page with the order id {string}")
    public void the_user_opens_the_track_order_page_with_the_order_id(String orderId) {
        log.info("STEP: open /track-order?orderId={} (auto-submit deep link).", orderId);
        trackPage = new TrackOrdersPage(driver);
        trackPage.openWithOrderId(orderId);
        if (new StagingGatePage(driver).unlockIfPresent()) {
            trackPage.openWithOrderId(orderId);
        }
    }

    // ==========================================
    // Entry form
    // ==========================================
    @And("the order id input is displayed")
    public void the_order_id_input_is_displayed() {
        assertThat(trackPage.isOrderIdInputDisplayed())
                .as("The order-id input should be displayed").isTrue();
    }

    @And("the track order button is disabled while the input is empty")
    public void the_track_order_button_is_disabled_while_the_input_is_empty() {
        assertThat(trackPage.isTrackButtonEnabled())
                .as("The Track order button should be disabled while the input is empty").isFalse();
    }

    @When("the user tracks the order id {string}")
    public void the_user_tracks_the_order_id(String orderId) {
        log.info("STEP: track the order id '{}'.", orderId);
        trackPage.trackOrder(orderId);
    }

    // ==========================================
    // Outcomes
    // ==========================================
    @Then("the order-not-found error is displayed")
    public void the_order_not_found_error_is_displayed() {
        assertThat(trackPage.getErrorMessage())
                .as("The order-not-found error should be displayed")
                .containsIgnoringCase("Order not found");
    }

    @Then("the tracking contact support link is available")
    public void the_tracking_contact_support_link_is_available() {
        assertThat(trackPage.isContactSupportPresent())
                .as("The contact-support link should be available").isTrue();
    }
}
