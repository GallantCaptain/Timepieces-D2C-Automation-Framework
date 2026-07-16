@AuthGated @Orders
Feature: Account Orders Management
  As an authenticated customer
  I want to review my account navigation, order history and order details
  So that I can manage my Timepieces orders end to end

  # AUTH-GATED — deliberately NOT tagged @Regression: production login serves a
  # Cloudflare Turnstile that WebDriver cannot solve (see LoginPage Javadoc and
  # the CI Cloudflare blocker), so these scenarios only run in a session where a
  # human has completed login (or via a pre-authenticated browser profile). The
  # step definitions are fully wired to the AccountPage/OrderHistoryPage/
  # OrderDetailsPage POMs — the first step fails fast with a clear message when
  # the session is unauthenticated.

  Scenario: Verify the account page navigation is complete
    Given the user is authenticated on the account page
    Then the account navigation shows Orders, Owned timepieces and Wishlist
    And the logout button is displayed

  Scenario: Verify the order history lists the placed orders
    Given the user is authenticated on the account page
    When the user opens the order history
    Then the orders page heading is displayed
    And each order card shows its status and links to its details page

  Scenario: Verify the order details page shows the full order record
    Given the user is authenticated on the account page
    When the user opens the order details of "Timepieces3306"
    Then the order status and payment status are displayed
    And the shipping and billing address blocks are displayed

  Scenario: Verify the order-details Help button opens the support request sheet
    Given the user is authenticated on the account page
    When the user opens the order details of "Timepieces3306"
    And the user opens the Help support sheet
    Then the support request sheet is displayed
    And the support submit button is disabled until the form is filled
    When the user closes the support request sheet
    Then the support request sheet is hidden
