package com.example.udtbe.survey.repository;

import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_GUEST;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.common.fixture.SurveyFixture;
import com.example.udtbe.common.support.DataJpaSupport;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.repository.MemberRepository;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.repository.SurveyRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SurveyRepositoryTest extends DataJpaSupport {

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("설문조사를 저장한다.")
    @Test
    void saveSurvey() {
        // given
        final String email = "test@naver.com";

        Member member = MemberFixture.member(email, ROLE_GUEST);
        Member savedMember = memberRepository.save(member);

        List<String> platforms = List.of("넷플릭스", "디즈니+");
        List<String> genres = List.of("코미디", "범죄");

        List<String> platformTypes = PlatformType.toPlatformTypes(platforms);
        List<String> genreTypes = GenreType.toGenreTypes(genres);

        Survey survey = SurveyFixture.survey(platformTypes, genreTypes, savedMember);

        // when
        Survey savedSurvey = surveyRepository.save(survey);

        // then
        Assertions.assertAll(
                () -> assertThat(savedSurvey.getPlatformTag()).containsExactly(
                        platformTypes.get(0),
                        platformTypes.get(1)
                ),
                // given
                () -> assertThat(savedSurvey.getGenreTag()).containsExactly(
                        genreTypes.get(0),
                        genreTypes.get(1)
                )
        );
    }

    @DisplayName("선택된 콘텐츠가 없어도 설문조사를 저장한다.")
    @Test
    void saveSurveyWhenContentsIsNull() {
        final String email = "test@naver.com";

        Member member = MemberFixture.member(email, ROLE_GUEST);
        Member savedMember = memberRepository.save(member);

        Survey survey = SurveyFixture.survey(null, savedMember);

        // when
        Survey savedSurvey = surveyRepository.save(survey);

        // then
        assertThat(savedSurvey.getContentTag()).isNull();
    }
}
