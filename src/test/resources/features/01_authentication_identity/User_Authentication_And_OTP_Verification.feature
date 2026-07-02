@Authentication @Regression
Feature: User Authentication and OTP Flow
  As a premium user of Timepieces
  I want to securely log into the platform using my mobile number and OTP
  So that I can access my wishlist, order history, and exclusive drops

  @Smoke @Login
  Scenario Outline: Successful initialization of the Login and OTP prompt — <country>
    Given a user navigates to the Timepieces login page
    When the user selects the country "<country>" from the region dropdown
    And the user enters the phone number "<phone_number>" in the phone number input field
    And clicks the send OTP button
    Then the OTP verification screen should be displayed

    Examples:
      | country       | phone_number |
      | India         |   9000000001 |
      | United States |   1234567890 |
