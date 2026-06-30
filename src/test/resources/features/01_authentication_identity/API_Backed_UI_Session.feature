Feature: API-Backed UI Session Validation
  As a premium Timepieces customer
  I want to bypass the slow UI login flow via fast API authentication
  So that I can load my account details page instantly and securely

  @Smoke @Bypass
  Scenario: Authenticated user bypasses login using API token injection
    Given the customer bypasses the UI login using API identity "customer@timepieces-d2c.example"
    And they navigate straight to their account details page
    Then the dashboard should welcome them without prompting for an OTP login
