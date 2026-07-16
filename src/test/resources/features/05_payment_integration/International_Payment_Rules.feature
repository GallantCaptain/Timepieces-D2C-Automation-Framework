@Regression @IntlPayments
Feature: International Payment Rules
  As an international customer
  I want the checkout to route, price and gate my payment per my region
  So that I can pay by the supported methods for my country

  # Owner rules (2026-07-16): international Slide-to-Pay routes to /checkout
  # (not the in-page modal); prices are 1.5x domestic in EUR/AED/USD by region
  # (the multiplier itself is a MANUAL Zephyr check — no fx-rate oracle);
  # unsupported countries are blocked at Proceed to Payment; the international
  # Razorpay modal offers ONLY cards + Apple Pay. No scenario ever completes a
  # payment.

  Background:
    Given the user bypasses the staging password gate
    And the user is on the storefront homepage
    And the user switches the storefront region to "US"
    Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"
    When the user clicks the "Add to cart" button on the accessory PDP
    Then the slide-out cart drawer is displayed

  Scenario: Verify international Slide to Pay routes to the delivery information page
    When the user slides the transactional checkout slide trigger element
    Then the user is routed to the delivery information checkout page

  Scenario: Verify an unsupported country is blocked at Proceed to Payment
    When the user slides the transactional checkout slide trigger element
    Then the user is routed to the delivery information checkout page
    When the user fills the delivery information with the country "KP"
    And the user proceeds to payment from the checkout page
    Then the unsupported-country alert is displayed

  Scenario: Verify the international Razorpay modal offers only cards and Apple Pay
    When the user slides the transactional checkout slide trigger element
    Then the user is routed to the delivery information checkout page
    When the user fills the delivery information with the country "US"
    And the user proceeds to payment from the checkout page
    Then the Razorpay payment options offer only cards and Apple Pay
    When the user exits the Razorpay modal via the exit confirmation
