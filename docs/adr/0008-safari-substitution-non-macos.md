# ADR 0008 - Safari substitution on non-macOS hosts

- **Status:** Accepted
- **Date:** 2026-06-24
- **Record note:** This ADR documents an existing implementation. The original decision date is TBD.
- **Owner:** TBD
- **Context tags:** browser support, safari, selenium, reporting

## Context

`DriverFactory` supports `browser=safari`, but Selenium SafariDriver is available only on macOS. On non-macOS hosts, the implementation substitutes a configured Chromium browser and annotates Allure environment properties so reports do not imply real Safari execution.

Verified sources:

- `DriverFactory.java`
- `staging.properties`
- `README.md`

## Alternatives Considered

Original alternatives considered are TBD. Common alternatives for future review include failing fast for Safari outside macOS, using cloud browser providers, using Playwright WebKit, or restricting Safari tests to macOS runners, but these are not recorded as historical facts.

## Decision

Use real SafariDriver on macOS only. On Windows/Linux/Docker/CI, substitute `safari.fallback.browser` (`chrome`, `edge`, or `brave`; default `chrome`) and annotate the substitution in Allure.

## Reason Chosen

This lets scenarios continue to execute on non-macOS hosts while preserving report honesty that the run is not real Safari/WebKit coverage.

## Consequences

- Non-macOS Safari runs do not validate WebKit-specific behavior.
- Reports must be read carefully for substitution annotations.
- True Safari coverage requires macOS infrastructure or a browser cloud decision.

## Traceability

- Requirement: REQ-FWK-002
- Task: TASK-DOC-001
- Implementation: `DriverFactory.java`, `src/test/resources/config/staging.properties`
- Related OKF file: `docs/okf/testing-strategy.md`
