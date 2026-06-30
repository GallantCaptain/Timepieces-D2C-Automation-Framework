package com.timepieces.automation.bdd.stepdefinitions;

import static org.assertj.core.api.Assertions.assertThat;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.tdd.api.AuthenticationEngine;
import com.timepieces.automation.pages.web.account.AccountPage;
import com.timepieces.automation.utils.ConfigReader;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

@Log4j2
public class AccountSteps {

    private final TestContext testContext;
    private final AuthenticationEngine authEngine;
    private AccountPage accountPage;

    public AccountSteps(TestContext testContext) {
        this.testContext = testContext;
        this.authEngine = new AuthenticationEngine(testContext);
    }

    @Given("the customer bypasses the UI login using API identity {string}")
    public void the_customer_bypasses_the_ui_login_using_api_identity(String emailOrPhone) {
        WebDriver driver = DriverFactory.getDriver();
        
        // 1. Fetch token via API and save to context
        authEngine.loginViaBackendApi(emailOrPhone);
        String token = (String) testContext.getContextData("AUTH_TOKEN");
        
        // 2. Open page on target domain before setting cookies
        String baseUrl = ConfigReader.get("base.url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://staging.timepieces-d2c.example";
        }
        driver.get(baseUrl + "/404");

        // 3. Inject token as cookie
        log.info("Injecting JWT token into cookie jar.");
        Cookie authCookie = new Cookie.Builder("timepieces_session", token)
                .domain("staging.timepieces-d2c.example")
                .path("/")
                .isSecure(true)
                .build();
        driver.manage().addCookie(authCookie);
    }

    @Given("they navigate straight to their account details page")
    public void they_navigate_straight_to_their_account_details_page() {
        WebDriver driver = DriverFactory.getDriver();
        String baseUrl = ConfigReader.get("base.url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://staging.timepieces-d2c.example";
        }
        driver.get(baseUrl + "/account");
        accountPage = new AccountPage(driver);
    }

    @Then("the dashboard should welcome them without prompting for an OTP login")
    public void the_dashboard_should_welcome_them_without_prompting_for_an_otp_login() {
        // Assert welcome message is displayed
        assertThat(accountPage.isAccountPageLoaded())
                .as("Dashboard welcome message should be displayed for the bypassed session.")
                .isTrue();
    }
}
