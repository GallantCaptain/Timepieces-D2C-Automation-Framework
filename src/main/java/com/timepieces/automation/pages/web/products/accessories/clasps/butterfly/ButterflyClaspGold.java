package com.timepieces.automation.pages.web.products.accessories.clasps.butterfly;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * ButterflyClaspGold: https://www.timepieces-d2c.example/accessories/clasp/butterfly-clasp-gold-18mm
 * The Gold (18mm) variant of the Butterfly Clasp family. Standard accessory PDP
 * shape (Add to cart, color card-button switcher that navigates, Description/
 * Technical accordions, Return/Guarantee side drawers, model carousel). Butterfly
 * Clasps pair with Leather straps (Teju Lizard, Ostrich & Suede). Confirmed
 * behavior lives in {@link SharedTemplate_ButterflyClasp} / {@code SharedAccessoryPdp}.
 *
 * <p>
 * SCRAPE NOTE (2026-07-15): enabled "Add to cart" CTA (purchasable). The color
 * card-buttons navigate to distinct PDPs; single size (18mm).
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (ButterflyClaspGold) to allow method
 * chaining in step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
 * @since 2026-07-03
 */

@Log4j2
public class ButterflyClaspGold extends SharedTemplate_ButterflyClasp<ButterflyClaspGold> {

    // ==========================================
    // PAGE CONSTANTS
    // ==========================================
    private static final String PAGE_PATH = "/accessories/clasp/butterfly-clasp-gold-18mm";
    // Distinguishing substring (contains-match); color word verified.
    private static final String PAGE_TITLE = "Butterfly Clasp, Gold";

    // ==========================================
    // Constructor
    // ==========================================
    public ButterflyClaspGold(WebDriver driver) {
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
}