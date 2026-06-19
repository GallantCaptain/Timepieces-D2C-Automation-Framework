import { api, type Summary, type TrendPoint } from '../api/client';
import { useApi } from '../hooks/useApi';
import { TrendChart } from '../components/TrendChart';

function fmtDate(iso: string | null): string {
  if (!iso) return '—';
  return new Date(iso).toLocaleString();
}

export function DashboardPage() {
  const summary = useApi<Summary>(() => api.get<Summary>('/runs/summary'));
  const trends = useApi<{ days: number; points: TrendPoint[] }>(() =>
    api.get('/runs/trends?days=30'),
  );

  return (
    <div className="stack">
      <h1>Overview</h1>

      <section className="kpi-grid">
        <Kpi label="Total runs" value={summary.data?.totalRuns ?? '—'} />
        <Kpi
          label="Pass rate (all time)"
          value={summary.data?.passRate != null ? `${summary.data.passRate}%` : '—'}
          tone={passTone(summary.data?.passRate)}
        />
        <Kpi label="Scenarios passed" value={summary.data?.passed ?? '—'} tone="good" />
        <Kpi label="Scenarios failed" value={summary.data?.failed ?? '—'} tone="bad" />
        <Kpi label="Last run" value={fmtDate(summary.data?.lastRun ?? null)} small />
      </section>

      <section className="card">
        <div className="card-head">
          <h2>Pass rate &amp; volume — last 30 days</h2>
          <Legend />
        </div>
        {trends.loading && <div className="muted pad">Loading trends…</div>}
        {trends.error && <div className="alert error">{trends.error}</div>}
        {trends.data && <TrendChart points={trends.data.points} />}
      </section>
    </div>
  );
}

function passTone(rate: number | null | undefined): Tone {
  if (rate == null) return 'neutral';
  if (rate >= 90) return 'good';
  if (rate >= 70) return 'warn';
  return 'bad';
}

type Tone = 'good' | 'bad' | 'warn' | 'neutral';

function Kpi({
  label,
  value,
  tone = 'neutral',
  small = false,
}: {
  label: string;
  value: string | number;
  tone?: Tone;
  small?: boolean;
}) {
  return (
    <div className={`card kpi tone-${tone}`}>
      <div className="kpi-label">{label}</div>
      <div className={small ? 'kpi-value sm' : 'kpi-value'}>{value}</div>
    </div>
  );
}

function Legend() {
  return (
    <div className="legend">
      <span>
        <i className="swatch pass" /> passed
      </span>
      <span>
        <i className="swatch fail" /> failed
      </span>
      <span>
        <i className="swatch line" /> pass rate
      </span>
    </div>
  );
}