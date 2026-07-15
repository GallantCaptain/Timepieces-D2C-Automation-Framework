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
