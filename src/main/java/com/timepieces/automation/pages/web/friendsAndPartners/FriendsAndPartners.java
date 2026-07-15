package com.timepieces.automation.pages.web.friendsAndPartners;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The FriendsAndPartnersPage: https://www.timepieces-d2c.example/friends-and-partners
 * is a page that showcases the various collaborations and partnerships that
 * Timepieces has with other brands and organizations. It is a page that is meant
 * to highlight the various collaborations and partnerships that Timepieces has
 * with other brands and organizations.
 * The prominent personalities in this page are meant to be separate from the
 * regular buyers of the Timepieces timepieces as it meant to showcase that
 * eminent personalities prefer to buy a Timepieces timepiece and their status is
 * a further reflection of Timepieces as a brand. The Friends & Partnesr page is
 * divided into 3 sections with each section containing a number of
 * personalities.
 * Though these prominent personalities have been divided into 3 sections, but
 * they are all meant to be influential. The 3 sections are: Founders & Makers,
 * Thought Leaders, and Voices. Some of the profile cards have a video icon
 * on them. By clicking on any of the profiles in the Friends and Partners page,
 * the user is taken to the particular personality's profile (highlighted) and
 * designed to stand out when compared to a regular customer profile. From the
 * personality profile page, the user can navigate to the personality's
 * video through the Play Video button, if their profile contains any videos.
 * Note that not all personalities have videos. Also, every profile page
 * contains the numbered watches bought and clicking on any will take the user
 * to the particular watch's product details page.
 * This page has a header and a footer.
 * Each of the sections are represented by their own directory to ensure that
 * the profiles from each of the sections are correctly ordered for posterity.
 * 
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (FriendsAndPartnersPage) to allow method chaining in step definitions.
 * 
 * @version 1.0
 * @author Avinash Rath
 * @Description "Description of FriendsAndPartners"
 * @since 2026-07-03
 */

@Log4j2
public class FriendsAndPartners extends StoreFrontBasePage<FriendsAndPartners> {

    // ==========================================
    // PAGE CONSTANTS
    // ==========================================
    // The Page Path Variable
    private static final String PAGE_PATH = "/friends-and-partners";

    // The Page Title Variable (confirmed by the 2026-07-16 scrape).
    private static final String PAGE_TITLE = "Friends & Partners | Timepieces Watches";

    // ==========================================
    // Constructor
    // ==========================================
    public FriendsAndPartners(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // PAGE METADATA (drives the inherited fluent open())
    // ==========================================
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
    /*
     * https://www.timepieces-d2c.example/friends-and-partners
     * https://www.timepieces-d2c.example/friends-and-partners/vinamra-shastri
     * https://www.timepieces-d2c.example/friends-and-partners/arshdeep-singh
     * https://www.timepieces-d2c.example/friends-and-partners/peter-wilson
     * https://www.timepieces-d2c.example/friends-and-partners/vivek-oberoi
     * https://www.timepieces-d2c.example/friends-and-partners/axar-patel
     * https://www.timepieces-d2c.example/friends-and-partners/rakesh-kapoor
     * https://www.timepieces-d2c.example/friends-and-partners/vir-das
     * https://www.timepieces-d2c.example/friends-and-partners/bhuvan-bam
     * https://www.timepieces-d2c.example/friends-and-partners/danish-pandor
     * https://www.timepieces-d2c.example/friends-and-partners/cp-gurnani
     * https://www.timepieces-d2c.example/friends-and-partners/tanay-tiwari
     * https://www.timepieces-d2c.example/friends-and-partners/saif-ali-khan
     * https://www.timepieces-d2c.example/friends-and-partners/anirudh-taparia
     * https://www.timepieces-d2c.example/friends-and-partners/anshul-jubli
     * https://www.timepieces-d2c.example/friends-and-partners/nikhil-kamath
     * https://www.timepieces-d2c.example/friends-and-partners/rajat-patidar
     * https://www.timepieces-d2c.example/friends-and-partners/abdulla-ajmal
     * https://www.timepieces-d2c.example/friends-and-partners/peyush-bansal
     * https://www.timepieces-d2c.example/friends-and-partners/divyendu-sharma
     * https://www.timepieces-d2c.example/friends-and-partners/suyash-saraf
     * https://www.timepieces-d2c.example/friends-and-partners/siddharth-dungarwal
     * https://www.timepieces-d2c.example/friends-and-partners/sameer
     * https://www.timepieces-d2c.example/friends-and-partners/ca-varun-gupta
     * https://www.timepieces-d2c.example/friends-and-partners/abhishek-chakravarthy
     * https://www.timepieces-d2c.example/friends-and-partners/hitesh-dhingra
     * https://www.timepieces-d2c.example/friends-and-partners/amit-burman
     * https://www.timepieces-d2c.example/friends-and-partners/aankith-aroraa
     * https://www.timepieces-d2c.example/friends-and-partners/karam-sawhney
     * https://www.timepieces-d2c.example/friends-and-partners/dr-mohit-lalvani
     * https://www.timepieces-d2c.example/friends-and-partners/kynan-chenai
     * https://www.timepieces-d2c.example/friends-and-partners/ayush-shukla
     * https://www.timepieces-d2c.example/friends-and-partners/lokesh-verma
     * https://www.timepieces-d2c.example/friends-and-partners/vidur-mayor
     * https://www.timepieces-d2c.example/friends-and-partners/sanchit-arora
     * https://www.timepieces-d2c.example/friends-and-partners/clas-wulff
     * https://www.timepieces-d2c.example/friends-and-partners/chaitanya-mathur
     * https://www.timepieces-d2c.example/friends-and-partners/ashutosh-taparia
     * https://www.timepieces-d2c.example/friends-and-partners/jaideep-singh
     * https://www.timepieces-d2c.example/friends-and-partners/gaurav-khatri
     * https://www.timepieces-d2c.example/friends-and-partners/anchit-kapil
     * https://www.timepieces-d2c.example/friends-and-partners/salil-jaisingh
     * https://www.timepieces-d2c.example/friends-and-partners/raj-shamani
     * https://www.timepieces-d2c.example/friends-and-partners/mr-manoj-adlakha
     * https://www.timepieces-d2c.example/friends-and-partners/tanmay-bhatt
     * https://www.timepieces-d2c.example/friends-and-partners/kush-maini
     * https://www.timepieces-d2c.example/friends-and-partners/vineet-sagar
     * https://www.timepieces-d2c.example/friends-and-partners/deep-bajaj
     * https://www.timepieces-d2c.example/friends-and-partners/rahul-dua
     * https://www.timepieces-d2c.example/friends-and-partners/mayank-arya
     * https://www.timepieces-d2c.example/friends-and-partners/sumit-nagal
     * https://www.timepieces-d2c.example/friends-and-partners/yashraj-akashi
     * https://www.timepieces-d2c.example/friends-and-partners/sharan-hegde
     * https://www.timepieces-d2c.example/friends-and-partners/sarthak-ahuja
     * https://www.timepieces-d2c.example/friends-and-partners/ashwin-johar
     * https://www.timepieces-d2c.example/friends-and-partners/dravya-dholakia
     * https://www.timepieces-d2c.example/friends-and-partners/himeesh-madaan
     * https://www.timepieces-d2c.example/friends-and-partners/rahul-malodia
     * https://www.timepieces-d2c.example/friends-and-partners/arjun-vaidya
     * https://www.timepieces-d2c.example/friends-and-partners/vikramjeet-singh-sahney
     * https://www.timepieces-d2c.example/friends-and-partners/rahul-kapur
     * https://www.timepieces-d2c.example/friends-and-partners/kanchun-kaushal
     * https://www.timepieces-d2c.example/friends-and-partners/viraj-ghelani
     * https://www.timepieces-d2c.example/friends-and-partners/ganesh-prasad-sridharan
     * https://www.timepieces-d2c.example/friends-and-partners/mrunal-jhaveri
     * https://www.timepieces-d2c.example/friends-and-partners/siddhi-kaul
     * https://www.timepieces-d2c.example/friends-and-partners/amanpreet-singh-bajaj
     * https://www.timepieces-d2c.example/friends-and-partners/aditya-zutshi
     * https://www.timepieces-d2c.example/friends-and-partners/neeraj-kalyan
     * https://www.timepieces-d2c.example/friends-and-partners/abhimanyu-bhandari
     * https://www.timepieces-d2c.example/products/astonia-dusk-gold
     * https://www.timepieces-d2c.example/products/manifesta-black-onyx
     * https://www.timepieces-d2c.example/products/monarch-silver-black
     * https://www.timepieces-d2c.example/products/arvion-navy-silver
     * https://www.timepieces-d2c.example/products/auriqua-noir-rose
     * https://www.timepieces-d2c.example/accessories/strap
     * https://www.timepieces-d2c.example/accessories/clasp
     * https://www.timepieces-d2c.example/accessories/watch-case
     * https://www.timepieces-d2c.example/friends-and-partners
     * https://www.timepieces-d2c.example/experience-center
     * https://www.timepieces-d2c.example/timepieces-wall
     * https://world.timepieces-d2c.example/
     * https://www.timepieces-d2c.example/contact
     * https://www.timepieces-d2c.example/account
     * https://www.timepieces-d2c.example/timepieces-wall/share
     * https://www.timepieces-d2c.example/updates
     * https://www.facebook.com/people/Timepieces/00000000000000/
     * https://x.com/timepieces
     * https://www.instagram.com/timepieces-world.example/
     * https://www.youtube.com/@timepiecesworld
     * https://www.linkedin.com/company/timepieces
     * https://www.timepieces-d2c.example/policies
     * https://www.timepieces-d2c.example/sitemap
     */
    // The main "Friends & Partners" heading.
    private final By mainHeading = By.xpath("//h1[contains(normalize-space(),'Friends & Partners')]"); // ROLLOUT-GAP: text-anchor
    // The intro paragraph under the heading.
    private final By introText = By.xpath("//p[contains(normalize-space(),'The individuals who embody')]"); // ROLLOUT-GAP: text-anchor
    // All partner profile cards (each links to /friends-and-partners/<slug>).
    private final By profileCards = By.cssSelector("a[href^='/friends-and-partners/']"); // ROLLOUT-GAP: structural
    // The video-icon badge that marks cards which have an associated video.
    private final By videoBadges = By.cssSelector("a[href^='/friends-and-partners/'] div.absolute.top-3.left-3"); // ROLLOUT-GAP: utility-class
    // Dynamic: a section filter TAB button by its exact label (Voices / Founders & Makers / Thought Leaders).
    private final By sectionTabTemplate = By.xpath("//button[normalize-space()='%s']"); // ROLLOUT-GAP: text-anchor
    // Dynamic: a section HEADING (h2) by its exact label (may be visually hidden per active filter).
    private final By sectionHeadingTemplate = By.xpath("//h2[normalize-space()='%s']"); // ROLLOUT-GAP: text-anchor
    // Dynamic: a profile card by the person's name (its first inner h2).
    private final By profileCardByNameTemplate = By.xpath("//a[starts-with(@href,'/friends-and-partners/')][.//h2[normalize-space()='%s']]"); // ROLLOUT-GAP: text-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Clicks a section filter tab by its label to filter the grid
     * ("Voices", "Founders & Makers", "Thought Leaders").
     *
     * @param sectionLabel the exact tab label.
     * @return this page instance for method chaining.
     */
    public FriendsAndPartners selectSection(String sectionLabel) {
        log.info("Selecting Friends & Partners section: [{}]", sectionLabel);
        clickElement(formatLocator(sectionTabTemplate, sectionLabel));
        return this;
    }

    /**
     * B.2 Clicks a partner profile card by the person's name, navigating to their
     * profile page.
     *
     * @param name the exact person name shown on the card.
     * @return this page instance for method chaining.
     */
    public FriendsAndPartners clickProfile(String name) {
        log.info("Opening partner profile: [{}]", name);
        clickElement(formatLocator(profileCardByNameTemplate, name));
        return this;
    }

    /**
     * B.3 Returns the main heading ("Friends & Partners") text.
     *
     * @return the trimmed heading text.
     */
    public String getMainHeadingText() {
        return getText(mainHeading);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the browser title matches {@code Friends & Partners | Timepieces
     * Watches}.
     *
     * @return true if the document title contains the expected page title.
     */
    public boolean isPageTitleCorrect() {
        String actual = driver.getTitle();
        return actual != null && actual.contains(PAGE_TITLE);
    }

    /**
     * V.2 Verifies the main heading is displayed.
     *
     * @return true if the main heading is displayed.
     */
    public boolean isMainHeadingDisplayed() {
        return isDisplayed(mainHeading);
    }

    /**
     * V.3 Verifies the intro paragraph is displayed.
     *
     * @return true if the intro text is displayed.
     */
    public boolean isIntroDisplayed() {
        return isDisplayed(introText);
    }

    /**
     * V.4 Counts the partner profile cards currently rendered.
     *
     * @return the number of profile cards.
     */
    public int getProfileCardCount() {
        return driver.findElements(profileCards).size();
    }

    /**
     * V.5 Counts the cards carrying a video badge (i.e. profiles that have a
     * video).
     *
     * @return the number of video-badged cards.
     */
    public int getVideoBadgeCount() {
        return driver.findElements(videoBadges).size();
    }

    /**
     * V.6 Verifies a section filter tab with the given label exists.
     *
     * @param sectionLabel the exact tab label.
     * @return true if the tab is present.
     */
    public boolean hasSectionTab(String sectionLabel) {
        return !driver.findElements(formatLocator(sectionTabTemplate, sectionLabel)).isEmpty();
    }

    /**
     * V.7 Verifies a section heading with the given label is present in the DOM
     * (it may be visually hidden depending on the active filter).
     *
     * @param sectionLabel the exact section heading label.
     * @return true if the section heading is present.
     */
    public boolean hasSectionHeading(String sectionLabel) {
        return !driver.findElements(formatLocator(sectionHeadingTemplate, sectionLabel)).isEmpty();
    }

    /**
     * V.8 Verifies a profile card for the given person name is present.
     *
     * @param name the exact person name.
     * @return true if the profile card is present.
     */
    public boolean hasProfileCard(String name) {
        return !driver.findElements(formatLocator(profileCardByNameTemplate, name)).isEmpty();
    }
}
