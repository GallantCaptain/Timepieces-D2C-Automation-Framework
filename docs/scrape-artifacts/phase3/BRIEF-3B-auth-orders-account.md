# Antigravity Scrape Brief — Phase 3, Chunk 3B (auth half): Orders + Account nav

**For:** the Antigravity browser agent (drives real Chrome, executes JS).

> **PRECONDITION — LOGIN REQUIRED.** Run this in an **authenticated** Antigravity
> session. Production login serves a real Cloudflare Turnstile that WebDriver
> cannot solve, so you (the human) solve the Cloudflare challenge live in the
> visible Chrome window and enter the SMS OTP for the pilot number
> **<pilot-phone-redacted>** (India). Once `/account` loads, the session cookies persist, so
> all 3 pages below map in the same session without re-login.

Locator ladder: `data-testid` → `id` → aria/role → text-anchor → semantic class →
structural; record the tier + whether a `data-testid` exists; report only the
live DOM. **Redact any personal data** (real name, address, phone, email) — we
need SELECTORS/structure, not the values; write `<redacted>` for private text.

| # | URL | Class | Chrome |
| --- | --- | --- | --- |
| 1 | https://www.timepieces-d2c.example/account | `AccountPage` (finish nav) | header only |
| 2 | https://www.timepieces-d2c.example/account/orders | `OrderHistoryPage` | header only |
| 3 | https://www.timepieces-d2c.example/account/orders/Timepieces{OrderNo} | `OrderDetailsPage` | header only |

## 1. AccountPage (`/account`) — finish the navigation
The scaffold already maps the profile-image container + incomplete/complete
profile states. ADD the account **navigation**:
- The links to **Profile**, **Order History**, **Owned Timepieces**, **Wishlist**
  (selectors + labels + hrefs).
- The **Logout** button (selector + label).
- The profile summary block: photo, **Name**, **Designation**, brief description
  (selectors only — redact the values).

## 2. OrderHistoryPage (`/account/orders`)
- Page `<title>` + main heading.
- The **EMPTY-state** (if this account has no orders): message + "start shopping"
  CTA selector.
- The **order-card** selector (if any orders exist): how the **final status**,
  **order number** (Timepieces-prefixed), and **order date** are structured.
- Confirm clicking a card navigates to `OrderDetailsPage`
  (`/account/orders/Timepieces{OrderNo}`) — capture the card's link.

## 3. OrderDetailsPage (`/account/orders/Timepieces{OrderNo}`)
Reached by clicking an order card. If this account has no orders, say so and skip
(we'll map it later). If reachable, capture selectors for:
- Order **number**, **date**, **status**, **total**.
- The **order items** list (item card selector + name/qty/price structure).
- **Shipping address** + **billing address** blocks (selectors only — redact).
- The **receipt / invoice** control (present only when delivered).
- The **Contact Support** button (Javadoc flags it as "currently bugged" — note
  its selector + whether clicking it errors / does nothing).
1
## Raw inventory (per page)
- Full `data-testid` inventory.
- Consolidated list of NO-`data-testid` elements + the fallback selector used
  (with tier) to flag `ROLLOUT-GAP`s.

## Output format
Return 3 Markdown files: `account-nav.md`, `order-history.md`,
`order-details.md`, each with the captures above + the raw inventory (personal
data redacted). Drop them back and I'll finish `AccountPage` + implement
`OrderHistoryPage` + `OrderDetailsPage` — which COMPLETES Phase 3 (all incomplete
POM pages mapped except the user-owned Razorpay COM).
