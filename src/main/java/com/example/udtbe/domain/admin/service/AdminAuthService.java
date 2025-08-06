package com.example.udtbe.domain.admin.service;

import static com.example.udtbe.domain.auth.exception.AuthErrorCode.INVALID_CREDENTIALS;

import com.example.udtbe.domain.admin.dto.request.AdminSinginRequest;
import com.example.udtbe.domain.admin.entity.Admin;
import com.example.udtbe.domain.auth.exception.AuthErrorCode;
import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.security.dto.AuthInfo;
import com.example.udtbe.global.security.dto.CustomOauth2User;
import com.example.udtbe.global.token.cookie.CookieUtil;
import com.example.udtbe.global.token.service.TokenProvider;
import com.example.udtbe.global.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private static final String BLACKLIST = "black_list_token";
    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private final AdminQuery adminQuery;
    private final TokenProvider tokenProvider;
    private final CookieUtil cookieUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    @Transactional
    public void signin(AdminSinginRequest request, HttpServletResponse response) {
        Admin findAdmin = adminQuery.getAdmin(request.email());

        if (!passwordEncoder.matches(request.password(), findAdmin.getPassword())) {
            throw new RestApiException(INVALID_CREDENTIALS);
        }

        AuthInfo authInfo = AuthInfo.of(
                findAdmin.getName(), findAdmin.getEmail(), findAdmin.getRole()
        );
        CustomOauth2User customOauth2User = new CustomOauth2User(authInfo);

        String accessToken = tokenProvider.generateAccessToken(findAdmin, customOauth2User,
                new Date());
        tokenProvider.generateRefreshToken(findAdmin, customOauth2User, new Date());

        findAdmin.updateLastLoginAt(LocalDateTime.now());

        response.addCookie(cookieUtil.createCookie(accessToken));
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = cookieUtil.getCookieValue(request);

        if (!tokenProvider.validateToken(accessToken, new Date())) {
            throw new RestApiException(AuthErrorCode.UNAUTHORIZED_TOKEN);
        }

        Authentication authentication = tokenProvider.getAdminAuthentication(accessToken);
        Admin admin = (Admin) authentication.getPrincipal();

        deleteRefreshTokenIfExists(admin.getEmail());
        addToBlacklist(accessToken);
        cookieUtil.deleteCookie(response);
    }

    private void deleteRefreshTokenIfExists(String email) {
        String refreshKey = getRefreshTokenPrefix(email);
        String value = redisUtil.getValues(refreshKey);
        if (StringUtils.hasText(value)) {
            redisUtil.deleteValues(refreshKey);
        }
    }

    private void addToBlacklist(String accessToken) {
        Long expiration = tokenProvider.getExpiration(accessToken, new Date());
        redisUtil.setValues(accessToken, BLACKLIST, Duration.ofMillis(expiration));

        if (!BLACKLIST.equals(redisUtil.getValues(accessToken))) {
            throw new RestApiException(AuthErrorCode.LOGOUT_FAILED);
        }
    }

    private String getRefreshTokenPrefix(String email) {
        return REFRESH_TOKEN_PREFIX + email;
    }
}