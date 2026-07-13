# Emailing the Allure report

After every run — **local, Docker, and CI/CD** — the framework can email the
Allure HTML report (pass/fail summary in the body, full report zipped as an
attachment) to a recipient. It is driven by `scripts/email_allure_report.py`.

It is **safe by default**: if SMTP isn't configured it logs a notice and exits 0,
so it never breaks a run. Sending failures are also non-fatal (use `--strict` to
change that).

Default recipient: **167759523+GallantCaptain@users.noreply.github.com** (override with `MAIL_TO`).

## What you must provide: an SMTP sender

The recipient address is not the dependency — a **sending SMTP account** is. Set
these (locally as shell env vars; in CI as **repository secrets**):

| Variable | Required | Notes |
| --- | --- | --- |
| `SMTP_HOST` | yes | e.g. `smtp.gmail.com`, `smtp.office365.com`, `smtp.sendgrid.net` |
| `SMTP_USER` | yes | SMTP login |
| `SMTP_PASSWORD` | yes | password / app-password / API key |
| `SMTP_PORT` | no | default `587` |
| `SMTP_STARTTLS` | no | `true` (default) for port 587 |
| `SMTP_SSL` | no | `true` for implicit TLS (port 465); default `false` |
| `MAIL_FROM` | no | default = `SMTP_USER` |
| `MAIL_TO` | no | default `167759523+GallantCaptain@users.noreply.github.com` (comma-separated ok) |
| `MAIL_MAX_ATTACH_MB` | no | default `20`; larger reports are sent as summary-only |

> **Gmail / Google Workspace** (e.g. a `@timepieces-d2c.example` account): use
> `SMTP_HOST=smtp.gmail.com`, `SMTP_PORT=587`, `SMTP_USER=<the address>`, and an
> **App Password** (Google Account → Security → App passwords) as `SMTP_PASSWORD`
> — a normal password will not work with 2FA.

## Local

The helper script emails automatically after generating the report:

```powershell
$env:SMTP_HOST='smtp.gmail.com'; $env:SMTP_USER='you@timepieces-d2c.example'; $env:SMTP_PASSWORD='<app-password>'
./scripts/run-local-headed.ps1
```

Or after any manual run:

```powershell
mvn test ; mvn allure:report ; python scripts/email_allure_report.py
```

## Docker

`docker/run-tests.ps1` and `scripts/run-docker-headed.ps1` forward any `SMTP_*` /
`MAIL_*` env vars from your shell into the container, and the entrypoint emails
the report at the end (the image now ships `python3` + `ffmpeg`). Just export the
vars first, then run the script as usual. **Rebuild the image** once to pick up
`python3`.

## CI/CD (GitHub Actions)

Every test workflow (`staging-tests`, `production-tests`,
`exploratory-search-tests`, `test-pipeline`) has an **Email Allure Report** step
after report generation. Add these as **repository secrets**
(Settings → Secrets and variables → Actions):

```
SMTP_HOST, SMTP_USER, SMTP_PASSWORD          (required)
SMTP_PORT, SMTP_STARTTLS, SMTP_SSL, MAIL_FROM, MAIL_TO   (optional)
```

Until the secrets exist, the step runs and cleanly no-ops (the secret
expressions resolve to empty strings).

## Deliverability note

Some corporate mail systems quarantine `.zip` attachments. If the report doesn't
arrive, check spam/quarantine first; if attachments are blocked, switch to a
summary-only email plus a link to the CI **artifact** (the report is already
uploaded as `allure-report-*`) — ask and we can wire that variant.
