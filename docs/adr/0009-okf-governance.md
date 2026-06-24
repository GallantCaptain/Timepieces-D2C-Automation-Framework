# ADR 0009 - OKF governance and navigation

- **Status:** Accepted
- **Date:** 2026-06-24
- **Owner:** Project orchestrator
- **Context tags:** documentation, governance, knowledge management

## Context

The project needs a durable Organizational Knowledge File that acts as authoritative project memory across development, QA, architecture, product, and leadership audiences.

The user provided governance rules requiring:

- README as navigation hub.
- One topic per file.
- ADRs for decisions.
- Append-only changelog.
- Accurate current status.
- Traceability across requirements, tasks, decisions, and implementations.

## Alternatives Considered

- Single large OKF file.
- Existing scattered docs only.
- Topic-per-file OKF with ADR-backed decisions.

## Decision

Use `docs/okf/` as the topic-per-file OKF, make `docs/okf/OKF_INDEX.md` the OKF index, keep root `README.md` as the repository navigation hub, use `docs/adr/` for decisions, and maintain append-only `docs/okf/changelog.md`.

## Reason Chosen

Topic files keep each area searchable and maintainable. ADRs preserve decision context and history. README navigation makes the knowledge base discoverable from the repository root.

## Consequences

- Meaningful changes must update the relevant OKF topic, current status, backlog, decisions, and changelog.
- History must be preserved by marking facts as drift/superseded instead of deleting decision rationale.
- OKF upkeep becomes part of the project workflow.

## Traceability

- Requirement: OKF governance rules from user request
- Task: TASK-OKF-001
- Implementation: `docs/okf/`, `docs/adr/`, `README.md`
- Related OKF file: `docs/okf/OKF_INDEX.md`
