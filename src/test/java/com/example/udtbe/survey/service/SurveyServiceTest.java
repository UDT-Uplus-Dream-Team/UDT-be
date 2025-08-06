package com.example.udtbe.survey.service;

import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_GUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.domain.auth.service.AuthQuery;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.dto.request.SurveyCreateRequest;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.exception.SurveyErrorCode;
import com.example.udtbe.domain.survey.service.SurveyQuery;
import com.example.udtbe.domain.survey.service.SurveyService;
import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.security.dto.CustomOauth2User;
import com.example.udtbe.global.token.cookie.CookieUtil;
import com.example.udtbe.global.token.service.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class SurveyServiceTest {

    @Mock
    SurveyQuery surveyQuery;

    @Mock
    CookieUtil cookieUtil;

    @Mock
    AuthQuery authQuery;

    @Mock
    TokenProvider tokenProvider;

    @InjectMocks
    SurveyService surveyService;

    @DisplayName("설문조사를 저장한다.")
    @Test
    void createSurvey() {
        // given
        final String email = "test@naver.com";
        final String token = "exnofneonon141418befbqub";
        final HttpServletResponse response = new MockHttpServletResponse();
        final Cookie accessToken = new Cookie("Authorization", token);

        Member member = MemberFixture.member(email, ROLE_GUEST);
        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = List.of("코미디", "범죄");
        List<Long> contentId = Collections.emptyList();
        SurveyCreateRequest request = new SurveyCreateRequest(platforms, genres, contentId);

        given(surveyQuery.existsByMember(member)).willReturn(Boolean.FALSE);
        given(surveyQuery.save(any(Survey.class))).willReturn(null);
        given(authQuery.saveMember(any(Member.class))).willReturn(null);
        willDoNothing().given(cookieUtil).deleteCookie(any(HttpServletResponse.class));
        given(tokenProvider.generateAccessToken(
                any(Member.class),
                any(CustomOauth2User.class),
                any(Date.class))
        ).willReturn(token);
        given(cookieUtil.createCookie(token)).willReturn(accessToken);

        // when
        surveyService.createSurvey(request, member, response);

        // then
        assertAll(
                () -> verify(surveyQuery, times(1)).existsByMember(member),
                () -> verify(surveyQuery, times(1)).save(any(Survey.class)),
                () -> {
                    MockHttpServletResponse mockResponse = (MockHttpServletResponse) response;
                    Cookie[] cookies = mockResponse.getCookies();
                    assertThat(cookies).isNotNull();
                    assertThat(cookies).hasSize(1);
                    assertThat(cookies[0].getName()).isEqualTo("Authorization");
                    assertThat(cookies[0].getValue()).isEqualTo(token);
                }
        );
    }

    @DisplayName("설문조사는 2회 이상 할 수 없다.")
    @Test
    void throwExceptionIfSurveyAlreadyExists() {
        // given
        final String email = "test@naver.com";
        final HttpServletResponse response = new MockHttpServletResponse();

        Member member = MemberFixture.member(email, ROLE_GUEST);
        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = List.of("코미디", "범죄");
        List<Long> contentId = Collections.emptyList();

        SurveyCreateRequest request = new SurveyCreateRequest(platforms, genres, contentId);

        given(surveyQuery.existsByMember(member)).willReturn(Boolean.TRUE);

        // when  // then
        assertThatThrownBy(() -> surveyService.createSurvey(request, member, response))
                .isInstanceOf(RestApiException.class)
                .hasMessage(SurveyErrorCode.SURVEY_ALREADY_EXISTS_FOR_MEMBER.getMessage());
    }
}