package com.timepieces.automation.pages.web.products.accessories.straps.fkmRubber;

import com.timepieces.automation.pages.web.products.accessories.straps.SharedStrapPdp;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Shared template for the FKM Rubber Strap PDP pages. All shared accessory + strap
 * behavior (Add to cart, wishlist, gallery, color card-button switcher,
 * Description/Technical accordions, Return/Guarantee side drawers, model carousel,
 * dial-try carousel, clasp-upgrade cross-sell) lives in {@code SharedStrapPdp} /
 * {@code SharedAccessoryPdp}.
 * <p>
 * FKM Rubber Straps (Black / Orange / White / Yellow) are compatible with the
 * Astonia and Astonia Sports timepieces, and pair with Single Deployment Clasps.
 * The color card-buttons NAVIGATE to distinct PDP URLs, so each color is its own
 * class.
 *
 * <p>
 * TITLE: this family supplies a family-level {@code PAGE_TITLE} substring
 * ("FKM Rubber Watch Strap", verified) shared by every color — the per-color
 * identity is the URL/path (and the {@code <h1>} via {@code getProductTitleText()}),
 * because the browser titles vary/carry typos (e.g. Black's title reads "…in Bkack").
 *
 * GENERIC SELF-TYPE (CRTP): each variant binds itself, e.g.
 * {@code class FKMRubberStrapBlack extends SharedTemplate_FKMRubberStrap<FKMRubberStrapBlack>}.
 *
 * @param <SELF> the concrete FKM Rubber Strap variant subtype
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_FKMRubberStrap"
 */

@Log4j2
public abstract class SharedTemplate_FKMRubberStrap<SELF extends SharedTemplate_FKMRubberStrap<SELF>>
        extends SharedStrapPdp<SELF> {

    // Family-level title substring (contains-match), shared by all colors.
    private static final String PAGE_TITLE = "FKM Rubber Watch Strap";

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_FKMRubberStrap(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getPageTitle() {
        return PAGE_TITLE;
    }
}
