@Authentication @Regression
Feature: Cart Addition and Domestic Checkout
  As a premium user of Timepieces
  I want to securely add the items to my cart 
  So that I can purchase the items through a secure checkout funnel without any redirection

  @Smoke @Login
  Scenario Outline: Verify Item Cart Injection and Secure Checkout Funnel Initialization
    Given the user is viewing a valid Product Detail Page Layout
    When the user clicks the primary transaction action element "<action_button>"
    Then the selected product should be added to the active shopping bag
    When the user opens the active shopping bag interface
    Then the cart drawer container should display the selected product item
    And the transactional summary with the chosen region price should render correctly
    When the user slides the transactional checkout slide trigger element
    Then the Razorpay secure payment modal interface should initialize successfully within the active viewing
    And the customer contact information form should become visible
    And the system should not perform a hard URL redirect

    Examples:
      | action_button |
      | Add to Cart   |
