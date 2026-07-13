import { defineConfig } from "@playwright/test";

/**
 * Playwright config for the TDD/API side of the framework only (mirrors the
 * Java project's testng-tdd.xml). BDD/E2E scenarios run separately through
 * Cucumber (see cucumber.cjs) — that split matches the Java framework's own
 * BDD-vs-TDD suite separation.
 */
export default defineConfig({
  testDir: "./tests",
  timeout: 30_000,
  reporter: [["list"]],
  use: {
    baseURL: process.env.BASE_URL ?? "https://staging.timepieces-d2c.example",
    extraHTTPHeaders: {
      "User-Agent":
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36",
    },
  },
});
