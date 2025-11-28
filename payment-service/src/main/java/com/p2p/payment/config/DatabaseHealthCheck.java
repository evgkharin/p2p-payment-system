package com.p2p.payment.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseHealthCheck implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            String result = jdbcTemplate.queryForObject(
                    "SELECT current_database()",
                    String.class
            );
            log.info("✅ PostgreSQL connection successful! Database: {}", result);

            // Проверка версии PostgreSQL
            String version = jdbcTemplate.queryForObject(
                    "SELECT version()",
                    String.class
            );
            log.info("📊 PostgreSQL version: {}", version);

        } catch (Exception e) {
            log.error("❌ PostgreSQL connection failed: {}", e.getMessage());
        }
    }
}