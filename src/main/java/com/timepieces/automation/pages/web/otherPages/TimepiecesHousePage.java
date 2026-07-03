package com.timepieces.automation.pages.web.otherPages;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.HeaderOnlyBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The TimepiecesHousePage: https://www.timepieces-d2c.example/experience-center is meant to
 * direct the users to the physical stores of Timepieces in 'Delhi' & 'Gurugram'
 * only for Q3 2026. It provides the google maps links and address for the
 * users to be able to visit the stores and have a direct experience and
 * consultations with the Timepieces Official Concierge. They will be able to
 * touch and see the timepieces in person. It is meant to the elevate the
 * premium watch experience.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (TimepiecesHousePage)
 * to allow method chaining in step definitions.
 * 
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TimepiecesHousePage"
 */

@Log4j2
public class TimepiecesHousePage extends HeaderOnlyBasePage<TimepiecesHousePage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Page Path Variable
    private static final String PAGE_PATH = "/experience-center";

    // The Page Title Variable {Keep whether Unique or Not}
    // Decide if it needs to be added to assertions if not unique
    private static final String PAGE_TITLE = "Timepieces Watches | Early Access to Limited Edition Pieces";

    // ==========================================
    // Constructor
    // ==========================================
    public TimepiecesHousePage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // PAGE METADATA (drives the inherited fluent open())
    // ==========================================
    @Override
    protected String getPagePath() {
        return PAGE_PATH;
    }

    @Override
    protected String getPageTitle() {
        return PAGE_TITLE;
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
