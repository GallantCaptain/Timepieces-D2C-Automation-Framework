package com.timepieces.automation.pages.web.otherPages.timepieces_world;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * TimepiecesWorld_MonarchPage: https://world.timepieces-d2c.example/products/monarch
 *
 * The Monarch showcase page on the Timepieces World subdomain. All structure and
 * behavior (hero logo/tagline, ideation, calibre specs grid, learn-more
 * material buttons, waitlist form, footer) is inherited from
 * {@link SharedTemplate_TimepiecesWorldProduct} — the 2026-07-16 scrape confirmed
 * the 5 showcase pages share ONE template. This class supplies only the family
 * identity and its unique document title.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (TimepiecesWorld_MonarchPage) to allow method chaining in step
 * definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TimepiecesWorld_MonarchPage"
* @since 2026-07-03
 */

@Log4j2
public class TimepiecesWorld_MonarchPage extends SharedTemplate_TimepiecesWorldProduct<TimepiecesWorld_MonarchPage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Page Title Variable (unique per family: <Family> | <Tagline> | Timepieces Watches).
    private static final String PAGE_TITLE = "Monarch | A Moon Phase Watch Shaped by Roman Heritage | Timepieces Watches";

    // ==========================================
    // Constructor
    // ==========================================
    public TimepiecesWorld_MonarchPage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // FAMILY HOOK + PAGE METADATA
    // ==========================================
    @Override
    protected String familyName() {
        return "Monarch";
    }

    @Override
    protected String getPageTitle() {
        return PAGE_TITLE;
    }
}
