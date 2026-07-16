# Antigravity Scrape Brief — Phase 4, Chunk 4A: Timepieces World (world.timepieces-d2c.example)

**For:** the Antigravity browser agent (drives real Chrome, executes JS).

**Goal:** map the Timepieces World subdomain — the LAST unmapped page-object group
(discovered after the www plan completed): 6 empty page scaffolds + 2 empty
component scaffolds under `pages/web/otherPages/timepieces_world/`. All PUBLIC, no
login. Locator ladder: `data-testid` → `id` → aria/role → text-anchor →
semantic class → structural; record the tier + whether a `data-testid` exists;
report only the live DOM.

| # | URL | Class | Notes |
| --- | --- | --- | --- |
| 1 | https://world.timepieces-d2c.example/ | `TimepiecesWorld_ExperiencePage` | scroll-canvas experience |
| 2 | https://world.timepieces-d2c.example/products/astonia (representative) | `TimepiecesWorld_AstoniaPage` (+ Arvion/Monarch/Auriqua/Manifesta) | check shared template |
| — | (both pages) | `TimepiecesWorld_HeaderComponent`, `TimepiecesWorld_SidebarMenuComponent` | RW-specific chrome |

## 1. TimepiecesWorld_ExperiencePage (`https://world.timepieces-d2c.example/`)
A scroll-driven image-sequence canvas (images load forward/backward with scroll,
like a scrubbed video; speed non-controllable, only nudged). Sections in order:
**Arvion → Monarch → Auriqua → Astonia → Manifesta**, then a final section with
ALL products as cards in a **static carousel**. Capture:
- Page `<title>`; the main canvas/section container selector(s).
- How one product **section** is delimited (per-family wrapper/heading) + the
  family name element.
- The **"Discover" CTA** per section: selector + exact label + target URL
  (confirm `/products/<family>` on the world subdomain).
- The **side progress bars** (non-interactable): container + per-section
  segment selector; how "active/progress" is encoded (class/style/width).
- The final **product-card carousel**: container, card selector, how the card's
  family name/image/link are structured; any prev/next controls.
- Scroll behavior notes: what actually changes in the DOM when scrolling
  (canvas frames? transform? image src swaps?) — enough for us to assert
  "the canvas advanced".

## 2. Product showcase page (representative: `/products/astonia`)
Visit https://world.timepieces-d2c.example/products/astonia in full, then QUICKLY spot-check
the other four (`/products/arvion`, `/products/monarch`, `/products/auriqua`,
`/products/manifesta`) and give a **shared-template verdict** (same structure,
family-specific content?). Capture for the representative:
- Page `<title>` pattern (family name included?).
- The hero/showcase structure: family name heading, imagery/canvas, any
  scroll-sequence like the experience page.
- Any **CTA back to the www storefront PDP** (e.g. "Buy"/"Explore" →
  `www.timepieces-d2c.example/products/<handle>`) — selector + target.
- Content sections (specs, story, gallery) — enough to model one
  `SharedTemplate_TimepiecesWorldProduct` if the 5 match.

## 3. TimepiecesWorld_HeaderComponent (on BOTH pages)
- Full header inventory (testids?).
- The **Timepieces logo dual navigation**: on the EXPERIENCE page the logo →
  `https://www.timepieces-d2c.example`; on a PRODUCT showcase page the logo →
  `https://world.timepieces-d2c.example` (back to the experience). CONFIRM this and capture
  the logo selector + its `href` in each context.
- Any menu/hamburger trigger for the RW sidebar.

## 4. TimepiecesWorld_SidebarMenuComponent
- Open the RW sidebar/menu: trigger selector, panel selector, close control.
- Every entry: label + href (do they point to world.timepieces-d2c.example pages, www
  pages, or both?).

## Raw inventory (per page)
- Full `data-testid` inventory.
- Consolidated list of NO-`data-testid` elements + the fallback selector used
  (with tier) to flag `ROLLOUT-GAP`s.

## Output format
Return 3 Markdown files: `rw-experience.md`, `rw-product-showcase.md` (with the
shared-template verdict + per-family title/URL list), `rw-chrome.md` (header +
sidebar). Drop them back and I'll implement the 6 pages + 2 components —
IMPLEMENTATION NOTE (for me): these pages live on a different origin, so the
page objects will override `getBaseUrl()` to `https://world.timepieces-d2c.example` instead
of the config `base.url`.
