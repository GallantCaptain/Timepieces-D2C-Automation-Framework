#Requires -Version 5.1
<#
.SYNOPSIS
    Run the Timepieces suite LOCALLY in headed Chrome, mirroring the CI/CD pipeline.

.DESCRIPTION
    Reproduces the exact `mvn test` invocation from the GitHub Actions workflows
    (.github/workflows/staging-tests.yml and production-tests.yml) so a local run
    matches CI as closely as possible. The ONLY intentional deviation is that the
    browser runs HEADED (-Dheadless=false) so you can watch it, plus a `clean` up
    front for repeatable local results. After the run it builds the Allure report
    exactly like the workflow's "Generate Allure Report" step.

    Pipeline parity:
      staging    -> mvn -B test -Denv=staging -Dbrowser=chrome -Dheadless=<h>
                       -Dchrome.no.sandbox=true -Dstaging.password=<secret>
      production -> mvn -B test -Denv=production -Dsurefire.suite=testng-production.xml
                       -Dbrowser=chrome -Dheadless=<h> -Dchrome.no.sandbox=true

    Secrets are read from your shell environment, just like the workflow job env:
      CF_BYPASS_TOKEN, NEON_DATABASE_URL  (inherited by the Maven process)
      STAGING_PASSWORD / STAGING_GATE_PASS (passed as -Dstaging.password for staging)

.PARAMETER Environment
    'staging' (default) or 'production'. Selects the same -Denv and suite the matching
    workflow uses.

.PARAMETER Browser
    chrome (default), firefox, edge, or brave. CI uses chrome.

.PARAMETER Headless
    Run headless to match CI byte-for-byte (default: headed, for visual verification).

.PARAMETER Serve
    After the run, launch `mvn allure:serve` to open the report in a browser.

.EXAMPLE
    ./scripts/run-local-headed.ps1
    # Headed staging smoke + regression, exactly as the PR/nightly staging job runs it.

.EXAMPLE
    ./scripts/run-local-headed.ps1 -Environment production -Serve
#>
[CmdletBinding()]
param(
    [ValidateSet('staging', 'production')]
    [string]$Environment = 'staging',

    [ValidateSet('chrome', 'firefox', 'edge', 'brave')]
    [string]$Browser = 'chrome',

    [switch]$Headless,

    [switch]$Serve
)

$ErrorActionPreference = 'Stop'

# Repo root = parent of this script's directory, regardless of where it's invoked from.
$RepoRoot = Split-Path -Parent $PSScriptRoot
$Mvnw = Join-Path $RepoRoot 'mvnw.cmd'
Set-Location $RepoRoot

$headlessValue = if ($Headless) { 'true' } else { 'false' }

# --- Build the EXACT mvn args the matching workflow uses (see .github/workflows). ---
# NOTE: every -D arg is quoted as a single token; PowerShell otherwise splits on the dot
# (e.g. `.no.sandbox=true` is parsed as a separate Maven phase).
$mvnArgs = @(
    '-B', 'test',
    "-Denv=$Environment",
    "-Dbrowser=$Browser",
    "-Dheadless=$headlessValue",
    '-Dchrome.no.sandbox=true'
)

if ($Environment -eq 'staging') {
    $stagingPass = if ($env:STAGING_PASSWORD) { $env:STAGING_PASSWORD }
                   elseif ($env:STAGING_GATE_PASS) { $env:STAGING_GATE_PASS }
                   else { $null }
    if ($stagingPass) {
        $mvnArgs += "-Dstaging.password=$stagingPass"
    }
    else {
        Write-Warning "STAGING_PASSWORD / STAGING_GATE_PASS not set. The staging gate may not unlock unless config/staging.local.properties provides staging.password."
    }
}
else {
    # production-tests.yml runs the dedicated, non-login @ProdReady suite.
    $mvnArgs += '-Dsurefire.suite=testng-production.xml'
    Write-Warning "Production scope is @ProdReady (non-login) — it currently matches 0 scenarios, so no browser will open. This mirrors the production nightly."
}

if (-not $env:CF_BYPASS_TOKEN) {
    Write-Warning "CF_BYPASS_TOKEN not set; the Cloudflare bypass header won't be applied (usually fine for a local headed run from a residential IP)."
}

Write-Host "==> [1/3] Cleaning previous build/report artifacts" -ForegroundColor Cyan
& $Mvnw -q clean

Write-Host "==> [2/3] Running suite (env=$Environment, browser=$Browser, headless=$headlessValue)" -ForegroundColor Cyan
Write-Host "    $Mvnw $($mvnArgs -join ' ')" -ForegroundColor DarkGray
& $Mvnw @mvnArgs
$testExit = $LASTEXITCODE

Write-Host "==> [3/3] Generating Allure report (mirrors CI 'Generate Allure Report')" -ForegroundColor Cyan
& $Mvnw -q allure:report

if ($Serve) {
    Write-Host "==> Serving Allure report — press Ctrl+C to stop." -ForegroundColor Cyan
    & $Mvnw allure:serve
}
else {
    $report = Join-Path $RepoRoot 'target\site\allure-maven-plugin\index.html'
    Write-Host "Allure report: $report" -ForegroundColor Green
}

if ($testExit -ne 0) {
    Write-Host "Test run exit code: $testExit (failures present — see the Allure report)." -ForegroundColor Yellow
}
exit $testExit
