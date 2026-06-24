# ADR 0007 - Cloudflare automation bypass header

- **Status:** Accepted
- **Date:** 2026-06-24
- **Record note:** This ADR documents an existing implementation. The original decision date is TBD.
- **Owner:** TBD
- **Context tags:** cloudflare, ci, staging, browser automation

## Context

CI runs against `staging.timepieces-d2c.example` can be blocked by Cloudflare human verification before the app loads. `DriverFactory` can apply an `X-Automation-Bypass` HTTP header to Chromium sessions when a token is supplied through environment variable, JVM property, or config.

Verified sources:

- `README.md`
- `DriverFactory.java`
- `docs/proposals/Timepieces-QA-Infrastructure-Proposal.html`
- `.github/workflows/test-pipeline.yml`

## Alternatives Considered

The README and proposal mention two known options:

- Cloudflare WAF skip rule keyed on an automation header.
- Self-hosted runner with an allowlisted IP.

Other original alternatives considered are TBD.

## Decision

Implement framework support for a secret automation bypass header, with the external Cloudflare WAF rule tracked separately.

## Reason Chosen

The bypass header lets automation identify trusted non-production traffic without trying to solve a CAPTCHA challenge in WebDriver.

## Consequences

- The framework-side header is not sufficient unless Cloudflare is configured to honor it.
- The current implementation applies the header through Chromium CDP, so Firefox is not covered by this path.
- Header token must be stored as a secret.
- This is for non-production automation access and must not be treated as a CAPTCHA-solving mechanism.

## Traceability

- Requirement: REQ-FWK-001
- Task: TASK-CI-001
- Implementation: `DriverFactory.java`, `.github/workflows/test-pipeline.yml`
- Related OKF issue: ISSUE-001
