package com.timepieces.automation.pages;

import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.components.HeaderComponent;

import lombok.extern.log4j.Log4j2;

/**
 * For all the webpages having only the global header and no footer, this will
 * be the base page. All the common methods related to header can be added here.
 * For example, if we have a method to click on the "Login" link in the header,
 * we can add that method here so that it can be reused across all the pages
 * that have only the header and no footer.
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
public abstract class HeaderOnlyBasePage<SELF extends HeaderOnlyBasePage<SELF>> extends BasePage<SELF> {

    public final HeaderComponent header;

    // ==========================================
    // Constructor
    // ==========================================
    public HeaderOnlyBasePage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }
}