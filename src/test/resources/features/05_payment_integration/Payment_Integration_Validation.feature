@Regression @Payment
Feature: Payment Integration and Security
  As a customer
  I want to securely execute my credit card payments
  So that I can verify payment validation, declined alerts, and success pages

  Background:
    Given the user bypasses the staging password gate
    And the user has filled in all required delivery information on the checkout page
    And has navigated to the payment section

  Scenario: Verify credit card format validation errors
    When the user enters invalid card credentials:
      | Card Number | 4111 1111      |
      | Expiry Date | 13/29          |
      | CVV         | 12             |
    Then the credit card form displays inline errors for number, expiry, and CVV formats

  Scenario: Verify error on card declined due to insufficient funds
    When the user enters card credentials for insufficient funds "4000 0000 0000 0002"
    And clicks the place order button
    Then the payment system displays an error "Your card has insufficient funds"
    And the order is not placed

  Scenario: Verify error on card declined due to expired card
    When the user enters card credentials for an expired card "4000 0000 0000 0003"
    And clicks the place order button
    Then the payment system displays an error "Your card is expired"
    And the order is not placed

  Scenario: Verify error on card declined due to incorrect security code
    When the user enters card credentials for incorrect CVV "4000 0000 0000 0004"
    And clicks the place order button
    Then the payment system displays an error "Your card's security code is incorrect"
    And the order is not placed

  Scenario: Verify secure token transmission during card processing
    When the user submits valid card credentials
    Then the checkout submission transmits a secure one-time payment token
    And does not send raw credit card numbers directly to storefront APIs

  Scenario: Verify successful payment routes to order confirmation page
    When the user enters a valid test card number "4000 0000 0000 0001"
    And clicks the place order button
    Then the user is redirected to the Order Confirmation success page
    And the page displays a unique order ID and purchase summary
