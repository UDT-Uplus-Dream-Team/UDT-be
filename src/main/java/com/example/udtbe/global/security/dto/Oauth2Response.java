package com.example.udtbe.global.security.dto;

public interface Oauth2Response {


    String getProviderId();

    String getEmail();

    String getName();

    String getOauth2AccessToken();

    String getProfileImageUrl();
}
