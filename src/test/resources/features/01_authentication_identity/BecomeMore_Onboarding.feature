@Regression @BecomeMore @ProdReady
Feature: Become More Onboarding Journey
  As a prospective Timepieces owner
  I want to build my profile through the guided Become More journey
  So that Timepieces knows who I am and what I am building towards

  # The /becomemore flow is PUBLIC (no login gate). The final "Submit Profile"
  # creates a REAL profile, so no scenario ever clicks it — the consent-gate
  # scenario only asserts the button ENABLES.

  Background:
    Given the user bypasses the staging password gate

  Scenario: Verify the intro screen and profile method dialog
    Given the user navigates to the Become More onboarding
    Then the Become More intro screen is displayed
    When the user begins their profile
    Then the profile method dialog is displayed with both entry options
    When the user closes the profile method dialog

  Scenario: Verify the personal info step gates progression until valid
    Given the user opens the Become More section "name"
    Then the step indicator reads "Step 01 of 07"
    And the next step control is disabled
    When the user fills the personal info with "Test User", "Mr", "01/01/1990" and "Professional"
    Then the next step control is enabled

  Scenario Outline: Verify each journey section deep-links with its heading
    Given the user opens the Become More section "<section>"
    Then the browser is on the Become More section "<section>"
    And the step heading contains "<heading>"

    Examples:
      | section      | heading                          |
      | name         | Tell us about yourself           |
      | location     | Where are you based              |
      | about        | More about you                   |
      | building     | What are you building towards    |
      | watchjourney | Tell us about your watch journey |
      | interest     | What interests you               |
      | timepieceshouse | The Timepieces House                |
      | photo        | Put a face to the story          |

  Scenario: Verify a watch-journey chip auto-advances the journey
    Given the user opens the Become More section "watchjourney"
    When the user selects the chip "This would be my first serious watch"
    Then the browser is on the Become More section "interest"

  Scenario: Verify the interests multi-select gates progression
    Given the user opens the Become More section "interest"
    Then the next step control is disabled
    When the user selects the chip "Watches"
    Then the chip "Watches" is selected
    And the next step control is enabled

  Scenario: Verify the Timepieces House step offers inspiration and can be skipped
    Given the user opens the Become More section "timepieceshouse"
    When the user opens the curated inspirations
    Then the curated inspirations are displayed
    When the user skips the house step
    Then the browser is on the Become More section "photo"

  Scenario: Verify the profile consent gates the final submission
    Given the user opens the Become More section "photo"
    Then the photo step is displayed
    And the submit profile button is disabled
    When the user ticks the profile consent
    Then the submit profile button is enabled
