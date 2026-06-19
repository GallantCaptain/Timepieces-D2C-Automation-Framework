import { Router } from 'express';
import bcrypt from 'bcryptjs';
import { z } from 'zod';
import { query } from '../db.js';
import { signToken, requireAuth, type AuthUser } from '../auth.js';

export const authRouter = Router();

const loginSchema = z.object({
  email: z.string().email(),
  password: z.string().min(1),
});

interface UserRow {
  id: number;
  email: string;
  display_name: string | null;
  password_hash: string;
}

authRouter.post('/login', async (req, res) => {
  const parsed = loginSchema.safeParse(req.body);
  if (!parsed.success) {
    res.status(400).json({ error: 'email and password are required' });
    return;
  }

  const email = parsed.data.email.toLowerCase();
  const { rows } = await query<UserRow>(
    'SELECT id, email, display_name, password_hash FROM dashboard_users WHERE email = $1',
    [email],
  );
  const user = rows[0];

  // Compare against a dummy hash when the user is missing so the response time
  // doesn't reveal whether the email exists.
  const hash = user?.password_hash ?? '$2a$12$invalidinvalidinvalidinvalidinvalidinvalidinv';
  const ok = await bcrypt.compare(parsed.data.password, hash);

  if (!user || !ok) {
    res.status(401).json({ error: 'Invalid email or password' });
    return;
  }

  await query('UPDATE dashboard_users SET last_login_at = CURRENT_TIMESTAMP WHERE id = $1', [
    user.id,
  ]);

  const authUser: AuthUser = {
    id: user.id,
    email: user.email,
    displayName: user.display_name,
  };
  res.json({ token: signToken(authUser), user: authUser });
});

authRouter.get('/me', requireAuth, (req, res) => {
  res.json({ user: req.user });
});