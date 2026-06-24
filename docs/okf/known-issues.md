# Known Issues

Last reviewed: 2026-06-24

## Open Issues

| ID | Description | Severity | Status | Workaround | Traceability |
| --- | --- | --- | --- | --- | --- |
| ISSUE-001 | CI smoke suite is blocked when `staging.timepieces-d2c.example` serves a Cloudflare "verify you are human" challenge to the GitHub-hosted runner. | High | Open | Planned WAF skip rule keyed on automation header, or self-hosted runner with allowlisted IP. | TASK-CI-001, RISK-CI-CF |
| ISSUE-002 | OTP verification accepts unlimited wrong OTP submissions according to committed security probe comments and scenario expectations. | Critical | Open | No framework workaround; requires application-side attempt cap/lockout. Keep `@OtpBruteForce` opt-in. | TASK-SEC-001, RISK-SEC-OTP |
| ISSUE-003 | Feature files for catalog, checkout, footer, homepage, search, and sidebar exist but are not registered by the current Cucumber runners. | High | Open | Run only verified runner scope until runners/glue are reconciled. | TASK-RUN-001, RISK-COVERAGE-DRIFT |
| ISSUE-004 | Documentation drift exists across README/context/workflow/dashboard config. Examples: `SanityRunner` is referenced but absent; README mentions GitHub Pages publish without a current deploy step; dashboard lists `dev` while workflow accepts `staging`/`prod`. | Medium | Open | Treat OKF CurrentStatus as current truth; preserve older docs and update them deliberately. | TASK-DOC-001, TASK-CI-002, TASK-DASH-002 |
| ISSUE-005 | `staging.properties` defines `is.headless=false`, but `DriverFactory` reads `headless`; production has both keys. | Medium | Open | Use CLI `-Dheadless=...` or add `headless` to staging until config is reconciled. | TASK-CFG-001 |
| ISSUE-006 | `hs_err_pid4140.log` records a JVM `EXCEPTION_ACCESS_VIOLATION` in VS Code Red Hat Java language server using Temurin 21.0.10. Root cause is TBD. | Medium | Open | Restart affected IDE/JDT LS process; deeper root-cause analysis TBD. | TASK-IDE-001 |
| ISSUE-007 | CI workflow browser inputs include Edge and Brave, but the workflow install step only installs Chrome and Firefox. | Medium | Open | Use Docker test bed for Edge/Brave until CI browser setup is aligned, or update workflow. | TASK-RUN-001 |
| ISSUE-008 | Uncommitted working-tree edit in `AstoniaCardComponent.java` has an incomplete `log.info("The ")` statement at line 63. | High | Open | Complete the catch-block statement before running compile/test verification. | TASK-CODE-001, RISK-BUILD-BREAK |

## Resolved Issues

No resolved issues have been recorded in the OKF yet.

## Incident Notes

| Incident | Evidence | Learning |
| --- | --- | --- |
| JVM crash on 2026-06-23 at 11:08:06 IST | `hs_err_pid4140.log` shows `EXCEPTION_ACCESS_VIOLATION`, problematic frame `jvm.dll+0x70c0c9`, current thread `GC Thread#1`, command line for Eclipse JDT LS. | Treat as IDE/JDT LS crash until proven otherwise; do not present as an automation test failure. |
