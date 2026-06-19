-- Dashboard schema. Safe to run repeatedly (idempotent).

-- Mirrors the table created by the Java TestMetricsPublisher so the dashboard
-- can boot and query even before the first test run has published anything.
CREATE TABLE IF NOT EXISTS test_runs (
    id SERIAL PRIMARY KEY,
    build_id VARCHAR(50),
    environment VARCHAR(50),
    run_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    browser VARCHAR(50),
    total_scenarios INT,
    passed_scenarios INT,
    failed_scenarios INT,
    duration_seconds INT
);

CREATE INDEX IF NOT EXISTS idx_test_runs_timestamp ON test_runs (run_timestamp DESC);

-- Authorized dashboard users.
CREATE TABLE IF NOT EXISTS dashboard_users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);