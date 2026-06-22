package com.timepieces.automation.runners;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = {
        "src/test/resources/features/01_authentication_identity/Staging_Password_Bypass.feature",
        "src/test/resources/features/01_authentication_identity/User_Authentication_And_OTP_Verification.feature",
        "src/test/resources/features/01_authentication_identity/OTP_Login_End_To_End.feature"
    },
    glue = { "com.timepieces.automation.stepdefinitions", "com.timepieces.automation.hooks" },
    // "and not @Smoke" prevents a scenario tagged BOTH @Smoke and @Regression from
    // executing twice (once per runner) within a single suite. Double execution made
    // OTP scenarios hammer the same phone number back-to-back and trip the login
    // rate-limiter ("Too many attempts, wait Ns"). Smoke owns the dual-tagged scenarios.
    tags = "@Regression and not @Smoke",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/regression-report.html",
        "json:target/cucumber-reports/RegressionCucumberTestReport.json",
        "rerun:target/cucumber-reports/regression-rerun.txt",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
    },
    monochrome = true,
    dryRun = false
)
public class RegressionRunner extends BaseRunner {
    // Inherits parallel execution and global hooks from BaseRunner
}
