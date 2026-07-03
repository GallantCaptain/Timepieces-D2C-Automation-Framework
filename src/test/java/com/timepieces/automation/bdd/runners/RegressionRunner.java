package com.timepieces.automation.bdd.runners;

import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Test;
import lombok.extern.log4j.Log4j2;

/**
 * RegressionRunner
 * 
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
                "src/test/resources/features/02_catalog_product_discovery/Global_Footer_Navigation.feature"
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
/**
 * @Description "Description of RegressionRunner"
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
 */
public class RegressionRunner extends BaseRunner {
        // Inherits parallel execution and global hooks from BaseRunner
}
