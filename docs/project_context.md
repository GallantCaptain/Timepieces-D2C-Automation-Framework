# Timepieces Automation Framework: Project Context

Last updated: 2026-06-05

## Purpose

This repository contains the Timepieces web automation framework: a Java/Maven BDD test suite for validating Timepieces storefront flows such as homepage initialization, sidebar navigation, login/OTP, product discovery, cart, checkout, wishlist, payment, and account/session behavior.

The project is intended to act as both an executable test harness and living test documentation through Cucumber feature files.

## Stack

- Language/runtime: Java, Maven, compiler release 25
- Test automation: Selenium WebDriver 4.44.0
- BDD: Cucumber-JVM 7.18.0
- Runner: TestNG 7.10.2
- Reporting: Allure via `allure-testng` and `allure-cucumber7-jvm`
- Logging: Log4j2
- Optional/forward-looking integrations: Rest-Assured, Appium, Healenium
- Screen recording: Monte Screen Recorder
- DI/shared scenario state: Cucumber PicoContainer

## Repository Map

- `pom.xml`: dependency and plugin source of truth. Surefire is configured to execute `testng.xml`.
- `testng.xml`: suite definition. Runs `SmokeRunner`, `SanityRunner`, and `RegressionRunner`; passes `environment=staging`.
- `src/main/java/com/timepieces/automation/base`: browser driver lifecycle.
- `src/main/java/com/timepieces/automation/pages`: page object hierarchy and page/component models.
- `src/main/java/com/timepieces/automation/utils`: config, browser/window/action utilities, and screen recording.
- `src/test/java/com/timepieces/automation/hooks`: Cucumber scenario setup/teardown.
- `src/test/java/com/timepieces/automation/runners`: TestNG+Cucumber runners.
- `src/test/java/com/timepieces/automation/stepdefinitions`: Cucumber glue. At the time of this note, only login steps are implemented.
- `src/test/resources/features`: Gherkin specs grouped into smoke, functional, and regression folders.
- `src/test/resources/config`: environment properties for `staging` and `production`.
- `docs`: product, architecture, test strategy, and this context note.
- `docker`: Dockerized multi-browser test bed.
- Generated/local runtime folders include `.allure`, `logs`, and `target`.

## Execution Model

The intended local Maven path is:

```powershell
mvn test
```

Useful overrides:

```powershell
mvn test -Denv=staging -Dbrowser=chrome -Dheadless=true "-Dcucumber.filter.tags=@Smoke"
mvn allure:serve
```

Surefire delegates discovery to `testng.xml`, because the runner classes do not follow default `Test*.java` naming. Allure results are redirected to `target/allure-results` so `mvn clean` removes stale report state.

The suite XML currently invokes all three suite runners:

- `SmokeRunner`: tag filter `@Smoke`
- `SanityRunner`: tag filter `@Sanity`
- `RegressionRunner`: tag filter `@Regression`

`BaseRunner` overrides Cucumber's TestNG data provider with `parallel = true`, so scenarios may execute concurrently. Browser isolation depends on `DriverFactory` using a `ThreadLocal<WebDriver>`.

## Docker Workflow

The Docker test bed is under `docker/` and targets Ubuntu with Java 25, Maven 3.9.9, Chrome, Firefox, Edge, and Brave.

Typical invocation:

```powershell
.\docker\run-tests.ps1
```

Defaults:

- Environment: `staging`
- Tags: `@Smoke and @Login`
- Browsers: Chrome, Firefox, Edge, Brave
- Mode: headless

Visible debugging can be enabled with:

```powershell
.\docker\run-tests.ps1 -Vnc
```

The entrypoint loops through browsers and runs Maven with:

```bash
mvn test -Denv="$ENV_TARGET" -Dbrowser="$browser" -Dheadless="$HEADLESS" -Dchrome.no.sandbox=true "-Dcucumber.filter.tags=${TAGS}"
```

## Configuration

`ConfigReader` loads `config/<env>.properties` from the classpath and then optionally overlays `config/<env>.local.properties` if present. Lookup precedence is:

1. JVM system property, such as `-Dbrowser=firefox`
2. OS environment variable with the same key
3. Environment properties file

Known keys used by core code include:

- `env`: selected by Maven/TestNG, defaults to `staging`
- `browser`: `chrome`, `firefox`, `edge`, `safari`, or `brave`
- `headless`: read by `DriverFactory`
- `chrome.no.sandbox`: enables `--no-sandbox` for containers
- `explicit.wait.timeout`: used by `BasePage`
- `recordVideo`: controls Monte recording in hooks
- `max.retry.count`: used by retry analyzer
- `base.url` and `login.path`: used by page objects

Observation: `staging.properties` currently defines `is.headless=false`, but `DriverFactory` reads `headless`. Production includes both `headless=false` and `is.headless=false`. Prefer `headless` for new code and CLI overrides unless this is reconciled.

## Browser Lifecycle

`Hooks` initializes a fresh driver before every scenario with `DriverFactory.initDriver()`.

On scenario failure, hooks attach:

- PNG screenshot from Selenium
- AVI video, when `recordVideo` is enabled and a recording exists

On success, videos are discarded. In all cases the driver is quit and removed from the `ThreadLocal`.

`DriverFactory` supports Chrome, Firefox, Edge, Safari, and Brave. Chromium-based browsers receive stability and anti-fingerprint flags such as `--disable-dev-shm-usage`, `--window-size=1920,1080`, `--disable-blink-features=AutomationControlled`, and optional `--no-sandbox`.

For Brave, `BRAVE_PATH` must be set.

## Page Object Conventions

Core inheritance:

```text
BasePage
  -> HeaderOnlyBasePage
       -> StoreFrontBasePage
```

- `BasePage` owns Selenium wrappers, waits, click/input/text helpers, visibility checks, and dynamic locator formatting.
- `HeaderOnlyBasePage` adds `HeaderComponent`.
- `StoreFrontBasePage` adds `FooterComponent`.
- Pages should expose actions and state queries, not raw locators.
- Assertions belong in step definitions, not page objects.
- Shared header/footer behavior belongs in components.

This design is also documented in `docs/system_architecture.md`; use that file as the coding standard reference.

## Current Test Surface

Smoke feature files currently cover:

- Staging password bypass
- Homepage initialization and sidebar
- Valid login via responsive sidebar
- Product category navigation
- Domestic checkout initialization
- International checkout initialization
- Global search validation
- User authentication and OTP prompt

Regression specs are organized by domain:

- `01_Authentication_Identity`
- `02_Catalog_Product_Discovery`
- `03_Cart_Bag_Management`
- `04_Checkout_Secure_Funnel`
- `05_Payment_Integration`
- `06_Customer_Retention_Wishlist`

Functional specs currently include login and checkout positive/negative suites.

Important gap: most feature files appear to be ahead of implemented glue. `LoginSteps.java` is the only step definition file present as of this note, so many scenarios may be living specs until matching step definitions are added.

## Reporting And Retry

- Runner plugins produce HTML, JSON, rerun text, and Allure outputs under `target/cucumber-reports` and `target/allure-results`.
- `RetryAnnotationTransformer` attaches `RetryAnalyzer` to TestNG scenario methods.
- Retry count is config-driven through `max.retry.count`.
- `DriverFactory` writes Allure `environment.properties` under `target/allure-results`.

## Generated Artifacts

Common generated paths:

- `target/allure-results`
- `target/cucumber-reports`
- `target/screen-recordings`
- `.allure`
- `logs`

Avoid committing runtime output unless explicitly needed.

## Contribution Notes

- Keep new step definitions thin; route browser behavior into page objects.
- Keep page objects assertion-free.
- Add new shared scenario data to `TestContext` when it is meaningful and typed; use the generic map only for short-lived incidental data.
- Prefer `ConfigReader.get(...)` over hardcoded environment values.
- For new suite filters, update runners and document expected tags.
- For local secrets or environment-specific overrides, use uncommitted `<env>.local.properties` files rather than committed config.
- Be careful with parallel execution: any new shared state must be scenario-local or thread-safe.

## Quick Rehydration Checklist

1. Read `docs/system_architecture.md`.
2. Read this file.
3. Inspect `pom.xml` and `testng.xml`.
4. Check `src/test/resources/config/staging.properties`.
5. Inspect the relevant feature file under `src/test/resources/features`.
6. Confirm whether step definitions exist for that feature.
7. Run a narrow tagged Maven command before broad suite execution.
