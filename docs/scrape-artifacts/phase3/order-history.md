# Order History Page POM Mapping

## 1. Page Info & Access
* **URL**: `https://www.timepieces-d2c.example/account/orders`
* **Page Title**: `"Timepieces Watches | Early Access to Limited Edition Pieces"`
* **Main Heading**: `"Orders"` (`h1` with class `text-white text-2xl md:text-3xl font-semibold` and `data-testid="account-page-title"`)
* **Breadcrumbs**: `nav` with `data-testid="account-breadcrumbs"` containing profile link `a[href="/account"]` ("My Profile") and current page text `"Orders"`.

---

## 2. Empty State Map
If the account has no orders:
* **Empty State Container**: `div[data-testid="orders-empty"]` (class: `flex flex-col items-center justify-center py-16`)
* **Empty State Illustration**: `img[alt="logo"]` with src `https://cdn.shopify.com/.../watch.svg?v=1769174134` (class: `w-18 h-18`)
* **Empty State Title**: `h2` containing text `"This space awaits your first Timepieces."` (class: `text-white text-xl font-semibold mb-2`)
* **Empty State Subtext**: `p` containing text `"A timepiece chosen thoughtfully belongs here."` (class: `text-gray-400 text-center mb-6 max-w-md`)
* **Continue Shopping CTA**: `a[data-testid="orders-empty-cta"]` with href `"/collections/timepieces"` (class: `px-8 py-3 bg-[#007A61] text-white font-medium rounded-full hover:bg-[#3d8577] transition-colors`)

---

## 3. Populated State: Order Cards
If orders exist:
* **List Container**: `div[data-testid="orders-list"]` (class: `space-y-4 md:space-y-0 md:grid md:grid-cols-3 md:gap-5`)
* **Order Card Selector**: `div[data-testid="order-card"]`
  * Contains attribute `data-order-number="Timepieces{OrderNo}"` (e.g. `data-order-number="Timepieces3306"`).
* **Order Details Link**: `a[data-testid="order-card-link"]`
  * Href format: `/account/orders/Timepieces{OrderNo}` (e.g. `/account/orders/Timepieces3306`).
* **Final Status Label**: `p[data-testid="order-card-status"]`
  * Text value: e.g. `"Cancelled"` (class: `font-semibold`, colored according to status e.g. `text-[#FF4444]` for Cancelled).
* **Status Date**: `p` next to final status (class: `text-gray-400 text-sm`)
  * Text format: `"on <date>"` (e.g., `"on 19 Jun 2026"`).
* **Order Card Item Title**: `h3[data-testid="order-card-title"]`
  * Text value: `"Order Item"` (class: `text-white font-medium md:text-[16px] mb-1`).
* **Order ID**: `p` next to item title (class: `text-gray-500 text-sm`)
  * Text format: `"Order #<internalOrderId>"` (e.g., `"Order #6814875680812"`).

---

## 4. Raw Inventory & Rollout Gaps

### 4.1 `data-testid` Inventory
* `orders-page` (main root container)
* `account-page-header` (header component wrapper)
* `account-page-title` (main orders heading)
* `account-breadcrumbs` (breadcrumb nav)
* `orders-error` (error display box, if fetch fails)
* `orders-empty` (empty state wrapper)
* `orders-empty-cta` (empty state "Continue Shopping" link)
* `orders-list` (order cards list wrapper)
* `order-card` (individual order card container)
* `order-card-link` (link wrapper to details page)
* `order-card-status` (order status label)
* `order-card-title` (card item title)

### 4.2 NO-`data-testid` Elements & Fallback Selectors (`ROLLOUT-GAP`)
* **Order Date Text**:
  * **Selector**: `div[data-testid="order-card"] a[data-testid="order-card-link"] div p.text-gray-400` (Tier: Structural)
* **Order ID Number**:
  * **Selector**: `div[data-testid="order-card"] div.p-3 div.flex-1 p.text-gray-500` (Tier: Structural)
* **Card Icon / Image**:
  * **Selector**: `div[data-testid="order-card"] img[alt="logo"]` (Tier: Semantic/Attribute)
