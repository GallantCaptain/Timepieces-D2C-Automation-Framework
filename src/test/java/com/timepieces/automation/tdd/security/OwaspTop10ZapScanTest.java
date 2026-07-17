package com.timepieces.automation.tdd.security;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.timepieces.automation.tdd.security.ZapScanEngine.ZapAlert;
import com.timepieces.automation.utils.ConfigReader;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import lombok.extern.log4j.Log4j2;

/**
 * DAST (Dynamic Application Security Testing) coverage of the OWASP Top 10 (2021),
 * driven by an OWASP ZAP daemon.
 *
 * <h2>What this suite does</h2>
 * One scan runs in {@link #runSecurityScan()}. Its findings are then bucketed into the
 * ten OWASP categories, and each category becomes its own test case, so the Allure
 * report reads as "A03: Injection - passed" rather than as a wall of raw ZAP alerts.
 * A scan is expensive (minutes), so it deliberately runs ONCE per class rather than
 * once per test.
 *
 * <h2>Scan modes, and why production is different</h2>
 * <ul>
 *   <li><b>{@code zap.scan.mode=full}</b> (staging) - spider, then ACTIVE scan: real
 *       SQLi/XSS/command-injection payloads fired at every parameter found.</li>
 *   <li><b>{@code zap.scan.mode=baseline}</b> (production) - spider + PASSIVE rules only.
 *       Passive rules just read responses that would have been sent anyway. Nothing is
 *       injected, no form is submitted, no data is written.</li>
 * </ul>
 * Production is baseline by deliberate design. An active scan against www.timepieces-d2c.example
 * could create junk orders, bill and send real OTP SMS to real customers via Twilio,
 * throw attack traffic at Razorpay (a third party nobody has authorised us to scan),
 * and degrade the live storefront. {@link #assertProductionIsNotBeingAttacked} enforces
 * that; it is the single most important method in this class.
 *
 * <h2>Running it</h2>
 * <pre>
 *   docker compose --profile security up -d zap
 *   ./mvnw test "-Dsurefire.suite=testng-security.xml" "-Denv=staging"
 * </pre>
 *
 * @since 2026-07-17
 * @version 1.0
 * @author Avinash Rath
 */
@Log4j2
public class OwaspTop10ZapScanTest {

    /** Findings grouped by OWASP category. Populated once by {@link #runSecurityScan()}. */
    private final Map<OwaspCategory, List<ZapAlert>> alertsByCategory = new EnumMap<>(OwaspCategory.class);

    /** Minimum risk that fails a category test; anything lower is reported but tolerated. */
    private int failRiskRank;

    private String targetUrl;
    private String scanMode;

    // ---------------------------------------------------------------------------
    // The scan
    // ---------------------------------------------------------------------------

    @BeforeClass(alwaysRun = true)
    public void runSecurityScan() {
        String env = System.getProperty("env", "staging").trim().toLowerCase();

        targetUrl = valueOr("zap.target.url", ConfigReader.get("base.url"));
        scanMode = valueOr("zap.scan.mode", "baseline").toLowerCase();
        failRiskRank = ZapAlert.rankOf(valueOr("zap.fail.on.risk", "High"));

        String apiUrl = valueOr("zap.api.url", "http://localhost:8090");
        String apiKey = valueOr("zap.api.key", "timepieces-zap-dev-key");

        // SAFETY GATE. Runs before a single packet is sent.
        assertProductionIsNotBeingAttacked(env, scanMode);

        ZapScanEngine zap = new ZapScanEngine(apiUrl, apiKey);

        // If ZAP is not running, SKIP rather than FAIL. The daemon is opt-in
        // infrastructure (a compose profile), so a developer running the whole suite
        // without it should not see a red build for a service they never asked for.
        try {
            zap.awaitZapReady(Duration.ofSeconds(90));
        } catch (IllegalStateException e) {
            throw new SkipException("ZAP daemon unavailable, skipping the security suite. " + e.getMessage(), e);
        }

        log.info("=== ZAP {} scan | env={} | target={} | fail-on={} ===",
                scanMode, env, targetUrl, valueOr("zap.fail.on.risk", "High"));

        zap.newSession("timepieces-" + env);
        zap.excludeFromScans(ConfigReader.get("zap.exclude.regex"));
        applyAlertFilters(zap);

        // Discovery: seed the tree, crawl links, then crawl JS-rendered routes.
        zap.accessUrl(targetUrl);
        zap.spider(targetUrl, intValueOr("zap.spider.max.duration.minutes", 5));

        if (Boolean.parseBoolean(valueOr("zap.ajax.spider.enabled", "true"))) {
            zap.ajaxSpider(targetUrl,
                    intValueOr("zap.ajax.spider.max.duration.minutes", 5),
                    valueOr("zap.ajax.spider.browser", "firefox-headless"));
        }

        // Passive rules grade everything discovered above. Both modes do this.
        zap.awaitPassiveScan(Duration.ofMinutes(5));

        // Only "full" mode attacks. Baseline stops after passive analysis.
        if ("full".equals(scanMode)) {
            zap.activeScan(targetUrl, intValueOr("zap.active.scan.max.duration.minutes", 25));
            zap.awaitPassiveScan(Duration.ofMinutes(2)); // Attack traffic queues more passive work.
        } else {
            log.info("Baseline mode: passive rules only. No attack payloads were sent.");
        }

        List<ZapAlert> alerts = zap.alerts(targetUrl);
        for (OwaspCategory category : OwaspCategory.values()) {
            alertsByCategory.put(category, alerts.stream()
                    .filter(a -> a.category() == category)
                    .sorted(Comparator.comparingInt(ZapAlert::riskRank).reversed())
                    .toList());
        }

        publishReports(zap, alerts, env);
    }

    /**
     * Registers the configured alert filters, which re-grade known false positives.
     *
     * <p>Config format ({@code zap.alert.filters}), semicolon-separated:
     * <pre>ruleId|newLevel|urlRegex</pre>
     * {@code newLevel}: -1 = False Positive, 0 = Informational, 1 = Low, 2 = Medium,
     * 3 = High. {@code urlRegex} may be empty to mean "every URL". Example:
     * <pre>10062|0|</pre>
     *
     * <p><b>Why this exists — a real, verified case.</b> ZAP's "PII Disclosure" rule
     * (10062) finds card numbers by pattern-matching digit runs and Luhn-checking them.
     * Timepieces runs on Shopify, whose numeric GIDs are long Luhn-valid digit strings that
     * land squarely in card-number ranges. The first production baseline raised 18 High
     * "PII Disclosure" alerts; every one was a Shopify GID, confirmed by fetching the
     * pages — e.g. {@code gid://shopify/MediaImage/36474036551724} and
     * {@code gid://shopify/Article/574019469356}. The rule only ever sees the bare
     * number, never the surrounding {@code gid://shopify/...}, so it cannot tell them
     * apart on this stack.
     *
     * <p>Left alone, the Sunday production scan would fail every single week on those
     * false positives, and a scan that cries wolf gets switched off. So they are demoted
     * to Informational — still listed in the report for a human to review, no longer
     * failing the build. They are demoted, NOT deleted; see
     * {@link ZapScanEngine#addAlertFilter} for why that distinction matters.
     */
    private void applyAlertFilters(ZapScanEngine zap) {
        String configured = ConfigReader.get("zap.alert.filters");
        if (configured == null || configured.isBlank()) {
            return;
        }
        for (String entry : configured.split(";")) {
            if (entry.isBlank()) {
                continue;
            }
            // -1 keeps trailing empty fields, so "10062|0|" yields a blank urlRegex
            // rather than a 2-element array and an AIOOBE.
            String[] parts = entry.split("\\|", -1);
            if (parts.length < 2) {
                log.warn("Ignoring malformed zap.alert.filters entry [{}]; expected ruleId|newLevel|urlRegex", entry);
                continue;
            }
            try {
                zap.addAlertFilter(
                        Integer.parseInt(parts[0].trim()),
                        Integer.parseInt(parts[1].trim()),
                        parts.length > 2 ? parts[2].trim() : "");
            } catch (NumberFormatException e) {
                log.warn("Ignoring zap.alert.filters entry [{}]: ruleId and newLevel must be numbers.", entry);
            }
        }
    }

    /**
     * Refuses to run an active scan against production unless someone has explicitly
     * and knowingly authorised it.
     *
     * <p>This is a hard failure, not a silent downgrade to baseline. If a config change
     * or a stray {@code -Dzap.scan.mode=full} would have pointed attack traffic at the
     * live store, that is a mistake worth stopping the build over — quietly "fixing" it
     * would hide the misconfiguration until the day someone sets the override too.
     */
    private void assertProductionIsNotBeingAttacked(String env, String mode) {
        boolean isProduction = env.contains("prod");
        boolean isActiveScan = "full".equals(mode);
        boolean explicitlyAllowed = Boolean.parseBoolean(valueOr("zap.allow.prod.active", "false"));

        if (isProduction && isActiveScan && !explicitlyAllowed) {
            throw new IllegalStateException("""
                    REFUSING to run a ZAP ACTIVE scan against PRODUCTION.

                    An active scan fires real SQLi/XSS/command-injection payloads at every form and
                    endpoint it discovers. Against the live store that can create junk orders, bill and
                    send real OTP SMS to real customers through Twilio, throw attack traffic at Razorpay
                    (a third party we are not authorised to scan), and degrade the site for shoppers.

                    Production is configured for 'baseline' (passive, read-only) scanning on purpose.

                    If an authorised, supervised active scan is genuinely intended, set
                    -Dzap.allow.prod.active=true, and only with written sign-off from the store owner
                    plus an agreed maintenance window.""");
        }

        if (isProduction && isActiveScan) {
            log.warn("!!! ACTIVE scan against PRODUCTION, explicitly authorised via zap.allow.prod.active=true. !!!");
        }
    }

    /** Attaches ZAP's own reports to Allure and writes them under target/zap for CI upload. */
    private void publishReports(ZapScanEngine zap, List<ZapAlert> alerts, String env) {
        try {
            byte[] html = zap.htmlReport();
            byte[] json = zap.jsonReport();

            Allure.addAttachment("ZAP " + scanMode + " report (" + env + ")",
                    "text/html", new ByteArrayInputStream(html), ".html");
            Allure.addAttachment("ZAP alerts (JSON)",
                    "application/json", new ByteArrayInputStream(json), ".json");
            Allure.addAttachment("OWASP Top 10 summary", "text/plain", summaryTable(alerts), ".txt");

            // Also drop them on disk: the CI workflow uploads target/zap as an artifact,
            // and the emailer links to it. Allure alone is not enough for that path.
            Path dir = Path.of("target", "zap");
            Files.createDirectories(dir);
            Files.write(dir.resolve("zap-report.html"), html);
            Files.write(dir.resolve("zap-alerts.json"), json);
            Files.writeString(dir.resolve("owasp-top10-summary.txt"), summaryTable(alerts), StandardCharsets.UTF_8);

            log.info("\n{}", summaryTable(alerts));
        } catch (Exception e) {
            // Reporting must never mask the findings themselves.
            log.error("Failed to publish ZAP reports; the assertions below still stand.", e);
        }
    }

    /** A plain-text count of findings per category — the thing worth reading first. */
    private String summaryTable(List<ZapAlert> alerts) {
        StringBuilder sb = new StringBuilder();
        sb.append("OWASP Top 10 (2021) - ZAP ").append(scanMode).append(" scan of ").append(targetUrl).append('\n');
        sb.append("Total alert instances: ").append(alerts.size()).append('\n');
        sb.append("-".repeat(84)).append('\n');
        // Width 56: the longest label ("A07:2021 - Identification and Authentication
        // Failures") is 52 chars, and %-52s would leave it flush against the counts.
        sb.append(String.format("%-56s %5s %7s %5s %5s%n", "Category", "High", "Medium", "Low", "Info"));
        sb.append("-".repeat(84)).append('\n');

        for (OwaspCategory category : OwaspCategory.values()) {
            List<ZapAlert> found = alertsByCategory.getOrDefault(category, List.of());
            if (category == OwaspCategory.UNMAPPED && found.isEmpty()) {
                continue; // Only show the escape hatch when it actually caught something.
            }
            sb.append(String.format("%-56s %5d %7d %5d %5d%n",
                    category.label(),
                    count(found, 3), count(found, 2), count(found, 1), count(found, 0)));
        }
        return sb.toString();
    }

    private static long count(List<ZapAlert> alerts, int rank) {
        return alerts.stream().filter(a -> a.riskRank() == rank).count();
    }

    // ---------------------------------------------------------------------------
    // The ten test cases
    // ---------------------------------------------------------------------------

    /**
     * Yields the ten OWASP Top 10 2021 categories, one test case each.
     *
     * <p>A data provider rather than ten near-identical methods: the assertion is the
     * same for every category, and only the bucket changes.
     */
    @DataProvider(name = "owaspTop10")
    public Object[][] owaspTop10() {
        return OwaspCategory.topTen().stream()
                .map(category -> new Object[] { category })
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "owaspTop10", groups = { "security" })
    @Severity(SeverityLevel.CRITICAL)
    @Description("Asserts the ZAP scan raised no alert at or above the configured risk "
            + "threshold for this OWASP Top 10 2021 category.")
    public void testOwaspCategoryIsClean(OwaspCategory category) {
        Allure.getLifecycle().updateTestCase(t -> t.setName(category.label()));

        List<ZapAlert> found = alertsByCategory.getOrDefault(category, List.of());
        List<ZapAlert> failing = found.stream()
                .filter(a -> a.riskRank() >= failRiskRank)
                .toList();

        // Everything below the threshold is still surfaced — it just does not fail the
        // build. Silent low-risk findings are how a Medium becomes a High next quarter.
        if (!found.isEmpty()) {
            Allure.addAttachment(category.id() + " - all findings", "text/plain", describe(found), ".txt");
        }

        if (!failing.isEmpty()) {
            throw new AssertionError(String.format(
                    "%s: %d finding(s) at or above %s risk on %s.%n%n%s",
                    category.label(), failing.size(), valueOr("zap.fail.on.risk", "High"),
                    targetUrl, describe(failing)));
        }
        log.info("{}: clean at/above the {} threshold ({} lower-risk finding(s) reported).",
                category.label(), valueOr("zap.fail.on.risk", "High"), found.size());
    }

    /**
     * Report-only: surfaces alerts that could not be mapped to a Top 10 category.
     *
     * <p>Never fails. Its job is to stop unclassifiable findings from vanishing, and to
     * flag when {@link OwaspCategory}'s mapping needs extending.
     */
    @Test(groups = { "security" })
    @Severity(SeverityLevel.MINOR)
    @Description("Reports ZAP alerts that map to no OWASP Top 10 2021 category, so that "
            + "unclassified findings stay visible rather than being silently dropped.")
    public void reportUnmappedFindings() {
        List<ZapAlert> unmapped = alertsByCategory.getOrDefault(OwaspCategory.UNMAPPED, List.of());
        if (unmapped.isEmpty()) {
            log.info("Every ZAP alert mapped cleanly onto an OWASP Top 10 2021 category.");
            return;
        }
        String detail = describe(unmapped);
        Allure.addAttachment("Unmapped ZAP findings", "text/plain", detail, ".txt");
        log.warn("{} ZAP alert(s) did not map to an OWASP category. Consider extending "
                + "OwaspCategory's CWE table:\n{}", unmapped.size(), detail);
    }

    /** Renders alerts as readable text: one block per finding, worst risk first. */
    private String describe(List<ZapAlert> alerts) {
        return alerts.stream()
                .map(a -> String.format("[%s] %s%n    URL      : %s%n    Param    : %s%n"
                        + "    CWE      : %s%n    Evidence : %s%n    Fix      : %s",
                        a.risk(), a.name(), a.url(),
                        a.param().isBlank() ? "-" : a.param(),
                        a.cweId() > 0 ? String.valueOf(a.cweId()) : "-",
                        truncate(a.evidence()), truncate(a.solution())))
                .collect(Collectors.joining("\n\n"));
    }

    /** ZAP's solution text can be paragraphs of HTML; keep the log readable. */
    private static String truncate(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        String flat = value.replaceAll("\\s+", " ").trim();
        return flat.length() <= 240 ? flat : flat.substring(0, 240) + "...";
    }

    // ---------------------------------------------------------------------------
    // Config helpers
    // ---------------------------------------------------------------------------

    /** {@link ConfigReader#get} returns null for unmapped keys; these add a default. */
    private static String valueOr(String key, String fallback) {
        String value = ConfigReader.get(key);
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private static int intValueOr(String key, int fallback) {
        try {
            return Integer.parseInt(valueOr(key, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            log.warn("Config key [{}] is not a number; falling back to {}.", key, fallback);
            return fallback;
        }
    }
}
