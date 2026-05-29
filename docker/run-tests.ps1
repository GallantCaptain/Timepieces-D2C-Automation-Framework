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
    [string]$Tags = "@Smoke and @Login"
)

$ErrorActionPreference = "Stop"
$projectRoot = (Get-Item (Join-Path $PSScriptRoot "..")).FullName

# Pre-download Linux msedgedriver on the Windows host to bypass Docker DNS/network failures
$driversDir = Join-Path $PSScriptRoot "drivers"
if (-not (Test-Path $driversDir)) {
    New-Item -ItemType Directory -Path $driversDir | Out-Null
}
$driverPath = Join-Path $driversDir "msedgedriver"
if (-not (Test-Path $driverPath)) {
    Write-Host "[run-tests] msedgedriver not found locally. Pre-downloading Linux x64 msedgedriver on host..."
    try {
        # Fetch the latest stable release version from Microsoft's endpoint
        $respBytes = Invoke-WebRequest -Uri "https://msedgedriver.azureedge.net/LATEST_STABLE" -UseBasicParsing
        $version = [System.Text.Encoding]::Unicode.GetString($respBytes.Content).Trim()
        Write-Host "[run-tests] Detected latest Edge stable version: $version"
        
        $url = "https://msedgedriver.azureedge.net/$version/edgedriver_linux64.zip"
        $zipPath = Join-Path $env:TEMP "edgedriver_linux64.zip"
        Write-Host "[run-tests] Downloading driver from: $url"
        Invoke-WebRequest -Uri $url -OutFile $zipPath -UseBasicParsing
        
        Write-Host "[run-tests] Extracting Linux msedgedriver..."
        $tempExtractDir = Join-Path $env:TEMP "msedge_extract"
        if (Test-Path $tempExtractDir) { Remove-Item -Recurse -Force $tempExtractDir }
        Expand-Archive -Path $zipPath -DestinationPath $tempExtractDir -Force
        
        Copy-Item -Path (Join-Path $tempExtractDir "msedgedriver") -Destination $driverPath -Force
        Remove-Item -Path $zipPath -Force
        Remove-Item -Recurse -Force $tempExtractDir
        Write-Host "[run-tests] msedgedriver successfully placed in docker/drivers/msedgedriver"
    } catch {
        Write-Error "Failed to download msedgedriver on host: $_"
        exit 1
    }
}

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
