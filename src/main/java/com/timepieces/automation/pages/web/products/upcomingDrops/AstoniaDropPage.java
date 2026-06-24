package com.timepieces.automation.pages.web.products.upcomingDrops;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * This is the upcoming Astonia Product Drop Page, the product launches in
 * August 2026. The sale window will probably go live by July-end.
 * URL: https://www.timepieces-d2c.example/new-release/astonia
 * Currently has the new Astonia product image, the new Timepieces Sports
 * video, the watch heart (model animation), watch components animation, watch
 * details sections, and form at the botton to sign up for the waitlist, so that
 * user gets added to the watch waitlist and get access when the product
 * launches.
 * The user can be redirected to this page via Astonia PDP pages (still to be
 * mapped), where there is a static card that takes them to the upcoming Astonia
 * drops page.
 * Users can also navigate to this page by clicking on the
 * AstoniaDropCardComponent as well as the Discover button in the Homepage
 * (still to be mapped).
 */
@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public class AstoniaDropPage extends StoreFrontBasePage {
    // ==========================================
    // Constructor
    // ==========================================
    public AstoniaDropPage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Astonia Product Model Canvas - Watch animation that moves down to up to
    // showcase the product
    // Will replace the locator with a better data-testid when it is implemented.

    private final By astoniaWatchDropCanvas = By.cssSelector("canvas[aria-label='Astonia chronograph']");

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
