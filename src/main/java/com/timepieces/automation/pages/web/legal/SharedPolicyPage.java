package com.timepieces.automation.pages.web.legal;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * SharedPolicyPage — the common base for the 5 Timepieces legal/policy pages
 * (Guarantee, Privacy, Refund, Shipping, Terms of Service), all served under
 * {@code /policies/<slug>}.
 *
 * <p>
 * The 2026-07-15 scrape confirmed the 5 pages share ONE template: a
 * {@code div.prose.prose-lg.prose-invert} content block inside a
 * {@code max-w-4xl} wrapper, with a "Back to Policies" link at the top and a
 * "View all policies" link at the bottom. The page title is the first heading —
 * an {@code h1} on 4 of the pages, but an {@code h2} ("Timepieces Shipping Policy")
 * on the Shipping page — so the main-heading locator matches the first
 * {@code h1} OR {@code h2}. Section headings are {@code h2}/{@code h3} inside the
 * prose block. There are no {@code data-testid}s on the policy body, so all
 * locators are fallbacks tagged {@code // ROLLOUT-GAP}. Concrete pages supply only
 * their {@code PAGE_PATH} + {@code PAGE_TITLE}.
 *
 * GENERIC SELF-TYPE (CRTP): each policy binds itself, e.g.
 * {@code class PrivacyPolicyPage extends SharedPolicyPage<PrivacyPolicyPage>}.
 *
 * @param <SELF> the concrete policy page subtype, for fluent return typing.
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedPolicyPage"
* @since 2026-07-15
 */

@Log4j2
public abstract class SharedPolicyPage<SELF extends SharedPolicyPage<SELF>> extends StoreFrontBasePage<SELF> {

    // ==========================================
    // Constructor
    // ==========================================
    public SharedPolicyPage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    private final By proseContainer = By.cssSelector("div.prose.prose-lg.prose-invert"); // ROLLOUT-GAP: semantic-class
    // Page title = first h1/h2 on the page (h1 for 4 pages, h2 for Shipping).
    private final By mainHeading = By.xpath("(//h1 | //h2)[1]"); // ROLLOUT-GAP: structural
    private final By lastUpdated = By.xpath("//*[contains(normalize-space(),'Last Updated')]"); // ROLLOUT-GAP: text-anchor
    private final By sectionHeadings = By.cssSelector("div.prose h2, div.prose h3"); // ROLLOUT-GAP: semantic-class
    private final By backToPoliciesLink = By.xpath("//a[contains(normalize-space(),'Back to Policies')]"); // ROLLOUT-GAP: text-anchor
    private final By viewAllPoliciesLink = By.xpath("//a[contains(normalize-space(),'View all policies')]"); // ROLLOUT-GAP: text-anchor
    private final By supportEmailLink = By.cssSelector("div.prose a[href^='mailto:']"); // ROLLOUT-GAP: href-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * P.1 Returns {@code this} typed as the concrete subclass ({@code SELF}) so
     * inherited business methods stay fluent (CRTP).
     *
     * @return this page instance, typed as the concrete subclass.
     */
    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Clicks the top "Back to Policies" link.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickBackToPolicies() {
        log.info("Clicking 'Back to Policies'.");
        clickElement(backToPoliciesLink);
        return self();
    }

    /**
     * B.2 Clicks the bottom "View all policies" link.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickViewAllPolicies() {
        log.info("Clicking 'View all policies'.");
        clickElement(viewAllPoliciesLink);
        return self();
    }

    /**
     * B.3 Returns the main heading (page title) text.
     *
     * @return the trimmed main heading text.
     */
    public String getMainHeadingText() {
        return getText(mainHeading);
    }

    /**
     * B.4 Returns the "Last Updated" line text (empty behaviour if the page has no
     * such line, e.g. Shipping).
     *
     * @return the trimmed last-updated text.
     */
    public String getLastUpdatedText() {
        return getText(lastUpdated);
    }

    /**
     * B.5 Returns the support/contact email link target in the policy body
     * (e.g. {@code mailto:support@timepieces-d2c.example}).
     *
     * @return the mailto href.
     */
    public String getSupportEmailHref() {
        return getAttribute(supportEmailLink, "href");
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the browser title matches this policy's expected
     * {@code PAGE_TITLE}.
     *
     * @return true if the document title contains the expected page title.
     */
    public boolean isPageTitleCorrect() {
        String actual = driver.getTitle();
        String expected = getPageTitle();
        return expected != null && !expected.isBlank() && actual != null && actual.contains(expected);
    }

    /**
     * V.2 Verifies the main heading (page title) is displayed.
     *
     * @return true if the main heading is displayed.
     */
    public boolean isMainHeadingDisplayed() {
        return isDisplayed(mainHeading);
    }

    /**
     * V.3 Verifies the policy prose content block is displayed.
     *
     * @return true if the prose content is displayed.
     */
    public boolean isProseContentDisplayed() {
        return isDisplayed(proseContainer);
    }

    /**
     * V.4 Counts the section headings ({@code h2}/{@code h3}) in the policy body.
     *
     * @return the number of section headings.
     */
    public int getSectionHeadingCount() {
        return driver.findElements(sectionHeadings).size();
    }

    /**
     * V.5 Verifies the "Back to Policies" link is displayed.
     *
     * @return true if the back link is displayed.
     */
    public boolean isBackToPoliciesDisplayed() {
        return isDisplayed(backToPoliciesLink);
    }

    /**
     * V.6 Verifies the "View all policies" link is displayed.
     *
     * @return true if the view-all link is displayed.
     */
    public boolean isViewAllPoliciesDisplayed() {
        return isDisplayed(viewAllPoliciesLink);
    }

    /**
     * V.7 Verifies the page carries a "Last Updated" date line (no-wait; the
     * Shipping page has none).
     *
     * @return true if a last-updated line is present.
     */
    public boolean hasLastUpdatedDate() {
        return !driver.findElements(lastUpdated).isEmpty();
    }

    /**
     * V.8 Verifies a contact (mailto) email link is present in the policy body.
     *
     * @return true if a contact email link is present.
     */
    public boolean isContactEmailPresent() {
        return !driver.findElements(supportEmailLink).isEmpty();
    }
}
