package org.nexusscode.backend.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.security.jwt.JWTProvider;
import org.nexusscode.backend.security.repository.RedisRefreshTokenRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Log4j2
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;


    private static final String REFRESH_ENDPOINT = "/api/member/refresh";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals(REFRESH_ENDPOINT);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                    .filter(c -> "refreshToken".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_INVALID));

            Map<String, Object> claims = jwtProvider.validateToken(refreshToken);

            String userId = (String) claims.get("userId");

            String storedToken = redisRefreshTokenRepository.getRefreshToken(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

            if (!storedToken.equals(refreshToken)) {
                throw new CustomException(ErrorCode.TOKEN_TAMPERED);
            }

            filterChain.doFilter(request, response);

        } catch (CustomException e) {
            log.warn("RefreshTokenFilter - 인증 실패: {}", e.getMessage());
            request.setAttribute("customException", e);
            throw new BadCredentialsException("INVALID_REFRESH_TOKEN");
        } catch (Exception e) {
            log.error("RefreshTokenFilter - 처리 중 예외 발생", e);
            request.setAttribute("customException", new CustomException(ErrorCode.JWT_VALIDATION_ERROR));
            throw new BadCredentialsException("REFRESH_FILTER_ERROR");
        }
    }
}
