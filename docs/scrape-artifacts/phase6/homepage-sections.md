# Homepage Sections POM Mapping

## 1. Full Section Sweep (Top → Bottom)

### 1.1 Hero Section
* **Section Container**: `section[data-testid="home-hero"]` (class: `relative w-full h-[90vh] lg:h-[85vh] lg:max-h-[800px]`)
* **Heading / Tagline**: `div[data-testid="home-hero-overlay"] > h1` / `StaticText` `"The Next Chapter"`
* **Background Videos**:
  * Desktop: `video[data-testid="home-hero-video-desktop"]`
  * Mobile: `video[data-testid="home-hero-video-mobile"]`
* **Mute Toggle Button**: `button[data-testid="home-hero-mute-toggle"]`
* **CTA Button**: `button[data-testid="home-hero-discover-cta"]` (Label: `"Discover"`, target URL: `/new-release/astonia`)

### 1.2 Two Stories Section (Product Previews)
* **Section Container**: `div[data-testid="home-two-stories"]`
* **Heading**: `p[data-testid="home-two-stories-title"]` (text: `"Two Stories, One Philosophy"`)
* **Astonia Release Card**:
  * **Card Link**: `a[data-testid="home-discover-astonia"]` (target URL: `/new-release/astonia`)
  * **Inner Structure**: Title (`h2` `"Astonia"`), Subheading (`"Swiss Chronograph. Built to be noticed."`), trigger button (`button[data-testid="home-discover-astonia-cta"]` with arrow SVG).
* **Manifesta Release Card**:
  * **Card Link**: `a[data-testid="home-discover-manifesta"]` (target URL: `/new-release/manifesta`)
  * **Inner Structure**: Title (`h2` `"Manifesta"`), Subheading (`"Semi-precious stone dials. A Japanese beating heart."`), locked status label (`div[data-testid="home-discover-manifesta-locked"]` displaying `"SEP, 2026"`).

### 1.3 Brand Story Section (Parallax/Quotes)
* **Section Containers**:
  * Desktop: `section[data-testid="home-parallax-desktop"]` (class: `hidden lg:block relative`)
  * Mobile: `section[data-testid="home-parallax-mobile"]` (class: `lg:hidden relative`)
* **Main Quote**: `h2` containing `"What you wear on your wrist is your drive to becoming."`
* **Paragraphs**:
  * `"Timepieces is for the man in motion..."`
  * `"We build the watch the way he builds himself..."`
* **Teaser Images**: Machining/movement close-up illustration images (e.g. `img[alt="Watch mechanism machining"]`).

### 1.4 Friends & Partners Section
* **Section Container**: `section[data-testid="home-friends-partners"]` (class: `bg-[#060606]`)
* **Heading**: `h2[data-testid="home-friends-partners-title"]` (text: `"Friends & Partners"`)
* **View All CTA**: `a[data-testid="home-friends-partners-view-all"]` (Label: `"View all"`, target URL: `/friends-and-partners`)
* **Carousel Wrapper**: `div[data-testid="home-friends-partners-carousel"]`
* **Carousel Track**: `div[data-testid="home-friends-partners-carousel-track"]`
* **Content Card**: `a[data-testid="home-partner-card"]` (target URL: `/friends-and-partners/<handle>`)
  * **Inner Structure**:
    * Partner image: `img` with descriptive alt (`alt="<Name> wearing Timepieces <Family>"`).
    * Partner name: `h2` (e.g. `"Saif Ali Khan"`).
    * Watch close-up image: `img` with alt (`alt="<Family> Watch"`).
    * Watch family name: `h3` (e.g. `"Manifesta"`).
    * Watch dial style: `span` (e.g. `"Mother of Pearl"`).
* **Carousel Controls**:
  * **Next Button**: `button[data-testid="home-friends-partners-carousel-next"]` (hides/reveals next slide by native scroll offset).
  * **Prev Button**: Not present.

### 1.5 Last Release Section (Product Slider)
* **Section Container**: `section[data-testid="home-product-slider"]`
* **Heading**: `h2[data-testid="home-product-slider-title"]` (text: `"Last Release"`)
* **Subheading**: `p[data-testid="home-product-slider-subtitle"]` (text: `"February 2026. Fully allocated."`)
* **View All CTA**: `a[data-testid="home-product-slider-view-all"]` (Label: `"View all"`, target URL: `/collections/timepieces`)
* **Carousel Wrapper**: `div[data-testid="home-product-slider-carousel"]`
* **Carousel Track**: `div[data-testid="home-product-slider-carousel-track"]`
* **Content Card**: `a[data-testid="home-product-slider-card"]` (target URL: `/products/<handle>`)
  * **Inner Structure**:
    * Product image: `img` with alt matching the family (e.g. `alt="Manifesta"`).
    * Movement description subtitle: `span` (e.g. `"AUTOMATIC"` or `"Q-MATIC"`).
    * Product title: `h3` (e.g. `"Manifesta"`).
* **Carousel Controls**:
  * **Next Button**: `button[data-testid="home-product-slider-carousel-next"]`
  * **Prev Button**: Not present.

### 1.6 Crafted With Intent Section (Scroll Sequence)
* **Section Containers**:
  * Desktop: `section[data-testid="home-image-sequence-desktop"]` (class: `desktop-sequence`)
  * Mobile: `section[data-testid="home-image-sequence-mobile"]` (class: `mobile-sequence`)
* **Intro Heading**: `h2` containing `"Crafted With Intent"`
* **Intro Subheading**: `p` containing `"Every detail considered. Every timepiece earned."`
* **Scroll Sequence Canvas**: `canvas` inside the container wrapper.
* **Text Overlay**: `div.text-container` / `p.heading-text` (value: e.g. `"Highly precise movements engineered for lasting accuracy."`).
* **Behavior**: Scrolls and advances frame sequence inside canvas dynamically.

### 1.7 USP / Marquee Section
* **Section Container**: `section[data-testid="home-usp"]`
* **Heading**: `h2[data-testid="home-usp-title"]` (text: `"FEATURED IN"`)
* **Marquee Track**: `div[data-testid="home-usp-marquee"]` (contains publication logo SVGs scrolling horizontally).

---

## 2. In-Page Navigation CTAs Summary

| CTA Source Section | Selector | Label | Target URL |
| --- | --- | --- | --- |
| **Hero Section** | `button[data-testid="home-hero-discover-cta"]` | `"Discover"` | `/new-release/astonia` |
| **Two Stories (Astonia)** | `a[data-testid="home-discover-astonia"]` | `"Astonia Swiss Chronograph. Built to be noticed. Discover Astonia"` | `/new-release/astonia` |
| **Two Stories (Manifesta)** | `a[data-testid="home-discover-manifesta"]` | `"Manifesta Semi-precious stone dials. A Japanese beating heart. SEP, 2026"` | `/new-release/manifesta` |
| **Friends & Partners** | `a[data-testid="home-friends-partners-view-all"]` | `"View all"` | `/friends-and-partners` |
| **Friends & Partners Cards** | `a[data-testid="home-partner-card"]` | Partner profile card links | `/friends-and-partners/<handle>` |
| **Last Release** | `a[data-testid="home-product-slider-view-all"]` | `"View all"` | `/collections/timepieces` |
| **Last Release Cards** | `a[data-testid="home-product-slider-card"]` | Product details links | `/products/<handle>` |

---

## 3. Raw Inventory & Rollout Gaps

### 3.1 `data-testid` Inventory
* `home-main` (main tags)
* `home-hero` (hero block)
* `home-hero-video-mobile` / `home-hero-video-desktop` (background video nodes)
* `home-hero-mute-toggle` (mute/unmute button)
* `home-hero-overlay` (hero content wrap)
* `home-hero-discover-cta` (hero discover button)
* `home-two-stories` (stories section block)
* `home-two-stories-title` (stories title)
* `home-discover-astonia` (Astonia card link)
* `home-discover-astonia-cta` (Astonia arrow button)
* `home-discover-manifesta` (Manifesta card link)
* `home-discover-manifesta-locked` (Manifesta Locked tag button)
* `home-parallax-desktop` / `home-parallax-mobile` (brand quote wraps)
* `home-friends-partners` (Friends & partners container)
* `home-friends-partners-title` (Partners heading)
* `home-friends-partners-view-all` (Partners view all link)
* `home-friends-partners-carousel` (Partners carousel wrap)
* `home-friends-partners-carousel-track` (Partners track)
* `home-partner-card` (individual partner links)
* `home-friends-partners-carousel-next` (Partners carousel scroll-next arrow)
* `home-product-slider` (Last release container)
* `home-product-slider-title` (Last release heading)
* `home-product-slider-subtitle` (Last release subtext)
* `home-product-slider-view-all` (Last release view all link)
* `home-product-slider-carousel` (Last release carousel wrap)
* `home-product-slider-carousel-track` (Last release track)
* `home-product-slider-card` (individual product card links)
* `home-product-slider-carousel-next` (Last release scroll-next arrow)
* `home-image-sequence-desktop` / `home-image-sequence-mobile` (scroll sequence wraps)
* `home-usp` (USP section wrap)
* `home-usp-title` (Featured in heading)
* `home-usp-marquee` (marquee container)

### 3.2 NO-`data-testid` Elements & Fallback Selectors (`ROLLOUT-GAP`)
* **Hero Heading**:
  * **Selector**: `section[data-testid="home-hero"] div[data-testid="home-hero-overlay"] h1` (Tier: Structural)
* **Brand Quote Text**:
  * **Selector**: `section[data-testid="home-parallax-desktop"] h2, section[data-testid="home-parallax-mobile"] h2` (Tier: Structural)
* **Crafted With Intent Canvas**:
  * **Selector**: `section[data-testid="home-image-sequence-desktop"] canvas` (Tier: Structural)
* **Crafted With Intent Heading Overlay**:
  * **Selector**: `section[data-testid="home-image-sequence-desktop"] div.text-container p` (Tier: Structural/Class)
* **USP Publisher Logos**:
  * **Selector**: `section[data-testid="home-usp"] div[data-testid="home-usp-marquee"] svg` (Tier: Structural)
