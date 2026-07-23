"""
Push the Phase-3 (content / F&P / wishlist-adjacent / orders) test cases to
Zephyr Scale (project QA).

BDD cases come from the feature files under
  src/test/resources/features/07_content_brand_pages/*.feature
  src/test/resources/features/06_customer_retention_wishlist/Guest_Order_Tracking.feature
  src/test/resources/features/06_customer_retention_wishlist/Account_Orders_Management.feature
TDD cases come from
  src/test/java/com/timepieces/automation/tdd/ui/Phase3ContentSmokeTest.java

The Account_Orders_Management cases are AUTH-GATED (Cloudflare Turnstile blocks
WebDriver login) — their precondition says so explicitly.

Token: read from the ZEPHYR_TOKEN env var (NOT hardcoded here), e.g.:

  ZEPHYR_TOKEN=$(grep -oP 'token = "\\K[^"]+' scripts/add_cart_tcs.py) \\
    python scripts/add_phase3_tcs.py

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
AUTH_PRE = ("AUTHENTICATED session required — production login is behind a "
            "Cloudflare Turnstile (human-solved) + SMS OTP; run with a "
            "pre-authenticated browser session")

TESTCASES = [
    # ---------- BDD: Legal & Policy Pages ----------
    (meta("Policy_Pages_Shared_Template", "Each policy page renders the shared policy template",
          "Storefront reachable (staging gate bypassed)",
          "Verify each policy page renders the shared policy template",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the "<policy>" policy page\n'
     'Then the policy browser title contains "<title>"\n'
     'And the policy main heading "<heading>" is displayed\n'
     'And the policy prose content is displayed\n'
     'And the policy page offers the "Back to Policies" and "View all policies" navigation\n'
     '# Examples: guarantee-policy / privacy-policy / refund-policy (heading "Refund & Exchange Policy")\n'
     '#           shipping-policy (heading "Timepieces Shipping Policy" — an h2!) / terms-of-service'),

    (meta("Policy_Support_Email", "A policy body offers a support contact email",
          "Storefront reachable", "Verify a policy body offers a support contact email",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the "guarantee-policy" policy page\n'
     'Then the policy body contains a support contact email link'),

    # ---------- BDD: Brand Content Pages ----------
    (meta("TimepiecesWall_Canvas_And_Music", "Timepieces Wall renders the WebGL testimonial wall with music control",
          "Storefront reachable", "Verify the Timepieces Wall renders the WebGL testimonial wall with music control",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Timepieces Wall page\n'
     'Then the wall canvas container is displayed\n'
     'And the background music toggle is displayed\n'
     'When the user toggles the background music\n'
     'Then the background music is playing'),

    (meta("ShareYourMoment_Guest_Gate", "Share Your Moment gates guest submissions behind sign-in",
          "Guest (unauthenticated) session", "Verify Share Your Moment gates guest submissions behind sign-in",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Share Your Moment page\n'
     'Then the "Show your Timepieces moment" heading is displayed\n'
     'And the watch-photo carousel is displayed\n'
     'When the user clicks the gated "Share your moment" button\n'
     'Then the sign-in gate modal is displayed\n'
     'And the gate modal explains that only Timepieces owners can submit\n'
     'When the user closes the gate modal\n'
     'Then the sign-in gate modal is hidden'),

    (meta("Sitemap_Indexes_Storefront", "The Sitemap indexes the storefront",
          "Storefront reachable", "Verify the Sitemap indexes the storefront",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Sitemap page\n'
     'Then the sitemap main heading is displayed\n'
     'And the sitemap has the section "Main Pages"\n'
     'And the sitemap has the section "Collections"\n'
     'And the sitemap has the section "Products"\n'
     'And the sitemap contains links to "/products/" pages\n'
     'And the sitemap contains links to "/accessories/" pages\n'
     'And the sitemap lists at least 40 links'),

    (meta("Updates_Content_Filters", "The Updates page filters news content",
          "Storefront reachable", "Verify the Updates page filters news content",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Updates page\n'
     'Then the "Timepieces Updates" heading is displayed\n'
     'And the content filter "All" is available\n'
     'And the content filter "Drops & Releases" is available\n'
     'And the content filter "Delivery Updates" is available\n'
     'And the content filter "Community" is available\n'
     'And the content filter "Production & Quality" is available\n'
     'When the user selects the content filter "Drops & Releases"\n'
     'Then update cards are displayed'),

    # ---------- BDD: Friends & Partners ----------
    (meta("FnP_Landing_Sections_And_Cards", "F&P landing shows the three sections and profile cards",
          "Storefront reachable", "Verify the landing page shows the three sections and profile cards",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Friends and Partners page\n'
     'Then the "Friends & Partners" heading is displayed\n'
     'And the section tab "Voices" is available\n'
     'And the section tab "Founders & Makers" is available\n'
     'And the section tab "Thought Leaders" is available\n'
     'And partner profile cards are displayed\n'
     'And at least one partner card carries a video badge'),

    (meta("FnP_Profile_Hero_And_Video", "A partner profile shows the standout hero and embedded video",
          "Partner 'kush-maini' has a video + purchased watch", "Verify a partner profile shows the standout hero and embedded video",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the partner profile of "kush-maini"\n'
     'Then the partner profile hero is displayed with the tagline "A Timepieces Man"\n'
     'And the partner profile features an embedded video\n'
     'And the sticky numbered-watch bar is displayed\n'
     'And every watch link on the profile targets a product PDP'),

    (meta("FnP_Profile_SeeMore_Drawer", "A multi-watch partner profile expands the See More drawer",
          "Partner 'vivek-oberoi' owns multiple watches", "Verify a multi-watch partner profile expands the See More drawer",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the partner profile of "vivek-oberoi"\n'
     'When the user expands the "See More" watches drawer\n'
     'Then the watches drawer lists additional purchased watches'),

    (meta("FnP_Profile_Back_Navigation", "A partner profile navigates back to the directory",
          "Storefront reachable", "Verify a partner profile navigates back to the directory",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the partner profile of "kush-maini"\n'
     'When the user navigates back to Friends and Partners\n'
     'Then the "Friends & Partners" heading is displayed'),

    # ---------- BDD: Guest Order Tracking ----------
    (meta("TrackOrder_Entry_Form", "Tracking entry form is displayed with a disabled submit",
          "Guest session", "Verify the tracking entry form is displayed with a disabled submit",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Track Order page\n'
     'Then the "Track your order" heading is displayed\n'
     'And the order id input is displayed\n'
     'And the track order button is disabled while the input is empty'),

    (meta("TrackOrder_Unknown_Id_Error", "An unknown order id shows the order-not-found error",
          "Guest session", "Verify an unknown order id shows the order-not-found error",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Track Order page\n'
     'When the user tracks the order id "INVALID-0000"\n'
     'Then the order-not-found error is displayed'),

    (meta("TrackOrder_DeepLink_AutoSubmit", "The orderId deep link auto-submits the tracking search",
          "Guest session", "Verify the orderId deep link auto-submits the tracking search",
          BDD, "Not Automated", "bdd"),
     'Given the user opens the Track Order page with the order id "INVALID-0000"\n'
     'Then the order-not-found error is displayed'),

    (meta("TrackOrder_Contact_Support", "Contact support is offered on the tracking page",
          "Guest session", "Verify contact support is offered on the tracking page",
          BDD, "Not Automated", "bdd"),
     'Given the user navigates to the Track Order page\n'
     'Then the tracking contact support link is available'),

    # ---------- BDD: Account Orders Management (AUTH-GATED) ----------
    (meta("Account_Nav_Complete", "AUTH: the account page navigation is complete",
          AUTH_PRE, "Verify the account page navigation is complete",
          BDD, "Not Automated", "bdd"),
     'Given the user is authenticated on the account page\n'
     'Then the account navigation shows Orders, Owned timepieces and Wishlist\n'
     'And the logout button is displayed'),

    (meta("Account_Order_History_Cards", "AUTH: the order history lists the placed orders",
          AUTH_PRE, "Verify the order history lists the placed orders",
          BDD, "Not Automated", "bdd"),
     'Given the user is authenticated on the account page\n'
     'When the user opens the order history\n'
     'Then the orders page heading is displayed\n'
     'And each order card shows its status and links to its details page'),

    (meta("Account_Order_Details_Record", "AUTH: the order details page shows the full order record",
          AUTH_PRE, "Verify the order details page shows the full order record",
          BDD, "Not Automated", "bdd"),
     'Given the user is authenticated on the account page\n'
     'When the user opens the order details of "Timepieces3306"\n'
     'Then the order status and payment status are displayed\n'
     'And the shipping and billing address blocks are displayed'),

    (meta("Account_Order_Support_Sheet", "AUTH: the order-details Help button opens the support request sheet",
          AUTH_PRE, "Verify the order-details Help button opens the support request sheet",
          BDD, "Not Automated", "bdd"),
     'Given the user is authenticated on the account page\n'
     'When the user opens the order details of "Timepieces3306"\n'
     'And the user opens the Help support sheet\n'
     'Then the support request sheet is displayed\n'
     'And the support submit button is disabled until the form is filled\n'
     'When the user closes the support request sheet\n'
     'Then the support request sheet is hidden'),

    # ---------- TDD (plaintext): Phase3ContentSmokeTest ----------
    (meta("TDD_Policy_Shared_Template", "TDD smoke: policy page renders the shared legal template",
          "Production storefront reachable", "policyPageRendersSharedTemplate()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open the Guarantee Policy page via the GuaranteePolicyPage page object.\n"
     "2. Unlock the staging gate if present.\n"
     "3. Assert the main heading is displayed.\n"
     "4. Assert the prose content block is displayed.\n"
     "5. Assert 'Back to Policies' is displayed.\n"
     "6. Assert a contact mailto link is present."),

    (meta("TDD_Sitemap_Link_Index", "TDD smoke: sitemap indexes the storefront",
          "Production storefront reachable", "sitemapIndexesTheStorefront()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open the Sitemap page via SiteMapPage.\n"
     "2. Assert the heading + the Collections/Products sections.\n"
     "3. Assert at least 40 links, including /products/ and /accessories/ links."),

    (meta("TDD_Updates_Filters", "TDD smoke: updates page filters content",
          "Production storefront reachable", "updatesPageFiltersContent()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open the Updates page via UpdatesPage.\n"
     "2. Assert the heading and the 'All' + 'Drops & Releases' filters.\n"
     "3. Assert news cards render; select 'Drops & Releases'; assert cards remain."),

    (meta("TDD_FnP_Directory", "TDD smoke: Friends & Partners directory loads",
          "Production storefront reachable", "friendsAndPartnersDirectoryLoads()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open Friends & Partners via the FriendsAndPartners page object.\n"
     "2. Assert the heading + the 'Founders & Makers' tab.\n"
     "3. Assert profile cards render (count > 0)."),

    (meta("TDD_Guest_Wishlist_Empty", "TDD smoke: guest wishlist shows the empty state",
          "Fresh guest browser session", "guestWishlistShowsEmptyState()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open /wishlist via WishlistPage in a fresh guest session.\n"
     "2. Assert the page heading (data-testid account-page-title) is displayed.\n"
     "3. Assert the 'Your Wishlist is Empty' state is displayed."),

    (meta("TDD_TrackOrder_Unknown_Id", "TDD smoke: track-order rejects an unknown order id",
          "Production storefront reachable", "trackOrderRejectsAnUnknownOrderId()",
          ["Smoke", "TDD"], "Automated", "plain"),
     "1. Open /track-order via TrackOrdersPage.\n"
     "2. Assert the heading and that submit is disabled while empty.\n"
     "3. Track 'INVALID-0000'.\n"
     "4. Assert the 'Order not found' error renders."),
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
