# PLP Scrape: all-accessories-page

URL used: https://www.timepieces-d2c.example/accessories/all
Page title: All Accessories | Timepieces Accessories

## Core elements
- Page banner: `section[data-testid="accessories-banner"]`
- Page title: `h1[data-testid="accessories-banner-title"]` (text: "All Accessories")
- Category tabs container: `div[data-testid="accessories-category-tabs"]`
- Category tab buttons: `button[data-testid="accessories-category-tab"]` (options: "All", "Strap", "Clasp", "Watch Case")
- Active category class: Toggled tab has a styled border/background
- Product count: `span[data-testid="accessories-product-count"]` (observed: "21 Products")
- Product grid container: `div[data-testid="accessories-products-grid"]`
- Product card container: `div[data-testid="accessories-product-card"]`
- Product card link: `a[data-testid="accessories-product-link"]` (wraps the product card)
- Product image carousel: `div[data-testid="accessories-product-carousel"]`
- Product image carousel track: `div[data-testid="accessories-product-carousel-track"]`
- Product image carousel next button: `button[data-testid="accessories-product-carousel-next"]`
- Product wishlist button: `div[data-testid="accessories-product-wishlist"] button[title="Add to wishlist"]`
- Product title: `h3[data-testid="accessories-product-title"]` (inside product card)

## Filters & Sort panel
- Open button (desktop): `button[data-testid="accessories-filters-button-desktop"]`
- Open button (mobile): `button[data-testid="accessories-filters-button-mobile"]`
- Drawer container: `aside[data-testid="accessories-filter-drawer-desktop"]` (desktop)
- Close button: `button[data-testid="accessories-filter-close"]`
- Sort options: `label[data-testid="accessories-filter-sort-option"]` (options: "Price, Low To High", "Price, High To Low")
- Product type filter toggle: `button[data-testid="accessories-filter-section-product-type-toggle"]`
- Product type options: `label[data-testid="accessories-filter-product-type"]` (options: "Strap", "Clasp", "Watch Case")
- Size filter toggle: `button[data-testid="accessories-filter-section-size-toggle"]`
- Apply button: `button[data-testid="accessories-filter-apply-desktop"]`

## AR / other
- AR present: no
- Anything else notable found: N/A

## Raw data-testid inventory
- div[data-testid="accessories-template"] — main accessories page wrapper
- section[data-testid="accessories-banner"] — accessories header hero image banner
- h1[data-testid="accessories-banner-title"] — accessories title header
- div[data-testid="accessories-category-tabs"] — category filter tabs wrapper
- button[data-testid="accessories-category-tab"] — tab button for "All", "Strap", "Clasp", "Watch Case"
- span[data-testid="accessories-product-count"] — count text indicating total products
- button[data-testid="accessories-filters-button-mobile"] — open filter button for mobile layout
- button[data-testid="accessories-filters-button-desktop"] — open filter button for desktop layout
- div[data-testid="accessories-products-wrapper"] — products list section wrapper
- div[data-testid="accessories-products-grid"] — products grid layout container
- div[data-testid="accessories-product-card"] — product card container
- a[data-testid="accessories-product-link"] — anchor link around the product card
- div[data-testid="accessories-product-carousel"] — product image carousel wrapper
- div[data-testid="accessories-product-carousel-track"] — carousel slide track containing image elements
- button[data-testid="accessories-product-carousel-next"] — slide next button in card carousel
- div[data-testid="accessories-product-wishlist"] — wishlist button container on product card
- h3[data-testid="accessories-product-title"] — product title text
- aside[data-testid="accessories-filter-drawer-desktop"] — filters drawer sidebar container
- button[data-testid="accessories-filter-close"] — close filters drawer
- div[data-testid="accessories-filter-body-desktop"] — scrollable filter body container
- label[data-testid="accessories-filter-sort-option"] — sort order label options
- div[data-testid="accessories-filter-section-product-type"] — product type accordion wrapper
- button[data-testid="accessories-filter-section-product-type-toggle"] — toggle product type filters
- label[data-testid="accessories-filter-product-type"] — checkbox label option for product types
- div[data-testid="accessories-filter-section-size"] — size accordion wrapper
- button[data-testid="accessories-filter-section-size-toggle"] — toggle size filters
- div[data-testid="accessories-filter-footer-desktop"] — drawer bottom section container
- button[data-testid="accessories-filter-apply-desktop"] — apply filters button

## Elements with NO data-testid (fallback locators used)
- Sort option input checkbox — no data-testid; used `input[type="radio"]` (structural tier) or nested inside `label[data-testid="accessories-filter-sort-option"]`
- Filter option input checkbox — no data-testid; used `input[type="checkbox"]` (structural tier) or nested inside `label[data-testid="accessories-filter-product-type"]`
