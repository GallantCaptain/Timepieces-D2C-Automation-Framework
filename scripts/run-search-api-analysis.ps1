#Requires -Version 5.1
<#
.SYNOPSIS
    Runs the Search API test suites (deterministic + exploratory) N times each and
    compiles a combined pass/fail analysis report.

.DESCRIPTION
    Repeatedly invokes:
      - SearchApiTest        (deterministic, testng-search-api.xml, default env=staging)
      - SearchApiExploratoryTest (probabilistic/randomized, testng-exploratory.xml, env=production)

    Each run's generated markdown report (target/search_keyword_api_report.md or
    target/exploratory_search_api_report.md) is copied out to its own numbered file
    before the next run overwrites it, so nothing is lost. After all runs complete,
    every "Full Results" table is parsed and merged into a single per-keyword
    aggregation: how many times each keyword was tested, how many times it passed,
    and whether it is a PERSISTENT failure (fails every time — a real bug), a FLAKY
    result (passes sometimes, fails other times — worth another look), or healthy.
    The 9 business-priority keywords (watch, watches, timepiece, timepieces, loom,
    dial, strap, clasp, watch case) get their own dedicated summary section.

    Use this after any change to search_keywords.json, SearchApiTest.java, or the
    storefront's search backend/index, to get a fast read on whether search
    relevance regressed or improved. Both the automation team and app developers
    can run this directly.

.PARAMETER DeterministicRuns
    How many times to run the deterministic SearchApiTest suite. Default 2.
    Set to 0 (or pass -SkipDeterministic) to skip it entirely.

.PARAMETER ExploratoryRuns
    How many times to run the randomized SearchApiExploratoryTest suite. Default 10.
    Each run reshuffles with a fresh time-based seed, so more runs = broader coverage
    of the keyword pool. Set to 0 (or pass -SkipExploratory) to skip it entirely.

.PARAMETER Environment
    Environment the deterministic suite targets (staging|production). Default staging.
    Passed as -Denv=<value> to Maven; matches ConfigReader's env resolution.

.PARAMETER ExploratoryEnvironment
    Optional override for the exploratory suite's target environment. Default: unset,
    which lets SearchApiExploratoryTest use its own built-in default (production).
    Pass 'staging' here to smoke-test the exploratory pool against staging instead.

.PARAMETER SkipDeterministic
    Skip the deterministic SearchApiTest suite entirely.

.PARAMETER SkipExploratory
    Skip the exploratory SearchApiExploratoryTest suite entirely.

.PARAMETER OutputDir
    Where per-run logs/reports and the final compiled summary are written.
    Default: output/search-api-analysis/<timestamp> under the repo root.

.EXAMPLE
    ./scripts/run-search-api-analysis.ps1
    Runs the deterministic suite 2x and the exploratory suite 10x (the defaults used
    to produce the "Automated Search API Test Results" report), then writes a
    combined summary.

.EXAMPLE
    ./scripts/run-search-api-analysis.ps1 -DeterministicRuns 1 -ExploratoryRuns 3
    A quicker smoke check — useful when iterating locally on search_keywords.json.

.EXAMPLE
    ./scripts/run-search-api-analysis.ps1 -SkipDeterministic -ExploratoryRuns 20
    Only run the exploratory suite, 20 times, for deeper randomized coverage.
#>
[CmdletBinding()]
param(
    [int]$DeterministicRuns = 2,
    [int]$ExploratoryRuns = 10,

    [ValidateSet('staging', 'production')]
    [string]$Environment = 'staging',

    [ValidateSet('staging', 'production', '')]
    [string]$ExploratoryEnvironment = '',

    [switch]$SkipDeterministic,
    [switch]$SkipExploratory,

    [string]$OutputDir
)

$ErrorActionPreference = 'Stop'
$RepoRoot = Split-Path -Parent $PSScriptRoot
Set-Location $RepoRoot

$timestamp = Get-Date -Format 'yyyy-MM-dd_HH-mm-ss'
if (-not $OutputDir) {
    $OutputDir = Join-Path $RepoRoot "output/search-api-analysis/$timestamp"
}
$runsDir = Join-Path $OutputDir 'runs'
New-Item -ItemType Directory -Force -Path $runsDir | Out-Null

$doDeterministic = (-not $SkipDeterministic) -and ($DeterministicRuns -gt 0)
$doExploratory = (-not $SkipExploratory) -and ($ExploratoryRuns -gt 0)

if (-not $doDeterministic -and -not $doExploratory) {
    Write-Warning "Both suites are skipped/zero — nothing to run. Set -DeterministicRuns and/or -ExploratoryRuns above 0."
    exit 0
}

# --------------------------------------------------------------------------------
# Parses the "## Full Results" markdown table that both SearchApiTest and
# SearchApiExploratoryTest write into their generated report. Both reports use the
# same 6-column shape (Group/Category, Test Name, Keyword, Status, Returned
# Handles, Error) — only the first column's header label differs.
# --------------------------------------------------------------------------------
function Get-FullResultsRows {
    param([string]$ReportPath)

    $rows = @()
    if (-not (Test-Path $ReportPath)) {
        return $rows
    }

    $lines = Get-Content -Path $ReportPath -Encoding UTF8
    $afterHeading = $false
    $headerSeen = $false

    foreach ($line in $lines) {
        if ($line -match '^##\s+Full Results') {
            $afterHeading = $true
            continue
        }
        if (-not $afterHeading) { continue }

        if (-not $headerSeen) {
            if ($line -match '^\|\s*(Group|Category)\s*\|') {
                $headerSeen = $true
            }
            continue
        }

        $trimmed = $line.Trim()
        if (-not $trimmed.StartsWith('|')) {
            break
        }

        $cols = $trimmed.Trim('|') -split '\|'
        if ($cols.Count -lt 6) { continue }
        # Skip the markdown header-separator row (| --- | --- | ... |).
        $isSeparatorRow = -not ($cols | Where-Object { $_.Trim() -notmatch '^:?-{2,}:?$' })
        if ($isSeparatorRow) { continue }

        $rows += [pscustomobject]@{
            Group    = $cols[0].Trim()
            Name     = $cols[1].Trim()
            Keyword  = ($cols[2].Trim() -replace '`', '')
            Status   = $cols[3].Trim()
            Returned = $cols[4].Trim()
            Error    = $cols[5].Trim()
        }
    }

    return $rows
}

# --------------------------------------------------------------------------------
# Cross-check helper: pulls the report's own self-declared "Total Queries Tested"
# from its Executive Summary table, so we can warn loudly if our Full Results
# table parse ever disagrees with what the test class itself counted — instead of
# silently trusting a parse that could be wrong on a future report format change.
# --------------------------------------------------------------------------------
function Get-ExecutiveSummaryTotal {
    param([string]$ReportPath)
    if (-not (Test-Path $ReportPath)) { return $null }
    $content = Get-Content -Path $ReportPath -Raw -Encoding UTF8
    $m = [regex]::Match($content, '\*\*Total Queries Tested\*\*\s*\|\s*(\d+)')
    if ($m.Success) { return [int]$m.Groups[1].Value }
    return $null
}

function Invoke-SuiteRuns {
    param(
        [string]$Label,
        [string]$SuiteFile,
        [int]$Count,
        [string]$RunPrefix,
        [string]$ReportFileName,
        [string[]]$ExtraMvnArgs
    )

    $results = @()
    Write-Host ""
    Write-Host "=== $Label`: $Count run(s) ===" -ForegroundColor Cyan

    for ($i = 1; $i -le $Count; $i++) {
        Write-Host "-- $Label run $i/$Count --" -ForegroundColor Yellow
        $logPath = Join-Path $runsDir "$RunPrefix-$i.log"

        $mvnArgs = @('-o', 'test', "-Dsurefire.suite=$SuiteFile") + $ExtraMvnArgs
        & .\mvnw.cmd @mvnArgs *> $logPath
        $exitCode = $LASTEXITCODE

        $reportSrc = Join-Path $RepoRoot "target/$ReportFileName"
        $reportDst = Join-Path $runsDir "$RunPrefix-$i-report.md"
        if (Test-Path $reportSrc) {
            # Copy by content rather than Copy-Item: on some machines a fresh Copy-Item
            # immediately after the JVM exits can race antivirus/indexer file locks and
            # silently grab a stale/partial snapshot. Reading the bytes ourselves forces
            # a fully-materialized read of what the JVM actually wrote.
            $reportBytes = [System.IO.File]::ReadAllBytes($reportSrc)
            [System.IO.File]::WriteAllBytes($reportDst, $reportBytes)
        }
        else {
            Write-Warning "  No report generated at $reportSrc (check $logPath for a build failure before tests ran)."
        }

        $status = if ($exitCode -eq 0) { 'all assertions passed' } else { 'some assertions failed (this is expected — see report)' }
        Write-Host "   exit code $exitCode ($status)"

        $results += [pscustomobject]@{
            Run        = $i
            ExitCode   = $exitCode
            LogPath    = $logPath
            ReportPath = $reportDst
        }
    }

    return $results
}

# --------------------------------------------------------------------------------
# 1. Run both suites the requested number of times.
# --------------------------------------------------------------------------------
$detResults = @()
if ($doDeterministic) {
    $detResults = Invoke-SuiteRuns -Label 'Deterministic SearchApiTest' `
        -SuiteFile 'testng-search-api.xml' -Count $DeterministicRuns -RunPrefix 'det-run' `
        -ReportFileName 'search_keyword_api_report.md' `
        -ExtraMvnArgs @("-Denv=$Environment")
}
else {
    Write-Host "Skipping deterministic SearchApiTest suite." -ForegroundColor DarkGray
}

$expResults = @()
if ($doExploratory) {
    $expExtraArgs = @()
    if ($ExploratoryEnvironment) {
        $expExtraArgs += "-Denv=$ExploratoryEnvironment"
    }
    $expResults = Invoke-SuiteRuns -Label 'Exploratory SearchApiExploratoryTest' `
        -SuiteFile 'testng-exploratory.xml' -Count $ExploratoryRuns -RunPrefix 'exp-run' `
        -ReportFileName 'exploratory_search_api_report.md' `
        -ExtraMvnArgs $expExtraArgs
}
else {
    Write-Host "Skipping exploratory SearchApiExploratoryTest suite." -ForegroundColor DarkGray
}

# --------------------------------------------------------------------------------
# 2. Parse every generated report and merge into one flat row-set tagged with which
#    suite/run it came from.
# --------------------------------------------------------------------------------
Write-Host ""
Write-Host "=== Aggregating results ===" -ForegroundColor Cyan

$allRows = New-Object System.Collections.Generic.List[object]

function Add-ReportRows {
    param([string]$RunType, [object[]]$RunList)
    foreach ($r in $RunList) {
        $parsedRows = @(Get-FullResultsRows -ReportPath $r.ReportPath)
        $declaredTotal = Get-ExecutiveSummaryTotal -ReportPath $r.ReportPath
        if ($null -ne $declaredTotal -and $declaredTotal -ne $parsedRows.Count) {
            Write-Warning "$RunType run $($r.Run): parsed $($parsedRows.Count) Full Results rows but the report's own Executive Summary declares $declaredTotal. Using the report's declared total is not possible from row data alone -- inspect $($r.ReportPath) manually before trusting this run's aggregation."
        }
        foreach ($row in $parsedRows) {
            $row | Add-Member -NotePropertyName RunType -NotePropertyValue $RunType
            $row | Add-Member -NotePropertyName RunIndex -NotePropertyValue $r.Run
            $allRows.Add($row)
        }
    }
}

Add-ReportRows -RunType 'Deterministic' -RunList $detResults
Add-ReportRows -RunType 'Exploratory' -RunList $expResults

if ($allRows.Count -eq 0) {
    Write-Warning "No result rows parsed from any report — nothing to aggregate. Check the per-run logs under $runsDir."
    exit 1
}

# Per-run pass rate (sanity check / flakiness-over-time view).
$byRun = $allRows | Group-Object RunType, RunIndex | ForEach-Object {
    $total = $_.Count
    $passed = ($_.Group | Where-Object { $_.Status -eq 'PASS' }).Count
    [pscustomobject]@{
        RunType  = $_.Group[0].RunType
        RunIndex = $_.Group[0].RunIndex
        Tested   = $total
        Passed   = $passed
        Failed   = $total - $passed
        PassRate = [math]::Round(100.0 * $passed / $total, 1)
    }
} | Sort-Object RunType, RunIndex

# Per-keyword aggregation across every run of both suites.
$byKeyword = $allRows | Group-Object Keyword | ForEach-Object {
    $total = $_.Count
    $passed = ($_.Group | Where-Object { $_.Status -eq 'PASS' }).Count
    $passRate = [math]::Round(100.0 * $passed / $total, 1)
    $verdict = if ($passRate -eq 100) { 'HEALTHY' }
    elseif ($passRate -eq 0 -and $total -ge 2) { 'PERSISTENT FAILURE' }
    elseif ($passRate -eq 0) { 'FAILED (single observation)' }
    else { 'FLAKY / INTERMITTENT' }

    [pscustomobject]@{
        Keyword  = $_.Name
        Tested   = $total
        Passed   = $passed
        Failed   = $total - $passed
        PassRate = $passRate
        Verdict  = $verdict
    }
} | Sort-Object PassRate, Keyword

$priorityTerms = @('watch', 'watches', 'timepiece', 'timepieces', 'loom', 'dial', 'strap', 'clasp', 'watch case')
$priorityStats = $byKeyword | Where-Object { $priorityTerms -contains $_.Keyword.ToLowerInvariant() }
$persistentFailures = $byKeyword | Where-Object { $_.Verdict -eq 'PERSISTENT FAILURE' }
$flaky = $byKeyword | Where-Object { $_.Verdict -eq 'FLAKY / INTERMITTENT' }

# --------------------------------------------------------------------------------
# 3. Write the compiled summary (markdown, for humans) and a CSV (for spreadsheets).
# --------------------------------------------------------------------------------
$summaryPath = Join-Path $OutputDir 'summary.md'
$csvPath = Join-Path $OutputDir 'keyword-aggregation.csv'
$byKeyword | Export-Csv -Path $csvPath -NoTypeInformation

$sb = New-Object System.Text.StringBuilder
[void]$sb.AppendLine('# Search API Analysis — Compiled Summary')
[void]$sb.AppendLine('')
[void]$sb.AppendLine("Generated: $timestamp")
[void]$sb.AppendLine("Deterministic runs: $($detResults.Count) | Exploratory runs: $($expResults.Count)")
[void]$sb.AppendLine('')

[void]$sb.AppendLine('## Per-Run Pass Rates')
[void]$sb.AppendLine('')
[void]$sb.AppendLine('| Suite | Run | Tested | Passed | Failed | Pass Rate |')
[void]$sb.AppendLine('| --- | --- | --- | --- | --- | --- |')
foreach ($r in $byRun) {
    [void]$sb.AppendLine("| $($r.RunType) | $($r.RunIndex) | $($r.Tested) | $($r.Passed) | $($r.Failed) | $($r.PassRate)% |")
}
[void]$sb.AppendLine('')

[void]$sb.AppendLine('## Priority Keyword Focus')
[void]$sb.AppendLine('')
[void]$sb.AppendLine('Business-critical terms: watch, watches, timepiece, timepieces, loom, dial, strap, clasp, watch case.')
[void]$sb.AppendLine('')
[void]$sb.AppendLine('| Keyword | Tested | Passed | Failed | Pass Rate | Verdict |')
[void]$sb.AppendLine('| --- | --- | --- | --- | --- | --- |')
foreach ($k in $priorityStats) {
    [void]$sb.AppendLine("| ``$($k.Keyword)`` | $($k.Tested) | $($k.Passed) | $($k.Failed) | $($k.PassRate)% | $($k.Verdict) |")
}
[void]$sb.AppendLine('')

[void]$sb.AppendLine('## Persistent Failures (fail every time tested — real bugs, not flakiness)')
[void]$sb.AppendLine('')
if ($persistentFailures) {
    [void]$sb.AppendLine('| Keyword | Tested | Pass Rate |')
    [void]$sb.AppendLine('| --- | --- | --- |')
    foreach ($k in $persistentFailures) {
        [void]$sb.AppendLine("| ``$($k.Keyword)`` | $($k.Tested) | $($k.PassRate)% |")
    }
}
else {
    [void]$sb.AppendLine('None found in this run set.')
}
[void]$sb.AppendLine('')

[void]$sb.AppendLine('## Flaky / Intermittent Keywords (pass sometimes, fail other times)')
[void]$sb.AppendLine('')
if ($flaky) {
    [void]$sb.AppendLine('| Keyword | Tested | Passed | Pass Rate |')
    [void]$sb.AppendLine('| --- | --- | --- | --- |')
    foreach ($k in $flaky) {
        [void]$sb.AppendLine("| ``$($k.Keyword)`` | $($k.Tested) | $($k.Passed) | $($k.PassRate)% |")
    }
}
else {
    [void]$sb.AppendLine('None found in this run set.')
}
[void]$sb.AppendLine('')

[void]$sb.AppendLine('## Full Per-Keyword Aggregation')
[void]$sb.AppendLine('')
[void]$sb.AppendLine('See keyword-aggregation.csv in this same folder for the full sortable dataset.')
[void]$sb.AppendLine('')
[void]$sb.AppendLine('| Keyword | Tested | Passed | Failed | Pass Rate | Verdict |')
[void]$sb.AppendLine('| --- | --- | --- | --- | --- | --- |')
foreach ($k in $byKeyword) {
    [void]$sb.AppendLine("| ``$($k.Keyword)`` | $($k.Tested) | $($k.Passed) | $($k.Failed) | $($k.PassRate)% | $($k.Verdict) |")
}

Set-Content -Path $summaryPath -Value $sb.ToString() -Encoding UTF8

# --------------------------------------------------------------------------------
# 4. Console recap — the part a dev running this interactively actually reads.
# --------------------------------------------------------------------------------
Write-Host ""
Write-Host "=== Summary ===" -ForegroundColor Green
Write-Host "Per-run pass rates:"
$byRun | Format-Table -AutoSize | Out-String | Write-Host

Write-Host "Priority keyword coverage:"
$priorityStats | Format-Table -AutoSize | Out-String | Write-Host

if ($persistentFailures) {
    Write-Host "PERSISTENT FAILURES (real bugs — same keyword failed on every run it was tested):" -ForegroundColor Red
    $persistentFailures | Format-Table -AutoSize | Out-String | Write-Host
}
else {
    Write-Host "No persistent (100%-fail) keywords found in this run set." -ForegroundColor Green
}

if ($flaky) {
    Write-Host "FLAKY keywords (intermittent — worth another look):" -ForegroundColor Yellow
    $flaky | Format-Table -AutoSize | Out-String | Write-Host
}

Write-Host ""
Write-Host "Full compiled summary : $summaryPath" -ForegroundColor Cyan
Write-Host "Keyword CSV           : $csvPath" -ForegroundColor Cyan
Write-Host "Per-run logs/reports  : $runsDir" -ForegroundColor Cyan
