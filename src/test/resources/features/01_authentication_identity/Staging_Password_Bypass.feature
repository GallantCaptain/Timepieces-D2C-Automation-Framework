@Staging @PasswordBypass
Feature: Staging Password Bypass
As a premium test user of Timepieces
  I want to bypass the staging gatekeeper password
  So that I can access the staging environment storefront and perform necessary testing without any hindrance

  @Smoke @Authentication
  Scenario Outline: Bypass the staging gatekeeper password
    Given the user navigates to the staging environment application URL "<env_url>"
    When the user enters the valid gatekeeper password "<password_secret>"
    And clicks the submit button
    Then the password wall clears successfully
    And the active storefront landing page initializes with the visible logo tracking element "<logo_locator>"

    Examples:
      | env_url                     | password_secret      | logo_locator          |
      | https://staging.timepieces-d2c.example | ${STAGING_GATE_PASS} | header a[href='/'] svg |
