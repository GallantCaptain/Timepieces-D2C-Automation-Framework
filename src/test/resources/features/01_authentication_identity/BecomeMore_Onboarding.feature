@Regression @BecomeMore @ProdReady
Feature: Become More Onboarding Journey
  As a prospective Timepieces owner
  I want to build my profile through the guided Become More journey
  So that Timepieces knows who I am and what I am building towards

  # The /becomemore flow is PUBLIC (no login gate). The final "Submit Profile"
  # creates a REAL profile, so no scenario ever clicks it — the consent-gate
  # scenario only asserts the button ENABLES.
  #
  # SHAPE (verified end-to-end in Chrome, 2026-07-17): 9 screens, 7 NUMBERED
  # steps. The journey starts on `contact` ("Let's get started"), which the
  # original scrape missed entirely. Numbered steps run name (01) -> location
  # (02) -> about (03) -> building (04) -> watchjourney (05) -> interest (06) ->
  # timepieceshouse (07), then the unnumbered `photo` screen closes it out.
  #
  # `name` and `photo` CANNOT be deep-linked — the app guards them behind
  # in-journey state and redirects to `contact`. Scenarios needing those two
  # screens enter via `contact` or walk the whole journey. See the page object.
  #
  # ############################ APP BUG — FILE WITH DEV ########################
  # The `contact` screen validates NOTHING: "Continue" advances to Step 01 with
  # BOTH the phone and the email field EMPTY, and the button is never disabled
  # (verified 2026-07-17). Its own copy says "How can we reach you? We'll verify
  # your number at the end." — so a visitor can finish the whole journey with no
  # contact details at all, having been told a number would be verified. Every
  # other gated screen in this journey behaves correctly, so this is an
  # oversight, not a design choice.
  # There is DELIBERATELY no scenario asserting the contact screen gates: it does
  # not, and encoding the broken behaviour would cement it. When dev fixes this,
  # add a gating scenario mirroring the personal-info one below.
  # #############################################################################

  Background:
    Given the user bypasses the staging password gate

  Scenario: Verify the intro screen and profile method dialog
    Given the user navigates to the Become More onboarding
    Then the Become More intro screen is displayed
    When the user begins their profile
    Then the profile method dialog is displayed with both entry options
    When the user closes the profile method dialog
    Then the profile method dialog is dismissed

  # Entered via `contact` rather than a "name" deep-link: ?section=name
  # redirects to `contact`, which has no step indicator, so the deep-link entry
  # waited 15s for "Step 01 of 07" on a screen that never shows one.
  Scenario: Verify the personal info step gates progression until valid
    Given the user enters the Become More journey at the personal info step
    Then the browser is on the Become More section "name"
    And the step heading contains "Tell us about yourself"
    And the step indicator reads "Step 01 of 07"
    And the next step control is disabled
    When the user fills the personal info with "Test User", "Mr", "01/01/1990" and "Professional"
    Then the next step control is enabled

  Scenario Outline: Verify each journey section deep-links with its heading
    Given the user opens the Become More section "<section>"
    Then the browser is on the Become More section "<section>"
    And the step heading contains "<heading>"

    # The `contact` screen is listed FIRST because it is the journey's real
    # starting point — the 2026-07-16 scrape missed it and the table started at
    # `name`.
    #
    # `name` and `photo` are DELIBERATELY ABSENT from this table. They are not
    # deep-linkable: opening ?section=name or ?section=photo cold redirects to
    # `contact`, so both rows asserted against the wrong screen and timed out
    # after 15s. That is the app guarding those screens behind journey state, not
    # a locator fault — no wait or selector can fix it, and for `photo` every
    # alias was tried too (avatar, profilephoto, picture, image, submit, review,
    # final: all redirect). Neither heading loses coverage: `name` is asserted by
    # the personal-info scenario above and `photo` by the full-walk scenario
    # below, both of which reach those screens the way a real visitor does.
    Examples:
      | section      | heading                          |
      | contact      | Let's get started                |
      | location     | Where are you based              |
      | about        | More about you                   |
      | building     | What are you building towards    |
      | watchjourney | Tell us about your watch journey |
      | interest     | What interests you               |
      | timepieceshouse | The Timepieces House                |

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

  # Walks all 9 screens because `photo` cannot be deep-linked (see the Examples
  # note above). This also carries the `photo` heading assertion that the
  # deep-link table can no longer reach.
  Scenario: Verify the profile consent gates the final submission
    Given the user walks the whole Become More journey to the photo step
    Then the browser is on the Become More section "photo"
    And the step heading contains "Put a face to the story"
    And the photo step is displayed
    And the submit profile button is disabled
    When the user ticks the profile consent
    Then the submit profile button is enabled
