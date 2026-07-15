package com.timepieces.automation.pages.web.products.timepieces.astonia;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.web.products.timepieces.SharedTimepiecePdp;

import lombok.extern.log4j.Log4j2;

/**
 * Shared template for the Astonia timepiece PDP pages. All shared PDP behavior
 * lives in {@link SharedTimepiecePdp}; Astonia (the Phase-0 pilot) supplies its
 * family hook and overrides the two markup differences it has from the newer
 * family pages.
 *
 * <p>
 * ASTONIA SHAPE: lume ✔, 3D ✔ (canvas {@code aria-label='Astonia'}; ABSENT on
 * Astonia Trophy Gold). The 2026-07-15 verify scrape confirmed Astonia behaves
 * like the active families — the primary CTA is the enabled "Register Interest"
 * (it opens the shared register-interest form), and the share button, floating
 * CTA, and Book Consultation link are all present. (The Phase-0 scrape's
 * "Interest submitted / disabled" reading was inaccurate and has been removed.)
 * Astonia only differs in its title/gallery markup, overridden below.
 *
 * <p>
 * NOTE: the title/gallery overrides are from the Phase-0 scrape and are not yet
 * re-verified; a full Astonia re-scrape (matching the other families) would
 * confirm them.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_Astonia"
* @since 2026-07-03
 */

@Log4j2
public abstract class SharedTemplate_Astonia<SELF extends SharedTemplate_Astonia<SELF>> extends SharedTimepiecePdp<SELF> {

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_Astonia(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // FAMILY HOOKS
    // ==========================================
    @Override
    protected String familyName() {
        return "Astonia";
    }

    /** Astonia's product title uses a Tailwind arbitrary-value class. */
    @Override
    protected By productTitleLocator() {
        return By.cssSelector("h1.text-\\[32px\\]"); // ROLLOUT-GAP: semantic-class
    }

    /** Astonia's image gallery uses a different container class. */
    @Override
    protected By imageGalleryLocator() {
        return By.cssSelector("div.w-full.aspect-square.bg-white"); // ROLLOUT-GAP: semantic-class
    }
}
