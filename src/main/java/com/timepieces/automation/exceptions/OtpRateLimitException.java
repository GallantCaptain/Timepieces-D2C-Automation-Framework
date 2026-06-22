package com.timepieces.automation.exceptions;

/**
 * Thrown when the Timepieces login throttles OTP requests for a phone number and
 * shows the "Too many attempts — Please wait Ns before trying" backoff toast
 * instead of the OTP verify screen.
 *
 * <p>The backoff is keyed on the phone number and <b>escalates with each retry</b>
 * (observed from ~9s up to 180s). Retrying the same number therefore makes things
 * worse, so {@code RetryAnalyzer} treats this exception as non-retryable. It is a
 * distinct type (not a bare Selenium {@code TimeoutException}) so the failure is
 * reported honestly as a rate-limit, not a generic "element not visible".
 */
public class OtpRateLimitException extends RuntimeException {

    /** The cooldown the UI asked the user to wait, in seconds; -1 if it could not be parsed. */
    private final int waitSeconds;

    public OtpRateLimitException(int waitSeconds) {
        super(waitSeconds >= 0
                ? "OTP request rate-limited: the login asked to wait " + waitSeconds
                        + "s before trying again. The OTP verify screen was never shown. "
                        + "Do not retry the same number — the backoff escalates up to 180s."
                : "OTP request rate-limited: the login showed the 'Too many attempts' backoff "
                        + "toast instead of the OTP verify screen. Do not retry the same number.");
        this.waitSeconds = waitSeconds;
    }

    public int getWaitSeconds() {
        return waitSeconds;
    }
}
