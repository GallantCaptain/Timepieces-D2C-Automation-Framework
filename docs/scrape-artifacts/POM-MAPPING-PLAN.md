# Timepieces POM Locator-Mapping Plan (Antigravity-driven scrape)

**Owner:** Avinash Rath · **Started:** 2026-07-15 · **Method:** external browser
agent ("Antigravity") drives live Chrome on production, returns per-page
Markdown artifacts, which are then translated into Page Object Model (POM)
classes in this framework.

## Goal

Map real locators + Business Library / Private Helper / Verification methods for
**every incomplete POM page** in `src/main/java/.../pages/`, so the
`PendingException`-stubbed BDD steps (cart, checkout, wishlist) can be wired to
real page objects.

**Explicitly out of scope (owned by another team):** the Razorpay checkout —
`components/RazorpayModalComponent.java` and the payment portion of
`web/payments/CheckoutPage.java`. This plan does not touch them.

## The per-chunk loop

Each chunk repeats the same validated loop (proven in Phase 0):

1. **Brief** — I write a scrape brief listing the target production URLs and
   exactly what to capture per page: `data-testid`s first, then the fallback
   ladder (id → aria/alt/name/role → text-anchor → semantic class → structural),
   plus `<video>`/`<canvas>`, accordions, price, CTA/stock state, sibling-variant
   nav, and compatibility carousels.
2. **Scrape** — Antigravity drives Chrome on `https://www.timepieces-d2c.example`, returns
   one `.md` artifact per page.
3. **Land** — copy artifacts into `docs/scrape-artifacts/phaseN/`.
4. **Implement** — fill the `SharedTemplate_*` once per family, then the thin
   variant subclasses (real `PAGE_PATH`/`PAGE_TITLE` + any per-variant override),
   reusing inherited `BasePage` helpers. Tag every non-`data-testid` locator
   `// ROLLOUT-GAP` so it is swapped when the rollout reaches that page.
5. **Verify** — `mvnw -o clean test-compile` → BUILD SUCCESS.
6. **Record** — OKF changelog/CurrentStatus/task-backlog, then commit.

## Locator reality (from Phase 0)

- **Collection PLPs, Contact form** — fully instrumented with `data-testid`
  (`collection-*`, `accessories-*`, `contact-*`). Cheap, stable.
- **PDPs (timepieces + accessories)** — **no `data-testid` yet**; core elements
  fall back to aria-labels (lume/3D toggles), semantic/utility classes (title,
  price, gallery), and product-handle `href`s (sibling nav). These are the
  `ROLLOUT-GAP` locators.
- **Experience Center / content pages** — almost no `data-testid`; text-anchor
  XPath. Selenium CSS supports neither `:has()` nor `:contains()`, so
  parent-scoped cards must use XPath.

---

## Phase 0 — Pilot ✅ DONE (2026-07-15)

Validated the conventions end-to-end.

- **Astonia timepiece PDP family:** `SharedTemplate_Astonia` (16 locators, 11
  business + 15 verification methods incl. lume toggle, 3D viewer open/close,
  spec accordion, sibling-variant nav, wishlist, "Interest submitted" sold-out
  state) + `AstoniaStealthSilver`, `AstoniaDuskGold`, `AstoniaDuskRose`
  (real handle `astonia-bimetal-dusk-rose`), `AstoniaTrophyGold`
  (overrides `has3DViewer()` → false, adds `is3DViewerAbsent()`).
- **PLPs:** `AllTimepiecesPage` (collection tabs/filters/sort/grid/next-release),
  `AllAccessoriesPage` (added missing `PAGE_PATH`/`PAGE_TITLE`).
- **Support/other:** `ContactPage` (enquiry form + support links, no-submit
  guard), `TimepiecesHousePage` (store cards by city via XPath, event video toggle).
- **Bonus fix:** corrected `AuriquaOceanBlue` CRTP self-type
  (`<AuriquaNoirRose>` → `<AuriquaOceanBlue>`).
- **Result:** clean compile of 111 main + 28 test sources, BUILD SUCCESS.

---

## Phase 1 — Remaining timepiece PDP families (19 files, 14 sellable variants)

Highest reuse: these share the exact Astonia template shape (loom + 3D + specs +
sibling nav). Lowest risk, so do them first. One chunk per family.

| Chunk | Family | Files | Notes |
| --- | --- | --- | --- |
| 1A | **Monarch** | `SharedTemplate_Monarch` + `MonarchRoseGold`, `MonarchSilverBlack` (3) | loom ✔, 3D ✔ |
| 1B | **Arvion** | `SharedTemplate_Arvion` + `ArvionBurgundyGold`, `ArvionEspressoSilver`, `ArvionNavySilver` (4) | loom ✔, 3D ✔ |
| 1C | **Astonia Sports** | `SharedTemplate_AstoniaSports` + `AstoniaSportsCircuitBlue`, `AstoniaSportsPhantomBlack`, `AstoniaSportsPodiumGreen` (4) | loom ✔, 3D ✔ |
| 1D | **Auriqua** | `SharedTemplate_Auriqua` + `AuriquaNoirRose`, `AuriquaOceanBlue`, `AuriquaRacingGreen` (4) | loom ✔, 3D ✔ (product owner confirmed Auriqua HAS loom; only Manifesta lacks it) |
| 1E | **Manifesta** | `SharedTemplate_Manifesta` + `ManifestaBlackOnyx`, `ManifestaBlueAventurine`, `ManifestaMotherOfPearl` (4) | **no loom / no autoplay video**, 3D ✔ — the exception family |

`Astonia_Unnammed` and `Manifesta_Unnammed` are unlaunched placeholders — skipped
until they go live. Post-Phase-1 candidate refactor: hoist the shared loom/3D/spec
behavior into a common `SharedTimepiecePdp` base to cut duplication across the six
family templates (defer until ≥3 families are mapped and the shape is confirmed
stable).

**Shared PDP owners modal:** every timepiece PDP carries a "See all {Family}
owners" social-proof modal with an internal **filter-by-variant + search-owners**.
Because it is identical across timepiece PDPs, it is mapped **once** as
`PdpOwnersModalComponent` (Component Object, like `HeaderComponent`/
`FooterComponent`) in chunk 1A and referenced by every family template — not
duplicated per family.

> Correction (2026-07-15): this owners modal is **not** the storefront's
> FILTERS & SORT. The product **FILTERS & SORT drawer lives on the collection/PLP
> pages** (`/collections/timepieces`, `/accessories/all`) — already mapped in
> Phase 0 (`AllTimepiecesPage` / `AllAccessoriesPage`) — and the two PLPs differ:
> timepieces filter by **Watch Movement** + **Dial Size**, accessories by
> **Product Type** + **Size**; both share the Price ↑/↓ sort. There is no
> product filter/sort on the PDPs themselves.

## Phase 2 — Accessory PDP families (29 files, 21 variants)

Different template: compatibility carousels (which watches/straps a clasp fits),
size toggles, no loom/3D. One chunk per accessory type.

| Chunk | Family | Files | Notes |
| --- | --- | --- | --- |
| 2A | **Watch Cases** | `SharedTemplate_WatchCase` + `PremiumWatchCaseBlack` (2) | user updated `PremiumWatchCaseBlack` PDP — re-scrape to confirm |
| 2B | **Butterfly Clasps** | `SharedTemplate_ButterflyClasp` + Gold, RoseGold, Steel (4) | |
| 2C | **Single-Deployment Clasps** | `SharedTemplate_SingleDeploymentClasp` + Black, Gold, RoseGold, Steel (5) | **RoseGold has a 20mm/22mm "Capsule-Button" toggle** — see decision below |
| 2D | **FKM Rubber Straps** | `SharedTemplate_FKMRubberStrap` + Black, Orange, White, Yellow (5) | |
| 2E | **Hybrid Leather Straps** | `SharedTemplate_HybridLeatherStrap` + Black, Blue (3) | |
| 2F | **Ostrich Leather Straps** | `SharedTemplate_OstrichLeatherStrap` + Black, Brown (3) | |
| 2G | **Suede Leather Straps** | `SharedTemplate_SuedeLeatherStrap` + Blue, Brown, Burgundy (4) | |
| 2H | **Teju Lizard Leather Straps** | `SharedTemplate_TejuLizardLeatherStrap` + Black, Blue (3) | |

**Decision — Single-Deployment Clasp Rose Gold (20mm/22mm):** model as **two
separate classes** (`SingleDeploymentClaspRoseGold20mm` / `...22mm`), both
extending the shared template, with a `selectSize22mm()` business method that
returns the sibling page type. Rationale: two distinct SKUs / handles / URLs, and
the framework's one-class-per-URL invariant underpins `open()`/title/URL
verification. The capsule toggle is just sibling navigation (same as Astonia
variant cards). **Confirm during the 2C scrape** that the toggle navigates to a
distinct URL; if it turns out to be client-side only (same URL, no navigation),
fall back to a single class with a size-state parameter.

## Phase 3 — Content & account-adjacent pages (14 files)

| Chunk | Group | Files | Notes |
| --- | --- | --- | --- |
| 3A | **Legal** | Guarantee, Privacy, Refund, Shipping, TermsOfService (5) | static text; light heading/section locators |
| 3B | **Orders** (auth-gated) | OrderHistory, OrderDetails, TrackOrders (partial), Wishlist (4) | needs an **authenticated session** — Antigravity logs in via the human-solved Cloudflare + OTP relay used for the pilot |
| 3C | **Other content** | TimepiecesWall, ShareYourMoment (partial), SiteMap, Updates (4) | |
| 3D | **Friends & Partners** | FriendsAndPartners (1) | compile-fixed in Phase 0 groundwork; map real content |

## Phase 4 — Timepieces World microsite (8 files)

Architecturally separate — its own header + sidebar. Map the 2 components first,
then the 6 pages against them.

- Components: `TimepiecesWorld_HeaderComponent`, `TimepiecesWorld_SidebarMenuComponent`.
- Pages: `TimepiecesWorld_ArvionPage`, `TimepiecesWorld_AstoniaPage`,
  `TimepiecesWorld_AuriquaPage`, `TimepiecesWorld_ExperiencePage`,
  `TimepiecesWorld_ManifestaPage`, `TimepiecesWorld_MonarchPage`.

## Phase 5 — Loose ends

- `components/cards/ManifestaDropCardComponent` (mirror the done
  `AstoniaDropCardComponent`).
- Drop pages (`AstoniaDropPage`, `ManifestaDropPage`): by design these stay
  minimal on `StoreFrontBasePage` (each drop is unique/frequently changing) —
  confirm the design decision still holds and leave minimal or lightly enrich.
- **Razorpay (user-owned):** `RazorpayModalComponent` + `CheckoutPage` payment
  bits — another team maps these.

---

## Progress ledger

| Phase | Scope | Files | Status |
| --- | --- | --- | --- |
| 0 | Astonia pilot + 2 PLPs + Contact + House | 9 (+Auriqua fix) | ✅ Done 2026-07-15 |
| 1 | Timepiece PDP families (Monarch/Arvion/Astonia Sports/Auriqua/Manifesta) | 19 | ✅ Done 2026-07-15 — 1A Monarch, 1B Arvion, 1C Astonia Sports, 1D Auriqua (lume, NO 3D), 1E Manifesta (NO lume, 3D yes). Shared components: `PdpOwnersModalComponent`, `PdpRegisterInterestModalComponent`. **Base refactor done (TASK-PDP-004): `SharedTimepiecePdp` — 6 templates ~2700→309 lines, register/share/owners on every family.** |
| 2 | Accessory PDP families (cases/clasps/straps) | 29 | ⬜ |
| 3 | Content & account-adjacent (legal/orders/other/F&P) | 14 | ⬜ |
| 4 | Timepieces World microsite | 8 | ⬜ |
| 5 | Loose ends (drop card, drop-page review) | 1+ | ⬜ |
| — | Razorpay checkout | — | 🚫 User-owned |
