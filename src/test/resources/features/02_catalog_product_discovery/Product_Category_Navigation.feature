@Smoke
Feature: Product Category Navigation

  Scenario Outline: Navigate to Product Category and Verify Product Listing and Detail Page
    Given the user is on the main navigation sidebar interface
    When the user selects the primary product category link "<category_link>"
    Then the application should list the corresponding Product Listing Page
    And the product listing matrix should display atleast one visible item asset
    When the user selects the primary listing item card element
    Then the application should navigate to the target Product Detail Page
    And the core transaction element "<action_button>" should be visible and interactable

    Examples:
      | category_link | action_button |
      | Timepieces    | Wishlist      |
      | Accessories   | Wishlist      |
