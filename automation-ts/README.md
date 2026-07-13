# Timepieces Automation — TypeScript/Playwright Migration Template

This is a **proof-of-concept and migration template**, not a full port of the
Java framework. It exists to answer one question concretely: *if we move this
framework from Selenium/Java to Playwright/TypeScript, what does the pattern
look like, and does it actually work?*

It ports one full vertical slice — page objects, a BDD feature end to end, and
one API test — and **all of it has been run live against real staging** (see
[Validated results](#validated-results) below). Everything else (the other
~100 page objects, ~99 remaining API cases, Allure reporting, per-environment
config) is deliberately left out; porting them is repetition of the patterns
shown here, not new design work.

## Why this exists

The Java framework's page objects use a CRTP self-type generic
(`BasePage<SELF extends BasePage<SELF>>`) so that inherited methods like
`open()` return the concrete page type instead of a bare `BasePage`. That was
necessary in Java. **TypeScript does not need it** — a method typed to return
`Promise<this>` resolves to the caller's concrete class automatically. That
single fact is why the class hierarchy here is shorter and simpler than its
Java counterpart at every level.

## Quick start

```bash
cd automation-ts
npm install
npx playwright install chromium
cp .env.example .env        # then fill in STAGING_PASSWORD
```

```bash
npm run typecheck       # tsc --noEmit
npm run test:bdd:dry    # Cucumber step-matching check, no browser/network
npm run test:bdd        # runs Global_Footer_Navigation.feature live
npm run test:tdd:list   # Playwright test discovery, no network
npm run test:tdd        # runs the search API test live
npm run test:all        # typecheck + bdd + tdd, in that order
```

## Validated results (2026-07-05)

Every command above was actually run against `https://staging.timepieces-d2c.example`
from this environment, not just compiled:

- `tsc --noEmit` — **0 errors** across all 11 source files.
- `cucumber-js --dry-run` — all 4 Gherkin steps matched their step definitions
  (0 undefined, 0 ambiguous).
- `cucumber-js` (live) — **full pass**: staging password gate detected and
  unlocked, homepage loaded, footer visibility confirmed, all 5 policy links
  confirmed visible, and all 5 correctly opened their target page in a new
  tab with the expected URL.
- `playwright test` (live) — the search API test passed against the real
  `/api/request-token` → `/api/search` flow.

**Caveat:** this does **not** prove the CI Cloudflare blocker (tracked
separately for the Java framework) is resolved. That block is specific to
GitHub-hosted runner IPs tripping Cloudflare's bot heuristics — this was run
from a different network path. Treat "works here" as "the pattern is sound,"
not "CI will be green."

## Architecture: Java → TypeScript

| Concept | Java | TypeScript | Notes |
|---|---|---|---|
| Fluent self-return | `BasePage<SELF extends BasePage<SELF>>` | `abstract class BasePage` + methods return `Promise<this>` | No generic parameter anywhere in the TS hierarchy. |
| Page composition | `StoreFrontBasePage extends HeaderOnlyBasePage<SELF>` | `StoreFrontBasePage extends HeaderOnlyBasePage` | Same shape, minus the generic threading. |
| Product-family template | `SharedTemplate_Monarch<SELF extends SharedTemplate_Monarch<SELF>>` | `abstract class SharedTemplate_Monarch extends StoreFrontBasePage` | See `src/pages/web/products/timepieces/monarch/`. |
| Locators | `private final By x = By.cssSelector(...)` | `private readonly x = "css-selector-string"` | Passed straight into `page.locator()`. |
| Wait/click/input helpers | `getElement`, `clickElement`, `sendKeys`, `getText`, `isDisplayed` | Same names, same signatures, all `async` | Direct 1:1 port target when porting a page's BUSINESS LIBRARY section. |
| Config | `ConfigReader.get("key")` (properties files + `-D` overrides) | `ConfigReader.get("KEY")` (`.env` + real env vars) | Scope-cut: no per-env `.properties` layering yet — see below. |
| Driver lifecycle | `DriverFactory` (`ThreadLocal<WebDriver>`) + TestNG `@BeforeTest`/`@AfterTest` | `step-definitions/hooks.ts` (`BrowserContext` per scenario) | A fresh `BrowserContext` gives the same isolation `ThreadLocal` gives; Cucumber's `--parallel N` is the analogue of TestNG's parallel data provider. |
| New-tab handling | `WindowUtils` (manual window-handle capture/switch/close) | `context.waitForEvent('page')` | Genuinely simpler in Playwright — an event replaces manual handle bookkeeping. Watch for the `about:blank` timing quirk documented inline in `footer.steps.ts`. |
| BDD runner | Cucumber-JVM + TestNG (`SmokeRunner`/`RegressionRunner`) | `@cucumber/cucumber` (cucumber-js) | **Feature files need zero changes** — `Global_Footer_Navigation.feature` here is byte-for-byte the Java one. |
| API tests | Rest Assured + TestNG | `@playwright/test` (`request` fixture) | One package covers both browser E2E and API testing. |

## What's ported (in full)

- `src/pages/BasePage.ts` — full helper vocabulary (getElement, clickElement,
  sendKeys, getText, getAttribute, isDisplayed, isMobileViewport, open()).
- `src/pages/HeaderOnlyBasePage.ts`, `StoreFrontBasePage.ts` — the same
  two-level composition as Java, minus generics.
- `src/pages/components/FooterComponent.ts` — full business logic for the
  links/accordion/copyright/new-tab surface exercised by the ported feature.
- `src/pages/web/HomePage.ts`, `StagingGatePage.ts` — load-confirmation and
  password-gate logic, both exercised live.
- `src/pages/web/otherPages/UpdatesPage.ts` — the exact `open()` fluent-chain
  example from the framework's own convention doc.
- `src/pages/web/products/timepieces/monarch/` — `SharedTemplate_Monarch` +
  `MonarchSilverBlack`, demonstrating the product-family template pattern.
- `features/Global_Footer_Navigation.feature` + `step-definitions/footer.steps.ts`
  — full BDD scenario, run live.
- `tests/api/search.api.spec.ts` — one representative API case, run live.

## What's deliberately NOT ported yet

Don't mistake these gaps for oversights — they're out of scope for a
proof-of-concept and are the actual remaining migration work:

1. **The other ~100 Java page objects** (all the PDP variants, cart, checkout,
   account, orders). Porting each is: create the TS class extending the right
   `SharedTemplate_*`/base, copy the `PAGE_PATH` constant into
   `getPagePath()`, port the locators, port the BUSINESS LIBRARY methods
   1:1 using the same helper names.
2. **The remaining ~99 API test cases** in `SearchApiTest.java` (product
   matrix, collections, brand/misspellings, deep keywords, edge cases). The
   `performSearch()` helper in `search.api.spec.ts` already does the token→search
   flow — the rest is just more `test()` blocks with different keywords/expectations.
3. **Per-environment config layering.** Java's `ConfigReader` loads
   `config/<env>.properties` + a gitignored `.local.properties` override. This
   POC only reads `.env`/real env vars. If multiple environments are needed,
   add `.env.staging`, `.env.production`, etc., and pass the target via
   `dotenv.config({ path: `.env.${process.env.TARGET_ENV}` })`.
4. **Allure reporting.** Java uses `allure-testng` + `allure-cucumber7-jvm`.
   The TS equivalents are `allure-playwright` (for the `@playwright/test` side)
   and `allure-cucumberjs` (for the BDD side) — neither is wired in here.
5. **OTP/Twilio flow, retry-analyzer, rate-limit handling.** The Java
   framework has real, hard-won logic here (`OtpRateLimitException`, a
   typed retry-skip). Don't attempt to re-derive it from scratch — read
   `ci-cloudflare-blocker` project notes and the Java `RetryAnalyzer`/
   `LoginPage` first, then port deliberately.
6. **Parallel execution tuning.** `cucumber-js --parallel N` and
   Playwright's own worker count both exist but aren't tuned here.

## How to port your next page object

1. Pick the Java file. Note its parent (`BasePage`/`HeaderOnlyBasePage`/
   `StoreFrontBasePage`/a `SharedTemplate_*`) and its `PAGE_PATH` constant.
2. Create the `.ts` file in the equivalent path under `src/pages/`, extending
   the **TypeScript** version of that same parent — no generic type argument.
3. Copy each `By.cssSelector(...)`/`By.xpath(...)` locator to a plain string
   field of the same name.
4. Port each BUSINESS LIBRARY / VERIFICATION method: same name, same
   parameters, add `async`/`await`, replace the Java helper call with its TS
   equivalent from the table above.
5. If the page has a real endpoint, override `getPagePath()` (and
   `getPageTitle()` if used) — same as Java's `@Override protected String
   getPagePath()`.
6. Run `npm run typecheck` before wiring it into a step definition or spec.

## Decision this POC settled: keep Cucumber for BDD

The alternative was dropping Gherkin and using Playwright's own test runner
with tags for BDD-style scenarios. This POC keeps `@cucumber/cucumber`
instead, because:

- All 14 existing `.feature` files port with **zero changes** — only the
  step-definition implementation language changes.
- It was proven to work live (see Validated results) with no framework-level
  friction beyond two default-timeout tweaks (documented inline in
  `step-definitions/hooks.ts`).
- The team keeps the same "business-readable scenario, engineer-owned steps"
  split they already have.

If a future migration phase wants to reconsider this, the tradeoff is:
Playwright's own runner gives built-in parallelism/trace-viewer/HTML reports
without a second tool in the stack, at the cost of losing Gherkin's
plain-English scenario format.
