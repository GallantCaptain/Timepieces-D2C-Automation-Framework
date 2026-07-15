@Regression @Accessories
Feature: Accessory Product Detail Page
  As a customer
  I want to browse accessory product detail pages (watch cases, clasps, straps)
  So that I can review a purchasable accessory, switch variants, and add it to my cart

  Background:
    Given the user bypasses the staging password gate
    And the user is on the storefront homepage

  Scenario: Verify the accessory PDP shows the purchasable core details
    Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"
    Then the accessory product title is displayed
    And the accessory price is displayed
    And the accessory compatibility caption is displayed
    And the "Add to cart" button is displayed and enabled

  Scenario: Verify a strap accessory can be added to the cart
    Given the user navigates to the accessory PDP of "fkm-rubber-strap-black"
    When the user clicks the "Add to cart" button on the accessory PDP
    Then the slide-out cart drawer is displayed
    And the cart badge count is updated to "1"
    And the cart drawer displays the product "fkm-rubber-strap-black" with its correct title and price

  Scenario: Verify the color card-button switcher navigates to a distinct accessory PDP
    Given the user navigates to the accessory PDP of "ostrich-leather-strap-black"
    When the user selects the color variant that links to "ostrich-leather-strap-brown"
    Then the browser navigates to the accessory PDP "ostrich-leather-strap-brown"

  Scenario: Verify the Rose Gold clasp size switcher navigates between 20mm and 22mm
    Given the user navigates to the accessory PDP of "single-deployment-clasp-rose-gold-20mm"
    When the user selects the "22mm" size on the clasp PDP
    Then the browser navigates to the accessory PDP "single-deployment-clasp-rose-gold-22mm"
    When the user selects the "20mm" size on the clasp PDP
    Then the browser navigates to the accessory PDP "single-deployment-clasp-rose-gold-20mm"

  Scenario Outline: Verify the product-detail accordions expand
    Given the user navigates to the accessory PDP of "premium-watch-case"
    When the user expands the "<section>" accordion
    Then the "<section>" accordion content is visible

    Examples:
      | section           |
      | Description       |
      | Technical details |

  Scenario: Verify the Guarantee side drawer opens and closes
    Given the user navigates to the accessory PDP of "butterfly-clasp-gold-18mm"
    When the user opens the Guarantee side drawer
    Then the side drawer is displayed
    When the user closes the side drawer using the close button
    Then the side drawer is hidden

  Scenario: Verify the Return and Refund side drawer closes on backdrop click
    Given the user navigates to the accessory PDP of "butterfly-clasp-gold-18mm"
    When the user opens the Return and Refund side drawer
    Then the side drawer is displayed
    When the user clicks outside the side drawer on the backdrop
    Then the side drawer is hidden

  Scenario: Verify wishlisting an accessory toggles its state
    Given the user navigates to the accessory PDP of "hybrid-leather-strap-blue"
    When the user clicks the wishlist button on the accessory PDP
    Then the accessory is marked as wishlisted

  Scenario: Verify the bottom recommended-models carousel links to other accessory PDPs
    Given the user navigates to the accessory PDP of "suede-leather-strap-brown"
    Then the recommended-models carousel is displayed
    And every recommended-model card links to an "/accessories/" product page
