package com.example.udtbe.global.security.dto;

import java.util.Map;

public class KakaoResponse implements Oauth2Response {

    private final Map<String, Object> attribute;
    private final Long id;
    private final String oauth2AccessToken;

    public KakaoResponse(Map<String, Object> attribute, String oauth2AccessToken) {
        this.attribute = (Map<String, Object>) attribute.get("kakao_account");
        this.id = (Long) attribute.get("id");
        this.oauth2AccessToken = oauth2AccessToken;
    }

    @Override
    public String getProviderId() {
        return this.id.toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return ((Map<String, Object>) attribute.get("profile")).get("nickname").toString();
    }

    @Override
    public String getOauth2AccessToken() {
        return this.oauth2AccessToken;
    }

    public String getProfileImageUrl() {
        return ((Map<String, Object>) attribute.get("profile")).get("profile_image_url").toString();
    }

}
