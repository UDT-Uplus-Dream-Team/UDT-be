package com.example.udtbe.common.support;

import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.repository.MemberRepository;
import com.example.udtbe.domain.member.entity.enums.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class ApiSupport extends TestContainerSupport {

    private static final String BEARER = "Bearer ";
    protected Member loginAdmin;
    protected Member loginUser;
    protected String accessTokenOfUser;
    protected String accessTokenOfAdmin;
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    protected String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public void setUpMembers() {
        if (loginAdmin != null && loginUser != null) {
            return;
        }

        this.loginAdmin = memberRepository.save(
                MemberFixture.member("admin@naver.com", Role.ROLE_ADMIN));
        this.loginUser = memberRepository.save(
                MemberFixture.member("user@naver.com", Role.ROLE_USER));

        this.accessTokenOfAdmin = BEARER + "tmpToken";
        this.accessTokenOfUser = BEARER + "tmpToken";
    }

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        setUpMembers();
    }
}
