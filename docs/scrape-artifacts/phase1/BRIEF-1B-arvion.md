# Antigravity Scrape Brief — Phase 1, Chunk 1B: Arvion timepiece PDPs

**For:** the Antigravity browser agent (drives real Chrome, executes JS).
**Why a real browser:** the storefront is a client-rendered React/Shopify app;
`data-testid`s and the 3D/modal DOM only exist after JS runs, so a raw HTML fetch
won't see them.

## Targets (3 pages, PUBLIC — no login required)

| Handle | URL | Class it feeds |
| --- | --- | --- |
| `arvion-burgundy-gold` | https://www.timepieces-d2c.example/products/arvion-burgundy-gold | `ArvionBurgundyGold` |
| `arvion-espresso-silver` | https://www.timepieces-d2c.example/products/arvion-espresso-silver | `ArvionEspressoSilver` |
| `arvion-navy-silver` | https://www.timepieces-d2c.example/products/arvion-navy-silver | `ArvionNavySilver` |

All three extend `SharedTemplate_Arvion`. Everything common goes in the shared
template; per-variant differences are called out below.

## Context — this is a KNOWN template shape

Arvion is in the same loom + 3D group as Monarch and Astonia (already mapped).
Expect the **same structure** as those PDPs; your job is mainly to **confirm the
shape holds** and to capture the **per-variant specifics** and **any deltas**.
For reference, the confirmed shape on Monarch was:

- Product title: `h1` (price span nested inside the `h1`).
- Price: `div.product-price span`.
- Image gallery: `div.desktop-image-gallery` (desktop) / `div.product-media-carousel` (mobile).
- Primary CTA in `.desktop-sticky-sidebar` — Monarch showed **"Register Interest"
  ENABLED** even at 100/100 allocated; Astonia showed **"Interest submitted"
  DISABLED**. Report which Arvion uses and whether it is enabled/disabled.
- Wishlist: `.desktop-sticky-sidebar button[title='Add to wishlist']` → toggles to
  "Remove from wishlist".
- Lume toggle: `button[aria-label='Show lume view']`.
- 3D viewer: `button[aria-label='Open 3D View']`, canvas `canvas[aria-label='…']`
  (the aria-label was the family name, e.g. `Monarch`), close
  `button[aria-label='Close 3D view']`, container `div.fixed.inset-0.z-[9999]`.
- Sibling-variant nav: `div.hidden.md:flex.gap-3.flex-wrap` with one `a` per
  variant (href = `/products/<handle>`).
- Spec accordion: `button[aria-controls^='spec-content-']`
  (sections Case / Strap / Dial / Movement).
- Owners modal: "See all {Family} owners" → modal
  (`div.fixed…z-[101]`) with variant-filter buttons + `input[placeholder='Search owners...']`,
  close `button[aria-label='Close']`. (Already mapped as the shared
  `PdpOwnersModalComponent` — just confirm it exists and matches for Arvion.)
- Book Consultation: `a[href*='/consultation']`.
- Background videos: `video.object-cover` (autoplay/muted/loop, no controls).

## What to capture (per page) — use the locator ladder

Locator priority: `data-testid` → `id` → aria/alt/name/role → text-anchor →
semantic/utility class → structural. Record the tier used and whether a
`data-testid` exists. **Do NOT invent selectors — report only the live DOM.**

### Must-capture per variant
1. **Exact page `<title>`** (needed for `PAGE_TITLE`) — for all three handles.
2. **Price** text (₹) per variant.
3. **Primary CTA** label + **enabled/disabled** state (Register Interest vs
   Interest submitted vs Add to Bag) + any allocation/stock text.
4. **3D canvas `aria-label`** value (expected `Arvion` — confirm).
5. **Sibling nav**: confirm **3** cards and the exact variant names + handles they
   link to.
6. **Owners modal**: confirm present; note the trigger text ("See all Arvion
   owners"?) and the variant-filter option labels.
7. **Lume toggle** present? (expected yes.)

### Report any DELTA vs the Monarch shape above
Anything Arvion does differently — extra/missing sections, a floating CTA or not,
band-color/size selector (Monarch had none), volume/unmute controls, a
scroll-to-section nav, different gallery/price/title selectors, etc.

### Raw inventory
- Full `data-testid` inventory (attribute value + one-line purpose).
- Consolidated list of elements with **NO** `data-testid` + the fallback selector
  used (with tier), so we can flag `ROLLOUT-GAP`s.

## Output format (one file per page)

Return three Markdown files named `arvion-burgundy-gold.md`,
`arvion-espresso-silver.md`, `arvion-navy-silver.md`, using the SAME section
structure as the Phase 0/1 artifacts in `docs/scrape-artifacts/` (Core elements →
Video/Audio → Lume → 3D viewer → Sticky/CTA → Sibling nav → Specification
accordion → Owners modal → other → Raw data-testid inventory → Elements with NO
data-testid). Drop the three files back and I'll implement `SharedTemplate_Arvion`
+ the three variants (reusing `PdpOwnersModalComponent`).
