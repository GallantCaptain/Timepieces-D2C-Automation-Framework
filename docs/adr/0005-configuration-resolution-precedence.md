# ADR 0005 - Configuration resolution precedence

- **Status:** Accepted
- **Date:** 2026-06-24
- **Record note:** This ADR documents an existing implementation. The original decision date is TBD.
- **Owner:** TBD
- **Context tags:** configuration, environments, secrets

## Context

`ConfigReader` loads `config/<env>.properties` from the classpath, optionally overlays `config/<env>.local.properties`, and resolves individual keys in this order:

1. JVM system property
2. OS environment variable
3. Loaded properties file

Verified sources:

- `ConfigReader.java`
- `src/test/resources/config/staging.properties`
- `src/test/resources/config/production.properties`
- `docs/project_context.md`

## Alternatives Considered

Original alternatives considered are TBD. Common alternatives for future review include environment-only configuration, Maven profiles, Spring-style profiles, and dedicated secret managers, but these are not recorded as historical facts.

## Decision

Use `ConfigReader` as the framework configuration source with JVM property precedence, then environment variables, then environment properties files with optional local overrides.

## Reason Chosen

This supports local developer overrides, CI secrets, and committed environment defaults without requiring source changes for each run.

## Consequences

- Tests can be parameterized from Maven CLI.
- Sensitive values should be provided by environment variables or local uncommitted properties.
- Config key drift must be avoided; current `headless` vs `is.headless` drift is tracked as technical debt.

## Traceability

- Requirement: REQ-FWK-001
- Task: TASK-CFG-001
- Implementation: `ConfigReader.java`, `src/test/resources/config/`
- Related OKF file: `docs/okf/development-standards.md`
