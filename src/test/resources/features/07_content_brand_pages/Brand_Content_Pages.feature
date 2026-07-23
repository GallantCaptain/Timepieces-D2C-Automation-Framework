@Regression @ContentPages @ProdReady
Feature: Brand Content Pages
  As a customer
  I want to browse the Timepieces brand content pages (Wall, Share Your Moment, Sitemap, Updates)
  So that I can explore the community wall, submit moments, index the site and read news

  Scenario: Verify the Timepieces Wall renders the WebGL testimonial wall with music control
    Given the user navigates to the Timepieces Wall page
    Then the wall canvas container is displayed
    And the background music toggle is displayed
    # Asserts the toggle FLIPS, not that music ends up playing. The wall AUTOPLAYS
    # (verified 2026-07-17: the control starts at "Mute background music"), so one click
    # MUTES it — the old "Then the background music is playing" could never pass. Checking
    # the flip is both the honest assertion and the one that survives autoplay changing.
    When the user toggles the background music
    Then the background music state is flipped

  Scenario: Verify Share Your Moment gates guest submissions behind sign-in
    Given the user navigates to the Share Your Moment page
    Then the "Show your Timepieces moment" heading is displayed
    And the watch-photo carousel is displayed
    When the user clicks the gated "Share your moment" button
    Then the sign-in gate modal is displayed
    And the gate modal explains that only Timepieces owners can submit
    When the user closes the gate modal
    Then the sign-in gate modal is hidden

  Scenario: Verify the Sitemap indexes the storefront
    Given the user navigates to the Sitemap page
    Then the sitemap main heading is displayed
    And the sitemap has the section "Main Pages"
    And the sitemap has the section "Collections"
    And the sitemap has the section "Products"
    And the sitemap contains links to "/products/" pages
    And the sitemap contains links to "/accessories/" pages
    And the sitemap lists at least 40 links

  Scenario: Verify the Updates page filters news content
    Given the user navigates to the Updates page
    Then the "Timepieces Updates" heading is displayed
    And the content filter "All" is available
    And the content filter "Drops & Releases" is available
    And the content filter "Delivery Updates" is available
    And the content filter "Community" is available
    And the content filter "Production & Quality" is available
    When the user selects the content filter "Drops & Releases"
    Then update cards are displayed
