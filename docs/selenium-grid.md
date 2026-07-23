# Selenium Grid (self-hosted)

Real cross-browser execution with **no browsers installed on the host**, scaled by adding
containers rather than buying cloud minutes.

---

## TL;DR

```powershell
docker compose --profile grid up -d                       # hub + chrome/firefox/edge nodes
.\mvnw.cmd test "-Dexecution.mode=GRID" "-Dbrowser=firefox" "-Denv=production"
docker compose --profile grid down                        # stop when finished
```

Watch a live session at **http://localhost:4444/ui** (VNC password: `secret`).

---

## What this replaced, and why it was worth doing

Before 2026-07-23 the framework had **no Grid at all**, and the configuration actively
misled you about it:

| Claim | Reality before |
|---|---|
| `execution.target=local \| grid \| browserstack` in both property files | **Dead key — read by nothing.** Setting `grid` silently did nothing. |
| A Grid exists | No hub, no nodes, nothing in `docker-compose.yml` |
| BrowserStack support | 4 config keys, **0** code references |
| Remote mode does cross-browser | `execution.mode=CLOUD` **always built `ChromeOptions`** — "cross-browser in the cloud" ran Chrome three times |

The dead `execution.target` key was the worst part: a new engineer would reasonably set
`execution.target=grid`, watch tests run locally, and have no idea why. It has been
**removed**, and the key the code actually reads (`execution.mode`) is now present and
documented in both property files.

---

## Execution modes

`execution.mode` decides **where the browser runs**. Everything else — tags, suites, env,
headless — behaves identically across modes.

| Mode | Where the browser runs | Honours `-Dbrowser`? |
|---|---|---|
| `local` *(default)* | A browser installed on this machine | yes |
| `GRID` | The self-hosted Grid below | **yes** |
| `CLOUD` | LambdaTest (POC) | **no — always Chrome.** See the caveat below |

```powershell
.\mvnw.cmd test "-Dexecution.mode=GRID" "-Dbrowser=edge" "-Denv=production"
```

Precedence for the hub URL: `-Dgrid.hub.url` → `GRID_HUB_URL` env var → `grid.hub.url` in
the properties file → `http://localhost:4444/wd/hub`.

> **CLOUD mode is still a POC and is knowingly left that way.** It is LambdaTest-shaped
> (`LT:Options`, hardcoded `Windows 11` / `latest` / build name) and ignores `-Dbrowser`.
> It was left alone deliberately: fixing it properly means a provider abstraction, and
> that was not a change to make days before handover. **Use GRID for cross-browser.**

---

## The Grid itself

Four services in `docker-compose.yml`, all behind the **`grid` compose profile** so they
never start with a plain `docker compose up` — the same convention as the ZAP service.

| Service | Image | Notes |
|---|---|---|
| `selenium-hub` | `selenium/hub:4.44` | ports 4442/4443 (event bus), 4444 (WebDriver + UI) |
| `selenium-node-chrome` | `selenium/node-chrome:4.44` | 2 sessions, 1920x1080 |
| `selenium-node-firefox` | `selenium/node-firefox:4.44` | 2 sessions, 1920x1080 |
| `selenium-node-edge` | `selenium/node-edge:4.44` | 2 sessions, 1920x1080 |

**`shm_size: 2gb` on every node is not optional.** Docker gives a container 64MB of
`/dev/shm` by default, which Chrome exhausts and then dies with tab crashes that read
exactly like random test flake. This is the documented remedy and it removes a whole class
of phantom failures.

### Scaling

Add **replicas**, not sessions-per-node:

```powershell
docker compose --profile grid up -d --scale selenium-node-chrome=3
```

`SE_NODE_MAX_SESSIONS=2` is deliberately conservative. Raising it past what the container's
CPU and RAM can serve makes runs **slower and flakier**, not faster — a starved browser is
indistinguishable from a flaky application, and you will spend a day chasing the wrong bug.

---

## Cross-browser: run it three times, not once

```powershell
docker compose --profile grid up -d
.\mvnw.cmd clean test "-Dexecution.mode=GRID" "-Dbrowser=chrome"  "-Denv=production"
.\mvnw.cmd test       "-Dexecution.mode=GRID" "-Dbrowser=firefox" "-Denv=production"
.\mvnw.cmd test       "-Dexecution.mode=GRID" "-Dbrowser=edge"    "-Denv=production"
.\mvnw.cmd allure:report
```

**`clean` only on the first line** — Allure results accumulate in `target/allure-results/`,
so all three browsers merge into one report.

This sequential form is the **simplest** option and still perfectly valid — three isolated
JVMs, no shared state at all.

If you want the three browsers running **concurrently in one JVM**, that now works too —
see [Parallel cross-browser in ONE JVM](#parallel-cross-browser-in-one-jvm-dec-0014) at the
end of this document. It needed a per-thread browser override because `DriverFactory`
otherwise resolves the browser from a JVM-global system property, which three concurrent
`<test>` blocks would race on.

---

## Browser substitutions (logged, never silent)

`resolveGridBrowser()` maps the configured browser onto one the Grid can serve:

| Requested | On the Grid | Why |
|---|---|---|
| chrome / firefox / edge | as-is | real nodes exist |
| **brave** | chrome | no Brave node; Brave *is* Chromium — the difference is shields and branding, which these tests do not exercise |
| **safari** | `safari.fallback.browser` | no WebKit node exists; falls back the same way the local path does, so behaviour is consistent across modes |

Both substitutions are **logged**, and Safari's at WARN. A report that quietly claims Safari
coverage it does not have is worse than one that admits the gap — see the Playwright/WebKit
plan in the OKF for the real fix.

---

## Verified

Against production on 2026-07-23, `testng-homepage-smoke.xml`:

| Browser | Result |
|---|---|
| chrome | **4/4** |
| firefox | **4/4** |
| edge | **4/4** |

And independently confirmed it was genuinely cross-browser rather than Chrome three times —
each node serves **only** its own browser, and all three reported sessions:

```
chrome  node: 8 session(s)
firefox node: 8 session(s)
edge    node: 8 session(s)
```

That check matters specifically because the pre-existing CLOUD path failed exactly this way:
it reported success while running Chrome every time.

---

## Troubleshooting

**"GRID driver initialization failed. Is the hub up?"** — the hub is not running or is not
reachable. `docker compose --profile grid up -d`, then open http://localhost:4444/ui.

**Hub is up but sessions queue forever** — no node matches the requested browser. Check the
UI: the requested `browserName` must appear in the registered stereotypes.

**Chrome tabs dying mid-test** — `/dev/shm` exhaustion. Confirm `shm_size: 2gb` is still on
the node service.

**Everything is slower than local** — expected for a handful of tests: you are paying
container startup and a network hop. The Grid wins on *breadth* (three browsers, no local
installs) and *scale* (add replicas), not on latency for a single small run.

---

## Parallel cross-browser in ONE JVM (DEC-0014)

`testng-crossbrowser.xml` runs chrome, firefox and edge **concurrently** instead of three
sequential Maven invocations:

```powershell
docker compose --profile grid up -d
.\mvnw.cmd test "-Dsurefire.suite=testng-crossbrowser.xml" "-Dexecution.mode=GRID" `
                "-Denv=production" "-Dcucumber.filter.tags=not @StagOnly"
```

### How the browser reaches the driver

`parallel="tests"` gives each `<test>` block its own thread and a `browser` parameter.
`BaseRunner.pinBrowserForThread()` reads it in `@BeforeMethod` and stores it in a
**ThreadLocal** that `DriverFactory` prefers over the JVM-wide `-Dbrowser`.

That indirection is the entire point. The browser normally comes from a **JVM-global**
system property, so three blocks writing it would race — last writer wins and all three
would quietly run the same browser while the suite file claimed otherwise. This was
deferred once for exactly that reason, and it is the failure the CLOUD branch already had.

It is safe because TestNG runs `@BeforeMethod` on the **same thread** as its `@Test`, even
under a parallel DataProvider and even when the pool reuses threads — verified empirically
before the code was written, not assumed.

### Thread budget

3 blocks × `data-provider-thread-count=2` = **6 concurrent browsers**, which exactly matches
Grid capacity (3 nodes × `SE_NODE_MAX_SESSIONS=2`). Asking for more makes sessions queue and
time out: an early run at 3 threads/block (9 concurrent) produced extra failures that
vanished at 2. **Add node replicas, not threads.**

### Verified

Do not trust a green run — the failure mode is a silent wrong answer. Two checks:

1. **Grid node sessions** (each node serves only its own browser):
   `chrome 12 · firefox 12 · edge 12` — all three genuinely ran.
2. **Parity against sequential.** Same scenarios run one browser at a time produced the
   *same* failures:

   | | Sequential | Parallel |
   |---|---|---|
   | Firefox failures | 3 | 3–4 |
   | Reasons | Pending scaffold · InvalidCookieDomain · new-tab `[Legal]` | identical |

   All three are **pre-existing** — the first is a deliberate TODO, the other two were in
   the original failure list before any of this work. Parallelism introduced none of them.

**Expect Firefox to fail slightly more than Chrome.** That is cross-browser testing doing
its job, not a defect in the runner.

### Rollback

Delete `testng-crossbrowser.xml`. The ThreadLocal in `DriverFactory` goes inert because
nothing sets it, and every other suite behaves exactly as before.
