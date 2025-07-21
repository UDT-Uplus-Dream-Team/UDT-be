package com.example.udtbe.member.service;

import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.common.fixture.SurveyFixture;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import com.example.udtbe.domain.content.repository.CuratedContentRepository;
import com.example.udtbe.domain.member.dto.request.MemberCuratedContentGetsRequest;
import com.example.udtbe.domain.member.dto.request.MemberUpdateGenreRequest;
import com.example.udtbe.domain.member.dto.request.MemberUpdatePlatformRequest;
import com.example.udtbe.domain.member.dto.response.MemberCuratedContentGetResponse;
import com.example.udtbe.domain.member.dto.response.MemberInfoResponse;
import com.example.udtbe.domain.member.dto.response.MemberUpdateGenreResponse;
import com.example.udtbe.domain.member.dto.response.MemberUpdatePlatformResponse;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.service.MemberQuery;
import com.example.udtbe.domain.member.service.MemberService;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.service.SurveyQuery;
import com.example.udtbe.global.dto.CursorPageResponse;
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

    @Mock
    private CuratedContentRepository curatedContentRepository;

    @InjectMocks
    private MemberService memberService;


    @DisplayName("마이페이지에서 회원 정보를 조회할 수 있다.")
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
                () -> assertThat(response.platforms()).isEqualTo(
                        PlatformType.toKoreanTypes(survey.getPlatformTag())),
                () -> assertThat(response.genres()).isEqualTo(
                        GenreType.toKoreanTypes(survey.getGenreTag())),
                () -> assertThat(response.profileImageUrl()).isEqualTo(member.getProfileImageUrl())
        );
    }

    @DisplayName("마이페이지에서 엄선된 추천 콘텐츠 목록을 무한스크롤로 조회할 수 있다.")
    @Test
    void getCuratedContents() {
        // given
        final String email = "test@email.com";

        Member member = MemberFixture.member(email, ROLE_USER);

        Content content1 = ContentFixture.content("test_content1", "description1");
        Content content2 = ContentFixture.content("test_content2", "description2");
        Content content3 = ContentFixture.content("test_content3", "description3");

        MemberCuratedContentGetResponse response1 = new MemberCuratedContentGetResponse(
                1L, "test_content1", "poster1.jpg"
        );
        MemberCuratedContentGetResponse response2 = new MemberCuratedContentGetResponse(
                2L, "test_content2", "poster2.jpg"
        );
        MemberCuratedContentGetResponse response3 = new MemberCuratedContentGetResponse(
                3L, "test_content3", "poster3.jpg"
        );

        MemberCuratedContentGetsRequest firstRequest = new MemberCuratedContentGetsRequest(null, 2);
        CursorPageResponse<MemberCuratedContentGetResponse> firstPageResponse =
                new CursorPageResponse<>(List.of(response1, response2), "2", true);

        given(curatedContentRepository.getCuratedContentByCursor(null, 2, member))
                .willReturn(firstPageResponse);

        MemberCuratedContentGetsRequest secondRequest = new MemberCuratedContentGetsRequest(2L, 2);
        CursorPageResponse<MemberCuratedContentGetResponse> secondPageResponse =
                new CursorPageResponse<>(List.of(response3), null, false);

        given(curatedContentRepository.getCuratedContentByCursor(2L, 2, member))
                .willReturn(secondPageResponse);

        // when
        CursorPageResponse<MemberCuratedContentGetResponse> firstResponse =
                memberService.getCuratedContents(firstRequest, member);

        CursorPageResponse<MemberCuratedContentGetResponse> secondResponse =
                memberService.getCuratedContents(secondRequest, member);

        // then
        assertAll(
                () -> assertThat(firstResponse.item()).hasSize(2),
                () -> assertThat(firstResponse.item().get(0).contentId()).isEqualTo(1L),
                () -> assertThat(firstResponse.item().get(1).contentId()).isEqualTo(2L),
                () -> assertThat(firstResponse.nextCursor()).isEqualTo("2"),
                () -> assertThat(firstResponse.hasNext()).isTrue()
        );

        // then 2
        assertAll(
                () -> assertThat(secondResponse.item()).hasSize(1),
                () -> assertThat(secondResponse.item().get(0).contentId()).isEqualTo(3L),
                () -> assertThat(secondResponse.nextCursor()).isNull(),
                () -> assertThat(secondResponse.hasNext()).isFalse()
        );
    }

    @DisplayName("마이페이지에서 회원 선호 장르를 수정할 수 있다.")
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
                () -> assertEquals(request.genres(), response.genres()),
                () -> assertEquals(
                        request.genres().stream()
                                .map(e -> GenreType.fromByType(e).name()).toList(),
                        survey.getGenreTag())
        );
    }

    @DisplayName("마이페이지에서 올바르지 않은 장르타입으로 수정할 수 없다.")
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


    @DisplayName("마이페이지에서 회원 구독 플렛폼을 수정할 수 있다.")
    @Test
    void updatePlatform() {
        // given
        final MemberUpdatePlatformRequest request = new MemberUpdatePlatformRequest(
                List.of("넷플릭스", "왓챠")
        );

        final String email = "test@email.com";

        Member member = MemberFixture.member(email, ROLE_USER);
        Survey survey = SurveyFixture.survey(List.of("NETFLIX"), List.of("SF", "ROMANCE"), member);

        given(surveyQuery.findSurveyByMemberId(member.getId())).willReturn(survey);
        // when
        MemberUpdatePlatformResponse response =
                memberService.updateMemberPlatforms(member.getId(), request);

        // then
        then(surveyQuery).should().findSurveyByMemberId(member.getId());
        assertAll(
                () -> assertEquals(request.platforms(), response.platforms()),
                () -> assertEquals(
                        request.platforms().stream()
                                .map(e -> PlatformType.fromByType(e).name()).toList(),
                        survey.getPlatformTag())
        );
    }

    @DisplayName("마이페이지에서 올바르지 않은 플렛폼 타입으로 수정할 수 없다.")
    @Test
    public void updateInvalidPlatform() {
        // given
        final MemberUpdatePlatformRequest request = new MemberUpdatePlatformRequest(
                List.of("엥?", "액션")
        );

        final String email = "test@email.com";

        Member member = MemberFixture.member(email, ROLE_USER);
        Survey survey = SurveyFixture.survey(List.of("NETFLIX"), List.of("SF", "ROMANCE"), member);

        given(surveyQuery.findSurveyByMemberId(member.getId())).willReturn(survey);

        // when & then
        assertThatThrownBy(() -> memberService.updateMemberPlatforms(member.getId(), request))
                .isInstanceOf(RestApiException.class)
                .hasMessage(EnumErrorCode.PLATFORM_TYPE_BAD_REQUEST.getMessage());
    }


}
