import express from 'express';
import cors from 'cors';
import { config } from './config.js';
import { authRouter } from './routes/auth.routes.js';
import { runsRouter } from './routes/runs.routes.js';
import { flowsRouter } from './routes/flows.routes.js';

const app = express();

app.use(express.json());
app.use(
  cors({
    origin: config.corsOrigins,
    credentials: true,
  }),
);

app.get('/api/health', (_req, res) => {
  res.json({ status: 'ok' });
});

app.use('/api/auth', authRouter);
app.use('/api/runs', runsRouter);
app.use('/api/flows', flowsRouter);

// Centralized fallback so an unexpected throw in a handler returns JSON, not HTML.
app.use(
  (
    err: unknown,
    _req: express.Request,
    res: express.Response,
    _next: express.NextFunction,
  ) => {
    console.error('[unhandled]', err);
    res.status(500).json({ error: 'Internal server error' });
  },
);

app.listen(config.port, () => {
  console.log(`[server] Timepieces dashboard API listening on :${config.port}`);
  console.log(`[server] CORS origins: ${config.corsOrigins.join(', ')}`);
});