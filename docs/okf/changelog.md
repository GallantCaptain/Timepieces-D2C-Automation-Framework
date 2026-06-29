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
- Integrated the API executions suite into `testng.xml` under a new `<test name="API Executions">` block.
- Logged key search engine bugs (e.g. searching 'wristwatch' returning nothing; searching 'watches' failing to return timepieces) and summarized a 59.38% baseline pass rate in a markdown report.
- Updated `learnings.md` and `development-standards.md` to establish TDD preferences for API automation.
- Expanded global footer BDD coverage by registering `Global_Footer_Navigation.feature` in both `SmokeRunner` and `RegressionRunner`.
- Added `FooterSteps.java` to validate footer visibility, expected footer links, and new-tab navigation using `FooterComponent`, `HomePage`, `WindowUtils`, and `ConfigReader`.
- Reworked `FooterComponent.java` with viewport-aware mobile/desktop locators, cookie-banner dismissal, footer link discovery, mobile accordion expansion, and link click/navigation helpers.
- Added `BasePage.isMobileViewport()` so page/component objects can branch on responsive layout through a shared wrapper.
- Expanded `CartDrawerComponent.java` locator coverage for cart line items, upsells, order summary, policy links, and slide-to-pay controls.
- Updated OKF standards to instruct agents to import shared utilities and reuse inherited `BasePage` methods instead of calling packages inline.
