# Homepage Modals & Overlays POM Mapping

## 1. Stories Modal Dialog
* **Trigger Button**: `button[data-testid="header-stories-toggle"]` (located in the header; separate desktop and mobile nodes exist, only the visible viewport node is interactable).
* **Modal Container**: `div[role="dialog"]` (class: `fixed inset-0 z-[60] bg-black/80 backdrop-blur-sm flex items-center justify-center`, with attribute `aria-modal="true"`).
* **Story Content Area Container**: `div.relative.w-full.h-full.md\:w-\[50vh\].md\:h-\[95vh\].md\:rounded-xl`

### 1.1 Progression Indicators & Auto-Advance
* **Indicators Track Wrapper**: `div.absolute.top-4.md\:top-3.left-4.right-4.md\:left-0.md\:right-0.z-20.flex.gap-1`
* **Per-Story Progress Bar**: `div.flex-1.h-\[3px\].bg-white\/40`
* **Active Story Fill Segment**: `div.h-full.bg-white.transition-\[width\]`
  * **Auto-advance behavior**: **Verified.** The segment width updates incrementally over time, changing its inline `style="width: X%"` value. Once the active story fill reaches `100%`, it transitions to the next segment. When the final segment reaches `100%`, the modal automatically closes.
* **Pause / Play Toggle Button**: `button[aria-label="Pause"]` (class: `bg-black/40 hover:bg-black/55 text-white p-2 rounded-full`). Switches `aria-label` to `"Play"` when paused.
* **Close Button**: `button[aria-label="Close stories"]` (resides inside the top-right controls group).

### 1.2 Story Content & Tap-Navigation
* **Story Image**: `div.absolute.h-\[90\%\].inset-0.m-auto img`
* **Tap-Previous Hotspot (Left 25%)**: `button[aria-label="Previous story"]` (inside the story container, class: `absolute top-0 bottom-0 left-0 w-1/4 z-10 rounded-r-full`)
* **Tap-Next Hotspot (Right 25%)**: `button[aria-label="Next story"]` (inside the story container, class: `absolute top-0 bottom-0 right-0 w-1/4 z-10 rounded-l-full`)
* **Desktop Previous Arrow Button**: `button[aria-label="Previous story"]` (positioned outside the left edge of the story panel, class: `hidden md:flex absolute top-1/2 -left-[10vh]`)

---

## 2. Drop Card Popup Component
* **Active Drop Card**: **Astonia**
* **Popup Main Wrapper Selector**: `div[data-testid="astonia-popup"]` (class: `pointer-events-none fixed bottom-4 left-1/2 z-[9998] w-[calc(100vw-2rem)] max-w-[358px] h-[132px] -translate-x-1/2 md:bottom-6 md:left-auto md:right-6 md:translate-x-0`)
* **Popup Card Button Link**: `div[data-testid="astonia-popup-card"]` (role `"button"`, `aria-label` `"Astonia — The Next Chapter. Open the Astonia page."`)
  * **Action**: Clicking navigates to `/new-release/astonia`.
* **Popup Title Text**: `h3[data-testid="astonia-popup-title"]` (text: `"Astonia"`)
* **Trigger Logic (Verified)**:
  * Mounts in layout dynamically after **15 seconds** of page load on the homepage (`/`) if it has not been previously dismissed and has been displayed less than 2 times.
  * Dismissal and display state is tracked in `sessionStorage` under `astonia_popup_visited` (set to `"1"`) and `astonia_popup_show_count`.

---

## 3. Region Picker (Country Selector Listbox)
* **Trigger Button Selector**: `button[data-testid="country-selector-toggle"]` (located in the footer, class: `flex items-center gap-2 cursor-pointer hover:opacity-80 transition-opacity`, display: `"🇮🇳IN"`)
* **Listbox Dropdown Panel**: `div[data-testid="country-selector-dropdown"]` (role `"listbox"`, class: `absolute bottom-full left-0 mb-2 w-[280px] rounded-xl z-50 bg-[#1A1A1A] border border-[#333]`)
* **Country Search Box**: `input[data-testid="country-selector-search"]` (class: `w-full pl-9 pr-3 py-2 text-[16px] rounded-lg bg-[#2A2A2A] border border-[#444]`, placeholder `"Search country..."`)
* **Country Option Link/Button**: `button[data-testid="country-selector-option"]`
  * Attributes: `data-country-code="<CODE>"` (e.g. `data-country-code="US"`), `role="option"`, `aria-selected="true"|"false"`.
  * Contains country flag icon and country name text.
* **Close Control**: Clicking the backdrop container or clicking `country-selector-toggle` again.

---

## 4. Raw Inventory & Rollout Gaps

### 4.1 `data-testid` Inventory
* `header-stories-toggle` (stories modal trigger)
* `astonia-popup` (popup card container wrapper)
* `astonia-popup-card` (clickable card element)
* `astonia-popup-title` (card name header)
* `country-selector` (dropdown panel container)
* `country-selector-toggle` (footer trigger button)
* `country-selector-dropdown` (listbox dropdown overlay)
* `country-selector-search` (search text inputs)
* `country-selector-option` (individual country listbox choices)

### 4.2 NO-`data-testid` Elements & Fallback Selectors (`ROLLOUT-GAP`)
* **Stories Dialog Backdrop**:
  * **Selector**: `div[role="dialog"][aria-modal="true"]` (Tier: Aria/Role)
* **Stories Progress Bar Wrapper**:
  * **Selector**: `div[role="dialog"] div.absolute.top-4.left-4.right-4.flex` (Tier: Structural)
* **Stories Pause Button**:
  * **Selector**: `button[aria-label="Pause"], button[aria-label="Play"]` (Tier: Aria/Role)
* **Stories Close Button**:
  * **Selector**: `button[aria-label="Close stories"]` (Tier: Aria/Role)
* **Stories Image Node**:
  * **Selector**: `div[role="dialog"] div.absolute.h-\[90\%\] img` (Tier: Structural)
