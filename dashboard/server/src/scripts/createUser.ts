import bcrypt from 'bcryptjs';
import { pool } from '../db.js';

// Usage: npm run user:create -- <email> <password> ["Display Name"]
// Upserts an authorized dashboard user with a bcrypt-hashed password.
async function main() {
  const [email, password, displayName] = process.argv.slice(2);

  if (!email || !password) {
    console.error('Usage: npm run user:create -- <email> <password> ["Display Name"]');
    process.exit(1);
  }

  const hash = await bcrypt.hash(password, 12);
  const { rows } = await pool.query(
    `INSERT INTO dashboard_users (email, password_hash, display_name)
     VALUES ($1, $2, $3)
     ON CONFLICT (email)
     DO UPDATE SET password_hash = EXCLUDED.password_hash,
                   display_name = COALESCE(EXCLUDED.display_name, dashboard_users.display_name)
     RETURNING id, email, display_name`,
    [email.toLowerCase(), hash, displayName ?? null],
  );

  console.log('[createUser] upserted:', rows[0]);
  await pool.end();
}

main().catch((err) => {
  console.error('[createUser] failed:', err);
  process.exit(1);
});