# POM Scrape: FriendsAndPartners Page

URL: https://www.timepieces-d2c.example/friends-and-partners

## 1. Page Details & Structure
- **Page `<title>`**: `Friends & Partners | Timepieces Watches` (exact)
- **Main Heading / Title**: `h1` containing `"Friends & Partners"` (Tier: Semantic, data-testid: no)
- **Main Intro / Subheading**: `p` containing `"The individuals who embody the Timepieces philosophy. Makers, thinkers, builders, leaders."` (Tier: Text-Anchor, data-testid: no)
- **The 3 Sections**:
  - The page sections are separated and filtered via tabs/buttons, and delimited by `h2` headings inside the list container:
    - **Voices Section Heading**: `h2` containing `"Voices"` (has class `hidden` in some views, but is present in DOM)
    - **Founders & Makers Section Heading**: `h2` containing `"Founders & Makers"` (has class `block text-white...`)
    - **Thought Leaders Section Heading**: `h2` containing `"Thought Leaders"` (has class `block text-white...`)
- **Profile Card Selector**:
  - Selector: `a[href^="/friends-and-partners/"]` with class `relative group/card cursor-pointer block w-full rounded-2xl overflow-hidden` (Tier: Structural, data-testid: no)
  - Card Inner Structure:
    - Portrait Image: `img` with `alt` like `"<Name> wearing Timepieces <Watch>"`
    - Name Heading: `h2` containing the person's name (e.g., `"Vivek Oberoi"`)
    - Role/Title Heading: `h2` containing their title (e.g., `"Actor, Entrepreneur, and Philanthropist"`)
- **Video-Icon Badge**:
  - Selector: `div.absolute.top-3.left-3.inline-flex.h-9.aspect-square.justify-center.items-center.rounded-full.bg-black\/40.z-10` (Tier: Utility/Class, data-testid: no)
  - Details: Inside the `div`, there is an `svg` element representing a play icon. This badge indicates a card that has an associated video (e.g., Kush Maini and Kynan Chenai cards have it, others do not).
- **Category / Section Filters**:
  - Container Selector: `div` wrapping the buttons below the main intro (Tier: Structural)
  - Buttons List: `button` inside the filter wrapper
  - Labels:
    - `"Voices"` (Selected state: classes contain `border-white text-white`)
    - `"Founders & Makers"` (Unselected state: classes contain `border-transparent text-white/50`)
    - `"Thought Leaders"` (Unselected state: classes contain `border-transparent text-white/50`)
- **Profile Cards Count**:
  - Total profile cards listed: **69**

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
- Page Main Heading — `h1` (semantic tier)
- Page Intro Text — `p` containing `"The individuals who embody"` (text-anchor tier)
- Section Filter Buttons — `button` containing `"Voices"`, `"Founders & Makers"`, `"Thought Leaders"` (text-anchor tier)
- Section Category Headings — `h2` containing `"Voices"`, `"Founders & Makers"`, `"Thought Leaders"` (semantic tier)
- Profile Card Link — `a[href^="/friends-and-partners/"]` (structural tier)
- Profile Card Name — `h2` inside profile link card (semantic tier)
- Profile Card Role — `h2 + h2` inside profile link card (structural/semantic tier)
- Video Badge Container — `div.absolute.top-3.left-3` (utility/class tier)
