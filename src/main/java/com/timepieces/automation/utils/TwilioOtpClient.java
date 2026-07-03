package com.timepieces.automation.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j2;

/**
 * Reads the most recent inbound OTP SMS for the test phone number from Twilio.
 *
 * <p>
 * This is a legitimate, single-credential client: it authenticates with ONE
 * Account SID + Auth Token (yours) and reads messages delivered to ONE Twilio
 * number you own. It is the read-back side of an end-to-end "log in with the
 * real OTP" test. (It deliberately replaces the previous TwilioValidator, which
 * brute-forced credentials by enumerating SID/token permutations — never do
 * that.)
 *
 * <p>
 * Credentials and the inbox number are supplied via environment (never
 * committed). All three are required:
 * 
 * <pre>
 *   TWILIO_ACCOUNT_SID   - your Account SID (AC...)
 *   TWILIO_AUTH_TOKEN    - your Auth Token (rotate it if it has ever been shared)
 *   TWILIO_PHONE_NUMBER  - the Twilio number (E.164, e.g. +15551234567) that
 *                          receives the OTP, i.e. the number you log in with
 * </pre>
 * 
 * Reads fall back to ConfigReader keys (twilio.account.sid / twilio.auth.token
 * /
 * twilio.phone.number) for local runs that prefer a gitignored
 * *.local.properties.
 * 
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TwilioOtpClient"
* @since 2026-07-03
 */

@Log4j2
public final class TwilioOtpClient {

    private static final String MESSAGES_API = "https://api.twilio.com/2010-04-01/Accounts/%s/Messages.json?To=%s&PageSize=20";

    // Grabs each message body from the Messages list JSON, newest first (Twilio
    // returns them date-sent descending), so the first one with a 6-digit code
    // wins.
    private static final Pattern BODY_FIELD = Pattern.compile("\"body\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
    private static final Pattern SIX_DIGIT = Pattern.compile("\\b(\\d{6})\\b");

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Polls Twilio for up to {@code maxWaitSeconds} until an inbound SMS containing
     * a 6-digit code arrives for the configured number, then returns that code.
     *
     * @param maxWaitSeconds   total time to wait for the OTP SMS to land
     * @param pollEverySeconds delay between polls
     * @return the 6-digit OTP
     * @throws IllegalStateException if Twilio is not configured
     * @throws RuntimeException      if no OTP arrives within the budget
     */
    public String fetchLatestOtp(int maxWaitSeconds, int pollEverySeconds) {
        String sid = require("TWILIO_ACCOUNT_SID", "twilio.account.sid");
        String token = require("TWILIO_AUTH_TOKEN", "twilio.auth.token");
        String number = require("TWILIO_PHONE_NUMBER", "twilio.phone.number");

        String uri = String.format(MESSAGES_API,
                java.net.URLEncoder.encode(sid, StandardCharsets.UTF_8),
                java.net.URLEncoder.encode(number, StandardCharsets.UTF_8));
        String basicAuth = "Basic " + Base64.getEncoder()
                .encodeToString((sid + ":" + token).getBytes(StandardCharsets.UTF_8));

        long deadline = System.nanoTime() + Duration.ofSeconds(maxWaitSeconds).toNanos();
        int attempt = 0;
        while (System.nanoTime() < deadline) {
            attempt++;
            log.info("Polling Twilio for OTP to {} (attempt {})...", number, attempt);
            Optional<String> otp = readOtpOnce(uri, basicAuth);
            if (otp.isPresent()) {
                log.info("OTP retrieved from Twilio inbox.");
                return otp.get();
            }
            sleep(pollEverySeconds);
        }
        throw new RuntimeException("No OTP SMS with a 6-digit code arrived for " + number
                + " within " + maxWaitSeconds + "s. Check the number is correct, owned by this "
                + "Twilio account, and that the login actually dispatched an SMS.");
    }

    /** One Twilio API call; returns the newest 6-digit code found, if any. */
    private Optional<String> readOtpOnce(String uri, String basicAuth) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Authorization", basicAuth)
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.warn("Twilio Messages API returned HTTP {} (check credentials/permissions).",
                        response.statusCode());
                return Optional.empty();
            }
            Matcher bodies = BODY_FIELD.matcher(response.body());
            while (bodies.find()) {
                Matcher code = SIX_DIGIT.matcher(bodies.group(1));
                if (code.find()) {
                    return Optional.of(code.group(1));
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Twilio poll failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /** Reads env var first, then the ConfigReader key; fails loudly if unset. */
    private String require(String envName, String configKey) {
        String value = System.getenv(envName);
        if (value == null || value.isBlank()) {
            value = ConfigReader.get(configKey);
        }
        // ConfigReader returns the literal "${TWILIO_...}" placeholder from
        // staging.properties
        // when nothing is wired — treat that as unset rather than a real value.
        if (value == null || value.isBlank() || value.startsWith("${")) {
            throw new IllegalStateException("Twilio is not configured: set the " + envName
                    + " environment variable (or " + configKey + " in a gitignored local properties file).");
        }
        return value.trim();
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(Duration.ofSeconds(seconds).toMillis());
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for the OTP SMS.", ie);
        }
    }
}
