package com.timepieces.automation.bdd.runners;

import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Test;
import lombok.extern.log4j.Log4j2;

/**
 * @Description "Description of RegressionRunner"
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
@Test(groups = "regression")
@CucumberOptions(features = {
                "src/test/resources/features/01_authentication_identity/Staging_Password_Bypass.feature",
                "src/test/resources/features/01_authentication_identity/User_Authentication_And_OTP_Verification.feature",
                "src/test/resources/features/01_authentication_identity/OTP_Login_End_To_End.feature",
                "src/test/resources/features/01_authentication_identity/OTP_Bruteforce_Security.feature",
                "src/test/resources/features/01_authentication_identity/API_Backed_UI_Session.feature",
                "src/test/resources/features/03_checkout_payments/Checkout_Idempotency.feature",
                "src/test/resources/features/02_catalog_product_discovery/Global_Footer_Navigation.feature",
                "src/test/resources/features/02_catalog_product_discovery/Homepage_Content_Sections.feature",
                "src/test/resources/features/02_catalog_product_discovery/Accessory_Product_Details.feature",
                "src/test/resources/features/02_catalog_product_discovery/Accessory_Strap_Configurator.feature",
                "src/test/resources/features/04_checkout_secure_funnel/Cart_Addition_And_Domestic_Checkout.feature",
                "src/test/resources/features/04_checkout_secure_funnel/Checkout_Form_Validation.feature",
                "src/test/resources/features/05_payment_integration/Payment_Integration_Validation.feature",
                "src/test/resources/features/06_customer_retention_wishlist/Wishlist_And_Account_Management.feature",
                "src/test/resources/features/06_customer_retention_wishlist/Guest_Order_Tracking.feature",
                "src/test/resources/features/07_content_brand_pages/Legal_Policy_Pages.feature",
                "src/test/resources/features/07_content_brand_pages/Brand_Content_Pages.feature",
                "src/test/resources/features/07_content_brand_pages/Friends_And_Partners.feature",
                "src/test/resources/features/08_timepieces_world/Timepieces_World_Experience.feature",
                "src/test/resources/features/08_timepieces_world/Timepieces_World_Showcase.feature"
                // Account_Orders_Management.feature is @AuthGated (NOT @Regression) —
                // deliberately unregistered: production login is Cloudflare-gated.
}, glue = { "com.timepieces.automation.bdd.stepdefinitions", "com.timepieces.automation.bdd.hooks" },
                /*
                 * Disjointness with Smoke is enforced on the SmokeRunner side (tags =
                 * "@Smokeand not @Regression"), so this runner OWNS every @Regression scenario
                 * including those also tagged @Smoke (e.g. the login/OTP init). Keep this as
                 * plain "@Regression" adding "and not @Smoke" here too would exclude
                 * dual-tagged scenarios from BOTH runners, so they would run nowhere.
                 * Exactly one runner must carry the overlap.
                 */
                tags = "@Regression", plugin = {
                                "pretty",
                                "html:target/cucumber-reports/regression-report.html",
                                "json:target/cucumber-reports/RegressionCucumberTestReport.json",
                                "rerun:target/cucumber-reports/regression-rerun.txt",
                                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
                }, monochrome = true, dryRun = false)
public class RegressionRunner extends BaseRunner {
        // Inherits parallel execution and global hooks from BaseRunner
}
