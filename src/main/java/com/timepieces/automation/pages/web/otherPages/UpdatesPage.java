package com.timepieces.automation.pages.web.otherPages;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The UpdatesPage: https://www.timepieces-d2c.example/updates provides users
 * with news updates and announcements on this page. Users will be able to
 * view older news and announcements pertaining to the brand.
 * The news/announcements can be filtered by clicking on any of the
 * content filter buttons, like 'All', 'Drops', etc.
 * There is a header and a footer on this page.
 *
 * <p>
 * SCRAPE NOTE (2026-07-15): the page title is {@code Timepieces Updates}, the main
 * heading is {@code h1} "Timepieces Updates", and the content-filter row is a
 * horizontally-scrolling {@code div.flex.gap-2.overflow-x-auto} holding the
 * filter buttons — "All", "Drops &amp; Releases", "Delivery Updates",
 * "Community", "Production &amp; Quality". A hero Swiper carousel sits on top
 * (slide-dot buttons "Go to slide N"), and each news item is a clickable
 * {@code button} card ({@code rounded-2xl}, dark {@code bg-[#111215]}) holding a
 * category {@code span}, a date {@code span}, an {@code h3} title and an
 * {@code img}. No element on the body carries a {@code data-testid} (only the
 * shared header/footer do), so every locator below is a fallback tagged
 * {@code // ROLLOUT-GAP}.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (UpdatesPage)
 * to allow method chaining in step definitions.
 *
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of UpdatesPage"
 */

@Log4j2
public class UpdatesPage extends StoreFrontBasePage<UpdatesPage> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Page Path Variable
    private static final String PAGE_PATH = "/updates";

    // The Page Title Variable
    private static final String PAGE_TITLE = "Timepieces Updates";

    // ==========================================
    // Constructor
    // ==========================================
    public UpdatesPage(WebDriver driver) {
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
    // The main "Timepieces Updates" heading.
    private final By mainHeading = By.xpath("//h1[contains(normalize-space(),'Timepieces Updates')]"); // ROLLOUT-GAP: text-anchor
    // The subheading line ("The latest from Timepieces ...").
    private final By subHeading = By.xpath("//p[contains(normalize-space(),'The latest from Timepieces')]"); // ROLLOUT-GAP: text-anchor
    // The horizontally-scrolling content-filter button row.
    private final By filterContainer = By.cssSelector("div.flex.gap-2.overflow-x-auto"); // ROLLOUT-GAP: utility-class
    // All content-filter buttons within the filter row.
    private final By filterButtons = By.cssSelector("div.flex.gap-2.overflow-x-auto button"); // ROLLOUT-GAP: structural
    // Dynamic: a content-filter button by its exact label.
    private final By filterButtonTemplate = By.xpath("//div[contains(@class,'overflow-x-auto')]//button[normalize-space()='%s']"); // ROLLOUT-GAP: text-anchor
    // A news/announcement card (clickable button, rounded-2xl dark tile).
    private final By updateCards = By.cssSelector("button.flex.gap-3.rounded-2xl.text-left.cursor-pointer"); // ROLLOUT-GAP: utility-class
    // The title heading inside a news card.
    private final By updateCardTitle = By.cssSelector("button.rounded-2xl h3"); // ROLLOUT-GAP: structural
    // The hero carousel slide-dot buttons ("Go to slide N").
    private final By heroSlideDots = By.xpath("//button[contains(@aria-label,'Go to slide')]"); // ROLLOUT-GAP: text-anchor
    // The "View more" / "Read more" pagination control.
    private final By viewMoreButton = By.xpath("//button[contains(normalize-space(),'View more') or contains(normalize-space(),'Read more')]"); // ROLLOUT-GAP: text-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Clicks a content-filter button by its exact label (e.g. "All",
     * "Drops &amp; Releases", "Community").
     *
     * @param filterLabel the exact filter button label.
     * @return this page instance for method chaining.
     */
    public UpdatesPage selectFilter(String filterLabel) {
        log.info("Selecting Updates content filter: [{}]", filterLabel);
        clickElement(formatLocator(filterButtonTemplate, filterLabel));
        return this;
    }

    /**
     * B.2 Returns the labels of every content-filter button, in DOM order.
     *
     * @return the list of filter labels.
     */
    public List<String> getFilterLabels() {
        return getElements(filterButtons).stream()
                .map(e -> e.getText().trim())
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * B.3 Returns the main heading ("Timepieces Updates") text.
     *
     * @return the trimmed heading text.
     */
    public String getMainHeadingText() {
        return getText(mainHeading);
    }

    /**
     * B.4 Clicks the "View more" / "Read more" pagination control to load older
     * updates.
     *
     * @return this page instance for method chaining.
     */
    public UpdatesPage clickViewMore() {
        log.info("Clicking 'View more' on the Updates page.");
        clickElement(viewMoreButton);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the browser title matches {@code Timepieces Updates}.
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
     * V.3 Verifies the subheading line is displayed.
     *
     * @return true if the subheading is displayed.
     */
    public boolean isSubHeadingDisplayed() {
        return isDisplayed(subHeading);
    }

    /**
     * V.4 Verifies the content-filter row is displayed.
     *
     * @return true if the filter container is displayed.
     */
    public boolean isFilterContainerDisplayed() {
        return isDisplayed(filterContainer);
    }

    /**
     * V.5 Verifies a content-filter button with the given label exists.
     *
     * @param filterLabel the exact filter button label.
     * @return true if the filter button is present.
     */
    public boolean hasFilter(String filterLabel) {
        return !driver.findElements(formatLocator(filterButtonTemplate, filterLabel)).isEmpty();
    }

    /**
     * V.6 Counts the news/announcement cards currently rendered.
     *
     * @return the number of update cards.
     */
    public int getUpdateCardCount() {
        return driver.findElements(updateCards).size();
    }

    /**
     * V.7 Verifies at least one news/announcement card is displayed.
     *
     * @return true if update cards are present.
     */
    public boolean areUpdateCardsDisplayed() {
        return !driver.findElements(updateCards).isEmpty();
    }

    /**
     * V.8 Verifies at least one update card title is present.
     *
     * @return true if a card title is present.
     */
    public boolean hasUpdateCardTitle() {
        return !driver.findElements(updateCardTitle).isEmpty();
    }

    /**
     * V.9 Counts the hero carousel slide-dot controls.
     *
     * @return the number of slide dots.
     */
    public int getHeroSlideDotCount() {
        return driver.findElements(heroSlideDots).size();
    }
}
