package org.nexusscode.backend.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.security.jwt.JWTProvider;
import org.nexusscode.backend.user.domain.MemberRole;
import org.nexusscode.backend.user.dto.UserDTO;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {
    private final JWTProvider jwtProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> EXCLUDE_URLS = List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/configuration/**",
             "/internal-actuator/health/**",
             //커뮤니티나 인증 필요없는 부분 나중에 수정하기

            "/api/user/signup",
            "/api/user/kakao",
            "/api/user/kakao/login-link",
            "/api/user/find/email",
            "/api/user/find/password",
            "/api/user/check-email",
            "/api/user/login"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        return EXCLUDE_URLS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("---------- JWTCheckFilter ----------");

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Authorization 헤더 없음 또는 형식 불일치");
            request.setAttribute("customException", new CustomException(ErrorCode.NO_AUTH_HEADER));
            throw new BadCredentialsException("NO_AUTH_HEADER");
        }

        try {
            String token = authHeader.substring(7);
            Map<String, Object> claims = jwtProvider.validateToken(token);

            log.info("JWT Claims: {}", claims);

            Long userId = ((Number) claims.get("userId")).longValue();
            String email = (String) claims.get("email");
            String password = (String) claims.get("password");
            String name = (String) claims.get("name");
            Boolean social = (Boolean) claims.get("social");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            List<GrantedAuthority> authorities = roleNames.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UserDTO memberDTO = new UserDTO(userId, email, password, name, social, roleNames);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(memberDTO, password, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (CustomException e) {
            log.error("JWT 검증 실패: {}", e.getMessage());
            request.setAttribute("customException", e);
            throw new BadCredentialsException("INVALID_TOKEN");
        } catch (Exception e) {
            request.setAttribute("customException", new CustomException(ErrorCode.JWT_VALIDATION_ERROR));
            throw new BadCredentialsException("JWT_PROCESSING_ERROR");
        }
    }
}
