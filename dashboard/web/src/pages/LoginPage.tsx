import { useState, type FormEvent } from 'react';
import { useAuth } from '../auth/AuthContext';
import { ApiError } from '../api/client';

export function LoginPage() {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await login(email, password);
      // RequireAuth/App routing takes over once user is set.
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Unable to sign in.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="center">
      <form className="card login-card" onSubmit={onSubmit}>
        <div className="brand large">
          <span className="brand-dot" />
          Timepieces&nbsp;<strong>Test Dashboard</strong>
        </div>
        <p className="muted">Sign in to view test trends and run flows.</p>

        <label>
          Email
          <input
            type="email"
            value={email}
            autoComplete="username"
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </label>
        <label>
          Password
          <input
            type="password"
            value={password}
            autoComplete="current-password"
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>

        {error && <div className="alert error">{error}</div>}

        <button className="btn primary" type="submit" disabled={submitting}>
          {submitting ? 'Signing in…' : 'Sign in'}
        </button>
      </form>
    </div>
  );
}