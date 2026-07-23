package com.timepieces.automation.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

import com.timepieces.automation.utils.ConfigReader;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.util.HashMap;
import java.util.Map;

// HEALENIUM: SelfHealingDriver is the healenium-web entry point. It wraps a normal
// Selenium WebDriver in a decorator that intercepts every By-based lookup
// (driver.findElement/findElements, and therefore every WebDriverWait/ExpectedConditions
// call too, since they all resolve through the same driver instance). On a
// NoSuchElementException it asks the hlm-backend service for the closest healed locator
// learned from prior successful runs, retries with it, and reports back so future breaks
// keep healing. See com.epam.healenium:healenium-web in pom.xml.
import com.epam.healenium.SelfHealingDriver;

import lombok.extern.log4j.Log4j2;

/**
 * DriverFactory
 * 
 * @since 2026-07-03
 * @version 1.0
 * @author Avinash Rath
 */

@Log4j2
public class DriverFactory {

  private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

  /**
   * ChromeOptions for Chrome & Brave: Utilized --headless=new instead of the
   * legacy --headless,
   * which is the modern standard for Chromium-based browsers starting from
   * version 109 to ensure it
   * renders pages identically to the headed version.
   *
   * <p>
   * DYNAMIC SAFARI BYPASS: SafariDriver only exists on macOS. When
   * {@code browser=safari} runs
   * on macOS it is used as-is; on Linux (Docker/CI) and Windows it is dynamically
   * substituted with
   * a Chromium browser ({@code safari.fallback.browser}, default chrome) so the
   * same suite runs on
   * every platform. The substitution is NOT real Safari — it is logged and
   * annotated in the Allure
   * environment ({@code Browser.SAFARI = SUBSTITUTED ...}) so reports never imply
   * a real Safari run.
   */
  public static WebDriver initDriver() {
    String browser = ConfigReader.get("browser");
    boolean isHeadless = Boolean.parseBoolean(ConfigReader.get("headless"));
    boolean disableChromeSandbox = Boolean.parseBoolean(ConfigReader.get("chrome.no.sandbox"));
    writeAllureEnvironmentInfo(browser, System.getProperty("env", "staging"));

    String executionMode = System.getProperty("execution.mode");
    if (executionMode == null) {
      executionMode = ConfigReader.get("execution.mode");
    }
    if (executionMode == null) {
      executionMode = "local";
    }

    if (executionMode.equalsIgnoreCase("CLOUD")) {
      String gridHubUrl = System.getProperty("cloud.grid.hub.url");
      if (gridHubUrl == null) {
        gridHubUrl = System.getenv("CLOUD_GRID_HUB_URL");
      }
      if (gridHubUrl == null) {
        gridHubUrl = ConfigReader.get("cloud.grid.hub.url");
      }
      if (gridHubUrl == null || gridHubUrl.isBlank()) {
        gridHubUrl = "https://hub.lambdatest.com/wd/hub";
      }

      String user = System.getProperty("cloud.grid.username");
      if (user == null) {
        user = System.getenv("CLOUD_GRID_USERNAME");
      }
      if (user == null) {
        user = ConfigReader.get("cloud.grid.username");
      }

      String key = System.getProperty("cloud.grid.access.key");
      if (key == null) {
        key = System.getenv("CLOUD_GRID_ACCESS_KEY");
      }
      if (key == null) {
        key = ConfigReader.get("cloud.grid.access.key");
      }

      try {
        org.openqa.selenium.chrome.ChromeOptions browserOptions = new org.openqa.selenium.chrome.ChromeOptions();
        browserOptions.setPlatformName("Windows 11");
        browserOptions.setBrowserVersion("latest");
        Map<String, Object> cloudOptions = new HashMap<>();
        cloudOptions.put("build", "Timepieces Scaling Suite");
        cloudOptions.put("name", "E2E Checkout Run POC");
        cloudOptions.put("user", user);
        cloudOptions.put("accessKey", key);
        browserOptions.setCapability("LT:Options", cloudOptions);

        log.info("Initializing RemoteWebDriver (CLOUD mode) against: {}", gridHubUrl);
        WebDriver driver = new RemoteWebDriver(new java.net.URL(gridHubUrl), browserOptions);
        // Apply the Cloudflare CDP header BEFORE wrapping with Healenium: the cast to
        // ChromiumDriver below only works against the raw vendor driver, not the
        // SelfHealingDriver decorator produced by wrapWithHealenium(...).
        applyCloudflareBypassHeader(driver);
        driver = wrapWithHealenium(driver);
        tlDriver.set(driver);
        return getDriver();
      } catch (Exception e) {
        log.error("Failed to initialize RemoteWebDriver for CLOUD mode: {}", e.getMessage(), e);
        throw new RuntimeException("CLOUD mode driver initialization failure", e);
      }
    }

    WebDriver driver = switch (browser.toLowerCase()) {
      case "firefox" -> {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        // Removes navigator.webdriver=true — masks the most obvious webdriver
        // fingerprint
        // from bot detectors like Cloudflare Turnstile. Same intent as the Chrome
        // flags.
        firefoxOptions.addPreference("dom.webdriver.enabled", false);
        firefoxOptions.addPreference("useAutomationExtension", false);
        if (isHeadless) {
          firefoxOptions.addArguments("--headless");
        }
        log.info("Initialized FirefoxDriver with headless={}", isHeadless);
        yield new FirefoxDriver(firefoxOptions);
      }
      case "chrome" -> newChromiumDriver("chrome", isHeadless, disableChromeSandbox);

      case "edge" -> newChromiumDriver("edge", isHeadless, disableChromeSandbox);

      case "brave" -> newChromiumDriver("brave", isHeadless, disableChromeSandbox);

      case "safari" -> {
        // Real SafariDriver exists ONLY on macOS. On a Mac, use it directly.
        if (isMacOs()) {
          if (isHeadless) {
            log.warn("Safari does not support headless mode; running headed on macOS.");
          }
          log.info("Initialized SafariDriver (macOS).");
          yield new SafariDriver();
        }
        // DYNAMIC SAFARI BYPASS — Linux (Docker/CI) and Windows have no
        // Selenium-drivable
        // Safari/WebKit, so substitute a Chromium browser so the scenario still
        // EXECUTES on
        // every platform. This is NOT real Safari: WebKit-specific behaviour/bugs are
        // not
        // covered here. The substitution is logged and annotated in the Allure
        // environment so
        // reports never imply a real Safari run.
        String fallback = resolveSafariFallbackBrowser();
        String os = System.getProperty("os.name");
        log.warn("Safari is macOS-only and this host is [{}]. Falling back to '{}' "
            + "(Chromium substitute — NOT real Safari).", os, fallback);
        annotateSafariSubstitution(os, fallback);
        yield newChromiumDriver(fallback, isHeadless, disableChromeSandbox);
      }

      default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
    };
    // Same ordering as the CLOUD branch above: apply the raw-driver CDP header first,
    // then wrap with Healenium last so every downstream page object gets self-healing.
    applyCloudflareBypassHeader(driver);
    driver = wrapWithHealenium(driver);
    tlDriver.set(driver);
    return getDriver();
  }

  /**
   * HEALENIUM INTEGRATION POINT.
   *
   * <p>
   * This is the single place the whole framework wraps WebDriver with Healenium's
   * self-healing decorator. It is safe to call unconditionally because every page
   * object, component, and step definition obtains its driver exclusively through
   * {@link #getDriver()} (see {@code BasePage}, {@code Hooks}, and the step
   * definition classes) — so wrapping here once makes every {@code getElement},
   * {@code WebDriverWait}, and {@code ExpectedConditions} call in the framework
   * self-healing for free, with no changes needed in page objects.
   *
   * <p>
   * Gated behind the {@code healenium.enabled} config key (default {@code false}
   * — see {@code staging.properties} / {@code production.properties}) so
   * environments that have not started the Healenium backend stack (see
   * {@code docker-compose.yml}: {@code healenium-postgres} + {@code hlm-backend})
   * — such as plain CI runs — are completely unaffected and keep using the raw
   * WebDriver. When disabled, {@code SelfHealingDriver.create(...)} is never
   * called at all, so there is zero dependency on the backend being reachable.
   *
   * <p>
   * When Healenium itself throws during wrapping (e.g. it cannot parse
   * {@code healenium.properties}), the failure is logged and the raw driver is
   * returned instead of failing the whole test run — self-healing is a
   * resilience feature and must never be the reason a suite cannot start.
   *
   * @param driver the fully-configured vendor WebDriver (Chrome/Firefox/Edge/etc.),
   *               with any Cloudflare CDP header already applied
   * @return a Healenium {@code SelfHealingDriver} wrapping {@code driver} when
   *         {@code healenium.enabled=true}, otherwise the original {@code driver}
   *         unchanged
   */
  private static WebDriver wrapWithHealenium(WebDriver driver) {
    boolean healeniumEnabled = Boolean.parseBoolean(ConfigReader.get("healenium.enabled"));
    if (!healeniumEnabled) {
      log.debug("healenium.enabled=false; using the raw WebDriver without self-healing.");
      return driver;
    }
    try {
      // SelfHealingDriver.create(...) reads src/test/resources/healenium.properties
      // from the classpath for the backend URL and healing tuning (recovery-tries,
      // score-cap, etc.) — it does NOT go through ConfigReader, so that file must be
      // kept in sync with healenium.server.url in the environment .properties files.
      WebDriver selfHealingDriver = SelfHealingDriver.create(driver);
      log.info("Healenium self-healing is ENABLED; WebDriver wrapped with SelfHealingDriver.");
      return selfHealingDriver;
    } catch (Exception e) {
      // Fail-open: a mis-configured or unreachable Healenium backend must never
      // block test execution. Log loudly and fall back to the raw driver.
      log.error("Failed to initialize Healenium SelfHealingDriver; continuing with the raw WebDriver. {}",
          e.getMessage(), e);
      return driver;
    }
  }

  private static void applyCloudflareBypassHeader(WebDriver driver) {
    String token = System.getenv("CF_BYPASS_TOKEN");
    if (token == null || token.isBlank()) {
      token = System.getProperty("CF_BYPASS_TOKEN");
    }
    if (token == null || token.isBlank()) {
      token = ConfigReader.get("cf.bypass.token");
    }
    if (token == null || token.isBlank()) {
      log.debug(
          "No CF_BYPASS_TOKEN environment variable, system property, or config property set. Cloudflare bypass header will not be applied.");
      return;
    }

    if (driver instanceof ChromiumDriver chromiumDriver) {
      log.info("Applying Cloudflare bypass header x-automation-bypass to Chromium session.");
      try {
        // CRITICAL: Network.setExtraHTTPHeaders is silently ignored unless the Network
        // domain
        // is enabled on the SAME CDP session first. Without this enable call the
        // command still
        // returns OK (so it looks applied) but the header is never attached to any
        // request — so
        // the Cloudflare skip rule never matches and CI keeps getting challenged.
        // Enable first.
        chromiumDriver.executeCdpCommand("Network.enable", new HashMap<>());

        Map<String, Object> headers = new HashMap<>();
        headers.put("x-automation-bypass", token);
        Map<String, Object> params = new HashMap<>();
        params.put("headers", headers);
        chromiumDriver.executeCdpCommand("Network.setExtraHTTPHeaders", params);
        log.info("Cloudflare bypass header applied (Network domain enabled, header attached to all requests).");
      } catch (Exception e) {
        log.error("Failed to apply Cloudflare bypass header via CDP: {}", e.getMessage(), e);
      }
    } else {
      log.debug("Driver is not an instance of ChromiumDriver; skipping CDP Cloudflare bypass header.");
    }
  }

  // Shared by Chrome, Edge, and Brave — all of which extend ChromiumOptions.
  // --disable-dev-shm-usage avoids the tiny default /dev/shm crashing Chromium in
  // Docker.
  // --no-sandbox is required when the browser runs as root inside a container;
  // opt-in via
  // chrome.no.sandbox so it doesn't silently turn off the sandbox on developer
  // machines.
  // The AutomationControlled / excludeSwitches / useAutomationExtension trio
  // masks the
  // most obvious webdriver fingerprints so Cloudflare Turnstile's bot score
  // drops.
  // Not a CAPTCHA bypass — a hostile challenge still blocks — but it gives
  // managed
  // challenges a fighting chance of auto-passing.
  private static void applyChromiumStabilityFlags(ChromiumOptions<?> options, boolean disableSandbox) {
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--window-size=1920,1080");
    options.addArguments("--disable-blink-features=AutomationControlled");
    options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
    options.setExperimentalOption("useAutomationExtension", false);
    applyPasswordManagerSuppression(options);
    if (disableSandbox) {
      options.addArguments("--no-sandbox");
    }
  }

  /**
   * Stops Chromium popping its "Save password?" / "Change your password" bubbles during a
   * run.
   *
   * <p>WHY THIS MATTERS FOR TESTS: any scenario that types into a password field (login,
   * OTP, checkout) makes Chrome offer to save the credential. That bubble is browser
   * chrome painted OVER the page near the omnibox — Selenium cannot see or dismiss it via
   * the DOM, and it steals focus and can swallow the next click. The result is a flaky
   * failure that looks like an app bug but is purely the browser being helpful.
   *
   * <p>Belt AND braces on purpose, because these knobs have moved between Chrome versions:
   * <ul>
   *   <li>the {@code prefs} switch off the password manager and the "leak detection"
   *       dialog ("Change your password" after a breach match) at the profile level;</li>
   *   <li>the {@code --disable-features} flags cover the same behaviour where it has been
   *       re-homed behind a feature flag;</li>
   *   <li>{@code --disable-save-password-bubble} kills the bubble itself.</li>
   * </ul>
   * Unknown feature names are ignored by Chromium rather than fatal, so listing extras is
   * safe and keeps this working across the version upgrades that inevitably follow.
   */
  private static void applyPasswordManagerSuppression(ChromiumOptions<?> options) {
    Map<String, Object> prefs = new HashMap<>();
    // The classic pair: no "save password?" offer, no password manager UI at all.
    prefs.put("credentials_enable_service", false);
    prefs.put("profile.password_manager_enabled", false);
    // Chrome 116+: the "your password was found in a data breach" interstitial.
    prefs.put("profile.password_manager_leak_detection", false);
    // Autofill's own address/card prompts can steal focus on the checkout forms too.
    prefs.put("autofill.profile_enabled", false);
    prefs.put("autofill.credit_card_enabled", false);
    options.setExperimentalOption("prefs", prefs);

    options.addArguments("--disable-save-password-bubble");
    options.addArguments("--disable-features=PasswordLeakDetection,PasswordChangeSupport,"
        + "AutofillEnableAccountWalletStorage,PasswordManagerOnboarding,SafeBrowsingEnhancedProtection");
    // Without this, a fresh profile shows the first-run/"make Chrome default" surfaces,
    // which are the same class of focus-stealing nuisance.
    options.addArguments("--no-first-run");
    options.addArguments("--no-default-browser-check");
    options.addArguments("--password-store=basic");
  }

  /**
   * Builds a Chromium-family WebDriver (Chrome, Edge, or Brave). Shared by the
   * direct browser
   * cases AND the dynamic Safari fallback, so headless handling and the
   * stability/anti-bot flags
   * live in exactly one place. Any unrecognised value defaults to Chrome.
   */
  private static WebDriver newChromiumDriver(String chromiumBrowser, boolean isHeadless, boolean disableSandbox) {
    switch (chromiumBrowser) {
      case "edge" -> {
        EdgeOptions edgeOptions = new EdgeOptions();
        applyChromiumStabilityFlags(edgeOptions, disableSandbox);
        if (isHeadless) {
          edgeOptions.addArguments("--headless=new");
        }
        log.info("Initialized EdgeDriver with headless={}", isHeadless);
        return new EdgeDriver(edgeOptions);
      }
      case "brave" -> {
        ChromeOptions braveOptions = new ChromeOptions();
        String bravePath = System.getenv("BRAVE_PATH");
        if (bravePath == null || bravePath.isEmpty()) {
          bravePath = ConfigReader.get("brave.path");
        }
        if (bravePath == null || bravePath.isEmpty()) {
          throw new IllegalStateException(
              "Brave path is not set: set the BRAVE_PATH environment variable or brave.path in config.");
        }
        braveOptions.setBinary(bravePath);
        applyChromiumStabilityFlags(braveOptions, disableSandbox);
        if (isHeadless) {
          braveOptions.addArguments("--headless=new");
        }
        log.info("Initialized BraveDriver with headless={}", isHeadless);
        return new ChromeDriver(braveOptions);
      }
      default -> {
        ChromeOptions chromeOptions = new ChromeOptions();
        applyChromiumStabilityFlags(chromeOptions, disableSandbox);
        if (isHeadless) {
          chromeOptions.addArguments("--headless=new");
        }
        log.info("Initialized ChromeDriver with headless={} and no-sandbox={}", isHeadless, disableSandbox);
        return new ChromeDriver(chromeOptions);
      }
    }
  }

  /**
   * True when the host OS is macOS — the only platform with a real,
   * Selenium-drivable Safari.
   */
  private static boolean isMacOs() {
    return System.getProperty("os.name", "").toLowerCase().contains("mac");
  }

  /**
   * Resolves the Chromium browser used to substitute Safari on non-macOS hosts.
   * Configurable via
   * {@code safari.fallback.browser} (chrome | edge | brave); defaults to and
   * falls back to chrome.
   */
  private static String resolveSafariFallbackBrowser() {
    String fb = ConfigReader.get("safari.fallback.browser");
    if (fb == null || fb.isBlank()) {
      return "chrome";
    }
    fb = fb.trim().toLowerCase();
    if (fb.equals("chrome") || fb.equals("edge") || fb.equals("brave")) {
      return fb;
    }
    log.warn("Configured safari.fallback.browser='{}' is not a supported Chromium target; using 'chrome'.", fb);
    return "chrome";
  }

  /**
   * Records the Safari→Chromium substitution in the Allure environment so reports
   * never imply a
   * real Safari run. Overrides the {@code Browser.SAFARI} entry written by
   * writeAllureEnvironmentInfo.
   */
  private static void annotateSafariSubstitution(String os, String fallback) {
    try {
      File resultsDir = new File("target/allure-results");
      if (!resultsDir.exists()) {
        resultsDir.mkdirs();
      }
      File envFile = new File(resultsDir, "environment.properties");
      Properties props = new Properties();
      if (envFile.exists()) {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(envFile)) {
          props.load(fis);
        } catch (IOException e) {
          // ignore
        }
      }
      props.setProperty("Browser.SAFARI", "SUBSTITUTED by " + fallback + " — real Safari unsupported on " + os);
      props.setProperty("Browser." + fallback.toUpperCase(), "Active / Executed (Safari fallback)");
      try (FileOutputStream fos = new FileOutputStream(envFile)) {
        props.store(fos, "Allure Environment Properties");
      }
    } catch (Exception e) {
      log.warn("Failed to annotate Safari substitution in Allure env: {}", e.getMessage());
    }
  }

  /**
   * getDriver() using ThreadLocal to ensure thread safety when running tests in
   * parallel. Each
   * thread will have its own instance of WebDriver, preventing conflicts and
   * ensuring that tests do
   * not interfere with each other.
   */
  public static synchronized WebDriver getDriver() {
    return tlDriver.get();
  }

  /**
   * removeDriver() using ThreadLocal to ensure thread safety when running tests
   * in parallel. Each
   * thread will have its own instance of WebDriver, preventing conflicts and
   * ensuring that tests do
   * not interfere with each other.
   */
  public static void removeDriver() {
    // QUIT THE BROWSER, THEN drop the reference.
    //
    // This used to be a bare tlDriver.remove(), which only cleared the ThreadLocal and
    // left the Chrome + chromedriver processes ALIVE. Every caller already assumed the
    // name meant "release the driver", and most of them worked only because they happened
    // to call driver.quit() themselves first — so the leak was invisible until a caller
    // forgot. A method whose name promises cleanup must actually clean up; relying on
    // every future caller to remember the second half is how a machine ends up with forty
    // orphaned chromedrivers after a long run.
    //
    // Double-quit is safe: callers that already quit (Hooks, the TDD UI smoke classes)
    // will hit a NoSuchSessionException here, which is caught and ignored. Better a
    // harmless duplicate than a leaked process.
    try {
      WebDriver driver = tlDriver.get();
      if (driver != null) {
        driver.quit();
      }
    } catch (Exception e) {
      // Already quit, or the session died with the browser. Nothing to release.
      log.debug("removeDriver(): driver was already gone ({}).", e.getClass().getSimpleName());
    } finally {
      // ALWAYS drop the ThreadLocal, even if quit() threw. Leaving a dead driver in the
      // ThreadLocal would hand the next test on this thread a stale, unusable session.
      tlDriver.remove();
    }
  }

  private static void writeAllureEnvironmentInfo(String browser, String env) {
    try {
      File resultsDir = new File("target/allure-results");
      if (!resultsDir.exists()) {
        resultsDir.mkdirs();
      }
      File envFile = new File(resultsDir, "environment.properties");
      Properties props = new Properties();
      if (envFile.exists()) {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(envFile)) {
          props.load(fis);
        } catch (IOException e) {
          // ignore
        }
      }
      props.setProperty("Environment", env);
      props.setProperty("OS.Name", System.getProperty("os.name"));
      props.setProperty("OS.Version", System.getProperty("os.version"));
      props.setProperty("OS.Arch", System.getProperty("os.arch"));
      props.setProperty("Java.Version", System.getProperty("java.version"));
      props.setProperty("Browser." + browser.toUpperCase(), "Active / Executed");
      try (FileOutputStream fos = new FileOutputStream(envFile)) {
        props.store(fos, "Allure Environment Properties");
      }
    } catch (Exception e) {
      log.warn("Failed to write Allure environment info: {}", e.getMessage());
    }
  }
}
