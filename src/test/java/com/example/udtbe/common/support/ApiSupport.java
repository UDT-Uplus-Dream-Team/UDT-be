package com.example.udtbe.common.support;

import com.example.udtbe.common.config.TestRedisConfig;
import com.example.udtbe.common.fixture.AdminFixture;
import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.domain.admin.entity.Admin;
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
import java.util.UUID;
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

    protected Admin loginAdmin;
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

        this.loginAdmin = authQuery.saveAdmin(
                AdminFixture.admin(UUID.randomUUID().toString()));
        this.loginMember = authQuery.saveMember(
                MemberFixture.member("user@naver.com", Role.ROLE_USER));
        this.loginTempMember = authQuery.saveMember(
                MemberFixture.member("tempuser@naver.com", Role.ROLE_GUEST));

        AuthInfo authInfoOfAdmin = getAuthInfoByAdmin(loginAdmin);
        AuthInfo authInfoOfMember = getAuthInfoByMember(loginMember);
        AuthInfo authInfoOfTempMember = getAuthInfoByMember(loginTempMember);

        this.accessTokenOfAdmin = cookieUtil.createCookie(
                generateTokensByAdmin(loginAdmin, authInfoOfAdmin)
        );
        this.accessTokenOfMember = cookieUtil.createCookie(
                generateTokensByMember(loginMember, authInfoOfMember)
        );
        this.accessTokenOfTempMember = cookieUtil.createCookie(
                generateTokensByMember(loginTempMember, authInfoOfTempMember)
        );
    }

    private String generateTokensByMember(Member member, AuthInfo authInfo) {
        tokenProvider.generateRefreshToken(member, new CustomOauth2User(authInfo),
                new Date());

        return tokenProvider.generateAccessToken(member, new CustomOauth2User(authInfo),
                new Date());
    }

    private String generateTokensByAdmin(Admin admin, AuthInfo authInfo) {
        tokenProvider.generateRefreshToken(admin, new CustomOauth2User(authInfo),
                new Date());

        return tokenProvider.generateAccessToken(admin, new CustomOauth2User(authInfo),
                new Date());
    }

    private AuthInfo getAuthInfoByMember(Member member) {
        return AuthInfo.of(
                member.getName(),
                member.getEmail(),
                member.getRole()
        );
    }

    private AuthInfo getAuthInfoByAdmin(Admin admin) {
        return AuthInfo.of(
                admin.getName(),
                admin.getEmail(),
                admin.getRole()
        );
    }

}
