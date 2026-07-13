import { Locator, Page } from "@playwright/test";

import { ConfigReader } from "../config/ConfigReader";

/**
 * BasePage — Abstract foundation for all Page Object Model (POM) classes.
 * TypeScript analogue of the Java framework's BasePage<SELF>.
 *
 * RESPONSIBILITIES:
 * 1. Wraps the Playwright {@link Page} instance so every page object drives
 *    the same browser tab/context.
 * 2. Provides the same wait/click/input helper vocabulary as the Java
 *    BasePage (getElement, clickElement, sendKeys, getText, getAttribute,
 *    isDisplayed, isMobileViewport) so porting a Java page object's BUSINESS
 *    LIBRARY / VERIFICATION METHODS section is close to line-for-line.
 *
 * WHAT DOES NOT BELONG HERE (same rule as the Java BasePage):
 * - Business logic (belongs in individual page objects)
 * - Assertions (belong in step definitions / spec files)
 * - Browser/context lifecycle (belongs in step-definitions/hooks.ts, the
 *   analogue of Java's DriverFactory + Hooks)
 *
 * FLUENT INTERFACE — NO GENERICS NEEDED:
 * The Java version needs `BasePage<SELF extends BasePage<SELF>>` (CRTP) purely
 * so that `open()` can return the CONCRETE subtype instead of a bare
 * `BasePage`. TypeScript has this built in: a method typed to return
 * `Promise<this>` automatically resolves to the caller's concrete class with
 * zero type parameters anywhere in the hierarchy. Compare:
 *   Java:       class UpdatesPage extends StoreFrontBasePage<UpdatesPage>
 *   TypeScript: class UpdatesPage extends StoreFrontBasePage
 * This is the single biggest simplification when porting a page — see
 * README.md for the full write-up.
 *
 * @since 2026-07-05
 * @version 1.0
 * @author Avinash Rath
 */
export abstract class BasePage {
  protected readonly page: Page;

  constructor(page: Page) {
    this.page = page;
  }

  // ==========================================
  // PRIVATE HELPERS
  // ==========================================
  private locator(selector: string): Locator {
    return this.page.locator(selector);
  }

  // ==========================================
  // BUSINESS LIBRARY
  // ==========================================
  /**
   * B.1 Waits for DOM presence/visibility of the first matching element and
   * returns its Locator. Playwright's `waitFor` already covers Java's
   * separate "presence vs visibility" distinction (B.1/B.4 in the Java
   * BasePage) — auto-waiting locators make that split unnecessary here.
   */
  protected async getElement(selector: string, timeoutMs = 10_000): Promise<Locator> {
    const element = this.locator(selector).first();
    await element.waitFor({ state: "visible", timeout: timeoutMs });
    return element;
  }

  /** B.2 Returns all elements matching the locator, waiting for at least one to be visible. */
  protected async getElements(selector: string, timeoutMs = 10_000): Promise<Locator> {
    const elements = this.locator(selector);
    await elements.first().waitFor({ state: "visible", timeout: timeoutMs });
    return elements;
  }

  /** B.3 Pure "wait for visible" without returning the element — navigation confirmation guard. */
  protected async waitForVisibility(selector: string, timeoutMs = 10_000): Promise<void> {
    await this.locator(selector).first().waitFor({ state: "visible", timeout: timeoutMs });
  }

  /** B.4 Waits for invisibility/disappearance — e.g. loading spinners, dismissed modals. */
  protected async waitForInvisibility(selector: string, timeoutMs = 10_000): Promise<void> {
    await this.locator(selector).first().waitFor({ state: "hidden", timeout: timeoutMs });
  }

  /** B.5 Centralized click wrapper. Playwright auto-waits for actionability (visible, enabled, stable) before clicking. */
  protected async clickElement(selector: string): Promise<void> {
    await this.locator(selector).click();
  }

  /** B.6 Centralized input wrapper. `.fill()` clears existing text before typing, same as Java's clear()+sendKeys(). */
  protected async sendKeys(selector: string, value: string | null | undefined): Promise<void> {
    if (value == null) {
      return;
    }
    await this.locator(selector).fill(value);
  }

  /** B.7 Safely pulls trimmed inner text from the first matching element. */
  protected async getText(selector: string): Promise<string> {
    const text = await this.locator(selector).first().innerText();
    return text.trim();
  }

  /** B.8 Returns the named attribute's value from the first matching element. */
  protected async getAttribute(selector: string, attributeName: string): Promise<string | null> {
    return this.locator(selector).first().getAttribute(attributeName);
  }

  /** B.9 Returns the base url for navigation. Defaults to BASE_URL from config. */
  getBaseUrl(): string {
    return ConfigReader.get("BASE_URL") ?? "https://staging.timepieces-d2c.example";
  }

  /** B.10 Returns the page path for navigation. Empty by default — pages with a real endpoint override this. */
  protected getPagePath(): string {
    return "";
  }

  /** B.11 Returns the page title for assertions. Empty by default — pages with a known title override this. */
  protected getPageTitle(): string {
    return "";
  }

  /**
   * B.12 Opens the page by navigating to baseUrl + pagePath. Returns `this`,
   * typed by TypeScript as the concrete subclass automatically — no override
   * required in any subclass purely to fix the return type (see class doc).
   */
  async open(): Promise<this> {
    await this.page.goto(this.getBaseUrl() + this.getPagePath());
    return this;
  }

  // ==========================================
  // VERIFICATION METHODS
  // ==========================================
  /** V.1 Checks if the current viewport is sized as mobile (Tailwind `md` breakpoint). */
  async isMobileViewport(): Promise<boolean> {
    const size = this.page.viewportSize();
    return (size?.width ?? 1280) < 768;
  }

  /** V.2 Boolean verification wrapper for element display status — never throws. */
  protected async isDisplayed(selector: string): Promise<boolean> {
    try {
      return await this.locator(selector).first().isVisible();
    } catch {
      return false;
    }
  }
}
