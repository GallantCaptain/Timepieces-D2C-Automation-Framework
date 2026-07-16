package com.timepieces.automation.pages.web.products.timepieces;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * GenericTimepiecePdp — a concrete, path-agnostic timepiece PDP page object used
 * by step definitions to drive ANY timepiece PDP ({@code /products/<handle>}) by
 * URL, without needing the specific family/variant class. The timepiece analogue
 * of {@code GenericAccessoryPdp}.
 *
 * <p>
 * It exposes the full shared timepiece surface (title, gallery, Register
 * Interest, wishlist toggle, share, owners modal, spec/FAQ accordions).
 * Family-specific hooks fall back to defaults: {@code familyName()} returns
 * "Generic", so the 3D-viewer canvas locator will not match — do not assert 3D
 * or lume through this class (use the family classes for those).
 *
 * <p>
 * Navigation is done by the caller (e.g. {@code driver.get(baseUrl + path)}), so
 * this class does not bind a {@code PAGE_PATH}.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of GenericTimepiecePdp"
 * @since 2026-07-16
 */

@Log4j2
public class GenericTimepiecePdp extends SharedTimepiecePdp<GenericTimepiecePdp> {

    // ==========================================
    // Constructor
    // ==========================================
    public GenericTimepiecePdp(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // FAMILY HOOKS
    // ==========================================
    @Override
    protected String familyName() {
        return "Generic";
    }
}
