package com.example.udtbe.member.service;

import static com.example.udtbe.common.fixture.CuratedContentFixture.curatedContent;
import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.common.fixture.SurveyFixture;
import com.example.udtbe.domain.content.dto.request.CuratedContentGetRequest;
import com.example.udtbe.domain.content.dto.response.CuratedContentGetListResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.CuratedContent;
import com.example.udtbe.domain.content.service.CuratedContentQuery;
import com.example.udtbe.domain.member.dto.response.MemberInfoResponse;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.service.MemberQuery;
import com.example.udtbe.domain.member.service.MemberService;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.service.SurveyQuery;
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
    private CuratedContentQuery curatedContentQuery;

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
                () -> assertThat(response.platforms()).isEqualTo(survey.getPlatformTag()),
                () -> assertThat(response.genres()).isEqualTo(survey.getGenreTag()),
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

        CuratedContent curatedContent1 = curatedContent(member, content1);
        CuratedContent curatedContent2 = curatedContent(member, content2);
        CuratedContent curatedContent3 = curatedContent(member, content3);

        CuratedContentGetRequest request = new CuratedContentGetRequest(null, 2);

        given(curatedContentQuery.getCuratedContentsByCursor(member, request))
                .willReturn(List.of(curatedContent1, curatedContent2, curatedContent3));
        // when
        CuratedContentGetListResponse response = memberService.getCuratedContents(request, member);

        // then
        assertAll(
                () -> assertThat(response.contents()).hasSize(2),
                () -> assertThat(response.contents().get(0).title()).isEqualTo("test_content1"),
                () -> assertThat(response.contents().get(1).title()).isEqualTo("test_content2"),
                () -> assertThat(response.nextCursor()).isEqualTo(curatedContent2.getId()),
                () -> assertThat(response.hasNext()).isTrue()
        );

    }
}
