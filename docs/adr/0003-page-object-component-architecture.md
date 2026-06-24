# ADR 0003 - Page object and component architecture

- **Status:** Accepted
- **Date:** 2026-06-24
- **Record note:** This ADR documents an existing implementation. The original decision date is TBD.
- **Owner:** TBD
- **Context tags:** page objects, component objects, selenium, maintainability

## Context

The framework uses a layered page architecture:

- `BasePage`
- `HeaderOnlyBasePage`
- `StoreFrontBasePage`
- Page objects under `pages/web/`
- Reusable components under `pages/components/`

The architecture guidance states that page objects expose actions and state queries, while assertions belong in step definitions.

Verified sources:

- `docs/system_architecture.md`
- `docs/project_context.md`
- `src/main/java/com/timepieces/automation/pages/`
- `src/test/java/com/timepieces/automation/stepdefinitions/`

## Alternatives Considered

Original alternatives considered are TBD. Common alternatives for future review include direct Selenium calls in step definitions, Screenplay pattern, or service-object abstractions, but these are not recorded as historical facts.

## Decision

Use Page Object Model and Component Object Model. Keep shared UI behavior in components, keep browser interactions in page objects, and keep assertions in step definitions.

## Reason Chosen

The current storefront has reusable header, footer, sidebar, cart, product card, payment, account, legal, and product page behavior. POM/COM keeps these interactions organized and reusable while keeping Gherkin glue readable.

## Consequences

- Page object methods must be maintained as UI locators evolve.
- Shared UI behavior should be moved to components instead of duplicated across pages.
- Step definitions should remain thin.
- Locator stability is important; the `data-testid` rollout remains a tracked task.

## Traceability

- Requirement: REQ-FWK-001
- Task: TASK-LOC-001
- Implementation: `src/main/java/com/timepieces/automation/pages/`
- Related OKF file: `docs/okf/development-standards.md`
