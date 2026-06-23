import { Router } from 'express';
import { z } from 'zod';
import { query } from '../db.js';
import { requireAuth } from '../auth.js';
import { existsSync } from 'node:fs';
import { mkdir } from 'node:fs/promises';
import { join } from 'node:path';
import AdmZip from 'adm-zip';
import { downloadArtifact, GitHubError } from '../github.js';

export const runsRouter = Router();
runsRouter.use(requireAuth);

interface RunRow {
  id: number;
  build_id: string | null;
  environment: string | null;
  run_timestamp: string;
  browser: string | null;
  total_scenarios: number | null;
  passed_scenarios: number | null;
  failed_scenarios: number | null;
  duration_seconds: number | null;
}

/** Builds the secure Allure report URL for a build, if it looks like a CI run number. */
function reportUrl(buildId: string | null): string | null {
  if (!buildId || !/^\d+$/.test(buildId)) return null; // LOCAL_* runs aren't published
  return `/api/runs/${buildId}/report/index.html`;
}

function shape(row: RunRow) {
  const passed = row.passed_scenarios ?? 0;
  const total = row.total_scenarios ?? 0;
  return {
    id: row.id,
    buildId: row.build_id,
    environment: row.environment,
    browser: row.browser,
    timestamp: row.run_timestamp,
    total,
    passed,
    failed: row.failed_scenarios ?? 0,
    durationSeconds: row.duration_seconds ?? 0,
    passRate: total > 0 ? Math.round((passed / total) * 1000) / 10 : null,
    reportUrl: reportUrl(row.build_id),
  };
}

const listQuery = z.object({
  limit: z.coerce.number().int().min(1).max(200).default(50),
  offset: z.coerce.number().int().min(0).default(0),
  env: z.string().optional(),
  browser: z.string().optional(),
});

// GET /api/runs — paginated, filterable history.
runsRouter.get('/', async (req, res) => {
  const parsed = listQuery.safeParse(req.query);
  if (!parsed.success) {
    res.status(400).json({ error: 'Invalid query parameters' });
    return;
  }
  const { limit, offset, env, browser } = parsed.data;

  const where: string[] = [];
  const params: unknown[] = [];
  if (env) {
    params.push(env);
    where.push(`environment = $${params.length}`);
  }
  if (browser) {
    params.push(browser);
    where.push(`browser = $${params.length}`);
  }
  const whereSql = where.length ? `WHERE ${where.join(' AND ')}` : '';

  params.push(limit, offset);
  const { rows } = await query<RunRow>(
    `SELECT * FROM test_runs ${whereSql}
     ORDER BY run_timestamp DESC
     LIMIT $${params.length - 1} OFFSET $${params.length}`,
    params,
  );

  res.json({ runs: rows.map(shape) });
});

// GET /api/runs/trends?days=30 — daily aggregates for charting.
runsRouter.get('/trends', async (req, res) => {
  const days = z.coerce.number().int().min(1).max(365).catch(30).parse(req.query.days);

  const { rows } = await query<{
    day: string;
    runs: number;
    total: number;
    passed: number;
    failed: number;
    avg_duration: number;
  }>(
    `SELECT date_trunc('day', run_timestamp) AS day,
            COUNT(*)                          AS runs,
            COALESCE(SUM(total_scenarios), 0)  AS total,
            COALESCE(SUM(passed_scenarios), 0) AS passed,
            COALESCE(SUM(failed_scenarios), 0) AS failed,
            COALESCE(AVG(duration_seconds), 0) AS avg_duration
       FROM test_runs
      WHERE run_timestamp >= NOW() - ($1 || ' days')::interval
      GROUP BY day
      ORDER BY day ASC`,
    [days],
  );

  res.json({
    days,
    points: rows.map((r) => ({
      day: r.day,
      runs: Number(r.runs),
      total: Number(r.total),
      passed: Number(r.passed),
      failed: Number(r.failed),
      passRate:
        Number(r.total) > 0 ? Math.round((Number(r.passed) / Number(r.total)) * 1000) / 10 : null,
      avgDurationSeconds: Math.round(Number(r.avg_duration)),
    })),
  });
});

// GET /api/runs/summary — headline stats for the dashboard landing.
runsRouter.get('/summary', async (_req, res) => {
  const { rows } = await query<{
    total_runs: number;
    sum_total: number;
    sum_passed: number;
    sum_failed: number;
    last_run: string | null;
  }>(
    `SELECT COUNT(*)                          AS total_runs,
            COALESCE(SUM(total_scenarios), 0)  AS sum_total,
            COALESCE(SUM(passed_scenarios), 0) AS sum_passed,
            COALESCE(SUM(failed_scenarios), 0) AS sum_failed,
            MAX(run_timestamp)                 AS last_run
       FROM test_runs`,
  );
  const r = rows[0];
  const total = Number(r.sum_total);
  res.json({
    totalRuns: Number(r.total_runs),
    totalScenarios: total,
    passed: Number(r.sum_passed),
    failed: Number(r.sum_failed),
    passRate: total > 0 ? Math.round((Number(r.sum_passed) / total) * 1000) / 10 : null,
    lastRun: r.last_run,
  });
});

// GET /api/runs/:id — single run detail.
runsRouter.get('/:id', async (req, res) => {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) {
    res.status(400).json({ error: 'Invalid run id' });
    return;
  }
  const { rows } = await query<RunRow>('SELECT * FROM test_runs WHERE id = $1', [id]);
  if (!rows[0]) {
    res.status(404).json({ error: 'Run not found' });
    return;
  }
  res.json({ run: shape(rows[0]) });
});

const REPORTS_DIR = join(process.cwd(), 'data', 'reports');

// GET /api/runs/:buildId/report/* — secure static file server for private Allure reports.
runsRouter.get('/:buildId/report/*', async (req, res) => {
  const buildIdStr = req.params.buildId;
  const buildId = Number(buildIdStr);
  if (!Number.isInteger(buildId)) {
    res.status(400).json({ error: 'Invalid build id' });
    return;
  }

  const runDir = join(REPORTS_DIR, buildIdStr);
  const requestedPath = (req.params as Record<string, string>)['0'];

  // Check if report index file exists, if not, fetch and unzip it
  if (!existsSync(join(runDir, 'index.html'))) {
    try {
      console.log(`[reports] cache miss for run #${buildId}. Fetching from GitHub...`);
      const buffer = await downloadArtifact(buildId);

      await mkdir(runDir, { recursive: true });
      const zip = new AdmZip(buffer);
      zip.extractAllTo(runDir, true);
      console.log(`[reports] successfully cached report for run #${buildId}`);
    } catch (err) {
      if (err instanceof GitHubError) {
        console.error(`[reports] GitHub error fetching run #${buildId}:`, err.message, err.detail);
        res.status(err.status === 404 ? 404 : 502).json({
          error: err.status === 404 ? 'Report not found' : 'Failed to fetch report from GitHub API',
          detail: err.message,
        });
        return;
      }
      console.error(`[reports] error caching run #${buildId}:`, err);
      res.status(500).json({ error: 'Failed to extract Allure report' });
      return;
    }
  }

  // Serve file statically
  const filePath = join(runDir, requestedPath || 'index.html');
  if (!existsSync(filePath)) {
    res.status(404).send('Not Found');
    return;
  }

  res.sendFile(filePath);
});