# Antigravity Scrape Brief — Phase 2 DISCOVERY: accessory PDP template

**For:** the Antigravity browser agent (drives real Chrome, executes JS).
**Why a real browser:** the storefront is a client-rendered React/Shopify app;
`data-testid`s, drawers, and carousels only exist after JS runs.

**Goal:** the accessory PDPs are a NEW template we have not mapped yet (different
from timepiece PDPs — no lume/3D/owners/register-interest; instead accordions,
a card-button variant switcher, Return/Guarantee side drawers, a bottom model
carousel, and — straps only — a dial-try carousel). This is a **discovery**
scrape across a representative of each accessory type so we can build the shared
templates. URL pattern is `/accessories/<type>/<handle>` (NOT `/products/…`).

## Targets (5 representatives, PUBLIC — no login)

| # | URL | Represents |
| --- | --- | --- |
| 1 | https://www.timepieces-d2c.example/accessories/watch-case/premium-watch-case | Watch Case (single product) |
| 2 | https://www.timepieces-d2c.example/accessories/clasp/butterfly-clasp-gold-18mm | Butterfly Clasp (single-size 18mm) |
| 3 | https://www.timepieces-d2c.example/accessories/clasp/single-deployment-clasp-rose-gold-20mm | Single-Deployment Clasp **with 20/22mm size toggle** |
| 4 | https://www.timepieces-d2c.example/accessories/strap/fkm-rubber-strap-black | Rubber Strap (+ dial-try carousel) |
| 5 | https://www.timepieces-d2c.example/accessories/strap/ostrich-leather-strap-black | Leather Strap (confirm strap template) |

## Locator ladder

`data-testid` → `id` → aria/alt/name/role → text-anchor → semantic/utility class
→ structural. Record the tier + whether a `data-testid` exists. **Report only the
live DOM — don't invent selectors.**

## Per-page capture checklist

### A. Core identity + purchase
- Page `<title>` (exact) and product title element.
- Price (₹) + selector. (Accessories are purchasable — e.g. straps ₹6,500.)
- Compatibility caption ("For Astonia", etc.) selector.
- **Primary CTA** — is it "Add to Bag"/"Add to Cart" (enabled, purchasable) or
  "Register Interest" or something else? Selector + exact label + enabled/disabled.
- **Quantity selector** (if any) — selector + how it increments.
- Wishlist button — selector + toggle behavior.
- Image gallery container — selector + image count.

### B. Card-button variant switcher  ⚠️ KEY for the Rose Gold decision
- The feature/category card-buttons that switch variants by **color** (and for
  target 3, by **size 20mm/22mm**) — container + per-card selectors.
- **CRITICAL (target 3):** when you click the **22mm** card-button, does the URL
  change to `/accessories/clasp/single-deployment-clasp-rose-gold-22mm` (i.e. it
  NAVIGATES to a distinct PDP), or does it stay on the same URL and just update
  in place (client-side state)? Report exactly which — this decides whether we
  model 20mm/22mm as two classes or one.
- For target 2 (butterfly, single-size) and target 1 (watch case, "feature not
  yet implemented"): note whether the switcher is present/disabled and what the
  color cards link to.

### C. Accordion panels
- Product Details accordion (collapsed by default) + any other accordions —
  container + toggle selectors + section titles.

### D. Return & Refund / Guarantee side drawers
- The "Return & Refund" and "Guarantee" links — selectors.
- On click each opens a side drawer — capture the drawer container selector, the
  close 'X' selector, and confirm it also closes on outside-click.

### E. Bottom model carousel (all types)
- The bottom scrollable carousel of all models/variants — container + card
  selectors; confirm each card links to `/accessories/<type>/<handle>`.

### F. Dial-try carousel  (STRAPS ONLY — targets 4 & 5)
- The carousel that lets the user drag watch **dials** across the strap to preview
  combinations — container + dial-item selectors + the drag track. Note which
  dials are shown (should be only compatible ones).

### G. Anything else + raw inventory
- Any other notable interactive elements.
- Full `data-testid` inventory (value + one-line purpose).
- Consolidated list of NO-`data-testid` elements + fallback selector used (with
  tier) to flag `ROLLOUT-GAP`s.

## Output format (one file per page)

Return five Markdown files named after the handles
(`premium-watch-case.md`, `butterfly-clasp-gold-18mm.md`,
`single-deployment-clasp-rose-gold-20mm.md`, `fkm-rubber-strap-black.md`,
`ostrich-leather-strap-black.md`), each with sections A–G above. Drop them back
and I'll design the accessory shared templates (watch-case / clasp / strap) from
what's actually shared vs type-specific, resolve the Rose Gold 20/22mm modelling
(TASK-PDP-003), and start implementing Phase 2 chunk by chunk.
