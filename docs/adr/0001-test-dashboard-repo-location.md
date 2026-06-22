# ADR 0001 — Test Dashboard: monorepo subfolder now, dedicated repo on deployment

- **Status:** Accepted
- **Date:** 2026-06-19
- **Deciders:** QA Automation + Dev team
- **Context tags:** repo structure, dashboard, deployment

## Context

The Test Dashboard (`dashboard/`) is a React + Node (TypeScript) app that visualizes
historical test trends/history and triggers predefined flows. It currently lives as a
subfolder on `main` in the `qa-automation` repo and is merged/working.

We need to decide where it should live long-term: **monorepo subfolder** (current) vs a
**dedicated repository**. (A long-lived feature branch was explicitly rejected — it
drifts from `main`, never integrates, and rots.)

Key facts that drive the decision:

- **Coupling is via stable contracts, not shared code.** The dashboard talks to the
  test framework only through: the `test_runs` Postgres table schema, the GitHub Actions
  API (dispatching `test-pipeline.yml`), published Allure report URLs, and Cucumber tag
  names. There is no shared build.
- **Different toolchains.** Dashboard = Node/TS; framework = Java/Maven. Today this
  requires CI path-filters (the smoke suite is scoped away from `dashboard/**`) to keep
  the two CIs from triggering each other.
- **It is a *deployed application*** with its own release lifecycle (build → deploy to
  `dev.timepieces-world.example`), unlike the test framework, which only runs in CI/Docker.
- **It will hold deploy/runtime secrets** the test repo otherwise wouldn't: a
  `GITHUB_TOKEN` with `actions:write`, a DB connection string, and (planned) an
  `ANTHROPIC_API_KEY` for LLM result summaries.
- **Small team, early stage** — monorepo overhead is currently the lowest.

## Decision

**Keep the dashboard in the monorepo (`qa-automation/dashboard/`) for now. Extract it to
a dedicated repository when it gets its own deployment** — which is expected soon, since
hosting at `dev.timepieces-world.example` is owned by the dev team and imminent.

### Triggers to split (revisit this ADR when ANY occurs)

1. The dashboard gets a **deploy pipeline** to `dev.timepieces-world.example` *(most likely first trigger)*.
2. It needs **deploy/runtime secrets** we don't want in the test repo (`ANTHROPIC_API_KEY`,
   a production DB URL, etc.).
3. **More than one person** actively develops it, or the roadmap features (RBAC, the LLM
   explainer, run-configuration filters) start landing.

## Consequences

- **Now:** lowest overhead; the dashboard and framework stay in sync; one clone. We accept
  the minor cost of CI path-filtering and Node artifacts living beside Java.
- **Later (post-split):** independent CI/CD, Dependabot, and secret scoping; the deployed
  app evolves on its own cadence. Cross-repo triggering is already part of the planned
  architecture (`QA_TRIGGER_TOKEN`), so the dashboard dispatching `qa-automation` via a
  token is consistent.
- **Migration is cheap and clean** because `dashboard/` is self-contained. When the time
  comes, preserve history with either:
  - `git filter-repo --path dashboard/` (then move to repo root in the new repo), or
  - `git subtree split --prefix=dashboard -b dashboard-extract` → push to the new repo.
  After extraction, the only wiring needed is the cross-repo dispatch token and the
  dashboard's own deploy config; the `test_runs` schema and tag contracts are unchanged.

## Not chosen

- **Dedicated repo immediately** — premature; adds repo/permission overhead before the
  deployment and secret-isolation pressures actually exist.
- **Long-lived feature branch** — rejected outright (drift, no integration).