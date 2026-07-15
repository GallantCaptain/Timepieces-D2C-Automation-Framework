package com.timepieces.automation.pages.web.otherPages;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The SiteMapPage: https://www.timepieces-d2c.example/sitemap has links for all
 * pages on the website. This page is meant for the search engine crawlers
 * to be able to crawl all the pages on the website. It has both header &
 * footer.
 *
 * <p>
 * SCRAPE NOTE (2026-07-15): the page title is {@code Sitemap | Timepieces}, the
 * main heading is {@code h1} "Sitemap", and the ~60 links are grouped under
 * {@code h2} section headings — "Main Pages", "Collections", "Products",
 * "Accessories", "All Accessories". Link items are ordinary {@code <a>} tags
 * inside the main content, covering products / collections / policies /
 * accessories. No element on the body carries a {@code data-testid} (only the
 * shared header/footer do), so every locator below is a fallback tagged
 * {@code // ROLLOUT-GAP}.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (SiteMapPage)
 * to allow method chaining in step definitions.
 *
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SiteMapPage"
 */

@Log4j2
public class SiteMapPage extends StoreFrontBasePage<SiteMapPage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Page Path Variable
    private static final String PAGE_PATH = "/sitemap";

    // The Page Title Variable
    private static final String PAGE_TITLE = "Sitemap | Timepieces";

    // ==========================================
    // Constructor
    // ==========================================
    public SiteMapPage(WebDriver driver) {
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

    // ==========================================
    // LOCATORS
    // ==========================================
    // The main "Sitemap" heading.
    private final By mainHeading = By.xpath("//h1[contains(normalize-space(),'Sitemap')]"); // ROLLOUT-GAP: text-anchor
    // The group/section headings (Main Pages, Collections, Products, ...).
    private final By sectionHeadings = By.cssSelector("main h2"); // ROLLOUT-GAP: structural
    // All sitemap link items in the main content.
    private final By sitemapLinks = By.cssSelector("main a"); // ROLLOUT-GAP: structural
    // Dynamic: a section heading by its exact text.
    private final By sectionByNameTemplate = By.xpath("//h2[normalize-space()='%s']"); // ROLLOUT-GAP: text-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Returns the main heading ("Sitemap") text.
     *
     * @return the trimmed heading text.
     */
    public String getMainHeadingText() {
        return getText(mainHeading);
    }

    /**
     * B.2 Returns the {@code href} of every sitemap link in the main content.
     *
     * @return the list of link hrefs (may include nulls filtered out).
     */
    public List<String> getAllLinkHrefs() {
        return driver.findElements(sitemapLinks).stream()
                .map(e -> e.getAttribute("href"))
                .filter(h -> h != null && !h.isBlank())
                .collect(Collectors.toList());
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the browser title matches {@code Sitemap | Timepieces}.
     *
     * @return true if the document title contains the expected page title.
     */
    public boolean isPageTitleCorrect() {
        String actual = driver.getTitle();
        return actual != null && actual.contains(PAGE_TITLE);
    }

    /**
     * V.2 Verifies the main heading is displayed.
     *
     * @return true if the main heading is displayed.
     */
    public boolean isMainHeadingDisplayed() {
        return isDisplayed(mainHeading);
    }

    /**
     * V.3 Counts the group/section headings on the sitemap.
     *
     * @return the number of section headings.
     */
    public int getSectionHeadingCount() {
        return driver.findElements(sectionHeadings).size();
    }

    /**
     * V.4 Counts the sitemap link items in the main content.
     *
     * @return the number of links.
     */
    public int getLinkCount() {
        return driver.findElements(sitemapLinks).size();
    }

    /**
     * V.5 Verifies a section with the given heading text exists.
     *
     * @param sectionName the exact section heading text (e.g. "Collections").
     * @return true if the section heading is present.
     */
    public boolean hasSection(String sectionName) {
        return !driver.findElements(formatLocator(sectionByNameTemplate, sectionName)).isEmpty();
    }

    /**
     * V.6 Verifies at least one sitemap link's {@code href} contains the given
     * fragment (e.g. "/policies/", "/products/astonia-stealth-silver").
     *
     * @param hrefFragment the href substring to look for.
     * @return true if a matching link is present.
     */
    public boolean hasLinkContaining(String hrefFragment) {
        return !driver.findElements(By.cssSelector(String.format("main a[href*='%s']", hrefFragment))).isEmpty();
    }
}
