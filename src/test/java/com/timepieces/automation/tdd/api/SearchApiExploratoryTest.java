package com.timepieces.automation.tdd.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.qameta.allure.Allure;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.timepieces.automation.utils.ConfigReader;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;

/**
 * Timepieces Search API Exploratory Test Suite — 50 randomized tests.
 * <p>
 * This class reads all keywords from search_keywords.json, shuffles them
 * using a time-based seed (or configured seed), selects exactly 50
 * combinations, and executes them against the target environment
 * (defaulting to production).
 * 
 * @version 1.0
 * @author Avinash Rath
 * @since 2026-07-03
 */

@Log4j2
public class SearchApiExploratoryTest {

    static {
        // Set env system property to production BEFORE ConfigReader loads, if not
        // already set.
        if (System.getProperty("env") == null) {
            System.setProperty("env", "production");
        }
    }

    private List<Map<String, Object>> testMatrix;
    private final List<KeywordTestInstance> selectedInstances = new ArrayList<>();
    private final List<Map<String, Object>> testResults = new ArrayList<>();
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;

    private String baseUrl;
    private String userAgent;
    private String cfBypassToken;
    private long randomSeed;

    public static class KeywordTestInstance {
        public final String keyword;
        public final String expectedHandle;
        public final String productName;
        public final String category;

        public KeywordTestInstance(String keyword, String expectedHandle, String productName, String category) {
            this.keyword = keyword;
            this.expectedHandle = expectedHandle;
            this.productName = productName;
            this.category = category;
        }

        @Override
        public String toString() {
            return String.format("'%s' expected '%s' (%s - %s)", keyword, expectedHandle, productName, category);
        }
    }

    @BeforeClass
    public void setUp() {
        log.info("Setting up SearchApiExploratoryTest and loading matrix data...");

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

        // 2. Resolve Base URL (defaulting to production if not specified)
        baseUrl = ConfigReader.get("base.url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://www.timepieces-d2c.example";
        }
        RestAssured.baseURI = baseUrl;
        log.info("Base URI set to: {}", RestAssured.baseURI);

        // Configure RestAssured timeouts (10 seconds)
        RestAssured.config = RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", 10000)
                .setParam("http.socket.timeout", 10000));

        // Resolve Cloudflare bypass token
        cfBypassToken = System.getProperty("CF_BYPASS_TOKEN");
        if (cfBypassToken == null || cfBypassToken.isEmpty()) {
            cfBypassToken = System.getenv("CF_BYPASS_TOKEN");
        }

        // 3. Set User Agent
        userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36";

        // 4. Bypass gatekeeper password wall (if staging password exists)
        String password = ConfigReader.get("staging.password");
        if (password != null && !password.isEmpty()) {
            log.info("Bypassing staging gate password wall...");
            try {
                var reqSpec = given()
                        .header("User-Agent", userAgent)
                        .header("Content-Type", "application/json")
                        .header("Referer", baseUrl + "/");

                if (cfBypassToken != null && !cfBypassToken.isEmpty()) {
                    reqSpec.header("x-automation-bypass", cfBypassToken);
                }

                Response verifyResponse = reqSpec
                        .body(Map.of("password", password))
                        .post("/api/verify-password");

                log.info("Verify Password Status Code: {}", verifyResponse.getStatusCode());
            } catch (Exception e) {
                log.warn("Password verification request failed. Proceeding anyway.", e);
            }
        }

        // 5. Build and shuffle the exploratory pool
        buildAndShufflePool();
    }

    @SuppressWarnings("unchecked")
    private void buildAndShufflePool() {
        List<KeywordTestInstance> allInstances = new ArrayList<>();
        for (Map<String, Object> product : testMatrix) {
            String handle = (String) product.get("handle");
            String name = (String) product.get("name");

            addInstances(allInstances, (List<String>) product.get("names_keywords"), handle, name, "NAMES");
            addInstances(allInstances, (List<String>) product.get("attributes_keywords"), handle, name, "ATTRIBUTES");
            addInstances(allInstances, (List<String>) product.get("misspellings_keywords"), handle, name,
                    "MISSPELLINGS");
            addInstances(allInstances, (List<String>) product.get("related_keywords"), handle, name, "RELATED");
        }

        // Generate seed or read from system property for reproducibility
        String seedProperty = System.getProperty("exploratory.test.seed");
        if (seedProperty != null && !seedProperty.isEmpty()) {
            try {
                randomSeed = Long.parseLong(seedProperty);
                log.info("Using configured random seed: {}", randomSeed);
            } catch (NumberFormatException e) {
                randomSeed = System.currentTimeMillis();
                log.warn("Invalid seed property '{}'. Falling back to time-based seed: {}", seedProperty, randomSeed);
            }
        } else {
            randomSeed = System.currentTimeMillis();
            log.info("Using time-based random seed: {}", randomSeed);
        }

        Collections.shuffle(allInstances, new Random(randomSeed));

        // Select 50 unique keywords (mapping each to its expected handle)
        int count = 0;
        for (KeywordTestInstance instance : allInstances) {
            if (count >= 50) {
                break;
            }
            selectedInstances.add(instance);
            count++;
        }

        log.info("Selected 50 exploratory keyword combinations for execution.");
    }

    private void addInstances(List<KeywordTestInstance> list, List<String> kws, String handle, String name,
            String category) {
        if (kws != null) {
            for (String kw : kws) {
                if (kw != null && !kw.trim().isEmpty()) {
                    list.add(new KeywordTestInstance(kw.trim(), handle, name, category));
                }
            }
        }
    }

    private SearchResult performSearch(String keyword) {
        try {
            // 1. Request Search Token
            var tokenReqSpec = given()
                    .header("User-Agent", userAgent)
                    .header("Content-Type", "application/json")
                    .header("Referer", baseUrl + "/");

            if (cfBypassToken != null && !cfBypassToken.isEmpty()) {
                tokenReqSpec.header("x-automation-bypass", cfBypassToken);
            }

            Response tokenResponse = tokenReqSpec
                    .body(Map.of("identifier", keyword))
                    .post("/api/request-token");

            if (tokenResponse.getStatusCode() != 200) {
                return new SearchResult(tokenResponse.getStatusCode(), List.of(), List.of(),
                        "Token request status: " + tokenResponse.getStatusCode());
            }

            String token = tokenResponse.path("token");
            Object timestampObj = tokenResponse.path("timestamp");
            String timestamp = timestampObj != null ? timestampObj.toString() : "";

            if (token == null || token.isEmpty()) {
                return new SearchResult(200, List.of(), List.of(), "Null token returned");
            }

            // 2. Perform search GET request
            var searchReqSpec = given()
                    .header("User-Agent", userAgent)
                    .header("Referer", baseUrl + "/")
                    .header("x-request-token", token)
                    .header("x-request-timestamp", timestamp)
                    .queryParam("q", keyword);

            if (cfBypassToken != null && !cfBypassToken.isEmpty()) {
                searchReqSpec.header("x-automation-bypass", cfBypassToken);
            }

            Response searchResponse = searchReqSpec.get("/api/search");

            if (searchResponse.getStatusCode() != 200) {
                return new SearchResult(searchResponse.getStatusCode(), List.of(), List.of(),
                        "Search request status: " + searchResponse.getStatusCode());
            }

            JsonPath jp = searchResponse.jsonPath();
            List<String> timepieces = jp.getList("data.timepieces.handle");
            List<String> accessories = jp.getList("data.accessories.handle");

            if (timepieces == null)
                timepieces = List.of();
            if (accessories == null)
                accessories = List.of();

            return new SearchResult(200, timepieces, accessories, null);

        } catch (Exception e) {
            return new SearchResult(-1, List.of(), List.of(), "Exception: " + e.getMessage());
        }
    }

    private void throttle() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private static class SearchResult {
        final int statusCode;
        final List<String> timepieces;
        final List<String> accessories;
        final String error;

        SearchResult(int statusCode, List<String> timepieces, List<String> accessories, String error) {
            this.statusCode = statusCode;
            this.timepieces = timepieces;
            this.accessories = accessories;
            this.error = error;
        }

        List<String> allHandles() {
            List<String> all = new ArrayList<>();
            all.addAll(timepieces);
            all.addAll(accessories);
            return all;
        }
    }

    private synchronized void recordResult(String group, String testName, String keyword,
            String status, String error, List<String> returned) {
        totalTests++;
        if ("PASS".equals(status)) {
            passedTests++;
        } else {
            failedTests++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("group", group);
        result.put("name", testName);
        result.put("keyword", keyword);
        result.put("category", group);
        result.put("status", status);
        result.put("error", error != null ? error : "");
        result.put("returned", returned);
        testResults.add(result);
    }

    @DataProvider(name = "exploratorySearchData")
    public Object[][] exploratorySearchData() {
        Object[][] data = new Object[selectedInstances.size()][1];
        for (int i = 0; i < selectedInstances.size(); i++) {
            data[i][0] = selectedInstances.get(i);
        }
        return data;
    }

    @Test(dataProvider = "exploratorySearchData", groups = "exploratory", description = "Validates a randomly selected keyword returns its expected product handle")
    public void testExploratoryKeyword(KeywordTestInstance instance) {
        SearchResult result = performSearch(instance.keyword);
        throttle();

        boolean found = result.allHandles().contains(instance.expectedHandle);
        String status = found ? "PASS" : (result.error != null ? "API_ERROR" : "FAIL");

        recordResult(instance.category, instance.productName, instance.keyword, status,
                result.error != null ? result.error
                        : (found ? null
                                : "Expected handle '" + instance.expectedHandle + "' not in results (got: "
                                        + result.allHandles() + ")"),
                result.allHandles());

        log.info("[Exploratory] [{}] {} — '{}' {} expected handle '{}' for '{}'",
                instance.category, status, instance.keyword, found ? "found" : "MISSED",
                instance.expectedHandle, instance.productName);

        assertThat(result.statusCode)
                .as("Search API for '%s' should return HTTP 200", instance.keyword)
                .isEqualTo(200);
        assertThat(found)
                .as("Search API query '%s' should return product '%s' (%s)",
                        instance.keyword, instance.expectedHandle, instance.productName)
                .isTrue();
    }

    @Test(dependsOnMethods = "testExploratoryKeyword", alwaysRun = true, groups = "report", description = "Generates the comprehensive exploratory search analysis report")
    public void generateSearchReport() {
        generateReport();

        double passRate = totalTests > 0 ? ((double) passedTests / totalTests) * 100 : 0;
        log.info("========================================");
        log.info("Exploratory Search Test Suite Complete");
        log.info("Random Seed: {}", randomSeed);
        log.info("Total: {}, Passed: {}, Failed: {}, Pass Rate: {}%",
                totalTests, passedTests, failedTests, String.format("%.2f", passRate));
        log.info("========================================");

        assertThat(totalTests).as("Exactly 50 exploratory search tests should have executed.").isEqualTo(50);
    }

    private String formatReturnedHandles(Object returnedObj) {
        if (!(returnedObj instanceof List<?> list) || list.isEmpty()) {
            return "—";
        }
        @SuppressWarnings("unchecked")
        String joined = String.join(", ", ((List<String>) list));
        if (joined.length() > 300) {
            joined = joined.substring(0, 297) + "...";
        }
        return sanitizeCell(joined);
    }

    private String sanitizeCell(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString()
                .replace("|", "\\|")
                .replace("\r", " ")
                .replace("\n", " ");
    }

    private void generateReport() {
        log.info("Generating detailed exploratory search analysis report file...");
        File reportFile = new File("target/exploratory_search_api_report.md");
        try {
            reportFile.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(reportFile, StandardCharsets.UTF_8)) {
                pw.println("# Timepieces Exploratory Search Bar Keyword API Test Analysis");
                pw.println();
                pw.println(
                        "This report summarizes the verification of randomly sampled search keywords against the search API.");
                pw.println();
                pw.println("## Executive Summary");
                pw.println();
                pw.println("| Metric | Value |");
                pw.println("| --- | --- |");
                pw.printf("| **Target Environment** | %s |%n", baseUrl);
                pw.printf("| **Random Seed Used** | %d |%n", randomSeed);
                pw.printf("| **Total Queries Tested** | %d |%n", totalTests);
                pw.printf("| **Passed Matches** | %d |%n", passedTests);
                pw.printf("| **Failed Matches** | %d |%n", failedTests);
                double passRate = totalTests > 0 ? ((double) passedTests / totalTests) * 100 : 0;
                pw.printf("| **Pass Rate** | %.2f%% |%n", passRate);
                pw.println();

                // Group-wise breakdown
                pw.println("## Category breakdown");
                pw.println();
                pw.println("| Category | Tested | Passed | Failed | Pass Rate |");
                pw.println("| --- | --- | --- | --- | --- |");

                Map<String, int[]> groupStats = new HashMap<>();
                for (Map<String, Object> r : testResults) {
                    String group = (String) r.get("group");
                    if (group == null)
                        group = "UNKNOWN";
                    String status = (String) r.get("status");
                    groupStats.putIfAbsent(group, new int[] { 0, 0, 0 });
                    groupStats.get(group)[0]++; // Total
                    if ("PASS".equals(status)) {
                        groupStats.get(group)[1]++; // Passed
                    } else {
                        groupStats.get(group)[2]++; // Failed
                    }
                }

                String[] groupOrder = { "NAMES", "ATTRIBUTES", "MISSPELLINGS", "RELATED" };
                for (String group : groupOrder) {
                    int[] stats = groupStats.getOrDefault(group, new int[] { 0, 0, 0 });
                    double rate = stats[0] > 0 ? ((double) stats[1] / stats[0]) * 100 : 0;
                    pw.printf("| %s | %d | %d | %d | %.2f%% |%n", group, stats[0], stats[1], stats[2], rate);
                }
                pw.println();

                // Failed tests detail
                pw.println("## List of Failed Search Queries");
                pw.println();
                pw.println("| Category | Test Name (Product) | Keyword | Status | Error |");
                pw.println("| --- | --- | --- | --- | --- |");

                for (Map<String, Object> r : testResults) {
                    if (!"PASS".equals(r.get("status"))) {
                        pw.printf("| %s | %s | `%s` | %s | %s |%n",
                                r.get("group"),
                                r.get("name"),
                                r.get("keyword"),
                                r.get("status"),
                                sanitizeCell(r.get("error")));
                    }
                }
                pw.println();

                // Full results detail
                pw.println("## Full Results");
                pw.println();
                pw.println("| Category | Test Name (Product) | Keyword | Status | Returned Handles | Error |");
                pw.println("| --- | --- | --- | --- | --- | --- |");

                for (Map<String, Object> r : testResults) {
                    pw.printf("| %s | %s | `%s` | %s | %s | %s |%n",
                            r.get("group"),
                            r.get("name"),
                            r.get("keyword"),
                            r.get("status"),
                            formatReturnedHandles(r.get("returned")),
                            sanitizeCell(r.get("error")));
                }
            }
            log.info("Search analysis report successfully written to: {}", reportFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to write search analysis report.", e);
        }

        try {
            String markdownContent = new String(Files.readAllBytes(Paths.get(reportFile.getAbsolutePath())),
                    StandardCharsets.UTF_8);
            Allure.addAttachment("Exploratory Search Keyword API Report", "text/markdown", markdownContent);
            log.info("Successfully attached Exploratory Search Keyword API Report to Allure.");
        } catch (Exception e) {
            log.error("Failed to attach the Markdown report to Allure.", e);
        }
    }
}
