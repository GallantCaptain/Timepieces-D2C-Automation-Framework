package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.components.SiteOverlayHandler;
import com.timepieces.automation.pages.web.StagingGatePage;
import com.timepieces.automation.pages.web.HomePage;
import com.timepieces.automation.utils.BrowserUtils;
import com.timepieces.automation.utils.ConfigReader;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

/**
 * StagingBypassSteps
* @Description "Description of StagingBypassSteps"
* @since 2026-07-03
* @version 1.0
* @author Avinash Rath
 */

@Log4j2
public class StagingBypassSteps {

    private final TestContext testContext;
    private StagingGatePage stagingGatePage;
    private HomePage homePage;

    public StagingBypassSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Given("the user bypasses the staging password gate")
    public void the_user_bypasses_the_staging_password_gate() {
        org.openqa.selenium.WebDriver driver = DriverFactory.getDriver();
        String baseUrl = ConfigReader.get("base.url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://staging.timepieces-d2c.example";
        }
        log.info("Bypassing staging gate for storefront: {}", baseUrl);
        driver.get(baseUrl);
        new StagingGatePage(driver).unlockIfPresent();
        // CLEAR THE CONSENT BAR HERE TOO.
        //
        // This step navigates with driver.get() directly rather than through
        // BasePage.open(), so it never reaches the SiteOverlayHandler sweep that open()
        // performs — and this step is the Background for NINE feature files. Every
        // scenario in them therefore started on a page with the cookie bar still up,
        // which is why ElementClickInterceptedException kept appearing on controls low in
        // the viewport (footer links, the guarantee/returns tiles, the cart drawer's
        // checkout button) long after the bar was supposedly handled globally.
        //
        // NOTE the step name is historical and a little misleading: unlockIfPresent() is
        // environment-agnostic and simply no-ops after a 4s probe when no gate exists, so
        // this Background is SAFE on production — it is not what breaks prod runs.
        SiteOverlayHandler.dismissBlockingOverlays(driver);
    }

    @Given("the user is on the storefront homepage")
    public void the_user_is_on_the_storefront_homepage() {
        org.openqa.selenium.WebDriver driver = DriverFactory.getDriver();
        String baseUrl = ConfigReader.get("base.url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://staging.timepieces-d2c.example";
        }
        log.info("Navigating to storefront homepage: {}", baseUrl);
        driver.get(baseUrl);
        // Same reason as the gate-bypass step above: this navigates with driver.get()
        // rather than BasePage.open(), so it misses the global overlay sweep. Without
        // this, the consent bar survives into the scenario and eats clicks near the
        // bottom of the page.
        SiteOverlayHandler.dismissBlockingOverlays(driver);
        new HomePage(driver).waitUntilLoaded();
    }

    @Given("the user navigates to the staging environment application URL {string}")
    public void the_user_navigates_to_the_staging_environment_application_url(String url) {
        stagingGatePage = new StagingGatePage(DriverFactory.getDriver());
        homePage = new HomePage(DriverFactory.getDriver());

        String resolvedUrl = resolvePlaceholder(url);
        log.info("Navigating to staging environment URL: {}", resolvedUrl);
        BrowserUtils.navigateTo(DriverFactory.getDriver(), resolvedUrl, 15);
    }

    @When("the user enters the valid gatekeeper password {string}")
    public void the_user_enters_the_valid_gatekeeper_password(String password) {
        if (stagingGatePage.isPasswordGatePresent()) {
            String resolvedPassword = resolvePlaceholder(password);
            stagingGatePage.enterPassword(resolvedPassword);
        } else {
            log.info(
                    "Staging gate password field is not present (likely already bypassed). Skipping entering password.");
        }
    }

    @And("clicks the submit button")
    public void clicks_the_submit_button() {
        if (stagingGatePage.isPasswordGatePresent()) {
            stagingGatePage.clickSubmitButton();
        } else {
            log.info("Staging gate was not present. Skipping click submit.");
        }
    }

    @Then("the password wall clears successfully")
    public void the_password_wall_clears_successfully() {
        assertThat(stagingGatePage.isPasswordWallCleared())
                .as("The staging gate password wall was not cleared successfully.")
                .isTrue();
    }

    @And("the active storefront landing page initializes with the visible hero section")
    public void the_active_storefront_landing_page_initializes_with_the_visible_hero_section() {
        assertThat(homePage.isHeroSectionDisplayed())
                .as("Active storefront landing page hero section was not visible after the gate cleared.")
                .isTrue();
    }

    private String resolvePlaceholder(String value) {
        if (value != null && value.startsWith("${") && value.endsWith("}")) {
            String key = value.substring(2, value.length() - 1);
            String resolved = ConfigReader.get(key);
            if (resolved == null) {
                if ("STAGING_GATE_PASS".equals(key)) {
                    resolved = ConfigReader.get("staging.password");
                } else if ("base.url".equals(key)) {
                    resolved = ConfigReader.get("base.url");
                }
            }
            if (resolved != null) {
                return resolved;
            }
        }
        return value;
    }
}
