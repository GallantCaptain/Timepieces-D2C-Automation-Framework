# ADR 0006 - Reporting and metrics stack

- **Status:** Accepted
- **Date:** 2026-06-24
- **Record note:** This ADR documents an existing implementation. The original decision date is TBD.
- **Owner:** TBD
- **Context tags:** reporting, allure, metrics, postgres, dashboard

## Context

The framework produces Allure and Cucumber runner outputs. `MetricsListener` aggregates TestNG suite results and `TestMetricsPublisher` writes summaries to a Postgres `test_runs` table. The dashboard reads the same table and displays history/trends. Grafana dashboard JSON is present under `grafana-dashboards/`.

Verified sources:

- `pom.xml`
- `SmokeRunner.java`
- `RegressionRunner.java`
- `MetricsListener.java`
- `TestMetricsPublisher.java`
- `dashboard/server/src/db/schema.sql`
- `dashboard/README.md`
- `grafana-dashboards/test-trends.json`

## Alternatives Considered

Original alternatives considered are TBD. Common alternatives for future review include report-only storage, test management system integration, cloud metrics services, or direct GitHub artifact-only reporting, but these are not recorded as historical facts.

## Decision

Use Allure/Cucumber report outputs for detailed execution evidence and Postgres `test_runs` metrics for trend/history dashboards.

## Reason Chosen

Allure provides rich scenario-level reports, while Postgres gives the dashboard and Grafana a stable, queryable metrics contract.

## Consequences

- `test_runs` schema is a contract between Java publisher, dashboard API, and Grafana.
- Database connectivity must be available for metrics publishing; publisher falls back to local Docker Postgres if no env URL is set.
- Documentation and workflow must stay aligned on whether reports are artifact-only or published to GitHub Pages.

## Traceability

- Requirement: REQ-FWK-003, REQ-DASH-001
- Task: TASK-CI-002
- Implementation: `MetricsListener.java`, `TestMetricsPublisher.java`, `dashboard/server/src/db/schema.sql`
- Related OKF file: `docs/okf/system-architecture.md`
