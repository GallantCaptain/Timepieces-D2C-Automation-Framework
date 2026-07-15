# PDP Scrape: single-deployment-clasp-rose-gold-20mm

URL: https://www.timepieces-d2c.example/accessories/clasp/single-deployment-clasp-rose-gold-20mm
Represents: Single-Deployment Clasp with 20/22mm size switcher

## A. Core identity + purchase
- **Page `<title>`**: `Single Deployment Clasp, Rose Gold - Timepieces Watches` (exact)
- **Product title element**: `h1` containing `"Single Deployment Clasp"` (Tier: Semantic, data-testid: no)
- **Price (₹)**: `div` containing `"₹4,500"` (Tier: Text-Anchor, data-testid: no)
- **Compatibility caption**: `div` containing `"Rose Gold, compatible with rubber straps"` (Tier: Text-Anchor, data-testid: no)
- **Primary CTA**: `"Add to cart"` button (or `"Add to Cart"` in sticky bar)
  - Desktop selector: `button` with text `"Add to cart"` or `.flex-col button` containing `"Add to cart"` (Tier: Text-Anchor, data-testid: no)
  - Sticky mobile/tablet selector: `button` containing `"Add to Cart"` inside bottom bar (Tier: Text-Anchor, data-testid: no)
  - State: **Enabled** (purchasable)
- **Quantity selector**: None present on page.
- **Wishlist button**: `button[title="Add to wishlist"]` (Tier: Attribute, data-testid: no)
  - Behavior: Toggles SVG state and description to `"Remove from wishlist"`.
- **Image gallery container**: `.desktop-image-gallery` (desktop), `.product-media-carousel` (mobile) (Tier: Utility/Class, data-testid: no)
  - Image count: 2 images (`Singledeplomentrosegoldcopy...`, `OPEN_SD_ROSE_GOLD...`)

## B. Card-button variant switcher
- **Color Switcher container**: `.flex.flex-wrap.gap-2` (Tier: Utility/Class, data-testid: no)
- **Color Switcher buttons**:
  - Steel: `button[title="Single Deployment Clasp"]` with style `background-color: rgb(163, 163, 163)`
  - Black: `button[title="Single Deployment Clasp"]` with style `background-color: rgb(0, 0, 0)`
  - Rose Gold: `button[title="Single Deployment Clasp"]` with style `background-color: rgb(251, 147, 147)` (selected, has `aria-pressed="true"`)
  - Gold: `button[title="Single Deployment Clasp"]` with style `background-color: rgb(227, 169, 53)`
- **Size Switcher container**: `.flex.flex-wrap.gap-2` or similar under the size header (Tier: Utility/Class, data-testid: no)
- **Size Switcher buttons**:
  - 20mm: `button` containing `"20mm"` (pressed/selected)
  - 22mm: `button` containing `"22mm"`
- **Behavior / Implementation**:
  - **Color**: Clicking a different color button triggers a **navigation to a distinct PDP** (e.g. clicking Black navigates to `/accessories/clasp/single-deployment-clasp-black-20mm`).
  - **Size**: Clicking a different size button triggers a **navigation to a distinct PDP** (e.g. clicking 22mm navigates to `/accessories/clasp/single-deployment-clasp-rose-gold-22mm`).

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
- **Card selector**: `a` element containing product card details (e.g. `a[href*='/accessories/clasp/single-deployment-clasp-black-20mm']`).
- **Links path**: All link cards point to `/accessories/<type>/<handle>` (e.g. `/accessories/clasp/butterfly-clasp-gold-18mm`, etc.).

## F. Dial-try carousel
- **Status**: None (not applicable to clasps).

## G. Anything else + raw inventory
- No other notable interactive elements.

### Raw data-testid inventory
- Same as target 1 (site header/footer, mobile menu toggles, story toggles, legal/sitemap links in footer).

### Elements with NO data-testid (fallback selectors)
- Product Title — `h1` (semantic tier)
- Price — `div` containing `"₹4,500"` (text-anchor tier)
- Compatibility caption — `div` containing `"Rose Gold, compatible with rubber straps"` (text-anchor tier)
- Add to cart button — `button` containing `"Add to cart"` (text-anchor tier)
- Wishlist button — `button[title="Add to wishlist"]` (attribute tier)
- Accordion panels — `button` containing accordion section title (text-anchor tier)
- Guarantee trigger — `button[aria-label="Open guarantee details"]` (aria-attribute tier)
- Return & Refund trigger — `button[aria-label="Open return and refunds details"]` (aria-attribute tier)
- Drawer overlay — `div.fixed.inset-0.z-\[9999\]` (utility/class tier)
- Drawer close — `button[aria-label="Close"]` (aria-attribute tier)
- Backdrop — `div.absolute.inset-0.bg-black/50.backdrop-blur-sm` (utility/class tier)
- Bottom recommended products carousel cards — `a` inside bottom carousel container (structural tier)
