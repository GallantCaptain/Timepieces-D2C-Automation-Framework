# SECURITY REGRESSION — the OTP VERIFY step enforces NO attempt limit. A caller can submit
# unlimited wrong 6-digit codes (clearing + retrying), so the 1,000,000-code space is fully
# brute-forceable. This scenario DOCUMENTS the exposure: it PASSES today (no lockout ever appears)
# and is expected to START FAILING once a server-side attempt cap / lockout is added — at which
# point the assertion should be inverted to require the lockout.
#
# Feature-level tag is @OtpBruteForce ONLY (not @Smoke / @Regression) so neither runner picks it
# up on a normal `mvn test`. It is slow (submits many wrong codes) and is a deliberate probe.
# Run it explicitly:
#   mvn test "-Dcucumber.filter.tags=@OtpBruteForce" "-DCF_BYPASS_TOKEN=<token>"
@OtpBruteForce @Security @Authentication
Feature: OTP verification brute-force exposure

  @OtpBruteForce @Security
  Scenario: OTP verify accepts unlimited wrong codes without any lockout
    Given a user navigates to the Timepieces login page
    When the user selects the country "India" from the region dropdown
    And the user enters the phone number "7428730894" in the phone number input field
    And clicks the send OTP button
    Then the OTP verification screen should be displayed
    When the user submits 20 incorrect OTP codes in a row
    Then the OTP entry is never locked or rate limited
