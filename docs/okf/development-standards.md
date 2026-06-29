# Development Standards

Last reviewed: 2026-06-29

## Languages

| Area | Language | Source |
| --- | --- | --- |
| Automation framework | Java | `pom.xml`, `src/main/java`, `src/test/java` |
| BDD specs | Gherkin | `src/test/resources/features` |
| Dashboard server | TypeScript | `dashboard/server/package.json` |
| Dashboard web | TypeScript + TSX | `dashboard/web/package.json` |
| Infrastructure scripts | Shell, PowerShell, YAML | `docker/`, `.github/workflows/` |

## Frameworks

| Area | Framework/Tool | Source |
| --- | --- | --- |
| Build | Maven wrapper | `mvnw`, `mvnw.cmd`, `pom.xml` |
| UI automation | Selenium WebDriver | `pom.xml`, `DriverFactory.java` |
| BDD | Cucumber JVM | `pom.xml`, runners |
| Test runner | TestNG | `pom.xml`, `testng.xml` |
| Assertions | AssertJ | `pom.xml`, step definitions |
| Reporting | Allure + Cucumber HTML/JSON/rerun outputs | runners, `pom.xml` |
| Logging | Log4j2 | `src/test/resources/log4j2.xml` |
| Dashboard API | Express | `dashboard/server/package.json` |
| Dashboard UI | React + Vite | `dashboard/web/package.json` |

## Coding Conventions

| Convention | Status | Source |
| --- | --- | --- |
| Page objects expose actions and state queries, not raw locators. | Current | `docs/system_architecture.md` |
| Assertions belong in step definitions, not page objects. | Current | `docs/system_architecture.md`, `LoginSteps.java` |
| Shared header/footer/sidebar/cart behavior belongs in component classes. | Current | `docs/system_architecture.md`, `pages/components/` |
| Step definitions should remain thin and route browser behavior into page objects. | Current | `docs/project_context.md` |
| Use `TestContext` for scenario state shared across steps. | Current | `TestContext.java` |
| Use `ConfigReader.get(...)` instead of hardcoded environment values. | Current | `docs/project_context.md` |
| Local secrets and environment-specific overrides should use uncommitted `<env>.local.properties`. | Current | `ConfigReader.java`, `docs/project_context.md` |
| Dashboard real secrets belong in `.env`, with `.env.example` as the committed template. | Current | `dashboard/README.md`, `.env.example` |
| API testing should be built and run purely in TDD (TestNG/Rest Assured), avoiding BDD/Cucumber layers. | Current | `SearchApiTest.java`, `testng.xml` |
| Agents must import and reuse shared utility classes and inherited `BasePage` methods instead of calling fully qualified package names inline. | Current | `BasePage.java`, `BrowserUtils.java`, `WindowUtils.java`, `FooterComponent.java`, `FooterSteps.java` |
| Page and component objects should call inherited `BasePage` wrappers such as `getElement`, `getElements`, `clickElement`, `getText`, `getAttribute`, `formatLocator`, and `isMobileViewport` directly when available. | Current | `BasePage.java`, `FooterComponent.java` |

## Design Patterns

| Pattern | Usage |
| --- | --- |
| Page Object Model | Storefront pages under `src/main/java/com/timepieces/automation/pages/web/` |
| Component Object Model | Reusable UI components under `pages/components/` |
| Fluent page APIs | Used by page objects such as `LoginPage` |
| Dependency injection | Cucumber PicoContainer injects `TestContext` into steps |
| ThreadLocal driver lifecycle | `DriverFactory` isolates WebDriver per scenario thread |
| Listener-based cross-cutting behavior | Retry and metrics listeners are wired through `testng.xml` |
| Shared utility imports | Step definitions and page objects import `ConfigReader`, `BrowserUtils`, `WindowUtils`, and similar utilities at the top of the file, then call the short class name in code |

## Agent Implementation Instructions

- When adding or modifying Java code, add explicit imports for utilities under `com.timepieces.automation.utils` and page objects under `com.timepieces.automation.pages` instead of using inline fully qualified calls such as `com.timepieces.automation.utils.BrowserUtils.scrollToBottom(driver)`.
- When a page or component extends `BasePage`, use the inherited helper methods directly instead of reimplementing waits, visibility checks, clicks, element lookup, attribute reads, locator formatting, or viewport checks in the child class.
- If a reusable operation does not exist in `BasePage` or `utils`, add a small named helper to the appropriate shared class only when it is broadly useful; otherwise keep the behavior inside the owning page/component object.
- Keep step definitions thin: import page objects, components, and utilities, then route browser behavior into page/component APIs while leaving assertions in the step or test class.

## Branching Strategy

| Item | Current Fact |
| --- | --- |
| PR target | Workflow triggers on pull requests to `main` |
| Long-lived feature branch for dashboard | Rejected in ADR 0001 |
| Full branching model | TBD |

## Commit Standards

Commit message format, required trailers, and release tagging standards are TBD. No committed standard was found during OKF creation.

## Quality Gates

| Gate | Status | Source |
| --- | --- | --- |
| Java test workflow on PRs to `main` | Present, path-filtered | `.github/workflows/test-pipeline.yml` |
| Dashboard server typecheck script | Present | `dashboard/server/package.json` |
| Dashboard web typecheck/build scripts | Present | `dashboard/web/package.json` |
| Automated dashboard CI | TBD; no dashboard-specific workflow was found |
| Markdown/documentation linting | TBD |

## Traceability

| Standard | Decision | Task |
| --- | --- | --- |
| Java/Selenium/Cucumber/TestNG stack | DEC-0002 | TASK-RUN-001 |
| POM/COM structure | DEC-0003 | TASK-LOC-001 |
| ThreadLocal parallel execution | DEC-0004 | TASK-RUN-001 |
| Config precedence | DEC-0005 | TASK-CFG-001 |
| ADR use for decisions | DEC-0009 | TASK-OKF-001 |
