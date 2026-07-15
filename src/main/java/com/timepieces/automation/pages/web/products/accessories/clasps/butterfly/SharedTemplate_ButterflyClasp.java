package com.timepieces.automation.pages.web.products.accessories.clasps.butterfly;

import com.timepieces.automation.pages.web.products.accessories.SharedAccessoryPdp;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Shared template for the Butterfly Clasp accessory PDP pages. All shared
 * accessory PDP behavior lives in {@link SharedAccessoryPdp}; this thin subclass
 * groups the Butterfly Clasp variants (Gold / Rose Gold / Steel).
 *
 * <p>
 * VARIANT MODEL: Butterfly Clasps are single-size (18mm); the COLOR card-buttons
 * NAVIGATE to distinct PDP URLs (confirmed 2026-07-15), so each color is its own
 * class. Butterfly Clasps pair with Leather straps (Teju Lizard, Ostrich &amp;
 * Suede).
 *
 * GENERIC SELF-TYPE (CRTP): each variant binds itself, e.g.
 * {@code class ButterflyClaspGold extends SharedTemplate_ButterflyClasp<ButterflyClaspGold>}.
 *
 * @param <SELF> the concrete Butterfly Clasp variant subtype
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_ButterflyClasp"
 */

@Log4j2
public abstract class SharedTemplate_ButterflyClasp<SELF extends SharedTemplate_ButterflyClasp<SELF>>
        extends SharedAccessoryPdp<SELF> {

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_ButterflyClasp(WebDriver driver) {
        super(driver);
    }
}
