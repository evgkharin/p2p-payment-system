package com.p2p.payment.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseHealthCheck implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) {
        checkPostgreSQL();
        checkRedis();
    }

    private void checkPostgreSQL() {
        try {
            String result = jdbcTemplate.queryForObject(
                    "SELECT current_database()",
                    String.class
            );
            log.info("✅ PostgreSQL connection successful! Database: {}", result);
        } catch (Exception e) {
            log.error("❌ PostgreSQL connection failed: {}", e.getMessage());
        }
    }

    private void checkRedis() {
        try {
            // Тестовая запись
            String testKey = "health:check";
            String testValue = "OK";

            redisTemplate.opsForValue().set(testKey, testValue);
            String result = (String) redisTemplate.opsForValue().get(testKey);

            if (testValue.equals(result)) {
                log.info("✅ Redis connection successful! Test key: {}", testKey);
                redisTemplate.delete(testKey); // Удаляем тестовый ключ
            }
        } catch (Exception e) {
            log.error("❌ Redis connection failed: {}", e.getMessage());
        }
    }
}