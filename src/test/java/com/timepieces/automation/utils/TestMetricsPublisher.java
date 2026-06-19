package com.timepieces.automation.utils;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TestMetricsPublisher {

    public static void publish(String buildId, String env, String browser, int total, int passed, int failed, int durationSeconds) {
        String dbUrl = System.getenv("NEON_DATABASE_URL");
        if (dbUrl == null || dbUrl.isEmpty()) {
            dbUrl = System.getenv("DATABASE_URL");
        }
        
        // Fallback to local Docker postgres if no environment variable is provided
        if (dbUrl == null || dbUrl.isEmpty()) {
            log.warn("Database connection string (NEON_DATABASE_URL / DATABASE_URL) not found in environment. Defaulting to local dev PostgreSQL.");
            dbUrl = "jdbc:postgresql://localhost:5432/timepieces_metrics?user=postgres&password=devpassword";
        }

        Connection conn = null;
        try {
            String jdbcUrl;
            String user = null;
            String password = null;

            if (dbUrl.startsWith("jdbc:")) {
                jdbcUrl = dbUrl;
            } else if (dbUrl.startsWith("postgres://") || dbUrl.startsWith("postgresql://")) {
                URI uri = new URI(dbUrl);
                String host = uri.getHost();
                int port = uri.getPort();
                if (port == -1) {
                    port = 5432;
                }
                String path = uri.getPath();
                String userInfo = uri.getUserInfo();
                
                jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                if (uri.getQuery() != null) {
                    jdbcUrl += "?" + uri.getQuery();
                }
                
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    user = parts[0];
                    password = parts[1];
                }
            } else {
                throw new IllegalArgumentException("Unsupported database connection URL format: " + dbUrl);
            }

            log.info("Connecting to metrics database...");
            if (user != null && password != null) {
                conn = DriverManager.getConnection(jdbcUrl, user, password);
            } else {
                conn = DriverManager.getConnection(jdbcUrl);
            }

            // Create table if not exists
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS test_runs (" +
                             "id SERIAL PRIMARY KEY," +
                             "build_id VARCHAR(50)," +
                             "environment VARCHAR(50)," +
                             "run_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                             "browser VARCHAR(50)," +
                             "total_scenarios INT," +
                             "passed_scenarios INT," +
                             "failed_scenarios INT," +
                             "duration_seconds INT" +
                             ")");
            }

            // Insert metrics
            String sql = "INSERT INTO test_runs (build_id, environment, browser, total_scenarios, passed_scenarios, failed_scenarios, duration_seconds) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, buildId);
                pstmt.setString(2, env);
                pstmt.setString(3, browser);
                pstmt.setInt(4, total);
                pstmt.setInt(5, passed);
                pstmt.setInt(6, failed);
                pstmt.setInt(7, durationSeconds);
                pstmt.executeUpdate();
                log.info("Successfully published test metrics to database.");
            }

        } catch (Exception e) {
            log.error("Failed to publish test execution metrics to database.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }
}
