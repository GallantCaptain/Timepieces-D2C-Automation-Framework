package com.timepieces.automation.pages.web.products.timepieces;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The Timepiece Collection: https://www.timepieces-d2c.example/collections/timepieces
 * The whole timepieces collection page. It contains all '18' timepieces that
 * have been sold by Timepieces so far.
 * There is a div containing the Product Drop Links to 'Astonia' & 'Manifesta'
 * for Q3 2026. There is a filter & sort option that can filter and/or sort the
 * timepieces as the attribute requirements. There are also timepiece type
 * category capsule-button tabs, that switch between 'All', 'Automatic', and
 * 'Q-Matic' timepiece movement types.
 *
 * <p>
 * LOCATOR SOURCE: real {@code data-testid}s captured from the 2026-07-10 scrape
 * ({@code docs/scrape-artifacts/phase0/all-timepieces-page.md}). This PLP is
 * fully instrumented with {@code collection-*} test ids; only the raw filter
 * {@code <input>}s inside their labels lack ids (handled by clicking the label).
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (AllTimepiecesPage)
 * to allow method chaining in step definitions.
 *
 * @since 2026-7-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of AllTimepiecesPage"
 */

@Log4j2
public class AllTimepiecesPage extends StoreFrontBasePage<AllTimepiecesPage> {

    // ==========================================
    // PAGE CONSTANTS
    // ==========================================
    // The Page Path Variable
    private static final String PAGE_PATH = "/collections/timepieces";

    // The Page Title Variable {Keep whether Unique or Not}
    // Decide if it needs to be added to assertions if not unique
    private static final String PAGE_TITLE = "Timepieces Collection | Timepieces Watches";

    // ==========================================
    // Constructor
    // ==========================================
    public AllTimepiecesPage(WebDriver driver) {
        super(driver);
    }

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
    // --- Banner / header ---
    private final By collectionBanner = By.cssSelector("div[data-testid='collection-banner']");
    private final By bannerTitle = By.cssSelector("h1[data-testid='collection-banner-title']");

    // --- Category capsule tabs (All / Automatic / Q-Matic) ---
    private final By categoryTabs = By.cssSelector("div[data-testid='collection-category-tabs']");
    private final By categoryTabButtons = By.cssSelector("button[data-testid='collection-category-tab']");
    private final By productCount = By.cssSelector("span[data-testid='collection-product-count']");

    // --- Product grid ---
    private final By productsGrid = By.cssSelector("div[data-testid='collection-products-grid']");
    private final By productCards = By.cssSelector("div[data-testid='collection-product-card']");
    private final By productLinks = By.cssSelector("a[data-testid='collection-product-link']");
    private final By productTitles = By.cssSelector("h3[data-testid='collection-product-title']");

    // --- Filter & sort drawer ---
    private final By filtersButtonDesktop = By.cssSelector("button[data-testid='collection-filters-button-desktop']");
    private final By filtersButtonMobile = By.cssSelector("button[data-testid='collection-filters-button-mobile']");
    private final By filterDrawerDesktop = By.cssSelector("aside[data-testid='collection-filter-drawer-desktop']");
    private final By filterCloseButton = By.cssSelector("button[data-testid='collection-filter-close']");
    private final By sortOptions = By.cssSelector("label[data-testid='collection-filter-sort-option']");
    private final By movementFilterToggle = By
            .cssSelector("button[data-testid='collection-filter-section-watch-movement-toggle']");
    private final By movementFilterOptions = By.cssSelector("label[data-testid='collection-filter-watch-movement']");
    private final By sizeFilterToggle = By.cssSelector("button[data-testid='collection-filter-section-size-toggle']");
    // Dial-size options (39.5mm / 40mm / 42mm). Option testid follows the
    // confirmed <prefix>-filter-<section> convention (verify on next PLP scrape).
    private final By dialSizeOptions = By.cssSelector("label[data-testid='collection-filter-size']");
    private final By filterApplyButton = By.cssSelector("button[data-testid='collection-filter-apply-desktop']");

    // --- Upcoming-drop banner cards ---
    private final By nextReleaseCardsContainer = By.cssSelector("div[data-testid='collection-next-release-cards']");
    private final By nextReleaseCards = By.cssSelector("a[data-testid='collection-next-release-card']");

    // --- Dynamic templates ---
    private final By categoryTabByNameTemplate = By
            .xpath("//button[@data-testid='collection-category-tab'][normalize-space()='%s']");
    private final By sortOptionByLabelTemplate = By
            .xpath("//label[@data-testid='collection-filter-sort-option'][contains(normalize-space(),'%s')]");
    private final By movementOptionByLabelTemplate = By
            .xpath("//label[@data-testid='collection-filter-watch-movement'][contains(normalize-space(),'%s')]");
    private final By dialSizeOptionByLabelTemplate = By
            .xpath("//label[@data-testid='collection-filter-size'][contains(normalize-space(),'%s')]");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * P.1 Builds a locator for a product card link by the product handle it links
     * to (e.g. {@code astonia-stealth-silver}). Product handles are stable, so this
     * is preferred over positional card selection.
     *
     * @param handle the product handle the card links to.
     * @return a By locator for that product's link.
     */
    private By productLinkByHandle(String handle) {
        return By.cssSelector("a[data-testid='collection-product-link'][href*='" + handle + "']");
    }

    /**
     * P.2 Returns the filter open button appropriate for the current viewport.
     *
     * @return the mobile filter button on a mobile viewport, else the desktop one.
     */
    private By activeFiltersButton() {
        return isMobileViewport() ? filtersButtonMobile : filtersButtonDesktop;
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Selects a movement category capsule tab by its label
     * ({@code All} / {@code Automatic} / {@code Q-Matic}).
     *
     * @param tabName the visible tab label.
     * @return this page for method chaining.
     */
    public AllTimepiecesPage selectCategoryTab(String tabName) {
        log.info("Selecting timepiece category tab: '{}'.", tabName);
        clickElement(formatLocator(categoryTabByNameTemplate, tabName));
        return this;
    }

    /**
     * B.2 Opens the filter & sort drawer (viewport-aware).
     *
     * @return this page for method chaining.
     */
    public AllTimepiecesPage openFilters() {
        log.info("Opening the filter & sort drawer.");
        clickElement(activeFiltersButton());
        return this;
    }

    /**
     * B.3 Closes the filter & sort drawer.
     *
     * @return this page for method chaining.
     */
    public AllTimepiecesPage closeFilters() {
        log.info("Closing the filter & sort drawer.");
        clickElement(filterCloseButton);
        return this;
    }

    /**
     * B.4 Selects a sort option by its label
     * (e.g. "Price, Low To High" / "Price, High To Low").
     *
     * @param sortLabel a distinguishing substring of the sort option label.
     * @return this page for method chaining.
     */
    public AllTimepiecesPage selectSortOption(String sortLabel) {
        log.info("Selecting sort option: '{}'.", sortLabel);
        clickElement(formatLocator(sortOptionByLabelTemplate, sortLabel));
        return this;
    }

    /**
     * B.5 Expands the watch-movement filter section, then selects a movement
     * option ({@code Automatic} / {@code Q-Matic}).
     *
     * @param movement the movement label to filter by.
     * @return this page for method chaining.
     */
    public AllTimepiecesPage filterByMovement(String movement) {
        log.info("Filtering by movement: '{}'.", movement);
        clickElement(movementFilterToggle);
        clickElement(formatLocator(movementOptionByLabelTemplate, movement));
        return this;
    }

    /**
     * B.6 Applies the currently selected filters.
     *
     * @return this page for method chaining.
     */
    public AllTimepiecesPage applyFilters() {
        log.info("Applying filters.");
        clickElement(filterApplyButton);
        return this;
    }

    /**
     * B.7 Opens a product's PDP by clicking its card in the grid, identified by
     * product handle.
     *
     * @param handle the product handle to open (e.g. {@code monarch-rose-gold}).
     */
    public void openProductByHandle(String handle) {
        log.info("Opening product PDP from the grid: '{}'.", handle);
        clickElement(productLinkByHandle(handle));
    }

    /**
     * B.8 Clicks an upcoming-drop banner card by the drop handle it links to
     * ({@code astonia} or {@code manifesta}).
     *
     * @param dropHandle the drop slug the banner links to.
     */
    public void openNextReleaseDrop(String dropHandle) {
        log.info("Opening next-release drop banner: '{}'.", dropHandle);
        clickElement(By.cssSelector("a[data-testid='collection-next-release-card'][href*='" + dropHandle + "']"));
    }

    /**
     * B.9 Returns the raw product-count caption (e.g. "18 Timepieces").
     *
     * @return the trimmed product-count text.
     */
    public String getProductCountText() {
        return getText(productCount);
    }

    /**
     * B.10 Expands the dial-size filter accordion section.
     *
     * @return this page for method chaining.
     */
    public AllTimepiecesPage expandSizeFilter() {
        log.info("Expanding the dial-size filter section.");
        clickElement(sizeFilterToggle);
        return this;
    }

    /**
     * B.13 Expands the dial-size filter section, then ticks a dial-size option
     * (e.g. "39.5mm", "40mm", "42mm").
     *
     * @param dialSize the dial-size label to select.
     * @return this page for method chaining.
     */
    public AllTimepiecesPage selectDialSize(String dialSize) {
        log.info("Filtering by dial size: '{}'.", dialSize);
        clickElement(sizeFilterToggle);
        clickElement(formatLocator(dialSizeOptionByLabelTemplate, dialSize));
        return this;
    }

    /**
     * B.11 Returns the banner heading text (observed: "Become More").
     *
     * @return the trimmed banner title text.
     */
    public String getBannerTitleText() {
        return getText(bannerTitle);
    }

    /**
     * B.12 Returns the visible product-card titles currently loaded in the grid.
     *
     * @return a list of trimmed product titles.
     */
    public List<String> getLoadedProductTitles() {
        return getElements(productTitles).stream()
                .map(e -> e.getText().trim())
                .toList();
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the collection banner is displayed.
     *
     * @return true if the banner is displayed.
     */
    public boolean isBannerDisplayed() {
        return isDisplayed(collectionBanner);
    }

    /**
     * V.2 Verifies the browser title matches the expected {@code PAGE_TITLE}.
     *
     * @return true if the document title contains the expected page title.
     */
    public boolean isPageTitleCorrect() {
        String actual = driver.getTitle();
        return actual != null && actual.contains(PAGE_TITLE);
    }

    /**
     * V.3 Verifies the category tabs strip is displayed.
     *
     * @return true if the category tabs are displayed.
     */
    public boolean isCategoryTabsDisplayed() {
        return isDisplayed(categoryTabs);
    }

    /**
     * V.4 Counts the category capsule tabs (observed: All / Automatic / Q-Matic).
     *
     * @return the number of category tabs.
     */
    public int getCategoryTabCount() {
        return driver.findElements(categoryTabButtons).size();
    }

    /**
     * V.5 Verifies the product grid is displayed.
     *
     * @return true if the product grid is displayed.
     */
    public boolean isProductGridDisplayed() {
        return isDisplayed(productsGrid);
    }

    /**
     * V.6 Counts the product cards currently loaded in the grid.
     *
     * @return the number of product cards.
     */
    public int getProductCardCount() {
        List<org.openqa.selenium.WebElement> cards = driver.findElements(productCards);
        log.info("Timepiece product cards loaded: {}", cards.size());
        return cards.size();
    }

    /**
     * V.7 Parses the numeric product count from the count caption
     * (e.g. "18 Timepieces" &rarr; 18). Returns -1 if it cannot be parsed.
     *
     * @return the parsed product count, or -1 on failure.
     */
    public int getReportedProductCount() {
        String text = getProductCountText().replaceAll("[^0-9]", "");
        try {
            return text.isEmpty() ? -1 : Integer.parseInt(text);
        } catch (NumberFormatException e) {
            log.warn("Could not parse product count from '{}'.", text);
            return -1;
        }
    }

    /**
     * V.8 Verifies the filter drawer is displayed (call after
     * {@link #openFilters()}).
     *
     * @return true if the filter drawer is displayed.
     */
    public boolean isFilterDrawerDisplayed() {
        return isDisplayed(filterDrawerDesktop);
    }

    /**
     * V.9 Verifies the upcoming-drop banner is displayed.
     *
     * @return true if the next-release banner container is displayed.
     */
    public boolean isNextReleaseBannerDisplayed() {
        return isDisplayed(nextReleaseCardsContainer);
    }

    /**
     * V.10 Counts the upcoming-drop banner cards (observed: Astonia + Manifesta).
     *
     * @return the number of next-release cards.
     */
    public int getNextReleaseCardCount() {
        return driver.findElements(nextReleaseCards).size();
    }

    /**
     * V.11 Counts the clickable product links currently in the grid.
     *
     * @return the number of product links.
     */
    public int getProductLinkCount() {
        return driver.findElements(productLinks).size();
    }

    /**
     * V.12 Counts the sort options available in the filter drawer (call after
     * {@link #openFilters()}).
     *
     * @return the number of sort options.
     */
    public int getSortOptionCount() {
        return driver.findElements(sortOptions).size();
    }

    /**
     * V.13 Counts the watch-movement filter options available (call after
     * expanding the movement filter section).
     *
     * @return the number of movement filter options.
     */
    public int getMovementOptionCount() {
        return driver.findElements(movementFilterOptions).size();
    }

    /**
     * V.14 Counts the dial-size filter options available (call after
     * {@link #expandSizeFilter()}). Observed: 39.5mm / 40mm / 42mm.
     *
     * @return the number of dial-size filter options.
     */
    public int getDialSizeOptionCount() {
        return driver.findElements(dialSizeOptions).size();
    }
}
