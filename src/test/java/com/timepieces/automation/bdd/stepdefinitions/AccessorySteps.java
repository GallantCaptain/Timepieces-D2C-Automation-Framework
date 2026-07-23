package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.components.CartDrawerComponent;
import com.timepieces.automation.pages.components.HeaderComponent;
import com.timepieces.automation.pages.web.StagingGatePage;
import com.timepieces.automation.pages.web.products.accessories.GenericAccessoryPdp;
import com.timepieces.automation.utils.ConfigReader;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

/**
 * AccessorySteps — glue for the accessory PDP feature files
 * (Accessory_Product_Details.feature, Accessory_Strap_Configurator.feature),
 * driving the {@link GenericAccessoryPdp} page object.
 *
 * <p>
 * The accessory-PDP interactions are fully implemented (real POM assertions). The
 * cart-contents outcomes (badge count, drawer line items) and the visual
 * dial-preview transitions throw {@link PendingException} — matching the
 * framework's scaffold convention in {@code CartCheckoutSteps}, since they depend
 * on cart-drawer internals that are pending project-wide (and on non-deterministic
 * visual state). This lets the scenarios execute and report as pending rather than
 * undefined.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of AccessorySteps"
* @since 2026-07-15
 */

@Log4j2
public class AccessorySteps {

    @SuppressWarnings("unused")
    private final TestContext testContext;
    private final WebDriver driver;
    private GenericAccessoryPdp pdp;

    public AccessorySteps(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverFactory.getDriver();
    }

    // ==========================================
    // Private helpers
    // ==========================================
    /** Builds the /accessories/<type>/<handle> path from a product handle. */
    private String accessoryPathFor(String handle) {
        String type;
        if (handle.contains("watch-case")) {
            type = "watch-case";
        } else if (handle.contains("clasp")) {
            type = "clasp";
        } else if (handle.contains("strap")) {
            type = "strap";
        } else {
            throw new IllegalArgumentException("Cannot derive accessory type from handle: " + handle);
        }
        return "/accessories/" + type + "/" + handle;
    }

    private String baseUrl() {
        String baseUrl = ConfigReader.get("base.url");
        return (baseUrl == null || baseUrl.isEmpty()) ? "https://staging.timepieces-d2c.example" : baseUrl;
    }

    // ==========================================
    // Navigation
    // ==========================================
    @Given("the user navigates to the accessory PDP of {string}")
    public void the_user_navigates_to_the_accessory_pdp_of(String handle) {
        String url = baseUrl() + accessoryPathFor(handle);
        log.info("Navigating to accessory PDP: {}", url);
        driver.get(url);
        if (new StagingGatePage(driver).unlockIfPresent()) {
            driver.get(url);
        }
        pdp = new GenericAccessoryPdp(driver);
    }

    // ==========================================
    // Core details
    // ==========================================
    @Then("the accessory product title is displayed")
    public void the_accessory_product_title_is_displayed() {
        assertThat(pdp.isProductTitleDisplayed()).as("Accessory product title should be displayed").isTrue();
    }

    @And("the accessory price is displayed")
    public void the_accessory_price_is_displayed() {
        assertThat(pdp.isPriceDisplayed()).as("Accessory price should be displayed").isTrue();
    }

    @And("the accessory compatibility caption is displayed")
    public void the_accessory_compatibility_caption_is_displayed() {
        assertThat(pdp.isCompatibilityCaptionDisplayed())
                .as("Accessory compatibility caption should be displayed").isTrue();
    }

    @And("the {string} button is displayed and enabled")
    public void the_button_is_displayed_and_enabled(String label) {
        if ("Add to cart".equalsIgnoreCase(label)) {
            assertThat(pdp.isAddToCartDisplayed()).as("'%s' should be displayed", label).isTrue();
            assertThat(pdp.isAddToCartEnabled()).as("'%s' should be enabled", label).isTrue();
        } else {
            throw new PendingException("No enabled-state check wired for button label: " + label);
        }
    }

    // ==========================================
    // Add to cart
    // ==========================================
    @When("the user clicks the {string} button on the accessory PDP")
    public void the_user_clicks_the_button_on_the_accessory_pdp(String label) {
        if ("Add to cart".equalsIgnoreCase(label)) {
            pdp.clickAddToCart();
        } else {
            throw new PendingException("No click wired for accessory PDP button: " + label);
        }
    }

    @Then("the slide-out cart drawer is displayed")
    public void the_slide_out_cart_drawer_is_displayed() {
        assertThat(new CartDrawerComponent(driver).isCartDrawerDisplayed())
                .as("The slide-out cart drawer should be displayed").isTrue();
    }

    @And("the cart badge count is updated to {string}")
    public void the_cart_badge_count_is_updated_to(String count) {
        // TODO: assert the header cart badge count == count (needs a badge getter).
        throw new PendingException("Implement cart badge-count assertion (expected: " + count + ").");
    }

    @And("the cart drawer displays the product {string} with its correct title and price")
    public void the_cart_drawer_displays_the_product_with_its_correct_title_and_price(String handle) {
        // TODO: assert the cart drawer line item matches the handle (title + price).
        throw new PendingException("Implement cart-drawer line-item assertion for: " + handle);
    }

    // ==========================================
    // Variant switcher navigation
    // ==========================================
    @When("the user selects the color variant that links to {string}")
    public void the_user_selects_the_color_variant_that_links_to(String handle) {
        // The color card-buttons share a title (differ by background-color), so we
        // navigate to the sibling variant via its handle-linked model-carousel card.
        pdp.navigateToModel(handle);
    }

    @When("the user selects the {string} size on the clasp PDP")
    public void the_user_selects_the_size_on_the_clasp_pdp(String size) {
        pdp.selectSize(size);
    }

    @Then("the browser navigates to the accessory PDP {string}")
    public void the_browser_navigates_to_the_accessory_pdp(String handle) {
        // WAIT for the URL, do not sample it once.
        //
        // Clicking a size/colour chip triggers a client-side route change, so reading
        // getCurrentUrl() on the very next statement usually still returns the OUTGOING
        // page — the browser has not swapped the URL yet. That is the whole reason
        // "Rose Gold clasp size switcher navigates between 20mm and 22mm" failed: the
        // click is fine and the navigation does happen (verified against production on
        // 2026-07-23: clicking 22mm lands on .../single-deployment-clasp-rose-gold-22mm),
        // the assertion just looked before the browser moved.
        String actual;
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15))
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains(handle));
        } catch (org.openqa.selenium.TimeoutException ignored) {
            // Fall through and let the assertion below report the URL we actually landed
            // on — a bare "timed out" is far less useful than "expected X, got Y".
        }
        actual = driver.getCurrentUrl();

        assertThat(actual)
                .as("The browser should have navigated to the accessory PDP for '%s'", handle)
                .contains(handle);
        pdp = new GenericAccessoryPdp(driver);
    }

    // ==========================================
    // Accordions
    // ==========================================
    @When("the user expands the {string} accordion")
    public void the_user_expands_the_accordion(String section) {
        pdp.expandAccordion(section);
    }

    @Then("the {string} accordion content is visible")
    public void the_accordion_content_is_visible(String section) {
        assertThat(pdp.isAccordionDisplayed())
                .as("The '%s' accordion should be present/expanded", section).isTrue();
    }

    // ==========================================
    // Return & Guarantee side drawers
    // ==========================================
    @When("the user opens the Guarantee side drawer")
    public void the_user_opens_the_guarantee_side_drawer() {
        pdp.openGuaranteeDrawer();
    }

    @When("the user opens the Return and Refund side drawer")
    public void the_user_opens_the_return_and_refund_side_drawer() {
        pdp.openReturnRefundDrawer();
    }

    @Then("the side drawer is displayed")
    public void the_side_drawer_is_displayed() {
        assertThat(pdp.isDrawerDisplayed()).as("The side drawer should be displayed").isTrue();
    }

    @When("the user closes the side drawer using the close button")
    public void the_user_closes_the_side_drawer_using_the_close_button() {
        pdp.closeDrawer();
    }

    @When("the user clicks outside the side drawer on the backdrop")
    public void the_user_clicks_outside_the_side_drawer_on_the_backdrop() {
        pdp.closeDrawerViaBackdrop();
    }

    @Then("the side drawer is hidden")
    public void the_side_drawer_is_hidden() {
        assertThat(pdp.isDrawerDisplayed()).as("The side drawer should be hidden").isFalse();
    }

    // ==========================================
    // Wishlist
    // ==========================================
    @When("the user clicks the wishlist button on the accessory PDP")
    public void the_user_clicks_the_wishlist_button_on_the_accessory_pdp() {
        pdp.clickWishlist();
    }

    @Then("the accessory is marked as wishlisted")
    public void the_accessory_is_marked_as_wishlisted() {
        assertThat(pdp.isWishlistActive()).as("The accessory should be marked as wishlisted").isTrue();
    }

    // ==========================================
    // Model carousel
    // ==========================================
    @Then("the recommended-models carousel is displayed")
    public void the_recommended_models_carousel_is_displayed() {
        assertThat(pdp.isModelCarouselDisplayed())
                .as("The recommended-models carousel should be displayed").isTrue();
    }

    @And("every recommended-model card links to an {string} product page")
    public void every_recommended_model_card_links_to_a_product_page(String pathFragment) {
        assertThat(pdp.allModelCardsLinkToAccessories())
                .as("Every recommended-model card should link to an '%s' page", pathFragment).isTrue();
    }

    // ==========================================
    // Strap: dial-try carousel
    // ==========================================
    @Then("the dial-try carousel {string} is displayed")
    public void the_dial_try_carousel_is_displayed(String heading) {
        assertThat(pdp.isDialTryCarouselDisplayed())
                .as("The dial-try carousel ('%s') should be displayed", heading).isTrue();
    }

    @When("the user advances the dial-try carousel to the next watch")
    public void the_user_advances_the_dial_try_carousel_to_the_next_watch() {
        pdp.nextDialPreview();
    }

    @When("the user moves the dial-try carousel to the previous watch")
    public void the_user_moves_the_dial_try_carousel_to_the_previous_watch() {
        pdp.previousDialPreview();
    }

    @Then("the strap preview updates to the next compatible watch")
    public void the_strap_preview_updates_to_the_next_compatible_watch() {
        // TODO: assert the previewed watch image/case overlay changed to the next dial.
        throw new PendingException("Implement dial-preview 'next' transition assertion.");
    }

    @Then("the strap preview updates to the previous compatible watch")
    public void the_strap_preview_updates_to_the_previous_compatible_watch() {
        // TODO: assert the previewed watch image/case overlay changed to the previous dial.
        throw new PendingException("Implement dial-preview 'previous' transition assertion.");
    }

    @Then("the dial-try carousel only shows dials compatible with {string} and {string}")
    public void the_dial_try_carousel_only_shows_dials_compatible_with(String familyA, String familyB) {
        // TODO: read the dial images and assert only families [familyA, familyB] are offered.
        throw new PendingException(
                "Implement dial-compatibility assertion for: " + familyA + " & " + familyB + ".");
    }

    // ==========================================
    // Strap: clasp-upgrade cross-sell
    // ==========================================
    @Then("the {string} clasp-upgrade card is displayed")
    public void the_clasp_upgrade_card_is_displayed(String heading) {
        assertThat(pdp.isClaspUpgradeCardDisplayed())
                .as("The '%s' clasp-upgrade card should be displayed", heading).isTrue();
    }

    @When("the user closes the cart drawer")
    public void the_user_closes_the_cart_drawer() {
        // A real step, not a workaround. Adding to cart opens the drawer automatically,
        // and it covers the right-hand side of the page — including the clasp-upgrade
        // card, which sits OUTSIDE the drawer at around x=1736 (verified on production
        // 2026-07-23). A shopper has to dismiss the drawer before they can carry on
        // shopping the page behind it, so the scenario should say so out loud rather
        // than having some other step quietly dismiss it.
        new com.timepieces.automation.pages.components.CartDrawerComponent(driver).clickCloseButton();
    }

    @When("the user clicks the {string} button on the clasp-upgrade card")
    public void the_user_clicks_the_button_on_the_clasp_upgrade_card(String label) {
        pdp.addRecommendedClasp();
    }

    @Then("the recommended clasp is added to the cart")
    public void the_recommended_clasp_is_added_to_the_cart() {
        // "Clasp" is the reliable common fragment — the upgrade card is always a clasp
        // ("Single Deployment Clasp", "Butterfly Clasp"), whatever the colour/material.
        CartDrawerComponent cart = openBagDrawer();
        assertThat(cart.containsLineItem("clasp"))
                .as("The recommended clasp should appear as a line item in the bag. Bag held: %s",
                        cart.getLineItemTitles())
                .isTrue();
    }

    @Then("the cart drawer contains both the strap and the recommended clasp")
    public void the_cart_drawer_contains_both_the_strap_and_the_recommended_clasp() {
        // Asserts the identities, not just the count: a bag of two clasps, or two straps,
        // would satisfy "2 line items" while failing the actual claim that a strap and its
        // upsold clasp are in the bag together.
        CartDrawerComponent cart = openBagDrawer();
        java.util.List<String> titles = cart.getLineItemTitles();
        assertThat(cart.containsLineItem("strap"))
                .as("The bag should contain the strap. Bag held: %s", titles).isTrue();
        assertThat(cart.containsLineItem("clasp"))
                .as("The bag should contain the recommended clasp. Bag held: %s", titles).isTrue();
    }

    /**
     * Returns a cart drawer that is guaranteed OPEN.
     *
     * <p>The clasp-upgrade "+ Add" lives on the PDP, not inside the drawer, and clicking it
     * adds the clasp WITHOUT re-opening the bag — and the scenario has usually just closed
     * the drawer to reach that card. So reading line items straight afterwards found an
     * empty, hidden drawer and timed out. This opens it via the header bag toggle first
     * when it is not already showing, which is exactly what a shopper does to check the bag.
     */
    private CartDrawerComponent openBagDrawer() {
        CartDrawerComponent cart = new CartDrawerComponent(driver);
        if (!cart.isCartDrawerDisplayed()) {
            new HeaderComponent(driver).clickCartToggle();
        }
        return cart;
    }
}
