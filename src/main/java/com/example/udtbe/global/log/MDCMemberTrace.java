package com.example.udtbe.global.log;

import com.example.udtbe.domain.auth.service.AuthQuery;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.security.dto.CustomOauth2User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Order(Ordered.LOWEST_PRECEDENCE)
@Component
@RequiredArgsConstructor
class MDCMemberTrace implements Filter {

    private final AuthQuery authQuery;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                Long memberId = null;

                if (principal instanceof Member member) {
                    memberId = member.getId();
                } else if (principal instanceof CustomOauth2User oauth2User) {
                    try {
                        String email = oauth2User.getEmail();
                        Member member = authQuery.getMemberByEmail(email);
                        memberId = member.getId();
                    } catch (Exception e) {
                        // OAuth2 사용자 조회 실패 시 로그만 남기고 계속 진행
                        // 로깅을 위한 필터이므로 예외가 발생해도 요청 처리를 중단하지 않음
                    }
                }

                if (memberId != null) {
                    MDC.put("memberId", String.valueOf(memberId));
                }
            }
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}