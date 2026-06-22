# Timepieces QA Automation

End-to-end test automation for the Timepieces web application, plus a dashboard for
viewing results and launching test runs.

The repo has two parts:

1. **Test framework** (`src/`, root) — a Java BDD suite: Selenium + Cucumber +
   TestNG, structured as a Page Object Model, run across multiple browsers, with
   Allure reporting and run metrics published to Postgres.
2. **Dashboard** (`dashboard/`) — a React + Node app to view historical
   trends/history and trigger predefined flows on demand. See
   [dashboard/README.md](dashboard/README.md).

---

## Tech stack

| Area | Choice |
| --- | --- |
| Language / build | Java 25, Maven (`./mvnw`) |
| Test runner | TestNG + Cucumber (BDD, `.feature` files) |
| Browser automation | Selenium 4 (Chrome, Firefox, Edge, Brave) |
| Assertions | AssertJ |
| Reporting | Allure (published to GitHub Pages with history) |
| Metrics | Postgres `test_runs` table (local Docker or Neon) |
| Dashboards | Grafana (`grafana-dashboards/`) + the React dashboard (`dashboard/`) |
| CI | GitHub Actions (`.github/workflows/test-pipeline.yml`) |

## Repository layout

```
.
├── src/                         # Java test framework (POM, step defs, features)
│   ├── main/java/…/pages/       # Page Objects (BasePage + components)
│   └── test/                    # step definitions, hooks, listeners, runners
│       └── resources/features/  # Cucumber features, grouped by domain
├── docker/                      # container entrypoint for the test bed
├── docker-compose.yml           # local Postgres (timepieces_metrics) for metrics
├── grafana-dashboards/          # exported Grafana dashboard JSON
├── dashboard/                   # React + Node test dashboard (see its README)
├── docs/                        # PRD, architecture, test strategy, context
├── .github/workflows/           # CI: test runner & metrics publisher
├── pom.xml • testng.xml         # build + suite config
```

## Running the tests

Prerequisites: JDK 25 and a supported browser. Use the Maven wrapper (`./mvnw`).

```bash
# Default smoke run (staging, chrome, headless)
./mvnw test

# Parameterized
./mvnw test \
  -Denv=staging \           # dev | staging | prod
  -Dbrowser=chrome \        # chrome | firefox | edge | brave
  -Dheadless=true \
  "-Dcucumber.filter.tags=@Smoke"
```

Common tags: `@Smoke`, `@Regression`, `@Authentication`, `@Login`, `@Checkout`,
`@Homepage`, `@Footer`. Features are grouped under
`src/test/resources/features/` by domain (authentication, catalog, checkout, …).

### Docker

```bash
docker compose up -d postgres-dev      # local metrics DB (timepieces_metrics)
# The test bed image runs docker/entrypoint.sh, iterating browsers; set
# DEBUG_VNC=true to run headed and attach a VNC viewer on :5900.
```

## Reporting & metrics

- **Allure** — `./mvnw allure:report` (CI publishes per-run reports to GitHub
  Pages with retained history).
- **Metrics** — each suite publishes totals/pass/fail/duration to the Postgres
  `test_runs` table (`NEON_DATABASE_URL` / `DATABASE_URL`, falling back to local
  Docker). Visualized in Grafana and in the dashboard.

## Continuous integration

`.github/workflows/test-pipeline.yml` runs the suite and publishes metrics +
Allure. Triggers:

- `pull_request` → `main` (scoped to test-relevant paths)
- `workflow_dispatch` — manual, with `env` / `browser` / `tags` inputs
- `repository_dispatch` (`run-smoke-tests`) — used by the dashboard to launch flows

## Dashboard

`dashboard/` is a React + Node (TypeScript) app: email/password auth (bcrypt +
JWT), historical trends/history from `test_runs`, and on-demand flow execution by
dispatching the CI workflow. Setup and API details are in
[dashboard/README.md](dashboard/README.md).

## Documentation

- [docs/PRD.md](docs/PRD.md) — product requirements
- [docs/system_architecture.md](docs/system_architecture.md) — architecture
- [docs/test-strategy/](docs/test-strategy/) — test strategy
- [docs/project_context.md](docs/project_context.md) — project context

---

## Roadmap / upcoming changes

- **`data-testid` locator rollout (in progress, target ~2026-06-21).** Replacing
  brittle exact-Tailwind-class CSS selectors with stable `data-testid` locators
  across the page objects (Header, Footer, SidebarMenu, Login [to refactor],
  Cart, OrderHistory, AstoniaCard; OrderDetails + SupportSheetModal already done)
  to remove flaky failures.
- **Dashboard rollout.** Hosting at `dev.timepieces-world.example` (handled by the dev team) —
  strictly test analytics + Allure reports, a **separate** system from the existing
  production analytics/admin console at `dashboard.timepieces-world.example` (future
  cross-navigation between the two). On-demand flow execution activates once a
  `GITHUB_TOKEN` (scope `actions:write`) is configured on the server.
- **CI staging access (known blocker).** The smoke suite currently fails *in CI*
  because `staging.timepieces-d2c.example` serves a Cloudflare "verify you are human"
  challenge to the GitHub-hosted runner (datacenter IP + headless Chrome) — the
  app is never reached, so this is **not** a test-code issue. Planned fixes under
  evaluation: a Cloudflare WAF skip rule keyed on an automation header, pointing
  CI at the `dev` environment (which has the login bypass), or a self-hosted
  runner with an allowlisted IP.