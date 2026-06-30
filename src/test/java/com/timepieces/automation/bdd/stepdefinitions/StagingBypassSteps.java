package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.web.StagingGatePage;
import com.timepieces.automation.pages.web.HomePage;
import com.timepieces.automation.utils.BrowserUtils;
import com.timepieces.automation.utils.ConfigReader;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class StagingBypassSteps {

    private final TestContext testContext;
    private StagingGatePage stagingGatePage;
    private HomePage homePage;

    public StagingBypassSteps(TestContext testContext) {
        this.testContext = testContext;
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
            log.info("Staging gate password field is not present (likely already bypassed). Skipping entering password.");
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
