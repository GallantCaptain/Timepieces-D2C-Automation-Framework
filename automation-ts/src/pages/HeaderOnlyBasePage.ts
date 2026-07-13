import { Page } from "@playwright/test";

import { BasePage } from "./BasePage";
import { HeaderComponent } from "./components/HeaderComponent";

/**
 * For all pages having only the global header and no footer. TypeScript
 * analogue of the Java framework's HeaderOnlyBasePage<SELF>.
 * <p>
 * No self-type generic needed here (contrast with the Java
 * `HeaderOnlyBasePage<SELF extends HeaderOnlyBasePage<SELF>> extends
 * BasePage<SELF>`) — TypeScript's `this` typing on BasePage.open() already
 * flows through this class to every concrete subclass.
 *
 * @since 2026-07-05
 * @version 1.0
 * @author Avinash Rath
 */
export abstract class HeaderOnlyBasePage extends BasePage {
  readonly header: HeaderComponent;

  constructor(page: Page) {
    super(page);
    this.header = new HeaderComponent(page);
  }
}
