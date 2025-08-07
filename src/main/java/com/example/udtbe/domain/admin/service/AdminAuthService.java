package com.example.udtbe.domain.admin.service;

import static com.example.udtbe.domain.auth.exception.AuthErrorCode.INVALID_CREDENTIALS;

import com.example.udtbe.domain.admin.dto.request.AdminSigninRequest;
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
import java.util.Objects;
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
    public void signin(AdminSigninRequest request, HttpServletResponse response) {
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

    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = cookieUtil.getCookieValue(request);

        if (Objects.isNull(accessToken)) {
            throw new RestApiException(AuthErrorCode.MISSING_ACCESS_TOKEN);
        }

        validateAccessDeniedToken(accessToken);

        Admin findAdmin = tokenProvider.getAdminAllowExpired(accessToken);
        String refreshKey = getRefreshTokenPrefix(findAdmin.getEmail());

        validateRefreshToken(refreshKey);
        redisUtil.deleteValues(refreshKey);
        addToBlacklist(accessToken);
        reissueTokens(response, findAdmin);
    }

    private void validateAccessDeniedToken(String accessToken) {
        if (BLACKLIST.equals(redisUtil.getValues(accessToken))) {
            throw new RestApiException(AuthErrorCode.UNAUTHORIZED_TOKEN);
        }
    }

    private void validateRefreshToken(String refreshKey) {
        try {
            redisUtil.validateExpiredFromKey(refreshKey);
            String refreshToken = redisUtil.getValues(refreshKey);

            if ("false".equals(refreshToken)) {
                throw new RestApiException(AuthErrorCode.UNAUTHORIZED_TOKEN);
            }
        } catch (Exception e) {
            throw new RestApiException(AuthErrorCode.FAIL_REISSUE_TOKEN);
        }
    }

    private void reissueTokens(HttpServletResponse response, Admin findAdmin) {
        CustomOauth2User customUser = new CustomOauth2User(
                AuthInfo.of(findAdmin.getName(), findAdmin.getEmail(), findAdmin.getRole())
        );

        String reissuedAccessToken = tokenProvider.generateAccessToken(findAdmin, customUser,
                new Date());
        tokenProvider.generateRefreshToken(findAdmin, customUser, new Date());

        cookieUtil.deleteCookie(response);
        response.addCookie(cookieUtil.createCookie(reissuedAccessToken));
    }
}
