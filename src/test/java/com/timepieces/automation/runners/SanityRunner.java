package com.timepieces.automation.runners;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = { "com.timepieces.automation.stepdefinitions", "com.timepieces.automation.hooks" },
    tags = "@Sanity",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/sanity-report.html",
        "json:target/cucumber-reports/SanityCucumberTestReport.json",
        "rerun:target/cucumber-reports/sanity-rerun.txt",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
    },
    monochrome = true,
    dryRun = false
)
public class SanityRunner extends BaseRunner {
    // Inherits parallel execution and global hooks from BaseRunner
}
