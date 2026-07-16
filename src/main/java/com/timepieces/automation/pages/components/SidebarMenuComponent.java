package com.timepieces.automation.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * SidebarMenuComponent represents the navigation sidebar menu card drawer.
 * 
 * The Sidebar Menu overlay contains:
 * - The Timepieces logo at the top.
 * - A close button.
 * - The main website page links.
 * - A sub-menu for Timepieces.
 * - A sub-menu for Accessories.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (SidebarMenuComponent) to allow method chaining in step definitions.
 * 
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SidebarMenuComponent"
 */

@Log4j2
public class SidebarMenuComponent extends BasePage<SidebarMenuComponent> {

        // ==========================================
        // Constructor
        // ==========================================
        public SidebarMenuComponent(WebDriver driver) {
                super(driver);
        }

        // ==========================================
        // LOCATORS
        // ==========================================
        // Sidebar Overlay:
        private final By sidebarOverlay = By.cssSelector("div[data-testid='nav-sidebar-overlay']");

        // Whole Sidebar Container:
        private final By sidebarContainer = By.cssSelector("div[data-testid='nav-sidebar']");

        // Timepieces Logo inside Sidebar:
        private final By sidebarTimepiecesLogo = By.cssSelector("a[data-testid='nav-sidebar-logo']");

        // Close Sidebar Menu Button:
        // Can click outside the overlay to dismiss the SidebarMenu overlay too
        private final By closeButton = By.cssSelector("button[data-testid='nav-sidebar-close']");

        // Website Major Pages Links Menu List: [Can use a list for this]
        private final By menuLinksList = By.cssSelector("ul[data-testid='nav-sidebar-menu']");

        // Menu List Items: [Can use a list for this] (Not unique. Contains the 7 links.
        // All the major links have been mapped as unique locators below.)
        private final By menuListItems = By.cssSelector("li[data-testid='nav-sidebar-menu-item']");

        /*
         * The timepieces and accessories in the sidebar menu dont have direct links.
         * They have a button with them that opens yet another sub-menu overlay within
         * the SideBarOverlay
         */
        /*
         * Shared sub menu overlay locator between accessories and timepieces. Only 1 of
         * the submenu overlays can be active at a time. Pressing the other button
         * (accessories/timepieces) switches.
         */
        // ------Sub-Menu-------//
        // The products (timepieces & accessories - share this) submenu that opens after
        // clicking on Timepieces/Accessories button in desktop viewport
        private final By productsSubMenu_In_SidebarMenu_Desktop = By
                        .cssSelector("div[data-testid='nav-submenu-desktop']");

        // The products (timepieces & accessories - share this) submenu that opens after
        // clicking on Timepieces/Accessories button in mobile viewport
        private final By productsSubMenu_In_SidebarMenu_Mobile = By
                        .cssSelector("div[data-testid='nav-submenu-mobile']");

        // Timepieces Menu Button: [Agnostic Viewport]
        private final By timepiecesMenuItemButton = By
                        .cssSelector("button[data-testid='nav-sidebar-menu-button'][data-menu-title='Timepieces']");

        /*
         * The timepiece submenu header logo link and menu link redirect to the exact
         * same page -> All Timepieces Collection page
         */
        // The Timepieces Collection SubMenu Header Logo Link on the the Accessories
        // submenu
        private final By timepiecesCollection_HeaderLogo_PageLinkDesktop = By
                        .cssSelector("a[href='/accessories/all'][data-testid='nav-submenu-parent-desktop']");

        // The Timepieces Collection SubMenu Header Logo Link on the the Accessories
        // submenu
        private final By timepiecesCollection_HeaderLogo_PageLinkMobile = By
                        .cssSelector("a[href='/accessories/all'][data-testid='nav-submenu-parent-desktop']");

        // The All Timepieces Collection Menu Link in SubMenu in desktop viewport
        private final By allTimepiecesCollectionPageLinkDesktop = By
                        .cssSelector("a[href='/collections/timepieces'][data-testid='nav-submenu-link-desktop']");

        // The All Timepieces Collection Menu Link in SubMenu in mobile viewport
        private final By allTimepiecesCollectionPageLinkMobile = By
                        .cssSelector("a[href='/collections/timepieces'][data-testid='nav-submenu-link']");

        // Q-Matic Timepieces Menu Link in SubMenu in desktop viewport
        private final By qMaticTimepiecesCollectionPageLinkDesktop = By
                        .cssSelector("a[href='/collections/q-matic-timepiece'][data-testid='nav-submenu-link-desktop']");

        // The Q-Matic Timepieces Collection Menu Link in SubMenu in mobile viewport
        private final By qMaticTimepiecesCollectionPageLinkMobile = By
                        .cssSelector("a[href='/collections/q-matic-timepieces'][data-testid='nav-submenu-link']");

        // Automatic Timepieces Menu Link in SubMenu in desktop viewport
        private final By automaticTimepiecesCollectionPageLinkDesktop = By
                        .cssSelector("a[href='/collections/automatic'][data-testid='nav-submenu-link-desktop']");

        // Automatic Timepieces Menu Link in SubMenu in desktop viewport
        private final By automaticTimepiecesCollectionPageLinkMobile = By
                        .cssSelector("a[href='/collections/automatic'][data-testid='nav-submenu-link']");

        // Accessories Menu Button: [Agnostic Viewport]
        private final By accessoriesMenuItemButton = By
                        .cssSelector("button[data-testid='nav-sidebar-menu-button'][data-menu-title='Accessories']");

        /*
         * The accessories submenu header logo link and menu link redirect to the exact
         * same page -> All Accessories Collection page
         */
        // The Accessories Collection SubMenu Header Logo Link on the the Accessories
        // submenu
        private final By accessoriesCollection_HeaderLogo_PageLinkDesktop = By
                        .cssSelector("a[href='/accessories/all'][data-testid='nav-submenu-parent-desktop']");

        // The Accessories Collection SubMenu Header Logo Link on the the Accessories
        // submenu
        private final By accessoriesCollection_HeaderLogo_PageLinkMobile = By
                        .cssSelector("a[href='/accessories/all'][data-testid='nav-submenu-parent-desktop']");

        // The straps collection page in the Accessories submenu in mobile viewport
        private final By watchCaseCollectionPageLinkMobile = By
                        .cssSelector("a[href='/accessories/watch-case'][data-testid='nav-submenu-link']");

        // Back Button from Products SubMenu to SidebarMenu desktop viewport
        // Users can also get back to the SidebarMenu by clickling outside of the
        // Sidebar overlay when in the SubMenu, but if clicking anywhere in the
        // SidebarContainer, then it won't be dismissed
        private final By backButtonProductsSubMenu_To_SidebarMenu_Desktop = By
                        .cssSelector("button[data-testid='nav-submenu-back-desktop']");

        // Back Button from Products SubMenu to SidebarMenu mobile viewport
        // Users can also get back to the SidebarMenu by clickling outside of the
        // Sidebar overlay when in the SubMenu, but if clicking anywhere in the
        // SidebarContainer, then it won't be dismissed
        private final By backButtonProductsSubMenu_To_SidebarMenu_Mobile = By
                        .cssSelector("button[data-testid='nav-submenu-back-mobile']");

        // --------Back In The SidebarMenu---------//
        // Friends & Partners Menu Link:
        private final By friendsAndPartnersPageMenuItemLink = By
                        .cssSelector("a[href='/friends-and-partners'][data-testid='nav-sidebar-menu-link']");

        // Timepieces House Menu Link:
        private final By timepiecesHousePageMenuItemLink = By
                        .cssSelector("a[href='/experience-center'][data-testid='nav-sidebar-menu-link']");

        // The Wall Menu Link:
        private final By theWallMenuPageItemLink = By
                        .cssSelector("a[href='/timepieces-wall'][data-testid='nav-sidebar-menu-link']");

        // Updates Menu Link:
        private final By updatesMenuPageItemLink = By
                        .cssSelector("a[href='/updates'][data-testid='nav-sidebar-menu-link']");

        // Timepieces World Menu Link:
        private final By timepiecesWorldPageMenuItemLink = By
                        .cssSelector("a[href='https://world.timepieces-d2c.example'][data-testid='nav-sidebar-menu-link']");

        // Menu Item Button: [Can use a list for this]
        private final By menuItemButton = By.cssSelector("button[data-testid='nav-sidebar-menu-button']");

        // Menu Item Link:
        private final By menuItemLink = By.cssSelector("a[data-testid='nav-sidebar-menu-link']");

        // Label: Become a Timepieces member label
        // This will be helpful in validating the user login status alongwith the
        // sign-in container status, i.e., this label is only there when the user is not
        // logged in.
        private final By becomeTimepiecesMember_Message_Label = By.xpath(
                        "//p[contains(normalize-space(),'Become a Timepieces member for the latest timepiece updates, inspiration and stories')]");

        // Sign In Button: -- When user is not logged in --
        // Button is replaced by logged in user container
        private final By signInButton = By.cssSelector("button[data-testid='nav-sidebar-signin']");

        /*
         * Logged In User Container. This contains the user name, designation, and
         * profile picture. This container will be available only when the user has
         * logged in.
         */
        // The Logged In User Container
        private final By loggedInUserProfileContainer = By.cssSelector("a[data-testid='nav-sidebar-account-link']");

        // If the user has uploaded a profile photo then it is shown here
        // If user has not uploaded any photo, then the locator will not find anything.
        // It is replaced by the user initials in the photo container.
        private final By userPhoto_In_ProfileContainer = By.cssSelector("img[alt='User avatar']");

        // If user has not uploaded photo or other parts of their account details are
        // incomplete, then this message label is shown
        private final By incompleteProfileMessageLabel_In_ProfileContainer = By
                        .xpath("//span[contains(normalize-space(),'Profile not complete')]");

        // --- Logged-in container CHILDREN (2026-07-16 auth scrape; closes DEBT-008) ---
        // The avatar wrapper inside the profile container.
        private final By avatarWrapper_In_ProfileContainer = By
                        .cssSelector("a[data-testid='nav-sidebar-account-link'] div.w-13.h-13"); // ROLLOUT-GAP: structural

        // The user's display name span.
        private final By userName_In_ProfileContainer = By
                        .cssSelector("a[data-testid='nav-sidebar-account-link'] span.font-timepieces-semibold"); // ROLLOUT-GAP: utility-class

        // The user's designation/persona span (e.g. "Professional").
        private final By userDesignation_In_ProfileContainer = By
                        .cssSelector("a[data-testid='nav-sidebar-account-link'] span.font-timepieces-regular"); // ROLLOUT-GAP: utility-class

        // The chevron-right affordance on the profile container.
        private final By chevron_In_ProfileContainer = By
                        .cssSelector("a[data-testid='nav-sidebar-account-link'] svg"); // ROLLOUT-GAP: structural

        // Contact Link:
        private final By contactLink = By.cssSelector("button[data-testid='nav-sidebar-contact']");

        // Wishlist Link:
        private final By wishlistLink = By.cssSelector("button[data-testid='nav-sidebar-wishlist']");

        // Track Orders Link: [Track Orders is not available for Logged In Users]
        private final By trackOrdersLink = By.cssSelector("button[data-testid='nav-sidebar-track-orders']");

        // Order Link: [This replaces Track Orders Link when Logged In and redirects the
        // user to their OrderHistoryPage (/account/orders)]
        private final By orderPageLink = By.cssSelector("a[href='/account/orders'][data-testid='nav-sidebar-orders']");

        /*
         * The region selector toggle impacts the product prices as well as the prices
         * and languages, although the language features are yet to be implemented. The
         * language features are planned to be implemented sometime in 2027 and is
         * currently low priority. The toggle will be helpful for ui/language validation
         * tests. Separate language files for the ui verification will be added in the
         * configuration directory as and when required.
         */
        // Country Selector Toggle Button
        private final By countryToggleButton = By.cssSelector("button[data-testid='country-selector-toggle']");

        // Country Selector Container
        private final By countryToggleContainer = By.cssSelector("div[data-testid='country-selector-dropdown']");

        // Search Field inside the Country Selector Container
        private final By searchField_In_CountryToggleContainer = By
                        .cssSelector("input[data-testid='country-selector-search']");

        /*
         * Match both the country name and the data-country-code attribute to ensure
         * that the exact country/region is selected. Country Phone Code is not
         * applicable, only the alphabetical code. E.g.: India 'IN' and British Indian
         * Ocean Territory 'IO'. In this case, the British Indian Ocean Territory 'IO'
         * is retrieved first in the list and will return the wrong value if only
         * matched with the country name, if the intention was to get the country
         * 'India'.
         */
        // The Countries in the Country Selector Container (Total 245) [Use list to
        // retrieve the list]
        private final By countrySelector_In_CountryToggleContainer = By
                        .cssSelector("button[data-testid='country-selector-option']");

        // ==========================================
        // PRIVATE HELPERS
        // ==========================================

        // ==========================================
        // BUSINESS LIBRARY
        // ==========================================
        /**
         * B.1 Clicks the close button to dismiss the sidebar.
         * 
         * @return this SidebarMenuComponent for method chaining.
         */
        public SidebarMenuComponent clickCloseButton() {
                log.info("Clicking sidebar close button.");
                clickElement(closeButton);
                return this;
        }

        /**
         * B.2 Clicks on the Sign In button inside the sidebar.
         * 
         * @return this SidebarMenuComponent for method chaining.
         */
        public SidebarMenuComponent clickSignIn() {
                log.info("Clicking sidebar sign in button.");
                clickElement(signInButton);
                return this;
        }

        /**
         * B.3 Clicks on the Contact Us link inside the sidebar.
         *
         * @return this SidebarMenuComponent for method chaining.
         */
        public SidebarMenuComponent clickContact() {
                log.info("Clicking sidebar contact link.");
                clickElement(contactLink);
                return this;
        }

        /**
         * B.4 Clicks the Timepieces logo at the top of the sidebar (navigates to the
         * homepage).
         *
         * @return this SidebarMenuComponent for method chaining.
         */
        public SidebarMenuComponent clickTimepiecesLogo() {
                log.info("Clicking sidebar Timepieces logo.");
                clickElement(sidebarTimepiecesLogo);
                return this;
        }

        /**
         * B.5 Opens the Timepieces sub-menu overlay. Viewport-agnostic — the same
         * button drives both the desktop and mobile sub-menu; assert on the
         * matching sub-menu locator afterwards.
         *
         * @return this SidebarMenuComponent for method chaining.
         */
        public SidebarMenuComponent openTimepiecesSubMenu() {
                log.info("Opening the Timepieces sub-menu.");
                clickElement(timepiecesMenuItemButton);
                return this;
        }

        /**
         * B.6 Opens the Accessories sub-menu overlay. Viewport-agnostic — the same
         * button drives both the desktop and mobile sub-menu. Only one products
         * sub-menu is active at a time, so this switches away from the Timepieces
         * sub-menu if it was open.
         *
         * @return this SidebarMenuComponent for method chaining.
         */
        public SidebarMenuComponent openAccessoriesSubMenu() {
                log.info("Opening the Accessories sub-menu.");
                clickElement(accessoriesMenuItemButton);
                return this;
        }

        /**
         * B.7 Returns from the products sub-menu back to the top-level sidebar menu
         * (desktop button).
         *
         * @return this SidebarMenuComponent for method chaining.
         */
        public SidebarMenuComponent backToSidebarMenuFromSubMenu() {
                log.info("Returning from products sub-menu to the sidebar menu.");
                clickElement(backButtonProductsSubMenu_To_SidebarMenu_Desktop);
                return this;
        }

        /**
         * B.8 Clicks the Wishlist link inside the sidebar.
         *
         * @return this SidebarMenuComponent for method chaining.
         */
        public SidebarMenuComponent clickWishlist() {
                log.info("Clicking sidebar wishlist link.");
                clickElement(wishlistLink);
                return this;
        }

        /**
         * B.9 Clicks the Track Orders link (visible for guests only; logged-in users
         * see the Orders link instead — see {@link #clickOrders()}).
         *
         * @return this SidebarMenuComponent for method chaining.
         */
        public SidebarMenuComponent clickTrackOrders() {
                log.info("Clicking sidebar track orders link.");
                clickElement(trackOrdersLink);
                return this;
        }

        /**
         * B.10 Clicks the Orders link (logged-in users only; redirects to the order
         * history page).
         *
         * @return this SidebarMenuComponent for method chaining.
         */
        public SidebarMenuComponent clickOrders() {
                log.info("Clicking sidebar orders link.");
                clickElement(orderPageLink);
                return this;
        }

        /**
         * B.11 Clicks the logged-in user's profile/account link in the sidebar
         * (redirects to /account).
         * Only present when a user is authenticated — guard with
         * {@link #isUserLoggedIn()}.
         *
         * @return this SidebarMenuComponent for method chaining.
         */
        public SidebarMenuComponent clickAccountProfile() {
                log.info("Clicking sidebar logged-in account/profile link.");
                clickElement(loggedInUserProfileContainer);
                return this;
        }

        /**
         * B.12 Opens the country/region selector dropdown inside the sidebar.
         *
         * @return this SidebarMenuComponent for method chaining.
         */
        public SidebarMenuComponent openCountrySelector() {
                log.info("Opening the country/region selector.");
                clickElement(countryToggleButton);
                return this;
        }

        // ==========================================
        // VERIFICATION METHODS
        // ==========================================
        /**
         * V.1 Verifies if the sidebar container is displayed.
         * 
         * @return true if displayed, false otherwise.
         */
        public boolean isSidebarDisplayed() {
                boolean displayed = isDisplayed(sidebarContainer);
                log.info("Sidebar Container displayed check: {}", displayed);
                return displayed;
        }

        /**
         * V.2 Verifies if the close button is displayed.
         *
         * @return true if displayed, false otherwise.
         */
        public boolean isCloseButtonDisplayed() {
                boolean displayed = isDisplayed(closeButton);
                log.info("Sidebar Close Button displayed check: {}", displayed);
                return displayed;
        }

        /**
         * V.3 Verifies if the Sign In button is displayed. Present for guests; replaced
         * by the logged-in user container after authentication.
         *
         * @return true if displayed, false otherwise.
         */
        public boolean isSignInButtonDisplayed() {
                boolean displayed = isDisplayed(signInButton);
                log.info("Sidebar Sign In button displayed check: {}", displayed);
                return displayed;
        }

        /**
         * V.4 Determines whether a user is currently logged in, from the sidebar's
         * perspective.
         *
         * The sidebar swaps the Sign In button + "Become a Timepieces member" prompt for
         * the logged-in user profile container once authenticated. This uses a fast,
         * non-blocking {@code findElements} presence check on the profile container
         * so it adds no wait when the user is a guest.
         *
         * @return true if the logged-in user profile container is present, false
         *         otherwise.
         */
        public boolean isUserLoggedIn() {
                boolean loggedIn = !driver.findElements(loggedInUserProfileContainer).isEmpty();
                log.info("Sidebar user-logged-in check: {}", loggedIn);
                return loggedIn;
        }

        /**
         * V.5 Verifies the "Become a Timepieces member" prompt is displayed — the
         * guest-state marker that accompanies the
         * Sign In button (absent once logged in).
         *
         * @return true if displayed, false otherwise.
         */
        public boolean isBecomeMemberPromptDisplayed() {
                boolean displayed = isDisplayed(becomeTimepiecesMember_Message_Label);
                log.info("Sidebar 'Become a Timepieces member' prompt displayed check: {}", displayed);
                return displayed;
        }

        /**
         * V.6 Verifies the Timepieces logo inside the sidebar is displayed.
         *
         * @return true if displayed, false otherwise.
         */
        public boolean isSidebarLogoDisplayed() {
                boolean displayed = isDisplayed(sidebarTimepiecesLogo);
                log.info("Sidebar Timepieces logo displayed check: {}", displayed);
                return displayed;
        }

        /**
         * V.7 Verifies the products (Timepieces/Accessories) sub-menu overlay is
         * displayed in the desktop viewport. Call after
         * {@link #openTimepiecesSubMenu()} or {@link #openAccessoriesSubMenu()}.
         *
         * @return true if displayed, false otherwise.
         */
        public boolean isProductsSubMenuDisplayed() {
                boolean displayed = isDisplayed(productsSubMenu_In_SidebarMenu_Desktop);
                log.info("Sidebar products sub-menu displayed check: {}", displayed);
                return displayed;
        }

        /**
         * V.8 Returns the number of top-level menu items currently rendered in the
         * sidebar. Useful for asserting the navigation list is fully populated.
         *
         * @return the count of menu items (0 if none are present).
         */
        public int getMenuItemCount() {
                int count = driver.findElements(menuListItems).size();
                log.info("Sidebar menu item count: {}", count);
                return count;
        }

        /**
         * V.9 Returns the logged-in user's display name from the profile container
         * (2026-07-16 auth scrape — closes DEBT-008's container children).
         *
         * @return the trimmed display name.
         */
        public String getLoggedInUserName() {
                return getText(userName_In_ProfileContainer);
        }

        /**
         * V.10 Returns the logged-in user's designation/persona label
         * (e.g. "Professional").
         *
         * @return the trimmed designation text.
         */
        public String getLoggedInUserDesignation() {
                return getText(userDesignation_In_ProfileContainer);
        }

        /**
         * V.11 Verifies the profile container renders its full child set: avatar
         * wrapper, display name, designation and the chevron affordance.
         *
         * @return true if every profile-container child is present.
         */
        public boolean isProfileContainerComplete() {
                boolean complete = !driver.findElements(avatarWrapper_In_ProfileContainer).isEmpty()
                                && !driver.findElements(userName_In_ProfileContainer).isEmpty()
                                && !driver.findElements(userDesignation_In_ProfileContainer).isEmpty()
                                && !driver.findElements(chevron_In_ProfileContainer).isEmpty();
                log.info("Sidebar profile-container children complete check: {}", complete);
                return complete;
        }

        /**
         * V.12 Verifies the sidebar renders the GUEST state: Sign In + the
         * become-member prompt + Track Orders present, and no profile container.
         *
         * @return true if the sidebar is in its guest state.
         */
        public boolean isSidebarInGuestState() {
                boolean guest = !driver.findElements(signInButton).isEmpty()
                                && !driver.findElements(trackOrdersLink).isEmpty()
                                && driver.findElements(loggedInUserProfileContainer).isEmpty();
                log.info("Sidebar guest-state check: {}", guest);
                return guest;
        }

        /**
         * V.13 Verifies the sidebar renders the AUTHENTICATED state: the profile
         * container + the Orders link present, and no Sign In / Track Orders.
         *
         * @return true if the sidebar is in its authenticated state.
         */
        public boolean isSidebarInAuthenticatedState() {
                boolean authed = !driver.findElements(loggedInUserProfileContainer).isEmpty()
                                && !driver.findElements(orderPageLink).isEmpty()
                                && driver.findElements(signInButton).isEmpty()
                                && driver.findElements(trackOrdersLink).isEmpty();
                log.info("Sidebar authenticated-state check: {}", authed);
                return authed;
        }
}
