import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import { dirname, join } from 'node:path';
import { pool } from '../db.js';

// Applies db/schema.sql. Run once before first boot: `npm run db:init`.
async function main() {
  const here = dirname(fileURLToPath(import.meta.url));
  const schemaPath = join(here, '..', 'db', 'schema.sql');
  const sql = await readFile(schemaPath, 'utf8');

  console.log('[initDb] applying schema...');
  await pool.query(sql);
  console.log('[initDb] schema applied (test_runs + dashboard_users ready).');
  await pool.end();
}

main().catch((err) => {
  console.error('[initDb] failed:', err);
  process.exit(1);
});