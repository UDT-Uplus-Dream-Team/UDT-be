package com.example.udtbe.common.support;

import com.example.udtbe.common.config.TestRedisConfig;
import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.domain.auth.service.AuthQuery;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Role;
import com.example.udtbe.global.security.dto.AuthInfo;
import com.example.udtbe.global.security.dto.CustomOauth2User;
import com.example.udtbe.global.token.cookie.CookieUtil;
import com.example.udtbe.global.token.service.TokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Date;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@Import(TestRedisConfig.class)
@AutoConfigureMockMvc
public abstract class ApiSupport extends TestContainerSupport {

    protected Member loginAdmin;
    protected Member loginMember;
    protected Member loginTempMember;
    protected Cookie accessTokenOfAdmin;
    protected Cookie accessTokenOfMember;
    protected Cookie accessTokenOfTempMember;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected AuthQuery authQuery;
    @Autowired
    protected CookieUtil cookieUtil;
    @Autowired
    protected TokenProvider tokenProvider;

    protected String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    @BeforeEach
    void setUp() {
        authQuery.deleteAll();
        setUpMembers();
    }

    public void setUpMembers() {
        if (!Objects.isNull(loginAdmin) && !Objects.isNull(loginMember) && Objects.isNull(
                loginTempMember)) {
            return;
        }

        this.loginAdmin = authQuery.save(MemberFixture.member("admin@naver.com", Role.ROLE_ADMIN));
        this.loginMember = authQuery.save(MemberFixture.member("user@naver.com", Role.ROLE_USER));
        this.loginTempMember = authQuery.save(
                MemberFixture.member("tempuser@naver.com", Role.ROLE_GUEST));

        AuthInfo authInfoOfAdmin = getAuthInfo(loginAdmin);
        AuthInfo authInfoOfMember = getAuthInfo(loginMember);
        AuthInfo authInfoOfTempMember = getAuthInfo(loginTempMember);

        this.accessTokenOfAdmin = cookieUtil.createCookie(
                generateTokens(loginAdmin, authInfoOfAdmin)
        );
        this.accessTokenOfMember = cookieUtil.createCookie(
                generateTokens(loginMember, authInfoOfMember)
        );
        this.accessTokenOfTempMember = cookieUtil.createCookie(
                generateTokens(loginTempMember, authInfoOfTempMember)
        );
    }

    private String generateTokens(Member member, AuthInfo authInfo) {
        tokenProvider.generateRefreshToken(member, new CustomOauth2User(authInfo),
                new Date());

        return tokenProvider.generateAccessToken(member, new CustomOauth2User(authInfo),
                new Date());
    }

    private AuthInfo getAuthInfo(Member member) {
        return AuthInfo.of(
                member.getName(),
                member.getEmail(),
                member.getRole()
        );
    }


}
