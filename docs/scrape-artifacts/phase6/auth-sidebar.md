# Authenticated Sidebar & Header POM Mapping

## 1. Logged-in Sidebar User Container
When a user is authenticated, the guest "Sign In" button is replaced by a personalized user profile container card.

* **Profile Container Card (Selector)**: `a[data-testid="nav-sidebar-account-link"]` (href: `/account`, Tier: TestID)
* **Click Action**: Clicking the container card (or any of its child elements) navigates directly to the customer profile page (`/account`).

### Child Elements inside Profile Container:
1. **User Avatar Wrapper**:
   * **Selector**: `a[data-testid="nav-sidebar-account-link"] div.w-13.h-13` (Tier: Structural)
2. **User Avatar Image**:
   * **Selector**: `a[data-testid="nav-sidebar-account-link"] img[alt="User avatar"]` (Tier: Structural/Attr)
   * **Source URL**: `https://prelaunch-timepieces.s3.ap-south-1.amazonaws.com/become-more/linkedin-profile-<id>.jpg` (Redacted personal value)
3. **Display Name**:
   * **Selector**: `a[data-testid="nav-sidebar-account-link"] span.font-timepieces-semibold` (Tier: Class/Semantic)
   * **Value**: `"Avinash Rath"` (Redacted personal value)
4. **Designation / Persona**:
   * **Selector**: `a[data-testid="nav-sidebar-account-link"] span.font-timepieces-regular` (Tier: Class/Semantic)
   * **Value**: `"Professional"`
5. **Chevron Right Icon**:
   * **Selector**: `a[data-testid="nav-sidebar-account-link"] svg` (Tier: Structural)

---

## 2. Guest vs. Authenticated Menu Differences

The sidebar navigation dynamically modifies entries based on the user's login state:

### 2.1 Authenticated-Only Sidebar Entries
* **Profile Container**: `a[data-testid="nav-sidebar-account-link"]` (target URL: `/account`, replaces `"Sign In"` button and its subtext message).
* **Orders Link**: `a[data-testid="nav-sidebar-orders"]` (target URL: `/account/orders`, label: `"Orders"`, replaces `"Track Orders"` button).

### 2.2 Guest-Only Sidebar Entries
* **Sign In Button**: `button[data-testid="nav-sidebar-signin"]` (opens login overlay/view).
* **Become Member Message**: `//p[contains(normalize-space(), "Become a Timepieces member...")]` (accompanies Sign In button).
* **Track Orders Button**: `button[data-testid="nav-sidebar-track-orders"]` (redirects to guest order tracking page).

### 2.3 Shared Sidebar Entries (All States)
* **Close Menu**: `button[data-testid="nav-sidebar-close"]`
* **Sidebar Logo**: `a[data-testid="nav-sidebar-logo"]` -> `/`
* **Timepieces Expandable Sub-Menu**: `button[data-testid="nav-sidebar-menu-button"][data-menu-title="Timepieces"]`
* **Accessories Expandable Sub-Menu**: `button[data-testid="nav-sidebar-menu-button"][data-menu-title="Accessories"]`
* **Friends & Partners Link**: `a[data-testid="nav-sidebar-menu-link"][href="/friends-and-partners"]`
* **Timepieces House Link**: `a[data-testid="nav-sidebar-menu-link"][href="/experience-center"]`
* **The Wall Link**: `a[data-testid="nav-sidebar-menu-link"][href="/timepieces-wall"]`
* **Updates Link**: `a[data-testid="nav-sidebar-menu-link"][href="/updates"]`
* **Timepieces World Link**: `a[data-testid="nav-sidebar-menu-link"][href="https://world.timepieces-d2c.example"]`
* **Contact Link**: `a[data-testid="nav-sidebar-contact"]` -> `/contact`
* **Wishlist Link**: `a[data-testid="nav-sidebar-wishlist"]` -> `/wishlist`
* **Country / Region Selector**: `button[data-testid="country-selector-toggle"]`

---

## 3. Sidebar & Header Counters
* **Wishlist Counter**: No dynamic indicator/badge is visible in the sidebar or header (displays raw static `"Wishlist"` link).
* **Shopping Cart Counter**: The cart toggle button uses a text/badge layout: `button[data-testid="header-cart-toggle"]` (value: `"Shopping cart with 0 items"`).

---

## 4. Authenticated Header State
* **Account Toggle Button**: `button[data-testid="header-account-toggle"]`
  * **Icon Layout**: Retains the standard outline avatar SVG icon (no dynamic profile photo avatar rendered in the top header).
  * **Click Action**: Clicking the account toggle button when logged in triggers a direct hard navigation to the account details page `/account`. (Unlike in guest state, where clicking it navigates to `/account` but gets intercepted and redirected to `/account/login`).

---

## 5. Raw Inventory & Rollout Gaps (Sidebar Menu)

### 5.1 `data-testid` Inventory (Authenticated Menu)
* `nav-sidebar-close` (Close drawer button)
* `nav-sidebar-logo` (Timepieces branding link)
* `nav-sidebar-menu-button` (Expandable folders: Timepieces, Accessories)
* `nav-sidebar-menu-link` (Direct page links: Friends & Partners, Timepieces House, The Wall, Updates, Timepieces World)
* `nav-sidebar-account-link` (User profile container block)
* `nav-sidebar-contact` (Contact page link)
* `nav-sidebar-wishlist` (Wishlist page link)
* `nav-sidebar-orders` (Orders history page link)
* `country-selector-toggle` (Region listbox toggle)

### 5.2 NO-`data-testid` Elements & Fallback Selectors (`ROLLOUT-GAP`)

| Element | Selector | Tier |
| --- | --- | --- |
| **User avatar container** | `a[data-testid="nav-sidebar-account-link"] div.w-13.h-13` | Structural |
| **User avatar image** | `a[data-testid="nav-sidebar-account-link"] img` | Structural |
| **User display name text** | `a[data-testid="nav-sidebar-account-link"] span.font-timepieces-semibold` | Class/Semantic |
| **User designation label** | `a[data-testid="nav-sidebar-account-link"] span.font-timepieces-regular` | Class/Semantic |
| **Profile container arrow** | `a[data-testid="nav-sidebar-account-link"] svg` | Structural |
