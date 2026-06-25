# Organizational Knowledge File Index

Status: Authoritative project memory  
Last reviewed: 2026-06-25  
Project: Timepieces QA Automation

## Governance

- The OKF is the single source of truth for current project knowledge.
- Root `README.md` is the navigation hub and must link to this index.
- One topic equals one file under `docs/okf/`.
- Architecture and framework decisions are recorded as ADRs under `docs/adr/`.
- `CurrentStatus.md` must reflect the current implementation state.
- `changelog.md` is append-only.
- Historical notes are preserved; conflicting or obsolete facts are marked as drift, superseded, or TBD.

## Topic Files

| Topic | File |
| --- | --- |
| Project overview | [project-overview.md](project-overview.md) |
| System architecture | [system-architecture.md](system-architecture.md) |
| Development standards | [development-standards.md](development-standards.md) |
| Testing strategy | [testing-strategy.md](testing-strategy.md) |
| Project structure | [project-structure.md](project-structure.md) |
| Current implementation status | [CurrentStatus.md](CurrentStatus.md) |
| Decisions log | [decisions-log.md](decisions-log.md) |
| Known issues | [known-issues.md](known-issues.md) |
| Task backlog | [task-backlog.md](task-backlog.md) |
| Risks | [risks.md](risks.md) |
| Learnings | [learnings.md](learnings.md) |
| Changelog | [changelog.md](changelog.md) |
| Delta updates | [delta-updates.md](delta-updates.md) |

## Primary Source Documents

| Source | Purpose | OKF Treatment |
| --- | --- | --- |
| [../PRD.md](../PRD.md) | Product requirements for authentication and homepage | Historical/source input |
| [../system_architecture.md](../system_architecture.md) | Framework architecture and coding standards | Historical/source input; OKF records current drifts |
| [../project_context.md](../project_context.md) | Project context and execution notes | Historical/source input; OKF records current drifts |
| [../test-strategy/loginAndAccountStates.md](../test-strategy/loginAndAccountStates.md) | Login/account state test strategy | Historical/source input |
| [../adr/](../adr/) | Accepted architecture decision records | Decision source of truth |

## Traceability Keys

| Prefix | Meaning |
| --- | --- |
| `REQ-` | Requirement |
| `DEC-` | Decision |
| `TASK-` | Backlog task |
| `IMPL-` | Implementation artifact |
| `ISSUE-` | Known issue |
| `RISK-` | Risk |
| `LEARN-` | Reusable learning |

## Assumptions Register

| ID | Assumption | Status |
| --- | --- | --- |
| ASM-001 | Product owners and QA automation are the operating owners for most OKF items unless a named owner is documented elsewhere. | Assumption; owner remains `TBD` where not verified |
| ASM-002 | Existing docs describe intended behavior even when current code does not fully implement it. | Assumption; code wins for current implementation status |

## Self-Audit

- Duplicates checked: Initial OKF uses one file per topic; source docs are linked rather than copied in full.
- Contradictions checked: Initial drift items are recorded in [known-issues.md](known-issues.md) and [task-backlog.md](task-backlog.md).
- Missing dependencies checked: Backlog entries include dependency information.
- Stale information checked: Existing docs with mismatches are preserved and flagged rather than rewritten.
