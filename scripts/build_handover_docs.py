#!/usr/bin/env python3
"""
Builds the Timepieces automation-framework HANDOVER GUIDES as self-contained HTML.

WHY THIS SCRIPT EXISTS
----------------------
The handover is a set of ~20 topic guides (setup, how-to-run, locator strategy, the OKF,
the dashboard, requirements, ...). Rather than hand-write 20 HTML files with 20 copies of
the same CSS, every guide is authored here as an HTML body string and wrapped in one shared
shell. Edit a body below, re-run, commit. Each output file is FULLY self-contained (CSS is
inlined), so any single guide can be opened on its own or converted to PDF without assets.

USAGE
-----
    py scripts/build_handover_docs.py

No third-party dependencies — this is plain string templating, unlike build_cheatsheet.py
(which needs PyMuPDF because it also renders PDF). These guides are HTML only; the reader
converts to PDF themselves if they want it.

OUTPUT
------
    output/handover/index.html          navigation hub, grouped by section
    output/handover/<slug>.html         one file per guide
"""

import pathlib
import re

REPO_ROOT = pathlib.Path(__file__).resolve().parent.parent

# Escape plain-text fields (title, subtitle, section) for HTML WITHOUT double-escaping any
# entity that is already present (e.g. a title that already contains "&amp;"). Never applied
# to the doc bodies — those are hand-authored raw HTML and must pass through untouched.
_ENTITY = re.compile(r"&(?!(#\d+|#x[0-9a-fA-F]+|[a-zA-Z][a-zA-Z0-9]*);)")
def esc(text):
    return _ENTITY.sub("&amp;", text).replace("<", "&lt;").replace(">", "&gt;")
OUT_DIR = REPO_ROOT / "output" / "handover"
OUT_DIR.mkdir(parents=True, exist_ok=True)

UPDATED = "23 Jul 2026"

# --------------------------------------------------------------------------------------
# Shared shell: one stylesheet, inlined into every page so each file stands alone.
# Deliberately light-theme and print-friendly — the reader converts these to PDF.
# --------------------------------------------------------------------------------------
CSS = """
:root {
  --ink: #1a1d24; --muted: #5a6472; --line: #e2e6ec; --bg: #ffffff;
  --accent: #7a1f2b; --accent-soft: #f6eced; --code-bg: #f5f7fa; --code-ink: #26303b;
  --note: #0b6bcb; --note-bg: #eef5fd; --warn: #9a6700; --warn-bg: #fdf6e3;
  --danger: #a3271f; --danger-bg: #fbeceb; --ok: #216e39; --ok-bg: #eaf5ee;
}
* { box-sizing: border-box; }
html { -webkit-text-size-adjust: 100%; }
body {
  margin: 0; color: var(--ink); background: var(--bg);
  font: 16px/1.62 -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
}
.wrap { max-width: 900px; margin: 0 auto; padding: 0 28px 96px; }
header.doc {
  border-bottom: 3px solid var(--accent); padding: 40px 0 22px; margin-bottom: 8px;
}
header.doc .kicker {
  text-transform: uppercase; letter-spacing: .14em; font-size: 12px; font-weight: 700;
  color: var(--accent);
}
header.doc h1 { font-size: 30px; line-height: 1.15; margin: 10px 0 6px; }
header.doc .sub { color: var(--muted); font-size: 16px; margin: 0; }
header.doc .meta { color: var(--muted); font-size: 13px; margin-top: 14px; }
header.doc .meta a { color: var(--accent); text-decoration: none; }
h2 { font-size: 22px; margin: 40px 0 10px; padding-bottom: 6px; border-bottom: 1px solid var(--line); }
h3 { font-size: 18px; margin: 26px 0 8px; }
h4 { font-size: 15px; margin: 20px 0 6px; text-transform: uppercase; letter-spacing: .04em; color: var(--muted); }
p, li { color: var(--ink); }
a { color: var(--accent); }
code {
  font: 13.5px/1.5 "SFMono-Regular", "Cascadia Code", Consolas, "Liberation Mono", monospace;
  background: var(--code-bg); color: var(--code-ink); padding: 1.5px 5px; border-radius: 4px;
}
pre {
  background: var(--code-bg); color: var(--code-ink); border: 1px solid var(--line);
  border-radius: 8px; padding: 14px 16px; overflow-x: auto; font-size: 13px; line-height: 1.55;
}
pre code { background: none; padding: 0; }
table { border-collapse: collapse; width: 100%; margin: 14px 0; font-size: 14.5px; }
th, td { border: 1px solid var(--line); padding: 8px 11px; text-align: left; vertical-align: top; }
th { background: var(--accent-soft); font-weight: 700; }
tbody tr:nth-child(even) { background: #fafbfc; }
blockquote { margin: 14px 0; padding: 2px 16px; border-left: 3px solid var(--line); color: var(--muted); }
.callout { border-radius: 8px; padding: 12px 16px; margin: 16px 0; border: 1px solid; font-size: 14.5px; }
.callout p:first-child { margin-top: 0; } .callout p:last-child { margin-bottom: 0; }
.callout .tag { font-weight: 700; text-transform: uppercase; letter-spacing: .05em; font-size: 12px; }
.note   { background: var(--note-bg);   border-color: #cfe2f6; } .note .tag { color: var(--note); }
.warn   { background: var(--warn-bg);   border-color: #f0e2b6; } .warn .tag { color: var(--warn); }
.danger { background: var(--danger-bg); border-color: #f2cdc9; } .danger .tag { color: var(--danger); }
.ok     { background: var(--ok-bg);     border-color: #c3e4cd; } .ok .tag { color: var(--ok); }
.pill { display: inline-block; font-size: 12px; font-weight: 700; padding: 2px 9px; border-radius: 20px;
  background: var(--accent-soft); color: var(--accent); }
.toc { background: #fafbfc; border: 1px solid var(--line); border-radius: 8px; padding: 14px 20px; margin: 18px 0; }
.toc h4 { margin: 0 0 6px; } .toc ul { margin: 0; padding-left: 18px; } .toc li { margin: 2px 0; }
footer.doc { margin-top: 56px; padding-top: 18px; border-top: 1px solid var(--line);
  color: var(--muted); font-size: 13px; }
.crumbs { margin: 22px 0 0; font-size: 13px; }
.crumbs a { color: var(--accent); text-decoration: none; }
/* index */
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; margin: 14px 0 30px; }
.card { border: 1px solid var(--line); border-radius: 10px; padding: 15px 17px; text-decoration: none; color: var(--ink);
  display: block; transition: border-color .15s; }
.card:hover { border-color: var(--accent); }
.card .n { font-size: 12px; font-weight: 700; color: var(--accent); }
.card .t { font-size: 16px; font-weight: 700; margin: 3px 0; }
.card .d { font-size: 13.5px; color: var(--muted); }
.section-h { margin: 26px 0 4px; font-size: 13px; text-transform: uppercase; letter-spacing: .12em; color: var(--muted); }
@media (max-width: 640px) { .grid { grid-template-columns: 1fr; } .wrap { padding: 0 16px 60px; } }
@media print {
  a { color: var(--ink); } pre, table, .callout { page-break-inside: avoid; }
  header.doc { padding-top: 0; }
}
"""

PAGE = """<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>{title} — Timepieces Automation Handover</title>
<style>{css}</style>
</head>
<body>
<div class="wrap">
{crumbs}
<header class="doc">
  <div class="kicker">Timepieces QA Automation · Handover Guide {num}</div>
  <h1>{title}</h1>
  <p class="sub">{subtitle}</p>
  <p class="meta">Framework: Java 25 · Selenium 4.44 · Cucumber 7 · TestNG 7 · Updated {updated}</p>
</header>
{body}
<footer class="doc">
  Timepieces QA Automation Framework — Handover documentation set. Generated by
  <code>scripts/build_handover_docs.py</code>; edit the body there and re-run rather than
  hand-editing this HTML. Updated {updated}.
</footer>
</div>
</body>
</html>
"""

# Each guide: (num, slug, section, title, subtitle, body_html)
DOCS = []

def doc(num, slug, section, title, subtitle, body):
    DOCS.append(dict(num=num, slug=slug, section=section, title=title, subtitle=subtitle, body=body))


# ======================================================================================
# GUIDES
# ======================================================================================

doc(17, "framework-setup", "Getting started",
    "Automation Framework — Basic Setup Guide",
    "Everything a new SDET installs and configures before the first test run.",
    r"""
<div class="toc">
  <h4>On this page</h4>
  <ul>
    <li><a href="#prereqs">1. Prerequisites</a></li>
    <li><a href="#clone">2. Clone and open</a></li>
    <li><a href="#jdk">3. JDK 25 and Maven wrapper</a></li>
    <li><a href="#secrets">4. Secrets: <code>.env</code> and local property overrides</a></li>
    <li><a href="#compile">5. First compile and smoke check</a></li>
    <li><a href="#optional">6. Optional services (Docker, Grid, ZAP, dashboard)</a></li>
    <li><a href="#trouble">7. Common setup problems</a></li>
  </ul>
</div>

<h2 id="prereqs">1. Prerequisites</h2>
<table>
  <thead><tr><th>Tool</th><th>Version</th><th>Why</th><th>Check</th></tr></thead>
  <tbody>
    <tr><td>JDK</td><td><b>25</b> (Temurin)</td><td>The framework targets Java 25; CI uses Temurin 25.</td><td><code>java -version</code></td></tr>
    <tr><td>Maven</td><td>use the <b>wrapper</b></td><td>Do not install Maven globally — <code>mvnw</code>/<code>mvnw.cmd</code> pins the exact version.</td><td><code>./mvnw -v</code></td></tr>
    <tr><td>Git</td><td>any recent</td><td>Clone, branch, history.</td><td><code>git --version</code></td></tr>
    <tr><td>Chrome</td><td>stable</td><td>Default browser the suite drives. Selenium Manager fetches the matching driver automatically.</td><td>—</td></tr>
    <tr><td>Docker Desktop</td><td>any recent</td><td><b>Optional</b> — only for Grid, ZAP, the metrics DB, or containerised runs.</td><td><code>docker --version</code></td></tr>
    <tr><td>Python 3</td><td>3.10+</td><td><b>Optional</b> — for the report-email and cheatsheet/handover generator scripts.</td><td><code>py --version</code></td></tr>
    <tr><td>Node 18+</td><td>LTS</td><td><b>Optional</b> — only to run the dashboard (<code>dashboard/</code>) or the TypeScript POC (<code>automation-ts/</code>).</td><td><code>node -v</code></td></tr>
  </tbody>
</table>
<div class="callout ok"><p><span class="tag">No driver management</span> Selenium 4's built-in Selenium Manager downloads the correct ChromeDriver/GeckoDriver/EdgeDriver on first use. You never commit a driver binary or set a <code>webdriver.*.driver</code> path.</p></div>

<h2 id="clone">2. Clone and open</h2>
<pre><code>git clone https://github.com/TimepiecesTech/qa-automation.git
cd qa-automation</code></pre>
<div class="callout warn"><p><span class="tag">Re-clone, don't pull</span> The git history was rewritten (a leaked Zephyr token was scrubbed — see the Release Notes guide). Any clone taken before that rewrite is invalid; always start from a fresh clone.</p></div>

<h2 id="jdk">3. JDK 25 and the Maven wrapper</h2>
<p>Point <code>JAVA_HOME</code> at a JDK 25 install, then always invoke Maven through the wrapper so the build is reproducible:</p>
<pre><code># Windows PowerShell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25"
.\mvnw.cmd -v

# macOS / Linux
export JAVA_HOME=$(/usr/libexec/java_home -v 25)
./mvnw -v</code></pre>

<h2 id="secrets">4. Secrets: <code>.env</code> and local property overrides</h2>
<p>Nothing secret lives in source control. There are two places real values go, both gitignored:</p>
<h4>a. <code>.env</code> at the repo root</h4>
<p>Copy the template and fill in what you actually need (email reporting, DB metrics, Cloudflare bypass, staging gate):</p>
<pre><code>cp .env.example .env      # then edit .env</code></pre>
<p><code>.env.example</code> is placeholder-only and stays committed; <code>.env</code> is ignored (<code>*.env</code> in <code>.gitignore</code>).</p>
<h4>b. <code>&lt;env&gt;.local.properties</code> on the classpath</h4>
<p><code>ConfigReader</code> layers an optional <code>src/test/resources/config/&lt;env&gt;.local.properties</code> on top of the committed base config. This is where a developer keeps machine-specific secrets — the staging gate password, or the pilot phone number for account tests — without touching committed files:</p>
<pre><code># src/test/resources/config/staging.local.properties  (gitignored)
staging.password=CHANGE_ME
pilot.phone=NNNNNNNNNN</code></pre>
<div class="callout danger"><p><span class="tag">Never commit real values</span> <code>pilot.phone</code> and <code>staging.password</code> are intentionally blank/absent in the committed files. The previous owner's real number was hardcoded once and had to be scrubbed at offboarding — do not reintroduce that pattern. See the <b>Configuration (Properties) Reader</b> guide for the full precedence rules.</p></div>

<h2 id="compile">5. First compile and smoke check</h2>
<pre><code># compile only, offline, no tests — fastest confidence check
.\mvnw.cmd -q -o test-compile

# a tiny live run: the smoke tag against production (no login, no staging gate)
.\mvnw.cmd test "-Denv=production" "-Dcucumber.filter.tags=@Smoke and not @StagOnly"</code></pre>
<p>A green compile plus a passing smoke scenario confirms the toolchain. Full-suite and per-area commands live in the <b>How-To-Run</b> guide.</p>

<h2 id="optional">6. Optional services</h2>
<table>
  <thead><tr><th>Service</th><th>Bring up</th><th>Needed for</th></tr></thead>
  <tbody>
    <tr><td>Metrics Postgres</td><td><code>docker compose up -d postgres-dev</code></td><td>Persisting run metrics (tests still pass without it — the publisher logs and moves on).</td></tr>
    <tr><td>Selenium Grid</td><td><code>docker compose --profile grid up -d</code></td><td>Cross-browser and scale. See the <b>Selenium Grid</b> and <b>Cross-Browser</b> guides.</td></tr>
    <tr><td>OWASP ZAP</td><td><code>docker compose --profile security up -d zap</code></td><td>The DAST security suite. See the <b>CI/CD</b> and security docs.</td></tr>
    <tr><td>Healenium</td><td><code>docker compose --profile healenium up -d</code></td><td>Self-healing locators (off by default). See the <b>Healenium</b> guide.</td></tr>
    <tr><td>Dashboard</td><td><code>cd dashboard &amp;&amp; ...</code></td><td>The React trends dashboard. See the <b>Dashboard Setup</b> guide.</td></tr>
  </tbody>
</table>
<div class="callout note"><p><span class="tag">Profiles matter</span> Healenium, Grid and ZAP each sit behind a Compose <em>profile</em>. A plain <code>docker compose up</code> starts only the metrics DB and Allure service. This is deliberate — a service with no profile starts with everything, which once broke the Grid command (ISSUE-021).</p></div>

<h2 id="trouble">7. Common setup problems</h2>
<table>
  <thead><tr><th>Symptom</th><th>Cause / fix</th></tr></thead>
  <tbody>
    <tr><td><code>invalid target release: 25</code></td><td><code>JAVA_HOME</code> points at an older JDK. Install/point at JDK 25.</td></tr>
    <tr><td>Cloudflare "verify you are human" on staging</td><td>GitHub-hosted runners outside India trip Cloudflare; local runs from India are fine. Set <code>CF_BYPASS_TOKEN</code>, or run against production. See <b>Known Issues</b> ISSUE-001.</td></tr>
    <tr><td><code>Connection to localhost:5432 refused</code></td><td>The metrics Postgres is not up. Harmless — the run still passes. Start it with <code>docker compose up -d postgres-dev</code> to silence it.</td></tr>
    <tr><td>A staging password prompt on a production run</td><td>You are actually on staging. Confirm <code>-Denv</code> is honoured (ISSUE-020 fixed this; the CLI now wins over the suite XML).</td></tr>
  </tbody>
</table>
""")
doc(18, "framework-how-to-run", "Getting started",
    "Automation Framework — How-To-Run Guide",
    "The commands that actually run the suite, locally, in Docker and in CI.",
    r"""
<div class="callout note"><p><span class="tag">Companion</span> This is the narrative guide. The one-page command reference lives at
<code>output/cheatsheet/Timepieces_Test_Execution_Cheatsheet.html</code> — keep it open beside this.</p></div>

<h2>1. The mental model</h2>
<p>Every run is the same Maven command with a few <code>-D</code> flags that answer four questions:</p>
<table>
  <thead><tr><th>Question</th><th>Flag</th><th>Values</th><th>Default</th></tr></thead>
  <tbody>
    <tr><td><b>Which environment?</b></td><td><code>-Denv</code></td><td><code>staging</code> · <code>production</code></td><td><code>staging</code></td></tr>
    <tr><td><b>Which browser?</b></td><td><code>-Dbrowser</code></td><td><code>chrome</code> · <code>firefox</code> · <code>edge</code> · <code>brave</code> · <code>safari</code></td><td><code>chrome</code></td></tr>
    <tr><td><b>Headed or headless?</b></td><td><code>-Dheadless</code></td><td><code>true</code> · <code>false</code></td><td><code>true</code></td></tr>
    <tr><td><b>Which scenarios?</b></td><td><code>-Dcucumber.filter.tags</code> or <code>-Dsurefire.suite</code></td><td>tag expr / a <code>testng-*.xml</code></td><td><code>testng.xml</code></td></tr>
  </tbody>
</table>
<div class="callout warn"><p><span class="tag">Windows quoting</span> In PowerShell, wrap any flag whose value contains a space or an <code>@</code> in quotes:
<code>"-Dcucumber.filter.tags=@Smoke and not @StagOnly"</code>. Unquoted, PowerShell splits on the space and the tag expression breaks.</p></div>

<h2>2. The default suite</h2>
<p><code>testng.xml</code> is the default (set in <code>pom.xml</code> as <code>surefire.suite</code>). It runs both BDD runners plus every TDD class, and the two BDD runners own <b>disjoint</b> tags:</p>
<ul>
  <li><code>SmokeRunner</code> — <code>@Smoke and not @Regression</code></li>
  <li><code>RegressionRunner</code> — <code>@Regression or @AuthGated</code></li>
</ul>
<pre><code># the whole suite, headless, against production, skipping staging-gate-only tests
.\mvnw.cmd clean test "-Denv=production" "-Dcucumber.filter.tags=not @StagOnly"</code></pre>
<div class="callout danger"><p><span class="tag">Do not add a tag filter in CI</span> A tag override is applied to <em>both</em> runners, so a scenario matching it runs twice → duplicate OTP requests → a 90-second "too many attempts" cooldown. The staging CI job passes <b>no</b> tag filter for exactly this reason.</p></div>

<h2>3. Everyday recipes</h2>
<pre><code># One area, headed, so you can watch it (also what video recording needs)
.\mvnw.cmd test "-Denv=staging" "-Dbrowser=chrome" "-Dheadless=false" `
  "-Dcucumber.filter.tags=@Accessories"

# A specific convenience suite (isolated TestNG XML)
.\mvnw.cmd test "-Dsurefire.suite=testng-search-api.xml"

# By profile (groups + tags baked in)
.\mvnw.cmd test -PSmokeSuite         # groups=smoke,            tags=@Smoke
.\mvnw.cmd test -PRegressionSuite    # groups=smoke,regression, tags=@Smoke or @Regression

# Environment-scoped tags
.\mvnw.cmd test "-Denv=production" "-Dcucumber.filter.tags=not @StagOnly"
.\mvnw.cmd test "-Denv=staging"    "-Dcucumber.filter.tags=not @ProdOnly"

# Tune parallelism (data-provider threads)
.\mvnw.cmd test "-Ddataproviderthreadcount=4"   # more browsers at once
.\mvnw.cmd test "-Ddataproviderthreadcount=1"   # serialise for debugging</code></pre>

<h2>4. The report</h2>
<pre><code>.\mvnw.cmd allure:report     # static HTML at target/site/allure-maven-plugin/
.\mvnw.cmd allure:serve      # generate + open on an ephemeral port
py scripts/email_allure_report.py   # emails a summary + link (needs SMTP_* + MAIL_TO)</code></pre>
<div class="callout note"><p><span class="tag">Video</span> Recording is enabled but only engages when it can capture something real — a <b>headed</b> local run (<code>-Dheadless=false</code>) or Docker (Xvfb virtual display). Headless runs skip video with a loud warning and still capture failure screenshots. See <code>ScreenRecorderUtil.canRecordMeaningfully()</code>.</p></div>

<h2>5. Docker and Grid</h2>
<pre><code># Headed-equivalent run inside a container (Xvfb display → real video)
docker compose up -d postgres-dev
./scripts/run-docker-headed.ps1        # wraps the container mvn invocation

# Cross-browser on the self-hosted Grid
docker compose --profile grid up -d
.\mvnw.cmd test "-Dsurefire.suite=testng-crossbrowser.xml" "-Dexecution.mode=GRID" "-Denv=production"</code></pre>
<p>Details in the <b>Docker</b>, <b>Selenium Grid</b> and <b>Cross-Browser</b> guides.</p>

<h2>6. Helper scripts</h2>
<table>
  <thead><tr><th>Script</th><th>Does</th></tr></thead>
  <tbody>
    <tr><td><code>scripts/run-local-headed.ps1</code></td><td>Reproduces the exact CI <code>mvn test</code> locally in headed Chrome.</td></tr>
    <tr><td><code>scripts/run-docker-headed.ps1</code></td><td>Same, but inside a container with a virtual display.</td></tr>
    <tr><td><code>scripts/run-search-api-analysis.ps1</code></td><td>Runs the search suites N times and compiles a per-keyword pass/fail analysis.</td></tr>
    <tr><td><code>scripts/run-zap-scan.ps1</code></td><td>Brings up ZAP and runs the DAST security suite.</td></tr>
  </tbody>
</table>
""")

doc(8, "configuration-properties-reader", "Configuration & data",
    "Configuration (Properties) Reader",
    "How ConfigReader resolves every setting across CLI, environment and property files.",
    r"""
<h2>1. What ConfigReader is</h2>
<p><code>com.timepieces.automation.utils.ConfigReader</code> is the single entry point for every tunable value in the
framework — base URLs, timeouts, browser, execution mode, feature flags, secrets. Page objects and utilities
never read <code>System.getProperty</code> directly; they call <code>ConfigReader.get("some.key")</code> so that
the resolution rules are applied consistently in one place.</p>

<h2 id="precedence">2. The three-tier precedence ladder</h2>
<p><code>get(key)</code> returns the <b>first</b> source that has the key, in this order:</p>
<table>
  <thead><tr><th>Tier</th><th>Source</th><th>Example</th><th>Wins over</th></tr></thead>
  <tbody>
    <tr><td>1</td><td>JVM system property (<code>-D</code> on the command line)</td><td><code>-Dbrowser=firefox</code></td><td>everything</td></tr>
    <tr><td>2</td><td>OS environment variable</td><td><code>CF_BYPASS_TOKEN</code></td><td>the properties file</td></tr>
    <tr><td>3</td><td>the env properties file (+ optional local override)</td><td><code>staging.properties</code></td><td>nothing (base layer)</td></tr>
  </tbody>
</table>
<pre><code>// simplified from ConfigReader.get(key)
String v = System.getProperty(key);       // tier 1
if (v != null) return v.trim();
v = System.getenv(key);                    // tier 2
if (v != null) return v.trim();
return prop.getProperty(key);              // tier 3 (may be null → logged as unmapped)</code></pre>
<div class="callout note"><p><span class="tag">Design intent</span> The command line is the operator's voice and must win. This is why <code>ADR-0005</code> fixes this precedence, and why <code>BaseRunner</code> was fixed (ISSUE-020) to stop the suite XML silently overriding <code>-Denv</code>.</p></div>

<h2 id="files">3. Which properties file is loaded</h2>
<p>On first use, <code>ConfigReader</code> reads <code>-Denv</code> (default <code>staging</code>) and loads, from the test classpath:</p>
<pre><code>config/&lt;env&gt;.properties            # committed base config
config/&lt;env&gt;.local.properties      # OPTIONAL, gitignored — layered on top if present</code></pre>
<p>The local override is where machine-specific secrets go (staging password, pilot phone) without touching committed files. Keys in the local file replace keys in the base file.</p>
<div class="callout warn"><p><span class="tag">One environment per JVM</span> The loaded config is cached once per JVM in a <code>static</code> block, keyed on the <code>-Denv</code> seen at class-load time. You cannot switch environments mid-run; start a new Maven invocation for a different <code>-Denv</code>.</p></div>

<h2 id="keys">4. Keys you will actually touch</h2>
<table>
  <thead><tr><th>Key</th><th>Meaning</th><th>Typical source</th></tr></thead>
  <tbody>
    <tr><td><code>base.url</code></td><td>Storefront root for the environment</td><td>properties file</td></tr>
    <tr><td><code>browser</code></td><td>Which browser to launch</td><td><code>-Dbrowser</code></td></tr>
    <tr><td><code>headless</code></td><td>Headless toggle (true by default)</td><td><code>-Dheadless</code></td></tr>
    <tr><td><code>execution.mode</code></td><td><code>local</code> · <code>GRID</code> · <code>CLOUD</code> — where the browser runs</td><td><code>-Dexecution.mode</code></td></tr>
    <tr><td><code>staging.password</code></td><td>Staging gate password (secret)</td><td>env var / local props / <code>-D</code></td></tr>
    <tr><td><code>pilot.phone</code></td><td>Account-test phone number (secret, no default)</td><td>local props / <code>-D</code></td></tr>
    <tr><td><code>cf.bypass.token</code></td><td>Cloudflare bypass header value</td><td><code>CF_BYPASS_TOKEN</code> env</td></tr>
    <tr><td><code>healenium.enabled</code></td><td>Self-healing on/off (off by default)</td><td>properties / <code>-D</code></td></tr>
  </tbody>
</table>

<h2 id="behaviour">5. Behaviour to know</h2>
<ul>
  <li><b>Trimming.</b> Every returned value is <code>trim()</code>-ed, so trailing whitespace in a properties file never bites.</li>
  <li><b>Unmapped keys.</b> A key found in no tier returns <code>null</code> and logs a WARN (<em>"Configuration key lookup requested but returned empty/unmapped"</em>). These warnings are normal for optional keys — they are not errors.</li>
  <li><b>Missing base file is fatal.</b> If <code>config/&lt;env&gt;.properties</code> is not on the classpath, initialisation throws — a typo in <code>-Denv</code> fails fast rather than silently testing the wrong place.</li>
  <li><b>Secrets never defaulted.</b> <code>pilot.phone</code> has no fallback: a test that needs it throws with a message naming the key, instead of quietly using someone's real number.</li>
</ul>

<h2>6. Adding a new setting</h2>
<ol>
  <li>Add the key to <b>both</b> <code>staging.properties</code> and <code>production.properties</code> (keep them in step so neither can silently disagree — the lesson of ISSUE-005).</li>
  <li>Read it via <code>ConfigReader.get("your.key")</code>; never <code>System.getProperty</code> directly.</li>
  <li>If it is a secret, leave it blank in the committed file and document the local-override / env-var path.</li>
</ol>
""")

doc(9, "data-extraction", "Configuration & data",
    "Data Extraction — Excel, CSV and JSON",
    "Where test data comes from, how it is read, and how results are written back out.",
    r"""
<h2>1. The three formats and their roles</h2>
<table>
  <thead><tr><th>Format</th><th>Direction</th><th>Role</th><th>Library</th></tr></thead>
  <tbody>
    <tr><td><b>Excel (.xlsx)</b></td><td>in &amp; out</td><td>Business-maintained <em>source of truth</em> for search keywords; also the <em>report</em> twin written after a run.</td><td>Apache POI (XSSF)</td></tr>
    <tr><td><b>JSON</b></td><td>in</td><td>The machine-readable keyword matrix the tests actually read at runtime.</td><td>Jackson</td></tr>
    <tr><td><b>CSV</b></td><td>out</td><td>Aggregated per-keyword pass/fail analysis from the multi-run search harness.</td><td>plain writer</td></tr>
  </tbody>
</table>

<h2>2. The keyword data pipeline</h2>
<pre><code>Timepieces_Search_Keywords.xlsx        (business edits this, monthly)
        │   regenerate
        ▼
testdata/search_keywords.json       (committed, machine-readable)
        │   read at runtime
        ▼
SearchApiTest / SearchApiExploratoryTest</code></pre>
<p>The spreadsheet lives at <code>src/test/resources/testdata/Timepieces_Search_Keywords.xlsx</code>, right beside the JSON derived from it. <b>Edit the spreadsheet, regenerate the JSON — never hand-edit the JSON.</b></p>
<div class="callout note"><p><span class="tag">Why JSON at runtime, not the .xlsx</span> Reading a structured JSON matrix is simpler, faster and diff-friendly in review. The spreadsheet is the human editing surface; the JSON is the code contract.</p></div>

<h2>3. Reading JSON at runtime</h2>
<p>Both search tests load the matrix from the classpath and parse it with Jackson:</p>
<pre><code>try (InputStream is = getClass().getClassLoader()
        .getResourceAsStream("testdata/search_keywords.json")) {
    if (is == null)
        throw new IllegalStateException(
            "Could not find testdata/search_keywords.json in resources classpath.");
    // Jackson ObjectMapper → typed keyword records → TestNG DataProvider rows
}</code></pre>
<p>Loading via <code>getResourceAsStream</code> (not a file path) means it works identically from the IDE, the Maven build and inside a container — the file is on the classpath, wherever the classpath is.</p>

<h2>4. Writing Excel reports (Apache POI)</h2>
<p><code>com.timepieces.automation.utils.ExcelReportUtil</code> writes an <code>.xlsx</code> workbook from result rows and attaches it to the Allure report:</p>
<pre><code>ExcelReportUtil.writeAndAttach(
    "target/search_keyword_api_report.xlsx",   // where to write
    "Search API Results",                        // Allure attachment name
    groupLabel, headers, rows);                  // content
// internally: new XSSFWorkbook() → sheets → Allure.addAttachment(..., XLSX_MIME, ...)</code></pre>
<p>Two things worth knowing:</p>
<ul>
  <li>The report is written to <code>target/</code> (regenerable build output) and <em>also</em> streamed into Allure, so a reviewer can download it straight from the report.</li>
  <li>It is a genuine <code>.xlsx</code> (XSSF), not CSV-renamed — formulas, multiple sheets and styling are available if a future report needs them.</li>
</ul>

<h2>5. CSV analysis output</h2>
<p><code>scripts/run-search-api-analysis.ps1</code> runs the deterministic and exploratory search suites several times, then compiles a per-keyword aggregation into
<code>output/search-api-analysis/&lt;timestamp&gt;/keyword-aggregation.csv</code> plus a <code>summary.md</code>. That directory is gitignored and fully regenerable — see the <b>DataProvider vs Invocation Count</b> guide for why the suite is run many times.</p>

<h2>6. Handling binary test data in Maven</h2>
<div class="callout warn"><p><span class="tag">Never enable resource filtering on the .xlsx</span> Maven resource <em>filtering</em> does token substitution byte-by-byte and will silently corrupt a binary <code>.xlsx</code> while the build still reports success. Filtering is deliberately off for this resource path; if you ever add filtering, exclude <code>**/*.xlsx</code>. The safe check after any move is an <code>md5</code> comparison of the source and the <code>target/test-classes</code> copy.</p></div>
""")

doc(10, "dataprovider-vs-invocationcount", "Execution & scaling",
    "DataProvider vs Invocation Count — Deterministic vs Probabilistic",
    "Two different reasons to run a test more than once, and which knob to reach for.",
    r"""
<h2>1. The distinction in one line</h2>
<blockquote>A <b>DataProvider</b> runs a test once <em>per data row</em>, in parallel — that is <b>deterministic coverage</b>.
An <b>invocation count</b> runs the <em>same</em> test many times to expose flakiness — that is <b>probabilistic sampling</b>.</blockquote>

<h2>2. DataProvider — parallel, deterministic coverage</h2>
<p>The framework's parallelism comes from a TestNG <code>@DataProvider(parallel = true)</code>. In BDD, <code>BaseRunner</code> overrides Cucumber's scenario provider so every scenario becomes one parallel data row:</p>
<pre><code>@Override
@DataProvider(parallel = true)
public Object[][] scenarios() {
    return super.scenarios();   // one row per Cucumber scenario, run concurrently
}</code></pre>
<p>Because <code>DriverFactory</code> holds the WebDriver in a <code>ThreadLocal</code>, each parallel row gets its own isolated browser. The number of concurrent browsers is the data-provider thread count:</p>
<pre><code>.\mvnw.cmd test "-Ddataproviderthreadcount=4"   # 4 scenarios (4 browsers) at once
.\mvnw.cmd test "-Ddataproviderthreadcount=1"   # fully serial — best for debugging</code></pre>
<p>The TDD search tests use the same pattern with a named provider:</p>
<pre><code>@DataProvider(name = "keywordData", parallel = true)   // one row per search keyword</code></pre>
<div class="callout note"><p><span class="tag">Deterministic</span> Given the same data, a DataProvider run covers exactly the same set of cases every time. It answers <em>"does each case pass?"</em> — not <em>"is this case stable?"</em></p></div>

<h2>3. Invocation count — repetition, probabilistic sampling</h2>
<p>Some questions can only be answered by running the <em>same</em> case repeatedly: <em>is this keyword's ranking stable? does this flow flake 1 in 20 times?</em> A single pass cannot tell you; you have to sample.</p>
<p>In this framework that repetition is driven at the <b>suite level</b> by <code>scripts/run-search-api-analysis.ps1</code>, which invokes the search suites a configurable number of times and aggregates every run:</p>
<pre><code>output/search-api-analysis/&lt;timestamp&gt;/
    keyword-aggregation.csv     # per-keyword pass/fail across all runs
    summary.md                  # headline stability findings
    runs/det-run-1.log ...      # each individual invocation</code></pre>
<p>The exploratory search test exists precisely for this: it de-duplicates keywords and samples them repeatedly to surface <em>non-deterministic</em> relevance behaviour that a single deterministic pass would miss. That is how the search-fallback defects (ISSUE-009…012) were characterised — not from one run, but from the <em>distribution</em> across many.</p>
<div class="callout warn"><p><span class="tag">Probabilistic</span> A repeated run answers <em>"how often does this pass?"</em>. One green pass proves a case <em>can</em> pass; it does not prove it is stable. Flakiness is a frequency, and you need samples to measure a frequency.</p></div>

<h2>4. Choosing the knob</h2>
<table>
  <thead><tr><th>You want to know…</th><th>Reach for</th><th>Why</th></tr></thead>
  <tbody>
    <tr><td>Does every scenario / keyword pass?</td><td>DataProvider (the normal run)</td><td>One row per case, run once, in parallel.</td></tr>
    <tr><td>Is this flow flaky?</td><td>Repeat the suite (analysis script)</td><td>Flakiness is a rate; measure it over many runs.</td></tr>
    <tr><td>Is a search result stable week to week?</td><td>Exploratory suite × N via the script</td><td>Samples the distribution, not a point.</td></tr>
    <tr><td>Faster feedback on a big suite?</td><td>Raise <code>dataproviderthreadcount</code></td><td>More browsers concurrently — coverage, not repetition.</td></tr>
  </tbody>
</table>
<div class="callout danger"><p><span class="tag">Don't confuse the two</span> Raising the thread count does <em>not</em> make results more reliable — it just runs the same single-pass coverage faster. Reliability evidence comes only from repetition. And repetition without isolation (shared login numbers, shared cart) manufactures false flakiness — which is why repeated auth runs must vary identities and stay within OTP cooldowns.</p></div>
""")

doc(11, "cross-browser-testing", "Execution & scaling",
    "Cross-Browser Testing",
    "Running the same scenarios on Chrome, Firefox and Edge — sequentially or in parallel.",
    r"""
<h2>1. Two supported shapes</h2>
<table>
  <thead><tr><th>Shape</th><th>How</th><th>When</th></tr></thead>
  <tbody>
    <tr><td><b>Sequential</b> (one JVM per browser)</td><td>Run <code>mvn test</code> three times with different <code>-Dbrowser</code>, letting Allure results accumulate into one report.</td><td>The simple, always-safe default. Exactly what <code>docker/entrypoint.sh</code> does.</td></tr>
    <tr><td><b>Parallel</b> (one JVM, all browsers at once)</td><td>The <code>testng-crossbrowser.xml</code> suite on the Grid (DEC-0014).</td><td>When you want all three concurrently and have the Grid up.</td></tr>
  </tbody>
</table>

<h2>2. Sequential — the safe default</h2>
<pre><code>.\mvnw.cmd clean test "-Dexecution.mode=GRID" "-Dbrowser=chrome"  "-Denv=production"
.\mvnw.cmd test       "-Dexecution.mode=GRID" "-Dbrowser=firefox" "-Denv=production"
.\mvnw.cmd test       "-Dexecution.mode=GRID" "-Dbrowser=edge"    "-Denv=production"
.\mvnw.cmd allure:report   # one merged report across all three</code></pre>
<p>Each invocation is a clean JVM with one browser, so there is no shared state to race on. Local runs work the same way with <code>execution.mode=local</code>, provided the browser is installed.</p>

<h2>3. Parallel in one JVM (DEC-0014)</h2>
<p><code>testng-crossbrowser.xml</code> runs chrome, firefox and edge <b>concurrently</b> with <code>parallel="tests"</code>, one <code>&lt;test&gt;</code> block per browser:</p>
<pre><code>docker compose --profile grid up -d
.\mvnw.cmd test "-Dsurefire.suite=testng-crossbrowser.xml" "-Dexecution.mode=GRID" "-Denv=production"</code></pre>

<h3>How the browser reaches the driver</h3>
<p>The browser normally comes from <code>-Dbrowser</code>, a <b>JVM-global</b> system property. Three <code>&lt;test&gt;</code> blocks writing it would race — last writer wins, and all three would silently run the same browser while the report claimed otherwise. The suite avoids this with a per-thread override:</p>
<pre><code>// DriverFactory
private static final ThreadLocal&lt;String&gt; tlBrowserOverride = new ThreadLocal&lt;&gt;();
// resolveBrowser() prefers the thread-local, else falls back to ConfigReader.get("browser")

// BaseRunner @BeforeMethod reads the suite's &lt;parameter name="browser"&gt; and pins it
DriverFactory.setBrowserForThread(browser);   // @AfterMethod clears it</code></pre>
<div class="callout note"><p><span class="tag">Why @BeforeMethod, not @BeforeTest</span> Under a parallel DataProvider the scenarios execute on pool threads, <em>not</em> the thread that ran <code>@BeforeTest</code>. TestNG runs <code>@BeforeMethod</code> on the same thread as its <code>@Test</code>, so the pin is set on the thread that will actually build the driver. This was verified empirically before the code was written.</p></div>

<h2>4. Verifying it was REALLY cross-browser</h2>
<div class="callout danger"><p><span class="tag">Never trust a green run alone</span> The failure mode here is a <em>silent wrong answer</em>: a green report that quietly used one browser three times. Ask the Grid instead — each node serves only its own browser:</p></div>
<pre><code>docker compose --profile grid logs selenium-node-chrome  | Select-String "Session created"
docker compose --profile grid logs selenium-node-firefox | Select-String "Session created"
docker compose --profile grid logs selenium-node-edge    | Select-String "Session created"</code></pre>
<p>Three roughly-equal non-zero counts = genuine cross-browser. A zero anywhere means that browser never ran, whatever the report says. The Allure environment page should likewise list <code>Browser.CHROME</code>, <code>Browser.FIREFOX</code> and <code>Browser.EDGE</code>.</p>

<h2>5. Browser substitutions (logged, never silent)</h2>
<table>
  <thead><tr><th>Requested</th><th>Actually used</th><th>Why</th></tr></thead>
  <tbody>
    <tr><td>chrome / firefox / edge</td><td>as-is</td><td>Real nodes / local installs exist.</td></tr>
    <tr><td><b>brave</b></td><td>chrome</td><td>Brave is Chromium; the difference (shields, branding) is not exercised by these tests. Logged.</td></tr>
    <tr><td><b>safari</b></td><td><code>safari.fallback.browser</code></td><td>Real Safari only on macOS (ADR-0008); elsewhere a configured Chromium fallback, annotated in the report.</td></tr>
  </tbody>
</table>
<div class="callout warn"><p><span class="tag">Thread budget</span> 3 test blocks × <code>data-provider-thread-count=2</code> = up to 6 concurrent browsers, matching the Grid's 3 nodes × 2 sessions = 6 slots. Asking for more than the Grid can serve makes sessions queue and time out — and a starved browser looks exactly like a flaky app. Add node replicas for more throughput, not more threads.</p></div>
""")

doc(12, "selenium-grid", "Execution & scaling",
    "Selenium Grid — and the path to cloud (BrowserStack / LambdaTest)",
    "The self-hosted Grid, how it scales, and where a cloud provider would slot in.",
    r"""
<h2>1. Execution modes</h2>
<p><code>execution.mode</code> decides <b>where</b> the browser runs. Everything else — tags, suites, env — is unchanged.</p>
<table>
  <thead><tr><th>Mode</th><th>Where the browser runs</th><th>Honours <code>-Dbrowser</code>?</th></tr></thead>
  <tbody>
    <tr><td><code>local</code> <span class="pill">default</span></td><td>A browser installed on this machine</td><td>yes</td></tr>
    <tr><td><code>GRID</code></td><td>The self-hosted Grid below</td><td><b>yes</b></td></tr>
    <tr><td><code>CLOUD</code></td><td>LambdaTest (POC)</td><td><b>no — always Chrome.</b> See §5</td></tr>
  </tbody>
</table>

<h2>2. The Grid itself</h2>
<p>Defined in <code>docker-compose.yml</code> behind the <code>grid</code> profile:</p>
<table>
  <thead><tr><th>Service</th><th>Image</th><th>Notes</th></tr></thead>
  <tbody>
    <tr><td><code>selenium-hub</code></td><td><code>selenium/hub:4.44</code></td><td>Ports 4442/4443 (event bus), 4444 (WebDriver + UI)</td></tr>
    <tr><td><code>selenium-node-chrome</code></td><td><code>selenium/node-chrome:4.44</code></td><td>2 sessions, 1920×1080, <code>shm_size: 2gb</code></td></tr>
    <tr><td><code>selenium-node-firefox</code></td><td><code>selenium/node-firefox:4.44</code></td><td>2 sessions</td></tr>
    <tr><td><code>selenium-node-edge</code></td><td><code>selenium/node-edge:4.44</code></td><td>2 sessions</td></tr>
  </tbody>
</table>
<pre><code>docker compose --profile grid up -d          # start hub + 3 nodes
# then open http://localhost:4444/ui
.\mvnw.cmd test "-Dexecution.mode=GRID" "-Dbrowser=firefox" "-Denv=production"</code></pre>
<div class="callout danger"><p><span class="tag">ISSUE-021, now fixed</span> Two Healenium services once had no Compose <em>profile</em> — and "no profile" means "start with everything", so the Grid command tried to pull a Healenium image that no longer resolves, and one failed pull aborted the whole <code>up</code>. The result was 15/15 tests failing with <code>SessionNotCreatedException</code> — reading like "cross-browser is broken" when the Grid simply never started. Every heavyweight service now sits behind a profile. Before blaming the framework, ask Compose what a command will start: <code>docker compose --profile grid config --services</code>.</p></div>

<h2>3. Scaling out</h2>
<p>Throughput is <em>nodes</em>, not threads. Raise a node's replica count or its <code>SE_NODE_MAX_SESSIONS</code>, and keep the data-provider thread count ≤ total Grid slots so sessions never starve:</p>
<pre><code>docker compose --profile grid up -d --scale selenium-node-chrome=3</code></pre>

<h2>4. Verifying the Grid ran what you think</h2>
<pre><code>curl http://localhost:4444/status                 # ready:true, node + slot counts
docker compose --profile grid logs selenium-node-chrome | Select-String "Session created"</code></pre>

<h2 id="cloud">5. The path to cloud (BrowserStack / LambdaTest)</h2>
<p>The Grid is the <b>supported</b> cross-browser path today. Cloud is deliberately left as a POC:</p>
<ul>
  <li><code>execution.mode=CLOUD</code> is LambdaTest-shaped and <b>ignores <code>-Dbrowser</code></b> (it always builds <code>ChromeOptions</code>). Do not use it for cross-browser — it would run Chrome three times while claiming otherwise.</li>
  <li>BrowserStack has config keys reserved but <b>no code</b> behind them.</li>
</ul>
<h3>How to finish the cloud path</h3>
<p>The clean extension is a small <em>provider abstraction</em> in <code>DriverFactory</code> — a strategy that, given <code>execution.mode</code>, returns a <code>RemoteWebDriver</code> pointed at the right hub URL with the right capabilities:</p>
<ul>
  <li><code>GRID</code> → local hub URL, W3C caps (already done).</li>
  <li><code>CLOUD:lambdatest</code> / <code>CLOUD:browserstack</code> → the provider's hub URL, provider-prefixed caps (<code>bstack:options</code> / <code>LT:Options</code>), credentials from env vars.</li>
  <li>Crucially, the CLOUD branch must read the requested browser the same way the GRID branch does (via <code>resolveGridBrowser</code>), so it stops hard-coding Chrome.</li>
</ul>
<div class="callout note"><p><span class="tag">Why Grid first</span> A self-hosted Grid proves the whole remote-execution plumbing (capabilities, thread-local browser, session verification) with no per-minute billing and no third-party flakiness. Once that is solid, swapping the hub URL for a cloud provider is a small, well-scoped change — recorded as an open decision in <code>decisions-log.md</code>.</p></div>
""")

doc(6, "locator-strategies", "Design & comparison",
    "Locator Strategies",
    "How elements are found, why data-testid is preferred, and the traps that caused real failures.",
    r"""
<h2>1. The locator hierarchy</h2>
<p>Locators are chosen in this order of preference — earlier is more stable:</p>
<table>
  <thead><tr><th>Rank</th><th>Strategy</th><th>Example</th><th>Use when</th></tr></thead>
  <tbody>
    <tr><td>1</td><td><b><code>data-testid</code></b></td><td><code>main[data-testid='home-main']</code></td><td>Almost always. Purpose-built for automation; survives copy/style changes.</td></tr>
    <tr><td>2</td><td>stable attribute (<code>name</code>, <code>href</code>, <code>id</code>, <code>role</code>)</td><td><code>a[href='/accessories/all']</code></td><td>No testid exists but a semantic attribute is stable.</td></tr>
    <tr><td>3</td><td>text anchor (XPath <code>normalize-space</code>)</td><td><code>//h3[normalize-space()='Contact details']</code></td><td>Third-party UI (Razorpay) with neither testid nor stable attribute.</td></tr>
    <tr><td>4</td><td>structural / positional</td><td><code>...following::input[position()&lt;=4]</code></td><td>Last resort; marked <code>VERIFY</code>-tier in comments because it is brittle.</td></tr>
  </tbody>
</table>
<div class="callout note"><p><span class="tag">The rollout</span> A repo-wide <code>data-testid</code> rollout replaced brittle CSS/text selectors across the page objects specifically to kill flaky tests. When the app adds a testid, prefer it and retire the older locator.</p></div>

<h2>2. Where locators live</h2>
<p>Locators are <code>private final By</code> fields on the page/component object, never inline in step definitions. Business methods and verifications reference the fields, so a UI change is a one-line edit in one place. Provenance for third-party locators (e.g. Razorpay's real testids, captured live) is documented in <code>docs/scrape-artifacts/</code>.</p>

<h2>3. Finding elements safely — the BasePage helpers</h2>
<table>
  <thead><tr><th>Helper</th><th>What it does</th><th>Why it exists</th></tr></thead>
  <tbody>
    <tr><td><code>waitForVisibility(By)</code></td><td>Wait until present <em>and</em> visible.</td><td>Presence ≠ visibility; asserting on a present-but-hidden node is a classic false pass/fail.</td></tr>
    <tr><td><code>waitForFirstVisible(By)</code></td><td>Return the first <em>visible</em> match, preferring in-viewport.</td><td>Responsive pages render hidden desktop/mobile twins that share a selector — see §4.</td></tr>
    <tr><td><code>isEventuallyDisplayed / isEventuallyHidden</code></td><td>Wait for a state to settle.</td><td>Assertions must wait for state, not sample it mid-transition.</td></tr>
    <tr><td><code>clickElement(By)</code></td><td>Scroll to centre, native click, JS-click fallback.</td><td>Overlays and off-screen elements intercept naive clicks.</td></tr>
  </tbody>
</table>

<h2>4. Traps that caused real failures</h2>
<div class="callout danger"><p><span class="tag">Hidden responsive twins</span> A selector shared by a desktop and a mobile variant matches <em>two</em> nodes, only one visible. <code>clickElement</code> takes the first in DOM order — often the hidden one — so the click "succeeds" but nothing happens, and the next assertion fails on the wrong page. Fix: <code>waitForFirstVisible(...).click()</code>. This bit the sidebar Accessories link and several accessory PDPs.</p></div>
<div class="callout warn"><p><span class="tag">Presence vs visibility on teardown</span> Some third-party overlays (Razorpay) <em>hide</em> without removing their DOM nodes. A presence check (<code>findElements(...).isEmpty()</code>) reports the modal as still open forever. Use a <em>visibility</em> check (<code>isEventuallyHidden</code>) when asserting something has gone.</p></div>
<div class="callout warn"><p><span class="tag">Overlays intercept clicks</span> The cookie bar, drop cards and the cart drawer sit over other controls. <code>BasePage.open()</code> runs <code>SiteOverlayHandler.dismissBlockingOverlays()</code>; a control behind an auto-opened drawer needs the drawer closed first (an explicit step), not a forced click.</p></div>
<div class="callout warn"><p><span class="tag">Selector typos fail loudly, later</span> A malformed selector (e.g. a stray <code>]</code>) throws <code>InvalidSelectorException</code> on first use — which reads like a driver fault, not a typo. Validate new selectors against the live DOM before wiring them in.</p></div>

<h2>5. Healable locators</h2>
<p>When Healenium is enabled (off by default), the driver is wrapped so that a locator which no longer matches can be <em>self-healed</em> from previously-seen node attributes. That is a safety net, not a substitute for good locators — see the <b>Healenium</b> guide.</p>
""")

doc(13, "selenium-vs-playwright", "Design & comparison",
    "Selenium vs Playwright — in this project",
    "How the Java/Selenium framework and the Playwright POC differ where it actually matters.",
    r"""
<h2>1. What exists in this repo</h2>
<table>
  <thead><tr><th>Implementation</th><th>Location</th><th>Status</th></tr></thead>
  <tbody>
    <tr><td>Selenium + Java</td><td><code>src/</code></td><td>The production framework — ~430 files, full BDD + TDD coverage.</td></tr>
    <tr><td>Playwright + TypeScript</td><td><code>automation-ts/</code></td><td>A proof-of-concept / migration template (DEC-0013): BasePage, a few page objects, one BDD feature, one API spec.</td></tr>
  </tbody>
</table>
<p>This guide compares the two tools <em>as used here</em>, not in the abstract.</p>

<h2>2. Waiting</h2>
<table>
  <thead><tr><th></th><th>Selenium (this framework)</th><th>Playwright (POC)</th></tr></thead>
  <tbody>
    <tr><td>Model</td><td>Explicit <code>WebDriverWait</code> in <code>BasePage</code> helpers.</td><td><b>Auto-waiting</b> built into every action/assertion.</td></tr>
    <tr><td>Consequence</td><td>You must choose the right wait (<code>waitForVisibility</code>, <code>waitForFirstVisible</code>, <code>isEventuallyHidden</code>). Getting it wrong caused real flakes (assertions sampling state; presence-vs-visibility).</td><td>Locators are lazy and retried until actionable, so a whole class of races simply does not arise.</td></tr>
  </tbody>
</table>
<div class="callout note"><p><span class="tag">Takeaway</span> Most of this framework's hard-won fixes (the BasePage wait helpers, the hidden-twin handling) are compensating for something Playwright gives for free. That is the single biggest practical difference.</p></div>

<h2>3. Cross-origin iframes</h2>
<p>The Razorpay checkout runs in a cross-origin iframe. In Selenium you must <code>switchTo().frame(...)</code> and remember to switch back — every modal-internal method assumes the right frame context, and forgetting is a common bug. Playwright's <code>frameLocator()</code> scopes into the frame per-call with no global switching state, which is materially safer for this exact flow.</p>

<h2>4. Parallelism</h2>
<table>
  <thead><tr><th></th><th>Selenium (this framework)</th><th>Playwright (POC)</th></tr></thead>
  <tbody>
    <tr><td>Unit</td><td>TestNG <code>@DataProvider(parallel=true)</code> + <code>ThreadLocal</code> WebDriver.</td><td>Worker processes; isolated <code>BrowserContext</code> per test.</td></tr>
    <tr><td>Isolation</td><td>Manual — the ThreadLocal is what keeps drivers from colliding.</td><td>Automatic — contexts are cheap and fully isolated by design.</td></tr>
  </tbody>
</table>

<h2>5. Driver / browser management</h2>
<ul>
  <li><b>Selenium:</b> Selenium Manager auto-fetches the matching driver; real browsers must exist locally or on the Grid.</li>
  <li><b>Playwright:</b> <code>npx playwright install</code> pulls pinned browser builds; no separate driver concept.</li>
</ul>

<h2>6. Where each wins here</h2>
<table>
  <thead><tr><th>Dimension</th><th>Winner in this project</th><th>Why</th></tr></thead>
  <tbody>
    <tr><td>Flake resistance</td><td>Playwright</td><td>Auto-waiting removes the failure modes we spent weeks fixing.</td></tr>
    <tr><td>Iframe / trace debugging</td><td>Playwright</td><td>Frame locators + built-in trace viewer.</td></tr>
    <tr><td>Ecosystem maturity / team familiarity</td><td>Selenium</td><td>The team, the Grid, ZAP and the reporting stack are all built around it.</td></tr>
    <tr><td>Real-device / cloud breadth</td><td>Selenium</td><td>Grid today; BrowserStack/LambdaTest are Selenium-shaped here.</td></tr>
    <tr><td>Language reach</td><td>Selenium</td><td>Java is the team's primary language and the wider codebase.</td></tr>
  </tbody>
</table>
<div class="callout warn"><p><span class="tag">Migration stance</span> The POC exists to de-risk a future move, not to signal one is scheduled. It mirrors the Java structure 1:1 (BasePage → StoreFrontBasePage → page objects) so the mental model transfers. Any migration is a product decision, recorded via ADR if taken.</p></div>
""")

doc(14, "java-vs-typescript", "Design & comparison",
    "Java vs TypeScript — in this project",
    "The two language implementations of the same framework, and what changes between them.",
    r"""
<h2>1. Same architecture, two languages</h2>
<p>The Playwright POC (<code>automation-ts/</code>) is a deliberate <b>1:1 port</b> of the Java framework's structure, so the design ideas transfer even though the syntax does not:</p>
<table>
  <thead><tr><th>Concept</th><th>Java (<code>src/</code>)</th><th>TypeScript (<code>automation-ts/</code>)</th></tr></thead>
  <tbody>
    <tr><td>Base page</td><td><code>BasePage&lt;SELF&gt;</code></td><td><code>BasePage.ts</code></td></tr>
    <tr><td>Layered bases</td><td><code>HeaderOnlyBasePage</code>, <code>StoreFrontBasePage</code></td><td>same names, <code>.ts</code></td></tr>
    <tr><td>Config</td><td><code>ConfigReader.java</code></td><td><code>src/config/ConfigReader.ts</code></td></tr>
    <tr><td>Runner / BDD</td><td>TestNG + <code>cucumber-jvm</code></td><td><code>playwright test</code> + <code>cucumber-js</code></td></tr>
    <tr><td>Build</td><td>Maven (<code>pom.xml</code>)</td><td>npm (<code>package.json</code>)</td></tr>
  </tbody>
</table>
<div class="callout note"><p><span class="tag">There is also a Python port</span> A separate Playwright-<em>Python</em> port exists outside this repo (<code>d2c-automation-python</code>) as a third language surface of the same design. Same page-object model, pytest instead of TestNG/cucumber-js.</p></div>

<h2>2. The fluent page object — where the languages differ most</h2>
<p>Java uses the <b>CRTP</b> (curiously recurring template pattern) so an inherited <code>open()</code> returns the concrete page type without every page overriding it:</p>
<pre><code>// Java: self-type generic
public abstract class BasePage&lt;SELF extends BasePage&lt;SELF&gt;&gt; {
    public SELF open() { ...; return self(); }   // returns the concrete subtype
}
public class HomePage extends StoreFrontBasePage&lt;HomePage&gt; { ... }</code></pre>
<pre><code>// TypeScript: polymorphic `this` gives the same fluent typing for free
class BasePage {
  async open(): Promise&lt;this&gt; { ...; return this; }   // `this` is the concrete subtype
}</code></pre>
<p>TypeScript's polymorphic <code>this</code> expresses in one keyword what Java needs a recursive generic bound to achieve. Same fluent ergonomics, far less ceremony.</p>

<h2>3. Config resolution</h2>
<ul>
  <li><b>Java:</b> <code>ConfigReader</code> — <code>-D</code> &gt; env var &gt; <code>&lt;env&gt;.properties</code>, cached once per JVM.</li>
  <li><b>TypeScript:</b> <code>ConfigReader.ts</code> — <code>process.env</code> &gt; <code>.env</code> file &gt; defaults, the same three-tier idea using Node conventions.</li>
</ul>

<h2>4. Async is the pervasive difference</h2>
<div class="callout warn"><p><span class="tag">Everything is a Promise</span> In TypeScript every interaction is <code>async</code>/<code>await</code>. A missing <code>await</code> is the #1 porting bug — the line "passes" because it returns a pending promise instead of doing the work. Java's calls are synchronous, so this class of error does not exist there. Enable <code>@typescript-eslint/no-floating-promises</code> to catch it.</p></div>

<h2>5. Typing and null-safety</h2>
<table>
  <thead><tr><th></th><th>Java</th><th>TypeScript</th></tr></thead>
  <tbody>
    <tr><td>Null handling</td><td>Nullable refs; <code>firstNonBlank(...)</code> helpers; runtime NPEs.</td><td><code>strict</code> + optional chaining <code>?.</code> / nullish <code>??</code> catch many at compile time.</td></tr>
    <tr><td>Boilerplate</td><td>Lombok (<code>@Log4j2</code>, getters) reduces it.</td><td>Structural typing needs less to begin with.</td></tr>
    <tr><td>Build speed</td><td>Slower (JVM start + Maven).</td><td>Faster incremental (esbuild/tsc).</td></tr>
  </tbody>
</table>

<h2>6. Practical guidance for the next SDET</h2>
<ul>
  <li>Extend the <b>Java</b> framework for real coverage work — it is the production surface.</li>
  <li>Treat <code>automation-ts/</code> as a reference for what a Playwright migration would look like; keep its structure mirroring the Java side so the comparison stays honest.</li>
  <li>When porting a page object, port the <em>locators and business methods verbatim</em> first, then adapt waits (Playwright auto-waits — delete most explicit waits) and add <code>await</code> everywhere.</li>
</ul>
""")

doc(4, "cicd-pipelines", "Engineering & process",
    "CI/CD Pipelines",
    "The GitHub Actions workflows, what triggers them, and the secrets they need.",
    r"""
<h2>1. The workflows</h2>
<table>
  <thead><tr><th>Workflow</th><th>Trigger</th><th>Does</th></tr></thead>
  <tbody>
    <tr><td><code>staging-tests.yml</code></td><td>PR to <code>main</code> · nightly 20:30 UTC · manual</td><td>Full smoke + regression against staging, headless Chrome.</td></tr>
    <tr><td><code>production-tests.yml</code></td><td>nightly 22:30 UTC · manual</td><td>Production-safe scope (no login / no staging gate).</td></tr>
    <tr><td><code>test-pipeline.yml</code></td><td>manual · repository-dispatch</td><td>Parameterised run + publishes the Allure report (Pages).</td></tr>
    <tr><td><code>exploratory-search-tests.yml</code></td><td>scheduled · manual</td><td>Repeated search runs for relevance-stability analysis.</td></tr>
    <tr><td><code>security-zap-scan.yml</code></td><td>weekly (Sat staging / Sun prod) · manual</td><td>OWASP ZAP DAST — passive/baseline on prod, active on staging.</td></tr>
    <tr><td><code>cf-bypass-check.yml</code></td><td>scheduled</td><td>Verifies the Cloudflare bypass header still works.</td></tr>
  </tbody>
</table>

<h2>2. Anatomy of a run (staging-tests)</h2>
<pre><code>checkout → setup JDK 25 (Temurin, maven cache) → install google-chrome-stable
        → mvn -B test -Denv=staging -Dbrowser=chrome -Dheadless=true
                      -Dchrome.no.sandbox=true -Dstaging.password=***
        → mvn allure:report (always)
        → upload allure-report artifact (always, 30-day retention)
        → email summary + report link (if SMTP_* set)</code></pre>
<div class="callout danger"><p><span class="tag">No tag filter in CI — on purpose</span> <code>testng.xml</code>'s two runners own disjoint tags, so a bare <code>mvn test</code> runs every scenario exactly once. Passing <code>-Dcucumber.filter.tags</code> forces the filter onto <em>both</em> runners and double-executes matches → duplicate OTP requests → a 90-second cooldown. The workflow comment says this explicitly; do not add a tag filter.</p></div>

<h2>3. Concurrency and scheduling</h2>
<ul>
  <li>Staging uses a <code>concurrency</code> group keyed on the ref with <code>cancel-in-progress</code>, so a fresh push supersedes an in-flight run — two runs must not hit the same login number and trip the OTP cooldown.</li>
  <li>Nightlies are staggered (staging 20:30 UTC, production 22:30 UTC) for the same reason.</li>
  <li>Staging PRs skip when only non-test assets change (<code>paths-ignore</code>: <code>dashboard/</code>, <code>docs/</code>, <code>grafana-dashboards/</code>, <code>**/*.md</code>).</li>
</ul>

<h2>4. Secrets (repo settings → Actions secrets)</h2>
<table>
  <thead><tr><th>Secret</th><th>Used for</th></tr></thead>
  <tbody>
    <tr><td><code>STAGING_PASSWORD</code></td><td>Passing the staging gate (<code>-Dstaging.password</code>).</td></tr>
    <tr><td><code>CF_BYPASS_TOKEN</code></td><td>Cloudflare bypass header on hosted runners.</td></tr>
    <tr><td><code>NEON_DATABASE_URL</code></td><td>Publishing run metrics to the hosted Postgres.</td></tr>
    <tr><td><code>SMTP_HOST/PORT/USER/PASSWORD</code>, <code>MAIL_FROM/TO</code></td><td>Emailing the report (link mode in CI).</td></tr>
  </tbody>
</table>

<h2>5. The Cloudflare reality</h2>
<div class="callout warn"><p><span class="tag">ISSUE-001</span> GitHub-hosted runners live outside India and trip Cloudflare's "verify you are human" challenge on staging; local and Docker runs from India do not. Post-handover, a red CI on Cloudflare grounds is expected — the mandate is <em>coverage</em>, verifiable locally/Docker, not a permanently-green hosted pipeline. The browser-side bypass header is wired; the Cloudflare-side rule (disable Bot Fight Mode / broaden the WAF skip) is an infra task.</p></div>

<h2>6. The report as the deliverable</h2>
<p>Every run uploads the Allure report as an artifact (30-day retention) and can email a summary + link. Link mode is used in CI to dodge corporate <code>.zip</code>-attachment filters. <code>test-pipeline.yml</code> additionally publishes to GitHub Pages for click-only stakeholder access.</p>
""")

doc(5, "docker", "Engineering & process",
    "Docker",
    "The compose services, the profiles that gate them, and containerised runs.",
    r"""
<h2>1. Services in <code>docker-compose.yml</code></h2>
<table>
  <thead><tr><th>Service</th><th>Profile</th><th>Purpose</th></tr></thead>
  <tbody>
    <tr><td><code>postgres-dev</code></td><td><em>(none — always)</em></td><td>Metrics database the run publisher writes to.</td></tr>
    <tr><td><code>allure</code></td><td><em>(none — always)</em></td><td>Allure report service for hosted viewing.</td></tr>
    <tr><td><code>selenium-hub</code> + 3 nodes</td><td><code>grid</code></td><td>Self-hosted cross-browser Grid.</td></tr>
    <tr><td><code>zap</code></td><td><code>security</code></td><td>OWASP ZAP daemon for DAST.</td></tr>
    <tr><td><code>healenium-postgres</code>, <code>hlm-backend</code></td><td><code>healenium</code></td><td>Self-healing locator backend.</td></tr>
  </tbody>
</table>

<h2>2. Profiles are load-bearing</h2>
<div class="callout danger"><p><span class="tag">The ISSUE-021 lesson</span> A Compose service with <b>no profile starts with everything</b>. The two Healenium services once had no profile, so <code>docker compose --profile grid up -d</code> tried to pull a Healenium image that no longer resolves — and one failed pull aborts the whole <code>up</code>, so the Grid never started and 15/15 tests failed with <code>SessionNotCreatedException</code>. Every heavyweight, opt-in service now sits behind a profile. Confirm what a command will start before running it:</p>
<pre><code>docker compose --profile grid config --services   # expect: hub + 3 nodes (+ allure, postgres-dev)</code></pre></div>

<h2>3. Bringing services up</h2>
<pre><code>docker compose up -d postgres-dev                 # just the metrics DB
docker compose --profile grid up -d               # Grid: hub + chrome/firefox/edge
docker compose --profile security up -d zap       # ZAP daemon (port 8090)
docker compose --profile healenium up -d          # Healenium backend (also needs -Dhealenium.enabled=true)
docker compose --profile grid down                # tear a profile down</code></pre>

<h2>4. Containerised test runs (headed video for free)</h2>
<p>A container ships with an <b>Xvfb virtual display</b>, so a "headed" browser paints somewhere the Monte recorder can see it — meaning <em>video recording works in Docker even for an unattended run</em>, unlike a headless local run. <code>docker/entrypoint.sh</code> loops over browsers:</p>
<pre><code>for browser in $BROWSERS; do
    mvn test -Dbrowser="$browser" ...     # results accumulate into one Allure report
done</code></pre>
<pre><code>./scripts/run-docker-headed.ps1   # convenience wrapper around the container run</code></pre>

<h2>5. Two Postgres instances — not a mistake</h2>
<ul>
  <li><code>postgres-dev</code> (<code>postgres:16-alpine</code>) — stores <b>test-run metrics</b>.</li>
  <li><code>healenium-postgres</code> (<code>postgres:13</code>) — stores <b>locator-healing data</b>, isolated from metrics.</li>
</ul>
<p>They are separate on purpose: unrelated data, different lifecycles, and Healenium pins an older Postgres major.</p>

<h2>6. ZAP port mapping — the one that bites</h2>
<div class="callout warn"><p><span class="tag">Must be <code>8090:8090</code></span> ZAP's single port serves both its REST API and its HTTP proxy, telling them apart by the <code>Host</code> header. A rewriting map like <code>8090:8080</code> makes ZAP not recognise itself as the target, treat the API call as proxy traffic, and return a baffling self-inflicted <code>502</code>. Keep the inside and outside ports identical.</p></div>

<h2>7. Kubernetes</h2>
<p>Kubernetes is covered as a <em>generic foundations primer</em> in the team-transition material, but is <b>not implemented</b> in this repo. Docker Compose is the actual container orchestration here. Treat any K8s manifests as illustrative, not deployed.</p>
""")

doc(7, "healenium", "Engineering & process",
    "Healenium — Self-Healing Locators",
    "What self-healing does, why it is off by default, and how to turn it on safely.",
    r"""
<h2>1. What Healenium does</h2>
<p>Healenium wraps the WebDriver so that when a locator stops matching (the app changed an attribute), it can <b>recover</b> the element from previously-observed node attributes and log a healing event, instead of failing outright. It is a resilience net for locator drift — not a licence to write weak locators.</p>

<h2>2. Off by default — and why (DEC-0010 / ADR-0010)</h2>
<div class="callout warn"><p><span class="tag">Opt-in twice over</span> Healing is disabled unless <b>both</b> are true:</p>
<pre><code>docker compose --profile healenium up -d     # 1) the backend is running
.\mvnw.cmd test -Dhealenium.enabled=true      # 2) the framework is told to use it</code></pre>
<p>With <code>healenium.enabled=false</code> (the default), <code>DriverFactory</code> returns the raw WebDriver untouched — byte-identical behaviour to not having Healenium at all.</p></div>
<p>It is off by default because a self-healing net can <em>mask</em> a real regression: a locator that "heals" is a locator the app broke, and you want to know about that during active development rather than have it silently paper over the change.</p>

<h2>3. Configuration (<code>src/test/resources/healenium.properties</code>)</h2>
<table>
  <thead><tr><th>Key</th><th>Value</th><th>Meaning</th></tr></thead>
  <tbody>
    <tr><td><code>hlm.server.url</code></td><td><code>http://localhost:7878</code></td><td>The <code>hlm-backend</code> REST endpoint (must match the compose port mapping).</td></tr>
    <tr><td><code>recovery-tries</code></td><td><code>5</code></td><td>How many candidate elements Healenium tries when healing.</td></tr>
    <tr><td><code>score-cap</code></td><td><code>0.6</code></td><td>Minimum similarity score to accept a healed match (below this it fails rather than guess).</td></tr>
    <tr><td><code>imitate-hover</code></td><td><code>false</code></td><td>Whether to simulate hover during healing.</td></tr>
    <tr><td><code>select-node-attribute</code></td><td><code>id,name,type,value,text</code></td><td>Which attributes fingerprint a node for later recovery.</td></tr>
  </tbody>
</table>
<div class="callout note"><p><span class="tag">Read directly</span> These values are read by the Healenium client itself, not through <code>ConfigReader</code>. The <code>healenium.server.url</code> key in the env properties files is a reference copy only.</p></div>

<h2>4. The backend stack</h2>
<ul>
  <li><code>hlm-backend</code> — the REST service <code>SelfHealingDriver</code> talks to.</li>
  <li><code>healenium-postgres</code> (<code>postgres:13</code>) — its own database, separate from the metrics DB.</li>
</ul>
<div class="callout danger"><p><span class="tag">Image tag caveat</span> <code>healenium/hlm-backend:latest</code> does not currently resolve on Docker Hub. Because Healenium is off by default this is inert — but whoever opts in must first pin a tag that exists. This is also why both services now sit behind the <code>healenium</code> profile (ISSUE-021): an unresolvable image must never be pulled by an unrelated command.</p></div>

<h2>5. How the driver is wrapped</h2>
<pre><code>// DriverFactory.wrapWithHealenium(...)
// healenium.enabled=false → log and return the raw WebDriver (default path)
// healenium.enabled=true  → return new SelfHealingDriver(raw), pointed at hlm.server.url</code></pre>

<h2>6. When to actually use it</h2>
<table>
  <thead><tr><th>Situation</th><th>Healing?</th></tr></thead>
  <tbody>
    <tr><td>Active development / building coverage</td><td><b>Off.</b> You want breakages visible.</td></tr>
    <tr><td>A large, stable suite against a rapidly-restyled app</td><td>Consider on — to ride out cosmetic churn while you catch up.</td></tr>
    <tr><td>Debugging a specific flaky locator</td><td>Off. Fix the locator (see the <b>Locator Strategies</b> guide).</td></tr>
  </tbody>
</table>
""")

doc(1, "okf-agentic-engineering", "Engineering & process",
    "OKF — Organizational Knowledge Framework for Agentic Engineering",
    "The project's durable memory: how it is structured, and how it lets AI agents build the framework across sessions.",
    r"""
<h2>1. What the OKF is</h2>
<p>The <b>Organizational Knowledge Framework</b> (<code>docs/okf/</code>) is the authoritative, living memory of the project — the single place that answers <em>"why is it like this, what is done, what is broken, and what was decided."</em> It is not narrative documentation; it is a set of structured, cross-referenced registers designed to be read and written by both humans and AI agents.</p>
<div class="callout note"><p><span class="tag">Why it matters for handover</span> Code tells you <em>what</em>; git tells you <em>when</em>. The OKF is the only place that records <em>why</em> — the decisions, the dead ends, the constraints that are not derivable from the source. For a new SDET (or a new agent), it is the fastest path to context.</p></div>

<h2>2. The topic files</h2>
<table>
  <thead><tr><th>File</th><th>Holds</th></tr></thead>
  <tbody>
    <tr><td><code>project-overview.md</code></td><td>Goals, stakeholders, current capability snapshot.</td></tr>
    <tr><td><code>system-architecture.md</code> / <code>project-structure.md</code></td><td>Layout, patterns, directory map.</td></tr>
    <tr><td><code>development-standards.md</code></td><td>Coding conventions and practices.</td></tr>
    <tr><td><code>testing-strategy.md</code> / <code>test-case-roadmap.md</code></td><td>Levels, coverage, runners, what is planned.</td></tr>
    <tr><td><code>CurrentStatus.md</code></td><td>Live implementation state and verified results.</td></tr>
    <tr><td><code>decisions-log.md</code></td><td>Index of ADRs + inline decisions (DEC-####).</td></tr>
    <tr><td><code>known-issues.md</code></td><td>Open/fixed defects and drifts (ISSUE-###).</td></tr>
    <tr><td><code>risks.md</code> / <code>task-backlog.md</code></td><td>Risks (RISK-*) and pending work (TASK-*).</td></tr>
    <tr><td><code>learnings.md</code></td><td>Reusable lessons and troubleshooting.</td></tr>
    <tr><td><code>changelog.md</code></td><td><b>Append-only</b> history — never rewrite past entries.</td></tr>
    <tr><td><code>delta-updates.md</code></td><td>Incremental / transient change tracking.</td></tr>
  </tbody>
</table>

<h2>3. Traceability IDs — the connective tissue</h2>
<p>Every meaningful thing has a stable ID, and entries cross-reference each other so a claim can always be traced to evidence:</p>
<table>
  <thead><tr><th>Prefix</th><th>Register</th><th>Example use</th></tr></thead>
  <tbody>
    <tr><td><code>REQ-</code></td><td>Requirements</td><td>A capability the suite must verify.</td></tr>
    <tr><td><code>TASK-</code></td><td>Backlog</td><td>A unit of work.</td></tr>
    <tr><td><code>DEC-</code> / ADR</td><td>Decisions</td><td>An architectural choice + its rationale.</td></tr>
    <tr><td><code>STAT-</code></td><td>Status/milestones</td><td>A completed, verified achievement.</td></tr>
    <tr><td><code>ISSUE-</code></td><td>Known issues</td><td>A defect (app-side or framework-side).</td></tr>
    <tr><td><code>RISK-</code></td><td>Risks</td><td>Something that could go wrong + mitigation.</td></tr>
  </tbody>
</table>
<p>A decision cites the tasks and issues it resolves; an issue cites the status entry that fixed it. This is what makes the OKF a <em>graph</em>, not a pile of notes.</p>

<h2>4. The OKF as an agent harness</h2>
<p>This framework was built largely by AI agents (an orchestrator directing capable models) across many sessions. A model's context window is finite and resets between sessions — so the OKF is the <b>externalised, durable memory</b> that lets an agent pick up exactly where the last one left off. In practice:</p>
<ul>
  <li><b>Read on entry.</b> An agent starting work reads <code>CurrentStatus.md</code>, <code>known-issues.md</code> and <code>decisions-log.md</code> to rebuild context no window can hold.</li>
  <li><b>Write on exit.</b> Every substantive change appends to <code>changelog.md</code> and updates the relevant register, so the <em>next</em> agent inherits the reasoning, not just the diff.</li>
  <li><b>Decisions are load-bearing.</b> When a choice is reversed (e.g. DEC-0014 was deferred then implemented the same day once a verification oracle appeared), the log records both the reversal <em>and</em> the evidence that justified it — so the reasoning is auditable, not lost.</li>
  <li><b>Append-only discipline.</b> The changelog is never rewritten. History that can be silently edited cannot be trusted as memory.</li>
</ul>
<div class="callout warn"><p><span class="tag">Rule for the next maintainer</span> Treat the OKF as you would production code. If you change behaviour, record <em>why</em> in the OKF in the same commit. A fix with no OKF trail is half-done — the next person (or agent) will re-discover the same dead end you just escaped.</p></div>

<h2>5. Governance (DEC-0009 / ADR-0009)</h2>
<p>The OKF's authority, structure and append-only rules are themselves an accepted decision. <code>OKF_INDEX.md</code> is the entry point and lists the primary source documents; <code>README.md</code> in <code>docs/okf/</code> is the navigation hub.</p>
""")

doc(2, "architecture-decision-records", "Engineering & process",
    "Architecture Decision Records (ADRs)",
    "Every accepted architectural decision, its rationale, and where it lives.",
    r"""
<h2>1. How decisions are recorded</h2>
<p>Formal decisions live as numbered ADRs under <code>docs/adr/</code>; <code>docs/okf/decisions-log.md</code> is the index and also holds decisions captured inline (DEC-####) where a full ADR was not warranted. Each carries a status, the decision, the rationale, and traceability IDs.</p>

<h2>2. The accepted ADRs</h2>
<table>
  <thead><tr><th>ID</th><th>Decision</th><th>Why it matters</th></tr></thead>
  <tbody>
    <tr><td><b>ADR-0001</b></td><td>Keep the dashboard in the monorepo for now; extract when deployment/secrets/team-size demand it.</td><td>Avoids premature repo-splitting; DEC-0013 later realised the extraction trigger.</td></tr>
    <tr><td><b>ADR-0002</b></td><td>Stack: Java 25, Maven, Selenium, Cucumber, TestNG, AssertJ, Allure.</td><td>The foundational technology choice.</td></tr>
    <tr><td><b>ADR-0003</b></td><td>Page Object + Component Object model; assertions live in step definitions.</td><td>Separation of concerns — page objects interact, steps assert.</td></tr>
    <tr><td><b>ADR-0004</b></td><td>TestNG + Cucumber with ThreadLocal WebDriver and scenario-level DataProvider parallelism.</td><td>The whole parallel-execution model rests on this.</td></tr>
    <tr><td><b>ADR-0005</b></td><td>Config precedence: JVM property → env var → properties file (+ local override).</td><td>The command line must win — the rule ISSUE-020 restored.</td></tr>
    <tr><td><b>ADR-0006</b></td><td>Allure/Cucumber reports + Postgres metrics consumed by the dashboard.</td><td>Reporting and the metrics pipeline.</td></tr>
    <tr><td><b>ADR-0007</b></td><td>Apply a Cloudflare automation-bypass header for Chromium when configured.</td><td>Lets hosted runners past the bot challenge (ISSUE-001).</td></tr>
    <tr><td><b>ADR-0008</b></td><td>Real Safari only on macOS; substitute a configured Chromium fallback elsewhere, annotated in the report.</td><td>Honest cross-browser reporting where WebKit is unavailable.</td></tr>
    <tr><td><b>ADR-0009</b></td><td>Use the OKF as authoritative project memory (topic files, ADRs, append-only changelog).</td><td>Governs how knowledge is recorded — see the OKF guide.</td></tr>
    <tr><td><b>ADR-0010</b></td><td>Wrap the driver in Healenium's SelfHealingDriver, gated off by default behind <code>healenium.enabled</code>.</td><td>Self-healing as an opt-in net, not a default.</td></tr>
  </tbody>
</table>

<h2>3. Inline decisions (decisions-log.md)</h2>
<table>
  <thead><tr><th>ID</th><th>Decision</th></tr></thead>
  <tbody>
    <tr><td><b>DEC-0011</b></td><td>Email the Allure report after every run; config from env/<code>.env</code>. CI uses link mode to dodge corporate zip filters.</td></tr>
    <tr><td><b>DEC-0012</b></td><td>Host Allure reports for click-only stakeholder access (GitHub Pages primary; allure-docker-service locally).</td></tr>
    <tr><td><b>DEC-0013</b></td><td>Extract the dashboard to its own repo (realising the ADR-0001 trigger); embed the Playwright/TS POC at <code>automation-ts/</code>.</td></tr>
    <tr><td><b>DEC-0014</b></td><td><b>Deferred, then implemented and verified the same day.</b> Parallel cross-browser in one JVM. Deferred while its failure mode (a plausible-looking wrong answer) could not be proven; implemented once the Grid supplied a verification oracle (per-node session counts). Rollback is deleting one file.</td></tr>
  </tbody>
</table>

<h2>4. The DEC-0014 story — why it is the model to follow</h2>
<div class="callout note"><p><span class="tag">Decisions can reverse — on evidence</span> DEC-0014 was first <em>deferred</em> because its worst outcome was a silent wrong answer (a green report that secretly ran one browser three times), which could not be verified before handover. Building the Grid changed the calculus: each node serves only its own browser, so per-node session counts <em>prove</em> cross-browser actually happened. With a way to verify, the risk dropped from "unacceptable" to "moderate and testable," and it was implemented additively (unset behaviour is byte-identical) with a one-file rollback. The log records the deferral, the reversal, and the evidence — which is exactly how a decision record should behave.</p></div>

<h2>5. Writing a new ADR</h2>
<ol>
  <li>Copy the format of an existing file in <code>docs/adr/</code> (context → decision → alternatives → consequences → status).</li>
  <li>Add a row to <code>decisions-log.md</code> with the ID, status, decision, owner and traceability.</li>
  <li>Cross-reference the tasks/issues it resolves so the OKF graph stays connected.</li>
</ol>
""")

doc(3, "build-history-release-notes", "Engineering & process",
    "Build History & Release Notes",
    "The milestones that got the framework to handover, newest first.",
    r"""
<div class="callout note"><p><span class="tag">Source of truth</span> This is a curated summary. The complete, append-only record is <code>docs/okf/changelog.md</code> — consult it for the full detail behind any line here.</p></div>

<h2>Late July 2026 — Handover hardening</h2>
<ul>
  <li><b>Personal data scrubbed.</b> The departing owner's phone and email removed from every tracked file; secrets (<code>pilot.phone</code>) converted to fail-loud config keys with no defaults. <em>(History still contains them — a filter-repo pass is available if the team wants them gone from history too.)</em></li>
  <li><b>Payment / Security suite reworked.</b> Was failing 6/6 in its Background (asked a homepage for a checkout form). Rewritten to the real flow — sidebar → accessories → Secure Checkout → Razorpay modal → guest contact — 5 green scenarios; card/decline/confirmation left as documented <code>@Manual</code>. Guest contact is filled but never submitted (safety rail against SMSing a real number).</li>
  <li><b>Selenium Grid + DEC-0014 delivered.</b> Self-hosted hub + chrome/firefox/edge nodes; parallel cross-browser in one JVM via a thread-local browser pin, verified by per-node session counts.</li>
  <li><b>ISSUE-021 fixed.</b> Healenium services had no Compose profile, so the Grid command tried to pull an unresolvable image and aborted — 15/15 false failures. Every heavyweight service now sits behind a profile.</li>
  <li><b>Repository layout split.</b> <code>docs/</code> holds <code>.md</code> source; <code>output/</code> holds all generated pdf/html (cheatsheet, proposals, these handover guides).</li>
  <li><b>Zephyr token scrubbed from history.</b> A leaked API token removed via <code>git filter-repo</code> after a verified backup bundle; all clones invalidated (re-clone, don't pull). Rotation in SmartBear is a team task.</li>
</ul>

<h2>Mid July 2026 — Security, cross-env, RCA</h2>
<ul>
  <li><b>OWASP ZAP DAST integrated.</b> Daemon-mode ZAP driven over its REST API; OWASP Top 10 2021→2025 tag-driven classification; production is passive/baseline-only by design, active scans gated to staging with sign-off. Scheduled weekly (Sat staging, Sun prod).</li>
  <li><b>Big RCA pass.</b> ~90% of a 71-failure run proved to be framework bugs, not app bugs — cookie-bar interception, hidden responsive twins, tag-qualified testids matching zero nodes, assertions sampling state instead of waiting. Real app defects filed (ISSUE-016…019, incl. a High-severity privacy draft-restore with an on-demand repro probe).</li>
  <li><b>ISSUE-020 fixed.</b> <code>BaseRunner</code> was silently overriding <code>-Denv</code> with the suite XML — the framework could test the wrong environment while claiming otherwise. The CLI now wins.</li>
  <li><b>Headless by default; guarded video.</b> Video records only where it can capture something real (headed/Docker), skipping headless with a warning.</li>
  <li><b>@StagOnly / @ProdOnly</b> environment scopes added.</li>
</ul>

<h2>Early July 2026 — Coverage build-out</h2>
<ul>
  <li>Page-object model completed across timepieces, accessories, Timepieces World, content and legal pages, with a repo-wide <code>data-testid</code> locator rollout to kill brittle selectors.</li>
  <li>BDD + TDD suites, parallel execution via ThreadLocal driver + DataProvider, retry analysis, Allure reporting, and the Postgres metrics pipeline.</li>
  <li>Search-relevance analysis harness (deterministic + exploratory, run N times) surfaced four confirmed search defects (ISSUE-009…012).</li>
  <li>Team-transition guides authored (CI/CD, Docker/K8s, Healenium, framework design) and the QA infrastructure proposal.</li>
</ul>

<h2>June 2026 — Foundations</h2>
<ul>
  <li>Framework bootstrapped: Java 25 / Maven / Selenium 4.44 / Cucumber 7 / TestNG 7 / Rest-Assured / Allure.</li>
  <li>OKF established as authoritative project memory; ADRs 0001–0010 recorded.</li>
  <li>Config resolution, DriverFactory (ThreadLocal), Hooks, reporting and the dashboard POC stood up.</li>
</ul>

<h2>Known state at handover</h2>
<ul>
  <li>Local/Docker runs from India are the reliable execution path; hosted CI can show red on Cloudflare grounds (ISSUE-001) — coverage is the mandate, not a green hosted pipeline.</li>
  <li>Open app-side defects tracked in <code>known-issues.md</code> (privacy draft-restore, checkout redirect, contact-step validation, alt-text, watch-case cart join).</li>
  <li>The login/OTP scenario is <code>@KnownBroken</code> pending re-mapping after a dev-side rework (ISSUE-023).</li>
</ul>
""")

doc(16, "dashboard-setup", "Dashboard",
    "Dashboard — Setup Guide",
    "Standing up the React + Node trends dashboard locally and for deployment.",
    r"""
<h2>1. What the dashboard is</h2>
<p>A React + Node app (<code>dashboard/</code>) over the same Postgres <code>test_runs</code> table the test publisher writes to. It provides historical trends, per-run history with links to published Allure reports, on-demand flow execution (dispatches <code>test-pipeline.yml</code>), and email/password auth (bcrypt + JWT).</p>
<pre><code>dashboard/
  server/   Node + TypeScript API (Express, pg, JWT)
  web/      React + Vite SPA (TypeScript)</code></pre>

<h2>2. Prerequisites</h2>
<ul>
  <li>Node 20+ (developed on Node 26).</li>
  <li>Postgres reachable via a URL. Locally the repo's compose already provides one: <code>docker compose up -d postgres-dev</code> (db <code>timepieces_metrics</code>, user <code>postgres</code> / <code>devpassword</code>).</li>
</ul>

<h2>3. Server</h2>
<pre><code>cd dashboard/server
npm install
cp .env.example .env          # set JWT_SECRET, DATABASE_URL, GITHUB_TOKEN, ...
npm run db:init               # creates dashboard_users (+ test_runs if missing)
npm run user:create -- you@timepieces-d2c.example 'a-strong-password' 'Your Name'
npm run dev                   # API on http://localhost:4000</code></pre>
<table>
  <thead><tr><th>Env var</th><th>Purpose</th></tr></thead>
  <tbody>
    <tr><td><code>JWT_SECRET</code></td><td>Signs login tokens — use a long random string.</td></tr>
    <tr><td><code>DATABASE_URL</code></td><td>Postgres URL; falls back to <code>NEON_DATABASE_URL</code>.</td></tr>
    <tr><td><code>GITHUB_TOKEN</code></td><td>PAT with <code>actions:write</code> — required to dispatch flows.</td></tr>
    <tr><td><code>GITHUB_OWNER</code> / <code>GITHUB_REPO</code> / <code>GITHUB_WORKFLOW_FILE</code></td><td>Which workflow the "run flow" buttons trigger.</td></tr>
    <tr><td><code>CORS_ORIGIN</code></td><td>The web app's origin (default <code>http://localhost:5173</code>).</td></tr>
  </tbody>
</table>
<div class="callout note"><p><span class="tag">Flow execution is opt-in</span> Until <code>GITHUB_TOKEN</code> is set, the run buttons are greyed out (<code>executionEnabled:false</code>). Trends and history work without it — read-only mode is fully functional.</p></div>

<h2>4. Web</h2>
<pre><code>cd dashboard/web
npm install
npm run dev          # SPA on http://localhost:5173, proxies /api → :4000
npm run build        # production build → web/dist/</code></pre>
<p>Serve <code>dist/</code> behind the same origin as the API (or reverse-proxy <code>/api</code> → the Node server). Hosting at <code>dev.timepieces-world.example</code> is handled separately.</p>

<h2>5. API surface (all but login/health require a Bearer JWT)</h2>
<table>
  <thead><tr><th>Method · Path</th><th>Notes</th></tr></thead>
  <tbody>
    <tr><td><code>POST /api/auth/login</code></td><td><code>{email,password}</code> → <code>{token,user}</code></td></tr>
    <tr><td><code>GET /api/runs</code></td><td>History; <code>?env=&amp;browser=&amp;limit=&amp;offset=</code></td></tr>
    <tr><td><code>GET /api/runs/summary</code></td><td>All-time headline stats</td></tr>
    <tr><td><code>GET /api/runs/trends?days=30</code></td><td>Daily aggregates for charting</td></tr>
    <tr><td><code>GET /api/flows</code> · <code>POST /api/flows/:key/run</code></td><td>Flow definitions; dispatch a flow (<code>{env,browser}</code>)</td></tr>
  </tbody>
</table>
<div class="callout warn"><p><span class="tag">Repo split trigger</span> The dashboard lives in the monorepo for now (ADR-0001 / DEC-0013). Recommend extracting it to its own repo once it actually deploys to <code>dev.timepieces-world.example</code> — that is the deployment/secrets trigger the ADR names.</p></div>
""")

doc(15, "dashboard-customization", "Dashboard",
    "Dashboard — Customization Guide",
    "How to change the flows, charts, theming and data the dashboard shows.",
    r"""
<h2>1. Flows → Cucumber tags</h2>
<p>The "run a flow" buttons map flow keys to Cucumber tag expressions in <code>dashboard/server/src/flows.config.ts</code>. This is the first thing to adjust as the suite's tags evolve:</p>
<table>
  <thead><tr><th>Flow</th><th>Tag expression</th></tr></thead>
  <tbody>
    <tr><td>Login Flow</td><td><code>@Login</code></td></tr>
    <tr><td>Checkout Flow</td><td><code>@Checkout</code></td></tr>
    <tr><td>Complete End-to-End Flow</td><td><code>@Smoke or @Regression</code></td></tr>
  </tbody>
</table>
<pre><code>// dashboard/server/src/flows.config.ts — add or edit a flow
{ key: "payment", label: "Payment Flow", tags: "@Payment and @Regression" }</code></pre>
<div class="callout note"><p><span class="tag">Keep tags real</span> A flow whose tag matches zero scenarios dispatches a run that does nothing. After editing, confirm the tag expression selects what you expect with a local <code>--dry-run</code>-style check.</p></div>

<h2>2. Charts and trends</h2>
<p>Trend data comes from <code>GET /api/runs/trends?days=N</code> — daily aggregates over the <code>test_runs</code> table. To add or change a chart in the React SPA (<code>dashboard/web</code>):</p>
<ol>
  <li>Extend the trends query/endpoint if you need a new aggregate (e.g. per-browser pass rate).</li>
  <li>Consume it in a web component; the SPA is React + Vite + TypeScript.</li>
  <li>Keep the read path auth-gated — every route except login/health requires a JWT.</li>
</ol>

<h2>3. What the dashboard is meant to show (planned scope)</h2>
<p>The intended shape, per the dashboard requirements, is a stakeholder-facing view with:</p>
<ul>
  <li><b>Trends</b> — pass rate and volume over time.</li>
  <li><b>On-demand flows</b> — trigger predefined runs and watch live status.</li>
  <li><b>Execution history</b> — per-run results linking to the published Allure reports.</li>
  <li><b>Auth-gated access</b> — email/password, so it can be shared without exposing raw pipelines.</li>
</ul>
<p>Hosting (at <code>dev.timepieces-world.example</code>) is out of the framework's scope and handled separately.</p>

<h2>4. Theming and branding</h2>
<ul>
  <li>The SPA is a standard Vite React app — global styles and a theme layer live in <code>dashboard/web/src</code>. Adjust tokens/variables there rather than per-component for a consistent restyle.</li>
  <li>Keep charts legible in both a projector/boardroom setting and a laptop — trends are the headline artefact for non-technical stakeholders.</li>
</ul>

<h2>5. Data contract — don't break the publisher</h2>
<div class="callout warn"><p><span class="tag">The table is shared</span> The dashboard reads the exact <code>test_runs</code> table the Java <code>TestMetricsPublisher</code> writes. If you change the schema for a new chart, update <b>both</b> the publisher (writer) and the dashboard (reader) together, or one side will silently break. The publisher already tolerates the DB being absent (it logs and moves on), so a schema mismatch will not fail tests — it will just produce wrong or empty charts, which is harder to notice.</p></div>

<h2>6. Where it will grow next</h2>
<p>The natural next features — and where a new owner would extend — are per-flow drill-downs, flaky-test surfacing (tie into the invocation-count analysis), and richer filters (env × browser × tag). All of these are additive over the existing <code>/api/runs*</code> surface.</p>
""")

doc(19, "reverse-requirements", "Requirements",
    "Reverse Requirements &amp; Traceability Matrix",
    "Requirements recovered from the live product and the test suite, mapped to the tests that verify them.",
    r"""
<h2>1. Why "reverse" requirements</h2>
<p>Timepieces did not hand over a complete written specification. Much of what the suite verifies was <b>reverse-engineered</b> from the live product and from behaviour confirmed with the product owner — then written down so coverage is auditable. This document is that recovered specification plus a <b>Requirements Traceability Matrix (RTM)</b> linking each requirement to the tests that exercise it.</p>
<div class="callout note"><p><span class="tag">Two requirement sources</span> Forward requirements (from the PRD — see guide 20) and reverse requirements (recovered here) together define the coverage contract. Where they conflict, the live product's confirmed behaviour wins and the discrepancy is filed as an ISSUE.</p></div>

<h2>2. Recovered requirements → tests (RTM)</h2>
<table>
  <thead><tr><th>REQ</th><th>Requirement (recovered)</th><th>Verified by</th><th>Notes / defects</th></tr></thead>
  <tbody>
    <tr><td>REQ-AUTH-01</td><td>Phone + OTP login: region dropdown sets dialing code; Send OTP routes to the OTP screen.</td><td><code>User_Authentication_And_OTP_Verification.feature</code></td><td><span class="pill">@KnownBroken</span> ISSUE-023 — dev reworked the page.</td></tr>
    <tr><td>REQ-AUTH-02</td><td>Staging is gated by a password; production is not.</td><td><code>Staging_Password_Bypass.feature</code> (@StagOnly)</td><td>Env-scoped so it never runs on prod.</td></tr>
    <tr><td>REQ-AUTH-03</td><td>OTP verification should rate-limit attempts.</td><td><code>OTP_Bruteforce_Security.feature</code> (@OtpBruteForce)</td><td>ISSUE-002 — no cap exists (app defect).</td></tr>
    <tr><td>REQ-HOME-01</td><td>Homepage renders hero, drops, stories, brand story, partners, last-release, footer.</td><td><code>Homepage_Content_Sections.feature</code>, <code>HomepageContentSmokeTest</code></td><td>—</td></tr>
    <tr><td>REQ-CAT-01</td><td>Accessories reachable from the sidebar; PDPs render title/price/compatibility.</td><td><code>Accessory_Product_Details.feature</code>, <code>Payment_Integration_Validation.feature</code></td><td>—</td></tr>
    <tr><td>REQ-CAT-02</td><td>Strap PDPs offer a dial-try carousel and a clasp-upgrade cross-sell.</td><td><code>Accessory_Strap_Configurator.feature</code></td><td>—</td></tr>
    <tr><td>REQ-CART-01</td><td>Add-to-cart opens the drawer; the bag totals and persists across navigation.</td><td><code>Cart_Bag_Management.feature</code>, payment scenarios</td><td>ISSUE-022 — watch-case add did not join a strap+clasp bag.</td></tr>
    <tr><td>REQ-PAY-01</td><td>Secure Checkout opens the Razorpay modal in-page; a guest is asked for contact details first.</td><td><code>Payment_Integration_Validation.feature</code></td><td>Card entry onward is third-party — <span class="pill">@Manual</span>.</td></tr>
    <tr><td>REQ-PAY-02</td><td>International routes to <code>/checkout</code>; the modal offers only cards + Apple Pay; unsupported countries are blocked.</td><td><code>International_Payment_Rules.feature</code></td><td>Prices 1.5× domestic (manual Zephyr check).</td></tr>
    <tr><td>REQ-CHK-01</td><td>Checkout is idempotent under a double-click.</td><td><code>Checkout_Idempotency.feature</code>, <code>IdempotencyApiTest</code></td><td>UI part <span class="pill">@Manual</span> (ISSUE-018); server guarantee covered by the API test.</td></tr>
    <tr><td>REQ-SEARCH-01</td><td>Storefront search returns relevant, query-dependent results.</td><td><code>SearchApiTest</code>, <code>SearchApiExploratoryTest</code></td><td>ISSUE-009…012 — fallback returns fixed sets; missing brand metadata.</td></tr>
    <tr><td>REQ-CONTENT-01</td><td>Brand/legal/Timepieces-World content pages render correctly.</td><td><code>Brand_Content_Pages</code>, <code>Legal_Policy_Pages</code>, <code>Timepieces_World_*</code></td><td>ISSUE-016 — Monarch alt-text defect caught here.</td></tr>
    <tr><td>REQ-ACCT-01</td><td>Authenticated account/orders/wishlist views.</td><td><code>Account_Orders_Management</code>, <code>Wishlist_And_Account_Management</code></td><td>@AuthGated; needs <code>pilot.phone</code> configured.</td></tr>
    <tr><td>REQ-PRIV-01</td><td>A profile draft must not restore into a brand-new anonymous session.</td><td><code>ProfileDraftRestorePrivacyProbeTest</code></td><td>ISSUE-019 (High) — probe reproduces the leak on demand.</td></tr>
    <tr><td>REQ-SEC-01</td><td>OWASP Top 10 exposure scanned regularly.</td><td><code>OwaspTop10ZapScanTest</code></td><td>Prod passive-only; active gated to staging.</td></tr>
  </tbody>
</table>

<h2>3. How to keep the RTM honest</h2>
<ul>
  <li>Every new <code>.feature</code> or TDD class should map to a REQ (recovered or PRD). If it maps to none, the requirement is missing — write it.</li>
  <li>When a requirement is only partially automated (e.g. a third-party or gated step), say so in the Notes column and tag the scenario <code>@Manual</code> — never leave a gap looking covered.</li>
  <li>When the product's real behaviour contradicts a requirement, the requirement stands and the gap becomes an ISSUE; the RTM row points at both.</li>
</ul>
<div class="callout warn"><p><span class="tag">Coverage ≠ green</span> A row can be "verified by" a test that currently fails by design (a privacy probe, a bruteforce probe) — because the requirement is <em>correct behaviour</em> and the test asserts it. Those failures are findings, not regressions.</p></div>
""")

doc(20, "product-requirements", "Requirements",
    "Product Requirements Document (PRD) — to current",
    "The forward product requirements for identity, homepage and the automation framework.",
    r"""
<div class="callout note"><p><span class="tag">Status</span> This is the product-owner-authored PRD as it stands. Going forward, new features and PRD updates are handled manually. For requirements <em>recovered</em> from the live product and the mapping to tests, see the <b>Reverse Requirements &amp; Traceability</b> guide.</p></div>

<h2>1. Identity &amp; Authentication</h2>
<h3>1.1 Phone-number OTP login (passwordless)</h3>
<p><b>User flow:</b> land on the login page (Welcome message) → select country (sets dialing code) → enter mobile number → Send OTP → CAPTCHA validation (Cloudflare Turnstile) → OTP verification screen.</p>
<h4>Acceptance criteria</h4>
<ul>
  <li>The Welcome message is clearly visible on load.</li>
  <li>The region dropdown filters and applies the correct dialing code.</li>
  <li>Send OTP triggers CAPTCHA validation (unless bypassed in staging).</li>
  <li>A successful OTP request routes immediately to the OTP entry screen.</li>
</ul>

<h2>2. Homepage</h2>
<h3>2.1 Header</h3>
<ul>
  <li><b>Hamburger (top-left):</b> side-drawer with Timepieces, Accessories (New), Friends &amp; Partners, Timepieces House, The Wall, Updates, Timepieces World; plus Sign in, Contact, Wishlist, Track Orders, and a region/language selector.</li>
  <li><b>Logo (top-center)</b>, <b>icon array (top-right):</b> Search (planned), Stories, Profile, Wishlist, Cart/Bag.</li>
</ul>
<h3>2.2 Main sections (top → bottom)</h3>
<ol>
  <li>Hero — video background + Early Access CTA.</li>
  <li>Upcoming Drops.</li>
  <li>Collections.</li>
  <li>Quotes.</li>
  <li>Friends and Partners.</li>
  <li>Rotating watch component ("Crafted with intent", scroll-driven).</li>
  <li>Timepieces Advantage (USP cards).</li>
  <li>Emotions in Motion (social/lifestyle grid).</li>
  <li>Featured In (press).</li>
</ol>
<h3>2.3 Footer</h3>
<p>Global footer with categorized navigation to all active and utility pages.</p>

<h2>3. Automation Framework (as specified)</h2>
<h3>3.1 Technology stack</h3>
<p>Java 25 · Maven · Selenium (web), Appium (mobile), Rest-Assured (API) · Healenium (self-healing) · Cucumber-JVM (BDD) · TestNG (parallel + ThreadLocal driver) · Allure · Log4j2 · Monte screen recorder.</p>
<h3>3.2 Design principles</h3>
<ul>
  <li><b>BDD</b> — Gherkin scenarios as living documentation.</li>
  <li><b>Page Object + Component Object Model</b> — locators abstracted into classes.</li>
  <li><b>Fluent interfaces</b> — e.g. <code>loginPage.open().configureCountryCodeByName(...)</code>.</li>
  <li><b>Separation of concerns</b> — assertions only in step definitions; page objects interact/query.</li>
  <li><b>Parallel-ready</b> — thread-safe driver management via <code>DriverFactory</code> + <code>Hooks</code>.</li>
</ul>

<h2>4. Delta between PRD and delivered framework</h2>
<p>Worth noting for the next owner — the PRD names some capabilities that are <em>specified</em> but not the current focus of the delivered suite:</p>
<table>
  <thead><tr><th>PRD item</th><th>Delivered state</th></tr></thead>
  <tbody>
    <tr><td>Appium / mobile</td><td>In the stack list; web + API are the implemented surfaces today.</td></tr>
    <tr><td>Healenium</td><td>Wired but off by default (ADR-0010) — see the Healenium guide.</td></tr>
    <tr><td>Global Search (header)</td><td>PRD marks it "planned"; search is covered at the API level (<code>SearchApiTest</code>).</td></tr>
    <tr><td>CAPTCHA on Send OTP</td><td>Real; the Cloudflare challenge is exactly what blocks hosted CI (ISSUE-001).</td></tr>
  </tbody>
</table>
<div class="callout note"><p><span class="tag">Keeping this current</span> The PRD says future updates are manual. When a feature ships, add it here <em>and</em> add a REQ row to the Reverse Requirements RTM so the coverage contract stays complete.</p></div>
""")

# ======================================================================================
# === END DOCS ===  (append new doc(...) calls above this line)
# ======================================================================================

NAV_SECTIONS = [
    "Getting started", "Configuration & data", "Execution & scaling",
    "Design & comparison", "Engineering & process", "Dashboard", "Requirements",
]

def render_index():
    cards_by_section = {}
    for d in DOCS:
        cards_by_section.setdefault(d["section"], []).append(d)
    body = ['<p class="sub" style="color:var(--muted);margin:0 0 8px">'
            'Twenty guides for running, extending and handing over the Timepieces QA automation '
            'framework. Each is a standalone HTML file — open any one on its own or convert it '
            'to PDF.</p>']
    for section in NAV_SECTIONS:
        items = sorted(cards_by_section.get(section, []), key=lambda x: x["num"])
        if not items:
            continue
        body.append(f'<div class="section-h">{esc(section)}</div>')
        body.append('<div class="grid">')
        for d in items:
            body.append(
                f'<a class="card" href="{d["slug"]}.html">'
                f'<div class="n">Guide {d["num"]:02d}</div>'
                f'<div class="t">{esc(d["title"])}</div>'
                f'<div class="d">{esc(d["subtitle"])}</div></a>')
        body.append('</div>')
    html = PAGE.format(
        css=CSS, num="Index", title="Handover Index",
        subtitle="Start here. Every handover guide, grouped by section.",
        updated=UPDATED, crumbs="", body="\n".join(body))
    (OUT_DIR / "index.html").write_text(html, encoding="utf-8")

def render_docs():
    for d in DOCS:
        crumbs = '<p class="crumbs"><a href="index.html">&larr; Handover index</a></p>'
        html = PAGE.format(
            css=CSS, num=f"{d['num']:02d}", title=esc(d["title"]), subtitle=esc(d["subtitle"]),
            updated=UPDATED, crumbs=crumbs, body=d["body"])
        (OUT_DIR / f"{d['slug']}.html").write_text(html, encoding="utf-8")

if __name__ == "__main__":
    render_docs()
    render_index()
    print(f"Wrote {len(DOCS)} guide(s) + index to {OUT_DIR.relative_to(REPO_ROOT)}")
    for d in sorted(DOCS, key=lambda x: x["num"]):
        print(f"  {d['num']:02d}  {d['slug']}.html")
