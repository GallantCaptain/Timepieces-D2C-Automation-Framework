"""
Push the Homepage (Phase 6) test cases to Zephyr Scale (project QA).

BDD cases come from
  src/test/resources/features/02_catalog_product_discovery/Homepage_Content_Sections.feature
TDD cases come from
  src/test/java/com/timepieces/automation/tdd/ui/HomepageContentSmokeTest.java

Token: read from the ZEPHYR_TOKEN env var (NOT hardcoded here), e.g.:

  ZEPHYR_TOKEN=$(grep -oP 'token = "\\K[^"]+' scripts/add_cart_tcs.py) \\
    python scripts/add_homepage_tcs.py

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
PRE = "User on the storefront homepage (staging gate bypassed)"

TESTCASES = [
    # ---------- BDD: Homepage Content Sections ----------
    (meta("Home_Hero_Showreel", "The hero renders its showreel and controls",
          PRE, "Verify the hero renders its showreel and Discover CTA",
          BDD, "Not Automated", "bdd"),
     'Then the homepage hero section is displayed\n'
     'And the hero background showreel video is present\n'
     'And the hero mute toggle is displayed'),

    (meta("Home_TwoStories_Cards", "The Two Stories release cards render (Manifesta locked)",
          PRE, "Verify the Two Stories release cards",
          BDD, "Not Automated", "bdd"),
     'Then the "Two Stories, One Philosophy" section is displayed with both release cards\n'
     'And the Astonia story card carries its arrow CTA\n'
     'And the Manifesta story card is locked with the badge "SEP, 2026"'),

    (meta("Home_Brand_Quote", "The brand story quote is presented",
          PRE, "Verify the brand story quote is presented",
          BDD, "Not Automated", "bdd"),
     'Then the brand story section is displayed\n'
     'And the brand quote contains "What you wear on your wrist"'),

    (meta("Home_FnP_Carousel", "The homepage Friends & Partners carousel renders and links out",
          PRE, "Verify the homepage Friends and Partners carousel",
          BDD, "Not Automated", "bdd"),
     'Then the homepage Friends and Partners section is displayed\n'
     'And homepage partner cards are displayed\n'
     'And every homepage partner card links to a partner profile'),

    (meta("Home_LastRelease_Slider", "The Last Release slider renders and links to PDPs",
          PRE, "Verify the Last Release slider",
          BDD, "Not Automated", "bdd"),
     'Then the Last Release slider is displayed\n'
     'And Last Release cards are displayed\n'
     'And every Last Release card links to a timepiece PDP'),

    (meta("Home_Sequence_And_USP", "The crafted-with-intent sequence and USP marquee render",
          PRE, "Verify the crafted-with-intent sequence and USP marquee",
          BDD, "Not Automated", "bdd"),
     'Then the crafted-with-intent scroll sequence is displayed\n'
     'And the FEATURED IN marquee is displayed'),

    (meta("Home_Stories_Modal", "The stories modal opens, pauses, advances and closes",
          PRE, "Verify the stories modal opens, pauses and closes",
          BDD, "Not Automated", "bdd"),
     'When the user opens the stories modal\n'
     'Then the stories modal is open\n'
     'And the stories modal shows at least 1 story\n'
     'And the current story image is displayed\n'
     'When the user pauses the stories\n'
     'Then the stories auto-advance is paused\n'
     'When the user taps to the next story\n'
     'And the user closes the stories modal\n'
     'Then the stories modal is closed'),

    (meta("Home_DropPopup_Mounts", "The drop card popup mounts on a fresh session (~15s)",
          PRE + "; fresh session (popup not yet shown/dismissed)", "Verify the drop card popup mounts on a fresh session",
          BDD, "Not Automated", "bdd"),
     'Then the drop card popup mounts within 25 seconds'),

    (meta("Home_DropPopup_Suppression", "The drop popup is suppressible via its sessionStorage state",
          PRE, "Verify the drop card popup can be suppressed via its session state",
          BDD, "Not Automated", "bdd"),
     'When the user marks the drop popup as already visited in the session\n'
     'And the user reloads the homepage\n'
     'Then the drop card popup does not mount within 20 seconds'),

    (meta("Home_Country_Selector", "The footer country selector opens and filters",
          PRE, "Verify the footer country selector opens and filters",
          BDD, "Not Automated", "bdd"),
     'When the user opens the footer country selector\n'
     'Then the country selector dropdown is open\n'
     'When the user searches the country selector for "United"\n'
     'Then the country options are filtered down'),

    # ---------- TDD (plaintext): HomepageContentSmokeTest ----------
    (meta("TDD_Home_Section_Sweep", "TDD smoke: full homepage section sweep renders",
          "Production storefront reachable", "homepageSectionSweepRenders()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open the homepage (unlock the staging gate if present).\n"
     "2. Assert hero + showreel video; Two Stories (Manifesta LOCKED);\n"
     "   brand-story parallax; Friends & Partners (cards -> profiles);\n"
     "   Last Release (cards -> PDPs); Crafted With Intent; FEATURED IN marquee."),

    (meta("TDD_Home_Stories_Modal", "TDD smoke: stories modal opens, pauses and closes",
          "Production storefront reachable", "storiesModalOpensPausesAndCloses()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open the stories modal from the header trigger.\n"
     "2. Assert it opens with at least one story; pause it; close it."),

    (meta("TDD_Home_Popup_Suppression", "TDD smoke: drop popup suppressible via sessionStorage",
          "Production storefront reachable", "dropPopupIsSuppressibleViaSessionState()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open the homepage; set sessionStorage astonia_popup_visited=1.\n"
     "2. Reload; assert the popup does NOT mount within 20s."),

    (meta("TDD_Home_Country_Selector", "TDD smoke: country selector opens and filters",
          "Production storefront reachable", "countrySelectorOpensAndFilters()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open the footer country selector.\n"
     "2. Assert the dropdown opens; search 'United'; assert the options filter down."),
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
