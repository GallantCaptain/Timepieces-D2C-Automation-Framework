# Project Structure

Last reviewed: 2026-07-08

## Directory Layout

```text
timepieces-automation-framework/
|-- .github/workflows/              # GitHub Actions workflow
|-- dashboard/                      # React + Node dashboard
|   |-- server/                     # Express/TypeScript API
|   `-- web/                        # React/Vite SPA
|-- docker/                         # Docker test image and run scripts
|-- docs/                           # Project docs, ADRs, proposals, OKF
|   |-- adr/                        # Architecture Decision Records
|   |-- okf/                        # Organizational Knowledge File
|   `-- test-strategy/              # Focused testing strategy docs
|-- grafana-dashboards/             # Exported Grafana dashboard JSON
|-- src/main/java/...               # Framework page objects, utilities, driver factory
|-- src/test/java/...               # Hooks, runners, listeners, step definitions, TestNG tests, test context
|-- src/test/resources/config/      # Environment properties
|-- src/test/resources/features/    # Cucumber feature files
|-- src/test/resources/lang/        # Language/resource properties
|-- src/test/resources/testdata/    # Structured data for TestNG/API tests
|-- target/                         # Generated Maven/test artifacts
|-- logs/                           # Generated runtime logs
|-- pom.xml                         # Maven source of truth
|-- testng.xml                      # TestNG suite source of truth
`-- README.md                       # Repository navigation hub
```

## Important Files

| File | Purpose |
| --- | --- |
| `README.md` | Navigation hub and quick start |
| `pom.xml` | Java dependencies, plugin versions, compiler release |
| `testng.xml` | Suite listeners, runners, default environment |
| `src/main/java/com/timepieces/automation/base/DriverFactory.java` | WebDriver lifecycle and browser support |
| `src/main/java/com/timepieces/automation/utils/ConfigReader.java` | Configuration resolution |
| `src/main/java/com/timepieces/automation/pages/BasePage.java` | Shared page-object synchronization, element wrappers, dynamic locator helper, and viewport helper |
| `src/main/java/com/timepieces/automation/pages/components/FooterComponent.java` | Global footer component behavior for mobile and desktop layouts |
| `src/test/java/com/timepieces/automation/hooks/Hooks.java` | Cucumber scenario setup/teardown |
| `src/test/java/com/timepieces/automation/stepdefinitions/FooterSteps.java` | Cucumber glue for global footer visibility/link/navigation validation |
| `src/test/java/com/timepieces/automation/api/SearchApiTest.java` | TestNG/Rest-Assured storefront search keyword validation |
| `src/test/java/com/timepieces/automation/listeners/MetricsListener.java` | Metrics listener |
| `src/test/java/com/timepieces/automation/utils/TestMetricsPublisher.java` | Postgres metrics publisher |
| `src/test/resources/testdata/search_keywords.json` | Structured keyword matrix for storefront search API tests; derived from `Timepieces_Search_Keywords.xlsx` |
| `testng-search-api.xml` | Committed convenience suite isolating `SearchApiTest` (deterministic search API suite) for repeated/standalone runs |
| `scripts/run-search-api-analysis.ps1` | Runs the deterministic + exploratory search API suites a configurable number of times each and compiles a combined per-keyword pass/fail analysis |
| `src/test/resources/testdata/Timepieces_Search_Keywords.xlsx` | **Source-of-truth** keyword spreadsheet (moved from the repo root 2026-07-23 to sit beside the JSON derived from it). Maintained monthly by the business; regenerate `search_keywords.json` from it rather than editing that JSON by hand. |
| `output/cheatsheet/Timepieces_Test_Execution_Cheatsheet.pdf` / `.html` | Printable + searchable command reference. **Generated** by `scripts/build_cheatsheet.py` — edit the HTML in that script and re-run; do not hand-edit either output. Lives under `output/` (all generated pdf/html) not `docs/` (all `.md` source) since the 2026-07-23 layout split. |
| `src/test/resources/config/staging.properties` | Staging configuration |
| `src/test/resources/config/production.properties` | Production configuration |
| `src/test/resources/healenium.properties` | Healenium self-healing engine config (backend URL, recovery tries, score cap); read directly by `SelfHealingDriver`, not via `ConfigReader` |
| `.github/workflows/test-pipeline.yml` | CI/manual/repository-dispatch workflow |
| `docker-compose.yml` | Local Postgres metrics database |
| `docker/Dockerfile` | Dockerized Java/browser test bed |
| `dashboard/server/src/db/schema.sql` | Dashboard/database schema |
| `dashboard/server/src/flows.config.ts` | Dashboard flow-to-tag mapping |
| `dashboard/web/vite.config.ts` | Dashboard web dev server/proxy config |
| `docs/adr/` | Decision records |
| `docs/okf/OKF_INDEX.md` | OKF index |

## Configuration Locations

| Config | Location | Notes |
| --- | --- | --- |
| Java dependency versions | `pom.xml` | Maven source of truth |
| TestNG suite | `testng.xml` | Runs smoke and regression runners |
| Framework env config | `src/test/resources/config/*.properties` | `ConfigReader` loads selected env and local override |
| Logging | `src/test/resources/log4j2.xml` | Console, application, and error rolling logs |
| Dashboard API env template | `dashboard/server/.env.example` | Real `.env` must not be committed |
| Dashboard API TS config | `dashboard/server/tsconfig.json` | TypeScript compiler config |
| Dashboard web TS/Vite config | `dashboard/web/tsconfig.json`, `dashboard/web/vite.config.ts` | TypeScript and dev proxy |
| Local DB | `docker-compose.yml` | Postgres 16 Alpine |
| Healenium backend (opt-in) | `docker-compose.yml` (`healenium-postgres`, `hlm-backend`) | Postgres 13 + `healenium/hlm-backend`; only needed when `healenium.enabled=true` |
| CI | `.github/workflows/test-pipeline.yml` | Workflow inputs and secrets |

## Generated Artifacts

| Path | Generated By | Commit Policy |
| --- | --- | --- |
| `target/allure-results` | Maven/Cucumber/Allure | Do not commit unless explicitly required |
| `target/cucumber-reports` | Cucumber plugins | Do not commit |
| `target/screen-recordings` | `ScreenRecorderUtil` | Do not commit |
| `target/site/allure-maven-plugin` | `mvn allure:report` | Do not commit |
| `target/search_keyword_api_report.md` | `SearchApiTest` | Generated verification report; commit only when explicitly requested as evidence |
| `.allure` | Allure tooling | Do not commit unless explicitly required |
| `logs/` | Log4j2 | Do not commit runtime output |
| `output/pdf/timepieces-framework-design-guide.pdf` | Documentation generation | Generated/exported artifact; commit policy TBD unless explicitly requested |
| `output/pdf/healenium-implementation-guide.pdf` | Documentation generation | Generated/exported Healenium onboarding guide; commit policy TBD unless explicitly requested |
| `output/pdf/docker-kubernetes-guide.pdf` | Documentation generation | Generated/exported Docker (this repo's actual usage) + Kubernetes (generic foundations primer, not implemented here) onboarding guide for team transition; commit policy TBD unless explicitly requested |
| `output/pdf/cicd-pipelines-guide.pdf` | Documentation generation | Generated/exported guide: this repo's actual GitHub Actions pipelines (5 workflows) plus illustrative Jenkins/GitLab CI/CD/Azure DevOps equivalents for comparison — only GitHub Actions is implemented; commit policy TBD unless explicitly requested |
| `output/pdf/Automated Search API Test Results.pdf` | Documentation generation | Generated/exported search relevance test report (2 deterministic + 10 exploratory runs); commit policy TBD unless explicitly requested |
| `output/search-api-analysis/` | `scripts/run-search-api-analysis.ps1` | Per-run logs/reports and compiled summary/CSV; gitignored, fully regenerable |
| `output/cheatsheet/` | `scripts/build_cheatsheet.py` | Generated test-execution cheatsheet (pdf + html); tracked |
| `output/proposals/` | Documentation generation | QA infrastructure proposal (pdf + html); moved from `docs/proposals/` in the 2026-07-23 layout split (generated pdf/html live under `output/`, `.md` source under `docs/`) |
| `dashboard/web/dist/` | Vite build | Generated artifact |
| `dashboard/**/node_modules/` | npm install | Do not commit |

## Traceability

| Area | Related Decision | Related Task |
| --- | --- | --- |
| OKF structure | DEC-0009 | TASK-OKF-001 |
| Framework suite structure | DEC-0002, DEC-0004 | TASK-RUN-001 |
| Dashboard structure | DEC-0001 | TASK-DASH-001 |
