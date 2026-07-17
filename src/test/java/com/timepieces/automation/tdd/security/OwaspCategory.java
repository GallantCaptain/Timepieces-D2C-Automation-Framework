package com.timepieces.automation.tdd.security;

import java.util.List;
import java.util.Set;

/**
 * One OWASP Top 10 risk category, and the logic that decides which category a given ZAP
 * alert belongs to.
 *
 * <h2>Why this is an interface</h2>
 * OWASP publishes a new Top 10 every few years, and the categories genuinely change:
 * 2025 folded SSRF into Broken Access Control, promoted Security Misconfiguration from
 * A05 to A02, and added a brand-new "Mishandling of Exceptional Conditions". So an
 * edition is a set of categories, not a renaming — hence one enum per edition
 * ({@link Owasp2021Category}, {@link Owasp2025Category}) behind this common type, chosen
 * at runtime via {@link OwaspEdition}.
 *
 * <h2>Why this class exists at all</h2>
 * ZAP does not report findings as "A05: Security Misconfiguration". It reports individual
 * alerts like "Content Security Policy Header Not Set". To assert against the OWASP Top 10
 * — which is what stakeholders and auditors actually ask about — every alert has to be
 * bucketed into a category. That bucketing is this type's only job.
 *
 * <h2>How an alert is classified (two strategies, in order)</h2>
 * <ol>
 *   <li><b>ZAP alert tags (preferred).</b> ZAP tags its own alerts with keys like
 *       {@code OWASP_2025_A02}. ZAP's maintainers curate those tags, so when one is
 *       present it beats anything hand-maintained here. Measured on a real staging scan:
 *       <b>237 of 239 alert instances (99.2%) carried a tag</b>, for both editions.</li>
 *   <li><b>CWE ID fallback.</b> For the ~1% with no tag, match the alert's CWE against
 *       the category's CWE set.</li>
 * </ol>
 * If both miss, the alert becomes the edition's UNMAPPED bucket. That is deliberate: an
 * alert we cannot classify must stay visible rather than be silently dropped, because
 * "we didn't understand it" is not the same as "it's not a problem".
 *
 * <p><b>Tag-first is not a stylistic preference — it changes answers.</b> ZAP's "Sub
 * Resource Integrity Attribute Missing" reports {@code CWE-345}, which OWASP's published
 * table maps to A08. But ZAP tags it {@code OWASP_2021_A05}. Tag-first files it as A05 —
 * ZAP's own answer. A CWE-only implementation would have put every Medium finding from
 * the first staging scan in the wrong category.
 *
 * @since 2026-07-17
 * @version 2.0
 * @author Avinash Rath
 */
public interface OwaspCategory {

    /** e.g. {@code "A02:2025"}. */
    String id();

    /** e.g. {@code "Security Misconfiguration"}. */
    String title();

    /** CWE IDs that fall back to this category when ZAP supplies no OWASP tag. */
    Set<Integer> cweIds();

    /** True only for the edition's UNMAPPED escape hatch. */
    boolean isUnmapped();

    /** e.g. {@code "A02:2025 - Security Misconfiguration"}. Used for report headings. */
    default String label() {
        return id() + " - " + title();
    }

    /** The ten real categories for an edition, excluding UNMAPPED. */
    static List<OwaspCategory> topTen(OwaspEdition edition) {
        return edition == OwaspEdition.E2021 ? Owasp2021Category.topTen() : Owasp2025Category.topTen();
    }

    /** Every category for an edition, UNMAPPED last. */
    static List<OwaspCategory> all(OwaspEdition edition) {
        return edition == OwaspEdition.E2021
                ? List.of(Owasp2021Category.values()).stream().map(OwaspCategory.class::cast).toList()
                : List.of(Owasp2025Category.values()).stream().map(OwaspCategory.class::cast).toList();
    }

    /** The edition's escape-hatch bucket. */
    static OwaspCategory unmapped(OwaspEdition edition) {
        return edition == OwaspEdition.E2021 ? Owasp2021Category.UNMAPPED : Owasp2025Category.UNMAPPED;
    }

    /**
     * Classifies a single ZAP alert against the given edition.
     *
     * @param zapTagKeys keys of the alert's {@code tags} map as ZAP reported them
     *                   (e.g. {@code OWASP_2025_A02}, {@code WSTG-v42-INPV-05}). May be
     *                   empty — older ZAP versions, custom rules, and notably any alert
     *                   that an alert filter has re-graded, because <b>ZAP strips tags
     *                   from filtered alerts</b>.
     * @param cweId      the alert's CWE ID. ZAP uses {@code -1} (sometimes {@code 0}) for
     *                   "no CWE"; both are treated as absent.
     * @param edition    which Top 10 to classify against.
     * @return the matching category, or the edition's UNMAPPED bucket.
     */
    static OwaspCategory classify(Set<String> zapTagKeys, int cweId, OwaspEdition edition) {
        List<OwaspCategory> candidates = topTen(edition);

        // Strategy 1: trust ZAP's own OWASP tag when it gives us one.
        if (zapTagKeys != null) {
            for (String tag : zapTagKeys) {
                if (tag == null) {
                    continue;
                }
                String normalised = tag.trim().toUpperCase();
                if (!normalised.startsWith(edition.tagPrefix())) {
                    continue; // e.g. a 2021 tag while we are reporting 2025.
                }
                for (OwaspCategory candidate : candidates) {
                    // candidate.id() is "A02:2025" -> compare on the "A02" part, so we are
                    // not brittle about any suffix ZAP may append later.
                    String shortId = candidate.id().substring(0, candidate.id().indexOf(':'));
                    if (normalised.startsWith("OWASP_" + edition.year() + "_" + shortId)) {
                        return candidate;
                    }
                }
            }
        }

        // Strategy 2: fall back to the CWE mapping.
        if (cweId > 0) {
            for (OwaspCategory candidate : candidates) {
                if (candidate.cweIds().contains(cweId)) {
                    return candidate;
                }
            }
        }

        return unmapped(edition);
    }
}
