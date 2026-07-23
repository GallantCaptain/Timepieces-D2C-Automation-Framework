# Project Overview

Last reviewed: 2026-06-24

## Project Name

Timepieces QA Automation

## Purpose

This repository contains an end-to-end automation framework for the Timepieces web storefront and a dashboard for reviewing test execution history and launching predefined automation flows.

Verified source references:
- Root `README.md`
- `docs/project_context.md`
- `docs/PRD.md`
- `dashboard/README.md`

## Business Goals

| ID | Goal | Source | Status |
| --- | --- | --- | --- |
| REQ-FWK-001 | Validate critical Timepieces storefront flows through executable BDD tests. | `README.md`, `docs/project_context.md` | Current |
| REQ-FWK-002 | Use Cucumber feature files as living test documentation. | `docs/project_context.md` | Current |
| REQ-FWK-003 | Produce Allure reports and structured execution metrics. | `README.md`, `pom.xml`, `MetricsListener.java` | Current |
| REQ-DASH-001 | Provide a dashboard for historical trends, run history, and Allure report access. | `dashboard/README.md` | Current |
| REQ-DASH-002 | Trigger predefined test flows through GitHub Actions dispatch when configured. | `dashboard/README.md`, `dashboard/server/src/github.ts` | Current but gated by `GITHUB_TOKEN` |
| REQ-SEC-001 | Validate and document authentication security behavior, including OTP controls. | `OTP_Bruteforce_Security.feature`, `LoginPage.java` | Current; security issue open |

## Scope

- Java/Maven BDD UI automation for Timepieces storefront flows.
- Selenium WebDriver execution across supported desktop browsers.
- Page Object Model and Component Object Model test implementation.
- Cucumber/TestNG runners for smoke and regression execution.
- Allure report generation and Cucumber report outputs.
- Test metrics publishing to Postgres.
- Local Postgres via Docker Compose for metrics/dashboard development.
- React + Node/TypeScript dashboard for test history and flow dispatch.
- GitHub Actions workflow for CI/manual/repository-dispatch test execution.
- Documentation governance through this OKF and ADRs.

## Out of Scope

| Item | Status |
| --- | --- |
| Production customer analytics/admin console at `dashboard.timepieces-world.example` | Out of scope for this QA dashboard, per `dashboard/README.md` |
| Confirmed mobile device automation coverage | TBD; Appium dependency exists, but no verified mobile tests were found |
| API automation coverage | TBD; Rest-Assured dependency exists, but no verified API tests were found |
| Selenium Grid execution | **Wired and verified 2026-07-23 (STAT-075).** Self-hosted hub + chrome/firefox/edge nodes in `docker-compose.yml` behind the `grid` profile; `-Dexecution.mode=GRID` honours `-Dbrowser`. Verified 4/4 on all three browsers against production, with per-node session counts proving it was genuinely cross-browser. See `docs/selenium-grid.md`. |
| BrowserStack / LambdaTest CLOUD execution | Still a POC and knowingly left so: `execution.mode=CLOUD` is LambdaTest-shaped and **ignores `-Dbrowser`** (always Chrome); BrowserStack has config keys but no code. Use GRID for cross-browser. |
| Real Safari coverage on non-macOS hosts | Out of scope; current implementation substitutes a Chromium browser and records this in Allure |

## Traceability

| Requirement | Related Decision | Related Task | Implementation |
| --- | --- | --- | --- |
| REQ-FWK-001 | DEC-0002, DEC-0003, DEC-0004 | TASK-RUN-001 | IMPL-FWK-RUNNERS-001 |
| REQ-FWK-003 | DEC-0006 | TASK-CI-002 | IMPL-FWK-METRICS-001, IMPL-CI-001 |
| REQ-DASH-001 | DEC-0001, DEC-0006 | TASK-DASH-001 | IMPL-DASH-API-001, IMPL-DASH-WEB-001 |
| REQ-DASH-002 | DEC-0001 | TASK-DASH-001, TASK-DASH-002 | IMPL-DASH-API-001, IMPL-CI-001 |
| REQ-SEC-001 | DEC-0007 | TASK-SEC-001 | IMPL-FWK-RUNNERS-001 |
