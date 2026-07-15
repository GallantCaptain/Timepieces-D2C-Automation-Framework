# Antigravity Scrape Brief — Phase 3, Chunk 3A: Legal / Policy pages

**For:** the Antigravity browser agent (drives real Chrome, executes JS).
**Why a real browser:** the storefront is a client-rendered React/Shopify app;
`data-testid`s and footer content only exist after JS runs.

**Goal:** map the 5 legal/policy pages. Their POM scaffolds have NO `PAGE_PATH`
(the real URLs are unknown), so **STEP 0 is URL discovery via the footer**, then
map each page. These are public (no login).

| Policy page | Class it feeds |
| --- | --- |
| Guarantee | `GuaranteePolicyPage` |
| Privacy | `PrivacyPolicyPage` |
| Refund | `RefundPolicyPage` |
| Shipping | `ShippingPolicyPage` |
| Terms of Service | `TermsOfServicePage` |

## ⚠️ STEP 0 — discover the policy URLs from the footer

On the homepage (`https://www.timepieces-d2c.example`), open/scroll to the footer and read
the legal links:
- `a[data-testid='footer-legal']` (and any policy links under
  `a[data-testid='footer-menu-link']` / `a[data-testid='footer-accordion-link']`).

Report, for EACH of the 5 policies, the **exact href/URL** (e.g.
`/policies/refund-policy` vs `/legal/refund` — Shopify commonly uses
`/policies/…`) and the visible link text. If a policy has no footer link, note it
and try a sensible direct URL. List every legal/policy link found even if it maps
to none of the 5 (e.g. cookie policy).

## Per-page capture (visit each discovered URL)

Locator ladder: `data-testid` → `id` → aria/role → text-anchor → semantic class →
structural. Record the tier + whether a `data-testid` exists. Report only the live
DOM.

1. Exact page `<title>` and the main heading (`h1`) text + selector.
2. **Last-updated / effective date** line (if any) + selector.
3. Content structure: is it plain prose, or does it have section headings
   (`h2`/`h3`), a **table-of-contents / anchor nav**, or **accordions**? Give the
   content-container selector and the section-heading selector.
4. Any **contact links** (email `mailto:`, support links) inside the policy body.
5. Any interactive elements (a side drawer, a "back to top", print, download PDF).

## ⭐ MOST IMPORTANT — do the 5 pages share ONE template?

Compare the 5 pages. If they share the same layout (same header/footer, same
content container + heading selectors, differing only in text), say so explicitly
and give the **shared selectors** — we will build a single `SharedPolicyPage`
base and have the 5 classes supply only their `PAGE_PATH` + `PAGE_TITLE`. If any
page differs structurally, call out how.

## Raw inventory

- Full `data-testid` inventory on a policy page.
- Consolidated list of NO-`data-testid` elements + the fallback selector used
  (with tier) to flag `ROLLOUT-GAP`s.

## Output format

Return ONE consolidated Markdown file `legal-pages.md` with:
- a **URL discovery** section (the 5 policy URLs + link texts from the footer),
- a **shared template** section (shared selectors + the verdict on whether all 5
  share it),
- a short **per-page** section for each of the 5 (title, heading, last-updated,
  content structure, contact links, anything unique),
- the raw `data-testid` inventory + NO-`data-testid` fallback list.

Drop it back and I'll build `SharedPolicyPage` (if shared) + the 5 policy classes
(real `PAGE_PATH`/`PAGE_TITLE`), then move to chunk 3C.
