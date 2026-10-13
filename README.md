# Timepieces D2C Automation Framework

End-to-end test automation for a direct-to-consumer (D2C) luxury watch storefront:
a **Java BDD framework** (Selenium 4 + Cucumber + TestNG) covering UI, API and security
testing, a **React + Node results dashboard**, and a **Playwright/TypeScript migration
proof-of-concept**.

> **Portfolio note.** This framework was built for a real e-commerce brand. For
> publication the brand has been anonymised as *Timepieces*: all hostnames point to
> reserved `.example` domains, credentials and contact details have been removed, and
> the CI workflows are **manual-only**. The suite will not run against a live site
> as-is; it is published to show framework design, structure and engineering practice.

---

## Highlights

- **BDD + TDD in one framework** — Cucumber `.feature` files for business flows,
  plain TestNG tests for API, security and content smoke checks.
- **Page Object Model with components** — a generic `BasePage<SELF>` (CRTP self-type)
  so fluent methods return the concrete page; reusable header/footer/sidebar components.
- **Thread-safe parallel execution** — `ThreadLocal` WebDriver and Cucumber
  PicoContainer context injection, safe for parallel TestNG runs.
- **Cross-browser** — Chrome, Firefox, Edge and Brave; local, headless, Docker or
  Selenium Grid.
- **Self-healing locators** — Healenium integration to recover from DOM changes, plus a
  `data-testid`-first locator strategy.
- **API testing** — REST Assured suites for authentication, search and idempotency,
  with OTP retrieval via Twilio.
- **Security testing** — OWASP Top 10 (2021 & 2025) scans driven through OWASP ZAP,
  with guardrails that keep production scans passive.
- **Reporting & metrics** — Allure reports (with screenshots and screen recordings),
  run metrics written to Postgres and visualised in Grafana and a custom dashboard.
- **CI/CD** — GitHub Actions pipelines for staging, production, exploratory and
  security suites, plus Dockerised execution with optional VNC for headed debugging.
- **Documented decisions** — 10 Architecture Decision Records (ADRs) in `docs/adr/`.

## Tech stack

| Area | Tools |
| --- | --- |
| Language / build | Java 25, Maven (wrapper included) |
| Test runners | TestNG, Cucumber 7 (Gherkin BDD) |
| UI automation | Selenium 4, Healenium |
| API testing | REST Assured, Twilio API (OTP) |
| Security | OWASP ZAP |
| Assertions / utils | AssertJ, Lombok, Apache POI (Excel test data), Log4j 2 |
| Reporting | Allure (TestNG + Cucumber), Monte screen recorder |
| Metrics | PostgreSQL, Grafana |
| Dashboard | React 18 + Vite, Node/Express + TypeScript, JWT auth |
| Migration PoC | Playwright, TypeScript, Cucumber.js |
| Infra / CI | Docker, Docker Compose, Selenium Grid, GitHub Actions |

## Repository layout

```
.
├── src/
│   ├── main/java/com/timepieces/automation/
│   │   ├── base/            # DriverFactory, driver lifecycle
│   │   ├── pages/           # Page Objects + reusable components
│   │   ├── exceptions/
│   │   └── utils/           # ConfigReader, waits, helpers
│   └── test/
│       ├── java/com/timepieces/automation/
│       │   ├── bdd/         # Cucumber step definitions, hooks, runners
│       │   ├── tdd/         # api/ · security/ · ui/ TestNG suites
│       │   ├── context/     # shared scenario context (PicoContainer)
│       │   ├── listeners/   # TestNG / Allure listeners
│       │   └── tools/       # locator scraping utilities
│       └── resources/
│           ├── features/    # Gherkin features grouped by business domain
│           └── config/      # per-environment properties
├── automation-ts/           # Playwright + TypeScript migration proof-of-concept
├── dashboard/               # React + Node test results dashboard
├── docker/                  # test-bed Dockerfile and entrypoint
├── grafana-dashboards/      # exported Grafana dashboards
├── docs/                    # PRD, architecture, test strategy, ADRs
├── scripts/                 # helper scripts (reports, test-case sync, Docker runs)
├── .github/workflows/       # CI pipelines (manual-only in this copy)
└── testng*.xml              # suite definitions
```

Features cover the full shopping journey: authentication, catalogue and product
discovery, cart, checkout, payment integration, wishlist/retention and content pages.

## Getting started

**Prerequisites:** JDK 25 and a supported browser.

```bash
git clone https://github.com/GallantCaptain/Timepieces-D2C-Automation-Framework.git
cd Timepieces-D2C-Automation-Framework

# Compile and resolve dependencies
./mvnw -q test-compile
```

Environment settings live in `src/test/resources/config/<env>.properties`. Secrets
(passwords, tokens) are read from git-ignored `*.local.properties` files or environment
variables; see `.env.example`.

### Running tests

```bash
# Default suite
./mvnw test

# Parameterised run
./mvnw test -Denv=staging -Dbrowser=firefox -Dheadless=true "-Dcucumber.filter.tags=@Smoke"

# A specific TestNG suite
./mvnw test -Dsurefire.suite=testng-crossbrowser.xml
```

Common tags: `@Smoke`, `@Regression`, `@Authentication`, `@Checkout`, `@Homepage`.

### Docker

```bash
docker compose up -d postgres-dev     # local metrics database
# The test-bed image runs docker/entrypoint.sh across browsers.
# Set DEBUG_VNC=true to run headed and attach a VNC viewer on :5900.
```

### Reports

```bash
./mvnw allure:report     # generates target/site/allure-maven-plugin/
```

## Dashboard

`dashboard/` is a full-stack TypeScript app (React + Express + Postgres) with
email/password auth (bcrypt + JWT), historical pass/fail trends from the `test_runs`
table, and on-demand test runs via GitHub Actions dispatch. See
[dashboard/README.md](dashboard/README.md).

## Playwright / TypeScript proof-of-concept

`automation-ts/` ports one vertical slice of the framework — page objects, a BDD
feature and an API test — to Playwright + TypeScript, to evaluate a migration path
away from Selenium/Java. See [automation-ts/README.md](automation-ts/README.md).

## Documentation

- [docs/adr/](docs/adr/) — architecture decision records
- [docs/system_architecture.md](docs/system_architecture.md) — architecture overview
- [docs/test-strategy/](docs/test-strategy/) — test strategy
- [docs/selenium-grid.md](docs/selenium-grid.md) — Selenium Grid setup
- [docs/security-zap.md](docs/security-zap.md) — OWASP ZAP security scanning
- [docs/okf/](docs/okf/) — project knowledge base (status, decisions, issues, learnings)

## Author

**Avinash Rath** — SDET · [GitHub @GallantCaptain](https://github.com/GallantCaptain)
