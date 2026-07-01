@Regression @Cart
Feature: Cart and Bag Management Drawer
  As a customer
  I want to manage items in my slide-out cart drawer
  So that I can review and adjust my items before entering the checkout funnel

  Background:
    Given the user bypasses the staging password gate
    And the user is on the storefront homepage

  Scenario: Verify slide-out cart drawer open and close transitions
    When the user clicks the cart icon on the header
    Then the slide-out cart drawer is displayed
    When the user clicks the close button in the cart drawer
    Then the cart drawer is hidden

  Scenario: Verify successful product addition from PDP to cart
    Given the user navigates to the Product Detail Page of "manifesta-black-onyx"
    When the user clicks the "Add to Cart" button
    Then the slide-out cart drawer is displayed
    And the cart badge count is updated to "1"
    And the cart drawer displays the product "manifesta-black-onyx" with its correct title, configuration, and price

  Scenario: Verify quantity modification dynamically updates subtotal
    Given the user has added "manifesta-black-onyx" to the cart
    And the slide-out cart drawer is displayed
    When the user increases the quantity of "manifesta-black-onyx" to "2"
    Then the item quantity shows "2"
    And the cart subtotal is updated to match twice the item unit price
    When the user decreases the quantity of "manifesta-black-onyx" to "1"
    Then the item quantity shows "1"
    And the cart subtotal is updated to match the item unit price

  Scenario: Verify product removal from cart drawer
    Given the user has added "manifesta-black-onyx" to the cart
    And the slide-out cart drawer is displayed
    When the user clicks the remove icon for "manifesta-black-onyx"
    Then the item "manifesta-black-onyx" is removed from the cart drawer
    And the cart shows "Your cart is currently empty"
    And the cart badge count on the header updates to "0"

  Scenario: Verify free shipping progress bar updates dynamically
    Given the free shipping threshold is "$150.00"
    And the user has a cart subtotal of "$0.00"
    Then the free shipping progress bar shows "$150.00 away from free shipping"
    When the user adds a product priced at "$100.00" to the cart
    Then the progress bar updates to show "$50.00 away from free shipping"
    When the user adds a product priced at "$60.00" to the cart
    Then the progress bar displays "You have qualified for Free Shipping!"

  Scenario: Verify cart items persist across page refreshes
    Given the user has added "manifesta-black-onyx" to the cart
    When the user reloads the storefront page
    And clicks the cart icon on the header
    Then the slide-out cart drawer displays the product "manifesta-black-onyx" with its correct quantity
