Feature: Payment Submission Idempotency Gate
  As an impatient customer completing a luxury transaction
  I want the purchase submission button to lock automatically after a single click
  So that I do not get double-billed if I tap the interface repeatedly

  @Regression @UI
  Scenario: Rapid double clicking on order completion locks the control element
    Given the user is on the final payment verification checkout module
    When they execute a rapid double-click on the "Place Order" trigger element
    Then the system should process exactly one transaction pipeline
    And the "Place Order" button should transform to a disabled state
