package com.example.udtbe.global.token.filter;

import static com.example.udtbe.global.token.exception.TokenErrorCode.INVALID_TOKEN;

import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.token.cookie.CookieUtil;
import com.example.udtbe.global.token.service.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final List<String> SKIP_URLS = Arrays.asList(
            "/error", "/favicon.ico", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html"
    );
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final TokenProvider tokenProvider;
    private final CookieUtil cookieUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        for (String pattern : SKIP_URLS) {
            if (pathMatcher.match(pattern, request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String accessToken = cookieUtil.getCookieValue(request);

        if (tokenProvider.validateToken(accessToken, new Date())) {
            // accessToken logout 여부 확인
            if (tokenProvider.verifyBlackList(accessToken)) {
                saveAuthentication(accessToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(String accessToken) {
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String resolveToken(HttpServletRequest request) {
        String accessToken = cookieUtil.getCookieValue(request);

        if (ObjectUtils.isEmpty(accessToken) || !accessToken.startsWith(TOKEN_PREFIX)) {
            throw new RestApiException(INVALID_TOKEN);
        }
        return accessToken.substring(TOKEN_PREFIX.length());
    }

}
