package org.nexusscode.backend.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.common.CommonErrorResponse;
import org.nexusscode.backend.global.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler{

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        CustomException customException = (CustomException) request.getAttribute("customException");

        HttpStatus status = HttpStatus.FORBIDDEN;
        String message = "접근 권한이 없습니다.";
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
