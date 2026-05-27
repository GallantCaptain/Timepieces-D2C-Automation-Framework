package com.timepieces.automation.base;

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
    WebDriver driver =
        switch (browser.toLowerCase()) {
          case "chrome" -> {
            ChromeOptions chromeOptions = new ChromeOptions();
            if (isHeadless) {
              chromeOptions.addArguments("--headless=new");
            }
            yield new ChromeDriver(chromeOptions);
          }
          case "firefox" -> {
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            if (isHeadless) {
              firefoxOptions.addArguments("--headless");
            }
            yield new FirefoxDriver(firefoxOptions);
          }
          case "edge" -> {
            EdgeOptions edgeOptions = new EdgeOptions();
            if (isHeadless) {
              edgeOptions.addArguments("--headless");
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
            if (isHeadless) braveOptions.addArguments("--headless=new");
            yield new ChromeDriver(braveOptions);
          }
          default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    tlDriver.set(driver);
    return getDriver();
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
