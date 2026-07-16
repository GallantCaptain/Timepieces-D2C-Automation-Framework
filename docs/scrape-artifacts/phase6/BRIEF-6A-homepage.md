# Antigravity Scrape Brief — Phase 6, Chunk 6A: Content-rich Homepage

**For:** the Antigravity browser agent (drives real Chrome, executes JS).

**Goal:** complete the HOMEPAGE mapping (`https://www.timepieces-d2c.example/`,
`HomePage.java`). Only the HERO is mapped today (`home-main`, `home-hero`,
`home-hero-overlay`, `home-hero-mute-toggle`, `home-hero-discover-cta` — all
real testids, please RE-CONFIRM they are live). Everything below the hero is
unmapped (TASK-PDP-007 / DEBT-008). Run LOGGED OUT (guest view). Locator
ladder: `data-testid` → `id` → aria/role → text-anchor → semantic class →
structural; record the tier + whether a `data-testid` exists.

## 1. Full section sweep (top → bottom)
Scroll the entire homepage and, for EVERY section below the hero, capture:
- The section container selector + its heading/label.
- The section's content unit (card/tile/link) selector + inner structure
  (image/title/price/link target).
- Any carousel controls (prev/next arrows, dots, auto-scroll) per section.
Expected sections (confirm the real list + order; report any I missed):
featured collections, upcoming-drop promo(s), timepiece family showcases,
accessories, Timepieces Wall / community teaser, Friends & Partners teaser,
updates/news teaser, brand story, Timepieces World teaser.

## 2. Modals & overlays
- **Stories modal** (header `header-stories-toggle` opens it): the modal
  container, the story tiles/progression controls, close control, and whether
  stories auto-advance.
- The **drop card popup** (`astonia-popup-card` etc.) — already componentized;
  just CONFIRM the testids and note which family's card currently serves.
- Any OTHER homepage modal/overlay (newsletter, cookie/consent, region picker)
  — container + trigger + close.

## 3. Carousels
For each carousel found in the sweep: container, slide/item selector, active
state encoding, prev/next/dot controls (+ aria-labels), and whether it
auto-plays (interval, and what changes in the DOM per tick).

## 4. In-page navigation CTAs
- Every section-level CTA ("View all", "Discover", "Shop now", …): selector +
  exact label + target URL.
- The hero `home-hero-discover-cta` target (currently → the Astonia drop page —
  confirm).

## Raw inventory
- FULL `data-testid` inventory of the homepage (body, not just header/footer).
- Consolidated list of NO-`data-testid` elements + the fallback selector used
  (with tier) to flag `ROLLOUT-GAP`s.

## Out of scope (separate auth pass later)
The LOGGED-IN sidebar user-container children (DEBT-008's second half) need an
authenticated session — do NOT attempt login in this run.

## Output format
Return 2 Markdown files: `homepage-sections.md` (sweep + CTAs + carousels) and
`homepage-modals.md` (stories modal + popups/overlays). Drop them back and I'll
complete `HomePage` (+ carousel/story components as needed).
