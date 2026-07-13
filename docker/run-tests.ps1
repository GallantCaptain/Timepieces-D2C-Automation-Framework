<#
.SYNOPSIS
  Build and run the Timepieces test bed inside an Ubuntu Docker container.

.DESCRIPTION
  Mounts the project root into /workspace, persists Maven's ~/.m2 in a named
  Docker volume so subsequent runs are fast, then chains Chrome, Firefox, Edge,
  and Brave smoke tests against the configured environment.

.PARAMETER Vnc
  Run the container with Xvfb + x11vnc on localhost:5900 so you can VNC in
  and watch the browsers. Default is fully headless.

.PARAMETER Env
  Environment profile (matches -Denv=). Default: staging.

.PARAMETER Tags
  Cucumber tag expression. Default: "@Smoke and @Login".

.EXAMPLE
  ./docker/run-tests.ps1
  Headless run, env=staging, Chrome, Firefox, Edge, and Brave sequentially.

.EXAMPLE
  ./docker/run-tests.ps1 -Vnc
  Visible run inside container. Open a VNC viewer to localhost:5900 to watch.
#>

param(
    [switch]$Vnc,
    [int]$VncPort = 5900,
    [string]$Env = "staging",
    [string]$Tags = "@Smoke"
)

$ErrorActionPreference = "Stop"
$projectRoot = (Get-Item (Join-Path $PSScriptRoot "..")).FullName

Write-Host "[run-tests] Building image timepieces-tests (first build can take several minutes)..."
docker build -t timepieces-tests -f (Join-Path $PSScriptRoot "Dockerfile") $projectRoot
if ($LASTEXITCODE -ne 0) {
    Write-Error "Docker image build failed (exit $LASTEXITCODE)."
    exit $LASTEXITCODE
}

$mount = "${projectRoot}:/workspace"
$dockerArgs = @(
    "run", "--rm",
    "-v", $mount,
    "-v", "timepieces-m2-cache:/root/.m2",
    "-e", "ENV_TARGET=$Env",
    "-e", "TAGS=$Tags"
)

# Forward SMTP / mail config from the host so the container can email the Allure
# report (entrypoint.sh runs scripts/email_allure_report.py — a no-op if unset).
foreach ($name in 'SMTP_HOST', 'SMTP_PORT', 'SMTP_USER', 'SMTP_PASSWORD', 'SMTP_STARTTLS', 'SMTP_SSL', 'MAIL_FROM', 'MAIL_TO') {
    $val = [Environment]::GetEnvironmentVariable($name)
    if ($val) { $dockerArgs += @("-e", "$name=$val") }
}

if ($Vnc) {
    # Diagnostic: Check if VncPort is already bound on the host
    $portActive = Get-NetTCPConnection -LocalPort $VncPort -ErrorAction SilentlyContinue
    if ($portActive) {
        Write-Error "Port $VncPort is already in use on your host machine (PID: $($portActive.OwningProcess[0])). VNC listener cannot start."
        Write-Error "Please close the application using port $VncPort, or run with a custom port using: ./docker/run-tests.ps1 -Vnc -VncPort 5901"
        exit 1
    }
    Write-Host "[run-tests] VNC mode: connect a viewer to localhost:$VncPort once the container starts."
    $dockerArgs += @("-e", "DEBUG_VNC=true", "-p", "${VncPort}:5900")
}

$dockerArgs += "timepieces-tests"

Write-Host "[run-tests] Starting container..."
& docker @dockerArgs
exit $LASTEXITCODE
