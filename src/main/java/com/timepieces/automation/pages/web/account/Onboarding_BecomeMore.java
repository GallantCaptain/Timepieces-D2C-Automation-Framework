package com.timepieces.automation.pages.web.account;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import com.timepieces.automation.pages.HeaderOnlyBasePage;

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
 * SCRAPE NOTE (2026-07-16): the flow is PUBLIC (reachable logged out — no login
 * gate). Shape: an intro screen ("Become More" + "Begin your Profile" +
 * "Takes less than 60 seconds") opens a method-selector dialog ("How do you
 * like to fill your profile?" — LinkedIn import / "Start Manually" / Close),
 * then 8 sections driven by the URL query param
 * {@code /becomemore/profile?section=<key>}:
 * <ol>
 * <li>{@code name} — "Step 01 of 07": full name + salutation select + DOB +
 * persona select; Next ({@code button[type='submit']}) DISABLED until all
 * validate.</li>
 * <li>{@code location} — city + use-location button.</li>
 * <li>{@code about} — bio textarea + voice input + "Refine my bio" (AI) +
 * "See examples".</li>
 * <li>{@code building} — goals textarea + voice + "Refine my story" +
 * examples.</li>
 * <li>{@code watchjourney} — SINGLE-select chips that AUTO-ADVANCE on click
 * (active = border-black).</li>
 * <li>{@code interest} — MULTI-select chips (selected = dark bg); Next enables
 * after ≥1 selection.</li>
 * <li>{@code timepieceshouse} — optional inspiration textarea + "Need
 * inspiration?" (renders "Curated inspirations") + skip ("I'll do this
 * later"); Next enabled even when empty.</li>
 * <li>{@code photo} — final step (no step indicator): avatar upload (hidden
 * {@code input[type=file]}) + consent checkbox that GATES the "Submit Profile"
 * button (disabled attribute removed on tick).</li>
 * </ol>
 * ZERO page-specific {@code data-testid}s exist on the flow (only the shared
 * header) — every locator is a fallback tagged {@code // ROLLOUT-GAP}.
 *
 * <p>
 * SUBMISSION WARNING: {@link #clickSubmitProfile()} creates a REAL Timepieces
 * profile. Automated tests should exercise field entry, chip selection and the
 * consent/validation gates only, and must NOT submit against production.
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

    // The 8 section keys, in journey order.
    public static final String SECTION_NAME = "name";
    public static final String SECTION_LOCATION = "location";
    public static final String SECTION_ABOUT = "about";
    public static final String SECTION_BUILDING = "building";
    public static final String SECTION_WATCH_JOURNEY = "watchjourney";
    public static final String SECTION_INTEREST = "interest";
    public static final String SECTION_TIMEPIECES_HOUSE = "timepieceshouse";
    public static final String SECTION_PHOTO = "photo";

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
    private final By methodCloseButton = By.xpath("//button[normalize-space()='Close']"); // ROLLOUT-GAP: text-anchor

    // --- Shared step chrome ---
    private final By stepIndicator = By.xpath("//main//*[contains(normalize-space(),'Step 0')]"); // ROLLOUT-GAP: text-anchor
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
     * @param sectionKey the section query value (e.g. {@code SECTION_NAME}).
     * @return this page instance for method chaining.
     */
    public Onboarding_BecomeMore openSection(String sectionKey) {
        String url = getBaseUrl() + SECTION_PATH_PREFIX + sectionKey;
        log.info("Opening Become More section: [{}]", url);
        driver.get(url);
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
     * V.9 Reports whether "Submit Profile" is enabled (it stays DISABLED until
     * the consent checkbox is ticked).
     *
     * @return true if the submit button is enabled.
     */
    public boolean isSubmitProfileEnabled() {
        try {
            return getElementPresence(submitProfileButton).isEnabled();
        } catch (Exception e) {
            log.warn("Could not evaluate the Submit Profile state.", e);
            return false;
        }
    }
}
