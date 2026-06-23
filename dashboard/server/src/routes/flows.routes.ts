import { Router } from 'express';
import { z } from 'zod';
import { requireAuth } from '../auth.js';
import { githubConfigured } from '../config.js';
import {
  FLOWS,
  findFlow,
  SUPPORTED_BROWSERS,
  SUPPORTED_ENVS,
} from '../flows.config.js';
import { dispatchWorkflow, listRecentRuns, GitHubError } from '../github.js';

export const flowsRouter = Router();
flowsRouter.use(requireAuth);

// GET /api/flows — the predefined flows plus valid env/browser options.
flowsRouter.get('/', (_req, res) => {
  res.json({
    flows: FLOWS,
    browsers: SUPPORTED_BROWSERS,
    environments: SUPPORTED_ENVS,
    executionEnabled: githubConfigured(),
  });
});

// GET /api/flows/runs — recent CI runs of the pipeline (live status from GitHub).
flowsRouter.get('/runs', async (_req, res) => {
  if (!githubConfigured()) {
    res.status(503).json({ error: 'GitHub dispatch is not configured (set GITHUB_TOKEN).' });
    return;
  }
  try {
    res.json({ runs: await listRecentRuns() });
  } catch (err) {
    handleGitHubError(err, res);
  }
});

const runSchema = z.object({
  env: z.enum(SUPPORTED_ENVS).default('staging'),
  browser: z.enum(SUPPORTED_BROWSERS).default('chrome'),
});

// POST /api/flows/:key/run — dispatch a flow as a GitHub Actions run.
flowsRouter.post('/:key/run', async (req, res) => {
  if (!githubConfigured()) {
    res.status(503).json({ error: 'GitHub dispatch is not configured (set GITHUB_TOKEN).' });
    return;
  }

  const flow = findFlow(req.params.key);
  if (!flow) {
    res.status(404).json({ error: `Unknown flow: ${req.params.key}` });
    return;
  }

  const parsed = runSchema.safeParse(req.body ?? {});
  if (!parsed.success) {
    res.status(400).json({ error: 'Invalid env or browser' });
    return;
  }

  try {
    // Restrict concurrency: verify if any workflow run is currently in_progress or queued.
    // NOTE: As execution resources grow, this can be increased, parameterized, or replaced with a queuing mechanism.
    const recent = await listRecentRuns(5);
    const activeRun = recent.find((r) => r.status === 'in_progress' || r.status === 'queued' || r.status === 'waiting');
    if (activeRun) {
      res.status(429).json({
        error: `A test execution (Run #${activeRun.runNumber}) is already in progress. Concurrent runs are restricted to prevent rate limits.`,
      });
      return;
    }

    await dispatchWorkflow({
      env: parsed.data.env,
      browser: parsed.data.browser,
      tags: flow.tags,
    });
    // workflow_dispatch is fire-and-forget; the client polls /api/flows/runs.
    res.status(202).json({
      message: `Dispatched ${flow.name}`,
      flow: flow.key,
      env: parsed.data.env,
      browser: parsed.data.browser,
      tags: flow.tags,
    });
  } catch (err) {
    handleGitHubError(err, res);
  }
});

function handleGitHubError(err: unknown, res: import('express').Response): void {
  if (err instanceof GitHubError) {
    console.error('[github]', err.message, err.detail);
    res.status(502).json({ error: 'GitHub API error', status: err.status });
    return;
  }
  console.error('[flows] unexpected error', err);
  res.status(500).json({ error: 'Internal error' });
}