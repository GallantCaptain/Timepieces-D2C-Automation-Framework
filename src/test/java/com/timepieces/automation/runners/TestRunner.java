package com.timepieces.automation.runners;

import org.testng.annotations.DataProvider;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

//@RunWith(Cucumber.class)
/**
 * Main Test Runner integrating Cucumber with TestNG.
 * * - features: Points to the directory containing our .feature files.
 * - glue: Points to Step Definitions AND Hooks so Cucumber can find the execution lifecycle.
 * - plugin: Generates a standard HTML report and hooks into Allure for our CI/CD dashboard.
 * - tags: Allows us to execute specific suites (e.g., "@Smoke", "@Regression").
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue = { "com.timepieces.automation.stepdefinitions",
    "com.timepieces.automation.hooks" },
plugin = {
    "pretty",
    "html:target/cucumber-reports/cucumber-pretty",
    "json:target/cucumber-reports/CucumberTestReport.json",
    "rerun:target/cucumber-reports/rerun.txt",
    "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm" // Integrates with Allure in pom.xml
},
monochrome = true,
dryRun = false,
tags = "@Smoke"
)
public class TestRunner extends AbstractTestNGCucumberTests {
    /**
     * ARCHITECTURAL DECISION:
     * Overriding the default DataProvider to return scenarios in parallel.
     * Because our DriverFactory uses ThreadLocal, setting parallel = true 
     * allows TestNG to safely spin up a new browser thread for every single scenario.
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
