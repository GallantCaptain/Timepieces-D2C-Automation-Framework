package com.timepieces.automation.pages.web.products.timepieces.astoniaSports;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.web.products.timepieces.SharedTimepiecePdp;

import lombok.extern.log4j.Log4j2;

/**
 * Shared template for the Astonia Sports timepiece PDP pages. All shared PDP
 * behavior lives in {@link SharedTimepiecePdp}; Astonia Sports only supplies its
 * family hook.
 *
 * <p>
 * ASTONIA SPORTS SHAPE (confirmed by the 2026-07-15 scrape): lume ✔, 3D ✔
 * (canvas {@code aria-label='Astonia Sports'}), enabled "Register Interest" CTA.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_AstoniaSports"
* @since 2026-07-03
 */

@Log4j2
public abstract class SharedTemplate_AstoniaSports<SELF extends SharedTemplate_AstoniaSports<SELF>> extends SharedTimepiecePdp<SELF> {

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_AstoniaSports(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // FAMILY HOOKS
    // ==========================================
    @Override
    protected String familyName() {
        return "Astonia Sports";
    }
}
