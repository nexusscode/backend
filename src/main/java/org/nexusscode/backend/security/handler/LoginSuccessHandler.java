package org.nexusscode.backend.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.security.dto.TokenResponseDTO;
import org.nexusscode.backend.security.jwt.JWTProvider;
import org.nexusscode.backend.security.repository.RedisRefreshTokenRepository;
import org.nexusscode.backend.user.dto.UserDTO;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        log.info("--------------------------------------------- ");
        log.info(authentication.getName());
        log.info("--------------------------------------------- ");

        UserDTO memberDTO = (UserDTO) authentication.getPrincipal();
        Map<String, Object> claims = memberDTO.getClaims();

        String accessToken = jwtProvider.generateAccessToken(claims);
        String refreshToken = jwtProvider.generateRefreshToken(claims);

        String userId = (String) claims.get("userId");
        redisRefreshTokenRepository.saveRefreshToken(userId, refreshToken, Duration.ofDays(1));

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(1))
                .build();

        response.setHeader("Set-Cookie", refreshCookie.toString());

        TokenResponseDTO tokenResponse = new TokenResponseDTO(accessToken);

        response.setContentType("application/json; charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), tokenResponse);
    }
}

