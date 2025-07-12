package com.example.udtbe.member.service;

import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.common.fixture.SurveyFixture;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.member.dto.request.MemberUpdateGenreRequest;
import com.example.udtbe.domain.member.dto.response.MemberInfoResponse;
import com.example.udtbe.domain.member.dto.response.MemberUpdateGenreResponse;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.service.MemberQuery;
import com.example.udtbe.domain.member.service.MemberService;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.service.SurveyQuery;
import com.example.udtbe.global.exception.RestApiException;
import com.example.udtbe.global.exception.code.EnumErrorCode;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberQuery memberQuery;

    @Mock
    private SurveyQuery surveyQuery;

    @InjectMocks
    private MemberService memberService;

    @DisplayName("마이페이지에서 회원 정보를 조회한다.")
    @Test
    void getMemberInfo() {
        // given
        final String email = "test@email.com";

        Member member = MemberFixture.member(email, ROLE_USER);
        Survey survey = SurveyFixture.survey(null, member);

        given(memberQuery.findMemberById(member.getId())).willReturn(member);
        given(surveyQuery.findSurveyByMemberId(member.getId())).willReturn(survey);

        // when
        MemberInfoResponse response = memberService.getMemberInfo(member.getId());

        // then
        then(memberQuery).should().findMemberById(member.getId());
        then(surveyQuery).should().findSurveyByMemberId(survey.getId());

        assertAll(
                () -> assertThat(response.name()).isEqualTo(member.getName()),
                () -> assertThat(response.email()).isEqualTo(member.getEmail()),
                () -> assertThat(response.platforms()).isEqualTo(survey.getPlatformTag()),
                () -> assertThat(response.genres()).isEqualTo(survey.getGenreTag()),
                () -> assertThat(response.profileImageUrl()).isEqualTo(member.getProfileImageUrl())
        );

    }

    @DisplayName("마이페이지에서 회원 선호 장르를 수정한다.")
    @Test
    void updateGenre() {
        // given
        final MemberUpdateGenreRequest request = new MemberUpdateGenreRequest(
                List.of("키즈", "액션")
        );

        final String email = "test@email.com";

        Member member = MemberFixture.member(email, ROLE_USER);
        Survey survey = SurveyFixture.survey(List.of("NETFLIX"), List.of("SF", "ROMANCE"), member);

        given(surveyQuery.findSurveyByMemberId(member.getId())).willReturn(survey);
        // when
        MemberUpdateGenreResponse response =
                memberService.updateMemberGenres(member.getId(), request);

        // then
        then(surveyQuery).should().findSurveyByMemberId(member.getId());
        assertAll(
                () -> assertEquals(
                        request.genres().stream()
                                .map(e -> GenreType.fromByType(e).name()).toList(),
                        response.genres()),
                () -> assertEquals(
                        request.genres().stream()
                                .map(e -> GenreType.fromByType(e).name()).toList(),
                        survey.getGenreTag())
        );
    }

    @DisplayName("마이페이지에서 올바르지 않은 장르타입으로 수정 시 400에러가 나온다.")
    @Test
    public void updateInvalidGenre() {
        // given
        final MemberUpdateGenreRequest request = new MemberUpdateGenreRequest(
                List.of("엥?", "액션")
        );

        final String email = "test@email.com";

        Member member = MemberFixture.member(email, ROLE_USER);
        Survey survey = SurveyFixture.survey(List.of("NETFLIX"), List.of("SF", "ROMANCE"), member);

        given(surveyQuery.findSurveyByMemberId(member.getId())).willReturn(survey);

        // when & then
        assertThatThrownBy(() -> memberService.updateMemberGenres(member.getId(), request))
                .isInstanceOf(RestApiException.class)
                .hasMessage(EnumErrorCode.GENRE_TYPE_BAD_REQUEST.getMessage());
    }
}
