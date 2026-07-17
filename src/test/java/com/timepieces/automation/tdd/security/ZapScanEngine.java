package com.timepieces.automation.tdd.security;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;

/**
 * A thin client for the OWASP ZAP daemon's REST API, built on Rest-Assured.
 *
 * <h2>Why Rest-Assured instead of ZAP's official Java client?</h2>
 * ZAP ships a {@code zap-clientapi} artifact, but this framework already depends on
 * Rest-Assured and already speaks REST fluently everywhere else (see
 * {@code tdd.api.*}). ZAP's API is plain HTTP+JSON — every call below is a single GET —
 * so pulling in another dependency would buy nothing and add a version to keep alive
 * after handover. One less moving part.
 *
 * <h2>The ZAP API in 60 seconds</h2>
 * Everything is a GET. Two URL shapes matter:
 * <ul>
 *   <li>{@code /JSON/<component>/action/<name>/} - does something (start a spider, set an option)</li>
 *   <li>{@code /JSON/<component>/view/<name>/} - reads something (scan status, alerts)</li>
 * </ul>
 * Long operations are asynchronous: the {@code action} returns a {@code scanId}
 * immediately, and you poll the matching {@code view/status/} until it reports 100.
 * That poll loop is {@link #awaitScanCompletion}.
 *
 * <h2>Scanning vocabulary (worth getting straight)</h2>
 * <ul>
 *   <li><b>Spider</b> - crawls links to discover URLs. Sends only normal requests.</li>
 *   <li><b>AJAX spider</b> - drives a real browser so JS-rendered routes are discovered
 *       too. Timepieces is a JS-heavy storefront, so the plain spider alone misses a lot.</li>
 *   <li><b>Passive scan</b> - inspects traffic that already happened. Never sends a
 *       payload. This is what makes a "baseline" scan safe against production.</li>
 *   <li><b>Active scan</b> - ATTACKS. Injects SQLi/XSS/command-injection payloads into
 *       every parameter it found. Safe on staging; dangerous on a live store.</li>
 * </ul>
 *
 * @since 2026-07-17
 * @version 1.0
 * @author Avinash Rath
 */
@Log4j2
public class ZapScanEngine {

    /** ZAP reports scan progress as 0-100; 100 means finished. */
    private static final int SCAN_COMPLETE = 100;

    /** How often we ask ZAP "are you done yet?" while a scan runs. */
    private static final Duration POLL_INTERVAL = Duration.ofSeconds(5);

    private final String apiUrl;
    private final String apiKey;

    public ZapScanEngine(String apiUrl, String apiKey) {
        this.apiUrl = apiUrl.endsWith("/") ? apiUrl.substring(0, apiUrl.length() - 1) : apiUrl;
        this.apiKey = apiKey;
    }

    // ---------------------------------------------------------------------------
    // Low-level plumbing
    // ---------------------------------------------------------------------------

    /**
     * Issues a GET against the ZAP API and returns the raw response.
     *
     * <p>Note the deliberate use of {@code RestAssured.given()} with an explicit
     * {@code baseUri} rather than the global {@code RestAssured.baseURI} static: the
     * API suites set that global to the Timepieces API, and stomping on it here would
     * break any test sharing the JVM.
     */
    private Response call(String path, Map<String, String> params) {
        var request = RestAssured.given()
                .baseUri(apiUrl)
                .relaxedHTTPSValidation()
                .queryParam("apikey", apiKey);

        if (params != null) {
            params.forEach(request::queryParam);
        }
        return request.when().get(path).andReturn();
    }

    /** Convenience for calls that take no parameters beyond the API key. */
    private Response call(String path) {
        return call(path, Map.of());
    }

    // ---------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------

    /**
     * Blocks until the ZAP daemon answers its version endpoint, or the timeout expires.
     *
     * <p>ZAP is a JVM app that takes 20-40s to boot. Without this, a scan kicked off
     * immediately after {@code docker compose up} fails with a connection refused that
     * looks like a config error but is really just a race.
     *
     * @return the reported ZAP version
     * @throws IllegalStateException if ZAP never became reachable
     */
    public String awaitZapReady(Duration timeout) {
        Instant deadline = Instant.now().plus(timeout);
        Exception last = null;

        while (Instant.now().isBefore(deadline)) {
            try {
                Response response = call("/JSON/core/view/version/");
                if (response.statusCode() == 200) {
                    String version = response.jsonPath().getString("version");
                    log.info("ZAP daemon is up at {} (version {}).", apiUrl, version);
                    return version;
                }
            } catch (Exception e) {
                last = e; // Connection refused while ZAP is still booting: expected, keep waiting.
            }
            sleep(POLL_INTERVAL);
        }
        throw new IllegalStateException(
                "ZAP daemon at " + apiUrl + " was not reachable within " + timeout.toSeconds()
                        + "s. Start it with: docker compose --profile security up -d zap",
                last);
    }

    /** Wipes ZAP's state so a run's alerts cannot be polluted by a previous run's traffic. */
    public void newSession(String name) {
        call("/JSON/core/action/newSession/", Map.of("name", name, "overwrite", "true"));
        log.info("Started a fresh ZAP session: {}", name);
    }

    // ---------------------------------------------------------------------------
    // Safety: exclusions
    // ---------------------------------------------------------------------------

    /**
     * Registers a regex whose matching URLs must never be crawled or attacked.
     *
     * <p>This is the enforcement point for the "do not touch OTP or Razorpay" rule (see
     * {@code zap.exclude.regex} in the env properties). It is applied to the spider AND
     * the active scanner separately, because ZAP treats them as independent components —
     * excluding from one does NOT exclude from the other, which is an easy and expensive
     * mistake to make.
     */
    public void excludeFromScans(String regex) {
        if (regex == null || regex.isBlank()) {
            return;
        }
        for (String component : List.of("spider", "ascan")) {
            Response response = call("/JSON/" + component + "/action/excludeFromScan/", Map.of("regex", regex));
            if (response.statusCode() != 200) {
                // A bad regex here silently means "nothing is excluded", which would let an
                // active scan reach Twilio/Razorpay. Fail loudly instead.
                throw new IllegalStateException("ZAP rejected the exclusion regex for '" + component
                        + "'. Nothing would be excluded, so refusing to scan. ZAP said: " + response.asString());
            }
        }
        log.info("Excluded from ZAP spider + active scan: {}", regex);
    }

    /**
     * Re-grades a ZAP rule's findings, e.g. to demote a known false positive.
     *
     * <p>Filters only affect alerts raised AFTER they are registered, so this must be
     * called before any scanning starts.
     *
     * <p><b>Demote, do not delete.</b> {@code newLevel} 0 (Informational) keeps the
     * finding in the report where a human can still review it, while stopping it from
     * failing the build. {@code -1} marks it a false positive and hides it. Prefer 0:
     * a rule that is noisy today can become the one that matters tomorrow, and a scan
     * nobody trusts gets switched off.
     *
     * @param ruleId   ZAP plugin ID, e.g. 10062 for "PII Disclosure"
     * @param newLevel -1 = False Positive, 0 = Informational, 1 = Low, 2 = Medium, 3 = High
     * @param urlRegex limit the filter to matching URLs; blank/null = every URL
     */
    public void addAlertFilter(int ruleId, int newLevel, String urlRegex) {
        Map<String, String> params = new java.util.LinkedHashMap<>();
        params.put("ruleId", String.valueOf(ruleId));
        params.put("newLevel", String.valueOf(newLevel));
        params.put("enabled", "true");
        if (urlRegex != null && !urlRegex.isBlank()) {
            params.put("url", urlRegex);
            params.put("urlIsRegex", "true");
        }

        Response response = call("/JSON/alertFilter/action/addGlobalAlertFilter/", params);
        if (response.statusCode() != 200) {
            // Loud, not silent: a rejected filter means the scan will report the very
            // noise this filter existed to remove, and someone will wrongly trust it.
            throw new IllegalStateException("ZAP rejected the alert filter for rule " + ruleId
                    + ". ZAP said: " + response.asString());
        }
        log.info("Alert filter: rule {} re-graded to level {}{}",
                ruleId, newLevel, urlRegex == null || urlRegex.isBlank() ? " (all URLs)" : " for " + urlRegex);
    }

    // ---------------------------------------------------------------------------
    // Discovery
    // ---------------------------------------------------------------------------

    /** Makes ZAP fetch a single URL, seeding the site tree so the spider has a root. */
    public void accessUrl(String url) {
        call("/JSON/core/action/accessUrl/", Map.of("url", url, "followRedirects", "true"));
    }

    /**
     * Runs the traditional (HTTP link-following) spider and blocks until it finishes.
     *
     * @param maxDurationMinutes ZAP's own crawl budget. 0 means unlimited — avoid in CI.
     * @return number of URLs ZAP knows about afterwards
     */
    public int spider(String url, int maxDurationMinutes) {
        call("/JSON/spider/action/setOptionMaxDuration/", Map.of("Integer", String.valueOf(maxDurationMinutes)));

        Response start = call("/JSON/spider/action/scan/",
                Map.of("url", url, "recurse", "true", "subtreeOnly", "true"));
        String scanId = start.jsonPath().getString("scan");
        if (scanId == null) {
            throw new IllegalStateException("ZAP would not start the spider for " + url + ": " + start.asString());
        }
        log.info("Spider started (scanId={}) against {}", scanId, url);

        awaitScanCompletion("/JSON/spider/view/status/", scanId,
                Duration.ofMinutes(maxDurationMinutes + 2L), "spider");

        int found = call("/JSON/spider/view/results/", Map.of("scanId", scanId))
                .jsonPath().getList("results").size();
        log.info("Spider finished; {} URLs discovered.", found);
        return found;
    }

    /**
     * Runs the AJAX spider (browser-driven) and blocks until it stops.
     *
     * <p>Unlike the other scans this one has no percentage — its status is just the
     * string "running" or "stopped" — so it gets its own poll loop.
     */
    public void ajaxSpider(String url, int maxDurationMinutes, String browserId) {
        call("/JSON/ajaxSpider/action/setOptionMaxDuration/", Map.of("Integer", String.valueOf(maxDurationMinutes)));

        // Must be headless: the daemon container has no X server. Must also be a browser
        // the image actually ships — zaproxy/zap-stable carries Firefox ESR but NOT
        // Chrome, so "chrome-headless" fails here with a bare {"code":"internal_error"}
        // that names no cause. Hence firefox-headless as the default.
        Response browser = call("/JSON/ajaxSpider/action/setOptionBrowserId/", Map.of("String", browserId));
        if (browser.statusCode() != 200) {
            log.warn("ZAP rejected AJAX spider browser '{}' ({}). Continuing with plain-spider coverage only.",
                    browserId, browser.asString());
            return;
        }

        // inScope=false is REQUIRED here, counter-intuitive as it reads. "In scope" means
        // "inside a ZAP Context", and this suite defines no Context — so inScope=true
        // makes ZAP reject its own starting URL with an opaque
        // {"code":"internal_error"} whose real cause ("The starting URI is not in scope")
        // only appears in the container log. subtreeOnly=true is what actually keeps the
        // crawl constrained to the target, which is the containment we wanted anyway.
        Response start = call("/JSON/ajaxSpider/action/scan/",
                Map.of("url", url, "inScope", "false", "subtreeOnly", "true"));
        if (!"OK".equalsIgnoreCase(start.jsonPath().getString("Result"))) {
            // Not fatal: the plain spider has already seeded the tree, so we degrade to
            // shallower discovery rather than failing the whole security run.
            log.warn("AJAX spider did not start ({}). Continuing with plain-spider coverage only.", start.asString());
            return;
        }
        log.info("AJAX spider started against {}", url);

        Instant deadline = Instant.now().plus(Duration.ofMinutes(maxDurationMinutes + 2L));
        while (Instant.now().isBefore(deadline)) {
            String status = call("/JSON/ajaxSpider/view/status/").jsonPath().getString("status");
            if ("stopped".equalsIgnoreCase(status)) {
                log.info("AJAX spider finished.");
                return;
            }
            sleep(POLL_INTERVAL);
        }
        log.warn("AJAX spider overran its budget; stopping it and continuing.");
        call("/JSON/ajaxSpider/action/stop/");
    }

    /**
     * Waits for ZAP's passive scanner to drain its queue.
     *
     * <p>Passive rules run asynchronously over captured traffic. Reading alerts before
     * the queue empties gives a partial, run-to-run-varying picture — a subtle source of
     * flaky security results.
     */
    public void awaitPassiveScan(Duration timeout) {
        Instant deadline = Instant.now().plus(timeout);
        while (Instant.now().isBefore(deadline)) {
            int remaining = Integer.parseInt(
                    call("/JSON/pscan/view/recordsToScan/").jsonPath().getString("recordsToScan"));
            if (remaining == 0) {
                log.info("Passive scan queue drained.");
                return;
            }
            log.debug("Passive scan: {} records left.", remaining);
            sleep(POLL_INTERVAL);
        }
        log.warn("Passive scan queue did not drain within {}s; reporting what is available.", timeout.toSeconds());
    }

    // ---------------------------------------------------------------------------
    // Active scanning (attack traffic)
    // ---------------------------------------------------------------------------

    /**
     * Runs a full ACTIVE scan and blocks until it completes or exhausts its budget.
     *
     * <p><b>This sends real attack payloads.</b> Callers are responsible for making sure
     * the target is allowed to be attacked — {@link OwaspTop10ZapScanTest} enforces that
     * production is never scanned this way without an explicit override.
     */
    public void activeScan(String url, int maxDurationMinutes) {
        call("/JSON/ascan/action/setOptionMaxScanDurationInMins/",
                Map.of("Integer", String.valueOf(maxDurationMinutes)));

        Response start = call("/JSON/ascan/action/scan/",
                Map.of("url", url, "recurse", "true", "inScopeOnly", "false"));
        String scanId = start.jsonPath().getString("scan");
        if (scanId == null) {
            throw new IllegalStateException("ZAP would not start the active scan: " + start.asString());
        }
        log.warn("ACTIVE scan started (scanId={}) against {} - attack payloads are now in flight.", scanId, url);

        awaitScanCompletion("/JSON/ascan/view/status/", scanId,
                Duration.ofMinutes(maxDurationMinutes + 5L), "active scan");
    }

    /**
     * Polls a {@code view/status/} endpoint until it reports 100, or the deadline passes.
     *
     * <p>On timeout the scan is force-stopped and we carry on with whatever it found.
     * Partial coverage that gets reported beats a hung pipeline that reports nothing.
     */
    private void awaitScanCompletion(String statusPath, String scanId, Duration timeout, String label) {
        Instant deadline = Instant.now().plus(timeout);
        int lastLogged = -1;

        while (Instant.now().isBefore(deadline)) {
            int progress = Integer.parseInt(call(statusPath, Map.of("scanId", scanId)).jsonPath().getString("status"));
            if (progress != lastLogged && progress % 20 == 0) {
                log.info("{} progress: {}%", label, progress);
                lastLogged = progress;
            }
            if (progress >= SCAN_COMPLETE) {
                log.info("{} complete.", label);
                return;
            }
            sleep(POLL_INTERVAL);
        }
        log.warn("{} exceeded its {}min budget; stopping it and reporting partial findings.",
                label, timeout.toMinutes());
        String component = statusPath.contains("ascan") ? "ascan" : "spider";
        call("/JSON/" + component + "/action/stop/", Map.of("scanId", scanId));
    }

    // ---------------------------------------------------------------------------
    // Results
    // ---------------------------------------------------------------------------

    /**
     * Fetches every alert ZAP raised for the target, page by page.
     *
     * <p>The API caps how much it returns per call, so this pages with start/count until
     * a short page signals the end. Asking for everything in one shot silently truncates.
     */
    public List<ZapAlert> alerts(String baseUrl) {
        List<ZapAlert> collected = new ArrayList<>();
        final int pageSize = 500;
        int start = 0;

        while (true) {
            JsonPath json = call("/JSON/alert/view/alerts/", Map.of(
                    "baseurl", baseUrl,
                    "start", String.valueOf(start),
                    "count", String.valueOf(pageSize))).jsonPath();

            List<Map<String, Object>> page = json.getList("alerts");
            if (page == null || page.isEmpty()) {
                break;
            }
            page.forEach(raw -> collected.add(toAlert(raw)));

            if (page.size() < pageSize) {
                break; // Short page => last page.
            }
            start += pageSize;
        }
        log.info("ZAP raised {} alert instances for {}", collected.size(), baseUrl);
        return collected;
    }

    /** Maps one raw ZAP alert JSON object onto our {@link ZapAlert} record. */
    @SuppressWarnings("unchecked")
    private ZapAlert toAlert(Map<String, Object> raw) {
        // ZAP hands back "cweid" as a string, and uses "-1" for "no CWE".
        int cwe;
        try {
            cwe = Integer.parseInt(String.valueOf(raw.getOrDefault("cweid", "-1")).trim());
        } catch (NumberFormatException e) {
            cwe = -1;
        }

        // "tags" is a map of tagName -> reference URL, and is absent on older ZAP
        // versions. We only need the keys (e.g. OWASP_2021_A03).
        Set<String> tagKeys = new LinkedHashSet<>();
        Object tags = raw.get("tags");
        if (tags instanceof Map<?, ?> tagMap) {
            ((Map<String, Object>) tagMap).keySet().forEach(tagKeys::add);
        }

        return new ZapAlert(
                str(raw, "name"),
                str(raw, "risk"),
                str(raw, "confidence"),
                cwe,
                str(raw, "url"),
                str(raw, "param"),
                str(raw, "evidence"),
                str(raw, "solution"),
                tagKeys,
                OwaspCategory.classify(tagKeys, cwe));
    }

    private static String str(Map<String, Object> raw, String key) {
        Object value = raw.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    /** ZAP's own self-contained HTML report — the artifact stakeholders actually open. */
    public byte[] htmlReport() {
        return call("/OTHER/core/other/htmlreport/").asByteArray();
    }

    /**
     * The same findings as JSON, for anything that wants to post-process them.
     *
     * <p>HEADS UP — this report does NOT agree with {@link #alerts} on totals, and that
     * is expected, not a bug. The report groups findings by rule and caps the
     * {@code instances} it lists per rule (5 by default), so summing them undercounts.
     * A verified example from staging: the report listed 36 instances while ZAP's own
     * {@code alertsSummary} endpoint reported 204 (High 0 / Medium 10 / Low 178 / Info
     * 16) — which is exactly what {@link #alerts} returns, because that endpoint yields
     * one record per instance. Trust {@link #alerts} for counts; treat this report as a
     * human-readable digest.
     */
    public byte[] jsonReport() {
        return call("/OTHER/core/other/jsonreport/").asByteArray();
    }

    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            // Restore the flag so the interruption is not swallowed, then bail out.
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting on ZAP.", e);
        }
    }

    /**
     * One ZAP finding, already classified against the OWASP Top 10.
     *
     * @param name       human-readable rule name, e.g. "Content Security Policy Header Not Set"
     * @param risk       "High" | "Medium" | "Low" | "Informational"
     * @param confidence ZAP's confidence in the finding, e.g. "Medium"
     * @param cweId      CWE ID, or -1 when ZAP did not supply one
     * @param url        the exact URL the alert fired on
     * @param param      the parameter involved, when relevant
     * @param evidence   the snippet of response that triggered the rule
     * @param solution   ZAP's suggested remediation
     * @param tags       raw ZAP tag keys
     * @param category   the OWASP Top 10 2021 bucket this alert was classified into
     */
    public record ZapAlert(
            String name,
            String risk,
            String confidence,
            int cweId,
            String url,
            String param,
            String evidence,
            String solution,
            Set<String> tags,
            OwaspCategory category) {

        /** Ranks risk so we can compare against a configured fail threshold. */
        public int riskRank() {
            return switch (risk == null ? "" : risk.trim().toLowerCase()) {
                case "high" -> 3;
                case "medium" -> 2;
                case "low" -> 1;
                default -> 0; // Informational, or anything unrecognised.
            };
        }

        public static int rankOf(String riskLabel) {
            return new ZapAlert("", riskLabel, "", -1, "", "", "", "", Set.of(), OwaspCategory.UNMAPPED).riskRank();
        }
    }
}
