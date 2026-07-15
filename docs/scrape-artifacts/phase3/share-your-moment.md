# PDP Scrape: ShareYourMomentPage

URL: https://www.timepieces-d2c.example/timepieces-wall/share

## 1. Page Details & Structure
- **Page `<title>`**: `Timepieces Watches | Early Access to Limited Edition Pieces` (exact)
- **Submit Moment Button**:
  - Selector: `button` containing text `"Submit your moment"` (type `"button"`, initially has `disabled=""`)
  - Tier: Text-Anchor (data-testid: no)
- **Gate Behavior (Logged-Out User)**:
  - When a logged-out user clicks `"Share your moment"` on the welcome/landing screen, it opens a login gate modal:
    - Selector: `div.fixed.inset-0.z-50.flex.items-center.justify-center.p-6` (Tier: Utility/Class, data-testid: no)
    - Heading: `h2` containing text `"Sign in to share your Timepieces moment"` (Tier: Text-Anchor, data-testid: no)
    - Subtext/Message: `p` containing `"Only Timepieces owners can submit reviews and be featured on the Timepieces Wall."` (Tier: Text-Anchor, data-testid: no)
    - Action CTA: `button` containing text `"Sign in"` (which directs to sign-in workflow)
    - Close button: `button.absolute.top-4.right-4` (closes the modal on click)
- **Go to Timepieces Wall button/link**:
  - Selector: `button` containing text `"Go to Timepieces Wall"` (Tier: Text-Anchor, data-testid: no)
  - Links to: `/timepieces-wall`
- **Watch Photos Carousel**:
  - Selector: `.carousel-track`
  - Columns/Tracks:
    - Horizontal (Mobile): `div.carousel-track.flex.gap-3.overflow-hidden.select-none.pointer-events-none`
    - Vertical (Desktop): `div.carousel-track.flex.flex-col.gap-6.overflow-hidden.select-none.pointer-events-none.px-6.py-6.h-\[100vh\]`
  - Items: `div.shrink-0` containing watch images
  - Navigation controls: None (the tracks have `pointer-events-none` and auto-scroll/remain static).
- **Form Fields (Visible before Submit)**:
  - Textarea Story Input: `textarea` (placeholder: `"Where did you wear it? What made it yours?"`)
  - File Upload Input: `input[type="file"][accept="image/*,video/*"][multiple]` (hidden input field)
  - Google Review cross-sell: `a` containing text `"Write a review"` (href: `https://g.page/r/EXAMPLE/review`)

## 2. Raw Inventory & Fallback Selectors

### Raw data-testid Inventory
- Same as Timepieces Wall page (site header/footer, mobile menu toggles, story toggles).

### Elements with NO data-testid (fallback selectors)
- Page Title Heading — `h1` containing `"Show your Timepieces moment"` (text-anchor tier)
- Textarea Story Input — `textarea` (semantic tier)
- Submit Moment button — `button` containing `"Submit your moment"` (text-anchor tier)
- Go to Timepieces Wall button — `button` containing `"Go to Timepieces Wall"` (text-anchor tier)
- Google Review link — `a` containing `"Write a review"` (text-anchor tier)
- Gate Modal Wrapper — `div.fixed.inset-0.z-50` (utility/class tier)
- Gate Modal Heading — `h2` containing `"Sign in to share"` (text-anchor tier)
- Gate Modal Close button — `button.absolute.top-4.right-4` (utility/class tier)
- Carousel Track — `div.carousel-track` (utility/class tier)
- File Input field — `input[type="file"]` (semantic tier)
