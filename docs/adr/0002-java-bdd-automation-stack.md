# ADR 0002 - Java BDD automation stack

- **Status:** Accepted
- **Date:** 2026-06-24
- **Record note:** This ADR documents an existing implementation. The original decision date is TBD.
- **Owner:** TBD
- **Context tags:** framework, java, selenium, cucumber, testng, reporting

## Context

The repository implements a Timepieces storefront automation framework using Java/Maven with Selenium WebDriver, Cucumber JVM, TestNG, AssertJ, Allure, Log4j2, and supporting dependencies.

Verified sources:

- `pom.xml`
- `README.md`
- `docs/project_context.md`
- `docs/PRD.md`

## Alternatives Considered

Original alternatives considered are TBD. Common alternatives for future review include Playwright, Cypress, JUnit-only execution, and non-BDD test design, but these are not recorded as historical facts.

## Decision

Use Java 25, Maven, Selenium WebDriver, Cucumber JVM, TestNG, AssertJ, Allure, and Log4j2 as the core automation framework stack.

## Reason Chosen

The current repository is already implemented around this stack. It supports BDD feature files, browser automation, TestNG suite orchestration, parallel-ready execution, and reporting integrations.

## Consequences

- Feature files can act as living documentation.
- Test execution depends on JDK 25 compatibility for project builds.
- Selenium browser and driver behavior must be maintained across Chrome, Firefox, Edge, Brave, and Safari fallback handling.
- Dashboard and Java framework use different toolchains, which increases monorepo coordination needs.

## Traceability

- Requirement: REQ-FWK-001, REQ-FWK-002, REQ-FWK-003
- Task: TASK-RUN-001
- Implementation: `pom.xml`, `testng.xml`, `src/`
- Related OKF file: `docs/okf/system-architecture.md`
