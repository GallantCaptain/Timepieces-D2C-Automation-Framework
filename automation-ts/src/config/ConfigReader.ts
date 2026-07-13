import * as dotenv from "dotenv";

dotenv.config({ quiet: true });

/**
 * Minimal TypeScript analogue of the Java ConfigReader. Reads from
 * process.env, populated either by a local .env file (via dotenv) or by real
 * environment variables / CI secrets — so the same precedence idea as Java's
 * "runtime override wins" is preserved without needing a separate properties-
 * file layer.
 *
 * Deliberate scope cut for this proof-of-concept: the Java ConfigReader
 * supports per-environment .properties files (staging/production) plus a
 * gitignored local-override layer. That is not reproduced here yet — see
 * README.md "What's deliberately not ported yet" before relying on this for
 * more than one environment.
 *
 * @since 2026-07-05
 * @version 1.0
 * @author Avinash Rath
 */
export class ConfigReader {
  static get(key: string): string | undefined {
    const value = process.env[key];
    return value?.trim() || undefined;
  }
}
