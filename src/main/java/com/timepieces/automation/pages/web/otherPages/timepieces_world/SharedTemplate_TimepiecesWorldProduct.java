package com.timepieces.automation.pages.web.otherPages.timepieces_world;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * SharedTemplate_TimepiecesWorldProduct — the common base for the 5 Timepieces World
 * product showcase pages ({@code world.timepieces-d2c.example/products/<family>}:
 * Arvion, Monarch, Auriqua, Astonia, Manifesta).
 *
 * <p>
 * The 2026-07-16 scrape returned a UNIFIED SHARED TEMPLATE verdict: all 5 pages
 * share one structure populated with family-specific content. Shape: a "Back to
 * scene" button (→ the experience page), the family logo image
 * ({@code img[alt='<FAMILY>']}), a tagline, an ideation section, an
 * engineering/calibre specs grid (label → value pairs like "BASE CALIBRE" →
 * "TMI VK63"), "Learn more about &lt;material&gt;" buttons, a WAITLIST signup
 * form (full name / email / country / phone / postal / optional Instagram /
 * consent checkbox; submit stays disabled until every required field
 * validates), and a footer (Back to top, {@code mailto:reservations@timepieces-d2c.example},
 * Instagram, LinkedIn). There is NO CTA back to the www storefront — these
 * pages are strictly pre-launch waitlist signups. The document title pattern is
 * {@code <Family> | <UniqueTagline> | Timepieces Watches}. NO {@code data-testid}s
 * exist on the subdomain, so every locator is a fallback tagged
 * {@code // ROLLOUT-GAP}.
 *
 * <p>
 * SUBMISSION WARNING: {@link #clickSubmitWaitlist()} raises a REAL waitlist
 * signup. Automated tests should exercise field entry and the disabled-submit
 * validation only, and must NOT submit against production.
 *
 * <p>
 * ORIGIN NOTE: these pages live on {@code https://world.timepieces-d2c.example}, not the
 * config {@code base.url} — {@link #getBaseUrl()} is overridden accordingly.
 *
 * GENERIC SELF-TYPE (CRTP): each family binds itself, e.g.
 * {@code class TimepiecesWorld_AstoniaPage extends SharedTemplate_TimepiecesWorldProduct<TimepiecesWorld_AstoniaPage>}.
 *
 * @param <SELF> the concrete family page subtype, for fluent return typing.
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of SharedTemplate_TimepiecesWorldProduct"
 * @since 2026-07-16
 */

@Log4j2
public abstract class SharedTemplate_TimepiecesWorldProduct<SELF extends SharedTemplate_TimepiecesWorldProduct<SELF>>
        extends BasePage<SELF> {

    // The Timepieces World origin (distinct from the storefront base.url).
    protected static final String WORLD_BASE_URL = "https://world.timepieces-d2c.example";

    // ==========================================
    // Constructor
    // ==========================================
    public SharedTemplate_TimepiecesWorldProduct(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // FAMILY HOOK
    // ==========================================
    /**
     * The family display name (e.g. "Astonia") — drives the page path
     * ({@code /products/<lowercase>}) and the logo image locator
     * ({@code img[alt='<UPPERCASE>']}).
     *
     * @return the family display name.
     */
    protected abstract String familyName();

    // ==========================================
    // PAGE METADATA (drives the inherited fluent open())
    // ==========================================
    @Override
    public String getBaseUrl() {
        return WORLD_BASE_URL;
    }

    @Override
    protected String getPagePath() {
        return "/products/" + familyName().toLowerCase();
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // The "Back to scene" button (→ the experience page).
    private final By backToSceneButton = By.xpath("//main//button[contains(normalize-space(),'Back to scene')]"); // ROLLOUT-GAP: text-anchor
    // The family tagline heading in the hero.
    private final By taglineHeading = By.cssSelector("main h1"); // ROLLOUT-GAP: semantic
    // Dynamic: a specs-grid value by its label (e.g. "BASE CALIBRE" → "TMI VK63").
    private final By specValueTemplate = By.xpath("//div[normalize-space()='%s']/following-sibling::div[1]"); // ROLLOUT-GAP: structural
    // The "Learn more about <material>" buttons.
    private final By learnMoreButtons = By.xpath("//button[contains(normalize-space(),'Learn more about')]"); // ROLLOUT-GAP: text-anchor
    // Dynamic: a learn-more button by its material name.
    private final By learnMoreByMaterialTemplate = By.xpath("//button[contains(normalize-space(),'Learn more about %s')]"); // ROLLOUT-GAP: text-anchor

    // --- Waitlist form ---
    // The "Join the waitlist for <Family>" heading.
    private final By waitlistHeading = By.xpath("//h2[contains(normalize-space(),'Join the waitlist for')]"); // ROLLOUT-GAP: text-anchor
    // The trigger button that scrolls to the form.
    private final By waitlistTriggerButton = By.xpath("//button[contains(normalize-space(),'Join the waitlist')]"); // ROLLOUT-GAP: text-anchor
    private final By fullNameInput = By.cssSelector("input[placeholder*='Full name']"); // ROLLOUT-GAP: placeholder-anchor
    private final By emailInput = By.cssSelector("input[placeholder*='Email id']"); // ROLLOUT-GAP: placeholder-anchor
    private final By phoneInput = By.cssSelector("input[type='tel']"); // ROLLOUT-GAP: semantic
    private final By postalCodeInput = By.cssSelector("input[placeholder*='Postal/Zip code']"); // ROLLOUT-GAP: placeholder-anchor
    private final By instagramInput = By.cssSelector("input[placeholder*='Instagram username']"); // ROLLOUT-GAP: placeholder-anchor
    private final By consentCheckbox = By.cssSelector("input[type='checkbox']"); // ROLLOUT-GAP: semantic
    private final By waitlistSubmitButton = By.cssSelector("button[type='submit']"); // ROLLOUT-GAP: semantic

    // --- Footer ---
    private final By backToTopButton = By.xpath("//button[contains(normalize-space(),'Back to top')]"); // ROLLOUT-GAP: text-anchor
    private final By reservationsEmailLink = By.cssSelector("a[href='mailto:reservations@timepieces-d2c.example']"); // ROLLOUT-GAP: href-anchor
    private final By instagramLink = By.cssSelector("a[href*='instagram.com/timepieces-world.example']"); // ROLLOUT-GAP: href-anchor
    private final By linkedInLink = By.cssSelector("a[href*='linkedin.com/company/timepieces']"); // ROLLOUT-GAP: href-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * P.1 Returns {@code this} typed as the concrete subclass ({@code SELF}) so
     * inherited business methods stay fluent (CRTP).
     *
     * @return this page instance, typed as the concrete subclass.
     */
    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }

    /**
     * P.2 The family logo image locator ({@code img[alt='<FAMILY UPPERCASE>']}).
     *
     * @return the family logo By locator.
     */
    private By familyLogo() {
        return By.cssSelector("main img[alt='" + familyName().toUpperCase() + "']"); // ROLLOUT-GAP: alt-anchor
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Clicks "Back to scene", returning to the Timepieces World experience page.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickBackToScene() {
        log.info("[{}] Clicking 'Back to scene'.", familyName());
        clickElement(backToSceneButton);
        return self();
    }

    /**
     * B.2 Clicks the "Join the waitlist" trigger (scrolls to the signup form).
     *
     * @return this page instance for method chaining.
     */
    public SELF clickJoinWaitlist() {
        log.info("[{}] Opening the waitlist form.", familyName());
        clickElement(waitlistTriggerButton);
        return self();
    }

    /**
     * B.3 Fills the waitlist form's text fields (name, email, phone, postal,
     * optional Instagram). Does NOT tick consent or submit — see the class-level
     * submission warning.
     *
     * @param fullName  full name.
     * @param email     email address.
     * @param phone     phone number.
     * @param postal    postal/zip code.
     * @param instagram Instagram username (nullable/skippable).
     * @return this page instance for method chaining.
     */
    public SELF fillWaitlistForm(String fullName, String email, String phone, String postal, String instagram) {
        log.info("[{}] Filling the waitlist form (no submit).", familyName());
        sendKeys(fullNameInput, fullName);
        sendKeys(emailInput, email);
        sendKeys(phoneInput, phone);
        sendKeys(postalCodeInput, postal);
        sendKeys(instagramInput, instagram);
        return self();
    }

    /**
     * B.4 Ticks the terms/privacy consent checkbox.
     *
     * @return this page instance for method chaining.
     */
    public SELF tickConsent() {
        log.info("[{}] Ticking the waitlist consent checkbox.", familyName());
        clickElement(consentCheckbox);
        return self();
    }

    /**
     * B.5 Submits the waitlist form.
     * <p>
     * WARNING: this raises a REAL waitlist signup. Do NOT call against production
     * in automated runs — see the class-level warning.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickSubmitWaitlist() {
        log.warn("[{}] Submitting the waitlist form — this raises a REAL signup.", familyName());
        clickElement(waitlistSubmitButton);
        return self();
    }

    /**
     * B.6 Returns a calibre-spec value by its grid label (e.g. "BASE CALIBRE",
     * "TYPE", "FREQUENCY").
     *
     * @param specLabel the exact uppercase spec label.
     * @return the trimmed spec value text.
     */
    public String getSpecValue(String specLabel) {
        return getText(formatLocator(specValueTemplate, specLabel));
    }

    /**
     * B.7 Clicks a "Learn more about <material>" button (e.g. "Sapphire Crystal").
     *
     * @param material the material name as it appears in the button label.
     * @return this page instance for method chaining.
     */
    public SELF clickLearnMoreAbout(String material) {
        log.info("[{}] Opening 'Learn more about {}'.", familyName(), material);
        clickElement(formatLocator(learnMoreByMaterialTemplate, material));
        return self();
    }

    /**
     * B.8 Clicks the footer "Back to top" button.
     *
     * @return this page instance for method chaining.
     */
    public SELF clickBackToTop() {
        log.info("[{}] Clicking 'Back to top'.", familyName());
        clickElement(backToTopButton);
        return self();
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the browser title matches the family pattern
     * ({@code <Family> | <Tagline> | Timepieces Watches}).
     *
     * @return true if the title starts with the family name and carries the
     *         Timepieces Watches suffix.
     */
    public boolean isPageTitleCorrect() {
        String actual = driver.getTitle();
        return actual != null && actual.startsWith(familyName() + " |") && actual.contains("Timepieces Watches");
    }

    /**
     * V.2 Verifies the family logo image is displayed.
     *
     * @return true if the family logo is displayed.
     */
    public boolean isFamilyLogoDisplayed() {
        return isDisplayed(familyLogo());
    }

    /**
     * V.3 Verifies the hero tagline is displayed.
     *
     * @return true if the tagline heading is displayed.
     */
    public boolean isTaglineDisplayed() {
        return isDisplayed(taglineHeading);
    }

    /**
     * V.4 Verifies the "Back to scene" button is displayed.
     *
     * @return true if the back-to-scene button is displayed.
     */
    public boolean isBackToSceneDisplayed() {
        return isDisplayed(backToSceneButton);
    }

    /**
     * V.5 Verifies a calibre-spec entry with the given label exists.
     *
     * @param specLabel the exact uppercase spec label (e.g. "BASE CALIBRE").
     * @return true if the spec entry is present.
     */
    public boolean hasSpec(String specLabel) {
        return !driver.findElements(formatLocator(specValueTemplate, specLabel)).isEmpty();
    }

    /**
     * V.6 Counts the "Learn more about ..." material buttons.
     *
     * @return the number of learn-more buttons.
     */
    public int getLearnMoreButtonCount() {
        return driver.findElements(learnMoreButtons).size();
    }

    /**
     * V.7 Verifies the waitlist heading + form fields are present.
     *
     * @return true if the waitlist form is present.
     */
    public boolean isWaitlistFormDisplayed() {
        return !driver.findElements(waitlistHeading).isEmpty()
                && !driver.findElements(fullNameInput).isEmpty()
                && !driver.findElements(emailInput).isEmpty();
    }

    /**
     * V.8 Reports whether the waitlist submit is enabled (it starts DISABLED and
     * only enables once every required field validates).
     *
     * @return true if the submit button is enabled.
     */
    public boolean isWaitlistSubmitEnabled() {
        try {
            return getElementPresence(waitlistSubmitButton).isEnabled();
        } catch (Exception e) {
            log.warn("[{}] Could not evaluate the waitlist submit state.", familyName(), e);
            return false;
        }
    }

    /**
     * V.9 Verifies the footer support links are present (reservations mailto,
     * Instagram, LinkedIn).
     *
     * @return true if all three footer links are present.
     */
    public boolean areFooterLinksPresent() {
        return !driver.findElements(reservationsEmailLink).isEmpty()
                && !driver.findElements(instagramLink).isEmpty()
                && !driver.findElements(linkedInLink).isEmpty();
    }
}
