package com.timepieces.automation.tdd.security;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The ten OWASP Top 10 (2021) risk categories, plus the logic that decides which
 * category a given ZAP alert belongs to.
 *
 * <h2>Why this class exists</h2>
 * ZAP does not report findings as "A03: Injection". It reports individual alerts
 * like "SQL Injection" or "Content Security Policy Header Not Set". To assert
 * against the OWASP Top 10 — which is what stakeholders and auditors actually ask
 * about — every alert has to be bucketed into one of the ten categories. That
 * bucketing is this class's only job.
 *
 * <h2>How an alert is classified (two strategies, in order)</h2>
 * <ol>
 *   <li><b>ZAP alert tags (preferred).</b> Modern ZAP tags its own alerts with keys
 *       like {@code OWASP_2021_A03}. ZAP's own maintainers curate those tags, so when
 *       a tag is present it beats anything we could hand-maintain here. This also
 *       catches alerts whose CWE is not in OWASP's published list at all — the CSP
 *       alerts, for instance, report CWE-693, which appears in no official category
 *       list, yet ZAP correctly tags them A05.</li>
 *   <li><b>CWE ID fallback.</b> If an alert carries no OWASP tag (older ZAP versions,
 *       or custom rules), we fall back to matching its CWE ID against the published
 *       OWASP Top 10 2021 CWE mappings held in {@link #cweIds}.</li>
 * </ol>
 * If BOTH strategies miss, the alert becomes {@link #UNMAPPED}. That bucket is
 * deliberate: an alert we cannot classify must stay visible rather than be silently
 * dropped, because "we didn't understand it" is not the same as "it's not a problem".
 *
 * @since 2026-07-17
 * @version 1.0
 * @author Avinash Rath
 */
public enum OwaspCategory {

    A01("A01:2021", "Broken Access Control",
            22, 23, 35, 59, 200, 201, 219, 264, 275, 276, 284, 285, 352, 359, 377, 402,
            425, 441, 497, 538, 540, 548, 552, 566, 601, 639, 651, 668, 706, 862, 863,
            913, 922, 1275),

    A02("A02:2021", "Cryptographic Failures",
            261, 296, 310, 319, 321, 322, 323, 324, 325, 326, 327, 328, 329, 330, 331,
            335, 336, 337, 338, 340, 347, 523, 720, 757, 759, 760, 780, 818, 916),

    A03("A03:2021", "Injection",
            20, 74, 75, 77, 78, 79, 80, 83, 87, 88, 89, 90, 91, 93, 94, 95, 96, 97, 98,
            99, 100, 113, 116, 138, 184, 470, 471, 564, 610, 643, 644, 652, 917),

    A04("A04:2021", "Insecure Design",
            73, 183, 209, 213, 235, 256, 257, 266, 269, 280, 311, 312, 313, 316, 419,
            430, 434, 444, 451, 472, 501, 522, 525, 539, 579, 598, 602, 642, 646, 650,
            653, 656, 657, 799, 807, 840, 841, 927, 1021, 1173),

    A05("A05:2021", "Security Misconfiguration",
            2, 11, 13, 15, 16, 260, 315, 520, 526, 537, 541, 547, 611, 614, 756, 776,
            942, 1004, 1032, 1174,
            // Not in OWASP's published list, but it is what ZAP stamps on its
            // CSP / anti-clickjacking / permissions-policy alerts, which are
            // misconfiguration findings by any reasonable reading.
            693),

    A06("A06:2021", "Vulnerable and Outdated Components",
            937, 1035, 1104),

    A07("A07:2021", "Identification and Authentication Failures",
            255, 259, 287, 288, 290, 294, 295, 297, 300, 302, 304, 306, 307, 346, 384,
            521, 613, 620, 640, 798, 940, 1216),

    A08("A08:2021", "Software and Data Integrity Failures",
            345, 353, 426, 494, 502, 565, 784, 829, 830, 915),

    A09("A09:2021", "Security Logging and Monitoring Failures",
            117, 223, 532, 778),

    A10("A10:2021", "Server-Side Request Forgery (SSRF)",
            918),

    /**
     * Escape hatch for alerts that carry neither a recognised OWASP tag nor a CWE we
     * can map. Reported separately so nothing disappears; see the class javadoc.
     */
    UNMAPPED("UNMAPPED", "Not mapped to an OWASP Top 10 2021 category");

    private final String id;
    private final String title;
    private final Set<Integer> cweIds;

    OwaspCategory(String id, String title, int... cweIds) {
        this.id = id;
        this.title = title;
        // LinkedHashSet keeps the declaration order, which makes the sets easy to
        // eyeball against OWASP's published tables during review.
        this.cweIds = new LinkedHashSet<>(Arrays.stream(cweIds).boxed().toList());
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    /** e.g. {@code "A03:2021 - Injection"}. Used for report headings and test names. */
    public String label() {
        return id + " - " + title;
    }

    /**
     * The ten real categories, excluding {@link #UNMAPPED}. Handy for iterating the
     * Top 10 without special-casing the escape hatch every time.
     */
    public static List<OwaspCategory> topTen() {
        return Arrays.stream(values()).filter(c -> c != UNMAPPED).toList();
    }

    /**
     * Classifies a single ZAP alert.
     *
     * @param zapTagKeys the keys of the alert's {@code tags} map as ZAP reported them
     *                   (e.g. {@code OWASP_2021_A03}, {@code WSTG-v42-INPV-05}). May be
     *                   empty for older ZAP versions or custom rules.
     * @param cweId      the alert's CWE ID. ZAP uses {@code -1} (and sometimes
     *                   {@code 0}) to mean "no CWE", so both are treated as absent.
     * @return the matching category, or {@link #UNMAPPED} if neither strategy matched.
     */
    public static OwaspCategory classify(Set<String> zapTagKeys, int cweId) {
        // Strategy 1: trust ZAP's own OWASP tag when it gives us one.
        if (zapTagKeys != null) {
            for (String tag : zapTagKeys) {
                if (tag == null) {
                    continue;
                }
                String normalised = tag.trim().toUpperCase();
                // Tags look like "OWASP_2021_A03". Match on the prefix so we are not
                // brittle about any suffix ZAP may append later.
                if (normalised.startsWith("OWASP_2021_A")) {
                    for (OwaspCategory candidate : topTen()) {
                        // candidate.id is "A03:2021" -> we want the "A03" part.
                        String shortId = candidate.id.substring(0, candidate.id.indexOf(':'));
                        if (normalised.startsWith("OWASP_2021_" + shortId)) {
                            return candidate;
                        }
                    }
                }
            }
        }

        // Strategy 2: fall back to the published CWE -> category mapping.
        if (cweId > 0) {
            for (OwaspCategory candidate : topTen()) {
                if (candidate.cweIds.contains(cweId)) {
                    return candidate;
                }
            }
        }

        return UNMAPPED;
    }
}
