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

## 2026-07-08

- **Added `G7_PriorityKeywords` test group to `SearchApiTest.java`** — 9 dedicated, always-executed checks for the business-critical bare terms `watch`, `watches`, `timepiece`, `timepieces`, `loom`, `dial`, `strap`, `clasp`, `watch case`, each asserting HTTP 200 and a non-empty result. Total deterministic test count is now 109 across 7 groups (G1-G7).
- Added `timepiece`/`timepieces`/`dial` to one representative product per watch collection, and bare `strap`/`clasp` to every Strap/Clasp SKU, in `search_keywords.json`, so the exploratory suite's random sampling pool can also draw the priority terms. `loom` was deliberately left out — no product in the 40-SKU catalog legitimately matches it.
- Added `testng-search-api.xml`, a committed convenience suite isolating `SearchApiTest` so it can be run repeatedly without also running `IdempotencyApiTest`.
- **Added `scripts/run-search-api-analysis.ps1`**, a reusable script (for both the automation team and app developers) that runs the deterministic and exploratory search API suites a configurable number of times each, parses every generated report's Full Results table, and compiles a combined per-keyword pass/fail analysis (persistent failures, flaky keywords, priority-keyword focus) to `output/search-api-analysis/<timestamp>/summary.md` + `keyword-aggregation.csv`. Fixed a file-copy race (antivirus/indexer lock immediately after the JVM exits could grab a stale snapshot) by reading report bytes directly instead of using `Copy-Item`, and added a self-consistency check against each report's own Executive Summary total.
- Ran the deterministic suite 2x (staging, 109 tests each, byte-for-byte identical results both times) and the exploratory suite 10x (production, 50 randomized tests each, fresh time-based seed per run) — 718 total observations.
- Cross-referenced the original source-of-truth spreadsheet `Timepieces_Search_Keywords.xlsx` (43 sheets: README, ALL PRODUCTS, GLOBAL, one per-product tab) to distinguish intentional "layering" design (brand-wide terms merged into every product, including accessories) from genuine live-site defects.
- **Identified and logged 4 confirmed search-relevance defects** as `ISSUE-009` through `ISSUE-012` and `TASK-SEARCH-001` through `TASK-SEARCH-005`: the entire Strap/Clasp accessory line (21 SKUs) never surfaces for any of the 11 brand-wide attribute terms (0/164 observations); `wristwatch`/`limited edition` and related dial/style compound terms return zero results every time; generic/brand queries appear capped around 18-19 results; and the priority keyword `loom` returns an unrelated fallback result set instead of an honest empty response.
- Authored `output/pdf/Automated Search API Test Results.pdf` — executive summary, methodology, priority-keyword deep-dive, the 4 findings above with business-impact framing and developer recommendations, full persistent-failure and flaky-keyword lists, and per-run raw numbers appendix.
- Updated OKF: `known-issues.md` (`ISSUE-009`-`ISSUE-012`), `task-backlog.md` (`TASK-SEARCH-001`-`TASK-SEARCH-005`), `CurrentStatus.md` (`STAT-026`), `testing-strategy.md`, `project-structure.md`, `learnings.md` (`LEARN-017`-`LEARN-020`), `.gitignore` (`output/search-api-analysis/` — regenerable, not committed).
- Verified with `mvnw -o clean test-compile` → BUILD SUCCESS.

## 2026-07-10

- **Replaced truncated flat handle lists with the full, categorized actual API response** in both `SearchApiTest.java` and `SearchApiExploratoryTest.java`'s generated reports, so a failure (or a pass) can be sent to developers as-is instead of as a bare pass/fail flag. `recordResult`'s last parameter changed from a raw `List<String>` (previously rendered via `formatReturnedHandles`, truncated at 300 characters) to a pre-formatted `String` built by a new `formatSearchResult(SearchResult)` helper that reports `Timepieces (N): ...` and `Accessories (M): ...` in full, with no truncation.
- For `SearchApiTest`'s `G1_ProductMatrix` group specifically (the only group that performs up to 4 separate searches per test — one per NAMES/ATTRIBUTES/MISSPELLINGS/RELATED category), added a per-category results breakdown (`resultsDetail`) capturing the exact keyword and full categorized response for every search performed, not just the first/failing one.
- Removed the now-dead `formatReturnedHandles` and `recordFailure` helpers from both classes; both report tables ("List of Failed Search Queries" and "Full Results") now render an `Actual Results Returned` column sourced directly from the stored formatted string.
- Ran both suites once each to verify: deterministic (`testng-search-api.xml`, staging) — 109 tests, same 7 failures as prior runs, now each failure row shows the exact untruncated timepieces/accessories returned; exploratory (`testng-exploratory.xml`, production) — 50 tests, same pattern confirmed.
- Verified with `mvnw -o clean test-compile` → BUILD SUCCESS.

### PDP locator-scraping tooling, pivoted to an external browser agent (later same day)

- **Fixed a hard compile blocker**: `FriendsAndPartners.java` had ~90 raw URLs sitting
  directly in the class body — a hard Java syntax error breaking `mvn compile` for
  the whole module. The URL list was already wrapped in a proper block comment by
  the time this was picked up; set real `PAGE_PATH`/`PAGE_TITLE` values
  (`/friends-and-partners`, `Timepieces | Friends & Partners`) to replace the
  copy-pasted placeholders that remained.
- **Investigated the PDP page-object gap**: all ~40 PDP page objects under
  `pages/web/products/` (18 timepiece + 22 accessory variants) exist only as empty
  scaffolds — correct CRTP structure and a rich behavioral Javadoc on every class,
  but zero real locators and zero Business Library / Private Helper / Verification
  methods across all of them. This is the direct blocker for the `PendingException`
  stubs already wired in `CartCheckoutSteps.java` and `WishlistSteps.java`.
- Added `src/test/java/com/timepieces/automation/tools/PdpLocatorScraper.java` (new
  `tools` package) + `testng-pdp-scraper.xml`: a one-off diagnostic tool (not a
  regression test) that logs into production with a real phone number, a
  human-solved Cloudflare Turnstile, and a file-hand-off for the manually-relayed
  SMS OTP (`target/pdp-scrape/otp-handoff.txt` — there is no existing
  interactive-input mechanism in this framework; the other two OTP paths are a
  hardcoded feature-file string or full Twilio-account auto-fetch, neither of which
  fits a real personal phone number), then scrapes every `[data-testid]` element
  (plus video/canvas/accordion/gallery/price heuristics) from each configured PDP
  handle to a markdown file.
- **First live run against production failed before even reaching the CAPTCHA
  step**: `driver.get("https://www.timepieces-d2c.example/login")` itself hung for ~3 minutes
  and threw a client-side `TimeoutException`, even though the user confirmed the
  page had actually rendered and was fully usable on screen. Root cause: Selenium's
  default "normal" page-load strategy waits for the browser's top-level `load`
  event, which likely never fired due to a persistent background connection (most
  likely Cloudflare's bot-management heartbeat). Fixed by having the tool build its
  own `ChromeDriver` directly (bypassing `DriverFactory`, scoped to this one tool)
  with `pageLoadStrategy=EAGER`, which only waits for `DOMContentLoaded` — matching
  what a human actually perceives as "loaded." Verified this fix compiles; not yet
  re-verified with a live run.
- **Pivoted the live browser-interaction step to an external browser agent
  ("Antigravity")** rather than continuing to drive it through this Selenium tool
  turn-by-turn. Authored a detailed, self-contained hand-off brief (login
  procedure with real `data-testid` values, the 4 Astonia pilot PDP URLs with a
  fallback for the `astonia-bimetal-dusk-rose` handle's uncertain URL slug, exactly
  what to capture per page, and a strict output template) so that agent can
  perform the actual production scrape independently and report back structured
  findings for the real locators/methods to be hand-written from afterward.
- `PdpLocatorScraper.java` and `testng-pdp-scraper.xml` are kept committed as a
  working, reusable fallback tool even though this pass's live scrape is being
  performed by the external agent instead.
- Verified with `mvnw -o clean test-compile` → BUILD SUCCESS (110 main + 27 test
  sources).

## 2026-07-10

- **Production Storefront Locator Scrape**:
  - Executed interactive login sequence on `https://www.timepieces-d2c.example/login` using browser automation tools.
  - Successfully bypassed Turnstile challenge and entered manual SMS OTP code.
  - Visited 8 production storefront pages and performed data gathering for interactive and verifiable elements.
  - Documented locator details, data-testids, and fallback strategies (titles, class names, aria-labels, and xpath anchors) in 8 markdown files in the artifacts directory (`astonia-stealth-silver.md`, `astonia-dusk-gold.md`, `astonia-bimetal-dusk-rose.md`, `astonia-trophy-gold.md`, `all-timepieces-page.md`, `all-accessories-page.md`, `contact-page.md`, `timepieces-house-page.md`).
- **OKF Synchronizations**:
  - Updated `task-backlog.md` (`TASK-PDP-001` status to In Progress).
  - Updated `CurrentStatus.md` (added `STAT-029`).
  - Updated `learnings.md` (added `LEARN-022`).

## 2026-07-13

### BDD Phase 4-6 features wired to runners + step definitions

- The Phase 4-6 feature specs (cart injection + domestic/international checkout,
  checkout form & promo validation, payment integration, wishlist & account
  management) existed but were registered in no runner, so Cucumber never
  executed them. Registered the international checkout-init smoke feature in
  `SmokeRunner`; registered the domestic-checkout, checkout-form, payment, and
  wishlist regression features in `RegressionRunner` (which owns `@Regression`
  scenarios including dual-tagged ones, so no double execution).
- Added matching glue for every new step across five step-definition classes
  (`CartCheckoutSteps`, `CheckoutFormSteps`, `PaymentSteps`, `WishlistSteps`,
  `AccountManagementSteps`). Params/DataTables are captured into `TestContext`;
  interactions `throw new PendingException(...)` with a targeted TODO until the
  page-object locators/methods are implemented, so scenarios execute and report
  as pending rather than undefined. Verified `mvnw test-compile` → BUILD SUCCESS.

### Inline-playable Allure videos (ffmpeg MP4 transcode)

- Monte captures a TechSmith-codec AVI, which browsers cannot play inline, so
  Allure only offered a download link. `ScreenRecorderUtil.stopAndExtract()` now
  returns a `Recording(bytes, mediaType, ext)` and transcodes the AVI to H.264
  MP4 (`yuv420p`, `+faststart`, even-dimension guard) via ffmpeg, attached as
  `video/mp4` so Allure renders an inline HTML5 player. Falls back to the raw AVI
  if ffmpeg is missing. Enabled `recordVideo` and added `ffmpeg.path` in
  `staging.properties`. Verified the exact transcode produces h264/yuv420p with
  the moov atom first.

### Docker: ffmpeg + python3 + Allure report-serving service

- Added `ffmpeg` and `python3` to `docker/Dockerfile`; forwarded `SMTP_*`/`MAIL_*`
  into the container from `run-tests.ps1` / `run-docker-headed.ps1`; entrypoint
  generates + emails the report.
- Added an `allure` service (`frankescobar/allure-docker-service`) to
  `docker-compose.yml` that watches `target/allure-results` and serves the report
  at `http://localhost:5252`, videos playing inline; verified end-to-end (report
  generates, index 200, mp4 served as `content-type: video/mp4`). Rebuilt the
  `timepieces-tests` image and confirmed ffmpeg 6.1.1 + libx264 are present.

### Emailed Allure reports (local, Docker, CI)

- Added `scripts/email_allure_report.py`: sends a pass/fail summary with the
  Allure report, fully env-driven (`SMTP_HOST/USER/PASSWORD`, `MAIL_TO`), no
  secrets in source, auto-loads a gitignored `.env`, no-ops (exit 0) when SMTP is
  unset, non-fatal on send errors. Wired into local (`run-local-headed.ps1`),
  Docker (entrypoint), and CI (an `Email Allure Report` step in staging /
  production / exploratory / test-pipeline workflows).
- CI uses link mode (`MAIL_ATTACH=false`): summary + link to the uploaded
  artifact and (in test-pipeline) the public Pages report, avoiding corporate
  `.zip` attachment filters. Local/Docker attach the zipped report.
- Verified with a real SMTP send (Gmail app password). The live send surfaced a
  bug — the body was set after adding the attachment (`set_content not valid on
  multipart`); fixed the ordering (decide attachment, set body, then attach).

### Stakeholder report hosting via GitHub Pages

- The dashboard's `reportUrl` builds `https://<owner>.github.io/<repo>/runs/<run>/`
  but no workflow published to Pages, so those links 404. Added a
  `peaceiris/actions-gh-pages@v4` "Publish Allure Reports to GitHub Pages" step to
  `test-pipeline.yml` (publishes `deploy/`, `keep_files: true` so runs accumulate),
  and pointed the CI email at the public, login-free Pages URL. Requires Pages to
  be enabled on the repo with the `gh-pages` branch as source. Verified the
  equivalent hosted-report UX locally via the Allure docker service (HTTP 200
  report at a click-only URL).

### Repository consolidation

- Extracted the test dashboard from the deleted `feature/dev-bypass-metrics-tracking`
  branch into a dedicated repo `TimepiecesTech/qa-dashboard` via `git subtree split`
  (history preserved, secrets scrubbed, only `.env.example`); added a self-contained
  `docker-compose.yml` (postgres-dev) and fixed monorepo-era README paths. This
  realizes the DEC-0001 / ADR-0001 extraction trigger.
- Embedded the Playwright/TypeScript migration POC (`automation-ts/`) into the
  monorepo by removing its nested `.git`; verified no `.env`/`node_modules`/staging
  password were staged.
- Rewrote the stale `Timepieces_Test_Execution_Cheatsheet.pdf` to current reality
  (Docker image/env vars, Allure-serve-via-Docker, inline MP4 video, BDD/TDD suite
  split, live tag set, config precedence).
- Committed the in-progress `SearchApiTest` / `SearchApiExploratoryTest` change
  (capture returned products by handle and title).

### OKF Synchronizations

- Updated `CurrentStatus.md` (added `STAT-030`..`STAT-035`), `decisions-log.md`
  (added `DEC-0011`..`DEC-0013`), `learnings.md` (added three symptom rows), and
  this changelog.
- Confirmed the automation account cannot enable GitHub Pages (lacks admin on
  `qa-automation`; `POST /pages` → 404). Logged `BLK-006`; enabling Pages is a
  repo-admin handoff task. Verified the email→link→report mechanism end-to-end
  against a locally-hosted report (the Pages link is correct but 404s until an
  admin enables Pages).
- Refreshed `Timepieces_Test_Execution_Cheatsheet.pdf` with the report email +
  hosting commands (local run helper `run-local-headed.ps1`, `docker compose up
  -d allure`, the GitHub Pages report link, `scripts/email_allure_report.py`) and
  expanded local/Docker command coverage (raw + manual-email paths, handy
  clean/compile/postgres commands).

### Excel (.xlsx) API test reports

- `SearchApiTest` and `SearchApiExploratoryTest` now also emit a downloadable
  Excel report attached to Allure alongside the existing markdown report, via a
  shared `ExcelReportUtil` (Apache POI `poi-ooxml`, test scope). Sheets: Summary
  + group/category breakdown, Full Results (every executed query), and Failures
  (non-PASS only). Added `STAT-036`. Verified end-to-end — POI reads the
  generated workbook back with the correct sheets, headers, and failure filter.

### Search API suites -> keyword-coverage reports

- Refactored `SearchApiTest` and `SearchApiExploratoryTest` into keyword-coverage
  reports. Each unique keyword is searched once (case-insensitive de-dup;
  misspellings/variations kept as distinct), and the report records only
  Category · Keyword · Actual Results Returned · Error — the subjective pass/fail
  Status column and the redundant Test Name column are removed (search relevance
  is tuned by the devs). Tests now assert only API health (HTTP 200).
  `SearchApiTest` became one flattened, de-duplicated sweep (106 unique keywords
  across NAMES/ATTRIBUTES/MISSPELLINGS/RELATED/DEEP/GENERIC/COLLECTION/BRAND/
  PRIORITY/EDGE_CASE); `ExcelReportUtil` moved to a two-sheet Summary + Results
  shape. Verified: 0 duplicate keywords, Results columns = Category/Keyword/
  Results/Error, no Failures sheet.

### Search coverage: expected results, comments, and high-priority keywords

- Added an **Expected Results** column (before Actual) and a **Comment** column
  (what is missing or extra vs. expected) to both search suites' reports (md +
  Excel), replacing any verdict. Each keyword now carries an expected handle set:
  product keywords → their handle; collection names → members; category terms
  (timepieces/straps/…) → the whole set.
- `SearchApiTest` now **always runs the high-priority keywords** (collection
  names, every product/accessory handle, and category terms) plus real-user
  **variants** (the slug and its spaced form). Sweep grew to 190 unique keywords
  (HIGH_PRIORITY=98). `ExcelReportUtil` Results sheet is now Category · Keyword ·
  Expected · Actual · Comment · Error. Verified: 0 duplicates, expected mappings
  correct (e.g. `timepieces` → all 18 timepiece handles), and the Comment diff
  (`Missing: …` / `Extra: …`).


## 2026-07-15

### PDP locator mapping — Phase 0 pilot implemented from the Antigravity scrape

- Translated the 8 production scrape artifacts (STAT-029) into real Page Object
  Model code. Copied the artifacts into the repo at
  `docs/scrape-artifacts/phase0/` for traceability.
- **Astonia timepiece PDP family** (`SharedTemplate_Astonia` + 4 variants):
  filled the shared template with 16 locators and 11 business / 15 verification
  methods — lume (light/night) image toggle, 3D viewer open/close, specification
  accordion, sibling-variant navigation (by product `href`), wishlist toggle,
  product title/price getters, and the sold-out "Interest submitted" CTA state.
  Variants set real `PAGE_PATH`/`PAGE_TITLE` (`AstoniaDuskRose` → real handle
  `astonia-bimetal-dusk-rose`); `AstoniaTrophyGold` overrides `has3DViewer()` →
  false and adds `is3DViewerAbsent()` (it is the only Astonia without a 3D viewer).
- **PLPs:** `AllTimepiecesPage` (category tabs, filter/sort drawer, product grid,
  next-release drop cards) and `AllAccessoriesPage` (same, plus the previously
  **missing** `PAGE_PATH` `/accessories/all` and `PAGE_TITLE`) — both fully
  `data-testid`-driven (`collection-*` / `accessories-*`).
- **`ContactPage`:** enquiry-form field entry + support links (WhatsApp/email/
  toll-free via stable `href`), with a no-submit safety guard documented (real
  ticket). **`TimepiecesHousePage`:** store cards scoped by city via XPath (Selenium
  CSS has no `:has()`/`:contains()`), event-video audio toggle; confirmed the
  existing `PAGE_TITLE` is the real experience-center title.
- Every non-`data-testid` PDP/content locator is tagged `// ROLLOUT-GAP` with its
  fallback tier so it is swapped when the `data-testid` rollout reaches the page.
- **Bonus fix:** corrected a CRTP self-type bug in the user's new
  `AuriquaOceanBlue` (`<AuriquaNoirRose>` → `<AuriquaOceanBlue>`).
- Verified: `mvnw -o clean test-compile` → **BUILD SUCCESS** (111 main + 28 test
  sources). Added `STAT-037`; `TASK-PDP-001` → Completed.

### Full POM-mapping plan (Phases 1–5) recorded

- Surveyed all 102 page/component files: ~71 remain incomplete scaffolds
  (excluding base abstracts and the user-owned Razorpay checkout). Recorded a
  phased plan at `docs/scrape-artifacts/POM-MAPPING-PLAN.md`: Phase 1 = remaining
  timepiece PDP families (Monarch/Arvion/Astonia Sports/Auriqua/Manifesta, 19
  files, chunks 1A–1E); Phase 2 = accessory families (cases/clasps/straps, 29);
  Phase 3 = content & account-adjacent pages (legal/orders/other/F&P, 14); Phase
  4 = Timepieces World microsite (8); Phase 5 = loose ends. Razorpay is user-owned
  and excluded. Superseded the coarse `TASK-PDP-002` with this structure.
- Recorded the Single-Deployment Clasp Rose Gold 20mm/22mm modelling decision
  (two separate URL-backed classes with a `selectSize22mm()` sibling-nav method;
  reconfirm during the Phase-2C scrape that the capsule toggle navigates).

### PDP mapping Phase 1 chunk 1A — Monarch + shared owners modal

- Implemented `SharedTemplate_Monarch` + `MonarchRoseGold`/`MonarchSilverBlack`
  from the 2026-07-15 Antigravity scrape (`docs/scrape-artifacts/phase1/`). The
  scrape resolved several Javadoc-vs-reality gaps: both variants have the lume
  toggle AND the same 3D canvas viewer as Astonia (`canvas[aria-label='Monarch']`)
  — there is no THREE.js "AR" iframe; the primary CTA is "Register Interest" and
  is ENABLED even though the product is FULLY ALLOCATED (Astonia's was a disabled
  "Interest submitted"); there is a floating Register-Interest bar; and there is
  NO band-color selector, NO video unmute control, and NO side/top scroll nav.
- Built `PdpOwnersModalComponent` — the "See all {Family} owners" social-proof
  modal (variant-filter + "Search owners…"), identical across timepiece PDPs, so
  mapped ONCE as a Component Object and wired into both `SharedTemplate_Monarch`
  and (additively) `SharedTemplate_Astonia`.
- **Correction:** an earlier note framed a shared PDP "filter/sort" widget. That
  was wrong — the widget on the PDPs is the owners modal above. The storefront's
  product FILTERS & SORT lives on the collection/PLP pages (mapped in Phase 0),
  and the two PLPs differ: timepieces filter by Watch Movement + Dial Size,
  accessories by Product Type + Size (both share Price ↑/↓ sort). Closed the
  matching gap by adding `selectDialSize()` + `getDialSizeOptionCount()` to
  `AllTimepiecesPage` and `selectSize()` + `getSizeOptionCount()` to
  `AllAccessoriesPage` (exact-label match so overlapping accessory sizes like
  "20mm" vs "20-18mm" don't collide). Fixed the mislabelling in
  `PdpOwnersModalComponent` and `POM-MAPPING-PLAN.md`.
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS (112 main + 28 test).
  Added `STAT-038`; `TASK-PDP-002` now In Progress (1A done; 1B–1E next).

### PDP mapping Phase 1 chunk 1B — Arvion

- Implemented `SharedTemplate_Arvion` + `ArvionBurgundyGold`/`ArvionEspressoSilver`/
  `ArvionNavySilver` from the 2026-07-15 Antigravity scrape
  (`docs/scrape-artifacts/phase1/arvion-*.md`). Arvion is identical in shape to
  Monarch: `h1` title with nested price, `div.product-price span`, enabled
  "Register Interest" CTA (fully allocated 184/180/153 PCS), lume toggle, 3D canvas
  viewer (`canvas[aria-label='Arvion']`), 3 sibling cards, Case/Strap/Dial/Movement
  spec accordion, "See all Arvion owners" modal (reuses `PdpOwnersModalComponent`),
  Book Consultation link, FAQ, 4 background videos. No band-color/unmute/nav.
- Variant paths/titles/prices: arvion-burgundy-gold ₹59,000, arvion-espresso-silver
  ₹55,000, arvion-navy-silver ₹57,000 (titles carry a literal " ," per the site's
  title template).
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS (112 main + 28 test).
  Added `STAT-039`.
- Flagged `TASK-PDP-004`: three families (Astonia/Monarch/Arvion) now share ~95%
  identical templates — the `SharedTimepiecePdp` base-class refactor is due.

### PDP mapping Phase 1 chunk 1C — Astonia Sports

- Implemented `SharedTemplate_AstoniaSports` + `AstoniaSportsCircuitBlue`/
  `AstoniaSportsPhantomBlack`/`AstoniaSportsPodiumGreen` from the 2026-07-15
  Antigravity scrape (`docs/scrape-artifacts/phase1/astonia-sports-*.md`). Same
  shape as Monarch/Arvion: h1 title (nested price), div.product-price span,
  enabled "Register Interest" CTA (100/100 allocated), lume toggle, 3D canvas
  (`canvas[aria-label='Astonia Sports']`), 3 sibling cards, Case/Strap/Dial/
  Movement spec accordion, "See all Astonia Sports owners" modal (reuses
  `PdpOwnersModalComponent`), Book Consultation, FAQ, 4 background videos.
- Prices: circuit-blue 71k, phantom-black 67k, podium-green 79k. Titles stored as
  the distinguishing variant-name prefix (contains-match) because the scrape
  reported inconsistent title spacing across the three pages
  ("Watch - Timepieces" vs "Watch -Timepieces"); full observed titles kept in code
  comments.
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS (112 main + 28 test).
  Added `STAT-040`. Phase 1: 1A/1B/1C done; 1D Auriqua + 1E Manifesta remain.

### PDP mapping Phase 1 chunk 1D — Auriqua (lume, NO 3D)

- Implemented `SharedTemplate_Auriqua` + `AuriquaNoirRose`/`AuriquaOceanBlue`/
  `AuriquaRacingGreen` from the 2026-07-15 Antigravity scrape
  (`docs/scrape-artifacts/phase1/auriqua-*.md`).
- KEY DELTA: Auriqua has the lume toggle but **NO 3D viewer** — the scrape
  confirmed the "Open 3D View" button and canvas are absent on all three variants
  (correcting the old class prose that implied every non-Trophy-Gold product has
  3D). The template therefore omits the 3D locators/methods and `has3DViewer()`
  returns false (kept for interface parity with the 3D-capable families).
- Otherwise the known shape: h1 title (nested price), div.product-price span,
  enabled "Register Interest" CTA (176/176/157 allocated), spec accordion, 3
  sibling cards, "See all Auriqua owners" modal (reuses `PdpOwnersModalComponent`),
  Book Consultation, FAQ, 4 background videos. Prices: noir-rose 75k, ocean-blue
  75k, racing-green 85k.
- Titles stored as the distinguishing variant-name substring (the site's title
  word order flips: "Auriqua Noir Rose, Auriqua Watch …" vs "Auriqua Watch,
  Auriqua Ocean Blue …"). AuriquaOceanBlue keeps the Phase-0 CRTP self-type fix.
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS (112 main + 28 test).
  Added `STAT-041`. Only chunk 1E (Manifesta, no loom) remains in Phase 1.

### PDP mapping Phase 1 chunk 1E — Manifesta (+ shared register-interest form); Phase 1 COMPLETE

- Implemented `SharedTemplate_Manifesta` + `ManifestaBlackOnyx`/
  `ManifestaBlueAventurine`/`ManifestaMotherOfPearl` from the 2026-07-15 Antigravity
  scrape (`docs/scrape-artifacts/phase1/manifesta-*.md`). All three are LIVE (not a
  locked drop despite the SEP-2026 teaser). DELTA: Manifesta has NO lume
  (`hasLume()`→false) but DOES have the 3D viewer (`canvas[aria-label='Manifesta']`).
  Prices: black-onyx 1,00,000, blue-aventurine 95,000, mother-of-pearl 1,15,000
  (titles stored as the distinguishing variant-name substring; site title word
  order varies).
- Built `PdpRegisterInterestModalComponent` — the "Register your interest" form
  modal (Full name / Email / Phone + country toggle / consent checkbox / submit)
  that the "Register Interest" CTA opens on every timepiece PDP — mapped ONCE as a
  Component Object and wired into the Manifesta template (`openRegisterInterest()`).
  Added a PDP share button (`clickShare()` / `isShareButtonDisplayed()`). The
  owners capsule-toggle was already modelled by `PdpOwnersModalComponent`
  (`filterByVariant`).
- The register-form + share locators are best-effort (mapped from live
  screenshots), tagged `ROLLOUT-GAP · VERIFY` pending a targeted form scrape
  (TASK-PDP-005).
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS (113 main + 28 test).
  Added `STAT-042`. **Phase 1 (all 6 timepiece families) is complete.** Next:
  `TASK-PDP-004` base refactor (dedupe the 6 templates into `SharedTimepiecePdp`
  and roll register/share onto every family), then Phase 2 (accessories).

### Timepiece PDP base refactor — SharedTimepiecePdp (TASK-PDP-004)

- Extracted `SharedTimepiecePdp`, a single CRTP base
  (`web/products/timepieces/SharedTimepiecePdp.java`) holding ALL shared
  timepiece-PDP behavior: title/price/gallery, "Register Interest" CTA + the
  shared register-interest form modal, floating CTA, share button, wishlist, lume
  toggle, 3D viewer, spec accordion, sibling-variant nav, owners modal, Book
  Consultation, FAQ, and background-video count.
- Refit all six family templates (`SharedTemplate_Astonia`/`_Monarch`/`_Arvion`/
  `_AstoniaSports`/`_Auriqua`/`_Manifesta`) to thin subclasses that only supply
  hooks: `familyName()` (3D canvas aria-label), `hasLume()` (Manifesta → false),
  `has3DViewer()` (Auriqua → false; Astonia Trophy Gold overrides at variant
  level), and Astonia's overridden `productTitleLocator()` / `imageGalleryLocator()`
  (it uses different markup). The six templates went from ~2,700 to ~309 total
  lines (99 insertions, 2,616 deletions).
- This ALSO rolled the register-interest form + share button onto EVERY timepiece
  family in one pass (previously wired into Manifesta only), satisfying the
  "register/share on every timepiece PDP" requirement.
- No external code called any timepiece PDP method (only a `PendingException`
  comment stub in `CartCheckoutSteps`), so the rename/move broke nothing.
- Astonia is the sold-out pilot (disabled "Interest submitted" CTA), so the
  inherited register-interest methods no-op there; its share/floating/consultation
  state still needs a re-scrape (TASK-PDP-005).
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS (114 main + 28 test).
  Added `STAT-043`; `TASK-PDP-004` → Completed. Next: Phase 2 (accessory PDPs).

### Applied the register/share verify scrape (TASK-PDP-005) + Astonia correction

- Ran the targeted verify scrape (`docs/scrape-artifacts/phase1/verify-register-share.md`)
  and replaced the best-effort `ROLLOUT-GAP · VERIFY` locators with confirmed ones:
  - Share button: `button[aria-label='Share']` → `button[aria-label='Share product']`
    (opens the native OS share sheet via `navigator.share`; no in-page fallback).
  - Register-interest form (`PdpRegisterInterestModalComponent`): submit →
    `button[type='submit']`; heading scoped to `h2`; phone country toggle scoped
    to the open modal via `+91`; name/email/phone placeholders confirmed. The
    modal container class is shared with the 3D viewer, so the heading (not the
    container) confirms the modal. Documented the required-field rule (submit
    disabled until all fields + consent) and added `isSubmitEnabled()`.
- CORRECTION: the verify scrape found **Astonia is NOT sold out** — its primary
  CTA is the enabled "Register Interest" that opens the register-interest form,
  and the share button, floating bar, and Book Consultation link are all present
  (same as the active families). Removed `SharedTemplate_Astonia`'s inaccurate
  `isInterestSubmittedState()` + disabled-CTA locator (no external callers) and
  corrected the Astonia notes in `SharedTimepiecePdp`. Astonia's title/gallery
  overrides remain from Phase-0 and are flagged as not-yet-re-verified.
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS (114 main + 28 test).
  Added `STAT-044`; `TASK-PDP-005` → Completed. Also wrote the Phase-2 accessories
  discovery brief (`docs/scrape-artifacts/phase2/BRIEF-2-accessories-discovery.md`).

### Phase 2 accessory mapping started — SharedAccessoryPdp base + 2A/2C

- Built `SharedAccessoryPdp` (accessory analogue of `SharedTimepiecePdp`) from the
  discovery scrape: `h1` title, text-anchored price/compat (`ROLLOUT-GAP · WEAK`,
  no stable selector), **enabled "Add to cart"** (accessories are purchasable —
  unblocks the cart/checkout BDD steps), wishlist, gallery, color/size card-button
  switcher (each navigates to a distinct PDP), Description/Technical accordions,
  Return & Guarantee side drawers (aria triggers, close 'X' + backdrop close), and
  the bottom recommended-models carousel (`div.relative.group` →
  `a[href*='/accessories/…']`).
- Chunk 2A: `SharedTemplate_WatchCase` + `PremiumWatchCaseBlack`
  (`/accessories/watch-case/premium-watch-case`).
- Chunk 2C: `SharedTemplate_SingleDeploymentClasp` + `Black`/`Gold`/`Steel` (all
  20mm) + the Rose Gold split into `SingleDeploymentClaspRoseGold20mm`/`22mm`
  (with `selectSize20mm()`/`selectSize22mm()` sibling-nav; old single
  `SingleDeploymentClaspRoseGold` removed). Per TASK-PDP-003 the color AND size
  card-buttons navigate to distinct URLs, so each is its own class. The 20mm/22mm
  pages share one `<title>` (only the URL distinguishes them). Non-Rose-Gold clasp
  titles are inferred from the verified "Single Deployment Clasp, <Color>" pattern.
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS (116 main + 28 test).
  Added `STAT-045`.

### Phase 2 accessory mapping COMPLETE — SharedStrapPdp + straps (2D–2H) + butterfly (2B)

- Built `SharedStrapPdp` over `SharedAccessoryPdp`, adding the two strap-only
  features: the dial-try carousel ("A new look for your watch";
  `button.strap-nav.strap-prev/next`) and the clasp-upgrade cross-sell ("+ Add").
- Mapped every remaining accessory family:
  - 2B Butterfly Clasps: `SharedTemplate_ButterflyClasp` + Gold/RoseGold/Steel (18mm).
  - 2D–2H Straps: 5 family templates (FKM Rubber, Hybrid/Ostrich/Suede Leather,
    Teju Lizard) + 13 variants (FKM Black/Orange/White/Yellow, Hybrid Black/Blue,
    Ostrich Black/Brown, Suede Blue/Brown/Burgundy, Teju Black/Blue).
- Strap variants inherit a family-level `PAGE_TITLE` from their family template
  (per-color identity is the URL/path — strap browser titles vary and carry typos,
  e.g. FKM Black's title reads "…in Bkack"). Clasp/watch-case variants keep
  per-variant titles.
- Every accessory PDP is now mapped; accessories carry the real enabled
  "Add to cart" that unblocks the cart/checkout BDD flows.
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS (117 main + 28 test).
  Added `STAT-046`. Phase 2 done; Phases 3–5 remain.

### Accessory test cases (BDD + TDD) authored + registered in Zephyr (QA-T68–QA-T87)

- BDD feature files under `src/test/resources/features/02_catalog_product_discovery/`:
  `Accessory_Product_Details.feature` (core details, add-to-cart, color/size
  card-button navigation, accordions, Return/Guarantee side drawers, wishlist,
  model carousel) and `Accessory_Strap_Configurator.feature` (dial-try carousel,
  clasp-upgrade cross-sell, combined strap+clasp purchase).
- TDD: `AccessoryPdpSmokeTest` (5 TestNG UI smokes driving the accessory Page
  Objects — watch case, butterfly clasp, SD clasp, rubber strap, leather strap —
  asserting the purchasable core + strap configurator). Opt-in via
  `testng-accessory-smoke.xml`; NOT in the default `testng.xml` (needs a browser +
  the live site, and CI staging is Cloudflare-blocked).
- Registered all 20 in Zephyr Scale project QA as QA-T68..QA-T87 (15 BDD `bdd`
  scripts + 5 TDD `plain` scripts) via `scripts/add_accessory_tcs.py`, which reads
  `ZEPHYR_TOKEN` from the environment (not hardcoded). Token confirmed valid
  (HTTP 200). The "Straps" tag isn't a Zephyr custom-field option, so strap cases
  reuse Accessories/Regression tags.
- SECURITY: logged ISSUE-013 — the older `scripts/add_*.py` hardcode a live,
  committed Zephyr token; recommend rotating it and moving to a secret.
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS (117 main + 29 test).
  Added `STAT-047`.

### Accessory BDD step definitions wired (AccessorySteps)

- Implemented `AccessorySteps` glue for `Accessory_Product_Details.feature` +
  `Accessory_Strap_Configurator.feature` and registered both in `RegressionRunner`
  (@Regression). Added a concrete `GenericAccessoryPdp` (drives any accessory PDP
  by URL, exposing the full accessory + strap surface) and
  `SharedAccessoryPdp.allModelCardsLinkToAccessories()`.
- Fully implemented (real POM assertions): navigate to accessory PDP, title/price/
  compat displayed, enabled "Add to cart", color variant navigation (via the
  handle-linked model carousel, since color card-buttons share a title) + size
  navigation (20mm/22mm) with URL assertions, accordion expand, Return/Guarantee
  side drawers (open + close via X and via backdrop), wishlist toggle + active
  check, model carousel + all-cards-link-to-/accessories, strap dial-try carousel
  displayed + next/prev, clasp-upgrade card displayed + "+ Add".
- Left as `PendingException` (cart-drawer internals pending project-wide; matches
  `CartCheckoutSteps`): cart badge count, cart-drawer line-item contents, the
  visual dial-preview "updates to next/previous watch" transitions, and dial
  compatibility filtering.
- Verified: clean compile (118 main + 30 test) → BUILD SUCCESS; a Cucumber
  dry-run over `@Accessories` resolved every step to glue with ZERO undefined
  steps. Added `STAT-048`.

### Phase 3 chunk 3A — Legal / policy pages

- Antigravity discovered the policy URLs via the footer legal link: all 5 live at
  `/policies/<slug>` (guarantee-policy, privacy-policy, refund-policy,
  shipping-policy, terms-of-service) and share ONE template.
- Built `SharedPolicyPage` (prose content block `div.prose.prose-lg.prose-invert`,
  main heading via first-h1/h2 [Shipping's title is an h2, not h1], last-updated
  line, section-heading count, "Back to Policies" / "View all policies" nav,
  contact mailto). Filled the 5 policy classes with real `PAGE_PATH` +
  `PAGE_TITLE` ("<Policy> | Timepieces"). No `data-testid`s on the policy body — all
  fallbacks tagged `ROLLOUT-GAP`.
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS (119 main + 30 test).
  Added `STAT-049`. Phase 3 next: 3C other content, 3D Friends & Partners, then
  3B orders + wishlist (auth-gated).

### Phase 3 chunk 3C — Other content pages

- Antigravity mapped the 4 remaining public content pages. They do NOT share a
  template (unlike the policies), so each got its own page object filled in.
- `TimepiecesWallPage` (`/timepieces-wall`): the polaroid testimonials + photos are
  rendered INSIDE a WebGL `<canvas>` via Three.js (data from `/api/timepieces-wall`)
  — there is no per-card DOM to assert on. Modeled the draggable wall container,
  the canvas, the background `<audio>`, and the music on/off toggle (whose
  `aria-label` flips between "Play background music" and "Mute background music");
  `isMusicPlaying()`/`isMusicMuted()` read that state.
- `ShareYourMomentPage` (`/timepieces-wall/share`): replaced the empty scaffold
  locators with real ones. Heading is `h1` "Show your Timepieces moment"; the gated
  "Share your moment" CTA opens a login modal ("Sign in to share your Timepieces
  moment" / "Only Timepieces owners can submit reviews…"); the "Submit your moment"
  button is rendered `disabled` until authenticated (`isSubmitMomentDisabled()`);
  the watch-photo carousel is an auto-scrolling `div.carousel-track`
  (`pointer-events-none`, no prev/next); plus Go-to-Timepieces-Wall + a Google-review
  cross-sell link.
- `SiteMapPage` (`/sitemap`): set the real title `Sitemap | Timepieces` (was
  commented out). `h1` "Sitemap" + `h2` section headings (Main Pages, Collections,
  Products, Accessories, All Accessories) over ~60 links; `hasSection(name)` +
  `hasLinkContaining(hrefFragment)` + `getAllLinkHrefs()`.
- `UpdatesPage` (`/updates`): set the real title `Timepieces Updates` (was commented
  out). Content-filter row (`div.flex.gap-2.overflow-x-auto`) with buttons "All",
  "Drops & Releases", "Delivery Updates", "Community", "Production & Quality"
  (`selectFilter(label)` / `getFilterLabels()` / `hasFilter(label)`); clickable
  news cards; hero Swiper slide-dots; View-more pagination.
- No element on any of the 4 bodies carries a `data-testid` (only the shared
  header/footer do) — every locator is a fallback tagged `ROLLOUT-GAP`.
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS. Added `STAT-050`.
  Phase 3 next: 3D Friends & Partners (public), then 3B orders + wishlist
  (auth-gated — needs the authenticated Antigravity session).

### Phase 3 chunk 3D — Friends & Partners

- Antigravity mapped the public Friends & Partners landing page + one
  representative partner profile (`kush-maini` / `vivek-oberoi`).
- `FriendsAndPartners` (`/friends-and-partners`): the scrape corrected two
  scaffold assumptions — the real title is `Friends & Partners | Timepieces Watches`
  (not "Timepieces | Friends & Partners"), and the scaffold was MISSING the
  `getPagePath()`/`getPageTitle()` overrides (it had the constants but never
  wired them), so `open()` was navigating to the bare base URL — both fixed. The
  3 "sections" (Founders & Makers, Thought Leaders, Voices) are filter TABS with
  matching `h2` headings; 69 profile cards (`a[href^='/friends-and-partners/']`,
  name = first inner `h2`, role = second); a video badge
  (`div.absolute.top-3.left-3`) marks the cards that have a video. Methods:
  `selectSection`, `clickProfile`, `getProfileCardCount`, `getVideoBadgeCount`,
  `hasSectionTab`, `hasSectionHeading`, `hasProfileCard`.
- NEW `PartnerProfilePage` (`/friends-and-partners/<slug>`): the profile shape
  warranted its own class. Path-agnostic via `openProfile(slug)` (mirrors
  `GenericAccessoryPdp`'s caller-navigates pattern). Title pattern
  `<Name> | Friends & Partners | Timepieces Watches`. Hero: name `h1`, tagline `h1`
  "A Timepieces Man", role `p`, portrait as a CSS background-image. IMPORTANT: there
  is NO "Play Video" button (the brief guessed one) — profiles that feature a
  video embed an inline autoplaying/muted/looping YouTube `<iframe>`
  (`isVideoPresent()`, only some profiles). The "numbered watches bought" render
  in a FIXED sticky bottom bar (highlighted watch → its PDP + a serial like
  `#009`); partners with multiple watches also get a "See More" button that
  expands a drawer (`max-h-0` → `max-h-[60vh]`) listing the rest, each linking to
  a `/products/<handle>` PDP. Methods: `getProfileName`, `getTaglineText`,
  `getRoleText`, `getHighlightedWatchHref`, `clickSeeMore`,
  `clickBackToFriendsAndPartners`, `isTitleForPartner`, `isTaglineCorrect`,
  `isVideoPresent`, `isStickyWatchBarDisplayed`, `hasSeeMoreDrawer`,
  `isWatchesDrawerExpanded`, `getDrawerWatchCount`, `allWatchLinksArePdps`.
- No `data-testid` on either body (only the shared header/footer) — all
  fallbacks tagged `ROLLOUT-GAP`.
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS. Added `STAT-051`. Phase
  3 next (and last): 3B Orders + Wishlist — AUTH-GATED, needs the authenticated
  Antigravity session (human-solved Cloudflare + OTP relay).

### Phase 3 chunk 3B-guest — Wishlist + Track Order (no login needed)

- Antigravity mapped both guest-accessible account-adjacent pages in a normal
  logged-out session (the 3B split paid off — no Cloudflare/OTP ceremony).
- `WishlistPage` (`/wishlist` — CONFIRMED; the scaffold Javadoc said
  `/account/wishlist`): guest wishlist state persists in `localStorage` under
  `timepieces_wishlist` (JSON array of product objects). Exposed as the public
  constant `WishlistPage.WISHLIST_STORAGE_KEY` so `WishlistSteps` can SEED the
  wishlist via JavascriptExecutor instead of clicking PDP hearts. The page has
  REAL `data-testid`s — `account-page-title` (h1), `account-breadcrumbs`,
  `nav-sidebar-wishlist` (sidebar entry) — evidence of a shared account-section
  template (good sign for 3B-auth). Empty state: `h2` "Your Wishlist is Empty" +
  "Continue Shopping" (→ `/collections/timepieces`). Populated: item-count line
  ("2 items") with `isItemCountInSync()` asserting it matches the rendered card
  count; "Clear All" (instant, NO confirm dialog); cards
  (`div.bg-[#141414].rounded-xl`) with instant per-card remove
  (`button[title='Remove from wishlist']`), PDP link, category label, `h3`
  title, variant line, price, "Sold Out" overlay. Guest cards have NO
  add-to-cart / move-to-cart — purchase goes through the PDP.
- `TrackOrdersPage` (`/track-order`): the page has BOTH header and footer (the
  Javadoc's "header only" was wrong; the `StoreFrontBasePage` base was already
  correct). `?orderId=<id>` auto-fills and auto-submits on mount
  (`openWithOrderId()`). The scaffold's 2026-07-03 locators were STALE — the
  `data-testid`s `order-details-card`/`order-number`/`order-date` and the
  "Contact Support" `button` do NOT exist in the live DOM. Replaced with the
  real structure: entry form (h1 "Track your order",
  `input[placeholder='Enter order Id']`, submit disabled while empty, reads
  "Tracking..." while loading); error "Order not found. Please check your order
  ID and try again."; result view (`h1` "Order #<id>", "Placed on <date> •
  <time>", "Timepieces in this order" with a dropdown selector for multi-item
  orders and "SN : <serial>" per item, a vertical status stepper [Order
  confirmed → In Assembly → In Testing → Ready for pickup/Shipped → Out for
  delivery → Delivered; terminal alt: Order cancelled], a shipped-only Tracking
  section [carrier, "AWB: <n>", target=_blank Track-Shipment link], and a
  Contact-support `a[href='/contact']` — an anchor, not a button).
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS. Added `STAT-052`.
  Phase 3 remaining: ONLY 3B-auth (Orders + Account nav — authenticated
  Antigravity session).

### Phase 3 chunk 3B-auth — Orders + Account nav (authenticated) — PHASE 3 COMPLETE

- Antigravity mapped the 3 auth-gated pages in a single authenticated session
  (human-solved Cloudflare + SMS-OTP for the pilot number; personal data
  redacted in the artifacts as briefed).
- `OrderHistoryPage` (`/account/orders`): FULLY `data-testid`-instrumented —
  `orders-page`, `account-page-title`, `account-breadcrumbs`, `orders-error`,
  `orders-empty` ("This space awaits your first Timepieces." + `orders-empty-cta`
  → `/collections/timepieces`), `orders-list`, `order-card` (carrying a
  `data-order-number="Timepieces<No>"` attribute — used by `openOrder()`/
  `hasOrder()`/`getOrderStatus()`), `order-card-link`
  (→ `/account/orders/Timepieces<No>`), `order-card-status`, `order-card-title`.
  Only the date/internal-id/icon inner texts are ROLLOUT-GAPs. Document title is
  the generic storefront title (not unique).
- `OrderDetailsPage` (`/account/orders/Timepieces<No>`, path-agnostic via
  `openOrder(orderNumber)`): FULLY testid'd — back link, title, variant, status
  (+label), price toggle/item-price/total/View-Breakup, internal order id,
  payment status, shipping + billing address blocks (inner texts are personal
  data; POM asserts display only), and a DELIVERED-only "Get Invoice" button.
  The old "Contact Support is currently bugged" Javadoc claim is DISPROVED: the
  "Help" button opens a Support Request sheet, itself fully testid'd with
  separate desktop/mobile variants (backdrop, sheet, close, subject toggle,
  note textarea, hidden file input [`attachSupportFile()` via sendKeys], submit
  [starts disabled — `isSupportSubmitEnabled()`]); the POM picks the variant
  via the inherited `isMobileViewport()`.
- `AccountPage` (`/account`): navigation finished — NO testids on its body (all
  ROLLOUT-GAP, unlike the orders pages): Orders (`a[href='/account/orders']`),
  Owned timepieces (`a[href='/account/owned-watches']`), Wishlist
  (`a[href='/wishlist']` — the SAME guest wishlist page), direct "Edit profile"
  link (vs the existing incomplete-banner route), "Export Profile", Logout
  (text-anchor xpath; the scrape's `:has-text()` suggestions are
  Playwright-only and were converted), profile name/designation/LinkedIn.
  `isAccountNavComplete()` asserts all four nav entries at once.
- NEW discovery: `/account/owned-watches` (Owned Timepieces) exists in the nav
  but has NO page-object scaffold in the repo — logged as a follow-up mapping
  candidate rather than silently inventing a class.
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS.
- **PHASE 3 COMPLETE → THE POM MAPPING PLAN IS DONE.** Phases 0/1/2/3 all
  mapped; the only exclusion is the user-owned Razorpay COM. Added `STAT-053`.
  Next: Phase-3 BDD + TDD test cases → Zephyr → wire step definitions
  (matching the accessories flow).

### Phase-3 test cases (BDD + TDD) + Zephyr + step-definition wiring

- Authored the Phase-3 test cases and wired the glue in the same pass (the
  mapping was already verified page-by-page, so the map → test cases → wire
  pipeline collapsed into one step).
- BDD features (4 public, registered in `RegressionRunner`; 1 auth-gated,
  deliberately NOT registered):
  - `07_content_brand_pages/Legal_Policy_Pages.feature` — outline over all 5
    policies (title/heading/prose/back+view-all nav; Shipping's h2 heading and
    Refund's "Refund & Exchange Policy" handled) + support-email check.
  - `07_content_brand_pages/Brand_Content_Pages.feature` — Wall (canvas +
    music-toggle state flip), Share Your Moment (guest gate modal open/message/
    close), Sitemap (sections, /products/ + /accessories/ links, ≥40 links),
    Updates (all 5 filters + filtered cards).
  - `07_content_brand_pages/Friends_And_Partners.feature` — landing (3 tabs,
    cards, video badge), profile hero/tagline/video/watch-links (kush-maini),
    See-More drawer (vivek-oberoi), back-nav.
  - `06_customer_retention_wishlist/Guest_Order_Tracking.feature` — entry form +
    disabled submit, unknown-id error, `?orderId` deep-link auto-submit,
    contact support.
  - `06_customer_retention_wishlist/Account_Orders_Management.feature` —
    tagged `@AuthGated @Orders` (NOT `@Regression`): Cloudflare-gated login
    means CI cannot execute it; the feature header documents this. Scenarios:
    account nav, order history cards, order details record, support sheet.
- TDD: `Phase3ContentSmokeTest` (6 smokes: policy template, sitemap index,
  updates filters, F&P directory, guest wishlist empty state, track-order
  unknown-id error) + opt-in `testng-phase3-smoke.xml`.
- Zephyr: pushed 24/24 clean via `scripts/add_phase3_tcs.py` (ZEPHYR_TOKEN from
  env) → **QA-T88–QA-T111** (18 BDD incl. 4 auth-gated + 6 TDD plain).
- Glue wiring:
  - NEW `ContentPagesSteps` — policies (slug→page switch), Wall, Share gate,
    Sitemap, Updates; owns the SHARED generic step
    `the {string} heading is displayed` (page-agnostic h1 assertion).
  - NEW `FriendsPartnersSteps`, `TrackOrderSteps`.
  - NEW `AccountOrdersSteps` — fully wired to the auth POMs; the authentication
    precondition fails fast with a clear Cloudflare message when the session is
    unauthenticated.
  - `WishlistSteps` guest flows IMPLEMENTED (was all PendingException): guest
    session reset clears the `timepieces_wishlist` localStorage key; PDP heart via
    the NEW `GenericTimepiecePdp` (timepiece analogue of `GenericAccessoryPdp`);
    item presence/removal asserted BY HANDLE via the card's PDP link
    (`WishlistPage.hasItemByHandle`/`removeItemByHandle` added); the
    displays-product precondition self-seeds via the PDP heart when absent.
    Login-sync steps remain `PendingException` (Cloudflare).
  - `CheckoutFormSteps` shared `clicks the {string} button` now handles
    "Wishlist"; the old feature's empty-state text corrected to the real DOM
    ("Your Wishlist is Empty").
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS; Cucumber dry-run over
  `@ContentPages or @FriendsPartners or @TrackOrder or @Wishlist` → 24
  scenarios, 0 failures, ZERO undefined steps. Added `STAT-054`.
- Follow-ups: `/account/owned-watches` unscaffolded (from STAT-053); auth-gated
  scenarios need a pre-authenticated session to execute.

### Manifesta drop card — deliberately deferred (product-owner decision)

- `ManifestaDropCardComponent` stays an EMPTY scaffold on purpose: the Manifesta
  drop card does not exist on the site yet (expected ~SEPTEMBER 2026), so there
  is nothing to scrape or map today. The class Javadoc now records the deferral
  and that the eventual behavior/locators will mirror `AstoniaDropCardComponent`
  (timed appearance, two-stage dismissal, permanent dismissal on drop-page
  visit; `<family>-popup-*` testid pattern). Tracked as `TASK-PDP-006` (P3,
  blocked on the site feature). Phase-5 "loose ends" in the mapping plan is
  therefore reduced to this deferred item — no actionable mapping remains in it.

### Phase 4 chunk 4A — Timepieces World (world.timepieces-d2c.example) — ALL MAPPING PHASES CLOSED

- Antigravity mapped the Timepieces World subdomain (3 artifacts: rw-experience,
  rw-product-showcase, rw-chrome). ZERO `data-testid`s exist on the entire
  subdomain — the biggest rollout gap so far; every locator is a fallback
  tagged `ROLLOUT-GAP`. Antigravity''s `:has-text()` suggestions are
  Playwright-only and were converted to XPath text-anchors.
- Showcase pages: UNIFIED SHARED TEMPLATE verdict over all 5 families → built
  NEW `SharedTemplate_TimepiecesWorldProduct` (CRTP): `getBaseUrl()` overridden to
  `https://world.timepieces-d2c.example` (distinct origin from config `base.url`); hero
  family logo (`img[alt=''<FAMILY>'']`) + tagline; "Back to scene"; calibre
  specs grid (label→value template, e.g. BASE CALIBRE → TMI VK63); "Learn more
  about <material>" buttons; the WAITLIST signup form (full name / email id /
  tel / postal / optional Instagram / consent; submit disabled-until-valid;
  `clickSubmitWaitlist()` carries a REAL-signup warning mirroring ContactPage''s
  submission warning); footer (Back to top, reservations@ mailto, Instagram,
  LinkedIn). IMPORTANT: there is NO CTA back to the www storefront — the
  showcases are strictly pre-launch waitlist pages. The 5 family classes
  (`TimepiecesWorld_{Arvion,Monarch,Auriqua,Astonia,Manifesta}Page`) thinned to
  `familyName()` + their unique `PAGE_TITLE`
  (`<Family> | <Tagline> | Timepieces Watches`).
- `TimepiecesWorld_ExperiencePage`: 7 scenes (Opening → Arvion → Monarch →
  Auriqua → Astonia → Manifesta → End). NEW discovery: an "Enter Experience"
  welcome gate precedes the canvas. Native scrolling is DISABLED (body
  overflow:hidden, scrollTop stays 0) — `advanceCanvas(deltaY)` dispatches a
  synthetic JS `WheelEvent` to nudge the canvas; progress is asserted from the
  right-side rail (active section = tall pill `h-6`, inactive = dots) and the
  "<Section><NN>%" tooltip. Family scenes expose an h2 heading + DISCOVER CTA
  (→ `/products/<family>?from=scene&frame=<n>`). The end scene is a STATIC
  grid (desktop) / list (mobile) — `isMobileViewport()` picks the variant;
  cards link to the 5 showcases (no prev/next, despite the scaffold saying
  "carousel").
- Chrome: `TimepiecesWorld_HeaderComponent` — hamburger
  (`button[aria-label=''Toggle menu'']`, icon src encodes open/closed:
  hamburger_expanded/collapased.svg) + the dual-navigation logo VERIFIED
  (experience page → www.timepieces-d2c.example; showcase page → world.timepieces-d2c.example).
  `TimepiecesWorld_SidebarMenuComponent` — drawer open/closed via
  `translate-x-0`/`-translate-x-full`, backdrop click closes, exactly the 5
  family links.
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS. Added `STAT-055`.
  With Phase 5 reduced to the deferred Manifesta drop card (TASK-PDP-006), the
  POM mapping plan has NO actionable mapping left. Next: Phase-4 test cases
  (BDD + TDD) → Zephyr → wire step definitions, matching the Phase-3 flow.

### Phase-4 (Timepieces World) test cases + Zephyr + wiring; plan extended with Phases 6–7

- Completed the map → test cases → wire pipeline for the Timepieces World
  microsite (same flow as Phase 3).
- BDD (both registered in `RegressionRunner`, tag `@TimepiecesWorld`):
  - `08_timepieces_world/Timepieces_World_Experience.feature` — welcome gate starts
    the canvas (+ 7-segment progress rail); nudging the canvas reaches a family
    scene (DISCOVER CTA); DISCOVER opens a showcase; the sidebar lists all 5
    family destinations (+ backdrop close); the header logo leaves for
    www.timepieces-d2c.example from the experience page.
  - `08_timepieces_world/Timepieces_World_Showcase.feature` — scenario outline over
    all 5 families (title pattern / logo / tagline / waitlist form); the
    waitlist submit is DISABLED until the form validates (never submitted — it
    raises a REAL signup); calibre specs + material learn-more + footer links;
    Back-to-scene; the logo returns to the experience from a showcase.
- TDD: `TimepiecesWorldSmokeTest` (6 smokes: gate/canvas/rail, nudge-to-scene,
  showcase template, waitlist gate, sidebar links, dual-nav logo) + opt-in
  `testng-timepieces-world-smoke.xml`.
- Zephyr: pushed 16/16 clean via `scripts/add_rw_tcs.py` → **QA-T112–QA-T127**
  (10 BDD + 6 TDD).
- Glue: NEW `TimepiecesWorldSteps` — a handle→page switch over the 5 showcase
  classes; the "nudges the canvas" step dispatches `advanceCanvas(3000)` inside
  a 90s WebDriverWait until the DISCOVER CTA renders (native scroll is disabled
  on the microsite); cross-origin URL assertions verify the dual-nav logo
  (experience → www.timepieces-d2c.example; showcase → world.timepieces-d2c.example). Duplicate
  When/And step-expression pitfall avoided (Cucumber ignores keywords —
  identical expressions are duplicates).
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS; Cucumber dry-run over
  `@TimepiecesWorld` → 14 scenarios, 0 failures, ZERO undefined. Added `STAT-056`.
- **PLAN EXTENDED (product owner, 2026-07-16)**: Phase 6 — the content-rich
  HOMEPAGE is still incomplete (`TODO(locators)` gaps per DEBT-008 + its many
  modals/carousels) → TASK-PDP-007 (P1, Antigravity chunk 6A). Phase 7 — the
  onboarding flow is being replaced by a NEW guided 7–8-step journey at
  `www.timepieces-d2c.example/becomemore`; `account/OnboardingPage` will be reworked
  against it once live → TASK-PDP-008 (P2, chunk 7A).

### Phase 6 chunk 6A — content-rich Homepage completed; Phase 7 approach decided

- Antigravity swept the homepage (2 artifacts: homepage-sections,
  homepage-modals). The homepage body is HEAVILY `data-testid`-instrumented
  (30+ real ids) — the opposite of Timepieces World.
- `HomePage` completed (closes the `TODO(locators)` half of DEBT-008):
  - Hero extras: desktop/mobile background videos.
  - "Two Stories, One Philosophy": Astonia release card (→
    `/new-release/astonia`, arrow CTA) + Manifesta release card with its
    LOCKED badge ("SEP, 2026") — `isManifestaStoryLocked()` /
    `getManifestaLockedText()`.
  - Brand-story parallax (desktop/mobile variants) + quote getter.
  - Friends & Partners section: title, View-all (→ `/friends-and-partners`),
    `home-partner-card`s (name/watch-family/dial-style inner structure),
    FORWARD-ONLY carousel next (no prev exists);
    `allPartnerCardsLinkToProfiles()`.
  - "Last Release" slider: title + subtitle ("February 2026. Fully
    allocated."), View-all (→ `/collections/timepieces`), cards → PDPs,
    forward-only next; `allLastReleaseCardsLinkToPdps()`.
  - "Crafted With Intent" scroll-sequence (desktop/mobile canvas variants) +
    "FEATURED IN" USP marquee.
- NEW `StoriesModalComponent` (`pages/components/`): the header-triggered
  stories viewer (`role=dialog`, `aria-modal`). Per-story progress segments;
  AUTO-ADVANCE verified — the active fill animates its inline `width:X%` and
  the modal AUTO-CLOSES after the final story; pause/play via aria-label flip;
  close button; left/right tap-hotspots ("Previous story"/"Next story");
  `getActiveFillStyle()` lets tests assert progression between two reads.
- `AstoniaDropCardComponent` upgraded: popup wrapper (`astonia-popup`)
  confirmed; VERIFIED trigger logic (~15s mount on `/`, max 2 displays) with
  the sessionStorage state exposed as public constants
  (`astonia_popup_visited`, `astonia_popup_show_count`) so step definitions can
  SUPPRESS the popup (kills a whole class of homepage flake); added
  title/message getters + `isPopupMounted()`.
- `FooterComponent` country-selector completed: dropdown listbox
  (`country-selector-dropdown`), search input, options carrying
  `data-country-code` + `aria-selected`; `searchCountry()` /
  `selectCountryByCode()` / `isCountryDropdownOpen()` /
  `getCountryOptionCount()`.
- PHASE 7 DECISION (product owner): build a NEW `Onboarding_BecomeMore.java`
  for the guided 7–8-step `/becomemore` journey and map it directly; the old
  `account/OnboardingPage` stays untouched until the old flow is sunset, then
  gets replaced with the BecomeMore locators/data. TASK-PDP-008 updated; brief
  written at `docs/scrape-artifacts/phase7/BRIEF-7A-becomemore.md` (asks
  Antigravity to walk every step, capture progression/validation, and confirm
  whether the flow is auth-gated).
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS. Added `STAT-057`.
  Remaining DEBT-008 half: the logged-in sidebar user-container children (needs
  an authenticated pass). Next: Phase-6 test cases (homepage BDD+TDD) → Zephyr
  → wire, matching the established flow.

### Phase-6 (Homepage) test cases + Zephyr + wiring

- Completed the map → test cases → wire pipeline for the content-rich homepage.
- BDD: `02_catalog_product_discovery/Homepage_Content_Sections.feature`
  (@Regression @HomepageContent, registered in `RegressionRunner`; Background
  reuses the existing StagingBypassSteps). 10 scenarios: hero showreel + mute
  chrome; Two-Stories cards (Astonia arrow CTA; Manifesta LOCKED badge
  "SEP, 2026"); brand quote ("What you wear on your wrist"); homepage Friends &
  Partners carousel (cards link to profiles); Last-Release slider (cards link
  to PDPs); Crafted-With-Intent + FEATURED-IN marquee; stories modal
  (open / ≥1 story / image / pause / next / close); the drop popup MOUNTS
  within 25s on a fresh session; the drop popup is SUPPRESSED via its
  sessionStorage state (mark `astonia_popup_visited=1` → reload → no mount in
  20s — the reusable flake-killer for every homepage-touching suite); the
  footer country selector opens and search-filters ("United").
- TDD: `HomepageContentSmokeTest` (4 smokes: full section sweep, stories modal
  open/pause/close, popup suppression, country-selector filter) + opt-in
  `testng-homepage-smoke.xml`.
- Zephyr: pushed 14/14 clean via `scripts/add_homepage_tcs.py` →
  **QA-T128–QA-T141** (10 BDD + 4 TDD).
- Glue: NEW `HomepageContentSteps` — lazy page/component fields so any scenario
  order works; the country-filter step captures the pre-search option count in
  TestContext and asserts the filtered count is smaller but non-zero.
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS; Cucumber dry-run over
  `@HomepageContent` → 10 scenarios, 0 failures, ZERO undefined. Added
  `STAT-058`. Remaining threads: 7A /becomemore scrape (brief ready) and the
  authenticated pass (logged-in sidebar children + executing the @AuthGated
  orders scenarios).

### Phase 7 chunk 7A — /becomemore onboarding: mapped, test-cased, Zephyr''d, wired

- Antigravity walked the full journey (authenticated session available, but the
  flow turned out to be PUBLIC — no login gate; it is a pre-profile funnel).
  Shape: intro ("Become More" / "Begin your Profile" / "Takes less than 60
  seconds") → method dialog (LinkedIn import / Start Manually / Close +
  privacy/terms links) → 8 sections driven by the URL query param
  `?section=name|location|about|building|watchjourney|interest|timepieceshouse|photo`
  with "Step 0X of 07" indicators (the photo step has none). Antigravity
  correctly STOPPED before the final "Submit Profile" (real profile data).
- NEW `Onboarding_BecomeMore` (per the swap-at-sunset decision — the legacy
  `OnboardingPage` is untouched): section-key constants + `openSection()`
  deep-linking; `fillPersonalInfo()` (name textarea, salutation/persona via
  Selenium `Select`, DOB; Next `button[type=submit]` DISABLED until all four
  validate); city + use-location; bio/goals textareas with voice, AI "Refine
  my bio/story" and "See examples" helpers; `selectChip()` — single-select
  watch-journey chips AUTO-ADVANCE on click, multi-select interest chips gate
  Next (selected = border-black or dark-bg encoding, both handled by
  `isChipSelected()`); Timepieces-House optional step ("Need inspiration?" →
  "Curated inspirations", "I''ll do this later" skip); photo step (avatar
  upload via hidden file input → `attachProfilePhoto()`, consent checkbox
  GATES "Submit Profile"). `clickSubmitProfile()` carries a REAL-profile
  warning mirroring ContactPage/waitlist conventions.
- ZERO page-specific data-testids on the whole flow (only the shared header) —
  another rollout gap for the testid backlog.
- BDD: `01_authentication_identity/BecomeMore_Onboarding.feature`
  (@Regression @BecomeMore, registered in RegressionRunner) — intro+dialog;
  step-1 validation gate; an 8-row section deep-link outline; watch-journey
  chip auto-advance; interests multi-select gate; house inspiration + skip;
  the consent gate (assert-enable ONLY — submit is never clicked).
- TDD: `BecomeMoreSmokeTest` (4 smokes) + opt-in `testng-becomemore-smoke.xml`.
- Zephyr: pushed 11/11 clean via `scripts/add_becomemore_tcs.py` →
  **QA-T142–QA-T152** (7 BDD + 4 TDD).
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS; Cucumber dry-run over
  `@BecomeMore` → 14 scenarios, 0 failures, ZERO undefined. Added `STAT-059`.
- Remaining thread: the AUTHENTICATED pass — the logged-in sidebar
  user-container children (DEBT-008 second half) and executing the @AuthGated
  orders scenarios in the authenticated Antigravity/browser session.

### /becomemore entry points clarified; test-account gap logged

- PRODUCT CONTEXT (owner, 2026-07-16): the Become More journey opens when a
  customer clicks "Register Interest" / "Join the waitlist" on the
  Astonia/Manifesta DROP pages — the onboarding for (usually first-time) users
  without a completed profile. A SECOND entry is design-planned from the
  regular timepiece PDPs'' "Register Interest" CTA (form filled + user logged
  in on registering interest); whether that routing is implemented is
  UNVERIFIED → logged as TASK-PDP-009 (targeted Antigravity check; if routed,
  wire the PDP register-interest flows to `Onboarding_BecomeMore`).
  `Onboarding_BecomeMore`''s Javadoc now records the entry points.
- ISSUE-014 (High): NO test / ephemeral user accounts exist, so ephemeral
  authenticated states (first-time onboarding gating, post-submit completion,
  guest→account wishlist sync, repeatable @AuthGated order flows) cannot be
  tested deterministically — everything leans on the single real pilot account
  whose state cannot be reset. Ask: dev-provisioned (ideally resettable) test
  accounts. Until then: display/gate-level assertions + manual pilot runs.
- Next: the user is running the 6B auth-sidebar scrape (authenticated
  Antigravity session) — artifact `auth-sidebar.md` expected back.

### Phase 6 chunk 6B — authenticated sidebar (DEBT-008 CLOSED) + handover coverage mandate

- Antigravity''s authenticated scrape (`auth-sidebar.md`) mapped the last gap:
  the logged-in sidebar profile container (`nav-sidebar-account-link`, a real
  testid) and its children — avatar wrapper (`div.w-13.h-13`), display name
  (`span.font-timepieces-semibold`), designation (`span.font-timepieces-regular`),
  chevron. `SidebarMenuComponent` gains `getLoggedInUserName()`,
  `getLoggedInUserDesignation()`, `isProfileContainerComplete()`, and the
  state-diff checks `isSidebarInGuestState()` (Sign-In + become-member +
  Track-Orders, no container) / `isSidebarInAuthenticatedState()` (container +
  `nav-sidebar-orders`, no Sign-In/Track-Orders). Scrape also confirmed: the
  authenticated header account-toggle hard-navigates to `/account` (guest gets
  redirected to `/account/login`), and NO wishlist counter exists in the
  sidebar or header. **DEBT-008 fully resolved — no unmapped page objects
  remain** (Razorpay COM user-owned; Manifesta drop card deferred, TASK-PDP-006).
- HANDOVER COVERAGE MANDATE (product owner, 2026-07-16): the CI Cloudflare
  blocker is GEO-CAUSED — GitHub Actions runners sit outside India, which is
  what triggers the challenge; local & Docker runs (India) are never challenged
  and must always work. Post-handover, CI red/green is the company''s concern —
  the requirement is that EVERY test is covered in local, Docker, and CI (PR +
  cron), red allowed. Wired:
  - `RegressionRunner` tags → `"@Regression or @AuthGated"` (the account-orders
    feature the owner registered now actually executes; its precondition first
    attempts an API-backed session-cookie injection via
    `AuthenticationEngine.loginViaBackendApi(9000000001)` → `timepieces_session`
    cookie [owner/Agy change], then fails fast with the Cloudflare message).
  - Default `testng.xml` now carries a "UI Smoke Executions (TDD)" block with
    all 5 smoke classes (Accessory/Phase3/TimepiecesWorld/Homepage/BecomeMore) —
    local `mvn test`, the Docker entrypoint, and the staging PR + nightly cron
    (staging-tests.yml, no tag override) all run the default suite, so all
    three environments cover everything.
  - The 8 new feature files tagged `@ProdReady` → the production nightly
    (production-tests.yml → testng-production.xml → ProductionRunner, which
    sweeps the features dir with the `@ProdReady` filter) covers them too. The
    auth-gated feature is deliberately NOT `@ProdReady` (login flow).
  - `AccountPage.open()` load signal switched to the logout button (owner/Agy
    change — steadier than the profile-image container across complete/
    incomplete profile states).
- Verified: `mvnw -o clean test-compile` → BUILD SUCCESS; FULL RegressionRunner
  dry-run (no tag override) → **99 scenarios, 0 failures, ZERO undefined**.
  Added `STAT-060`; DEBT-008 marked resolved.

### RazorpayModalComponent completed (behavior spec from the product owner)

- The last user-owned scaffold is now implemented, from the owner''s behavior
  spec + 7 live checkout screenshots (2026-07-16). The modal DOM is
  Razorpay-owned, so the inner locators are best-effort text/type anchors
  tagged `VERIFY` — TASK-PDP-010 tracks the one-time live iframe capture to
  swap in Razorpay''s real data-testids.
- BEHAVIOR SPEC recorded in the component Javadoc:
  - Opens ONLY with ≥1 cart item.
  - DOMESTIC (INR): cart "Slide to Pay" → Razorpay MAGIC CHECKOUT
    (Contact → Address → Payment). Guests enter mobile+email → magic-link OTP
    prompt ("Enter OTP sent to +91…", 4 boxes, Skip/Resend) → saved addresses
    (Home/Office, Standard FREE) surface from the magic-link identity.
    Domestic rail: UPI (incl. QR) / Cards / EMI / Netbanking / Wallet +
    offers + recommended saved card.
  - INTERNATIONAL: "Slide to Pay" routes to the Timepieces /checkout Delivery
    Information page instead; "Proceed to Payment" opens the modal in compact
    "Payment Options" mode with ONLY two method families — credit cards
    (saved/add-new) and Apple Pay (`isInternationalPaymentOptionsMode()`
    asserts both the presence of those AND the absence of UPI/EMI/Netbanking/
    Wallet).
  - PRICING: international = 1.5× domestic on ALL products; currency by region
    (EUR Europe, AED UAE, USD rest); unsupported countries (e.g. Afghanistan)
    must NOT allow purchases.
  - The modal renders in the Razorpay IFRAME — the component is frame-aware
    (`waitForModalOpen()` [default content] / `switchToCheckoutFrame()` /
    `switchBackToStorefront()` / `closeModal()` via X — esc/cancel do NOT
    close it, per the original Javadoc).
  - PAYMENT SAFETY: no component method completes a payment; verification
    payments only via Razorpay test cards, ₹1, test customers.
- With this, EVERY page/component scaffold in the framework is implemented
  (Manifesta drop card remains the single deliberate deferral, TASK-PDP-006).

### TASK-PDP-010 closed — Razorpay capture applied, payment glue wired, intl rules test-cased

- Antigravity''s iframe capture (razorpay-domestic.md / razorpay-international.md)
  delivered Razorpay''s REAL testids — `RazorpayModalComponent` rewritten around
  them (checkout-v2-container, order-amount(+fallback), gstin/order-instructions
  buttons, identity chips [domestic `overview-contact-cta` vs intl
  `contact-sidebar-widget`], nav-back/more-options/checkout-close,
  address-block/delivery-*/proceed-to-pay, saved-address items + edit +
  continue-selected, the nav-sidebar method rail [upi/card/emi/netbanking/
  wallet/recommended] with inline-offer badges, offer-container/offer-cards/
  view-all, upi-qr-container, save-card-checkbox, exit-dialog
  confirm-positive/negative). Razorpay quirks preserved verbatim: the
  misspelled `merchnatLogoImage` testid and the HYPHENATED `data-test-id` on
  `country-code-selector` + `add-card-cta`; the card form uses `card.*`
  name-attribute inputs (no testids).
- Behavioral corrections folded in: domestic step 1 is REVIEW ORDER (contact
  details are a POPUP off the identity chip, not a first step); the X opens an
  EXIT-CONFIRMATION dialog — `closeModal()` now does X → "Yes, exit", and
  `stayInCheckout()` covers the negative path. Only the guest magic-link OTP
  block remains VERIFY-tier (the captured session had a recognized identity).
- Wiring: `CartDrawerComponent.performSlideToPay()` (Actions drag across the
  full track width); `CheckoutPage.fillDeliveryInformation()` +
  `clickProceedToPayment()` + the unsupported-country alert (verified with
  KP/CU: "Country/Region in the shipping address isn''t supported");
  `CartCheckoutSteps` slide/modal-init/contact-surface/no-hard-redirect steps
  implemented; `PaymentSteps` wired end-to-end for the SANCTIONED test-card
  decline scenarios (delivery-fill → Cards → add-new-card entry → submit →
  decline-message + order-not-placed assertions) — the CDP-level token/PAN
  checks and the order-confirmation page stay honestly pending.
- NEW `05_payment_integration/International_Payment_Rules.feature`
  (@IntlPayments, registered in RegressionRunner; Background reuses the
  accessory add-to-cart seeding): /checkout routing, unsupported-country
  block, cards+ApplePay-only modal (asserts the ABSENCE of the domestic
  rails too) — glue in NEW `IntlPaymentSteps` (region switch via the footer
  country selector). 4 MANUAL Zephyr cases for the no-oracle rules: the 1.5×
  multiplier (needs the live fx rate), EUR/AED currency-by-region, the guest
  magic-link OTP flow (real SMS), and the modal close semantics.
- Zephyr: 7/7 clean via `scripts/add_razorpay_tcs.py` → **QA-T153–QA-T159**.
- Verified: clean compile; Cucumber dry-run over `@IntlPayments` → 3
  scenarios, 0 failures, ZERO undefined. Added `STAT-062`; TASK-PDP-010
  completed. (Owner also tagged 8 public features `@ProdReady` so the
  production nightly sweeps them.)

## 2026-07-17

### OWASP ZAP (DAST) integration + weekly schedule (STAT-040, TASK-SEC-002)

- **Viability assessed first: viable, and a strong fit.** The framework already
  solves ZAP's hardest problem (authenticated access to a JS-heavy SPA), already
  speaks REST, already has Docker/Allure/email/scheduling rails, and ZAP has prior
  precedent here (`docs/project_context.md:120` — earlier DAST + red-team findings
  disclosed to devs). Built on **Rest-Assured against ZAP's REST API — no new Maven
  dependency** — rather than pulling in `zap-clientapi`: one less thing to rot after
  handover.
- **New:** `docker-compose.yml` `zap` service behind a **`security` compose profile**
  (does not start with a plain compose up); `ZapScanEngine` (spider / AJAX spider /
  passive wait / active scan / alerts / reports / alert filters); `OwaspCategory`
  (**ZAP tag first → CWE fallback → UNMAPPED escape hatch**); `OwaspTop10ZapScanTest`
  (**10 category tests + 1 report-only unmapped test**); `testng-security.xml`;
  `scripts/run-zap-scan.ps1`; `.github/workflows/security-zap-scan.yml`;
  `docs/security-zap.md`.
- **Schedule:** Sat `0 18 * * 6` (Sat 23:30 IST) staging = **full ACTIVE**;
  Sun `0 18 * * 0` (Sun 23:30 IST) production = **baseline PASSIVE**. 18:00 UTC keeps
  the run on the same day in IST and is off-peak.
- **Production is passive-only by design**, enforced in **four independent places**
  (prod properties, `assertProductionIsNotBeingAttacked`, workflow guard, PS1 guard).
  An active scan on the live store could create junk orders, bill and send **real OTP
  SMS to real customers** via Twilio, and throw attack traffic at **Razorpay, a third
  party we have no authorisation to scan**. Override requires a deliberate
  `-Dzap.allow.prod.active=true`. OTP/Razorpay/payment/logout excluded from the spider
  **and** the active scanner (ZAP treats them as separate components).
- **Verified locally from an India IP (genuine results, not Cloudflare pages):**
  staging baseline 49 URLs → **204 alerts, 0 High** (matched ZAP's own `alertsSummary`
  exactly: 0/10/178/16); staging + AJAX spider → **295 alerts** (JS routes the plain
  spider misses); production baseline 285 URLs → ~4.4k alerts, **0 High after
  filtering**; the prod interlock **hard-fails in 1.5s before any packet is sent**.
  Remaining findings are configuration-level hardening (CSP, HSTS, anti-clickjacking,
  `X-Content-Type-Options`, SRI), not exploits.
- **BLK-007 raised — read before trusting a green CI run.** GitHub-hosted runners are
  Cloudflare-challenged (same geo cause as BLK-001/ISSUE-001; `cf-bypass-check` proved
  403 `cf-mitigated: challenge` **even with** the bypass header). ZAP would scan
  Cloudflare's interstitial and report zero findings — for a security control that is
  **false assurance, worse than a red build**. Workflow is therefore
  **runner-agnostic**: `runs-on: ${{ vars.ZAP_RUNNER || 'ubuntu-latest' }}`, so a dev
  sets one repo variable to a self-hosted India runner and the scans become real with
  **no code change**. The workflow also probes for a challenge and emits a `::warning::`
  so the situation is visible rather than inferred.
- **ISSUE-009 raised + mitigated:** ZAP's "PII Disclosure" rule (10062) flagged 18 High
  "credit card" leaks on production that were **all Shopify GIDs** (verified:
  `gid://shopify/MediaImage/36474036551724`, `gid://shopify/Article/574019469356`).
  Shopify's numeric GIDs are Luhn-valid and land in card-number ranges, and the rule
  never sees the surrounding `gid://shopify/…`. Demoted to **Informational, not hidden**
  via the new `zap.alert.filters` — a scan that cries wolf every Sunday gets switched
  off. Trade-off documented.
- **Bugs found and fixed by actually running it** (all four documented in
  `docs/security-zap.md`): ZAP's port **must be identical inside and outside the
  container** (`8090:8090`) or ZAP proxies its own API and self-inflicts a 502;
  XML comments **cannot contain `--`** (Surefire died on `--profile` inside
  `testng-security.xml`); the AJAX spider needs **`inScope=false` + `subtreeOnly=true`**
  (no ZAP Context is defined, so `inScope=true` makes ZAP reject its own start URL with
  an opaque `internal_error`); the AJAX spider browser must be **`firefox-headless`**
  (`zaproxy/zap-stable` ships Firefox ESR, **not** Chrome). Also recorded: ZAP's JSON
  report caps `instances` at 5 per rule, so its totals legitimately disagree with the
  summary (36 vs the authoritative 204).

### OWASP Top 10:2025 upgrade + cheatsheet refresh (STAT-041, STAT-042)

- **Assessed on evidence before building.** ZAP 2.17 tags every alert with BOTH editions
  (`OWASP_2021_A05` and `OWASP_2025_A02` side by side), and measured tag coverage is
  **identical: 237/239 = 99.2%** either way. Since classification is tag-driven, the
  upgrade was a new enum plus a prefix switch, not a rewrite — viable and cheap.
- **2025 is now the default** (`zap.owasp.edition=2025`); 2021 is retained because audit
  and compliance paperwork still cites it. An unrecognised value **fails loudly** rather
  than silently choosing a taxonomy for the whole report.
- `OwaspCategory` became an interface over `Owasp2021Category` / `Owasp2025Category`,
  chosen by the new `OwaspEdition`. Titles verified **two independent ways**:
  owasp.org/Top10/2025 and the reference URLs ZAP ships inside its own alert tags.
- **CWE fallback without duplication:** each 2025 category declares the 2021 categories it
  **descends from** and inherits their CWE sets, so there is one copy of the numbers and
  the lineage reads in code — `A01:2025` taking both `A01` and `A10` **is** the SSRF merge
  (CWE-918 now sits in Broken Access Control, confirmed on OWASP's own A01:2025 page).
  `A10:2025` "Mishandling of Exceptional Conditions" is new, has no lineage, and relies on
  tags alone. These inherited sets are a documented approximation for the ~1% no-tag path,
  not a transcription of OWASP's published 2025 CWE lists.
- **Verified on one staging scan, both editions (239 alerts, 2 UNMAPPED in each):** the 10
  CSP/SRI/anti-clickjacking Mediums move **A05:2021 → A02:2025**, exactly the documented
  promotion of Security Misconfiguration; the 2021 path stayed green; an invalid edition
  was rejected with a clear message.
- **Cheatsheet refreshed to 4 pages, and its generator committed** as
  `scripts/build_cheatsheet.py` — earlier versions were ad-hoc and the generator was lost,
  forcing a reverse-engineer from the PDF each time. New **section 11 (OWASP ZAP / DAST)**
  covers local/Docker/CI commands, the ZAP `-D` key table, the weekly schedule, the
  "staging is attacked, production is NOT" warning and the BLK-007 false-assurance box;
  new **section 12** lists every CI workflow. Also added `testng-security.xml` +
  `testng-search-api.xml`, the compose-services list, `ZAP_API_KEY`/`ZAP_RUNNER`, and the
  clean/target commands.
- **Bug caught by rendering the PDF instead of trusting a zero exit code:** a CSS `width`
  on `td` is not honoured by PyMuPDF's Story layout — it collapsed the key column and drew
  the value cell **on top of it, mangling every table in the document**, including ones
  that had been fine for months. Column widths must come from content. Recorded in the
  generator so it cannot be reintroduced.

### Failure triage: ~90% of the 71 failures were the framework, not the app (STAT-043, STAT-044)

- **Baseline:** the 2026-07-17 production run — 476 tests, **71 failed**, 148 skipped. The
  owner's read ("most of these are false failures; the app is fine") was **correct**, and
  is now proven per-failure rather than asserted.
- **Method:** browser probes dumping the **real rendered DOM**. The storefront is
  client-rendered Next.js, so curl cannot see the app — an early probe pass wasted effort
  because three of five dumps were 404 pages from guessed URLs (`/timepieces-world` is a
  separate origin, `world.timepieces-d2c.example`; `/become-more` is really `/becomemore`;
  `/share-your-moment` is `/timepieces-wall/share`). Recorded here because "verify the URL
  before you trust the dump" is the cheap lesson.
- **Cause 1 — the cookie consent bar was eating clicks.** `role=dialog`,
  `aria-labelledby=cookie-consent-title`, `z-index:9999`, pinned `bottom-0`, occupying
  **y=896..986** in the 1920x1080 window; wrapper `pointer-events:none` but the inner card
  `pointer-events:auto`. **Every** `ElementClickInterceptedException` reported a click
  point inside that band — e.g. **(740,929)**. New `SiteOverlayHandler` (reject by
  default, `cookie.consent.mode`) runs from `BasePage.open()`. One click per scenario; the
  site persists `timepieces_cookie_consent`, so later navigations no-op. Verified: bar 1→0
  nodes; the element at (740,929) is now real page content.
- **Cause 2 — `BasePage` took the first DOM match, which is usually the hidden twin.** The
  site renders responsive mobile+desktop duplicates. `visibilityOfElementLocated` /
  `elementToBeClickable` only ever consider the FIRST match, so the suite waited the full
  15s on a node CSS will never show, then reported "not displayed" about something plainly
  on screen. Measured: the hidden mobile `product-media-carousel` sits at document
  ~31,940, **before** the visible `desktop-image-gallery` at ~36,688 — which is why all
  five accessory PDP tests claimed the gallery was missing. Same shape for
  `header-stories-toggle` and the consent buttons. Now resolves the first **visible**
  match, which is also faster.
- **Cause 3 — tag-qualified testid selectors matching zero nodes.**
  `section[data-testid='home-hero-overlay']` → 0 (it is a `<div>`). An audit of every such
  selector against the rendered DOM caught `footer-accordion-toggle` (really `<button>`)
  and `country-selector` (really `<div>`, used by the intl payment tests).
  `home-two-stories-title` is not a `<p>` at the headless viewport. **The testid is the
  contract; the tag is an implementation detail.** All now tag-free.
- **Text-anchoring traps found (two of a kind).** (a) `<h1>Show your<br><span>Timepieces
  moment</span></h1>` — a `<br>` contributes **no character** to DOM text, so XPath
  `normalize-space()` can never match "Show your Timepieces moment" even though `getText()`
  shows the break; worse, it matched a *hidden* mobile h1, so it failed "not displayed"
  instead of "not found". (b) `unsupportedCountryAlert` hard-coded a **straight**
  apostrophe in "isn't supported" while web copy routinely uses a **typographic** `’`
  (U+2019). Both now anchor on text that avoids the trap.
- **A locator collision worth remembering:** `StoriesModalComponent`'s dialog was the bare
  `div[role=dialog][aria-modal=true]` — **markup-identical to the cookie bar**. It could
  have reported the stories modal open while looking at the consent banner: a false PASS,
  which is worse than a failure.
- **(b) Chrome's save-password / leak-detection / autofill bubbles suppressed** in
  `DriverFactory`. They are browser chrome painted over the page — Selenium cannot see or
  dismiss them, and they steal focus and swallow the next click.
- **The build changed mid-triage.** Devs shipped a production build that **removed "Slide
  to Pay"**: the drag track, handle and label all match 0 nodes; it is now
  `<button data-testid="cart-secure-checkout">`. `performSlideToPay()` became a plain
  click — a straight win, as the synthetic pixel-offset drag was the suite's most fragile
  interaction. The same build rolled data-testids across the whole cart drawer. All
  previously-fixed suites were re-verified against the new build and stayed green.
- **(g) The idempotency scenario was lying, and now says so.** `/checkout` redirects to the
  homepage with an empty cart (126 testids on the landed page, **zero** of them checkout),
  a `catch` swallowed the resulting `NoSuchElementException`, and
  `isProcessingIndicatorActive()` fell back to `isPlaceOrderButtonDisabled()` — the very
  thing the next step asserts, i.e. a tautology. There is also **no "Place Order" button**:
  "Proceed to Payment" only launches the Razorpay modal and the real submit lives in a
  third-party iframe. Now an explicit `PendingException` naming exactly what is missing
  (bag pre-seeding; **staging-only** if ever completed — double-clicking would create real
  orders in the payment gateway). The meaningful server-side guarantee **is** covered by
  `IdempotencyApiTest`.
- **Three real app bugs the suite caught correctly** (ISSUE-010/011/012): Monarch's hero
  logo carries `alt="ARVION"`; the Become More `contact` step performs **zero validation**
  (Continue with both phone and email empty advances); `/checkout` redirects with an empty
  cart. **ISSUE-010 was initially assumed to be one of the "false failures" and was not** —
  worth checking every one.
- **Verified:** `AccessoryPdpSmokeTest` **0/5 → 5/5**, `HomepageContentSmokeTest` **2/4 →
  4/4**, each run twice (a single green proves nothing) and again after the new build.

### Become More rework + two serious findings (STAT-045, ISSUE-013, ISSUE-014)

- **Become More onboarding reworked** against the real journey: `/becomemore` (not
  `/become-more`, which 404s), **9 screens / 7 numbered steps**, and `?section=name` /
  `?section=photo` are **not deep-linkable** — both redirect to a `contact` first screen
  the page object never modelled. TDD smoke **2/4 → 4/4** (twice); BDD `@BecomeMore`
  **4 failures → 13/13**. Submit Profile is never clicked; no profile is created.
- **ISSUE-013 (PRIVACY, High) — the journey restores a saved profile draft into brand-new
  anonymous sessions on production.** A fresh Chrome profile with a never-used phone/email
  arrived at the `name` screen already holding `"Test User" / "Mr" / "01/01/1990" /
  "Professional"`, and at `interest` with "Watches" pre-selected. **Not localStorage, and
  not keyed to the contact details entered** — the server is serving one anonymous
  visitor's in-progress details to another. **This single bug caused all four BDD
  failures**: it opens the gates the tests assert are closed, and it made `selectChip()`
  *toggle off* an already-selected chip. It also explains why they passed once and never
  again — the earlier run seeded the draft. The tests now CREATE the state they assert
  rather than depend on it; measured proof the gate itself is sound: clear name →
  `disabled=true`, refill → `disabled=false`. **Needs a dev privacy review.**
- **ISSUE-014 (High) — `BaseRunner` silently overrode `-Denv`.** It did an unconditional
  `System.setProperty("env", suiteParameter)`, and every suite XML hard-codes
  `value="staging"` — so `-Dsurefire.suite=testng-bdd.xml -Denv=production` ran against
  **staging** while the command line, the operator and the report all said production. This
  inverts the framework's own documented precedence (`-D` > OS env > properties). Caught
  when a **staging-gate password prompt** appeared in a run pointed at production, which
  has no such gate. Fixed: an explicit `-Denv` now wins and a warning is logged when the
  suite parameter is ignored. Verified after the fix: BaseRunner reports `production` and
  the staging gate is hit **0 times** (it was being hit before).
  **A framework that quietly tests the wrong environment is worse than one that fails:
  every result it gives is a truthful answer about a system you did not mean to look at.**
  It also means any earlier "verified on production" claim made through `testng-bdd.xml`
  was really a staging result — re-verified on genuine production afterwards.
- **Still open:** one background-music toggle scenario. It surfaced only because the other
  fixes unblocked steps that previously aborted first. The obvious hypothesis (headless
  blocks audio autoplay) was **disproved** by running it headed — it fails there too. Most
  likely the test assumes music starts muted and inverts when it does not. Left documented
  rather than guessed at.

### A standalone reproduction for the privacy bug (STAT-069)

- **New:** `ProfileDraftRestorePrivacyProbeTest` + `testng-privacy-probe.xml` — an isolated,
  hand-to-developer reproduction of **ISSUE-013**:
  `./mvnw test "-Dsurefire.suite=testng-privacy-probe.xml" "-Denv=production"`
- **It proves rather than asserts.** Fresh Chrome profile → phone/email generated from the
  clock (`@example.invalid`, so it can never have been used) → **Step 01 of 07 arrives
  holding `"Test User" / "Mr" / "01/01/1990" / "Professional"`**. It then re-reads
  localStorage/sessionStorage and shows the restored values are **not there** — so they came
  from the **server**, to a session whose identity has never existed. Reproduced twice with
  different identities.
- **It fails on purpose.** It asserts the CORRECT behaviour (a new visitor's form is empty),
  so it reproduces today and becomes the regression test the day it is fixed. Both the suite
  file and the class say so, to stop a well-meaning reader "fixing" it by seeding the form.
- **It redacts.** Values matching the automation suite's own seeds are printed (they are
  ours); anything else is reduced to a length + character-shape and flagged. If this bug can
  serve a real customer's details to a stranger, a probe that dumps them into a CI report,
  an emailed artifact and a screenshot has made the leak worse. **A privacy probe that leaks
  is not a privacy probe.**
- **A lead for the fixer, not just a symptom.** `ds_visitor_id` was **identical across
  separate fresh Chrome profiles** (`5520b8f06dc744458497225e7c1ee77d`) while
  `ds_session_id` changed every run. A visitor id that survives a brand-new browser profile
  is derived — IP and/or fingerprint — which would explain every observation: **the draft
  follows the machine, not the person.** If that is the key, anyone sharing an IP (office,
  household, cafe, mobile CGNAT) could be served each other's in-progress details, and it is
  served **before the phone number is ever verified**, so there is no authentication
  boundary in front of it. Recorded as unconfirmed: a client-side lead, not a diagnosis.
- **A false positive I caught in my own evidence, and removed:** an early version reported
  "profile draft present on the client? YES" because a substring scan for `persona` matched
  the consent key `ad_personalization`. A privacy report that overstates its evidence is
  worse than one that claims less — the first thing a developer does is check the claim, and
  the first false one costs the report its credibility. It now asks the narrower, verifiable
  question: are the ACTUAL restored values anywhere in client storage?

### OKF traceability repair (STAT-070)

- **I duplicated the STAT id space and had to fix it.** Every status id added on 2026-07-17
  (`STAT-040`–`STAT-045`) collided with an existing entry: I assigned ids by counting from
  the top of the table rather than from the highest id in use (`STAT-062`). It surfaced only
  because a later `sed` silently no-oped against an id that already existed.
- Renumbered to **STAT-063…STAT-069**, re-pointed every cross-reference in
  `known-issues.md`, and verified both directions: zero duplicate ids remain, and every
  `ISSUE-*` → `STAT-*` reference resolves to a real entry.
- **The lesson, recorded because it is exactly the kind of thing the OKF is for:** an id is
  only worth anything if it means one thing. A duplicate id is worse than no id, because it
  looks like traceability. Assign from `max(existing) + 1`, never from the top of the file.

- **Same mistake, second register: the ISSUE id space was duplicated too.** `ISSUE-009`–
  `ISSUE-014` added on 2026-07-17 collided with existing entries (the real `ISSUE-009` is
  "Strap/Clasp SKUs never appear", nothing to do with ZAP). Renumbered to
  **ISSUE-015…ISSUE-020**, with every reference re-pointed in `CurrentStatus.md`, the page
  objects, the feature files, the probe and its suite:
  `009→015 (ZAP PII false positive)`, `010→016 (Monarch alt)`, `011→017 (contact validates
  nothing)`, `012→018 (/checkout redirect)`, `013→019 (privacy draft restore)`,
  `014→020 (BaseRunner -Denv override)`.
  Earlier changelog entries are left as written — this log is append-only, and rewriting it
  to look tidy would destroy the one thing it is for. **This note is the mapping.**

### The last false failure: the Timepieces Wall music toggle (STAT-071)

- **The bug was in the assertion's shape, not its locator.** The scenario clicked the music
  toggle ONCE and then asserted `isMusicPlaying()` — an **absolute** state after a
  **relative** action. That is only correct if the page starts muted.
- **Measured on production 2026-07-23** — it starts the other way round:
  ```
  initial        -> aria-label "Mute background music"   (i.e. already PLAYING)
  after 1 click  -> aria-label "Play background music"   (i.e. now MUTED)
  after 2 clicks -> "Mute background music"              (playing again)
  ```
  The wall **autoplays**, so one click mutes it and the old assertion **could never pass**.
- **Fixed by asserting the invariant that is actually true:** capture the state before the
  click, then assert it **flipped**. That holds whichever way the page starts, so it keeps
  testing the control rather than the page's default — and it survives the day someone
  turns autoplay off.
- The earlier hypothesis (headless blocking audio autoplay) was **disproved** by running it
  headed: it failed there too. Recording that because a plausible-but-wrong theory that
  goes unchecked is how a test gets a `@Disabled` instead of a fix.
- `@ContentPages` now **10/10 on production**.

### removeDriver() was leaking browsers (STAT-072)

- **`DriverFactory.removeDriver()` was a bare `tlDriver.remove()`** — it cleared the
  ThreadLocal and left the Chrome + chromedriver processes **alive**.
- **Why it stayed hidden:** every caller reads that name as "release the driver", and the
  BDD `Hooks` plus the five TDD UI smoke classes all happened to call `driver.quit()`
  themselves first. So the method's own omission was invisible — until a caller forgot,
  which is precisely what the new `ProfileDraftRestorePrivacyProbeTest` did
  (`@AfterClass` → `removeDriver()` only). Over a long run that orphans one browser per
  invocation.
- **Fixed at the source rather than in the callers:** `removeDriver()` now quits
  defensively and drops the ThreadLocal in a `finally`, so even a `quit()` that throws
  cannot strand a dead driver for the next test on that thread. Double-quit is harmless —
  callers that already quit hit an already-closed session, which is caught and ignored.
  **Better a duplicate quit than a leaked process.**
- **A method whose name promises cleanup must actually clean up.** Relying on every future
  caller to remember the second half is how a machine ends up with forty orphaned
  chromedrivers after an overnight run.
- **VERIFIED 2026-07-23**, both paths, with process counts either side:
  the double-quit path (homepage smoke) still **4/4**, and the previously-leaking path
  (privacy probe) still reproduces the bug — **0 chromedriver processes left behind by
  either**.

### Headless by default + honest video recording (STAT-073, closes ISSUE-005)

- **Headless is now the DEFAULT** for both staging and production. Override with
  `-Dheadless=false`.
- **ISSUE-005 closed on the way.** `staging.properties` defined only `is.headless`, but
  `DriverFactory` reads `headless` — so `ConfigReader.get("headless")` returned null,
  `Boolean.parseBoolean(null)` was false, and **every staging run silently went HEADED**
  regardless of what the file said. Both keys are now present, in agreement, and
  documented as to which one actually takes effect.
- **`recordVideo=true` in production too** — video is on for every test.
- **The guard is the important half.** Monte records the **physical desktop**
  (`Toolkit.getDefaultToolkit().getScreenSize()`), not the browser. A headless browser
  never paints there, so "record video" + "run headless" do not combine: together they
  produce a video of your wallpaper. On a 476-test suite that is hundreds of large,
  identical, useless files whose names give no hint they are junk.
- **Why the old check missed it:** `GraphicsEnvironment.isHeadless()` describes the **JVM**,
  not the browser. On Windows/macOS with a desktop session it is `false` even when Chrome
  runs `--headless=new` — so recording proceeded and captured an empty screen.
  `canRecordMeaningfully()` now consults the browser's own headless flag as well and skips
  with a loud, actionable warning naming both ways to get real video.
- **Where video DOES work:** headed locally (`-Dheadless=false`), and **Docker**, whose
  **Xvfb** virtual display gives the browser somewhere to paint that Monte can see. That
  path already exists in the image — no new code needed.
- **Screenshots are unaffected.** Selenium's `TakesScreenshot` asks the BROWSER for its
  pixels, so failure screenshots keep working headless — evidenced by an earlier headless
  run (48 headless drivers) that attached 11 screenshots.
- **VERIFIED 2026-07-23** on a 10-scenario headless run: driver reported `headless=true`,
  the guard fired 10/10, and `target/screen-recordings/` contained **0 files**.

### Environment-scope tags + the cross-browser deferral (STAT-074, DEC-0014)

- **`@StagOnly` / `@ProdOnly` added.** Metadata only — no runner or behaviour change, so
  they cannot destabilise the comprehensive run they were added ahead of.
- **`@StagOnly` (2 scenarios):** `Staging_Password_Bypass` (production has no password wall,
  so the scenario is *meaningless* there, not flaky) and `Valid_User_Login_Sidebar`
  (production login sits behind a **human-solved** Cloudflare Turnstile — impossible to
  automate, not merely failing).
- **`@ProdOnly` (13 scenarios):** both Timepieces World features. The reason is the
  interesting part: `TimepiecesWorld_ExperiencePage` and `SharedTemplate_TimepiecesWorldProduct`
  **override `getBaseUrl()`** to a hardcoded `https://world.timepieces-d2c.example`, so **`-Denv` is
  ignored** — a `-Denv=staging` run silently drives PRODUCTION while the report says
  staging. That is the same class of lie as ISSUE-020, and the tag is what makes it visible.
- **`@ProdReady` is NOT `@ProdOnly`.** The first is a permission ("safe to run against
  production"); the second a restriction ("only meaningful on production"). A scenario can
  hold both — these do.
- Verified by dry-run: `@StagOnly`→2, `@ProdOnly`→13, `not @StagOnly and not @ProdOnly`→95.
  110 total, consistent.
- **DEC-0014 — cross-browser stays as separate Maven invocations; the in-suite parallel
  refactor is DEFERRED to the owner's personal repo.** `DriverFactory` reads the browser
  from a **JVM-global system property** with no ThreadLocal, so a TestNG `parallel="tests"`
  suite passing `browserName` per block would race on one global value: last writer wins,
  **both suites quietly run the same browser while the report claims otherwise** — exactly
  the ISSUE-020 failure mode fixed the same week. The safe pattern is already in use in
  `docker/entrypoint.sh:34` (`for browser in $BROWSERS`). Deferring a refactor whose
  failure mode is a *plausible-looking wrong answer* is the right call days before handover.

### ISSUE-013 — Zephyr token removed from HEAD (rotation still required)

- The hardcoded Zephyr Scale API bearer token is gone from `scripts/add_cart_tcs.py`,
  `add_checkout_tcs.py`, `add_payment_tcs.py`, `add_retention_tcs.py`. All ten
  `add_*_tcs.py` scripts now read `os.environ.get("ZEPHYR_TOKEN")` and abort with exit 2
  when it is unset — the pattern the newer scripts already used, copied verbatim rather
  than reinvented so all ten behave identically.
- **Verified:** all ten compile; **zero** literal token assignments remain anywhere in
  `scripts/`; running one without the env var aborts cleanly (exit 2) **without making a
  network call**.
- **THE TOKEN IS STILL IN GIT HISTORY AND STILL LIVE. Rotating it in SmartBear remains
  REQUIRED and is the only action that actually ends the exposure.** Removing it from HEAD
  narrows future copying; it does not un-leak a credential that is already public.
- **History was deliberately NOT rewritten.** Force-pushing a `filter-repo` rewrite breaks
  every existing clone of a shared repo, and it buys nothing once the credential is known
  to have leaked — rotation is the fix, not archaeology.
- Fixed a cosmetic defect while there: the abort message used an **em-dash**, which renders
  as `?` on a Windows cp1252 console — the same mojibake already fixed once in
  `email_allure_report.py`. Replaced with a hyphen in all ten scripts, so the one message a
  developer sees when the token is missing is actually readable.
- These scripts are standalone Zephyr uploaders: they appear in no `pom.xml`, workflow,
  Docker file or `src/` path, so this change cannot affect a test run.
