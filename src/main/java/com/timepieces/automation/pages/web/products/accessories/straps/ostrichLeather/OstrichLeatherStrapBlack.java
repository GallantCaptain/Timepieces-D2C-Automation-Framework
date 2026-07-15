package com.timepieces.automation.pages.web.products.accessories.straps.ostrichLeather;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * OstrichLeatherStrapBlack: https://www.timepieces-d2c.example/accessories/strap/ostrich-leather-strap-black
 * A Black variant of the Ostrich Leather Strap family. Standard accessory + strap PDP shape
 * (Add to cart, Description/Technical accordions, Return/Guarantee side drawers,
 * model carousel, dial-try carousel, clasp-upgrade cross-sell). Confirmed
 * behavior lives in {@link SharedTemplate_OstrichLeatherStrap} / {@code SharedStrapPdp} / {@code SharedAccessoryPdp}.
 *
 * <p>
 * SCRAPE NOTE (2026-07-15): enabled "Add to cart" CTA (purchasable); the color
 * card-buttons navigate to distinct PDPs. Title verification is family-level (see
 * the family template); the per-color identity is the URL/path.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (OstrichLeatherStrapBlack) to allow method
 * chaining in step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
 * @since 2026-07-15
 */

@Log4j2
public class OstrichLeatherStrapBlack extends SharedTemplate_OstrichLeatherStrap<OstrichLeatherStrapBlack> {

    // ==========================================
    // PAGE CONSTANTS
    // ==========================================
    private static final String PAGE_PATH = "/accessories/strap/ostrich-leather-strap-black";

    // ==========================================
    // Constructor
    // ==========================================
    public OstrichLeatherStrapBlack(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // PAGE METADATA (drives the inherited fluent open())
    // ==========================================
    @Override
    protected String getPagePath() {
        return PAGE_PATH;
    }
}