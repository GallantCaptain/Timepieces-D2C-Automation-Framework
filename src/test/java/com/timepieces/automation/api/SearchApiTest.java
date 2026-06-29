package com.timepieces.automation.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.timepieces.automation.utils.ConfigReader;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SearchApiTest {

    private List<Map<String, Object>> testMatrix;
    private final List<Map<String, Object>> testResults = new ArrayList<>();
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;

    @BeforeClass
    public void setUp() {
        log.info("Setting up SearchApiTest and loading matrix data...");
        
        // 1. Load the search keywords test matrix JSON resource
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testdata/search_keywords.json")) {
            if (is == null) {
                throw new IllegalStateException("Could not find testdata/search_keywords.json in resources classpath.");
            }
            String jsonContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JsonPath jp = JsonPath.from(jsonContent);
            testMatrix = jp.getList("");
            log.info("Successfully loaded {} products from test matrix.", testMatrix.size());
        } catch (Exception e) {
            log.error("Failed to load search keywords test matrix.", e);
            throw new RuntimeException(e);
        }

        // 2. Resolve Base URL
        String baseUrl = ConfigReader.get("base.url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://staging.timepieces-d2c.example";
        }
        RestAssured.baseURI = baseUrl;
        log.info("Base URI set to: {}", RestAssured.baseURI);

        // 3. Bypass gatekeeper password wall
        String password = ConfigReader.get("staging.password");
        if (password == null || password.isEmpty()) {
            password = "CHANGE_ME";
        }
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36";
        
        log.info("Bypassing staging gate password wall...");
        try {
            Response verifyResponse = given()
                .header("User-Agent", userAgent)
                .header("Content-Type", "application/json")
                .header("Referer", baseUrl + "/")
                .body(Map.of("password", password))
                .post("/api/verify-password");
                
            log.info("Verify Password Status Code: {}", verifyResponse.getStatusCode());
            log.info("Verify Password Response: {}", verifyResponse.getBody().asString());
        } catch (Exception e) {
            log.warn("Password verification request failed. Proceeding anyway (in case gate is disabled).", e);
        }
    }

    @Test
    public void testSearchKeywordsMatrix() {
        String baseUrl = RestAssured.baseURI;
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36";

        log.info("Executing API searches for all products and keywords...");
        for (Map<String, Object> product : testMatrix) {
            String handle = (String) product.get("handle");
            String name = (String) product.get("name");

            Map<String, List<String>> categories = Map.of(
                "NAMES", (List<String>) product.get("names_keywords"),
                "ATTRIBUTES", (List<String>) product.get("attributes_keywords"),
                "MISSPELLINGS", (List<String>) product.get("misspellings_keywords"),
                "RELATED", (List<String>) product.get("related_keywords")
            );

            for (Map.Entry<String, List<String>> entry : categories.entrySet()) {
                String catName = entry.getKey();
                List<String> kws = entry.getValue();

                if (kws == null || kws.isEmpty()) {
                    continue;
                }

                // Test the first keyword in each category for speed and coverage
                String kw = kws.get(0);
                totalTests++;

                try {
                    // 1. Request Search Token
                    Response tokenResponse = given()
                        .header("User-Agent", userAgent)
                        .header("Content-Type", "application/json")
                        .header("Referer", baseUrl + "/")
                        .body(Map.of("identifier", kw))
                        .post("/api/request-token");

                    if (tokenResponse.getStatusCode() != 200) {
                        recordFailure(handle, name, kw, catName, "API_ERROR", "Token request status: " + tokenResponse.getStatusCode(), List.of());
                        continue;
                    }

                    String token = tokenResponse.path("token");
                    Object timestampObj = tokenResponse.path("timestamp");
                    String timestamp = timestampObj != null ? timestampObj.toString() : "";

                    if (token == null || token.isEmpty()) {
                        recordFailure(handle, name, kw, catName, "API_ERROR", "Null token returned", List.of());
                        continue;
                    }

                    // 2. Perform search GET request
                    Response searchResponse = given()
                        .header("User-Agent", userAgent)
                        .header("Referer", baseUrl + "/")
                        .header("x-request-token", token)
                        .header("x-request-timestamp", timestamp)
                        .queryParam("q", kw)
                        .get("/api/search");

                    if (searchResponse.getStatusCode() != 200) {
                        recordFailure(handle, name, kw, catName, "API_ERROR", "Search request status: " + searchResponse.getStatusCode(), List.of());
                        continue;
                    }

                    JsonPath jp = searchResponse.jsonPath();
                    List<String> timepieces = jp.getList("data.timepieces.handle");
                    List<String> accessories = jp.getList("data.accessories.handle");

                    List<String> allReturned = new ArrayList<>();
                    if (timepieces != null) allReturned.addAll(timepieces);
                    if (accessories != null) allReturned.addAll(accessories);

                    boolean found = allReturned.contains(handle);

                    if (found) {
                        passedTests++;
                        Map<String, Object> passResult = new HashMap<>();
                        passResult.put("handle", handle);
                        passResult.put("name", name);
                        passResult.put("keyword", kw);
                        passResult.put("category", catName);
                        passResult.put("status", "PASS");
                        passResult.put("returned", allReturned);
                        testResults.add(passResult);
                    } else {
                        recordFailure(handle, name, kw, catName, "FAIL", "Product not returned in search results", allReturned);
                    }

                } catch (Exception e) {
                    log.error("Exception occurred during search validation for product [{}] keyword [{}]", handle, kw, e);
                    recordFailure(handle, name, kw, catName, "API_ERROR", "Exception: " + e.getMessage(), List.of());
                }

                // Tiny throttle to be gentle on staging server
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Generate report and assert pass rate
        generateReport();
        
        double passRate = ((double) passedTests / totalTests) * 100;
        log.info("API Search Test Complete. Total: {}, Passed: {}, Failed: {}, Pass Rate: {}%", 
            totalTests, passedTests, failedTests, String.format("%.2f", passRate));

        assertThat(totalTests).as("No search terms were tested.").isGreaterThan(0);
        assertThat(passRate).as("Search index pass rate is below baseline threshold (55%)").isGreaterThanOrEqualTo(55.0);
    }

    private void recordFailure(String handle, String name, String kw, String catName, String status, String error, List<String> returned) {
        failedTests++;
        Map<String, Object> failResult = new HashMap<>();
        failResult.put("handle", handle);
        failResult.put("name", name);
        failResult.put("keyword", kw);
        failResult.put("category", catName);
        failResult.put("status", status);
        failResult.put("error", error);
        failResult.put("returned", returned);
        testResults.add(failResult);
        log.warn("  [FAIL] {} | Keyword: '{}' | Expected: '{}' | Got: {}", catName, kw, handle, returned);
    }

    private void generateReport() {
        log.info("Generating detailed search analysis report file...");
        File reportFile = new File("target/search_keyword_api_report.md");
        try {
            reportFile.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(reportFile, StandardCharsets.UTF_8)) {
                pw.println("# Timepieces Search Bar Keyword API Test Analysis");
                pw.println();
                pw.println("This report summarizes the verification of Timepieces Search Bar keywords against the staging search API.");
                pw.println();
                pw.println("## Executive Summary");
                pw.println();
                pw.println("| Metric | Value |");
                pw.println("| --- | --- |");
                pw.printf("| **Total Queries Tested** | %d |\n", totalTests);
                pw.printf("| **Passed Matches** | %d |\n", passedTests);
                pw.printf("| **Failed Matches** | %d |\n", failedTests);
                double passRate = ((double) passedTests / totalTests) * 100;
                pw.printf("| **Pass Rate** | %.2f%% |\n", passRate);
                pw.println();
                
                pw.println("## Category-wise Failure Rate");
                pw.println();
                pw.println("| Category | Tested | Failed | Pass Rate |");
                pw.println("| --- | --- | --- | --- |");
                
                Map<String, int[]> catStats = new HashMap<>();
                for (Map<String, Object> r : testResults) {
                    String cat = (String) r.get("category");
                    String status = (String) r.get("status");
                    catStats.putIfAbsent(cat, new int[]{0, 0});
                    catStats.get(cat)[0]++; // Total
                    if ("FAIL".equals(status) || "API_ERROR".equals(status)) {
                        catStats.get(cat)[1]++; // Failed
                    }
                }
                
                for (Map.Entry<String, int[]> entry : catStats.entrySet()) {
                    String cat = entry.getKey();
                    int total = entry.getValue()[0];
                    int failed = entry.getValue()[1];
                    double rate = ((double) (total - failed) / total) * 100;
                    pw.printf("| %s | %d | %d | %.2f%% |\n", cat, total, failed, rate);
                }
                pw.println();
                
                pw.println("## List of Failed Search Queries");
                pw.println();
                pw.println("The following keywords failed to return their respective products. Send this list to developers to fix search index overrides.");
                pw.println();
                pw.println("| Product Name | Expected Handle | Keyword | Category | Status | Returned Handles |");
                pw.println("| --- | --- | --- | --- | --- | --- |");
                
                for (Map<String, Object> r : testResults) {
                    if (!"PASS".equals(r.get("status"))) {
                        pw.printf("| %s | `%s` | `%s` | %s | %s | %s |\n",
                            r.get("name"),
                            r.get("handle"),
                            r.get("keyword"),
                            r.get("category"),
                            r.get("status"),
                            r.get("returned")
                        );
                    }
                }
            }
            log.info("Search analysis report successfully written to: {}", reportFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to write search analysis report.", e);
        }
    }
}
