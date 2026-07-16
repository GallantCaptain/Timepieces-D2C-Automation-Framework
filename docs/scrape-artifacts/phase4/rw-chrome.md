# Timepieces World Chrome (Header & Sidebar) POM Mapping

## 1. TimepiecesWorld_HeaderComponent

### 1.1 Header Structure
* **Header Container Selector**: `header` (class: `fixed top-0 left-0 right-0 w-screen z-[1000]`)
* **Menu/Hamburger Trigger Button**: `header button[aria-label="Toggle menu"]`
  * Inside contains `img[alt="Menu"]`.
  * Image src changes based on state:
    * Closed: `.../hamburger_collapased.svg`
    * Open: `.../hamburger_expanded.svg`

### 1.2 Dual-Navigation Logo
* **Logo Element Selector**: `header div.absolute.left-1\/2` (parent wrapper) or `header img[alt="Timepieces Logo"]`
* **Dual-Navigation Behavior (Verified)**:
  * **On Experience Page (`https://world.timepieces-d2c.example/`)**:
    * Clicking the logo redirects out to the main storefront website: `https://www.timepieces-d2c.example/`.
  * **On Showcase Pages (`https://world.timepieces-d2c.example/products/<family>`)**:
    * Clicking the logo navigates back to the experience page homepage: `https://world.timepieces-d2c.example/`.

---

## 2. TimepiecesWorld_SidebarMenuComponent

### 2.1 Sidebar Panel & Controls
* **Open Trigger Selector**: `header button[aria-label="Toggle menu"]`
* **Sidebar Drawer Selector**: `header div.fixed.top-0.left-0.h-screen.w-full.md\:w-\[500px\]`
  * **Open State Indicator**: Has class `translate-x-0`.
  * **Closed State Indicator**: Has class `-translate-x-full`.
* **Backdrop Overlay**: `header div.fixed.inset-0.bg-black\/30.backdrop-blur-\[5px\]`
* **Close Controls**:
  * Clicking the Backdrop Overlay closes the sidebar.
  * Clicking the hamburger trigger button (`header button[aria-label="Toggle menu"]`) again while open closes the sidebar.

### 2.2 Navigation Entries
All sidebar menu links point to the `world.timepieces-d2c.example` subdomain using relative paths:

* **ARVION**: `a[href="/products/arvion"]` (Label: `"ARVION"`)
* **MONARCH**: `a[href="/products/monarch"]` (Label: `"MONARCH"`)
* **AURIQUA**: `a[href="/products/auriqua"]` (Label: `"AURIQUA"`)
* **ASTONIA**: `a[href="/products/astonia"]` (Label: `"ASTONIA"`)
* **MANIFESTA**: `a[href="/products/manifesta"]` (Label: `"MANIFESTA"`)

---

## 3. Raw Inventory & Rollout Gaps

### 3.1 `data-testid` Inventory
There are **no** `data-testid` attributes present on either the header or the sidebar menu drawer elements.

### 3.2 NO-`data-testid` Elements & Fallback Selectors (`ROLLOUT-GAP`)
* **Header Menu Toggle**:
  * **Selector**: `header button[aria-label="Toggle menu"]` (Tier: Aria/Role / Selector)
* **Logo Image & Click Container**:
  * **Selector**: `header div.absolute.left-1\/2 img[alt="Timepieces Logo"]` (Tier: Semantic/Attribute)
* **Sidebar Drawer Panel**:
  * **Selector**: `header div.fixed.top-0.left-0` (Tier: Structural)
* **Sidebar Backdrop Overlay**:
  * **Selector**: `header div.fixed.inset-0.bg-black\/30` (Tier: Structural/Class)
* **Sidebar Links**:
  * **Selector**: `header div.fixed.top-0.left-0 a[href="/products/arvion"]` (etc.) (Tier: Attribute)
