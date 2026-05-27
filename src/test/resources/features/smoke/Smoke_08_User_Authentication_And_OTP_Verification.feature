@Authentication @Regression
Feature: User Authentication and OTP Flow
  As a premium user of Timepieces
  I want to securely log into the platform using my mobile number and OTP
  So that I can access my wishlist, order history, and exclusive drops

  @Smoke @Login
  Scenario: Successful initialization of the Login and OTP prompt
    Given a user navigates to the Timepieces login page
    When the user selects the country "India" from the region dropdown
    And enters the phone number "9876543210"
    And clicks the send OTP button
    Then the OTP verification screen should be displayed
