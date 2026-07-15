package com.timepieces.automation.pages.web.products.accessories.clasps.singleDeployment;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Single Deployment Clasp Rose Gold — 22mm:
 * https://www.timepieces-d2c.example/accessories/clasp/single-deployment-clasp-rose-gold-22mm
 * The 22mm variant of the Rose Gold Single Deployment Clasp. Rose Gold is the
 * ONLY Single Deployment Clasp offered in two sizes (20mm + 22mm); the size
 * card-buttons NAVIGATE to distinct PDP URLs, so 20mm and 22mm are modelled as
 * two classes. Use {@link #selectSize20mm()} to jump to the sibling.
 *
 * <p>
 * SCRAPE NOTE (2026-07-15): shares the title "Single Deployment Clasp, Rose Gold"
 * with the 20mm page — only the URL distinguishes them, so title verification
 * cannot tell them apart. Price on the 22mm page was not separately captured.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (SingleDeploymentClaspRoseGold22mm) to allow method chaining in step
 * definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SingleDeploymentClaspRoseGold22mm"
* @since 2026-07-15
 */

@Log4j2
public class SingleDeploymentClaspRoseGold22mm
        extends SharedTemplate_SingleDeploymentClasp<SingleDeploymentClaspRoseGold22mm> {

    // ==========================================
    // PAGE CONSTANTS
    // ==========================================
    private static final String PAGE_PATH = "/accessories/clasp/single-deployment-clasp-rose-gold-22mm";
    // Shared with the 20mm sibling — only PAGE_PATH distinguishes the two.
    private static final String PAGE_TITLE = "Single Deployment Clasp, Rose Gold";

    // ==========================================
    // Constructor
    // ==========================================
    public SingleDeploymentClaspRoseGold22mm(WebDriver driver) {
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
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * Clicks the "20mm" size card-button, which navigates to the 20mm PDP.
     *
     * @return the sibling {@link SingleDeploymentClaspRoseGold20mm} page.
     */
    public SingleDeploymentClaspRoseGold20mm selectSize20mm() {
        selectSize("20mm");
        return new SingleDeploymentClaspRoseGold20mm(driver);
    }
}
