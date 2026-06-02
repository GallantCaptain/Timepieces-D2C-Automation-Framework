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
    When the user inputs a valid identifier "<user_phone>"
    And inputs the corresponding secret access key "<pass_otp>"
    And clicks the authentication submission element
    Then the session context should update to the authenticated state
    And the sidebar element should display the personalized element "<account_marker>"

    Examples:
      | user_phone          | pass_otp             | account_marker                |
      | qa_test@timepieces-d2c.example | ${TEST_ACCOUNT_PASS} | My Account {Name/Designation} |
