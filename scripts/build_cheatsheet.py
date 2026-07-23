#!/usr/bin/env python3
"""
Rebuilds Timepieces_Test_Execution_Cheatsheet.pdf from the HTML below.

WHY THIS SCRIPT EXISTS
----------------------
The first two versions of the cheatsheet were generated ad-hoc and the generator was
thrown away, so every refresh meant reverse-engineering the layout out of the PDF. This
file is the source of truth now: edit the HTML, re-run, commit both.

USAGE
-----
    py scripts/build_cheatsheet.py

Requires PyMuPDF:  py -m pip install pymupdf

The PDF is built with PyMuPDF's Story API, which lays out a subset of HTML/CSS across as
many pages as it needs. Footers (title + date + "Page N / M") are stamped in a second
pass, because the page count is not known until the story has finished placing.
"""

import datetime
import pathlib
import sys

try:
    import fitz  # PyMuPDF
except ImportError:
    sys.exit("PyMuPDF is required:  py -m pip install pymupdf")

REPO_ROOT = pathlib.Path(__file__).resolve().parent.parent
OUT_PATH = REPO_ROOT / "Timepieces_Test_Execution_Cheatsheet.pdf"
HTML_PATH = REPO_ROOT / "Timepieces_Test_Execution_Cheatsheet.html"

# Stamped into the footer. Kept explicit rather than date.today() so a rebuild that
# changes nothing does not churn the PDF's visible date.
UPDATED = "23 Jul 2026"

CSS = """
* { font-family: sans-serif; }
body { font-size: 8.2pt; color: #1a1a1a; line-height: 1.34; }

h1 { font-size: 16pt; color: #0f2b46; margin: 0 0 1pt 0; }
p.sub { font-size: 8pt; color: #5a6b7a; margin: 0 0 8pt 0; }

h2 { font-size: 10pt; color: #0f2b46; margin: 9pt 0 3pt 0;
     border-bottom: 1px solid #c9d6e2; padding-bottom: 1pt; }
h3 { font-size: 8.6pt; color: #24425f; margin: 6pt 0 2pt 0; }

p { margin: 0 0 3pt 0; }
code { font-family: monospace; font-size: 7.6pt; color: #0b3d2c; }

pre { font-family: monospace; font-size: 7.3pt; background-color: #f4f6f8;
      color: #14251c; padding: 4pt; margin: 2pt 0 4pt 0; line-height: 1.3; }

/* NOTE: do NOT put a CSS `width` on td/th. PyMuPDF's Story layout does not honour it the
   way a browser does — a percentage width collapses the first column and the next cell is
   drawn overlapping it, which silently mangles every table in the document. Column widths
   are set with the HTML `width` attribute on the <td> instead (see the tables below). */
table { width: 100%; margin: 2pt 0 4pt 0; }
th { font-size: 7.6pt; text-align: left; color: #0f2b46;
     background-color: #eaf0f6; padding: 2.5pt; }
td { font-size: 7.6pt; padding: 2.5pt; vertical-align: top; }
td.k { font-family: monospace; font-size: 7.2pt; color: #0b3d2c; }

.warn { background-color: #fff4f4; color: #7a1620; padding: 4pt; margin: 3pt 0; }
.note { background-color: #f2f8f2; color: #1d4d2b; padding: 4pt; margin: 3pt 0; }
.small { font-size: 7.2pt; color: #5a6b7a; }
"""

HTML = """
<h1>TIMEPIECES TEST EXECUTION CHEATSHEET</h1>
<p class="sub">Selenium/Java &middot; Cucumber BDD + Rest-Assured TDD &middot; OWASP ZAP DAST
&middot; Local &middot; Docker &middot; CI reference</p>

<h2>1 &middot; Configuration &amp; Precedence</h2>
<p>Every setting resolves through <code>ConfigReader</code> in three tiers &mdash; first hit wins:
<b>1.</b> CLI <code>-Dkey=value</code> &rarr; <b>2.</b> OS environment variable of the same name
&rarr; <b>3.</b> <code>config/&lt;env&gt;.properties</code> (with a gitignored
<code>&lt;env&gt;.local.properties</code> layered on top for secrets).
Target profile is chosen with <code>-Denv=</code> (default <code>staging</code>).</p>
<table>
<tr><th>Common key</th><th>Meaning / values</th></tr>
<tr><td class="k">-Denv</td><td>staging (default) | production</td></tr>
<tr><td class="k">-Dbrowser</td><td>chrome (default) | firefox | edge | brave | safari*</td></tr>
<tr><td class="k">-Dheadless</td><td>true | false &mdash; headless required inside containers</td></tr>
<tr><td class="k">-Dstaging.password</td><td>staging gate password (or env STAGING_GATE_PASS)</td></tr>
<tr><td class="k">recordVideo / ffmpeg.path</td><td>record failing scenarios (headed) + transcode to inline MP4</td></tr>
<tr><td class="k">max.retry.count</td><td>flaky-test retry attempts (RetryAnalyzer)</td></tr>
</table>
<p class="small">*Safari is macOS-only; elsewhere it is auto-substituted with
<code>safari.fallback.browser</code>.</p>

<h2>2 &middot; Suites &amp; Project Layout</h2>
<p>Discovery is driven by a TestNG suite XML (Surefire <code>${surefire.suite}</code>, default
<code>testng.xml</code>). BDD lives under <code>com.timepieces.automation.bdd.*</code>; API/TDD under
<code>com.timepieces.automation.tdd.*</code>.</p>
<table>
<tr><th>Suite file</th><th>Runs</th></tr>
<tr><td class="k">testng.xml</td><td>Everything: Smoke + Regression runners, API tests, unit test (default)</td></tr>
<tr><td class="k">testng-bdd.xml</td><td>BDD only: SmokeRunner + RegressionRunner</td></tr>
<tr><td class="k">testng-tdd.xml</td><td>API/TDD: SearchApiTest, IdempotencyApiTest, TwilioServiceTest</td></tr>
<tr><td class="k">testng-search-api.xml</td><td>Deterministic search API only (repeatable keyword sweep)</td></tr>
<tr><td class="k">testng-exploratory.xml</td><td>Nightly probabilistic API: SearchApiExploratoryTest</td></tr>
<tr><td class="k">testng-production.xml</td><td>ProductionRunner (@ProdReady, non-login prod-safe scope)</td></tr>
<tr><td class="k">testng-security.xml</td><td>OWASP ZAP DAST: OwaspTop10ZapScanTest (needs the ZAP daemon &mdash; section 11)</td></tr>
<tr><td class="k">testng-crossbrowser.xml</td><td>chrome + firefox + edge <b>CONCURRENTLY</b> in one JVM (needs the Grid &mdash; section 8)</td></tr>
<tr><td class="k">testng-privacy-probe.xml</td><td>ISSUE-019 reproduction. <b>Expected to FAIL</b> while the bug exists &mdash; that is the point</td></tr>
<tr><td class="k">testng-*-smoke.xml</td><td>Per-area TDD UI smoke: accessory, homepage, becomemore, timepieces-world, phase3</td></tr>
</table>
<p>Because <code>&lt;suiteXmlFiles&gt;</code> is configured, <code>-Dtest=ClassName</code> is
<b>ignored</b>. Select scope with <code>-Dsurefire.suite=</code> or the Maven profiles below.</p>

<h2>3 &middot; Command-Line Runner (shell syntax matters)</h2>
<h3>A. Bash / Zsh (macOS, Linux, Git Bash)</h3>
<pre># Default suite (Smoke + Regression BDD + API + unit), Chrome
./mvnw clean test

# Headless Firefox on staging
./mvnw test -Denv=staging -Dbrowser=firefox -Dheadless=true

# Filter by Cucumber tags (quote the whole -D so the shell keeps '@' and spaces)
./mvnw test "-Dcucumber.filter.tags=@Smoke and not @Regression"</pre>
<h3>B. PowerShell (Windows Terminal default)</h3>
<pre># Quote each -D argument; '@' and spaces are PowerShell operators otherwise
.\\mvnw.cmd test "-Dbrowser=brave" "-Dheadless=true"
.\\mvnw.cmd test "-Dcucumber.filter.tags=@Authentication and @Smoke"</pre>
<h3>C. Windows Command Prompt (CMD)</h3>
<pre>.\\mvnw.cmd test -Denv=staging -Dbrowser=chrome -Dheadless=false -Dcucumber.filter.tags=@Regression</pre>

<h2>4 &middot; Running a Specific Layer</h2>
<pre># BDD browser tests only
./mvnw test -Dsurefire.suite=testng-bdd.xml

# API / TDD tests only (no browser, fast)
./mvnw test -Dsurefire.suite=testng-tdd.xml

# Deterministic search-keyword sweep
./mvnw test -Dsurefire.suite=testng-search-api.xml

# Nightly probabilistic API exploration
./mvnw test -Dsurefire.suite=testng-exploratory.xml

# Production-safe scope (@ProdReady)
./mvnw test -Denv=production -Dsurefire.suite=testng-production.xml

# Security scan (see section 11 - the ZAP daemon must be up first)
./mvnw test -Dsurefire.suite=testng-security.xml -Denv=staging</pre>
<p>Maven profiles bundle a group + tag filter:</p>
<pre>./mvnw test -PSmokeSuite        # groups=smoke,             tags=@Smoke
./mvnw test -PRegressionSuite   # groups=smoke,regression,  tags=@Smoke or @Regression</pre>

<h2>5 &middot; Cucumber Tag Filtering</h2>
<table>
<tr><th>Expression</th><th>Behavior</th></tr>
<tr><td class="k">@Smoke</td><td>Only @Smoke scenarios</td></tr>
<tr><td class="k">@Smoke and not @Regression</td><td>@Smoke, excluding @Regression</td></tr>
<tr><td class="k">@Payment or @Checkout</td><td>Either payment or checkout scenarios</td></tr>
<tr><td class="k">(@Smoke or @Regression) and @Authentication</td><td>Auth scenarios that are smoke or regression</td></tr>
</table>
<p class="small">Live tags: @Smoke, @Regression, @Authentication, @Login, @Checkout, @Payment,
@Wishlist, @Account, @ProdReady, @IntlPayments, @AuthGated, @Accessories, @ContentPages,
@FriendsPartners, @TimepiecesWorld, @BecomeMore.
Opt-in: @OtpE2E (real Twilio SMS), @OtpBruteForce (security probe).</p>

<h3>Environment-scope tags &mdash; use these or you WILL get false failures</h3>
<table>
<tr><th>Tag</th><th>Meaning &amp; why</th></tr>
<tr><td class="k">@StagOnly</td><td><b>Cannot pass on production.</b> The staging password wall does not exist there, and production login sits behind a HUMAN-solved Cloudflare Turnstile &mdash; impossible, not flaky</td></tr>
<tr><td class="k">@ProdOnly</td><td><b>Always hits production regardless of -Denv.</b> Timepieces World overrides getBaseUrl() to the fixed world.timepieces-d2c.example, so a staging run silently drives PROD while the report says staging</td></tr>
<tr><td class="k">@ProdReady</td><td>NOT the same thing: a <i>permission</i> ("safe against production"), not a restriction. A scenario can carry @ProdReady AND @ProdOnly</td></tr>
</table>
<pre># Production run - exclude the staging-only scenarios
./mvnw test -Denv=production "-Dcucumber.filter.tags=not @StagOnly"

# Staging run - exclude the ones pinned to the production microsite
./mvnw test -Denv=staging "-Dcucumber.filter.tags=not @ProdOnly"</pre>

<h2>6 &middot; Parallelism &amp; Concurrency</h2>
<pre># Scale up to 4 concurrent browser threads
./mvnw test -Ddataproviderthreadcount=4

# Force sequential (debugging, shared state, or OTP send caps)
./mvnw test -Ddataproviderthreadcount=1</pre>

<h2>7 &middot; Local Run Helpers &amp; Handy Commands</h2>
<h3>One-shot headed run (builds report + emails it automatically)</h3>
<pre>./scripts/run-local-headed.ps1                                  # headed staging, then emails the report
./scripts/run-local-headed.ps1 -Environment production -Serve   # open the report afterwards
./scripts/run-local-headed.ps1 -Headless                        # byte-for-byte CI parity</pre>
<h3>Raw Maven (no auto-email) + manual email</h3>
<pre>./mvnw.cmd clean test
./mvnw.cmd allure:report ; py scripts/email_allure_report.py</pre>
<h3>Cleaning</h3>
<pre>./mvnw.cmd -q clean                  # wipe target/ (allure results+reports, recordings, zap output)
./mvnw.cmd clean test ...            # clean THEN run, in one go - the usual way to start fresh
./mvnw.cmd -q test-compile           # compile sources without running tests
docker compose up -d postgres-dev    # local metrics DB (postgres:5432)</pre>

<h2>8 &middot; Dockerized Execution</h2>
<p>The image ships Java 25 + Maven, Chrome/Firefox/Edge/Brave, Xvfb + x11vnc, ffmpeg and
python3. Rebuild after pulling changes.</p>
<pre>docker build -t timepieces-tests -f docker/Dockerfile .</pre>
<h3>Helper scripts (recommended &mdash; they auto-generate + email the report)</h3>
<pre># Headless run across all browsers
./docker/run-tests.ps1
./docker/run-tests.ps1 -Vnc -Env staging -Tags "@Smoke"     # watch via VNC :5900

# CI-mirror single browser, headed via VNC (or -Headless for byte-for-byte CI)
./scripts/run-docker-headed.ps1
./scripts/run-docker-headed.ps1 -Headless -Rebuild</pre>
<h3>Raw docker run</h3>
<pre>docker run --rm \\
  -v "$PWD:/workspace" -v timepieces-m2:/root/.m2 \\
  -e ENV_TARGET=staging -e TAGS="@Smoke" -e BROWSERS="chrome firefox edge brave" \\
  -e SMTP_HOST=... -e SMTP_USER=... -e SMTP_PASSWORD=... -e MAIL_TO=... \\
  timepieces-tests
# Headed + VNC: add -e DEBUG_VNC=true -p 5900:5900, then connect a viewer to localhost:5900</pre>
<p class="small">Entrypoint env vars are ENV_TARGET, TAGS, BROWSERS, DEBUG_VNC (not
ENV/BROWSER/HEADLESS). The helper scripts forward SMTP_* / MAIL_* into the container
automatically.</p>
<h3>Compose services (all opt-in)</h3>
<pre>docker compose up -d postgres-dev              # metrics DB
docker compose up -d allure                    # live report browser at http://localhost:5252
docker compose up -d healenium-postgres hlm-backend   # self-healing locators (healenium.enabled=true)
docker compose --profile security up -d zap    # ZAP daemon on :8090 (profile = does NOT autostart)
docker compose --profile grid up -d            # Selenium Grid hub + chrome/firefox/edge nodes
docker compose down                            # stop everything</pre>

<h3>Selenium Grid &mdash; real cross-browser, no local browsers needed</h3>
<pre>docker compose --profile grid up -d            # hub :4444 + chrome/firefox/edge nodes
./mvnw test -Dexecution.mode=GRID -Dbrowser=firefox -Denv=production

# Cross-browser: clean ONLY on the first, so Allure merges all three into one report
./mvnw clean test -Dexecution.mode=GRID -Dbrowser=chrome  -Denv=production
./mvnw test       -Dexecution.mode=GRID -Dbrowser=firefox -Denv=production
./mvnw test       -Dexecution.mode=GRID -Dbrowser=edge    -Denv=production
./mvnw allure:report

docker compose --profile grid up -d --scale selenium-node-chrome=3   # scale by REPLICAS
docker compose --profile grid down</pre>

<h3>All three browsers AT ONCE (one JVM)</h3>
<pre>./mvnw test -Dsurefire.suite=testng-crossbrowser.xml -Dexecution.mode=GRID \
            -Denv=production "-Dcucumber.filter.tags=not @StagOnly"</pre>
<p class="small"><b>Do not trust a green run</b> &mdash; the failure mode is a SILENT WRONG
ANSWER (all three blocks running the same browser). Ask the Grid instead; each node serves
only its own browser, so all three must report sessions:</p>
<pre>docker compose --profile grid logs selenium-node-firefox | grep -c "Session created"</pre>
<p class="small">A zero means that browser never ran, whatever the report says. Thread
budget: 3 blocks x 2 threads = 6 concurrent = exactly the Grid's 6 slots. Asking for more
makes sessions queue and time out &mdash; <b>add node replicas, not threads</b>. Expect
Firefox to fail slightly more than Chrome: that is cross-browser testing working, not a
broken runner. Rollback = delete the suite file.</p>
<p class="small">Watch a live session at <b>http://localhost:4444/ui</b> (VNC password:
<code>secret</code>). Verified 4/4 on all three browsers. <b>execution.mode</b> = local
(default) | GRID | CLOUD. Note <b>CLOUD is a POC and ignores -Dbrowser</b> (it always runs
Chrome) &mdash; use GRID for cross-browser. The old <code>execution.target</code> key was
dead config and has been removed. Full guide: <code>docs/selenium-grid.md</code>.</p>

<h2>9 &middot; Reports: View, Host &amp; Email</h2>
<p>Runs write raw results to <code>target/allure-results/</code>; failure screenshots and inline
MP4 videos are embedded in every report.</p>
<h3>View locally</h3>
<pre>./mvnw.cmd allure:serve      # generate + open on an ephemeral port
./mvnw.cmd allure:report     # static HTML at target/site/allure-maven-plugin/</pre>
<h3>Host so stakeholders just click a link</h3>
<pre># Live, self-updating report browser at http://localhost:5252
# (watches target/allure-results; keeps history for trends). Stop: docker compose down
docker compose up -d allure</pre>
<p>CI (test-pipeline) publishes each run to GitHub Pages &mdash; a permanent, login-free link:
<code>https://timepiecestech.github.io/qa-automation/runs/&lt;run_number&gt;/</code>.
The Pages link needs a repo admin to enable Pages once: <b>Settings &rarr; Pages &rarr; Source =
gh-pages branch, / (root)</b>. It matches the dashboard's report links. (BLK-006)</p>
<h3>Email the report</h3>
<pre># Config lives in a gitignored .env (auto-loaded): SMTP_HOST/USER/PASSWORD, MAIL_TO
py scripts/email_allure_report.py        # emails whatever is in target/site/...</pre>
<p class="small">Local/Docker attach the zipped report; CI emails a summary + link
(MAIL_ATTACH=false). No-ops safely if SMTP is unset. The local helper scripts and the Docker
entrypoint email automatically; a bare <code>mvn test</code> does not.</p>

<h2>10 &middot; Environment &amp; Secrets</h2>
<table>
<tr><th>Name</th><th>Purpose</th></tr>
<tr><td class="k">STAGING_GATE_PASS / -Dstaging.password</td><td>Unlock the staging password gate</td></tr>
<tr><td class="k">CF_BYPASS_TOKEN</td><td>Cloudflare bypass (x-automation-bypass header)</td></tr>
<tr><td class="k">NEON_DATABASE_URL</td><td>Metrics DB (or docker compose up -d postgres-dev)</td></tr>
<tr><td class="k">SMTP_HOST / SMTP_USER / SMTP_PASSWORD</td><td>Email sender (Gmail: use an App Password)</td></tr>
<tr><td class="k">MAIL_TO / MAIL_FROM / MAIL_ATTACH</td><td>Report recipient / sender / attach-vs-link toggle</td></tr>
<tr><td class="k">TWILIO_*</td><td>Twilio creds for @OtpE2E real-SMS read-back</td></tr>
<tr><td class="k">ZAP_API_KEY</td><td>ZAP daemon API key (default timepieces-zap-dev-key)</td></tr>
<tr><td class="k">ZAP_RUNNER <span class="small">(repo VARIABLE, not secret)</span></td><td>Label of a self-hosted India runner for the weekly scan &mdash; see 11</td></tr>
</table>
<p class="small">Local email config goes in a gitignored <code>.env</code> (see
<code>.env.example</code>); CI reads the same names as repository secrets. Full guide:
<code>docs/allure-email.md</code>.</p>

<h2>11 &middot; Security Scanning (OWASP ZAP / DAST)</h2>
<p>Scans the running storefront and asserts the findings against the <b>OWASP Top 10 (2025 by
default; 2021 available)</b>. Full guide: <code>docs/security-zap.md</code>.</p>

<div class="warn"><b>Staging is attacked. Production is NOT.</b>
<code>full</code> = spider + <b>ACTIVE</b> scan: real SQLi/XSS/command-injection payloads.
<code>baseline</code> = spider + <b>PASSIVE</b> rules only &mdash; nothing injected, no form
submitted, no data written. Production is baseline-only <b>by design</b>: an active scan on the
live store could create junk orders, bill and send <b>real OTP SMS to real customers</b> via
Twilio, and throw attack traffic at <b>Razorpay, a third party we are not authorised to scan</b>.
The suite refuses an active prod scan unless <code>-Dzap.allow.prod.active=true</code> is set
deliberately &mdash; which needs written sign-off and a maintenance window.</div>

<h3>Easiest path (starts ZAP, scans, opens the report)</h3>
<pre>./scripts/run-zap-scan.ps1                          # staging, full ACTIVE scan
./scripts/run-zap-scan.ps1 -Environment production  # production, baseline PASSIVE scan
./scripts/run-zap-scan.ps1 -KeepZapRunning          # leave the daemon up between scans</pre>

<h3>Manual (local or Docker)</h3>
<pre># 1. Start the ZAP daemon. It is behind a compose PROFILE, so a plain `up` will not start it.
docker compose --profile security up -d zap

# 2. Scan. Staging = full active; production = baseline passive.
./mvnw test -Dsurefire.suite=testng-security.xml -Denv=staging    -Dzap.scan.mode=full
./mvnw test -Dsurefire.suite=testng-security.xml -Denv=production -Dzap.scan.mode=baseline

# 3. Report it to the 2021 list instead (auditors still cite it)
./mvnw test -Dsurefire.suite=testng-security.xml -Denv=staging -Dzap.owasp.edition=2021

# 4. Stop the daemon
docker compose --profile security stop zap</pre>
<p class="small">Output: <code>target/zap/zap-report.html</code> (ZAP's own report),
<code>zap-alerts.json</code>, <code>owasp-top10-summary.txt</code> &mdash; all three also attach
to Allure. If the ZAP daemon is not running the suite <b>skips</b> rather than fails.</p>

<table>
<tr><th>ZAP key</th><th>Meaning</th></tr>
<tr><td class="k">-Dzap.scan.mode</td><td>full (ACTIVE payloads) | baseline (passive, read-only)</td></tr>
<tr><td class="k">-Dzap.owasp.edition</td><td>2025 (default) | 2021</td></tr>
<tr><td class="k">-Dzap.fail.on.risk</td><td>High (default) | Medium | Low &mdash; bar that fails a category</td></tr>
<tr><td class="k">-Dzap.allow.prod.active</td><td>false. The prod interlock. Do not flip casually.</td></tr>
<tr><td class="k">-Dzap.spider.max.duration.minutes</td><td>crawl budget (default 5)</td></tr>
<tr><td class="k">-Dzap.ajax.spider.enabled</td><td>browser-driven crawl for JS routes (default true)</td></tr>
<tr><td class="k">-Dzap.active.scan.max.duration.minutes</td><td>attack budget (default 25)</td></tr>
<tr><td class="k">-Dzap.exclude.regex</td><td>never crawled/attacked (OTP, Razorpay, payment, logout)</td></tr>
<tr><td class="k">-Dzap.alert.filters</td><td>re-grade false positives: ruleId|newLevel|urlRegex</td></tr>
</table>

<h3>CI &mdash; scheduled weekly (workflow: security-zap-scan)</h3>
<table>
<tr><th>When (cron is UTC)</th><th>Target</th><th>Mode</th></tr>
<tr><td>Saturday 18:00 UTC = Sat 23:30 IST</td><td>staging</td><td>full &mdash; ACTIVE payloads</td></tr>
<tr><td>Sunday 18:00 UTC = Sun 23:30 IST</td><td>production</td><td>baseline &mdash; passive, read-only</td></tr>
</table>
<p class="small">18:00 UTC is deliberate: +5:30 keeps the run on the <b>same day</b> in IST, so
"the Saturday scan" means Saturday to everyone reading the report. It is also off-peak.
Manual run: <b>Actions &rarr; security-zap-scan &rarr; Run workflow</b>, choosing environment
and mode.</p>

<div class="warn"><b>BLK-007 &mdash; a green CI scan does NOT mean "no vulnerabilities".</b>
GitHub-hosted runners sit outside India and Cloudflare challenges them (403
<code>cf-mitigated: challenge</code> even <i>with</i> the bypass header &mdash; ISSUE-001). ZAP
then scans Cloudflare's "Just a moment..." page and reports zero findings. For a security
control that is <b>false assurance, worse than a red build</b>.
<b>Fix:</b> provision a self-hosted runner on an India IP and set the repo <b>variable</b>
<code>ZAP_RUNNER</code> to its label (Settings &rarr; Secrets and variables &rarr; Actions &rarr;
Variables). The workflow uses
<code>runs-on: ${{ vars.ZAP_RUNNER || 'ubuntu-latest' }}</code>, so no code change is needed.
Local/Docker runs from India are unaffected and give genuine results today.</div>

<h2>12 &middot; Handy Scripts</h2>
<table>
<tr><th>Script</th><th>What it does</th></tr>
<tr><td class="k">scripts/run-zap-scan.ps1</td><td>Starts ZAP, runs the OWASP Top 10 scan, opens the report</td></tr>
<tr><td class="k">scripts/check_zephyr_token.py</td><td>Is a Zephyr token LIVE or revoked? Prints only an HTTP status &mdash; never the token</td></tr>
<tr><td class="k">scripts/email_allure_report.py</td><td>Emails the Allure report (no-ops safely if SMTP is unset)</td></tr>
<tr><td class="k">scripts/build_cheatsheet.py</td><td>Rebuilds THIS document (PDF + HTML). Edit the HTML in it, re-run, commit both</td></tr>
<tr><td class="k">scripts/run-local-headed.ps1</td><td>One-shot headed run + report + email</td></tr>
<tr><td class="k">scripts/run-docker-headed.ps1</td><td>CI-mirror single-browser run via Docker, headed over VNC</td></tr>
</table>
<pre># Verify a credential rotation actually took (the step people skip)
ZEPHYR_TOKEN=&lt;new-token&gt; py scripts/check_zephyr_token.py    # want: HTTP 200, LIVE</pre>

<h2>13 &middot; Other CI Workflows</h2>
<table>
<tr><th>Workflow</th><th>Trigger</th></tr>
<tr><td class="k">staging-tests</td><td>PRs to main + nightly 20:30 UTC + manual</td></tr>
<tr><td class="k">production-tests</td><td>nightly 22:30 UTC + manual (@ProdReady scope)</td></tr>
<tr><td class="k">exploratory-search-tests</td><td>scheduled + manual (probabilistic search API)</td></tr>
<tr><td class="k">test-pipeline</td><td>manual/dashboard-triggered; publishes report to Pages</td></tr>
<tr><td class="k">cf-bypass-check</td><td>manual diagnostic: probes Cloudflare with/without the bypass header</td></tr>
<tr><td class="k">security-zap-scan</td><td>Sat staging (active) / Sun production (baseline) &mdash; see 11</td></tr>
</table>
<p class="small">Every workflow uploads its Allure report as an artifact and emails a summary +
link when the SMTP_* secrets are set.</p>
"""


def build() -> int:
    """Lays the HTML out across A4 pages and stamps footers. Returns the page count."""
    story = fitz.Story(html=HTML, user_css=CSS)
    writer = fitz.DocumentWriter(OUT_PATH)

    mediabox = fitz.paper_rect("a4")
    # Generous bottom margin so the footer never collides with body text.
    content_area = mediabox + (38, 40, -38, -34)

    pages = 0
    more = True
    while more:
        device = writer.begin_page(mediabox)
        more, _ = story.place(content_area)
        story.draw(device)
        writer.end_page()
        pages += 1
    writer.close()

    # Second pass for footers: the total page count is only known now.
    doc = fitz.open(OUT_PATH)
    for i, page in enumerate(doc):
        page.insert_text(
            fitz.Point(38, mediabox.height - 20),
            f"Timepieces Test Execution Cheatsheet  ·  Updated {UPDATED}",
            fontname="helv", fontsize=7, color=(0.36, 0.42, 0.48),
        )
        page.insert_text(
            fitz.Point(mediabox.width - 78, mediabox.height - 20),
            f"Page {i + 1} / {pages}",
            fontname="helv", fontsize=7, color=(0.36, 0.42, 0.48),
        )
    doc.set_metadata({
        "title": "Timepieces Test Execution Cheatsheet",
        "author": "Avinash Rath",
        "subject": "Local / Docker / CI test execution reference, incl. OWASP ZAP DAST",
        "keywords": "timepieces selenium cucumber testng allure docker zap owasp",
        "creationDate": fitz.get_pdf_now(),
    })
    doc.saveIncr()
    doc.close()
    return pages


def build_html() -> None:
    """
    Writes a standalone HTML twin of the PDF, from the SAME `HTML` source string.

    <p>Why bother when a PDF exists: the HTML is <b>searchable with Ctrl-F, copy-pasteable
    without PDF line-break damage, readable on a phone, and viewable with no PDF reader</b>
    — which matters for a document whose entire purpose is copying commands out of it.

    <p>Sharing the one `HTML` constant is the point. Two hand-maintained copies would drift
    within a week, and the stale one would be the one somebody trusted.

    <p>Self-contained on purpose: the CSS is inlined and there are no external assets, so it
    survives being emailed, dropped on a share, or opened offline.
    """
    # Screen-friendly overrides. The PDF CSS is tuned for print (8pt type, tight leading);
    # the same values on a monitor are a squint. Layout/structure stay identical.
    screen_css = """
    body { max-width: 980px; margin: 0 auto; padding: 28px 20px 60px;
           font-size: 15px; line-height: 1.55;
           font-family: -apple-system, "Segoe UI", Roboto, sans-serif;
           color: #1a1a1a; background: #fff; }
    h1 { font-size: 30px; }
    h2 { font-size: 20px; margin-top: 30px; padding-bottom: 4px; }
    h3 { font-size: 15px; margin-top: 16px; }
    pre { font-size: 13px; padding: 12px; border-radius: 6px; overflow-x: auto;
          border: 1px solid #e3e8ee; }
    code { font-size: 13px; }
    table { border-collapse: collapse; margin: 10px 0 16px; }
    th, td { font-size: 13.5px; padding: 7px 10px; border-bottom: 1px solid #e3e8ee;
             vertical-align: top; }
    td.k { font-family: ui-monospace, Consolas, monospace; font-size: 12.5px;
           white-space: nowrap; }
    .warn, .note { border-radius: 6px; padding: 12px 14px; }
    .small { font-size: 12.5px; }
    /* Wide tables must scroll inside themselves rather than shoving the page sideways. */
    table { display: block; overflow-x: auto; max-width: 100%; }

    @media (prefers-color-scheme: dark) {
      body { background: #14171a; color: #e6e6e6; }
      h1, h2, h3 { color: #cfe3f5; }
      h2 { border-bottom-color: #33404d; }
      pre { background: #1c2126; color: #d7e3d9; border-color: #2b333b; }
      code { color: #86c9a4; }
      th { background: #1e242a; color: #cfe3f5; }
      th, td { border-bottom-color: #2b333b; }
      .warn { background: #2a1c1e; color: #f0b3ba; }
      .note { background: #16241a; color: #a9dfb8; }
      .small, p.sub { color: #9fb0c0; }
    }
    """
    page = (
        "<!doctype html>\n<html lang=\"en\">\n<head>\n"
        "<meta charset=\"utf-8\">\n"
        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
        "<title>Timepieces Test Execution Cheatsheet</title>\n"
        f"<style>{CSS}\n{screen_css}</style>\n"
        "</head>\n<body>\n"
        f"{HTML}\n"
        "<hr style=\"margin-top:40px;border:none;border-top:1px solid #ccc\">\n"
        f"<p class=\"small\">Timepieces Test Execution Cheatsheet &middot; updated {UPDATED} &middot; "
        "generated by <code>scripts/build_cheatsheet.py</code> "
        "(edit the HTML in that script and re-run &mdash; do not hand-edit this file).</p>\n"
        "</body>\n</html>\n"
    )
    HTML_PATH.write_text(page, encoding="utf-8")


if __name__ == "__main__":
    n = build()
    build_html()
    print(f"Wrote {OUT_PATH.name}: {n} pages, {OUT_PATH.stat().st_size / 1024:.1f} KB")
    print(f"Wrote {HTML_PATH.name}: {HTML_PATH.stat().st_size / 1024:.1f} KB")
    print(f"(updated {UPDATED}; built {datetime.date.today().isoformat()})")
