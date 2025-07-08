package com.example.udtbe.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.domain.auth.service.AuthService;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Role;
import com.example.udtbe.global.security.dto.CustomOauth2User;
import com.example.udtbe.global.token.cookie.CookieUtil;
import com.example.udtbe.global.token.service.TokenProvider;
import com.example.udtbe.global.token.service.TokenStore;
import com.example.udtbe.global.util.RedisUtil;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private TokenStore tokenStore;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private CookieUtil cookieUtil;

    @InjectMocks
    private AuthService authService;

    @DisplayName("로그인 회원은 로그아웃 할 수 있다.")
    @Test
    void logout() {
        // given
        final Long fiveMinutes = 300_000L;
        final String email = "test@naver.com";
        final String token = "testtesttesttest";

        Member principal = MemberFixture.member(email, Role.ROLE_USER);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "test");

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        mockRequest.setCookies(new Cookie("Authorization", token));

        given(cookieUtil.getCookieValue(mockRequest)).willReturn(token);
        given(tokenProvider.validateToken(anyString(), any(Date.class))).willReturn(true);
        given(tokenProvider.getAuthentication(anyString())).willReturn(authentication);
        given(tokenProvider.getExpiration(anyString(), any(Date.class))).willReturn(fiveMinutes);
        given(redisUtil.getValues(anyString())).willReturn("refresh");
        given(redisUtil.getValues(anyString())).willReturn("delete");

        willDoNothing().given(redisUtil).setValues(anyString(), anyString(), any(Duration.class));

        // when
        authService.logout(mockRequest, mockResponse);

        // then
        Assertions.assertAll(
                () -> verify(cookieUtil, times(1)).getCookieValue(mockRequest),
                () -> verify(tokenProvider, times(1)).getAuthentication(token),
                () -> verify(cookieUtil, times(1)).deleteCookie(mockResponse)
        );
    }

    @DisplayName("refresh 토큰이 만료되지 않았다면 재발급 할 수 있다.")
    @Test
    void reissue() {
        // given
        final String email = "test@naver.com";
        final String token = "testtesttesttest";
        final String reissueToken = "reissueToken";

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        mockRequest.setCookies(new Cookie("Authorization", token));

        Member principal = MemberFixture.member(email, Role.ROLE_USER);

        given(cookieUtil.getCookieValue(mockRequest)).willReturn("testtesttesttest");
        given(cookieUtil.createCookie(anyString())).willReturn(
                new Cookie("Authorization", reissueToken));
        given(tokenProvider.getMemberAllowExpired(anyString())).willReturn(principal);
        given(redisUtil.getValues(anyString())).willReturn(reissueToken);
        given(tokenProvider.generateAccessToken(
                any(Member.class),
                any(CustomOauth2User.class),
                any(Date.class)))
                .willReturn("reissueToken");
        doNothing().when(tokenProvider)
                .generateRefreshToken(any(Member.class), any(CustomOauth2User.class),
                        any(Date.class));

        // when
        authService.reissue(mockRequest, mockResponse);
        Cookie[] cookies = mockResponse.getCookies();

        // then
        Assertions.assertAll(
                () -> assertThat(cookies)
                        .hasSize(1)
                        .extracting(Cookie::getName, Cookie::getValue)
                        .containsExactly(tuple("Authorization", reissueToken))
        );
    }
}
