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
        // THE CLI WINS. This used to do an unconditional System.setProperty("env", env),
        // which silently overwrote -Denv with the suite XML's <parameter> — inverting the
        // framework's own documented 3-tier precedence (-D  >  OS env  >  properties file).
        //
        // The consequence was not theoretical. Every suite XML hard-codes
        // <parameter name="environment" value="staging"/>, so
        //     ./mvnw test -Dsurefire.suite=testng-bdd.xml -Denv=production
        // ran against STAGING while the operator, the command line and the report all said
        // production. It was caught on 2026-07-17 by a staging-gate password prompt
        // (input[type='password']) appearing in a run that was supposed to be pointed at
        // production, which has no such gate. A test framework that quietly tests the wrong
        // environment is worse than one that fails: every result it gives you is a lie
        // about a system you did not mean to look at.
        //
        // Now: an explicit -Denv is honoured, and the suite parameter only supplies the
        // default when the operator did not choose one.
        String cliEnv = System.getProperty("env");
        String effectiveEnv = (cliEnv == null || cliEnv.isBlank()) ? env : cliEnv.trim();

        if (cliEnv != null && !cliEnv.isBlank() && !cliEnv.equalsIgnoreCase(env)) {
            log.warn("Suite parameter environment='{}' is being IGNORED because -Denv={} was "
                    + "passed explicitly. The command line wins.", env, cliEnv);
        }

        System.setProperty("test.environment", effectiveEnv);
        System.setProperty("env", effectiveEnv);
        log.info("=== BEFORE TEST: Starting test execution for environment: {} ===", effectiveEnv);
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
