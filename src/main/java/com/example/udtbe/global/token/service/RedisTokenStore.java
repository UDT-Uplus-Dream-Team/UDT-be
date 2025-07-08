package com.example.udtbe.global.token.service;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.security.dto.Oauth2Response;
import com.example.udtbe.global.util.RedisUtil;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTokenStore implements TokenStore {

    private final RedisUtil redisUtil;

    @Override
    public void deleteRefreshTokenIfExists(Member m) {
        if (redisUtil.getValues("RT:" + m.getEmail()) != null) {
            redisUtil.deleteValues("RT:" + m.getEmail());
        }
    }

    @Override
    public void deleteOauthAccessTokenIfExists(Member m) {
        if (redisUtil.getValues("AT(oauth):" + m.getEmail()) != null) {
            redisUtil.deleteValues("AT(oauth):" + m.getEmail());
        }
    }

    @Override
    public void saveOauth2AccessToken(Oauth2Response oauth2Response, Member m) {
        redisUtil.setValues(
                "AT(oauth):" + m.getEmail(),
                oauth2Response.getOauth2AccessToken(),
                Duration.ofMillis(ACCESS_TOKEN_EXPIRATION)
        );
    }

    @Override
    public void save(String key, String value, Duration ttl) {
        redisUtil.setValues(key, value, ttl);
    }

    @Override
    public Optional<String> find(String key) {
        return Optional.empty();
    }

    @Override
    public void delete(String key) {

    }
}
