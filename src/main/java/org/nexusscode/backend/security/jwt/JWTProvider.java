package org.nexusscode.backend.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class JWTProvider {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        try {
            return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Secret Key", e);
        }
    }

    public String generateAccessToken(Map<String, Object> claims) {
        return generateToken(claims, jwtProperties.getAccessExpireMin());
    }

    public String generateRefreshToken(Map<String, Object> claims) {
        return generateToken(claims, jwtProperties.getRefreshExpireMin());
    }

    private String generateToken(Map<String, Object> claims, int expireMinutes) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofMinutes(expireMinutes));

        return Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Map<String, Object> validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw new CustomException(ErrorCode.MALFORMED_JWT);

        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            throw new CustomException(ErrorCode.EXPIRED_JWT);

        } catch (InvalidClaimException e) {
            log.warn("Invalid JWT claims: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_CLAIM);

        } catch (JwtException e) {
            log.warn("JWT validation error: {}", e.getMessage());
            throw new CustomException(ErrorCode.JWT_VALIDATION_ERROR);
        }
    }

}
