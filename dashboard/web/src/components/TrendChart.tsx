import type { TrendPoint } from '../api/client';

// Dependency-free SVG: bars = scenarios/day (green passed, red failed),
// overlaid line = pass-rate %.
export function TrendChart({ points }: { points: TrendPoint[] }) {
  if (points.length === 0) {
    return <div className="muted pad">No runs in this window yet.</div>;
  }

  const W = 720;
  const H = 220;
  const padX = 36;
  const padY = 20;
  const innerW = W - padX * 2;
  const innerH = H - padY * 2;

  const maxTotal = Math.max(1, ...points.map((p) => p.total));
  const barGap = 4;
  const barW = Math.max(2, innerW / points.length - barGap);

  const x = (i: number) => padX + i * (innerW / points.length) + barGap / 2;
  const yBar = (v: number) => padY + innerH - (v / maxTotal) * innerH;
  const yRate = (r: number) => padY + innerH - (r / 100) * innerH;

  const linePts = points
    .map((p, i) => (p.passRate == null ? null : `${x(i) + barW / 2},${yRate(p.passRate)}`))
    .filter(Boolean)
    .join(' ');

  return (
    <svg className="trend-chart" viewBox={`0 0 ${W} ${H}`} role="img" aria-label="Test trends">
      {[0, 25, 50, 75, 100].map((g) => (
        <g key={g}>
          <line x1={padX} x2={W - padX} y1={yRate(g)} y2={yRate(g)} className="grid" />
          <text x={4} y={yRate(g) + 4} className="axis">
            {g}%
          </text>
        </g>
      ))}
      {points.map((p, i) => {
        const passedH = padY + innerH - yBar(p.passed);
        const failedH = padY + innerH - yBar(p.failed);
        return (
          <g key={p.day}>
            <rect x={x(i)} y={yBar(p.passed)} width={barW} height={passedH} className="bar-pass" />
            <rect
              x={x(i)}
              y={yBar(p.passed) - failedH}
              width={barW}
              height={failedH}
              className="bar-fail"
            />
          </g>
        );
      })}
      {linePts && <polyline points={linePts} className="rate-line" />}
    </svg>
  );
}