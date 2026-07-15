# Antigravity Scrape Brief — Phase 1, Chunk 1C: Astonia Sports timepiece PDPs

**For:** the Antigravity browser agent (drives real Chrome, executes JS).
**Why a real browser:** the storefront is a client-rendered React/Shopify app;
`data-testid`s and the 3D/modal DOM only exist after JS runs.

## Targets (3 pages, PUBLIC — no login required)

| Handle | URL | Class it feeds |
| --- | --- | --- |
| `astonia-sports-circuit-blue` | https://www.timepieces-d2c.example/products/astonia-sports-circuit-blue | `AstoniaSportsCircuitBlue` |
| `astonia-sports-phantom-black` | https://www.timepieces-d2c.example/products/astonia-sports-phantom-black | `AstoniaSportsPhantomBlack` |
| `astonia-sports-podium-green` | https://www.timepieces-d2c.example/products/astonia-sports-podium-green | `AstoniaSportsPodiumGreen` |

All three extend `SharedTemplate_AstoniaSports`. Common → shared template;
per-variant differences called out below.

## Context — this is a KNOWN template shape

Astonia Sports is in the same loom + 3D group as Astonia, Monarch and Arvion
(all mapped). Expect the **same structure**; your job is to **confirm the shape
holds** and capture the **per-variant specifics** + **any deltas**. Confirmed
shape on Monarch/Arvion:

- Product title: `h1` (price span nested inside the `h1`).
- Price: `div.product-price span`.
- Image gallery: `div.desktop-image-gallery` (desktop) / `div.product-media-carousel` (mobile).
- Primary CTA in `.desktop-sticky-sidebar` — Monarch/Arvion showed
  **"Register Interest" ENABLED** even when fully allocated; Astonia showed
  **"Interest submitted" DISABLED**. Report which Astonia Sports uses + enabled/disabled.
- Wishlist: `.desktop-sticky-sidebar button[title='Add to wishlist']`.
- Lume toggle: `button[aria-label='Show lume view']`.
- 3D viewer: `button[aria-label='Open 3D View']`, canvas `canvas[aria-label='…']`
  (family name — **confirm the exact value**, likely `Astonia Sports`), close
  `button[aria-label='Close 3D view']`, container `div.fixed.inset-0.z-[9999]`.
- Sibling-variant nav: `div.hidden.md:flex.gap-3.flex-wrap` with one `a` per
  variant (href = `/products/<handle>`).
- Spec accordion: `button[aria-controls^='spec-content-']` (Case/Strap/Dial/Movement).
- Owners modal: "See all {Family} owners" → `div.fixed…z-[101]` with variant-filter
  buttons + `input[placeholder='Search owners...']`, close `button[aria-label='Close']`.
  (Reuses the shared `PdpOwnersModalComponent` — confirm it matches; note the exact
  trigger text, e.g. "See all Astonia Sports owners".)
- Book Consultation: `a[href*='/consultation']`.
- Background videos: `video.object-cover` (autoplay/muted/loop, no controls).

## What to capture (per page) — use the locator ladder

`data-testid` → `id` → aria/alt/name/role → text-anchor → semantic/utility class
→ structural. Record the tier + whether a `data-testid` exists. **Report only the
live DOM — don't invent selectors.**

### Must-capture per variant
1. **Exact page `<title>`** (for `PAGE_TITLE`) — all three handles.
2. **Price** (₹) per variant.
3. **Primary CTA** label + **enabled/disabled** + any allocation/stock text.
4. **3D canvas `aria-label`** value — confirm exact string.
5. **Sibling nav**: confirm the count (expected 3) + variant names/handles.
6. **Owners modal**: confirm present; exact trigger text + variant-filter labels.
7. **Lume toggle** present? (expected yes.)

### Report any DELTA vs the Monarch/Arvion shape
Extra/missing sections, floating CTA present or not, band-color/size selector,
volume/unmute controls, scroll-to-section nav, different gallery/price/title
selectors, sub-dial/chronograph-specific controls (Astonia Sports is a sport
chronograph — note anything unique), etc.

### Raw inventory
- Full `data-testid` inventory (value + one-line purpose).
- Consolidated list of NO-`data-testid` elements + the fallback selector used
  (with tier) to flag `ROLLOUT-GAP`s.

## Output format (one file per page)

Return three Markdown files: `astonia-sports-circuit-blue.md`,
`astonia-sports-phantom-black.md`, `astonia-sports-podium-green.md`, using the
SAME section structure as the earlier artifacts in `docs/scrape-artifacts/`
(Core → Video/Audio → Lume → 3D viewer → Sticky/CTA → Sibling nav → Spec
accordion → Owners modal → other → Raw data-testid inventory → NO-data-testid).
Drop the three files back and I'll implement `SharedTemplate_AstoniaSports` + the
three variants (reusing `PdpOwnersModalComponent`).
