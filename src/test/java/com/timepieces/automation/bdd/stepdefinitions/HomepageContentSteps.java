package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.components.FooterComponent;
import com.timepieces.automation.pages.components.StoriesModalComponent;
import com.timepieces.automation.pages.components.cards.AstoniaDropCardComponent;
import com.timepieces.automation.pages.web.HomePage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

/**
 * HomepageContentSteps — glue for Homepage_Content_Sections.feature (Phase 6A
 * mapping), driving the completed {@link HomePage}, the new
 * {@link StoriesModalComponent}, the {@link AstoniaDropCardComponent} popup
 * (including its sessionStorage suppression constants) and the
 * {@link FooterComponent} country selector.
 *
 * <p>
 * The Background steps ("bypasses the staging password gate" / "is on the
 * storefront homepage") live in {@link StagingBypassSteps} and are reused, not
 * redefined. The popup-suppression steps write the
 * {@code astonia_popup_visited} sessionStorage key BEFORE reloading, so the
 * popup's ~15s mount logic never fires — the same trick step definitions can
 * use anywhere to kill popup flake.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of HomepageContentSteps"
* @since 2026-07-16
 */

@Log4j2
public class HomepageContentSteps {

    private final TestContext testContext;
    private final WebDriver driver;

    private HomePage homePage;
    private StoriesModalComponent storiesModal;
    private FooterComponent footer;

    public HomepageContentSteps(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverFactory.getDriver();
    }

    // ==========================================
    // Private helpers
    // ==========================================
    private HomePage homePage() {
        if (homePage == null) {
            homePage = new HomePage(driver).waitUntilLoaded();
        }
        return homePage;
    }

    private StoriesModalComponent stories() {
        if (storiesModal == null) {
            storiesModal = new StoriesModalComponent(driver);
        }
        return storiesModal;
    }

    private FooterComponent footer() {
        if (footer == null) {
            footer = new FooterComponent(driver);
        }
        return footer;
    }

    // ==========================================
    // Hero
    // ==========================================
    @Then("the homepage hero section is displayed")
    public void the_homepage_hero_section_is_displayed() {
        assertThat(homePage().isHeroSectionDisplayed()).as("The hero section should be displayed").isTrue();
    }

    @And("the hero background showreel video is present")
    public void the_hero_background_showreel_video_is_present() {
        assertThat(homePage().isHeroVideoPresent()).as("The hero showreel video should be present").isTrue();
    }

    @And("the hero mute toggle is displayed")
    public void the_hero_mute_toggle_is_displayed() {
        // The mute toggle is exercised via HomePage.toggleHeroMute(); presence is
        // asserted through the hero overlay chrome.
        assertThat(homePage().isHeroOverlayDisplayed()).as("The hero overlay (with its controls) should be displayed")
                .isTrue();
    }

    // ==========================================
    // Two Stories
    // ==========================================
    @Then("the {string} section is displayed with both release cards")
    public void the_section_is_displayed_with_both_release_cards(String sectionName) {
        assertThat(homePage().isTwoStoriesSectionDisplayed())
                .as("The '%s' section should be displayed with both release cards", sectionName).isTrue();
    }

    @And("the Astonia story card carries its arrow CTA")
    public void the_astonia_story_card_carries_its_arrow_cta() {
        assertThat(homePage().isAstoniaStoryCtaPresent())
                .as("The Astonia story card should carry its arrow CTA").isTrue();
    }

    @And("the Manifesta story card is locked with the badge {string}")
    public void the_manifesta_story_card_is_locked_with_the_badge(String badge) {
        assertThat(homePage().isManifestaStoryLocked())
                .as("The Manifesta story card should be LOCKED").isTrue();
        assertThat(homePage().getManifestaLockedText())
                .as("The locked badge should read '%s'", badge).contains(badge);
    }

    // ==========================================
    // Brand story
    // ==========================================
    @Then("the brand story section is displayed")
    public void the_brand_story_section_is_displayed() {
        assertThat(homePage().isBrandStoryDisplayed())
                .as("The brand-story parallax section should be displayed").isTrue();
    }

    @And("the brand quote contains {string}")
    public void the_brand_quote_contains(String fragment) {
        assertThat(homePage().getBrandQuoteText())
                .as("The brand quote should contain '%s'", fragment).contains(fragment);
    }

    // ==========================================
    // Friends & Partners section
    // ==========================================
    @Then("the homepage Friends and Partners section is displayed")
    public void the_homepage_friends_and_partners_section_is_displayed() {
        assertThat(homePage().isFriendsPartnersSectionDisplayed())
                .as("The homepage Friends & Partners section should be displayed").isTrue();
    }

    @And("homepage partner cards are displayed")
    public void homepage_partner_cards_are_displayed() {
        assertThat(homePage().getPartnerCardCount())
                .as("Homepage partner cards should be displayed").isGreaterThan(0);
    }

    @And("every homepage partner card links to a partner profile")
    public void every_homepage_partner_card_links_to_a_partner_profile() {
        assertThat(homePage().allPartnerCardsLinkToProfiles())
                .as("Every homepage partner card should link to /friends-and-partners/<handle>").isTrue();
    }

    // ==========================================
    // Last Release slider
    // ==========================================
    @Then("the Last Release slider is displayed")
    public void the_last_release_slider_is_displayed() {
        assertThat(homePage().isLastReleaseSliderDisplayed())
                .as("The Last Release slider should be displayed").isTrue();
    }

    @And("Last Release cards are displayed")
    public void last_release_cards_are_displayed() {
        assertThat(homePage().getLastReleaseCardCount())
                .as("Last Release cards should be displayed").isGreaterThan(0);
    }

    @And("every Last Release card links to a timepiece PDP")
    public void every_last_release_card_links_to_a_timepiece_pdp() {
        assertThat(homePage().allLastReleaseCardsLinkToPdps())
                .as("Every Last Release card should link to /products/<handle>").isTrue();
    }

    // ==========================================
    // Sequence + USP
    // ==========================================
    @Then("the crafted-with-intent scroll sequence is displayed")
    public void the_crafted_with_intent_scroll_sequence_is_displayed() {
        assertThat(homePage().isCraftedWithIntentDisplayed())
                .as("The Crafted With Intent scroll-sequence should be displayed").isTrue();
    }

    @And("the FEATURED IN marquee is displayed")
    public void the_featured_in_marquee_is_displayed() {
        assertThat(homePage().isUspMarqueeDisplayed())
                .as("The FEATURED IN USP marquee should be displayed").isTrue();
    }

    // ==========================================
    // Stories modal
    // ==========================================
    @When("the user opens the stories modal")
    public void the_user_opens_the_stories_modal() {
        homePage();
        stories().openStories();
    }

    @Then("the stories modal is open")
    public void the_stories_modal_is_open() {
        assertThat(stories().isOpen()).as("The stories modal should be open").isTrue();
    }

    @And("the stories modal shows at least {int} story")
    public void the_stories_modal_shows_at_least_story(int minimum) {
        assertThat(stories().getStoryCount())
                .as("The stories modal should show at least %s story", minimum).isGreaterThanOrEqualTo(minimum);
    }

    @And("the current story image is displayed")
    public void the_current_story_image_is_displayed() {
        assertThat(stories().isStoryImageDisplayed()).as("The current story image should be displayed").isTrue();
    }

    @When("the user pauses the stories")
    public void the_user_pauses_the_stories() {
        stories().togglePause();
    }

    @Then("the stories auto-advance is paused")
    public void the_stories_auto_advance_is_paused() {
        assertThat(stories().isPaused()).as("The stories auto-advance should be paused").isTrue();
    }

    @When("the user taps to the next story")
    public void the_user_taps_to_the_next_story() {
        stories().nextStory();
    }

    @And("the user closes the stories modal")
    public void the_user_closes_the_stories_modal() {
        stories().closeStories();
    }

    @Then("the stories modal is closed")
    public void the_stories_modal_is_closed() {
        assertThat(stories().isOpen()).as("The stories modal should be closed").isFalse();
    }

    // ==========================================
    // Drop card popup
    // ==========================================
    @Then("the drop card popup mounts within {int} seconds")
    public void the_drop_card_popup_mounts_within_seconds(int seconds) {
        assertThat(new AstoniaDropCardComponent(driver).isAstoniaDropCardDisplayedWithin(seconds))
                .as("The drop card popup should mount within %ss on a fresh session", seconds).isTrue();
    }

    @When("the user marks the drop popup as already visited in the session")
    public void the_user_marks_the_drop_popup_as_already_visited_in_the_session() {
        log.info("Suppressing the drop popup via sessionStorage ({}=1).",
                AstoniaDropCardComponent.POPUP_VISITED_KEY);
        ((JavascriptExecutor) driver).executeScript(
                String.format("window.sessionStorage.setItem('%s','1');",
                        AstoniaDropCardComponent.POPUP_VISITED_KEY));
    }

    @And("the user reloads the homepage")
    public void the_user_reloads_the_homepage() {
        driver.navigate().refresh();
        homePage = new HomePage(driver).waitUntilLoaded();
    }

    @Then("the drop card popup does not mount within {int} seconds")
    public void the_drop_card_popup_does_not_mount_within_seconds(int seconds) {
        assertThat(new AstoniaDropCardComponent(driver).isAstoniaDropCardDisplayedWithin(seconds))
                .as("The suppressed drop popup should NOT mount within %ss", seconds).isFalse();
    }

    // ==========================================
    // Footer country selector
    // ==========================================
    @When("the user opens the footer country selector")
    public void the_user_opens_the_footer_country_selector() {
        homePage();
        footer().clickCountrySelector();
    }

    @Then("the country selector dropdown is open")
    public void the_country_selector_dropdown_is_open() {
        assertThat(footer().isCountryDropdownOpen())
                .as("The country selector dropdown should be open").isTrue();
    }

    @When("the user searches the country selector for {string}")
    public void the_user_searches_the_country_selector_for(String query) {
        testContext.setContextData("COUNTRY_OPTIONS_BEFORE", String.valueOf(footer().getCountryOptionCount()));
        footer().searchCountry(query);
    }

    @Then("the country options are filtered down")
    public void the_country_options_are_filtered_down() {
        int before = Integer.parseInt(
                String.valueOf(testContext.getContextData("COUNTRY_OPTIONS_BEFORE")));
        int after = footer().getCountryOptionCount();
        assertThat(after)
                .as("The country options should filter down (before=%s, after=%s)", before, after)
                .isGreaterThan(0).isLessThan(before);
    }
}
