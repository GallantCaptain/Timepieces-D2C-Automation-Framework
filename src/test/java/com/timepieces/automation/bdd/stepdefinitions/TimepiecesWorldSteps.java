package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.web.otherPages.timepieces_world.TimepiecesWorld_ArvionPage;
import com.timepieces.automation.pages.web.otherPages.timepieces_world.TimepiecesWorld_AstoniaPage;
import com.timepieces.automation.pages.web.otherPages.timepieces_world.TimepiecesWorld_AuriquaPage;
import com.timepieces.automation.pages.web.otherPages.timepieces_world.TimepiecesWorld_ExperiencePage;
import com.timepieces.automation.pages.web.otherPages.timepieces_world.TimepiecesWorld_ManifestaPage;
import com.timepieces.automation.pages.web.otherPages.timepieces_world.TimepiecesWorld_MonarchPage;
import com.timepieces.automation.pages.web.otherPages.timepieces_world.SharedTemplate_TimepiecesWorldProduct;
import com.timepieces.automation.pages.web.otherPages.timepieces_world.components.TimepiecesWorld_HeaderComponent;
import com.timepieces.automation.pages.web.otherPages.timepieces_world.components.TimepiecesWorld_SidebarMenuComponent;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

/**
 * TimepiecesWorldSteps — glue for the Timepieces World features (Phase 4A mapping),
 * driving {@link TimepiecesWorld_ExperiencePage},
 * {@link SharedTemplate_TimepiecesWorldProduct} (via the 5 family pages) and the
 * RW chrome components.
 *
 * <p>
 * The microsite lives on the fixed {@code https://world.timepieces-d2c.example} origin
 * (independent of the configured {@code base.url}; no staging gate applies).
 * Native scrolling is disabled there — the canvas is advanced with synthetic
 * wheel events, so the "nudge until a family scene appears" step dispatches
 * repeated {@code WheelEvent}s and polls for the DISCOVER CTA. The waitlist
 * submit raises a REAL signup, so no step ever submits the form.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TimepiecesWorldSteps"
* @since 2026-07-16
 */

@Log4j2
public class TimepiecesWorldSteps {

    private static final String WORLD_ORIGIN = "world.timepieces-d2c.example";

    @SuppressWarnings("unused")
    private final TestContext testContext;
    private final WebDriver driver;

    private TimepiecesWorld_ExperiencePage experiencePage;
    private SharedTemplate_TimepiecesWorldProduct<?> showcasePage;
    private TimepiecesWorld_HeaderComponent header;
    private TimepiecesWorld_SidebarMenuComponent sidebar;

    public TimepiecesWorldSteps(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverFactory.getDriver();
    }

    // ==========================================
    // Private helpers
    // ==========================================
    /** Maps a lowercase family handle to its concrete showcase page object. */
    private SharedTemplate_TimepiecesWorldProduct<?> showcaseFor(String family) {
        switch (family.toLowerCase()) {
            case "arvion":
                return new TimepiecesWorld_ArvionPage(driver);
            case "monarch":
                return new TimepiecesWorld_MonarchPage(driver);
            case "auriqua":
                return new TimepiecesWorld_AuriquaPage(driver);
            case "astonia":
                return new TimepiecesWorld_AstoniaPage(driver);
            case "manifesta":
                return new TimepiecesWorld_ManifestaPage(driver);
            default:
                throw new IllegalArgumentException("Unknown Timepieces World family: " + family);
        }
    }

    // ==========================================
    // Experience page
    // ==========================================
    @Given("the user navigates to the Timepieces World experience page")
    public void the_user_navigates_to_the_timepieces_world_experience_page() {
        log.info("STEP: open the Timepieces World experience (world.timepieces-d2c.example).");
        experiencePage = new TimepiecesWorld_ExperiencePage(driver).open();
        header = new TimepiecesWorld_HeaderComponent(driver);
        sidebar = new TimepiecesWorld_SidebarMenuComponent(driver);
    }

    @Then("the Enter Experience gate is displayed")
    public void the_enter_experience_gate_is_displayed() {
        assertThat(experiencePage.isEnterExperienceDisplayed())
                .as("The 'Enter Experience' welcome gate should be displayed").isTrue();
    }

    @When("the user enters the experience")
    public void the_user_enters_the_experience() {
        experiencePage.enterExperience();
    }

    @Then("the experience canvas is running")
    public void the_experience_canvas_is_running() {
        assertThat(experiencePage.isCanvasDisplayed())
                .as("The experience canvas should be displayed").isTrue();
        assertThat(experiencePage.isProgressRailDisplayed())
                .as("The right-side progress rail should be displayed").isTrue();
    }

    @And("the progress rail shows {int} scene segments")
    public void the_progress_rail_shows_scene_segments(int expected) {
        assertThat(experiencePage.getProgressSegmentCount())
                .as("The progress rail should show %s scene segments", expected).isEqualTo(expected);
    }

    @When("the user nudges the canvas forward until a family scene appears")
    public void the_user_nudges_the_canvas_forward_until_a_family_scene_appears() {
        log.info("STEP: nudge the canvas forward until a DISCOVER CTA appears.");
        new WebDriverWait(driver, Duration.ofSeconds(90)).until(d -> {
            if (experiencePage.isDiscoverCtaDisplayed()) {
                return true;
            }
            experiencePage.advanceCanvas(3000);
            return false;
        });
    }

    @Then("the active scene shows a family heading")
    public void the_active_scene_shows_a_family_heading() {
        assertThat(experiencePage.getActiveFamilyHeading())
                .as("The active scene should show a family heading").isNotBlank();
    }

    @And("the DISCOVER call to action is displayed")
    public void the_discover_call_to_action_is_displayed() {
        assertThat(experiencePage.isDiscoverCtaDisplayed())
                .as("The DISCOVER CTA should be displayed on a family scene").isTrue();
    }

    @When("the user clicks the DISCOVER call to action")
    public void the_user_clicks_the_discover_call_to_action() {
        experiencePage.clickDiscover();
    }

    @Then("the browser lands on a Timepieces World showcase page")
    public void the_browser_lands_on_a_timepieces_world_showcase_page() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> d.getCurrentUrl().contains("/products/"));
        assertThat(driver.getCurrentUrl())
                .as("DISCOVER should land on a world.timepieces-d2c.example showcase page")
                .contains(WORLD_ORIGIN).contains("/products/");
    }

    // ==========================================
    // Chrome (header + sidebar)
    // ==========================================
    @When("the user opens the Timepieces World sidebar menu")
    public void the_user_opens_the_timepieces_world_sidebar_menu() {
        header.toggleMenu();
    }

    @Then("the Timepieces World sidebar lists all {int} family destinations")
    public void the_timepieces_world_sidebar_lists_all_family_destinations(int expected) {
        assertThat(sidebar.isOpen()).as("The RW sidebar should be open").isTrue();
        assertThat(sidebar.getFamilyLinkCount())
                .as("The RW sidebar should list %s family links", expected).isEqualTo(expected);
        for (String family : new String[] { "arvion", "monarch", "auriqua", "astonia", "manifesta" }) {
            assertThat(sidebar.hasFamilyLink(family))
                    .as("The RW sidebar should link to '%s'", family).isTrue();
        }
    }

    @When("the user closes the Timepieces World sidebar via the backdrop")
    public void the_user_closes_the_timepieces_world_sidebar_via_the_backdrop() {
        sidebar.closeViaBackdrop();
    }

    @Then("the Timepieces World sidebar is closed")
    public void the_timepieces_world_sidebar_is_closed() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> !sidebar.isOpen());
        assertThat(sidebar.isOpen()).as("The RW sidebar should be closed").isFalse();
    }

    @When("the user clicks the Timepieces logo in the Timepieces World header")
    public void the_user_clicks_the_timepieces_logo_in_the_timepieces_world_header() {
        if (header == null) {
            header = new TimepiecesWorld_HeaderComponent(driver);
        }
        header.clickLogo();
    }

    @Then("the browser lands on the main Timepieces storefront")
    public void the_browser_lands_on_the_main_timepieces_storefront() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> d.getCurrentUrl().contains("www.timepieces-d2c.example"));
        assertThat(driver.getCurrentUrl())
                .as("The logo should leave the experience page for the main storefront")
                .contains("www.timepieces-d2c.example");
    }

    @Then("the browser returns to the Timepieces World experience page")
    public void the_browser_returns_to_the_timepieces_world_experience_page() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> d.getCurrentUrl().contains(WORLD_ORIGIN) && !d.getCurrentUrl().contains("/products/"));
        assertThat(driver.getCurrentUrl())
                .as("The browser should return to the Timepieces World experience page")
                .contains(WORLD_ORIGIN).doesNotContain("/products/");
    }

    // ==========================================
    // Showcase pages
    // ==========================================
    @Given("the user navigates to the Timepieces World showcase of {string}")
    public void the_user_navigates_to_the_timepieces_world_showcase_of(String family) {
        log.info("STEP: open the Timepieces World showcase of '{}'.", family);
        showcasePage = showcaseFor(family);
        showcasePage.open();
        header = new TimepiecesWorld_HeaderComponent(driver);
    }

    @Then("the showcase title matches the {string} family pattern")
    public void the_showcase_title_matches_the_family_pattern(String family) {
        assertThat(showcasePage.isPageTitleCorrect())
                .as("The document title should match '<%s> | <Tagline> | Timepieces Watches'", family).isTrue();
    }

    @And("the family logo and tagline are displayed")
    public void the_family_logo_and_tagline_are_displayed() {
        assertThat(showcasePage.isFamilyLogoDisplayed())
                .as("The family logo should be displayed").isTrue();
        assertThat(showcasePage.isTaglineDisplayed())
                .as("The family tagline should be displayed").isTrue();
    }

    @And("the waitlist form is displayed")
    public void the_waitlist_form_is_displayed() {
        assertThat(showcasePage.isWaitlistFormDisplayed())
                .as("The waitlist signup form should be present").isTrue();
    }

    @And("the waitlist submit button is disabled")
    public void the_waitlist_submit_button_is_disabled() {
        assertThat(showcasePage.isWaitlistSubmitEnabled())
                .as("The waitlist submit should start DISABLED until the form validates").isFalse();
    }

    @Then("the showcase lists the calibre spec {string}")
    public void the_showcase_lists_the_calibre_spec(String specLabel) {
        assertThat(showcasePage.hasSpec(specLabel))
                .as("The calibre specs grid should list '%s'", specLabel).isTrue();
    }

    @And("material learn-more buttons are offered")
    public void material_learn_more_buttons_are_offered() {
        assertThat(showcasePage.getLearnMoreButtonCount())
                .as("'Learn more about <material>' buttons should be offered").isGreaterThan(0);
    }

    @And("the showcase footer support links are present")
    public void the_showcase_footer_support_links_are_present() {
        assertThat(showcasePage.areFooterLinksPresent())
                .as("The footer reservations/Instagram/LinkedIn links should be present").isTrue();
    }

    @When("the user clicks Back to scene")
    public void the_user_clicks_back_to_scene() {
        showcasePage.clickBackToScene();
    }
}
