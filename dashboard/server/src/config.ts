import 'dotenv/config';

function required(name: string, value: string | undefined): string {
  if (!value || value.trim() === '') {
    throw new Error(`Missing required environment variable: ${name}`);
  }
  return value;
}

function optional(value: string | undefined, fallback: string): string {
  return value && value.trim() !== '' ? value : fallback;
}

// DATABASE_URL takes precedence, but fall back to NEON_DATABASE_URL so the
// dashboard can share the exact connection string the test publisher uses.
const databaseUrl =
  process.env.DATABASE_URL?.trim() || process.env.NEON_DATABASE_URL?.trim();

export const config = {
  port: Number(optional(process.env.PORT, '4000')),
  corsOrigins: optional(process.env.CORS_ORIGIN, 'http://localhost:5173')
    .split(',')
    .map((o) => o.trim())
    .filter(Boolean),

  jwtSecret: required('JWT_SECRET', process.env.JWT_SECRET),
  jwtExpiresIn: optional(process.env.JWT_EXPIRES_IN, '12h'),

  databaseUrl: required('DATABASE_URL/NEON_DATABASE_URL', databaseUrl),

  github: {
    token: process.env.GITHUB_TOKEN?.trim() ?? '',
    owner: optional(process.env.GITHUB_OWNER, 'TimepiecesTech'),
    repo: optional(process.env.GITHUB_REPO, 'qa-automation'),
    workflowFile: optional(process.env.GITHUB_WORKFLOW_FILE, 'test-pipeline.yml'),
    defaultRef: optional(process.env.GITHUB_DEFAULT_REF, 'main'),
  },
} as const;

/** True when GitHub dispatch is configured; flow execution is disabled otherwise. */
export const githubConfigured = (): boolean => config.github.token !== '';