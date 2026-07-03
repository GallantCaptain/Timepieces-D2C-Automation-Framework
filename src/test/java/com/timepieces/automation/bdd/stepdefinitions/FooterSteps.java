package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.components.FooterComponent;
import com.timepieces.automation.pages.web.HomePage;
import com.timepieces.automation.utils.ConfigReader;
import com.timepieces.automation.utils.WindowUtils;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.extern.log4j.Log4j2;

/**
 * FooterSteps
* @Description "Description of FooterSteps"
* @since 2026-07-03
* @version 1.0
* @author Avinash Rath
 */

@Log4j2
public class FooterSteps {
    private final TestContext testContext;
    private final WebDriver driver;
    private final FooterComponent footerComponent;

    public FooterSteps(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverFactory.getDriver();
        this.footerComponent = new FooterComponent(driver);
    }

    @Given("the user is on the Timepieces storefront homepage")
    public void the_user_is_on_the_timepieces_storefront_homepage() {
        String baseUrl = ConfigReader.get("base.url");
        log.info("Navigating to Timepieces storefront homepage: {}", baseUrl);
        driver.get(baseUrl);
        new com.timepieces.automation.pages.web.StagingGatePage(driver).unlockIfPresent();
        new HomePage(driver).waitUntilLoaded();
    }

    @Then("the global footer should be visible at the bottom of the page")
    public void the_global_footer_should_be_visible_at_the_bottom_of_the_page() {
        log.info("Verifying global footer is visible.");
        assertThat(footerComponent.isFooterDisplayed())
                .as("Global footer container was not displayed.")
                .isTrue();
    }

    @And("the footer should display the following policy and utility links:")
    public void the_footer_should_display_the_following_policy_and_utility_links(DataTable dataTable) {
        List<String> expectedLinks = dataTable.asList();
        log.info("Verifying policy and utility links in the footer: {}", expectedLinks);

        boolean allLinksDisplayed = footerComponent.areLinksDisplayed(expectedLinks);
        assertThat(allLinksDisplayed)
                .as("Some expected footer policy/utility links were not displayed.")
                .isTrue();
    }

    @And("each footer link should navigate to the correct page in a new tab when clicked and then close the tab:")
    public void each_footer_link_should_navigate_to_the_correct_page_in_a_new_tab(DataTable dataTable) {
        List<String> expectedLinks = dataTable.asList();
        String originalHandle = WindowUtils.getCurrentWindowHandle(driver);

        Keys modifier = System.getProperty("os.name").toLowerCase().contains("mac")
                ? Keys.COMMAND
                : Keys.CONTROL;

        for (String linkText : expectedLinks) {
            log.info("Verifying navigation for link: [{}]", linkText);

            // Resolve the expected full URL from the link's href attribute (via BasePage)
            String expectedPath = footerComponent.getLinkHref(linkText);
            log.info("Expected target URL/path: {}", expectedPath);

            // Retrieve the anchor element via FooterComponent (delegates to
            // BasePage.getElement())
            WebElement element = footerComponent.getLinkElement(linkText);

            // Ctrl/Cmd+Click to open in a new tab without navigating the current page
            new Actions(driver).keyDown(modifier).click(element).keyUp(modifier).build().perform();

            // Wait for the new tab and switch into it
            WindowUtils.waitAndSwitchToNewWindow(driver, originalHandle, 15);

            // Validate the URL in the new tab matches the expected path
            String currentUrl = driver.getCurrentUrl();
            log.info("Navigated tab URL: {}", currentUrl);
            assertThat(currentUrl)
                    .as("The URL of the new tab did not contain the expected path for link [%s].", linkText)
                    .contains(expectedPath);

            // Close the new tab and return focus to the original window
            WindowUtils.closeCurrentWindowAndReturn(driver, originalHandle);
            log.info("Closed the tab and returned to original window.");
        }
    }
}
