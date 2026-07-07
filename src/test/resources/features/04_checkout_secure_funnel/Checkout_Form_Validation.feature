@Regression @Checkout
Feature: Checkout Funnel and Validation
  As a customer
  I want to enter my delivery and billing details securely
  So that I can verify shipping options and discount calculations before paying

  Background:
    Given the user bypasses the staging password gate
    And the user has at least one active product in the shopping bag
    And the user initiates international checkout to navigate to the checkout page

  Scenario: Verify validation errors for mandatory fields left blank
    When the user leaves all delivery details blank
    And clicks the "Proceed to Payment" button
    Then the checkout form displays mandatory error messages under:
      | First Name     |
      | Last Name      |
      | Phone          |
      | Email          |
      | Street Address |
      | City           |
      | Pin code       |
    And a global alert message "Please fill in all required fields" is displayed

  Scenario: Verify email address format validation
    When the user enters an invalid email "invalid-email-format"
    And fills out all other mandatory delivery details
    And clicks the "Proceed to Payment" button
    Then the email field error message "Please enter a valid email address" is displayed

  Scenario: Verify pincode numeric validation
    When the user enters an invalid pincode "ABC-123"
    And fills out all other mandatory delivery details
    And clicks the "Proceed to Payment" button
    Then the pincode field error message "Please enter a valid postal code" is displayed

  Scenario: Verify billing address form expansion
    Given the billing address same as delivery checkbox is checked
    When the user unchecks the billing address same as delivery checkbox
    Then the separate Billing Address form section expands
    And the billing form displays mandatory input fields for name, address, and phone

  Scenario: Verify error when applying invalid promo code
    When the user enters a promo code "INVALIDCODE123" in the discount field
    And clicks the apply promo code button
    Then the promo field displays an error message "Discount code is invalid"
    And the subtotal and order total pricing structure remains unchanged

  Scenario: Verify dynamic total calculation with valid promo code
    Given a valid promo code "SUMMER10" provides a 10% discount
    When the user enters a promo code "SUMMER10" in the discount field
    And clicks the apply promo code button
    Then the discount is successfully applied to the order summary
    And the order total is dynamically updated to reflect the 10% reduction
