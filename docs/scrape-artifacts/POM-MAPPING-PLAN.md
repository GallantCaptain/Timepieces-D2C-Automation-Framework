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

**Decision — Single-Deployment Clasp Rose Gold (20mm/22mm): RESOLVED → two
classes.** The 2026-07-15 discovery scrape confirmed the size card-buttons
**navigate to distinct PDP URLs** (clicking 22mm → `/accessories/clasp/
single-deployment-clasp-rose-gold-22mm`), and the COLOR card-buttons navigate too.
So every color/size is its own URL/class: `SingleDeploymentClaspRoseGold20mm` +
`SingleDeploymentClaspRoseGold22mm`, both extending the shared clasp template,
with a `selectSize("22mm")`/`selectColor(...)` nav method. (TASK-PDP-003 closed.)
Note: the 20mm and 22mm pages share the SAME `<title>` ("Single Deployment Clasp,
Rose Gold") — only the URL distinguishes them, so title verification can't tell
them apart; `open()`/path can.

### Accessory PDP template (from the discovery scrape)

All accessory PDPs share ONE template (watch case / clasp / strap identical),
so it maps as a `SharedAccessoryPdp` base analogous to `SharedTimepiecePdp`:
- Common: `h1` title, price (`div` ₹ text — ROLLOUT-GAP, no clean selector),
  compatibility caption, **enabled "Add to cart" CTA** (accessories are
  PURCHASABLE — unlike the sold-out timepieces; this is what unblocks the
  cart/checkout BDD steps), no quantity selector, wishlist
  (`button[title='Add to wishlist']`), gallery (`.desktop-image-gallery` /
  `.product-media-carousel`), **color/size card-button switcher**
  (`.flex.flex-wrap.gap-2` → `button[title='<name>']` + `aria-pressed`, each
  navigates to a distinct PDP), Description/Technical-details accordions
  (`div.w-full.border-t`), **Return & Guarantee side drawers**
  (`button[aria-label='Open guarantee details']` / `…return and refunds…`, drawer
  `div.fixed.inset-0.z-[9999]`, close `button[aria-label='Close']`, backdrop
  `div.absolute.inset-0.bg-black/50.backdrop-blur-sm`), and a bottom model
  carousel (`div.relative.group` → `a[href*='/accessories/…']`).
- **Straps only** (add a `SharedStrapPdp` layer over the base): a **dial-try
  carousel** ("A new look for your watch"; `button.strap-nav.strap-prev/next`,
  compatible-watch dial images) + a **clasp-upgrade cross-sell** card ("Upgrade
  your strap"; `+ Add`).

Hierarchy: `SharedAccessoryPdp` → (`SharedTemplate_WatchCase`,
`SharedTemplate_ButterflyClasp`, `SharedTemplate_SingleDeploymentClasp`) and
`SharedAccessoryPdp` → `SharedStrapPdp` → (the 5 strap family templates).

## Phase 3 — Content & account-adjacent pages (14 files)

Detailed plan (2026-07-15). Two-thirds are **public** and easy; the Orders +
Wishlist chunk is **auth-gated** and left last. Execution order:
**3A → 3C → 3D → 3B** (public first; auth-gated last).

| Chunk | Group | Files | Auth | URL discovery | Notes |
| --- | --- | --- | --- | --- | --- |
| 3A | **Legal** | `GuaranteePolicyPage`, `PrivacyPolicyPage`, `RefundPolicyPage`, `ShippingPolicyPage`, `TermsOfServicePage` (5) | public | scrape needs the real paths (no `PAGE_PATH` set) — follow the **footer legal links** (`a[data-testid='footer-legal']`); likely `/policies/…` | Static prose. Candidate `SharedPolicyPage` base if the 5 share one template (heading + prose sections + last-updated + maybe the same side-drawer as the accessory Return/Guarantee). |
| 3C | **Other content** | `TimepiecesWallPage` (`/timepieces-wall`), `ShareYourMomentPage` (`/timepieces-wall/share`, partial 12 loc), `SiteMapPage` (`/sitemap`), `UpdatesPage` (`/updates`) (4) | public | paths known (in scaffolds) | Mixed: TimepiecesWall = UGC/social wall; ShareYourMoment = an upload/submission **form**; SiteMap = link index; Updates = news list. Confirm ShareYourMoment isn't login-gated. |
| 3D | **Friends & Partners** | `FriendsAndPartners` (`/friends-and-partners`) (1) | public | path known | Partner directory (the scaffold held ~90 raw partner URLs). Map the partner-link grid + any category filter. |
| 3B-guest | **Wishlist + Track Order** (public) | `WishlistPage`, `TrackOrdersPage` (partial 5 loc) | **public** | `WishlistPage` via sidebar (confirm `/wishlist` vs `/account/wishlist`); `TrackOrdersPage` = `/track-order` | SPLIT OUT from 3B once the scaffolds revealed both are GUEST-accessible (Wishlist Javadoc: "displayed to both guest and logged-in users", sidebar entry; TrackOrders Javadoc: "browse as a guest and track by order number"). No login needed. Wishlist must be seeded (add items via PDP heart) before the item-card maps. **`WishlistPage` unblocks the `WishlistSteps` `PendingException` glue.** Brief: `BRIEF-3B-guest-wishlist-track.md`. |
| 3B-auth | **Orders + Account nav** (auth-gated) | `OrderHistoryPage`, `OrderDetailsPage`, finish `AccountPage` nav (partial 3 loc/5 m) | **login required** | `/account/orders`, `/account/orders/Timepieces{OrderNo}`, `/account` | Needs an **authenticated Antigravity session** — human-solved Cloudflare + SMS-OTP for pilot 9000000001, cookies then persist for all 3. Redact personal data (selectors only). Orders unblock order-management scenarios; AccountPage nav = the Profile/Orders/Owned/Wishlist links + logout. Brief: `BRIEF-3B-auth-orders-account.md`. |

Per-chunk loop is unchanged (brief → Antigravity scrape → map → compile → OKF →
commit). AFTER all four chunks are mapped: author Phase-3 **test cases** (BDD
`.feature` + TDD TestNG) and push to Zephyr, THEN wire the step definitions —
matching the accessories flow (map → test cases → glue).

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
| 2 | Accessory PDP families (cases/clasps/straps) | 29 | ✅ Done 2026-07-15 — `SharedAccessoryPdp` base + `SharedStrapPdp` layer; 2A watch case, 2B butterfly clasps, 2C single-deployment clasps (Rose Gold 20/22mm split), 2D–2H straps (FKM/Hybrid/Ostrich/Suede/Teju) all mapped |
| 3 | Content & account-adjacent (legal/orders/other/F&P) | 14 | ✅ Done 2026-07-16 — 3A Legal (`SharedPolicyPage` + 5 policy pages); 3C Other content (`TimepiecesWallPage` [WebGL canvas wall], `ShareYourMomentPage` [login-gated submit], `SiteMapPage`, `UpdatesPage`); 3D Friends & Partners (`FriendsAndPartners` + NEW `PartnerProfilePage`); 3B-guest (`WishlistPage` [/wishlist, localStorage seeding] + `TrackOrdersPage` [stale testids replaced]); 3B-auth (`OrderHistoryPage` + `OrderDetailsPage` [both FULLY testid'd; Support Request sheet mapped — "bugged" claim disproved] + `AccountPage` nav finished). **PLAN COMPLETE** — every incomplete POM mapped except the user-owned Razorpay COM. New follow-up discovered: `/account/owned-watches` has no scaffold. |
| 4 | Timepieces World microsite | 8 | ⏳ Brief ready (2026-07-16): `phase4/BRIEF-4A-timepieces-world.md` — experience scroll-canvas + representative product showcase (shared-template verdict over the 5 families) + RW header (dual-nav logo) + RW sidebar. Implementation note: RW page objects must override `getBaseUrl()` → `https://world.timepieces-d2c.example`. |
| 5 | Loose ends (drop card, drop-page review) | 1+ | ⬜ |
| — | Razorpay checkout | — | 🚫 User-owned |
