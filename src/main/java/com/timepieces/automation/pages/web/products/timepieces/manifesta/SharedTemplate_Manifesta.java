package com.timepieces.automation.pages.web.products.timepieces.manifesta;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.web.products.timepieces.SharedTimepiecePdp;

import lombok.extern.log4j.Log4j2;

/**
 * Shared template for the Manifesta timepiece PDP pages. All shared PDP behavior
 * lives in {@link SharedTimepiecePdp}; Manifesta supplies its family hook and
 * overrides {@link #hasLume()} to false.
 *
 * <p>
 * MANIFESTA SHAPE (confirmed by the 2026-07-15 scrape): the DIVERGENT family —
 * <b>NO lume</b> toggle, but it DOES have the 3D viewer (canvas
 * {@code aria-label='Manifesta'}). All three variants are live (not a locked
 * drop); enabled "Register Interest" CTA that opens the register-interest form.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_Manifesta"
* @since 2026-07-03
 */

@Log4j2
public abstract class SharedTemplate_Manifesta<SELF extends SharedTemplate_Manifesta<SELF>> extends SharedTimepiecePdp<SELF> {

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_Manifesta(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // FAMILY HOOKS
    // ==========================================
    @Override
    protected String familyName() {
        return "Manifesta";
    }

    /** Manifesta does not have the lume toggle (confirmed absent). */
    @Override
    public boolean hasLume() {
        return false;
    }
}
