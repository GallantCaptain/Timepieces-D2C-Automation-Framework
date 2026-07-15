# PDP Scrape: ostrich-leather-strap-black

URL: https://www.timepieces-d2c.example/accessories/strap/ostrich-leather-strap-black
Represents: Leather Strap (confirm strap template)

## A. Core identity + purchase
- **Page `<title>`**: `Ostrich Leather Watch Strap, Black - Timepieces Watches` (exact)
- **Product title element**: `h1` containing `"Ostrich Leather Strap - Black"` (Tier: Semantic, data-testid: no)
- **Price (₹)**: `div` containing `"₹8,100"` (Tier: Text-Anchor, data-testid: no)
- **Compatibility caption**: `div` containing `"For Monarch, Manifesta & Arvion"` (Tier: Text-Anchor, data-testid: no)
- **Primary CTA**: `"Add to cart"` button (or `"Add to Cart"` in sticky bar)
  - Desktop selector: `button` with text `"Add to cart"` or `.flex-col button` containing `"Add to cart"` (Tier: Text-Anchor, data-testid: no)
  - Sticky mobile/tablet selector: `button` containing `"Add to Cart"` inside bottom bar (Tier: Text-Anchor, data-testid: no)
  - State: **Enabled** (purchasable)
- **Quantity selector**: None present on page.
- **Wishlist button**: `button[title="Add to wishlist"]` (Tier: Attribute, data-testid: no)
  - Behavior: Toggles SVG state and description to `"Remove from wishlist"`.
- **Image gallery container**: `.desktop-image-gallery` (desktop), `.product-media-carousel` (mobile) (Tier: Utility/Class, data-testid: no)
  - Image count: 2 images (`MONARCHBLACKFRONT...`, `MONACRHBLACKBACK...`)

## B. Card-button variant switcher
- **Color Switcher container**: `.flex.flex-wrap.gap-2` or similar color container (Tier: Utility/Class, data-testid: no)
- **Color Switcher buttons**:
  - Black: `button[title="Ostrich Leather Strap - Black"]` (pressed, has `aria-pressed="true"`)
  - Brown: `button[title="Ostrich Leather Strap - Brown"]`
- **Behavior / Implementation**: Clicking a different color button navigates to a **distinct PDP** (e.g. clicking Brown navigates to `/accessories/strap/ostrich-leather-strap-brown`).

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
- **Links path**: All link cards point to `/accessories/<type>/<handle>` (e.g. `/accessories/strap/fkm-rubber-strap-orange`, etc.).

## F. Dial-try carousel
- **Strap Try-On Container**: Section containing heading `"A new look for your watch"` (Tier: Text-Anchor, data-testid: no)
- **Navigation buttons**:
  - Previous watch: `button.strap-nav.strap-prev` (has `aria-label="Previous watch"`)
  - Next watch: `button.strap-nav.strap-next` (has `aria-label="Next watch"`)
- **Interactive dials**:
  - 8 dial images are loaded representing compatible watch variants for Monarch, Manifesta, and Arvion:
    1. Monarch Silver Black (`Monarch_silver_black_dial.webp`)
    2. Monarch Rose Gold (`Monarch_Rose_Gold_Dial.webp`)
    3. Manifesta Mother of Pearl (`Mother_of_Pearl_dial.webp`)
    4. Manifesta Black Onyx (`Black_Onyx_dial_ade3c5ce-302f-4742-96d3-7c4f84a2baf9.webp`)
    5. Manifesta Blue Aventurine (`Blue_Aventurine_dial_774b6d53-19f1-4fb0-9f04-79d594209239.webp`)
    6. Arvion Navy Silver (`Blue_dial.webp`)
    7. Arvion Espresso Silver (`Brown_dial.webp`)
    8. Arvion Burgundy Gold (`Burgundy_dial.webp`)
- **Drag track / preview area**: The watch case overlay slides over the static strap image as the user navigates or drags.

## G. Anything else + raw inventory
- **Clasp Upgrade card**: A recommended cross-sell section `"Upgrade your strap RECOMMENDED"` offering `"Butterfly Clasp"` in Gold, Steel, or Rose Gold with a `button` containing `"+ Add"`.

### Raw data-testid inventory
- Same as target 1 (site header/footer, mobile menu toggles, story toggles, legal/sitemap links in footer).

### Elements with NO data-testid (fallback selectors)
- Product Title — `h1` (semantic tier)
- Price — `div` containing `"₹8,100"` (text-anchor tier)
- Compatibility caption — `div` containing `"For Monarch, Manifesta & Arvion"` (text-anchor tier)
- Add to cart button — `button` containing `"Add to cart"` (text-anchor tier)
- Wishlist button — `button[title="Add to wishlist"]` (attribute tier)
- Accordion panels — `button` containing accordion section title (text-anchor tier)
- Guarantee trigger — `button[aria-label="Open guarantee details"]` (aria-attribute tier)
- Return & Refund trigger — `button[aria-label="Open return and refunds details"]` (aria-attribute tier)
- Drawer overlay — `div.fixed.inset-0.z-\[9999\]` (utility/class tier)
- Drawer close — `button[aria-label="Close"]` (aria-attribute tier)
- Backdrop — `div.absolute.inset-0.bg-black/50.backdrop-blur-sm` (utility/class tier)
- Dial-Try container — Section containing heading `"A new look for your watch"` (text-anchor tier)
- Dial-Try navigation — `button.strap-nav.strap-prev` and `button.strap-nav.strap-next` (utility/class tier)
- Bottom recommended products carousel cards — `a` inside bottom carousel container (structural tier)
