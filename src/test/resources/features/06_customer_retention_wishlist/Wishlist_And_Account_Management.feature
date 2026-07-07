@Regression @Wishlist @Account
Feature: Wishlist and Account Profile Management
  As a customer
  I want to manage my wishlist items and view my order profile details
  So that I can personalize my shopping experience and track past purchases

  Scenario: Verify guest user can add product to wishlist
    Given the user bypasses the staging password gate
    And the user is a guest customer
    When the user navigates to the Product Detail Page of "manifesta-black-onyx"
    And clicks the "Wishlist" button
    Then the product "manifesta-black-onyx" is added to the user's wishlist
    When the user navigates to the Wishlist page
    Then the wishlist page displays the product "manifesta-black-onyx"

  Scenario: Verify guest user can remove product from wishlist
    Given the user is on the Wishlist page
    And the wishlist displays the product "manifesta-black-onyx"
    When the user clicks the remove button for "manifesta-black-onyx" on the wishlist page
    Then the product "manifesta-black-onyx" is deleted from the wishlist
    And the wishlist page displays "Your wishlist is currently empty"

  Scenario: Verify guest wishlist items sync to account on login
    Given the user bypasses the staging password gate
    And the user is a guest customer
    And the user has added "manifesta-black-onyx" to their wishlist
    When the user logs in with a valid account
    And navigates to the Wishlist page
    Then the wishlist page displays the product "manifesta-black-onyx" synced under the account

  Scenario: Verify authenticated customer can view order history
    Given the user is logged in with a valid account
    When the user navigates to the Track Orders account section
    Then the order history table displays all past orders with date, total price, and status

  Scenario: Verify order tracking shipping status details
    Given the user is on the Track Orders account section
    When the user clicks on a past order ID "ORD-98765"
    Then the order detail screen displays the carrier shipping tracking link
    And the delivery status shows the current transit checkpoints

  Scenario: Verify customer can edit saved shipping address
    Given the user is logged in with a valid account
    When the user navigates to the Account Address Book settings
    And clicks the "Edit Address" button for the default address
    And updates the street address field to "456 Luxury Lane"
    And clicks the save address button
    Then the default address displays the updated street "456 Luxury Lane"
