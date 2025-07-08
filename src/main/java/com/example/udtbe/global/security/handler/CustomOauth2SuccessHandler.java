package com.example.udtbe.global.security.handler;

import com.example.udtbe.domain.auth.service.AuthQuery;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Role;
import com.example.udtbe.global.security.dto.CustomOauth2User;
import com.example.udtbe.global.token.cookie.CookieUtil;
import com.example.udtbe.global.token.service.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOauth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthQuery authQuery;
    private final TokenProvider tokenProvider;
    private final CookieUtil cookieUtil;
    @Value("${direct.sign-up}")
    private String REDIRECTION_SIGNUP;
    @Value("${direct.home}")
    private String REDIRECTION_HOME;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        CustomOauth2User customOauth2User = (CustomOauth2User) authentication.getPrincipal();

        String email = customOauth2User.getEmail();
        Member findmember = authQuery.getMemberByEmail(email);

        String token = tokenProvider.generateAccessToken(findmember, customOauth2User, new Date());
        tokenProvider.generateRefreshToken(findmember, customOauth2User, new Date());

        response.addCookie(cookieUtil.createCookie(token));

        if (isRoleGuest(findmember.getRole())) {
            response.sendRedirect(REDIRECTION_SIGNUP);
        } else {
            response.sendRedirect(REDIRECTION_HOME);
        }
    }

    private boolean isRoleGuest(Role role) {
        return Role.ROLE_GUEST.equals(role);
    }
}
