# Timepieces Automation Framework: Project Context

Last updated: 2026-07-08

> Governance note: the authoritative, living project memory is the OKF under
> `docs/okf/` (start at `docs/okf/OKF_INDEX.md`). This file is a fast-rehydration
> overview; where it and the OKF differ, the OKF `CurrentStatus.md` wins.

## Purpose

This repository contains the Timepieces web automation program: a Java/Maven
BDD + TDD test suite validating the Timepieces headless-Shopify storefront
(authentication/OTP, catalog & product discovery, cart, checkout, payments,
retention/wishlist, account/session), plus a React/Node QA dashboard for run
history, trends, and on-demand flow execution.

Active goal (final push before handoff): reach **250+ automated test cases** and
a **completed Page Object Model**, working directly on `main`.

## Stack

- Language/runtime: Java, Maven, compiler release 25
- UI automation: Selenium WebDriver 4.44.0 (migration to Playwright + Python is on the roadmap; a validated Playwright/TypeScript PoC lives under `automation-ts/`)
- BDD: Cucumber-JVM 7.18.0 · Runner: TestNG 7.10.2 · Assertions: AssertJ
- API/TDD: Rest-Assured 5.4.0 (active — search, idempotency, auth-engine)
- Reporting: Allure (`allure-testng`, `allure-cucumber7-jvm`); Cucumber HTML/JSON
- Metrics: PostgreSQL (`test_runs`) via `MetricsListener` → Grafana + dashboard
- Logging: Log4j2 · DI/scenario state: Cucumber PicoContainer
- Self-healing/mobile (present, not primary): Healenium 3.4.6, Appium 9.2.3
- Screen recording: Monte Screen Recorder

## Test Layers & Suites (BDD/TDD segregation)

Test code is split into two package roots:

- `com.timepieces.automation.bdd.*` — Cucumber runners (`SmokeRunner`,
  `RegressionRunner`, `ProductionRunner`), step definitions, and hooks.
- `com.timepieces.automation.tdd.api.*` — pure TestNG/Rest-Assured tests
  (`SearchApiTest` — 100 deterministic search tests across 6 groups;
  `SearchApiExploratoryTest` — 50 randomized nightly tests; `IdempotencyApiTest`;
  `AuthenticationEngine`; `TwilioServiceTest`).

Suite XMLs:

- `testng.xml` — default combined suite (CI staging, test-pipeline, Docker).
- `testng-bdd.xml` / `testng-tdd.xml` — local-only convenience suites to run BDD
  or TDD scopes independently.
- `testng-production.xml` — production-scoped suite (`-Dsurefire.suite=...`).
- `testng-exploratory.xml` — nightly exploratory search suite.

Maven profiles `SmokeSuite` and `RegressionSuite` map runner groups and Cucumber
tags together. `SanityRunner` has been removed (no `@Sanity` scenarios).

## Execution Model

```powershell
mvn test                                             # default testng.xml
mvn test "-Dsurefire.suite=testng-tdd.xml"           # TDD/API only
mvn test "-Dsurefire.suite=testng-bdd.xml"           # BDD only
mvn test -PSmokeSuite                                 # smoke profile
mvn test -Denv=staging -Dbrowser=chrome -Dheadless=true "-Dcucumber.filter.tags=@Smoke"
mvn allure:serve
```

Surefire delegates discovery to the suite XML because runner classes do not
follow default `Test*.java` naming. Allure results go to `target/allure-results`.
`BaseRunner` overrides Cucumber's TestNG data provider with `parallel = true`
(scenario-level parallelism); browser isolation relies on `DriverFactory`'s
`ThreadLocal<WebDriver>`.

## Page Object Model (fluent, CRTP generics)

Core inheritance, now parameterized with a recursive self-type so inherited
fluent methods (e.g. `open()`) return the concrete page type with no per-page
override:

```text
BasePage<SELF extends BasePage<SELF>>
  -> HeaderOnlyBasePage<SELF>            (adds HeaderComponent)
       -> StoreFrontBasePage<SELF>      (adds FooterComponent)
```

- Concrete pages bind themselves: `class UpdatesPage extends StoreFrontBasePage<UpdatesPage>`.
- Product families use abstract `SharedTemplate_*` intermediates (timepieces:
  Monarch, Arvion, Astonia, AstoniaSports, Auriqua, Manifesta; accessories:
  FKMRubber/Hybrid/Ostrich/Suede/TejuLizard straps, Butterfly/SingleDeployment
  clasps, WatchCase) — a variant PDP is a ~10-line class.
- `getPagePath()`/`getPageTitle()` are non-abstract (default `""`); pages with a
  real endpoint override them. `open()` navigates to `baseUrl + pagePath`.
- ~101 page/component objects total. Assertions live in step definitions, not
  page objects; shared header/footer behavior lives in components.
- Drops pages (`upcomingDrops/`) intentionally stay on `StoreFrontBasePage`
  directly (each drop is unique/frequently changing — no shared template).

See `docs/system_architecture.md` for the full coding standard.

## Configuration

`ConfigReader` loads `config/<env>.properties`, then optionally overlays
`config/<env>.local.properties`. Lookup precedence: (1) JVM system property,
(2) OS environment variable, (3) properties file. Common keys: `env`
(default `staging`), `browser`, `headless`, `chrome.no.sandbox`,
`explicit.wait.timeout`, `recordVideo`, `max.retry.count`, `base.url`,
`staging.password`, `CF_BYPASS_TOKEN`.

Known drift (tracked in OKF): `staging.properties` uses `is.headless` while
`DriverFactory` reads `headless` — prefer `-Dheadless=...` until reconciled.

## CI/CD, Cloudflare & Security

- GitHub Actions workflows: `test-pipeline.yml` (PR/manual/dispatch),
  `staging-tests.yml`, `production-tests.yml`, `exploratory-search-tests.yml`.
- **Cloudflare (open blocker):** GitHub-hosted runners are challenged by
  Cloudflare on `staging.timepieces-d2c.example`. Browser-side bypass (CDP header via
  `Network.enable` + `X-Automation-Bypass`) is implemented; the Cloudflare-side
  WAF-skip rule is owned by infra. CI is not reliably green until that lands.
  Do not treat "compiles/passes locally" as "CI green."
- **OTP security (open):** an automated probe documents a missing OTP
  attempt-limit (brute-force exposure); requires an app-side fix.
- Security testing: OWASP ZAP (DAST) and AI-assisted red-team assessments;
  findings (incl. an exposed Google Cloud Run endpoint on the collection page)
  disclosed to developers for remediation.

## Dashboard (kept separate)

`dashboard/` is a React (Vite) + Node/Express/TypeScript + Postgres app for run
history, trends, and GitHub Actions flow dispatch (gated on a configured token).
Per ADR 0001 it stays in the monorepo for now and is slated to split into its own
repository (trigger: deployment to `dev.timepieces-world.example`). Do not fold dashboard
work into the framework's main line.

## Test Case Coverage & Tracking

Roadmap phases (see `docs/okf/test-case-roadmap.md`): 1 Authentication &
Identity (complete), 2 Catalog & Discovery, 3 Cart & Bag, 4 Checkout Funnel,
5 Payments & Security, 6 Retention & Wishlist. 67 test cases are registered and
tagged (`BDD`/`TDD`, `Automated`/`Not Automated`) in Zephyr Scale; the push to
250+ continues on `main`.

Known gap (tracked): some feature files are ahead of implemented glue / runner
registration — treat `docs/okf/testing-strategy.md` "Current Executable Runner
Scope" as ground truth for what actually runs.

## Contribution Notes

- Work on `main` (post-consolidation). Keep step definitions thin; route browser
  behavior into page objects; keep page objects assertion-free.
- New page objects: parameterize the `extends` clause with the class's own name;
  wire `getPagePath()`/`getPageTitle()` only for real endpoints.
- Prefer `ConfigReader.get(...)`; keep secrets in uncommitted
  `<env>.local.properties` or CI secrets.
- Any new shared state must be scenario-local or thread-safe (parallel runs).
- Meaningful changes must update the OKF (CurrentStatus, changelog, delta,
  decisions) per OKF governance.

## Quick Rehydration Checklist

1. Read `docs/okf/OKF_INDEX.md` and `docs/okf/CurrentStatus.md`.
2. Read `docs/system_architecture.md` and this file.
3. Inspect `pom.xml`, `testng.xml`, and the profile/suite XMLs.
4. Check `src/test/resources/config/staging.properties`.
5. Inspect the relevant feature file and confirm step-definition/runner wiring.
6. Run a narrow tagged command before broad suite execution.