import { Page } from "@playwright/test";

import { FooterComponent } from "./components/FooterComponent";
import { HeaderOnlyBasePage } from "./HeaderOnlyBasePage";

/**
 * For all pages having both the global header and footer. TypeScript
 * analogue of the Java framework's StoreFrontBasePage<SELF>.
 * <p>
 * Same simplification as HeaderOnlyBasePage: no self-type generic. Every
 * concrete page under this class inherits a correctly-typed `open()` for
 * free purely by extending it — compare to Java's
 * `class UpdatesPage extends StoreFrontBasePage<UpdatesPage>`.
 *
 * @since 2026-07-05
 * @version 1.0
 * @author Avinash Rath
 */
export abstract class StoreFrontBasePage extends HeaderOnlyBasePage {
  readonly footer: FooterComponent;

  constructor(page: Page) {
    super(page);
    this.footer = new FooterComponent(page);
  }
}
