# Timepieces World Product Showcase POM Mapping

## 1. Shared-Template Verdict
**VERDICT: UNIFIED SHARED TEMPLATE.**
All 5 product showcase pages (`/products/arvion`, `/products/monarch`, `/products/auriqua`, `/products/astonia`, `/products/manifesta`) share an identical structure, page layout, and styling. The templates are populated dynamically with family-specific content (images, taglines, specs table values). They can all be represented by a single shared POM template class: `SharedTemplate_TimepiecesWorldProduct`.

---

## 2. Per-Family Title & URL List

| Family | URL | Page Title |
| --- | --- | --- |
| **Arvion** | `https://world.timepieces-d2c.example/products/arvion` | `Arvion | Get Early Access to Single-Hand Watch | Timepieces Watches` |
| **Monarch** | `https://world.timepieces-d2c.example/products/monarch` | `Monarch | A Moon Phase Watch Shaped by Roman Heritage | Timepieces Watches` |
| **Auriqua** | `https://world.timepieces-d2c.example/products/auriqua` | `Auriqua | Join Waitlist for the Yacht-Inspired Timepiece | Timepieces Watches` |
| **Astonia** | `https://world.timepieces-d2c.example/products/astonia` | `Astonia | Early Access for Q-Matic Flyback Chronograph | Timepieces Watches` |
| **Manifesta** | `https://world.timepieces-d2c.example/products/manifesta` | `Manifesta | Semi Precious Stone Dial Watch for Men | Timepieces Watches` |

* **Title Pattern**: `{FamilyName} | {UniqueTagline} | Timepieces Watches`

---

## 3. Product Showcase Page Structure

### 3.1 Hero & Branding Section
* **Back to Scene Button**: `main button:has-text("Back to scene")` (returns to `https://world.timepieces-d2c.example`)
* **Family Logo Image**: `main img[alt="{FAMILY_NAME_UPPERCASE}"]` (e.g. `img[alt="ASTONIA"]`, src: `.../Astonia-text.svg`)
* **Family Tagline**: `main h1` / `main img + p` (text: e.g. `"For those who build momentum"`)

### 3.2 Ideation Section
* **Ideation Subheading**: `main h2` inside the ideation block (text: e.g. `"Where speed meets precision & motion becomes mastery."`)
* **Ideation Images**: `img` elements inside the ideation block (e.g., story sketches or yacht design illustrations)

### 3.3 Engineering & Calibre Specs Section
* **Engineering Title**: `h2` / `h3` containing `"Powered by Q-matic™ technology"` or `"Automatic power. Maritime endurance."`
* **Movement Detail Images**: `img` elements showing calibre/movement closeups.
* **Calibre Specs Grid/Table**:
  * **Base Calibre**: `div:has-text("BASE CALIBRE") + div` (e.g. `"TMI VK63"`)
  * **Type**: `div:has-text("TYPE") + div` (e.g. `"Q-matic™"`)
  * **Power Source/Reserve**: `div:has-text("POWER SOURCE") + div` / `div:has-text("POWER RESERVE") + div` (e.g. `"Battery (~3-5 years)"`)
  * **Frequency**: `div:has-text("FREQUENCY") + div` (e.g. `"32,768 Hz"`)

### 3.4 Key Materials Section
* **Stainless Steel 316L CTA**: `button:has-text("Learn more about Stainless Steel 316L")`
* **Sapphire Crystal CTA**: `button:has-text("Learn more about Sapphire Crystal")`
* **Strap/Material CTA**: `button:has-text("Learn more about FKM Rubber")` or `"Learn more about Suede Leather"` (varies by family)

### 3.5 Waitlist Signup Form
* **Waitlist Heading**: `h2:has-text("Join the waitlist for")` (e.g. `"Join the waitlist for Astonia"`)
* **Open Waitlist Trigger Button**: `button:has-text("Join the waitlist")` (scrolls viewport down to the form)
* **Form Inputs**:
  * **Full Name Textbox**: `input[placeholder="Full name*"]` / `input[name="fullName"]`
  * **Email id Textbox**: `input[placeholder="Email id*"]` / `input[name="email"]`
  * **Country Dropdown (Phone)**: `combobox` / `select` displaying country code (starts on `"India"`)
  * **Phone Number Textbox**: `input[type="tel"]`
  * **Postal/Zip code Textbox**: `input[placeholder="Postal/Zip code*"]`
  * **Instagram Username Textbox**: `input[placeholder="Instagram username (optional)"]`
  * **Consent Checkbox**: `input[type="checkbox"]` (labels terms and privacy policies)
  * **Submit Button**: `button[type="submit"]` or `button:has-text("Submit")` (starts disabled and becomes enabled only when all required fields are validated)

### 3.6 Footer Area
* **Back to Top Button**: `button:has-text("Back to top")`
* **Footer Email Link**: `a[href="mailto:reservations@timepieces-d2c.example"]`
* **Footer Instagram Link**: `a[href="https://www.instagram.com/timepieces-world.example"]`
* **Footer LinkedIn Link**: `a[href="https://linkedin.com/company/timepieces"]`

---

## 4. CTA to Main Storefront (`www.timepieces-d2c.example`)
* **Verdict**: **None exist.**
* There are no buttons (e.g. "Buy" or "Explore") linking back to the www storefront PDPs (`www.timepieces-d2c.example/products/<handle>`). The world showcase pages are strictly pre-launch signups for early access.

---

## 5. Raw Inventory & Rollout Gaps

### 5.1 `data-testid` Inventory
There are **no** `data-testid` attributes present on any elements inside the product showcase pages.

### 5.2 NO-`data-testid` Elements & Fallback Selectors (`ROLLOUT-GAP`)
* **Back to Scene Button**:
  * **Selector**: `main button:has-text("Back to scene")` (Tier: Text-Anchor)
* **Family Logo Image**:
  * **Selector**: `main img[alt*="text.svg"]` or `main img[alt="ASTONIA"]` (Tier: Semantic/Attribute)
* **Specs Table Values**:
  * **Selector**: `main div.grid div.text-white` (Tier: Structural)
* **Learn More Material Buttons**:
  * **Selector**: `button:has-text("Learn more about")` (Tier: Text-Anchor)
* **Waitlist Textbox Inputs**:
  * **Selector**: `input[placeholder*="Full name"]`, `input[placeholder*="Email id"]`, `input[placeholder*="Postal/Zip code"]` (Tier: Attribute)
* **Waitlist Consent Checkbox**:
  * **Selector**: `input[type="checkbox"]` (Tier: Semantic/Tag)
* **Waitlist Submit Button**:
  * **Selector**: `button:has-text("Submit")` (Tier: Text-Anchor)
