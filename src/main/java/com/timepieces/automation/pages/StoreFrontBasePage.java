package com.timepieces.automation.pages;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.components.FooterComponent;

import lombok.extern.log4j.Log4j2;

/**
 * For all the webpages having both the global header and footer, this will be
 * the base page. All the common methods related to header and footer can be
 * added here.
 * For example, if we have a method to click on the "Contact Us" link in the
 * footer, we can add that method here so that it can be reused across all the
 * pages that have the header and footer.
 * This way, we can avoid code duplication and maintain a cleaner codebase.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (HeaderOnlyBasePage)
 * to allow method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public abstract class StoreFrontBasePage extends HeaderOnlyBasePage {

    public final FooterComponent footer;

    // ==========================================
    // Constructor
    // ==========================================
    public StoreFrontBasePage(WebDriver driver) {
        super(driver);
        this.footer = new FooterComponent(driver);
    }
}
