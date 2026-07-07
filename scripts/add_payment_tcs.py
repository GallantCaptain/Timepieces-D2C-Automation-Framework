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
                "name": "Payment_Card_Validation_Format",
                "objective": "Verify credit card format validation errors on billing payment form",
                "precondition": "User has filled out all mandatory delivery information and navigated to the payment section",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Negative",
                    "Scenario Outline ": "Verify credit card format validation errors",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Payment"]
                }
            },
            "bdd_text": "When the user enters invalid card credentials:\n  | Card Number | 4111 1111      |\n  | Expiry Date | 13/29          |\n  | CVV         | 12             |\nThen the credit card form displays inline errors for number, expiry, and CVV formats"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Payment_Declined_Insufficient_Funds",
                "objective": "Verify error when payment is declined due to insufficient funds",
                "precondition": "User has filled out all mandatory delivery information and navigated to the payment section",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Negative",
                    "Scenario Outline ": "Verify error on card declined due to insufficient funds",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Payment"]
                }
            },
            "bdd_text": "When the user enters card credentials for insufficient funds \"4000 0000 0000 0002\"\nAnd clicks the place order button\nThen the payment system displays an error \"Your card has insufficient funds\"\nAnd the order is not placed"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Payment_Declined_Expired_Card",
                "objective": "Verify error when payment is declined due to expired card",
                "precondition": "User has filled out all mandatory delivery information and navigated to the payment section",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Negative",
                    "Scenario Outline ": "Verify error on card declined due to expired card",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Payment"]
                }
            },
            "bdd_text": "When the user enters card credentials for an expired card \"4000 0000 0000 0003\"\nAnd clicks the place order button\nThen the payment system displays an error \"Your card is expired\"\nAnd the order is not placed"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Payment_Declined_Incorrect_CVV",
                "objective": "Verify error when payment is declined due to incorrect security code",
                "precondition": "User has filled out all mandatory delivery information and navigated to the payment section",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Negative",
                    "Scenario Outline ": "Verify error on card declined due to incorrect security code",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Payment"]
                }
            },
            "bdd_text": "When the user enters card credentials for incorrect CVV \"4000 0000 0000 0004\"\nAnd clicks the place order button\nThen the payment system displays an error \"Your card's security code is incorrect\"\nAnd the order is not placed"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Payment_Secure_Token_Transmission",
                "objective": "Verify secure token transmission during card processing",
                "precondition": "User has filled out all mandatory delivery information and navigated to the payment section",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify secure token transmission during card processing",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Security",
                    "tags": ["Smoke", "Payment"]
                }
            },
            "bdd_text": "When the user submits valid card credentials\nThen the checkout submission transmits a secure one-time payment token\nAnd does not send raw credit card numbers directly to storefront APIs"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Payment_Redirection_Success_Page",
                "objective": "Verify successful payment routes to order confirmation page",
                "precondition": "User has filled out all mandatory delivery information and navigated to the payment section",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify successful payment routes to order confirmation page",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Payment"]
                }
            },
            "bdd_text": "When the user enters a valid test card number \"4000 0000 0000 0001\"\nAnd clicks the place order button\nThen the user is redirected to the Order Confirmation success page\nAnd the page displays a unique order ID and purchase summary"
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
