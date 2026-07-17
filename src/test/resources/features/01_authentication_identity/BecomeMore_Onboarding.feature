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
  # ############ DRAFT RESTORE — WHY NO SCENARIO ASSUMES AN EMPTY FORM #########
  # The journey does NOT start empty on production. It restores a previously
  # entered profile draft into a BRAND-NEW anonymous session. Measured
  # 2026-07-17 in a fresh Chrome profile using a phone/email never used before,
  # the `name` screen still arrived holding "Test User" / "Mr" / "01/01/1990" /
  # "Professional" and the `interest` screen arrived with "Watches" already
  # selected — the exact values this suite types, so the suite's own earlier runs
  # seeded it. It is not localStorage and not keyed to the contact details
  # entered, so a fresh browser cannot escape it.
  #
  # Consequence for these scenarios: "Next starts disabled" and "nothing is
  # selected" are only true for a genuine first-time visitor, which production is
  # not. Any scenario needing those preconditions CREATES them (clears the name,
  # clears the chips) instead of assuming them. That is why the gate scenarios
  # below look more verbose than they otherwise would — the extra steps are the
  # point, not noise. Reported for dev; a privacy review is warranted, since one
  # anonymous visitor's in-progress details are served to another.
  # ############################################################################
  #
  # ##################### APP BUG — ISSUE-017 (filed, app-side) #################
  # Tracked in docs/okf/known-issues.md as ISSUE-017.
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
  #
  # The full name is CLEARED before the gate is asserted. The screen arrives
  # PRE-FILLED from the restored draft (see the DRAFT RESTORE block at the top),
  # so "Next starts disabled" is not observable on arrival — the form is already
  # valid and Next is legitimately enabled. Emptying a required field re-arms the
  # gate deterministically and asserts the app's real rule rather than an
  # assumption about how the journey happened to arrive.
  Scenario: Verify the personal info step gates progression until valid
    Given the user enters the Become More journey at the personal info step
    Then the browser is on the Become More section "name"
    And the step heading contains "Tell us about yourself"
    And the step indicator reads "Step 01 of 07"
    When the user clears the full name
    Then the next step control is disabled
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

  # The selection is cleared first: this screen also arrives with chips already
  # chosen from the restored draft, which makes "Next starts disabled"
  # unobservable (the gate is legitimately open) AND makes "select Watches"
  # DESELECT it, because the chips toggle.
  Scenario: Verify the interests multi-select gates progression
    Given the user opens the Become More section "interest"
    When the user clears every selected interest
    Then the next step control is disabled
    When the user selects the chip "Watches"
    Then the chip "Watches" is selected
    And the next step control is enabled

  # The modal is CLOSED before the step is left. "Need inspiration?" opens a real
  # full-viewport modal (fixed inset-0 z-[80]) that covers everything behind it,
  # including "I'll do this later" — so clicking skip while it is open is
  # intercepted BY THE MODAL. Closing it first is what a real user does; the
  # previous version of this scenario simply skipped that step.
  #
  # This scenario deliberately STOPS at closing the modal. It used to end with
  # "Then the browser is on the Become More section photo", which CANNOT hold from
  # a deep-link entry: `photo` is guarded by journey state, so skipping from a
  # cold ?section=timepieceshouse lands on `contact` ("Let's get started"), not
  # `photo` — measured 2026-07-17, the URL sits on section=contact for 6s+ after
  # the click. That is the same guard that makes ?section=photo redirect.
  # The skip -> photo transition is NOT lost: the full-walk scenario below reaches
  # timepieceshouse legitimately and skips to photo, and asserts exactly that.
  Scenario: Verify the Timepieces House step offers curated inspiration
    Given the user opens the Become More section "timepieceshouse"
    When the user opens the curated inspirations
    Then the curated inspirations are displayed
    When the user closes the curated inspirations
    Then the curated inspirations are dismissed

  # Walks all 9 screens because `photo` cannot be deep-linked (see the Examples
  # note above). This carries two assertions the deep-link scenarios cannot reach:
  # the `photo` heading, and that "I'll do this later" on the Timepieces House step
  # really does advance the journey to `photo` (walkToPhotoStep leaves that step
  # via the skip control).
  Scenario: Verify the profile consent gates the final submission
    Given the user walks the whole Become More journey to the photo step
    Then the browser is on the Become More section "photo"
    And the step heading contains "Put a face to the story"
    And the photo step is displayed
    And the submit profile button is disabled
    When the user ticks the profile consent
    Then the submit profile button is enabled
