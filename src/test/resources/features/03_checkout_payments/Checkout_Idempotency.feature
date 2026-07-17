# WHY THIS SCENARIO IS A KNOWN GAP (investigated 2026-07-17)
#
# It used to FAIL with "[Processing cycle should be active] Expecting true but was false",
# which read like an application bug. It was not. The scenario never reached a checkout
# page at all:
#
#   1. /checkout REDIRECTS to the homepage when the cart is empty. (Of the 126 data-testids
#      on the landed page, every one is home-*/header-*/footer-* — ZERO checkout testids.
#      /cart is a 404; the cart is a drawer, not a page.)
#   2. The double-click step then threw NoSuchElementException, which a catch SWALLOWED
#      ("expected on mock/empty state"), so the scenario carried on as if it had clicked.
#   3. The assertion therefore ran against the HOMEPAGE and reported a meaningless failure.
#
# Two deeper problems mean seeding a cart is necessary but NOT sufficient:
#
#   * THERE IS NO "PLACE ORDER" BUTTON. "Proceed to Payment" only LAUNCHES the Razorpay
#     modal; the real order submission happens inside Razorpay's third-party iframe, which
#     Selenium cannot reach and which we have no authorisation to drive. Double-clicking it
#     would also create real orders in the payment gateway — so if this is ever completed
#     it must run against STAGING ONLY, never production.
#   * The double-submit guarantee that actually protects the customer is SERVER-side, and
#     it is already covered at the API layer by IdempotencyApiTest: the same
#     Idempotency-Key sent twice must be de-duplicated. That is the meaningful test, and
#     it passes.
#
# The @Given now fails fast with an explicit PendingException naming exactly what is
# missing, so this shows up as a deliberate, self-documenting gap rather than a lie. A
# skipped test that announces itself beats a "passing" test that asserts nothing.

Feature: Payment Submission Idempotency Gate
  As an impatient customer completing a luxury transaction
  I want the purchase submission button to lock automatically after a single click
  So that I do not get double-billed if I tap the interface repeatedly

  # KNOWN GAP: needs bag pre-seeding + staging-only execution. See the header above.
  # The server-side guarantee is covered by IdempotencyApiTest.
  @Regression @UI @KnownGap
  Scenario: Rapid double clicking on order completion locks the control element
    Given the user is on the final payment verification checkout module
    When they execute a rapid double-click on the "Place Order" trigger element
    Then the system should process exactly one transaction pipeline
    And the "Place Order" button should transform to a disabled state
