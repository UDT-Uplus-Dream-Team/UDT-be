package com.example.udtbe.global.security.handler;

import com.example.udtbe.global.exception.ErrorResponse;
import com.example.udtbe.global.security.exception.SecurityErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final String ROLE_GUEST = "ROLE_GUEST";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private static boolean matchAuthenticationFromRole(Authentication authentication, String role) {
        String authRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        return Objects.equals(authRole, role);
    }

    private static void setUpResponse(
            HttpServletResponse response,
            SecurityErrorCode securityErrorCode
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                securityErrorCode.getMessage(),
                securityErrorCode.getHttpStatus().name()
        );

        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.error(authentication.getName());
        // 사용자 권한에 따라 다른 응답 제공
        if (!Objects.isNull(accessDeniedException)) {
            if (!matchAuthenticationFromRole(authentication, ROLE_USER)) {
                // ROLE_USER 권한이 없는 경우
                log.info(SecurityErrorCode.FORBIDDEN_USER.getMessage());
                setUpResponse(response, SecurityErrorCode.FORBIDDEN_USER);
            } else if (!matchAuthenticationFromRole(authentication, ROLE_GUEST)) {
                // ROLE_GUEST 권한이 없는 경우
                log.info(SecurityErrorCode.FORBIDDEN_GUEST.getMessage());
                setUpResponse(response, SecurityErrorCode.FORBIDDEN_GUEST);
            } else if (!matchAuthenticationFromRole(authentication, ROLE_ADMIN)) {
                // ROLE_GUEST 권한이 없는 경우
                log.info(SecurityErrorCode.FORBIDDEN_GUEST.getMessage());
                setUpResponse(response, SecurityErrorCode.FORBIDDEN_GUEST);
            } else {
                // 기타 권한이 없는 경우
                log.info(SecurityErrorCode.FORBIDDEN_MISMATCH.getMessage());
                setUpResponse(response, SecurityErrorCode.FORBIDDEN_MISMATCH);
            }
        } else {
            log.info(SecurityErrorCode.FORBIDDEN_MISMATCH.getMessage());
            setUpResponse(response, SecurityErrorCode.FORBIDDEN_MISMATCH);
        }
    }
}
