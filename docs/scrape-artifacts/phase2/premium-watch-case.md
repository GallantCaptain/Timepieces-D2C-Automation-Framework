# PDP Scrape: premium-watch-case

URL: https://www.timepieces-d2c.example/accessories/watch-case/premium-watch-case
Represents: Watch Case (single product)

## A. Core identity + purchase
- **Page `<title>`**: `Premium Watch Case, Watch Storage Box - Timepieces` (exact)
- **Product title element**: `h1` containing `"Premium Watch Case"` (Tier: Semantic, data-testid: no)
- **Price (₹)**: `div` containing `"₹4,500"` (Tier: Text-Anchor, data-testid: no)
- **Compatibility caption**: `div` containing `"Suitable for all watch sizes"` (Tier: Text-Anchor, data-testid: no)
- **Primary CTA**: `"Add to cart"` button (or `"Add to Cart"` in stick bar)
  - Desktop selector: `button` with text `"Add to cart"` or `.flex-col button` containing `"Add to cart"` (Tier: Text-Anchor, data-testid: no)
  - Sticky mobile/tablet selector: `button` containing `"Add to Cart"` inside bottom bar (Tier: Text-Anchor, data-testid: no)
  - State: **Enabled** (purchasable)
- **Quantity selector**: None present on page.
- **Wishlist button**: `button[title="Add to wishlist"]` (Tier: Attribute, data-testid: no)
  - Behavior: Toggles SVG state and description to `"Remove from wishlist"`.
- **Image gallery container**: `.desktop-image-gallery` (desktop), `.product-media-carousel` (mobile) (Tier: Utility/Class, data-testid: no)
  - Image count: 4 images (`1_dd389f58...`, `2_5676c36f...`, `3_432355c9...`, `4_0e3b9999...`)

## B. Card-button variant switcher
- **Switcher container**: `.flex.flex-wrap.gap-2` (Tier: Utility/Class, data-testid: no)
- **Switcher buttons**: `button[title="Premium Watch Case"]` (has `aria-pressed="true"`, background-color `rgb(0, 0, 0)`)
- **Behavior / Implementation**: Single color/variant option ("Black"). The switcher button has `aria-pressed="true"`. Not navigatable or togglable.

## C. Accordion panels
- **Accordion container**: `div.w-full.border-t` (nearest enclosing elements, Tier: Utility/Class, data-testid: no)
- **Section headers / Toggle buttons**:
  - Description: `button` containing `"Description"` (expanded by default, has class/text `Description -`)
  - Technical details: `button` containing `"Technical details"` (collapsed by default, has class/text `Technical details +`)

## D. Return & Refund / Guarantee side drawers
- **Drawer triggers**:
  - Guarantee: `button[aria-label="Open guarantee details"]`
  - Return & Refunds: `button[aria-label="Open return and refunds details"]`
- **Drawer container**: `div.fixed.inset-0.z-\[9999\]`
- **Drawer close 'X' selector**: `button[aria-label="Close"]` inside the drawer overlay container
- **Outside-click / Backdrop**: `div.absolute.inset-0.bg-black/50.backdrop-blur-sm` (clicking this successfully closes the drawer).

## E. Bottom model carousel
- **Carousel container**: `div.relative.group` (parent of cards/links, Tier: Utility/Class, data-testid: no)
- **Card selector**: `a` element containing product card details (e.g. `a[href*='/accessories/strap/fkm-rubber-strap-yellow']`).
- **Links path**: All link cards point to `/accessories/<type>/<handle>` (e.g. `/accessories/strap/fkm-rubber-strap-yellow`, `/accessories/strap/ostrich-leather-strap-black`, etc.).

## F. Dial-try carousel
- **Status**: None (not applicable to Watch Cases).

## G. Anything else + raw inventory
- No other notable interactive elements.

### Raw data-testid inventory
- `site-header` (header) — Site header banner
- `header-menu-toggle` (button) — Mobile menu toggle trigger
- `header-stories-toggle` (button) — Open stories modal trigger
- `header-logo` (a) — Home page link
- `header-search-toggle` (button) — Mobile search panel toggle trigger
- `header-account-toggle` (button) — User authentication trigger
- `header-cart-toggle` (button) — User shopping cart drawer trigger
- `site-footer` (footer) — Main footer container
- `footer-mobile` (div) — Footer wrapper on mobile
- `footer-accordion` (div) — Accordion section wrapper in footer
- `footer-accordion-toggle` (button) — Accordion category toggle button
- `footer-accordion-link` (a) — Footer links
- `footer-socials` (div) — Social links container
- `footer-social-link` (a) — Individual social site link
- `country-selector` (div) — Currency/region container
- `country-selector-toggle` (button) — Open currency/region list dropdown
- `footer-legal` (a) — Legal policy link
- `footer-sitemap` (a) — Site map link
- `footer-copyright` (p) — Site copyright text
- `footer-desktop` (div) — Desktop footer container
- `footer-menu-column` (div) — Desktop footer menu column wrapper
- `footer-menu-link` (a) — Desktop footer menu individual link

### Elements with NO data-testid (fallback selectors)
- Product Title — `h1` (semantic tier)
- Price — `div` containing `"₹4,500"` (text-anchor tier)
- Compatibility caption — `div` containing `"Suitable for all watch sizes"` (text-anchor tier)
- Add to cart button — `button` containing `"Add to cart"` (text-anchor tier)
- Wishlist button — `button[title="Add to wishlist"]` (attribute tier)
- Accordion panels — `button` containing accordion section title (text-anchor tier)
- Guarantee trigger — `button[aria-label="Open guarantee details"]` (aria-attribute tier)
- Return & Refund trigger — `button[aria-label="Open return and refunds details"]` (aria-attribute tier)
- Drawer overlay — `div.fixed.inset-0.z-\[9999\]` (utility/class tier)
- Drawer close — `button[aria-label="Close"]` (aria-attribute tier)
- Backdrop — `div.absolute.inset-0.bg-black/50.backdrop-blur-sm` (utility/class tier)
- Bottom recommended products carousel cards — `a` inside bottom carousel container (structural tier)
