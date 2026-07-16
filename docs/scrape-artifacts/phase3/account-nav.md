# Account Page Navigation POM Mapping

## 1. Page Info & Access
* **URL**: `https://www.timepieces-d2c.example/account`
* **Access**: Requires authentication. Persistent cookies allow subsequent access.

---

## 2. Profile Summary Block Selectors
* **Profile Image / Avatar**:
  * **Selector**: `img[alt="Profile"]` (inside `div.w-\[100px\].h-\[100px\]` or `div.rounded-full`)
  * **Value**: `<redacted>` (image URL)
* **Name**:
  * **Selector**: `div.flex.flex-col.items-center.text-center.px-4.mt-3 > h1`
  * **Value**: `<redacted>` (e.g. "Avinash Rath")
* **Designation / Profession**:
  * **Selector**: `div.flex.flex-col.items-center.text-center.px-4.mt-3 > p.text-gray-400`
  * **Value**: `<redacted>` (e.g. "Professional")
* **LinkedIn Profile Link**:
  * **Selector**: `div.flex.flex-col.items-center.text-center.px-4.mt-3 > h1 > a[href*="linkedin.com"]`
  * **Value**: `<redacted>` (URL)
* **Export Profile Button**:
  * **Selector**: `button:has-text("Export Profile")` / `button.flex.items-center.gap-2`
  * **Label**: `"Export Profile"`
* **Edit Profile Button**:
  * **Selector**: `a[href="/account/profile"]`
  * **Label**: `"Edit profile"`

---

## 3. Account Navigation Links
* **Owned Timepieces**:
  * **Selector**: `a[href="/account/owned-watches"]`
  * **Label**: `"Owned timepieces Manage your timepieces"` (contains `h3` `"Owned timepieces"` and `p` `"Manage your timepieces"`)
* **Order History**:
  * **Selector**: `a[href="/account/orders"]`
  * **Label**: `"Orders Track and manage your orders"` (contains `h3` `"Orders"` and `p` `"Track and manage your orders"`)
* **Wishlist**:
  * **Selector**: `a[href="/wishlist"]`
  * **Label**: `"Wishlist View your saved timepieces"` (contains `h3` `"Wishlist"` and `p` `"View your saved timepieces"`)
* **Logout Button**:
  * **Selector**: `button.w-full.flex.items-center.justify-between:has(span.text-red-500)` / `button:has-text("Logout")`
  * **Label**: `"Logout"`

---

## 4. Raw Inventory & Rollout Gaps

### 4.1 `data-testid` Inventory
There are **no** `data-testid` attributes present on any elements inside the main content area of this page.

### 4.2 NO-`data-testid` Elements & Fallback Selectors (`ROLLOUT-GAP`)
* **Profile Image Wrapper**:
  * **Selector**: `div.flex.justify-center.mt-24 > div.rounded-full` (Tier: Structural)
* **Profile Name Element**:
  * **Selector**: `h1.text-white.text-xl.font-semibold` (Tier: Semantic/Class)
* **Designation Element**:
  * **Selector**: `p.text-gray-400.text-sm.mt-1` (Tier: Semantic/Class)
* **Export Profile Button**:
  * **Selector**: `button.flex.items-center.gap-2.text-white` (Tier: Class)
* **Edit Profile Link**:
  * **Selector**: `a[href="/account/profile"]` (Tier: Attribute)
* **Owned Timepieces Navigation Link**:
  * **Selector**: `a[href="/account/owned-watches"]` (Tier: Attribute)
* **Orders Navigation Link**:
  * **Selector**: `a[href="/account/orders"]` (Tier: Attribute)
* **Wishlist Navigation Link**:
  * **Selector**: `a[href="/wishlist"]` (Tier: Attribute)
* **Logout Button**:
  * **Selector**: `button.w-full.flex.items-center.justify-between.py-5` (Tier: Class/Structure)
