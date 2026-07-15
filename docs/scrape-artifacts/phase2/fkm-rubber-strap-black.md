# PDP Scrape: fkm-rubber-strap-black

URL: https://www.timepieces-d2c.example/accessories/strap/fkm-rubber-strap-black
Represents: Rubber Strap with dial-try carousel

## A. Core identity + purchase
- **Page `<title>`**: `FKM Rubber Watch Strap in Bkack - Timepieces Watches` (exact spelling includes "Bkack")
- **Product title element**: `h1` containing `"FKM Rubber Strap - Black"` (Tier: Semantic, data-testid: no)
- **Price (₹)**: `div` containing `"₹6,500"` (Tier: Text-Anchor, data-testid: no)
- **Compatibility caption**: `div` containing `"For Astonia"` (Tier: Text-Anchor, data-testid: no)
- **Primary CTA**: `"Add to cart"` button (or `"Add to Cart"` in sticky bar)
  - Desktop selector: `button` with text `"Add to cart"` or `.flex-col button` containing `"Add to cart"` (Tier: Text-Anchor, data-testid: no)
  - Sticky mobile/tablet selector: `button` containing `"Add to Cart"` inside bottom bar (Tier: Text-Anchor, data-testid: no)
  - State: **Enabled** (purchasable)
- **Quantity selector**: None present on page.
- **Wishlist button**: `button[title="Add to wishlist"]` (Tier: Attribute, data-testid: no)
  - Behavior: Toggles SVG state and description to `"Remove from wishlist"`.
- **Image gallery container**: `.desktop-image-gallery` (desktop), `.product-media-carousel` (mobile) (Tier: Utility/Class, data-testid: no)
  - Image count: 2 images (`ASTONIABLACKFRONT...`, `ASTONIABLACKBACK...`)

## B. Card-button variant switcher
- **Color Switcher container**: `.flex.flex-wrap.gap-2` or similar color container (Tier: Utility/Class, data-testid: no)
- **Color Switcher buttons**:
  - Black: `button[title="FKM Rubber Strap - Black"]` (pressed, has `aria-pressed="true"`)
  - Yellow: `button[title="FKM Rubber Strap - Yellow"]`
  - Orange: `button[title="FKM Rubber Strap - Orange"]`
  - White: `button[title="FKM Rubber Strap - White"]`
- **Behavior / Implementation**: Clicking a different color button navigates to a **distinct PDP** (e.g. clicking Yellow navigates to `/accessories/strap/fkm-rubber-strap-yellow`).

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
  - 7 dial images are loaded representing compatible Astonia watch variants:
    1. Phantom Black (`ASTONIA_SPORTS_phantom_black_dial.webp`)
    2. Podium Green (`ASTONIA_SPORTS_podium_green.webp`)
    3. Circuit Blue (`ASTONIA_SPORTS_circuit_blue_dial.webp`)
    4. Trophy Gold (`ASTONIA_rophy_Gold.webp`)
    5. Stealth Silver (`ASTONIA_Stealth_Silver.webp`)
    6. Dusk Gold (`ASTONIA_Dusk_Gold.webp`)
    7. Dusk Rose (`ASTONIA_Dusk_Rose.webp`)
- **Drag track / preview area**: The watch case overlay slides over the static strap image as the user navigates or drags.

## G. Anything else + raw inventory
- **Clasp Upgrade card**: A recommended cross-sell section `"Upgrade your strap RECOMMENDED"` offering `"Single Deployment Clasp"` in Steel, Black, Rose Gold, or Gold with a `button` containing `"+ Add"`.

### Raw data-testid inventory
- Same as target 1 (site header/footer, mobile menu toggles, story toggles, legal/sitemap links in footer).

### Elements with NO data-testid (fallback selectors)
- Product Title — `h1` (semantic tier)
- Price — `div` containing `"₹6,500"` (text-anchor tier)
- Compatibility caption — `div` containing `"For Astonia"` (text-anchor tier)
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
