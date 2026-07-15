package com.timepieces.automation.pages.web.products.accessories.straps.suedeLeather;

import com.timepieces.automation.pages.web.products.accessories.straps.SharedStrapPdp;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Shared template for the Suede Leather Strap PDP pages. All shared accessory +
 * strap behavior lives in {@code SharedStrapPdp} / {@code SharedAccessoryPdp}.
 * <p>
 * Suede Leather Straps (Blue / Brown / Burgundy) are compatible with the Arvion
 * and Manifesta timepieces (Burgundy and Brown pair with Arvion; Blue
 * additionally pairs with Manifesta) and pair with Butterfly Clasps. The color
 * card-buttons NAVIGATE to distinct PDP URLs, so each color is its own class.
 *
 * <p>
 * TITLE: family-level {@code PAGE_TITLE} substring ("Suede Leather Watch Strap",
 * inferred from the verified strap title pattern) shared by all colors — per-color
 * identity is the URL/path.
 *
 * GENERIC SELF-TYPE (CRTP): each variant binds itself, e.g.
 * {@code class SuedeLeatherStrapBlue extends SharedTemplate_SuedeLeatherStrap<SuedeLeatherStrapBlue>}.
 *
 * @param <SELF> the concrete Suede Leather Strap variant subtype
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_SuedeLeatherStrap"
 */

@Log4j2
public abstract class SharedTemplate_SuedeLeatherStrap<SELF extends SharedTemplate_SuedeLeatherStrap<SELF>>
        extends SharedStrapPdp<SELF> {

    // Family-level title substring (contains-match), shared by all colors. INFERRED.
    private static final String PAGE_TITLE = "Suede Leather Watch Strap";

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_SuedeLeatherStrap(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getPageTitle() {
        return PAGE_TITLE;
    }
}
