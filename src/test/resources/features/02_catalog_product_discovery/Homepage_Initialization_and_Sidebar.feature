@Regression @Smoke @Homepage
Feature: Homepage Initialization and Sidebar Functionality
  As a premium user of Timepieces
  I want to ensure that the homepage initializes correctly and the sidebar functions as expected
  So that I can navigate through the platform seamlessly and access all features

  @Smoke @Homepage
  Scenario Outline: Verify the homepage initialization and sidebar functionality on the authenticated Timepieces storefront
    Given the user is on the authenticated Timepieces storefront homepage
    When the user checks the visibility of the primary storefront layout
    Then the hero header element should display the text "<hero_text>"
    When the user clicks on the global navigation hamburger menu
    Then the flyout sidebar container should expand successfully
    And the sidebar should contain the following visible interaction links:
      | Timepieces         |
      | Accessories        |
      | Friends & Partners |
      | Sign In            |

    Examples:
      | hero_text      |
      | Revealing Soon |
