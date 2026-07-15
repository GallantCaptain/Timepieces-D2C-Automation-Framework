# POM Scrape: PartnerProfilePage

Representative Slug Used: `kush-maini` (video & single watch purchase) and `vivek-oberoi` (multiple watches purchase)

## 1. Page Details & Structure
- **Page `<title>` Pattern**: `<Name> | Friends & Partners | Timepieces Watches` (e.g. `Kush Maini | Friends & Partners | Timepieces Watches` or `Vivek Oberoi | Friends & Partners | Timepieces Watches`)
- **Profile Hero Elements**:
  - Name Heading: `h1` containing name (e.g. `"Kush Maini"`) (Tier: Semantic, data-testid: no)
  - Standout/Tagline Heading: `h1` containing `"A Timepieces Man"` (Tier: Semantic, data-testid: no)
  - Role/Bio text: `p` containing role/description (e.g. `"Formula 1 Reserve Driver & ART Grand Prix Formula 2 Racer"`) (Tier: Text-Anchor, data-testid: no)
  - Portrait Image: Set as CSS `background-image` style on the hero wrapper div:
    - Selector: `div.jsx-5433de6ba48f9c4e.flex.flex-col.justify-end.relative.overflow-hidden` (Tier: Utility/Class, style contains `background-image: url(...)`)
- **Video Embed / Player**:
  - There is **no explicit "Play Video" button**. Instead, profile pages featuring a video (such as Kush Maini & Kynan Chenai) have an **inline, autoplaying, looping, muted YouTube video** embedded in the DOM.
  - Video Player Selector: `iframe.jsx-5433de6ba48f9c4e.absolute.top-0.left-0.w-full.h-full.rounded-2xl` inside a `div.relative.w-full` container (Tier: Structural, data-testid: no)
  - YouTube Embed URL pattern: `https://www.youtube.com/embed/<video-id>?autoplay=1&mute=1&loop=1&rel=0&modestbranding=1&playlist=<video-id>`
- **"Numbered Watches Bought" Sticky Bar & Drawer**:
  - Highlighted Watch (fixed sticky bar):
    - Selector: `div.fixed.left-0.right-0.z-20.bg-black\/60.backdrop-blur-sm` (Tier: Utility/Class, data-testid: no)
    - Watch link: `a` with classes `flex items-center gap-3` (href links to PDP, e.g., `/products/astonia-bimetal-dusk-rose`)
    - Watch Title & Number text: contains name/variant (e.g. `"Astonia"`, `"Dusk Rose"`) and serial number (e.g. `"#009"` or `"#004"`)
  - Multiple Purchases Drawer (only present for partners who bought multiple watches, e.g., Vivek Oberoi):
    - Toggle Button: `button` containing `"See More"` inside the sticky bar (Tier: Text-Anchor, has classes `border border-[#3a3a3a] rounded-full px-6 py-2.5 bg-white text-black`)
    - Action: Clicking `"See More"` toggles the drawer container classes from `max-h-0` to `max-h-[60vh]`.
    - Drawer List Container: `div.jsx-5433de6ba48f9c4e.overflow-y-auto.max-h-\[60vh\].px-4.md\:px-14.pb-4.space-y-3` (Tier: Structural, data-testid: no)
    - Watch list items: links `a.flex.items-center.gap-3` pointing to product PDPs (e.g., `/products/astonia-trophy-gold`, `/products/arvion-espresso-silver`, `/products/auriqua-ocean-blue`)
- **Back Navigation**:
  - Selector: `a` containing text `"Friends & Partners"` (Tier: Text-Anchor, href: `https://www.timepieces-d2c.example/friends-and-partners`, class: `text-white text-[14px] font-timepieces-regular`)

## 2. Raw Inventory & Fallback Selectors

### Raw data-testid Inventory
- Same as standard content pages (site header/footer, mobile menu toggles, story toggles).

### Elements with NO data-testid (fallback selectors)
- Hero Profile Name — `h1` (semantic tier)
- Hero Profile Tagline — `h1 + h1` (structural/semantic tier)
- Hero Profile Role — `p` containing role/description (semantic tier)
- Hero Portrait Background — `div.flex.flex-col.justify-end.relative.overflow-hidden` (utility/class tier)
- Video Player iframe — `iframe` (semantic tier)
- Sticky Bottom Bar Container — `div.fixed.left-0.right-0.z-20` (utility/class tier)
- See More Button — `button` containing `"See More"` (text-anchor tier)
- Watches List Drawer — `div.overflow-y-auto.max-h-\[60vh\]` (utility/class tier)
- Watch Card Link — `a[href*="/products/"]` (structural tier)
- Back Navigation Link — `a` containing `"Friends & Partners"` (text-anchor tier)
