# Changelog

This changelog is append-only. New entries must be added chronologically and must not rewrite earlier entries.

## 2026-06-24

- Created the initial OKF under `docs/okf/`.
- Added topic files for project overview, system architecture, development standards, testing strategy, project structure, current status, decisions, known issues, backlog, risks, learnings, changelog, and delta updates.
- Added ADRs for current framework decisions and OKF governance that were not yet captured in formal ADR format.
- Updated root `README.md` to act as navigation hub for the OKF.
- Recorded drift items found during repository inspection, including runner/feature mismatch, `SanityRunner` documentation drift, config key drift, dashboard env/workflow mismatch, and Allure publishing documentation mismatch.
- Recorded `hs_err_pid4140.log` as an IDE/JDT LS JVM crash incident with root cause TBD.
- Recorded an uncommitted `AstoniaCardComponent.java` incomplete statement as a known issue, backlog item, current-status blocker, and build-break risk.

### Page-object refactor cleanup (later same day)

- Resolved the page-object build break (ISSUE-008 / TASK-CODE-001 / RISK-BUILD-BREAK / BLK-005): removed compile-breaking placeholder stubs from `HomePage.java` (duplicate `varName` fields + invalid identifier `15`) and `SidebarMenuComponent.java` (duplicate `varName` placeholders).
- `AstoniaCardComponent.java`: removed the `BasePage.wait`-shadowing field and redundant try/catch blocks, added the missing `dismissAstoniaCard()` business method, and replaced the misleading reappearance methods with correctly-scoped timed checks (`clickAstoniaDropCardWhenVisible(int)`, `isAstoniaDropCardDisplayedWithin(int)`).
- Added business/verification methods to `HomePage` (hero/loaded checks, `clickDiscoverCta` → `AstoniaDropPage`) and `SidebarMenuComponent` (submenu navigation, login-state checks incl. `isUserLoggedIn`).
- `LoginPage.java`: removed dead commented-out OTP-send rate-limit locators (throttle disabled on staging).
- Verified with `mvnw -o test-compile` → BUILD SUCCESS.
- Left `TODO(locators)` markers where real `data-testid`s are still pending (tracked as DEBT-008 / TASK-LOC-001).

### CI/CD Failure Analysis (later same day)

- Analyzed failed GitHub Actions workflow run `#28103614499` (staging-tests).
- Downloaded and inspected the `allure-report-staging-1` artifact.
- Traced the test failures (unmute button missing, login welcome message missing) to a Cloudflare human verification check page shown to the headless browser.
- Logged the incident under `Incident Notes` in `known-issues.md` to track resolution block.

## 2026-06-25

- Triaged and analyzed failed CI staging run `#28148134339` on the `fix/cloudflare-bypass-header` branch.
- Inspected the logs of the manual `cf-bypass-check` diagnostic run `#28148005172`.
- Confirmed that the `X-Automation-Bypass` header is correctly injected browser-side after the `Network.enable` fix, but Cloudflare still challenges both authenticated and unauthenticated requests.
- Documented that either Bot Fight Mode on the Free plan is active (which ignores custom bypass rules), or Browser Integrity Check/Security Level settings are triggering before custom WAF rules can bypass them.
- Updated `known-issues.md` with today's incident report and revised bypass recommendations.
- Queried and analyzed the complete Jira backlog for project `QA` (208 total issues, 49 active unresolved issues). Created the detailed backlog status artifact `jira_backlog_status.md`.
- Generated and uploaded 7 new BDD test cases (`QA-T37` to `QA-T43`) to Zephyr Scale using the SmartBear API, covering the Astonia timed drop card popup logic, Cloudflare bypass, and OTP resend cooldown.
- Updated `learnings.md` with a new key learning (`LEARN-012`) and troubleshooting notes on Zephyr Scale Cloud API endpoints, authentication, Gherkin formatting, and required custom fields.

## 2026-06-29

- Designed and automated storefront search bar keyword tests using Rest Assured.
- Converted `Timepieces_Search_Keywords.xlsx` into a structured test resource JSON (`search_keywords.json`) under `src/test/resources/testdata/` to keep tests fast and self-contained.
- Implemented `SearchApiTest.java` as a pure TDD TestNG test class under `com.timepieces.automation.api`, adhering to the standard that API tests run in TDD rather than BDD/Cucumber.
- Bypassed the gatekeeper password wall programmatically using `POST /api/verify-password` and extracted tokens/timestamps via `POST /api/request-token` to execute secure, headless search API requests.
- Refactored and expanded `SearchApiTest.java` to contain exactly 100 individual TestNG tests across 6 groups (Product Matrix, Generic Keywords, Collection Search, Brand & Misspellings, Deep Keywords, Edge Cases).
- Integrated generic watch terms (e.g. `watches`, `watch`, `luxury watch`, `wristwatch`, etc.) into standalone test assertions.
- Integrated the API executions suite into `testng.xml` under a new `<test name="API Executions">` block.
- Logged key search engine bugs (e.g. searching 'wristwatch' returning nothing; searching 'watches' failing to return timepieces) and summarized a 59.38% baseline pass rate in a markdown report.
- Refactored the report generator in `SearchApiTest` to produce group-wise pass/fail metrics.
- Updated `learnings.md` and `development-standards.md` to establish TDD preferences for API automation.
- Expanded global footer BDD coverage by registering `Global_Footer_Navigation.feature` in both `SmokeRunner` and `RegressionRunner`.
- Added `FooterSteps.java` to validate footer visibility, expected footer links, and new-tab navigation using `FooterComponent`, `HomePage`, `WindowUtils`, and `ConfigReader`.
- Reworked `FooterComponent.java` with viewport-aware mobile/desktop locators, cookie-banner dismissal, footer link discovery, mobile accordion expansion, and link click/navigation helpers.
- Added `BasePage.isMobileViewport()` so page/component objects can branch on responsive layout through a shared wrapper.
- Expanded `CartDrawerComponent.java` locator coverage for cart line items, upsells, order summary, policy links, and slide-to-pay controls.
- Updated OKF standards to instruct agents to import shared utilities and reuse inherited `BasePage` methods instead of calling packages inline.

## 2026-06-30

- Performed full CI/CD ↔ TestNG suite ↔ Docker mapping audit; confirmed all mappings are correct.
- Updated staging-tests cron schedule to 02:00 IST (20:30 UTC) and production-tests cron schedule to 04:00 IST (22:30 UTC).
- Fixed JDK distribution mismatch in test-pipeline.yml (`oracle` → `temurin`) for consistency with other workflows and Docker.
- Confirmed `.github/modernize/` and `.github/java-upgrade/` directories are safe to delete (modernization tool artifacts with no code/CI dependencies).
- Stashed email report notification plan (TASK-CI-003) for all 3 CI/CD workflows to `167759523+GallantCaptain@users.noreply.github.com`, `stakeholder.two@timepieces-d2c.example`, `stakeholder.one@timepieces-d2c.example` using `dawidd6/action-send-mail@v5`; awaiting SMTP credentials from admin.
- Implemented nightly exploratory search API test suite (TASK-CI-004) executing 50 randomized search queries from the data pool, running at 6:00 AM IST (00:30 UTC), defaulting to production environment.
- Created `SearchApiExploratoryTest.java` with randomized shuffling, time-based seed logging for reproducibility, and RestAssured connection/socket timeouts (10 seconds) to prevent pipeline hangs.
- Configured Cloudflare bypass header `x-automation-bypass` in `SearchApiExploratoryTest.java` and `SearchApiTest.java`.
- Created `testng-exploratory.xml` to isolate the exploratory suite.
- Created `.github/workflows/exploratory-search-tests.yml` to trigger execution nightly.
- Restructured package directories to segregate BDD (`com.timepieces.automation.bdd`) and TDD (`com.timepieces.automation.tdd`) tests.
- Replaced TestNG runner config and XML suite files to reflect the new package structure.
- Introduced `SmokeSuite` and `RegressionSuite` Maven profiles in `pom.xml` to run TestNG groups and Cucumber tags together.
- Enhanced `DriverFactory.java` to support generic RemoteWebDriver target under `CLOUD` mode.
- Created `AuthenticationEngine.java` to generate fast session tokens via Rest-Assured, and added step definitions in `AccountSteps.java` to inject `"timepieces_session"` cookies for API-backed UI login bypass POC.
- Created `IdempotencyApiTest.java` to test API-level idempotency, and added `Checkout_Idempotency.feature` / `CheckoutSteps.java` to verify button lockout after double-clicks to prevent duplicate payment submissions.

## 2026-07-01

- Structured the storefront test case roadmap into a new OKF topic document `test-case-roadmap.md` covering 6 distinct functional phases.
- Registered the new roadmap topic in `OKF_INDEX.md` and updated its review timestamp.
- Created `Cart_Bag_Management.feature` under `src/test/resources/features/03_cart_bag_management/` detailing 6 Gherkin BDD test scenarios for cart drawer transitions, product additions, quantity modifications, item removal, free shipping progress indicators, and session persistence.
- Implemented and executed `scripts/add_cart_tcs.py` to register the new BDD scenarios as official test cases `QA-T44` through `QA-T49` in Zephyr Scale using the SmartBear Connect API.
- Verified successful registration of the test cases via `list_zephyr_testcases.py`.
- Refactored FLUENT INTERFACE doc comments in all page objects under the `timepieces` directory to return the correct class type in `{@code this}` and removed redundant "final step" comments.

## 2026-07-02

- **Refactored Browser Synchronization:** Refactored `waitForPageLoad` in `BrowserUtils.java` to return `boolean` (consistent with other wait methods), aligned Javadoc `@param` ordering, and used a safe readyState check `"complete".equals(...)` to prevent potential NPEs.
- **Aligned TestNG Maven Profiles:** Added TestNG `@Test(groups = "smoke")` to `SmokeRunner.java` and `@Test(groups = "regression")` to `RegressionRunner.java` and `SearchApiTest.java`. This ensures the runner classes are correctly resolved and executed by the `SmokeSuite` and `RegressionSuite` Maven profiles in `pom.xml`.
- **Page Object Restructuring & Cleaning:** Reorganized the `otherPages` directory structure, renaming `TimepiecesHouse.java` -> `TimepiecesHousePage.java`, `SiteMap.java` -> `SiteMapPage.java`, and `TheWallPage.java` -> `TimepiecesWallPage.java`. Created the `timepieces_world` directory to house `TimepiecesWorld_ArvionPage.java` etc., and cleaned up unused classes.
- **Codebase-wide Javadoc & Formatting:** Refactored class Javadoc blocks across all page objects, test runners, step definitions, and utility classes to standard templates (adding `@version 1.0` and `@author Avinash Rath`), normalized import sections, and formatted indentation.

## 2026-07-03

- **Codebase Javadoc Refactoring**: Scanned all 131 Java files and added/updated class-level Documentation Comments containing `@Description`, `@since 2026-07-03`, `@version 1.0`, and `@author Avinash Rath`. Built and ran a parsing state machine to ensure comments/Javadocs are not nested or broken.
- **BDD/TDD Staging Gate Alignment**: Verified and aligned staging gate bypass logic across BDD and TDD test layers.
  - Implemented missing step definitions `Given the user bypasses the staging password gate` and `Given the user is on the storefront homepage` in `StagingBypassSteps.java` to support background scenarios in feature files.
  - Added UI staging gate unlock hooks (`StagingGatePage.unlockIfPresent()`) in `CheckoutSteps.java` and `AccountSteps.java` directly after navigating.
  - Added the `x-automation-bypass` header in RestAssured requests within `AuthenticationEngine.java` and `IdempotencyApiTest.java` to prevent Cloudflare edge blocking on GHA runners.
- **Verification**: Ran `./mvnw clean test-compile` successfully.

## 2026-07-04

- Created `Checkout_Form_Validation.feature` under `src/test/resources/features/04_checkout_secure_funnel/` detailing 6 Gherkin BDD scenarios for checkout form validation (mandatory fields, email formats, pincodes), billing address form expansion, and promo code applications.
- Implemented and executed `scripts/add_checkout_tcs.py` to register the new BDD scenarios as official test cases `QA-T50` through `QA-T55` in Zephyr Scale.
- Verified successful registration of the test cases via `list_zephyr_testcases.py`.
- Updated `test-case-roadmap.md` Phase 4 status to `IN PROGRESS` and logged `STAT-021` in `CurrentStatus.md`.
- Created `Payment_Integration_Validation.feature` under `src/test/resources/features/05_payment_integration/` detailing 6 Gherkin BDD scenarios for payment validations (card formats, decline reasons like insufficient funds, expired card, incorrect CVV), secure token transmission, and redirection to the success page.
- Implemented and executed `scripts/add_payment_tcs.py` to register the new BDD scenarios as official test cases `QA-T56` through `QA-T61` in Zephyr Scale.
- Verified successful registration of the test cases via `list_zephyr_testcases.py`.
- Updated `test-case-roadmap.md` Phase 5 status to `IN PROGRESS` and logged `STAT-022` in `CurrentStatus.md`.
- Created `Wishlist_And_Account_Management.feature` under `src/test/resources/features/06_customer_retention_wishlist/` detailing 6 Gherkin BDD scenarios for wishlist additions/removal, wishlist guest-to-member syncing, order history viewing, order tracking checkpoints, and shipping address edit settings.
- Implemented and executed `scripts/add_retention_tcs.py` to register the new BDD scenarios as official test cases `QA-T62` through `QA-T67` in Zephyr Scale.
- Verified successful registration of the test cases via `list_zephyr_testcases.py`.
- Updated `test-case-roadmap.md` Phase 6 status to `IN PROGRESS` and logged `STAT-023` in `CurrentStatus.md`.

## 2026-07-06

- Performed a complete audit of all 67 test cases in Zephyr Scale.
- Updated `Automation Status` custom field to `"Automated"` for test cases `QA-T1`, `QA-T3`, `QA-T9`, `QA-T18`, and `QA-T32` (reflecting actual automation status in runners and step definitions). All other test cases marked `"Not Automated"`.
- Appended the new `"TDD"` tag and ensured the `"API"` tag is present for the 5 TDD-scoped API test cases (`QA-T7`, `QA-T33`, `QA-T34`, `QA-T35`, `QA-T36`).
- Appended the new `"BDD"` tag to the remaining 62 UI/storefront test cases to separate BDD and TDD automation scopes.
- Batch-pushed all updates successfully to Zephyr Scale using GET-modify-PUT calls with retry handling.

## 2026-07-07

- **Implemented Healenium self-healing locators (DEC-0010, ADR 0010):** Wrapped the WebDriver built in `DriverFactory.initDriver()` with `com.epam.healenium.SelfHealingDriver` at the single construction point shared by every page object, component, and step definition (`getDriver()`), so no page object changes were needed.
- Added a new `wrapWithHealenium(WebDriver)` helper in `DriverFactory.java`, gated by a new `healenium.enabled` config key (default `false` in both `staging.properties` and `production.properties`) and fail-open on any Healenium initialization error.
- Fixed the wrap ordering relative to the existing Cloudflare CDP bypass header: `applyCloudflareBypassHeader(driver)` now always runs on the raw vendor driver BEFORE `wrapWithHealenium`, since the CDP `instanceof ChromiumDriver` cast would not match the Healenium decorator.
- Added `src/test/resources/healenium.properties` (backend URL, recovery tries, score cap, node-attribute scoring) — read directly by the Healenium client, independent of `ConfigReader`.
- Added `healenium-postgres` and `hlm-backend` services plus a `healenium-pgdata` volume to `docker-compose.yml`, kept separate from the existing `postgres-dev` metrics database.
- Verified with `mvnw -o clean test-compile` → BUILD SUCCESS.
- Updated OKF: `system-architecture.md` (new component/integration entries), `project-structure.md` (new files/config locations), `CurrentStatus.md` (`STAT-025`, resolved the Healenium half of `DEBT-006`), `task-backlog.md` (`TASK-MOBILE-API-001`), `decisions-log.md` (`DEC-0010`), and added `docs/adr/0010-healenium-self-healing-locators.md`.
- Authored `output/pdf/healenium-implementation-guide.pdf` documenting the integration for a developer who has never used Healenium before.

### Docker & Kubernetes onboarding guide (later same day)

- Authored `output/pdf/docker-kubernetes-guide.pdf` ahead of a team transition: walks this repo's actual Docker usage (`docker-compose.yml` service by service, `docker/Dockerfile` instruction by instruction, `docker/entrypoint.sh`, and a comparison of `docker/run-tests.ps1` vs. `scripts/run-docker-headed.ps1`), clarifies CI does NOT run inside this project's Docker image, then adds a clearly-labeled generic Kubernetes foundations primer (concept map from Docker terms, a standalone `kubectl` exercise) since Kubernetes is not implemented anywhere in this repository today.

### CI/CD pipelines guide (later same day)

- Authored `output/pdf/cicd-pipelines-guide.pdf` ahead of the same team transition: documents this repo's real GitHub Actions setup in full (`staging-tests.yml`, `production-tests.yml`, `exploratory-search-tests.yml`, `test-pipeline.yml`, `cf-bypass-check.yml` — triggers, secrets, concurrency groups, and the dashboard's `workflow_dispatch` REST integration via `dashboard/server/src/github.ts`), cross-references already-documented pipeline drift (`ISSUE-001`, `BLK-001`, `BLK-002`, `DEBT-004`, `DEBT-005`, `ISSUE-007`), then reproduces the staging smoke+regression suite as an illustrative Jenkinsfile, `.gitlab-ci.yml`, and `azure-pipelines.yml`, with a platform comparison and concept-translation table. Only the GitHub Actions pipeline is implemented in this repository; the other three are clearly labeled illustrative.
