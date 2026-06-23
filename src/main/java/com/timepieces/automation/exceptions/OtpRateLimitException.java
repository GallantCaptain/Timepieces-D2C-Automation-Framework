package com.timepieces.automation.exceptions;

/**
 * Thrown when OTP send is blocked by the ~90s "Too many attempts" cooldown
 * ({@code login-cooldown}) — the OTP verify screen never appears because the
 * number is in a cooldown window (triggered by phone-number churn, not by wrong
 * OTP guesses).
 *
 * <p>The cooldown is a fixed window (~90s), so an immediate retry just fails again.
 * {@code RetryAnalyzer} therefore treats this exception as non-retryable, and tests
 * that must proceed should instead wait it out (see {@code LoginPage.waitOutOtpCooldown}).
 * It is a distinct type (not a bare Selenium {@code TimeoutException}) so the failure
 * is reported honestly as a cooldown, not a generic "element not visible".
 */
public class OtpRateLimitException extends RuntimeException {

    /** The cooldown to wait out, in seconds; -1 if it could not be determined. */
    private final int waitSeconds;

    public OtpRateLimitException(int waitSeconds) {
        super(waitSeconds >= 0
                ? "OTP send blocked by the 'Too many attempts' cooldown (~" + waitSeconds
                        + "s). The OTP verify screen was never shown. Do not retry immediately —"
                        + " wait out the cooldown window."
                : "OTP send blocked by the 'Too many attempts' cooldown. The OTP verify screen "
                        + "was never shown. Do not retry immediately — wait out the cooldown window.");
        this.waitSeconds = waitSeconds;
    }

    public int getWaitSeconds() {
        return waitSeconds;
    }
}
