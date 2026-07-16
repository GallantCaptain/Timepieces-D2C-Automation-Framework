# Onboarding Become More POM Mapping

## 0. Access & Entry
* **Gate Behavior**: Reachable **LOGGED OUT** (guest view). Does NOT redirect to login; it is a public-facing onboarding funnel.
* **Page Title**: `"Timepieces Watches | Early Access to Limited Edition Pieces"`
* **Root/Container Selector**: `div.min-h-screen.text-white.flex.flex-col` (class-based, wrapper under `main`)

---

## 1. The Step Journey

### Intro / Start Screen
* **URL**: `https://www.timepieces-d2c.example/becomemore`
* **Heading**: `"Become More"` (`h1` inside `main`)
* **Graphics Image**: `img[alt="Timepieces"]` (source: `Graphics_Container.webp`)
* **Begin Button**: `button` containing `"Begin your Profile"` (class: `block w-full bg-[#2f7d5a] text-white py-4`)
* **Footer Text**: `"Takes less than 60 seconds"`

### Method Selector Dialog (Step 0)
* **URL**: `https://www.timepieces-d2c.example/becomemore`
* **Heading**: `"How do you like to fill your profile?"` (`h2`)
* **LinkedIn Import Button**: `button` containing `"Import data from LinkedIn"` (class: `w-full flex ...`)
* **Manual Entry Button**: `button` containing `"Start Manually"` (class: `w-full flex ...`)
* **Close Button**: `button` containing `"Close"` (upper left of container)
* **Policy Links**:
  * Privacy Policy: `a` containing `"Privacy Policy"` -> `/policies/privacy-policy`
  * Terms & Conditions: `a` containing `"Terms & Conditions"` -> `/policies/terms-of-service`

### Step 1: Personal Info (`name`)
* **URL**: `https://www.timepieces-d2c.example/becomemore/profile?section=name`
* **Step Indicator**: `"Step 01 of 07"` (`span` or text layout)
* **Heading**: `"Tell us about yourself."` (`h1`)
* **Subheading**: `"Let's start with your name."`
* **Input Fields**:
  * **Full Name Textarea**: `textarea[placeholder="Enter your full name"]` (class: `resize-none whitespace-nowrap w-full border-b border-black`)
  * **Salutation Dropdown**: `select` (first select on page, options: `Mr`, `Mrs`, `Ms`, `Prefer not to say`)
  * **Date of Birth Textarea**: `textarea[placeholder="DD/MM/YYYY"]` (class: `resize-none whitespace-nowrap w-full border-b border-black`)
  * **Persona Dropdown**: `select` (second select on page, options: `Entrepreneur`, `Investor`, `Professional`, `Doctor`, `Creator`, `Athlete`, `Student`, `Lawyer`, `Artist`, `Executive`, `Officer`, `Politician`, `Other`)
* **Progression Controls**:
  * **Back Button**: `button` with back arrow SVG (resides in upper left corner of main content)
  * **Cancel Button**: `button` containing `"Cancel"` (resides in upper right corner of main content)
  * **Next Button**: `button[type="submit"]` (disabled by default with class `bg-gray-300 cursor-not-allowed` until name, salutation, DOB, and persona are filled and validated).

### Step 2: Location (`location`)
* **URL**: `https://www.timepieces-d2c.example/becomemore/profile?section=location`
* **Step Indicator**: `"Step 02 of 07"`
* **Heading**: `"Where are you based?"` (`h1`)
* **Subheading**: `"Timepieces is worn by ambitious people across the world..."`
* **Input Fields**:
  * **City Textarea**: `textarea[placeholder="City"]` (class: `resize-none whitespace-nowrap w-full`)
  * **Use Location Button**: `button` next to the input (class: `p-2 text-gray-600`)
* **Progression Controls**:
  * Back, Cancel, and Next (`button[type="submit"]` with class `w-16 h-16`)

### Step 3: Bio (`about`)
* **URL**: `https://www.timepieces-d2c.example/becomemore/profile?section=about`
* **Step Indicator**: `"Step 03 of 07"`
* **Heading**: `"More about you"` (`h1`)
* **Subheading**: `"This helps us understand you a little better."`
* **Input Fields**:
  * **Bio Textarea**: `textarea[placeholder*="product marketing at a SaaS company"]` (class: `flex-1 text-[16px] py-2 resize-none`)
  * **Voice Input Button**: `button` containing microphone SVG (class: `w-10 h-10 ml-1`)
  * **AI Refine Button**: `button` containing `"Refine my bio"`
  * **Examples Help Button**: `button` containing `"See examples"`
* **Progression Controls**:
  * Back, Cancel, and Next (`button[type="submit"]`)

### Step 4: Goals (`building`)
* **URL**: `https://www.timepieces-d2c.example/becomemore/profile?section=building`
* **Step Indicator**: `"Step 04 of 07"`
* **Heading**: `"What are you building towards?"` (`h1`)
* **Subheading**: `"Every Timepieces owner is building something meaningful..."`
* **Input Fields**:
  * **Goals Textarea**: `textarea[placeholder*="product leadership role"]` (class: `flex-1 text-[16px] py-2 resize-none`)
  * **Voice Input Button**: `button` containing microphone SVG
  * **AI Refine Button**: `button` containing `"Refine my story"`
  * **Examples Help Button**: `button` containing `"See examples"`
* **Progression Controls**:
  * Back, Cancel, and Next (`button[type="submit"]`)

### Step 5: Watch Journey (`watchjourney`)
* **URL**: `https://www.timepieces-d2c.example/becomemore/profile?section=watchjourney`
* **Step Indicator**: `"Step 05 of 07"`
* **Heading**: `"Tell us about your watch journey"` (`h1`)
* **Choice Chips (Single-select)**:
  * **Options**:
    * `"This would be my first serious watch"`
    * `"I own 1–3 watches"`
    * `"I own 4–10 watches"`
    * `"I actively collect watches"`
    * `"I own premium or luxury timepieces"`
  * **Active State Encoding**: Border transitions from `border-gray-200` to `border-black`.
  * **Behavior**: Clicking any option immediately auto-submits/advances to the next step.

### Step 6: Interests (`interest`)
* **URL**: `https://www.timepieces-d2c.example/becomemore/profile?section=interest`
* **Step Indicator**: `"Step 06 of 07"`
* **Heading**: `"What interests you?"` (`h1`)
* **Choice Chips (Multi-select)**:
  * **Options**: `"Entrepreneurship"`, `"Investing"`, `"Watches"`, `"Technology"`, `"Design"`, `"Cars"`, `"Travel"`, `"Fitness"`, `"Food"`, `"Art"`, `"Music"`, `"Other"`
  * **Active State Encoding**: Unselected chip uses `bg-[#f2f2f2] text-[#6b6b6b]`; Selected chip uses `bg-[#141414] text-white`.
  * **Progression**: Next button becomes enabled after selecting at least one option.

### Step 7: The Timepieces House (`timepieceshouse`)
* **URL**: `https://www.timepieces-d2c.example/becomemore/profile?section=timepieceshouse`
* **Step Indicator**: `"Step 07 of 07"`
* **Heading**: `"The Timepieces House"` (`h1`)
* **Input Fields**:
  * **House Textarea**: `textarea[placeholder*="Anand Mahindra"]`
  * **Inspiration Button**: `button` containing `"Need inspiration?"`
    * *Behavior*: Clicking renders `div` `"Curated inspirations"` (`h2`) with recommendations `"Satya Nadella • Jensen Huang • Sundar Pichai • Sam Altman • Demis Hassabis"`.
  * **Skip Button**: `button` containing `"I'll do this later"` (skips step, advances immediately).
* **Progression Controls**:
  * Back, Cancel, and Next (Next button is enabled by default here to allow empty submits).

### Step 8: Profile Photo & Submit (`photo`)
* **URL**: `https://www.timepieces-d2c.example/becomemore/profile?section=photo`
* **Step Indicator**: None (this is the final submission step)
* **Heading**: `"Put a face to the story"` (`h1`)
* **Greeting**: `"You're almost there,"`
* **Input Fields**:
  * **Avatar Upload Button**: `button.relative.w-44.h-44`
  * **Upload Camera Button Overlay**: `button.absolute.bottom-1.right-1` (triggers hidden `input[type="file"]`)
  * **Consent Checkbox**: `input[type="checkbox"]` (class: `accent-[#141414]`)
    * Text: `"I agree to Timepieces storing and processing my information to create my Timepieces Profile"`
* **Progression Controls**:
  * **Back / Cancel**: Standard navigation
  * **Submit Profile Button**: `button` containing `"Submit Profile"`
    * *Behavior*: Disabled by default (`disabled` HTML attribute). Checked consent checkbox removes the `disabled` attribute, enabling submission.

---

## 2. Completion State & Submission Safety
* **Safety Hold**: **Stopped at Step 8.** Did not click the final "Submit Profile" button to avoid committing test data/altering account profiles in production.
* **Estimated Completion Screen**: Submitting the profile is expected to redirect the user to their finalized profile canvas page `/account` or a success screen showing their personalized profile card.

---

## 3. Raw Inventory & Rollout Gaps

### 3.1 `data-testid` Inventory
There are **zero (0) page-specific data-testids** on the onboarding flow pages. The only testids found are standard header items:
* `site-header`
* `header-menu-toggle`
* `header-stories-toggle`
* `header-logo`
* `header-search-toggle`
* `header-account-toggle`
* `header-cart-toggle`

### 3.2 NO-`data-testid` Elements & Fallback Selectors (`ROLLOUT-GAP`)

| Step / Element | Selector | Tier |
| --- | --- | --- |
| **Intro** Begin button | `main button:has-text("Begin your Profile")` | Semantic/Text |
| **Method** Start Manually button | `main button:has-text("Start Manually")` | Semantic/Text |
| **Step 1** Full name | `textarea[placeholder="Enter your full name"]` | Structural/Attr |
| **Step 1** Salutation | `select:nth-of-type(1)` | Structural |
| **Step 1** Date of Birth | `textarea[placeholder="DD/MM/YYYY"]` | Structural/Attr |
| **Step 1** Persona | `select:nth-of-type(2)` | Structural |
| **Step 2** City | `textarea[placeholder="City"]` | Structural/Attr |
| **Step 3** Bio | `textarea[placeholder*="SaaS company"]` | Structural/Attr |
| **Step 4** Goals | `textarea[placeholder*="leadership role"]` | Structural/Attr |
| **Step 5** Watch Journey Choices | `form button[type="button"]` (matched by index or text) | Structural |
| **Step 6** Interest Choices | `form button[type="button"]` (matched by text) | Structural |
| **Step 7** House Textarea | `textarea[placeholder*="Anand Mahindra"]` | Structural/Attr |
| **Step 7** Later / Skip | `button:has-text("I'll do this later")` | Semantic/Text |
| **Step 8** Consent Checkbox | `input[type="checkbox"]` | Structural |
| **Step 8** Submit button | `button:has-text("Submit Profile")` | Semantic/Text |
| **Global** Back arrow button | `main button:has(svg)` (in upper left) | Structural |
| **Global** Cancel button | `main button:has-text("Cancel")` | Semantic/Text |
| **Global** Step indicator | `main span:contains("Step")` | Semantic/Text |
