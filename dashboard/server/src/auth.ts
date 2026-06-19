import jwt from 'jsonwebtoken';
import type { Request, Response, NextFunction } from 'express';
import { config } from './config.js';

export interface AuthUser {
  id: number;
  email: string;
  displayName: string | null;
}

interface JwtClaims {
  sub: number;
  email: string;
  name: string | null;
}

export function signToken(user: AuthUser): string {
  const claims: JwtClaims = { sub: user.id, email: user.email, name: user.displayName };
  const options: jwt.SignOptions = {
    expiresIn: config.jwtExpiresIn as jwt.SignOptions['expiresIn'],
  };
  return jwt.sign(claims, config.jwtSecret, options);
}

// Express augmentation so route handlers can read req.user.
declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Express {
    interface Request {
      user?: AuthUser;
    }
  }
}

/** Rejects the request with 401 unless a valid Bearer token is present. */
export function requireAuth(req: Request, res: Response, next: NextFunction): void {
  const header = req.header('authorization') ?? '';
  const [scheme, token] = header.split(' ');

  if (scheme !== 'Bearer' || !token) {
    res.status(401).json({ error: 'Missing or malformed Authorization header' });
    return;
  }

  try {
    const claims = jwt.verify(token, config.jwtSecret) as unknown as JwtClaims;
    req.user = { id: claims.sub, email: claims.email, displayName: claims.name };
    next();
  } catch {
    res.status(401).json({ error: 'Invalid or expired token' });
  }
}