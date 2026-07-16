@Regression @ContentPages @Policies @ProdReady
Feature: Legal and Policy Pages
  As a customer
  I want to read the Timepieces legal and policy pages
  So that I understand the guarantee, privacy, refund, shipping and service terms

  Scenario Outline: Verify each policy page renders the shared policy template
    Given the user navigates to the "<policy>" policy page
    Then the policy browser title contains "<title>"
    And the policy main heading "<heading>" is displayed
    And the policy prose content is displayed
    And the policy page offers the "Back to Policies" and "View all policies" navigation

    Examples:
      | policy           | title            | heading                  |
      | guarantee-policy | Guarantee Policy | Guarantee Policy         |
      | privacy-policy   | Privacy Policy   | Privacy Policy           |
      | refund-policy    | Refund Policy    | Refund & Exchange Policy |
      | shipping-policy  | Shipping Policy  | Timepieces Shipping Policy  |
      | terms-of-service | Terms of Service | Terms of Service         |

  Scenario: Verify a policy body offers a support contact email
    Given the user navigates to the "guarantee-policy" policy page
    Then the policy body contains a support contact email link
