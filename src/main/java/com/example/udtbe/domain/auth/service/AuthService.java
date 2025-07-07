package com.example.udtbe.domain.auth.service;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Gender;
import com.example.udtbe.domain.member.entity.enums.Role;
import com.example.udtbe.global.security.dto.Oauth2Response;
import com.example.udtbe.global.token.service.TokenStore;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthQuery authQuery;
    @Qualifier("redisTokenStore")
    private final TokenStore tokenStore;

    public Member saveOrUpdate(Oauth2Response oauth2Response) {
        Member member = authQuery.getOptionalMemberByEmail(oauth2Response.getEmail())
                .map(m -> {
                    tokenStore.deleteRefreshTokenIfExists(m);
                    tokenStore.deleteOauthAccessTokenIfExists(m);
                    tokenStore.saveOauth2AccessToken(oauth2Response, m);
                    return m;
                })
                .orElseGet(() -> createMemberFromOauth2Response(oauth2Response));

        return authQuery.save(member);
    }

    private Member createMemberFromOauth2Response(Oauth2Response oauth2Response) {
        return Member.of(oauth2Response.getEmail(), oauth2Response.getName(), Role.ROLE_GUEST,
                oauth2Response.getProfileImageUrl(), Gender.MAN, LocalDateTime.now(), false);
    }
}
