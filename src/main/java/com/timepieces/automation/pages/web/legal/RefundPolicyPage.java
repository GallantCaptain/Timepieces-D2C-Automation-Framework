package com.timepieces.automation.pages.web.legal;

import org.openqa.selenium.WebDriver;

import lombok.extern.log4j.Log4j2;

/**
 * RefundPolicyPage: https://www.timepieces-d2c.example/policies/refund-policy
 * The Refund & Exchange Policy legal page. Standard policy layout (prose content block,
 * "Back to Policies" / "View all policies" nav) — all shared behavior lives in
 * {@link SharedPolicyPage}. This class supplies only the page path + title.
 *
 * <p>
 * SCRAPE NOTE (2026-07-15): observed heading "Refund & Exchange Policy".
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (RefundPolicyPage).
 *
 * @version 1.0
 * @author Avinash Rath
 * @since 2026-07-03
 */

@Log4j2
public class RefundPolicyPage extends SharedPolicyPage<RefundPolicyPage> {

    // ==========================================
    // PAGE CONSTANTS
    // ==========================================
    private static final String PAGE_PATH = "/policies/refund-policy";
    private static final String PAGE_TITLE = "Refund Policy | Timepieces";

    // ==========================================
    // Constructor
    // ==========================================
    public RefundPolicyPage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // PAGE METADATA (drives the inherited fluent open())
    // ==========================================
    @Override
    protected String getPagePath() {
        return PAGE_PATH;
    }

    @Override
    protected String getPageTitle() {
        return PAGE_TITLE;
    }
}