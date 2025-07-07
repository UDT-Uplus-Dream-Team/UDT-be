package com.example.udtbe.global.security.service;

import com.example.udtbe.domain.auth.service.AuthService;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.security.dto.AuthInfo;
import com.example.udtbe.global.security.dto.CustomOauth2User;
import com.example.udtbe.global.security.dto.KakaoResponse;
import com.example.udtbe.global.security.dto.Oauth2Response;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final AuthService authService;
    private static final String KAKAO = "kakao";

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String oauth2AccessToken = userRequest.getAccessToken().getTokenValue();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Oauth2Response oauth2Response = null;

        if (Objects.equals(registrationId, KAKAO)) {
            oauth2Response = new KakaoResponse(oAuth2User.getAttributes(), oauth2AccessToken);
        } else {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("ERROR"),
                    "UNSUPPORTED SOCIAL LOGIN"
            );
        }

        Member savedMember = authService.saveOrUpdate(oauth2Response);

        AuthInfo authInfo = AuthInfo.of(
                savedMember.getName(),
                savedMember.getEmail(),
                savedMember.getRole().getRole()
        );
        return new CustomOauth2User(authInfo);
    }

}

