package com.timepieces.automation.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;
import com.timepieces.automation.utils.ActionsUtils;

import lombok.extern.log4j.Log4j2;

/**
 * GlobalSearchComponent represents the global search modal overlay that
 * appears when the user clicks the Search toggle button in the site header.
 * It extends the BasePage class which provides common wait, click, and
 * input helper methods for all page components.
 */
@Log4j2
public class GlobalSearchComponent extends BasePage {
    // ==========================================
    // Constructor
    // ==========================================
    public GlobalSearchComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    /*
     * None of the locators are working as of yet, waiting on data-testid attributes
     * to be implemented. These are just dummy values to be replaced tomorrow.
     */
    // The Global Search Modal/Drawer Container
    private final By globalSearch_Container = By.cssSelector("div[data-testid='global-search-container']");

    // The Global Search Input Field
    private final By globalSearch_Input = By.cssSelector("input[data-testid='global-search-input']");

    // The Global Search Submit Button --Not Implemented yet, users need to press
    // enter to submit--
    private final By globalSearch_SubmitButton = By.cssSelector("button[data-testid='global-search-submit-button']");

    // The Global Search Clear Button --Not Implemented yet, users need to click
    // on the clear button to clear the search field--
    private final By globalSearch_ClearButton = By.cssSelector("button[data-testid='global-search-clear-button']");

    // The Global Search Suggestions Container --Not Implemented yet, users need to
    // wait for search suggestions to appear after entering a search query--
    private final By globalSearch_SuggestionsContainer = By
            .cssSelector("div[data-testid='global-search-suggestions-container']");

    // The Global Search Dismiss Button -- Dismisses the Global Seach Modal --
    private final By globalSearch_DismissButton = By.cssSelector("button[data-testid='global-search-dismiss-button']");

    // The Global Search Results List -- Not Implemented yet, users need to wait for
    // search results to appear after entering a search query--
    private final By globalSearchResults_List = By.cssSelector("ul[role='listbox']");

    // The Global Search Results List Tabs -- Not Implemented yet, users need to
    // wait for
    // search results to appear after entering a search query--
    private final By globalSearchResults_Tabs = By.cssSelector("div[data-testid='global-search-results-tabs']");

    // The Global Search Results Suggested Tab
    private final By globalSearchResults_SuggestedTab = By
            .cssSelector("button[data-testid='global-search-results-suggested-tab']");

    // The Global Search Results Recently Viewed Tab -- Not Implemented yet, users
    // need to wait for
    // search results to appear after entering a search query--
    private final By globalSearchResults_RecentlyViewedTab = By
            .cssSelector("button[data-testid='global-search-results-recently-viewed-tab']");

    // The Global Search Results Trending Right Now Products
    private final By globalSearchResults_TrendingProducts = By
            .cssSelector("div[data-testid='global-search-results-trending-products']");

    // The Global Search Products List -- Not Implemented yet, users need to wait
    // for
    // search results to appear after entering a search query--
    private final By globalSearchResults_ProductsList = By.cssSelector("ul[role='listbox']");

    // The Global Search No Results Found
    private final By globalSearchResults_NoResultsFound = By
            .cssSelector("div[data-testid='global-search-no-results-found']");

    // The Global Search Suggested Product Card -- Not implemented yet --
    private final By globalSearchResults_SuggestedProductCard = By
            .cssSelector("div[data-testid='global-search-results-suggested-product-card']");

    // The Global Search Recently Viewed Product Card -- Not implemented yet --
    private final By globalSearchResults_RecentlyViewedProductCard = By
            .cssSelector("div[data-testid='global-search-results-recently-viewed-product-card']");

    // The Global Search Trending Product Card -- Not implemented yet --
    private final By globalSearchResults_TrendingProductCard = By
            .cssSelector("div[data-testid='global-search-results-trending-product-card']");

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * P.1 Clears the search input field by clicking the clear button.
     */
    private void clearField() {
        log.info("Clearing the search field by clicking on the clear button.");
        clickElement(globalSearch_ClearButton);
    }

    /**
     * P.2 Types text into the Global Search input field using the keyboard
     * Actions chain. This fires real keydown/keypress/keyup events, which is
     * more reliable for React/NextJS controlled inputs than element.sendKeys().
     *
     * @param query The search text to type into the field.
     */
    private void typeSearchText(String query) {
        log.info("Typing search text into the Global Search Bar field.");
        ActionsUtils.typeWithKeyboard(driver, getElement(globalSearch_Input), query);
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Enters a search query into the global search input.
     * Clears any existing text first, then types the new query via the
     * keyboard Actions chain.
     *
     * @param query The search query to enter.
     * @return this GlobalSearchComponent for method chaining.
     */
    public GlobalSearchComponent enterSearchQuery(String query) {
        log.info("Entering search query: {}", query);
        clearField();
        typeSearchText(query);
        return this;
    }

    /**
     * B.2 Clears the current search query by clicking the clear button.
     *
     * @return this GlobalSearchComponent for method chaining.
     */
    public GlobalSearchComponent clearSearchQuery() {
        log.info("Clearing search query.");
        clearField();
        return this;
    }

    /**
     * B.3 Submits the search by clicking the submit button. Returns the
     * HeaderComponent since search results render on the same page with the
     * header still visible.
     *
     * @return HeaderComponent representing the page after search submission.
     */
    public HeaderComponent submitSearch() {
        log.info("Submitting search.");
        clickElement(globalSearch_SubmitButton);
        return new HeaderComponent(driver);
    }

    /**
     * B.4 Submits the search by pressing the Enter key on the search input.
     *
     * @return HeaderComponent representing the page after search submission.
     */
    public HeaderComponent pressEnterKeyToSubmitSearch() {
        log.info("Pressing enter key to submit search.");
        ActionsUtils.sendKey(driver, getElement(globalSearch_Input), Keys.ENTER);
        return new HeaderComponent(driver);
    }

    /**
     * B.5 Closes the global search overlay by pressing the Escape key.
     *
     * @return HeaderComponent representing the parent page.
     */
    public HeaderComponent pressEscapeKeyToCloseSearch() {
        log.info("Pressing escape key to close search.");
        ActionsUtils.sendKey(driver, getElement(globalSearch_Container), Keys.ESCAPE);
        waitForInvisibility(globalSearch_Container);
        return new HeaderComponent(driver);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies if the global search container is displayed.
     *
     * @return true if displayed, false otherwise.
     */
    public boolean isGlobalSearchDisplayed() {
        boolean displayed = isDisplayed(globalSearch_Container);
        log.info("Global search displayed check: {}", displayed);
        return displayed;
    }

    /**
     * V.2 Verifies if the search suggestions container is displayed.
     *
     * @return true if displayed, false otherwise.
     */
    public boolean isSearchSuggestionsDisplayed() {
        boolean displayed = isDisplayed(globalSearch_SuggestionsContainer);
        log.info("Search suggestions displayed check: {}", displayed);
        return displayed;
    }

    /**
     * V.3 Verifies that the search suggestions container is NOT displayed.
     *
     * @return true if not displayed, false if it is visible.
     */
    public boolean isSearchSuggestionsNotDisplayed() {
        log.info("Verifying search suggestions are not displayed.");
        return !isDisplayed(globalSearch_SuggestionsContainer);
    }

    /**
     * V.4 Gets the current value from the search input field.
     *
     * @return The current text value in the search input.
     */
    public String getSearchQuery() {
        log.info("Getting search query value.");
        return getAttribute(globalSearch_Input, "value");
    }
}
