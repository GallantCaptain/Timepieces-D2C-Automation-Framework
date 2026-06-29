# Testing Strategy

Last reviewed: 2026-06-29

## Test Levels

| Level | Current Coverage | Status |
| --- | --- | --- |
| UI smoke | Cucumber scenarios tagged `@Smoke`; current runner registers selected authentication and footer feature files | Partial |
| UI regression | Cucumber scenarios tagged `@Regression`; current runner registers selected authentication and footer feature files | Partial |
| Security probe | `@OtpBruteForce` documents missing OTP verification attempt limit | Present, opt-in |
| Real OTP E2E | `@OtpE2E` uses Twilio to retrieve live OTP | Present, opt-in and config-dependent |
| Dashboard API/UI tests | TBD; no automated dashboard test suite found |
| API tests with Rest-Assured | `SearchApiTest` verifies storefront search keywords through staging API endpoints | Present |
| Mobile tests with Appium | TBD; dependency present, no verified Appium tests found |
| Manual testing | TBD; no current manual testing standard found |

## Test Coverage Expectations

Coverage expectations are TBD. Verified current artifacts:

- 11 Gherkin feature files exist under `src/test/resources/features`.
- 4 step definition classes exist under `src/test/java/com/timepieces/automation/stepdefinitions`.
- 81 page/component object Java files exist under `src/main/java/com/timepieces/automation/pages`.
- `testng.xml` executes `SmokeRunner`, `RegressionRunner`, and `SearchApiTest`.
- `SmokeRunner` and `RegressionRunner` currently register authentication and global footer feature files.
- `SearchApiTest` loads `testdata/search_keywords.json`, calls the staging password/token/search API path, writes `target/search_keyword_api_report.md`, and asserts the baseline pass rate is at least 55%.

## Supported Browsers and Platforms

| Browser/Platform | Current Support | Notes |
| --- | --- | --- |
| Chrome | Supported by framework, Docker, dashboard flow config, workflow input | Current |
| Firefox | Supported by framework, Docker, dashboard flow config, workflow input | Current |
| Edge | Supported by framework, Docker, dashboard flow config, workflow input | CI workflow input exists; CI install step does not install Edge |
| Brave | Supported by framework, Docker, dashboard flow config, workflow input | Requires `BRAVE_PATH`; CI workflow input exists but install step does not install Brave |
| Safari | `DriverFactory` supports real Safari on macOS only; non-macOS substitutes configured Chromium fallback | Not real Safari coverage outside macOS |
| Desktop web | Current focus | Current |
| Mobile devices | TBD | Appium dependency exists but no verified tests found |

## Automation Standards

- Run local tests through Maven wrapper where possible.
- Use `testng.xml` for suite discovery.
- Keep runner feature registration and tag contracts aligned with feature files.
- Keep feature files and step definitions synchronized; feature-only living specs must be marked as not executable or wired into runners.
- Use `@OtpE2E` and `@OtpBruteForce` only as explicit opt-in flows because they rely on live OTP behavior or security probing.
- Keep secrets out of committed config. Use environment variables, CI secrets, or gitignored local properties.
- Failure artifacts: screenshots attach on failure; video attaches only when `recordVideo=true`.
- Empty workflow tag input is recommended for default runner tags; explicit `-Dcucumber.filter.tags` overrides runner tags across all runners.

## Current Executable Runner Scope

| Runner | Registered Feature Files | Tag Filter |
| --- | --- | --- |
| `SmokeRunner` | `Staging_Password_Bypass.feature`, `User_Authentication_And_OTP_Verification.feature`, `Global_Footer_Navigation.feature` | `@Smoke and not @Regression` |
| `RegressionRunner` | `Staging_Password_Bypass.feature`, `User_Authentication_And_OTP_Verification.feature`, `OTP_Login_End_To_End.feature`, `OTP_Bruteforce_Security.feature`, `Global_Footer_Navigation.feature` | `@Regression` |
| `SearchApiTest` | `src/test/resources/testdata/search_keywords.json` via TestNG/Rest-Assured, not Cucumber | TestNG class in `testng.xml` |

## Known Coverage Drift

| Drift | Impact | Related Issue |
| --- | --- | --- |
| Catalog, checkout, homepage, search UI, and sidebar feature files are present but not fully registered by current Cucumber runners. Footer is now registered in smoke and regression. | Tagged flows such as dashboard checkout may not execute intended feature files. | ISSUE-003 |
| `docs/project_context.md` mentions `SanityRunner`, but no `SanityRunner.java` exists and `testng.xml` does not reference one. | Older docs can mislead suite execution expectations. | ISSUE-004 |
| Dashboard flow config includes `dev`, but workflow dispatch accepts only `staging` and `prod`. | Dashboard-triggered `dev` runs may fail validation. | ISSUE-004 |

## Manual Testing Standards

Manual testing ownership, required evidence, test case management location, and release signoff process are TBD.

## Traceability

| Requirement | Decision | Implementation | Tasks |
| --- | --- | --- | --- |
| REQ-FWK-001 | DEC-0002, DEC-0003 | IMPL-FWK-RUNNERS-001, IMPL-FWK-PAGES-001 | TASK-RUN-001 |
| REQ-SEC-001 | DEC-0007 | `OTP_Bruteforce_Security.feature`, `LoginPage.java` | TASK-SEC-001 |
| REQ-DASH-002 | DEC-0001 | `flows.config.ts`, `github.ts`, `test-pipeline.yml` | TASK-DASH-001, TASK-DASH-002 |
