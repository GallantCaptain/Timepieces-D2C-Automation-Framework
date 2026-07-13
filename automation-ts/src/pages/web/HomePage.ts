import { Page } from "@playwright/test";

import { BasePage } from "../BasePage";

/**
 * The Timepieces Homepage: https://www.timepieces-d2c.example/ (staging:
 * https://staging.timepieces-d2c.example/). TypeScript port of the Java framework's
 * HomePage — only the load-confirmation surface used by
 * Global_Footer_Navigation.feature is ported here; the rest of the Java
 * page's TODO(locators) section (featured collections, drop strip, brand
 * story, etc.) follows the same pattern and is left for the next engineer.
 *
 * @since 2026-07-05
 * @version 1.0
 * @author Avinash Rath
 */
export class HomePage extends BasePage {
  // ==========================================
  // Constructor
  // ==========================================
  constructor(page: Page) {
    super(page);
  }

  // ==========================================
  // LOCATORS
  // ==========================================
  private readonly homepageFullPageContainer = "main[data-testid='home-main']";

  // ==========================================
  // BUSINESS LIBRARY
  // ==========================================
  /**
   * B.1 Waits until the homepage has rendered, using the root <main>
   * container as the readiness signal. Use as a navigation guard before
   * asserting on any child section.
   */
  async waitUntilLoaded(): Promise<this> {
    await this.waitForVisibility(this.homepageFullPageContainer);
    return this;
  }

  // ==========================================
  // VERIFICATION METHODS
  // ==========================================
  /** V.1 Verifies the homepage root container is displayed — a reliable "page loaded" check. */
  async isHomePageLoaded(): Promise<boolean> {
    return this.isDisplayed(this.homepageFullPageContainer);
  }
}
