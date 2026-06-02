package com.timepieces.automation.listeners;

import com.timepieces.automation.utils.ConfigReader;
import lombok.extern.log4j.Log4j2;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG IRetryAnalyzer implementation that allows failed tests to be retried.
 * Dynamically resolves retry limits via ConfigReader or defaults to 1 retry.
 */
@Log4j2
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    /**
     * Determines whether a test scenario should be retried upon failure.
     * Reads 'max.retry.count' parameter dynamically or defaults to 1 retry.
     */
    @Override
    public boolean retry(ITestResult result) {
        int maxRetryLimit = getMaxRetryLimit();
        if (retryCount < maxRetryLimit) {
            retryCount++;
            log.warn("=== RETRYING TEST SCENARIO: [{}] | Attempt: [{}/{}] ===", 
                    result.getName(), retryCount, maxRetryLimit);
            return true; // Rerun test
        }
        return false; // Do not rerun
    }

    private int getMaxRetryLimit() {
        String limitStr = ConfigReader.get("max.retry.count");
        if (limitStr != null) {
            try {
                return Integer.parseInt(limitStr);
            } catch (NumberFormatException e) {
                log.error("Failed to parse max.retry.count: [{}]. Defaulting to 1.", limitStr, e);
            }
        }
        return 1; // Default retry count
    }
}
