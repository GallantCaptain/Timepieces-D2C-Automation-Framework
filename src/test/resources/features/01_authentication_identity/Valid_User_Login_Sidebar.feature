@Staging @Smoke @Authentication
Feature: Valid User Login via Global Responsive Sidebar
As a premium user of Timepieces
I want to log in successfully using the global responsive sidebar
So that I can access my personalized account features and information seamlessly

  @Smoke @Authentication
  Scenario Outline: Valid User Login via Global Responsive Sidebar
    Given the user has opened the global responsive sidebar menu
    When the user clicks on the "Sign In" interaction link
    Then the application should display the user authentication interface
    When the user selects the country "<country>" from the region dropdown
    And the user enters the phone number "<phone_number>" in the phone number input field
    And clicks the send OTP button
    Then the OTP verification screen should be displayed
    When the user enters the OTP "<otp>"
    And clicks the continue button
    Then the session context should update to the authenticated state
    And the sidebar element should display the personalized element "<account_marker>"

    Examples:
      | country | phone_number | otp    | account_marker |
      | India   |   9000000001 | 123456 | My Account     |
