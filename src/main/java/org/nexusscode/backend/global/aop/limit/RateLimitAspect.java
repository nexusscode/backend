package org.nexusscode.backend.global.aop.limit;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.user.dto.UserDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(rateLimit)")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof UserDTO user)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String userId = user.getEmail();
        String methodName = joinPoint.getSignature().getName();

        String key = "rate:" + methodName + ":" + userId;

        Object raw = redisTemplate.opsForValue().get(key);
        Long count = raw != null ? ((Number) raw).longValue() : 0L;

        count++;
        redisTemplate.opsForValue().set(key, count);

        if (count == 1) {
            Duration ttl = Duration.of(rateLimit.duration(), rateLimit.timeUnit());
            redisTemplate.expire(key, ttl);
        }

        if (count > rateLimit.limit()) {
            throw new CustomException(ErrorCode.API_RATE_LIMIT_EXCEEDED);
        }

        return joinPoint.proceed();
    }
}
