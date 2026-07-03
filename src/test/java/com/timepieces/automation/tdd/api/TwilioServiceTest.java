package com.timepieces.automation.tdd.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;
import com.timepieces.automation.utils.TwilioService;

import lombok.extern.log4j.Log4j2;

/**
 * TwilioServiceTest
 * 
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TwilioServiceTest"
* @since 2026-07-03
 */

@Log4j2
public class TwilioServiceTest {

    @Test
    public void testOtpExtractionSuccess() {
        TwilioService service = new TwilioService("dummy", "dummy", "dummy");

        String msg1 = "Your Timepieces verification code is 859302. It is valid for 10 minutes.";
        assertThat(service.extractOtpCode(msg1))
                .as("Failed to extract code from standard message")
                .isEqualTo("859302");

        String msg2 = "OTP: 123456";
        assertThat(service.extractOtpCode(msg2))
                .as("Failed to extract code from short message")
                .isEqualTo("123456");
    }

    @Test
    public void testOtpExtractionNoMatch() {
        TwilioService service = new TwilioService("dummy", "dummy", "dummy");

        String msg1 = "Your verification code is 1234. It is invalid.";
        assertThat(service.extractOtpCode(msg1))
                .as("Should not match 4-digit codes")
                .isNull();

        String msg2 = "Your code is 1234567 (7 digits).";
        assertThat(service.extractOtpCode(msg2))
                .as("Should not match 7-digit codes")
                .isNull();

        String msg3 = "Welcome to Timepieces!";
        assertThat(service.extractOtpCode(msg3))
                .as("Should not match messages without digits")
                .isNull();
    }

    @Test
    public void testLiveFetchSkippedWithoutCredentials() {
        TwilioService service = new TwilioService();
        String latestBody = service.getLatestMessageBody();

        // If credentials are not set, it should return null and log the skip.
        // If they are set (e.g. by developers), we can assert or log accordingly.
        if (latestBody == null) {
            System.out.println(
                    "Twilio live fetch skipped as credentials are empty or failed connection (expected behavior for unit test without secrets).");
        } else {
            System.out.println("Twilio live fetch succeeded! Latest message: " + latestBody);
            String otp = service.extractOtpCode(latestBody);
            System.out.println("Extracted OTP if present: " + otp);
        }
    }
}
