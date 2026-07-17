package com.timepieces.automation.tdd.security;

/**
 * Which edition of the OWASP Top 10 the security suite reports against.
 *
 * <h2>Why this is a choice at all</h2>
 * OWASP published a new Top 10 in 2025, and ZAP 2.17 tags every alert with BOTH editions
 * — an alert carries {@code OWASP_2021_A05} and {@code OWASP_2025_A02} side by side.
 * Since {@link OwaspCategory#classify} is tag-driven, supporting both costs little more
 * than a prefix switch, and it keeps the 2021 view available for auditors and compliance
 * documents that still cite the older list.
 *
 * <p>Measured on a real staging scan (2026-07-17): 237 of 239 alert instances carried a
 * 2025 tag, and exactly the same 237 carried a 2021 tag. Coverage is identical, so moving
 * to 2025 costs no classification accuracy.
 *
 * <p>Selected with {@code zap.owasp.edition=2021|2025}; defaults to 2025.
 *
 * @since 2026-07-17
 * @version 1.0
 * @author Avinash Rath
 */
public enum OwaspEdition {

    /** OWASP Top 10 2021. Kept because plenty of audit paperwork still references it. */
    E2021("2021", "OWASP_2021_A"),

    /** OWASP Top 10 2025 — the current list, and this suite's default. */
    E2025("2025", "OWASP_2025_A");

    private final String year;
    private final String tagPrefix;

    OwaspEdition(String year, String tagPrefix) {
        this.year = year;
        this.tagPrefix = tagPrefix;
    }

    public String year() {
        return year;
    }

    /** The prefix ZAP stamps on its alert tags for this edition, e.g. {@code OWASP_2025_A}. */
    public String tagPrefix() {
        return tagPrefix;
    }

    /**
     * Resolves a config value ("2021" / "2025") onto an edition.
     *
     * @throws IllegalArgumentException on anything else — a typo here would silently
     *         change which taxonomy the whole security report is written against, which
     *         is not something to guess at.
     */
    public static OwaspEdition of(String value) {
        String normalised = value == null ? "" : value.trim();
        return switch (normalised) {
            case "2021" -> E2021;
            case "2025", "" -> E2025;
            default -> throw new IllegalArgumentException(
                    "Unknown zap.owasp.edition [" + value + "]. Expected 2021 or 2025.");
        };
    }
}
