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
                "name": "Checkout_Form_Mandatory_Fields",
                "objective": "Verify validation errors for mandatory fields left blank on checkout delivery form",
                "precondition": "User has at least one active product in the shopping bag and has initiated checkout",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Negative",
                    "Scenario Outline ": "Verify validation errors for mandatory fields left blank",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Checkout"]
                }
            },
            "bdd_text": "When the user leaves all delivery details blank\nAnd clicks the \"Proceed to Payment\" button\nThen the checkout form displays mandatory error messages under:\n  | First Name     |\n  | Last Name      |\n  | Phone          |\n  | Email          |\n  | Street Address |\n  | City           |\n  | Pin code       |\nAnd a global alert message \"Please fill in all required fields\" is displayed"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Checkout_Email_Format_Validation",
                "objective": "Verify email address format validation on checkout delivery form",
                "precondition": "User has at least one active product in the shopping bag and has initiated checkout",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Negative",
                    "Scenario Outline ": "Verify email address format validation",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Checkout"]
                }
            },
            "bdd_text": "When the user enters an invalid email \"invalid-email-format\"\nAnd fills out all other mandatory delivery details\nAnd clicks the \"Proceed to Payment\" button\nThen the email field error message \"Please enter a valid email address\" is displayed"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Checkout_Pincode_Numeric_Validation",
                "objective": "Verify pincode numeric validation on checkout delivery form",
                "precondition": "User has at least one active product in the shopping bag and has initiated checkout",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Negative",
                    "Scenario Outline ": "Verify pincode numeric validation",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Checkout"]
                }
            },
            "bdd_text": "When the user enters an invalid pincode \"ABC-123\"\nAnd fills out all other mandatory delivery details\nAnd clicks the \"Proceed to Payment\" button\nThen the pincode field error message \"Please enter a valid postal code\" is displayed"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Checkout_Billing_Address_Toggle",
                "objective": "Verify billing address form expansion when same as delivery is unchecked",
                "precondition": "User has at least one active product in the shopping bag and has initiated checkout",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify billing address form expansion",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Checkout"]
                }
            },
            "bdd_text": "Given the billing address same as delivery checkbox is checked\nWhen the user unchecks the billing address same as delivery checkbox\nThen the separate Billing Address form section expands\nAnd the billing form displays mandatory input fields for name, address, and phone"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Checkout_Promo_Code_Invalid",
                "objective": "Verify error when applying invalid promo code",
                "precondition": "User has at least one active product in the shopping bag and has initiated checkout",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Negative",
                    "Scenario Outline ": "Verify error when applying invalid promo code",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Checkout"]
                }
            },
            "bdd_text": "When the user enters a promo code \"INVALIDCODE123\" in the discount field\nAnd clicks the apply promo code button\nThen the promo field displays an error message \"Discount code is invalid\"\nAnd the subtotal and order total pricing structure remains unchanged"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Checkout_Promo_Code_Valid_Discount",
                "objective": "Verify dynamic total calculation with valid promo code",
                "precondition": "User has at least one active product in the shopping bag and has initiated checkout",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify dynamic total calculation with valid promo code",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Checkout"]
                }
            },
            "bdd_text": "Given a valid promo code \"SUMMER10\" provides a 10% discount\nWhen the user enters a promo code \"SUMMER10\" in the discount field\nAnd clicks the apply promo code button\nThen the discount is successfully applied to the order summary\nAnd the order total is dynamically updated to reflect the 10% reduction"
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
