# Timepieces Test Dashboard

A React + Node dashboard for the Timepieces automation suite. It provides:

- **Historical reports & trends** — pass rate and volume over time, read from the
  same Postgres `test_runs` table the test publisher writes to.
- **On-demand flow execution** — dispatches the existing GitHub Actions pipeline
  (`.github/workflows/test-pipeline.yml`) for predefined flows (Login, Checkout,
  Complete E2E) and shows live run status.
- **Execution history** — per-run results with links to the published Allure reports.
- **Auth** — email/password login backed by a Postgres `dashboard_users` table (bcrypt + JWT).

```
dashboard/
  server/   Node + TypeScript API (Express, pg, JWT)
  web/      React + Vite SPA (TypeScript)
```

## Prerequisites

- Node 20+ (developed on Node 26)
- Postgres reachable via a connection URL. For local dev, the repo's
  `docker-compose.yml` already starts one: `docker compose up -d postgres-dev`
  (db `timepieces_metrics`, user `postgres` / `devpassword`).

## Server setup

```bash
cd dashboard/server
npm install
cp .env.example .env          # then edit values (JWT_SECRET, DATABASE_URL, GITHUB_TOKEN, …)
npm run db:init               # creates dashboard_users (+ test_runs if missing)
npm run user:create -- you@timepieces-d2c.example 'a-strong-password' 'Your Name'
npm run dev                   # API on http://localhost:4000
```

Key env vars (see `.env.example` for the full list):

| Var | Purpose |
| --- | --- |
| `JWT_SECRET` | signs login tokens (use a long random string) |
| `DATABASE_URL` | Postgres URL; falls back to `NEON_DATABASE_URL` |
| `GITHUB_TOKEN` | PAT with `actions:write` — required to dispatch flows |
| `GITHUB_OWNER` / `GITHUB_REPO` / `GITHUB_WORKFLOW_FILE` | which workflow to trigger |
| `CORS_ORIGIN` | the web app's origin (default `http://localhost:5173`) |

> Flow execution is **disabled** (buttons greyed out, `executionEnabled: false`)
> until `GITHUB_TOKEN` is set. Trends and history work without it.

## Web setup

```bash
cd dashboard/web
npm install
npm run dev                   # SPA on http://localhost:5173, proxies /api → :4000
```

Build for production: `npm run build` (outputs `web/dist/`). Serve `dist/` behind
the same origin as the API (or point a reverse proxy `/api` → the Node server).
Hosting at `dev.timepieces-world.example` is handled separately. 

### Subdomain Authentication & Security
The QA dashboard (`dev.timepieces-world.example`) is completely isolated from the production analytics console (`dashboard.timepieces-world.example/dashboard`):
1. **Token Isolation:** Authenticaton uses `localStorage`, which is strictly isolated by browser origin. Tokens set on `dev.timepieces-world.example` do not leak to or conflict with `dashboard.timepieces-world.example`.
2. **Database Isolation:** User logins are stored in `dashboard_users` in the QA metrics database, completely separate from customer databases or PPI.

## Flows → tags

Flows map to Cucumber tag expressions in
[`server/src/flows.config.ts`](server/src/flows.config.ts). Adjust the tag
expressions there as the suite evolves:

| Flow | Tags |
| --- | --- |
| Login Flow | `@Login` |
| Checkout Flow | `@Checkout` |
| Complete End-to-End Flow | `@Smoke or @Regression` |

## API surface

| Method | Path | Notes |
| --- | --- | --- |
| `POST` | `/api/auth/login` | `{ email, password }` → `{ token, user }` |
| `GET` | `/api/auth/me` | current user (auth) |
| `GET` | `/api/runs` | history; `?env=&browser=&limit=&offset=` |
| `GET` | `/api/runs/summary` | all-time headline stats |
| `GET` | `/api/runs/trends?days=30` | daily aggregates for charting |
| `GET` | `/api/runs/:id` | single run |
| `GET` | `/api/flows` | flow definitions + options |
| `GET` | `/api/flows/runs` | recent pipeline runs (live status) |
| `POST` | `/api/flows/:key/run` | dispatch a flow (`{ env, browser }`) |

All routes except `/api/auth/login` and `/api/health` require a Bearer JWT.