package com.timepieces.automation.pages.web.products.accessories;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The Accessories Collection: https://www.timepieces-d2c.example/accessories/all
 * The whole accessories collection page. It contains all '4' accessory types
 * that have been sold by Timepieces so far.
 * There is a div containing the Product Drop Links to 'Astonia' & 'Manifesta'
 * for Q3 2026. There is a filter & sort option that can filter and/or sort the
 * accessories as per the attribute requirements. There are also accessory type
 * category capsule-button tabs, that switch between 'All', 'Straps', 'Clasps',
 * and 'Watch Case' accessory types.
 *
 * <p>
 * LOCATOR SOURCE: real {@code data-testid}s captured from the 2026-07-10 scrape
 * ({@code docs/scrape-artifacts/phase0/all-accessories-page.md}). This PLP is
 * fully instrumented with {@code accessories-*} test ids; only the raw filter
 * {@code <input>}s inside their labels lack ids (handled by clicking the label).
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (AllAccessoriesPage)
 * to allow method chaining in step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of AllAccessoriesPage"
* @since 2026-07-03
 */

@Log4j2
public class AllAccessoriesPage extends StoreFrontBasePage<AllAccessoriesPage> {

    // ==========================================
    // PAGE CONSTANTS
    // ==========================================
    private static final String PAGE_PATH = "/accessories/all";
    private static final String PAGE_TITLE = "All Accessories | Timepieces Accessories";

    // ==========================================
    // Constructor
    // ==========================================
    public AllAccessoriesPage(WebDriver driver) {
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
    private final By accessoriesBanner = By.cssSelector("section[data-testid='accessories-banner']");
    private final By bannerTitle = By.cssSelector("h1[data-testid='accessories-banner-title']");

    // --- Category capsule tabs (All / Strap / Clasp / Watch Case) ---
    private final By categoryTabs = By.cssSelector("div[data-testid='accessories-category-tabs']");
    private final By categoryTabButtons = By.cssSelector("button[data-testid='accessories-category-tab']");
    private final By productCount = By.cssSelector("span[data-testid='accessories-product-count']");

    // --- Product grid ---
    private final By productsGrid = By.cssSelector("div[data-testid='accessories-products-grid']");
    private final By productCards = By.cssSelector("div[data-testid='accessories-product-card']");
    private final By productLinks = By.cssSelector("a[data-testid='accessories-product-link']");
    private final By productTitles = By.cssSelector("h3[data-testid='accessories-product-title']");

    // --- Filter & sort drawer ---
    private final By filtersButtonDesktop = By.cssSelector("button[data-testid='accessories-filters-button-desktop']");
    private final By filtersButtonMobile = By.cssSelector("button[data-testid='accessories-filters-button-mobile']");
    private final By filterDrawerDesktop = By.cssSelector("aside[data-testid='accessories-filter-drawer-desktop']");
    private final By filterCloseButton = By.cssSelector("button[data-testid='accessories-filter-close']");
    private final By sortOptions = By.cssSelector("label[data-testid='accessories-filter-sort-option']");
    private final By productTypeFilterToggle = By
            .cssSelector("button[data-testid='accessories-filter-section-product-type-toggle']");
    private final By productTypeFilterOptions = By.cssSelector("label[data-testid='accessories-filter-product-type']");
    private final By sizeFilterToggle = By.cssSelector("button[data-testid='accessories-filter-section-size-toggle']");
    private final By filterApplyButton = By.cssSelector("button[data-testid='accessories-filter-apply-desktop']");

    // --- Dynamic templates ---
    private final By categoryTabByNameTemplate = By
            .xpath("//button[@data-testid='accessories-category-tab'][normalize-space()='%s']");
    private final By sortOptionByLabelTemplate = By
            .xpath("//label[@data-testid='accessories-filter-sort-option'][contains(normalize-space(),'%s')]");
    private final By productTypeOptionByLabelTemplate = By
            .xpath("//label[@data-testid='accessories-filter-product-type'][contains(normalize-space(),'%s')]");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * P.1 Builds a locator for a product card link by the accessory handle it
     * links to (e.g. {@code butterfly-clasp-gold-18mm}).
     *
     * @param handle the accessory handle the card links to.
     * @return a By locator for that accessory's link.
     */
    private By productLinkByHandle(String handle) {
        return By.cssSelector("a[data-testid='accessories-product-link'][href*='" + handle + "']");
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
     * B.1 Selects an accessory-type capsule tab by its label
     * ({@code All} / {@code Strap} / {@code Clasp} / {@code Watch Case}).
     *
     * @param tabName the visible tab label.
     * @return this page for method chaining.
     */
    public AllAccessoriesPage selectCategoryTab(String tabName) {
        log.info("Selecting accessory category tab: '{}'.", tabName);
        clickElement(formatLocator(categoryTabByNameTemplate, tabName));
        return this;
    }

    /**
     * B.2 Opens the filter & sort drawer (viewport-aware).
     *
     * @return this page for method chaining.
     */
    public AllAccessoriesPage openFilters() {
        log.info("Opening the filter & sort drawer.");
        clickElement(activeFiltersButton());
        return this;
    }

    /**
     * B.3 Closes the filter & sort drawer.
     *
     * @return this page for method chaining.
     */
    public AllAccessoriesPage closeFilters() {
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
    public AllAccessoriesPage selectSortOption(String sortLabel) {
        log.info("Selecting sort option: '{}'.", sortLabel);
        clickElement(formatLocator(sortOptionByLabelTemplate, sortLabel));
        return this;
    }

    /**
     * B.5 Expands the product-type filter section, then selects a type option
     * ({@code Strap} / {@code Clasp} / {@code Watch Case}).
     *
     * @param productType the product type label to filter by.
     * @return this page for method chaining.
     */
    public AllAccessoriesPage filterByProductType(String productType) {
        log.info("Filtering by product type: '{}'.", productType);
        clickElement(productTypeFilterToggle);
        clickElement(formatLocator(productTypeOptionByLabelTemplate, productType));
        return this;
    }

    /**
     * B.6 Expands the size filter accordion section.
     *
     * @return this page for method chaining.
     */
    public AllAccessoriesPage expandSizeFilter() {
        log.info("Expanding the size filter section.");
        clickElement(sizeFilterToggle);
        return this;
    }

    /**
     * B.7 Applies the currently selected filters.
     *
     * @return this page for method chaining.
     */
    public AllAccessoriesPage applyFilters() {
        log.info("Applying filters.");
        clickElement(filterApplyButton);
        return this;
    }

    /**
     * B.8 Opens an accessory's PDP by clicking its card in the grid, identified by
     * accessory handle.
     *
     * @param handle the accessory handle to open (e.g. {@code premium-watch-case}).
     */
    public void openProductByHandle(String handle) {
        log.info("Opening accessory PDP from the grid: '{}'.", handle);
        clickElement(productLinkByHandle(handle));
    }

    /**
     * B.9 Returns the raw product-count caption (e.g. "21 Products").
     *
     * @return the trimmed product-count text.
     */
    public String getProductCountText() {
        return getText(productCount);
    }

    /**
     * B.10 Returns the banner heading text (observed: "All Accessories").
     *
     * @return the trimmed banner title text.
     */
    public String getBannerTitleText() {
        return getText(bannerTitle);
    }

    /**
     * B.11 Returns the visible product-card titles currently loaded in the grid.
     *
     * @return a list of trimmed accessory titles.
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
     * V.1 Verifies the accessories banner is displayed.
     *
     * @return true if the banner is displayed.
     */
    public boolean isBannerDisplayed() {
        return isDisplayed(accessoriesBanner);
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
     * V.4 Counts the category capsule tabs (observed: All / Strap / Clasp / Watch
     * Case).
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
     * V.6 Counts the accessory cards currently loaded in the grid.
     *
     * @return the number of product cards.
     */
    public int getProductCardCount() {
        List<org.openqa.selenium.WebElement> cards = driver.findElements(productCards);
        log.info("Accessory product cards loaded: {}", cards.size());
        return cards.size();
    }

    /**
     * V.7 Parses the numeric product count from the count caption
     * (e.g. "21 Products" &rarr; 21). Returns -1 if it cannot be parsed.
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
     * V.9 Counts the clickable product links currently in the grid.
     *
     * @return the number of product links.
     */
    public int getProductLinkCount() {
        return driver.findElements(productLinks).size();
    }

    /**
     * V.10 Counts the sort options available in the filter drawer (call after
     * {@link #openFilters()}).
     *
     * @return the number of sort options.
     */
    public int getSortOptionCount() {
        return driver.findElements(sortOptions).size();
    }

    /**
     * V.11 Counts the product-type filter options available (call after expanding
     * the product-type filter section).
     *
     * @return the number of product-type filter options.
     */
    public int getProductTypeOptionCount() {
        return driver.findElements(productTypeFilterOptions).size();
    }
}
