package com.timepieces.automation.tdd.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.pages.components.SiteOverlayHandler;
import com.timepieces.automation.utils.ConfigReader;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import lombok.extern.log4j.Log4j2;

/**
 * PRIVACY REPRODUCTION — ISSUE-019: the Become More onboarding restores a saved profile
 * draft into a BRAND-NEW anonymous session.
 *
 * <h2>Read this first (this file is written to be handed to a developer)</h2>
 * This test is deliberately <b>self-contained</b>: it declares its own locators and drives
 * the page directly rather than going through the framework's page objects, so it can be
 * read, understood and re-run by someone who has never seen this repo. That duplication is
 * intentional — a bug report you have to learn a framework to read is a bug report that
 * does not get fixed.
 *
 * <h2>What it claims</h2>
 * A visitor with a <b>fresh browser profile</b> (no cookies, empty storage) who enters a
 * <b>phone number and email that have never been used before</b> reaches the first profile
 * step and finds it <b>already filled in with somebody else's answers</b>.
 *
 * <h2>How it proves it, rather than asserting it</h2>
 * <ol>
 *   <li><b>Fresh session.</b> Each run gets a brand-new Chrome profile. The probe dumps
 *       {@code localStorage} and {@code sessionStorage} before it starts. They are not
 *       literally empty — the site writes analytics, consent and currency keys on first
 *       paint — but they contain <b>no name, salutation, date of birth or persona</b>. The
 *       claim is deliberately the narrower, checkable one; overstating it as "storage is
 *       empty" would be the first thing a developer disproved.</li>
 *   <li><b>Never-used identity.</b> The phone/email are generated per-run from the clock,
 *       so no prior submission can be associated with them. If the draft still comes back,
 *       it is <b>not keyed to the person</b>.</li>
 *   <li><b>Storage is dumped again afterwards.</b> If the restored values are NOT in
 *       {@code localStorage} / {@code sessionStorage}, the data did not come from this
 *       browser — it came from the <b>server</b>. That is the whole finding.</li>
 * </ol>
 *
 * <h2>Expected result</h2>
 * This test <b>FAILS while the bug exists</b>. That is the point: it asserts the CORRECT
 * behaviour (a brand-new visitor's form is empty), so it reproduces today and becomes the
 * regression test the day it is fixed. Do not "fix" it by seeding the form.
 *
 * <h2>Handling of leaked data — please keep this</h2>
 * If the restored value is one of the automation suite's own known seeds ("Test User" etc.)
 * it is printed, because it is ours. <b>Anything else is REDACTED</b> to a length and
 * character-shape, and flagged loudly. The reason is uncomfortable but simple: if this bug
 * can serve a real customer's details to a stranger, then a test that dumps those details
 * into a CI report, an emailed artifact and a screenshot has just made the leak worse. A
 * privacy probe that leaks is not a privacy probe.
 *
 * <h2>Run it on its own</h2>
 * <pre>
 *   ./mvnw test "-Dsurefire.suite=testng-privacy-probe.xml" "-Denv=production"
 * </pre>
 * It is opt-in (group {@code privacy-probe}) and is NOT part of any default suite, in the
 * same spirit as the {@code @OtpBruteForce} probe. It never clicks "Submit Profile", so no
 * profile is created.
 *
 * @since 2026-07-17
 * @version 1.0
 * @author Avinash Rath
 */
@Log4j2
public class ProfileDraftRestorePrivacyProbeTest {

    /** Values the automation suite itself is known to submit. Anything else is a stranger's. */
    private static final List<String> KNOWN_SUITE_SEEDS =
            List.of("Test User", "Mr", "01/01/1990", "Professional", "Watches");

    // --- Locators. Duplicated from the page object ON PURPOSE; see the class javadoc. ---
    private static final By PHONE = By.cssSelector("input[type='tel']");
    private static final By EMAIL = By.cssSelector("input[type='email']");
    private static final By CONTINUE = By.xpath("//button[contains(normalize-space(),'Continue')]");
    private static final By STEP_INDICATOR = By.xpath("//span[starts-with(normalize-space(),'Step 0')]");
    private static final By FULL_NAME = By.cssSelector("textarea[placeholder='Enter your full name']");
    private static final By DOB = By.cssSelector("textarea[placeholder='DD/MM/YYYY']");
    private static final By SELECTS = By.tagName("select");

    private WebDriver driver;
    private JavascriptExecutor js;
    private WebDriverWait wait;
    private final StringBuilder evidence = new StringBuilder();

    private String uniquePhone;
    private String uniqueEmail;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.initDriver();
        js = (JavascriptExecutor) driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // A never-before-used identity, derived from the clock so it cannot collide with a
        // previous run and cannot already have a draft associated with it.
        long stamp = System.currentTimeMillis();
        uniquePhone = "98" + String.valueOf(stamp).substring(5); // 10 digits, never used
        uniqueEmail = "privacy.probe." + stamp + "@example.invalid"; // .invalid can never receive mail
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        publishEvidence();
        DriverFactory.removeDriver();
    }

    @Test(groups = { "privacy-probe", "security" })
    @Severity(SeverityLevel.CRITICAL)
    @Description("ISSUE-019 reproduction: proves the Become More journey restores a saved profile "
            + "draft into a brand-new anonymous session with a never-used phone/email. FAILS while "
            + "the bug exists; becomes the regression test once it is fixed.")
    public void aBrandNewVisitorMustNotSeeSomebodyElsesProfileDraft() {
        String base = ConfigReader.get("base.url");

        // ---------- STEP 1: a genuinely fresh session ----------
        driver.get(base + "/becomemore/profile?section=contact");
        SiteOverlayHandler.dismissBlockingOverlays(driver);
        wait.until(ExpectedConditions.visibilityOfElementLocated(PHONE));

        Map<String, String> storageBefore = dumpStorage();
        line("=".repeat(78));
        line("ISSUE-019 REPRODUCTION — profile draft restored into a new anonymous session");
        line("=".repeat(78));
        line("target        : " + base + "/becomemore/profile?section=contact");
        line("browser       : brand-new Chrome profile (fresh user-data-dir, no cookies)");
        line("");
        line("--- STEP 1: what the client is holding BEFORE we start ---");
        line("Read what is in here: analytics, consent and currency keys. Note what is NOT:");
        line("any name, salutation, date of birth or persona. Step 4 re-reads this and");
        line("checks whether the values the form arrives with are anywhere in it.");
        line("localStorage  : " + storageBefore.get("local"));
        line("sessionStorage: " + storageBefore.get("session"));
        line("");

        // ---------- STEP 2: identify as somebody who has never existed ----------
        line("--- STEP 2: identify as a never-before-seen visitor ---");
        line("phone used    : " + uniquePhone + "   (generated from the clock this run)");
        line("email used    : " + uniqueEmail + "   (.invalid — cannot ever have been used)");
        line("");

        // Navigation goes through the framework's page object rather than raw clicks. That
        // is a deliberate exception to this file being self-contained, and it is worth
        // knowing why: the consent bar re-renders while React hydrates and can land ON TOP
        // of Continue (whose click point sits inside the bar's band), so a naive
        // dismiss-then-click races it and the probe fails for a reason that has nothing to
        // do with the bug. openSection() sweeps until the site records the choice in its
        // timepieces_cookie_consent cookie. In plain terms it does three things:
        //     openSection("contact")  ->  fillContactDetails(phone, email)  ->  clickContactContinue()
        // The UNIQUE phone/email are ours, not the page object's fixed pair — that is the
        // whole experiment: if the draft still comes back for an identity that has never
        // existed, it is not keyed to the person.
        com.timepieces.automation.pages.web.account.Onboarding_BecomeMore journey =
                new com.timepieces.automation.pages.web.account.Onboarding_BecomeMore(driver);
        journey.openSection(com.timepieces.automation.pages.web.account.Onboarding_BecomeMore.SECTION_CONTACT)
                .fillContactDetails(uniquePhone, uniqueEmail)
                .clickContactContinue();

        // Accept EITHER signal that we advanced: the step indicator or the name field.
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(STEP_INDICATOR),
                ExpectedConditions.visibilityOfElementLocated(FULL_NAME)));

        // ---------- STEP 3: read what the server handed back ----------
        String fullName = valueOf(FULL_NAME);
        String dob = valueOf(DOB);
        List<WebElement> selects = driver.findElements(SELECTS);
        String salutation = selects.size() > 0 ? selectedText(selects.get(0)) : "";
        String persona = selects.size() > 1 ? selectedText(selects.get(1)) : "";

        Map<String, String> storageAfter = dumpStorage();

        line("--- STEP 3: what the FIRST profile step arrived holding ---");
        line("step          : " + text(STEP_INDICATOR));
        line("");
        line(String.format("  %-12s expected=%-8s actual=%s", "full name", "(empty)", present(fullName)));
        line(String.format("  %-12s expected=%-8s actual=%s", "salutation", "(empty)", present(salutation)));
        line(String.format("  %-12s expected=%-8s actual=%s", "date of birth", "(empty)", present(dob)));
        line(String.format("  %-12s expected=%-8s actual=%s", "persona", "(empty)", present(persona)));
        line("");

        // ---------- STEP 4: prove it did NOT come from this browser ----------
        line("--- STEP 4: prove the data is SERVER-side, not client-side ---");
        line("localStorage  : " + storageAfter.get("local"));
        line("sessionStorage: " + storageAfter.get("session"));
        boolean inClientStorage = containsAny(storageAfter.get("local") + storageAfter.get("session"),
                fullName, dob, salutation, persona);
        line("restored values found in client storage? " + (inClientStorage ? "YES" : "NO"));
        if (!inClientStorage) {
            line("  -> The browser never held these values. They were sent by the SERVER,");
            line("     to a session that supplied a phone/email never used before.");
        }
        line("");

        // ---------- STEP 5: a lead for whoever fixes this ----------
        // Handing over a symptom is fine; handing over a suspect is better.
        String visitorId = storageValue("ds_visitor_id");
        line("--- STEP 5: a lead — what COULD the draft be keyed to? ---");
        line("ds_visitor_id : " + visitorId);
        line("");
        line("This value was observed to be IDENTICAL across separate runs in BRAND-NEW");
        line("Chrome profiles (e.g. 5520b8f06dc744458497225e7c1ee77d, seen again on later");
        line("runs), while ds_session_id changed every time. A visitor id that survives a");
        line("fresh browser profile is not coming from the browser — it is being derived,");
        line("most likely from IP address and/or a device fingerprint.");
        line("");
        line("That makes it the prime suspect for the key the draft is restored against,");
        line("and it would explain every observation here: the draft follows the MACHINE,");
        line("not the person, which is why a never-used phone/email still receives it.");
        line("");
        line("If that is what is happening, the consequences are worth thinking through:");
        line("  * anyone sharing an IP — an office, a household, a cafe, CGNAT on mobile —");
        line("    could be served each other's in-progress profile details;");
        line("  * the data is served BEFORE any verification of the phone number, so there");
        line("    is no authentication boundary in front of it at all.");
        line("Please treat the mechanism as unconfirmed until you check it server-side —");
        line("this is a lead from the client, not a diagnosis.");
        line("");

        attachScreenshot();

        // ---------- The assertion: correct behaviour, so it fails while the bug lives ----------
        List<String> populated = new java.util.ArrayList<>();
        if (!fullName.isBlank()) {
            populated.add("full name");
        }
        if (!dob.isBlank()) {
            populated.add("date of birth");
        }
        if (isMeaningful(salutation)) {
            populated.add("salutation");
        }
        if (isMeaningful(persona)) {
            populated.add("persona");
        }

        line("--- VERDICT ---");
        if (populated.isEmpty()) {
            line("NOT REPRODUCED on this run: the form arrived empty, as it should.");
            line("NOTE: this bug is stateful — an earlier run may have seeded the draft, and a");
            line("      deploy or cache flush can clear it. A clean run is NOT proof of a fix;");
            line("      re-run a few times before concluding anything.");
        } else {
            line("REPRODUCED. Pre-filled fields: " + String.join(", ", populated));
            line("A brand-new anonymous visitor was served profile answers it never entered.");
        }

        assertThat(populated)
                .as("PRIVACY (ISSUE-019): a brand-new anonymous visitor, identifying with a phone/email "
                        + "never used before, must NOT be served an existing profile draft. Pre-filled "
                        + "fields found: %s. See the attached evidence report.", populated)
                .isEmpty();
    }

    // ---------------------------------------------------------------------------
    // Evidence helpers
    // ---------------------------------------------------------------------------

    /**
     * Renders a restored value for the report, redacting anything that is not demonstrably
     * ours.
     *
     * <p>See the class javadoc: if this bug can serve a real customer's details to a
     * stranger, a probe that prints them into a CI report has made the leak worse.
     */
    private String present(String value) {
        if (value == null || value.isBlank()) {
            return "(empty)  <- correct";
        }
        if (KNOWN_SUITE_SEEDS.stream().anyMatch(seed -> seed.equalsIgnoreCase(value.trim()))) {
            return "\"" + value + "\"  <- LEAKED (this one is the automation suite's own seed)";
        }
        return "[REDACTED: " + value.trim().length() + " chars, shape=" + shapeOf(value.trim()) + "]"
                + "  <- LEAKED, AND NOT OURS. Possibly a real person's data — value withheld"
                + " deliberately; reproduce locally to inspect.";
    }

    /** Character-class silhouette (e.g. "Test User" -> "Aaaa Aaaa"), so we can describe without disclosing. */
    private static String shapeOf(String value) {
        return value.replaceAll("[A-Z]", "A").replaceAll("[a-z]", "a").replaceAll("[0-9]", "9");
    }

    /*
     * A "does the client hold a profile draft?" verdict used to live here, implemented as a
     * substring scan for tokens like "persona". It was REMOVED because it reported a false
     * positive: localStorage contains "ad_personalization" (a consent key), which contains
     * "persona". A privacy report that overstates its evidence is worse than one that says
     * less — the first thing a developer will do is check the claim, and the first false
     * one costs the whole report its credibility.
     *
     * Step 4 makes the narrower, checkable claim instead: are the ACTUAL restored values
     * present anywhere in client storage? That is the question that distinguishes
     * server-side restore from client-side persistence, and it needs no guessing about key
     * names.
     */

    /** Reads a single localStorage key, for the ds_visitor_id lead in step 5. */
    private String storageValue(String key) {
        try {
            Object raw = js.executeScript("return window.localStorage.getItem(arguments[0]);", key);
            return raw == null ? "(not present)" : String.valueOf(raw);
        } catch (Exception e) {
            return "(unreadable)";
        }
    }

    /** A <select> reports its placeholder as selected; treat placeholder-ish text as empty. */
    private static boolean isMeaningful(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String v = value.trim().toLowerCase();
        return !(v.startsWith("select") || v.equals("-") || v.equals("--"));
    }

    private String valueOf(By locator) {
        try {
            String v = driver.findElement(locator).getAttribute("value");
            return v == null ? "" : v.trim();
        } catch (Exception e) {
            return "";
        }
    }

    private String selectedText(WebElement select) {
        try {
            return new org.openqa.selenium.support.ui.Select(select).getFirstSelectedOption().getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private String text(By locator) {
        try {
            return driver.findElement(locator).getText().trim();
        } catch (Exception e) {
            return "(not found)";
        }
    }

    private Map<String, String> dumpStorage() {
        Map<String, String> out = new LinkedHashMap<>();
        out.put("local", readStore("localStorage"));
        out.put("session", readStore("sessionStorage"));
        return out;
    }

    private String readStore(String store) {
        try {
            Object raw = js.executeScript(
                    "var s=window." + store + "; var o={};"
                            + "for(var i=0;i<s.length;i++){var k=s.key(i);o[k]=String(s.getItem(k)).slice(0,120);}"
                            + "return JSON.stringify(o);");
            String json = String.valueOf(raw);
            return "{}".equals(json) ? "{}  (EMPTY)" : json;
        } catch (Exception e) {
            return "(unreadable: " + e.getClass().getSimpleName() + ")";
        }
    }

    private static boolean containsAny(String haystack, String... needles) {
        if (haystack == null) {
            return false;
        }
        for (String n : needles) {
            if (n != null && !n.isBlank() && haystack.contains(n)) {
                return true;
            }
        }
        return false;
    }

    private void line(String text) {
        evidence.append(text).append('\n');
    }

    private void attachScreenshot() {
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("ISSUE-019 — the first profile step as a new visitor sees it",
                    "image/png", new ByteArrayInputStream(png), ".png");
        } catch (Exception e) {
            log.debug("Could not attach screenshot: {}", e.getMessage());
        }
    }

    /** Writes the report to Allure and to target/privacy/ so it can be emailed to a developer. */
    private void publishEvidence() {
        String report = evidence.toString();
        if (report.isBlank()) {
            return;
        }
        log.info("\n{}", report);
        try {
            Allure.addAttachment("ISSUE-019 evidence report", "text/plain", report, ".txt");
            Path dir = Path.of("target", "privacy");
            Files.createDirectories(dir);
            Files.writeString(dir.resolve("ISSUE-019-draft-restore.txt"), report, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Could not publish the evidence report: {}", e.getMessage());
        }
    }
}
