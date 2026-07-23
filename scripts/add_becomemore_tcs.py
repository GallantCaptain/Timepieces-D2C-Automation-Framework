"""
Push the Become More onboarding (Phase 7) test cases to Zephyr Scale (project QA).

BDD cases come from
  src/test/resources/features/01_authentication_identity/BecomeMore_Onboarding.feature
TDD cases come from
  src/test/java/com/timepieces/automation/tdd/ui/BecomeMoreSmokeTest.java

The /becomemore flow is PUBLIC. The final "Submit Profile" creates a REAL
profile — no case ever clicks it (the consent scenario asserts enablement only).

Token: read from the ZEPHYR_TOKEN env var (NOT hardcoded here), e.g.:

  ZEPHYR_TOKEN=$(grep -oP 'token = "\\K[^"]+' scripts/add_cart_tcs.py) \\
    python scripts/add_becomemore_tcs.py

Retry filter: ONLY="Name1,Name2" pushes just those cases.
NOTE: the Zephyr "tags" custom field only accepts pre-defined options — the
known-good ones used here are Regression / Smoke / TDD.
"""
import json
import os
import sys
import urllib.error
import urllib.request

BASE = "https://eu.api.zephyrscale.smartbear.com/v2"


def create_testcase(token, metadata):
    req = urllib.request.Request(
        f"{BASE}/testcases",
        data=json.dumps(metadata).encode("utf-8"),
        headers={
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json",
            "Accept": "application/json",
        },
        method="POST",
    )
    try:
        with urllib.request.urlopen(req) as response:
            return json.loads(response.read().decode("utf-8")).get("key")
    except urllib.error.HTTPError as e:
        print(f"HTTP {e.code} creating '{metadata.get('name')}':", file=sys.stderr)
        try:
            print(e.read().decode("utf-8"), file=sys.stderr)
        except Exception:
            pass
        return None
    except Exception as e:
        print(f"Error creating '{metadata.get('name')}': {e}", file=sys.stderr)
        return None


def add_testscript(token, key, script_type, text):
    req = urllib.request.Request(
        f"{BASE}/testcases/{key}/testscript",
        data=json.dumps({"type": script_type, "text": text}).encode("utf-8"),
        headers={
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json",
            "Accept": "application/json",
        },
        method="POST",
    )
    try:
        with urllib.request.urlopen(req):
            return True
    except urllib.error.HTTPError as e:
        print(f"HTTP {e.code} adding script to {key}:", file=sys.stderr)
        try:
            print(e.read().decode("utf-8"), file=sys.stderr)
        except Exception:
            pass
        return False
    except Exception as e:
        print(f"Error adding script to {key}: {e}", file=sys.stderr)
        return False


def meta(name, objective, precondition, scenario, tags, automation, script_type):
    return {
        "projectKey": "QA",
        "name": name,
        "objective": objective,
        "precondition": precondition,
        "priority": "Normal",
        "status": "Draft",
        "customFields": {
            "Test State": "Positive",
            "Scenario Outline ": scenario,
            "Automation Status": automation,
            "Test Case Type": "Functional",
            "tags": tags,
        },
    }, script_type


BDD = ["Regression"]
PRE = "/becomemore reachable (PUBLIC flow, no login; staging gate bypassed)"
NEVER = "; NEVER click 'Submit Profile' (creates a real profile)"

TESTCASES = [
    # ---------- BDD: Become More Onboarding ----------
    (meta("BM_Intro_And_Method_Dialog", "The intro screen and profile method dialog render",
          PRE, "Verify the intro screen and profile method dialog",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Become More onboarding\n'
     'Then the Become More intro screen is displayed\n'
     'When the user begins their profile\n'
     'Then the profile method dialog is displayed with both entry options\n'
     'When the user closes the profile method dialog'),

    (meta("BM_PersonalInfo_Validation_Gate", "The personal info step gates progression until valid",
          PRE, "Verify the personal info step gates progression until valid",
          BDD, "Not Automated", "bdd"),
     'Given the user opens the Become More section "name"\n'
     'Then the step indicator reads "Step 01 of 07"\n'
     'And the next step control is disabled\n'
     'When the user fills the personal info with "Test User", "Mr", "01/01/1990" and "Professional"\n'
     'Then the next step control is enabled'),

    (meta("BM_Section_DeepLinks", "Each journey section deep-links with its heading",
          PRE, "Verify each journey section deep-links with its heading",
          BDD, "Not Automated", "bdd"),
     'Given the user opens the Become More section "<section>"\n'
     'Then the browser is on the Become More section "<section>"\n'
     'And the step heading contains "<heading>"\n'
     '# Examples: name/location/about/building/watchjourney/interest/timepieceshouse/photo\n'
     '# (?section= query param drives the journey; photo has no step indicator)'),

    (meta("BM_WatchJourney_AutoAdvance", "A watch-journey chip auto-advances the journey",
          PRE, "Verify a watch-journey chip auto-advances the journey",
          BDD, "Not Automated", "bdd"),
     'Given the user opens the Become More section "watchjourney"\n'
     'When the user selects the chip "This would be my first serious watch"\n'
     'Then the browser is on the Become More section "interest"'),

    (meta("BM_Interests_MultiSelect_Gate", "The interests multi-select gates progression",
          PRE, "Verify the interests multi-select gates progression",
          BDD, "Not Automated", "bdd"),
     'Given the user opens the Become More section "interest"\n'
     'Then the next step control is disabled\n'
     'When the user selects the chip "Watches"\n'
     'Then the chip "Watches" is selected\n'
     'And the next step control is enabled'),

    (meta("BM_House_Inspiration_And_Skip", "The Timepieces House step offers inspiration and can be skipped",
          PRE, "Verify the Timepieces House step offers inspiration and can be skipped",
          BDD, "Not Automated", "bdd"),
     'Given the user opens the Become More section "timepieceshouse"\n'
     'When the user opens the curated inspirations\n'
     'Then the curated inspirations are displayed\n'
     'When the user skips the house step\n'
     'Then the browser is on the Become More section "photo"'),

    (meta("BM_Consent_Gates_Submit", "The profile consent gates the final submission",
          PRE + NEVER, "Verify the profile consent gates the final submission",
          BDD, "Not Automated", "bdd"),
     'Given the user opens the Become More section "photo"\n'
     'Then the photo step is displayed\n'
     'And the submit profile button is disabled\n'
     'When the user ticks the profile consent\n'
     'Then the submit profile button is enabled'),

    # ---------- TDD (plaintext): BecomeMoreSmokeTest ----------
    (meta("TDD_BM_Intro_Dialog", "TDD smoke: intro opens the method dialog",
          PRE, "introOpensTheMethodDialog()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open /becomemore (unlock the staging gate if present).\n"
     "2. Assert the intro ('Become More' + begin button); click Begin.\n"
     "3. Assert the method dialog (LinkedIn import + Start Manually)."),

    (meta("TDD_BM_Step1_Gate", "TDD smoke: personal info step gates until valid",
          PRE, "personalInfoStepGatesUntilValid()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Deep-link to ?section=name; assert 'Step 01 of 07'.\n"
     "2. Assert Next starts disabled.\n"
     "3. Fill name/salutation/DOB/persona; assert Next enables."),

    (meta("TDD_BM_Interests_Gate", "TDD smoke: interests multi-select gates progression",
          PRE, "interestsMultiSelectGatesProgression()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Deep-link to ?section=interest; assert Next starts disabled.\n"
     "2. Select 'Watches'; assert the chip renders selected and Next enables."),

    (meta("TDD_BM_Consent_Gate", "TDD smoke: photo consent gates the submit",
          PRE + NEVER, "photoConsentGatesTheSubmit()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Deep-link to ?section=photo; assert avatar + consent render.\n"
     "2. Assert 'Submit Profile' starts disabled.\n"
     "3. Tick consent; assert it enables. Do NOT click submit."),
]


def main():
    token = os.environ.get("ZEPHYR_TOKEN")
    if not token:
        print("ZEPHYR_TOKEN env var not set - aborting (no push).", file=sys.stderr)
        sys.exit(2)

    only = os.environ.get("ONLY")
    only_names = {n.strip() for n in only.split(",")} if only else None

    created, failed = 0, 0
    for (metadata, script_type), text in TESTCASES:
        name = metadata["name"]
        if only_names is not None and name not in only_names:
            continue
        print(f"Creating: {name} ...")
        key = create_testcase(token, metadata)
        if not key:
            failed += 1
            continue
        ok = add_testscript(token, key, script_type, text)
        print(f"  -> {key} ({'script added' if ok else 'SCRIPT FAILED'})")
        created += 1 if ok else 0
        failed += 0 if ok else 1
    print(f"\nDone. {created} created with scripts, {failed} failed, {len(TESTCASES)} total.")


if __name__ == "__main__":
    main()
