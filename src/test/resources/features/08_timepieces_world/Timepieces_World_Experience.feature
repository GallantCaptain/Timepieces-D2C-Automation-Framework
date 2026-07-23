@Regression @TimepiecesWorld @ProdReady @ProdOnly
# @ProdOnly — these ALWAYS hit production, whatever -Denv says.
# TimepiecesWorld_ExperiencePage overrides getBaseUrl() to the hardcoded constant
# WORLD_BASE_URL = "https://world.timepieces-d2c.example", so the configured base.url is ignored
# entirely and there is no staging equivalent of the microsite.
#
# That is exactly why the tag is worth having: run the suite with -Denv=staging and these
# scenarios still drive PRODUCTION, while the report says "staging". Nobody should read a
# staging run as covering Timepieces World. Keep them out of a staging run with:
#     -Dcucumber.filter.tags="not @ProdOnly"
#
# NOTE the distinction: @ProdReady means "SAFE to run against production" (a permission);
# @ProdOnly means "ONLY meaningful on production" (a restriction). They are not synonyms,
# and a scenario can legitimately carry both — as these do.
Feature: Timepieces World Experience
  As a customer
  I want to journey through the Timepieces World scroll-canvas experience
  So that I can discover each timepiece family and reach its showcase page

  # The Timepieces World microsite lives on https://world.timepieces-d2c.example (a fixed
  # public origin, independent of the configured base.url) and disables native
  # scrolling — the canvas is advanced with synthetic wheel events.

  Scenario: Verify the welcome gate starts the canvas experience
    Given the user navigates to the Timepieces World experience page
    Then the Enter Experience gate is displayed
    When the user enters the experience
    Then the experience canvas is running
    And the progress rail shows 7 scene segments

  Scenario: Verify nudging the canvas reaches a family scene with a DISCOVER call to action
    Given the user navigates to the Timepieces World experience page
    And the user enters the experience
    When the user nudges the canvas forward until a family scene appears
    Then the active scene shows a family heading
    And the DISCOVER call to action is displayed

  Scenario: Verify the DISCOVER call to action opens the family showcase
    Given the user navigates to the Timepieces World experience page
    And the user enters the experience
    And the user nudges the canvas forward until a family scene appears
    When the user clicks the DISCOVER call to action
    Then the browser lands on a Timepieces World showcase page

  Scenario: Verify the sidebar menu lists all five family destinations
    Given the user navigates to the Timepieces World experience page
    When the user opens the Timepieces World sidebar menu
    Then the Timepieces World sidebar lists all 5 family destinations
    When the user closes the Timepieces World sidebar via the backdrop
    Then the Timepieces World sidebar is closed

  Scenario: Verify the header logo leaves for the main storefront from the experience page
    Given the user navigates to the Timepieces World experience page
    When the user clicks the Timepieces logo in the Timepieces World header
    Then the browser lands on the main Timepieces storefront
