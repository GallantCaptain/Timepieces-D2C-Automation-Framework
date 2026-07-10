package com.timepieces.automation.tools;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.timepieces.automation.pages.web.account.LoginPage;
import com.timepieces.automation.utils.BrowserUtils;
import com.timepieces.automation.utils.ConfigReader;

import lombok.extern.log4j.Log4j2;

/**
 * PdpLocatorScraper — a one-off diagnostic tool, NOT a regression test.
 * <p>
 * Every PDP (Product Detail Page) page object under
 * {@code pages/web/products/} exists only as an empty scaffold: the real
 * locators the live site uses (its {@code data-testid} attribute names) have
 * never been captured. This tool logs into production once (phone number +
 * a human-solved Cloudflare Turnstile + a manually-relayed SMS OTP — see the
 * OTP hand-off note on {@link #resolveOtp()}), then visits each configured
 * PDP handle and dumps every element carrying a {@code data-testid} (plus a
 * few structural heuristics: video/canvas/accordion/image-gallery/price) to
 * a human-readable markdown file under {@code target/pdp-scrape/}. Those
 * files are then read by hand to write the real locators + Business
 * Library / Private Helper / Verification methods on the actual PDP page
 * objects — this tool only gathers raw data, it does not generate code.
 * <p>
 * Currently scoped to the Astonia timepiece family (the pilot family for
 * this first pass). Extend {@link #PILOT_HANDLES} to cover more families in
 * follow-up passes.
 * <p>
 * Run with (production, generous CAPTCHA window since a human must solve
 * the Turnstile challenge live in the visible browser window):
 * <pre>
 * mvnw -o test "-Dsurefire.suite=testng-pdp-scraper.xml" "-Denv=production" "-Dcaptcha.wait.timeout=300"
 * </pre>
 * The OTP hand-off: once the OTP entry screen is visible, this tool polls
 * {@code target/pdp-scrape/otp-handoff.txt} every 3s for up to 10 minutes.
 * Write the 6-digit SMS code to that file (plain text, nothing else) to
 * unblock it. If no OTP arrives in time, the tool logs a warning and
 * continues scraping anonymously (most PDP content needs no login).
 *
 * @version 1.0
 * @author Avinash Rath
 * @since 2026-07-10
 */
@Log4j2
public class PdpLocatorScraper {

    private static final File SCRAPE_DIR = new File("target/pdp-scrape");
    private static final File OTP_HANDOFF_FILE = new File(SCRAPE_DIR, "otp-handoff.txt");

    // Astonia pilot family: handle -> ordered list of URL slug candidates to try.
    // astonia-bimetal-dusk-rose is the catalog handle (search_keywords.json); the
    // AstoniaDuskRose.java Javadoc guessed a different slug (astonia-dusk-rose)
    // before anyone had actually verified the live URL — try both.
    private static final Map<String, List<String>> PILOT_HANDLES = new LinkedHashMap<>();
    static {
        PILOT_HANDLES.put("astonia-stealth-silver", List.of("astonia-stealth-silver"));
        PILOT_HANDLES.put("astonia-dusk-gold", List.of("astonia-dusk-gold"));
        PILOT_HANDLES.put("astonia-bimetal-dusk-rose", List.of("astonia-bimetal-dusk-rose", "astonia-dusk-rose"));
        PILOT_HANDLES.put("astonia-trophy-gold", List.of("astonia-trophy-gold"));
    }

    private WebDriver driver;
    private String baseUrl;

    @BeforeClass
    public void setUp() {
        SCRAPE_DIR.mkdirs();

        // Builds its own ChromeDriver directly instead of going through
        // DriverFactory: the framework's default driver uses Selenium's "normal"
        // page load strategy, which waits for the browser's top-level `load` event.
        // A first live run against production hung for the full HTTP client timeout
        // (~3 min) on the very first driver.get("/login") — the page had already
        // rendered and was fully usable, but something (most likely Cloudflare's
        // background bot-management heartbeat) kept a network connection open so
        // the `load` event never fired. "eager" only waits for DOMContentLoaded
        // (interactive DOM), which matches what a human actually sees as "loaded"
        // and avoids this class of hang entirely.
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        driver = new ChromeDriver(options);

        baseUrl = ConfigReader.get("base.url");
        log.info("PdpLocatorScraper starting against base URL: {}", baseUrl);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(description = "Logs into production (human solves CAPTCHA, OTP relayed via file hand-off), then scrapes every configured PDP handle")
    public void loginThenScrapePilotHandles() throws Exception {
        attemptLogin();
        for (Map.Entry<String, List<String>> entry : PILOT_HANDLES.entrySet()) {
            scrapeHandle(entry.getKey(), entry.getValue());
        }
        log.info("PDP scrape complete. Files written under: {}", SCRAPE_DIR.getAbsolutePath());
    }

    // ==========================================
    // Login (human-in-the-loop CAPTCHA + OTP hand-off)
    // ==========================================
    private void attemptLogin() {
        String phone = System.getProperty("pdp.scraper.phone", "9000000001");
        String country = System.getProperty("pdp.scraper.country", "India");

        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.configureCountryCodeByName(country).enterPhoneNumber(phone);

        log.info("=== ACTION NEEDED === A visible Chrome window is now on the login page. "
                + "Solve the Cloudflare challenge if/when it appears; the Send OTP button will "
                + "become enabled once it's resolved (waiting up to captcha.wait.timeout seconds).");

        try {
            loginPage.clickSendOtpButton();
        } catch (Exception e) {
            log.warn("Send OTP did not complete (CAPTCHA not solved in time, or an OTP-send cooldown was "
                    + "active). Continuing WITHOUT login — most PDP content needs no account. Cause: {}",
                    e.getMessage());
            return;
        }

        String otp = resolveOtp();
        if (otp == null) {
            log.warn("No OTP received within the wait window. Continuing WITHOUT login.");
            return;
        }
        try {
            loginPage.enterOtp(otp).clickContinueButton();
            Thread.sleep(3000); // brief settle time for the post-login redirect
            log.info("OTP submitted; proceeding as an authenticated session (assuming the code was correct).");
        } catch (Exception e) {
            log.warn("OTP submission failed ({}). Continuing WITHOUT a confirmed login.", e.getMessage());
        }
    }

    /**
     * Waits for a human to relay the SMS OTP by writing it to
     * {@code target/pdp-scrape/otp-handoff.txt}. There is no existing
     * interactive-input mechanism in this framework (see the other OTP paths in
     * {@code LoginSteps} — a hardcoded feature-file string, or full Twilio-account
     * auto-fetch); a file poll is the simplest reliable hand-off across the gap
     * between "browser is waiting on the OTP screen" and "a human reads their own
     * phone and reports the code back."
     */
    private String resolveOtp() {
        if (OTP_HANDOFF_FILE.exists()) {
            OTP_HANDOFF_FILE.delete(); // clear any stale value from a previous run
        }
        log.info("=== WAITING FOR OTP === Write the 6-digit SMS code (plain text) to: {}",
                OTP_HANDOFF_FILE.getAbsolutePath());

        long deadline = System.currentTimeMillis() + Duration.ofMinutes(10).toMillis();
        while (System.currentTimeMillis() < deadline) {
            if (OTP_HANDOFF_FILE.exists()) {
                try {
                    String content = Files.readString(OTP_HANDOFF_FILE.toPath()).trim();
                    if (content.matches("\\d{6}")) {
                        log.info("OTP received.");
                        return content;
                    }
                    log.debug("Hand-off file exists but doesn't look like a 6-digit OTP yet: '{}'", content);
                } catch (Exception e) {
                    log.debug("Could not read hand-off file yet (may still be mid-write): {}", e.getMessage());
                }
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return null;
    }

    // ==========================================
    // Scraping
    // ==========================================
    private void scrapeHandle(String handle, List<String> slugCandidates) {
        for (String slug : slugCandidates) {
            String url = baseUrl + "/products/" + slug;
            log.info("Scraping handle '{}' — trying URL: {}", handle, url);
            BrowserUtils.navigateTo(driver, url, 15);
            sleepQuietly(1500); // small buffer for client-side hydration beyond readyState=complete

            if (looksLikeNotFound()) {
                log.warn("URL {} looks like a 404/not-found page; trying next candidate slug (if any).", url);
                continue;
            }

            writeScrapeReport(handle, url);
            return;
        }
        log.error("No working URL found for handle '{}' among candidates: {}", handle, slugCandidates);
        writeNotFoundReport(handle, slugCandidates);
    }

    private boolean looksLikeNotFound() {
        String title = safe(driver.getTitle()).toLowerCase();
        String bodyText = safe(getBodyTextSample()).toLowerCase();
        return title.contains("404") || title.contains("not found")
                || bodyText.contains("page not found") || bodyText.contains("404");
    }

    private String getBodyTextSample() {
        try {
            WebElement body = driver.findElement(By.tagName("body"));
            String text = body.getText();
            return text.length() > 500 ? text.substring(0, 500) : text;
        } catch (Exception e) {
            return "";
        }
    }

    private void writeNotFoundReport(String handle, List<String> candidates) {
        File file = new File(SCRAPE_DIR, handle + ".md");
        try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8)) {
            pw.println("# PDP Scrape: " + handle);
            pw.println();
            pw.println("**NO WORKING URL FOUND.** Tried candidate slugs: " + candidates);
            pw.println("Timestamp: " + Instant.now());
        } catch (Exception e) {
            log.error("Failed to write not-found report for {}", handle, e);
        }
    }

    private void writeScrapeReport(String handle, String url) {
        File file = new File(SCRAPE_DIR, handle + ".md");
        try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8)) {
            pw.println("# PDP Scrape: " + handle);
            pw.println();
            pw.println("URL: " + url);
            pw.println("Page title: " + safe(driver.getTitle()));
            pw.println("Timestamp: " + Instant.now());
            pw.println();

            pw.println("## Elements with [data-testid]");
            pw.println();
            List<WebElement> testIdElements = driver.findElements(By.cssSelector("[data-testid]"));
            if (testIdElements.isEmpty()) {
                pw.println("(none found)");
            }
            for (WebElement el : testIdElements) {
                pw.println(describeElement(el));
            }
            pw.println();

            pw.println("## <video> elements (autoplay 'loom' video candidates)");
            pw.println();
            writeSimpleList(pw, driver.findElements(By.tagName("video")));

            pw.println("## <canvas> elements (3D viewer candidates)");
            pw.println();
            writeSimpleList(pw, driver.findElements(By.tagName("canvas")));

            pw.println("## <details>/<summary> elements (accordion candidates)");
            pw.println();
            writeSimpleList(pw, driver.findElements(By.cssSelector("details, summary")));

            pw.println("## <img> elements (gallery/thumbnail heuristic)");
            pw.println();
            List<WebElement> images = driver.findElements(By.tagName("img"));
            pw.println("count=" + images.size());
            for (WebElement img : images) {
                pw.println("- src=" + truncate(safe(img.getAttribute("src")), 120)
                        + " alt=\"" + truncate(safe(img.getAttribute("alt")), 60) + "\"");
            }
            pw.println();

            pw.println("## Elements whose text looks like a price (currency symbol heuristic)");
            pw.println();
            List<WebElement> priceCandidates = driver.findElements(
                    By.xpath("//*[contains(text(),'₹') or contains(text(),'Rs.') or contains(text(),'$')]"));
            for (WebElement el : priceCandidates) {
                pw.println(describeElement(el));
            }
        } catch (Exception e) {
            log.error("Failed to write scrape report for {}", handle, e);
        }
        log.info("Wrote scrape report: {}", file.getAbsolutePath());
    }

    private void writeSimpleList(PrintWriter pw, List<WebElement> elements) {
        if (elements.isEmpty()) {
            pw.println("(none found)");
        }
        for (WebElement el : elements) {
            pw.println(describeElement(el));
        }
        pw.println();
    }

    private String describeElement(WebElement el) {
        String tag = safe(el.getTagName());
        String testId = safe(el.getAttribute("data-testid"));
        String ariaLabel = safe(el.getAttribute("aria-label"));
        String role = safe(el.getAttribute("role"));
        String type = safe(el.getAttribute("type"));
        String href = safe(el.getAttribute("href"));
        String text;
        try {
            text = truncate(el.getText().replace("\n", " ").trim(), 80);
        } catch (Exception e) {
            text = "";
        }
        StringBuilder sb = new StringBuilder("- <").append(tag).append(">");
        if (!testId.isEmpty())
            sb.append(" data-testid=\"").append(testId).append("\"");
        if (!ariaLabel.isEmpty())
            sb.append(" aria-label=\"").append(ariaLabel).append("\"");
        if (!role.isEmpty())
            sb.append(" role=\"").append(role).append("\"");
        if (!type.isEmpty())
            sb.append(" type=\"").append(type).append("\"");
        if (!href.isEmpty())
            sb.append(" href=\"").append(truncate(href, 80)).append("\"");
        if (!text.isEmpty())
            sb.append(" text=\"").append(text).append("\"");
        return sb.toString();
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String truncate(String value, int maxLen) {
        return value.length() > maxLen ? value.substring(0, maxLen) + "..." : value;
    }
}
