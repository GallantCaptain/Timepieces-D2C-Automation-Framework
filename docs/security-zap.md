# OWASP ZAP (DAST) Security Scanning

Dynamic security scanning of the Timepieces storefront with [OWASP ZAP](https://www.zaproxy.org/),
asserted against the **OWASP Top 10 — 2025 by default, 2021 on request**.

Where the rest of the suite asks *"does the feature work?"*, this suite asks *"can the
feature be abused?"* — and it does it against a running site, not the source code.

---

## TL;DR

```powershell
# Staging: full ACTIVE scan (attack payloads). Staging is disposable.
./scripts/run-zap-scan.ps1 -Environment staging

# Production: baseline PASSIVE scan (read-only). Safe against the live store.
./scripts/run-zap-scan.ps1 -Environment production
```

The script starts ZAP, runs the scan, prints the OWASP summary, and opens the report.
Run it **from an India-based IP** — see [Cloudflare](#the-cloudflare-problem-read-this).

---

## The two scan modes, and why production is different

| | `full` (staging) | `baseline` (production) |
|---|---|---|
| **Spider + AJAX spider** | yes | yes |
| **Passive rules** (read responses) | yes | yes |
| **Active rules** (inject payloads) | **yes** | **no** |
| **Safe against a live store?** | no | yes |
| **Scheduled** | Saturday | Sunday |

**Passive** rules only inspect traffic that would have happened anyway. Nothing is
injected, no form is submitted, no data is written. That is what makes it safe to point
at a live store on a schedule.

**Active** rules attack. ZAP fires real SQLi, XSS, and command-injection payloads into
every parameter it discovered. Against `www.timepieces-d2c.example` that could:

- create junk orders in the live database,
- bill and send **real OTP SMS to real customers' phones** via Twilio,
- throw attack traffic at **Razorpay — a third party nobody has authorised us to scan**,
- degrade or take down the storefront for actual shoppers.

So **production is baseline-only by design**, and that decision is enforced in three
independent places, because a comment in a config file is not a control:

1. **`production.properties`** ships `zap.scan.mode=baseline` and `zap.allow.prod.active=false`.
2. **`OwaspTop10ZapScanTest.assertProductionIsNotBeingAttacked`** throws before a single
   packet is sent if anyone asks for `env=production` + `mode=full`. It is a hard failure,
   not a silent downgrade — quietly "fixing" a misconfiguration hides it until the day
   someone also sets the override.
3. **`security-zap-scan.yml`** rejects the combination at the workflow level, so a
   mistyped manual dispatch dies before ZAP even starts.
4. **`run-zap-scan.ps1`** refuses the same combination locally.

Overriding all of that requires `-Dzap.allow.prod.active=true`, deliberately typed. Do not
set it without **written sign-off from the store owner and an agreed maintenance window**.

### Endpoints never attacked, even on staging

`zap.exclude.regex` is applied to the spider **and** the active scanner (ZAP treats them as
separate components — excluding from one does *not* exclude from the other):

```
.*/api/request-token.*|.*otp.*|.*razorpay.*|.*payment.*|.*logout.*
```

OTP endpoints cost real money and send real SMS. Razorpay is someone else's system.
Production additionally excludes `/checkout` and `/cart`.

---

## The Cloudflare problem (read this)

> **A green run from a GitHub-hosted runner does not mean "no vulnerabilities". It means
> the scan never reached Timepieces.**

GitHub's runners sit outside India, and Cloudflare challenges them. `cf-bypass-check`
proved that **even with the `x-automation-bypass` header the response is HTTP 403
`cf-mitigated: challenge`** (see `docs/okf/known-issues.md`, ISSUE-001). ZAP will happily
spider and scan Cloudflare's *"Just a moment..."* interstitial and report zero findings.

For a functional test suite, a Cloudflare-blocked run is obvious noise — it goes red. For a
**security** scan it is worse than useless: it looks like a clean bill of health. This is
tracked as **BLK-007**.

**The fix — one repo variable, no code change:**

1. Provision a self-hosted runner on an India-based IP.
2. Set the repository variable **`ZAP_RUNNER`** to its label
   (*Settings → Secrets and variables → Actions → Variables*).

`runs-on: ${{ vars.ZAP_RUNNER || 'ubuntu-latest' }}` picks it up and the scans become
real. Until then, the workflow emits a `::warning::` on every run when it detects a
challenge, so the situation is visible in the log rather than inferred from a suspiciously
clean report.

Local and Docker runs from India are **not** challenged, so `run-zap-scan.ps1` produces
genuine results today.

---

## Schedule

| Day | Cron (UTC) | IST | Target | Mode |
|---|---|---|---|---|
| Saturday | `0 18 * * 6` | Sat 23:30 | `staging.timepieces-d2c.example` | `full` (active) |
| Sunday | `0 18 * * 0` | Sun 23:30 | `www.timepieces-d2c.example` | `baseline` (passive) |

18:00 UTC is deliberate: +5:30 keeps the run on the **same day** in IST, so "the Saturday
scan" means Saturday to everyone reading the report. It is also off-peak for the store.

Manual runs: *Actions → security-zap-scan → Run workflow*, choosing environment and mode.

---

## How it works

```
docker-compose.yml            ZAP daemon (compose profile: security), port 8090
        │
        │  REST (Rest-Assured, no extra dependency)
        ▼
ZapScanEngine.java            spider / ajax spider / passive wait / active scan / alerts
        │
        ▼
OwaspCategory.java            classifies each alert into an OWASP Top 10 2021 bucket
        │
        ▼
OwaspTop10ZapScanTest.java    one @Test per category + an UNMAPPED report-only test
        │
        ▼
target/zap/                   zap-report.html · zap-alerts.json · owasp-top10-summary.txt
Allure                        same three, attached to the run
```

The scan runs **once** in `@BeforeClass` (it takes minutes); the ten category tests are
cheap assertions over its in-memory results. That is why the suite is `parallel="none"`.

### How an alert becomes an OWASP category

ZAP reports *"Content Security Policy Header Not Set"*, not *"A02"*. Two strategies, in order:

1. **ZAP's own tags** (preferred) — ZAP stamps alerts with `OWASP_2025_A02`. ZAP's
   maintainers curate these, so they beat any table we hand-maintain. **Measured coverage:
   237 of 239 alert instances (99.2%)** on a real staging scan.
2. **CWE ID fallback** — for the ~1% with no tag, matched against the category's CWE set.

If both miss, the alert lands in **UNMAPPED**, which is reported but never fails. An alert
we cannot classify must stay visible; "we didn't understand it" is not "it's not a problem".

**Why tags win — a real example.** *Sub Resource Integrity Attribute Missing* reports
`cweid: 345`, which OWASP's published table maps to **A08**. But ZAP tags it
`OWASP_2021_A05`. Tag-first files it as A05 — ZAP's own answer. A CWE-only implementation
would have put all ten Medium findings in the wrong category.

---

## OWASP edition: 2025 (default) or 2021

```properties
zap.owasp.edition=2025    # or 2021
```

ZAP 2.17 tags every alert with **both** editions simultaneously — one alert carries
`OWASP_2021_A05` *and* `OWASP_2025_A02`. Because classification is tag-driven, supporting
both costs little more than a prefix switch, and **measured tag coverage is identical**
(237/239 either way), so the choice costs no accuracy. 2021 is kept because audit and
compliance paperwork still cites it. An unrecognised value fails loudly rather than
silently picking a default — a typo here would quietly rewrite the taxonomy the whole
report is built on.

Verified side by side on the same staging scan (239 alerts, 2 UNMAPPED in both):

| Finding | 2021 | 2025 |
|---|---|---|
| CSP / SRI / anti-clickjacking (10 Medium) | A05 Security Misconfiguration | **A02** Security Misconfiguration |
| Cookie / info-disclosure (80 Low) | A01 Broken Access Control | A01 Broken Access Control |

### What actually changed in 2025

Verified against [owasp.org/Top10/2025](https://owasp.org/Top10/2025/) **and** the reference
URLs ZAP ships inside its own tags:

| 2025 | Title | Came from |
|---|---|---|
| A01 | Broken Access Control | A01:2021 **+ SSRF (A10:2021)** — CWE-918 now lives here |
| A02 | Security Misconfiguration | A05:2021 — the biggest climber |
| A03 | Software Supply Chain Failures | A06:2021 "Vulnerable and Outdated Components", broadened |
| A04 | Cryptographic Failures | A02:2021 |
| A05 | Injection | A03:2021 |
| A06 | Insecure Design | A04:2021 |
| A07 | Authentication Failures | A07:2021, renamed |
| A08 | Software **or** Data Integrity Failures | A08:2021, note the "or" |
| A09 | Security Logging and Alerting Failures | A09:2021, renamed |
| A10 | Mishandling of Exceptional Conditions | **new in 2025** |

Rather than hand-copy CWE tables and let them drift, each 2025 category **declares the 2021
categories it descends from** and inherits their CWE sets — so `A01` taking both `A01` and
`A10` *is* the SSRF merge, expressed in code. Those inherited sets are a **documented
approximation used only for the ~1% fallback path**, not a transcription of OWASP's
published 2025 CWE lists. `A10:2025` has no lineage (it is new), so it relies on tags alone.

### Pass/fail

`zap.fail.on.risk` (default `High`) sets the bar. Anything at or above it fails that
category's test; everything below is still attached and logged, just tolerated. Silent
low-risk findings are how a Medium becomes a High next quarter.

---

## False positives and alert filters

**Shopify GIDs look exactly like credit card numbers to ZAP.**

The first production baseline raised **18 High "PII Disclosure" alerts**. Every one was a
false positive, confirmed by fetching the pages:

| Flagged "card number" | What it actually is |
|---|---|
| `36474036551724` | `gid://shopify/MediaImage/36474036551724` |
| `574019469356` | `gid://shopify/Article/574019469356` |

ZAP's PII Disclosure rule (plugin **10062**) finds card numbers by matching long digit runs
and Luhn-checking them. Shopify's numeric GIDs are Luhn-valid digit strings that land
squarely in card-number ranges — `36…` collides with the Diners Club prefix. The rule only
ever sees the bare number, never the surrounding `gid://shopify/…`, so **on a Shopify
storefront it cannot tell them apart.** This is structural, not a tuning accident.

Left alone, the Sunday production scan would fail every single week on those false
positives — and a scan that cries wolf is a scan that gets switched off. So rule 10062 is
demoted via `zap.alert.filters`.

```properties
# ruleId|newLevel|urlRegex   (semicolon-separated for multiple)
# newLevel: -1 = False Positive (hidden) | 0 = Informational | 1 = Low | 2 = Medium | 3 = High
zap.alert.filters=10062|0|
```

**Demoted (0), not hidden (-1), on purpose.** The findings stay in the report where a human
can review them; they just stop failing the build. **Trade-off, eyes open:** a genuine card
number leaked into a response would now also be reported as Informational rather than
failing. If PII leakage becomes a live concern, review those entries by hand or scope the
filter with a `urlRegex`.

Filters only affect alerts raised *after* they are registered, so they are applied before
any scanning starts.

> **Gotcha: alert filters strip an alert's tags.** A demoted alert comes back with no
> `OWASP_2021_Axx` tag, so it is classified by the **CWE fallback** instead. That is why the
> demoted PII findings move from A04 (ZAP's tag) to A01 (CWE-359's published mapping), and
> it is a concrete reason the CWE fallback exists — without it, all 18 would have landed in
> UNMAPPED.

If the ZAP daemon is not running the suite **skips** rather than fails — it is opt-in
infrastructure, and a developer running the whole suite should not get a red build for a
service they never asked for.

---

## Config reference

Keys live in `src/test/resources/config/{staging,production}.properties` and follow the
usual 3-tier precedence (`-D` → env var → properties file).

| Key | Staging | Production | Meaning |
|---|---|---|---|
| `zap.api.url` | `http://localhost:8090` | same | ZAP daemon REST endpoint |
| `zap.api.key` | `timepieces-zap-dev-key` | same | shared secret; override via `ZAP_API_KEY` |
| `zap.target.url` | `https://staging.timepieces-d2c.example` | `https://www.timepieces-d2c.example` | what gets scanned |
| `zap.scan.mode` | `full` | `baseline` | active vs passive |
| `zap.allow.prod.active` | — | `false` | the interlock override; leave false |
| `zap.spider.max.duration.minutes` | `5` | `5` | crawl budget |
| `zap.ajax.spider.enabled` | `true` | `true` | browser-driven crawl for JS routes |
| `zap.active.scan.max.duration.minutes` | `25` | `15` (unused) | attack budget |
| `zap.fail.on.risk` | `High` | `High` | fail threshold |
| `zap.exclude.regex` | OTP/Razorpay/payment | + checkout/cart | never crawled or attacked |
| `zap.alert.filters` | `10062\|0\|` | `10062\|0\|` | re-grade known false positives |
| `zap.owasp.edition` | `2025` | `2025` | which Top 10 to report against (`2021` or `2025`) |
| `zap.ajax.spider.browser` | `firefox-headless` | same | must be headless **and** present in the image |

---

## Gotchas worth knowing

**The ZAP port must be identical inside and outside the container (`8090:8090`).**
ZAP serves its REST API *and* its HTTP proxy on one port, and tells them apart by whether
the request's `Host` matches its own listening address:port. With a rewriting map like
`8090:8080`, the client sends `Host: localhost:8090` while ZAP listens on `8080`; ZAP does
not recognise itself, treats the API call as traffic to **proxy**, and forwards it to
`localhost:8090` inside the container where nothing is listening. The symptom is a baffling
HTTP 502 *"Connection refused"* emitted by ZAP itself.

**`zap-alerts.json` totals will not match the summary, and that is correct.**
ZAP's JSON report groups by rule and caps the `instances` it lists (5 per rule), so summing
it undercounts. Verified on staging: the report listed 36 instances while ZAP's own
`alertsSummary` reported **204** (High 0 / Medium 10 / Low 178 / Info 16) — which is what
the suite reports, because it reads one record per instance. Trust the summary; treat the
JSON report as a digest.

**`testng-security.xml` comments cannot contain `--`.** XML forbids it, and Surefire dies
with a `SAXParseException` that names a line number but not the reason. This is why the
suite file describes the compose profile instead of pasting the `--profile` flag.

**The AJAX spider needs `inScope=false`, counter-intuitive as that reads.** "In scope"
means "inside a ZAP *Context*", and this suite defines no Context — so `inScope=true` makes
ZAP reject its own starting URL. The API returns a bare `{"code":"internal_error"}`; the
real reason (*"The starting URI is not in scope"*) appears only in the container log
(`docker logs timepieces-zap`). `subtreeOnly=true` is what actually keeps the crawl
constrained to the target.

**The AJAX spider's browser must exist in the image.** `zaproxy/zap-stable` ships **Firefox
ESR but not Chrome**, so `chrome-headless` fails (same opaque `internal_error`). Hence
`zap.ajax.spider.browser=firefox-headless`. It must also be headless — the container has no
X server. The AJAX spider is worth the trouble: on staging it took the alert count from 239
to 295 by reaching JS-rendered routes the plain spider never sees.

---

## Baseline findings (2026-07-17)

Both verified locally from an India IP, so these are real results, not Cloudflare pages.

### Staging — 49 URLs, 204 alert instances, **no High findings**

ZAP's own `alertsSummary` agreed exactly: High 0 / Medium 10 / Low 178 / Info 16.

| Risk | CWE | Alert |
|---|---|---|
| Medium | 345 | Sub Resource Integrity Attribute Missing |
| Medium | 693 | Content Security Policy (CSP) Header Not Set |
| Medium | 1021 | Missing Anti-clickjacking Header |
| Low | 319 | Strict-Transport-Security Header Not Set |
| Low | 497 | Timestamp Disclosure - Unix |
| Low | 693 | X-Content-Type-Options Header Missing |
| Info | 525 | Re-examine Cache-control Directives |

### Production — 285 URLs, ~4,400 alert instances, **no High findings after filtering**

| Category | High | Medium | Low | Info |
|---|---|---|---|---|
| A01 Broken Access Control | 0 | 0 | 1736 | 19 |
| A05 Security Misconfiguration | 0 | 1450 | 571 | 0 |
| A07 Identification & Auth Failures | 0 | 0 | 0 | 474 |

The 18 High "PII Disclosure" alerts in the *unfiltered* run were all Shopify GID false
positives — see [False positives](#false-positives-and-alert-filters).

Everything remaining is **configuration-level hardening** (missing CSP, HSTS,
anti-clickjacking, and `X-Content-Type-Options` headers; SRI attributes) rather than an
exploitable flaw. That is the expected shape of a first baseline: high instance counts
because one missing header is re-reported on every URL, not many distinct problems. The
distinct-rule count is what matters, and it is small.

The counts drift a little run to run (4,252 vs 4,442) because the storefront's content and
the spider's reach vary — read the rules, not the totals.

---

## Possible next steps (not built)

- **Authenticated scanning.** The big one. ZAP's spider only sees what an anonymous
  visitor sees, so anything behind login is currently unscanned. This framework already
  knows the OTP login flow — proxy the existing Selenium/Rest-Assured suites through ZAP
  (`-Dzap.proxy=true`) and every authenticated journey seeds ZAP's site tree with traffic
  a spider could never reach. This is the single highest-value addition, and the reason
  hosting ZAP *inside* this framework beats a standalone ZAP job.
- **Excel export**, matching the search suites' reports, via `ExcelReportUtil`.
