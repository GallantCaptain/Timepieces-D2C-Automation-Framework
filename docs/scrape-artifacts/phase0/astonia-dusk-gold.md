# PDP Scrape: astonia-dusk-gold

URL used: https://www.timepieces-d2c.example/products/astonia-dusk-gold
Page title: Astonia Dusk Gold, Astonia Watch - Timepieces World

## Core elements
- Product title: `h1.text-\[32px\]` (data-testid: no) — text observed: "Astonia"
- Price: `.product-price span` (data-testid: no) — text observed: "₹1,00,000"
- Image gallery container: `div.w-full.aspect-square.bg-white` (data-testid: no) — 6 main images and thumbnails observed
- Add to Cart button: `.desktop-sticky-sidebar button[disabled]` (data-testid: no) — Shows "Interest submitted" because the product is fully allocated (100/100 PCS)
- Wishlist button: `.desktop-sticky-sidebar button[title='Add to wishlist']` (data-testid: no) — behavior observed: On click, changes class to `text-red-600` and changes title to "Remove from wishlist" with updated svg (`addedwishlist.svg`).

## "Loom" light/night image toggle
- Present: yes
- Toggle button selector: `button[aria-label='Show lume view']` (data-testid: no)
- Confirmed image swap on click: yes (toggles overlay with `Astonia_dusk_gold_lume.webp` having opacity 1)

## 3D viewer
- Present: yes
- Toggle button selector: `button[aria-label='Open 3D View']` (data-testid: no)
- Canvas element selector/aria-label: `canvas[aria-label='Astonia']` (Close button: `button[aria-label='Close 3D view']`)

## Sibling-variant navigation card
- Container selector: `div.hidden.md:flex.gap-3.flex-wrap` (desktop), `div.flex.gap-3.md:hidden` (mobile) (data-testid: no)
- Number of sibling cards shown: 4
- What each card shows: Links to variants: "Stealth Silver", "Dusk Gold", "Dusk Rose", "Trophy Gold". Each card contains variant image and variant title.

## Description / details accordion
- Container selector: `div.specification-item` (data-testid: no)
- Item/section selectors: `button[aria-controls^='spec-content-']`
- Section titles found: 
  - Case (controls: `spec-content-Case`)
  - Strap (controls: `spec-content-Strap`)
  - Dial (controls: `spec-content-Dial`)
  - Movement (controls: `spec-content-Movement`)

## Scroll-to-section navigation
- Nav link selectors: None (No scroll sub-navigation bar found on PDP)
- Target section ids/anchors: N/A

## AR / other
- AR present: no
- Anything else notable found:
  - "See all Astonia owners" button
  - FAQ accordion list with buttons containing headers such as:
    - "What functions does the Astonia offer?"
    - "How do the chronograph sub-dials work?"
    - "What is the tachymeter on the bezel used for?"
    - "Can the chronograph be used regularly without affecting accuracy?"
    - "How do I reset the chronograph correctly?"

## Raw data-testid inventory
- header[data-testid="site-header"] — site header banner
- button[data-testid="header-menu-toggle"] — open mobile menu toggle
- button[data-testid="header-stories-toggle"] — open stories toggle
- a[data-testid="header-logo"] — link to homepage logo
- button[data-testid="header-search-toggle"] — open search toggle
- button[data-testid="header-account-toggle"] — open user account settings
- button[data-testid="header-cart-toggle"] — open shopping cart
- footer[data-testid="site-footer"] — main site footer
- div[data-testid="footer-mobile"] — mobile view footer
- div[data-testid="footer-accordion"] — mobile footer accordion item
- button[data-testid="footer-accordion-toggle"] — toggle mobile footer accordion sections
- a[data-testid="footer-accordion-link"] — links inside mobile footer accordion
- div[data-testid="footer-socials"] — footer social media links container
- a[data-testid="footer-social-link"] — social media icons links
- div[data-testid="country-selector"] — country localization dropdown
- button[data-testid="country-selector-toggle"] — open country localization options
- a[data-testid="footer-legal"] — footer legal policy link
- a[data-testid="footer-sitemap"] — footer sitemap link
- p[data-testid="footer-copyright"] — footer copyright text
- div[data-testid="footer-desktop"] — desktop view footer columns
- div[data-testid="footer-menu-column"] — desktop footer category menu column
- a[data-testid="footer-menu-link"] — desktop footer category links

## Elements with NO data-testid (fallback locators used)
- Product Title — no data-testid; used `h1.text-\[32px\]` (semantic-class tier) — flag for data-testid rollout
- Product Price — no data-testid; used `.product-price span` (semantic-class tier) — flag for data-testid rollout
- Add to Cart / Interest Submitted button — no data-testid; used `.desktop-sticky-sidebar button[disabled]` (structural tier) — flag for data-testid rollout
- Wishlist button — no data-testid; used `.desktop-sticky-sidebar button[title='Add to wishlist']` (aria-attribute tier) — flag for data-testid rollout
- Lume toggle button — no data-testid; used `button[aria-label='Show lume view']` (aria-attribute tier) — flag for data-testid rollout
- 3D view toggle button — no data-testid; used `button[aria-label='Open 3D View']` (aria-attribute tier) — flag for data-testid rollout
- 3D view Close button — no data-testid; used `button[aria-label='Close 3D view']` (aria-attribute tier) — flag for data-testid rollout
- 3D view Canvas — no data-testid; used `canvas[aria-label='Astonia']` (aria-attribute tier) — flag for data-testid rollout
- Sibling variants container — no data-testid; used `div.hidden.md:flex.gap-3.flex-wrap` (semantic-class tier) — flag for data-testid rollout
- Specification accordion buttons — no data-testid; used `button[aria-controls^='spec-content-']` (aria-attribute tier) — flag for data-testid rollout
- FAQ accordion buttons — no data-testid; used `//button[h3[contains(text(), '...')]]` (text-anchor tier) — flag for data-testid rollout
