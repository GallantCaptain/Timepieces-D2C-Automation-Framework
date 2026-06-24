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
import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j2;

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
    tlDriver.set(driver);
    applyCloudflareBypassHeader(driver);
    return getDriver();
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
      log.info("Applying Cloudflare bypass header X-Automation-Bypass to Chromium session.");
      try {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();
        headers.put("X-Automation-Bypass", token);
        params.put("headers", headers);
        chromiumDriver.executeCdpCommand("Network.setExtraHTTPHeaders", params);
        log.info("Cloudflare bypass header successfully applied.");
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
    if (disableSandbox) {
      options.addArguments("--no-sandbox");
    }
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
          throw new IllegalStateException("Brave path is not set: set the BRAVE_PATH environment variable or brave.path in config.");
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
    tlDriver.remove();
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
