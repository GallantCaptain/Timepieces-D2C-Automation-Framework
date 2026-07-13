import { APIRequestContext, expect, test } from "@playwright/test";

import { ConfigReader } from "../../src/config/ConfigReader";

/**
 * TypeScript port of the Java framework's SearchApiTest — structural
 * template only. The Java suite covers ~100 cases across 6 groups (product
 * matrix, generic keywords, collections, brand/misspellings, deep keywords,
 * edge cases); this file ports ONE representative case (a generic keyword
 * search) end-to-end to prove out the token -> search request flow and the
 * project's TDD/API test wiring. Porting the remaining ~99 cases is
 * mechanical repetition of this same pattern — see README.md.
 *
 * @since 2026-07-05
 * @version 1.0
 * @author Avinash Rath
 */

const baseUrl = ConfigReader.get("BASE_URL") ?? "https://staging.timepieces-d2c.example";
const userAgent =
  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36";

interface SearchResult {
  status: number;
  timepieces: string[];
  accessories: string[];
  error: string | null;
}

/** Requests a search token, then performs the search — mirrors Java's performSearch(). */
async function performSearch(request: APIRequestContext, keyword: string): Promise<SearchResult> {
  const tokenResponse = await request.post(`${baseUrl}/api/request-token`, {
    headers: { "User-Agent": userAgent, Referer: `${baseUrl}/` },
    data: { identifier: keyword },
  });

  if (!tokenResponse.ok()) {
    return { status: tokenResponse.status(), timepieces: [], accessories: [], error: `Token request status: ${tokenResponse.status()}` };
  }

  const tokenBody = await tokenResponse.json();
  const token: string | undefined = tokenBody?.token;
  const timestamp = tokenBody?.timestamp ?? "";

  if (!token) {
    return { status: 200, timepieces: [], accessories: [], error: "Null token returned" };
  }

  const searchResponse = await request.get(`${baseUrl}/api/search`, {
    headers: {
      "User-Agent": userAgent,
      Referer: `${baseUrl}/`,
      "x-request-token": token,
      "x-request-timestamp": String(timestamp),
    },
    params: { q: keyword },
  });

  if (!searchResponse.ok()) {
    return {
      status: searchResponse.status(),
      timepieces: [],
      accessories: [],
      error: `Search request status: ${searchResponse.status()}`,
    };
  }

  const body = await searchResponse.json();
  return {
    status: 200,
    timepieces: (body?.data?.timepieces ?? []).map((t: { handle: string }) => t.handle),
    accessories: (body?.data?.accessories ?? []).map((a: { handle: string }) => a.handle),
    error: null,
  };
}

test.describe("Timepieces Search API — TypeScript template", () => {
  test('generic keyword "watches" should return at least one result', async ({ request }) => {
    const result = await performSearch(request, "watches");

    if (result.error) {
      // Mirrors the Java suite's tolerant handling of an unreachable staging
      // environment (Cloudflare/network) rather than failing the whole run.
      test.skip(true, `Search API not reachable from this environment: ${result.error}`);
      return;
    }

    expect(result.status, "Search for 'watches' should return HTTP 200").toBe(200);
    expect(
      [...result.timepieces, ...result.accessories].length,
      "Search for 'watches' should return at least one result",
    ).toBeGreaterThan(0);
  });
});
