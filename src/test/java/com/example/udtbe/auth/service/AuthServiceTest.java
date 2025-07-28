package com.example.udtbe.auth.service;

import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_USER;
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
import com.example.udtbe.domain.auth.service.AuthQuery;
import com.example.udtbe.domain.auth.service.AuthService;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.security.dto.CustomOauth2User;
import com.example.udtbe.global.security.dto.KakaoResponse;
import com.example.udtbe.global.security.dto.Oauth2Response;
import com.example.udtbe.global.token.cookie.CookieUtil;
import com.example.udtbe.global.token.service.TokenProvider;
import com.example.udtbe.global.token.service.TokenStore;
import com.example.udtbe.global.util.RedisUtil;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
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

    @Mock
    private AuthQuery authQuery;

    @InjectMocks
    private AuthService authService;

    @DisplayName("로그인 회원은 로그아웃 할 수 있다.")
    @Test
    void logout() {
        // given
        final Long fiveMinutes = 300_000L;
        final String email = "test@naver.com";
        final String token = "testtesttesttest";

        Member principal = MemberFixture.member(email, ROLE_USER);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "test");

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        mockRequest.setCookies(new Cookie("Authorization", token));

        given(cookieUtil.getCookieValue(mockRequest)).willReturn(token);
        given(tokenProvider.validateToken(anyString(), any(Date.class))).willReturn(true);
        given(tokenProvider.getAuthentication(anyString())).willReturn(authentication);
        given(tokenProvider.getExpiration(anyString(), any(Date.class))).willReturn(fiveMinutes);
        given(redisUtil.getValues(anyString())).willReturn("black_list_token");

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

        Member principal = MemberFixture.member(email, ROLE_USER);

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

    @DisplayName("회원의 마지막 로그인 시간을 업데이트한다.")
    @Test
    void updateLastLoginAtWhenMemberLogsIn() {
        // given
        final Long id = 173097189L;
        final String name = "홍길동";
        final String profile_image_url = "http://example.com/profile.png";
        final String email = "test@naver.com";
        String oauth2AccessToken = "diuu1429gh1rbf8bwe8yfbv8731g8";

        Map<String, Object> profile = Map.of(
                "nickname", name,
                "profile_image_url", profile_image_url
        );

        Map<String, Object> kakaoAccount = Map.of(
                "profile", profile,
                "has_email", true,
                "email", email
        );

        Map<String, Object> attribute = Map.of(
                "id", id,
                "kakao_account", kakaoAccount
        );

        KakaoResponse kakaoResponse = new KakaoResponse(attribute, oauth2AccessToken);

        Member member = MemberFixture.member(email, ROLE_USER);
        LocalDateTime beforeLogin = member.getLastLoginAt();

        member.updateLastLoginAt(LocalDateTime.now());
        given(authQuery.getOptionalMemberByEmail(anyString()))
                .willReturn(Optional.ofNullable(member));
        given(authQuery.save(any(Member.class))).willReturn(member);
        willDoNothing()
                .given(tokenStore)
                .deleteRefreshTokenIfExists(any(Member.class));
        willDoNothing()
                .given(tokenStore)
                .deleteOauthAccessTokenIfExists(any(Member.class));
        willDoNothing()
                .given(tokenStore)
                .saveOauth2AccessToken(any(Oauth2Response.class), any(Member.class));

        // when
        Member savedMember = authService.saveOrUpdate(kakaoResponse);

        // then
        assertThat(savedMember.getLastLoginAt()).isAfter(beforeLogin);
    }
}
