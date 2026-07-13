#!/usr/bin/env bash
# Entrypoint for the Timepieces test bed container.
#
# Env vars (all optional):
#   ENV_TARGET   profile to run against (default: staging) — maps to -Denv=
#   DEBUG_VNC    when "true", start Xvfb + x11vnc on :5900 and run headed
#   TAGS         cucumber tag expression (default: "@Smoke and @Login")
#   BROWSERS     space-separated browser list (default: "chrome firefox edge brave")
#
# Exits non-zero if any browser run failed; runs both regardless of order.

set -uo pipefail

ENV_TARGET="${ENV_TARGET:-staging}"
TAGS="${TAGS:-@Smoke}"
BROWSERS="${BROWSERS:-chrome firefox edge brave}"

if [[ "${DEBUG_VNC:-false}" == "true" ]]; then
    echo "[entrypoint] DEBUG_VNC=true — starting Xvfb + x11vnc on display :99 / port 5900"
    Xvfb :99 -screen 0 1920x1080x24 >/tmp/xvfb.log 2>&1 &
    sleep 1
    fluxbox >/tmp/fluxbox.log 2>&1 &
    x11vnc -display :99 -nopw -listen 0.0.0.0 -xkb -forever -shared >/tmp/x11vnc.log 2>&1 &
    HEADLESS=false
    echo "[entrypoint] VNC ready — connect a viewer to <host>:5900"
else
    HEADLESS=true
fi

echo "[entrypoint] Cleaning previous build artifacts..."
mvn clean

OVERALL=0
for browser in $BROWSERS; do
    echo
    echo "=============================================================="
    echo "[entrypoint] env=${ENV_TARGET} browser=${browser} headless=${HEADLESS}"
    echo "=============================================================="
    mvn test \
        -Denv="$ENV_TARGET" \
        -Dbrowser="$browser" \
        -Dheadless="$HEADLESS" \
        -Dchrome.no.sandbox=true \
        "-Dcucumber.filter.tags=${TAGS}"
    rc=$?
    if [[ $rc -ne 0 ]]; then
        echo "[entrypoint] FAILED browser=${browser} exit=${rc}"
        OVERALL=$rc
    else
        echo "[entrypoint] PASSED browser=${browser}"
    fi
done

echo
echo "[entrypoint] overall exit=${OVERALL}"

# Generate the Allure report and email it (no-op unless SMTP_* env is provided).
echo "[entrypoint] Generating Allure report and emailing it (if SMTP configured)..."
mvn -q allure:report || echo "[entrypoint] allure:report failed; emailing summary only."
RUN_SOURCE=docker env="${ENV_TARGET}" python3 scripts/email_allure_report.py || true

if [[ "${DEBUG_VNC:-false}" == "true" ]]; then
    echo "=============================================================="
    echo "[entrypoint] DEBUG_VNC is true. Keeping container alive for 30 minutes"
    echo "[entrypoint] so you can connect via VNC, inspect the report or debug."
    echo "[entrypoint] Press Ctrl+C in your terminal to stop the container."
    echo "=============================================================="
    sleep 1800
fi

exit $OVERALL
