# Antigravity Scrape Brief — Razorpay Modal (TASK-PDP-010 live capture)

**For:** the Antigravity browser agent (drives real Chrome, executes JS).

**Goal:** ONE live capture of the Razorpay checkout so the `VERIFY`-tier text
anchors in `RazorpayModalComponent` can be swapped for Razorpay's REAL
`data-testid`s. Two flows: DOMESTIC (Magic Checkout) and INTERNATIONAL
("Payment Options" mode), plus the currency/blocking spot-checks.

> ## SAFETY RAILS (read first)
> - **NEVER complete a payment.** Selecting method rail entries to reveal their
>   panes is fine; do NOT press any final Pay/amount button, do NOT scan the QR,
>   do NOT submit card fields.
> - **Redact personal values** (names, phone numbers, full addresses, card
>   digits beyond the display mask, avatar/photo URLs) — selectors and
>   structure only.
> - The modal only opens with **≥1 item in the cart** — seed it with a cheap
>   accessory (e.g. `/accessories/strap/fkm-rubber-strap-orange`, Add to cart).

## 0. The IFRAME boundary (capture this exactly)
The modal UI lives inside a Razorpay iframe. Capture from the TOP document:
- The overlay container (`div.razorpay-container`? confirm exact classes/id).
- The iframe element: tag + ALL identifying attributes (class, id, name, src
  origin) — our Selenium component must `switchTo().frame(...)` on it.
- What mounts/unmounts in the top document when the modal opens/closes.
Then run ALL further inspection INSIDE the iframe document.

## 1. DOMESTIC flow (region = India, INR)
Cart (≥1 item) → **Slide to Pay** → modal opens. Capture per area, inside the
iframe, with Razorpay's real `data-testid` wherever one exists:

### 1.1 Shared chrome
- Step rail **Contact / Address / Payment**: entry selectors + the ACTIVE-step
  encoding (class/aria).
- The **close (X)** control; also CONFIRM esc and any cancel affordance do NOT
  close the modal (documented behavior). The back arrow (when does it appear?).
- Left brand panel: order-summary row + amount, "GST Number — Add", "Order
  instructions — Add", "Coupons and offers (N offers)", the "Using as +91…"
  identity chip, the selected-address chip (appears after Address), "Secured by
  Razorpay".

### 1.2 Contact step (guest)
- Heading, the +91 mobile input, email input, "Send me offers…" checkbox,
  Continue button (+ its disabled/enabled encoding).

### 1.3 Magic-link OTP prompt
Use the pilot number (<pilot-phone-redacted>) so the prompt appears. Capture:
- The "N addresses found" banner, "Enter OTP sent to +91…" heading, the 4 OTP
  digit boxes (selectors + auto-advance behavior), **Skip OTP**, "Resend OTP in
  Ns" (+ post-timer state), Continue.
- You (the human) may enter the real OTP to proceed — or use Skip OTP; note
  which path you took and what Skip leads to.

### 1.4 Address step
- A saved-address card's full structure: label (Home/Office), name/phone/lines
  (redact values), the edit pencil, the SELECTED-state encoding, the shipping
  row ("Standard" + FREE badge), Continue.

### 1.5 Payment step (full domestic rail)
- Each method rail entry — **UPI / Cards / EMI / Netbanking / Wallet** — with
  its offers chip ("3 Offers", "Upto 1.5% savings…").
- The UPI pane: QR block ("Show QR"), UPI app icons, offers chip.
- "Available Offers" strip + the "View all" control.
- The "Recommended" saved-card row (e.g. "SBI Credit Card • ####" — mask kept,
  digits redacted).
- Do NOT go deeper than revealing panes (no card entry, no pay).

## 2. INTERNATIONAL flow
Switch region via the footer country selector to **United States** (USD), keep
the same cart. Capture:
- Quick price note: confirm the cart/PDP prices switch to $ at 1.5× domestic
  (e.g. ₹11,000 total → the $ figure shown) — one example is enough.
- **Slide to Pay now routes to `/checkout`** (the Timepieces Delivery Information
  page) instead of opening the modal — confirm the routing.
- Fill the delivery form with obvious TEST data → **Proceed to Payment** → the
  modal opens in compact **"Payment Options"** mode. Capture:
  - The "Payment Options" title, "Saved Cards" section + a saved-card row,
    the **Apple Pay** row, **"Add a new card"** row (all testids).
  - CONFIRM the ABSENCE of UPI / EMI / Netbanking / Wallet anywhere in this
    mode (the two-method-family rule).
  - The left panel differences (Price Summary in $, identity chip).

## 3. Currency + unsupported-country spot-checks (fast)
- **EUR**: switch to a European country (e.g. Germany) — confirm € pricing.
- **AED**: switch to United Arab Emirates — confirm AED pricing.
- **UNSUPPORTED** (e.g. Afghanistan): report HOW purchase is blocked — is the
  country absent from the selector, does Slide to Pay refuse, is there an error
  message? Capture the blocking element/selector + exact message.

## Raw inventory
- FULL `data-testid` dump INSIDE the iframe, grouped per step (Contact / OTP /
  Address / Payment / Payment-Options-intl).
- Top-document inventory (container/iframe + anything Timepieces-side that mounts
  with the modal).
- NO-testid elements + fallback selector used (with tier).

## Output format
Return 2 Markdown files: `razorpay-domestic.md` (§0–1 + inventory) and
`razorpay-international.md` (§2–3 + inventory). Drop them back and I'll swap
the VERIFY locators in `RazorpayModalComponent` for the real testids, wire the
pending `CartCheckoutSteps`/`PaymentSteps` stubs, and author the international
pricing/currency/blocking test cases (TASK-PDP-010).
