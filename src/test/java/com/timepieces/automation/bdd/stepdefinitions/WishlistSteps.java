package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.web.StagingGatePage;
import com.timepieces.automation.pages.web.orders.WishlistPage;
import com.timepieces.automation.pages.web.products.timepieces.GenericTimepiecePdp;
import com.timepieces.automation.utils.ConfigReader;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

/**
 * WishlistSteps — glue for the guest/authenticated wishlist scenarios
 * (Phase 06): add/remove products, empty-state messaging and guest→account
 * wishlist sync on login.
 *
 * <p>
 * GUEST flows are fully implemented against {@link WishlistPage} +
 * {@link GenericTimepiecePdp} (the 2026-07-16 scrape confirmed the guest
 * wishlist lives at {@code /wishlist} and persists in localStorage
 * {@code timepieces_wishlist}). Item presence/removal is asserted by product
 * HANDLE via the card's PDP link, which is robust against display-title
 * variations. The guest→account SYNC scenario still throws
 * {@link PendingException}: production login requires a human-solved Cloudflare
 * Turnstile (see the CI Cloudflare blocker), so it cannot run headlessly. The
 * generic {@code clicks the {string} button} step (used for the "Wishlist"
 * button here) is defined in {@link CheckoutFormSteps} and shared globally.
 *
 * @Description "Description of WishlistSteps"
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class WishlistSteps {

    @SuppressWarnings("unused")
    private final TestContext testContext;
    private final WebDriver driver;
    private WishlistPage wishlistPage;

    public WishlistSteps(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverFactory.getDriver();
    }

    // ==========================================
    // Private helpers
    // ==========================================
    private String baseUrl() {
        String baseUrl = ConfigReader.get("base.url");
        return (baseUrl == null || baseUrl.isEmpty()) ? "https://staging.timepieces-d2c.example" : baseUrl;
    }

    /** Navigates to a URL, transparently unlocking the staging gate. */
    private void navigate(String url) {
        driver.get(url);
        if (new StagingGatePage(driver).unlockIfPresent()) {
            driver.get(url);
        }
    }

    /** Opens the timepiece PDP for a handle and returns its page object. */
    private GenericTimepiecePdp openTimepiecePdp(String productSlug) {
        navigate(baseUrl() + "/products/" + productSlug);
        return new GenericTimepiecePdp(driver);
    }

    /** Opens the Wishlist page (staging-gate aware) and binds the field. */
    private WishlistPage openWishlist() {
        navigate(baseUrl() + "/wishlist");
        wishlistPage = new WishlistPage(driver);
        return wishlistPage;
    }

    // ==========================================
    // Preconditions
    // ==========================================

    @And("the user is a guest customer")
    public void the_user_is_a_guest_customer() {
        log.info("STEP: establish a guest (unauthenticated) customer session.");
        navigate(baseUrl());
        driver.manage().deleteAllCookies();
        // The guest wishlist persists in localStorage (timepieces_wishlist) — clear
        // it so every guest scenario starts from a clean slate.
        ((JavascriptExecutor) driver).executeScript(
                String.format("window.localStorage.removeItem('%s');", WishlistPage.WISHLIST_STORAGE_KEY));
        driver.navigate().refresh();
    }

    @When("the user navigates to the Product Detail Page of {string}")
    public void the_user_navigates_to_the_product_detail_page_of(String productSlug) {
        testContext.setContextData("WISHLIST_PRODUCT", productSlug);
        log.info("STEP: navigate to the PDP of [{}].", productSlug);
        openTimepiecePdp(productSlug);
    }

    // ==========================================
    // Add / verify
    // ==========================================

    @Then("the product {string} is added to the user's wishlist")
    public void the_product_is_added_to_the_users_wishlist(String productSlug) {
        log.info("STEP: assert [{}] was added to the wishlist.", productSlug);
        assertThat(new GenericTimepiecePdp(driver).isWishlistActive())
                .as("The wishlist heart for '%s' should be in the ACTIVE (Remove from wishlist) state", productSlug)
                .isTrue();
    }

    @When("the user navigates to the Wishlist page")
    public void the_user_navigates_to_the_wishlist_page() {
        log.info("STEP: navigate to the Wishlist page.");
        openWishlist();
    }

    @And("navigates to the Wishlist page")
    public void navigates_to_the_wishlist_page() {
        log.info("STEP: navigate to the Wishlist page (continuation).");
        openWishlist();
    }

    @Then("the wishlist page displays the product {string}")
    public void the_wishlist_page_displays_the_product(String productSlug) {
        log.info("STEP: assert the Wishlist page shows [{}].", productSlug);
        assertThat(wishlistPage.hasItemByHandle(productSlug))
                .as("The wishlist should display a card linked to '%s'", productSlug).isTrue();
    }

    // ==========================================
    // Remove / empty state
    // ==========================================

    @Given("the user is on the Wishlist page")
    public void the_user_is_on_the_wishlist_page() {
        log.info("STEP: land on the Wishlist page.");
        openWishlist();
    }

    @And("the wishlist displays the product {string}")
    public void the_wishlist_displays_the_product(String productSlug) {
        testContext.setContextData("WISHLIST_PRODUCT", productSlug);
        log.info("STEP: ensure the wishlist currently displays [{}] (seeding via the PDP heart if absent).",
                productSlug);
        if (!wishlistPage.hasItemByHandle(productSlug)) {
            // Guest wishlist is per-browser localStorage, so a fresh scenario
            // starts empty — seed through the PDP heart, then return.
            openTimepiecePdp(productSlug).clickWishlist();
            openWishlist();
        }
        assertThat(wishlistPage.hasItemByHandle(productSlug))
                .as("The wishlist should display a card linked to '%s' after seeding", productSlug).isTrue();
    }

    @When("the user clicks the remove button for {string} on the wishlist page")
    public void the_user_clicks_the_remove_button_for_on_the_wishlist_page(String productSlug) {
        log.info("STEP: click the remove button for [{}] on the wishlist page.", productSlug);
        wishlistPage.removeItemByHandle(productSlug);
    }

    @Then("the product {string} is deleted from the wishlist")
    public void the_product_is_deleted_from_the_wishlist(String productSlug) {
        log.info("STEP: assert [{}] was removed from the wishlist.", productSlug);
        assertThat(wishlistPage.hasItemByHandle(productSlug))
                .as("The wishlist should no longer display a card linked to '%s'", productSlug).isFalse();
    }

    @And("the wishlist page displays {string}")
    public void the_wishlist_page_displays(String message) {
        log.info("STEP: assert the Wishlist page shows the message [{}].", message);
        assertThat(wishlistPage.getEmptyStateText())
                .as("The wishlist empty-state message should match").containsIgnoringCase(message);
    }

    // ==========================================
    // Guest -> account sync
    // ==========================================

    @And("the user has added {string} to their wishlist")
    public void the_user_has_added_to_their_wishlist(String productSlug) {
        testContext.setContextData("WISHLIST_PRODUCT", productSlug);
        log.info("STEP: pre-seed the guest wishlist with [{}].", productSlug);
        openTimepiecePdp(productSlug).clickWishlist();
    }

    @When("the user logs in with a valid account")
    public void the_user_logs_in_with_a_valid_account() {
        log.info("STEP: authenticate with a valid account.");
        // Production login serves a Cloudflare Turnstile WebDriver cannot solve
        // (see LoginPage Javadoc / CI Cloudflare blocker) — headless login is not
        // possible, so the sync flow stays pending.
        throw new PendingException("Valid-account login requires a human-solved Cloudflare challenge.");
    }

    @Then("the wishlist page displays the product {string} synced under the account")
    public void the_wishlist_page_displays_the_product_synced_under_the_account(String productSlug) {
        log.info("STEP: assert [{}] is synced under the authenticated account wishlist.", productSlug);
        throw new PendingException("Guest→account wishlist-sync assertion depends on the login step above.");
    }
}
