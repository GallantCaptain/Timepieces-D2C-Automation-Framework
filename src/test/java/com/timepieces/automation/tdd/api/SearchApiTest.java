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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
 * Timepieces Search API Keyword Coverage Suite.
 * <p>
 * A single, flattened, de-duplicated sweep of curated search keywords. All
 * keyword sources — the product matrix (first keyword per category per product),
 * deep 2nd keywords, generic terms, collection names, brand/misspellings,
 * priority terms and edge cases — are merged, de-duplicated (case-insensitive,
 * trimmed) and each unique keyword is searched exactly once. Misspellings and
 * variations are kept: they are distinct keywords.
 * <p>
 * This is a <b>keyword-coverage report</b>: each test records the exact products
 * the search API returned for its keyword, tagged with the source category. It
 * deliberately does <b>not</b> judge relevance (whether the "right" product came
 * back) — that is subjective and tuned by the developers. The only assertion is
 * API health (HTTP 200).
 *
 * @version 2.0
 * @author Avinash Rath
 * @since 2026-07-03
 */

@Log4j2
@Test(groups = { "regression" })
public class SearchApiTest {

    private List<Map<String, Object>> testMatrix;
    private final List<Map<String, Object>> testResults = new ArrayList<>();
    private int totalTests = 0;
    private int expectedKeywordCount = 0;

    private String baseUrl;
    private String userAgent;
    private String cfBypassToken;

    // Curated keyword sets (category-tagged). Merged with the JSON product matrix
    // in keywordData(), then de-duplicated so each keyword runs once.
    private static final String[] GENERIC = {
            "watches", "watch", "wristwatch", "mens watch", "men's watch", "watch for men", "luxury watch",
            "luxury watch India", "premium watch", "automatic watch", "quartz watch", "limited edition",
            "sapphire crystal", "stainless steel watch", "Indian watch brand", "chronograph", "moonphase",
            "skeleton dial", "dress watch", "leather strap" };

    private static final String[] COLLECTION = {
            "Auriqua", "Monarch", "Arvion", "Manifesta", "Astonia", "Astonia Sports", "watch strap", "watch clasp" };

    private static final String[] BRAND = {
            "Timepieces", "Timepieces watch", "timepieces", "Rotoros", "Rotaris", "Timepiecese", "316L steel", "numbered watch",
            "watch with warranty", "watch with serial number", "rose gold", "rubber strap" };

    private static final String[] PRIORITY = {
            "watch", "watches", "timepiece", "timepieces", "loom", "dial", "strap", "clasp", "watch case" };

    // Non-empty edge inputs (the empty query is skipped — it is not a keyword).
    private static final String[] EDGE = {
            "xyzzyfoobarbaz123", "!!!@@@###", "a", "watch watch watch" };

    // Select products whose 2nd (deep) keyword is also included.
    private static final String[] DEEP_HANDLES = {
            "auriqua-noir-rose", "monarch-silver-black", "arvion-navy-silver",
            "manifesta-blue-aventurine", "astonia-sports-phantom-black" };

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

        // 4. Bypass gatekeeper password wall
        String password = ConfigReader.get("staging.password");
        if (password == null || password.isEmpty()) {
            password = System.getenv("STAGING_PASSWORD");
        }

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
                log.warn("Password verification request failed. Proceeding anyway (in case gate is disabled).", e);
            }
        }
    }

    // ==================================================================================
    // Keyword collection (single de-duplicated sweep)
    // ==================================================================================

    @DataProvider(name = "keywordData", parallel = true)
    public Object[][] keywordData() {
        List<Object[]> rows = new ArrayList<>();
        // Case-insensitive, trimmed de-duplication; the first occurrence wins.
        Set<String> seen = new HashSet<>();

        if (testMatrix != null) {
            // Product matrix: first keyword of each category per product.
            for (Map<String, Object> product : testMatrix) {
                addFirst(rows, seen, product, "names_keywords", "NAMES");
                addFirst(rows, seen, product, "attributes_keywords", "ATTRIBUTES");
                addFirst(rows, seen, product, "misspellings_keywords", "MISSPELLINGS");
                addFirst(rows, seen, product, "related_keywords", "RELATED");
            }
            // Deep: 2nd keyword for a few select products.
            for (String handle : DEEP_HANDLES) {
                for (Map<String, Object> product : testMatrix) {
                    if (handle.equals(product.get("handle"))) {
                        addAt(rows, seen, product, "names_keywords", 1, "DEEP");
                        addAt(rows, seen, product, "attributes_keywords", 1, "DEEP");
                        addAt(rows, seen, product, "misspellings_keywords", 1, "DEEP");
                        break;
                    }
                }
            }
        }

        addAll(rows, seen, GENERIC, "GENERIC");
        addAll(rows, seen, COLLECTION, "COLLECTION");
        addAll(rows, seen, BRAND, "BRAND");
        addAll(rows, seen, PRIORITY, "PRIORITY");
        addAll(rows, seen, EDGE, "EDGE_CASE");

        expectedKeywordCount = rows.size();
        log.info("Prepared {} unique keywords for the search coverage sweep.", expectedKeywordCount);
        return rows.toArray(new Object[0][]);
    }

    @SuppressWarnings("unchecked")
    private void addFirst(List<Object[]> rows, Set<String> seen, Map<String, Object> product, String key,
            String category) {
        List<String> kws = (List<String>) product.get(key);
        if (kws != null && !kws.isEmpty()) {
            addKeyword(rows, seen, kws.get(0), category);
        }
    }

    @SuppressWarnings("unchecked")
    private void addAt(List<Object[]> rows, Set<String> seen, Map<String, Object> product, String key, int index,
            String category) {
        List<String> kws = (List<String>) product.get(key);
        if (kws != null && kws.size() > index) {
            addKeyword(rows, seen, kws.get(index), category);
        }
    }

    private void addAll(List<Object[]> rows, Set<String> seen, String[] kws, String category) {
        for (String kw : kws) {
            addKeyword(rows, seen, kw, category);
        }
    }

    private void addKeyword(List<Object[]> rows, Set<String> seen, String keyword, String category) {
        if (keyword == null) {
            return;
        }
        String trimmed = keyword.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        if (seen.add(trimmed.toLowerCase())) {
            rows.add(new Object[] { trimmed, category });
        }
    }

    // ==================================================================================
    // Shared Search Helper
    // ==================================================================================

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

    /** Tiny throttle between API calls to be gentle on the server. */
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
    }

    private synchronized void recordResult(String category, String keyword, String returnedSummary, String error) {
        totalTests++;
        Map<String, Object> result = new HashMap<>();
        result.put("group", category);
        result.put("keyword", keyword);
        result.put("returned", returnedSummary != null ? returnedSummary : "—");
        result.put("error", error != null ? error : "");
        testResults.add(result);
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
     * the API sends back. Entries that refer to the same product are collapsed via
     * {@link #normalizeIdentifier(String)}. Returns an empty list if the path is
     * absent.
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

    // ==================================================================================
    // The single keyword-coverage test
    // ==================================================================================

    @Test(dataProvider = "keywordData", groups = "keywordCoverage", description = "Searches a unique keyword once and records the API results (relevance is left to the devs)")
    public void testSearchKeyword(String keyword, String category) {
        SearchResult result = performSearch(keyword);
        throttle();

        recordResult(category, keyword, formatSearchResult(result), result.error);

        log.info("[Search] [{}] '{}' -> {}", category, keyword, formatSearchResult(result));

        // Objective health check only — relevance is intentionally not asserted.
        assertThat(result.statusCode)
                .as("Search API for '%s' should respond HTTP 200 (result: %s)", keyword,
                        result.error != null ? result.error : "ok")
                .isEqualTo(200);
    }

    // ==================================================================================
    // Report Generation (runs after the sweep)
    // ==================================================================================

    @Test(dependsOnMethods = "testSearchKeyword", alwaysRun = true, groups = "report", description = "Generates the search keyword coverage report after the sweep completes")
    public void generateSearchReport() {
        generateReport();

        log.info("========================================");
        log.info("Search Keyword Coverage Complete");
        log.info("Unique keywords tested: {}", totalTests);
        log.info("========================================");

        assertThat(totalTests)
                .as("All %d unique keywords should have executed.", expectedKeywordCount)
                .isEqualTo(expectedKeywordCount);
    }

    /**
     * Escapes pipe/newline characters so a value cannot break the markdown table
     * layout.
     */
    private String sanitizeCell(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString()
                .replace("|", "\\|")
                .replace("\r", " ")
                .replace("\n", " ");
    }

    private Map<String, Integer> categoryCounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (Map<String, Object> r : testResults) {
            String category = (String) r.get("group");
            counts.merge(category == null ? "UNKNOWN" : category, 1, Integer::sum);
        }
        return counts;
    }

    private static final String[] CATEGORY_ORDER = {
            "NAMES", "ATTRIBUTES", "MISSPELLINGS", "RELATED", "DEEP",
            "GENERIC", "COLLECTION", "BRAND", "PRIORITY", "EDGE_CASE" };

    private void generateReport() {
        log.info("Generating search keyword coverage report file...");
        File reportFile = new File("target/search_keyword_api_report.md");
        try {
            reportFile.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(reportFile, StandardCharsets.UTF_8)) {
                pw.println("# Timepieces Search Keyword API Coverage");
                pw.println();
                pw.println("Each unique search keyword and the exact products the search API returned. "
                        + "Search relevance (whether the \"right\" products came back) is intentionally not judged "
                        + "here — that is tuned by the developers.");
                pw.println();
                pw.println("## Summary");
                pw.println();
                pw.println("| Metric | Value |");
                pw.println("| --- | --- |");
                pw.printf("| **Target Environment** | %s |%n", baseUrl);
                pw.printf("| **Unique Keywords Tested** | %d |%n", totalTests);
                pw.println();

                pw.println("## Keywords per Category");
                pw.println();
                pw.println("| Category | Keywords |");
                pw.println("| --- | --- |");
                Map<String, Integer> counts = categoryCounts();
                for (String category : CATEGORY_ORDER) {
                    pw.printf("| %s | %d |%n", category, counts.getOrDefault(category, 0));
                }
                pw.println();

                pw.println("## Keyword Results");
                pw.println();
                pw.println("\"Actual Results Returned\" is the exact, untruncated API response (timepieces vs. "
                        + "accessories) for that keyword.");
                pw.println();
                pw.println("| Category | Keyword | Actual Results Returned | Error |");
                pw.println("| --- | --- | --- | --- |");
                for (Map<String, Object> r : testResults) {
                    pw.printf("| %s | `%s` | %s | %s |%n",
                            r.get("group"),
                            r.get("keyword"),
                            sanitizeCell(r.get("returned")),
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
            Allure.addAttachment("Search Keyword API Report", "text/markdown", markdownContent);
            log.info("Successfully attached Search Keyword API Report to Allure.");
        } catch (Exception e) {
            log.error("Failed to attach the Markdown report to Allure.", e);
        }

        // Also emit a downloadable .xlsx twin attached to Allure.
        generateExcelReport();
    }

    /**
     * Builds the Excel (.xlsx) twin of the markdown report — Summary + category
     * counts and a Results sheet — and attaches it to Allure.
     */
    private void generateExcelReport() {
        LinkedHashMap<String, String> summary = new LinkedHashMap<>();
        summary.put("Target Environment", baseUrl);
        summary.put("Unique Keywords Tested", String.valueOf(totalTests));

        Map<String, Integer> counts = categoryCounts();
        List<Object[]> breakdown = new ArrayList<>();
        for (String category : CATEGORY_ORDER) {
            breakdown.add(new Object[] { category, counts.getOrDefault(category, 0) });
        }

        ExcelReportUtil.writeAndAttach(
                "target/search_keyword_api_report.xlsx",
                "Search Keyword API Report (Excel)",
                "Category",
                summary,
                new String[] { "Category", "Keywords" },
                breakdown,
                testResults);
    }
}
