package com.timepieces.automation.pages.web.products.timepieces.monarch;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.web.products.timepieces.SharedTimepiecePdp;

import lombok.extern.log4j.Log4j2;

/**
 * Shared template for the Monarch timepiece PDP pages. All shared PDP behavior
 * (title/price/gallery, "Register Interest" CTA + form, floating CTA, share,
 * wishlist, lume, 3D viewer, spec accordion, sibling nav, owners modal, book
 * consultation, FAQ, background videos) lives in {@link SharedTimepiecePdp};
 * Monarch only supplies its family hook.
 *
 * <p>
 * MONARCH SHAPE (confirmed by the 2026-07-15 scrape): lume ✔, 3D ✔ (canvas
 * {@code aria-label='Monarch'}), enabled "Register Interest" CTA.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_Monarch"
* @since 2026-07-03
 */

@Log4j2
public abstract class SharedTemplate_Monarch<SELF extends SharedTemplate_Monarch<SELF>> extends SharedTimepiecePdp<SELF> {

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_Monarch(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // FAMILY HOOKS
    // ==========================================
    @Override
    protected String familyName() {
        return "Monarch";
    }
}
