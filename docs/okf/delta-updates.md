# Delta Updates

Last updated: 2026-07-01

## 2026-07-01 Page Object Fluent Interface Comments Refactoring

### Modified Items

- `src/main/java/com/timepieces/automation/pages/web/products/timepieces/**/*.java` — Updated Fluent Interface javadoc comments to return correct page type instead of `LoginPage` and removed redundant final step comments.
- `docs/okf/changelog.md` — Logged today's comment refactoring.
- `docs/okf/delta-updates.md` — Logged the delta update.

### Status Changes

- All page objects under `timepieces` updated to have accurate Fluent Interface references in doc comments.

### Verification

- Successfully compiled the project: `.\mvnw.cmd test-compile` -> BUILD SUCCESS.

## 2026-07-01 Storefront Test Case Roadmap & Cart Integration

### Added Items

- `docs/okf/test-case-roadmap.md` — Detailed test case coverage roadmap dividing storefront functions into 6 progressive phases.
- `src/test/resources/features/03_cart_bag_management/Cart_Bag_Management.feature` — Gherkin BDD specifications outlining 6 scenarios for cart drawer, subtotal calculation, free shipping threshold, item removal, and session persistence.
- `scripts/add_cart_tcs.py` — Python registration script integrating the cart scenarios as `QA-T44` through `QA-T49` in Zephyr Scale.

### Modified Items

- `docs/okf/OKF_INDEX.md` — Registered the new roadmap topic and updated the last reviewed timestamp.
- `docs/okf/changelog.md` — Appended today's test roadmap and cart integration.

### Status Changes

- Officially registered 6 new test cases in Zephyr Scale (`QA-T44` to `QA-T49`) mapping directly to the Cart feature file.

### Verification

- Programmatic registration completed successfully with exit code 0.
- Executed `list_zephyr_testcases.py` to confirm that all 6 cart test cases are present in the Zephyr Scale registry.

## 2026-06-30 Timepieces Framework Scaling & Refactoring

### Added Items

- `src/test/java/com/timepieces/automation/tdd/api/AuthenticationEngine.java` — REST-Assured integration helper class for backend authentication.
- `src/test/java/com/timepieces/automation/tdd/api/IdempotencyApiTest.java` — TDD TestNG test validating API-level idempotency by replaying OTP generation calls with duplicate `Idempotency-Key` headers.
- `src/test/resources/features/01_authentication_identity/API_Backed_UI_Session.feature` — BDD feature specifying cookie-injection login bypass behavior.
- `src/test/resources/features/03_checkout_payments/Checkout_Idempotency.feature` — BDD feature specifying UI-level button disabling and double-click protection.
- `src/test/java/com/timepieces/automation/bdd/stepdefinitions/AccountSteps.java` — Step definitions for the login bypass feature.
- `src/test/java/com/timepieces/automation/bdd/stepdefinitions/CheckoutSteps.java` — Step definitions for the checkout idempotency feature.

### Modified Items

- `src/test/java/com/timepieces/automation/bdd/runners/*` — BaseRunner, SmokeRunner, RegressionRunner, and ProductionRunner relocated and package names updated.
- `src/test/java/com/timepieces/automation/bdd/stepdefinitions/*` — FooterSteps, LoginSteps, SidebarLoginSteps, and StagingBypassSteps relocated and package names updated.
- `src/test/java/com/timepieces/automation/tdd/api/*` — SearchApiTest, SearchApiExploratoryTest, and TwilioServiceTest relocated and package names updated.
- `src/main/java/com/timepieces/automation/base/DriverFactory.java` — Added generic RemoteWebDriver `CLOUD` mode switch using environment variables.
- `src/main/java/com/timepieces/automation/pages/web/payments/CheckoutPage.java` — Added `getPlaceOrderButton()` and processing indicator helpers.
- `pom.xml` — Configured `SmokeSuite` and `RegressionSuite` profiles.
- `testng*.xml` — Updated class packages to reflect directory segregation.
- `docs/okf/task-backlog.md` — Added P3 `TASK-CI-005` (scheduled mid/end-July).
- `docs/okf/CurrentStatus.md` — Added `STAT-017` and `STAT-018`.
- `docs/okf/changelog.md` — Updated with refactoring entries.

### Status Changes

- Restructured codebase to fully segregate TDD (API & Unit) and BDD (Gherkin Scenarios) packages.
- Added deconflicted profiles to POM and updated all TestNG XML files.
- Completed POC for API-backed UI login bypass and UI double-click idempotency.

### Verification

- Successfully compiled the project: `.\mvnw.cmd test-compile` -> BUILD SUCCESS.
- Executed the suite locally to verify renaming did not break runner executions.

## 2026-06-30 Exploratory Search API Nightly Test Suite

### Added Items

- `src/test/java/com/timepieces/automation/api/SearchApiExploratoryTest.java` — TestNG integration test implementing the 50 randomized search queries from the data pool.
- `testng-exploratory.xml` — TestNG suite XML mapping the exploratory test class and defaulting env parameter to production.
- `.github/workflows/exploratory-search-tests.yml` — GitHub Actions workflow executing nightly at 6:00 AM IST (00:30 UTC), defaulting env parameter to production.

### Modified Items

- `src/test/java/com/timepieces/automation/api/SearchApiTest.java` — Configured RestAssured socket/connection timeouts (10 seconds) and support for Cloudflare bypass header (`x-automation-bypass`).
- `docs/okf/CurrentStatus.md` — Added `STAT-016` to Completed Features.
- `docs/okf/task-backlog.md` — Added `TASK-CI-004` to completed tasks.
- `docs/okf/changelog.md` — Logged the implementation details.

### Status Changes

- Implemented nightly exploratory search API tests running 50 randomized query-handle checks against production environment by default.
- Shuffled keyword Matrix deterministic runs using time-based seeds (logged to Allure for full reproducibility).
- Configured 10 seconds RestAssured timeout and bypass headers to prevent pipeline hangs on production and staging zones.

### Verification

- Successfully compiled the project: `.\mvnw.cmd test-compile` -> BUILD SUCCESS.
- Ran tests locally: `.\mvnw.cmd test "-Dsurefire.suite=testng-exploratory.xml"` -> ran 50 tests, verified 23 passed, 27 failed (capturing expected search tuning bugs on production), and successfully generated the markdown analysis report.

## 2026-06-30 CI/CD ↔ TestNG Mapping Audit & Cron Schedule Update

### Modified Items

- `.github/workflows/staging-tests.yml` — Cron schedule changed from `0 0 * * *` (00:00 UTC / 05:30 IST) to `30 20 * * *` (20:30 UTC / 02:00 IST).
- `.github/workflows/production-tests.yml` — Cron schedule changed from `0 2 * * *` (02:00 UTC / 07:30 IST) to `30 22 * * *` (22:30 UTC / 04:00 IST).
- `.github/workflows/test-pipeline.yml` — JDK distribution changed from `oracle` to `temurin` (consistency fix with other workflows and Docker).
- `docs/okf/task-backlog.md` — Added `TASK-CI-003` for email report notifications (stashed, awaiting SMTP details from admin).

### Audit Findings

- Full mapping audit of TestNG suite XMLs ↔ GitHub Actions workflows ↔ Docker was performed.
- All mappings are correct: `testng.xml` is the default suite for staging-tests, test-pipeline, and Docker; `testng-production.xml` is correctly selected via `-Dsurefire.suite` in production-tests; `testng-bdd.xml` and `testng-tdd.xml` are local-only convenience suites.
- Runner class ↔ tag assignments confirmed: SmokeRunner (`@Smoke`), RegressionRunner (`@Regression`), ProductionRunner (`@ProdReady`).
- Only issue found was the `oracle` → `temurin` JDK distribution mismatch in test-pipeline.yml (fixed).

### Stashed Action Plan

- Email report notifications for all 3 CI/CD workflows to `167759523+GallantCaptain@users.noreply.github.com`, `stakeholder.two@timepieces-d2c.example`, `stakeholder.one@timepieces-d2c.example` using `dawidd6/action-send-mail@v5`. Blocked on SMTP credentials from admin. Tracked as `TASK-CI-003`.

### Verification

- Audited via file inspection, `git ls-files`, `git log`, and runner class review.
- No Maven build or test run was performed for this OKF update.

## 2026-06-29 Search Bar API Keyword Test Automation

### Added Items

- `src/test/resources/testdata/search_keywords.json` — Structured JSON test data resource extracted from `Timepieces_Search_Keywords.xlsx`.
- `src/test/java/com/timepieces/automation/api/SearchApiTest.java` — TestNG integration test verifying keywords against the staging search API.
- `Timepieces_Search_Keywords.xlsx` — Source spreadsheet used to derive the search keyword matrix.

### Modified Items

- `testng.xml` — Added `<test name="API Executions">` block to run `SearchApiTest`.
- `learnings.md` — Appended `LEARN-013` documenting TDD preference for API testing.
- `development-standards.md` — Added coding convention standards enforcing TDD for API automation and shared utility/BasePage reuse.
- `changelog.md` — Logged the complete search API implementation.

### Status Changes

- Bypassed staging gate programmatically to verify search keywords via REST-Assured.
- Expanded storefront search API suite to exactly 100 individual TestNG tests across 6 groups (Product Matrix, Generic Keywords, Collection Search, Brand & Misspellings, Deep Keywords, Edge Cases).
- Covered generic high-intent terms like `watches`, `watch`, `luxury watch`, `wristwatch`, etc. as standalone test assertions.
- Enhanced reporting to log results by test group with aggregated pass/fail statistics.

### Verification

- Successfully compiled the project: `.\mvnw.cmd test-compile` -> BUILD SUCCESS.
- Verified test suite executes all 100 tests properly.

## 2026-06-29 Global Footer Navigation Automation

### Added Items

- `src/test/java/com/timepieces/automation/stepdefinitions/FooterSteps.java` — Cucumber glue for footer visibility, link display, and new-tab navigation validation.

### Modified Items

- `src/main/java/com/timepieces/automation/pages/BasePage.java` — Added `isMobileViewport()` as a shared responsive-layout helper for page/component objects.
- `src/main/java/com/timepieces/automation/pages/components/FooterComponent.java` — Reworked footer locators and business/verification helpers for mobile and desktop footer behavior.
- `src/main/java/com/timepieces/automation/pages/components/CartDrawerComponent.java` — Expanded cart drawer locator coverage for future cart, upsell, summary, policy, and slide-to-pay automation.
- `src/main/java/com/timepieces/automation/pages/HeaderOnlyBasePage.java` and `src/main/java/com/timepieces/automation/pages/StoreFrontBasePage.java` — Cleaned constructor/comment formatting while preserving the header/footer composition model.
- `src/main/java/com/timepieces/automation/pages/web/HomePage.java` — Documented production/staging homepage context and fluent-interface behavior.
- `src/test/resources/features/02_catalog_product_discovery/Global_Footer_Navigation.feature` — Updated expected footer links to `Legal`, `Sitemap`, `Contact`, `Account`, and `Share your moment`, and added a new-tab navigation check.
- `src/test/java/com/timepieces/automation/runners/SmokeRunner.java` and `src/test/java/com/timepieces/automation/runners/RegressionRunner.java` — Registered `Global_Footer_Navigation.feature` in both runner scopes.
- `docs/okf/CurrentStatus.md`, `docs/okf/testing-strategy.md`, `docs/okf/project-structure.md`, `docs/okf/known-issues.md`, `docs/okf/task-backlog.md`, `docs/okf/development-standards.md`, `docs/okf/learnings.md`, and `docs/okf/changelog.md` — Updated OKF current truth for API and footer automation.

### Status Changes

- Footer automation is no longer only present as an unwired feature file; the footer feature is registered in both smoke and regression runners.
- Rest-Assured is now active for storefront search API validation; Appium and Healenium remain TBD.
- Agents now have explicit OKF guidance to import utilities and page objects and reuse `BasePage` helpers instead of inline fully qualified package calls.

### Verification

- Current working tree evidence reviewed via `git diff`, `git status --short`, and direct inspection of the changed/new files. No full Maven suite was run during this OKF update.

## 2026-06-25 Jira Backlog Analysis & Zephyr Scale Integration

### Modified Items

- `learnings.md` — Added `LEARN-012` regarding Zephyr Scale Cloud API endpoints and token authentication, and updated the troubleshooting table with error resolution guidance.
- `changelog.md` — Appended today's Jira backlog extraction and Zephyr Scale test case creation.
- `delta-updates.md` — Recorded today's modifications and Zephyr integration details.

### Status Changes

- Extracted full Jira project `QA` backlog (208 total, 49 active unresolved issues) and documented it in the artifact `jira_backlog_status.md`.
- Created 7 new BDD test cases in Zephyr Scale (`QA-T37` to `QA-T43`) covering Astonia drop card behavior, Cloudflare bypass verification, and OTP resend cooldown.

### Verification

- Successfully ran Python API scripts to fetch Jira issues and publish test cases, verifying connection status and response validation.

## 2026-06-25 Cloudflare Bypass Triage & CDP Fix

### Added Items

- `.github/workflows/cf-bypass-check.yml` — A manual diagnostic workflow that probes staging with/without the `x-automation-bypass` header to isolate the browser-send layer from the Cloudflare skip-rule layer.

### Modified Items

- `DriverFactory.java` — Fixed CDP header injection by calling `Network.enable` prior to `Network.setExtraHTTPHeaders`.
- `CurrentStatus.md` — Updated `PROG-003` and `BLK-001` to reflect that browser-side injection is resolved, but Cloudflare-side configuration blocks progress.
- `learnings.md` — Added `LEARN-011` regarding the CDP `Network.enable` requirement, and updated the troubleshooting table with guidance on using `cf-bypass-check.yml` for triage.
- `task-backlog.md` — Updated `TASK-CI-001` description and dependencies.
- `known-issues.md` — Updated `ISSUE-001` status/workaround and added CI Staging test failure triage (Run #28148134339) in Incident Notes.
- `changelog.md` — Appended today's triage and changes.

### Status Changes

- `ISSUE-001` status updated (browser-side fix completed, Cloudflare-side pending).
- `TASK-CI-001` dependencies updated.

### Verification

- Manual `cf-bypass-check` diagnostic run (`#28148005172`) verified header injection, but confirmed Cloudflare-side challenges remain.
- Framework compilation verified (`mvnw compile` -> BUILD SUCCESS).

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
