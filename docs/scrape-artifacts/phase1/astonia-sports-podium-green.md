# PDP Scrape: astonia-sports-podium-green

URL used: https://www.timepieces-d2c.example/products/astonia-sports-podium-green
Page title: Astonia Sports Podium Green, Astonia Sports Watch - Timepieces

## Core elements
- Product title: `h1` (data-testid: no) — text observed: "Astonia Sports"
- Price: `h1 div.product-price span` or `div.product-price span` (data-testid: no) — text observed: "₹79,000"
- Image gallery container: `div.desktop-image-gallery` (desktop), `div.product-media-carousel` (mobile) (data-testid: no) — 10 image sources observed
- Add to Cart / Register Interest button: `.desktop-sticky-sidebar button` containing "Register Interest" (desktop), `.fixed.bottom-0 button` containing "Register Interest" (mobile) (data-testid: no) — Titled "Register Interest", **enabled** (though product shows "FULLY ALLOCATED" 100/100 PCS on page)
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
- Toggle button selector: `button[aria-label='Open 3D View']` (data-testid: no)
- 3D View Container: `div.fixed.inset-0.z-\[9999\]` (data-testid: no)
- Close button: `button[aria-label='Close 3D view']` (data-testid: no)
- Canvas element selector/aria-label: `canvas[aria-label='Astonia Sports']` (data-testid: no)

## Sticky/CTA
- Sticky add-to-bag section: `.desktop-sticky-sidebar` (desktop), `.fixed.bottom-0` (mobile) (data-testid: no)
- Primary CTA: "Register Interest" button inside sticky sidebar/bar.
- Band color selector: None present. Sibling variants navigation serves as variant selection.

## Sibling nav
- Sibling variants container: `div.hidden.md:flex.gap-3.flex-wrap` (desktop), `div.flex.gap-3.md:hidden` (mobile) (data-testid: no)
- Number of sibling cards: 3
- Sibling links:
  - `Astonia Sports Phantom Black` -> `/products/astonia-sports-phantom-black`
  - `Astonia Sports Circuit Blue` -> `/products/astonia-sports-circuit-blue`
  - `Astonia Sports Podium Green` -> `/products/astonia-sports-podium-green`
- Additional link: Sibling variants also lists `"THE NEXT CHAPTER: Swiss Chronograph. Built to be noticed."` (which links to `/new-release/astonia` / `/products/astonia-dusk-gold`).

## Specification accordion
- Accordion container selector: `div.physical-specification` (nearest section)
- Accordion toggle button selector: `button[aria-controls^='spec-content-']` (data-testid: no)
- Accordion sections:
  - Case (controls: `spec-content-Case`)
  - Strap (controls: `spec-content-Strap`)
  - Dial (controls: `spec-content-Dial`)
  - Movement (controls: `spec-content-Movement`)

## Owners modal
- Owners Modal open button: `button` containing "See all Astonia Sports owners" (data-testid: no)
- Owners Modal Container: `div.fixed.inset-x-0.bottom-0.md:inset-0.z-\[101\]` (data-testid: no)
- Close button: `button[aria-label='Close']` (data-testid: no)
- Filter by variant options: `button` containing "Phantom Black", `button` containing "Circuit blue", `button` containing "Podium Green" (data-testid: no)
- Search Input selector: `input[placeholder='Search owners...']` (data-testid: no)

## other
- Virtual Consultation link: `a[href*='/consultation?product=astonia-sports-podium-green']` (text: "Book Consultation")
- FAQ Accordions: `button[aria-expanded]` (sections include "What functions does...", "How do the chronograph...", etc.)

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
- 3D view toggle button — no data-testid; used `button[aria-label='Open 3D View']` (aria-attribute tier)
- 3D view Close button — no data-testid; used `button[aria-label='Close 3D view']` (aria-attribute tier)
- 3D view Canvas — no data-testid; used `canvas[aria-label='Astonia Sports']` (aria-attribute tier)
- Sibling variants container — no data-testid; used `div.hidden.md:flex.gap-3.flex-wrap` (semantic-class tier)
- Specification accordion buttons — no data-testid; used `button[aria-controls^='spec-content-']` (aria-attribute tier)
- FAQ accordion buttons — no data-testid; used `button` containing specific header text (text-anchor tier)
- Owners Modal trigger — no data-testid; used `button` containing "See all Astonia Sports owners" (text-anchor tier)
- Owners Modal Close — no data-testid; used `button[aria-label='Close']` (aria-attribute tier)
- Owners Modal Variant Filters — no data-testid; used `button` containing variant name (text-anchor tier)
- Owners Modal Search Input — no data-testid; used `input[placeholder='Search owners...']` (aria-attribute tier)
