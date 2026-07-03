package com.timepieces.automation.pages.web.products.accessories.clasps.singleDeployment;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The Shared Template page exists solely to be inherited by the individual
 * single deployment clasp PDP pages as most of the pages share the same design
 * templates.
 * For those with a unique or missing feature, the individual pages will
 * have the feature but will have the same methods with the same name to
 * allow for method chaining in step definitions.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (SharedTemplate_SingleDeploymentClasp) to allow method chaining in step
 * definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class SharedTemplate_SingleDeploymentClasp extends StoreFrontBasePage<SharedTemplate_SingleDeploymentClasp> {

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_SingleDeploymentClasp(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
}
