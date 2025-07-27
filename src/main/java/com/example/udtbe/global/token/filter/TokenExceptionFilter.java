package com.example.udtbe.global.token.filter;

import com.example.udtbe.global.exception.ErrorResponse;
import com.example.udtbe.global.token.exception.TokenErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class TokenExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {

            log.warn("JWT 검증 실패로 인한 예외 발생 : {}", e.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            ErrorResponse errorResponse = new ErrorResponse(
                    TokenErrorCode.INVALID_TOKEN.getMessage(),
                    TokenErrorCode.INVALID_TOKEN.getHttpStatus().name());

            String jsonResponse = objectMapper.writeValueAsString(errorResponse);

            response.getWriter().write(jsonResponse);
        }
    }
}
