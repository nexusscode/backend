package org.nexusscode.backend.security.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Log4j2
public class RedisRefreshTokenRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private String getRefreshKey(String userId) {
        return "refresh:" + userId;
    }

    public void saveRefreshToken(String userId, String refreshToken, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(getRefreshKey(userId), refreshToken, ttl);
            log.info("Saved refresh token for {} with TTL {}", userId, ttl);
        } catch (Exception e) {
            log.error("Failed to save refresh token for userId: {}", userId, e);
        }
    }

    public Optional<String> getRefreshToken(String userId) {
        try {
            Object value = redisTemplate.opsForValue().get(getRefreshKey(userId));
            if (value instanceof String token) {
                return Optional.of(token);
            }
            log.warn("Refresh token is not a string for userId: {}", userId);
        } catch (Exception e) {
            log.error("Failed to get refresh token for userId: {}", userId, e);
        }
        return Optional.empty();
    }

    public void deleteRefreshToken(String userId) {
        try {
            redisTemplate.delete(getRefreshKey(userId));
            log.info("Deleted refresh token for userId: {}", userId);
        } catch (Exception e) {
            log.error("Failed to delete refresh token for userId: {}", userId, e);
        }
    }
}
