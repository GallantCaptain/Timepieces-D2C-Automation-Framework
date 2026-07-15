# PLP Scrape: all-timepieces-page

URL used: https://www.timepieces-d2c.example/collections/timepieces
Page title: Timepieces Collection | Timepieces Watches

## Core elements
- Page banner: `div[data-testid="collection-banner"]`
- Page title: `h1[data-testid="collection-banner-title"]` (text: "Become More")
- Category tabs container: `div[data-testid="collection-category-tabs"]`
- Category tab buttons: `button[data-testid="collection-category-tab"]` (options: "All", "Automatic", "Q-Matic")
- Active category class: `bg-[#007A61]` and `text-white`
- Product count: `span[data-testid="collection-product-count"]` (observed: "18 Timepieces")
- Product grid container: `div[data-testid="collection-products-grid"]`
- Product card container: `div[data-testid="collection-product-card"]`
- Product card link: `a[data-testid="collection-product-link"]` (wraps the product card)
- Product image carousel: `div[data-testid="collection-product-carousel"]`
- Product image carousel track: `div[data-testid="collection-product-carousel-track"]`
- Product image carousel next button: `button[data-testid="collection-product-carousel-next"]`
- Product wishlist button: `div[data-testid="collection-product-wishlist"] button[title="Add to wishlist"]`
- Product title: `h3[data-testid="collection-product-title"]` (inside product card)

## Filters & Sort panel
- Open button (desktop): `button[data-testid="collection-filters-button-desktop"]`
- Open button (mobile): `button[data-testid="collection-filters-button-mobile"]`
- Drawer container: `aside[data-testid="collection-filter-drawer-desktop"]` (desktop)
- Close button: `button[data-testid="collection-filter-close"]`
- Sort options: `label[data-testid="collection-filter-sort-option"]` (options: "Price, Low To High", "Price, High To Low")
- Movement filter toggle: `button[data-testid="collection-filter-section-watch-movement-toggle"]`
- Movement options: `label[data-testid="collection-filter-watch-movement"]` (options: "Automatic", "Q-Matic")
- Size filter toggle: `button[data-testid="collection-filter-section-size-toggle"]`
- Apply button: `button[data-testid="collection-filter-apply-desktop"]`

## AR / other
- AR present: no
- Anything else notable found:
  - Next release cards: `div[data-testid="collection-next-release-cards"]` with cards linking to `https://www.timepieces-d2c.example/new-release/astonia` and `https://www.timepieces-d2c.example/new-release/manifesta`.

## Raw data-testid inventory
- div[data-testid="collection-template-normal"] — main collection page wrapper
- div[data-testid="collection-banner"] — collection header hero image banner
- h1[data-testid="collection-banner-title"] — collection title header
- div[data-testid="collection-category-tabs"] — category filter tabs wrapper
- button[data-testid="collection-category-tab"] — tab button for "All", "Automatic", "Q-Matic"
- span[data-testid="collection-product-count"] — count text indicating total items
- button[data-testid="collection-filters-button-mobile"] — open filter button for mobile layout
- button[data-testid="collection-filters-button-desktop"] — open filter button for desktop layout
- div[data-testid="collection-next-release-cards"] — container for upcoming drop banners
- a[data-testid="collection-next-release-card"] — drop banner linking to new release PDPs
- div[data-testid="collection-products-wrapper"] — products list section wrapper
- div[data-testid="collection-products-grid"] — products grid layout container
- div[data-testid="collection-product-card"] — product card container
- a[data-testid="collection-product-link"] — anchor link around the product card
- div[data-testid="collection-product-carousel"] — product image carousel wrapper
- div[data-testid="collection-product-carousel-track"] — carousel slide track containing image elements
- button[data-testid="collection-product-carousel-next"] — slide next button in card carousel
- div[data-testid="collection-product-wishlist"] — wishlist button container on product card
- h3[data-testid="collection-product-title"] — product title text
- aside[data-testid="collection-filter-drawer-desktop"] — filters drawer sidebar container
- button[data-testid="collection-filter-close"] — close filters drawer
- div[data-testid="collection-filter-body-desktop"] — scrollable filter body container
- label[data-testid="collection-filter-sort-option"] — sort order label options
- div[data-testid="collection-filter-section-watch-movement"] — movement accordion wrapper
- button[data-testid="collection-filter-section-watch-movement-toggle"] — toggle watch movement filters
- label[data-testid="collection-filter-watch-movement"] — checkbox label option for movements
- div[data-testid="collection-filter-section-size"] — size accordion wrapper
- button[data-testid="collection-filter-section-size-toggle"] — toggle dial size filters
- div[data-testid="collection-filter-footer-desktop"] — drawer bottom section container
- button[data-testid="collection-filter-apply-desktop"] — apply filters button

## Elements with NO data-testid (fallback locators used)
- Sort option input checkbox — no data-testid; used `input[type="radio"]` (structural tier) or nested inside `label[data-testid="collection-filter-sort-option"]`
- Filter option input checkbox — no data-testid; used `input[type="checkbox"]` (structural tier) or nested inside `label[data-testid="collection-filter-watch-movement"]`
