# PDP Scrape: auriqua-noir-rose

URL used: https://www.timepieces-d2c.example/products/auriqua-noir-rose
Page title: Auriqua Noir Rose, Auriqua Watch - Timepieces Watches

## Core elements
- Product title: `h1` (data-testid: no) — text observed: "Auriqua"
- Price: `h1 div.product-price span` or `div.product-price span` (data-testid: no) — text observed: "₹75,000"
- Image gallery container: `div.desktop-image-gallery` (desktop), `div.product-media-carousel` (mobile) (data-testid: no) — 12 image sources observed
- Add to Cart / Register Interest button: `.desktop-sticky-sidebar button` containing "Register Interest" (desktop), `.fixed.bottom-0 button` containing "Register Interest" (mobile) (data-testid: no) — Titled "Register Interest", **enabled** (though product shows "FULLY ALLOCATED" 176/176 PCS on page)
- Wishlist button: `.desktop-sticky-sidebar button[title='Add to wishlist']` (data-testid: no) — behavior observed: On click, title changes to "Remove from wishlist", class updates to `text-red-600 hover:text-red-700`, and svg source updates to `addedwishlist.svg`.

## Video/Audio
- Video elements:
  - Mobile hero background video: `video.object-cover.md\:hidden` (autoplay, muted, loop, no controls, src: `https://cdn.shopify.com/videos/c/o/v/<video-id>.mp4`)
  - Desktop hero background video: `video.object-cover.hidden.md\:block` (autoplay, muted, loop, no controls, src: `https://cdn.shopify.com/videos/c/o/v/<video-id>.mp4`)
  - Section background video 1: `video.object-cover` (parent class `min-h-[400px]`, autoplay, muted, loop, src: `https://cdn.shopify.com/videos/c/o/v/<video-id>.mp4`)
  - Section background video 2: `video.object-cover` (parent class `aspect-[3/4]`, autoplay, muted, loop, src: `https://cdn.shopify.com/videos/c/o/v/<video-id>.mp4`)
- Volume/unmute controls: None present on page.

## Lume
- Present: yes
- Toggle button selector: `button[aria-label='Show lume view']` (data-testid: no) — toggles `aria-label` to "Hide lume view" on click, changing the parent opacity of `img[alt='Lume view']` to 1.

## 3D viewer
- Present: no (open 3D view button or canvas elements are **not** present in the DOM for Auriqua variants).

## Sticky/CTA
- Sticky add-to-bag section: `.desktop-sticky-sidebar` (desktop), `.fixed.bottom-0` (mobile) (data-testid: no)
- Primary CTA: "Register Interest" button inside sticky sidebar/bar.
- Band color selector: None present. Sibling variants navigation serves as variant selection.

## Sibling nav
- Sibling variants container: `div.hidden.md:flex.gap-3.flex-wrap` (desktop), `div.flex.gap-3.md:hidden` (mobile) (data-testid: no)
- Number of sibling cards: 3
- Sibling links:
  - `Auriqua Noir Rose` -> `/products/auriqua-noir-rose`
  - `Auriqua Racing Green` -> `/products/auriqua-racing-green`
  - `Auriqua Ocean Blue` -> `/products/auriqua-ocean-blue`

## Specification accordion
- Accordion container selector: `div.physical-specification` (nearest section)
- Accordion toggle button selector: `button[aria-controls^='spec-content-']` (data-testid: no)
- Accordion sections:
  - Case (controls: `spec-content-Case`)
  - Strap (controls: `spec-content-Strap`)
  - Dial (controls: `spec-content-Dial`)
  - Movement (controls: `spec-content-Movement`)

## Owners modal
- Owners Modal open button: `button` containing "See all Auriqua owners" (data-testid: no)
- Owners Modal Container: `div.fixed.inset-x-0.bottom-0.md:inset-0.z-\[101\]` (data-testid: no)
- Close button: `button[aria-label='Close']` (data-testid: no)
- Filter by variant options: `button` containing "Noir Rose", `button` containing "Racing Green", `button` containing "Ocean Blue" (data-testid: no)
- Search Input selector: `input[placeholder='Search owners...']` (data-testid: no)

## other
- Virtual Consultation link: `a[href*='/consultation?product=auriqua-noir-rose']` (text: "Book Consultation")
- FAQ Accordions: `button[aria-expanded]` (sections include "What type of movement...", "What does “skeleton”...", etc.)

## Raw data-testid inventory
- header[data-testid="site-header"] — site header banner
- footer[data-testid="site-footer"] — main site footer
- div[data-testid="country-selector"] — country localization dropdown
- button[data-testid="country-selector-toggle"] — open country localization options

## Elements with NO data-testid (fallback locators used)
- Product Title — no data-testid; used `h1` (semantic tier)
- Price — no data-testid; used `div.product-price span` (structural tier)
- Image gallery — no data-testid; used `div.desktop-image-gallery img` (structural tier)
- Register Interest button — no data-testid; used `.desktop-sticky-sidebar button` (structural tier)
- Wishlist button — no data-testid; used `button[title='Add to wishlist']` (aria-attribute tier)
- Lume toggle button — no data-testid; used `button[aria-label='Show lume view']` (aria-attribute tier)
- Sibling variants container — no data-testid; used `div.hidden.md:flex.gap-3.flex-wrap` (semantic-class tier)
- Specification accordion buttons — no data-testid; used `button[aria-controls^='spec-content-']` (aria-attribute tier)
- FAQ accordion buttons — no data-testid; used `button` containing specific header text (text-anchor tier)
- Owners Modal trigger — no data-testid; used `button` containing "See all Auriqua owners" (text-anchor tier)
- Owners Modal Close — no data-testid; used `button[aria-label='Close']` (aria-attribute tier)
- Owners Modal Variant Filters — no data-testid; used `button` containing variant name (text-anchor tier)
- Owners Modal Search Input — no data-testid; used `input[placeholder='Search owners...']` (aria-attribute tier)
