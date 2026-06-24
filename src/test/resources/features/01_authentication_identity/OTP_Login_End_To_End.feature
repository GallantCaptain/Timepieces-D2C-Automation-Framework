# Feature-level tag is @OtpE2E ONLY — deliberately NOT @Smoke or @Regression — so neither
# the SmokeRunner (@Smoke) nor the RegressionRunner (@Regression and not @Smoke) picks it up
# on a normal `mvn test`. It requires live Twilio config and a real inbound OTP, so run it
# explicitly and only when configured:
#
#   set TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN, TWILIO_PHONE_NUMBER  (rotate the token first)
#   set TWILIO_TEST_COUNTRY (e.g. "United States") and TWILIO_TEST_PHONE (national digits)
#   mvn test "-Dcucumber.filter.tags=@OtpE2E" "-DCF_BYPASS_TOKEN=<token>"
@OtpE2E @Authentication
Feature: End-to-end login using the real OTP delivered via Twilio

  @OtpE2E
  Scenario: Log in by reading the real OTP from the Twilio inbox
    Given a user navigates to the Timepieces login page
    When the user enters their Twilio-registered phone number
    And clicks the send OTP button
    Then the OTP verification screen should be displayed
    When the user enters the OTP retrieved from Twilio
    And clicks the continue button
