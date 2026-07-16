@Regression @TimepiecesWorld @ProdReady
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

  Scenario: Verify Back to scene returns to the experience page
    Given the user navigates to the Timepieces World showcase of "monarch"
    When the user clicks Back to scene
    Then the browser returns to the Timepieces World experience page

  Scenario: Verify the header logo returns to the experience from a showcase page
    Given the user navigates to the Timepieces World showcase of "auriqua"
    When the user clicks the Timepieces logo in the Timepieces World header
    Then the browser returns to the Timepieces World experience page
