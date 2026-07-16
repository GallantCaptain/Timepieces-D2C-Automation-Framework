package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.web.StagingGatePage;
import com.timepieces.automation.pages.web.friendsAndPartners.FriendsAndPartners;
import com.timepieces.automation.pages.web.friendsAndPartners.PartnerProfilePage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

/**
 * FriendsPartnersSteps — glue for the Friends & Partners feature, driving the
 * {@link FriendsAndPartners} landing page object and the
 * {@link PartnerProfilePage} (Phase-3 chunk 3D mapping).
 *
 * <p>
 * The shared {@code the {string} heading is displayed} assertion lives in
 * {@link ContentPagesSteps}.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of FriendsPartnersSteps"
* @since 2026-07-16
 */

@Log4j2
public class FriendsPartnersSteps {

    @SuppressWarnings("unused")
    private final TestContext testContext;
    private final WebDriver driver;

    private FriendsAndPartners landingPage;
    private PartnerProfilePage profilePage;

    public FriendsPartnersSteps(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverFactory.getDriver();
    }

    // ==========================================
    // Navigation
    // ==========================================
    @Given("the user navigates to the Friends and Partners page")
    public void the_user_navigates_to_the_friends_and_partners_page() {
        log.info("STEP: navigate to Friends & Partners.");
        landingPage = new FriendsAndPartners(driver).open();
        if (new StagingGatePage(driver).unlockIfPresent()) {
            landingPage.open();
        }
    }

    @Given("the user navigates to the partner profile of {string}")
    public void the_user_navigates_to_the_partner_profile_of(String slug) {
        log.info("STEP: navigate to the partner profile '{}'.", slug);
        profilePage = new PartnerProfilePage(driver).openProfile(slug);
        if (new StagingGatePage(driver).unlockIfPresent()) {
            profilePage.openProfile(slug);
        }
    }

    // ==========================================
    // Landing assertions
    // ==========================================
    @And("the section tab {string} is available")
    public void the_section_tab_is_available(String sectionLabel) {
        assertThat(landingPage.hasSectionTab(sectionLabel))
                .as("The section tab '%s' should be available", sectionLabel).isTrue();
    }

    @And("partner profile cards are displayed")
    public void partner_profile_cards_are_displayed() {
        assertThat(landingPage.getProfileCardCount())
                .as("Partner profile cards should be displayed").isGreaterThan(0);
    }

    @And("at least one partner card carries a video badge")
    public void at_least_one_partner_card_carries_a_video_badge() {
        assertThat(landingPage.getVideoBadgeCount())
                .as("At least one partner card should carry a video badge").isGreaterThan(0);
    }

    // ==========================================
    // Profile assertions
    // ==========================================
    @Then("the partner profile hero is displayed with the tagline {string}")
    public void the_partner_profile_hero_is_displayed_with_the_tagline(String tagline) {
        assertThat(profilePage.isProfileNameDisplayed())
                .as("The partner profile name should be displayed").isTrue();
        assertThat(profilePage.getTaglineText())
                .as("The profile tagline should match").isEqualTo(tagline);
    }

    @And("the partner profile features an embedded video")
    public void the_partner_profile_features_an_embedded_video() {
        assertThat(profilePage.isVideoPresent())
                .as("This partner profile should embed a YouTube video").isTrue();
    }

    @And("the sticky numbered-watch bar is displayed")
    public void the_sticky_numbered_watch_bar_is_displayed() {
        assertThat(profilePage.isStickyWatchBarDisplayed())
                .as("The sticky numbered-watch bar should be displayed").isTrue();
    }

    @And("every watch link on the profile targets a product PDP")
    public void every_watch_link_on_the_profile_targets_a_product_pdp() {
        assertThat(profilePage.allWatchLinksArePdps())
                .as("Every watch link should target a /products/ PDP").isTrue();
    }

    @When("the user expands the {string} watches drawer")
    public void the_user_expands_the_watches_drawer(String label) {
        log.info("STEP: expand the '{}' watches drawer.", label);
        profilePage.clickSeeMore();
    }

    @Then("the watches drawer lists additional purchased watches")
    public void the_watches_drawer_lists_additional_purchased_watches() {
        assertThat(profilePage.isWatchesDrawerExpanded())
                .as("The watches drawer should be expanded").isTrue();
        assertThat(profilePage.getDrawerWatchCount())
                .as("The drawer should list additional purchased watches").isGreaterThan(0);
    }

    @When("the user navigates back to Friends and Partners")
    public void the_user_navigates_back_to_friends_and_partners() {
        profilePage.clickBackToFriendsAndPartners();
        landingPage = new FriendsAndPartners(driver);
    }
}
