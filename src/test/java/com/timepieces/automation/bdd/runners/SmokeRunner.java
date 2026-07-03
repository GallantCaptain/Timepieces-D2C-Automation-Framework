package com.timepieces.automation.bdd.runners;

import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Test;
import lombok.extern.log4j.Log4j2;

/**
 * SmokeRunner
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
@Test(groups = "smoke")
@CucumberOptions(features = {
                "src/test/resources/features/01_authentication_identity/Staging_Password_Bypass.feature",
                "src/test/resources/features/01_authentication_identity/User_Authentication_And_OTP_Verification.feature",
                "src/test/resources/features/01_authentication_identity/API_Backed_UI_Session.feature",
                "src/test/resources/features/02_catalog_product_discovery/Global_Footer_Navigation.feature"
/*
 * NOTE: opt-in features (@OtpE2E, @OtpBruteForce) are registered ONLY in
 * RegressionRunner.They are always invoked via -Dcucumber.filter.tags, which
 * overrides BOTH runners' tags; registering them in one runner only keeps
 * them single-execution under that override.
 */
}, glue = { "com.timepieces.automation.bdd.stepdefinitions",
                "com.timepieces.automation.bdd.hooks" }, tags = "@Smoke", plugin = {
                                "pretty",
                                "html:target/cucumber-reports/smoke-report.html",
                                "json:target/cucumber-reports/SmokeCucumberTestReport.json",
                                "rerun:target/cucumber-reports/smoke-rerun.txt",
                                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
                }, monochrome = false, dryRun = false)
/**
 * @Description "Description of SmokeRunner"
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
 */
public class SmokeRunner extends BaseRunner {
        // Inherits parallel execution and global hooks from BaseRunner
}
