# Razorpay Checkout Modal — Domestic Flow (INR)

The domestic Razorpay checkout modal is launched for Indian users (INR region) when they perform a "Slide to Pay" action from the cart drawer.

## 0. Iframe & Top-Document Integration

- **Overlay container (top-document):** `div.razorpay-container`
  - Style/Attributes: `z-index: 2147483647; position: fixed; top: 0px; left: 0px; height: 100%; width: 100%;`
- **Backdrop (top-document):** `div.razorpay-backdrop`
- **Iframe:** `iframe.razorpay-checkout-frame`
  - Attributes: `allowtransparency="true" frameborder="0" width="100%" height="100%" src="https://api.razorpay.com/..."`
  - Note: The iframe is cross-origin, meaning elements inside it must be accessed by switching context or via the accessibility tree/CDP.

---

## 1. Overview Panel (Left Panel Chrome)

This section remains visible on the left side of the modal throughout all checkout steps.

- **Checkout container:** `div[data-testid="checkout-v2-container"]` (root wrapper inside the iframe)
- **Merchant Logo:** `img[data-testid="merchnatLogoImage"]` (note the spelling mismatch in the testid!)
- **Merchant Name:** `div[data-testid="merchantName"]` (contains text: `Timepieces`)
- **Order Summary Button:** `button[data-testid="order-summary-widget-multiple"]` (collapsible summary trigger)
- **Order Amount:** `div[data-testid="order-amount"]` (contains rolling character flips for the live currency value)
- **Order Amount Fallback:** `div[data-testid="order-amount-fallback"]` (fallback display of the total amount, e.g., `₹11,000`)
- **GST Number Button:** `button[data-testid="gstin-button"]` (triggers GST input)
- **Order Instructions Button:** `button[data-testid="order-instructions-button"]` (triggers instructions textbox)
- **Offers badge subtext:** `span[data-testid="summary-offer-coupon-subtext"]` (contains offers count, e.g., `7 offers available`)
- **Offers Chevron Button:** `button[data-testid="summary-chevron-button"]` (opens coupon list)
- **Identity Chip:** `button[data-testid="overview-contact-cta"]` (displays currently logged in contact, e.g., `Using as +91 98274 76997`, clickable to edit contact details)
- **Secured by Razorpay Badge:** `div[data-testid="secured-by-razorpay"]`

---

## 2. Step Header (Right Panel Chrome)

The right panel header contains controls that persist across steps.

- **Step Title:** `span[data-testid="title"]` (displays current step, e.g., `Review Order` or `Payment Options`)
- **Back Button:** `button[data-testid="nav-back"]` (appears when navigable back to a previous step)
- **More Options (three dots):** `button[data-testid="checkout-more-options"]`
- **Close Checkout (X):** `button[data-testid="checkout-close"]` (triggers exit confirmation popup)

---

## 3. Review Order Step (Step 1)

This is the initial screen showing the shipping address and shipping options.

- **Address card container:** `div[data-testid="address-block"]` (displays selected address, clickable to switch/add addresses)
- **Address card elements:**
  - Name/Phone: `span[data-clarity-mask="True"]` (contains recipient name, e.g., `Suraj Kumar`)
  - Address Lines: `p[data-clarity-mask="True"]` (contains address lines, e.g., `231, 1212, Nicobar...`)
- **Delivery options card:** `div[data-testid="delivery-card"]`
- **Delivery options list:** `div[data-testid="delivery-options-list"]`
- **Delivery options listitem:** `div[data-testid="delivery-options-listitem"]` (contains standard shipping details)
- **Proceed/Continue Button:** `button[data-testid="proceed-to-pay"]` (text: `Continue`, routes to Payment Options)

---

## 4. Address Switcher/Selector (Step 2)

Opened by clicking the "Add/Change" trigger inside the Address block on the Review Order screen.

- **Address List Title:** `h2` containing text: `Delivery address`
- **Add New Address Button:** `button[name="add_new_address_cta"]` (contains name, no direct data-testid)
- **Saved Address Item:** `div[data-testid="saved-address-listitem"]`
- **Saved Address Detail Block:** `div[data-testid="address-block"]`
- **Edit Address Button:** `button[data-testid="edit-address-btn"]` (opens edit address form)
- **Billing same as delivery checkbox:** `input#billing-same-as-shipping[name="billing_same_as_shipping"]`
- **Continue selected address button:** `button[data-testid="continue-selected-address"]` (saves selected address and returns to Review Order)

---

## 5. Contact Details Popup

Opened by clicking the identity chip or logging/changing phone number.

- **Popup Title:** `h3` containing text: `Contact details`
- **Close Button (X):** `button.absolute.top-0` (top right, no testid)
- **Country Code Dropdown:** `button[data-test-id="country-code-selector"]` (note hyphen in `data-test-id`)
- **Mobile Number Input:** `input[data-testid="contactNumber"]`
- **Email Address Input:** `input[data-testid="email"]`
- **Continue Button:** `button[validateform="true"]` (no testid, triggers validation and updates contact)

---

## 6. Payment Options Step (Step 3)

This screen lists all available payment rails and specific instruments.

### Left Methods Rail
- **Navigation sidebar:** `div[data-testid="nav-sidebar"]`
- **Recommended option:** `div[data-testid="recommended"]` (contains span `data-testid="Recommended"`)
- **UPI option:** `div[data-testid="upi"]` (contains span `data-testid="UPI"` and offer badge `data-testid="inline-offer-text"`)
- **Cards option:** `div[data-testid="card"]` (contains span `data-testid="Cards"` and offer badge `data-testid="inline-offer-text"`)
- **EMI option:** `div[data-testid="emi"]` (contains span `data-testid="EMI"` and offer badge `data-testid="inline-offer-text"`)
- **Netbanking option:** `div[data-testid="netbanking"]` (contains span `data-testid="Netbanking"`)
- **Wallet option:** `div[data-testid="wallet"]` (contains span `data-testid="Wallet"`)

### Right Area Panels
- **Offers Panel:** `div[data-testid="offer-container"]`
  - Offer cards: `div[data-testid^="offer-card-offer_"]` (e.g. `offer-card-offer_SzW6yqtFpa8hu8`)
  - View all offers button: `button[data-testid="view-all-offers"]`
- **UPI QR block:** `div[data-testid="upi-qr-container"]`
  - Show QR button: `button:has(span[name="generateQR"])` or `button[type="submit"]`
  - UPI QR offer: `div[data-testid="inline-offer-text"]` (inside upi-qr-container)
- **Recommended saved cards:**
  - Saved card row: `div[data-testid="card"]` (contains span `data-testid="SBI Credit Card • 3019"`)
  - Saved card brand logo: `img[src*="SBIN.gif"]`

---

## 7. Exit Confirmation Dialog

Triggers when clicking the close button on any step.

- **Exit Dialog Title:** `h3` containing text: `Are you sure you want to exit?`
- **Continue shopping button:** `button[data-testid="confirm-negative"]` (closes exit popup, stays in checkout)
- **Yes, exit button:** `button[data-testid="confirm-positive"]` (closes Razorpay checkout modal completely)
