package com.timepieces.automation.bdd.hooks;

import com.timepieces.automation.base.DriverFactory;
import com.timepieces.automation.utils.ConfigReader;
import com.timepieces.automation.utils.ScreenRecorderUtil;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.log4j.Log4j2;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

/**
 * Cucumber lifecycle hooks: provisions a fresh ThreadLocal WebDriver and screen
 * recorder per scenario; on failure, attaches a PNG screenshot and the captured
 * AVI to the Allure report.
 */
@Log4j2
public class Hooks {

    private static final String SEPARATOR = "==========================================================================";
    private static final String FILENAME_UNSAFE_CHARS = "[^A-Za-z0-9._-]";

    @Before(order = 0)
    public void setUp(Scenario scenario) {
        log.info(SEPARATOR);
        log.info("STARTING SCENARIO: {}", scenario.getName());
        log.info(SEPARATOR);

        DriverFactory.initDriver();

        if (isVideoRecordingEnabled()) {
            ScreenRecorderUtil.start(buildRecordingName(scenario));
        }
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverFactory.getDriver();
        try {
            if (scenario.isFailed()) {
                log.error("SCENARIO FAILED: {}", scenario.getName());
                attachScreenshot(driver, scenario);
                attachVideoIfRecording(scenario);
            } else {
                log.info("SCENARIO PASSED: {}", scenario.getName());
                if (isVideoRecordingEnabled()) {
                    ScreenRecorderUtil.stopAndDiscard();
                }
            }
        } finally {
            quitDriverQuietly(driver);
            log.info(SEPARATOR);
        }
    }

    private void attachScreenshot(WebDriver driver, Scenario scenario) {
        if (driver == null) {
            log.warn("No active WebDriver; screenshot capture skipped.");
            return;
        }
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Screenshot on Failure");
            log.info("Screenshot attached to Allure report.");
        } catch (WebDriverException e) {
            log.error("Failed to capture failure screenshot: {}", e.getMessage(), e);
        }
    }

    private void attachVideoIfRecording(Scenario scenario) {
        if (!isVideoRecordingEnabled()) {
            return;
        }
        byte[] video = ScreenRecorderUtil.stopAndExtract();
        if (video == null || video.length == 0) {
            log.warn("No video bytes available to attach for scenario [{}].", scenario.getName());
            return;
        }
        scenario.attach(video, "video/avi", "Video on Failure");
        log.info("Video attached to Allure report ({} bytes).", video.length);
    }

    private void quitDriverQuietly(WebDriver driver) {
        if (driver == null) {
            return;
        }
        try {
            driver.quit();
            log.info("Browser closed and driver instance safely removed.");
        } catch (Exception e) {
            log.error("Failed to quit WebDriver cleanly: {}", e.getMessage(), e);
        } finally {
            DriverFactory.removeDriver();
        }
    }

    private boolean isVideoRecordingEnabled() {
        String flag = ConfigReader.get("recordVideo");
        return flag == null || Boolean.parseBoolean(flag);
    }

    private String buildRecordingName(Scenario scenario) {
        String safeName = scenario.getName().replaceAll(FILENAME_UNSAFE_CHARS, "_");
        return safeName + "_" + System.currentTimeMillis();
    }
}
