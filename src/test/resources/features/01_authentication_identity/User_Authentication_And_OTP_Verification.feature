@Authentication @Regression
Feature: User Authentication and OTP Flow
  As a premium user of Timepieces
  I want to securely log into the platform using my mobile number and OTP
  So that I can access my wishlist, order history, and exclusive drops

  # ---------------------------------------------------------------------------------
  # KNOWN BROKEN — the developers reworked the login/OTP page on 2026-07-23 without
  # prior notice, and this scenario has NOT been re-aligned to the new page. It is left
  # failing ON PURPOSE, tagged @KnownBroken, for the next SDET to re-map once the new
  # login markup settles. Do not spend time "fixing" the framework here first: confirm
  # against the live page what changed (region dropdown, phone input, Send OTP control,
  # and whether an OTP screen still follows) before touching the step definitions.
  #
  # It is deliberately NOT deleted and NOT silently excluded from runs — a visible red
  # that names its own cause is more honest at handover than a green suite that quietly
  # stopped testing login. See ISSUE-023.
  #
  # Note: the India example number here is a throwaway (7428730894), NOT anyone's real
  # number — keep it that way.
  # ---------------------------------------------------------------------------------
  @Smoke @Login @KnownBroken
  Scenario Outline: Successful initialization of the Login and OTP prompt — <country>
    Given a user navigates to the Timepieces login page
    When the user selects the country "<country>" from the region dropdown
    And the user enters the phone number "<phone_number>" in the phone number input field
    And clicks the send OTP button
    Then the OTP verification screen should be displayed

    Examples:
      | country       | phone_number |
      | India         |   7428730894 |
      | United States |   1234567890 |
