package com.timepieces.automation.pages.web.products.accessories.straps;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.web.products.accessories.SharedAccessoryPdp;

import lombok.extern.log4j.Log4j2;

/**
 * SharedStrapPdp — the base for every STRAP accessory PDP family, adding the two
 * strap-only features on top of {@link SharedAccessoryPdp}:
 * <ul>
 * <li>a <b>dial-try carousel</b> ("A new look for your watch") that previews the
 * strap against compatible watch dials, navigated with prev/next buttons;</li>
 * <li>a <b>clasp-upgrade cross-sell</b> card ("Upgrade your strap") with a
 * "+ Add" button.</li>
 * </ul>
 * Everything else (title/price/compat, "Add to cart", wishlist, gallery, color
 * card-button switcher, accordions, Return/Guarantee drawers, model carousel)
 * is inherited from {@link SharedAccessoryPdp}.
 *
 * <p>
 * LOCATOR SOURCE: 2026-07-15 discovery scrape (rubber + ostrich straps). No
 * {@code data-testid}s — the dial-nav buttons use their semantic classes / aria,
 * the carousel + upgrade card are text-anchored ({@code // ROLLOUT-GAP}).
 *
 * GENERIC SELF-TYPE (CRTP): the strap family templates extend this, e.g.
 * {@code class SharedTemplate_FKMRubberStrap extends SharedStrapPdp<...>}.
 *
 * @param <SELF> the concrete strap page subtype, for fluent return typing.
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedStrapPdp"
* @since 2026-07-15
 */

@Log4j2
public abstract class SharedStrapPdp<SELF extends SharedStrapPdp<SELF>> extends SharedAccessoryPdp<SELF> {

    // ==========================================
    // Constructor
    // ==========================================
    public SharedStrapPdp(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS (strap-only)
    // ==========================================
    // Dial-try carousel ("A new look for your watch").
    private final By dialTryHeading = By.xpath("//*[contains(normalize-space(),'A new look for your watch')]"); // ROLLOUT-GAP: text-anchor
    private final By dialPreviousButton = By.cssSelector("button.strap-nav.strap-prev"); // ROLLOUT-GAP: semantic-class/aria
    private final By dialNextButton = By.cssSelector("button.strap-nav.strap-next"); // ROLLOUT-GAP: semantic-class/aria

    // Clasp-upgrade cross-sell card ("Upgrade your strap" + "+ Add").
    private final By claspUpgradeHeading = By.xpath("//*[contains(normalize-space(),'Upgrade your strap')]"); // ROLLOUT-GAP: text-anchor
    private final By claspUpgradeAddButton = By.xpath("//button[contains(normalize-space(),'+ Add')]"); // ROLLOUT-GAP: text-anchor

    // ==========================================
    // BUSINESS LIBRARY (strap-only)
    // ==========================================
    /**
     * Advances the dial-try carousel to the next compatible watch preview.
     *
     * @return this page instance for method chaining.
     */
    public SELF nextDialPreview() {
        log.info("Dial-try carousel: next watch.");
        clickElement(dialNextButton);
        return self();
    }

    /**
     * Moves the dial-try carousel to the previous compatible watch preview.
     *
     * @return this page instance for method chaining.
     */
    public SELF previousDialPreview() {
        log.info("Dial-try carousel: previous watch.");
        clickElement(dialPreviousButton);
        return self();
    }

    /**
     * Clicks the "+ Add" button in the clasp-upgrade cross-sell card to add the
     * recommended clasp.
     *
     * @return this page instance for method chaining.
     */
    public SELF addRecommendedClasp() {
        log.info("Adding the recommended clasp from the upgrade cross-sell card.");
        clickElement(claspUpgradeAddButton);
        return self();
    }

    // ==========================================
    // VERIFICATION METHODS (strap-only)
    // ==========================================
    /**
     * Verifies the dial-try carousel ("A new look for your watch") is displayed.
     *
     * @return true if the dial-try carousel is displayed.
     */
    public boolean isDialTryCarouselDisplayed() {
        return isDisplayed(dialTryHeading);
    }

    /**
     * Verifies the clasp-upgrade cross-sell card is displayed.
     *
     * @return true if the clasp-upgrade card is displayed.
     */
    public boolean isClaspUpgradeCardDisplayed() {
        return isDisplayed(claspUpgradeHeading);
    }
}
