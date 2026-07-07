# System Architecture

Last reviewed: 2026-07-07

## High-Level Architecture

The repository contains two main systems:

1. Java BDD automation framework at the repository root and under `src/`.
2. React + Node/TypeScript test dashboard under `dashboard/`.

Execution flow:

1. TestNG runs Cucumber runners from `testng.xml`.
2. Cucumber scenarios use Selenium WebDriver via `DriverFactory`.
3. Page objects and components execute browser interactions.
4. Hooks attach screenshots and optional videos to Cucumber/Allure reports.
5. Metrics listener publishes run summaries to Postgres.
6. Dashboard reads Postgres metrics and can dispatch GitHub Actions workflows when `GITHUB_TOKEN` is configured.

## Components

| ID | Component | Responsibility | Key Files |
| --- | --- | --- | --- |
| IMPL-FWK-DRIVER-001 | Driver factory | ThreadLocal WebDriver lifecycle, browser setup, Cloudflare header injection, Allure environment properties | `src/main/java/com/timepieces/automation/base/DriverFactory.java` |
| IMPL-FWK-PAGES-001 | Page and component objects | Encapsulate storefront pages and reusable UI components | `src/main/java/com/timepieces/automation/pages/` |
| IMPL-FWK-CONFIG-001 | Configuration reader | Resolve config from JVM properties, environment variables, and properties files | `ConfigReader.java`, `src/test/resources/config/` |
| IMPL-FWK-HOOKS-001 | Cucumber hooks | Start/stop drivers, attach failure screenshots/videos | `Hooks.java` |
| IMPL-FWK-RUNNERS-001 | Cucumber/TestNG runners | Smoke and regression orchestration with reporting plugins | `testng.xml`, `SmokeRunner.java`, `RegressionRunner.java`, `BaseRunner.java` |
| IMPL-FWK-METRICS-001 | Metrics publishing | Publish suite totals to Postgres `test_runs` | `MetricsListener.java`, `TestMetricsPublisher.java` |
| IMPL-DASH-API-001 | Dashboard API | Auth, metrics endpoints, flow dispatch, artifact download | `dashboard/server/src/` |
| IMPL-DASH-WEB-001 | Dashboard SPA | React/Vite user interface for dashboard pages | `dashboard/web/src/` |
| IMPL-CI-001 | CI workflow | GitHub Actions test execution, Allure generation, artifact upload | `.github/workflows/test-pipeline.yml` |
| IMPL-LOCAL-DB-001 | Local metrics database | Dockerized Postgres for local metrics/dashboard work | `docker-compose.yml` |
| IMPL-FWK-HEAL-001 | Healenium self-healing locators | Wraps the WebDriver built by `DriverFactory` in `SelfHealingDriver` when `healenium.enabled=true`; healed-locator storage backend runs as opt-in Docker services | `DriverFactory.java`, `healenium.properties`, `docker-compose.yml` (`healenium-postgres`, `hlm-backend`) |

## Dependencies

### Java Framework

| Dependency | Version | Source |
| --- | --- | --- |
| Java compiler release | 25 | `pom.xml` |
| Selenium | 4.44.0 | `pom.xml` |
| Cucumber JVM | 7.18.0 | `pom.xml` |
| TestNG | 7.10.2 | `pom.xml` |
| Rest-Assured | 5.4.0 | `pom.xml` |
| Appium Java client | 9.2.3 | `pom.xml` |
| Healenium web | 3.4.6 | `pom.xml` |
| Allure Java adapters | 2.29.1 | `pom.xml` |
| Allure CLI/report version | 2.27.0 | `pom.xml` |
| Log4j2 | 2.23.1 | `pom.xml` |
| Lombok | 1.18.42 | `pom.xml` |
| PostgreSQL JDBC | 42.7.3 | `pom.xml` |
| Monte screen recorder | 0.7.7.0 | `pom.xml` |
| AssertJ | 3.26.0 | `pom.xml` |

### Dashboard

| Area | Dependency | Version | Source |
| --- | --- | --- | --- |
| Server | Node | 20+ required, developed on 26 | `dashboard/README.md` |
| Server | TypeScript | 5.7.3 | `dashboard/server/package.json` |
| Server | Express | 4.21.2 | `dashboard/server/package.json` |
| Server | pg | 8.13.1 | `dashboard/server/package.json` |
| Server | bcryptjs | 2.4.3 | `dashboard/server/package.json` |
| Server | jsonwebtoken | 9.0.2 | `dashboard/server/package.json` |
| Server | zod | 3.24.1 | `dashboard/server/package.json` |
| Web | React | 18.3.1 | `dashboard/web/package.json` |
| Web | Vite | 6.0.7 | `dashboard/web/package.json` |
| Web | React Router | 6.28.0 | `dashboard/web/package.json` |

### Infrastructure

| Component | Version/Target | Source |
| --- | --- | --- |
| Local Postgres | `postgres:16-alpine` | `docker-compose.yml` |
| Docker test image | `eclipse-temurin:25-jdk-noble`, Maven 3.9.9 | `docker/Dockerfile` |
| Docker browsers | Chrome, Firefox, Edge, Brave | `docker/Dockerfile` |
| CI runner | `ubuntu-latest` | `.github/workflows/test-pipeline.yml` |

## Integrations

| Integration | Purpose | Status |
| --- | --- | --- |
| GitHub Actions | CI execution and dashboard-triggered workflow dispatch | Current |
| GitHub Actions artifacts | Allure report artifact upload | Current |
| GitHub Pages | README states reports are published to GitHub Pages; workflow currently lacks a deploy step in the inspected file | Drift open |
| Postgres / Neon | Metrics storage through `NEON_DATABASE_URL` or `DATABASE_URL` | Current |
| Grafana | Uses exported dashboard JSON in `grafana-dashboards/` | Current artifact present |
| Cloudflare | Staging challenge bypass header `X-Automation-Bypass` for Chromium drivers | Implementation present; external WAF rule status TBD |
| Twilio | Optional real OTP read-back for `@OtpE2E` | Code present; runtime configuration status TBD |
| Healenium (`hlm-backend`) | Self-healing locator lookup/storage for `SelfHealingDriver`, wrapped around the `DriverFactory` WebDriver | Implemented and config-gated via `healenium.enabled` (default `false`); opt-in Docker services |

## Environment Details

| Environment | Details | Status |
| --- | --- | --- |
| Local framework | JDK 25 expected; Maven wrapper available | Current |
| Local dashboard | API on `localhost:4000`, web on `localhost:5173`, local Postgres on `localhost:5432` | Current |
| Staging | `https://staging.timepieces-d2c.example`; default framework env | Current but CI blocked by Cloudflare challenge |
| Production | `https://www.timepieces-d2c.example`; login path marked placeholder in `production.properties` | TBD for production runs |
| Dashboard deployment | `dev.timepieces-world.example` documented as handled by dev team | TBD deployment status |
| CI | GitHub-hosted Ubuntu; Java 25; installs Chrome and Firefox; browser input also allows Edge and Brave | Drift open for Edge/Brave install coverage |

## Traceability

| Architecture Area | Decision | Tasks | Issues/Risks |
| --- | --- | --- | --- |
| Dashboard repo location | DEC-0001 | TASK-DASH-001 | RISK-DASH-MONOREPO |
| Java BDD stack | DEC-0002 | TASK-RUN-001 | RISK-COVERAGE-DRIFT |
| Page/component model | DEC-0003 | TASK-LOC-001 | RISK-FLAKE-LOCATORS |
| Parallel driver lifecycle | DEC-0004 | TASK-RUN-001 | RISK-COVERAGE-DRIFT |
| Config precedence | DEC-0005 | TASK-CFG-001 | ISSUE-005 |
| Reporting/metrics | DEC-0006 | TASK-CI-002 | ISSUE-004 |
| Cloudflare bypass header | DEC-0007 | TASK-CI-001 | ISSUE-001, RISK-CI-CF |
| Safari substitution | DEC-0008 | TASK-DOC-001 | RISK-COVERAGE-DRIFT |
