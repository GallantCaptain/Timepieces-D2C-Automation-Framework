"""
Push the Accessory (Phase 2) test cases to Zephyr Scale (project QA).

BDD cases come from the two feature files under
  src/test/resources/features/02_catalog_product_discovery/Accessory_*.feature
TDD cases come from
  src/test/java/com/timepieces/automation/tdd/ui/AccessoryPdpSmokeTest.java

Token: read from the ZEPHYR_TOKEN env var (NOT hardcoded here). The existing
scripts/add_*.py hardcode a committed token — that token should be rotated and
moved to a secret; this script deliberately takes it from the environment, e.g.:

  ZEPHYR_TOKEN=$(grep -oP 'token = "\\K[^"]+' scripts/add_cart_tcs.py) \\
    python scripts/add_accessory_tcs.py
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


# (metadata, script_type), text
# NOTE: the Zephyr "tags" custom field only accepts pre-defined options; "Straps"
# is not one, so strap cases reuse the generic Accessories/Regression tags.
BDD = ["Regression", "Accessories"]
STR = ["Regression", "Accessories"]

TESTCASES = [
    # ---------- BDD: Accessory Product Detail Page ----------
    (meta("Accessory_PDP_Core_Details", "Accessory PDP shows the purchasable core details",
          "User on the storefront (staging gate bypassed)", "Verify the accessory PDP shows the purchasable core details",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"\n'
     'Then the accessory product title is displayed\n'
     'And the accessory price is displayed\n'
     'And the accessory compatibility caption is displayed\n'
     'And the "Add to cart" button is displayed and enabled'),

    (meta("Accessory_Add_Strap_To_Cart", "A strap accessory can be added to the cart",
          "User on the storefront (staging gate bypassed)", "Verify a strap accessory can be added to the cart",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"\n'
     'When the user clicks the "Add to cart" button on the accessory PDP\n'
     'Then the slide-out cart drawer is displayed\n'
     'And the cart badge count is updated to "1"\n'
     'And the cart drawer displays the product "fkm-rubber-strap-black" with its correct title and price'),

    (meta("Accessory_Color_Switcher_Navigates", "Color card-button navigates to a distinct accessory PDP",
          "User on a strap PDP", "Verify the color card-button switcher navigates to a distinct accessory PDP",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "ostrich-leather-strap-black"\n'
     'When the user selects the color variant that links to "ostrich-leather-strap-brown"\n'
     'Then the browser navigates to the accessory PDP "ostrich-leather-strap-brown"'),

    (meta("Accessory_RoseGold_Size_Switcher", "Rose Gold clasp size switcher navigates 20mm<->22mm",
          "User on the Rose Gold single-deployment clasp PDP", "Verify the Rose Gold clasp size switcher navigates between 20mm and 22mm",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "single-deployment-clasp-rose-gold-20mm"\n'
     'When the user selects the "22mm" size on the clasp PDP\n'
     'Then the browser navigates to the accessory PDP "single-deployment-clasp-rose-gold-22mm"\n'
     'When the user selects the "20mm" size on the clasp PDP\n'
     'Then the browser navigates to the accessory PDP "single-deployment-clasp-rose-gold-20mm"'),

    (meta("Accessory_Accordions_Expand", "Product-detail accordions expand",
          "User on an accessory PDP", "Verify the product-detail accordions expand",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "premium-watch-case"\n'
     'When the user expands the "Description" accordion\n'
     'Then the "Description" accordion content is visible\n'
     'When the user expands the "Technical details" accordion\n'
     'Then the "Technical details" accordion content is visible'),

    (meta("Accessory_Guarantee_Drawer", "Guarantee side drawer opens and closes",
          "User on an accessory PDP", "Verify the Guarantee side drawer opens and closes",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "butterfly-clasp-gold-18mm"\n'
     'When the user opens the Guarantee side drawer\n'
     'Then the side drawer is displayed\n'
     'When the user closes the side drawer using the close button\n'
     'Then the side drawer is hidden'),

    (meta("Accessory_ReturnRefund_Drawer_Backdrop", "Return & Refund drawer closes on backdrop click",
          "User on an accessory PDP", "Verify the Return and Refund side drawer closes on backdrop click",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "butterfly-clasp-gold-18mm"\n'
     'When the user opens the Return and Refund side drawer\n'
     'Then the side drawer is displayed\n'
     'When the user clicks outside the side drawer on the backdrop\n'
     'Then the side drawer is hidden'),

    (meta("Accessory_Wishlist_Toggle", "Wishlisting an accessory toggles its state",
          "User on an accessory PDP", "Verify wishlisting an accessory toggles its state",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "hybrid-leather-strap-blue"\n'
     'When the user clicks the wishlist button on the accessory PDP\n'
     'Then the accessory is marked as wishlisted'),

    (meta("Accessory_Model_Carousel_Links", "Bottom model carousel links to other accessory PDPs",
          "User on an accessory PDP", "Verify the bottom recommended-models carousel links to other accessory PDPs",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "suede-leather-strap-brown"\n'
     'Then the recommended-models carousel is displayed\n'
     'And every recommended-model card links to an "/accessories/" product page'),

    # ---------- BDD: Accessory Strap Configurator ----------
    (meta("Strap_DialTry_Present", "Dial-try carousel is present on a strap PDP",
          "User on a strap PDP", "Verify the dial-try carousel is present on a strap PDP",
          STR, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"\n'
     'Then the dial-try carousel "A new look for your watch" is displayed'),

    (meta("Strap_DialTry_Cycles", "Dial-try carousel cycles through compatible watch previews",
          "User on a strap PDP with the dial-try carousel visible", "Verify the dial-try carousel cycles through compatible watch previews",
          STR, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "ostrich-leather-strap-black"\n'
     'And the dial-try carousel "A new look for your watch" is displayed\n'
     'When the user advances the dial-try carousel to the next watch\n'
     'Then the strap preview updates to the next compatible watch\n'
     'When the user moves the dial-try carousel to the previous watch\n'
     'Then the strap preview updates to the previous compatible watch'),

    (meta("Strap_DialTry_Compatible_Only", "Only compatible watch dials are offered in the strap preview",
          "User on a strap PDP", "Verify only compatible watch dials are offered in the strap preview",
          STR, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"\n'
     'Then the dial-try carousel only shows dials compatible with "Astonia" and "Astonia Sports"'),

    (meta("Strap_ClaspUpgrade_Present", "Clasp-upgrade cross-sell is offered on a strap PDP",
          "User on a strap PDP", "Verify the clasp-upgrade cross-sell is offered on a strap PDP",
          STR, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "ostrich-leather-strap-black"\n'
     'Then the "Upgrade your strap" clasp-upgrade card is displayed'),

    (meta("Strap_ClaspUpgrade_Add", "A recommended clasp can be added from the strap upgrade card",
          "User on a strap PDP with the upgrade card visible", "Verify a recommended clasp can be added from the strap upgrade card",
          STR, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "ostrich-leather-strap-black"\n'
     'And the "Upgrade your strap" clasp-upgrade card is displayed\n'
     'When the user clicks the "+ Add" button on the clasp-upgrade card\n'
     'Then the recommended clasp is added to the cart'),

    (meta("Strap_And_Clasp_Purchase_Together", "A strap and its recommended clasp can be purchased together",
          "User on a strap PDP", "Verify a strap and its recommended clasp can be purchased together",
          STR, "Not Automated", "bdd"),
     'Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"\n'
     'When the user clicks the "Add to cart" button on the accessory PDP\n'
     'And the user clicks the "+ Add" button on the clasp-upgrade card\n'
     'Then the cart drawer contains both the strap and the recommended clasp'),

    # ---------- TDD (plaintext): AccessoryPdpSmokeTest ----------
    (meta("TDD_WatchCase_PDP_Purchasable", "TDD smoke: Watch Case PDP is purchasable",
          "Production storefront reachable", "watchCasePdpIsPurchasable()",
          ["Smoke", "Accessories", "TDD"], "Automated", "plain"),
     "1. Open the Premium Watch Case PDP via the PremiumWatchCaseBlack page object.\n"
     "2. Unlock the staging gate if present.\n"
     "3. Assert the product title is displayed.\n"
     "4. Assert the image gallery is displayed.\n"
     "5. Assert the 'Add to cart' button is displayed AND enabled.\n"
     "6. Assert the wishlist button is displayed."),

    (meta("TDD_ButterflyClasp_PDP_Purchasable", "TDD smoke: Butterfly Clasp PDP is purchasable + switcher",
          "Production storefront reachable", "butterflyClaspPdpIsPurchasable()",
          ["Smoke", "Accessories", "TDD"], "Automated", "plain"),
     "1. Open the Butterfly Clasp Gold PDP via the ButterflyClaspGold page object.\n"
     "2. Assert the purchasable core (title, gallery, enabled Add to cart, wishlist).\n"
     "3. Assert the color card-button variant switcher is displayed."),

    (meta("TDD_SingleDeploymentClasp_PDP_Purchasable", "TDD smoke: Single Deployment Clasp PDP is purchasable + switcher",
          "Production storefront reachable", "singleDeploymentClaspPdpIsPurchasable()",
          ["Smoke", "Accessories", "TDD"], "Automated", "plain"),
     "1. Open the Rose Gold 20mm SD Clasp PDP via SingleDeploymentClaspRoseGold20mm.\n"
     "2. Assert the purchasable core.\n"
     "3. Assert the color/size card-button variant switcher is displayed."),

    (meta("TDD_RubberStrap_Configurator", "TDD smoke: Rubber Strap PDP has the configurator",
          "Production storefront reachable", "rubberStrapPdpHasConfigurator()",
          ["Smoke", "Accessories", "TDD"], "Automated", "plain"),
     "1. Open the FKM Rubber Strap Black PDP via FKMRubberStrapBlack.\n"
     "2. Assert the purchasable core.\n"
     "3. Assert the dial-try carousel is displayed.\n"
     "4. Assert the clasp-upgrade cross-sell card is displayed."),

    (meta("TDD_LeatherStrap_Configurator", "TDD smoke: Leather Strap PDP has the configurator",
          "Production storefront reachable", "leatherStrapPdpHasConfigurator()",
          ["Smoke", "Accessories", "TDD"], "Automated", "plain"),
     "1. Open the Ostrich Leather Strap Black PDP via OstrichLeatherStrapBlack.\n"
     "2. Assert the purchasable core.\n"
     "3. Assert the dial-try carousel is displayed.\n"
     "4. Assert the clasp-upgrade cross-sell card is displayed."),
]


def main():
    token = os.environ.get("ZEPHYR_TOKEN")
    if not token:
        print("ZEPHYR_TOKEN env var not set — aborting (no push).", file=sys.stderr)
        sys.exit(2)

    # Optional retry filter: ONLY="Name1,Name2" pushes just those (avoids re-creating already-made cases).
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
