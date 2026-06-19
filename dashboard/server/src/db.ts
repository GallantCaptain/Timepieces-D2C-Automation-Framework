import pg from 'pg';
import { config } from './config.js';

const { Pool } = pg;

// Neon and most managed Postgres require TLS. node-postgres won't enable it
// from the URL alone, so turn it on when the connection string asks for it.
const needsSsl = /sslmode=require|neon\.tech|render\.com|amazonaws\.com/.test(
  config.databaseUrl,
);

export const pool = new Pool({
  connectionString: config.databaseUrl,
  ssl: needsSsl ? { rejectUnauthorized: false } : undefined,
  max: 10,
  idleTimeoutMillis: 30_000,
});

pool.on('error', (err) => {
  // Surface idle-client failures instead of crashing the process.
  console.error('[db] unexpected idle client error', err);
});

export async function query<T extends pg.QueryResultRow = pg.QueryResultRow>(
  text: string,
  params?: unknown[],
): Promise<pg.QueryResult<T>> {
  return pool.query<T>(text, params as never[]);
}