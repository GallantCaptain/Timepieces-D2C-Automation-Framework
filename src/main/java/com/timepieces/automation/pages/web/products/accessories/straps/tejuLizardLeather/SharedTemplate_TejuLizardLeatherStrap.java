package com.timepieces.automation.pages.web.products.accessories.straps.tejuLizardLeather;

import com.timepieces.automation.pages.web.products.accessories.straps.SharedStrapPdp;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Shared template for the Teju Lizard Leather Strap PDP pages. All shared
 * accessory + strap behavior lives in {@code SharedStrapPdp} /
 * {@code SharedAccessoryPdp}.
 * <p>
 * Teju Lizard Leather Straps (Black / Blue) are compatible with the Manifesta,
 * Arvion and Monarch timepieces and pair with Butterfly Clasps. The color
 * card-buttons NAVIGATE to distinct PDP URLs, so each color is its own class.
 *
 * <p>
 * TITLE: family-level {@code PAGE_TITLE} substring ("Teju Lizard Leather Watch
 * Strap", inferred from the verified strap title pattern) shared by all colors —
 * per-color identity is the URL/path.
 *
 * GENERIC SELF-TYPE (CRTP): each variant binds itself, e.g.
 * {@code class TejuLizardLeatherStrapBlack extends SharedTemplate_TejuLizardLeatherStrap<TejuLizardLeatherStrapBlack>}.
 *
 * @param <SELF> the concrete Teju Lizard Leather Strap variant subtype
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_TejuLizardLeatherStrap"
 */

@Log4j2
public abstract class SharedTemplate_TejuLizardLeatherStrap<SELF extends SharedTemplate_TejuLizardLeatherStrap<SELF>>
        extends SharedStrapPdp<SELF> {

    // Family-level title substring (contains-match), shared by all colors. INFERRED.
    private static final String PAGE_TITLE = "Teju Lizard Leather Watch Strap";

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_TejuLizardLeatherStrap(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getPageTitle() {
        return PAGE_TITLE;
    }
}
