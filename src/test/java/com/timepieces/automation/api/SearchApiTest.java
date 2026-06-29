package com.timepieces.automation.api;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.timepieces.automation.utils.ConfigReader;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;

/**
 * Timepieces Search API Test Suite — 100 tests total.
 * <p>
 * Groups:
 * <ul>
 * <li>G1 — Product Matrix (40): Each product tested with first keyword per
 * category</li>
 * <li>G2 — Generic Watch Keywords (20): High-intent terms like "watches",
 * "watch", etc.</li>
 * <li>G3 — Collection Search (8): Collection-level searches (Auriqua, Monarch,
 * etc.)</li>
 * <li>G4 — Brand & Misspellings (12): Brand name variants and global
 * misspellings</li>
 * <li>G5 — Deep Keywords (15): 2nd/3rd keywords from select products</li>
 * <li>G6 — Edge Cases (5): Empty, nonsense, special chars</li>
 * </ul>
 */
@Log4j2
public class SearchApiTest {

    private List<Map<String, Object>> testMatrix;
    private final List<Map<String, Object>> testResults = new ArrayList<>();
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;

    private String baseUrl;
    private String userAgent;

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
        baseUrl = ConfigReader.get("base.url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://staging.timepieces-d2c.example";
        }
        RestAssured.baseURI = baseUrl;
        log.info("Base URI set to: {}", RestAssured.baseURI);

        // 3. Set User Agent
        userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36";

        // 4. Bypass gatekeeper password wall
        String password = ConfigReader.get("staging.password");
        if (password == null || password.isEmpty()) {
            password = "CHANGE_ME";
        }

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

    // ==================================================================================
    // Shared Search Helper
    // ==================================================================================

    /**
     * Performs a search API call: requests a token, then searches with the keyword.
     *
     * @param keyword the search term
     * @return SearchResult containing response status, handles found, and raw
     *         response
     */
    private SearchResult performSearch(String keyword) {
        try {
            // 1. Request Search Token
            Response tokenResponse = given()
                    .header("User-Agent", userAgent)
                    .header("Content-Type", "application/json")
                    .header("Referer", baseUrl + "/")
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
            Response searchResponse = given()
                    .header("User-Agent", userAgent)
                    .header("Referer", baseUrl + "/")
                    .header("x-request-token", token)
                    .header("x-request-timestamp", timestamp)
                    .queryParam("q", keyword)
                    .get("/api/search");

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

    /** Tiny throttle between API calls to be gentle on the staging server. */
    private void throttle() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /** Simple result container for search API calls. */
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

        boolean hasResults() {
            return !timepieces.isEmpty() || !accessories.isEmpty();
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

    // ==================================================================================
    // GROUP 1: Product-Specific Matrix Tests (40 tests)
    // ==================================================================================

    @DataProvider(name = "productMatrixData")
    public Object[][] productMatrixData() {
        // Wait for testMatrix to be loaded
        if (testMatrix == null || testMatrix.isEmpty()) {
            return new Object[0][0];
        }

        Object[][] data = new Object[testMatrix.size()][1];
        for (int i = 0; i < testMatrix.size(); i++) {
            data[i][0] = testMatrix.get(i);
        }
        return data;
    }

    @Test(dataProvider = "productMatrixData", groups = "G1_ProductMatrix", description = "Validates each product is returned for its first keyword across all 4 categories")
    @SuppressWarnings("unchecked")
    public void testProductKeywordMatrix(Map<String, Object> product) {
        String handle = (String) product.get("handle");
        String name = (String) product.get("name");

        Map<String, List<String>> categories = Map.of(
                "NAMES", (List<String>) product.get("names_keywords"),
                "ATTRIBUTES", (List<String>) product.get("attributes_keywords"),
                "MISSPELLINGS", (List<String>) product.get("misspellings_keywords"),
                "RELATED", (List<String>) product.get("related_keywords"));

        int catsPassed = 0;
        int catsTested = 0;
        StringBuilder failDetails = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : categories.entrySet()) {
            String catName = entry.getKey();
            List<String> kws = entry.getValue();
            if (kws == null || kws.isEmpty())
                continue;

            String kw = kws.get(0);
            catsTested++;

            SearchResult result = performSearch(kw);
            throttle();

            if (result.error != null) {
                failDetails.append(String.format("[%s] API error for '%s': %s; ", catName, kw, result.error));
                continue;
            }

            if (result.allHandles().contains(handle)) {
                catsPassed++;
            } else {
                failDetails.append(String.format("[%s] '%s' not found for keyword '%s' (got %d results); ",
                        catName, handle, kw, result.allHandles().size()));
            }
        }

        String status = (catsPassed >= 1) ? "PASS" : "FAIL";
        recordResult("G1_ProductMatrix", name, handle, status,
                failDetails.length() > 0 ? failDetails.toString() : null, List.of());

        log.info("[G1] {} — {}/{} categories passed for '{}'", status, catsPassed, catsTested, name);
        assertThat(catsPassed)
                .as("Product '%s' (%s) should be found for at least 1 keyword category", name, handle)
                .isGreaterThanOrEqualTo(1);
    }

    // ==================================================================================
    // GROUP 2: Generic Watch Keyword Tests (20 tests)
    // ==================================================================================

    @DataProvider(name = "genericKeywordData")
    public Object[][] genericKeywordData() {
        return new Object[][] {
                { "watches", "Should return any timepieces or accessories" },
                { "watch", "Singular form should return results" },
                { "wristwatch", "Common synonym for watch" },
                { "mens watch", "Gender-specific search term" },
                { "men's watch", "Apostrophe variant of gender search" },
                { "watch for men", "Phrase-based gender search" },
                { "luxury watch", "Premium positioning keyword" },
                { "luxury watch India", "Geo-targeted luxury search" },
                { "premium watch", "Alternative premium keyword" },
                { "automatic watch", "Movement-type keyword" },
                { "quartz watch", "Movement-type keyword" },
                { "limited edition", "Exclusivity keyword" },
                { "sapphire crystal", "Material/feature keyword" },
                { "stainless steel watch", "Material keyword" },
                { "Indian watch brand", "Brand origin keyword" },
                { "chronograph", "Complication-type keyword" },
                { "moonphase", "Complication-type keyword" },
                { "skeleton dial", "Dial style keyword" },
                { "dress watch", "Occasion-based keyword" },
                { "leather strap", "Material keyword for straps and watches" }
        };
    }

    @Test(dataProvider = "genericKeywordData", groups = "G2_GenericKeywords", description = "Validates generic watch-related keywords return non-empty search results")
    public void testGenericWatchKeyword(String keyword, String rationale) {
        SearchResult result = performSearch(keyword);
        throttle();

        boolean hasResults = result.hasResults();
        String status = hasResults ? "PASS" : (result.error != null ? "API_ERROR" : "FAIL");

        recordResult("G2_GenericKeywords", keyword, keyword, status,
                result.error != null ? result.error : (hasResults ? null : "Empty results"),
                result.allHandles());

        log.info("[G2] {} — '{}' returned {} results. {}", status, keyword,
                result.allHandles().size(), rationale);

        if (result.error != null) {
            log.warn("[G2] API error for '{}': {}", keyword, result.error);
        }

        assertThat(result.statusCode)
                .as("Search for '%s' should return HTTP 200", keyword)
                .isEqualTo(200);
        assertThat(result.allHandles())
                .as("Search for '%s' should return at least one result — %s", keyword, rationale)
                .isNotEmpty();
    }

    // ==================================================================================
    // GROUP 3: Collection / Type Search Tests (8 tests)
    // ==================================================================================

    @DataProvider(name = "collectionSearchData")
    public Object[][] collectionSearchData() {
        return new Object[][] {
                { "Auriqua", new String[] { "auriqua-noir-rose", "auriqua-racing-green", "auriqua-ocean-blue" } },
                { "Monarch", new String[] { "monarch-silver-black", "monarch-rose-gold" } },
                { "Arvion", new String[] { "arvion-navy-silver", "arvion-burgundy-gold", "arvion-espresso-silver" } },
                { "Manifesta",
                        new String[] { "manifesta-blue-aventurine", "manifesta-black-onyx",
                                "manifesta-mother-of-pearl" } },
                { "Astonia",
                        new String[] { "astonia-stealth-silver", "astonia-dusk-gold", "astonia-bimetal-dusk-rose",
                                "astonia-trophy-gold" } },
                { "Astonia Sports",
                        new String[] { "astonia-sports-phantom-black", "astonia-sports-circuit-blue",
                                "astonia-sports-podium-green" } },
                { "watch strap",
                        new String[] { "suede-leather-strap-burgundy", "suede-leather-strap-brown",
                                "suede-leather-strap-blue",
                                "fkm-rubber-strap-black", "fkm-rubber-strap-yellow", "fkm-rubber-strap-orange",
                                "fkm-rubber-strap-white",
                                "teju-lizard-leather-strap-black", "teju-lizard-leather-strap-blue",
                                "ostrich-leather-strap-black", "ostrich-leather-strap-brown",
                                "hybrid-leather-strap-black", "hybrid-leather-strap-blue" } },
                { "watch clasp",
                        new String[] { "butterfly-clasp-gold-18mm", "butterfly-clasp-steel-18mm",
                                "butterfly-clasp-rose-gold-18mm",
                                "single-deployment-clasp-steel-20mm", "single-deployment-clasp-black-20mm",
                                "single-deployment-clasp-rose-gold-20mm", "single-deployment-clasp-rose-gold-22mm",
                                "single-deployment-clasp-gold-20mm" } }
        };
    }

    @Test(dataProvider = "collectionSearchData", groups = "G3_CollectionSearch", description = "Validates collection/type keywords return at least one matching product from the collection")
    public void testCollectionSearch(String keyword, String[] expectedHandles) {
        SearchResult result = performSearch(keyword);
        throttle();

        List<String> allReturned = result.allHandles();
        boolean foundAny = false;
        List<String> matchedHandles = new ArrayList<>();

        for (String expected : expectedHandles) {
            if (allReturned.contains(expected)) {
                foundAny = true;
                matchedHandles.add(expected);
            }
        }

        String status = foundAny ? "PASS" : (result.error != null ? "API_ERROR" : "FAIL");
        recordResult("G3_CollectionSearch", keyword, keyword, status,
                result.error != null ? result.error : (foundAny ? null : "No collection members found"),
                allReturned);

        log.info("[G3] {} — '{}' matched {}/{} collection handles: {}", status, keyword,
                matchedHandles.size(), expectedHandles.length, matchedHandles);

        assertThat(result.statusCode)
                .as("Search for '%s' should return HTTP 200", keyword)
                .isEqualTo(200);
        assertThat(foundAny)
                .as("Search for '%s' should return at least one product from the collection", keyword)
                .isTrue();
    }

    // ==================================================================================
    // GROUP 4: Brand & Misspelling Tests (12 tests)
    // ==================================================================================

    @DataProvider(name = "brandMisspellingData")
    public Object[][] brandMisspellingData() {
        return new Object[][] {
                { "Timepieces", "Exact brand name" },
                { "Timepieces watch", "Brand + category" },
                { "timepieces", "Lowercase brand name (case insensitivity)" },
                { "Rotoros", "Common misspelling variant 1" },
                { "Rotaris", "Common misspelling variant 2" },
                { "Timepiecese", "Common misspelling variant 3" },
                { "316L steel", "Technical material specification" },
                { "numbered watch", "Unique Timepieces feature keyword" },
                { "watch with warranty", "Trust/assurance keyword" },
                { "watch with serial number", "Authenticity keyword" },
                { "rose gold", "Popular color/finish keyword" },
                { "rubber strap", "Accessory material keyword" }
        };
    }

    @Test(dataProvider = "brandMisspellingData", groups = "G4_BrandMisspellings", description = "Validates brand name and common misspellings return results")
    public void testBrandAndMisspellingSearch(String keyword, String description) {
        SearchResult result = performSearch(keyword);
        throttle();

        boolean hasResults = result.hasResults();
        String status = hasResults ? "PASS" : (result.error != null ? "API_ERROR" : "FAIL");

        recordResult("G4_BrandMisspellings", keyword + " (" + description + ")", keyword, status,
                result.error != null ? result.error : (hasResults ? null : "Empty results"),
                result.allHandles());

        log.info("[G4] {} — '{}' ({}) returned {} results", status, keyword, description,
                result.allHandles().size());

        assertThat(result.statusCode)
                .as("Search for '%s' should return HTTP 200", keyword)
                .isEqualTo(200);
        assertThat(result.allHandles())
                .as("Search for '%s' (%s) should return at least one result", keyword, description)
                .isNotEmpty();
    }

    // ==================================================================================
    // GROUP 5: Deep Keyword Tests (15 tests)
    // Tests 2nd and 3rd keywords from select products across all collections
    // ==================================================================================

    @DataProvider(name = "deepKeywordData")
    public Object[][] deepKeywordData() {
        if (testMatrix == null || testMatrix.isEmpty()) {
            return new Object[0][0];
        }

        // Select 5 representative products (1 per main watch collection)
        String[] targetHandles = {
                "auriqua-noir-rose", // Auriqua
                "monarch-silver-black", // Monarch
                "arvion-navy-silver", // Arvion
                "manifesta-blue-aventurine", // Manifesta
                "astonia-sports-phantom-black" // Astonia Sports
        };

        // For each product, test 3 deeper keywords (2nd keyword from NAMES, ATTRIBUTES,
        // MISSPELLINGS)
        List<Object[]> rows = new ArrayList<>();

        for (String targetHandle : targetHandles) {
            for (Map<String, Object> product : testMatrix) {
                if (targetHandle.equals(product.get("handle"))) {
                    String name = (String) product.get("name");

                    // 2nd NAMES keyword (index 1)
                    addDeepKeyword(rows, product, "names_keywords", 1, targetHandle, name, "NAMES[1]");
                    // 2nd ATTRIBUTES keyword (index 1)
                    addDeepKeyword(rows, product, "attributes_keywords", 1, targetHandle, name, "ATTRIBUTES[1]");
                    // 2nd MISSPELLINGS keyword (index 1)
                    addDeepKeyword(rows, product, "misspellings_keywords", 1, targetHandle, name, "MISSPELLINGS[1]");
                    break;
                }
            }
        }

        return rows.toArray(new Object[0][0]);
    }

    @SuppressWarnings("unchecked")
    private void addDeepKeyword(List<Object[]> rows, Map<String, Object> product,
            String jsonKey, int index, String handle, String name, String label) {
        List<String> kws = (List<String>) product.get(jsonKey);
        if (kws != null && kws.size() > index) {
            rows.add(new Object[] { kws.get(index), handle, name, label });
        }
    }

    @Test(dataProvider = "deepKeywordData", groups = "G5_DeepKeywords", description = "Validates 2nd/3rd keywords from select products return the expected product")
    public void testDeepKeyword(String keyword, String expectedHandle, String productName, String label) {
        SearchResult result = performSearch(keyword);
        throttle();

        boolean found = result.allHandles().contains(expectedHandle);
        String status = found ? "PASS" : (result.error != null ? "API_ERROR" : "FAIL");

        recordResult("G5_DeepKeywords", productName + " " + label, keyword, status,
                result.error != null ? result.error
                        : (found ? null : "Product '" + expectedHandle + "' not in results"),
                result.allHandles());

        log.info("[G5] {} — '{}' {} '{}' for {} ({})", status, keyword,
                found ? "found" : "MISSED", expectedHandle, productName, label);

        assertThat(result.statusCode)
                .as("Search for '%s' should return HTTP 200", keyword)
                .isEqualTo(200);
        // Soft assertion — deep keywords may not always match, log but don't hard-fail
        if (!found) {
            log.warn("[G5] Deep keyword '{}' did not return product '{}' ({}). {} results returned.",
                    keyword, expectedHandle, productName, result.allHandles().size());
        }
        assertThat(found)
                .as("Search for deep keyword '%s' should return product '%s' (%s)", keyword, expectedHandle,
                        productName)
                .isTrue();
    }

    // ==================================================================================
    // GROUP 6: Negative & Edge Case Tests (5 tests)
    // ==================================================================================

    @DataProvider(name = "edgeCaseData")
    public Object[][] edgeCaseData() {
        return new Object[][] {
                { "", "Empty query" },
                { "xyzzyfoobarbaz123", "Complete nonsense string" },
                { "!!!@@@###", "Special characters only" },
                { "a", "Single character query" },
                { "watch watch watch", "Repeated word query" }
        };
    }

    @Test(dataProvider = "edgeCaseData", groups = "G6_EdgeCases", description = "Validates edge case queries don't crash the API (graceful error or empty results)")
    public void testEdgeCaseSearch(String keyword, String description) {
        SearchResult result = performSearch(keyword);
        throttle();

        // For edge cases, we just want no crash — status 200 is ideal, but 400 is also
        // acceptable
        boolean graceful = (result.statusCode == 200 || result.statusCode == 400);
        String status = graceful ? "PASS" : "FAIL";

        recordResult("G6_EdgeCases", description, keyword, status,
                graceful ? null
                        : "Unexpected status: " + result.statusCode + " " + (result.error != null ? result.error : ""),
                result.allHandles());

        log.info("[G6] {} — '{}' ({}) returned status {} with {} results", status, keyword,
                description, result.statusCode, result.allHandles().size());

        assertThat(result.statusCode)
                .as("Edge case '%s' (%s) should return HTTP 200 or 400, not crash", keyword, description)
                .isIn(200, 400);
    }

    // ==================================================================================
    // Report Generation (runs after all tests)
    // ==================================================================================

    @Test(dependsOnGroups = { "G1_ProductMatrix", "G2_GenericKeywords", "G3_CollectionSearch",
            "G4_BrandMisspellings", "G5_DeepKeywords",
            "G6_EdgeCases" }, alwaysRun = true, groups = "report", description = "Generates the comprehensive search analysis report after all test groups complete")
    public void generateSearchReport() {
        generateReport();

        double passRate = totalTests > 0 ? ((double) passedTests / totalTests) * 100 : 0;
        log.info("========================================");
        log.info("API Search Test Suite Complete");
        log.info("Total: {}, Passed: {}, Failed: {}, Pass Rate: {}%",
                totalTests, passedTests, failedTests, String.format("%.2f", passRate));
        log.info("========================================");

        assertThat(totalTests).as("At least 95 search tests should have executed.").isGreaterThanOrEqualTo(95);
    }

    // ==================================================================================
    // Private Helpers
    // ==================================================================================

    private void recordFailure(String handle, String name, String kw, String catName, String status, String error,
            List<String> returned) {
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
                pw.println(
                        "This report summarizes the verification of Timepieces Search Bar keywords against the staging search API.");
                pw.println();
                pw.println("## Executive Summary");
                pw.println();
                pw.println("| Metric | Value |");
                pw.println("| --- | --- |");
                pw.printf("| **Total Queries Tested** | %d |%n", totalTests);
                pw.printf("| **Passed Matches** | %d |%n", passedTests);
                pw.printf("| **Failed Matches** | %d |%n", failedTests);
                double passRate = totalTests > 0 ? ((double) passedTests / totalTests) * 100 : 0;
                pw.printf("| **Pass Rate** | %.2f%% |%n", passRate);
                pw.println();

                // Group-wise breakdown
                pw.println("## Group-wise Results");
                pw.println();
                pw.println("| Group | Tested | Passed | Failed | Pass Rate |");
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

                String[] groupOrder = { "G1_ProductMatrix", "G2_GenericKeywords", "G3_CollectionSearch",
                        "G4_BrandMisspellings", "G5_DeepKeywords", "G6_EdgeCases" };
                for (String group : groupOrder) {
                    int[] stats = groupStats.getOrDefault(group, new int[] { 0, 0, 0 });
                    double rate = stats[0] > 0 ? ((double) stats[1] / stats[0]) * 100 : 0;
                    pw.printf("| %s | %d | %d | %d | %.2f%% |%n", group, stats[0], stats[1], stats[2], rate);
                }
                pw.println();

                // Failed tests detail
                pw.println("## List of Failed Search Queries");
                pw.println();
                pw.println("The following keywords failed to return their expected results.");
                pw.println();
                pw.println("| Group | Test Name | Keyword | Status | Error |");
                pw.println("| --- | --- | --- | --- | --- |");

                for (Map<String, Object> r : testResults) {
                    if (!"PASS".equals(r.get("status"))) {
                        pw.printf("| %s | %s | `%s` | %s | %s |%n",
                                r.get("group"),
                                r.get("name"),
                                r.get("keyword"),
                                r.get("status"),
                                r.get("error"));
                    }
                }
            }
            log.info("Search analysis report successfully written to: {}", reportFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to write search analysis report.", e);
        }
        // --- TO ATTACH THE REPORT TO ALLURE ---
        try {
            // Read the generated markdown file into a String
            String markdownContent = new String(Files.readAllBytes(Paths.get(reportFile.getAbsolutePath())),
                    StandardCharsets.UTF_8);

            // Attach it to the Allure Report with the text/markdown MIME type
            Allure.addAttachment("Search Keyword API Report", "text/markdown", markdownContent);
            log.info("Successfully attached Search Keyword API Report to Allure.");
        } catch (Exception e) {
            log.error("Failed to attach the Markdown report to Allure.", e);
        }
    }
}
