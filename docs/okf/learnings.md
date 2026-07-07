# Learnings

Last updated: 2026-07-07

| ID | Learning | Source | Reusable Guidance |
| --- | --- | --- | --- |
| LEARN-001 | `ConfigReader` precedence is JVM property, environment variable, then properties file. | `ConfigReader.java` | Prefer CLI `-D` overrides for runs; use env vars/secrets for sensitive values. |
| LEARN-002 | Optional `<env>.local.properties` files can override committed config without committing secrets. | `ConfigReader.java` | Use local override files for developer-only secrets. |
| LEARN-003 | `DriverFactory` reads `headless`, not `is.headless`. | `DriverFactory.java`, config files | Use `-Dheadless=true/false` until config keys are reconciled. |
| LEARN-004 | Empty workflow tag input lets runners use their own tags; explicit tag input overrides both runners. | `.github/workflows/test-pipeline.yml` | Avoid ad-hoc tag overrides unless intended; they can double-run overlapping scenarios. |
| LEARN-005 | Safari runs outside macOS are Chromium substitutions, not real Safari/WebKit coverage. | `DriverFactory.java` | Report and interpret Safari fallback results carefully. |
| LEARN-006 | Cloudflare bypass header is applied only for ChromiumDriver sessions. | `DriverFactory.java` | Firefox cannot receive this CDP header implementation as written. |
| LEARN-007 | OTP verification currently documents a missing attempt limit; the probe passes while the vulnerability exists. | `OTP_Bruteforce_Security.feature`, `LoginPage.java` | When app fix lands, invert the test expectation to assert lockout. |
| LEARN-008 | Allure CLI/report generation is pinned separately because Allure commandline 2.29.1 was not available in Maven Central. | `pom.xml` comment | Keep `allure.cli.version` separate from Java adapter version unless verified. |
| LEARN-009 | Dashboard flow execution is deliberately disabled until `GITHUB_TOKEN` is configured. | `dashboard/README.md`, `config.ts` | Treat greyed-out flow buttons as expected without token. |
| LEARN-010 | `hs_err_pid4140.log` is from Eclipse JDT LS on Temurin 21, not direct evidence of a Maven/Selenium test crash. | `hs_err_pid4140.log` | Triage IDE JVM crashes separately from framework execution failures. |
| LEARN-011 | CDP extra HTTP headers are silently ignored unless `Network.enable` is first executed in the same session. | `DriverFactory.java` | Always call `Network.enable` prior to `Network.setExtraHTTPHeaders` in Chromium CDP. |
| LEARN-012 | Zephyr Scale Cloud API uses region-specific base URLs (e.g. `eu.api.zephyrscale.smartbear.com/v2`) and requires Connect-app Bearer JWT tokens. | Zephyr Scale REST API | Ensure BDD Gherkin text uploaded via `/testscript` excludes `Feature:`/`Scenario:` headers, and `tags` custom fields use only pre-configured values. |
| LEARN-013 | Storefront API tests should be written in pure TDD (TestNG/Rest Assured) instead of BDD/Cucumber. | Search API testing | Maintain API-level validation as fast, code-first TestNG tests without Gherkin overlays. |
| LEARN-014 | Footer automation now depends on responsive footer behavior, so shared viewport and window helpers should be reused instead of duplicated or called with inline package names. | `BasePage.java`, `FooterComponent.java`, `FooterSteps.java`, `WindowUtils.java` | Import shared utilities at the top of Java files and call inherited `BasePage` wrappers directly from page/component classes. |
| LEARN-015 | `SelfHealingDriver.create(...)` returns a decorator that is no longer an `instanceof ChromiumDriver`, so any vendor-specific cast (like the Cloudflare CDP header cast) must run on the raw driver BEFORE wrapping with Healenium, never after. | `DriverFactory.java` | Always apply CDP/vendor-specific driver logic first, then wrap with `SelfHealingDriver` as the last step before publishing the driver. |
| LEARN-016 | `healenium.properties` is read directly by the Healenium client from the classpath root — it does not go through `ConfigReader`'s -D/env/file precedence, so it cannot vary per environment the way other config does. | `healenium.properties`, `ConfigReader.java` | Keep `hlm.server.url` manually in sync with the reference `healenium.server.url` key in the environment `.properties` files; do not expect `-Dhealenium.server.url=...` to change Healenium's own backend target. |

## Troubleshooting Notes

| Symptom | Likely Cause | First Checks |
| --- | --- | --- |
| CI cannot find page elements on staging | Cloudflare challenge may be blocking app load | Inspect screenshot/page source; confirm `CF_BYPASS_TOKEN` and Cloudflare WAF skip rule |
| Cloudflare bypass header is successfully applied but requests are still challenged | Cloudflare Bot Fight Mode or WAF Skip rule misconfiguration | Run manual diagnostic `.github/workflows/cf-bypass-check.yml` to isolate the browser-send layer from Cloudflare-rule layer. |
| Headless flag appears ignored on staging | Config key drift: staging has `is.headless`, driver reads `headless` | Run with `-Dheadless=true` or add `headless` config |
| Dashboard dispatch button disabled | Missing `GITHUB_TOKEN` | Check server env and `/api/flows` response |
| Checkout dashboard flow runs no checkout tests | Runner feature list excludes checkout feature files | Reconcile `flows.config.ts`, runner `features`, and tags |
| JDK crash file appears in repo root | IDE language server crash produced `hs_err_pid*.log` | Triage JDT LS/JDK version; do not assume test failure |
| Zephyr test case creation fails with 400 Bad Request | Required custom fields missing, invalid tag options, or Gherkin headers included in BDD script | Verify `tags` options match pre-configured Jira options (e.g., Smoke, Homepage, Login); verify BDD `testScript` text contains only raw steps. |
| Java automation code starts using inline fully qualified package calls | Agent or contributor skipped explicit imports for shared utilities/page objects | Add imports such as `BrowserUtils`, `WindowUtils`, `ConfigReader`, or page classes at the top of the file; call inherited `BasePage` methods directly where available. |
