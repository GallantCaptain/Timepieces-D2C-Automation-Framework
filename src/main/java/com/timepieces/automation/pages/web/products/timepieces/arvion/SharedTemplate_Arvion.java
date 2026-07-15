package com.timepieces.automation.pages.web.products.timepieces.arvion;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.web.products.timepieces.SharedTimepiecePdp;

import lombok.extern.log4j.Log4j2;

/**
 * Shared template for the Arvion timepiece PDP pages. All shared PDP behavior
 * lives in {@link SharedTimepiecePdp}; Arvion only supplies its family hook.
 *
 * <p>
 * ARVION SHAPE (confirmed by the 2026-07-15 scrape): lume ✔, 3D ✔ (canvas
 * {@code aria-label='Arvion'}), enabled "Register Interest" CTA.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_Arvion"
* @since 2026-07-03
 */

@Log4j2
public abstract class SharedTemplate_Arvion<SELF extends SharedTemplate_Arvion<SELF>> extends SharedTimepiecePdp<SELF> {

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_Arvion(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // FAMILY HOOKS
    // ==========================================
    @Override
    protected String familyName() {
        return "Arvion";
    }
}
