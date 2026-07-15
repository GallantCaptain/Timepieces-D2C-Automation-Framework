# PDP Scrape: SiteMapPage

URL: https://www.timepieces-d2c.example/sitemap

## 1. Page Details & Structure
- **Page `<title>`**: `Sitemap | Timepieces`
- **Main Heading**: `h1` containing `"Sitemap"` (Tier: Semantic, data-testid: no)
- **Link Groups / Sections**:
  - Main headings define the groups: `h2` (Tier: Semantic, data-testid: no)
  - Section titles:
    - `"Main Pages"`
    - `"Collections"`
    - `"Products"`
    - `"Accessories"`
    - `"All Accessories"`
- **Link Items**:
  - Selector: `.prose a` or `main a` (Tier: Structural, data-testid: no)
  - Number of links: Approximately 60 links.
  - Examples:
    - `/` (Home)
    - `/pages/the-brand` (The Brand)
    - `/collections/timepieces` (Timepieces)
    - `/blog` (Blogs Page)
    - `/experience-center` (Experience Center)
    - `/consultation` (Consultation)
    - `/contact` (Contact Page)
    - `/timepieces-wall` (Timepieces Wall)
    - `/timepieces-wall/share` (Share Your Moment)
    - `/collections/automatic` (Automatic Collection)
    - `/collections/q-matic-timepiece` (Q-Matic Collection)
    - `/products/auriqua-noir-rose` (Auriqua timepiece variant)
    - `/products/monarch-silver-black` (Monarch timepiece variant)
    - `/products/astonia-stealth-silver` (Astonia timepiece variant)
    - `/accessories/all` (All Accessories)
    - `/accessories/strap` (Straps)
    - `/accessories/clasp` (Clasp)
    - `/accessories/clasp/butterfly-clasp-gold-18mm` (Gold Butterfly Clasp)
    - `/accessories/strap/fkm-rubber-strap-black` (FKM Rubber Strap Black)
    - `/accessories/watch-case/premium-watch-case` (Premium Watch Case Black)

## 2. Raw Inventory & Fallback Selectors

### Raw data-testid Inventory
- Same as standard content pages (site header/footer, mobile menu toggles, story toggles, legal/sitemap links in footer).

### Elements with NO data-testid (fallback selectors)
- Sitemap Title — `h1` (semantic tier)
- Group Section Headings — `h2` (semantic tier)
- Sitemap Links — `main a` or `h2 + a` (structural/semantic tier)
