package com.timepieces.automation.pages.web.products.accessories.watchCases;

import com.timepieces.automation.pages.web.products.accessories.SharedAccessoryPdp;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Shared template for the Watch Case accessory PDP pages. All shared accessory
 * PDP behavior (title/price/compat, "Add to cart", wishlist, gallery, variant
 * switcher, accordions, Return/Guarantee drawers, model carousel) lives in
 * {@link SharedAccessoryPdp}; this thin subclass exists so the watch-case family
 * can grow (only "Black" exists today).
 *
 * <p>
 * WATCH CASE SHAPE (2026-07-15 discovery scrape): standard accessory PDP, single
 * variant. The color card-button switcher is present but effectively a no-op
 * (one option). Compatibility caption reads "Suitable for all watch sizes".
 *
 * GENERIC SELF-TYPE (CRTP): parameterised with a recursive self-type so the
 * inherited fluent {@code open()} returns the concrete variant page, e.g.
 * {@code class PremiumWatchCaseBlack extends SharedTemplate_WatchCase<PremiumWatchCaseBlack>}.
 *
 * @param <SELF> the concrete Watch Case variant subtype
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_WatchCase"
 */

@Log4j2
public abstract class SharedTemplate_WatchCase<SELF extends SharedTemplate_WatchCase<SELF>>
        extends SharedAccessoryPdp<SELF> {

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_WatchCase(WebDriver driver) {
        super(driver);
    }
}
