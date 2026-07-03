package com.timepieces.automation.bdd.runners;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import lombok.extern.log4j.Log4j2;

/**
 * Abstract Base Runner integrating Cucumber with TestNG.
 * Houses global shared configuration hooks and parallel data provider.
 * 
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of BaseRunner"
* @since 2026-07-03
 */

@Log4j2
public abstract class BaseRunner extends AbstractTestNGCucumberTests {

    @BeforeSuite(alwaysRun = true)
    public void beforeSuiteSetup() {
        log.info("=== [START] BEFORE SUITE: Initialize Reports & Environment ===");
    }

    @BeforeTest(alwaysRun = true)
    @Parameters({ "environment" })
    public void beforeTestSetup(@Optional("staging") String env) {
        System.setProperty("test.environment", env);
        System.setProperty("env", env);
        log.info("=== BEFORE TEST: Starting test execution for environment: " + env + " ===");
    }

    @AfterTest(alwaysRun = true)
    public void afterTestTeardown() {
        log.info("=== AFTER TEST: Completed test execution for environment: " + System.getProperty("test.environment")
                + " ===");
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuiteTeardown() {
        log.info("=== [END] AFTER SUITE: All tests have completed. Performing global teardown. ===");
    }

    /**
     * ARCHITECTURAL DECISION:
     * Overriding the default DataProvider to return scenarios in parallel.
     * Because our DriverFactory uses ThreadLocal, setting parallel = true
     * allows TestNG to safely spin up a new browser thread for every single
     * scenario.
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
