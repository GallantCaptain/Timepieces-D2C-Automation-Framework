# Task Backlog

Last reviewed: 2026-06-25

| ID | Priority | Description | Dependencies | Status |
| --- | --- | --- | --- | --- |
| TASK-OKF-001 | P0 | Create initial OKF, OKF index, topic files, ADR records, and README navigation. | Repository inspection | Completed |
| TASK-CI-001 | P0 | Unblock CI staging access. Browser-side fix completed (CDP Network.enable added in fix/cloudflare-bypass-header); Cloudflare-side configuration (Skip rule/Bot Fight Mode) remains open. | Cloudflare admin access, `CF_BYPASS_TOKEN` secret | Open |
| TASK-SEC-001 | P0 | Add server-side OTP verification attempt cap/lockout, then invert/update `@OtpBruteForce` expectations to assert the fix. | Product/app backend change | Open |
| TASK-CODE-001 | P0 | Resolve compile-breaking placeholder stubs across refactored page objects (`HomePage`, `SidebarMenuComponent`, `AstoniaCardComponent`); add business/verification/helper methods and fix Astonia logic. | Owner of component edits | Completed (2026-06-24) |
| TASK-RUN-001 | P1 | Reconcile runner feature registration, tags, and step definitions so documented smoke/regression/dashboard flows execute intended features. | QA decision on executable scope, missing glue implementation | Open |
| TASK-CFG-001 | P1 | Reconcile `headless` vs `is.headless` config drift across staging/production and docs. | Config owner decision | Open |
| TASK-DASH-001 | P1 | Configure dashboard `GITHUB_TOKEN` with required `actions:write` scope where flow dispatch is expected. | Secret management, deployment environment | Open |
| TASK-DASH-002 | P1 | Align dashboard supported envs with workflow dispatch inputs, especially `dev` vs `staging`/`prod`. | Product/dev/QA env policy | Open |
| TASK-CI-002 | P1 | Either add GitHub Pages deployment to workflow or update docs to state reports are artifact-only. | CI/release decision | Open |
| TASK-LOC-001 | P1 | Verify and complete `data-testid` locator rollout status; update page objects and OKF with exact completion state. | Product/app locator availability | Open |
| TASK-DOC-001 | P2 | Reconcile historical docs with OKF CurrentStatus without deleting history; mark superseded sections where needed. | OKF approval | Open |
| TASK-IDE-001 | P2 | Investigate `hs_err_pid4140.log` JVM crash and document root cause or mitigation. | IDE/JDK/JDT LS environment access | Open |
| TASK-QA-001 | P2 | Define manual testing standard, dashboard test coverage expectations, and release signoff requirements. | QA/product owner input | Open |
| TASK-MOBILE-API-001 | P3 | Decide whether Appium and Rest-Assured dependencies are active roadmap items or should be documented as future/TBD. | Architecture decision | Open |

## Backlog Self-Audit

- Every task has priority: yes.
- Every task has status: yes.
- Every task has dependency information: yes.
- Duplicate tasks checked: none found in this initial OKF snapshot.
