# Antigravity Scrape Brief — Phase 1, Chunk 1A: Monarch timepiece PDPs

**For:** the Antigravity browser agent (drives real Chrome, executes JS).
**Why a real browser:** the storefront is a client-rendered React/Shopify app;
`data-testid`s and the AR/3D DOM only exist after JS runs, so a raw HTML fetch
won't see them.

## Targets (2 pages, PUBLIC — no login required)

| Handle | URL | Class it feeds |
| --- | --- | --- |
| `monarch-rose-gold` | https://www.timepieces-d2c.example/products/monarch-rose-gold | `MonarchRoseGold` |
| `monarch-silver-black` | https://www.timepieces-d2c.example/products/monarch-silver-black | `MonarchSilverBlack` |

Both extend `SharedTemplate_Monarch`. Everything common goes in the shared
template; per-variant differences are called out below.

## How to capture (locator priority ladder)

For every element, record the **best** locator following this order and note
which tier you used:
1. `data-testid` (always prefer — record the exact attribute value)
2. `id`
3. aria-label / alt / name / role
4. text-anchor (stable visible text)
5. semantic/utility class
6. structural / positional (last resort)

Also record, per element: the tier used, whether a `data-testid` exists
(yes/no), and any observed text/behavior. **Do NOT invent selectors — only
report what is actually in the live DOM.**

## Per-page capture checklist

### A. Core PDP identity
- Page `<title>` (exact string — needed for `PAGE_TITLE`).
- Product title element + observed text.
- Price element + observed text (₹ amount).
- Image gallery container; how many images/thumbnails.
- Primary CTA (Add to Cart / Add to Bag): selector, **enabled or disabled**, and
  its exact label (e.g. "Add to Bag" vs "Interest submitted" if sold out / 100%
  allocated). Note stock/allocation text if shown.
- Wishlist button: selector + what changes on click (title/class/icon swap).

### B. Video + audio controls  *(Monarch emphasizes this — capture carefully)*
- `<video>` element selector(s); autoplay? muted by default?
- Volume / **unmute** control selector + aria-label (Silver Black documents
  per-video volume controls).

### C. Lume / light-night toggle  *(discrepancy to resolve)*
- Astonia's docs claim Monarch has a "lume"/light-night image toggle, but the
  Monarch page docs don't mention it. **Confirm on the live page:** is there a
  lume/night toggle button (e.g. `aria-label='Show lume view'` or similar)?
  Report present/absent + exact selector if present.

### D. AR viewer  *(Monarch-specific — replaces Astonia's 3D canvas?)*
- Is there an "AR" / "View in AR" / "3D" button in the sticky add-to-bag area?
  Selector + exact label.
- On click: does it open an **embedded iframe** (THREE.js), a `<canvas>`, redirect
  to another page, or do nothing / show "coming soon"? (Docs say AR is "not
  implemented yet / not currently working" — record the ACTUAL behavior.)
- If a viewer opens, capture: the iframe/canvas selector, fullscreen icon,
  close ('x') button, share button, and the dimming/backdrop modal selector.

### E. Sticky add-to-bag section + band-color selector  *(Silver Black)*
- Sticky add-to-bag container selector.
- **Band-color selector**: selector for the swatch group + individual swatches;
  how many colors; how a selection is indicated (aria-selected/class).

### F. Floating "Add to Bag" button  *(Silver Black)*
- The floating CTA that appears on scroll-down: selector; does clicking it scroll
  back up to the sticky add-to-bag? Confirm behavior.

### G. Sibling-variant navigation
- Container selector (desktop + mobile if different).
- Number of sibling cards; the variant names/handles each links to (Monarch has
  2 variants: Rose Gold, Silver Black — confirm).

### H. Specification / details accordion
- Container + per-section toggle selectors; list the section titles and their
  `aria-controls` values (Astonia used `spec-content-Case/Strap/Dial/Movement`).

### I. Navigation chrome  *(Rose Gold mentions side + top nav)*
- Any in-page side-navigation or top scroll-to-section nav bar: selectors +
  target section ids/anchors.

### J. Filter & sort toggle  *(SHARED across ALL timepiece PDPs — map ONCE as a component)*
- This toggle appears on every timepiece PDP with the **same function** — it will
  be implemented once as a reusable `PdpFilterSortComponent`, so capture it fully
  here (no need to re-capture on later families).
- Filter/sort toggle button: selector + label/icon.
- On open: drawer, dropdown, or modal? Container selector.
- **What does it filter/sort?** Identify which PDP section it controls — the
  compatible straps/clasps carousel, the owners/reviews list, related products,
  or something else. This is the most important thing to pin down.
- Filter option selectors + labels; sort option selectors + labels; how a
  selection/applied state is indicated.
- `data-testid`s present? The collection PLPs used `collection-filter-*` /
  `accessories-filter-*` — check whether the PDP reuses a similar scheme or a new
  one (e.g. `pdp-filter-*`), and record exact values.
- Confirm it is byte-for-byte the same widget on **both** Monarch pages (same
  selectors, same options) so we know it is safe to share.

### K. Anything else + raw inventory
- Any other notable interactive elements (owners button, FAQ accordion, etc.).
- **Full `data-testid` inventory** on the page (attribute value + one-line purpose).
- A consolidated list of elements with **NO** `data-testid` and the fallback
  selector used for each (with tier), so we can flag `ROLLOUT-GAP`s.

## Output format (one file per page)

Return two Markdown files named `monarch-rose-gold.md` and
`monarch-silver-black.md`, using the SAME section structure as the Phase 0
artifacts in `docs/scrape-artifacts/phase0/` (Core elements → Video/Audio →
Lume → AR viewer → Sticky/Band color → Floating CTA → Sibling nav →
Specification accordion → Navigation → **Filter & Sort** → other → Raw
data-testid inventory → Elements with NO data-testid). Drop both files back and
I'll implement `SharedTemplate_Monarch` + the two variants + the shared
`PdpFilterSortComponent` from them.
