@Regression @Smoke @Footer
Feature: Global Footer Navigation
  As a premium user of Timepieces
  I want to ensure that the global footer displays correct links
  So that I can access utility pages and legal policies from the bottom of any page

  @Smoke @Footer
  Scenario: Verify global footer links and policy routing
    Given the user is on the Timepieces storefront homepage 
    Then the global footer should be visible at the bottom of the page
    And the footer should display the following policy and utility links:
      | Legal              |
      | Sitemap            |
      | Contact            |
      | Account            |
      | Share your moment  |
    And each footer link should navigate to the correct page in a new tab when clicked and then close the tab:
      | Legal              |
      | Sitemap            |
      | Contact            |
      | Account            |
      | Share your moment  |
