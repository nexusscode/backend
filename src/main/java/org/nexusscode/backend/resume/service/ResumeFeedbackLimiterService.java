package org.nexusscode.backend.resume.service;

import java.time.Duration;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeFeedbackLimiterService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final int DAILY_LIMIT = 20;

    public void checkLimit(Long userId) {
        String key = "resume_ai_feedback:" + userId + ":" + LocalDate.now();

        // 현재까지 요청 횟수 조회
        Long count = (Long) redisTemplate.opsForValue().get(key);
        if (count == null) count = 0L;

        // 호출 횟수가 제한 초과이면 예외 발생
        if (count > DAILY_LIMIT) {
            throw new CustomException(ErrorCode.API_RATE_LIMIT_EXCEEDED);
        }

        // 호출 횟수 증가 (카운트 +1)
        Long newCount = redisTemplate.opsForValue().increment(key);

        // 키가 새로 생성되었다면, 만료시간도 설정 (처음에만)
        if (newCount == 1L) {
            redisTemplate.expire(key, Duration.ofDays(1));
        }
    }
}

