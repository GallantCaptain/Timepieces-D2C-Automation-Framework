package com.timepieces.automation.pages.web.products.accessories.straps.ostrichLeather;

import com.timepieces.automation.pages.web.products.accessories.straps.SharedStrapPdp;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Shared template for the Ostrich Leather Strap PDP pages. All shared accessory +
 * strap behavior lives in {@code SharedStrapPdp} / {@code SharedAccessoryPdp}.
 * <p>
 * Ostrich Leather Straps (Black / Brown) are compatible with the Monarch, Arvion
 * and Manifesta timepieces (Brown pairs with Monarch and Arvion; Black
 * additionally pairs with Manifesta) and pair with Butterfly Clasps. The color
 * card-buttons NAVIGATE to distinct PDP URLs, so each color is its own class.
 *
 * <p>
 * TITLE: family-level {@code PAGE_TITLE} substring ("Ostrich Leather Watch Strap",
 * verified) shared by all colors — per-color identity is the URL/path.
 *
 * GENERIC SELF-TYPE (CRTP): each variant binds itself, e.g.
 * {@code class OstrichLeatherStrapBlack extends SharedTemplate_OstrichLeatherStrap<OstrichLeatherStrapBlack>}.
 *
 * @param <SELF> the concrete Ostrich Leather Strap variant subtype
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_OstrichLeatherStrap"
 */

@Log4j2
public abstract class SharedTemplate_OstrichLeatherStrap<SELF extends SharedTemplate_OstrichLeatherStrap<SELF>>
        extends SharedStrapPdp<SELF> {

    // Family-level title substring (contains-match), shared by all colors.
    private static final String PAGE_TITLE = "Ostrich Leather Watch Strap";

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_OstrichLeatherStrap(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getPageTitle() {
        return PAGE_TITLE;
    }
}
