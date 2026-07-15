# PDP Scrape: UpdatesPage

URL: https://www.timepieces-d2c.example/updates

## 1. Page Details & Structure
- **Page `<title>`**: `Timepieces Updates`
- **Main Heading**: `h1` containing `"Timepieces Updates"` (Tier: Semantic, data-testid: no)
- **Content-Filter Buttons**:
  - Container selector: `div.flex.gap-2.overflow-x-auto.no-scrollbar.pb-3` (Tier: Utility/Class, data-testid: no)
  - Buttons list: `button` inside the filter container (Tier: Semantic, data-testid: no)
  - Filter labels:
    - `"All"`
    - `"Drops & Releases"`
    - `"Delivery Updates"`
    - `"Community"`
    - `"Production & Quality"`
- **Featured / Hero Updates Carousel**:
  - Swiper/slide dots control buttons: `button` with text/aria-label `"Go to slide 1"`, `"Go to slide 2"`, `"Go to slide 3"`. (Tier: Text-Anchor, data-testid: no)
- **News/Announcement Card**:
  - Selector: `button.flex.gap-3.bg-\[\#111215\].rounded-2xl.p-3.text-left.cursor-pointer` (Tier: Utility/Class, data-testid: no)
  - Card inner elements:
    - Category text: `span` (e.g., `"Drops & Releases"`)
    - Date text: `span` (e.g., `"Tue, 19 May, 2026"`)
    - Title heading: `h3.text-white.text-base.font-\[500\].leading-snug.line-clamp-2` containing the card title (e.g., `"Our Strap Collection Is Now Live"`)
    - Image: `img` inside the card wrapper
- **Pagination / Load More**:
  - Selector: `button` containing `"View more"` or `"Read more"` (Tier: Text-Anchor, data-testid: no)

## 2. Raw Inventory & Fallback Selectors

### Raw data-testid Inventory
- Same as standard content pages (site header/footer, mobile menu toggles, story toggles, legal/sitemap links in footer).

### Elements with NO data-testid (fallback selectors)
- Main Heading — `h1` (semantic tier)
- Subheading — `p` containing `"The latest from Timepieces"` (text-anchor tier)
- Filter Container — `div.flex.gap-2.overflow-x-auto` (utility/class tier)
- Filter Buttons — `div.flex.gap-2.overflow-x-auto button` (structural tier)
- Update Cards — `button.flex.gap-3.bg-\[\#111215\]` (utility/class tier)
- Update Card Title — `h3` inside card button (semantic tier)
- View More Button — `button` containing `"View more"` (text-anchor tier)
- Read More Button — `button` containing `"Read more"` (text-anchor tier)
