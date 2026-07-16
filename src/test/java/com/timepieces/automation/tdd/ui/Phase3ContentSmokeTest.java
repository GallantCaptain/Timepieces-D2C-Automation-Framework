package com.timepieces.automation.tdd.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.pages.web.StagingGatePage;
import com.timepieces.automation.pages.web.friendsAndPartners.FriendsAndPartners;
import com.timepieces.automation.pages.web.legal.GuaranteePolicyPage;
import com.timepieces.automation.pages.web.orders.TrackOrdersPage;
import com.timepieces.automation.pages.web.orders.WishlistPage;
import com.timepieces.automation.pages.web.otherPages.SiteMapPage;
import com.timepieces.automation.pages.web.otherPages.UpdatesPage;

import lombok.extern.log4j.Log4j2;

/**
 * Phase3ContentSmokeTest — a TDD (TestNG) smoke that drives the Phase-3 page
 * objects against the live storefront: a policy page (shared legal template),
 * the Sitemap link index, the Updates content filters, the Friends & Partners
 * directory, the guest Wishlist empty state, and the guest Track-Order error
 * path. One representative assertion set per page family; the auth-gated
 * orders/account pages are NOT smoked here (Cloudflare-gated login).
 *
 * <p>
 * OPT-IN: this class is NOT part of the default {@code testng.xml} suite (it
 * needs a real browser and reaches the live site). Run it explicitly via its own
 * suite:
 * {@code mvnw -o test "-Dsurefire.suite=testng-phase3-smoke.xml" "-Denv=production"}.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of Phase3ContentSmokeTest"
* @since 2026-07-16
 */

@Log4j2
public class Phase3ContentSmokeTest {

    private WebDriver driver;

    // ==========================================
    // Lifecycle
    // ==========================================
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
        this.driver = DriverFactory.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            DriverFactory.removeDriver();
        }
    }

    // ==========================================
    // Private helpers
    // ==========================================
    /**
     * Opens the given page and transparently unlocks the non-prod staging gate
     * (re-navigating once) so the assertions behave identically across envs.
     */
    private <T extends com.timepieces.automation.pages.BasePage<T>> T openPage(T page) {
        page.open();
        if (new StagingGatePage(driver).unlockIfPresent()) {
            page.open();
        }
        return page;
    }

    // ==========================================
    // Tests
    // ==========================================
    @Test(groups = { "ui-smoke", "phase3" })
    public void policyPageRendersSharedTemplate() {
        GuaranteePolicyPage page = openPage(new GuaranteePolicyPage(driver));
        assertThat(page.isMainHeadingDisplayed())
                .as("Guarantee policy: main heading should be displayed").isTrue();
        assertThat(page.isProseContentDisplayed())
                .as("Guarantee policy: prose content should be displayed").isTrue();
        assertThat(page.isBackToPoliciesDisplayed())
                .as("Guarantee policy: 'Back to Policies' should be displayed").isTrue();
        assertThat(page.isContactEmailPresent())
                .as("Guarantee policy: a contact mailto link should be present").isTrue();
    }

    @Test(groups = { "ui-smoke", "phase3" })
    public void sitemapIndexesTheStorefront() {
        SiteMapPage page = openPage(new SiteMapPage(driver));
        assertThat(page.isMainHeadingDisplayed())
                .as("Sitemap: heading should be displayed").isTrue();
        assertThat(page.hasSection("Collections"))
                .as("Sitemap: 'Collections' section should exist").isTrue();
        assertThat(page.hasSection("Products"))
                .as("Sitemap: 'Products' section should exist").isTrue();
        assertThat(page.getLinkCount())
                .as("Sitemap: should list a substantial link index").isGreaterThanOrEqualTo(40);
        assertThat(page.hasLinkContaining("/products/"))
                .as("Sitemap: should link to product PDPs").isTrue();
        assertThat(page.hasLinkContaining("/accessories/"))
                .as("Sitemap: should link to accessory pages").isTrue();
    }

    @Test(groups = { "ui-smoke", "phase3" })
    public void updatesPageFiltersContent() {
        UpdatesPage page = openPage(new UpdatesPage(driver));
        assertThat(page.isMainHeadingDisplayed())
                .as("Updates: heading should be displayed").isTrue();
        assertThat(page.hasFilter("All"))
                .as("Updates: 'All' filter should be available").isTrue();
        assertThat(page.hasFilter("Drops & Releases"))
                .as("Updates: 'Drops & Releases' filter should be available").isTrue();
        assertThat(page.areUpdateCardsDisplayed())
                .as("Updates: news cards should be displayed").isTrue();
        page.selectFilter("Drops & Releases");
        assertThat(page.areUpdateCardsDisplayed())
                .as("Updates: cards should remain displayed after filtering").isTrue();
    }

    @Test(groups = { "ui-smoke", "phase3" })
    public void friendsAndPartnersDirectoryLoads() {
        FriendsAndPartners page = openPage(new FriendsAndPartners(driver));
        assertThat(page.isMainHeadingDisplayed())
                .as("F&P: heading should be displayed").isTrue();
        assertThat(page.hasSectionTab("Founders & Makers"))
                .as("F&P: 'Founders & Makers' tab should be available").isTrue();
        assertThat(page.getProfileCardCount())
                .as("F&P: profile cards should be displayed").isGreaterThan(0);
    }

    @Test(groups = { "ui-smoke", "phase3" })
    public void guestWishlistShowsEmptyState() {
        WishlistPage page = openPage(new WishlistPage(driver));
        assertThat(page.isPageHeadingDisplayed())
                .as("Wishlist: page heading should be displayed").isTrue();
        assertThat(page.isEmptyStateDisplayed())
                .as("Wishlist: a fresh guest session should show the empty state").isTrue();
    }

    @Test(groups = { "ui-smoke", "phase3" })
    public void trackOrderRejectsAnUnknownOrderId() {
        TrackOrdersPage page = openPage(new TrackOrdersPage(driver));
        assertThat(page.isMainHeadingDisplayed())
                .as("Track order: heading should be displayed").isTrue();
        assertThat(page.isTrackButtonEnabled())
                .as("Track order: submit should be disabled while the input is empty").isFalse();
        page.trackOrder("INVALID-0000");
        assertThat(page.getErrorMessage())
                .as("Track order: an unknown id should show the not-found error")
                .containsIgnoringCase("Order not found");
    }
}
