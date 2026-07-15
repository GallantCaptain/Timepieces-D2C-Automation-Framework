package com.timepieces.automation.pages.web.products.accessories.clasps.singleDeployment;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Single Deployment Clasp Rose Gold — 20mm:
 * https://www.timepieces-d2c.example/accessories/clasp/single-deployment-clasp-rose-gold-20mm
 * The 20mm variant of the Rose Gold Single Deployment Clasp. Rose Gold is the
 * ONLY Single Deployment Clasp offered in two sizes (20mm + 22mm); the size
 * card-buttons NAVIGATE to distinct PDP URLs (confirmed 2026-07-15), so 20mm and
 * 22mm are modelled as two classes. Use {@link #selectSize22mm()} to jump to the
 * sibling.
 *
 * <p>
 * SCRAPE NOTE (2026-07-15): title "Single Deployment Clasp, Rose Gold" (₹4,500),
 * enabled "Add to cart". The 20mm and 22mm pages share this same title — only the
 * URL distinguishes them, so title verification cannot tell them apart.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (SingleDeploymentClaspRoseGold20mm) to allow method chaining in step
 * definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SingleDeploymentClaspRoseGold20mm"
* @since 2026-07-15
 */

@Log4j2
public class SingleDeploymentClaspRoseGold20mm
        extends SharedTemplate_SingleDeploymentClasp<SingleDeploymentClaspRoseGold20mm> {

    // ==========================================
    // PAGE CONSTANTS
    // ==========================================
    private static final String PAGE_PATH = "/accessories/clasp/single-deployment-clasp-rose-gold-20mm";
    // Shared with the 22mm sibling — only PAGE_PATH distinguishes the two.
    private static final String PAGE_TITLE = "Single Deployment Clasp, Rose Gold";

    // ==========================================
    // Constructor
    // ==========================================
    public SingleDeploymentClaspRoseGold20mm(WebDriver driver) {
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
     * Clicks the "22mm" size card-button, which navigates to the 22mm PDP.
     *
     * @return the sibling {@link SingleDeploymentClaspRoseGold22mm} page.
     */
    public SingleDeploymentClaspRoseGold22mm selectSize22mm() {
        selectSize("22mm");
        return new SingleDeploymentClaspRoseGold22mm(driver);
    }
}
