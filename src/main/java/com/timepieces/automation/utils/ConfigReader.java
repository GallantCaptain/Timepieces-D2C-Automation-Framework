package com.timepieces.automation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigReader {
    
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static Properties prop;

    // Static initialization block runs exactly once when the class is first loaded into memory
    static {
        loadFrameworkConfiguration();
    }

    private static synchronized void loadFrameworkConfiguration() {
        if (prop == null) {
            prop = new Properties();

            // Step 1: Detect environment profile passed from Maven CLI (e.g., -Denv=staging). Defaults to staging.
            String env = System.getProperty("env", "staging").trim().toLowerCase();
            String configFile = "config/" + env + ".properties";
            String localOverrideFile = "config/" + env + ".local.properties";

            logger.info("Initializing framework configuration manager for environment target: [{}]", env);

            // Step 2: Read target properties file from classpath once into memory
            try (InputStream fis = ConfigReader.class.getClassLoader().getResourceAsStream(configFile)) {
                if (fis == null) {
                    logger.fatal("CRITICAL: Configuration deployment file not found on classpath: {}", configFile);
                    throw new RuntimeException("Target environment configuration profile not found: " + configFile);
                }
                prop.load(fis);
                logger.info("Successfully cached configuration properties from source: [{}]", configFile);
            } catch (IOException e) {
                logger.error("Failed to compile properties input stream for profile: {}", configFile, e);
                throw new RuntimeException("Property memory hydration failed for: " + configFile, e);
            }

            // Step 3: If a gitignored <env>.local.properties exists on the classpath, layer its
            // keys on top of the base file. Lets developers keep secrets (e.g., staging.password)
            // out of source control without changing committed config.
            try (InputStream local = ConfigReader.class.getClassLoader().getResourceAsStream(localOverrideFile)) {
                if (local != null) {
                    prop.load(local);
                    logger.info("Merged local override profile: [{}]", localOverrideFile);
                }
            } catch (IOException e) {
                logger.warn("Failed to load local override profile [{}]; continuing with base config only.",
                        localOverrideFile, e);
            }
        }
    }

    /**
     * Resolves configuration queries across a 3-tier precedence ladder.
     * Precedence: 1. Runtime JVM Arguments (-D) -> 2. System Environment Variables -> 3. Target Properties File
     * @param key The target configuration parameter key
     * @return The trimmed configuration string value, or null if unmapped
     */
    public static String get(String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }
        
        String cleanKey = key.trim();

        // Tier 1: Check Runtime Command-Line System Overrides (e.g., mvn test -Dbrowser=firefox)
        String value = System.getProperty(cleanKey);
        if (value != null) {
            return value.trim();
        }

        // Tier 2: Check OS Environment Configurations (e.g., local machine variables like BRAVE_PATH)
        value = System.getenv(cleanKey);
        if (value != null) {
            return value.trim();
        }

        // Tier 3: Fall back to memory cache built from properties configurations
        value = prop.getProperty(cleanKey);
        if (value != null) {
            return value.trim();
        }

        logger.warn("Configuration key lookup requested but returned empty/unmapped: [{}]", cleanKey);
        return null;
    }
}