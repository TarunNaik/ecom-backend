package com.example.ecommerce.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


@Configuration
public class DBInitializer implements EnvironmentPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DBInitializer.class);

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;
    @Value("${spring.datasource.password}")
    private String datasourcePassword;


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String serverUrl = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");
        String bootStrapUrl = deriveBootstrapUrl(serverUrl);
        String dbName = serverUrl.substring(serverUrl.lastIndexOf("/") + 1);
        try (Connection conn = DriverManager.getConnection(bootStrapUrl, username, password);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE " + dbName);
            logger.info("✅ Database '" + dbName + "' created or already exists.");

        } catch (Exception e) {
            if (e.getMessage().contains("already exists")) {
                logger.error("ℹ️ Database '" + dbName + "' already exists.");
            } else {
                logger.error("❌ Failed to create database: " + e.getMessage());
                throw new RuntimeException("Database creation failed", e);
            }
        }

    }

    private String deriveBootstrapUrl(String fullUrl) {
        // Example: jdbc:postgresql://localhost:5432/ecommerce → jdbc:postgresql://localhost:5432/postgres
        int lastSlashIndex = fullUrl.lastIndexOf('/');
        if (lastSlashIndex == -1) {
            logger.error("Invalid JDBC URL format: " + fullUrl);
            throw new IllegalArgumentException("Invalid JDBC URL format: " + fullUrl);
        }
        return fullUrl.substring(0, lastSlashIndex + 1) + "postgres";
    }

}
