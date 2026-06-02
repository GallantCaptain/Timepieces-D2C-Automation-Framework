package com.timepieces.automation.runners;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = { "com.timepieces.automation.stepdefinitions", "com.timepieces.automation.hooks" },
    tags = "@Regression",
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
