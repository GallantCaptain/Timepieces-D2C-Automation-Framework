package com.timepieces.automation.pages.web.account;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.timepieces.automation.pages.HeaderOnlyBasePage;
import com.timepieces.automation.utils.ConfigReader;

import lombok.extern.log4j.Log4j2;

/**
 * Onboarding_BecomeMore — the NEW guided onboarding journey at
 * {@code www.timepieces-d2c.example/becomemore}, which REPLACES the old onboarding flow.
 * The legacy {@link OnboardingPage} stays untouched until the old flow is
 * sunset, at which point it is swapped for these locators/data (product-owner
 * decision, 2026-07-16).
 *
 * <p>
 * ENTRY POINTS (product owner, 2026-07-16): this journey opens when a customer
 * clicks "Register Interest" / "Join the waitlist" on the Astonia/Manifesta
 * DROP pages — it is the onboarding for (usually first-time) users who have
 * not completed their profile. A second entry is DESIGN-PLANNED from the
 * "Register Interest" CTA on the regular timepiece PDPs (form filled + user
 * logged in when registering interest) — whether that PDP routing is already
 * implemented is UNVERIFIED (see TASK-PDP-009). Tests currently enter via the
 * direct URL / {@link #openSection(String)} deep-links, which work regardless.
 *
 * <p>
 * TESTABILITY LIMIT (ISSUE-014): no test / ephemeral user accounts exist, so
 * the first-time-user states (profile-incomplete gating, post-submit
 * completion) cannot be exercised deterministically — display-level and
 * gate-level assertions only until dev provides test accounts.
 *
 * <p>
 * SCRAPE NOTE (2026-07-16, CORRECTED 2026-07-17 by a live end-to-end walk in
 * Chrome): the flow is PUBLIC (reachable logged out — no login gate; every
 * screen renders for an anonymous visitor). The URL is {@code /becomemore} for
 * the intro and {@code /becomemore/profile?section=<key>} for the journey.
 * NOTE: {@code /become-more} (hyphenated) is a 404 — the path has no hyphen.
 *
 * <p>
 * The journey is <b>9 screens but only 7 NUMBERED steps</b>. The original
 * 2026-07-16 scrape recorded 8 sections and MISSED the {@code contact} screen
 * entirely, which is why several tests entered the journey in the wrong place.
 * Shape: an intro screen ("Become More" + "Begin your Profile" + "Takes less
 * than 60 seconds") opens a method-selector dialog ("How do you like to fill
 * your profile?" — LinkedIn import / "Start Manually" / an ICON-ONLY close),
 * and "Start Manually" lands on {@code ?section=contact}.
 *
 * <table border="1">
 * <caption>The real journey, verified screen by screen on 2026-07-17</caption>
 * <tr><th>#</th><th>section key</th><th>step indicator</th><th>H1</th><th>deep-linkable?</th></tr>
 * <tr><td>1</td><td>{@code contact}</td><td>(none)</td><td>Let's get started</td><td>YES — and every UNKNOWN key lands here too</td></tr>
 * <tr><td>2</td><td>{@code name}</td><td>Step 01 of 07</td><td>Tell us about yourself.</td><td><b>NO</b> — redirects to contact</td></tr>
 * <tr><td>3</td><td>{@code location}</td><td>Step 02 of 07</td><td>Where are you based?</td><td>YES</td></tr>
 * <tr><td>4</td><td>{@code about}</td><td>Step 03 of 07</td><td>More about you</td><td>YES</td></tr>
 * <tr><td>5</td><td>{@code building}</td><td>Step 04 of 07</td><td>What are you building towards?</td><td>YES</td></tr>
 * <tr><td>6</td><td>{@code watchjourney}</td><td>Step 05 of 07</td><td>Tell us about your watch journey</td><td>YES</td></tr>
 * <tr><td>7</td><td>{@code interest}</td><td>Step 06 of 07</td><td>What interests you?</td><td>YES</td></tr>
 * <tr><td>8</td><td>{@code timepieceshouse}</td><td>Step 07 of 07</td><td>The Timepieces House</td><td>YES</td></tr>
 * <tr><td>9</td><td>{@code photo}</td><td>(none)</td><td>Put a face to the story</td><td><b>NO</b> — redirects to contact</td></tr>
 * </table>
 *
 * <p>
 * <b>WHY {@code name} AND {@code photo} CANNOT BE DEEP-LINKED.</b> The app
 * guards those two screens behind journey state held in memory, so opening
 * their URL cold bounces the visitor back to {@code contact}. This is not a
 * locator problem and no wait fixes it — the app genuinely navigates away.
 * Verified 2026-07-17 for {@code photo} against every plausible alias too
 * ({@code avatar}, {@code profilephoto}, {@code picture}, {@code image},
 * {@code submit}, {@code review}, {@code final}): ALL redirect to
 * {@code contact}. The only way onto those two screens is to WALK the journey —
 * see {@link #walkToPhotoStep()}.
 *
 * <p>
 * Per-screen detail:
 * <ol>
 * <li>{@code contact} — {@code input[type='tel']} ("Phone") +
 * {@code input[type='email']} ("you@example.com") + "Continue". SEE THE APP-BUG
 * NOTE ON {@link #clickContactContinue()}: this screen validates NOTHING.</li>
 * <li>{@code name} — "Step 01 of 07": full name + salutation select + DOB +
 * persona select; Next ({@code button[type='submit']}) DISABLED
 * ({@code bg-gray-300 cursor-not-allowed}) until all four validate, then
 * ENABLED ({@code bg-[#141414]}).</li>
 * <li>{@code location} — city + use-location button.</li>
 * <li>{@code about} — bio textarea + voice input + "Refine my bio" (AI) +
 * "See examples".</li>
 * <li>{@code building} — goals textarea + voice + "Refine my story" +
 * examples.</li>
 * <li>{@code watchjourney} — SINGLE-select chips that AUTO-ADVANCE on click
 * (active = border-black). There is NO Next button to press here.</li>
 * <li>{@code interest} — MULTI-select chips (selected = dark bg); Next enables
 * after ≥1 selection.</li>
 * <li>{@code timepieceshouse} — optional inspiration textarea + "Need
 * inspiration?" (renders "Curated inspirations") + skip ("I'll do this
 * later"); Next enabled even when empty.</li>
 * <li>{@code photo} — final screen (no step indicator): avatar upload (hidden
 * {@code input[type=file]}) + consent checkbox that GATES the "Submit Profile"
 * button. Measured on the real screen 2026-07-17: {@code button.relative.w-44.h-44}
 * = 1 node, {@code input[type='checkbox']} = 1, {@code input[type='file']} = 1,
 * {@code //button[contains(.,'Submit Profile')]} = 1. <b>This screen has NO
 * {@code button[type='submit']}</b> — "Submit Profile" carries no {@code type}
 * attribute — so {@link #isNextEnabled()} is meaningless here and would find 0
 * nodes; use {@link #isSubmitProfileEnabled()} instead.</li>
 * </ol>
 * ZERO page-specific {@code data-testid}s exist on the flow (only the shared
 * header) — every locator is a fallback tagged {@code // ROLLOUT-GAP}.
 *
 * <p>
 * SUBMISSION WARNING: {@link #clickSubmitProfile()} creates a REAL Timepieces
 * profile. Automated tests should exercise field entry, chip selection and the
 * consent/validation gates only, and must NOT submit against production. The
 * consent gate is asserted as a disabled->enabled TRANSITION; the button is
 * never actually pressed.
 *
 * FLUENT INTERFACE:
 * All methods that remain on this page return {@code this}
 * (Onboarding_BecomeMore) to allow method chaining in step definitions.
 *
 * @version 1.0
 * @author Avinash Rath
* @Description "Description of Onboarding_BecomeMore"
 * @since 2026-07-16
 */

@Log4j2
public class Onboarding_BecomeMore extends HeaderOnlyBasePage<Onboarding_BecomeMore> {

    // ==========================================
    // Page Constants
    // ==========================================
    // The Page Path Variable (the intro screen).
    private static final String PAGE_PATH = "/becomemore";
    // The per-section path prefix (section key appended by openSection()).
    private static final String SECTION_PATH_PREFIX = "/becomemore/profile?section=";

    // The cookie the site writes once the consent bar has been answered. This is
    // the termination signal for ensureConsentBarCleared() — see the note there
    // for why "the bar is not on screen" is NOT a usable signal on this journey.
    private static final String CONSENT_COOKIE = "timepieces_cookie_consent";
    // Generous enough for a cold load to hydrate and mount the bar, short enough
    // that a genuinely stuck bar reports quickly rather than stalling the suite.
    private static final Duration CONSENT_BAR_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration CONSENT_BAR_POLL = Duration.ofMillis(500);

    // The 9 section keys, in journey order. `contact` is FIRST — it was missing
    // from the 2026-07-16 scrape entirely, which is what sent tests into the
    // journey at the wrong screen (see the class javadoc table).
    public static final String SECTION_CONTACT = "contact";
    public static final String SECTION_NAME = "name";
    public static final String SECTION_LOCATION = "location";
    public static final String SECTION_ABOUT = "about";
    public static final String SECTION_BUILDING = "building";
    public static final String SECTION_WATCH_JOURNEY = "watchjourney";
    public static final String SECTION_INTEREST = "interest";
    public static final String SECTION_TIMEPIECES_HOUSE = "timepieceshouse";
    public static final String SECTION_PHOTO = "photo";

    // ------------------------------------------------------------------
    // WALK DATA — the values {@link #walkToPhotoStep()} types into the journey
    // to reach the two screens that refuse to deep-link (`name`, `photo`).
    //
    // Nothing here is ever persisted: the walk stops ON the photo screen and the
    // "Submit Profile" button is never pressed, so no Timepieces profile is created
    // and the phone number is never sent for verification. The values only have
    // to satisfy the client-side validators.
    // ------------------------------------------------------------------
    private static final String WALK_PHONE = "9876543210";
    private static final String WALK_EMAIL = "qa.automation@example.com";
    private static final String WALK_FULL_NAME = "Test User";
    private static final String WALK_SALUTATION = "Mr";
    private static final String WALK_DOB = "01/01/1990";
    private static final String WALK_PERSONA = "Professional";
    private static final String WALK_CITY = "Mumbai";
    private static final String WALK_BIO = "Automated test bio entered by the Timepieces QA suite.";
    private static final String WALK_GOALS = "Automated test goals entered by the Timepieces QA suite.";
    // The single-select chip that AUTO-ADVANCES watchjourney -> interest.
    private static final String WALK_WATCH_CHIP = "This would be my first serious watch";
    // Any one interest satisfies the "at least 1" gate on the interest screen.
    private static final String WALK_INTEREST_CHIP = "Watches";

    // ==========================================
    // Constructor
    // ==========================================
    public Onboarding_BecomeMore(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // PAGE METADATA (drives the inherited fluent open())
    // ==========================================
    @Override
    protected String getPagePath() {
        return PAGE_PATH;
    }

    // ==========================================
    // LOCATORS
    // ==========================================
    // --- Intro screen ---
    private final By introHeading = By.xpath("//main//h1[contains(normalize-space(),'Become More')]"); // ROLLOUT-GAP: text-anchor
    private final By beginProfileButton = By.xpath("//button[contains(normalize-space(),'Begin your Profile')]"); // ROLLOUT-GAP: text-anchor

    // --- Method-selector dialog ---
    private final By methodDialogHeading = By.xpath("//h2[contains(normalize-space(),'How do you like to fill your profile?')]"); // ROLLOUT-GAP: text-anchor
    private final By linkedInImportButton = By.xpath("//button[contains(normalize-space(),'Import data from LinkedIn')]"); // ROLLOUT-GAP: text-anchor
    private final By startManuallyButton = By.xpath("//button[contains(normalize-space(),'Start Manually')]"); // ROLLOUT-GAP: text-anchor
    // The dialog's close control is ICON-ONLY — it has no text node at all.
    //
    // The old locator was //button[normalize-space()='Close'], which matched
    // 0 NODES (measured 2026-07-17): it was looking for the literal text "Close"
    // inside the button, and there is none. Every attempt to close the dialog
    // therefore timed out against a dialog that closes perfectly for a human.
    //
    // The real markup exposes the control only via aria-label="Close". That
    // attribute alone matches 2 NODES site-wide, because the cookie consent bar
    // ships its own mobile close button with the identical label — so the
    // locator is SCOPED to the button that sits next to the dialog's own <h2>.
    // Scoped: 1 node, and a real click dismisses the dialog (verified 2026-07-17).
    private final By methodCloseButton = By.xpath(
            "//button[@aria-label='Close'][following-sibling::h2[contains(normalize-space(),'How do you like to fill your profile?')]]"); // ROLLOUT-GAP: aria-anchor

    // --- Screen 1: contact ("Let's get started" — the REAL first screen) ---
    // Missing from the 2026-07-16 scrape. "Start Manually" in the method dialog
    // lands here, and so does every unrecognised ?section= key.
    private final By contactPhoneInput = By.cssSelector("input[type='tel']"); // ROLLOUT-GAP: semantic (placeholder "Phone")
    private final By contactEmailInput = By.cssSelector("input[type='email']"); // ROLLOUT-GAP: semantic (placeholder "you@example.com")
    private final By contactContinueButton = By.xpath("//button[contains(normalize-space(),'Continue')]"); // ROLLOUT-GAP: text-anchor

    // --- Shared step chrome ---
    // The old locator was //main//*[contains(normalize-space(),'Step 0')], which
    // matched 6 NODES (measured 2026-07-17) — the <span> that actually holds the
    // text PLUS all five of its ancestor containers, because contains() on an
    // ancestor is true whenever the text appears anywhere inside it.
    //
    // getText() on the first match therefore returned the ENTIRE PAGE TEXT. The
    // assertion only ever passed by accident: the step definition uses
    // .contains("Step 01 of 07"), and a haystack of the whole page happens to
    // contain that needle. A .isEqualTo() assertion would have exposed it
    // immediately, and the locator would have reported "Step 03 of 07" as a pass
    // on a page showing step 1.
    //
    // starts-with() on the <span> itself matches EXACTLY 1 node on every numbered
    // screen, with text "Step 0N of 07" (verified on all 7 numbered steps).
    private final By stepIndicator = By.xpath("//span[starts-with(normalize-space(),'Step 0')]"); // ROLLOUT-GAP: text-anchor
    private final By stepHeading = By.cssSelector("main h1"); // ROLLOUT-GAP: structural
    private final By nextSubmitButton = By.cssSelector("button[type='submit']"); // ROLLOUT-GAP: semantic
    private final By cancelButton = By.xpath("//main//button[normalize-space()='Cancel']"); // ROLLOUT-GAP: text-anchor
    // WEAK: the back control is an icon-only button in the upper-left.
    private final By backArrowButton = By.xpath("(//main//button[.//*[local-name()='svg']])[1]"); // ROLLOUT-GAP · WEAK: structural

    // --- Step 1: personal info ---
    private final By fullNameInput = By.cssSelector("textarea[placeholder='Enter your full name']"); // ROLLOUT-GAP: placeholder-anchor
    private final By salutationSelect = By.xpath("(//select)[1]"); // ROLLOUT-GAP · WEAK: structural (first select)
    private final By dateOfBirthInput = By.cssSelector("textarea[placeholder='DD/MM/YYYY']"); // ROLLOUT-GAP: placeholder-anchor
    private final By personaSelect = By.xpath("(//select)[2]"); // ROLLOUT-GAP · WEAK: structural (second select)

    // --- Step 2: location ---
    private final By cityInput = By.cssSelector("textarea[placeholder='City']"); // ROLLOUT-GAP: placeholder-anchor
    private final By useLocationButton = By.cssSelector("button.p-2.text-gray-600"); // ROLLOUT-GAP · WEAK: utility-class

    // --- Steps 3/4: bio + goals ---
    private final By bioTextarea = By.cssSelector("textarea[placeholder*='SaaS company']"); // ROLLOUT-GAP: placeholder-anchor
    private final By goalsTextarea = By.cssSelector("textarea[placeholder*='leadership role']"); // ROLLOUT-GAP: placeholder-anchor
    private final By refineButton = By.xpath("//button[contains(normalize-space(),'Refine my')]"); // ROLLOUT-GAP: text-anchor
    private final By seeExamplesButton = By.xpath("//button[contains(normalize-space(),'See examples')]"); // ROLLOUT-GAP: text-anchor

    // --- Steps 5/6: choice chips (single-select auto-advance / multi-select) ---
    private final By choiceChips = By.cssSelector("form button[type='button']"); // ROLLOUT-GAP: structural
    private final By chipByTextTemplate = By.xpath("//form//button[@type='button'][contains(normalize-space(),'%s')]"); // ROLLOUT-GAP: text-anchor

    // --- Step 7: Timepieces House ---
    private final By houseTextarea = By.cssSelector("textarea[placeholder*='Anand Mahindra']"); // ROLLOUT-GAP: placeholder-anchor
    private final By needInspirationButton = By.xpath("//button[contains(normalize-space(),'Need inspiration?')]"); // ROLLOUT-GAP: text-anchor
    private final By curatedInspirationsHeading = By.xpath("//h2[contains(normalize-space(),'Curated inspirations')]"); // ROLLOUT-GAP: text-anchor
    private final By skipLaterButton = By.xpath("//button[contains(normalize-space(),\"I'll do this later\")]"); // ROLLOUT-GAP: text-anchor

    // --- Step 8: photo + submit ---
    private final By avatarUploadButton = By.cssSelector("button.relative.w-44.h-44"); // ROLLOUT-GAP: utility-class
    private final By photoFileInput = By.cssSelector("input[type='file']"); // ROLLOUT-GAP: semantic (hidden — sendKeys only)
    private final By consentCheckbox = By.cssSelector("input[type='checkbox']"); // ROLLOUT-GAP: semantic
    private final By submitProfileButton = By.xpath("//button[contains(normalize-space(),'Submit Profile')]"); // ROLLOUT-GAP: text-anchor

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================
    /**
     * P.1 Blocks until the browser reports the given section in its URL.
     *
     * <p>
     * This is the walk's safety rail. /becomemore is a single-page app: clicking
     * "Next" swaps the React tree in place rather than loading a document, so
     * Selenium returns from the click the instant it lands — long before the next
     * screen exists. Without this wait the very next {@code clickNext()} would
     * find the OLD screen's still-mounted submit button and press it a second
     * time, and the walk would silently desynchronise from the journey.
     *
     * <p>
     * The URL is the right thing to wait on because it is what the app itself
     * uses to drive the screen, and it is what {@link #isOnSection(String)}
     * already asserts.
     *
     * @param sectionKey the section key expected to appear in the URL.
     */
    private void waitForSection(String sectionKey) {
        log.debug("Awaiting the Become More section: [{}]", sectionKey);
        wait.until(d -> isOnSection(sectionKey));
    }

    /**
     * P.2 Blocks until the cookie consent bar is genuinely answered.
     *
     * <p>
     * ================= WHY THIS IS NOT REDUNDANT WITH dismissOverlays() ==============
     * <br>
     * The inherited sweep answers the bar IF the bar is on screen when it looks.
     * On this journey it frequently is not, and the result was a real failure —
     * measured on 2026-07-17, not theorised:
     *
     * <p>
     * The Next control on every numbered step is a 64x64 button whose rect is
     * {@code [1148, 897, 64, 64]}, so its click point is <b>(1180, 929)</b>. The
     * consent bar is {@code fixed inset-x-0 bottom-0 z-[9999]} and its
     * "Cookie Preferences" button covers exactly that point. With the bar up,
     * {@code document.elementFromPoint(1180, 929)} returns
     * <br>
     * {@code <button class="jsx-d09c14e3dcf74073 px-5 py-2.5 ... bg-[#3a3a3a] ...">Cookie Preferences</button>}
     * <br>
     * and Selenium throws ElementClickInterceptedException naming that exact
     * button. With the bar answered, the same point returns the Next button's own
     * {@code <svg>} and the click lands. The bar sits ON TOP of the journey's
     * primary control.
     *
     * <p>
     * The bar mounts AFTER React hydration — later than the shared handler's
     * 1.2s presence poll — so a single sweep straight after {@code driver.get()}
     * is a coin flip: it wins on a warm, fast load and loses on a cold or busy
     * one. That is exactly the shape of an intermittent failure, and it is why
     * the walk died on its very first Next while the same code passed in
     * isolation.
     *
     * <p>
     * The fix keys on the SITE'S OWN durable signal instead of on timing: the
     * choice is persisted as the {@code timepieces_cookie_consent} cookie, and once
     * that cookie exists the bar never mounts again for the session. So this
     * re-sweeps until the cookie appears rather than sweeping once and hoping.
     * When consent is already stored it costs a single cookie read.
     * ================================================================================
     */
    private void ensureConsentBarCleared() {
        if (isConsentStored()) {
            return; // Already answered this session — the bar cannot come back.
        }
        String mode = ConfigReader.get("cookie.consent.mode");
        if (mode != null && "ignore".equalsIgnoreCase(mode.trim())) {
            // The operator asked for the bar to be left alone (diagnostics). Do not
            // sit here waiting for a cookie that will never be written.
            return;
        }
        try {
            // Re-sweep on each poll: the bar may not have mounted yet, so "not found"
            // is not the same as "handled".
            new WebDriverWait(driver, CONSENT_BAR_TIMEOUT, CONSENT_BAR_POLL)
                    .until(d -> {
                        dismissOverlays();
                        return isConsentStored();
                    });
            log.debug("Cookie consent answered; timepieces_cookie_consent is now set.");
        } catch (Exception e) {
            // Never fail a navigation over this. If the bar really is stuck the
            // caller's click will fail with a clear interception message naming the
            // offending element, which is far more diagnosable than a timeout here.
            log.warn("The cookie consent bar was not confirmed answered within {}s. "
                    + "If a click is intercepted near the bottom of the viewport, this is why.",
                    CONSENT_BAR_TIMEOUT.toSeconds());
        }
    }

    /**
     * P.3 Reports whether the site has stored a cookie-consent choice.
     *
     * <p>
     * This is the durable, app-authored signal that the bar is done with — far
     * more trustworthy than "I could not see the bar just now", which is also
     * true a moment BEFORE it appears.
     *
     * @return true if the {@code timepieces_cookie_consent} cookie exists.
     */
    private boolean isConsentStored() {
        try {
            return driver.manage().getCookieNamed(CONSENT_COOKIE) != null;
        } catch (Exception e) {
            return false;
        }
    }

    // ==========================================
    // BUSINESS LIBRARY
    // ==========================================
    /**
     * B.1 Clicks "Begin your Profile" on the intro screen (opens the
     * method-selector dialog).
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore clickBeginProfile() {
        log.info("Beginning the Become More profile journey.");
        clickElement(beginProfileButton);
        return this;
    }

    /**
     * B.2 Chooses "Start Manually" in the method-selector dialog.
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore chooseStartManually() {
        log.info("Choosing manual profile entry.");
        clickElement(startManuallyButton);
        return this;
    }

    /**
     * B.3 Chooses "Import data from LinkedIn" in the method-selector dialog.
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore chooseLinkedInImport() {
        log.info("Choosing LinkedIn import.");
        clickElement(linkedInImportButton);
        return this;
    }

    /**
     * B.4 Closes the method-selector dialog.
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore closeMethodDialog() {
        log.info("Closing the method-selector dialog.");
        clickElement(methodCloseButton);
        return this;
    }

    /**
     * B.5 Deep-links to a journey section via its URL key (see the
     * {@code SECTION_*} constants).
     *
     * <p>
     * <b>NOT every section deep-links.</b> {@code name} and {@code photo} are
     * guarded by in-memory journey state and REDIRECT to {@code contact} when
     * opened cold, as does any unrecognised key — so this method landing
     * somewhere is not proof it landed where you asked. Callers that need those
     * two screens must use {@link #walkToPhotoStep()}. Confirm the destination
     * with {@link #isOnSection(String)} when it matters.
     *
     * @param sectionKey the section query value (e.g. {@code SECTION_LOCATION}).
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore openSection(String sectionKey) {
        String url = getBaseUrl() + SECTION_PATH_PREFIX + sectionKey;
        log.info("Opening Become More section: [{}]", url);
        driver.get(url);
        // The inherited open() clears the overlays for us, but this method
        // deliberately bypasses open() (it needs a query string, and getPagePath()
        // has no way to carry one), so the teardown has to be repeated here. It is
        // not optional: the consent bar is pinned at the bottom at z-9999, and
        // BOTH the Timepieces House screen's "I'll do this later" (y=918..940) and
        // every numbered step's Next button (click point 1180, 929) sit
        // underneath it.
        dismissOverlays(); // Clears the Astonia drop card too, not just the bar.
        ensureConsentBarCleared(); // Handles the bar mounting AFTER that sweep.
        return this;
    }

    /**
     * B.5a Fills the contact screen's phone and email fields.
     *
     * @param phone the phone number.
     * @param email the email address.
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore fillContactDetails(String phone, String email) {
        log.info("Filling the Become More contact screen.");
        sendKeys(contactPhoneInput, phone);
        sendKeys(contactEmailInput, email);
        return this;
    }

    /**
     * B.5b Clicks "Continue" on the contact screen (advances to "Step 01 of 07").
     *
     * <p>
     * ============================ APP BUG — NOT A TEST BUG ============================
     * <br>
     * The contact screen performs <b>ZERO validation</b>. Verified live on
     * 2026-07-17: clicking "Continue" with BOTH the phone and the email field
     * EMPTY advances straight to Step 01, and the button is never disabled at
     * any point. This is inconsistent with every other gated screen in the same
     * journey (the {@code name} screen disables Next until all four fields
     * validate, {@code interest} until at least one chip is picked), and the
     * screen's own copy promises otherwise: "How can we reach you? We'll verify
     * your number at the end."
     *
     * <p>
     * So a visitor can complete the entire Become More journey with no contact
     * details whatsoever — and the app has already told them it intends to
     * verify a number it never collected.
     *
     * <p>
     * DELIBERATELY NOT WORKED AROUND IN THE TESTS. There is no assertion here
     * that the button gates, because it does not gate, and writing a test that
     * expects the broken behaviour would cement it. <b>File this with dev.</b>
     * When it is fixed, add a gating scenario to
     * BecomeMore_Onboarding.feature mirroring the {@code name}-screen one.
     * =================================================================================
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore clickContactContinue() {
        log.info("Continuing from the Become More contact screen.");
        clickElement(contactContinueButton);
        return this;
    }

    /**
     * B.5c Enters the journey and stops on the personal-info screen
     * ("Step 01 of 07").
     *
     * <p>
     * WHY THIS EXISTS INSTEAD OF {@code openSection(SECTION_NAME)}: the
     * {@code name} screen does not deep-link. Opening
     * {@code ?section=name} cold REDIRECTS to {@code contact} (verified
     * 2026-07-17), and {@code contact} has no step indicator — so the old
     * {@code openSection(SECTION_NAME)} entry left the test staring at the wrong
     * screen until the 15s wait for "Step 01 of 07" expired. The failure looked
     * like a flaky locator; it was the app navigating away.
     *
     * <p>
     * {@code contact} is one screen earlier, so entering there and pressing
     * Continue reaches Step 01 legitimately, the way a real visitor does.
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore enterJourneyAtPersonalInfo() {
        log.info("Entering the Become More journey via the contact screen to reach Step 01.");
        openSection(SECTION_CONTACT);
        fillContactDetails(WALK_PHONE, WALK_EMAIL);
        clickContactContinue();
        waitForSection(SECTION_NAME);
        return this;
    }

    /**
     * B.5d Walks the ENTIRE journey — all 9 screens — and stops ON the photo
     * screen without submitting.
     *
     * <p>
     * WHY A WALK IS THE ONLY OPTION: the {@code photo} screen cannot be
     * deep-linked. {@code ?section=photo} redirects to {@code contact}, and so
     * does every alias tried on 2026-07-17 ({@code avatar}, {@code profilephoto},
     * {@code picture}, {@code image}, {@code submit}, {@code review},
     * {@code final}). The app guards the screen behind journey state that only
     * exists once the journey has actually been walked. No locator and no wait
     * can reach it; the test has to earn its way there.
     *
     * <p>
     * The route below was executed successfully end-to-end in Chrome on
     * 2026-07-17. Two steps are easy to get wrong:
     * <ul>
     * <li>{@code watchjourney} has NO Next button — its single-select chip
     * AUTO-ADVANCES on click. Calling {@code clickNext()} there fails.</li>
     * <li>{@code timepieceshouse} is left via "I'll do this later" rather than
     * Next, which keeps the walk from typing anything the step does not
     * need.</li>
     * </ul>
     *
     * <p>
     * SAFETY: this stops ON the photo screen. It never presses "Submit Profile",
     * so no real profile is created — see the class-level warning.
     *
     * @return this page instance for method chaining, parked on the photo screen.
     */
    public Onboarding_BecomeMore walkToPhotoStep() {
        log.info("Walking the full Become More journey to reach the photo screen.");

        // Screen 1/9 — contact ("Let's get started"). No step indicator.
        openSection(SECTION_CONTACT);
        fillContactDetails(WALK_PHONE, WALK_EMAIL);
        clickContactContinue();

        // Screen 2/9 — name ("Step 01 of 07").
        waitForSection(SECTION_NAME);
        fillPersonalInfo(WALK_FULL_NAME, WALK_SALUTATION, WALK_DOB, WALK_PERSONA);
        clickNext();

        // Screen 3/9 — location ("Step 02 of 07").
        waitForSection(SECTION_LOCATION);
        enterCity(WALK_CITY);
        clickNext();

        // Screen 4/9 — about ("Step 03 of 07").
        waitForSection(SECTION_ABOUT);
        enterBio(WALK_BIO);
        clickNext();

        // Screen 5/9 — building ("Step 04 of 07").
        waitForSection(SECTION_BUILDING);
        enterGoals(WALK_GOALS);
        clickNext();

        // Screen 6/9 — watchjourney ("Step 05 of 07"). The chip auto-advances;
        // there is deliberately no clickNext() here.
        waitForSection(SECTION_WATCH_JOURNEY);
        selectChip(WALK_WATCH_CHIP);

        // Screen 7/9 — interest ("Step 06 of 07"). Next enables after >= 1 chip.
        waitForSection(SECTION_INTEREST);
        selectChip(WALK_INTEREST_CHIP);
        clickNext();

        // Screen 8/9 — timepieceshouse ("Step 07 of 07"). Optional; skip it.
        waitForSection(SECTION_TIMEPIECES_HOUSE);
        skipHouseStep();

        // Screen 9/9 — photo ("Put a face to the story"). No step indicator.
        waitForSection(SECTION_PHOTO);
        log.info("Reached the Become More photo screen. Submit Profile will NOT be pressed.");
        return this;
    }

    /**
     * B.6 Fills the step-1 personal-info fields (name, salutation, DOB, persona).
     *
     * @param fullName   the full name.
     * @param salutation the salutation option (Mr / Mrs / Ms / Prefer not to say).
     * @param dob        the date of birth (DD/MM/YYYY).
     * @param persona    the persona option (e.g. Entrepreneur, Professional).
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore fillPersonalInfo(String fullName, String salutation, String dob, String persona) {
        log.info("Filling the Become More personal-info step.");
        sendKeys(fullNameInput, fullName);
        new Select(getElement(salutationSelect)).selectByVisibleText(salutation);
        sendKeys(dateOfBirthInput, dob);
        new Select(getElement(personaSelect)).selectByVisibleText(persona);
        return this;
    }

    /**
     * B.7 Enters the city on the location step.
     *
     * @param city the city name.
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore enterCity(String city) {
        sendKeys(cityInput, city);
        return this;
    }

    /**
     * B.8 Enters the bio on the about step.
     *
     * @param bio the bio text.
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore enterBio(String bio) {
        sendKeys(bioTextarea, bio);
        return this;
    }

    /**
     * B.9 Enters the goals on the building step.
     *
     * @param goals the goals text.
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore enterGoals(String goals) {
        sendKeys(goalsTextarea, goals);
        return this;
    }

    /**
     * B.9b Clicks the "use my location" button beside the city input.
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore clickUseLocation() {
        log.info("Clicking the use-location button.");
        clickElement(useLocationButton);
        return this;
    }

    /**
     * B.9c Clicks the AI refine button ("Refine my bio" / "Refine my story",
     * depending on the step).
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore clickRefine() {
        log.info("Clicking the AI refine button.");
        clickElement(refineButton);
        return this;
    }

    /**
     * B.9d Opens the "See examples" helper on the bio/goals steps.
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore clickSeeExamples() {
        log.info("Opening the step examples helper.");
        clickElement(seeExamplesButton);
        return this;
    }

    /**
     * B.10 Clicks a choice chip by its (partial) label — single-select chips
     * (watch journey) AUTO-ADVANCE to the next step; multi-select chips
     * (interests) toggle.
     *
     * @param chipText the chip label (or a distinctive fragment).
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore selectChip(String chipText) {
        log.info("Selecting the choice chip: [{}]", chipText);
        clickElement(formatLocator(chipByTextTemplate, chipText));
        return this;
    }

    /**
     * B.11 Enters the Timepieces House inspiration text (optional step).
     *
     * @param inspiration the inspiration text.
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore enterHouseInspiration(String inspiration) {
        sendKeys(houseTextarea, inspiration);
        return this;
    }

    /**
     * B.12 Opens the "Need inspiration?" curated suggestions.
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore clickNeedInspiration() {
        clickElement(needInspirationButton);
        return this;
    }

    /**
     * B.13 Skips the Timepieces House step via "I'll do this later" (advances
     * immediately).
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore skipHouseStep() {
        log.info("Skipping the Timepieces House step.");
        clickElement(skipLaterButton);
        return this;
    }

    /**
     * B.14 Attaches a profile photo via the hidden file input (sendKeys with an
     * absolute path — the input is not clickable).
     *
     * @param absoluteFilePath the absolute path of the image to upload.
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore attachProfilePhoto(String absoluteFilePath) {
        log.info("Attaching a profile photo.");
        getElementPresence(photoFileInput).sendKeys(absoluteFilePath);
        return this;
    }

    /**
     * B.15 Ticks the storage/processing consent checkbox (this GATES the final
     * submit).
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore tickConsent() {
        log.info("Ticking the profile consent checkbox.");
        clickElement(consentCheckbox);
        return this;
    }

    /**
     * B.16 Clicks the step's Next/submit control ({@code button[type='submit']}).
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore clickNext() {
        log.info("Advancing to the next Become More step.");
        clickElement(nextSubmitButton);
        return this;
    }

    /**
     * B.17 Clicks the icon-only Back control.
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore clickBack() {
        log.info("Going back one Become More step.");
        clickElement(backArrowButton);
        return this;
    }

    /**
     * B.18 Clicks Cancel (leaves the journey).
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore clickCancel() {
        log.info("Cancelling the Become More journey.");
        clickElement(cancelButton);
        return this;
    }

    /**
     * B.19 Submits the completed profile.
     * <p>
     * WARNING: this creates a REAL Timepieces profile. Do NOT call against
     * production in automated runs — see the class-level warning.
     *
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore clickSubmitProfile() {
        log.warn("Submitting the Become More profile — this creates a REAL profile.");
        clickElement(submitProfileButton);
        return this;
    }

    /**
     * B.20 Returns the current step indicator text (e.g. "Step 03 of 07"; the
     * final photo step has none).
     *
     * @return the trimmed step-indicator text.
     */
    public String getStepIndicatorText() {
        return getText(stepIndicator);
    }

    /**
     * B.21 Returns the current step heading text.
     *
     * @return the trimmed step heading.
     */
    public String getStepHeadingText() {
        return getText(stepHeading);
    }

    // ==========================================
    // VERIFICATION METHODS
    // ==========================================
    /**
     * V.1 Verifies the intro screen is displayed ("Become More" + begin button).
     *
     * @return true if the intro screen is displayed.
     */
    public boolean isIntroDisplayed() {
        return isDisplayed(introHeading) && !driver.findElements(beginProfileButton).isEmpty();
    }

    /**
     * V.2 Verifies the method-selector dialog is displayed with both entry
     * options.
     *
     * @return true if the dialog and both options are present.
     */
    public boolean isMethodDialogDisplayed() {
        return !driver.findElements(methodDialogHeading).isEmpty()
                && !driver.findElements(linkedInImportButton).isEmpty()
                && !driver.findElements(startManuallyButton).isEmpty();
    }

    /**
     * V.2a Verifies the method-selector dialog has CLOSED.
     *
     * <p>
     * Waits rather than checking instantly: the dialog closes with a CSS
     * transition, so an immediate check catches it mid-animation and calls a
     * working close button broken.
     *
     * @return true if the dialog is no longer visible.
     */
    public boolean isMethodDialogDismissed() {
        return isEventuallyHidden(methodDialogHeading);
    }

    /**
     * V.3 Verifies the browser sits on the given journey section (URL query).
     *
     * @param sectionKey the expected section key.
     * @return true if the current URL carries {@code section=<key>}.
     */
    public boolean isOnSection(String sectionKey) {
        String url = driver.getCurrentUrl();
        return url != null && url.contains("section=" + sectionKey);
    }

    /**
     * V.4 Reports whether the step's Next/submit control is enabled (step 1 keeps
     * it DISABLED until every field validates).
     *
     * <p>
     * DO NOT CALL THIS ON THE PHOTO SCREEN. It resolves
     * {@code button[type='submit']}, and the photo screen has none — "Submit
     * Profile" carries no {@code type} attribute at all (measured 2026-07-17), so
     * this would find 0 nodes and report a disabled button that does not exist.
     * Use {@link #isSubmitProfileEnabled()} there.
     *
     * <p>
     * The class check matters as much as {@code isEnabled()}: the journey styles
     * its blocked state as {@code bg-gray-300 cursor-not-allowed}, and a button
     * can carry that styling while still reporting {@code isEnabled() == true} to
     * Selenium, because Selenium only reads the {@code disabled} attribute.
     *
     * @return true if the Next control is enabled.
     */
    public boolean isNextEnabled() {
        try {
            var button = getElementPresence(nextSubmitButton);
            String cls = button.getAttribute("class");
            boolean styledDisabled = cls != null && cls.contains("cursor-not-allowed");
            return button.isEnabled() && !styledDisabled;
        } catch (Exception e) {
            log.warn("Could not evaluate the Next control state.", e);
            return false;
        }
    }

    /**
     * V.5 Counts the choice chips on the current step (watch journey: 5;
     * interests: 12).
     *
     * @return the number of chips.
     */
    public int getChipCount() {
        return driver.findElements(choiceChips).size();
    }

    /**
     * V.6 Reports whether a chip is in its SELECTED state — single-select chips
     * flip to a black border; multi-select chips flip to the dark background.
     *
     * @param chipText the chip label (or fragment).
     * @return true if the chip renders selected.
     */
    public boolean isChipSelected(String chipText) {
        By chip = formatLocator(chipByTextTemplate, chipText);
        if (driver.findElements(chip).isEmpty()) {
            return false;
        }
        String cls = getAttribute(chip, "class");
        return cls != null && (cls.contains("border-black") || cls.contains("bg-[#141414]"));
    }

    /**
     * V.7 Verifies the "Curated inspirations" suggestions rendered (after
     * "Need inspiration?").
     *
     * @return true if the curated inspirations heading is present.
     */
    public boolean isCuratedInspirationsDisplayed() {
        return !driver.findElements(curatedInspirationsHeading).isEmpty();
    }

    /**
     * V.8 Verifies the photo step renders its avatar upload + consent gate.
     *
     * @return true if the avatar control and consent checkbox are present.
     */
    public boolean isPhotoStepDisplayed() {
        return !driver.findElements(avatarUploadButton).isEmpty()
                && !driver.findElements(consentCheckbox).isEmpty();
    }

    /**
     * V.9 Reports, RIGHT NOW, whether "Submit Profile" is enabled (it stays
     * DISABLED until the consent checkbox is ticked).
     *
     * <p>
     * Instantaneous by design — this is the correct question for the
     * BEFORE-consent assertion, which must not sit in a wait loop hoping a button
     * it wants to be disabled stays disabled. For the AFTER-consent assertion use
     * {@link #waitUntilSubmitProfileEnabled()}, which tolerates React's re-render.
     *
     * @return true if the submit button is enabled at this instant.
     */
    public boolean isSubmitProfileEnabled() {
        try {
            return getElementPresence(submitProfileButton).isEnabled();
        } catch (Exception e) {
            log.warn("Could not evaluate the Submit Profile state.", e);
            return false;
        }
    }

    /**
     * V.10 Waits for "Submit Profile" to become enabled and reports whether it
     * did.
     *
     * <p>
     * Ticking the consent box flips a React state flag, and React re-renders on
     * its own schedule rather than synchronously with Selenium's click. Reading
     * the button the instant the click returns can therefore catch the pre-render
     * DOM and report a gate that is working perfectly as broken — the classic
     * intermittent failure that teaches a team to distrust its own suite. Polling
     * asks the honest question ("does it enable?") instead of the accidental one
     * ("had it enabled within a few milliseconds?").
     *
     * <p>
     * This still FAILS if the gate is genuinely broken: it returns false when the
     * timeout expires with the button never enabling.
     *
     * @return true if the submit button became enabled within the timeout.
     */
    public boolean waitUntilSubmitProfileEnabled() {
        try {
            return wait.until(d -> isSubmitProfileEnabled());
        } catch (Exception e) {
            log.warn("'Submit Profile' never became enabled within the timeout.");
            return false;
        }
    }
}
