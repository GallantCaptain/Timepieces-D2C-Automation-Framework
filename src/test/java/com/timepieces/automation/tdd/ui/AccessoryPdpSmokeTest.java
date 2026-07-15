package com.timepieces.automation.tdd.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.pages.web.StagingGatePage;
import com.timepieces.automation.pages.web.products.accessories.SharedAccessoryPdp;
import com.timepieces.automation.pages.web.products.accessories.clasps.butterfly.ButterflyClaspGold;
import com.timepieces.automation.pages.web.products.accessories.clasps.singleDeployment.SingleDeploymentClaspRoseGold20mm;
import com.timepieces.automation.pages.web.products.accessories.straps.SharedStrapPdp;
import com.timepieces.automation.pages.web.products.accessories.straps.fkmRubber.FKMRubberStrapBlack;
import com.timepieces.automation.pages.web.products.accessories.straps.ostrichLeather.OstrichLeatherStrapBlack;
import com.timepieces.automation.pages.web.products.accessories.watchCases.PremiumWatchCaseBlack;

import lombok.extern.log4j.Log4j2;

/**
 * AccessoryPdpSmokeTest — a TDD (TestNG) smoke that drives the accessory Page
 * Objects against the live storefront and asserts the shared accessory PDP shape
 * (product title, an ENABLED "Add to cart", image gallery, wishlist) plus the
 * strap-only dial-try carousel + clasp-upgrade card. One representative of each
 * accessory type is exercised (watch case, butterfly clasp, single-deployment
 * clasp, rubber strap, leather strap).
 *
 * <p>
 * OPT-IN: this class is NOT part of the default {@code testng.xml} suite (it
 * needs a real browser and reaches the live site). Run it explicitly via its own
 * suite:
 * {@code mvnw -o test "-Dsurefire.suite=testng-accessory-smoke.xml" "-Denv=production"}.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of AccessoryPdpSmokeTest"
* @since 2026-07-15
 */

@Log4j2
public class AccessoryPdpSmokeTest {

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
     * Opens the given accessory PDP and transparently unlocks the non-prod staging
     * gate (re-navigating once) so the assertions behave identically across envs.
     */
    private <T extends SharedAccessoryPdp<T>> T openAccessory(T page) {
        page.open();
        if (new StagingGatePage(driver).unlockIfPresent()) {
            page.open();
        }
        return page;
    }

    /** Asserts the shared accessory PDP core is present and purchasable. */
    private void assertPurchasableCore(SharedAccessoryPdp<?> pdp, String label) {
        assertThat(pdp.isProductTitleDisplayed())
                .as("%s: product title should be displayed", label).isTrue();
        assertThat(pdp.isImageGalleryDisplayed())
                .as("%s: image gallery should be displayed", label).isTrue();
        assertThat(pdp.isAddToCartDisplayed())
                .as("%s: 'Add to cart' should be displayed", label).isTrue();
        assertThat(pdp.isAddToCartEnabled())
                .as("%s: 'Add to cart' should be enabled (accessories are purchasable)", label).isTrue();
        assertThat(pdp.isWishlistButtonDisplayed())
                .as("%s: wishlist button should be displayed", label).isTrue();
    }

    // ==========================================
    // Tests
    // ==========================================
    @Test(groups = { "ui-smoke", "accessories" })
    public void watchCasePdpIsPurchasable() {
        assertPurchasableCore(openAccessory(new PremiumWatchCaseBlack(driver)), "Premium Watch Case");
    }

    @Test(groups = { "ui-smoke", "accessories" })
    public void butterflyClaspPdpIsPurchasable() {
        ButterflyClaspGold pdp = openAccessory(new ButterflyClaspGold(driver));
        assertPurchasableCore(pdp, "Butterfly Clasp Gold");
        assertThat(pdp.isVariantSwitcherDisplayed())
                .as("Butterfly Clasp: color card-button switcher should be displayed").isTrue();
    }

    @Test(groups = { "ui-smoke", "accessories" })
    public void singleDeploymentClaspPdpIsPurchasable() {
        SingleDeploymentClaspRoseGold20mm pdp = openAccessory(new SingleDeploymentClaspRoseGold20mm(driver));
        assertPurchasableCore(pdp, "Single Deployment Clasp Rose Gold 20mm");
        assertThat(pdp.isVariantSwitcherDisplayed())
                .as("SD Clasp: color/size card-button switcher should be displayed").isTrue();
    }

    @Test(groups = { "ui-smoke", "accessories", "straps" })
    public void rubberStrapPdpHasConfigurator() {
        FKMRubberStrapBlack pdp = openAccessory(new FKMRubberStrapBlack(driver));
        assertPurchasableCore(pdp, "FKM Rubber Strap Black");
        assertStrapExtras(pdp, "FKM Rubber Strap Black");
    }

    @Test(groups = { "ui-smoke", "accessories", "straps" })
    public void leatherStrapPdpHasConfigurator() {
        OstrichLeatherStrapBlack pdp = openAccessory(new OstrichLeatherStrapBlack(driver));
        assertPurchasableCore(pdp, "Ostrich Leather Strap Black");
        assertStrapExtras(pdp, "Ostrich Leather Strap Black");
    }

    /** Asserts the strap-only dial-try carousel and clasp-upgrade card are present. */
    private void assertStrapExtras(SharedStrapPdp<?> strap, String label) {
        assertThat(strap.isDialTryCarouselDisplayed())
                .as("%s: dial-try carousel should be displayed", label).isTrue();
        assertThat(strap.isClaspUpgradeCardDisplayed())
                .as("%s: clasp-upgrade cross-sell card should be displayed", label).isTrue();
    }
}
