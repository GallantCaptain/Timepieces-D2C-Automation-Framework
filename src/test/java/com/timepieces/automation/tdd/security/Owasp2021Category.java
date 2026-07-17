package com.timepieces.automation.tdd.security;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The ten OWASP Top 10 <b>2021</b> risk categories.
 *
 * <p>Kept alongside {@link Owasp2025Category} because a lot of audit and compliance
 * paperwork still cites the 2021 list. Select it with {@code zap.owasp.edition=2021};
 * the suite defaults to 2025. See {@link OwaspCategory} for how an alert is classified
 * and {@link OwaspEdition} for why both editions exist.
 *
 * <p>The CWE sets below are OWASP's published 2021 mappings. They are only a
 * <b>fallback</b> — ZAP's own tags win when present, which is ~99% of the time.
 *
 * @since 2026-07-17
 * @version 2.0
 * @author Avinash Rath
 */
public enum Owasp2021Category implements OwaspCategory {

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
     * Escape hatch for alerts that carry neither a recognised OWASP tag nor a mappable
     * CWE. Reported but never failed — see {@link OwaspCategory}.
     */
    UNMAPPED("UNMAPPED", "Not mapped to an OWASP Top 10 2021 category");

    private final String id;
    private final String title;
    private final Set<Integer> cweIds;

    Owasp2021Category(String id, String title, int... cweIds) {
        this.id = id;
        this.title = title;
        // LinkedHashSet keeps declaration order, which makes these sets easy to eyeball
        // against OWASP's published tables during review.
        this.cweIds = new LinkedHashSet<>(Arrays.stream(cweIds).boxed().toList());
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
