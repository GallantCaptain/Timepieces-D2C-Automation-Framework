package com.timepieces.automation.bdd.stepdefinitions;

import com.timepieces.automation.context.TestContext;

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
 * SCAFFOLD: product slugs are wired and captured into {@link TestContext} so
 * add/verify steps share state, but interactions throw {@link PendingException}
 * until the {@code WishlistPage} locators and business methods exist. The
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

    private final TestContext testContext;

    public WishlistSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    // ==========================================
    // Preconditions
    // ==========================================

    @And("the user is a guest customer")
    public void the_user_is_a_guest_customer() {
        log.info("STEP: establish a guest (unauthenticated) customer session.");
        // TODO: ensure no auth cookie/session is present for the guest flow.
        throw new PendingException("Implement guest-session precondition (clear auth state).");
    }

    @When("the user navigates to the Product Detail Page of {string}")
    public void the_user_navigates_to_the_product_detail_page_of(String productSlug) {
        testContext.setContextData("WISHLIST_PRODUCT", productSlug);
        log.info("STEP: navigate to the PDP of [{}].", productSlug);
        // TODO: resolve the slug to its PDP page object and open() it.
        throw new PendingException("Implement PDP navigation for product: " + productSlug);
    }

    // ==========================================
    // Add / verify
    // ==========================================

    @Then("the product {string} is added to the user's wishlist")
    public void the_product_is_added_to_the_users_wishlist(String productSlug) {
        log.info("STEP: assert [{}] was added to the wishlist.", productSlug);
        // TODO: assert the wishlist counter incremented / heart is active.
        throw new PendingException("Implement wishlist-add assertion for: " + productSlug);
    }

    @When("the user navigates to the Wishlist page")
    public void the_user_navigates_to_the_wishlist_page() {
        log.info("STEP: navigate to the Wishlist page.");
        // TODO: open the WishlistPage (SidebarMenuComponent for guests).
        throw new PendingException("Implement Wishlist page navigation (WishlistPage).");
    }

    @And("navigates to the Wishlist page")
    public void navigates_to_the_wishlist_page() {
        log.info("STEP: navigate to the Wishlist page (continuation).");
        throw new PendingException("Implement Wishlist page navigation (WishlistPage).");
    }

    @Then("the wishlist page displays the product {string}")
    public void the_wishlist_page_displays_the_product(String productSlug) {
        log.info("STEP: assert the Wishlist page shows [{}].", productSlug);
        throw new PendingException("Implement wishlist-item presence assertion for: " + productSlug);
    }

    // ==========================================
    // Remove / empty state
    // ==========================================

    @Given("the user is on the Wishlist page")
    public void the_user_is_on_the_wishlist_page() {
        log.info("STEP: land on the Wishlist page.");
        throw new PendingException("Implement direct Wishlist page landing (WishlistPage).");
    }

    @And("the wishlist displays the product {string}")
    public void the_wishlist_displays_the_product(String productSlug) {
        testContext.setContextData("WISHLIST_PRODUCT", productSlug);
        log.info("STEP: assert the wishlist currently displays [{}].", productSlug);
        throw new PendingException("Implement wishlist-item precondition assertion for: " + productSlug);
    }

    @When("the user clicks the remove button for {string} on the wishlist page")
    public void the_user_clicks_the_remove_button_for_on_the_wishlist_page(String productSlug) {
        log.info("STEP: click the remove button for [{}] on the wishlist page.", productSlug);
        throw new PendingException("Implement wishlist remove-item action for: " + productSlug);
    }

    @Then("the product {string} is deleted from the wishlist")
    public void the_product_is_deleted_from_the_wishlist(String productSlug) {
        log.info("STEP: assert [{}] was removed from the wishlist.", productSlug);
        throw new PendingException("Implement wishlist-removal assertion for: " + productSlug);
    }

    @And("the wishlist page displays {string}")
    public void the_wishlist_page_displays(String message) {
        log.info("STEP: assert the Wishlist page shows the message [{}].", message);
        // TODO: assert the empty-wishlist message text.
        throw new PendingException("Implement wishlist empty-state message assertion for: " + message);
    }

    // ==========================================
    // Guest -> account sync
    // ==========================================

    @And("the user has added {string} to their wishlist")
    public void the_user_has_added_to_their_wishlist(String productSlug) {
        testContext.setContextData("WISHLIST_PRODUCT", productSlug);
        log.info("STEP: pre-seed the guest wishlist with [{}].", productSlug);
        throw new PendingException("Implement guest wishlist pre-seed for: " + productSlug);
    }

    @When("the user logs in with a valid account")
    public void the_user_logs_in_with_a_valid_account() {
        log.info("STEP: authenticate with a valid account.");
        // TODO: perform login (API-backed identity injection is fastest — see AccountSteps).
        throw new PendingException("Implement valid-account login for the sync flow.");
    }

    @Then("the wishlist page displays the product {string} synced under the account")
    public void the_wishlist_page_displays_the_product_synced_under_the_account(String productSlug) {
        log.info("STEP: assert [{}] is synced under the authenticated account wishlist.", productSlug);
        throw new PendingException("Implement guest→account wishlist-sync assertion for: " + productSlug);
    }
}
