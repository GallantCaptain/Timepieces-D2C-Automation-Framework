package com.timepieces.automation.pages.web.products.accessories.clasps.singleDeployment;

import com.timepieces.automation.pages.web.products.accessories.SharedAccessoryPdp;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * Shared template for the Single Deployment Clasp accessory PDP pages. All shared
 * accessory PDP behavior lives in {@link SharedAccessoryPdp}; this thin subclass
 * groups the Single Deployment Clasp variants (Black / Gold / Rose Gold / Steel).
 *
 * <p>
 * VARIANT MODEL: the color AND size card-buttons NAVIGATE to distinct PDP URLs
 * (confirmed 2026-07-15), so every color/size is its own class. All colors are
 * 20mm EXCEPT Rose Gold, which additionally offers 22mm — hence
 * {@code SingleDeploymentClaspRoseGold20mm} and {@code ...22mm} are two classes.
 * Use the inherited {@link SharedAccessoryPdp#selectSize(String)} /
 * {@link SharedAccessoryPdp#navigateToModel(String)} to move between them.
 *
 * <p>
 * COMPATIBILITY: Single Deployment Clasps pair with Hybrid &amp; FKM Rubber straps.
 *
 * GENERIC SELF-TYPE (CRTP): parameterised with a recursive self-type so the
 * inherited fluent {@code open()} returns the concrete variant page.
 *
 * @param <SELF> the concrete Single Deployment Clasp variant subtype
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_SingleDeploymentClasp"
 */

@Log4j2
public abstract class SharedTemplate_SingleDeploymentClasp<SELF extends SharedTemplate_SingleDeploymentClasp<SELF>>
        extends SharedAccessoryPdp<SELF> {

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_SingleDeploymentClasp(WebDriver driver) {
        super(driver);
    }
}
