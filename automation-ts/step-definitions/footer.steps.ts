import { DataTable, Given, Then } from "@cucumber/cucumber";
import { expect } from "@playwright/test";

import { ConfigReader } from "../src/config/ConfigReader";
import { FooterComponent } from "../src/pages/components/FooterComponent";
import { HomePage } from "../src/pages/web/HomePage";
import { StagingGatePage } from "../src/pages/web/StagingGatePage";
import { TimepiecesWorld } from "./world";

/**
 * Step definitions for Global_Footer_Navigation.feature. TypeScript port of
 * the Java framework's FooterSteps — same three steps, same page objects,
 * same assertions. The only structural difference is `await` on every page-
 * object call (Playwright's API is async where Selenium's is synchronous).
 *
 * @since 2026-07-05
 * @version 1.0
 * @author Avinash Rath
 */

Given("the user is on the Timepieces storefront homepage", async function (this: TimepiecesWorld) {
  const baseUrl = ConfigReader.get("BASE_URL") ?? "https://staging.timepieces-d2c.example";
  await this.page.goto(baseUrl);
  await new StagingGatePage(this.page).unlockIfPresent();
  await new HomePage(this.page).waitUntilLoaded();
});

Then("the global footer should be visible at the bottom of the page", async function (this: TimepiecesWorld) {
  const footer = new FooterComponent(this.page);
  expect(await footer.isFooterDisplayed(), "Global footer container was not displayed.").toBeTruthy();
});

Then(
  "the footer should display the following policy and utility links:",
  async function (this: TimepiecesWorld, dataTable: DataTable) {
    const expectedLinks = dataTable.raw().flat();
    const footer = new FooterComponent(this.page);
    const allDisplayed = await footer.areLinksDisplayed(expectedLinks);
    expect(allDisplayed, "Some expected footer policy/utility links were not displayed.").toBeTruthy();
  },
);

Then(
  "each footer link should navigate to the correct page in a new tab when clicked and then close the tab:",
  async function (this: TimepiecesWorld, dataTable: DataTable) {
    const expectedLinks = dataTable.raw().flat();
    const footer = new FooterComponent(this.page);

    // Playwright's context 'page' event replaces the Java version's
    // WindowUtils manual window-handle capture/switch/close.
    const modifier: "Control" | "Meta" = process.platform === "darwin" ? "Meta" : "Control";

    for (const linkText of expectedLinks) {
      const expectedPath = await footer.getLinkHref(linkText);

      const [newPage] = await Promise.all([
        this.context.waitForEvent("page"),
        footer.clickLinkNewTab(linkText, modifier),
      ]);
      await newPage.waitForLoadState();

      // Known Playwright quirk: a target="_blank" tab can briefly report
      // "about:blank" as its url() before the real navigation lands, even
      // after 'load' fires. Fall back to waiting for the URL to actually
      // change before asserting on it.
      if (newPage.url() === "about:blank") {
        await newPage.waitForURL((url) => url.toString() !== "about:blank", { timeout: 10_000 });
      }

      expect(
        newPage.url(),
        `The URL of the new tab did not contain the expected path for link [${linkText}].`,
      ).toContain(expectedPath ?? "");

      await newPage.close();
    }
  },
);
