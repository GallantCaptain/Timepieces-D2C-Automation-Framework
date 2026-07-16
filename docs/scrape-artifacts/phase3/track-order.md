# Order Tracking Page POM Mapping

## Page Path & Routing
* **URL**: `https://www.timepieces-d2c.example/track-order`
* **Query Parameter Auto-Submit**: Navigating to `https://www.timepieces-d2c.example/track-order?orderId=<id>` automatically populates the input field and executes the tracking search on page mount.
* **Layout**: Two-column layout on desktop:
  * **Left side (sticky)**: Product hero image (`https://cdn.shopify.com/.../astonia-sport2-desktop.webp`).
  * **Right side**: Tracking input form and tracking result details.

---

## 1. Tracking Entry Form (Unsearched State)
* **Main Heading**: `h1` containing text `"Track your order"` (class: `text-[24px] font-timepieces-sans-semibold text-white mb-2`).
* **Subheading**: `p` containing text `"Monitor the progress of your Timepieces timepiece."` (class: `text-[14px] text-[#888888] mb-8`).
* **Input Label**: `label` containing text `"Order Id*"` (class: `block text-[12px] text-[#888888] mb-2`).
* **Order ID Input Field**:
  * **Selector**: `input[placeholder="Enter order Id"]` (class: `w-full px-0 py-3 border-b border-[#333333] focus:border-[#2E7A64] outline-none text-[16px] text-white bg-transparent placeholder:text-[#555555] transition-colors`).
  * **Event Trigger**: Listens to input value change events. Pressing `Enter` in this input triggers form submission (`j()`).
* **Submit Button**:
  * **Selector**: `button` containing text `"Track order"` or `"Tracking..."` (loading state).
  * **States**:
    * **Disabled (Empty Input)**: Button has property `disabled` (or visually styled via React state as disabled when the input is empty/whitespace: class contains `bg-[#333333] text-[#666666] cursor-not-allowed`).
    * **Enabled (Non-empty Input)**: Class contains `bg-[#2E7A64] text-white hover:bg-[#256B56]`.

---

## 2. Error State (Invalid Order Number)
If the entered order ID does not exist or fails validation:
* **Error Container**: `div` with class `mb-4 p-3 bg-red-900/30 border border-red-800 rounded-lg`.
* **Error Message**: `p.text-[14px].text-red-400` containing text: `"Order not found. Please check your order ID and try again."` (or API-driven message).

---

## 3. Success State (Order Details View)
Once a valid order ID is fetched successfully, the tracking form is replaced by the order status view:

### 3.1 Order Info Header
* **Order Number Title**: `h1` with class `text-[18px] font-timepieces-sans-semibold text-white` containing text matching `Order #<orderId>`.
* **Order Placement Date**: `p` with class `text-[13px] text-[#888888] mt-0.5` containing text matching `Placed on <date> • <time>` (e.g. `Placed on Jun 24, 2026 • 15:09`).

### 3.2 Timepieces List & Selector
* **Timepiece List Title**: `p` with class `text-[13px] text-[#888888] mb-3` containing text `"Timepieces in this order"`.
* **Multi-Item Dropdown Selector (if order contains >1 item)**:
  * **Dropdown Toggle**: `button` (class: `w-full flex items-center gap-3 bg-[#141414] border border-[#222222] rounded-2xl px-4 py-4 cursor-pointer transition-colors hover:border-[#333] text-left`). Shows the currently selected item's image, title, serial number, and a dropdown arrow.
  * **Dropdown Options List**: `div.absolute.left-0.right-0.top-full.mt-2.bg-\[\#1A1A1A\].border.border-\[\#222222\].rounded-2xl.overflow-hidden.z-50` containing a list of buttons for each item.
* **Single Item Card (or Active Selected Item Card)**:
  * **Card Container**: `div` (class: `flex items-center gap-3 bg-[#141414] border border-[#222222] rounded-2xl px-4 py-4`).
  * **Product Image**: `img` (class: `w-11 h-11 rounded-lg object-cover`) with `src` pointing to product thumbnail.
  * **Product Name**: `p` (class: `text-[14px] font-timepieces-sans-semibold text-white`) containing product title.
  * **Serial Number**: `p` (class: `text-[12px] text-[#888888] mt-0.5`) containing text matching `SN : <serialNumber>`.

### 3.3 Status Stepper Timeline
A vertical timeline component (`v`) representing the step history:
* **Step Item Container**: `div` with class `flex gap-4`.
* **Step Indicators**:
  * **Vertical Line**: `div` with class `w-0.5 flex-1 min-h-[60px]` showing active color (`#2E7A64`) or inactive color (`#333333`).
  * **Step Dot**: `div` with class `w-3 h-3 rounded-full` showing active color (`#2E7A64`) or inactive color (`#444444`).
* **Step Info Layout**:
  * **Step Title**: `p` (class: `text-[15px] font-timepieces-sans-semibold` plus `text-white` when active, or `text-[#666666]` when inactive). Possible values:
    * `"Order confirmed"`
    * `"In Assembly"`
    * `"In Testing"`
    * `"Ready for pickup"` / `"Shipped"`
    * `"Out for delivery"`
    * `"Delivered"`
    * `"Order cancelled"`
  * **Step Note / Date**: `p` (class: `text-[13px] mt-1 text-[#999999]`) showing additional step notes or timestamp.

### 3.4 Shipment Tracking Section (if shipped)
* **Container**: `div.bg-\[\#141414\].rounded-2xl.p-6`
* **Label**: `p.text-\[12px\].text-\[\#666666\].mb-2` with text `"Tracking"`
* **Carrier**: `p.text-\[14px\].text-white.font-timepieces-sans-medium` (e.g. `"Blue Dart"`)
* **AWB Number**: `p.text-\[13px\].text-\[\#999999\]` containing text matching `AWB: <awbNumber>`
* **Track Shipment Link**: `a` (class: `px-4 py-2 border border-[#333333] rounded-full text-[12px] font-timepieces-sans-medium text-white hover:border-[#555555] transition-colors`), target `_blank`, pointing to tracking details URL.

### 3.5 Support CTA
* **Support Subtext**: `p.text-\[14px\].text-\[\#888888\].mb-4` with text `"Need assistance? Our support team is here to help."`
* **Contact Support Button**: `a[href="/contact"]` (class: `inline-block py-3 px-8 rounded-full text-[14px] font-timepieces-sans-medium bg-white text-black hover:bg-[#e5e5e5] transition-colors`) with text `"Contact support"`.

---

## 4. Layout Header & Footer
* **Header**: Contains the site header (`data-testid="site-header"`).
* **Footer**: Unlike standard account pages, this page **displays the complete site footer** (`data-testid="site-footer"`).
