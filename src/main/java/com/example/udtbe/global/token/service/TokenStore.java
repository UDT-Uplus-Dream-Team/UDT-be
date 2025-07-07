package com.example.udtbe.global.token.service;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.security.dto.Oauth2Response;
import java.time.Duration;
import java.util.Optional;

public interface TokenStore {

    static final long ACCESS_TOKEN_EXPIRATION = 3600 * 1000;

    void deleteRefreshTokenIfExists(Member m);

    void deleteOauthAccessTokenIfExists(Member m);

    void saveOauth2AccessToken(Oauth2Response oauth2Response, Member m);

    void save(String key, String value, Duration ttl);

    Optional<String> find(String key);

    void delete(String key);
}
