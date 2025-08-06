package com.example.udtbe.domain.admin.service;

import static com.example.udtbe.domain.auth.exception.AuthErrorCode.INVALID_CREDENTIALS;

import com.example.udtbe.domain.admin.dto.request.AdminSigninRequest;
import com.example.udtbe.domain.admin.entity.Admin;
import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.security.dto.AuthInfo;
import com.example.udtbe.global.security.dto.CustomOauth2User;
import com.example.udtbe.global.token.cookie.CookieUtil;
import com.example.udtbe.global.token.service.TokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminQuery adminQuery;
    private final TokenProvider tokenProvider;
    private final CookieUtil cookieUtil;
    private final PasswordEncoder passwordEncoder;

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
}
