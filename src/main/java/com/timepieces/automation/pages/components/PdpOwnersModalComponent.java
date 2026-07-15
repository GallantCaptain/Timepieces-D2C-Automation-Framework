package com.timepieces.automation.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * PdpOwnersModalComponent — the "owners" social-proof modal shared by every
 * timepiece PDP.
 *
 * <p>
 * Every timepiece PDP carries a "See all {Family} owners" button that opens a
 * modal listing the people who own that watch. Inside, the list can be
 * <b>filtered by variant</b> (one button per sibling variant) and
 * <b>searched</b> ("Search owners…"). Because this owners modal is identical
 * across all timepiece PDPs, it is mapped ONCE here as a Component Object (like
 * {@code HeaderComponent}/{@code FooterComponent}) and referenced by each family
 * template rather than duplicated per family.
 *
 * <p>
 * NOT the collection FILTERS &amp; SORT drawer: this is a PDP social-proof modal
 * whose internal filter/search applies to <i>owners</i>. The storefront's actual
 * product FILTERS &amp; SORT lives on the collection/PLP pages
 * ({@code AllTimepiecesPage} / {@code AllAccessoriesPage}, mapped in Phase 0) and
 * is unrelated to this component.
 *
 * <p>
 * LOCATOR SOURCE: 2026-07-15 Monarch scrape
 * ({@code docs/scrape-artifacts/phase1/}). No element carries a
 * {@code data-testid}, so every locator is a fallback (aria/text-anchor/
 * structural) tagged {@code // ROLLOUT-GAP}. The trigger button is located
 * family-agnostically ("See all … owners") so the same component works on every
 * timepiece PDP.
 *
 * FLUENT INTERFACE: methods that stay in the modal return {@code this}.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of PdpOwnersModalComponent"
* @since 2026-07-15
 */

@Log4j2
public class PdpOwnersModalComponent extends BasePage<PdpOwnersModalComponent> {

    // ==========================================
    // Constructor
    // ==========================================
    public PdpOwnersModalComponent(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // Trigger lives on the PDP; family-agnostic text anchor ("See all X owners").
    private final By openTrigger = By
            .xpath("//button[contains(normalize-space(.),'See all') and contains(normalize-space(.),'owners')]"); // ROLLOUT-GAP: text-anchor
    private final By modalContainer = By
            .cssSelector("div.fixed.inset-x-0.bottom-0.md\\:inset-0.z-\\[101\\]"); // ROLLOUT-GAP: structural
    private final By searchInput = By.cssSelector("input[placeholder='Search owners...']"); // ROLLOUT-GAP: attribute
    private final By closeButton = By.cssSelector("button[aria-label='Close']"); // ROLLOUT-GAP: aria

    // Dynamic: variant filter button inside the modal, by variant label.
    private final By variantFilterTemplate = By.xpath(
            "//div[contains(@class,'z-')]//button[contains(normalize-space(),'%s')]"); // ROLLOUT-GAP: text-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Opens the owners modal from the current PDP by clicking its
     * "See all … owners" trigger, then waits for the search field to confirm the
     * modal is interactive.
     *
     * @return this component for method chaining.
     */
    public PdpOwnersModalComponent open() {
        log.info("Opening the PDP owners modal.");
        clickElement(openTrigger);
        waitForVisibility(searchInput);
        return this;
    }

    /**
     * B.2 Filters the owners list to a single variant by its label
     * (e.g. "Silver Black", "Rose Gold").
     *
     * @param variantLabel the sibling-variant label to filter by.
     * @return this component for method chaining.
     */
    public PdpOwnersModalComponent filterByVariant(String variantLabel) {
        log.info("Filtering owners by variant: '{}'.", variantLabel);
        clickElement(formatLocator(variantFilterTemplate, variantLabel));
        return this;
    }

    /**
     * B.3 Types a query into the "Search owners…" field.
     *
     * @param query the owner name / text to search for.
     * @return this component for method chaining.
     */
    public PdpOwnersModalComponent searchOwners(String query) {
        log.info("Searching owners for: '{}'.", query);
        sendKeys(searchInput, query);
        return this;
    }

    /**
     * B.4 Closes the owners modal and waits for it to disappear.
     *
     * @return this component for method chaining.
     */
    public PdpOwnersModalComponent close() {
        log.info("Closing the PDP owners modal.");
        clickElement(closeButton);
        waitForInvisibility(modalContainer);
        return this;
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the "See all … owners" trigger is present on the PDP.
     *
     * @return true if the owners-modal trigger is displayed.
     */
    public boolean isTriggerDisplayed() {
        return isDisplayed(openTrigger);
    }

    /**
     * V.2 Verifies the owners modal is open (search field visible).
     *
     * @return true if the modal is open and interactive.
     */
    public boolean isModalDisplayed() {
        return isDisplayed(searchInput);
    }

    /**
     * V.3 Verifies a variant filter button for the given label is present in the
     * open modal.
     *
     * @param variantLabel the variant label to look for.
     * @return true if that variant filter button is displayed.
     */
    public boolean isVariantFilterDisplayed(String variantLabel) {
        return isDisplayed(formatLocator(variantFilterTemplate, variantLabel));
    }
}
