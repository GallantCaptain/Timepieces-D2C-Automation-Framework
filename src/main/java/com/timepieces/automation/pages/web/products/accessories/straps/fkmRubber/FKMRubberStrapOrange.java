package com.timepieces.automation.pages.web.products.accessories.straps.fkmRubber;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * FKMRubber Strap Orange:
 * https://www.timepieces-d2c.example/accessories/strap/fkm-rubber-strap-orange
 * The FKMRubber Strap Orange PDP page. This page contains details about
 * the FKMRubber Strap Orange. It is a shared template for the FKMRubber
 * Strap family of accessories. The other clasps can be navigated to
 * in the PDP page with a card to toggle between each. The page also has a
 * sticky header and footer and scroll-to-section functionality. There are
 * Accordion panels that with accordion toggles to hide/show the product
 * details.
 * There are feature/category card-buttons to switch between the different
 * strap variants based on the model color. Clicking on the Return & Refund or
 * the Guarantee links opens a side drawer with the full details. The side
 * drawer can be closed by clicking on the anywhere outside the drawer or on the
 * 'X' button in the top-right corner of the drawer. The Product Details
 * accordion is collapsed by default and needs to be toggled open to view the
 * product details.
 * There is a variant carousel for the user to try the different dials on the
 * current strap so that the user can visually experience how the watch will
 * look with the different dials & strap combinations. This feature is only
 * available for the straps family of accessories. Furthermore, only compatible
 * straps and their watch dials are displayed in the variant carousel. The
 * watch dials can be dragged across the track to view them with the strap.
 * At the bottom, the there is a carousel of the all the straps
 * models and variants in a scrollable card list. Clicking on any of the cards
 * takes the user to that pdp.
 * The straps are in three categories: Rubber, Hyrbid, and Leather Straps.
 * 
 * Compatiblity List:
 * FKM Rubber Strap (Yellow, Orange, Black, & White) -> Astonia & Astonia Sports
 * Hybrid Leather Strap (Blue & Black) -> Auriqua
 * Ostrich Leather Strap (Brown) -> Monarch & Arvion
 * Ostrich Leather Strap (Black) -> Manifesta, Arvion, & Monarch
 * Teju Lizard Leather Strap (Black & Blue) -> Manifesta, Arvion, & Monarch
 * Suede Leather Strap (Burgundy & Brown) -> Arvion
 * Suede Leather Strap (Blue) -> Arvion & Manifesta
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (FKMRubberStrapOrange) to allow method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class FKMRubberStrapOrange extends SharedTemplate_FKMRubberStrap<FKMRubberStrapOrange> {

    // ==========================================
    // Constructor
    // ==========================================
    public FKMRubberStrapOrange(WebDriver driver) {
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
