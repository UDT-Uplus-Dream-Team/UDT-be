package com.example.udtbe.domain.auth.service;

import static com.example.udtbe.domain.member.entity.enums.Gender.MAN;
import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_ADMIN;

import com.example.udtbe.domain.auth.dto.request.TempAuthRequest;
import com.example.udtbe.domain.auth.exception.AuthErrorCode;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Role;
import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.log.annotation.LogReturn;
import com.example.udtbe.global.security.dto.AuthInfo;
import com.example.udtbe.global.security.dto.CustomOauth2User;
import com.example.udtbe.global.security.dto.Oauth2Response;
import com.example.udtbe.global.token.cookie.CookieUtil;
import com.example.udtbe.global.token.service.TokenProvider;
import com.example.udtbe.global.token.service.TokenStore;
import com.example.udtbe.global.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String BLACKLIST = "black_list_token";
    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private final AuthQuery authQuery;
    @Qualifier("redisTokenStore")
    private final TokenStore tokenStore;
    private final TokenProvider tokenProvider;
    private final CookieUtil cookieUtil;
    private final RedisUtil redisUtil;

    @LogReturn()
    public Member saveOrUpdate(Oauth2Response oauth2Response) {
        Member member = authQuery.getOptionalMemberByEmail(oauth2Response.getEmail())
                .map(m -> {
                    m.updateLastLoginAt(LocalDateTime.now());
                    tokenStore.deleteRefreshTokenIfExists(m);
                    tokenStore.deleteOauthAccessTokenIfExists(m);
                    tokenStore.saveOauth2AccessToken(oauth2Response, m);
                    return m;
                })
                .orElseGet(() -> createMemberFromOauth2Response(oauth2Response));

        return authQuery.saveMember(member);
    }

    @LogReturn()
    private Member createMemberFromOauth2Response(Oauth2Response oauth2Response) {
        return Member.of(oauth2Response.getEmail(), oauth2Response.getName(), Role.ROLE_GUEST,
                oauth2Response.getProfileImageUrl(), MAN, LocalDateTime.now(), false);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = cookieUtil.getCookieValue(request);

        if (!tokenProvider.validateToken(accessToken, new Date())) {
            throw new RestApiException(AuthErrorCode.UNAUTHORIZED_TOKEN);
        }

        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        Member member = (Member) authentication.getPrincipal();

        deleteRefreshTokenIfExists(member.getEmail());
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

        if (expiration <= 0) {
            return;
        }

        redisUtil.setValues(accessToken, BLACKLIST, Duration.ofMillis(expiration));

        if (!BLACKLIST.equals(redisUtil.getValues(accessToken))) {
            throw new RestApiException(AuthErrorCode.LOGOUT_FAILED);
        }
    }

    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = cookieUtil.getCookieValue(request);

        if (Objects.isNull(accessToken)) {
            throw new RestApiException(AuthErrorCode.MISSING_ACCESS_TOKEN);
        }

        validateAccessDeniedToken(accessToken);

        Member findMember = tokenProvider.getMemberAllowExpired(accessToken);
        String refreshKey = getRefreshTokenPrefix(findMember.getEmail());

        validateRefreshToken(refreshKey);
        redisUtil.deleteValues(refreshKey);
        addToBlacklist(accessToken);
        reissueTokens(response, findMember);
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

            // 로그아웃 또는 토큰 만료 경우 처리
            if ("false".equals(refreshToken)) {
                throw new RestApiException(AuthErrorCode.UNAUTHORIZED_TOKEN);
            }
        } catch (Exception e) {
            // 재로그인 요청 처리
            throw new RestApiException(AuthErrorCode.FAIL_REISSUE_TOKEN);
        }
    }

    private void reissueTokens(HttpServletResponse response, Member findMember) {
        CustomOauth2User customUser = new CustomOauth2User(
                AuthInfo.of(findMember.getName(), findMember.getEmail(), findMember.getRole())
        );

        String reissuedAccessToken = tokenProvider.generateAccessToken(findMember, customUser,
                new Date());
        tokenProvider.generateRefreshToken(findMember, customUser, new Date());

        cookieUtil.deleteCookie(response);
        response.addCookie(cookieUtil.createCookie(reissuedAccessToken));
    }

    private String getRefreshTokenPrefix(String email) {
        return REFRESH_TOKEN_PREFIX + email;
    }

    public void tempSignUp(TempAuthRequest request) {
        if (authQuery.existsByEmail(request.email())) {
            throw new RestApiException(AuthErrorCode.DUPLICATED_EMAIL);
        }

        Member member = Member.of(request.email(), "홍길동", ROLE_ADMIN, null, MAN,
                LocalDateTime.now(), false);

        authQuery.saveMember(member);
    }

    public void tempSignIn(TempAuthRequest request, HttpServletResponse response) {
        Date now = new Date();

        Member findMember = authQuery.getMemberByEmail(request.email());

        AuthInfo authInfo = AuthInfo.of(
                findMember.getName(), findMember.getEmail(), findMember.getRole()
        );
        CustomOauth2User customOauth2User = new CustomOauth2User(authInfo);

        tokenProvider.generateRefreshToken(findMember, customOauth2User, now);
        String accessToken = tokenProvider.generateAccessToken(findMember, customOauth2User, now);
        response.addCookie(cookieUtil.createCookie(accessToken));
    }
}
