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

    // The Page Title Variable {Keep whether Unique or Not}
    // Decide if it needs to be added to assertions if not unique
    private static final String PAGE_TITLE = "Timepieces | Friends & Partners";

    // ==========================================
    // Constructor
    // ==========================================
    public FriendsAndPartners(WebDriver driver) {
        super(driver);
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
    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
}
