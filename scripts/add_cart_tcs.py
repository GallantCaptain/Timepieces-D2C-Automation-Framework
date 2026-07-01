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
                "name": "Cart_Drawer_Open_Close",
                "objective": "Verify slide-out cart drawer open and close transitions",
                "precondition": "User has bypassed the staging gate and is on the homepage",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify slide-out cart drawer open and close transitions",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Cart"]
                }
            },
            "bdd_text": "When the user clicks the cart icon on the header\nThen the slide-out cart drawer is displayed\nWhen the user clicks the close button in the cart drawer\nThen the cart drawer is hidden"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Cart_Add_Product_Success",
                "objective": "Verify successful product addition from PDP to cart drawer",
                "precondition": "User has bypassed the staging gate",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify successful product addition from PDP to cart",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Cart"]
                }
            },
            "bdd_text": "Given the user navigates to the Product Detail Page of \"manifesta-black-onyx\"\nWhen the user clicks the \"Add to Cart\" button\nThen the slide-out cart drawer is displayed\nAnd the cart badge count is updated to \"1\"\nAnd the cart drawer displays the product \"manifesta-black-onyx\" with its correct title, configuration, and price"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Cart_Quantity_Modification",
                "objective": "Verify quantity modification dynamically updates subtotal",
                "precondition": "Product is added to cart and cart drawer is displayed",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify quantity modification dynamically updates subtotal",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Cart"]
                }
            },
            "bdd_text": "Given the user has added \"manifesta-black-onyx\" to the cart\nAnd the slide-out cart drawer is displayed\nWhen the user increases the quantity of \"manifesta-black-onyx\" to \"2\"\nThen the item quantity shows \"2\"\nAnd the cart subtotal is updated to match twice the item unit price\nWhen the user decreases the quantity of \"manifesta-black-onyx\" to \"1\"\nThen the item quantity shows \"1\"\nAnd the cart subtotal is updated to match the item unit price"
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Cart_Item_Removal",
                "objective": "Verify product removal from cart drawer updates quantities and badge",
                "precondition": "Product is added to cart and cart drawer is displayed",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify product removal from cart drawer",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Cart"]
                }
            },
            "bdd_text": "Given the user has added \"manifesta-black-onyx\" to the cart\nAnd the slide-out cart drawer is displayed\nWhen the user clicks the remove icon for \"manifesta-black-onyx\"\nThen the item \"manifesta-black-onyx\" is removed from the cart drawer\nAnd the cart shows \"Your cart is currently empty\"\nAnd the cart badge count on the header updates to \"0\""
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Cart_Free_Shipping_Progress",
                "objective": "Verify free shipping progress bar updates dynamically based on threshold",
                "precondition": "User has bypassed the staging gate",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify free shipping progress bar updates dynamically",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Cart"]
                }
            },
            "bdd_text": "Given the free shipping threshold is \"$150.00\"\nAnd the user has a cart subtotal of \"$0.00\"\nThen the free shipping progress bar shows \"$150.00 away from free shipping\"\nWhen the user adds a product priced at \"$100.00\" to the cart\nThen the progress bar updates to show \"$50.00 away from free shipping\"\nWhen the user adds a product priced at \"$60.00\" to the cart\nThen the progress bar displays \"You have qualified for Free Shipping!\""
        },
        {
            "metadata": {
                "projectKey": "QA",
                "name": "Cart_Persistence_Across_Session",
                "objective": "Verify cart items persist across page refreshes",
                "precondition": "Product is added to cart",
                "priority": "Normal",
                "status": "Draft",
                "customFields": {
                    "Test State": "Positive",
                    "Scenario Outline ": "Verify cart items persist across page refreshes",
                    "Automation Status": "Not Automated",
                    "Test Case Type": "Functional",
                    "tags": ["Smoke", "Cart"]
                }
            },
            "bdd_text": "Given the user has added \"manifesta-black-onyx\" to the cart\nWhen the user reloads the storefront page\nAnd clicks the cart icon on the header\nThen the slide-out cart drawer displays the product \"manifesta-black-onyx\" with its correct quantity"
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
