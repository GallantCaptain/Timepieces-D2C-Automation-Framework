# Timepieces World Experience Page POM Mapping

## 1. Page Info & Access
* **URL**: `https://world.timepieces-d2c.example/`
* **Page Title**: `"Timepieces Watches | Early Access to Limited Edition Pieces"`
* **Main Canvas Container**: `canvas.w-full.h-full` (where frames are drawn dynamically)
* **Canvas Wrapper**: `div.fixed.inset-0.w-full.h-screen.overflow-hidden`

---

## 2. Scroll Sections & Delimiters
The experience page consists of 7 scroll-advanced scenes:
1. **Opening Scene**
2. **Arvion**
3. **Monarch**
4. **Auriqua**
5. **Astonia**
6. **Manifesta**
7. **End Scene** (Static Grid)

### 2.1 Section Headers & Delimiters
* **Family Name Heading**: `div.pointer-events-auto h2`
  * Text value: e.g. `"Arvion"`, `"Monarch"`, etc. (class: `text-white uppercase text-[36px] transition-opacity duration-300`)
* **Discover CTA**: `div.pointer-events-auto button:has-text("DISCOVER")`
  * Text value: `"DISCOVER"`
  * Action: Navigates to `/products/<family>?from=scene&frame=<frameNumber>` on `world.timepieces-d2c.example` (e.g. `/products/arvion?from=scene&frame=298`).

---

## 3. Side Progress Navigation (Right Sidebar)
* **Container**: `div.fixed.right-3.md\:right-4.lg\:right-5.top-1\/2.transform.-translate-y-1\/2`
* **Per-Section Segment dot**: `div.relative.group`
* **Active Section Indicator**:
  * The active segment is represented by a taller pill-shaped indicator: `div.w-1\.5.h-6` (on desktop: `md:w-2 md:h-7`).
  * Inactive segments are represented by small circles: `div.w-1\.5.h-1\.5` (on desktop: `md:w-2 md:h-2`).
* **Active Section Tooltip**:
  * Selector: `div.absolute.right-4.md\:right-5.top-1\/2` (has class `opacity-100` when hovered or active).
  * Text value: Contains the name of the section + the current percentage progress, e.g. `"Arvion97%"`.

---

## 4. End Scene Product Grid (Static Cards)
Once the experience advances to 100% on `"End Scene"`, the static card grid is displayed:
* **Desktop Grid Container**: `div.grid-cols-2.md\:grid-cols-5.hidden.md\:grid.max-w-\[1000px\].mx-auto`
* **Mobile List Container**: `div.flex-col.md\:hidden.flex`
* **Card Selector**: `a[href="/products/arvion"]` (etc.)
  * Class: `group relative aspect-[3/4] overflow-hidden` (Desktop) or `group relative overflow-hidden rounded-lg h-[100px]` (Mobile).
* **Card Image**: `img` inside the card with alt matching the family name in uppercase (e.g. `alt="ARVION"`, url `..._v.webp`).
* **Card Heading**: `h3` inside the card with class `text-white text-sm font-timepieces font-semibold uppercase` (e.g. `"ARVION"`).
* **Controls**: No sliding controls (prev/next) are present; it is a static grid of all 5 families.

---

## 5. Scroll Behavior & DOM State
* Both `html` and `body` are styled with `overflow: hidden;`.
* Page scroll position does not advance using native scroll values (i.e. `scrollTop` remains `0` and `scrollHeight` matches `clientHeight`).
* The scroll state advances by listening to `wheel` and touch events.
* **Canvas advancing check**:
  * To programmatically advance the canvas in automation, dispatch a `wheel` event with a large `deltaY` (e.g., `new WheelEvent('wheel', { deltaY: 5000, bubbles: true })`) on the window/document.
  * The canvas advancing is validated by checking the active section's height pill (changing height from `h-1.5` to `h-6`) and checking the tooltip's percentage text (which updates, e.g. from `Opening Scene 100%` to `Arvion 5%`).

---

## 6. Raw Inventory & Rollout Gaps

### 6.1 `data-testid` Inventory
There are **no** `data-testid` attributes present on any elements inside this page.

### 6.2 NO-`data-testid` Elements & Fallback Selectors (`ROLLOUT-GAP`)
* **Welcome Screen Enter Button**:
  * **Selector**: `button:has-text("Enter Experience")` (Tier: Text-Anchor / Class: `px-8.py-3.h-\[56px\]`)
* **Main Canvas**:
  * **Selector**: `canvas` (Tier: Semantic/Tag)
* **Discover CTA Button**:
  * **Selector**: `div.pointer-events-auto button:has-text("DISCOVER")` (Tier: Text-Anchor)
* **Right Sidebar dots Container**:
  * **Selector**: `div.fixed.right-3.md\:right-4.lg\:right-5.top-1\/2` (Tier: Structural)
* **Active Pill Segment**:
  * **Selector**: `div.fixed.right-3.top-1\/2 div.h-6` (Tier: Structural/Class)
* **Desktop Product Cards Grid**:
  * **Selector**: `div.grid-cols-2.md\:grid-cols-5.hidden.md\:grid` (Tier: Class)
* **Mobile Product Cards List**:
  * **Selector**: `div.flex-col.md\:hidden.flex` (Tier: Class)
