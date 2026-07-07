# ADR 0010 - Healenium self-healing locators

- **Status:** Accepted
- **Date:** 2026-07-07
- **Owner:** QA Automation
- **Context tags:** selenium, locators, resilience, flakiness

## Context

`healenium-web` 3.4.6 and an AspectJ weaver javaagent (`-javaagent:aspectjweaver-...jar`
in the Surefire `argLine`) were already declared in `pom.xml` before this ADR, along
with a placeholder `healenium.server.url` key in `staging.properties` and
`production.properties`. None of it was wired to any code — `DriverFactory` built a
plain vendor `WebDriver` with no Healenium involvement, and no `healenium.properties`
file or backend service existed. This was tracked as `DEBT-006` / `TASK-MOBILE-API-001`
("Decide whether Appium and Healenium dependencies are active roadmap items").

The `data-testid` locator rollout (`PROG-001`) is still in progress, which means
plain CSS/XPath locators remain in several page objects and stay exposed to product
markup changes. Healenium's self-healing WebDriver decorator is a mitigation for that
exact failure mode: when a locator breaks, it looks up the closest previously-seen
match from its own history and retries instead of failing the scenario outright.

Verified sources:

- `pom.xml` (`healenium-web` dependency, `aspectjweaver` Surefire dependency)
- `src/main/java/com/timepieces/automation/base/DriverFactory.java`
- `src/test/resources/config/staging.properties`, `production.properties`
- `docs/okf/CurrentStatus.md` (`DEBT-006`), `docs/okf/task-backlog.md` (`TASK-MOBILE-API-001`)

## Alternatives Considered

- Leave Healenium as an unused dependency and keep chasing locator breaks manually as
  `data-testid` rollout (`TASK-LOC-001`) completes.
- Remove the dependency entirely as dead weight until a concrete need arises.
- Wire it up now, but default it OFF so it costs nothing until a team opts in.

## Decision

Wire `healenium-web` into `DriverFactory` as the single point where every WebDriver is
constructed, wrapping the finished vendor driver in `SelfHealingDriver.create(...)`
before it is published to `ThreadLocal`. Gate the wrap behind a new `healenium.enabled`
config key (`ConfigReader`), defaulting to `false` in both `staging.properties` and
`production.properties`. Add a `healenium.properties` classpath file (backend URL,
recovery tries, score cap) and two new `docker-compose.yml` services
(`healenium-postgres`, `hlm-backend`) that a developer can bring up locally to try it.

## Reason Chosen

Every page object, component, and step definition obtains its driver exclusively
through `DriverFactory.getDriver()` (see `BasePage`, `Hooks`, and the step definition
classes — enforced by the existing "reuse `BasePage`/shared utilities" development
standard). Wrapping the driver at that single construction point makes every
`getElement`, `WebDriverWait`, and `ExpectedConditions` call in the framework
self-healing for free, with zero changes required in any of the 81 existing
page/component classes. A config-gated default-off toggle means CI and any developer
who has not started the backend stack are completely unaffected — `SelfHealingDriver`
is never even instantiated when disabled, so there is no dependency on `localhost:7878`
being reachable unless a developer opts in.

## Consequences

- Healenium is real and callable now, but OFF everywhere by default; nobody gets
  self-healing until they explicitly set `healenium.enabled=true` and start
  `healenium-postgres` + `hlm-backend` via `docker-compose.yml`.
- The Cloudflare CDP bypass header (`applyCloudflareBypassHeader`, ADR 0007) must be
  applied to the raw vendor driver BEFORE the Healenium wrap, because
  `SelfHealingDriver` is not a `ChromiumDriver` and the CDP cast would silently stop
  matching after wrapping. `DriverFactory` now does this in a fixed order in both the
  local and `CLOUD` execution-mode branches.
- `healenium.properties` is a single static file shared by every environment (it is
  read directly by the Healenium client, not through `ConfigReader`'s -D/env/file
  precedence ladder), so its `hlm.server.url` must be kept manually in sync with the
  `healenium.server.url` reference key in `staging.properties`/`production.properties`.
  This is a known, documented limitation, not a bug.
- `healenium-web`'s healing lookups depend on locators having been seen and recorded
  by the backend at least once before a break occurs; it does not help on the very
  first run against a brand-new locator, and does not replace the `data-testid`
  rollout (`TASK-LOC-001`) — it is a safety net underneath it, not a substitute.
- Adds two more optional Docker services (`healenium-postgres`, `hlm-backend`) that
  are opt-in and independent of the existing `postgres-dev` metrics database.

## Traceability

- Requirement: REQ-FWK-001
- Decision: DEC-0010
- Task: TASK-MOBILE-API-001
- Debt resolved: DEBT-006 (Healenium half — Appium activation remains open/TBD)
- Implementation: `DriverFactory.java`, `healenium.properties`, `staging.properties`,
  `production.properties`, `docker-compose.yml`
- Related OKF file: `docs/okf/system-architecture.md`
