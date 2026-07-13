import { Page } from "@playwright/test";

import { ConfigReader } from "../../config/ConfigReader";
import { BasePage } from "../BasePage";

/**
 * Handles the standalone password gate that fronts non-production
 * environments. TypeScript port of the Java framework's StagingGatePage.
 * Selectors and the password are resolved through ConfigReader/.env, same
 * precedence idea as Java:
 * - STAGING_PASSWORD (required when the gate is present)
 * - STAGING_GATE_PASSWORD_FIELD (defaults to input[type='password'])
 * - STAGING_GATE_SUBMIT_BUTTON (defaults to button[type='submit'])
 * - STAGING_GATE_DETECTION_TIMEOUT_MS (defaults to 4000ms)
 * <p>
 * Detection is intentionally fast and non-fatal: unlockIfPresent() probes for
 * the password input with a short wait; if absent (cookie already set, or env
 * has no gate at all) it returns false and the caller continues normally.
 *
 * @since 2026-07-05
 * @version 1.0
 * @author Avinash Rath
 */
export class StagingGatePage extends BasePage {
  private readonly passwordField: string;
  private readonly submitButton: string;
  private readonly detectionTimeoutMs: number;

  // ==========================================
  // Constructor
  // ==========================================
  constructor(page: Page) {
    super(page);
    this.passwordField = ConfigReader.get("STAGING_GATE_PASSWORD_FIELD") ?? "input[type='password']";
    this.submitButton = ConfigReader.get("STAGING_GATE_SUBMIT_BUTTON") ?? "button[type='submit']";
    this.detectionTimeoutMs = Number(ConfigReader.get("STAGING_GATE_DETECTION_TIMEOUT_MS") ?? 4_000);
  }

  // ==========================================
  // PRIVATE HELPERS
  // ==========================================
  /**
   * P.1 If the gate is on the current page, fills the password and submits.
   * Returns true if the gate was detected and unlocked, false if no gate was
   * present (so the caller can decide whether to re-navigate).
   */
  async unlockIfPresent(): Promise<boolean> {
    try {
      await this.waitForVisibility(this.passwordField, this.detectionTimeoutMs);
    } catch {
      return false; // No staging gate detected within the timeout — assume absent or already cleared.
    }

    const password = ConfigReader.get("STAGING_PASSWORD");
    if (!password) {
      throw new Error(
        "Staging password gate detected but STAGING_PASSWORD is unset. Set it in automation-ts/.env " +
          "(copy from .env.example) or pass it as a real environment variable.",
      );
    }

    await this.sendKeys(this.passwordField, password);
    await this.clickElement(this.submitButton);
    await this.waitForInvisibility(this.passwordField, this.detectionTimeoutMs);
    return true;
  }

  // ==========================================
  // VERIFICATION METHODS
  // ==========================================
  /** V.1 Checks if the password gate field is present on the page. */
  async isPasswordGatePresent(): Promise<boolean> {
    try {
      await this.waitForVisibility(this.passwordField, this.detectionTimeoutMs);
      return true;
    } catch {
      return false;
    }
  }
}
