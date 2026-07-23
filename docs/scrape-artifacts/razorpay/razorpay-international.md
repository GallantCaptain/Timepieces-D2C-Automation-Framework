# Razorpay Checkout Modal — International Flow (USD)

The international checkout flow is triggered when the store country is set to United States (or any other USD-supported country) in the main page's footer selector. Doing so changes the product currency to USD. 

---

## 1. Custom International Delivery Info Page (`/checkout`)

When USD is active, performing the "Slide to Pay" action from the cart drawer does not immediately load the Razorpay checkout iframe. Instead, the user is redirected to a custom delivery information page on the host website: `https://www.timepieces-d2c.example/checkout`.

- **Delivery Page Title:** `h1` containing text: `Delivery Information`
- **First Name Input:** `input[data-testid="checkout-delivery-first-name"]`
- **Last Name Input:** `input[data-testid="checkout-delivery-last-name"]`
- **Phone Dropdown Trigger:** `button` (contains country flag and dial code, e.g., `🇺🇸 +1`)
- **Phone Number Input:** `input[data-testid="checkout-delivery-phone"]`
- **Email Input:** `input[data-testid="checkout-delivery-email"]`
- **Country Dropdown Selector:** `select[data-testid="checkout-delivery-country"]` (pre-filled with `US`)
- **State Input:** `input[data-testid="checkout-delivery-state"]`
- **Street Address Input:** `input[data-testid="checkout-delivery-address"]`
- **City Input:** `input[data-testid="checkout-delivery-city"]`
- **Pin code/Zip code Input:** `input[data-testid="checkout-delivery-pincode"]`
- **Billing same as shipping checkbox:** `input[data-testid="checkout-billing-same-as-delivery"]` (visually hidden under `label`)
- **Proceed to Payment Button:** `button[data-testid="checkout-order-summary-desktop-proceed-to-payment"]`

### Unsupported Country Block Check
If an unsupported country (e.g., North Korea `KP` or Cuba `CU`) is selected from the country dropdown, clicking "Proceed to Payment" halts progress and displays the following validation alert at the bottom of the form:
- **Block Alert:** `input: Country/Region in the shipping address isn't supported; input: Country/Region in the billing address isn't supported`

---

## 2. International Razorpay Checkout Modal

Clicking "Proceed to Payment" on the `/checkout` page with a valid international address launches the cross-origin Razorpay checkout iframe modal.

### Overview Panel (Left Panel Chrome)
Unlike the domestic panel which has order items, coupons, and GST additions, the international panel is highly streamlined:
- **Price Summary Label:** `span` with text `Price Summary`
- **Price Summary Value:** `h3` (displays total in USD, e.g. `$171.27`)
- **Identity widget:** `button[data-testid="contact-sidebar-widget"]` (displays currently logged in contact, e.g., `Using as +91 XXXXX XXXXX`)
- **Secured by Razorpay Badge:** `div[data-testid="secured-by-razorpay"]`

### Payment Options Panel (Right Panel Chrome)
The right panel lists payment options and steps.
- **Left Methods Rail:** `div[data-testid="nav-sidebar"]`
  - Contains only two options:
    1. **Recommended:** `div[data-testid="recommended"]` (contains span `data-testid="Recommended"`)
    2. **Cards:** `div[data-testid="card"]` (contains span `data-testid="Cards"`)
  - *Note: UPI, Netbanking, Wallet, and EMI rails are completely absent.*
- **Step Header Controls:**
  - Step Title: `span[data-testid="title"]` (text: `Payment Options`)
  - Sibling More Options button (three dots): `button[data-testid="checkout-more-options"]`
  - Sibling Close Checkout (X) button: `button[data-testid="checkout-close"]`

---

## 3. Cards Selection & Card Input Form

Selecting "Cards" from the left rail loads the Saved Cards and new card forms on the right.

### Saved Cards & Quick Payments
- **Saved Cards section label:** `button` with text: `Saved Cards (1)`
- **Saved Card Item:** `div[data-testid="card"]` (contains span `data-testid="SBI Credit Card • 3019"`)
- **Saved Card Brand Logo:** `img[src*="SBIN.gif"]`
- **Apple Pay Button:** `button` with text: `Apple Pay` (inside Cards list)
- **Add a new card Button:** `button` with text: `Add a new card` (has `uid=38_5` or similar)

### Add a New Card Form
Opened by clicking the "Add a new card" button:
- **Form Heading:** `h3` with text `Add a new card`
- **Card Number Input:** `input[name="card.number"]` (no testid! Selector: `input[name="card.number"]` or `input[aria-label="Card Number"]`)
- **Expiry Input (MM / YY):** `input[name="card.expiry"]` (no testid! Selector: `input[name="card.expiry"]` or `input[aria-label="MM / YY"]`)
- **CVV Input:** `input[name="card.cvv"]` (no testid! Selector: `input[name="card.cvv"]` or `input[aria-label="CVV"]`)
- **Cardholder Name Input:** `input[name="card.name"]` (no testid! Selector: `input[name="card.name"]` or `input[aria-label="Enter name on card"]`, pre-filled with name from checkout form)
- **Save Card Checkbox:** `input[data-testid="save-card-checkbox"]`
- **Continue Button:** `button[data-test-id="add-card-cta"]` (note hyphen in `data-test-id`)

---

## 4. Exit Confirmation Dialog

Triggers when clicking the close button on any step.

- **Exit Dialog Title:** `h3` containing text: `Are you sure you want to exit?`
- **Exit Dialog Subtext:** `div` or `span` with text: `You will be taken back to Timepieces website`
- **Continue to payment button:** `button[data-testid="confirm-negative"]` (closes exit popup, stays in checkout)
- **Yes, exit button:** `button[data-testid="confirm-positive"]` (closes Razorpay checkout modal completely)
