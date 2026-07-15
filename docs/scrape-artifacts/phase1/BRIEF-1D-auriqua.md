# Antigravity Scrape Brief — Phase 1, Chunk 1D: Auriqua timepiece PDPs

**For:** the Antigravity browser agent (drives real Chrome, executes JS).
**Why a real browser:** the storefront is a client-rendered React/Shopify app;
`data-testid`s and the 3D/modal DOM only exist after JS runs.

## Targets (3 pages, PUBLIC — no login required)

| Handle | URL | Class it feeds |
| --- | --- | --- |
| `auriqua-noir-rose` | https://www.timepieces-d2c.example/products/auriqua-noir-rose | `AuriquaNoirRose` |
| `auriqua-ocean-blue` | https://www.timepieces-d2c.example/products/auriqua-ocean-blue | `AuriquaOceanBlue` |
| `auriqua-racing-green` | https://www.timepieces-d2c.example/products/auriqua-racing-green | `AuriquaRacingGreen` |

All three extend `SharedTemplate_Auriqua`. Common → shared template; per-variant
differences called out below.

## Confirm the lume + 3D selectors (Auriqua HAS loom)

Per the product owner, **Auriqua DOES have loom** (only Manifesta lacks it), so
Auriqua is expected to be the same lume+3D shape as Arvion/Astonia Sports. Just
**confirm the selectors match** on all three live pages:
1. **Lume / light-night toggle** — expected `button[aria-label='Show lume view']`.
   Confirm present + exact selector.
2. **3D viewer** — `button[aria-label='Open 3D View']` + `canvas[aria-label='…']`.
   Confirm present + the exact canvas `aria-label` value (expected `Auriqua`).

## Context — a KNOWN template shape

Expect the same structure as Astonia/Monarch/Arvion/Astonia Sports (all mapped):

- Product title: `h1` (price span nested inside the `h1`).
- Price: `div.product-price span`.
- Image gallery: `div.desktop-image-gallery` (desktop) / `div.product-media-carousel` (mobile).
- Primary CTA in `.desktop-sticky-sidebar` — report the label
  ("Register Interest" vs "Interest submitted" vs "Add to Bag") + **enabled/disabled**
  + any allocation/stock text.
- Wishlist: `.desktop-sticky-sidebar button[title='Add to wishlist']`.
- 3D viewer: see above.
- Sibling-variant nav: `div.hidden.md:flex.gap-3.flex-wrap`, one `a` per variant
  (href = `/products/<handle>`).
- Spec accordion: `button[aria-controls^='spec-content-']` (Case/Strap/Dial/Movement).
- Owners modal: "See all {Family} owners" → `div.fixed…z-[101]` + variant-filter
  buttons + `input[placeholder='Search owners...']`, close `button[aria-label='Close']`.
  (Reuses `PdpOwnersModalComponent`; note exact trigger text, e.g. "See all Auriqua owners".)
- Book Consultation: `a[href*='/consultation']`.
- Background videos: `video.object-cover` (autoplay/muted/loop, no controls).

## Must-capture per variant

1. **Exact page `<title>`** (for `PAGE_TITLE`) — all three handles.
2. **Price** (₹) per variant.
3. **Confirm the lume selector + 3D canvas aria-label** (Auriqua has loom).
4. **Primary CTA** label + enabled/disabled + allocation text.
5. **Sibling nav**: confirm count (expected 3) + variant names/handles.
6. **Owners modal**: confirm present; exact trigger text + variant-filter labels.

### Report any other DELTA vs the known shape
Extra/missing sections, floating CTA present or not, band-color/size selector,
volume/unmute controls, scroll-to-section nav, different gallery/price/title
selectors, etc.

### Raw inventory
- Full `data-testid` inventory (value + one-line purpose).
- Consolidated list of NO-`data-testid` elements + fallback selector used (with
  tier) to flag `ROLLOUT-GAP`s.

## Output format (one file per page)

Return three Markdown files: `auriqua-noir-rose.md`, `auriqua-ocean-blue.md`,
`auriqua-racing-green.md`, using the SAME section structure as the earlier
artifacts in `docs/scrape-artifacts/` (Core → Video/Audio → **Lume (present?)** →
3D viewer → Sticky/CTA → Sibling nav → Spec accordion → Owners modal → other →
Raw data-testid inventory → NO-data-testid). Drop the three files back and I'll
implement `SharedTemplate_Auriqua` + the three variants (reusing
`PdpOwnersModalComponent`).
