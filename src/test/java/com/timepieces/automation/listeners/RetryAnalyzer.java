package com.timepieces.automation.listeners;

import com.timepieces.automation.exceptions.OtpRateLimitException;
import com.timepieces.automation.utils.ConfigReader;
import lombok.extern.log4j.Log4j2;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG IRetryAnalyzer implementation that allows failed tests to be retried.
 * Dynamically resolves retry limits via ConfigReader or defaults to 1 retry.
 * 
 * @verison 1.0
 * @author Avinash Rath
 * @since 2026-07-03
 * @version 1.0
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
        /*
         * Never retry an OTP rate-limit failure: the backoff is keyed on the phone
         * number and ESCALATES with each request (≈9s → up to 180s). Retrying
         * re-requests the OTP for the same number and guarantees a slower failure.
         * Surface it as a real failure instead.
         */

        if (isOtpRateLimit(result.getThrowable())) {
            log.warn("=== NOT RETRYING [{}]: failure is an OTP rate-limit; retrying would escalate the backoff. ===",
                    result.getName());
            return false;
        }
        int maxRetryLimit = getMaxRetryLimit();
        if (retryCount < maxRetryLimit) {
            retryCount++;
            log.warn("=== RETRYING TEST SCENARIO: [{}] | Attempt: [{}/{}] ===",
                    result.getName(), retryCount, maxRetryLimit);
            return true; // Rerun test
        }
        return false; // Do not rerun
    }

    /** True if this throwable (or any cause in its chain) is an OTP rate-limit. */
    private boolean isOtpRateLimit(Throwable t) {
        for (Throwable cause = t; cause != null; cause = cause.getCause()) {
            if (cause instanceof OtpRateLimitException) {
                return true;
            }
        }
        return false;
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
