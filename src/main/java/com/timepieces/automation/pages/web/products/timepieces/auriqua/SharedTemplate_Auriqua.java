package com.timepieces.automation.pages.web.products.timepieces.auriqua;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.web.products.timepieces.SharedTimepiecePdp;

import lombok.extern.log4j.Log4j2;

/**
 * Shared template for the Auriqua timepiece PDP pages. All shared PDP behavior
 * lives in {@link SharedTimepiecePdp}; Auriqua supplies its family hook and
 * overrides {@link #has3DViewer()} to false.
 *
 * <p>
 * AURIQUA SHAPE (confirmed by the 2026-07-15 scrape): lume ✔ but <b>NO 3D
 * viewer</b> (the Open-3D button/canvas are absent on all three variants),
 * enabled "Register Interest" CTA.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_Auriqua"
* @since 2026-07-03
 */

@Log4j2
public abstract class SharedTemplate_Auriqua<SELF extends SharedTemplate_Auriqua<SELF>> extends SharedTimepiecePdp<SELF> {

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_Auriqua(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // FAMILY HOOKS
    // ==========================================
    @Override
    protected String familyName() {
        return "Auriqua";
    }

    /** Auriqua has no 3D viewer (confirmed absent on all variants). */
    @Override
    public boolean has3DViewer() {
        return false;
    }
}
