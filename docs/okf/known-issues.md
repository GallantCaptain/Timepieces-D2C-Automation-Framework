# Known Issues

Last reviewed: 2026-06-29

## Open Issues

| ID | Description | Severity | Status | Workaround | Traceability |
| --- | --- | --- | --- | --- | --- |
| ISSUE-001 | CI smoke suite is blocked when `staging.timepieces-d2c.example` serves a Cloudflare "verify you are human" challenge to the GitHub-hosted runner. | High | Open | Browser-side header injection fixed (CDP Network.enable added in fix/cloudflare-bypass-header). Cloudflare side: must disable Bot Fight Mode (on Free plan) or broaden WAF Skip rule to cover Bot Fight Mode/Browser Integrity Check. | TASK-CI-001, RISK-CI-CF |
| ISSUE-002 | OTP verification accepts unlimited wrong OTP submissions according to committed security probe comments and scenario expectations. | Critical | Open | No framework workaround; requires application-side attempt cap/lockout. Keep `@OtpBruteForce` opt-in. | TASK-SEC-001, RISK-SEC-OTP |
| ISSUE-003 | Feature files for catalog, checkout, homepage, search UI, and sidebar remain partially or separately wired; footer is now registered in both current Cucumber runners. | High | Open | Run only verified runner scope until runners/glue are fully reconciled. Use `SearchApiTest` for current search API coverage. | TASK-RUN-001, RISK-COVERAGE-DRIFT |
| ISSUE-004 | Documentation drift exists across README/context/workflow/dashboard config. Examples: `SanityRunner` is referenced but absent; README mentions GitHub Pages publish without a current deploy step; dashboard lists `dev` while workflow accepts `staging`/`prod`. | Medium | Open | Treat OKF CurrentStatus as current truth; preserve older docs and update them deliberately. | TASK-DOC-001, TASK-CI-002, TASK-DASH-002 |
| ISSUE-005 | `staging.properties` defines `is.headless=false`, but `DriverFactory` reads `headless`; production has both keys. | Medium | Open | Use CLI `-Dheadless=...` or add `headless` to staging until config is reconciled. | TASK-CFG-001 |
| ISSUE-006 | `hs_err_pid4140.log` records a JVM `EXCEPTION_ACCESS_VIOLATION` in VS Code Red Hat Java language server using Temurin 21.0.10. Root cause is TBD. | Medium | Open | Restart affected IDE/JDT LS process; deeper root-cause analysis TBD. | TASK-IDE-001 |
| ISSUE-007 | CI workflow browser inputs include Edge and Brave, but the workflow install step only installs Chrome and Firefox. | Medium | Open | Use Docker test bed for Edge/Brave until CI browser setup is aligned, or update workflow. | TASK-RUN-001 |

## Resolved Issues

| ID | Description | Severity | Resolved | Resolution | Traceability |
| --- | --- | --- | --- | --- | --- |
| ISSUE-008 | Working-tree edits left compile-breaking placeholder stubs across refactored page objects: duplicate `private final By varName` fields and an invalid identifier (`private final By 15`) in `HomePage.java`, duplicate `varName` placeholders in `SidebarMenuComponent.java`, plus a redundant try/catch and a `BasePage.wait`-shadowing field in `AstoniaCardComponent.java`. | High | 2026-06-24 | Removed all placeholder stubs; real locators preserved and `TODO(locators)` markers left where genuine `data-testid`s are still pending. Added business/verification/helper methods (incl. the missing `dismissAstoniaCard()`) and fixed Astonia logic. `mvnw test-compile` → BUILD SUCCESS. | TASK-CODE-001, RISK-BUILD-BREAK |

## Incident Notes

| Incident | Evidence | Learning |
| --- | --- | --- |
| JVM crash on 2026-06-23 at 11:08:06 IST | `hs_err_pid4140.log` shows `EXCEPTION_ACCESS_VIOLATION`, problematic frame `jvm.dll+0x70c0c9`, current thread `GC Thread#1`, command line for Eclipse JDT LS. | Treat as IDE/JDT LS crash until proven otherwise; do not present as an automation test failure. |
| CI Staging test failure on 2026-06-24 at 13:53 UTC (Run #28103614499) | Allure artifact contains screenshot `2673bf9a230b778e.png` displaying the Cloudflare "Verify you are human" checkbox page. | Confirmed that headless Chrome in GitHub Actions runner is still intercepted; WAF skip rule is not yet implemented on Cloudflare (TASK-CI-001). |
| CI Staging test failure on 2026-06-25 at 07:21 UTC (Run #28148134339) | `cf-bypass-check` run logs (Run #28148005172) show HTTP 403 `cf-mitigated: challenge` on both authenticated and unauthenticated requests. | Confirmed that the WAF skip rule is not covering the challenge. This indicates that either Browser Integrity Check is triggering first, or the zone is on a Cloudflare Free plan where Bot Fight Mode is enabled and cannot be bypassed via custom WAF rules. |
