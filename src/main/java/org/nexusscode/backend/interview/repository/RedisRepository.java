package org.nexusscode.backend.interview.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public <HK, HV> void putHash(String key, HK hashKey, HV value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey.toString(), value);
        } catch (Exception e) {
            log.error("Failed to put hash entry for key: {}, hashKey: {}", key, hashKey, e);
        }
    }

    public <T> Optional<T> getHash(String key, Object hashKey, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForHash().get(key, String.valueOf(hashKey));
            if (clazz.isInstance(value)) {
                return Optional.of(clazz.cast(value));
            }
        } catch (Exception e) {
            log.error("Failed to get hash value for key: {}, hashKey: {}", key, hashKey, e);
        }
        return Optional.empty();
    }

    public void expire(String key, Duration ttl) {
        redisTemplate.expire(key, ttl);
    }

    public void deleteHashKey(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Failed to delete hash key: {}", key, e);
        }
    }

}
