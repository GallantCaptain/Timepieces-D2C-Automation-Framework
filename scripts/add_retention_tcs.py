import urllib.request
import json
import sys

def create_testcase(token, payload):
    url = "https://eu.api.zephyrscale.smartbear.com/v2/testcases"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
    req = urllib.request.Request(
        url,
        data=json.dumps(payload).encode('utf-8'),
        headers=headers,
        method='POST'
    )
    try:
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read().decode('utf-8'))
            return data.get("key")
    except urllib.error.HTTPError as e:
        print(f"HTTP Error creating testcase {payload.get('name')}: {e.code}", file=sys.stderr)
        try:
            print(e.read().decode('utf-8'), file=sys.stderr)
        except Exception:
            pass
        return None
    except Exception as e:
        print(f"Error creating testcase {payload.get('name')}: {e}", file=sys.stderr)
        return None

def add_testscript(token, key, bdd_text):
    url = f"https://eu.api.zephyrscale.smartbear.com/v2/testcases/{key}/testscript"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
    payload = {
        "type": "bdd",
        "text": bdd_text
    }
    req = urllib.request.Request(
        url,
        data=json.dumps(payload).encode('utf-8'),
        headers=headers,
        method='POST'
    )
    try:
        with urllib.request.urlopen(req) as response:
            return True
    except urllib.error.HTTPError as e:
        print(f"HTTP Error adding script to {key}: {e.code}", file=sys.stderr)
        try:
            print(e.read().decode('utf-8'), file=sys.stderr)
        except Exception:
            pass
        return False
    except Exception as e:
        print(f"Error adding script to {key}: {e}", file=sys.stderr)
        return False

def main():
    token = "***REMOVED-ZEPHYR-TOKEN-SEE-ISSUE-013***"
    
    testcases_to_add = [
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Wishlist_Guest_Add_Product",
                "objective": "Verify guest user can add product to wishlist from PDP",
                "precondition": "User has bypassed staging gate and is on a valid PDP",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify guest user can add product to wishlist",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Account"]
                }
            },
            "bdd_text": "Given the user bypasses the staging password gate\nAnd the user is a guest customer\nWhen the user navigates to the Product Detail Page of \"manifesta-black-onyx\"\nAnd clicks the \"Wishlist\" button\nThen the product \"manifesta-black-onyx\" is added to the user's wishlist\nWhen the user navigates to the Wishlist page\nThen the wishlist page displays the product \"manifesta-black-onyx\""
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Wishlist_Guest_Remove_Product",
                "objective": "Verify guest user can remove product from wishlist page",
                "precondition": "Product is added to guest wishlist and user is on wishlist page",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify guest user can remove product from wishlist",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Account"]
                }
            },
            "bdd_text": "Given the user is on the Wishlist page\nAnd the wishlist displays the product \"manifesta-black-onyx\"\nWhen the user clicks the remove button for \"manifesta-black-onyx\" on the wishlist page\nThen the product \"manifesta-black-onyx\" is deleted from the wishlist\nAnd the wishlist page displays \"Your wishlist is currently empty\""
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Wishlist_Guest_To_Member_Sync",
                "objective": "Verify guest wishlist items successfully sync to account on customer login",
                "precondition": "Product is added to guest wishlist and user is on login flow",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify guest wishlist items sync to account on login",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Account"]
                }
            },
            "bdd_text": "Given the user bypasses the staging password gate\nAnd the user is a guest customer\nAnd the user has added \"manifesta-black-onyx\" to their wishlist\nWhen the user logs in with a valid account\nAnd navigates to the Wishlist page\nThen the wishlist page displays the product \"manifesta-black-onyx\" synced under the account"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Account_Order_History_Viewing",
                "objective": "Verify authenticated customer can view past order history",
                "precondition": "User is logged in with a valid account",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify authenticated customer can view order history",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Account"]
                }
            },
            "bdd_text": "Given the user is logged in with a valid account\nWhen the user navigates to the Track Orders account section\nThen the order history table displays all past orders with date, total price, and status"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Account_Order_Status_Tracking",
                "objective": "Verify order tracking shipping status and checkpoints detail",
                "precondition": "User is on Track Orders account section",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify order tracking shipping status details",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Account"]
                }
            },
            "bdd_text": "Given the user is on the Track Orders account section\nWhen the user clicks on a past order ID \"ORD-98765\"\nThen the order detail screen displays the carrier shipping tracking link\nAnd the delivery status shows the current transit checkpoints"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Account_Settings_Address_Modification",
                "objective": "Verify customer can add, edit, or delete shipping address in address book",
                "precondition": "User is logged in with a valid account",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify customer can edit saved shipping address",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Account"]
                }
            },
            "bdd_text": "Given the user is logged in with a valid account\nWhen the user navigates to the Account Address Book settings\nAnd clicks the \"Edit Address\" button for the default address\nAnd updates the street address field to \"456 Luxury Lane\"\nAnd clicks the save address button\nThen the default address displays the updated street \"456 Luxury Lane\""
        }
    ]
    
    for tc in testcases_to_add:
        name = tc["metadata"]["name"]
        print(f"Creating testcase: {name}...")
        key = create_testcase(token, tc["metadata"])
        if key:
            print(f"Created: {key}. Adding steps...")
            success = add_testscript(token, key, tc["bdd_text"])
            if success:
                print(f"Steps added successfully to {key}!")
            else:
                print(f"Failed to add steps to {key}.")
        else:
            print(f"Failed to create testcase {name}.")

if __name__ == '__main__':
    main()
