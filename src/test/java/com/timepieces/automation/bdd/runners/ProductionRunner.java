package com.timepieces.automation.bdd.runners;

import io.cucumber.testng.CucumberOptions;

/**
 * ProductionRunner — the scope executed by the nightly production suite
 * (testng-production.xml, selected via -Dsurefire.suite).
 *
 * SCOPE: production currently runs ONLY scenarios explicitly vetted and tagged
 * {@code @ProdReady}. Login/OTP scenarios are deliberately excluded until a Twilio +
 * domestic virtual-number provider is integrated, because they would dispatch real OTPs
 * to a live number every night.
 *
 * IMPORTANT — today this runner matches ZERO scenarios on purpose: every implemented
 * step-definition flow (LoginSteps / SidebarLoginSteps / StagingBypassSteps) requires
 * login or the staging gate, and the non-login catalog/homepage/search/footer/checkout
 * features have no step definitions yet. As prod-safe, non-login coverage is implemented,
 * tag those scenarios {@code @ProdReady} and they will start running here automatically.
 * Until then the production nightly is a green no-op (it asserts nothing ran, rather than
 * spamming OTPs).
 *
 * The whole feature tree is scanned so a {@code @ProdReady} tag added anywhere is picked
 * up; unselected scenarios with undefined steps are ignored (only matched scenarios run).
 */
@CucumberOptions(
    features = { "src/test/resources/features" },
    glue = { "com.timepieces.automation.bdd.stepdefinitions", "com.timepieces.automation.bdd.hooks" },
    tags = "@ProdReady",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/production-report.html",
        "json:target/cucumber-reports/ProductionCucumberTestReport.json",
        "rerun:target/cucumber-reports/production-rerun.txt",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
    },
    monochrome = true,
    dryRun = false
)
public class ProductionRunner extends BaseRunner {
    // Inherits parallel execution and global hooks from BaseRunner
}
