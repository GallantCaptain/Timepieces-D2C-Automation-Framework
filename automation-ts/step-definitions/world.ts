import { IWorldOptions, World, setWorldConstructor } from "@cucumber/cucumber";
import { BrowserContext, Page } from "@playwright/test";

/**
 * Cucumber World carrying the per-scenario Playwright BrowserContext/Page.
 * <p>
 * This is the TypeScript analogue of the Java framework's
 * DriverFactory ThreadLocal<WebDriver>: a fresh BrowserContext per scenario
 * gives the same test-isolation guarantee (separate cookies/storage/tabs)
 * that ThreadLocal gives across parallel TestNG threads — except Playwright
 * gets it from one browser process instead of one browser-per-thread, which
 * is why scenario parallelism in cucumber-js is configured via
 * `--parallel N` workers rather than a data-provider thread count.
 *
 * @since 2026-07-05
 * @version 1.0
 * @author Avinash Rath
 */
export class TimepiecesWorld extends World {
  context!: BrowserContext;
  page!: Page;

  constructor(options: IWorldOptions) {
    super(options);
  }
}

setWorldConstructor(TimepiecesWorld);
