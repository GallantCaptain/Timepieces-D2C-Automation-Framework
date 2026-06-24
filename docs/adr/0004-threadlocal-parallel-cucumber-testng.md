# ADR 0004 - ThreadLocal parallel Cucumber/TestNG execution

- **Status:** Accepted
- **Date:** 2026-06-24
- **Record note:** This ADR documents an existing implementation. The original decision date is TBD.
- **Owner:** TBD
- **Context tags:** parallel execution, testng, cucumber, webdriver lifecycle

## Context

`BaseRunner` overrides the Cucumber TestNG data provider with `parallel = true`. `DriverFactory` stores WebDriver in a `ThreadLocal<WebDriver>`. `Hooks` starts a fresh driver before each scenario and quits/removes it after each scenario.

`testng.xml` has suite-level `parallel="none"` and `data-provider-thread-count="2"`, so TestNG suite tests are not parallelized while scenario data provider execution is parallel-ready.

Verified sources:

- `BaseRunner.java`
- `DriverFactory.java`
- `Hooks.java`
- `testng.xml`

## Alternatives Considered

Original alternatives considered are TBD. Common alternatives for future review include serial execution only, per-suite browser reuse, JUnit Platform parallel execution, Selenium Grid-managed concurrency, and container-per-browser execution, but these are not recorded as historical facts.

## Decision

Use TestNG+Cucumber runners with scenario-level parallelism through the Cucumber TestNG data provider and isolate WebDriver instances with `ThreadLocal`.

## Reason Chosen

This allows concurrent scenario execution while keeping browser instances isolated per execution thread.

## Consequences

- Shared state must be scenario-local or thread-safe.
- Driver leaks must be prevented by always quitting and removing the ThreadLocal driver.
- Test data and OTP flows must avoid rate limits and duplicate execution.
- Runner and tag overrides require care because explicit Cucumber tag filters apply across runners.

## Traceability

- Requirement: REQ-FWK-001
- Task: TASK-RUN-001
- Implementation: `BaseRunner.java`, `DriverFactory.java`, `Hooks.java`, `testng.xml`
- Related OKF file: `docs/okf/testing-strategy.md`
