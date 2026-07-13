#!/usr/bin/env python3
"""Email the Allure report after a test run (local / docker / CI).

Sends a pass/fail summary with the zipped Allure HTML report attached. Designed
to be safe to call unconditionally at the end of any run:

  * If SMTP is not configured, it logs a notice and exits 0 (never breaks a run).
  * If sending fails, it logs a warning and exits 0 (unless --strict).
  * If the report is missing, it sends a summary-only email.

Configuration is entirely via environment variables (no secrets in source):

  Required to actually send:
    SMTP_HOST                SMTP server host
    SMTP_USER                SMTP username / login
    SMTP_PASSWORD            SMTP password or app-password / API key
  Optional:
    SMTP_PORT                default 587
    SMTP_STARTTLS            'true' (default) | 'false'
    SMTP_SSL                 'false' (default) | 'true'  (implicit TLS, e.g. port 465)
    MAIL_FROM                default = SMTP_USER
    MAIL_TO                  default = 167759523+GallantCaptain@users.noreply.github.com (comma-separated ok)
    MAIL_MAX_ATTACH_MB       default 20; larger reports are not attached
    ALLURE_REPORT_DIR        default target/site/allure-maven-plugin
    RUN_SOURCE               local | docker | ci (labels the subject/body)
    MAIL_ATTACH              'true' (default) attaches the zip; 'false' = link-only
    REPORT_URL               link to the uploaded report artifact (CI link mode)
    RUN_URL                  link to the CI run page (Artifacts section)

Any of the above may also be placed in a gitignored `.env` file at the repo
root (KEY=VALUE per line); real environment variables take precedence.

Usage:
    python scripts/email_allure_report.py [--strict]
"""
import datetime
import json
import os
import pathlib
import smtplib
import ssl
import subprocess
import sys
import zipfile
from email.message import EmailMessage

DEFAULT_TO = "167759523+GallantCaptain@users.noreply.github.com"
REPORT_DIR = pathlib.Path(os.environ.get("ALLURE_REPORT_DIR", "target/site/allure-maven-plugin"))


def log(msg):
    print(f"[email-allure] {msg}", flush=True)


def smtp_config():
    host = os.environ.get("SMTP_HOST", "").strip()
    user = os.environ.get("SMTP_USER", "").strip()
    password = os.environ.get("SMTP_PASSWORD", "").strip()
    if not (host and user and password):
        return None
    return {
        "host": host,
        "port": int(os.environ.get("SMTP_PORT", "587")),
        "user": user,
        "password": password,
        "starttls": os.environ.get("SMTP_STARTTLS", "true").lower() == "true",
        "ssl": os.environ.get("SMTP_SSL", "false").lower() == "true",
        "mail_from": os.environ.get("MAIL_FROM", user).strip(),
        "mail_to": [a.strip() for a in os.environ.get("MAIL_TO", DEFAULT_TO).split(",") if a.strip()],
        "max_attach_mb": float(os.environ.get("MAIL_MAX_ATTACH_MB", "20")),
    }


def read_statistics():
    """Read pass/fail counts from the generated report's summary widget."""
    summary = REPORT_DIR / "widgets" / "summary.json"
    if summary.is_file():
        try:
            return json.loads(summary.read_text(encoding="utf-8")).get("statistic", {})
        except Exception as exc:  # noqa: BLE001 - summary is best-effort
            log(f"could not parse summary.json: {exc}")
    return {}


def git(*args):
    try:
        return subprocess.check_output(["git", *args], text=True, stderr=subprocess.DEVNULL).strip()
    except Exception:  # noqa: BLE001 - git context is best-effort
        return ""


def zip_report():
    if not REPORT_DIR.is_dir():
        return None
    out = pathlib.Path("target") / "allure-report.zip"
    out.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as archive:
        for item in REPORT_DIR.rglob("*"):
            if item.is_file():
                archive.write(item, pathlib.Path("allure-report") / item.relative_to(REPORT_DIR))
    return out


def build_message(cfg):
    stat = read_statistics()
    total = stat.get("total", 0)
    passed = stat.get("passed", 0)
    failed = stat.get("failed", 0)
    broken = stat.get("broken", 0)
    skipped = stat.get("skipped", 0)

    source = os.environ.get("RUN_SOURCE", "local")
    env = os.environ.get("env") or os.environ.get("ENV_TARGET") or "staging"
    branch = git("rev-parse", "--abbrev-ref", "HEAD")
    sha = git("rev-parse", "--short", "HEAD")
    run_no = os.environ.get("GITHUB_RUN_NUMBER", "")
    stamp = datetime.datetime.now().strftime("%Y-%m-%d %H:%M")

    # Link mode (used by CI): send a summary + link to the uploaded artifact
    # instead of attaching the zip, which corporate mail filters often block.
    attach = os.environ.get("MAIL_ATTACH", "true").lower() == "true"
    report_url = os.environ.get("REPORT_URL", "").strip()
    run_url = os.environ.get("RUN_URL", "").strip()

    if total == 0:
        status = "NO TESTS"
    elif failed == 0 and broken == 0:
        status = "PASSED"
    else:
        status = "FAILED"

    subject = f"[Timepieces Allure] {status} - {passed}/{total} passed ({failed} failed) - {env} [{source}]"

    lines = [
        f"Timepieces automation run - {stamp}",
        f"Source: {source}   Env: {env}   Branch: {branch or '?'} @ {sha or '?'}"
        + (f"   CI run #{run_no}" if run_no else ""),
        "",
        f"  Total:   {total}",
        f"  Passed:  {passed}",
        f"  Failed:  {failed}",
        f"  Broken:  {broken}",
        f"  Skipped: {skipped}",
        "",
    ]

    links = []
    if report_url:
        links.append(f"  Report artifact: {report_url}")
    if run_url:
        links.append(f"  CI run (Artifacts section): {run_url}")
    if links:
        lines.append("View / download the full Allure report:")
        lines.extend(links)
        lines.append("")

    msg = EmailMessage()
    msg["Subject"] = subject
    msg["From"] = cfg["mail_from"]
    msg["To"] = ", ".join(cfg["mail_to"])

    attached = False
    if attach:
        archive = zip_report()
        if archive and archive.is_file():
            size_mb = archive.stat().st_size / (1024 * 1024)
            if size_mb <= cfg["max_attach_mb"]:
                msg.add_attachment(
                    archive.read_bytes(), maintype="application", subtype="zip", filename="allure-report.zip"
                )
                attached = True
                log(f"attached allure-report.zip ({size_mb:.1f} MB)")
            else:
                log(f"report is {size_mb:.1f} MB (> {cfg['max_attach_mb']} MB) - sending summary/link only.")

    if attached:
        lines.append("The full Allure HTML report is attached (allure-report.zip): unzip and")
        lines.append("open index.html. Failure screenshots and MP4 videos are embedded in it.")
    elif links:
        lines.append("Open the link above and download the report artifact (a zip of the HTML")
        lines.append("report); failure screenshots and MP4 videos are embedded in it.")
    else:
        lines.append("No report was attached or linked - run `mvn allure:report` then re-send,")
        lines.append("or grab it from the CI run's Artifacts section.")

    msg.set_content("\n".join(lines) + "\n")
    return msg


def send(cfg, msg):
    context = ssl.create_default_context()
    if cfg["ssl"]:
        with smtplib.SMTP_SSL(cfg["host"], cfg["port"], context=context) as server:
            server.login(cfg["user"], cfg["password"])
            server.send_message(msg)
    else:
        with smtplib.SMTP(cfg["host"], cfg["port"]) as server:
            if cfg["starttls"]:
                server.starttls(context=context)
            server.login(cfg["user"], cfg["password"])
            server.send_message(msg)


def load_env_file():
    """Load KEY=VALUE lines from a gitignored .env at the repo root (or CWD),
    without overriding variables already set in the real environment. This gives
    local runs a place to keep SMTP creds out of the shell history and source."""
    for candidate in (pathlib.Path(".env"), pathlib.Path(__file__).resolve().parent.parent / ".env"):
        if not candidate.is_file():
            continue
        for raw in candidate.read_text(encoding="utf-8").splitlines():
            line = raw.strip()
            if not line or line.startswith("#") or "=" not in line:
                continue
            key, _, value = line.partition("=")
            key = key.strip()
            value = value.strip().strip('"').strip("'")
            if key and key not in os.environ:
                os.environ[key] = value
        log(f"loaded env from {candidate}")
        return


def main():
    load_env_file()
    strict = "--strict" in sys.argv[1:]
    cfg = smtp_config()
    if cfg is None:
        log("SMTP not configured (need SMTP_HOST, SMTP_USER, SMTP_PASSWORD) - skipping email.")
        return 1 if strict else 0

    msg = build_message(cfg)
    try:
        send(cfg, msg)
        log(f"sent report email to {', '.join(cfg['mail_to'])}")
        return 0
    except Exception as exc:  # noqa: BLE001 - mail failure must not break the run by default
        log(f"WARNING: failed to send email: {exc}")
        return 1 if strict else 0


if __name__ == "__main__":
    sys.exit(main())
