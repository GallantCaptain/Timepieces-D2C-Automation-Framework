# Risks

Last reviewed: 2026-06-25

| ID | Risk | Probability | Impact | Mitigation | Traceability |
| --- | --- | --- | --- | --- | --- |
| RISK-CI-CF | Cloudflare blocks GitHub-hosted runners before the app loads. | Observed | High | Complete WAF skip rule keyed on `X-Automation-Bypass`, or use self-hosted allowlisted runner. | ISSUE-001, TASK-CI-001 |
| RISK-SEC-OTP | OTP verify endpoint remains brute-forceable without attempt cap/lockout. | Observed in committed security probe | Critical | Implement server-side cap/lockout; keep probe opt-in until fixed. | ISSUE-002, TASK-SEC-001 |
| RISK-COVERAGE-DRIFT | Feature files, dashboard flow tags, and runner feature registration diverge. | High | High | Reconcile runner scope and glue; document non-executable specs. | ISSUE-003, TASK-RUN-001 |
| RISK-FLAKE-LOCATORS | Brittle selectors cause flaky UI automation. | Medium | High | Complete `data-testid` rollout and update page objects. | TASK-LOC-001 |
| RISK-SECRETS-DASH | Dashboard runtime secrets expand beyond what should live in the automation repo. | Medium | High | Follow ADR 0001 split triggers; isolate deployment secrets. | DEC-0001, TASK-DASH-001 |
| RISK-ENV-JAVA-MISMATCH | Local tooling may use Java 21 while project expects Java 25. | Observed in `hs_err_pid4140.log` | Medium | Align IDE runtime with project JDK or document supported IDE JDK matrix. | ISSUE-006, TASK-IDE-001 |
| RISK-DASH-MONOREPO | Dashboard and Java framework toolchains interfere with each other in monorepo. | Medium | Medium | Keep CI path filters; split repo when ADR 0001 triggers occur. | DEC-0001 |
| RISK-REPORTING-DRIFT | Report publishing expectations differ between docs and workflow. | Medium | Medium | Decide GitHub Pages vs artifact-only and update workflow/docs. | ISSUE-004, TASK-CI-002 |
| RISK-BUILD-BREAK | Compile-breaking placeholder stubs in refactored page objects blocked Maven compilation. | Resolved (2026-06-24) | High | Stubs removed across `HomePage`/`SidebarMenuComponent`/`AstoniaCardComponent`; `mvnw test-compile` → BUILD SUCCESS. Recurrence guard: keep `By.cssSelector("value")` / duplicate `varName` placeholders out of committed code. | ISSUE-008, TASK-CODE-001 |

## Risk Review Notes

- Probability values are qualitative and based only on current repository evidence.
- Items marked `Observed` have direct evidence in code/docs/logs.
- Unverified environment or ownership facts remain `TBD` rather than inferred.
