package com.timepieces.automation.pages.web.products.accessories;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.web.products.accessories.straps.SharedStrapPdp;

import lombok.extern.log4j.Log4j2;

/**
 * GenericAccessoryPdp — a concrete, path-agnostic accessory PDP page object used
 * by step definitions to drive ANY accessory PDP (watch case / clasp / strap) by
 * URL, without needing the specific variant class.
 *
 * <p>
 * It extends {@link SharedStrapPdp} so it exposes the full accessory surface
 * (title/price/compat, Add-to-cart, wishlist, gallery, variant switcher,
 * accordions, Return/Guarantee drawers, model carousel) PLUS the strap-only
 * dial-try carousel + clasp-upgrade card. On a non-strap PDP the strap-only
 * checks simply return false (the elements are absent), which is harmless.
 *
 * <p>
 * Navigation is done by the caller (e.g. {@code driver.get(baseUrl + path)}), so
 * this class does not bind a {@code PAGE_PATH}.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of GenericAccessoryPdp"
* @since 2026-07-15
 */

@Log4j2
public class GenericAccessoryPdp extends SharedStrapPdp<GenericAccessoryPdp> {

    // ==========================================
    // Constructor
    // ==========================================
    public GenericAccessoryPdp(WebDriver driver) {
        super(driver);
    }
}
