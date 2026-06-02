@Smoke @GlobalSearch
Feature: Global Search Functionality Validation
As a premium user of Timepieces  
I want to validate that the global search functionality returns relevant product results
So that I can efficiently find products and information on the storefront  

  Scenario Outline: Verify Core Search E​​​​​ngine Returns Valid Product Results
    Given the user is on the active storefront layout
    When the user activates the global search input element
    And inputs a known valid product catalog query "<search_term>"
    And submits the search request
    Then the system should route to the search results matrix
    And the results should contain at least one visible item matching the query

    Examples:
      | search_term |
      | Deployment  |
      | Strap       |
