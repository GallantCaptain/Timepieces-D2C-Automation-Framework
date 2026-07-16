# Wishlist Page POM Mapping

## Page Path & Accessibility
* **URL**: `https://www.timepieces-d2c.example/wishlist` (or accessible via `data-testid="nav-sidebar-wishlist"` inside the mobile/sidebar navigation).
* **Access**: Publicly accessible to guests; does not require user authentication. Wishlist data is persisted locally in the user's browser using `localStorage`.

---

## 1. Local Storage Structure
* **Key**: `timepieces_wishlist`
* **Value**: A JSON string representing an array of product objects:
  ```json
  [
    {
      "id": "gid://shopify/Product/8136978923564",
      "variantId": "gid://shopify/ProductVariant/45189716541484",
      "title": "Monarch",
      "handle": "monarch-rose-gold",
      "price": 125000,
      "image": "https://cdn.shopify.com/s/files/...webp",
      "availableForSale": false,
      "addedAt": "2026-07-16T05:26:45.339Z",
      "watchMovement": "Automatic",
      "productShortInfo": "40mm, Rose Gold",
      "productType": "timepieces"
    }
  ]
  ```

---

## 2. On-Page Toggle Button (PDP / Collection Cards)
The wishlist toggle is located on both timepiece and accessory PDPs and item cards.
* **Button Element Selector**: `button[title="Add to wishlist"]` / `button[title="Remove from wishlist"]`
* **States**:
  * **Inactive / Add to Wishlist**:
    * **Title**: `title="Add to wishlist"`
    * **Class List**: `flex items-center gap-2 transition-colors text-gray-600 hover:text-black !bg-transparent`
    * **Icon / Image**: `img[alt="Menu"]` pointing to `Favorite_icon222.svg?v=1765964674`
  * **Active / Remove from Wishlist**:
    * **Title**: `title="Remove from wishlist"`
    * **Class List**: `flex items-center gap-2 transition-colors text-red-600 hover:text-red-700 !bg-transparent`
    * **Icon / Image**: `img[alt="Menu"]` pointing to `addedwishlist.svg?v=1765962519`
* **Sidebar Menu Wishlist Link**:
  * **Selector**: `a[data-testid="nav-sidebar-wishlist"]`
  * **Label**: `"Wishlist"` (no counter text).

---

## 3. Wishlist Page DOM Layout

### 3.1 Header & Breadcrumbs
* **Page Title**: `h1[data-testid="account-page-title"]` / `h1` containing text `"Wishlist"`
* **Breadcrumbs**: `nav[data-testid="account-breadcrumbs"]` / `navigation` element containing home link `a[href="/"]` and current location text `"Wishlist"`.

### 3.2 Empty State
* **Empty Warning**: `h2` containing text `"Your Wishlist is Empty"`
* **Action Link**: `a` containing text `"Continue Shopping"` pointing to `/collections/timepieces`.

### 3.3 Populated State & Global Actions
* **Item Count Indicator**: `p.text-gray-400` containing text like `"2 items"` or `"1 item"` (updated dynamically in the DOM).
* **Clear All Button**: `button` containing text `"Clear All"` (class: `text-red-500 hover:text-red-400 text-sm font-medium transition-colors cursor-pointer`). Clicking this removes all items from the wishlist without confirmation.

### 3.4 Wishlist Item Card Structure
* **Card Container**: `div.bg-\[\#141414\].rounded-xl.overflow-hidden.group.relative`
* **Card Actions / Remove Button**: 
  * **Selector**: `button[title="Remove from wishlist"]` (class: `absolute top-3 right-3 z-10 text-red-500 hover:text-red-400 transition-colors`).
  * **Behavior**: Clicking this button immediately removes the item from the wishlist and decrements the page count. No confirmation dialog is shown.
* **Product Link (Image & Title)**:
  * **Selector**: `a` with href matching `/products/<handle>` or `/accessories/<type>/<handle>`.
  * **Image**: `img` inside the card showing the product's primary thumbnail.
  * **Stock Status Badge**: If a timepiece is sold out, a text overlay `span` or `div` containing text `"Sold Out"` appears over the image.
* **Card Text Details**:
  * **Category label**: Uppercase label like `"AUTOMATIC"` or `"PU LEATHER"` (`span` or `p`).
  * **Title**: `h3` containing product name (e.g., `"Monarch"`).
  * **Variant Details**: `p` or `span` showing subtitle text (e.g. `"40mm, Rose Gold"`, `"Suitable for all watch sizes"`).
  * **Price**: `p` or `span` containing the formatted price text (e.g., `"₹1,25,000"`, `"₹4,500"`).
* **Cart Actions**:
  * *Note*: There are no "Add to Cart" or "Move to Cart" buttons on guest wishlist item cards. Users must click through to the PDP to purchase.
