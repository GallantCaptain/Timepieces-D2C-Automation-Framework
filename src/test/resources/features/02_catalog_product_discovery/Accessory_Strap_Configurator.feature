@Regression @Accessories @Straps
Feature: Accessory Strap Configurator
  As a customer
  I want to preview a strap against compatible watches and add a matching clasp
  So that I can visualise and complete a strap purchase for my timepiece

  Background:
    Given the user bypasses the staging password gate
    And the user is on the storefront homepage

  Scenario: Verify the dial-try carousel is present on a strap PDP
    Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"
    Then the dial-try carousel "A new look for your watch" is displayed

  Scenario: Verify the dial-try carousel cycles through compatible watch previews
    Given the user navigates to the accessory PDP of "ostrich-leather-strap-black"
    And the dial-try carousel "A new look for your watch" is displayed
    When the user advances the dial-try carousel to the next watch
    Then the strap preview updates to the next compatible watch
    When the user moves the dial-try carousel to the previous watch
    Then the strap preview updates to the previous compatible watch

  Scenario: Verify only compatible watch dials are offered in the strap preview
    Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"
    Then the dial-try carousel only shows dials compatible with "Astonia" and "Astonia Sports"

  Scenario: Verify the clasp-upgrade cross-sell is offered on a strap PDP
    Given the user navigates to the accessory PDP of "ostrich-leather-strap-black"
    Then the "Upgrade your strap" clasp-upgrade card is displayed

  Scenario: Verify a recommended clasp can be added from the strap upgrade card
    Given the user navigates to the accessory PDP of "ostrich-leather-strap-black"
    And the "Upgrade your strap" clasp-upgrade card is displayed
    When the user clicks the "+ Add" button on the clasp-upgrade card
    Then the recommended clasp is added to the cart

  Scenario: Verify a strap and its recommended clasp can be purchased together
    Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"
    When the user clicks the "Add to cart" button on the accessory PDP
    And the user clicks the "+ Add" button on the clasp-upgrade card
    Then the cart drawer contains both the strap and the recommended clasp
