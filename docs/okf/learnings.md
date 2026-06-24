# Learnings

Last reviewed: 2026-06-24

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

## Troubleshooting Notes

| Symptom | Likely Cause | First Checks |
| --- | --- | --- |
| CI cannot find page elements on staging | Cloudflare challenge may be blocking app load | Inspect screenshot/page source; confirm `CF_BYPASS_TOKEN` and Cloudflare WAF skip rule |
| Headless flag appears ignored on staging | Config key drift: staging has `is.headless`, driver reads `headless` | Run with `-Dheadless=true` or add `headless` config |
| Dashboard dispatch button disabled | Missing `GITHUB_TOKEN` | Check server env and `/api/flows` response |
| Checkout dashboard flow runs no checkout tests | Runner feature list excludes checkout feature files | Reconcile `flows.config.ts`, runner `features`, and tags |
| JDK crash file appears in repo root | IDE language server crash produced `hs_err_pid*.log` | Triage JDT LS/JDK version; do not assume test failure |
