import { After, AfterAll, Before, BeforeAll, setDefaultTimeout } from "@cucumber/cucumber";
import { Browser, chromium } from "@playwright/test";

import { TimepiecesWorld } from "./world";

// Cucumber's built-in default (5s) is calibrated for unit-test speed, not real
// browser navigation + staging-gate detection. Java's equivalent knob is
// explicit.wait.timeout (10s) layered with staging.gate.detection.timeout (4s).
setDefaultTimeout(30_000);

/**
 * Browser/context lifecycle hooks — TypeScript analogue of the Java
 * framework's DriverFactory + Hooks. One Chromium process is launched once
 * per suite (BeforeAll/AfterAll); each scenario gets its own isolated
 * BrowserContext + Page (Before/After), which is closed immediately after the
 * scenario so state never leaks between scenarios.
 *
 * @since 2026-07-05
 * @version 1.0
 * @author Avinash Rath
 */
let browser: Browser;

// Cucumber's default 5s hook timeout is too short for a cold browser launch/close.
BeforeAll({ timeout: 30_000 }, async function () {
  browser = await chromium.launch({ headless: process.env.HEADLESS !== "false" });
});

Before(async function (this: TimepiecesWorld) {
  this.context = await browser.newContext();
  this.page = await this.context.newPage();
});

After(async function (this: TimepiecesWorld) {
  await this.page?.close();
  await this.context?.close();
});

AfterAll({ timeout: 15_000 }, async function () {
  await browser?.close();
});
