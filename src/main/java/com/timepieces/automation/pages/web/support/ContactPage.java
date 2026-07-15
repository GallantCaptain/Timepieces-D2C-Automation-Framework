package com.timepieces.automation.pages.web.support;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.StoreFrontBasePage;

import lombok.extern.log4j.Log4j2;

/**
 * The ContactPage has links for Phone Support (removed for now), WhatsApp
 * Support, Email link, and also a form to directly send an email to the Support
 * team. The support team normally replies within 1 Business day after receiving
 * a support request. A request number is raised and displayed on the page after
 * submitting the form. For email, a support number is given with the
 * auto-generated reply that the support team will get back within a business
 * day.
 *
 * <p>
 * LOCATOR SOURCE: real {@code data-testid}s captured from the 2026-07-10 scrape
 * ({@code docs/scrape-artifacts/phase0/contact-page.md}). The form is fully
 * instrumented with {@code contact-*} test ids; the support links fall back to
 * their stable {@code href} targets.
 *
 * <p>
 * SUBMISSION WARNING: {@link #clickSubmitEnquiry()} raises a REAL support ticket
 * for a live staff member. Automated tests should exercise field entry and
 * validation only and must NOT submit against production.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this} (ContactPage)
 * to allow method chaining in step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of ContactPage"
* @since 2026-07-03
 */

@Log4j2
public class ContactPage extends StoreFrontBasePage<ContactPage> {

    // ==========================================
    // PAGE CONSTANTS
    // ==========================================
    private static final String PAGE_PATH = "/contact";
    private static final String PAGE_TITLE = "Contact Us | Timepieces Watches";

    // ==========================================
    // Constructor
    // ==========================================
    public ContactPage(WebDriver driver) {
        super(driver);
    }

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
    // --- Enquiry form fields (fully data-testid'd) ---
    private final By firstNameInput = By.cssSelector("input[data-testid='contact-first-name']");
    private final By lastNameInput = By.cssSelector("input[data-testid='contact-last-name']");
    private final By countryToggle = By.cssSelector("button[data-testid='contact-country-toggle']");
    private final By phoneInput = By.cssSelector("input[data-testid='contact-phone']");
    private final By emailInput = By.cssSelector("input[data-testid='contact-email']");
    private final By subjectInput = By.cssSelector("input[data-testid='contact-subject']");
    private final By messageTextarea = By.cssSelector("textarea[data-testid='contact-message']");
    private final By submitButton = By.cssSelector("button[data-testid='contact-submit']");

    // --- Support links (no data-testid; located by stable href targets) ---
    private final By tollFreeLink = By.cssSelector("a[href^='tel:']"); // ROLLOUT-GAP: href-anchor
    private final By whatsAppLink = By.cssSelector("a[href*='wa.me']"); // ROLLOUT-GAP: href-anchor
    private final By supportEmailLink = By.cssSelector("a[href^='mailto:']"); // ROLLOUT-GAP: href-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Enters the first name.
     *
     * @param firstName the first name to enter.
     * @return this page for method chaining.
     */
    public ContactPage enterFirstName(String firstName) {
        sendKeys(firstNameInput, firstName);
        return this;
    }

    /**
     * B.2 Enters the last name.
     *
     * @param lastName the last name to enter.
     * @return this page for method chaining.
     */
    public ContactPage enterLastName(String lastName) {
        sendKeys(lastNameInput, lastName);
        return this;
    }

    /**
     * B.3 Enters the phone number (country code is set via the country toggle).
     *
     * @param phone the 10-digit phone number.
     * @return this page for method chaining.
     */
    public ContactPage enterPhone(String phone) {
        sendKeys(phoneInput, phone);
        return this;
    }

    /**
     * B.4 Opens the phone country-code selector.
     *
     * @return this page for method chaining.
     */
    public ContactPage openCountrySelector() {
        clickElement(countryToggle);
        return this;
    }

    /**
     * B.5 Enters the email address.
     *
     * @param email the email address to enter.
     * @return this page for method chaining.
     */
    public ContactPage enterEmail(String email) {
        sendKeys(emailInput, email);
        return this;
    }

    /**
     * B.6 Enters the subject line.
     *
     * @param subject the subject to enter.
     * @return this page for method chaining.
     */
    public ContactPage enterSubject(String subject) {
        sendKeys(subjectInput, subject);
        return this;
    }

    /**
     * B.7 Enters the enquiry message body.
     *
     * @param message the message to enter.
     * @return this page for method chaining.
     */
    public ContactPage enterMessage(String message) {
        sendKeys(messageTextarea, message);
        return this;
    }

    /**
     * B.8 Convenience: fills every text field of the enquiry form in one call.
     * Does NOT submit — see the class-level submission warning.
     *
     * @param firstName first name.
     * @param lastName  last name.
     * @param phone     phone number.
     * @param email     email address.
     * @param subject   subject line.
     * @param message   message body.
     * @return this page for method chaining.
     */
    public ContactPage fillEnquiryForm(String firstName, String lastName, String phone, String email,
            String subject, String message) {
        log.info("Filling the contact enquiry form (no submit).");
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPhone(phone);
        enterEmail(email);
        enterSubject(subject);
        enterMessage(message);
        return this;
    }

    /**
     * B.9 Submits the enquiry form.
     * <p>
     * WARNING: this raises a REAL support ticket for a live staff member. Do NOT
     * call against production in automated runs — see the class-level warning.
     *
     * @return this page for method chaining.
     */
    public ContactPage clickSubmitEnquiry() {
        log.warn("Submitting the contact enquiry form — this raises a REAL support ticket.");
        clickElement(submitButton);
        return this;
    }

    /**
     * B.10 Returns the WhatsApp support link target (e.g.
     * {@code https://wa.me/910000000000}).
     *
     * @return the WhatsApp link href.
     */
    public String getWhatsAppHref() {
        return getAttribute(whatsAppLink, "href");
    }

    /**
     * B.11 Returns the support email target (e.g.
     * {@code mailto:support@timepieces-d2c.example}).
     *
     * @return the support email link href.
     */
    public String getSupportEmailHref() {
        return getAttribute(supportEmailLink, "href");
    }

    /**
     * B.12 Returns the toll-free support number target (e.g. {@code tel:18000000000}).
     *
     * @return the toll-free link href.
     */
    public String getTollFreeHref() {
        return getAttribute(tollFreeLink, "href");
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the browser title matches the expected {@code PAGE_TITLE}.
     *
     * @return true if the document title contains the expected page title.
     */
    public boolean isPageTitleCorrect() {
        String actual = driver.getTitle();
        return actual != null && actual.contains(PAGE_TITLE);
    }

    /**
     * V.2 Verifies the enquiry form is displayed (checks the first + last name and
     * message fields are present).
     *
     * @return true if the core form fields are displayed.
     */
    public boolean isContactFormDisplayed() {
        return isDisplayed(firstNameInput) && isDisplayed(lastNameInput) && isDisplayed(messageTextarea);
    }

    /**
     * V.3 Verifies the submit ("Send Enquiry") button is displayed.
     *
     * @return true if the submit button is displayed.
     */
    public boolean isSubmitButtonDisplayed() {
        return isDisplayed(submitButton);
    }

    /**
     * V.4 Verifies the WhatsApp support link is displayed.
     *
     * @return true if the WhatsApp link is displayed.
     */
    public boolean isWhatsAppLinkDisplayed() {
        return isDisplayed(whatsAppLink);
    }

    /**
     * V.5 Verifies the support email link is displayed.
     *
     * @return true if the support email link is displayed.
     */
    public boolean isSupportEmailLinkDisplayed() {
        return isDisplayed(supportEmailLink);
    }

    /**
     * V.6 Verifies the toll-free support link is displayed.
     *
     * @return true if the toll-free link is displayed.
     */
    public boolean isTollFreeLinkDisplayed() {
        return isDisplayed(tollFreeLink);
    }
}
