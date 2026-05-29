package com.timepieces.automation.base;

import java.util.List;

import com.timepieces.automation.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

public class DriverFactory {

  private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

  /**
   * ChromeOptions for Chrome & Brave: Utilized --headless=new instead of the legacy --headless,
   * which is the modern standard for Chromium-based browsers starting from version 109 to ensure it
   * renders pages identically to the headed version. Safari Fallback: Added a System.out.println
   * warning so you have a trail in your console explaining why Safari is popping up even when
   * headless=true is set in your config.
   */
  public static WebDriver initDriver() {
    String browser = ConfigReader.get("browser");
    boolean isHeadless = Boolean.parseBoolean(ConfigReader.get("headless"));
    boolean disableChromeSandbox = Boolean.parseBoolean(ConfigReader.get("chrome.no.sandbox"));
    WebDriver driver =
        switch (browser.toLowerCase()) {
          case "chrome" -> {
            ChromeOptions chromeOptions = new ChromeOptions();
            applyChromiumStabilityFlags(chromeOptions, disableChromeSandbox);
            if (isHeadless) {
              chromeOptions.addArguments("--headless=new");
            }
            yield new ChromeDriver(chromeOptions);
          }
          case "firefox" -> {
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            // Removes navigator.webdriver=true — masks the most obvious webdriver fingerprint
            // from bot detectors like Cloudflare Turnstile. Same intent as the Chrome flags.
            firefoxOptions.addPreference("dom.webdriver.enabled", false);
            firefoxOptions.addPreference("useAutomationExtension", false);
            if (isHeadless) {
              firefoxOptions.addArguments("--headless");
            }
            yield new FirefoxDriver(firefoxOptions);
          }
          case "edge" -> {
            EdgeOptions edgeOptions = new EdgeOptions();
            applyChromiumStabilityFlags(edgeOptions, disableChromeSandbox);
            if (isHeadless) {
              edgeOptions.addArguments("--headless=new");
            }
            yield new EdgeDriver(edgeOptions);
          }
          case "safari" -> {
            if (isHeadless) {
              System.out.println("Safari browser is being used. Headless mode is not supported.");
            }
            yield new SafariDriver();
          }
          case "brave" -> {
            ChromeOptions braveOptions = new ChromeOptions();
            String bravePath = System.getenv("BRAVE_PATH");
            if (bravePath == null || bravePath.isEmpty()) {
              throw new IllegalStateException(
                  "Environment variable BRAVE_PATH is not set or empty.");
            }
            braveOptions.setBinary(bravePath);
            applyChromiumStabilityFlags(braveOptions, disableChromeSandbox);
            if (isHeadless) braveOptions.addArguments("--headless=new");
            yield new ChromeDriver(braveOptions);
          }
          default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    tlDriver.set(driver);
    return getDriver();
  }

  // --disable-dev-shm-usage avoids the tiny default /dev/shm crashing Chrome in Docker.
  // --no-sandbox is required when Chrome runs as root inside a container; opt-in so it
  //   doesn't silently turn off the sandbox on developer machines.
  // The AutomationControlled / excludeSwitches / useAutomationExtension trio masks the
  //   most obvious webdriver fingerprints so Cloudflare Turnstile's bot score drops.
  //   This is not a CAPTCHA bypass — a properly hostile challenge still blocks us — but
  //   it gives managed challenges a fighting chance of auto-passing.
  private static void applyChromiumStabilityFlags(ChromeOptions options, boolean disableSandbox) {
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--window-size=1920,1080");
    options.addArguments("--disable-blink-features=AutomationControlled");
    options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
    options.setExperimentalOption("useAutomationExtension", false);
    if (disableSandbox) {
      options.addArguments("--no-sandbox");
    }
  }

  private static void applyChromiumStabilityFlags(EdgeOptions options, boolean disableSandbox) {
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
   * getDriver() using ThreadLocal to ensure thread safety when running tests in parallel. Each
   * thread will have its own instance of WebDriver, preventing conflicts and ensuring that tests do
   * not interfere with each other.
   */
  public static synchronized WebDriver getDriver() {
    return tlDriver.get();
  }

  /**
   * removeDriver() using ThreadLocal to ensure thread safety when running tests in parallel. Each
   * thread will have its own instance of WebDriver, preventing conflicts and ensuring that tests do
   * not interfere with each other.
   */
  public static void removeDriver() {
    tlDriver.remove();
  }
}
