package com.timepieces.automation.pages.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * FooterComponent class represents the footer section of the web application.
 * It extends the BasePage class, which provides common functionalities for all
 * page components. The FooterComponent class contains a WebDriver instance to
 * interact with the web elements in the footer section. The constructor
 * initializes the WebDriver instance when an object of FooterComponent is
 * created.
 */
@Log4j2 // Lombok logger annotation, Takes care of the logger instances
public class FooterComponent extends BasePage<FooterComponent> {

        // ==========================================
        // Constructor
        // ==========================================
        public FooterComponent(WebDriver driver) {
                super(driver);
        }

        // ==========================================
        // LOCATORS
        // ==========================================
        // Site Footer Container: [Agnostic Viewport]
        private final By siteFooterContainer = By.cssSelector("footer[data-testid='site-footer']");

        // Footer Mobile Container:
        private final By footerContainer_Mobile = By.cssSelector("div[data-testid='footer-mobile']");

        // Footer Desktop Container:
        private final By footerContainer_Desktop = By.cssSelector("div[data-testid='footer-desktop']");

        // Footer Accordion Columns Mobile: [Use List to retrieve]
        private final By footerAccordion_Mobile = By.cssSelector("div[data-testid='footer-accordion']");

        // Footer Accordion Columns Desktop: [Use List to retrieve]
        private final By footerMenuColumns_Desktop = By.cssSelector("div[data-testid='footer-menu-column']");

        /* Only mobile view accordions and their links */
        // Footer Accordion Toggle Buttons: (Mobile Viewport) [Not Unique]
        private final By footerAccordionToggles = By.cssSelector("div[data-testid='footer-accordion-toggle']");

        // Timepieces Accordion Toggle Button (Mobile Viewport)
        private final By accordionButton_Timepieces = By.cssSelector("div[data-accordion='Timepieces'] > button");

        // Astonia Dusk Gold PDP Link in Timepieces Accordion (Mobile Viewport)
        private final By astoniaPdpLink_Footer_Mobile = By
                        .cssSelector("a[href='/products/astonia-dusk-gold'][data-testid='footer-accordion-link']");

        // Manifesta Black Onyx PDP Link in Timepieces Accordion (Mobile Viewport)
        private final By manifestaPdpLink_Footer_Mobile = By
                        .cssSelector("a[href='/products/manifesta-black-onyx'][data-testid='footer-accordion-link']");

        // Monarch Silver Black PDP Link in Timepieces Accordion (Mobile Viewport)
        private final By monarchPdpLink_Footer_Mobile = By
                        .cssSelector("a[href='/products/monarch-silver-black'][data-testid='footer-accordion-link']");

        // Arvion Navy Silver PDP Link in Timepieces Accordion (Mobile Viewport)
        private final By arvionPdpLink_Footer_Mobile = By
                        .cssSelector("a[href='/products/arvion-navy-silver'][data-testid='footer-accordion-link']");

        // Auriqua Noir Rose PDP Link in Timepieces Accordion (Mobile Viewport)
        private final By auriquaPdpLink_Footer_Mobile = By
                        .cssSelector("a[href='/products/auriqua-noir-rose'][data-testid='footer-accordion-link']");

        // Accessories Accordion Toggle Button {Only Mobile}
        private final By accordionButton_Accessories = By.cssSelector("div[data-accordion='Accessories'] > button");

        // Strap Collection Link in Accessories accordion (Mobile Viewport)
        private final By strapCollectionLink_Footer_Mobile = By
                        .cssSelector("a[href='/accessories/strap'][data-testid='footer-accordion-link']");

        // Clasp Collection Link in Accessories accordion (Mobile Viewport)
        private final By claspCollectionLink_Footer_Mobile = By
                        .cssSelector("a[href='/accessories/clasp'][data-testid='footer-accordion-link']");

        // Watch Case Collection Link in Accessories accordion (Mobile Viewport)
        private final By watchCaseCollectionLink_Footer_Mobile = By
                        .cssSelector("a[href='/accessories/watch-case'][data-testid='footer-accordion-link']");

        // Discover Accordion Toggle Button {Only Mobile}
        private final By accordionButton_Discover = By.cssSelector("div[data-accordion='Discover'] > button");

        // Friends & Partners Link in Discover accordion (Mobile Viewport)
        private final By friendsAndPartnersLink_Footer_Mobile = By
                        .cssSelector("a[href='/friends-and-partners'][data-testid='footer-accordion-link']");

        // Timepieces House Link in Discover accordion (Mobile Viewport)
        private final By timepiecesHouseLink_Footer_Mobile = By
                        .cssSelector("a[href='/experience-center'][data-testid='footer-accordion-link']");

        // The Wall Link in Discover accordion (Mobile Viewport)
        private final By theWallLink_Footer_Mobile = By
                        .cssSelector("a[href='/timepieces-wall'][data-testid='footer-accordion-link']");

        // Timepieces World Link in Discover accordion (Mobile Viewport)
        private final By timepiecesWorldLink_Footer_Mobile = By
                        .cssSelector("a[href='https://world.timepieces-d2c.example/'][data-testid='footer-accordion-link']");

        // Other Accordion Toggle Button {Only Mobile}
        private final By accordionButton_Other = By.cssSelector("div[data-accordion='Other'] > button");

        // Contact Link in Other accordion (Mobile Viewport)
        private final By contactLink_Footer_Mobile = By
                        .cssSelector("a[href='/contact'][data-testid='footer-accordion-link']");

        // Account Link in Other accordion (Mobile Viewport)
        private final By accountLink_Footer_Mobile = By
                        .cssSelector("a[href='/myaccount'][data-testid='footer-accordion-link']");

        // Share Your Moment Link in Other accordion (Mobile Viewport)
        private final By shareYourMomentLink_Footer_Mobile = By
                        .cssSelector("a[href='/timepieces-wall/share'][data-testid='footer-accordion-link']");

        // Footer Accordion Links (Mobile): [Use list to retrieve all accordions]
        private final By footerAccordionLinks = By.cssSelector("div[data-testid='footer-accordion']");

        // Footer Menu Links (Desktop): [Use list to retrieve all accordions]
        private final By footerMenuLinks = By.cssSelector("a[data-testid='footer-menu-link']");

        /* Only desktop view menu columns and their links */
        // Footer Menu Columns (Desktop): [Use a list to retrieve]
        private final By footerMenuColumns = By.cssSelector("div[data-testid='footer-menu-column']");

        // Timepieces Menu Column Label (only Desktop)
        private final By timepiecesMenuColumnLabel = By.cssSelector("div[data-column='Timepieces'] > h3");

        // Astonia Dusk Gold PDP Link in Timepieces Accordion (Desktop Viewport)
        private final By astoniaPdpLink_Footer_Desktop = By
                        .cssSelector("a[href='/products/astonia-dusk-gold'][data-testid='footer-menu-column']");

        // Manifesta Black Onyx PDP Link in Timepieces Accordion (Desktop Viewport)
        private final By manifestaPdpLink_Footer_Desktop = By
                        .cssSelector("a[href='/products/manifesta-black-onyx'][data-testid='footer-menu-column']");

        // Monarch Silver Black PDP Link in Timepieces Accordion (Desktop Viewport)
        private final By monarchPdpLink_Footer_Desktop = By
                        .cssSelector("a[href='/products/monarch-silver-black'][data-testid='footer-menu-column']");

        // Arvion Navy Silver PDP Link in Timepieces Accordion (Desktop Viewport)
        private final By arvionPdpLink_Footer_Desktop = By
                        .cssSelector("a[href='/products/arvion-navy-silver'][data-testid='footer-menu-column']");

        // Auriqua Noir Rose PDP Link in Timepieces Accordion (Desktop Viewport)
        private final By auriquaPdpLink_Footer_Desktop = By
                        .cssSelector("a[href='/products/auriqua-noir-rose'][data-testid='footer-menu-column']");

        // Accessories Menu Column Label (only Desktop)
        private final By accessoriesMenuColumnLabel = By.cssSelector("div[data-column='Accessories'] > h3");

        // Strap Collection Link in Accessories accordion (Desktop Viewport)
        private final By strapCollectionLink_Footer_Desktop = By
                        .cssSelector("a[href='/accessories/strap'][data-testid='footer-menu-column']");

        // Clasp Collection Link in Accessories accordion (Desktop Viewport)
        private final By claspCollectionLink_Footer_Desktop = By
                        .cssSelector("a[href='/accessories/clasp'][data-testid='footer-menu-column']");

        // Watch Case Collection Link in Accessories accordion (Desktop Viewport)
        private final By watchCaseCollectionLink_Footer_Desktop = By
                        .cssSelector("a[href='/accessories/watch-case'][data-testid='footer-menu-column']");

        // Discover Menu Column Label (only Desktop)
        private final By discoverMenuColumnLabel = By.cssSelector("div[data-column='Discover'] > h3");

        // Friends & Partners Link in Discover accordion (Desktop Viewport)
        private final By friendsAndPartnersLink_Footer_Desktop = By
                        .cssSelector("a[href='/friends-and-partners'][data-testid='footer-menu-column']");

        // Timepieces House Link in Discover accordion (Desktop Viewport)
        private final By timepiecesHouseLink_Footer_Desktop = By
                        .cssSelector("a[href='/experience-center'][data-testid='footer-menu-column']");

        // The Wall Link in Discover accordion (Desktop Viewport)
        private final By theWallLink_Footer_Desktop = By
                        .cssSelector("a[href='/timepieces-wall'][data-testid='footer-menu-column']");

        // Timepieces World Link in Discover accordion (Desktop Viewport)
        private final By timepiecesWorldLink_Footer_Desktop = By
                        .cssSelector("a[href='https://world.timepieces-d2c.example/'][data-testid='footer-menu-column']");

        // Other Menu Column Label (only Desktop)
        private final By otherMenuColumnLabel = By.cssSelector("div[data-column='Other'] > h3");

        // Contact Link in Other accordion (Desktop Viewport)
        private final By contactLink_Footer_Desktop = By
                        .cssSelector("a[href='/contact'][data-testid='footer-menu-column']");

        // Account Link in Other accordion (Desktop Viewport)
        private final By accountLink_Footer_Desktop = By
                        .cssSelector("a[href='/account'][data-testid='footer-menu-column']");

        // Share Your Moment Link in Other accordion (Desktop Viewport)
        private final By shareYourMomentLink_Footer_Desktop = By
                        .cssSelector("a[href='/timepieces-wall/share'][data-testid='footer-menu-column']");

        // Footer Socials Section (Mobile Viewport)
        private final By footerSocialsSection_Footer_Mobile = By
                        .cssSelector("div[data-testid='footer-mobile'] div[data-testid='footer-socials']");

        // Footer Socials Section (Desktop Viewport)
        private final By footerSocialsSection_Footer_Desktop = By
                        .cssSelector("div[data-testid='footer-desktop'] div[data-testid='footer-socials']");

        // Footer Social Link (Mobile Viewport): [Use List to retrieve]
        private final By footerSocialLinks_Footer_Mobile = By
                        .cssSelector("div[data-testid='footer-mobile'] a[data-testid='footer-social-link']");

        // Footer Social Link (Desktop Viewport): [Use List to retrieve]
        private final By footerSocialLinks_Footer_Desktop = By
                        .cssSelector("div[data-testid='footer-desktop'] a[data-testid='footer-social-link']");

        // Facebook Social Link (Mobile Viewport)
        private final By facebookSocialLink_Footer_Mobile = By
                        .cssSelector("div[data-testid='footer-mobile'] a[data-social='Facebook']");

        // Facebook Social Link (Desktop Viewport)
        private final By facebookSocialLink_Footer_Desktop = By
                        .cssSelector("div[data-testid='footer-desktop'] a[data-social='Facebook']");

        // X Social Link (Mobile Viewport)
        private final By xSocialLink_Footer_Mobile = By
                        .cssSelector("div[data-testid='footer-mobile'] a[data-social='Twitter']");

        // X Social Link (Mobile Viewport)
        private final By xSocialLink_Footer_Desktop = By
                        .cssSelector("div[data-testid='footer-desktop'] a[data-social='Twitter']");

        // Instagram Social Link (Mobile Viewport)
        private final By instagramSocialLink_Footer_Mobile = By
                        .cssSelector("div[data-testid='footer-mobile'] a[data-social='Instagram']");

        // Instagram Social Link (Desktop Viewport)
        private final By instagramSocialLink_Footer_Desktop = By
                        .cssSelector("div[data-testid='footer-desktop'] a[data-social='Instagram']");

        // Youtube Social Link (Mobile Viewport)
        private final By youtubeSocialLink_Footer_Mobile = By
                        .cssSelector("div[data-testid='footer-mobile'] a[data-social='Youtube']");

        // Youtube Social Link (Desktop Viewport)
        private final By youtubeSocialLink_Footer_Desktop = By
                        .cssSelector("div[data-testid='footer-desktop'] a[data-social='Youtube']");

        // LinkedIn Social Link (Mobile Viewport)
        private final By linkedinSocialLink_Footer_Mobile = By
                        .cssSelector("div[data-testid='footer-mobile'] a[data-social='LinkedIn']");

        // LinkedIn Social Link (Desktop Viewport)
        private final By linkedinSocialLink_Footer_Desktop = By
                        .cssSelector("div[data-testid='footer-desktop'] a[data-social='LinkedIn']");

        // Country Selector Wrapper (Mobile Viewport)
        private final By countrySelectorWrapper_Footer_Mobile = By
                        .cssSelector("div[data-testid='footer-mobile'] button[data-testid='country-selector']");

        // Country Selector Wrapper (Desktop Viewport)
        private final By countrySelectorWrapper_Footer_Desktop = By
                        .cssSelector("div[data-testid='footer-desktop'] button[data-testid='country-selector']");

        // Country Selector Button / Toggle (Mobile Viewport)
        private final By countrySelectorToggle_Footer_Mobile = By
                        .cssSelector("div[data-testid='footer-mobile'] button[data-testid='country-selector-toggle']");

        // Country Selector Button / Toggle (Desktop Viewport)
        private final By countrySelectorToggle_Footer_Desktop = By
                        .cssSelector("div[data-testid='footer-desktop'] button[data-testid='country-selector-toggle']");

        // Footer Legal Links (Mobile Viewport):
        private final By footerLegalLinks_Footer_Mobile = By
                        .cssSelector("div[data-testid='footer-mobile'] a[data-testid='footer-legal']");

        // Footer Legal Links (Desktop Viewport):
        private final By footerLegalLinks_Footer_Desktop = By
                        .cssSelector("div[data-testid='footer-desktop'] a[data-testid='footer-legal']");

        // Footer Sitemap Link (Mobile Viewport):
        private final By footerSitemapLink_Footer_Mobile = By
                        .cssSelector("div[data-testid='footer-mobile'] a[data-testid='footer-sitemap']");

        // Footer Sitemap Link (Desktop Viewport):
        private final By footerSitemapLink_Footer_Desktop = By
                        .cssSelector("div[data-testid='footer-desktop'] a[data-testid='footer-sitemap']");

        // Footer Copyright Text (Mobile Viewport):
        private final By footerCopyrightText_Footer_Mobile = By
                        .cssSelector("div[data-testid='footer-mobile'] p[data-testid='footer-copyright']");

        // Footer Copyright Text (Desktop Viewport):
        private final By footerCopyrightText_Footer_Desktop = By
                        .cssSelector("div[data-testid='footer-desktop'] p[data-testid='footer-copyright']");

        // ==========================================
        // PRIVATE HELPERS
        // ==========================================

        // ==========================================
        // BUSINESS LIBRARY
        // ==========================================
        /**
         * B.1 Clicks on the country selector toggle in the footer.
         * 
         * @return this FooterComponent for method chaining.
         */
        public FooterComponent clickCountrySelector() {
                log.info("Clicking footer country selector toggle.");
                if (isMobileViewport()) {
                        clickElement(countrySelectorToggle_Footer_Mobile);
                } else {
                        clickElement(countrySelectorToggle_Footer_Desktop);
                }
                return this;
        }

        /**
         * B.2 Retrieves the text displayed in the copyright element of the footer.
         * 
         * @return copyright text as String.
         */
        public String getCopyrightText() {
                log.info("Retrieving copyright text from footer.");
                if (isMobileViewport()) {
                        return getText(footerCopyrightText_Footer_Mobile);
                } else {
                        return getText(footerCopyrightText_Footer_Desktop);
                }
        }

        // ==========================================
        // VERIFICATION METHODS
        // ==========================================
        /**
         * V.1 Verifies if the footer container is displayed on the page.
         * 
         * @return true if displayed, false otherwise.
         */
        public boolean isFooterDisplayed() {
                if (isMobileViewport()) {
                        return isDisplayed(footerContainer_Mobile);
                } else {
                        return isDisplayed(footerContainer_Desktop);
                }
        }

        /**
         * V.2 Verifies if the country selector toggle is displayed.
         * 
         * @return true if displayed, false otherwise.
         */
        public boolean isCountrySelectorDisplayed() {
                if (isMobileViewport()) {
                        return isDisplayed(countrySelectorToggle_Footer_Mobile);
                } else {
                        return isDisplayed(countrySelectorToggle_Footer_Desktop);
                }
        }

        /**
         * V.3 Dismisses the cookie consent banner if it is present and clickable.
         * Uses a short-lived wait (3s) so it does not block test flow when the
         * banner has already been dismissed or is not shown on the current page.
         */
        public void acceptCookiesIfPresent() {
                By acceptButton = By.xpath("//button[text()='Accept Cookies' or contains(text(), 'Accept Cookies')]");
                try {
                        WebElement btn = wait.withTimeout(java.time.Duration.ofSeconds(3))
                                        .until(ExpectedConditions.elementToBeClickable(acceptButton));
                        btn.click();
                        log.info("Cookie consent banner accepted successfully.");
                } catch (Exception e) {
                        log.debug("Cookie consent banner not present or not clickable: {}", e.getMessage());
                }
        }

        /**
         * Debug utility: dumps all anchor elements inside the desktop footer to the log.
         * Uses the inherited getElements() synchronization wrapper from BasePage.
         */
        public void dumpFooterLinks() {
                try {
                        acceptCookiesIfPresent();
                        com.timepieces.automation.utils.BrowserUtils.scrollToBottom(driver);
                        Thread.sleep(1000);
                        By allLinksLocator = By.xpath("//div[@data-testid='footer-desktop']//a");
                        List<WebElement> elements = getElements(allLinksLocator);
                        log.info("DUMPING ALL FOOTER LINKS (Total: {}):", elements.size());
                        for (WebElement el : elements) {
                                log.info("Link text: [{}], href: [{}], tag: [{}], outerHTML: [{}]",
                                        el.getText(), el.getAttribute("href"), el.getTagName(), el.getAttribute("outerHTML"));
                        }
                } catch (Exception e) {
                        log.error("Failed to dump footer links: {}", e.getMessage(), e);
                }
        }

        public String getLinkHref(String linkText) {
                if (isMobileViewport()) {
                        expandAllAccordionsMobile();
                }
                By dynamicLinkLocator;
                if (isMobileViewport()) {
                        dynamicLinkLocator = By.xpath("//div[@data-testid='footer-mobile']//a[normalize-space(.)='" + linkText + "' or contains(normalize-space(.), '" + linkText + "')]");
                } else {
                        dynamicLinkLocator = By.xpath("//div[@data-testid='footer-desktop']//a[normalize-space(.)='" + linkText + "' or contains(normalize-space(.), '" + linkText + "')]");
                }
                return getAttribute(dynamicLinkLocator, "href");
        }

        /**
         * V.5 Retrieves the anchor WebElement for a footer link identified by its visible text.
         * Uses the inherited BasePage.getElement() wrapper for synchronization and logging.
         *
         * @param linkText The visible text of the link.
         * @return The WebElement representing the link anchor.
         */
        public WebElement getLinkElement(String linkText) {
                log.info("Retrieving footer link element: [{}]", linkText);
                By dynamicLinkLocator;
                if (isMobileViewport()) {
                        dynamicLinkLocator = By.xpath("//div[@data-testid='footer-mobile']//a[normalize-space(.)='" + linkText + "' or contains(normalize-space(.), '" + linkText + "')]");
                } else {
                        dynamicLinkLocator = By.xpath("//div[@data-testid='footer-desktop']//a[normalize-space(.)='" + linkText + "' or contains(normalize-space(.), '" + linkText + "')]");
                }
                return getElement(dynamicLinkLocator);
        }


        public boolean isLinkDisplayed(String linkText) {
                log.info("Verifying if footer link is displayed: [{}]", linkText);
                By dynamicLinkLocator;
                if (isMobileViewport()) {
                        dynamicLinkLocator = By.xpath("//div[@data-testid='footer-mobile']//a[normalize-space(.)='" + linkText + "' or contains(normalize-space(.), '" + linkText + "')]");
                } else {
                        dynamicLinkLocator = By.xpath("//div[@data-testid='footer-desktop']//a[normalize-space(.)='" + linkText + "' or contains(normalize-space(.), '" + linkText + "')]");
                }
                return isDisplayed(dynamicLinkLocator);
        }

        /**
         * V.4 Verifies if a list of links are all displayed in the footer.
         * If in mobile viewport, it will automatically expand all accordions first
         * to make sure hidden links are visible.
         * 
         * @param expectedLinks List of visible link text names.
         * @return true if all expected links are displayed, false otherwise.
         */
        public boolean areLinksDisplayed(List<String> expectedLinks) {
                acceptCookiesIfPresent();
                com.timepieces.automation.utils.BrowserUtils.scrollToBottom(driver);
                try {
                        Thread.sleep(1000); // Wait for smooth scroll and lazy loading if any
                } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                }
                if (isMobileViewport()) {
                        expandAllAccordionsMobile();
                }
                boolean allDisplayed = true;
                for (String linkText : expectedLinks) {
                        boolean displayed = isLinkDisplayed(linkText);
                        log.info("Link [{}] display status: {}", linkText, displayed);
                        if (!displayed) {
                                allDisplayed = false;
                        }
                }
                return allDisplayed;
        }

        /**
         * B.3 Clicks on a specific link in the footer by its visible text.
         * If in mobile viewport, it will automatically expand all accordions first.
         * 
         * @param linkText the exact visible text of the link to click.
         * @return this FooterComponent for method chaining.
         */
        public FooterComponent clickLink(String linkText) {
                log.info("Attempting to click footer link: [{}]", linkText);
                acceptCookiesIfPresent();
                com.timepieces.automation.utils.BrowserUtils.scrollToBottom(driver);
                if (isMobileViewport()) {
                        expandAllAccordionsMobile();
                }
                By dynamicLinkLocator;
                if (isMobileViewport()) {
                        dynamicLinkLocator = By.xpath("//div[@data-testid='footer-mobile']//a[normalize-space(.)='" + linkText + "' or contains(normalize-space(.), '" + linkText + "')]");
                } else {
                        dynamicLinkLocator = By.xpath("//div[@data-testid='footer-desktop']//a[normalize-space(.)='" + linkText + "' or contains(normalize-space(.), '" + linkText + "')]");
                }
                clickElement(dynamicLinkLocator);
                return this;
        }

        /**
         * H.1 Expands a specific accordion in the mobile viewport.
         * 
         * @param accordionName The name of the accordion (e.g., "Timepieces", "Accessories", "Discover", "Other").
         * @return this FooterComponent for method chaining.
         */
        public FooterComponent expandAccordionMobile(String accordionName) {
                if (!isMobileViewport()) {
                        log.info("Not in mobile viewport; skipping accordion expansion: [{}]", accordionName);
                        return this;
                }
                log.info("Expanding mobile footer accordion: [{}]", accordionName);
                By accordionHeaderLocator = By.cssSelector("div[data-accordion='" + accordionName + "'] > button");
                try {
                        WebElement button = getElement(accordionHeaderLocator);
                        String isExpandedStr = button.getAttribute("aria-expanded");
                        if (isExpandedStr == null || !isExpandedStr.equalsIgnoreCase("true")) {
                                clickElement(accordionHeaderLocator);
                                log.info("Clicked to expand accordion: [{}]", accordionName);
                        } else {
                                log.info("Accordion [{}] is already expanded.", accordionName);
                        }
                } catch (Exception e) {
                        log.warn("Failed to expand accordion via button attributes; attempting direct click on: [{}]", accordionHeaderLocator, e);
                        clickElement(accordionHeaderLocator);
                }
                return this;
        }

        /**
         * H.2 Expands all accordions in the mobile viewport footer.
         * 
         * @return this FooterComponent.
         */
        public FooterComponent expandAllAccordionsMobile() {
                if (!isMobileViewport()) {
                        return this;
                }
                log.info("Expanding all footer accordions in mobile viewport.");
                String[] accordions = {"Timepieces", "Accessories", "Discover", "Other"};
                for (String accordion : accordions) {
                        expandAccordionMobile(accordion);
                }
                return this;
        }
}
