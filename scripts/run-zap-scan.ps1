<#
.SYNOPSIS
    Runs the OWASP Top 10 (2021) ZAP security scan against staging or production.

.DESCRIPTION
    Wraps the three things a ZAP run needs into one command:
      1. starts the ZAP daemon (docker compose --profile security up -d zap),
      2. runs testng-security.xml against the chosen environment,
      3. leaves ZAP's HTML report in target/zap/ and opens it.

    SCAN MODES
      full     - spider + ACTIVE scan. Fires real SQLi/XSS/command-injection payloads
                 at every form and endpoint discovered. Fine against staging, which is
                 disposable. This is the default for -Environment staging.
      baseline - spider + PASSIVE rules only. Passive rules just read responses that
                 would have been sent anyway: nothing is injected, no form is submitted,
                 no data is written. This is the default (and the only sane choice) for
                 -Environment production, which is a live store taking real orders.

    The suite itself refuses an active scan against production unless
    zap.allow.prod.active=true is set deliberately, so this script cannot talk you into
    attacking the live site by accident.

.PARAMETER Environment
    staging (default) or production.

.PARAMETER Mode
    full or baseline. Defaults to full for staging, baseline for production.

.PARAMETER KeepZapRunning
    Leave the ZAP daemon up after the run (useful for poking at its UI/API between scans).

.EXAMPLE
    ./scripts/run-zap-scan.ps1
    Full active scan against staging.

.EXAMPLE
    ./scripts/run-zap-scan.ps1 -Environment production
    Baseline passive scan against the live store.

.NOTES
    Run these from an India-based IP. Cloudflare challenges datacenter IPs outside
    India (see docs/okf/known-issues.md ISSUE-001), and a challenged scan reports zero
    findings because it never reached Timepieces — not because the site is clean.
#>
[CmdletBinding()]
param(
    [ValidateSet('staging', 'production')]
    [string]$Environment = 'staging',

    [ValidateSet('full', 'baseline')]
    [string]$Mode,

    [switch]$KeepZapRunning
)

$ErrorActionPreference = 'Stop'

# Default the mode from the environment: staging is disposable, production is not.
if (-not $Mode) {
    $Mode = if ($Environment -eq 'production') { 'baseline' } else { 'full' }
}

# Always operate from the repo root so the relative paths below resolve.
$repoRoot = Split-Path -Parent $PSScriptRoot
Push-Location $repoRoot

try {
    if ($Environment -eq 'production' -and $Mode -eq 'full') {
        Write-Host ''
        Write-Host 'REFUSING: a full ACTIVE scan against PRODUCTION.' -ForegroundColor Red
        Write-Host 'Active scans send real attack payloads. Against the live store that can create' -ForegroundColor Red
        Write-Host 'junk orders, bill and send real OTP SMS to customers via Twilio, and throw attack' -ForegroundColor Red
        Write-Host 'traffic at Razorpay (a third party we are not authorised to scan).' -ForegroundColor Red
        Write-Host 'Production is baseline/passive by design. See docs/security-zap.md.' -ForegroundColor Yellow
        exit 1
    }

    Write-Host "==> Starting ZAP daemon (compose profile: security)" -ForegroundColor Cyan
    docker compose --profile security up -d zap
    if ($LASTEXITCODE -ne 0) {
        throw "Could not start the ZAP daemon. Is Docker Desktop running?"
    }

    # No sleep here on purpose: OwaspTop10ZapScanTest polls ZAP's version endpoint for
    # up to 90s, so it handles the daemon's 20-40s boot itself.
    Write-Host "==> Running OWASP Top 10 scan | env=$Environment | mode=$Mode" -ForegroundColor Cyan
    if ($Mode -eq 'full') {
        Write-Host "    ACTIVE scan: real attack payloads will be sent to $Environment." -ForegroundColor Yellow
    }
    else {
        Write-Host "    BASELINE scan: passive rules only, nothing will be injected." -ForegroundColor Green
    }

    & "$repoRoot\mvnw.cmd" test `
        "-Dsurefire.suite=testng-security.xml" `
        "-Denv=$Environment" `
        "-Dzap.scan.mode=$Mode"
    $scanExit = $LASTEXITCODE

    $report = Join-Path $repoRoot 'target\zap\zap-report.html'
    if (Test-Path $report) {
        Write-Host "==> ZAP report: $report" -ForegroundColor Green
        Start-Process $report
    }
    else {
        Write-Host "==> No ZAP report was produced; check the log above." -ForegroundColor Yellow
    }

    $summary = Join-Path $repoRoot 'target\zap\owasp-top10-summary.txt'
    if (Test-Path $summary) {
        Write-Host ''
        Get-Content $summary | Write-Host
    }

    # A non-zero Maven exit here means a category test FAILED, i.e. ZAP found something
    # at or above the configured risk threshold. Propagate it so CI treats it as a real
    # security failure rather than a passing run.
    exit $scanExit
}
finally {
    if (-not $KeepZapRunning) {
        Write-Host "==> Stopping ZAP daemon (use -KeepZapRunning to leave it up)" -ForegroundColor Cyan
        docker compose --profile security stop zap | Out-Null
    }
    Pop-Location
}
