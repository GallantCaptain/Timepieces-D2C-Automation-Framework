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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
 * A single, flattened, de-duplicated sweep of curated search keywords. It always
 * runs the business-critical <b>high-priority</b> keywords (collection names,
 * product/accessory handles and category terms such as "timepieces"/"straps")
 * plus realistic variants a real user would type (the handle slug and its
 * spaced form), then the product-matrix keywords and other curated sets. Each
 * unique keyword is searched exactly once; misspellings and variations are kept
 * as distinct keywords.
 * <p>
 * This is a <b>keyword-coverage report</b>: for each keyword it records the
 * <i>expected</i> products, the <i>actual</i> products the API returned, and a
 * <i>comment</i> describing what is missing or extra. It deliberately does not
 * emit a pass/fail verdict — relevance is subjective and tuned by the developers.
 * The only assertion is API health (HTTP 200).
 *
 * @version 3.0
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

    // ---- High-priority catalog (always run). Collection name -> member handles. ----
    private static final LinkedHashMap<String, String[]> COLLECTIONS = new LinkedHashMap<>();
    static {
        COLLECTIONS.put("Manifesta",
                new String[] { "manifesta-mother-of-pearl", "manifesta-blue-aventurine", "manifesta-black-onyx" });
        COLLECTIONS.put("Arvion",
                new String[] { "arvion-navy-silver", "arvion-espresso-silver", "arvion-burgundy-gold" });
        COLLECTIONS.put("Astonia",
                new String[] { "astonia-trophy-gold", "astonia-dusk-gold", "astonia-bimetal-dusk-rose",
                        "astonia-stealth-silver" });
        COLLECTIONS.put("Monarch", new String[] { "monarch-rose-gold", "monarch-silver-black" });
        COLLECTIONS.put("Auriqua", new String[] { "auriqua-noir-rose", "auriqua-racing-green", "auriqua-ocean-blue" });
        COLLECTIONS.put("Astonia Sports",
                new String[] { "astonia-sports-phantom-black", "astonia-sports-circuit-blue",
                        "astonia-sports-podium-green" });
    }

    private static final String[] STRAPS = {
            "fkm-rubber-strap-yellow", "fkm-rubber-strap-black", "fkm-rubber-strap-orange", "fkm-rubber-strap-white",
            "teju-lizard-leather-strap-black", "teju-lizard-leather-strap-blue",
            "ostrich-leather-strap-brown", "ostrich-leather-strap-black",
            "suede-leather-strap-burgundy", "suede-leather-strap-brown", "suede-leather-strap-blue",
            "hybrid-leather-strap-black", "hybrid-leather-strap-blue" };

    private static final String[] CLASPS = {
            "butterfly-clasp-gold-18mm", "butterfly-clasp-rose-gold-18mm", "butterfly-clasp-steel-18mm",
            "single-deployment-clasp-black-20mm", "single-deployment-clasp-rose-gold-20mm",
            "single-deployment-clasp-gold-20mm", "single-deployment-clasp-steel-20mm" };

    private static final String[] CASES = { "watch-case", "premium-watch-case" };

    // Broad, generic terms with no single expected product.
    private static final String[] GENERIC = {
            "watches", "watch", "wristwatch", "mens watch", "men's watch", "watch for men", "luxury watch",
            "luxury watch India", "premium watch", "automatic watch", "quartz watch", "limited edition",
            "sapphire crystal", "stainless steel watch", "Indian watch brand", "chronograph", "moonphase",
            "skeleton dial", "dress watch", "leather strap", "loom", "dial" };

    private static final String[] BRAND = {
            "Timepieces", "Timepieces watch", "timepieces", "Rotoros", "Rotaris", "Timepiecese", "316L steel", "numbered watch",
            "watch with warranty", "watch with serial number", "rose gold", "rubber strap" };

    private static final String[] EDGE = {
            "xyzzyfoobarbaz123", "!!!@@@###", "a", "watch watch watch" };

    private static final String[] DEEP_HANDLES = {
            "auriqua-noir-rose", "monarch-silver-black", "arvion-navy-silver",
            "manifesta-blue-aventurine", "astonia-sports-phantom-black" };

    private static final String[] CATEGORY_ORDER = {
            "HIGH_PRIORITY", "NAMES", "ATTRIBUTES", "MISSPELLINGS", "RELATED", "DEEP",
            "GENERIC", "BRAND", "EDGE_CASE" };

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
        // Case-insensitive, trimmed de-duplication; the first occurrence wins, so
        // high-priority keywords (added first) keep their expected mapping.
        Set<String> seen = new HashSet<>();

        addHighPriorityKeywords(rows, seen);

        if (testMatrix != null) {
            // Product matrix: first keyword of each category per product (expected = handle).
            for (Map<String, Object> product : testMatrix) {
                addProductKeyword(rows, seen, product, "names_keywords", 0, "NAMES");
                addProductKeyword(rows, seen, product, "attributes_keywords", 0, "ATTRIBUTES");
                addProductKeyword(rows, seen, product, "misspellings_keywords", 0, "MISSPELLINGS");
                addProductKeyword(rows, seen, product, "related_keywords", 0, "RELATED");
            }
            // Deep: 2nd keyword for a few select products.
            for (String handle : DEEP_HANDLES) {
                for (Map<String, Object> product : testMatrix) {
                    if (handle.equals(product.get("handle"))) {
                        addProductKeyword(rows, seen, product, "names_keywords", 1, "DEEP");
                        addProductKeyword(rows, seen, product, "attributes_keywords", 1, "DEEP");
                        addProductKeyword(rows, seen, product, "misspellings_keywords", 1, "DEEP");
                        break;
                    }
                }
            }
        }

        addBroad(rows, seen, GENERIC, "GENERIC");
        addBroad(rows, seen, BRAND, "BRAND");
        addBroad(rows, seen, EDGE, "EDGE_CASE");

        expectedKeywordCount = rows.size();
        log.info("Prepared {} unique keywords for the search coverage sweep.", expectedKeywordCount);
        return rows.toArray(new Object[0][]);
    }

    /** Business-critical keywords that must always run, plus real-user variants. */
    private void addHighPriorityKeywords(List<Object[]> rows, Set<String> seen) {
        List<String> allTimepieces = new ArrayList<>();
        for (String[] members : COLLECTIONS.values()) {
            allTimepieces.addAll(Arrays.asList(members));
        }
        List<String> allAccessories = new ArrayList<>();
        allAccessories.addAll(Arrays.asList(STRAPS));
        allAccessories.addAll(Arrays.asList(CLASPS));
        allAccessories.addAll(Arrays.asList(CASES));

        // Category / type terms (singular + plural + natural phrasing).
        addTypeTerm(rows, seen, allTimepieces, "timepieces", "timepiece");
        addTypeTerm(rows, seen, allAccessories, "accessories", "accessory");
        addTypeTerm(rows, seen, Arrays.asList(STRAPS), "straps", "strap", "watch strap");
        addTypeTerm(rows, seen, Arrays.asList(CLASPS), "clasps", "clasp", "watch clasp");
        addTypeTerm(rows, seen, Arrays.asList(CASES), "watch case", "watch-case", "cases", "case");

        // Collection names -> members (with variants).
        for (Map.Entry<String, String[]> e : COLLECTIONS.entrySet()) {
            addWithVariants(rows, seen, e.getKey(), Arrays.asList(e.getValue()));
        }

        // Specific product / accessory handles -> themselves (with variants).
        for (String h : allTimepieces) {
            addWithVariants(rows, seen, h, List.of(h));
        }
        for (String h : STRAPS) {
            addWithVariants(rows, seen, h, List.of(h));
        }
        for (String h : CLASPS) {
            addWithVariants(rows, seen, h, List.of(h));
        }
        for (String h : CASES) {
            addWithVariants(rows, seen, h, List.of(h));
        }
    }

    private void addTypeTerm(List<Object[]> rows, Set<String> seen, List<String> expected, String... forms) {
        for (String form : forms) {
            add(rows, seen, form, "HIGH_PRIORITY", expected);
        }
    }

    private void addWithVariants(List<Object[]> rows, Set<String> seen, String term, List<String> expected) {
        for (String variant : variantsOf(term)) {
            add(rows, seen, variant, "HIGH_PRIORITY", expected);
        }
    }

    /**
     * Realistic ways a user might type a handle/name: the slug itself, its
     * hyphens replaced by spaces (lower-case and Title Case). Case-only variants
     * collapse under the case-insensitive de-dup, which is intended.
     */
    private static List<String> variantsOf(String term) {
        LinkedHashSet<String> variants = new LinkedHashSet<>();
        variants.add(term.trim());
        String spaced = term.replace('-', ' ').replaceAll("\\s+", " ").trim();
        variants.add(spaced);
        variants.add(spaced.toLowerCase());
        variants.add(titleCase(spaced.toLowerCase()));
        return new ArrayList<>(variants);
    }

    private static String titleCase(String value) {
        StringBuilder sb = new StringBuilder();
        for (String part : value.split(" ")) {
            if (part.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private void addProductKeyword(List<Object[]> rows, Set<String> seen, Map<String, Object> product, String key,
            int index, String category) {
        List<String> kws = (List<String>) product.get(key);
        String handle = (String) product.get("handle");
        if (kws != null && kws.size() > index) {
            add(rows, seen, kws.get(index), category, handle == null ? List.of() : List.of(handle));
        }
    }

    private void addBroad(List<Object[]> rows, Set<String> seen, String[] kws, String category) {
        for (String kw : kws) {
            add(rows, seen, kw, category, List.of());
        }
    }

    private void add(List<Object[]> rows, Set<String> seen, String keyword, String category, List<String> expected) {
        if (keyword == null) {
            return;
        }
        String trimmed = keyword.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        if (seen.add(trimmed.toLowerCase())) {
            rows.add(new Object[] { trimmed, category, expected == null ? List.of() : expected });
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

        List<String> allHandles() {
            List<String> all = new ArrayList<>(timepieces);
            all.addAll(accessories);
            return all;
        }
    }

    private synchronized void recordResult(String category, String keyword, List<String> expected,
            String returnedSummary, String comment, String error) {
        totalTests++;
        Map<String, Object> result = new HashMap<>();
        result.put("group", category);
        result.put("keyword", keyword);
        result.put("expected", (expected == null || expected.isEmpty()) ? "(any relevant results)"
                : String.join(", ", expected));
        result.put("returned", returnedSummary != null ? returnedSummary : "—");
        result.put("comment", comment != null ? comment : "");
        result.put("error", error != null ? error : "");
        testResults.add(result);
    }

    /**
     * Compares the expected handles against what the API returned and describes,
     * in plain words, what is missing or extra. This replaces a subjective
     * pass/fail verdict: the developers read it and tune the search engine.
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
            return "All " + expected.size() + " expected returned; no extras";
        }
        StringBuilder sb = new StringBuilder();
        if (missing.isEmpty()) {
            sb.append("All expected returned. ");
        } else {
            sb.append("Missing ").append(missing.size()).append('/').append(expected.size())
                    .append(": ").append(String.join(", ", missing)).append(". ");
        }
        if (!extra.isEmpty()) {
            sb.append("Extra ").append(extra.size()).append(": ").append(String.join(", ", extra)).append('.');
        }
        return sb.toString().trim();
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
     * Collects the distinct products the API returned under {@code basePath},
     * reading BOTH the Shopify-style handle and the display title so the complete
     * result set is captured whichever format the API sends back.
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

    /** True if any entry in {@code list} refers to the same product as {@code target}. */
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

    // ==================================================================================
    // The single keyword-coverage test
    // ==================================================================================

    @Test(dataProvider = "keywordData", groups = "keywordCoverage", description = "Searches a unique keyword once and records expected vs. actual results (relevance is left to the devs)")
    public void testSearchKeyword(String keyword, String category, List<String> expected) {
        SearchResult result = performSearch(keyword);
        throttle();

        String comment = buildComment(expected, result);
        recordResult(category, keyword, expected, formatSearchResult(result), comment, result.error);

        log.info("[Search] [{}] '{}' -> {} | {}", category, keyword, formatSearchResult(result), comment);

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

    /** Escapes pipe/newline characters so a value cannot break the markdown table. */
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

    private void generateReport() {
        log.info("Generating search keyword coverage report file...");
        File reportFile = new File("target/search_keyword_api_report.md");
        try {
            reportFile.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(reportFile, StandardCharsets.UTF_8)) {
                pw.println("# Timepieces Search Keyword API Coverage");
                pw.println();
                pw.println("For each unique search keyword: the expected products, the exact products the API "
                        + "returned, and a comment on what is missing or extra. Relevance is not judged with a "
                        + "pass/fail here — that is tuned by the developers.");
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
                pw.println("\"Expected Results\" are the handles the keyword should surface; \"Actual Results "
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
     * counts and a Results sheet (Expected / Actual / Comment) — and attaches it
     * to Allure.
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
