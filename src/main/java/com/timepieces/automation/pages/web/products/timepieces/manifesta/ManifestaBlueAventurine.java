package com.timepieces.automation.pages.web.products.timepieces.manifesta;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Manifesta Blue Aventurine:
 * https://www.timepieces-d2c.example/products/manifesta-blue-aventurine
 * The Manifesta Blue Aventurine PDP page. This page contains details about the
 * Manifesta Blue Aventurine watch. It is a variant of the Manifesta
 * family of timepieces. The other watches can be navigated to in the PDP page
 * with a card to toggle between each. The page also has a sticky header and
 * footer and scroll-to-section functionality. The videos are on autoplay and
 * for those that support 'loom' functionality like Arvion, Astonia,
 * Astonia Sports, and Monarch, i.e., all except Manifesta. There is a 3D
 * view toggle on the top-most product image showcase that opens the 3D viewer
 * canvas and is used to view the product models in better detail in 3D. All
 * product except Astonia Trophy Gold has the 3D viewer toggle. The Manifesta
 * products do not have 'loom' functionality so far. There is also a planned
 * AR functionality 'tbd' sometime around the end of the year.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (ManifestaBlueAventurine) to allow method chaining in step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class ManifestaBlueAventurine extends SharedTemplate_Manifesta<ManifestaBlueAventurine> {

    // ==========================================
    // Constructor
    // ==========================================
    public ManifestaBlueAventurine(WebDriver driver) {
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
