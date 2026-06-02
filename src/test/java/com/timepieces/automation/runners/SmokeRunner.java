package com.timepieces.automation.runners;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = { "com.timepieces.automation.stepdefinitions", "com.timepieces.automation.hooks" },
    tags = "@Smoke",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/smoke-report.html",
        "json:target/cucumber-reports/SmokeCucumberTestReport.json",
        "rerun:target/cucumber-reports/smoke-rerun.txt",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
    },
    monochrome = true,
    dryRun = false
)
public class SmokeRunner extends BaseRunner {
    // Inherits parallel execution and global hooks from BaseRunner
}
