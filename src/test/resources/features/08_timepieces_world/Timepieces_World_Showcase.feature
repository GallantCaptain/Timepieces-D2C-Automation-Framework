@Regression @TimepiecesWorld @ProdReady @ProdOnly
# @ProdOnly — same reason as Timepieces_World_Experience.feature:
# SharedTemplate_TimepiecesWorldProduct overrides getBaseUrl() to the hardcoded
# WORLD_BASE_URL = "https://world.timepieces-d2c.example", so -Denv is ignored and these scenarios
# drive PRODUCTION even during a staging run. There is no staging microsite to point at.
# Keep them out of a staging run with:  -Dcucumber.filter.tags="not @ProdOnly"
Feature: Timepieces World Product Showcases
  As a customer
  I want to explore a timepiece family's Timepieces World showcase
  So that I can review its story and specs and join its pre-launch waitlist

  # All 5 showcase pages share ONE template (2026-07-16 scrape verdict). The
  # waitlist submit raises a REAL signup, so scenarios only assert the
  # disabled-until-valid gate and never submit.

  Scenario Outline: Verify each family showcase renders the shared template
    Given the user navigates to the Timepieces World showcase of "<family>"
    Then the showcase title matches the "<family>" family pattern
    And the family logo and tagline are displayed
    And the waitlist form is displayed

    Examples:
      | family    |
      | arvion    |
      | monarch   |
      | auriqua   |
      | astonia   |
      | manifesta |

  Scenario: Verify the waitlist submit is gated until the form validates
    Given the user navigates to the Timepieces World showcase of "astonia"
    Then the waitlist form is displayed
    And the waitlist submit button is disabled

  Scenario: Verify the calibre specs and material details are presented
    Given the user navigates to the Timepieces World showcase of "astonia"
    Then the showcase lists the calibre spec "BASE CALIBRE"
    And the showcase lists the calibre spec "TYPE"
    And material learn-more buttons are offered
    And the showcase footer support links are present

  # DELETED 2026-07-17 — "Verify Back to scene returns to the experience page".
  #
  # The scenario tested a feature that has never existed. "Back to scene" matches 0
  # nodes on all 5 showcase pages, and the string appears nowhere in any of their
  # DOMs — it came from the 2026-07-16 scrape's reading of the page, not from the
  # markup. A test cannot be repaired into passing against a button that was never
  # built, and leaving it red would have buried the real failures underneath it.
  #
  # No coverage is lost: the only route from a showcase back to the experience is
  # the header logo, which the scenario immediately below already exercises (and on
  # a different family, so both are still covered).
  #
  # If a "Back to scene" control is ever built, restore this scenario along with
  # clickBackToScene()/isBackToSceneDisplayed() in SharedTemplate_TimepiecesWorldProduct.

  Scenario: Verify the header logo returns to the experience from a showcase page
    Given the user navigates to the Timepieces World showcase of "auriqua"
    When the user clicks the Timepieces logo in the Timepieces World header
    Then the browser returns to the Timepieces World experience page
