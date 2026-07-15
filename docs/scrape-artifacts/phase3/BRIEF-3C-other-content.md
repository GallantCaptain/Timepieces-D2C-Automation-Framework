# Antigravity Scrape Brief — Phase 3, Chunk 3C: Other content pages

**For:** the Antigravity browser agent (drives real Chrome, executes JS).

**Goal:** map 4 distinct public content pages (they do NOT share a template).
URLs are known from the scaffolds. Locator ladder: `data-testid` → `id` →
aria/role → text-anchor → semantic class → structural; record the tier + whether a
`data-testid` exists; report only the live DOM.

| # | URL | Class | Chrome |
| --- | --- | --- | --- |
| 1 | https://www.timepieces-d2c.example/timepieces-wall | `TimepiecesWallPage` | header only (no footer) |
| 2 | https://www.timepieces-d2c.example/timepieces-wall/share | `ShareYourMomentPage` | header only (no footer) |
| 3 | https://www.timepieces-d2c.example/sitemap | `SiteMapPage` | header + footer |
| 4 | https://www.timepieces-d2c.example/updates | `UpdatesPage` | header + footer |

## 1. TimepiecesWallPage (`/timepieces-wall`)
An infinite-scroll canvas of customer testimonials/photos in polaroid-style
cards (some photos have no card if shared without a testimonial), over a rotating
black globe animation. Capture:
- Page `<title>`; the wall/canvas container selector.
- Testimonial "polaroid" card selector; photo-only item selector; how a card's
  image + testimonial text are structured.
- **Music on/off toggle** button selector (aria-label?) + its two states.
- The rotating-globe animation element (canvas/svg/div) selector.
- Does it lazy-load more on scroll? Note the load trigger if visible.

## 2. ShareYourMomentPage (`/timepieces-wall/share`)
A page to submit a moment — **gated**: the submit button checks login, then
whether the user is a registered owner. Capture (VIEW only — do NOT submit / log in):
- Page `<title>`; the "Submit your moment" button selector + exact label.
- On clicking it while logged-out, what happens (redirect to login / inline
  message / modal)? Report the gate behavior + any message selector.
- The "go to the Timepieces Wall" link selector.
- The side/top **carousel** of Timepieces-watch photos: container + item + any
  prev/next controls.
- Any upload/form fields that are visible before the gate (likely none until
  authenticated — confirm).

## 3. SiteMapPage (`/sitemap`)
A crawler-oriented index of all site links. Capture:
- Page `<title>` + main heading.
- The link-group/section structure (columns/sections + their headings).
- The link-item selector; roughly how many links + a few example hrefs
  (confirm they cover products/collections/policies/etc.).

## 4. UpdatesPage (`/updates`)
News/announcements, filterable. Capture:
- Page `<title>` + main heading.
- **Content-filter buttons** ("All", "Drops", …) — container + button selectors +
  the full list of filter labels.
- The news/announcement **card** selector; how title/date/excerpt are structured.
- Any pagination / "load more" control.

## Raw inventory (per page)
- Full `data-testid` inventory.
- Consolidated list of NO-`data-testid` elements + the fallback selector used
  (with tier) to flag `ROLLOUT-GAP`s.

## Output format
Return 4 Markdown files: `timepieces-wall.md`, `share-your-moment.md`, `sitemap.md`,
`updates.md`, each with the per-page captures above + the raw inventory. Drop them
back and I'll implement the 4 page objects, then move to 3D (Friends & Partners).
