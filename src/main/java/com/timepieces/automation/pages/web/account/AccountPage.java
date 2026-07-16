package com.timepieces.automation.pages.web.account;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.HeaderOnlyBasePage;
import com.timepieces.automation.pages.web.StagingGatePage;
import com.timepieces.automation.utils.BrowserUtils;
import com.timepieces.automation.utils.ConfigReader;

import lombok.extern.log4j.Log4j2;

/**
 * AccountPage: https://www.timepieces-d2c.example/account
 * This is the Page Object Repository for the Timepieces Account Page (/account).
 *
 * This page is displayed after a successful login (returning users) or after
 * completing the onboarding flow (new users). It shows the user's profile
 * summary, profile completion status, and provides navigation to the
 * profile edit page.
 *
 * Contains links to the Profile Page, Order History page,
 * Owned Timepieces page, and Wishlist Page. There is an option to logout of
 * the website using the logout button. The progile photo, Name, Designation,
 * brief description are showcased in the account page if filled prior during
 * the onboarding journey.
 * 
 * This page only contains the Header.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (AccountPage) to
 * allow
 * method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of AccountPage"
* @since 2026-07-03
 */

@Log4j2
public class AccountPage extends HeaderOnlyBasePage<AccountPage> {

    // ==========================================
    // Constructor
    // ==========================================
    public AccountPage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // When the profile is incomplete
    // Profile Image Container:
    private final By profileImageContainer = By
            .xpath("//span[contains(@class,'text-white')]/parent::div[contains(@class,'border')]");

    // Incomplete Profile Message:
    private final By incompleteProfileMessage = By.xpath(
            "//div[@class='mt-6 p-4 rounded-xl border border-[#3a3a1a] bg-gradient-to-r from-[#2a2a0a] to-[#1a1a0a]']");

    // When the profile is complete
    // Profile Image Container [Uploaded Photo]:
    private final By accountPageProfileImageContainer = By.cssSelector("img[alt='Profile']");

    /*
     * Image State Assertion in Profile Page can be done by checking the presence of
     * the profile image element with the alt text 'Profile' and its src attribute
     * to confirm that a photo has been uploaded, as opposed to the default
     * placeholder image which would have a different src or might not be present at
     * all.)
     */
    private final By accountPageProfileImage = By.xpath("//div[contains(@class,'rounded-full')]//img");

    // --- Profile summary (2026-07-16 scrape; no data-testids on the body) ---
    // The profile name heading.
    private final By profileNameHeading = By.cssSelector("h1.text-white.text-xl.font-semibold"); // ROLLOUT-GAP: utility-class
    // The designation/profession line under the name.
    private final By designationText = By.cssSelector("p.text-gray-400.text-sm.mt-1"); // ROLLOUT-GAP: utility-class
    // The LinkedIn profile link inside the name heading.
    private final By linkedInLink = By.cssSelector("h1 a[href*='linkedin.com']"); // ROLLOUT-GAP: href-anchor
    // The "Export Profile" button.
    private final By exportProfileButton = By.xpath("//button[contains(normalize-space(),'Export Profile')]"); // ROLLOUT-GAP: text-anchor
    // The direct "Edit profile" link (→ /account/profile).
    private final By editProfileLink = By.cssSelector("a[href='/account/profile']"); // ROLLOUT-GAP: href-anchor

    // --- Account navigation (2026-07-16 scrape) ---
    // The "Owned timepieces" nav link (→ /account/owned-watches).
    private final By ownedTimepiecesLink = By.cssSelector("a[href='/account/owned-watches']"); // ROLLOUT-GAP: href-anchor
    // The "Orders" nav link (→ /account/orders).
    private final By ordersLink = By.cssSelector("a[href='/account/orders']"); // ROLLOUT-GAP: href-anchor
    // The "Wishlist" nav link (→ the same guest /wishlist page).
    private final By wishlistLink = By.cssSelector("a[href='/wishlist']"); // ROLLOUT-GAP: href-anchor
    // The "Logout" button (red label row).
    private final By logoutButton = By.xpath("//button[.//span[normalize-space()='Logout'] or normalize-space()='Logout']"); // ROLLOUT-GAP: text-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================

    /**
     * B.1 Navigates to the Timepieces account page and waits for the profile image
     * container to confirm it has fully loaded. The base URL is sourced from config
     * so it works across environments (staging, prod) without touching test code.
     *
     * @return this AccountPage for method chaining.
     */
    public AccountPage open() {
        String baseUrl = ConfigReader.get("base.url");
        String accountUrl = baseUrl + "/account";
        log.info("Navigating to account page: {}", accountUrl);
        BrowserUtils.navigateTo(driver, accountUrl, 15);

        // Non-prod environments may sit behind a password gate. Unlock transparently
        // and re-navigate so the load-confirmation wait below behaves identically
        // across envs.
        if (new StagingGatePage(driver).unlockIfPresent()) {
            BrowserUtils.navigateTo(driver, accountUrl, 15);
        }

        waitForVisibility(profileImageContainer);
        log.info("Account page loaded and profile image container confirmed visible.");
        return this;
    }

    /**
     * B.2 Clicks the incomplete-profile message banner to navigate to the
     * profile edit page. The banner acts as a link to {@code /account/profile}.
     *
     * @return a new {@link ProfilePage} representing the edit-profile destination.
     */
    public ProfilePage clickEditProfile() {
        log.info("Clicking incomplete profile message banner to navigate to Edit Profile.");
        clickElement(incompleteProfileMessage);
        log.info("Navigated to Profile page.");
        return new ProfilePage(driver);
    }

    /**
     * B.3 Clicks the direct "Edit profile" link (→ {@code /account/profile}) —
     * present regardless of profile-completion state, unlike the banner.
     *
     * @return a new {@link ProfilePage} representing the edit-profile destination.
     */
    public ProfilePage clickEditProfileLink() {
        log.info("Clicking the 'Edit profile' link.");
        clickElement(editProfileLink);
        return new ProfilePage(driver);
    }

    /**
     * B.4 Clicks the "Orders" nav link (→ {@code /account/orders}).
     *
     * @return this page instance for method chaining.
     */
    public AccountPage clickOrders() {
        log.info("Navigating to Order History via the account nav.");
        clickElement(ordersLink);
        return this;
    }

    /**
     * B.5 Clicks the "Owned timepieces" nav link (→ {@code /account/owned-watches}).
     *
     * @return this page instance for method chaining.
     */
    public AccountPage clickOwnedTimepieces() {
        log.info("Navigating to Owned Timepieces via the account nav.");
        clickElement(ownedTimepiecesLink);
        return this;
    }

    /**
     * B.6 Clicks the "Wishlist" nav link (→ the shared guest {@code /wishlist}
     * page).
     *
     * @return this page instance for method chaining.
     */
    public AccountPage clickWishlist() {
        log.info("Navigating to the Wishlist via the account nav.");
        clickElement(wishlistLink);
        return this;
    }

    /**
     * B.7 Clicks the "Export Profile" button.
     *
     * @return this page instance for method chaining.
     */
    public AccountPage clickExportProfile() {
        log.info("Clicking 'Export Profile'.");
        clickElement(exportProfileButton);
        return this;
    }

    /**
     * B.8 Clicks the "Logout" button, ending the session.
     *
     * @return this page instance for method chaining.
     */
    public AccountPage clickLogout() {
        log.info("Logging out via the account page.");
        clickElement(logoutButton);
        return this;
    }

    /**
     * B.9 Returns the profile name heading text.
     *
     * @return the trimmed profile name.
     */
    public String getProfileName() {
        return getText(profileNameHeading);
    }

    /**
     * B.10 Returns the designation/profession line text.
     *
     * @return the trimmed designation text.
     */
    public String getDesignation() {
        return getText(designationText);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /*
     * These return boolean so step definitions can pass them to assertion libraries
     * (AssertJ, TestNG, JUnit) without the page object having a dependency on any
     * assertion framework.
     */

    /**
     * V.1 Verifies that the account page has loaded by checking whether
     * the profile image container (incomplete state) or the uploaded profile
     * image container (complete state) is displayed.
     *
     * @return true if the account page is loaded, false otherwise.
     */
    public boolean isAccountPageLoaded() {
        boolean loaded = isDisplayed(profileImageContainer) || isDisplayed(accountPageProfileImageContainer);
        if (loaded) {
            log.info("Verified: Account page is loaded.");
        } else {
            log.warn("Account page does not appear to be loaded — neither profile container is displayed.");
        }
        return loaded;
    }

    /**
     * V.2 Verifies whether the incomplete-profile message banner is displayed,
     * indicating the user has not yet completed their profile.
     *
     * @return true if the incomplete profile message is displayed, false otherwise.
     */
    public boolean isProfileIncomplete() {
        boolean incomplete = isDisplayed(incompleteProfileMessage);
        if (incomplete) {
            log.info("Verified: Incomplete profile message is displayed.");
        } else {
            log.warn("Incomplete profile message is not displayed.");
        }
        return incomplete;
    }

    /**
     * V.3 Verifies whether a user-uploaded profile image is displayed on the
     * account page (the {@code img[alt='Profile']} element).
     *
     * @return true if the uploaded profile image is displayed, false otherwise.
     */
    public boolean isProfileImageUploaded() {
        boolean uploaded = isDisplayed(accountPageProfileImageContainer);
        if (uploaded) {
            log.info("Verified: Uploaded profile image is displayed.");
        } else {
            log.warn("Uploaded profile image is not displayed.");
        }
        return uploaded;
    }

    /**
     * V.4 Verifies all four account nav entries are present (Orders, Owned
     * timepieces, Wishlist, Logout).
     *
     * @return true if every nav entry is present.
     */
    public boolean isAccountNavComplete() {
        boolean complete = !driver.findElements(ordersLink).isEmpty()
                && !driver.findElements(ownedTimepiecesLink).isEmpty()
                && !driver.findElements(wishlistLink).isEmpty()
                && !driver.findElements(logoutButton).isEmpty();
        if (!complete) {
            log.warn("One or more account nav entries are missing.");
        }
        return complete;
    }

    /**
     * V.5 Verifies the "Logout" button is displayed.
     *
     * @return true if the logout button is displayed.
     */
    public boolean isLogoutDisplayed() {
        return isDisplayed(logoutButton);
    }

    /**
     * V.6 Verifies the profile name heading is displayed.
     *
     * @return true if the profile name is displayed.
     */
    public boolean isProfileNameDisplayed() {
        return isDisplayed(profileNameHeading);
    }

    /**
     * V.7 Reports whether the profile name links out to LinkedIn.
     *
     * @return true if a LinkedIn link is present in the name heading.
     */
    public boolean hasLinkedInLink() {
        return !driver.findElements(linkedInLink).isEmpty();
    }

    /**
     * V.8 Verifies the "Export Profile" button is displayed.
     *
     * @return true if the export button is displayed.
     */
    public boolean isExportProfileDisplayed() {
        return isDisplayed(exportProfileButton);
    }
}
