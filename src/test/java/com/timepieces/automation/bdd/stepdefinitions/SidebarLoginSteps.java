package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.pages.components.HeaderComponent;
import com.timepieces.automation.pages.components.SidebarMenuComponent;
import com.timepieces.automation.pages.web.account.LoginPage;
import com.timepieces.automation.utils.ConfigReader;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;

/**
 * SidebarLoginSteps
* @Description "Description of SidebarLoginSteps"
* @since 2026-07-03
* @version 1.0
* @author Avinash Rath
 */

@Log4j2
public class SidebarLoginSteps {
    private final TestContext testContext;
    private final WebDriver driver;
    private final WebDriverWait wait;

    private HeaderComponent headerComponent;
    private SidebarMenuComponent sidebarMenuComponent;

    public SidebarLoginSteps(TestContext testContext) {
        this.testContext = testContext;
        this.driver = DriverFactory.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Given("the user has opened the global responsive sidebar menu")
    public void the_user_has_opened_the_global_responsive_sidebar_menu() {
        headerComponent = new HeaderComponent(driver);
        sidebarMenuComponent = new SidebarMenuComponent(driver);

        String baseUrl = ConfigReader.get("base.url");
        log.info("Navigating to home page to open sidebar: {}", baseUrl);
        driver.get(baseUrl);

        // Bypass staging gate if present
        new com.timepieces.automation.pages.web.StagingGatePage(driver).unlockIfPresent();

        log.info("Clicking the hamburger menu button via HeaderComponent.");
        headerComponent.clickMenuToggle();

        log.info("Waiting for the sidebar menu to open.");
        assertThat(sidebarMenuComponent.isSidebarDisplayed())
                .as("Sidebar menu was not displayed.")
                .isTrue();
    }

    @When("the user clicks on the {string} interaction link")
    public void the_user_clicks_on_the_interaction_link(String linkName) {
        if ("Sign In".equalsIgnoreCase(linkName)) {
            log.info("Clicking the Sign In button inside the sidebar via SidebarMenuComponent.");
            sidebarMenuComponent.clickSignIn();
        } else {
            throw new IllegalArgumentException("Unsupported sidebar link interaction requested: " + linkName);
        }
    }

    @Then("the application should display the user authentication interface")
    public void the_application_should_display_the_user_authentication_interface() {
        LoginPage loginPage = new LoginPage(driver);
        assertThat(loginPage.isWelcomeMessageDisplayed())
                .as("The user authentication interface welcome message was not displayed.")
                .isTrue();
    }

    @Then("the session context should update to the authenticated state")
    public void the_session_context_should_update_to_the_authenticated_state() {
        log.info("Verifying authentication state.");
        wait.until(d -> !d.getCurrentUrl().contains("/login"));
    }

    @And("the sidebar element should display the personalized element {string}")
    public void the_sidebar_element_should_display_the_personalized_element(String accountMarker) {
        if (!sidebarMenuComponent.isSidebarDisplayed()) {
            log.info("Sidebar closed. Re-opening to verify personalized element.");
            headerComponent.clickMenuToggle();
            assertThat(sidebarMenuComponent.isSidebarDisplayed())
                    .as("Sidebar menu was not displayed.")
                    .isTrue();
        }

        log.info("Verifying personalized element in sidebar matching: {}", accountMarker);
        By accountElement = By
                .xpath("//div[@data-testid='nav-sidebar']//a[@href='/account' or contains(normalize-space(text()), '"
                        + accountMarker + "') or contains(normalize-space(text()), 'Account')]");
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(accountElement));
        assertThat(element.isDisplayed())
                .as("Personalized account element in sidebar was not displayed.")
                .isTrue();
    }
}
