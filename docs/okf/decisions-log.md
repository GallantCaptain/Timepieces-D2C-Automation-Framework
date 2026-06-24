# Decisions Log

Last reviewed: 2026-06-24

Architecture and framework decisions are stored as ADRs under `docs/adr/`. This file is the OKF index for decision traceability.

## Active Decisions

| Decision ID | ADR | Status | Decision | Owner | Traceability |
| --- | --- | --- | --- | --- | --- |
| DEC-0001 | [../adr/0001-test-dashboard-repo-location.md](../adr/0001-test-dashboard-repo-location.md) | Accepted | Keep dashboard in monorepo for now; extract when deployment/secrets/team size create the need. | QA Automation + Dev team | REQ-DASH-001, TASK-DASH-001 |
| DEC-0002 | [../adr/0002-java-bdd-automation-stack.md](../adr/0002-java-bdd-automation-stack.md) | Accepted | Use Java 25, Maven, Selenium, Cucumber, TestNG, AssertJ, Allure for the automation framework. | TBD | REQ-FWK-001 |
| DEC-0003 | [../adr/0003-page-object-component-architecture.md](../adr/0003-page-object-component-architecture.md) | Accepted | Use Page Object Model plus Component Object Model with assertions in step definitions. | TBD | REQ-FWK-001, TASK-LOC-001 |
| DEC-0004 | [../adr/0004-threadlocal-parallel-cucumber-testng.md](../adr/0004-threadlocal-parallel-cucumber-testng.md) | Accepted | Use TestNG+Cucumber with ThreadLocal WebDriver and scenario-level data provider parallelism. | TBD | REQ-FWK-001, TASK-RUN-001 |
| DEC-0005 | [../adr/0005-configuration-resolution-precedence.md](../adr/0005-configuration-resolution-precedence.md) | Accepted | Resolve configuration from JVM property, then environment variable, then env properties file with optional local override. | TBD | TASK-CFG-001 |
| DEC-0006 | [../adr/0006-reporting-and-metrics-stack.md](../adr/0006-reporting-and-metrics-stack.md) | Accepted | Use Allure/Cucumber reports plus Postgres metrics consumed by Grafana/dashboard. | TBD | REQ-FWK-003, REQ-DASH-001 |
| DEC-0007 | [../adr/0007-cloudflare-automation-bypass-header.md](../adr/0007-cloudflare-automation-bypass-header.md) | Accepted | Apply an automation bypass header for Chromium sessions when configured. | TBD | TASK-CI-001 |
| DEC-0008 | [../adr/0008-safari-substitution-non-macos.md](../adr/0008-safari-substitution-non-macos.md) | Accepted | Use real Safari only on macOS; substitute configured Chromium fallback elsewhere and annotate reports. | TBD | REQ-FWK-002 |
| DEC-0009 | [../adr/0009-okf-governance.md](../adr/0009-okf-governance.md) | Accepted | Use OKF as authoritative project memory with topic files, ADRs, append-only changelog, and README navigation. | Project orchestrator | TASK-OKF-001 |

## Superseded or Rejected Decisions

| Decision | Status | Replacement/Reason |
| --- | --- | --- |
| Long-lived dashboard feature branch | Rejected | Rejected by ADR 0001 because it drifts from `main`, does not integrate, and can rot. |
| Immediate dashboard dedicated repository | Not chosen | ADR 0001 keeps dashboard in monorepo until deployment/secrets/team-size triggers appear. |

## Open Decision Gaps

| Gap | Status | Task |
| --- | --- | --- |
| Branching strategy beyond PRs to `main` | TBD | TASK-DOC-001 |
| Commit standards | TBD | TASK-DOC-001 |
| Manual testing standard and release signoff | TBD | TASK-DOC-001 |
| BrowserStack/Grid execution strategy | TBD | TASK-DOC-001 |
| Mobile/Appium and API/Rest-Assured scope | TBD | TASK-DOC-001 |
