package com.timepieces.automation.pages.web.otherPages;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * TimepiecesWallPage: https://www.timepieces-d2c.example/timepieces-wall/ showcases the
 * various user testimonials and photos in an infinite scroll canvas
 * with most of the testimonials and photos in a polaroid style container
 * and some photos without containers if they have been shared without any
 * testimonials. The background is black with a black rotating globe with
 * white lines of longitude and latitude giving the impression of a
 * rotating globe animation.
 * There is a button to toggle the music on/off.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (TheWallPage)
 * to allow method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class TimepiecesWallPage extends StoreFrontBasePage {

    // ==========================================
    // Constructor
    // ==========================================
    public TimepiecesWallPage(WebDriver driver) {
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
