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
 * ASTONIA SHAPE (Phase-0 scrape, 2026-07-10): lume ✔, 3D ✔ (canvas
 * {@code aria-label='Astonia'}; ABSENT on Astonia Trophy Gold). Astonia products
 * are fully allocated, so the primary CTA renders as a DISABLED
 * "Interest submitted" (see {@link #isInterestSubmittedState()}) rather than the
 * enabled "Register Interest" of the active families — so the inherited
 * register-interest methods do not apply while Astonia is sold out. Astonia also
 * uses different title/gallery markup, overridden below.
 *
 * <p>
 * NOTE: Astonia's share / floating-CTA / consultation state was not captured in
 * the Phase-0 scrape (it predates those checklist items) — a re-scrape is tracked
 * under TASK-PDP-004 / TASK-PDP-005.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_Astonia"
* @since 2026-07-03
 */

@Log4j2
public abstract class SharedTemplate_Astonia<SELF extends SharedTemplate_Astonia<SELF>> extends SharedTimepiecePdp<SELF> {

    // Astonia's sold-out CTA — disabled "Interest submitted".
    private final By interestSubmittedButton = By.cssSelector(".desktop-sticky-sidebar button[disabled]"); // ROLLOUT-GAP: structural

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

    // ==========================================
    // VERIFICATION METHODS (Astonia-specific)
    // ==========================================
    /**
     * Verifies the product is in the sold-out "Interest submitted" state — the
     * primary CTA is disabled and labelled accordingly. No-wait check.
     *
     * @return true if the CTA is disabled and reads "Interest submitted".
     */
    public boolean isInterestSubmittedState() {
        return driver.findElements(interestSubmittedButton).stream()
                .findFirst()
                .map(btn -> {
                    String text = btn.getText() == null ? "" : btn.getText().trim();
                    boolean disabled = btn.getAttribute("disabled") != null || !btn.isEnabled();
                    return disabled && text.toLowerCase().contains("interest");
                })
                .orElse(false);
    }
}
