@Regression @TrackOrder @ProdReady
Feature: Guest Order Tracking
  As a guest customer
  I want to track my order by its order id without logging in
  So that I can monitor the progress of my Timepieces timepiece

  Scenario: Verify the tracking entry form is displayed with a disabled submit
    Given the user navigates to the Track Order page
    Then the "Track your order" heading is displayed
    And the order id input is displayed
    And the track order button is disabled while the input is empty

  Scenario: Verify an unknown order id shows the order-not-found error
    Given the user navigates to the Track Order page
    When the user tracks the order id "INVALID-0000"
    Then the order-not-found error is displayed

  Scenario: Verify the orderId deep link auto-submits the tracking search
    Given the user opens the Track Order page with the order id "INVALID-0000"
    Then the order-not-found error is displayed

  Scenario: Verify contact support is offered on the tracking page
    Given the user navigates to the Track Order page
    Then the tracking contact support link is available
