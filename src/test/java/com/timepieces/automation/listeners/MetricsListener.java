package com.timepieces.automation.listeners;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import com.timepieces.automation.utils.TestMetricsPublisher;
import lombok.extern.log4j.Log4j2;
import java.util.Map;

/**
 * MetricsListener
 * 
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of MetricsListener"
* @since 2026-07-03
 */

@Log4j2
public class MetricsListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        log.info("=== Suite execution started: " + suite.getName() + " ===");
    }

    @Override
    public void onFinish(ISuite suite) {
        log.info("=== Suite execution finished: " + suite.getName() + ". Processing metrics... ===");

        int passed = 0;
        int failed = 0;
        int skipped = 0;
        long totalDurationMs = 0;

        Map<String, ISuiteResult> results = suite.getResults();
        for (ISuiteResult r : results.values()) {
            ITestContext context = r.getTestContext();
            passed += context.getPassedTests().size();
            failed += context.getFailedTests().size();
            skipped += context.getSkippedTests().size();

            long start = context.getStartDate().getTime();
            long end = context.getEndDate().getTime();
            totalDurationMs += (end - start);
        }

        int total = passed + failed + skipped;
        int durationSeconds = (int) (totalDurationMs / 1000);

        String buildId = System.getenv("GITHUB_RUN_NUMBER");
        if (buildId == null || buildId.isEmpty()) {
            buildId = "LOCAL_" + System.currentTimeMillis();
        }

        String env = System.getProperty("env");
        if (env == null || env.isEmpty()) {
            env = "staging";
        }

        String browser = System.getProperty("browser");
        if (browser == null || browser.isEmpty()) {
            browser = "chrome"; // Default fallback
        }

        log.info(String.format("Metrics Summary - Total: %d, Passed: %d, Failed: %d, Skipped: %d, Duration: %ds",
                total, passed, failed, skipped, durationSeconds));

        // Only publish if there are actual test runs to prevent writing empty data
        if (total > 0) {
            TestMetricsPublisher.publish(buildId, env, browser, total, passed, failed, durationSeconds);
        }
    }
}
