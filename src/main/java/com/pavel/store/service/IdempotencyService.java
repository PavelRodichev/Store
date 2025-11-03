package com.pavel.store.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@AllArgsConstructor
@Slf4j
public class IdempotencyService {

    private final String IDEMPOTENCY_KEY_PREFIX = "idempotency:";
    private final StringRedisTemplate redisTemplate;
    private final Duration TTL = Duration.ofMinutes(60);

    public Long getOrderIdByKey(String key) {
        String idempotencyKey = buildIdempotencyKey(key);
        String value = redisTemplate.opsForValue().get(idempotencyKey);
        return value != null ? Long.parseLong(value) : null;
    }

    // возвращает true если ключ установлен (не существовал ранее) и false если ключ не установлен (уже существует)
    public Boolean saveKeyWithOrderId(String key, Long orderId) {
        String keyIdempotency = buildIdempotencyKey(key);
        return redisTemplate.opsForValue().setIfAbsent(keyIdempotency, String.valueOf(orderId), TTL);
    }

    // проверяем наличие ключа в redis
    public Boolean hasExistKey(String key) {
        String idempotencyKey = buildIdempotencyKey(key);
        return redisTemplate.hasKey(idempotencyKey);
    }

    private String buildIdempotencyKey(String idempotencyKey) {
        return IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
    }

}
