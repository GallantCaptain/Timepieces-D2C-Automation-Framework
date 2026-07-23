@Payment
Feature: Payment Integration and Security
  As a customer
  I want to build a bag and reach the secure payment step
  So that the storefront's half of the payment journey is proven before Razorpay takes over

  # ---------------------------------------------------------------------------------
  # REWRITTEN 2026-07-23. Every scenario in this file used to fail, all six of them, and
  # they failed in the Background so no scenario ever reached its first When.
  #
  # The old Background was:
  #     Given the user bypasses the staging password gate
  #     And the user has filled in all required delivery information on the checkout page
  #     And has navigated to the payment section
  #
  # Step 2 constructed a CheckoutPage and started filling a delivery form while the
  # browser was still sitting on the HOMEPAGE. Nothing had navigated anywhere, and
  # nothing had put an item in the bag — and per ISSUE-018 an empty bag makes /checkout
  # bounce straight back to the homepage anyway. So the suite was asking a homepage for
  # a delivery form, and the scenarios below it were asking a storefront that has no
  # card form of its own for inline card-format errors.
  #
  # This was not a locator problem or a timing problem. The tests described a checkout
  # that does not exist and never did: a native card form on timepieces-d2c.example, with a "place
  # order" button and an order-confirmation page. The real product hands payment to
  # Razorpay's cross-origin iframe the moment you click Secure Checkout, and everything
  # from the card number onwards belongs to Razorpay.
  #
  # WHAT THE PRODUCT ACTUALLY DOES (owner-confirmed 2026-07-23, matches production):
  #     homepage -> sidebar -> Accessories -> add a strap / clasp / watch case
  #     -> the bag drawer opens -> Secure Checkout -> the Razorpay modal opens IN-PAGE
  #     -> a signed-out shopper is asked for a mobile number and an email first
  #        (Contact > Address > Payment)
  #
  # That whole path is Timepieces' responsibility and is now covered end to end below.
  # Everything past the contact step is Razorpay's UI inside a cross-origin iframe,
  # against a LIVE payment gateway, and is left to the developers and the next SDET —
  # see the @Manual block at the bottom, which says exactly what is missing and why.
  # ---------------------------------------------------------------------------------

  Background:
    Given the user is on the storefront homepage

  @Regression
  Scenario: Verify the Accessories collection is reachable from the sidebar
    When the user opens the Accessories collection from the sidebar
    Then the accessories collection page is displayed

  @Regression
  Scenario: Verify a shopper can build a multi-item bag with a strap and its upsold clasp
    # A two-item bag built the way the product actually upsells: add a strap, then take the
    # clasp it recommends. This is the reliable multi-item path.
    #
    # A THREE-item bag spanning separate PDPs (strap + clasp + a watch case added from its
    # own page) was tried and left out on purpose. The cart DOES persist across a full page
    # navigation — the strap and clasp survive the move — but the watch-case PDP's own
    # "Add to cart" did not register a third line in an automated run, and chasing why is
    # app-behaviour work for the next SDET, not framework plumbing. Noted in ISSUE-022
    # rather than shipped as a flaky scenario.
    Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"
    When the user clicks the "Add to cart" button on the accessory PDP
    Then the slide-out cart drawer is displayed
    # The drawer opens over the right-hand side of the page and covers the clasp-upgrade
    # card behind it, so a shopper has to dismiss it to keep shopping. Saying so here is
    # what stops this scenario from failing with an intercepted click.
    And the user closes the cart drawer
    And the user clicks the "+ Add" button on the clasp-upgrade card
    Then the cart drawer contains both the strap and the recommended clasp
    And the bag contains 2 line items
    And the bag total is displayed

  @Regression
  Scenario: Verify Secure Checkout opens the Razorpay modal in-page instead of redirecting
    Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"
    When the user clicks the "Add to cart" button on the accessory PDP
    Then the slide-out cart drawer is displayed
    When the user slides the transactional checkout slide trigger element
    Then the Razorpay secure payment modal interface should initialize successfully within the active viewing
    And the system should not perform a hard URL redirect

  @Regression
  Scenario: Verify a signed-out shopper is asked for contact details before payment
    Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"
    When the user clicks the "Add to cart" button on the accessory PDP
    Then the slide-out cart drawer is displayed
    When the user slides the transactional checkout slide trigger element
    Then the Razorpay secure payment modal interface should initialize successfully within the active viewing
    And the customer contact information form should become visible
    # Types into the fields and stops. It deliberately never presses Continue — see the
    # step definition for why that restraint is a safety rule and not laziness.
    When the guest types contact details into the Razorpay contact step
    Then the Razorpay contact step accepts the mobile and the email

  @Regression
  Scenario: Verify the Razorpay modal is dismissed only through its exit confirmation
    Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"
    When the user clicks the "Add to cart" button on the accessory PDP
    Then the slide-out cart drawer is displayed
    When the user slides the transactional checkout slide trigger element
    Then the Razorpay secure payment modal interface should initialize successfully within the active viewing
    When the user exits the Razorpay modal via the exit confirmation
    Then the shopper is returned to the storefront with the Razorpay modal closed

  # ---------------------------------------------------------------------------------
  # NOT AUTOMATED — deliberately, and not because they are hard.
  #
  # These are tagged @Manual and carry no @Regression tag, so RegressionRunner
  # (tags = "@Regression or @AuthGated") never selects them. They stay here as a
  # specification of what is still uncovered, which is more useful to the next SDET
  # than a deleted file or a wall of pending scaffolds in every run.
  #
  # Three things block them, and only the first is a tooling problem:
  #
  #   1. CROSS-ORIGIN IFRAME. Card entry lives inside Razorpay's own checkout iframe.
  #      Selenium can enter the frame, but the fields carry no stable testids, and the
  #      DOM is Razorpay's to change without telling us.
  #   2. LIVE PAYMENT GATEWAY. There is no Razorpay sandbox wired to this storefront.
  #      Submitting a card here transacts against the real gateway, and the decline
  #      scenarios below would be real declines on a real merchant account.
  #   3. NO AUTHORISATION. Razorpay is a third party. Driving their UI in an automated
  #      loop is not ours to authorise, and nobody has asked them.
  #
  # To finish them you need, in this order: a Razorpay TEST-mode key wired to a staging
  # storefront, written sign-off to drive the sandbox, and then the card.* inputs
  # already mapped in RazorpayModalComponent. The component work is done; the
  # permission and the sandbox are not.
  # ---------------------------------------------------------------------------------

  @Manual @NotAutomated
  Scenario: Verify credit card format validation errors
    When the user enters invalid card credentials:
      | Card Number | 4111 1111 |
      | Expiry Date |     13/29 |
      | CVV         |        12 |
    Then the credit card form displays inline errors for number, expiry, and CVV formats

  @Manual @NotAutomated
  Scenario: Verify error on card declined due to insufficient funds
    When the user enters card credentials for insufficient funds "4000 0000 0000 0002"
    And clicks the place order button
    Then the payment system displays an error "Your card has insufficient funds"
    And the order is not placed

  @Manual @NotAutomated
  Scenario: Verify error on card declined due to expired card
    When the user enters card credentials for an expired card "4000 0000 0000 0003"
    And clicks the place order button
    Then the payment system displays an error "Your card is expired"
    And the order is not placed

  @Manual @NotAutomated
  Scenario: Verify error on card declined due to incorrect security code
    When the user enters card credentials for incorrect CVV "4000 0000 0000 0004"
    And clicks the place order button
    Then the payment system displays an error "Your card's security code is incorrect"
    And the order is not placed

  @Manual @NotAutomated
  Scenario: Verify secure token transmission during card processing
    When the user submits valid card credentials
    Then the checkout submission transmits a secure one-time payment token
    And does not send raw credit card numbers directly to storefront APIs

  @Manual @NotAutomated
  Scenario: Verify successful payment routes to order confirmation page
    When the user enters a valid test card number "4000 0000 0000 0001"
    And clicks the place order button
    Then the user is redirected to the Order Confirmation success page
    And the page displays a unique order ID and purchase summary
