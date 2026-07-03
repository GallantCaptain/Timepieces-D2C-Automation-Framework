package com.timepieces.automation.pages.web.products.accessories.clasps.singleDeployment;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Single Deployment Clasp Rose Gold:
 * https://www.timepieces-d2c.example/accessories/clasp/single-deployment-clasp-rose-gold-20mm
 * or
 * https://www.timepieces-d2c.example/accessories/clasp/single-deployment-clasp-rose-gold-22mm
 * The Single Deployment Clasp Rose Gold PDP page. This page contains details
 * about the Single Deployment Clasp Rose Gold. It is a variant of the
 * Single Deployment Clasp family of accessories. The other clasps can be
 * navigated to in the PDP page with a card to toggle between each. The page
 * also has a sticky header and footer and scroll-to-section functionality.
 * There are Accordion panels that with accordion toggles to hide/show the
 * product details.
 * There are feature/category card-buttons to switch between the different
 * clasp variants based on the model color and in some cases the size. For now
 * the clasp size variant is available only for Single Deployment Rose Gold
 * clasp (choose between the 20mm or 22mm variants). All other Single Deployment
 * Clasps offer only one size option, 20mm. Butterfly Clasps also offer only
 * one size option, 18mm. Clicking on the Return & Refund or the Guarantee
 * links opens a side drawer with the full details. The side drawer can be
 * closed by clicking on the anywhere outside the drawer or on the 'X' button in
 * the top-right corner of the drawer. At the bottom, the there is a carousel of
 * the all the clasps models and variants in a scrollable card list. Clicking on
 * any of the cards takes the user to that pdp.
 * The clasps are of two types: Butterfly, and Single Deployment Clasps.
 * 
 * Compatiblity List:
 * Butterfly Clasp: Leather Straps (Teju Lizard, Ostrich & Suede Leather Straps)
 * Single Deployment Clasp: Hybrid & Rubber Straps (Hybrid & FKM Rubber Straps)
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (SingleDeploymentClaspRoseGold) to allow method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class SingleDeploymentClaspRoseGold extends SharedTemplate_SingleDeploymentClasp<SingleDeploymentClaspRoseGold> {

    // ==========================================
    // Constructor
    // ==========================================
    public SingleDeploymentClaspRoseGold(WebDriver driver) {
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
