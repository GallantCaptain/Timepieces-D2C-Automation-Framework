package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.web.StagingGatePage;
import com.timepieces.automation.pages.web.legal.GuaranteePolicyPage;
import com.timepieces.automation.pages.web.legal.PrivacyPolicyPage;
import com.timepieces.automation.pages.web.legal.RefundPolicyPage;
import com.timepieces.automation.pages.web.legal.SharedPolicyPage;
import com.timepieces.automation.pages.web.legal.ShippingPolicyPage;
import com.timepieces.automation.pages.web.legal.TermsOfServicePage;
import com.timepieces.automation.pages.web.otherPages.TimepiecesWallPage;
import com.timepieces.automation.pages.web.otherPages.ShareYourMomentPage;
import com.timepieces.automation.pages.web.otherPages.SiteMapPage;
import com.timepieces.automation.pages.web.otherPages.UpdatesPage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

/**
 * ContentPagesSteps — glue for the legal/policy pages and the brand content
 * pages (Timepieces Wall, Share Your Moment, Sitemap, Updates), driving the Phase-3
 * page objects ({@link SharedPolicyPage} family, {@link TimepiecesWallPage},
 * {@link ShareYourMomentPage}, {@link SiteMapPage}, {@link UpdatesPage}).
 *
 * <p>
 * The generic {@code the {string} heading is displayed} step lives here and is
 * shared across the content/F&P/track-order features: it asserts a visible
 * {@code h1} containing the given text on the CURRENT page, so it is
 * intentionally page-agnostic (and not redefined elsewhere).
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of ContentPagesSteps"
* @since 2026-07-16
 */

@Log4j2
public class ContentPagesSteps {

    @SuppressWarnings("unused")
    private final TestContext testContext;
    private final WebDriver driver;

    private SharedPolicyPage<?> policyPage;
    private TimepiecesWallPage wallPage;
    private ShareYourMomentPage sharePage;
    private SiteMapPage siteMapPage;
    private UpdatesPage updatesPage;

    public ContentPagesSteps(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverFactory.getDriver();
    }

    // ==========================================
    // Private helpers
    // ==========================================
    /** Opens a page object's endpoint, transparently unlocking the staging gate. */
    private <T extends com.timepieces.automation.pages.BasePage<T>> T openPage(T page) {
        page.open();
        if (new StagingGatePage(driver).unlockIfPresent()) {
            page.open();
        }
        return page;
    }

    /** Maps a policy slug to its concrete page object. */
    private SharedPolicyPage<?> policyFor(String slug) {
        switch (slug) {
            case "guarantee-policy":
                return new GuaranteePolicyPage(driver);
            case "privacy-policy":
                return new PrivacyPolicyPage(driver);
            case "refund-policy":
                return new RefundPolicyPage(driver);
            case "shipping-policy":
                return new ShippingPolicyPage(driver);
            case "terms-of-service":
                return new TermsOfServicePage(driver);
            default:
                throw new IllegalArgumentException("Unknown policy slug: " + slug);
        }
    }

    // ==========================================
    // Shared heading assertion
    // ==========================================
    @Then("the {string} heading is displayed")
    public void the_heading_is_displayed(String headingText) {
        // Matches on Selenium's RENDERED text, not on XPath's view of the DOM text, and
        // considers EVERY visible h1 rather than the first one in the document.
        //
        // The old form was `//h1[contains(normalize-space(),'...')]` + findElement(...),
        // and it failed on Share Your Moment for two independent reasons at once:
        //
        //  1. THE <br> TRAP. The markup is
        //         <h1>Show your<br><span>Timepieces moment</span></h1>
        //     A <br> contributes NO character to the DOM text, so the h1's textContent is
        //     the run-together "Show yourTimepieces moment". XPath matches DOM text, so
        //     contains(normalize-space(), 'Show your Timepieces moment') can never be true —
        //     the space a reader sees is painted by the line break, not present in the
        //     string. getText() reports the rendered text ("Show your\nTimepieces moment"),
        //     which normalises to exactly what the Gherkin says.
        //  2. THE HIDDEN-TWIN TRAP. The page renders a second, hidden mobile h1. The old
        //     XPath happened to match THAT one, so findElement(...).isDisplayed() returned
        //     false — reporting "not displayed" instead of "not found", which sent the
        //     diagnosis in completely the wrong direction.
        //
        // Both verified against production, 2026-07-17.
        String expected = normalise(headingText);
        boolean found = driver.findElements(By.tagName("h1")).stream().anyMatch(h1 -> {
            try {
                return h1.isDisplayed() && normalise(h1.getText()).contains(expected);
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                return false; // Re-rendered mid-check; the other candidates still get a look.
            }
        });
        assertThat(found)
                .as("An h1 heading containing '%s' should be displayed", headingText).isTrue();
    }

    /** Collapses every run of whitespace (including the newline a &lt;br&gt; renders as) to one space. */
    private static String normalise(String text) {
        return text == null ? "" : text.replaceAll("\\s+", " ").trim();
    }

    // ==========================================
    // Legal / policy pages
    // ==========================================
    @Given("the user navigates to the {string} policy page")
    public void the_user_navigates_to_the_policy_page(String slug) {
        log.info("STEP: navigate to the '{}' policy page.", slug);
        // Opened directly (not via openPage): the wildcard SharedPolicyPage<?>
        // cannot satisfy the helper's recursive T extends BasePage<T> bound.
        policyPage = policyFor(slug);
        policyPage.open();
        if (new StagingGatePage(driver).unlockIfPresent()) {
            policyPage.open();
        }
    }

    @Then("the policy browser title contains {string}")
    public void the_policy_browser_title_contains(String expected) {
        assertThat(driver.getTitle())
                .as("The policy document title should contain '%s'", expected).contains(expected);
    }

    @And("the policy main heading {string} is displayed")
    public void the_policy_main_heading_is_displayed(String heading) {
        assertThat(policyPage.isMainHeadingDisplayed()).as("The policy main heading should be displayed").isTrue();
        assertThat(policyPage.getMainHeadingText())
                .as("The policy main heading text should match").contains(heading);
    }

    @And("the policy prose content is displayed")
    public void the_policy_prose_content_is_displayed() {
        assertThat(policyPage.isProseContentDisplayed()).as("The policy prose content should be displayed").isTrue();
    }

    @And("the policy page offers the {string} and {string} navigation")
    public void the_policy_page_offers_the_and_navigation(String backLabel, String viewAllLabel) {
        assertThat(policyPage.isBackToPoliciesDisplayed())
                .as("'%s' should be displayed", backLabel).isTrue();
        assertThat(policyPage.isViewAllPoliciesDisplayed())
                .as("'%s' should be displayed", viewAllLabel).isTrue();
    }

    @Then("the policy body contains a support contact email link")
    public void the_policy_body_contains_a_support_contact_email_link() {
        assertThat(policyPage.isContactEmailPresent())
                .as("The policy body should contain a mailto contact link").isTrue();
    }

    // ==========================================
    // Timepieces Wall
    // ==========================================
    @Given("the user navigates to the Timepieces Wall page")
    public void the_user_navigates_to_the_timepieces_wall_page() {
        log.info("STEP: navigate to the Timepieces Wall.");
        wallPage = openPage(new TimepiecesWallPage(driver));
    }

    @Then("the wall canvas container is displayed")
    public void the_wall_canvas_container_is_displayed() {
        assertThat(wallPage.isWallContainerDisplayed())
                .as("The draggable wall container should be displayed").isTrue();
    }

    @And("the background music toggle is displayed")
    public void the_background_music_toggle_is_displayed() {
        assertThat(wallPage.isMusicToggleDisplayed()).as("The music toggle should be displayed").isTrue();
    }

    @When("the user toggles the background music")
    public void the_user_toggles_the_background_music() {
        wallPage.toggleMusic();
    }

    @Then("the background music is playing")
    public void the_background_music_is_playing() {
        assertThat(wallPage.isMusicPlaying())
                .as("The music toggle should be in the PLAYING (Mute background music) state").isTrue();
    }

    // ==========================================
    // Share Your Moment
    // ==========================================
    @Given("the user navigates to the Share Your Moment page")
    public void the_user_navigates_to_the_share_your_moment_page() {
        log.info("STEP: navigate to Share Your Moment.");
        sharePage = openPage(new ShareYourMomentPage(driver));
    }

    @And("the watch-photo carousel is displayed")
    public void the_watch_photo_carousel_is_displayed() {
        assertThat(sharePage.isCarouselDisplayed())
                .as("The watch-photo carousel track should be displayed").isTrue();
    }

    @When("the user clicks the gated {string} button")
    public void the_user_clicks_the_gated_button(String label) {
        log.info("STEP: click the gated '{}' CTA.", label);
        sharePage.clickShareYourMoment();
    }

    @Then("the sign-in gate modal is displayed")
    public void the_sign_in_gate_modal_is_displayed() {
        assertThat(sharePage.isGateModalDisplayed()).as("The sign-in gate modal should be displayed").isTrue();
        assertThat(sharePage.isGateSignInDisplayed()).as("The gate 'Sign in' CTA should be displayed").isTrue();
    }

    @And("the gate modal explains that only Timepieces owners can submit")
    public void the_gate_modal_explains_that_only_timepieces_owners_can_submit() {
        assertThat(sharePage.getGateMessageText())
                .as("The gate message should explain the owners-only rule")
                .containsIgnoringCase("Only Timepieces owners");
    }

    @When("the user closes the gate modal")
    public void the_user_closes_the_gate_modal() {
        sharePage.closeGateModal();
    }

    @Then("the sign-in gate modal is hidden")
    public void the_sign_in_gate_modal_is_hidden() {
        assertThat(sharePage.isGateModalDisplayed()).as("The sign-in gate modal should be hidden").isFalse();
    }

    // ==========================================
    // Sitemap
    // ==========================================
    @Given("the user navigates to the Sitemap page")
    public void the_user_navigates_to_the_sitemap_page() {
        log.info("STEP: navigate to the Sitemap.");
        siteMapPage = openPage(new SiteMapPage(driver));
    }

    @Then("the sitemap main heading is displayed")
    public void the_sitemap_main_heading_is_displayed() {
        assertThat(siteMapPage.isMainHeadingDisplayed()).as("The Sitemap heading should be displayed").isTrue();
    }

    @And("the sitemap has the section {string}")
    public void the_sitemap_has_the_section(String section) {
        assertThat(siteMapPage.hasSection(section))
                .as("The sitemap should have the section '%s'", section).isTrue();
    }

    @And("the sitemap contains links to {string} pages")
    public void the_sitemap_contains_links_to_pages(String hrefFragment) {
        assertThat(siteMapPage.hasLinkContaining(hrefFragment))
                .as("The sitemap should contain links to '%s' pages", hrefFragment).isTrue();
    }

    @And("the sitemap lists at least {int} links")
    public void the_sitemap_lists_at_least_links(int minimum) {
        assertThat(siteMapPage.getLinkCount())
                .as("The sitemap should list at least %s links", minimum).isGreaterThanOrEqualTo(minimum);
    }

    // ==========================================
    // Updates
    // ==========================================
    @Given("the user navigates to the Updates page")
    public void the_user_navigates_to_the_updates_page() {
        log.info("STEP: navigate to the Updates page.");
        updatesPage = openPage(new UpdatesPage(driver));
    }

    @And("the content filter {string} is available")
    public void the_content_filter_is_available(String filterLabel) {
        assertThat(updatesPage.hasFilter(filterLabel))
                .as("The content filter '%s' should be available", filterLabel).isTrue();
    }

    @When("the user selects the content filter {string}")
    public void the_user_selects_the_content_filter(String filterLabel) {
        updatesPage.selectFilter(filterLabel);
    }

    @Then("update cards are displayed")
    public void update_cards_are_displayed() {
        assertThat(updatesPage.areUpdateCardsDisplayed())
                .as("At least one update card should be displayed").isTrue();
    }
}
