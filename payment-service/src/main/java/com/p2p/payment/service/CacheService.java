package com.p2p.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Сохранить значение с TTL (Time To Live)
     */
    public void set(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
        log.debug("📝 Redis SET: {} = {}, TTL: {}", key, value, ttl);
    }

    /**
     * Получить значение по ключу
     */
    public Object get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        log.debug("📖 Redis GET: {} = {}", key, value);
        return value;
    }

    /**
     * Удалить ключ
     */
    public void delete(String key) {
        redisTemplate.delete(key);
        log.debug("🗑️ Redis DELETE: {}", key);
    }

    /**
     * Проверить существование ключа
     */
    public boolean exists(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * Установить TTL для существующего ключа
     */
    public void expire(String key, Duration ttl) {
        redisTemplate.expire(key, ttl);
        log.debug("⏱️ Redis EXPIRE: {} = {}", key, ttl);
    }
}