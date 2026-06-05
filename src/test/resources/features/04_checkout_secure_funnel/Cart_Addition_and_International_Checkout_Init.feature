@Smoke @Checkout
Feature: Cart Addition and International Checkout Initialization
As a premium user of Timepieces 
I want to add a product to the cart and initiate the international checkout process
So that I can verify the cart functionality and the checkout flow for international orders

  Scenario Outline: Add Product to Cart and Initiate International Checkout
    Given the user has atleast one active product in the shopping bag
    When the user initiates checkout for the international region "<country>"
    Then the system should safely redirect to the secure payment funnel view "<checkout_url>"
    And the regional delivery address form should render successfully
    And the localized pricing structure should become visible

    Examples:
      | country | checkout_url                 |
      | Norway  | https://timepieces-d2c.example/checkout |
