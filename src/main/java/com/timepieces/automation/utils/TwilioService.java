package com.timepieces.automation.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;

/**
 * TwilioService provides helper utilities to connect to the Twilio REST API,
 * fetch incoming SMS/WhatsApp messages, extract OTP verification codes,
 * and poll with retries during automation executions.
 */
@Log4j2
public class TwilioService {

    private final String accountSid;
    private final String authToken;
    private final String twilioPhoneNumber;
    private final HttpClient httpClient;

    /**
     * Initializes the service loading keys from the framework ConfigReader.
     */
    public TwilioService() {
        this.accountSid = ConfigReader.get("twilio.account.sid");
        this.authToken = ConfigReader.get("twilio.auth.token");
        this.twilioPhoneNumber = ConfigReader.get("twilio.phone.number");
        this.httpClient = HttpClient.newBuilder().build();
        
        log.info("TwilioService initialized. Account SID: [{}], Target Phone Number: [{}]", 
                accountSid, twilioPhoneNumber);
    }

    /**
     * Custom constructor allowing dynamic parameter injection (useful for tests or runtime overrides).
     */
    public TwilioService(String accountSid, String authToken, String twilioPhoneNumber) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.twilioPhoneNumber = twilioPhoneNumber;
        this.httpClient = HttpClient.newBuilder().build();
    }

    /**
     * Calls Twilio REST API to fetch the most recent message body received by the configured number.
     * 
     * @return the raw message body of the latest message, or null if no messages exist or request fails.
     */
    public String getLatestMessageBody() {
        if (accountSid == null || authToken == null || accountSid.isBlank() || authToken.isBlank() ||
            accountSid.contains("${") || authToken.contains("${")) {
            log.warn("Twilio credentials are unresolved or missing. Skipping API request.");
            return null;
        }

        String toNum = twilioPhoneNumber;
        if (toNum == null || toNum.isBlank() || toNum.contains("${")) {
            log.warn("Target Twilio phone number is unresolved or missing. Skipping API request.");
            return null;
        }

        // Standardize phone number formatting (Twilio API likes url-encoded + prefix)
        String encodedToNum = toNum.replace("+", "%2B");
        String uriStr = String.format("https://api.twilio.com/2010-04-01/Accounts/%s/Messages.json?To=%s&PageSize=1", 
                accountSid, encodedToNum);

        log.info("Requesting latest message from Twilio endpoint: {}", uriStr);

        try {
            // Build Basic Authentication Header
            String auth = accountSid + ":" + authToken;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uriStr))
                    .header("Authorization", "Basic " + encodedAuth)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Failed to query Twilio API. Status code: {}. Response body: {}", 
                        response.statusCode(), response.body());
                return null;
            }

            log.debug("Twilio response received successfully: {}", response.body());
            JsonPath jsonPath = JsonPath.from(response.body());
            
            int messageCount = jsonPath.getList("messages").size();
            if (messageCount == 0) {
                log.warn("No messages found for phone number: {}", toNum);
                return null;
            }

            String latestBody = jsonPath.getString("messages[0].body");
            log.info("Successfully fetched latest message body: [{}]", latestBody);
            return latestBody;

        } catch (Exception e) {
            log.error("Exception occurred while communicating with Twilio Messages API: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extracts a 6-digit OTP code from a message string using regex.
     * 
     * @param messageBody the body text of the message
     * @return the extracted 6-digit code, or null if none found
     */
    public String extractOtpCode(String messageBody) {
        if (messageBody == null || messageBody.isBlank()) {
            return null;
        }
        Pattern pattern = Pattern.compile("\\b\\d{6}\\b");
        Matcher matcher = pattern.matcher(messageBody);
        if (matcher.find()) {
            String code = matcher.group();
            log.info("Extracted 6-digit OTP code: [{}]", code);
            return code;
        }
        log.warn("No 6-digit OTP code found in message body: [{}]", messageBody);
        return null;
    }

    /**
     * Polling method that waits for a new OTP to arrive within a timeout period.
     * 
     * @param timeoutSeconds maximum wait time in seconds
     * @param pollIntervalSeconds interval between polls in seconds
     * @return the extracted OTP code, or null if it timed out
     */
    public String getMostRecentOtpWithRetry(int timeoutSeconds, int pollIntervalSeconds) {
        log.info("Awaiting new OTP code from Twilio (Timeout: {}s, Poll Interval: {}s)...", 
                timeoutSeconds, pollIntervalSeconds);
        
        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);
        String lastSeenMessage = getLatestMessageBody();
        log.info("Initial snapshot of latest message: [{}]", lastSeenMessage);

        while (System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(pollIntervalSeconds * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Twilio polling interrupted.");
                return null;
            }

            String currentMessage = getLatestMessageBody();
            if (currentMessage != null && !currentMessage.equals(lastSeenMessage)) {
                log.info("New message detected: [{}]", currentMessage);
                String otp = extractOtpCode(currentMessage);
                if (otp != null) {
                    return otp;
                }
            } else {
                log.debug("No new message received yet...");
            }
        }

        log.error("Timed out waiting for a new OTP message to arrive via Twilio.");
        return null;
    }
}
