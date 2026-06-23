import { config } from './config.js';

const API = 'https://api.github.com';

function headers() {
  return {
    Accept: 'application/vnd.github+json',
    Authorization: `Bearer ${config.github.token}`,
    'X-GitHub-Api-Version': '2022-11-28',
    'Content-Type': 'application/json',
  };
}

export interface DispatchInputs {
  env: string;
  browser: string;
  tags: string;
  ref?: string;
}

/**
 * Triggers test-pipeline.yml via the workflow_dispatch API.
 * GitHub returns 204 with no body and no run id, so callers should poll
 * listRecentRuns() to surface the freshly-created run.
 */
export async function dispatchWorkflow(inputs: DispatchInputs): Promise<void> {
  const { owner, repo, workflowFile, defaultRef } = config.github;
  const url = `${API}/repos/${owner}/${repo}/actions/workflows/${workflowFile}/dispatches`;

  const res = await fetch(url, {
    method: 'POST',
    headers: headers(),
    body: JSON.stringify({
      ref: inputs.ref ?? defaultRef,
      inputs: { env: inputs.env, browser: inputs.browser, tags: inputs.tags },
    }),
  });

  if (res.status !== 204) {
    const detail = await res.text().catch(() => '');
    throw new GitHubError(
      `workflow_dispatch failed (${res.status})`,
      res.status,
      detail.slice(0, 500),
    );
  }
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

export async function listRecentRuns(perPage = 15): Promise<WorkflowRun[]> {
  const { owner, repo, workflowFile } = config.github;
  const url = `${API}/repos/${owner}/${repo}/actions/workflows/${workflowFile}/runs?per_page=${perPage}`;

  const res = await fetch(url, { headers: headers() });
  if (!res.ok) {
    const detail = await res.text().catch(() => '');
    throw new GitHubError(`list runs failed (${res.status})`, res.status, detail.slice(0, 500));
  }

  const body = (await res.json()) as { workflow_runs?: RawRun[] };
  return (body.workflow_runs ?? []).map((r) => ({
    id: r.id,
    runNumber: r.run_number,
    status: r.status,
    conclusion: r.conclusion,
    event: r.event,
    htmlUrl: r.html_url,
    createdAt: r.created_at,
    displayTitle: r.display_title,
  }));
}

interface RawRun {
  id: number;
  run_number: number;
  status: string | null;
  conclusion: string | null;
  event: string;
  html_url: string;
  created_at: string;
  display_title: string;
}

export class GitHubError extends Error {
  constructor(
    message: string,
    public readonly status: number,
    public readonly detail: string,
  ) {
    super(message);
    this.name = 'GitHubError';
  }
}

/**
 * Downloads the private zip archive artifact for a given GitHub Action run number.
 */
export async function downloadArtifact(runNumber: number): Promise<Buffer> {
  const { owner, repo } = config.github;
  const listUrl = `${API}/repos/${owner}/${repo}/actions/artifacts?name=allure-report-${runNumber}`;

  const res = await fetch(listUrl, { headers: headers() });
  if (!res.ok) {
    const detail = await res.text().catch(() => '');
    throw new GitHubError(
      `Failed to list artifacts for run #${runNumber} (${res.status})`,
      res.status,
      detail.slice(0, 500),
    );
  }

  const body = (await res.json()) as { artifacts?: { archive_download_url: string }[] };
  const artifact = body.artifacts?.[0];
  if (!artifact) {
    throw new GitHubError(`Allure report artifact not found for run #${runNumber}`, 404, 'No matching artifact name');
  }

  const downloadRes = await fetch(artifact.archive_download_url, {
    headers: headers(),
  });

  if (!downloadRes.ok) {
    const detail = await downloadRes.text().catch(() => '');
    throw new GitHubError(
      `Failed to download artifact for run #${runNumber} (${downloadRes.status})`,
      downloadRes.status,
      detail.slice(0, 500),
    );
  }

  const arrayBuffer = await downloadRes.arrayBuffer();
  return Buffer.from(arrayBuffer);
}