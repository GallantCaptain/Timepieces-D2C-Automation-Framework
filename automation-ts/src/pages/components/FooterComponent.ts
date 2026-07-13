import { Page } from "@playwright/test";

import { BasePage } from "../BasePage";

/**
 * FooterComponent represents the global site footer: policy/utility links,
 * the mobile accordion vs. desktop menu-column layout, country selector, and
 * copyright text. TypeScript port of the Java framework's FooterComponent —
 * ported in full fidelity for the links/accordion/copyright surface exercised
 * by Global_Footer_Navigation.feature; the remaining ~50 Java locators
 * (social links, per-collection PDP shortcuts, etc.) follow the exact same
 * pattern and are left for whoever continues this migration to add as needed.
 *
 * @since 2026-07-05
 * @version 1.0
 * @author Avinash Rath
 */
export class FooterComponent extends BasePage {
  // ==========================================
  // Constructor
  // ==========================================
  constructor(page: Page) {
    super(page);
  }

  // ==========================================
  // LOCATORS
  // ==========================================
  private readonly footerContainerMobile = "div[data-testid='footer-mobile']";
  private readonly footerContainerDesktop = "div[data-testid='footer-desktop']";

  private readonly countrySelectorToggleMobile =
    "div[data-testid='footer-mobile'] button[data-testid='country-selector-toggle']";
  private readonly countrySelectorToggleDesktop =
    "div[data-testid='footer-desktop'] button[data-testid='country-selector-toggle']";

  private readonly footerCopyrightTextMobile = "div[data-testid='footer-mobile'] p[data-testid='footer-copyright']";
  private readonly footerCopyrightTextDesktop = "div[data-testid='footer-desktop'] p[data-testid='footer-copyright']";

  // ==========================================
  // PRIVATE HELPERS
  // ==========================================
  /**
   * Builds a viewport-scoped, text-matched link locator. Playwright's
   * `:has-text()` engine already does the "exact or contains normalized text"
   * matching that the Java version hand-rolls via two dynamic XPath branches
   * (normalize-space(.)='...' or contains(normalize-space(.), '...')).
   */
  private async linkLocator(linkText: string): Promise<string> {
    const scope = (await this.isMobileViewport()) ? this.footerContainerMobile : this.footerContainerDesktop;
    return `${scope} a:has-text("${linkText}")`;
  }

  // ==========================================
  // BUSINESS LIBRARY
  // ==========================================
  /** B.1 Clicks the country selector toggle in the footer. */
  async clickCountrySelector(): Promise<this> {
    const target = (await this.isMobileViewport())
      ? this.countrySelectorToggleMobile
      : this.countrySelectorToggleDesktop;
    await this.clickElement(target);
    return this;
  }

  /** B.2 Retrieves the copyright text displayed in the footer. */
  async getCopyrightText(): Promise<string> {
    const target = (await this.isMobileViewport()) ? this.footerCopyrightTextMobile : this.footerCopyrightTextDesktop;
    return this.getText(target);
  }

  /**
   * B.3 Dismisses the cookie consent banner if present. Non-fatal by design —
   * mirrors the Java version's short-lived, swallowed-exception approach so
   * it never blocks the rest of a scenario when the banner isn't shown.
   */
  async acceptCookiesIfPresent(): Promise<void> {
    try {
      await this.page.getByRole("button", { name: /Accept Cookies/i }).click({ timeout: 3_000 });
    } catch {
      // Banner not present or already dismissed — expected in most runs.
    }
  }

  /** H.1 Expands a single named accordion (mobile viewport only; no-op on desktop). */
  async expandAccordionMobile(accordionName: string): Promise<this> {
    if (!(await this.isMobileViewport())) {
      return this;
    }
    const accordionButton = `div[data-accordion='${accordionName}'] > button`;
    const expanded = await this.getAttribute(accordionButton, "aria-expanded");
    if (expanded?.toLowerCase() !== "true") {
      await this.clickElement(accordionButton);
    }
    return this;
  }

  /** H.2 Expands all four footer accordions (mobile viewport only; no-op on desktop). */
  async expandAllAccordionsMobile(): Promise<this> {
    if (!(await this.isMobileViewport())) {
      return this;
    }
    for (const accordion of ["Timepieces", "Accessories", "Discover", "Other"]) {
      await this.expandAccordionMobile(accordion);
    }
    return this;
  }

  /** B.4 Clicks a footer link identified by its visible text, expanding accordions first on mobile. */
  async clickLink(linkText: string): Promise<this> {
    await this.acceptCookiesIfPresent();
    await this.page.mouse.wheel(0, 100_000);
    await this.expandAllAccordionsMobile();
    const locator = await this.linkLocator(linkText);
    await this.clickElement(locator);
    return this;
  }

  /**
   * Clicks a footer link with a keyboard modifier held (Control/Meta) so the
   * browser opens it in a new tab — used together with
   * `page.context().waitForEvent('page')` in step definitions. This replaces
   * the Java version's WindowUtils manual window-handle juggling: Playwright
   * exposes the new tab as a `page` event on the BrowserContext instead.
   */
  async clickLinkNewTab(linkText: string, modifier: "Control" | "Meta"): Promise<void> {
    await this.acceptCookiesIfPresent();
    await this.page.mouse.wheel(0, 100_000);
    await this.expandAllAccordionsMobile();
    const locator = await this.linkLocator(linkText);
    await this.page.locator(locator).click({ modifiers: [modifier] });
  }

  /** Retrieves the resolved href for a footer link identified by its visible text. */
  async getLinkHref(linkText: string): Promise<string | null> {
    await this.expandAllAccordionsMobile();
    const locator = await this.linkLocator(linkText);
    return this.getAttribute(locator, "href");
  }

  // ==========================================
  // VERIFICATION METHODS
  // ==========================================
  /** V.1 Verifies the footer container is displayed on the page. */
  async isFooterDisplayed(): Promise<boolean> {
    const target = (await this.isMobileViewport()) ? this.footerContainerMobile : this.footerContainerDesktop;
    return this.isDisplayed(target);
  }

  /** V.2 Verifies whether a single footer link (by visible text) is displayed. */
  async isLinkDisplayed(linkText: string): Promise<boolean> {
    const locator = await this.linkLocator(linkText);
    return this.isDisplayed(locator);
  }

  /**
   * V.3 Verifies a whole list of footer links are displayed, expanding mobile
   * accordions and scrolling to the bottom first — mirrors the Java version's
   * areLinksDisplayed().
   */
  async areLinksDisplayed(expectedLinks: string[]): Promise<boolean> {
    await this.acceptCookiesIfPresent();
    await this.page.mouse.wheel(0, 100_000);
    await this.page.waitForTimeout(1_000); // smooth-scroll/lazy-load settle, matches Java's Thread.sleep(1000)
    await this.expandAllAccordionsMobile();

    let allDisplayed = true;
    for (const linkText of expectedLinks) {
      const displayed = await this.isLinkDisplayed(linkText);
      if (!displayed) {
        allDisplayed = false;
      }
    }
    return allDisplayed;
  }
}
