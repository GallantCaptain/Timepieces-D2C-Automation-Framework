# Legal & Policy Pages Scrape (Phase 3, Chunk 3A)

## URL Discovery (Step 0)

Discovered URLs from the Timepieces homepage footer legal links (specifically via `a[data-testid='footer-legal']` which points to `/policies`):

1. **Guarantee Policy**: 
   - URL: `https://www.timepieces-d2c.example/policies/guarantee-policy`
   - Link text: `"Guarantee Policy"`
2. **Privacy Policy**: 
   - URL: `https://www.timepieces-d2c.example/policies/privacy-policy`
   - Link text: `"Privacy Policy"`
3. **Refund Policy**: 
   - URL: `https://www.timepieces-d2c.example/policies/refund-policy`
   - Link text: `"Refund Policy"`
4. **Shipping Policy**: 
   - URL: `https://www.timepieces-d2c.example/policies/shipping-policy`
   - Link text: `"Shipping Policy"`
5. **Terms of Service**: 
   - URL: `https://www.timepieces-d2c.example/policies/terms-of-service`
   - Link text: `"Terms of Service"`

---

## Shared Template Verdict

**Verdict**: Yes, the 5 pages share the **same main structural layout** and wrapper containers, with minor differences in heading tags.

### Shared Selectors:
- **Main Container**: `div.prose.prose-lg.prose-invert` (Tier: Utility/Class, data-testid: no)
- **Outer Wrapper**: `div.max-w-4xl.mx-auto.px-4.sm\:px-6.lg\:px-8.py-16` (Tier: Utility/Class, data-testid: no)
- **Back Navigation**: `a` containing text `"Back to Policies"` (Tier: Text-Anchor, data-testid: no)
- **Bottom Navigation**: `a` containing text `"View all policies"` (Tier: Text-Anchor, data-testid: no)

### Structural Differences:
- **Heading**: Guarantee Policy, Privacy Policy, Refund Policy, and Terms of Service all use `h1` for the page title. However, **Shipping Policy has no `h1`** and uses a `h2` element for its main title: `h2` containing `"Timepieces Shipping Policy"`.

---

## Per-Page Content Details

### 1. Guarantee Policy Page
- **Page `<title>`**: `Guarantee Policy | Timepieces`
- **Main Heading**: `h1` -> `"Guarantee Policy"`
- **Last Updated Date**: `December 12, 2025`
- **Content Structure**: Section headings use `h2` (`1. Guarantee Coverage`, `2. What Is Not Covered`, etc.) inside the `div.prose` container.
- **Contact Links**: `mailto:support@timepieces-d2c.example` (link), `www.timepieces-d2c.example` (link).
- **Interactive Elements**: Standard navigation links. No unique interactive drawers or downloads.

### 2. Privacy Policy Page
- **Page `<title>`**: `Privacy Policy | Timepieces`
- **Main Heading**: `h1` -> `"Privacy Policy"`
- **Last Updated Date**: `11th July 2026`
- **Content Structure**: Section headings use `h2` (`1. Who We Are (Data Controller)`, `2. Information We Collect`, etc.) inside the `div.prose` container.
- **Contact Links**: `mailto:privacy@timepieces-d2c.example` (plain text under Contact: `"privacy@timepieces-d2c.example"` and link `privacy@timepieces-d2c.example`).
- **Interactive Elements**: Standard navigation links.

### 3. Refund Policy Page
- **Page `<title>`**: `Refund Policy | Timepieces`
- **Main Heading**: `h1` -> `"Refund & Exchange Policy"`
- **Last Updated Date**: `December 12, 2025`
- **Content Structure**: Section headings use `h2` (`1. No Returns or Refunds`, `2. Exchange Eligibility`, etc.) inside the `div.prose` container.
- **Contact Links**: `mailto:support@timepieces-d2c.example` (link).
- **Interactive Elements**: Standard navigation links.

### 4. Shipping Policy Page
- **Page `<title>`**: `Shipping Policy | Timepieces`
- **Main Heading**: `h2` -> `"Timepieces Shipping Policy"`
- **Last Updated Date**: None explicitly marked.
- **Content Structure**: Section headings use `h2` (`2. Order Processing 🇮🇳`, `3. Shipping Within India`, etc.) and `h3` (`1. How Your Watch is Made`) inside the `div.prose` container.
- **Contact Links**: `support@timepieces-d2c.example` (text).
- **Interactive Elements**: Standard navigation links.

### 5. Terms of Service Page
- **Page `<title>`**: `Terms of Service | Timepieces`
- **Main Heading**: `h1` -> `"Terms of Service"`
- **Last Updated Date**: `December 12, 2025`
- **Content Structure**: Section headings use `h2` (`1. Eligibility & Acceptance`, `2. Use of the Website`, etc.) inside the `div.prose` container.
- **Contact Links**: `mailto:support@timepieces-d2c.example` (link), `https://timepieces-d2c.example/policies/shipping-policy` (link), `https://timepieces-d2c.example/policies/privacy-policy` (link).
- **Interactive Elements**: Standard navigation links.

---

## Raw Inventory & Fallback Selectors

### Raw data-testid Inventory
- `site-header` (header) — Site header banner
- `header-menu-toggle` (button) — Mobile menu toggle trigger
- `header-stories-toggle` (button) — Open stories modal trigger
- `header-logo` (a) — Home page link
- `header-search-toggle` (button) — Mobile search panel toggle trigger
- `header-account-toggle` (button) — User authentication trigger
- `header-cart-toggle` (button) — User shopping cart drawer trigger
- `site-footer` (footer) — Main footer container
- `footer-mobile` (div) — Footer wrapper on mobile
- `footer-accordion` (div) — Accordion section wrapper in footer
- `footer-accordion-toggle` (button) — Accordion category toggle button
- `footer-accordion-link` (a) — Footer links
- `footer-socials` (div) — Social links container
- `footer-social-link` (a) — Individual social site link
- `country-selector` (div) — Currency/region container
- `country-selector-toggle` (button) — Open currency/region list dropdown
- `footer-legal` (a) — Legal policy link
- `footer-sitemap` (a) — Site map link
- `footer-copyright` (p) — Site copyright text
- `footer-desktop` (div) — Desktop footer container
- `footer-menu-column` (div) — Desktop footer menu column wrapper
- `footer-menu-link` (a) — Desktop footer menu individual link

### Elements with NO data-testid (fallback selectors)
- Back button — `a` containing `"Back to Policies"` (text-anchor tier)
- Bottom link — `a` containing `"View all policies"` (text-anchor tier)
- Policy Main Heading (Guarantee, Privacy, Refund, Terms) — `h1` (semantic tier)
- Policy Main Heading (Shipping) — `h2` containing `"Timepieces Shipping Policy"` (text-anchor tier)
- Last updated text — `span` or `p` containing `"Last Updated:"` (text-anchor tier)
- Prose container — `div.prose.prose-lg.prose-invert` (utility/class tier)
- Policy subheadings — `h2` or `h3` inside the prose container (semantic tier)
- Support/Privacy mailto links — `a[href^="mailto:"]` (attribute tier)
