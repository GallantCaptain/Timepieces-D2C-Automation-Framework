import { useState } from 'react';
import { api, type Run } from '../api/client';
import { useApi } from '../hooks/useApi';

function fmtDuration(s: number): string {
  if (s < 60) return `${s}s`;
  const m = Math.floor(s / 60);
  const r = s % 60;
  return `${m}m ${r}s`;
}

export function RunsPage() {
  const [env, setEnv] = useState('');
  const [browser, setBrowser] = useState('');

  const qs = new URLSearchParams();
  if (env) qs.set('env', env);
  if (browser) qs.set('browser', browser);
  qs.set('limit', '100');

  const { data, error, loading } = useApi<{ runs: Run[] }>(
    () => api.get(`/runs?${qs.toString()}`),
    [env, browser],
  );

  return (
    <div className="stack">
      <h1>Execution history</h1>

      <div className="filters">
        <label>
          Environment
          <select value={env} onChange={(e) => setEnv(e.target.value)}>
            <option value="">All</option>
            <option value="dev">dev</option>
            <option value="staging">staging</option>
            <option value="prod">prod</option>
          </select>
        </label>
        <label>
          Browser
          <select value={browser} onChange={(e) => setBrowser(e.target.value)}>
            <option value="">All</option>
            <option value="chrome">chrome</option>
            <option value="firefox">firefox</option>
            <option value="edge">edge</option>
            <option value="brave">brave</option>
          </select>
        </label>
      </div>

      {loading && <div className="muted pad">Loading runs…</div>}
      {error && <div className="alert error">{error}</div>}

      {data && (
        <div className="card no-pad">
          <table className="runs-table">
            <thead>
              <tr>
                <th>When</th>
                <th>Build</th>
                <th>Env</th>
                <th>Browser</th>
                <th className="num">Passed</th>
                <th className="num">Failed</th>
                <th className="num">Total</th>
                <th className="num">Pass&nbsp;rate</th>
                <th className="num">Duration</th>
                <th>Report</th>
              </tr>
            </thead>
            <tbody>
              {data.runs.length === 0 && (
                <tr>
                  <td colSpan={10} className="muted center-cell">
                    No runs match these filters.
                  </td>
                </tr>
              )}
              {data.runs.map((r) => (
                <tr key={r.id}>
                  <td>{new Date(r.timestamp).toLocaleString()}</td>
                  <td>{r.buildId ?? '—'}</td>
                  <td>{r.environment ?? '—'}</td>
                  <td>{r.browser ?? '—'}</td>
                  <td className="num pass">{r.passed}</td>
                  <td className={`num ${r.failed > 0 ? 'fail' : ''}`}>{r.failed}</td>
                  <td className="num">{r.total}</td>
                  <td className="num">
                    {r.passRate != null ? <PassRate rate={r.passRate} /> : '—'}
                  </td>
                  <td className="num">{fmtDuration(r.durationSeconds)}</td>
                  <td>
                    {r.reportUrl ? (
                      <a href={r.reportUrl} target="_blank" rel="noreferrer">
                        Allure ↗
                      </a>
                    ) : (
                      <span className="muted">local</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

function PassRate({ rate }: { rate: number }) {
  const tone = rate >= 90 ? 'good' : rate >= 70 ? 'warn' : 'bad';
  return <span className={`pill tone-${tone}`}>{rate}%</span>;
}