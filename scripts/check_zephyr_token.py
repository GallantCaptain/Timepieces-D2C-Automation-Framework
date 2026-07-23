#!/usr/bin/env python3
"""
ISSUE-013 helper: is a Zephyr Scale token still ALIVE?

Rotating a credential is only half the job — the half people skip is PROVING the old one
is dead afterwards. This does that, and it never prints the token.

USAGE
-----
Check the token currently in your environment (the new one, after rotation):

    ZEPHYR_TOKEN=<new-token> py scripts/check_zephyr_token.py

Check the OLD leaked token, read straight out of git history (it was removed from HEAD in
99ae673 but remains in history — that is exactly why rotation is required):

    py scripts/check_zephyr_token.py --from-git

WHAT THE RESULT MEANS
---------------------
    200/2xx  -> the token is LIVE. If this is the old one, it has NOT been revoked yet.
    401      -> revoked or invalid. For the old token this is the outcome you want.
    403      -> authenticated but not authorised (still a live credential — not revoked).

Only the status code and a short verdict are printed. The token value is never echoed,
logged, or written anywhere: a tool built to contain a credential leak must not become
another way to spread it.
"""
import subprocess
import sys
import urllib.error
import urllib.request

API_URL = "https://eu.api.zephyrscale.smartbear.com/v2/testcases?maxResults=1"

# The commit just before the token was removed from HEAD; any earlier one also carries it.
HISTORICAL_REF = "99ae673~1:scripts/add_cart_tcs.py"


def token_from_git() -> str:
    """Reads the old token out of git history without ever printing it."""
    try:
        src = subprocess.run(
            ["git", "show", HISTORICAL_REF],
            capture_output=True, text=True, check=True,
        ).stdout
    except subprocess.CalledProcessError:
        sys.exit(f"Could not read {HISTORICAL_REF} — run this from inside the repo.")

    import re
    match = re.search(r'token\s*=\s*"([A-Za-z0-9._\-]{20,})"', src)
    if not match:
        sys.exit("No hardcoded token found at that ref (already scrubbed from history?).")
    return match.group(1)


def check(token: str) -> int:
    request = urllib.request.Request(
        API_URL,
        headers={"Authorization": f"Bearer {token}", "Accept": "application/json"},
    )
    try:
        with urllib.request.urlopen(request, timeout=25) as response:
            return response.status
    except urllib.error.HTTPError as e:
        return e.code
    except Exception as e:  # network/DNS/TLS — not a verdict about the token
        sys.exit(f"Could not reach the Zephyr API ({e.__class__.__name__}). "
                 "Check connectivity and try again — this is NOT proof of revocation.")


def main() -> None:
    from_git = "--from-git" in sys.argv
    if from_git:
        token, label = token_from_git(), "the OLD token from git history"
    else:
        import os
        token = os.environ.get("ZEPHYR_TOKEN")
        if not token:
            sys.exit("ZEPHYR_TOKEN env var not set - aborting. "
                     "(Use --from-git to test the old leaked token instead.)")
        token, label = token, "the token in ZEPHYR_TOKEN"

    status = check(token)
    print(f"Checked {label}")
    print(f"  HTTP {status}")

    if status == 401:
        print("  VERDICT: REVOKED / invalid.")
        if from_git:
            print("  The leaked token is dead. ISSUE-013 can be closed.")
    elif 200 <= status < 300:
        print("  VERDICT: LIVE.")
        if from_git:
            print("  The leaked token STILL WORKS - rotation has not happened yet.")
            print("  It is public in git history, so anyone with the repo can use it.")
        sys.exit(1)
    elif status == 403:
        print("  VERDICT: authenticated but not authorised - the credential is still LIVE.")
        sys.exit(1)
    else:
        print("  VERDICT: unexpected status; check manually before drawing a conclusion.")
        sys.exit(2)


if __name__ == "__main__":
    main()
