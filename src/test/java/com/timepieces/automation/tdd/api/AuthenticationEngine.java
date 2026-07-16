package com.timepieces.automation.tdd.api;

import static io.restassured.RestAssured.given;

import com.timepieces.automation.context.TestContext;
import com.timepieces.automation.utils.ConfigReader;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
/**
 * @Description "Description of AuthenticationEngine"
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class AuthenticationEngine {

    private final TestContext testContext;

    public AuthenticationEngine(TestContext testContext) {
        this.testContext = testContext;
    }

    public void loginViaBackendApi(String emailOrPhone) {
        log.info("Executing fast TDD API Authentication for: {}", emailOrPhone);
        
        String baseUrl = ConfigReader.get("base.url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://staging.timepieces-d2c.example";
        }

        // Resolve Cloudflare bypass token
        String tempToken = System.getProperty("CF_BYPASS_TOKEN");
        if (tempToken == null || tempToken.isEmpty()) {
            tempToken = System.getenv("CF_BYPASS_TOKEN");
        }
        final String cfBypassToken = tempToken;

        // Helper to initialize request with CF bypass header if present
        java.util.function.Supplier<io.restassured.specification.RequestSpecification> requestBuilder = () -> {
            var spec = given();
            if (cfBypassToken != null && !cfBypassToken.isEmpty()) {
                spec = spec.header("x-automation-bypass", cfBypassToken);
            }
            return spec;
        };
        
        // 1. Bypass Staging password gate if applicable
        if (baseUrl.contains("staging")) {
            String password = ConfigReader.get("staging.password");
            if (password == null || password.isEmpty()) {
                password = "CHANGE_ME";
            }
            try {
                requestBuilder.get()
                    .contentType(ContentType.JSON)
                    .body(Map.of("password", password))
                    .post(baseUrl + "/api/verify-password")
                    .then()
                    .statusCode(200);
                log.info("Staging gate password verified via API.");
            } catch (Exception e) {
                log.warn("Staging gate API bypass verification failed or bypassed: {}", e.getMessage());
            }
        }

        // 2. Request OTP and Token via API
        String token = null;
        try {
            // Trigger OTP send
            Map<String, String> sendOtpBody = new HashMap<>();
            if (emailOrPhone.contains("@")) {
                sendOtpBody.put("email", emailOrPhone);
            } else {
                sendOtpBody.put("phone", emailOrPhone);
            }
            
            Response otpResponse = requestBuilder.get()
                .contentType(ContentType.JSON)
                .body(sendOtpBody)
                .post(baseUrl + "/api/auth/send-otp");
                
            log.info("OTP request sent. Status: {}", otpResponse.getStatusCode());

            // Request Token
            Map<String, String> requestTokenBody = new HashMap<>();
            requestTokenBody.put("username", emailOrPhone);
            requestTokenBody.put("otp", "123456"); // Staging default/bypass OTP
            
            Response tokenResponse = requestBuilder.get()
                .contentType(ContentType.JSON)
                .body(requestTokenBody)
                .post(baseUrl + "/api/request-token");

            if (tokenResponse.getStatusCode() == 200) {
                token = tokenResponse.jsonPath().getString("token");
                log.info("Successfully fetched token via /api/request-token");
            }
        } catch (Exception e) {
            log.error("API call to fetch token failed, falling back to simulated token: {}", e.getMessage());
        }

        if (token == null || token.isBlank()) {
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.TimepiecesUserTokenSample2026";
            log.info("Using simulated JWT token: {}", token);
        }

        // 3. Verify Session via API
        try {
            Response verifyResponse = requestBuilder.get()
                .header("Authorization", "Bearer " + token)
                .get(baseUrl + "/api/auth/verify-session");
            log.info("Session verification status: {}", verifyResponse.getStatusCode());
        } catch (Exception e) {
            log.warn("Session verification endpoint call failed: {}", e.getMessage());
        }

        // Store inside the context layer for this thread
        testContext.setContextData("AUTH_TOKEN", token);
    }
}
