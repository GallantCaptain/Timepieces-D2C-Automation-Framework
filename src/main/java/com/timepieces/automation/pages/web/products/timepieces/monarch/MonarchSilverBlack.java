package com.timepieces.automation.pages.web.products.timepieces.monarch;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Monarch Silver Black: https://www.timepieces-d2c.example/products/monarch-silver-black
 * The Monarch Silver Black PDP page. This page contains details about the
 * Monarch Silver Black watch. It is a shared template for the Monarch family of
 * timepieces. The other watches can be navigated to in the PDP page with a card
 * to toggle between each. The page also has a sticky header and footer and
 * scroll-to-section functionality. The videos are on autoplay and for those
 * that don't support autoplay, they are muted by default. All videos have
 * volume controls to allow the user to unmute them. The page also has a
 * floating 'Add to Bag' button, that appears when the user scrolls down the
 * page, that scrolls back up to the 'Add to Bag' button in the Sticky
 * add to bag section, when clicked. The floating add to bag button is not to be
 * confused with the Sticky add to bag section that is present in the Product
 * Details section of the page. The Sticky add to bag section has multiple
 * interactive elements, like the ability to select a band color, the ability to
 * view the product in augmented reality and finally add the product to the
 * cart.
 * The AR feature is not implemented yet.
 * 
 * The Augmented Reality (AR) viewer is an embedded iframe. The product in the
 * AR viewer can be rotated by dragging the product in the iframe, which is made
 * possible by using the THREE.js library. Additionally, the AR viewer can be
 * expanded to fullscreen by clicking the fullscreen icon present in the
 * top-right corner of the AR viewer. The AR viewer also has an 'x' button,
 * to close the AR viewer, and a button to share the AR viewer, which opens
 * up a share modal. The AR viewer opens up in a modal, which dims the light
 * in the rest of the page to focus the user's attention on the AR viewer.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (MonarchSilverBlack)
 * to allow method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class MonarchSilverBlack extends SharedTemplate_Monarch {

    // ==========================================
    // Constructor
    // ==========================================
    public MonarchSilverBlack(WebDriver driver) {
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
