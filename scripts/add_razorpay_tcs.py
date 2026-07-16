"""
Push the Razorpay / international-payment test cases to Zephyr Scale (project QA).

Automated BDD cases come from
  src/test/resources/features/05_payment_integration/International_Payment_Rules.feature
Manual cases cover the rules that lack a DOM oracle (the 1.5x multiplier needs
the live fx rate; EUR/AED symbols need region-eligible sessions; the guest
magic-link OTP needs a real SMS).

NO case ever completes a payment (Razorpay test cards, Rs.1, test customers
only — per RazorpayModalComponent's payment-safety warning).

Token: read from the ZEPHYR_TOKEN env var (NOT hardcoded here), e.g.:

  ZEPHYR_TOKEN=$(grep -oP 'token = "\\K[^"]+' scripts/add_cart_tcs.py) \\
    python scripts/add_razorpay_tcs.py

Retry filter: ONLY="Name1,Name2" pushes just those cases.
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
PRE = "Cart seeded with >=1 item; storefront region switched via the footer selector"

TESTCASES = [
    # ---------- Automated BDD: International Payment Rules ----------
    (meta("Intl_SlideToPay_Routes_To_Checkout", "International Slide to Pay routes to /checkout (not the in-page modal)",
          PRE + " (US)", "Verify international Slide to Pay routes to the delivery information page",
          BDD, "Not Automated", "bdd"),
     'Given the user switches the storefront region to "US" (cart seeded)\n'
     'When the user slides the transactional checkout slide trigger element\n'
     'Then the user is routed to the delivery information checkout page'),

    (meta("Intl_Unsupported_Country_Blocked", "An unsupported country is blocked at Proceed to Payment",
          PRE + " (US); unsupported country selected on the form (e.g. KP)",
          "Verify an unsupported country is blocked at Proceed to Payment",
          BDD, "Not Automated", "bdd"),
     'When the user slides the transactional checkout slide trigger element\n'
     'Then the user is routed to the delivery information checkout page\n'
     'When the user fills the delivery information with the country "KP"\n'
     'And the user proceeds to payment from the checkout page\n'
     'Then the unsupported-country alert is displayed\n'
     '# "Country/Region in the shipping address isn\'t supported"'),

    (meta("Intl_Modal_Cards_And_ApplePay_Only", "The international Razorpay modal offers only cards + Apple Pay",
          PRE + " (US); valid US delivery details", "Verify the international Razorpay modal offers only cards and Apple Pay",
          BDD, "Not Automated", "bdd"),
     'When the user slides the transactional checkout slide trigger element\n'
     'Then the user is routed to the delivery information checkout page\n'
     'When the user fills the delivery information with the country "US"\n'
     'And the user proceeds to payment from the checkout page\n'
     'Then the Razorpay payment options offer only cards and Apple Pay\n'
     '# rail = Recommended + Cards ONLY; UPI/EMI/Netbanking/Wallet absent;\n'
     '# Cards pane offers Saved Cards + Apple Pay + Add a new card\n'
     'When the user exits the Razorpay modal via the exit confirmation'),

    # ---------- Manual: rules without a DOM oracle ----------
    (meta("MANUAL_Intl_Price_Multiplier", "MANUAL: international prices are 1.5x the domestic price (all products)",
          "Two sessions (or region toggling): domestic INR + an international region; live fx rate reference",
          "Verify international prices are 1.5x domestic across products",
          BDD, "Not Automated", "plain"),
     "1. Note the INR price of 2-3 products (timepiece + accessories) in the domestic region.\n"
     "2. Switch the footer region to US (USD).\n"
     "3. For each product: converted price / fx rate should equal 1.5x the INR price\n"
     "   (e.g. Rs.11,000 cart -> $171.27 at the day's rate).\n"
     "4. Spot-check one EUR country and UAE (AED) the same way."),

    (meta("MANUAL_Currency_By_Region", "MANUAL: currency follows the region (EUR Europe / AED UAE / USD rest)",
          "Footer region selector available",
          "Verify the display currency per selected region",
          BDD, "Not Automated", "plain"),
     "1. Switch region to Germany -> all prices render in EUR (euro symbol).\n"
     "2. Switch region to United Arab Emirates -> prices render in AED.\n"
     "3. Switch region to United States (and one non-EU/non-UAE country) -> USD.\n"
     "4. Switch back to India -> INR restored."),

    (meta("MANUAL_Domestic_MagicLink_OTP", "MANUAL: guest magic-link OTP surfaces saved addresses (domestic)",
          "Domestic region (INR); cart seeded; a phone number with Razorpay magic-link history; real SMS access",
          "Verify the guest magic-link OTP flow in the domestic Razorpay modal",
          BDD, "Not Automated", "plain"),
     "1. As a guest, Slide to Pay -> the Razorpay Magic Checkout opens.\n"
     "2. Enter mobile + email on the Contact surface -> the magic-link OTP prompt\n"
     "   appears ('N addresses found', 4 boxes, Skip OTP, Resend timer).\n"
     "3. Enter the real SMS OTP -> saved addresses (Home/Office) surface with\n"
     "   Standard FREE shipping.\n"
     "4. Alternative path: Skip OTP -> continue without the saved-address fill.\n"
     "5. Do NOT complete any payment."),

    (meta("MANUAL_Domestic_Modal_Close_Semantics", "MANUAL: the modal closes only via X + exit confirmation",
          "Domestic region; cart seeded; modal open",
          "Verify the Razorpay modal close semantics",
          BDD, "Not Automated", "plain"),
     "1. With the modal open, press esc -> the modal must NOT close.\n"
     "2. Click the X (checkout-close) -> the exit-confirmation dialog appears\n"
     "   ('Are you sure you want to exit?').\n"
     "3. Choose the negative option -> checkout continues (modal stays).\n"
     "4. Click X again and choose 'Yes, exit' -> the modal closes completely."),
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
