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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.timepieces.automation.utils.ConfigReader;
import com.timepieces.automation.utils.ExcelReportUtil;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;

/**
 * Timepieces Search API Exploratory Coverage Suite.
 * <p>
 * Reads every keyword from {@code search_keywords.json}, de-duplicates them so
 * each unique keyword is searched exactly once (misspellings and variations are
 * kept — they are distinct keywords), shuffles with a time-based (or configured)
 * seed, and samples up to {@link #TARGET_TEST_COUNT} keywords against the target
 * environment (defaulting to production).
 * <p>
 * This is a <b>keyword-coverage report</b>: each test records the exact products
 * the search API returned for its keyword. It deliberately does <b>not</b> judge
 * relevance (whether the "right" product came back) — that is subjective and
 * tuned by the developers. The only assertion is API health (HTTP 200).
 *
 * @version 2.0
 * @author Avinash Rath
 * @since 2026-07-03
 */

@Log4j2
public class SearchApiExploratoryTest {

    /** How many unique keywords to sample and execute. */
    private static final int TARGET_TEST_COUNT = 250;

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

    private String baseUrl;
    private String userAgent;
    private String cfBypassToken;
    private long randomSeed;

    /** One unique search keyword, the category it was sourced from, and the product it belongs to. */
    public static class KeywordTestInstance {
        public final String keyword;
        public final String category;
        public final String expectedHandle;

        public KeywordTestInstance(String keyword, String category, String expectedHandle) {
            this.keyword = keyword;
            this.category = category;
            this.expectedHandle = expectedHandle;
        }

        @Override
        public String toString() {
            return String.format("'%s' (%s -> %s)", keyword, category, expectedHandle);
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

        // 5. Build the de-duplicated pool and sample it
        buildAndShufflePool();
    }

    @SuppressWarnings("unchecked")
    private void buildAndShufflePool() {
        List<KeywordTestInstance> allInstances = new ArrayList<>();
        // Case-insensitive, trimmed de-duplication so each keyword is tested once.
        // The first occurrence wins (keeps its original casing and category).
        Set<String> seen = new HashSet<>();
        for (Map<String, Object> product : testMatrix) {
            String handle = (String) product.get("handle");
            addUnique(allInstances, seen, (List<String>) product.get("names_keywords"), "NAMES", handle);
            addUnique(allInstances, seen, (List<String>) product.get("attributes_keywords"), "ATTRIBUTES", handle);
            addUnique(allInstances, seen, (List<String>) product.get("misspellings_keywords"), "MISSPELLINGS", handle);
            addUnique(allInstances, seen, (List<String>) product.get("related_keywords"), "RELATED", handle);
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

        int limit = Math.min(TARGET_TEST_COUNT, allInstances.size());
        for (int i = 0; i < limit; i++) {
            selectedInstances.add(allInstances.get(i));
        }

        log.info("Selected {} unique exploratory keywords for execution (from a de-duplicated pool of {}).",
                selectedInstances.size(), allInstances.size());
    }

    private void addUnique(List<KeywordTestInstance> list, Set<String> seen, List<String> kws, String category,
            String expectedHandle) {
        if (kws == null) {
            return;
        }
        for (String kw : kws) {
            if (kw == null) {
                continue;
            }
            String trimmed = kw.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (seen.add(trimmed.toLowerCase())) {
                list.add(new KeywordTestInstance(trimmed, category, expectedHandle));
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
            List<String> timepieces = collectReturnedProducts(jp, "data.timepieces");
            List<String> accessories = collectReturnedProducts(jp, "data.accessories");

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
            List<String> all = new ArrayList<>(timepieces);
            all.addAll(accessories);
            return all;
        }
    }

    private synchronized void recordResult(String category, String keyword, String expected, String returnedSummary,
            String comment, String error) {
        totalTests++;
        Map<String, Object> result = new HashMap<>();
        result.put("group", category);
        result.put("keyword", keyword);
        result.put("expected", expected != null ? expected : "(any relevant results)");
        result.put("returned", returnedSummary != null ? returnedSummary : "—");
        result.put("comment", comment != null ? comment : "");
        result.put("error", error != null ? error : "");
        testResults.add(result);
    }

    /**
     * Compares the expected handle against what the API returned and describes what
     * is missing or extra — a plain-words replacement for a subjective pass/fail.
     */
    private String buildComment(List<String> expected, SearchResult result) {
        if (result.error != null) {
            return "API error — no results returned";
        }
        List<String> actual = result.allHandles();
        if (expected == null || expected.isEmpty()) {
            return actual.isEmpty() ? "No results returned" : (actual.size() + " result(s) returned");
        }
        List<String> missing = new ArrayList<>();
        for (String e : expected) {
            if (!containsNormalized(actual, e)) {
                missing.add(e);
            }
        }
        List<String> extra = new ArrayList<>();
        for (String a : actual) {
            if (!containsNormalized(expected, a)) {
                extra.add(a);
            }
        }
        if (missing.isEmpty() && extra.isEmpty()) {
            return "Expected product returned; no extras";
        }
        StringBuilder sb = new StringBuilder();
        if (missing.isEmpty()) {
            sb.append("Expected product returned. ");
        } else {
            sb.append("Missing: ").append(String.join(", ", missing)).append(". ");
        }
        if (!extra.isEmpty()) {
            sb.append("Extra ").append(extra.size()).append(": ").append(String.join(", ", extra)).append('.');
        }
        return sb.toString().trim();
    }

    private boolean containsNormalized(List<String> list, String target) {
        String t = normalizeIdentifier(target);
        if (t.isEmpty()) {
            return false;
        }
        for (String candidate : list) {
            if (normalizeIdentifier(candidate).equals(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Formats one search's actual API response in full, split by category
     * (timepieces vs. accessories) exactly as the API returns it — never
     * truncated — so this is the real payload shape a developer can act on.
     */
    private String formatSearchResult(SearchResult result) {
        if (result.error != null) {
            return "—";
        }
        String tp = result.timepieces.isEmpty() ? "none" : String.join(", ", result.timepieces);
        String ac = result.accessories.isEmpty() ? "none" : String.join(", ", result.accessories);
        return String.format("Timepieces (%d): %s — Accessories (%d): %s",
                result.timepieces.size(), tp, result.accessories.size(), ac);
    }

    /**
     * Collects the distinct products the API returned under {@code basePath}
     * (e.g. {@code data.timepieces}), reading BOTH the Shopify-style handle and
     * the display title so the complete result set is captured whichever format
     * the API sends back. Entries that refer to the same product are collapsed
     * via {@link #normalizeIdentifier(String)}. Returns an empty list if the path
     * is absent.
     */
    private List<String> collectReturnedProducts(JsonPath jp, String basePath) {
        List<String> handles = jp.getList(basePath + ".handle");
        List<String> titles = jp.getList(basePath + ".title");
        List<String> merged = new ArrayList<>();
        Set<String> seenNormalized = new HashSet<>();
        if (handles != null) {
            for (String h : handles) {
                if (h != null && !h.isBlank() && seenNormalized.add(normalizeIdentifier(h))) {
                    merged.add(h);
                }
            }
        }
        if (titles != null) {
            for (String t : titles) {
                if (t != null && !t.isBlank() && seenNormalized.add(normalizeIdentifier(t))) {
                    merged.add(t);
                }
            }
        }
        return merged;
    }

    /**
     * Canonicalizes a product identifier so a handle and its display name collapse
     * to the same value: lowercases, replaces runs of non-alphanumerics with a
     * single space, and trims.
     */
    private static String normalizeIdentifier(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase().replaceAll("[^a-z0-9]+", " ").trim();
    }

    @DataProvider(name = "exploratorySearchData", parallel = true)
    public Object[][] exploratorySearchData() {
        Object[][] data = new Object[selectedInstances.size()][1];
        for (int i = 0; i < selectedInstances.size(); i++) {
            data[i][0] = selectedInstances.get(i);
        }
        return data;
    }

    @Test(dataProvider = "exploratorySearchData", groups = "exploratory", description = "Searches a unique keyword once and records the API results (relevance is left to the devs)")
    public void testExploratoryKeyword(KeywordTestInstance instance) {
        SearchResult result = performSearch(instance.keyword);
        throttle();

        List<String> expected = instance.expectedHandle == null ? List.of() : List.of(instance.expectedHandle);
        String comment = buildComment(expected, result);
        recordResult(instance.category, instance.keyword,
                expected.isEmpty() ? "(any relevant results)" : String.join(", ", expected),
                formatSearchResult(result), comment, result.error);

        log.info("[Exploratory] [{}] '{}' -> {} | {}", instance.category, instance.keyword,
                formatSearchResult(result), comment);

        // Objective health check only — relevance is intentionally not asserted.
        assertThat(result.statusCode)
                .as("Search API for '%s' should respond HTTP 200 (result: %s)", instance.keyword,
                        result.error != null ? result.error : "ok")
                .isEqualTo(200);
    }

    @Test(dependsOnMethods = "testExploratoryKeyword", alwaysRun = true, groups = "report", description = "Generates the exploratory search keyword coverage report")
    public void generateSearchReport() {
        generateReport();

        log.info("========================================");
        log.info("Exploratory Search Coverage Complete");
        log.info("Random Seed: {}", randomSeed);
        log.info("Unique keywords tested: {}", totalTests);
        log.info("========================================");

        assertThat(totalTests)
                .as("All %d selected exploratory keywords should have executed.", selectedInstances.size())
                .isEqualTo(selectedInstances.size());
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
        log.info("Generating exploratory search keyword coverage report file...");
        File reportFile = new File("target/exploratory_search_api_report.md");
        try {
            reportFile.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(reportFile, StandardCharsets.UTF_8)) {
                pw.println("# Timepieces Exploratory Search Keyword API Coverage");
                pw.println();
                pw.println("Each unique search keyword sampled and the exact products the search API returned. "
                        + "Search relevance (whether the \"right\" products came back) is intentionally not judged "
                        + "here — that is tuned by the developers.");
                pw.println();
                pw.println("## Summary");
                pw.println();
                pw.println("| Metric | Value |");
                pw.println("| --- | --- |");
                pw.printf("| **Target Environment** | %s |%n", baseUrl);
                pw.printf("| **Random Seed Used** | %d |%n", randomSeed);
                pw.printf("| **Unique Keywords Tested** | %d |%n", totalTests);
                pw.println();

                pw.println("## Keywords per Category");
                pw.println();
                pw.println("| Category | Keywords |");
                pw.println("| --- | --- |");
                Map<String, Integer> counts = categoryCounts();
                String[] categoryOrder = { "NAMES", "ATTRIBUTES", "MISSPELLINGS", "RELATED" };
                for (String category : categoryOrder) {
                    pw.printf("| %s | %d |%n", category, counts.getOrDefault(category, 0));
                }
                pw.println();

                pw.println("## Keyword Results");
                pw.println();
                pw.println("\"Expected Results\" is the product the keyword was sourced from; \"Actual Results "
                        + "Returned\" is the exact, untruncated API response; \"Comment\" notes what is missing "
                        + "or extra.");
                pw.println();
                pw.println("| Category | Keyword | Expected Results | Actual Results Returned | Comment | Error |");
                pw.println("| --- | --- | --- | --- | --- | --- |");
                for (Map<String, Object> r : testResults) {
                    pw.printf("| %s | `%s` | %s | %s | %s | %s |%n",
                            r.get("group"),
                            r.get("keyword"),
                            sanitizeCell(r.get("expected")),
                            sanitizeCell(r.get("returned")),
                            sanitizeCell(r.get("comment")),
                            sanitizeCell(r.get("error")));
                }
            }
            log.info("Search coverage report successfully written to: {}", reportFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to write search coverage report.", e);
        }

        try {
            String markdownContent = new String(Files.readAllBytes(Paths.get(reportFile.getAbsolutePath())),
                    StandardCharsets.UTF_8);
            Allure.addAttachment("Exploratory Search Keyword API Report", "text/markdown", markdownContent);
            log.info("Successfully attached Exploratory Search Keyword API Report to Allure.");
        } catch (Exception e) {
            log.error("Failed to attach the Markdown report to Allure.", e);
        }

        // Also emit a downloadable .xlsx twin attached to Allure.
        generateExcelReport();
    }

    private Map<String, Integer> categoryCounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (Map<String, Object> r : testResults) {
            String category = (String) r.get("group");
            counts.merge(category == null ? "UNKNOWN" : category, 1, Integer::sum);
        }
        return counts;
    }

    /**
     * Builds the Excel (.xlsx) twin of the markdown report — Summary + category
     * counts and a Results sheet — and attaches it to Allure.
     */
    private void generateExcelReport() {
        LinkedHashMap<String, String> summary = new LinkedHashMap<>();
        summary.put("Target Environment", baseUrl);
        summary.put("Random Seed Used", String.valueOf(randomSeed));
        summary.put("Unique Keywords Tested", String.valueOf(totalTests));

        Map<String, Integer> counts = categoryCounts();
        String[] categoryOrder = { "NAMES", "ATTRIBUTES", "MISSPELLINGS", "RELATED" };
        List<Object[]> breakdown = new ArrayList<>();
        for (String category : categoryOrder) {
            breakdown.add(new Object[] { category, counts.getOrDefault(category, 0) });
        }

        ExcelReportUtil.writeAndAttach(
                "target/exploratory_search_api_report.xlsx",
                "Exploratory Search Keyword API Report (Excel)",
                "Category",
                summary,
                new String[] { "Category", "Keywords" },
                breakdown,
                testResults);
    }
}
