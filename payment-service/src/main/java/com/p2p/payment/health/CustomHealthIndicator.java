package com.p2p.payment.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();

        // Проверка PostgreSQL
        boolean postgresUp = checkPostgreSQL(details);

        // Проверка Redis
        boolean redisUp = checkRedis(details);

        // Если хотя бы один DOWN — весь статус DOWN
        if (postgresUp && redisUp) {
            return Health.up().withDetails(details).build();
        } else {
            return Health.down().withDetails(details).build();
        }
    }

    private boolean checkPostgreSQL(Map<String, Object> details) {
        try {
            String database = jdbcTemplate.queryForObject(
                    "SELECT current_database()",
                    String.class
            );

            Integer connectionCount = jdbcTemplate.queryForObject(
                    "SELECT count(*) FROM pg_stat_activity WHERE datname = current_database()",
                    Integer.class
            );

            details.put("postgresql.status", "UP");
            details.put("postgresql.database", database);
            details.put("postgresql.connections", connectionCount);
            return true;
        } catch (Exception e) {
            details.put("postgresql.status", "DOWN");
            details.put("postgresql.error", e.getMessage());
            return false;
        }
    }

    private boolean checkRedis(Map<String, Object> details) {
        try {
            String testKey = "health:ping";
            redisTemplate.opsForValue().set(testKey, "pong");
            String response = (String) redisTemplate.opsForValue().get(testKey);
            redisTemplate.delete(testKey);

            if ("pong".equals(response)) {
                details.put("redis.status", "UP");
                details.put("redis.response_time_ms", "<1");
                return true;
            } else {
                details.put("redis.status", "DOWN");
                details.put("redis.error", "Unexpected response: " + response);
                return false;
            }
        } catch (Exception e) {
            details.put("redis.status", "DOWN");
            details.put("redis.error", e.getMessage());
            return false;
        }
    }
}