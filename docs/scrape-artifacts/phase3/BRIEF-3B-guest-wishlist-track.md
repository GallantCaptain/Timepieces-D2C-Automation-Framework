# Antigravity Scrape Brief — Phase 3, Chunk 3B (guest half): Wishlist + Track Order

**For:** the Antigravity browser agent (drives real Chrome, executes JS).

**Goal:** map the two account-adjacent pages that do NOT require login. Run this
in a NORMAL (logged-out) session — no Cloudflare/OTP needed. Locator ladder:
`data-testid` → `id` → aria/role → text-anchor → semantic class → structural;
record the tier + whether a `data-testid` exists; report only the live DOM.

| # | URL | Class | Chrome |
| --- | --- | --- | --- |
| 1 | https://www.timepieces-d2c.example/wishlist (confirm — may be `/account/wishlist`) | `WishlistPage` | header + footer |
| 2 | https://www.timepieces-d2c.example/track-order | `TrackOrdersPage` | header (+footer?) |

## 1. WishlistPage — guests CAN use it
The wishlist is global and works for guests (accessed via the
`SidebarMenuComponent` "Wishlist"). It starts EMPTY, so to map the item-card
structure you must **first add items**:
1. Confirm the wishlist URL (visit the sidebar "Wishlist" entry — is it
   `/wishlist` or `/account/wishlist`? report the real one).
2. Capture the **EMPTY-state**: the empty-wishlist message/selector + any
   "browse products" CTA.
3. Open 2 different PDPs (e.g. a timepiece + an accessory) and click the
   **wishlist heart/toggle** on each to add them. Note the heart toggle's
   selector + its active/inactive states, and whether the header wishlist
   counter increments.
4. Go back to the wishlist page and capture the **POPULATED-state**:
   - The wishlist **item-card** selector; how product **name / image / price /
     stock status** are structured inside a card.
   - The **remove-from-wishlist** control (selector + label/aria).
   - Any **add-to-cart / move-to-cart** button on the card.
   - The card's link to its PDP (`a[href*='/products/']` or `/accessories/`).
   - The **on-page wishlist count** vs the **header counter** (confirm they match;
     give both selectors — the header one likely lives in `WishlistIconComponent`).
5. Remove one item and confirm the counter decrements (note any confirm dialog).

## 2. TrackOrdersPage (`/track-order`) — guest order tracking
A guest enters an order number to see delivery status. The scaffold ALREADY has
these `data-testid` locators — CONFIRM they are still live and correct:
`order-details-card`, `order-number`, `order-date`. Capture:
- Page `<title>` + main heading.
- The **entry form** shown before a number is submitted: the order-number
  **input** selector (+ placeholder), the **Track** / submit button selector +
  label.
- What happens on an **invalid / unknown** order number (error message selector).
- If you can reach a result view, confirm the result locators:
  `order-details-card`, `order-number` (has "Timepieces" prefix?), `order-date`,
  the **status badge**, and the **Contact Support** button (→ ContactPage).
- Note whether the page has a footer (scaffold extends the header+footer base but
  its Javadoc says header-only — report which is actually true).

## Raw inventory (per page)
- Full `data-testid` inventory.
- Consolidated list of NO-`data-testid` elements + the fallback selector used
  (with tier) to flag `ROLLOUT-GAP`s.

## Output format
Return 2 Markdown files: `wishlist.md`, `track-order.md`, each with the captures
above + the raw inventory. Drop them back and I'll implement `WishlistPage`
(unblocks the `WishlistSteps` PendingException glue) + `TrackOrdersPage`. The
remaining Orders + Account-nav pages need the AUTHENTICATED brief
(`BRIEF-3B-auth-orders-account.md`).
