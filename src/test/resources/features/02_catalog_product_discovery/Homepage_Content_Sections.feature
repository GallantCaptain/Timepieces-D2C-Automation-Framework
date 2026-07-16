@Regression @HomepageContent @ProdReady
Feature: Homepage Content Sections and Overlays
  As a customer
  I want the content-rich homepage sections, stories and overlays to render correctly
  So that I can discover releases, partners and the brand story from the landing page

  Background:
    Given the user bypasses the staging password gate
    And the user is on the storefront homepage

  Scenario: Verify the hero renders its showreel and Discover CTA
    Then the homepage hero section is displayed
    And the hero background showreel video is present
    And the hero mute toggle is displayed

  Scenario: Verify the Two Stories release cards
    Then the "Two Stories, One Philosophy" section is displayed with both release cards
    And the Astonia story card carries its arrow CTA
    And the Manifesta story card is locked with the badge "SEP, 2026"

  Scenario: Verify the brand story quote is presented
    Then the brand story section is displayed
    And the brand quote contains "What you wear on your wrist"

  Scenario: Verify the homepage Friends and Partners carousel
    Then the homepage Friends and Partners section is displayed
    And homepage partner cards are displayed
    And every homepage partner card links to a partner profile

  Scenario: Verify the Last Release slider
    Then the Last Release slider is displayed
    And Last Release cards are displayed
    And every Last Release card links to a timepiece PDP

  Scenario: Verify the crafted-with-intent sequence and USP marquee
    Then the crafted-with-intent scroll sequence is displayed
    And the FEATURED IN marquee is displayed

  Scenario: Verify the stories modal opens, pauses and closes
    When the user opens the stories modal
    Then the stories modal is open
    And the stories modal shows at least 1 story
    And the current story image is displayed
    When the user pauses the stories
    Then the stories auto-advance is paused
    When the user taps to the next story
    And the user closes the stories modal
    Then the stories modal is closed

  Scenario: Verify the drop card popup mounts on a fresh session
    Then the drop card popup mounts within 25 seconds

  Scenario: Verify the drop card popup can be suppressed via its session state
    When the user marks the drop popup as already visited in the session
    And the user reloads the homepage
    Then the drop card popup does not mount within 20 seconds

  Scenario: Verify the footer country selector opens and filters
    When the user opens the footer country selector
    Then the country selector dropdown is open
    When the user searches the country selector for "United"
    Then the country options are filtered down
