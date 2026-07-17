package com.timepieces.automation.tdd.security;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The ten OWASP Top 10 <b>2025</b> risk categories — this suite's default.
 *
 * <p>Titles and ordering verified two independent ways on 2026-07-17: against
 * <a href="https://owasp.org/Top10/2025/">owasp.org/Top10/2025</a>, and against the
 * reference URLs ZAP itself ships inside its alert tags (e.g. {@code OWASP_2025_A02} →
 * {@code https://owasp.org/Top10/2025/A02_2025-Security_Misconfiguration/}).
 *
 * <h2>What changed from 2021, and why the constructor looks like this</h2>
 * 2025 is a genuine reshuffle, not a renaming:
 * <ul>
 *   <li><b>SSRF is gone as a category</b> — CWE-918 is now inside A01 Broken Access
 *       Control (confirmed on OWASP's own A01:2025 page).</li>
 *   <li><b>Security Misconfiguration</b> climbed A05 → A02.</li>
 *   <li><b>Vulnerable and Outdated Components</b> became the broader
 *       <b>Software Supply Chain Failures</b> (A06 → A03).</li>
 *   <li><b>Mishandling of Exceptional Conditions</b> (A10) is entirely new.</li>
 * </ul>
 *
 * <p>Rather than hand-copy CWE tables — and risk them drifting apart — each constant
 * below declares the 2021 categories it <b>descends from</b>, and inherits their CWE sets.
 * That keeps one copy of the numbers and makes the lineage readable: A01 taking both
 * {@code A01} and {@code A10} <i>is</i> the SSRF merge, expressed in code.
 *
 * <p><b>These CWE sets are a FALLBACK ONLY</b>, used for the ~1% of alerts ZAP does not
 * tag (measured: 237 of 239 instances carried a 2025 tag). They are a documented
 * approximation derived from the 2021 mappings via the lineage above, not a transcription
 * of OWASP's published 2025 CWE lists. {@link #A10} deliberately has no lineage: it is new
 * in 2025, so there is nothing to inherit, and it relies on ZAP's tags alone.
 *
 * @since 2026-07-17
 * @version 1.0
 * @author Avinash Rath
 */
public enum Owasp2025Category implements OwaspCategory {

    /** Absorbs 2021's SSRF category: CWE-918 now lives here. */
    A01("A01:2025", "Broken Access Control",
            Owasp2021Category.A01, Owasp2021Category.A10),

    /** Promoted from A05:2021 — the biggest mover in the 2025 list. */
    A02("A02:2025", "Security Misconfiguration",
            Owasp2021Category.A05),

    /** Broadened from A06:2021 "Vulnerable and Outdated Components". */
    A03("A03:2025", "Software Supply Chain Failures",
            Owasp2021Category.A06),

    A04("A04:2025", "Cryptographic Failures",
            Owasp2021Category.A02),

    A05("A05:2025", "Injection",
            Owasp2021Category.A03),

    A06("A06:2025", "Insecure Design",
            Owasp2021Category.A04),

    /** Renamed from "Identification and Authentication Failures". */
    A07("A07:2025", "Authentication Failures",
            Owasp2021Category.A07),

    /** Note OWASP's 2025 spelling: "Software <b>or</b> Data Integrity Failures". */
    A08("A08:2025", "Software or Data Integrity Failures",
            Owasp2021Category.A08),

    /** Renamed from "Security Logging and Monitoring Failures". */
    A09("A09:2025", "Security Logging and Alerting Failures",
            Owasp2021Category.A09),

    /** NEW in 2025. No 2021 lineage, so classification here relies on ZAP's tags. */
    A10("A10:2025", "Mishandling of Exceptional Conditions"),

    /**
     * Escape hatch for alerts that carry neither a recognised OWASP tag nor a mappable
     * CWE. Reported but never failed — see {@link OwaspCategory}.
     */
    UNMAPPED("UNMAPPED", "Not mapped to an OWASP Top 10 2025 category");

    private final String id;
    private final String title;
    private final Set<Integer> cweIds;

    Owasp2025Category(String id, String title, Owasp2021Category... inheritsCwesFrom) {
        this.id = id;
        this.title = title;
        // Union the CWE sets of every 2021 ancestor. LinkedHashSet keeps a stable,
        // reviewable order and drops the overlaps a merge inevitably creates.
        Set<Integer> merged = new LinkedHashSet<>();
        Arrays.stream(inheritsCwesFrom).forEach(ancestor -> merged.addAll(ancestor.cweIds()));
        this.cweIds = merged;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public Set<Integer> cweIds() {
        return cweIds;
    }

    @Override
    public boolean isUnmapped() {
        return this == UNMAPPED;
    }

    /** The ten real categories, excluding {@link #UNMAPPED}. */
    public static List<OwaspCategory> topTen() {
        return Arrays.stream(values())
                .filter(c -> c != UNMAPPED)
                .map(OwaspCategory.class::cast)
                .toList();
    }
}
