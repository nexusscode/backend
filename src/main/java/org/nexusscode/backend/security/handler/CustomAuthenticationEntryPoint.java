package org.nexusscode.backend.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nexusscode.backend.global.common.CommonErrorResponse;
import org.nexusscode.backend.global.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        CustomException customException = (CustomException) request.getAttribute("customException");

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String message = "인증이 필요합니다.";
        String errorCode = status.getReasonPhrase();

        if (customException != null) {
            status = customException.getStatusCode();
            message = customException.getMessage();
            errorCode = status.getReasonPhrase();
        }

        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .message(message)
                .error(errorCode)
                .statusCode(status.value())
                .timestamp(LocalDateTime.now())
                .build();

        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
