@Regression @FriendsPartners @ProdReady
Feature: Friends and Partners
  As a customer
  I want to browse the Friends & Partners directory and the partner profiles
  So that I can see the eminent personalities who own Timepieces timepieces

  Scenario: Verify the landing page shows the three sections and profile cards
    Given the user navigates to the Friends and Partners page
    Then the "Friends & Partners" heading is displayed
    And the section tab "Voices" is available
    And the section tab "Founders & Makers" is available
    And the section tab "Thought Leaders" is available
    And partner profile cards are displayed
    And at least one partner card carries a video badge

  Scenario: Verify a partner profile shows the standout hero and embedded video
    Given the user navigates to the partner profile of "kush-maini"
    Then the partner profile hero is displayed with the tagline "A Timepieces Man"
    And the partner profile features an embedded video
    And the sticky numbered-watch bar is displayed
    And every watch link on the profile targets a product PDP

  Scenario: Verify a multi-watch partner profile expands the See More drawer
    Given the user navigates to the partner profile of "vivek-oberoi"
    When the user expands the "See More" watches drawer
    Then the watches drawer lists additional purchased watches

  Scenario: Verify a partner profile navigates back to the directory
    Given the user navigates to the partner profile of "kush-maini"
    When the user navigates back to Friends and Partners
    Then the "Friends & Partners" heading is displayed
