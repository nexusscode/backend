package org.nexusscode.backend.security.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.repository.RedisRepository;
import org.nexusscode.backend.security.dto.TokenResponseDTO;
import org.nexusscode.backend.security.jwt.JWTProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
public class APIRefreshController {
    private final JWTProvider jwtProvider;
    private final RedisRepository redisRepository;

    @RequestMapping("/api/member/refresh")
    public ResponseEntity<CommonResponse<TokenResponseDTO>> refresh(
            @RequestHeader("Authorization") String authHeader,
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }

        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }

        String accessToken = authHeader.substring(7);

        if (!checkExpiredToken(accessToken)) {
            return ResponseEntity.ok(new CommonResponse<>("accessToken이 아직 만료되지 않았습니다..", 200, new TokenResponseDTO(accessToken)));
        }

        Map<String, Object> claims = jwtProvider.validateToken(refreshToken);

        String newAccessToken = jwtProvider.generateAccessToken(claims);

        if (checkTime((Integer) claims.get("exp")) == true) {
            String newRefreshToken = jwtProvider.generateRefreshToken(claims);

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(Duration.ofDays(1))
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        }

        return ResponseEntity.ok(new CommonResponse<>("accessToken이 교체되었습니다.", 200, new TokenResponseDTO(newAccessToken)));
    }

    private boolean checkTime(Integer exp) {
        Date expDate = new Date((long) exp * (1000));

        long gap = expDate.getTime() - System.currentTimeMillis();

        long leftMin = gap / (1000 * 60);

        return leftMin < 60;
    }

    private boolean checkExpiredToken(String token) {
        try {
            jwtProvider.validateToken(token);
            return false;
        }catch(CustomException e){
            return e.getMessage() == ErrorCode.EXPIRED_JWT.getMessage();
        }
    }
}
