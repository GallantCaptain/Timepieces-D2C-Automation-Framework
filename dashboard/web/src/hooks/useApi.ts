import { useCallback, useEffect, useState } from 'react';
import { ApiError } from '../api/client';

interface ApiState<T> {
  data: T | null;
  error: string | null;
  loading: boolean;
  reload: () => void;
}

/** Fetches once on mount (and when `deps` change); exposes a manual reload. */
export function useApi<T>(fetcher: () => Promise<T>, deps: unknown[] = []): ApiState<T> {
  const [data, setData] = useState<T | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [nonce, setNonce] = useState(0);

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const run = useCallback(fetcher, deps);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError(null);
    run()
      .then((d) => active && setData(d))
      .catch((e) => active && setError(e instanceof ApiError ? e.message : 'Failed to load.'))
      .finally(() => active && setLoading(false));
    return () => {
      active = false;
    };
  }, [run, nonce]);

  return { data, error, loading, reload: () => setNonce((n) => n + 1) };
}