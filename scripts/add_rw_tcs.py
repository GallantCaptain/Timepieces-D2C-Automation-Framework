"""
Push the Timepieces World (Phase 4) test cases to Zephyr Scale (project QA).

BDD cases come from the feature files under
  src/test/resources/features/08_timepieces_world/*.feature
TDD cases come from
  src/test/java/com/timepieces/automation/tdd/ui/TimepiecesWorldSmokeTest.java

The microsite lives on https://world.timepieces-d2c.example (fixed public origin). The
waitlist submit raises a REAL signup — no case ever submits it.

Token: read from the ZEPHYR_TOKEN env var (NOT hardcoded here), e.g.:

  ZEPHYR_TOKEN=$(grep -oP 'token = "\\K[^"]+' scripts/add_cart_tcs.py) \\
    python scripts/add_rw_tcs.py

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
RW_PRE = "world.timepieces-d2c.example reachable (fixed public origin, no login/gate)"

TESTCASES = [
    # ---------- BDD: Timepieces World Experience ----------
    (meta("RW_Experience_Welcome_Gate", "The welcome gate starts the canvas experience",
          RW_PRE, "Verify the welcome gate starts the canvas experience",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Timepieces World experience page\n'
     'Then the Enter Experience gate is displayed\n'
     'When the user enters the experience\n'
     'Then the experience canvas is running\n'
     'And the progress rail shows 7 scene segments'),

    (meta("RW_Experience_Canvas_Nudge", "Nudging the canvas reaches a family scene with DISCOVER",
          RW_PRE, "Verify nudging the canvas reaches a family scene with a DISCOVER call to action",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Timepieces World experience page\n'
     'And the user enters the experience\n'
     'When the user nudges the canvas forward until a family scene appears\n'
     'Then the active scene shows a family heading\n'
     'And the DISCOVER call to action is displayed'),

    (meta("RW_Experience_Discover_Navigation", "DISCOVER opens the family showcase",
          RW_PRE, "Verify the DISCOVER call to action opens the family showcase",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Timepieces World experience page\n'
     'And the user enters the experience\n'
     'And the user nudges the canvas forward until a family scene appears\n'
     'When the user clicks the DISCOVER call to action\n'
     'Then the browser lands on a Timepieces World showcase page'),

    (meta("RW_Sidebar_Family_Links", "The sidebar menu lists all five family destinations",
          RW_PRE, "Verify the sidebar menu lists all five family destinations",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Timepieces World experience page\n'
     'When the user opens the Timepieces World sidebar menu\n'
     'Then the Timepieces World sidebar lists all 5 family destinations\n'
     'When the user closes the Timepieces World sidebar via the backdrop\n'
     'Then the Timepieces World sidebar is closed'),

    (meta("RW_Logo_To_Storefront", "The header logo leaves for the main storefront from the experience",
          RW_PRE, "Verify the header logo leaves for the main storefront from the experience page",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Timepieces World experience page\n'
     'When the user clicks the Timepieces logo in the Timepieces World header\n'
     'Then the browser lands on the main Timepieces storefront'),

    # ---------- BDD: Timepieces World Showcases ----------
    (meta("RW_Showcase_Shared_Template", "Each family showcase renders the shared template",
          RW_PRE, "Verify each family showcase renders the shared template",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Timepieces World showcase of "<family>"\n'
     'Then the showcase title matches the "<family>" family pattern\n'
     'And the family logo and tagline are displayed\n'
     'And the waitlist form is displayed\n'
     '# Examples: arvion / monarch / auriqua / astonia / manifesta'),

    (meta("RW_Waitlist_Disabled_Until_Valid", "The waitlist submit is gated until the form validates",
          RW_PRE + "; NEVER submit (real signup)", "Verify the waitlist submit is gated until the form validates",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Timepieces World showcase of "astonia"\n'
     'Then the waitlist form is displayed\n'
     'And the waitlist submit button is disabled'),

    (meta("RW_Showcase_Specs_And_Materials", "The calibre specs and material details are presented",
          RW_PRE, "Verify the calibre specs and material details are presented",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Timepieces World showcase of "astonia"\n'
     'Then the showcase lists the calibre spec "BASE CALIBRE"\n'
     'And the showcase lists the calibre spec "TYPE"\n'
     'And material learn-more buttons are offered\n'
     'And the showcase footer support links are present'),

    (meta("RW_BackToScene_Navigation", "Back to scene returns to the experience page",
          RW_PRE, "Verify Back to scene returns to the experience page",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Timepieces World showcase of "monarch"\n'
     'When the user clicks Back to scene\n'
     'Then the browser returns to the Timepieces World experience page'),

    (meta("RW_Logo_DualNav_From_Showcase", "The header logo returns to the experience from a showcase",
          RW_PRE, "Verify the header logo returns to the experience from a showcase page",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Timepieces World showcase of "auriqua"\n'
     'When the user clicks the Timepieces logo in the Timepieces World header\n'
     'Then the browser returns to the Timepieces World experience page'),

    # ---------- TDD (plaintext): TimepiecesWorldSmokeTest ----------
    (meta("TDD_RW_Experience_Gate", "TDD smoke: experience gate starts the canvas",
          RW_PRE, "experienceGateStartsTheCanvas()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open world.timepieces-d2c.example via TimepiecesWorld_ExperiencePage.\n"
     "2. Assert the 'Enter Experience' gate; click it.\n"
     "3. Assert the canvas, the progress rail, and its 7 scene segments."),

    (meta("TDD_RW_Canvas_Nudge", "TDD smoke: canvas nudges to a family scene",
          RW_PRE, "canvasNudgesToAFamilySceneWithDiscover()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Enter the experience.\n"
     "2. Dispatch synthetic WheelEvents (advanceCanvas) until the DISCOVER CTA renders (<=90s).\n"
     "3. Assert a non-blank family heading accompanies it."),

    (meta("TDD_RW_Showcase_Template", "TDD smoke: showcase renders the shared template",
          RW_PRE, "showcaseRendersTheSharedTemplate()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open the Astonia showcase via TimepiecesWorld_AstoniaPage.\n"
     "2. Assert the title pattern, the ASTONIA logo, the BASE CALIBRE spec,\n"
     "   learn-more buttons and the footer support links."),

    (meta("TDD_RW_Waitlist_Gate", "TDD smoke: waitlist submit is disabled until valid",
          RW_PRE + "; NEVER submit (real signup)", "waitlistSubmitIsGatedUntilValid()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open the Astonia showcase.\n"
     "2. Assert the waitlist form is present.\n"
     "3. Assert the submit button starts DISABLED. Do NOT fill/submit."),

    (meta("TDD_RW_Sidebar_Links", "TDD smoke: sidebar lists all five families",
          RW_PRE, "sidebarListsAllFiveFamilies()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open the experience page; toggle the header hamburger.\n"
     "2. Assert the drawer opens (translate-x-0) and lists exactly 5 family links."),

    (meta("TDD_RW_Logo_DualNav", "TDD smoke: header logo returns to the experience from a showcase",
          RW_PRE, "headerLogoReturnsToExperienceFromAShowcase()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open the Monarch showcase.\n"
     "2. Click the header Timepieces logo.\n"
     "3. Assert the browser returns to world.timepieces-d2c.example (not /products/)."),
]


def main():
    token = os.environ.get("ZEPHYR_TOKEN")
    if not token:
        print("ZEPHYR_TOKEN env var not set — aborting (no push).", file=sys.stderr)
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
