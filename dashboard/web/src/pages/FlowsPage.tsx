import { useEffect, useState } from 'react';
import { api, ApiError, type FlowsResponse, type WorkflowRun } from '../api/client';
import { useApi } from '../hooks/useApi';

export function FlowsPage() {
  const flows = useApi<FlowsResponse>(() => api.get('/flows'));
  const [env, setEnv] = useState('staging');
  const [browser, setBrowser] = useState('chrome');
  const [busy, setBusy] = useState<string | null>(null);
  const [notice, setNotice] = useState<{ tone: 'ok' | 'error'; text: string } | null>(null);

  const executionEnabled = flows.data?.executionEnabled ?? false;

  async function runFlow(key: string, name: string) {
    setBusy(key);
    setNotice(null);
    try {
      await api.post(`/flows/${key}/run`, { env, browser });
      setNotice({ tone: 'ok', text: `Dispatched “${name}” on ${env}/${browser}. Watch progress below.` });
      // Give GitHub a moment to register the run, then refresh the list.
      setTimeout(() => setRunsNonce((n) => n + 1), 2500);
    } catch (err) {
      setNotice({
        tone: 'error',
        text: err instanceof ApiError ? err.message : 'Failed to dispatch flow.',
      });
    } finally {
      setBusy(null);
    }
  }

  const [runsNonce, setRunsNonce] = useState(0);

  return (
    <div className="stack">
      <h1>Run test flows</h1>

      {flows.error && <div className="alert error">{flows.error}</div>}
      {flows.data && !executionEnabled && (
        <div className="alert warn">
          Flow execution is disabled — the server has no <code>GITHUB_TOKEN</code> configured.
          You can still view definitions below.
        </div>
      )}

      <div className="filters">
        <label>
          Environment
          <select value={env} onChange={(e) => setEnv(e.target.value)}>
            {(flows.data?.environments ?? ['staging']).map((o) => (
              <option key={o} value={o}>
                {o}
              </option>
            ))}
          </select>
        </label>
        <label>
          Browser
          <select value={browser} onChange={(e) => setBrowser(e.target.value)}>
            {(flows.data?.browsers ?? ['chrome']).map((o) => (
              <option key={o} value={o}>
                {o}
              </option>
            ))}
          </select>
        </label>
      </div>

      {notice && <div className={`alert ${notice.tone === 'ok' ? 'ok' : 'error'}`}>{notice.text}</div>}

      <section className="flow-grid">
        {flows.data?.flows.map((f) => (
          <div key={f.key} className="card flow-card">
            <h3>{f.name}</h3>
            <p className="muted">{f.description}</p>
            <code className="tags">{f.tags}</code>
            <button
              className="btn primary"
              disabled={!executionEnabled || busy !== null}
              onClick={() => runFlow(f.key, f.name)}
            >
              {busy === f.key ? 'Dispatching…' : 'Run flow'}
            </button>
          </div>
        ))}
      </section>

      <RecentRuns enabled={executionEnabled} nonce={runsNonce} />
    </div>
  );
}

function RecentRuns({ enabled, nonce }: { enabled: boolean; nonce: number }) {
  const [runs, setRuns] = useState<WorkflowRun[]>([]);
  const [error, setError] = useState<string | null>(null);

  // Poll every 10s while any run is in progress (and on demand via `nonce`).
  useEffect(() => {
    if (!enabled) return;
    let active = true;
    let timer: ReturnType<typeof setTimeout>;

    const tick = async () => {
      try {
        const res = await api.get<{ runs: WorkflowRun[] }>('/flows/runs');
        if (!active) return;
        setRuns(res.runs);
        setError(null);
        const inProgress = res.runs.some((r) => r.status !== 'completed');
        timer = setTimeout(tick, inProgress ? 10_000 : 30_000);
      } catch (err) {
        if (!active) return;
        setError(err instanceof ApiError ? err.message : 'Failed to load CI runs.');
        timer = setTimeout(tick, 30_000);
      }
    };
    tick();
    return () => {
      active = false;
      clearTimeout(timer);
    };
  }, [enabled, nonce]);

  if (!enabled) return null;

  return (
    <section className="card no-pad">
      <div className="card-head pad-head">
        <h2>Recent pipeline runs</h2>
      </div>
      {error && <div className="alert error">{error}</div>}
      <table className="runs-table">
        <thead>
          <tr>
            <th>Run</th>
            <th>Title</th>
            <th>Trigger</th>
            <th>Status</th>
            <th>Started</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {runs.length === 0 && (
            <tr>
              <td colSpan={6} className="muted center-cell">
                No pipeline runs yet.
              </td>
            </tr>
          )}
          {runs.map((r) => (
            <tr key={r.id}>
              <td>#{r.runNumber}</td>
              <td>{r.displayTitle}</td>
              <td>{r.event}</td>
              <td>
                <StatusBadge status={r.status} conclusion={r.conclusion} />
              </td>
              <td>{new Date(r.createdAt).toLocaleString()}</td>
              <td>
                <a href={r.htmlUrl} target="_blank" rel="noreferrer">
                  View ↗
                </a>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}

function StatusBadge({ status, conclusion }: { status: string | null; conclusion: string | null }) {
  if (status !== 'completed') {
    return <span className="pill tone-warn">{status ?? 'queued'}</span>;
  }
  const tone = conclusion === 'success' ? 'good' : 'bad';
  return <span className={`pill tone-${tone}`}>{conclusion ?? 'completed'}</span>;
}