# Order Details Page POM Mapping

## 1. Page Info & Access
* **URL**: `https://www.timepieces-d2c.example/account/orders/Timepieces{OrderNo}` (e.g. `Timepieces3306`)
* **Page Title**: `"Timepieces Watches | Early Access to Limited Edition Pieces"`
* **Page Container**: `div[data-testid="order-detail-page"]`
* **Navigation Back Link**: `a[data-testid="order-detail-back"]` with href `"/account/orders"` (text: `"Back"`).

---

## 2. Order Information Selectors

### 2.1 Order Title & Header
* **Order Name / Title**: `h1[data-testid="order-detail-title"]`
  * Text value: `"Order Item"` (class: `text-white text-xl font-bold`).
* **Order Variant**: `p[data-testid="order-detail-variant"]`
  * Text value: e.g. empty or variant description (class: `text-gray-500 text-sm mt-1`).
* **Order Status Container**: `div[data-testid="order-detail-status"]`
* **Order Status Label**: `p[data-testid="order-detail-status-label"]`
  * Text value: e.g. `"Cancelled"` (class: `font-semibold`, colored by status).
* **Order Status Date**: `p[data-testid="order-detail-status-label"] + p`
  * Text format: `"on <day>, <date>"` (e.g., `"on Fri, 19 Jun, 2026"`).

### 2.2 Pricing Summary
* **Total Item Price Toggle Button**: `button[data-testid="order-detail-price-toggle"]`
* **Total Item Price Label**: `span[data-testid="order-detail-item-price"]`
  * Text value: e.g., `"₹0"` (class: `text-white font-semibold`).
* **Total Order Price Label**: `div[data-testid="order-detail-total"]` or `p[data-testid="order-detail-total"]`
  * Text value: e.g., `"₹8,250"` (class: `text-white font-semibold text-lg`).
* **View Price Breakup Button**: `button[data-testid="order-detail-view-breakup"]`
  * Label: `"View Breakup"`

### 2.3 Order IDs & Details
* **Order ID value**: `span[data-testid="order-detail-order-id"]`
  * Text value: e.g. `"6814875680812"` (class: `text-white text-sm`).
* **Payment Status**: `span[data-testid="order-detail-payment-status"]`
  * Text value: e.g. `"refunded"` (class: `text-sm capitalize`, e.g. `text-green-400`).

---

## 3. Shipping & Billing Addresses (Redacted)
* **Shipping Address Container**: `div[data-testid="order-detail-shipping-address"]`
  * **Recipient Name**: `div[data-testid="order-detail-shipping-address"] span.font-medium` -> `<redacted>` (e.g. "Avinash Rath")
  * **Recipient Phone**: `div[data-testid="order-detail-shipping-address"] span:nth-of-type(3)` -> `<redacted>` (e.g. "+919000000001")
  * **Address Lines**: `div[data-testid="order-detail-shipping-address"] p.text-gray-400` -> `<redacted>` (e.g. "Test - Payment, Test, New York 12344")
* **Billing Address Container**: `div[data-testid="order-detail-billing-address"]`
  * **Recipient Name**: `div[data-testid="order-detail-billing-address"] span.font-medium` -> `<redacted>`
  * **Recipient Phone**: `div[data-testid="order-detail-billing-address"] span:nth-of-type(3)` -> `<redacted>`
  * **Address Lines**: `div[data-testid="order-detail-billing-address"] p.text-gray-400` -> `<redacted>` (e.g. "Test - Payment, Test 12344")

---

## 4. Invoicing (Delivered Only)
* **Get Invoice Button**: `button[data-testid="order-detail-invoice"]` (class: `w-full py-3.5 bg-white border border-[#3a3a3a] rounded-lg text font-medium hover:bg-[#1a1a1a] transition-colors flex items-center justify-center gap-2`).
  * Only rendered/visible if the order is delivered (otherwise container is `hidden`).

---

## 5. Contact Support / Help Drawer
* **Help Button**: `button[data-testid="order-detail-help"]` (text: `"Help"`, class: `flex items-center gap-2 px-4 py-2 rounded-full border border-[#3a3a3a] text-gray-300 hover:text-white hover:border-gray-500 text-sm transition-colors cursor-pointer`)
* **Help drawer behavior**: Contrary to Javadoc flagging it as "currently bugged", clicking this button successfully opens the "Support Request" sheet/modal overlay in the DOM.
* **Support Drawer Selectors**:
  * **Backdrop**: `div[data-testid="support-sheet-backdrop"]`
  * **Mobile Sheet Container**: `div[data-testid="support-sheet-mobile"]`
  * **Mobile Close Button**: `button[data-testid="support-sheet-close-mobile"]`
  * **Desktop Sheet Container**: `div[data-testid="support-sheet-desktop"]`
  * **Desktop Close Button**: `button[data-testid="support-sheet-close-desktop"]`
  * **Subject Selection Toggle**: `button[data-testid="support-subject-toggle-desktop"]` / `button[data-testid="support-subject-toggle-mobile"]`
  * **Note Input Field**: `textarea[data-testid="support-note-desktop"]` / `textarea[data-testid="support-note-mobile"]`
  * **Image Upload Input**: `input[data-testid="support-file-input-desktop"]` / `input[data-testid="support-file-input-mobile"]` (type `"file"`)
  * **Submit Button**: `button[data-testid="support-submit-desktop"]` / `button[data-testid="support-submit-mobile"]` (starts disabled visually with class `bg-gray-300 cursor-not-allowed`).

---

## 6. Raw Inventory & Rollout Gaps

### 6.1 `data-testid` Inventory
* `order-detail-page` (main details root container)
* `order-detail-back` (back link to orders)
* `order-detail-help` (help/support drawer trigger button)
* `order-detail-title` (order card title)
* `order-detail-variant` (variant detail field)
* `order-detail-status` (order status indicator wrapper)
* `order-detail-status-label` (order status state text)
* `order-detail-price-toggle` (breakdown collapse toggle)
* `order-detail-item-price` (item total price label)
* `order-detail-invoice` (get invoice button, delivered only)
* `order-detail-shipping-address` (shipping details block)
* `order-detail-billing-address` (billing details block)
* `order-detail-order-id` (internal order ID text)
* `order-detail-payment-status` (payment transaction status)
* `order-detail-total` (total paid order price)
* `order-detail-view-breakup` (detailed price breakup button)
* `support-sheet-backdrop` (support backdrop)
* `support-sheet-mobile` (mobile support drawer)
* `support-sheet-close-mobile` (mobile support drawer close)
* `support-sheet-desktop` (desktop support modal)
* `support-sheet-close-desktop` (desktop support modal close)
* `support-subject-toggle-desktop` / `support-subject-toggle-mobile` (subject toggle)
* `support-note-desktop` / `support-note-mobile` (textarea note)
* `support-file-input-desktop` / `support-file-input-mobile` (file selector)
* `support-submit-desktop` / `support-submit-mobile` (submit button)

### 6.2 NO-`data-testid` Elements & Fallback Selectors (`ROLLOUT-GAP`)
* **Order Status Date**:
  * **Selector**: `div[data-testid="order-detail-status"] p.text-white` (Tier: Structural)
* **Sold-by Label**:
  * **Selector**: `main p.text-gray-500.text-xs` (Tier: Semantic/Class)
* **Address Name Texts**:
  * **Selector**: `div[data-testid="order-detail-shipping-address"] span.font-medium`, `div[data-testid="order-detail-billing-address"] span.font-medium` (Tier: Structural)
* **Address Phone Texts**:
  * **Selector**: `div[data-testid="order-detail-shipping-address"] span:nth-of-type(3)`, `div[data-testid="order-detail-billing-address"] span:nth-of-type(3)` (Tier: Structural)
* **Address Lines**:
  * **Selector**: `div[data-testid="order-detail-shipping-address"] p.text-gray-400`, `div[data-testid="order-detail-billing-address"] p.text-gray-400` (Tier: Structural)
