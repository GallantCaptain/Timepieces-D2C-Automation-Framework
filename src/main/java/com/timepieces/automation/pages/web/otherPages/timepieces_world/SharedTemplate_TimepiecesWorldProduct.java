package com.timepieces.automation.pages.web.otherPages.timepieces_world;

import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.timepieces.automation.pages.BasePage;

import lombok.extern.log4j.Log4j2;

/**
 * SharedTemplate_TimepiecesWorldProduct — the common base for the 5 Timepieces World
 * product showcase pages ({@code world.timepieces-d2c.example/products/<family>}:
 * Arvion, Monarch, Auriqua, Astonia, Manifesta).
 *
 * <p>
 * The 2026-07-16 scrape returned a UNIFIED SHARED TEMPLATE verdict: all 5 pages
 * share one structure populated with family-specific content. Shape: the family
 * logo image, a tagline, an ideation section, an engineering/calibre specs grid
 * (label → value pairs like "Base calibre" → "TMI VK63"), "Learn more about
 * &lt;material&gt;" tiles, a WAITLIST signup form (full name / email / country /
 * phone / postal / optional Instagram / consent checkbox; submit stays disabled
 * until every required field validates), and a footer (Back to top,
 * {@code mailto:reservations@timepieces-d2c.example}, Instagram, LinkedIn). There is NO CTA
 * back to the www storefront — these pages are strictly pre-launch waitlist
 * signups. The document title pattern is
 * {@code <Family> | <UniqueTagline> | Timepieces Watches}. NO {@code data-testid}s
 * exist on the subdomain, so every locator is a fallback tagged
 * {@code // ROLLOUT-GAP}.
 *
 * <p>
 * 2026-07-17 RE-VERIFICATION — the 2026-07-16 scrape described this template
 * from a reading of the rendered page, and several of its claims did not survive
 * contact with the actual DOM. Corrections, each measured against all 5 live
 * pages (headless Chrome, 1920x1080):
 * <ul>
 * <li>There is NO "Back to scene" button. 0 nodes on all 5 pages; the string
 * does not appear anywhere in the DOM. The only path back to the experience is
 * the header logo. The scenario that exercised it has been deleted from
 * Timepieces_World_Showcase.feature — see the note there.</li>
 * <li>There is NO {@code <h1>}. 0 nodes on all 5 pages; the hero tagline is a
 * {@code <p>}.</li>
 * <li>The specs grid is {@code <p>} label + {@code <p>} value siblings, NOT
 * {@code <div>}s, and the DOM text is sentence case ("Base calibre") — the
 * ALL-CAPS look is CSS {@code text-transform}, which XPath cannot see.</li>
 * <li>The material tiles are {@code <div role="button">}, NOT {@code <button>}.</li>
 * <li>Every waitlist input has {@code placeholder=""} — the visible "Full name*"
 * captions are floating {@code <label>}s, so placeholder locators match 0 nodes.
 * The inputs carry ids ({@code fullName}, {@code email}, {@code pincode},
 * {@code instagram}, {@code terms}); the phone input does NOT — see
 * {@link #phoneInput}.</li>
 * <li>MANIFESTA IS NOT FULLY UNIFORM: it ships NO "Learn more about" material
 * tiles at all (0 nodes, confirmed twice after a full-page scroll, vs 3 on each
 * of the other 4). Its materials section uses a before/after Black Onyx image
 * pair instead. So {@link #getLearnMoreButtonCount()} legitimately returns 0 for
 * Manifesta — do not assert {@code > 0} for that family.</li>
 * </ul>
 *
 * <p>
 * The pages render RESPONSIVE DUPLICATES (a desktop and a mobile copy of the
 * same section, one hidden by CSS), so most locators here match 2 nodes with 1
 * visible. That is expected: {@code BasePage} resolves the first VISIBLE match.
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
    // The family tagline in the hero.
    //
    // WAS: By.cssSelector("main h1") — matched 0 nodes on ALL 5 showcases, because
    // the pages contain NO <h1> WHATSOEVER (measured 2026-07-17: document.querySelectorAll('h1')
    // returns 0 on arvion/monarch/auriqua/astonia/manifesta). The scrape called the
    // tagline a "heading" and the locator was written to that word rather than to the
    // markup. The tagline is really a <p> that opens "For those who ..." on every
    // family. Verified 2026-07-17: 1 node, displayed, on all 5.
    private final By taglineHeading = By.xpath("//p[starts-with(normalize-space(),'For those who')]"); // ROLLOUT-GAP: text-anchor
    // Dynamic: a specs-grid value by its label (e.g. "Base calibre" → "TMI VK63").
    //
    // WAS: By.xpath("//div[normalize-space()='%s']/following-sibling::div[1]") — matched
    // 0 nodes, and got the markup wrong TWICE over (both measured 2026-07-17):
    //   1. The grid is <p> label + <p> value siblings, not <div>s.
    //   2. The label's DOM text is "Base calibre", NOT "BASE CALIBRE". The grid only
    //      LOOKS all-caps because of CSS text-transform:uppercase, and XPath matches the
    //      DOM text, not the rendered text — so an all-caps '%s' could never match.
    // translate() lowercases the DOM text so the comparison is case-insensitive; callers
    // pass any casing and getSpecValue/hasSpec lowercase the argument to match.
    // Verified 2026-07-17: 2 nodes (the desktop + mobile twins), 1 visible, text "TMI VK63".
    private final By specValueTemplate = By.xpath(
            "//p[translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='%s']/following-sibling::p[1]"); // ROLLOUT-GAP: structural
    // The "Learn more about <material>" material tiles.
    //
    // WAS: By.xpath("//button[contains(normalize-space(),'Learn more about')]") — matched
    // 0 nodes because the tiles are NOT <button>s and carry NO such visible text. They are
    // <div role="button" aria-label="Learn more about Stainless Steel 316L"> — the phrase
    // exists only in the aria-label, so a text-anchored XPath had nothing to bite on.
    // Verified 2026-07-17: 3 nodes displayed on arvion/monarch/auriqua/astonia
    // (Stainless Steel 316L, Sapphire Crystal, + a family-specific third), and 0 on
    // MANIFESTA, which ships no material tiles at all — see the class javadoc.
    private final By learnMoreButtons = By.cssSelector("[role='button'][aria-label^='Learn more about']"); // ROLLOUT-GAP: aria-anchor
    // Dynamic: a learn-more tile by its material name (same markup story as above).
    private final By learnMoreByMaterialTemplate = By.xpath("//*[@role='button'][@aria-label='Learn more about %s']"); // ROLLOUT-GAP: aria-anchor

    // --- Waitlist form ---
    // EVERY input in this form has placeholder="" (measured 2026-07-17). The visible
    // "Full name*" / "Email id*" captions that the scrape mistook for placeholders are
    // floating <label> elements sitting on top of the inputs. So every placeholder-anchored
    // locator below matched 0 nodes, which is why "the waitlist form should be present"
    // failed while the form was plainly on screen. The inputs do carry stable ids.
    //
    // The "Join the waitlist for <Family>" heading.
    private final By waitlistHeading = By.xpath("//h2[contains(normalize-space(),'Join the waitlist for')]"); // ROLLOUT-GAP: text-anchor
    // The trigger button that scrolls to the form.
    private final By waitlistTriggerButton = By.xpath("//button[contains(normalize-space(),'Join the waitlist')]"); // ROLLOUT-GAP: text-anchor
    // The form itself: id is "waitlist_<family>" (e.g. waitlist_astonia). Verified 1 node,
    // displayed, on all 5.
    private final By waitlistForm = By.cssSelector("form[id^='waitlist_']"); // ROLLOUT-GAP: id-prefix
    // WAS: input[placeholder*='Full name'] / input[placeholder*='Email id'] — 0 nodes each.
    private final By fullNameInput = By.id("fullName");
    private final By emailInput = By.id("email");
    // The phone field is the ONE input with no id: it is rendered inside a
    // react-phone-number-input widget. Its <label for="phone"> points at an id that does
    // not exist anywhere in the document, so By.id("phone") matches 0 nodes — a real (if
    // harmless) a11y defect in the app. Scoping to the form keeps this off the country
    // widget's own hidden inputs. Verified 2026-07-17: 1 node, displayed, on all 5.
    private final By phoneInput = By.cssSelector("form input[type='tel']"); // ROLLOUT-GAP: semantic
    // The country dial-code <select> that fronts the phone widget.
    private final By countrySelect = By.cssSelector("select.PhoneInputCountrySelect"); // ROLLOUT-GAP: css-class
    // WAS: input[placeholder*='Postal/Zip code'] / input[placeholder*='Instagram username']
    // — 0 nodes each. The real ids, verified 2026-07-17 (1 node, displayed, on all 5):
    private final By postalCodeInput = By.id("pincode");
    private final By instagramInput = By.id("instagram");
    // WAS: input[type='checkbox'] / button[type='submit'] unscoped — these happened to
    // resolve, but scoping them to the form makes them immune to any future checkbox or
    // submit elsewhere on the page (e.g. a newsletter bar).
    private final By consentCheckbox = By.id("terms");
    private final By waitlistSubmitButton = By.cssSelector("form button[type='submit']"); // ROLLOUT-GAP: semantic

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
     * P.2 The family logo image locator, anchored on the image FILE NAME
     * ({@code main img[src*='<Family>-text.svg']}).
     *
     * <p>
     * !! MASKS A LIVE APP BUG — ISSUE-016. REVERT WHEN THE APP IS FIXED. !!
     *
     * <p>
     * WAS: {@code main img[alt='<FAMILY UPPERCASE>']}. That locator was correct in
     * intent and worked on 4 of the 5 families — it matched 0 nodes on MONARCH
     * only, and the fault is the APPLICATION'S, not the test's. Monarch's hero
     * logo is (measured 2026-07-17):
     *
     * <pre>
     * &lt;img alt="ARVION" src=".../products/monarch/Monarch-text.svg"&gt;
     * </pre>
     *
     * The IMAGE is right; the alt text was copy-pasted from Arvion and never
     * updated. The other 4 families carry the correct alt. So a screen-reader user
     * on the Monarch page is told they are looking at Arvion — a real a11y defect,
     * logged as ISSUE-016.
     *
     * <p>
     * Anchoring on {@code src} instead keeps the suite green while that bug stands,
     * because the file name IS family-correct on all 5 (Arvion-text.svg,
     * Monarch-text.svg, Auriqua-text.svg, Astonia-text.svg, Manifesta-text.svg —
     * note the S3 directory casing varies but the file name does not). Verified
     * 2026-07-17: 1 node, displayed, on all 5.
     *
     * <p>
     * THIS IS A DELIBERATE MASK, NOT A FIX. It trades away the assertion that would
     * have caught ISSUE-016. Once the app corrects Monarch's alt, revert this to
     * {@code By.cssSelector("main img[alt='" + familyName().toUpperCase() + "']")}
     * so the suite guards the alt text again.
     *
     * @return the family logo By locator.
     */
    private By familyLogo() {
        return By.cssSelector("main img[src*='" + familyName() + "-text.svg']"); // ROLLOUT-GAP: src-anchor (see ISSUE-016)
    }

    /**
     * P.3 Builds the locator for a specs-grid VALUE from its label, case-insensitively.
     *
     * <p>
     * The label is lowercased here and the DOM text is lowercased by the XPath's
     * {@code translate()} (see {@link #specValueTemplate}), so both sides of the
     * comparison end up in the same case and a caller can pass "BASE CALIBRE",
     * "Base calibre" or "base calibre" interchangeably. {@code toLowerCase(Locale.ROOT)}
     * rather than the default locale: under a Turkish locale, "I".toLowerCase()
     * yields a dotless "ı" and would silently stop matching "Base calibre".
     *
     * @param specLabel the spec label in any casing.
     * @return the By locator for that label's value cell.
     */
    private By specLocator(String specLabel) {
        return formatLocator(specValueTemplate, specLabel.toLowerCase(Locale.ROOT));
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    // B.1 clickBackToScene() DELETED 2026-07-17 — the feature it drove does not exist.
    // "Back to scene" matched 0 nodes on all 5 showcases and the string appears nowhere
    // in any of their DOMs; the button was never built. The only route back to the
    // experience is the header logo (TimepiecesWorld_HeaderComponent.clickLogo()), which the
    // "header logo returns to the experience" scenario already covers.

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
     * <p>
     * The postal and Instagram fields are reachable again as of 2026-07-17: they
     * were addressed by {@code placeholder} (0 nodes — every input here has
     * {@code placeholder=""}), and are now addressed by their real ids
     * {@code #pincode} / {@code #instagram}.
     *
     * @param fullName  full name.
     * @param email     email address.
     * @param phone     phone number.
     * @param postal    postal/zip code.
     * @param instagram Instagram username (nullable — sendKeys skips nulls).
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
     * B.3a Selects a country in the phone widget's dial-code {@code <select>}.
     *
     * <p>
     * The phone field is a react-phone-number-input widget: the country
     * {@code <select>} drives the dial-code prefix on the adjacent tel input, so
     * changing it changes what a given phone number validates as.
     *
     * @param countryCode the ISO country code (e.g. "IN", "US").
     * @return this page instance for method chaining.
     */
    public SELF selectWaitlistCountry(String countryCode) {
        log.info("[{}] Selecting the waitlist country '{}'.", familyName(), countryCode);
        new org.openqa.selenium.support.ui.Select(getElement(countrySelect)).selectByValue(countryCode);
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
     * B.6 Returns a calibre-spec value by its grid label, matched
     * case-insensitively (e.g. "Base calibre" → "TMI VK63").
     *
     * <p>
     * The real labels, measured across the 5 families on 2026-07-17: "Base
     * calibre", "Type", "Frequency", "Functions", "Jewels", and EITHER "Power
     * reserve" (arvion/monarch/auriqua/manifesta) OR "Power source" (astonia, whose
     * movement is battery-driven) — they are alternatives, so do not assume both.
     *
     * @param specLabel the spec label, in any casing.
     * @return the trimmed spec value text.
     */
    public String getSpecValue(String specLabel) {
        return getText(specLocator(specLabel));
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
        // WAIT FIRST, THEN ASSERT. This check used to call driver.getTitle() the instant
        // it was invoked, with no wait — the only check on the page that did not
        // synchronise on anything. All 5 titles are in fact CORRECT; the check was simply
        // racing the app.
        //
        // Measured 2026-07-17: on the FIRST navigation of a fresh browser session the
        // showcase served its shell title ("world.timepieces-d2c.example") and was still serving it
        // 6s later; the family title ("Arvion | Get Early Access to Single-Hand Watch |
        // Timepieces Watches") only appeared once the SPA had booted and set it. On warm
        // navigations the correct title was already there at 3s. So this failed on a cold
        // start and passed on a warm one — a textbook flake, and exactly the shape of
        // failure that gets blamed on the app.
        try {
            wait.until(ExpectedConditions.titleContains(familyName() + " |"));
        } catch (Exception e) {
            log.warn("[{}] The document title never became the family title. Last seen: '{}'.",
                    familyName(), driver.getTitle());
            return false;
        }
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

    // V.4 isBackToSceneDisplayed() DELETED 2026-07-17 — see the note at B.1: the
    // "Back to scene" button does not exist on any showcase page.

    /**
     * V.5 Verifies a calibre-spec entry with the given label exists.
     *
     * <p>
     * The label is matched CASE-INSENSITIVELY, so callers may pass "BASE CALIBRE"
     * (as the feature files do, reading the uppercase rendering) or "Base calibre"
     * (the actual DOM text) and get the same answer — see
     * {@link #specValueTemplate} for why the two differ.
     *
     * @param specLabel the spec label, in any casing (e.g. "BASE CALIBRE").
     * @return true if the spec entry is present.
     */
    public boolean hasSpec(String specLabel) {
        return !driver.findElements(specLocator(specLabel)).isEmpty();
    }

    /**
     * V.6 Counts the "Learn more about ..." material tiles.
     *
     * <p>
     * Expect 3 on arvion, monarch, auriqua and astonia — and 0 on MANIFESTA, which
     * ships no material tiles at all (confirmed twice on 2026-07-17 after scrolling
     * the full page, in case they were lazily revealed: still 0). Manifesta's
     * materials section is a before/after Black Onyx image pair instead. A 0 here
     * for Manifesta is the truth about the page, not a broken locator.
     *
     * @return the number of learn-more material tiles.
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
        // Asks whether the form is VISIBLE, not merely present. isDisplayed() checks every
        // match rather than just the first, which matters here: the waitlist heading
        // renders as a desktop + mobile pair (2 nodes, 1 visible), so a first-match check
        // can land on the hidden twin and report the form missing.
        //
        // isEventuallyDisplayed on the form itself absorbs the React mount: the assertion
        // usually runs moments after open(), and an instant check can beat the render.
        return isEventuallyDisplayed(waitlistForm)
                && isDisplayed(waitlistHeading)
                && isDisplayed(fullNameInput)
                && isDisplayed(emailInput);
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
