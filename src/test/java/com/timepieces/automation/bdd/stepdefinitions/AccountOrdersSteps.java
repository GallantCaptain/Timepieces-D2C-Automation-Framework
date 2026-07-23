package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.web.account.AccountPage;
import com.timepieces.automation.pages.web.orders.OrderDetailsPage;
import com.timepieces.automation.pages.web.orders.OrderHistoryPage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

/**
 * AccountOrdersSteps — glue for the AUTH-GATED account orders feature
 * (Account_Orders_Management.feature, tagged {@code @AuthGated} and
 * deliberately NOT {@code @Regression}), driving {@link AccountPage},
 * {@link OrderHistoryPage} and {@link OrderDetailsPage}.
 *
 * <p>
 * Production login serves a Cloudflare Turnstile that WebDriver cannot solve,
 * so these scenarios only execute in a session where login was completed by a
 * human (or a pre-authenticated browser profile is reused). The steps are fully
 * wired to the POMs; the authentication precondition fails fast with a clear
 * message when the session is unauthenticated instead of timing out downstream.
 * 
 * @version 1.0
 * @author Avinash Rath
 * @Description "Description of AccountOrdersSteps"
 * @since 2026-07-16
 */

@Log4j2
public class AccountOrdersSteps {

    @SuppressWarnings("unused")
    private final TestContext testContext;
    private final WebDriver driver;

    private AccountPage accountPage;
    private OrderHistoryPage orderHistoryPage;
    private OrderDetailsPage orderDetailsPage;

    public AccountOrdersSteps(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverFactory.getDriver();
    }

    // ==========================================
    // Authentication precondition
    // ==========================================
    @Given("the user is authenticated on the account page")
    public void the_user_is_authenticated_on_the_account_page() {
        log.info("STEP: land on /account with an authenticated session.");
        try {
            // Attempt to inject session cookie using API bypass
            String baseUrl = com.timepieces.automation.utils.ConfigReader.get("base.url");
            if (baseUrl == null || baseUrl.isEmpty()) {
                baseUrl = "https://staging.timepieces-d2c.example";
            }
            driver.get(baseUrl + "/404");
            new com.timepieces.automation.pages.web.StagingGatePage(driver).unlockIfPresent();

            // The pilot phone number is NOT in source control. It used to be hardcoded here
            // as a real personal mobile belonging to the framework's first owner, which meant
            // every clone of this repo — and every future one — carried a real person's
            // contact details, and every run of this test sent an OTP to a phone that person
            // may no longer own. It was removed when they offboarded (2026-07-23).
            //
            // Supply your own. Either is fine:
            // src/test/resources/config/<env>.local.properties (gitignored)  ->  pilot.phone=NNNNNNNNNN
            // or on the command line                                         ->  -Dpilot.phone=NNNNNNNNNN
            String pilotPhone = com.timepieces.automation.utils.ConfigReader.get("pilot.phone");
            if (pilotPhone == null || pilotPhone.isBlank()) {
                throw new IllegalStateException(
                        "pilot.phone is not configured, so this @AuthGated scenario cannot log in. "
                                + "Set it in config/" + com.timepieces.automation.utils.ConfigReader.get("env")
                                + ".local.properties (gitignored) or pass -Dpilot.phone=<10-digit-number>. "
                                + "There is deliberately no default: the previous value was a real personal "
                                + "mobile number and was removed at offboarding.");
            }

            com.timepieces.automation.tdd.api.AuthenticationEngine authEngine = new com.timepieces.automation.tdd.api.AuthenticationEngine(
                    testContext);
            authEngine.loginViaBackendApi(pilotPhone);
            String token = (String) testContext.getContextData("AUTH_TOKEN");

            if (token != null && !token.isEmpty()) {
                log.info("Injecting JWT token into cookie jar for @AuthGated scenario.");
                org.openqa.selenium.Cookie authCookie = new org.openqa.selenium.Cookie.Builder("timepieces_session", token)
                        .domain("staging.timepieces-d2c.example")
                        .path("/")
                        .isSecure(true)
                        .build();
                driver.manage().addCookie(authCookie);
            }
        } catch (Exception e) {
            log.warn("Failed to inject API session cookie, attempting fallback: {}", e.getMessage());
        }

        try {
            accountPage = new AccountPage(driver).open();
        } catch (Exception e) {
            fail("Reaching /account requires an AUTHENTICATED session — production login is behind a "
                    + "human-solved Cloudflare Turnstile, so run this scenario with a pre-authenticated "
                    + "browser session. Root failure: " + e.getMessage());
        }
        assertThat(accountPage.isAccountPageLoaded())
                .as("The account page should be loaded (authenticated session)").isTrue();
    }

    // ==========================================
    // Account navigation
    // ==========================================
    @Then("the account navigation shows Orders, Owned timepieces and Wishlist")
    public void the_account_navigation_shows_orders_owned_timepieces_and_wishlist() {
        assertThat(accountPage.isAccountNavComplete())
                .as("The account nav should show Orders, Owned timepieces, Wishlist and Logout").isTrue();
    }

    @And("the logout button is displayed")
    public void the_logout_button_is_displayed() {
        assertThat(accountPage.isLogoutDisplayed()).as("The Logout button should be displayed").isTrue();
    }

    // ==========================================
    // Order history
    // ==========================================
    @When("the user opens the order history")
    public void the_user_opens_the_order_history() {
        accountPage.clickOrders();
        orderHistoryPage = new OrderHistoryPage(driver);
    }

    @Then("the orders page heading is displayed")
    public void the_orders_page_heading_is_displayed() {
        assertThat(orderHistoryPage.isPageHeadingDisplayed())
                .as("The 'Orders' page heading should be displayed").isTrue();
    }

    @And("each order card shows its status and links to its details page")
    public void each_order_card_shows_its_status_and_links_to_its_details_page() {
        if (orderHistoryPage.isEmptyStateDisplayed()) {
            log.info("Order history is EMPTY for this account — the empty state stands in for card checks.");
            assertThat(orderHistoryPage.isEmptyStateDisplayed()).isTrue();
            return;
        }
        assertThat(orderHistoryPage.areCardStatusesDisplayed())
                .as("Every order card should show a status label and date").isTrue();
        assertThat(orderHistoryPage.allCardsLinkToOrderDetails())
                .as("Every order card should link to /account/orders/Timepieces<No>").isTrue();
    }

    // ==========================================
    // Order details
    // ==========================================
    @When("the user opens the order details of {string}")
    public void the_user_opens_the_order_details_of(String orderNumber) {
        orderDetailsPage = new OrderDetailsPage(driver).openOrder(orderNumber);
    }

    @Then("the order status and payment status are displayed")
    public void the_order_status_and_payment_status_are_displayed() {
        assertThat(orderDetailsPage.isStatusDisplayed())
                .as("The order status should be displayed").isTrue();
        assertThat(orderDetailsPage.getPaymentStatusText())
                .as("The payment status should be non-empty").isNotBlank();
    }

    @And("the shipping and billing address blocks are displayed")
    public void the_shipping_and_billing_address_blocks_are_displayed() {
        assertThat(orderDetailsPage.isShippingAddressDisplayed())
                .as("The shipping address block should be displayed").isTrue();
        assertThat(orderDetailsPage.isBillingAddressDisplayed())
                .as("The billing address block should be displayed").isTrue();
    }

    // ==========================================
    // Support sheet
    // ==========================================
    @And("the user opens the Help support sheet")
    public void the_user_opens_the_help_support_sheet() {
        orderDetailsPage.clickHelp();
    }

    @Then("the support request sheet is displayed")
    public void the_support_request_sheet_is_displayed() {
        assertThat(orderDetailsPage.isSupportSheetOpen())
                .as("The Support Request sheet should be open").isTrue();
    }

    @And("the support submit button is disabled until the form is filled")
    public void the_support_submit_button_is_disabled_until_the_form_is_filled() {
        assertThat(orderDetailsPage.isSupportSubmitEnabled())
                .as("The support submit should start disabled").isFalse();
    }

    @When("the user closes the support request sheet")
    public void the_user_closes_the_support_request_sheet() {
        orderDetailsPage.closeSupportSheet();
    }

    @Then("the support request sheet is hidden")
    public void the_support_request_sheet_is_hidden() {
        assertThat(orderDetailsPage.isSupportSheetOpen())
                .as("The Support Request sheet should be hidden").isFalse();
    }
}
