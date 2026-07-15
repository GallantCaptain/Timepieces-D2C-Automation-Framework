# Antigravity Scrape Brief — Phase 1, Chunk 1E: Manifesta timepiece PDPs

**For:** the Antigravity browser agent (drives real Chrome, executes JS).
**Why a real browser:** the storefront is a client-rendered React/Shopify app;
`data-testid`s and the modal DOM only exist after JS runs.

## Targets (3 pages)

| Handle | URL | Class it feeds |
| --- | --- | --- |
| `manifesta-black-onyx` | https://www.timepieces-d2c.example/products/manifesta-black-onyx | `ManifestaBlackOnyx` |
| `manifesta-blue-aventurine` | https://www.timepieces-d2c.example/products/manifesta-blue-aventurine | `ManifestaBlueAventurine` |
| `manifesta-mother-of-pearl` | https://www.timepieces-d2c.example/products/manifesta-mother-of-pearl | `ManifestaMotherOfPearl` |

All three extend `SharedTemplate_Manifesta`. Manifesta is the DIVERGENT family —
it does NOT have loom/lume, and it may not be fully released yet.

## ⚠️ STEP 0 — is the PDP even live?

On the timepieces collection page, **Manifesta is shown as a LOCKED upcoming drop
("SEP, 2026")**. Before scraping, for EACH of the three URLs report:
- Does the PDP load normally, redirect (e.g. to `/new-release/manifesta`), show a
  "locked / coming soon / notify me" gate, or 404?
- If it is gated/not a normal PDP, **capture what IS shown** (the gate: countdown,
  "Notify me" button, any email-capture field + their selectors) and STOP the
  detailed PDP checklist for that page — note it as "not-yet-live".
- If it loads as a normal PDP, proceed with the checklist below.

## Manifesta-specific deltas to confirm (vs the mapped families)

1. **Lume / loom: expected ABSENT.** Confirm there is NO
   `button[aria-label='Show lume view']` (the class docs say Manifesta has no loom).
2. **3D viewer: UNKNOWN — confirm.** Auriqua turned out to have no 3D despite the
   old prose, so verify on the live page: is `button[aria-label='Open 3D View']` +
   `canvas[aria-label='…']` present? If present, capture the exact canvas
   `aria-label` value (expected `Manifesta`).
3. **Video:** does the page have autoplay background videos (`video.object-cover`),
   or is it images only? (Manifesta has "semi-precious stone dials" — note anything
   dial-specific.)

## Context — otherwise the KNOWN shape

Expect the rest to match the mapped families (Astonia/Monarch/Arvion/Astonia
Sports/Auriqua):

- Product title: `h1` (price span nested). Price: `div.product-price span`.
- Image gallery: `div.desktop-image-gallery` / `div.product-media-carousel`.
- Primary CTA in `.desktop-sticky-sidebar` — report label ("Register Interest" /
  "Interest submitted" / "Notify me" / "Add to Bag") + enabled/disabled + any
  allocation/stock/drop text.
- Wishlist: `.desktop-sticky-sidebar button[title='Add to wishlist']`.
- Sibling-variant nav: `div.hidden.md:flex.gap-3.flex-wrap`, one `a` per variant.
- Spec accordion: `button[aria-controls^='spec-content-']`.
- Owners modal: "See all {Family} owners" → `div.fixed…z-[101]` + variant filters
  + `input[placeholder='Search owners...']`. (May be absent if not yet released —
  report present/absent + exact trigger text.)
- Book Consultation: `a[href*='/consultation']`.

## Must-capture per variant (if live)

1. **Exact page `<title>`** (for `PAGE_TITLE`).
2. **Price** (₹).
3. **Lume absent? / 3D present? + canvas aria-label / video present?** (the deltas).
4. **Primary CTA** label + enabled/disabled + drop/stock text.
5. **Sibling nav** count + variant names/handles.
6. **Owners modal** present? + trigger text.

### Raw inventory
- Full `data-testid` inventory (value + purpose).
- NO-`data-testid` elements + fallback selector used (with tier) for `ROLLOUT-GAP`s.

## Output format (one file per page)

Return three Markdown files: `manifesta-black-onyx.md`,
`manifesta-blue-aventurine.md`, `manifesta-mother-of-pearl.md`, using the SAME
section structure as the earlier artifacts in `docs/scrape-artifacts/`, but LEAD
with a **"Liveness"** section answering STEP 0 (live / gated / redirect / 404).
Drop the three files back and I'll implement `SharedTemplate_Manifesta` + the
three variants — adapting to whatever the pages actually are (full PDP, or a
drop-gate mapping if not yet live).
