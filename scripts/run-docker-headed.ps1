#Requires -Version 5.1
<#
.SYNOPSIS
    Run the Timepieces suite inside Docker, mirroring the CI/CD pipeline, viewable over VNC.

.DESCRIPTION
    Builds the test image from docker/Dockerfile (eclipse-temurin:25 + google-chrome-stable
    — the same toolchain the GitHub Actions ubuntu runner uses) and runs the EXACT `mvn test`
    command from the matching workflow inside the container. This is the closest reproduction
    of a CI run you can get locally.

    Because a container has no host display, "headed" is provided via the image's bundled
    Xvfb + x11vnc: the browser runs on a virtual display and is streamed over VNC. Connect a
    VNC viewer (e.g. TightVNC / RealVNC / the built-in macOS viewer) to localhost:<VncPort>
    to watch the run live. The container is held open for 30 minutes afterwards so you can
    watch, then inspect the Allure report.

    Pipeline parity (identical -D flags to .github/workflows, headed for visual verify):
      staging    -> mvn -B test -Denv=staging -Dbrowser=chrome -Dheadless=<h>
                       -Dchrome.no.sandbox=true -Dstaging.password="$STAGING_GATE_PASS"
      production -> mvn -B test -Denv=production -Dsurefire.suite=testng-production.xml
                       -Dbrowser=chrome -Dheadless=<h> -Dchrome.no.sandbox=true

    Secrets are forwarded from your shell environment to the container, just like the
    workflow job env: CF_BYPASS_TOKEN, NEON_DATABASE_URL, STAGING_GATE_PASS/STAGING_PASSWORD.

.PARAMETER Environment
    'staging' (default) or 'production'.

.PARAMETER Browser
    chrome (default), firefox, edge, or brave. CI uses chrome.

.PARAMETER Headless
    Run headless to match CI byte-for-byte (default: headed via VNC for visual verification).

.PARAMETER VncPort
    Host port to publish the container's VNC server (5900) on. Default 5900.

.PARAMETER ImageTag
    Docker image tag to build/run. Default 'timepieces-tests:local'.

.PARAMETER Rebuild
    Force a rebuild of the image even if it already exists.

.EXAMPLE
    ./scripts/run-docker-headed.ps1
    # Builds the image, runs headed staging suite; connect a VNC viewer to localhost:5900.

.EXAMPLE
    ./scripts/run-docker-headed.ps1 -Environment staging -Headless
    # Byte-for-byte CI reproduction (headless), report written to ./target.
#>
[CmdletBinding()]
param(
    [ValidateSet('staging', 'production')]
    [string]$Environment = 'staging',

    [ValidateSet('chrome', 'firefox', 'edge', 'brave')]
    [string]$Browser = 'chrome',

    [switch]$Headless,

    [int]$VncPort = 5900,

    [string]$ImageTag = 'timepieces-tests:local',

    [switch]$Rebuild
)

$ErrorActionPreference = 'Stop'

$RepoRoot = Split-Path -Parent $PSScriptRoot
Set-Location $RepoRoot

# Docker on Windows wants forward-slash mount paths.
$MountPath = ($RepoRoot -replace '\\', '/')
$headlessValue = if ($Headless) { 'true' } else { 'false' }

# --- 1. Build the CI-mirror image (only if missing, unless -Rebuild). ---
$imageExists = $false
try { docker image inspect $ImageTag *> $null; if ($LASTEXITCODE -eq 0) { $imageExists = $true } } catch { }

if ($Rebuild -or -not $imageExists) {
    Write-Host "==> Building image '$ImageTag' from docker/Dockerfile (one-time, ~several minutes)" -ForegroundColor Cyan
    docker build -f docker/Dockerfile -t $ImageTag .
    if ($LASTEXITCODE -ne 0) { throw "docker build failed (exit $LASTEXITCODE)." }
}
else {
    Write-Host "==> Reusing existing image '$ImageTag' (pass -Rebuild to force a rebuild)." -ForegroundColor DarkGray
}

# --- 2. Assemble the EXACT mvn command the matching workflow runs. ---
$mvnCmd = "mvn -B test -Denv=$Environment -Dbrowser=$Browser -Dheadless=$headlessValue -Dchrome.no.sandbox=true"
if ($Environment -eq 'staging') {
    # Single quotes: keep $STAGING_GATE_PASS literal so bash expands it inside the container.
    $mvnCmd += ' -Dstaging.password="$STAGING_GATE_PASS"'
}
else {
    $mvnCmd += ' -Dsurefire.suite=testng-production.xml'
    Write-Warning "Production scope is @ProdReady (non-login) — it currently matches 0 scenarios, so no browser will open."
}

# --- 3. Inline container script (single-quoted template; tokens replaced literally). ---
$bashTemplate = @'
set -uo pipefail
if [ "__HEADLESS__" = "false" ]; then
  echo "[docker] headed mode -> starting Xvfb + x11vnc on display :99 (VNC port 5900)"
  Xvfb :99 -screen 0 1920x1080x24 >/tmp/xvfb.log 2>&1 &
  sleep 1
  fluxbox >/tmp/fluxbox.log 2>&1 &
  x11vnc -display :99 -nopw -listen 0.0.0.0 -xkb -forever -shared >/tmp/x11vnc.log 2>&1 &
  echo "[docker] >>> connect a VNC viewer to localhost:__VNCPORT__ to watch the run <<<"
fi
echo "[docker] cleaning previous artifacts"
mvn -q clean
echo "[docker] running CI-mirror command:"
echo "         __MVNCMD__"
__MVNCMD__
rc=$?
echo "[docker] generating Allure report"
mvn -q allure:report || true
echo "[docker] test exit code: $rc"
if [ "__HEADLESS__" = "false" ]; then
  echo "[docker] holding container 30m so you can watch via VNC and inspect ./target. Ctrl+C to stop."
  sleep 1800
fi
exit $rc
'@

$bash = $bashTemplate.Replace('__HEADLESS__', $headlessValue).Replace('__VNCPORT__', "$VncPort").Replace('__MVNCMD__', $mvnCmd)

# --- 4. Forward CI secrets from the host env (same names the workflow job env uses). ---
$envArgs = @()
if ($env:CF_BYPASS_TOKEN)   { $envArgs += @('-e', "CF_BYPASS_TOKEN=$($env:CF_BYPASS_TOKEN)") }
if ($env:NEON_DATABASE_URL) { $envArgs += @('-e', "NEON_DATABASE_URL=$($env:NEON_DATABASE_URL)") }
$stagingPass = if ($env:STAGING_GATE_PASS) { $env:STAGING_GATE_PASS } elseif ($env:STAGING_PASSWORD) { $env:STAGING_PASSWORD } else { $null }
if ($stagingPass) { $envArgs += @('-e', "STAGING_GATE_PASS=$stagingPass") }
elseif ($Environment -eq 'staging') {
    Write-Warning "STAGING_GATE_PASS / STAGING_PASSWORD not set; the staging gate may not unlock in the container."
}

# --- 5. Run: mount repo to /workspace (the image's WORKDIR), cache ~/.m2, publish VNC. ---
$dockerArgs = @(
    'run', '--rm', '-it',
    '-p', "${VncPort}:5900",
    '-v', "${MountPath}:/workspace",
    '-v', 'timepieces-m2:/root/.m2'
)
$dockerArgs += $envArgs
$dockerArgs += @('--entrypoint', 'bash', $ImageTag, '-lc', $bash)

Write-Host "==> Running container (env=$Environment, browser=$Browser, headless=$headlessValue)" -ForegroundColor Cyan
if ($headlessValue -eq 'false') {
    Write-Host "    Connect a VNC viewer to localhost:$VncPort once the browser launches." -ForegroundColor Green
}
& docker @dockerArgs
exit $LASTEXITCODE
