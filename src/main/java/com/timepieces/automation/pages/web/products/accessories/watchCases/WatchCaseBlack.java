package com.timepieces.automation.pages.web.products.accessories.watchCases;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Premium Watch Case Black:
 * https://www.timepieces-d2c.example/accessories/watch-case/premium-watch-case
 * The Premium Watch Case Black PDP page. This page contains details about
 * the Premium Watch Case Black. It is a variant of the Premium Watch
 * Case family of accessories. The other watch cases can be navigated to
 * in the PDP page with a card to toggle between each. The page also has a
 * sticky header and footer and scroll-to-section functionality. There are
 * Accordion panels that with accordion toggles to hide/show the product
 * details.
 * There are feature/category card-buttons to switch between the different
 * watch cases based on the model color {Feature not yet implemented}.
 * Clicking on the Return & Refund or the Guarantee links opens a side drawer
 * with the full details. The side drawer can be closed by clicking on the
 * anywhere outside the drawer or on the 'X' button in the top-right corner of
 * the drawer. The Product Details accordion is collapsed by default and needs
 * to be toggled open to view the product details. Clicking on any of the cards
 * takes the user to that pdp. The watch case is only in Black color as of now.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (PremiumWatchCaseBlack) to allow method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class WatchCaseBlack extends SharedTemplate_WatchCase<WatchCaseBlack> {

    // ==========================================
    // Constructor
    // ==========================================
    public WatchCaseBlack(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================

}
