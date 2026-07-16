package com.timepieces.automation.pages.web.otherPages.timepieces_world;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * TimepiecesWorld_ArvionPage: https://world.timepieces-d2c.example/products/arvion
 *
 * The Arvion showcase page on the Timepieces World subdomain. All structure and
 * behavior (hero logo/tagline, ideation, calibre specs grid, learn-more
 * material buttons, waitlist form, footer) is inherited from
 * {@link SharedTemplate_TimepiecesWorldProduct} — the 2026-07-16 scrape confirmed
 * the 5 showcase pages share ONE template. This class supplies only the family
 * identity and its unique document title.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (TimepiecesWorld_ArvionPage) to allow method chaining in step
 * definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of TimepiecesWorld_ArvionPage"
* @since 2026-07-03
 */

@Log4j2
public class TimepiecesWorld_ArvionPage extends SharedTemplate_TimepiecesWorldProduct<TimepiecesWorld_ArvionPage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Page Title Variable (unique per family: <Family> | <Tagline> | Timepieces Watches).
    private static final String PAGE_TITLE = "Arvion | Get Early Access to Single-Hand Watch | Timepieces Watches";

    // ==========================================
    // Constructor
    // ==========================================
    public TimepiecesWorld_ArvionPage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // FAMILY HOOK + PAGE METADATA
    // ==========================================
    @Override
    protected String familyName() {
        return "Arvion";
    }

    @Override
    protected String getPageTitle() {
        return PAGE_TITLE;
    }
}
