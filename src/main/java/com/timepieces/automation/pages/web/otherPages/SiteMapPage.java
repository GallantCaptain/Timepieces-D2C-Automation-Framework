package com.timepieces.automation.pages.web.otherPages;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The SiteMapPage: https://www.timepieces-d2c.example/sitemap has links for all
 * pages on the website. This page is meant for the search engine crawlers
 * to be able to crawl all the pages on the website. It has both header &
 * footer.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (SiteMapPage)
 * to allow method chaining in step definitions.
 * 
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class SiteMapPage extends StoreFrontBasePage<SiteMapPage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Page Path Variable
    private static final String PAGE_PATH = "/sitemap";

    // The Page Title Variable {Not Unique for Now}
    // private static final String PAGE_TITLE = "Sitemap | Timepieces";

    // ==========================================
    // Constructor
    // ==========================================
    public SiteMapPage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // PAGE METADATA (drives the inherited fluent open())
    // ==========================================
    @Override
    protected String getPagePath() {
        return PAGE_PATH;
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
