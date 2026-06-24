# Delta Updates

Last updated: 2026-06-24

## 2026-06-24 Page-Object Refactor Cleanup

### Modified Items

- `HomePage.java` — removed ~24 duplicate `varName` placeholder fields and the invalid identifier `private final By 15`; kept real hero locators; added `waitUntilLoaded`, `toggleHeroMute`, `clickDiscoverCta` (→ `AstoniaDropPage`), and `isHomePageLoaded`/`isHeroSectionDisplayed`/`isHeroOverlayDisplayed`; pruned unused imports.
- `SidebarMenuComponent.java` — placeholder `varName` stubs removed (real locators already filled in by component owner); added navigation business methods (logo, timepieces/accessories submenus, back, wishlist, track-orders/orders, account profile, country selector) and verification methods (`isSignInButtonDisplayed`, `isUserLoggedIn`, `isBecomeMemberPromptDisplayed`, `isSidebarLogoDisplayed`, `isProductsSubMenuDisplayed`, `getMenuItemCount`).
- `AstoniaCardComponent.java` — removed the `BasePage.wait`-shadowing field and redundant try/catch; added `dismissAstoniaCard()`; replaced misleading reappearance methods with `clickAstoniaDropCardWhenVisible(int)` and `isAstoniaDropCardDisplayedWithin(int)` plus a shared `becomesVisibleWithin(int)` helper.
- `LoginPage.java` — removed dead commented-out OTP-send rate-limit locators.

### Status Changes

- ISSUE-008 → Resolved; TASK-CODE-001 → Completed; RISK-BUILD-BREAK → Resolved; BLK-005 → cleared.
- DEBT-007 re-scoped (Astonia POM now compiles; lacks verified test coverage → TASK-RUN-001); new DEBT-008 for pending `TODO(locators)` (→ TASK-LOC-001).

### Verification

- `mvnw -o test-compile` → BUILD SUCCESS.

## 2026-06-24 Initial OKF Creation

### Added Items

- New OKF index: `docs/okf/OKF_INDEX.md`.
- New OKF topic files:
  - `project-overview.md`
  - `system-architecture.md`
  - `development-standards.md`
  - `testing-strategy.md`
  - `project-structure.md`
  - `CurrentStatus.md`
  - `decisions-log.md`
  - `known-issues.md`
  - `task-backlog.md`
  - `risks.md`
  - `learnings.md`
  - `changelog.md`
  - `delta-updates.md`
- New ADRs documenting existing framework decisions:
  - `0002-java-bdd-automation-stack.md`
  - `0003-page-object-component-architecture.md`
  - `0004-threadlocal-parallel-cucumber-testng.md`
  - `0005-configuration-resolution-precedence.md`
  - `0006-reporting-and-metrics-stack.md`
  - `0007-cloudflare-automation-bypass-header.md`
  - `0008-safari-substitution-non-macos.md`
  - `0009-okf-governance.md`
- New backlog entries for CI access, OTP security, runner coverage, config drift, dashboard dispatch, documentation drift, and JVM crash investigation.
- New risk register entries for Cloudflare, OTP security, coverage drift, locator flakiness, secrets, Java version mismatch, monorepo dashboard coupling, and reporting drift.
- New known issue, backlog task, current-status blocker, and risk for the incomplete `AstoniaCardComponent.java:63` working-tree edit discovered during final audit.

### Modified Items

- Root `README.md` updated to include OKF navigation.
- OKF issue, backlog, risk, current-status, changelog, and delta files updated after discovering the incomplete `AstoniaCardComponent.java:63` working-tree edit during self-audit.

### Deprecated or Superseded Items

- No files or decisions were deleted.
- Historical source docs remain intact.
- Conflicting claims are marked as drift in OKF issue/backlog records rather than removed.

### Drift Flagged

- `docs/project_context.md` references `SanityRunner`, but current code and `testng.xml` only show `SmokeRunner` and `RegressionRunner`.
- Several feature files are present but not registered by current runners.
- `staging.properties` uses `is.headless`; `DriverFactory` reads `headless`.
- README claims Allure GitHub Pages publishing, while the inspected workflow organizes deploy files and uploads artifacts but does not contain a Pages deploy step.
- Dashboard flow config includes `dev`; workflow dispatch input allows `staging` and `prod`.
- CI workflow browser input includes Edge and Brave; browser install step installs Chrome and Firefox.
- Working tree contains an incomplete `AstoniaCardComponent.java:63` statement that should be resolved before Maven verification.

### Reason for Update

The user requested creation and ongoing governance of the OKF as the authoritative project memory for this automation framework.
