# PDP Scrape: TimepiecesWallPage

URL: https://www.timepieces-d2c.example/timepieces-wall

## 1. Page Details & Structure
- **Page `<title>`**: `Timepieces Watches | Early Access to Limited Edition Pieces` (exact)
- **Wall / Canvas container**: `div.jsx-e089b15cf731d3a0.relative.w-full.h-screen.overflow-hidden.bg-\[\#060606\].cursor-grab.active:cursor-grabbing` (Tier: Utility/Class, data-testid: no)
- **Testimonial "polaroid" card / Photo-only item**:
  - **Verdict**: The testimonials, photos, and polaroid-style cards are **rendered directly inside the WebGL canvas** using Three.js (`__THREE__`). There are no HTML DOM elements representing individual polaroid cards or text testimonials. The data is fetched dynamically from `/api/timepieces-wall`.
- **Music on/off toggle button**:
  - Selector: `button.jsx-e089b15cf731d3a0.fixed.bottom-6.left-6` (Tier: Utility/Class, data-testid: no)
  - States:
    - **Initial state (music off)**: Has `aria-label="Play background music"` and contains an `img` pointing to `stop_music_equalizer.gif`.
    - **Playing state (music on)**: Has `aria-label="Mute background music"` and contains an `img` pointing to `music_equalizer.gif`.
- **Rotating-globe animation**:
  - Selector: `canvas` inside the main wrapper (Tier: Semantic, data-testid: no)
- **Lazy-loading / Infinite Scroll**:
  - Standard infinite scroll is implemented inside the WebGL canvas, triggering network events/asset loads as the user drags/navigates. Google Analytics tracks page scroll events (e.g., at `percent_scrolled=90`).

## 2. Raw Inventory & Fallback Selectors

### Raw data-testid Inventory
- `site-header` (header) — Site header banner
- `header-menu-toggle` (button) — Mobile menu toggle trigger
- `header-stories-toggle` (button) — Open stories modal trigger
- `header-logo` (a) — Home page link
- `header-search-toggle` (button) — Mobile search panel toggle trigger
- `header-account-toggle` (button) — User authentication trigger
- `header-cart-toggle` (button) — User shopping cart drawer trigger

### Elements with NO data-testid (fallback selectors)
- Wall Container — `div.cursor-grab` (utility/class tier)
- WebGL Canvas — `canvas` (semantic tier)
- Audio background player — `audio` (semantic tier)
- Music Toggle Button — `button.fixed.bottom-6.left-6` (utility/class tier)
- Music Equalizer Image — `img.fixed.bottom-6.left-6 img` (structural tier)
- Loader text — `div` containing `"LOADING MOMENTS"` (text-anchor tier)
