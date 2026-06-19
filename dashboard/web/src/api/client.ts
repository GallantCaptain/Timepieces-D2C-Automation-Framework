// Thin fetch wrapper. Attaches the JWT, parses JSON, and normalizes errors.
const TOKEN_KEY = 'timepieces.dashboard.token';

export const tokenStore = {
  get: () => localStorage.getItem(TOKEN_KEY),
  set: (t: string) => localStorage.setItem(TOKEN_KEY, t),
  clear: () => localStorage.removeItem(TOKEN_KEY),
};

export class ApiError extends Error {
  constructor(
    message: string,
    public readonly status: number,
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const token = tokenStore.get();
  const headers = new Headers(init.headers);
  headers.set('Content-Type', 'application/json');
  if (token) headers.set('Authorization', `Bearer ${token}`);

  const res = await fetch(`/api${path}`, { ...init, headers });

  if (res.status === 401) {
    tokenStore.clear();
    throw new ApiError('Session expired — please sign in again.', 401);
  }

  const body = res.status === 204 ? null : await res.json().catch(() => null);

  if (!res.ok) {
    const message = (body && (body.error as string)) || `Request failed (${res.status})`;
    throw new ApiError(message, res.status);
  }
  return body as T;
}

export const api = {
  get: <T>(path: string) => request<T>(path),
  post: <T>(path: string, data?: unknown) =>
    request<T>(path, { method: 'POST', body: data ? JSON.stringify(data) : undefined }),
};

// ---- Response types (mirror the server) ----
export interface User {
  id: number;
  email: string;
  displayName: string | null;
}

export interface LoginResponse {
  token: string;
  user: User;
}

export interface Summary {
  totalRuns: number;
  totalScenarios: number;
  passed: number;
  failed: number;
  passRate: number | null;
  lastRun: string | null;
}

export interface TrendPoint {
  day: string;
  runs: number;
  total: number;
  passed: number;
  failed: number;
  passRate: number | null;
  avgDurationSeconds: number;
}

export interface Run {
  id: number;
  buildId: string | null;
  environment: string | null;
  browser: string | null;
  timestamp: string;
  total: number;
  passed: number;
  failed: number;
  durationSeconds: number;
  passRate: number | null;
  reportUrl: string | null;
}

export interface Flow {
  key: string;
  name: string;
  description: string;
  tags: string;
}

export interface FlowsResponse {
  flows: Flow[];
  browsers: string[];
  environments: string[];
  executionEnabled: boolean;
}

export interface WorkflowRun {
  id: number;
  runNumber: number;
  status: string | null;
  conclusion: string | null;
  event: string;
  htmlUrl: string;
  createdAt: string;
  displayTitle: string;
}