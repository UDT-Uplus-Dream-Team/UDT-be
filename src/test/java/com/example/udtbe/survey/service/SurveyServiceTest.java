package com.example.udtbe.survey.service;

import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_GUEST;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.dto.request.SurveyCreateRequest;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.exception.SurveyErrorCode;
import com.example.udtbe.domain.survey.service.SurveyQuery;
import com.example.udtbe.domain.survey.service.SurveyService;
import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.token.cookie.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
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

    @InjectMocks
    SurveyService surveyService;

    @DisplayName("설문조사를 저장한다.")
    @Test
    void createSurvey() {
        // given
        final String email = "test@naver.com";
        final HttpServletResponse response = new MockHttpServletResponse();

        Member member = MemberFixture.member(email, ROLE_GUEST);
        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = List.of("코미디", "범죄");
        SurveyCreateRequest request = new SurveyCreateRequest(platforms, genres);

        given(surveyQuery.existsByMember(member)).willReturn(Boolean.FALSE);
        given(surveyQuery.save(any(Survey.class))).willReturn(null);
        willDoNothing().given(cookieUtil).deleteCookie(any(HttpServletResponse.class));

        // when
        surveyService.createSurvey(request, member, response);

        // then
        assertAll(
                () -> verify(surveyQuery, times(1)).existsByMember(member),
                () -> verify(surveyQuery, times(1)).save(any(Survey.class))
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
        SurveyCreateRequest request = new SurveyCreateRequest(platforms, genres);

        given(surveyQuery.existsByMember(member)).willReturn(Boolean.TRUE);
        
        // when  // then
        assertThatThrownBy(() -> surveyService.createSurvey(request, member, response))
                .isInstanceOf(RestApiException.class)
                .hasMessage(SurveyErrorCode.SURVEY_ALREADY_EXISTS_FOR_MEMBER.getMessage());
    }
}