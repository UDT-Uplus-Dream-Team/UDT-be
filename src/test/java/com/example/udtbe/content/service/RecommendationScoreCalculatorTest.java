package com.example.udtbe.content.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentMetadataFixture;
import com.example.udtbe.common.fixture.FeedbackFixture;
import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.service.RecommendationScoreCalculator;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Role;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class RecommendationScoreCalculatorTest {

    private RecommendationScoreCalculator calculator;
    private Member testMember;
    private Content testContent;
    private ContentMetadata testMetadata;

    @BeforeEach
    void setUp() {
        calculator = new RecommendationScoreCalculator();
        testMember = MemberFixture.member("test@example.com", Role.ROLE_USER);
        testContent = ContentFixture.content("테스트 영화", "테스트 설명");
        testMetadata = ContentMetadataFixture.metadata(testContent, "넷플릭스", "액션,스릴러");
        ReflectionTestUtils.setField(testContent, "id", 1L);
    }

    @Test
    @DisplayName("장르 부스트 계산 - 일치하는 장르가 있으면 부스트 점수 증가")
    void calculateGenreBoost_WithMatchingGenres() {
        // given
        Set<String> docGenres = Set.of("액션", "스릴러", "드라마");
        List<String> memberGenres = List.of("액션", "코미디", "스릴러");

        // when
        float result = calculator.calculateGenreBoost(docGenres, memberGenres);

        // then
        assertThat(result).isEqualTo(2.0f); // 액션(1.0) + 스릴러(1.0)
    }

    @Test
    @DisplayName("장르 부스트 계산 - 일치하는 장르가 없으면 0")
    void calculateGenreBoost_WithNoMatchingGenres() {
        // given
        Set<String> docGenres = Set.of("액션", "스릴러");
        List<String> memberGenres = List.of("코미디", "로맨스");

        // when
        float result = calculator.calculateGenreBoost(docGenres, memberGenres);

        // then
        assertThat(result).isEqualTo(0.0f);
    }

    @Test
    @DisplayName("장르 부스트 계산 - 빈 입력값이면 0")
    void calculateGenreBoost_WithEmptyInputs() {
        // given
        Set<String> emptyDocGenres = Set.of();
        List<String> emptyMemberGenres = List.of();

        // when
        float result1 = calculator.calculateGenreBoost(emptyDocGenres, List.of("액션"));
        float result2 = calculator.calculateGenreBoost(Set.of("액션"), emptyMemberGenres);
        float result3 = calculator.calculateGenreBoost(Set.of("액션"), null);

        // then
        assertThat(result1).isEqualTo(0.0f);
        assertThat(result2).isEqualTo(0.0f);
        assertThat(result3).isEqualTo(0.0f);
    }

    @Test
    @DisplayName("피드백 장르 부스트 계산 - 장르별 점수 합산")
    void calculateGenreFeedbackBoost_WithGenreScores() {
        // given
        Set<String> docGenres = Set.of("액션", "스릴러");
        Map<String, Float> genreScores = Map.of(
                "액션", 2.5f,
                "스릴러", 1.5f,
                "코미디", 3.0f
        );

        // when
        float result = calculator.calculateGenreFeedbackBoost(docGenres, genreScores);

        // then
        assertThat(result).isEqualTo(4.0f); // 액션(2.5) + 스릴러(1.5)
    }

    @Test
    @DisplayName("피드백 장르 부스트 계산 - 빈 docGenres이면 0")
    void calculateGenreFeedbackBoost_WithEmptyDocGenres() {
        // given
        Set<String> emptyDocGenres = Set.of();
        Map<String, Float> genreScores = Map.of("액션", 2.5f);

        // when
        float result = calculator.calculateGenreFeedbackBoost(emptyDocGenres, genreScores);

        // then
        assertThat(result).isEqualTo(0.0f);
    }

    @Test
    @DisplayName("콘텐츠 태그 부스트 계산 - 장르별 점수 합산")
    void calculateContentTagBoost_WithGenreScores() {
        // given
        Set<String> docGenres = Set.of("액션", "스릴러");
        Map<String, Float> contentTagGenreScores = Map.of(
                "액션", 3.0f,
                "스릴러", 2.0f
        );

        // when
        float result = calculator.calculateContentTagBoost(docGenres, contentTagGenreScores);

        // then
        assertThat(result).isEqualTo(5.0f);
    }

    @Test
    @DisplayName("콘텐츠 태그 부스트 계산 - 빈 입력값이면 0")
    void calculateContentTagBoost_WithEmptyInputs() {
        // given
        Set<String> emptyDocGenres = Set.of();
        Map<String, Float> emptyScores = Map.of();

        // when
        float result1 = calculator.calculateContentTagBoost(emptyDocGenres, Map.of("액션", 1.0f));
        float result2 = calculator.calculateContentTagBoost(Set.of("액션"), emptyScores);

        // then
        assertThat(result1).isEqualTo(0.0f);
        assertThat(result2).isEqualTo(0.0f);
    }

    @Test
    @DisplayName("피드백 기반 장르 점수 계산 - LIKE 피드백")
    void calculateGenreFeedbackScores_WithLikeFeedback() {
        // given
        Feedback likeFeedback = FeedbackFixture.feedback(testMember, testContent,
                FeedbackType.LIKE);
        Map<Long, ContentMetadata> metadataCache = Map.of(1L, testMetadata);

        // when
        Map<String, Float> result = calculator.calculateGenreFeedbackScores(
                List.of(likeFeedback), metadataCache, Optional.empty());

        // then
        assertThat(result).containsEntry("액션", 1.0f);
        assertThat(result).containsEntry("스릴러", 1.0f);
    }

    @Test
    @DisplayName("피드백 기반 장르 점수 계산 - DISLIKE 피드백")
    void calculateGenreFeedbackScores_WithDislikeFeedback() {
        // given
        Feedback dislikeFeedback = FeedbackFixture.feedback(testMember, testContent,
                FeedbackType.DISLIKE);
        Map<Long, ContentMetadata> metadataCache = Map.of(1L, testMetadata);

        // when
        Map<String, Float> result = calculator.calculateGenreFeedbackScores(
                List.of(dislikeFeedback), metadataCache, Optional.empty());

        // then
        assertThat(result).containsEntry("액션", -1.0f);
        assertThat(result).containsEntry("스릴러", -1.0f);
    }

    @Test
    @DisplayName("피드백 기반 장르 점수 계산 - UNINTERESTED 피드백")
    void calculateGenreFeedbackScores_WithUninterestedFeedback() {
        // given
        Feedback uninterestedFeedback = FeedbackFixture.feedback(testMember, testContent,
                FeedbackType.UNINTERESTED);
        Map<Long, ContentMetadata> metadataCache = Map.of(1L, testMetadata);

        // when
        Map<String, Float> result = calculator.calculateGenreFeedbackScores(
                List.of(uninterestedFeedback), metadataCache, Optional.empty());

        // then
        assertThat(result).containsEntry("액션", 0.2f);
        assertThat(result).containsEntry("스릴러", 0.2f);
    }

    @Test
    @DisplayName("피드백 기반 장르 점수 계산 - 여러 피드백 누적")
    void calculateGenreFeedbackScores_WithMultipleFeedbacks() {
        // given
        Content content2 = ContentFixture.content("영화2", "설명2");
        ReflectionTestUtils.setField(content2, "id", 2L);
        ContentMetadata metadata2 = ContentMetadataFixture.metadata(content2, "넷플릭스", "액션,코미디");

        Feedback feedback1 = FeedbackFixture.feedback(testMember, testContent, FeedbackType.LIKE);
        Feedback feedback2 = FeedbackFixture.feedback(testMember, content2, FeedbackType.LIKE);

        Map<Long, ContentMetadata> metadataCache = Map.of(
                1L, testMetadata,
                2L, metadata2
        );

        // when
        Map<String, Float> result = calculator.calculateGenreFeedbackScores(
                List.of(feedback1, feedback2), metadataCache, Optional.empty());

        // then
        assertThat(result).containsEntry("액션", 2.0f); // 두 번 LIKE
        assertThat(result).containsEntry("스릴러", 1.0f); // 한 번 LIKE
        assertThat(result).containsEntry("코미디", 1.0f); // 한 번 LIKE
    }

    @Test
    @DisplayName("피드백 기반 장르 점수 계산 - 빈 피드백 리스트면 빈 맵 반환")
    void calculateGenreFeedbackScores_WithEmptyFeedbacks() {
        // given
        Map<Long, ContentMetadata> metadataCache = Map.of(1L, testMetadata);

        // when
        Map<String, Float> result1 = calculator.calculateGenreFeedbackScores(
                List.of(), metadataCache, Optional.empty());
        Map<String, Float> result2 = calculator.calculateGenreFeedbackScores(
                null, metadataCache, Optional.empty());

        // then
        assertThat(result1).isEmpty();
        assertThat(result2).isEmpty();
    }

    @Test
    @DisplayName("콘텐츠 태그 장르 점수 계산")
    void calculateContentTagGenreScores_WithContentTags() {
        // given
        Content content2 = ContentFixture.content("영화2", "설명2");
        ReflectionTestUtils.setField(content2, "id", 2L);
        ContentMetadata metadata2 = ContentMetadataFixture.metadata(content2, "넷플릭스", "액션,코미디");

        Map<Long, ContentMetadata> metadataCache = Map.of(
                1L, testMetadata,
                2L, metadata2
        );

        // when
        Map<String, Float> result = calculator.calculateContentTagGenreScores(
                List.of(1L, 2L), metadataCache);

        // then
        assertThat(result).containsEntry("액션", 2.0f); // 두 콘텐츠에 있음
        assertThat(result).containsEntry("스릴러", 1.0f); // 한 콘텐츠에 있음
        assertThat(result).containsEntry("코미디", 1.0f); // 한 콘텐츠에 있음
    }

    @Test
    @DisplayName("콘텐츠 태그 장르 점수 계산 - 빈 입력값이면 빈 맵 반환")
    void calculateContentTagGenreScores_WithEmptyInputs() {
        // given
        Map<Long, ContentMetadata> metadataCache = Map.of(1L, testMetadata);

        // when
        Map<String, Float> result1 = calculator.calculateContentTagGenreScores(
                List.of(), metadataCache);
        Map<String, Float> result2 = calculator.calculateContentTagGenreScores(
                null, metadataCache);

        // then
        assertThat(result1).isEmpty();
        assertThat(result2).isEmpty();
    }

    @Test
    @DisplayName("큐레이션 점수 계산 - 모든 요소 포함")
    void calculateCuratedScore_WithAllFactors() {
        // given
        float luceneScore = 2.0f;
        Set<String> docGenres = Set.of("액션", "스릴러");
        List<String> feedbackGenres = List.of("액션", "드라마");
        List<String> surveyGenres = List.of("스릴러", "코미디");
        Map<String, Float> feedbackScores = Map.of("액션", 1.5f, "스릴러", 2.0f);

        // when
        float result = calculator.calculateCuratedScore(luceneScore, docGenres, feedbackGenres,
                surveyGenres, feedbackScores);

        // then
        // luceneScore * 3.0 = 6.0
        // feedbackGenreBoost(1.0) * 2.0 = 2.0  (액션 매칭)
        // surveyGenreBoost = 1.0  (스릴러 매칭)
        // feedbackScore = 3.5  (액션 1.5 + 스릴러 2.0)
        // 총합 = 6.0 + 2.0 + 1.0 + 3.5 = 12.5
        assertThat(result).isEqualTo(12.5f);
    }

    @Test
    @DisplayName("일반 점수 계산 - 모든 요소 포함")
    void calculateRegularScore_WithAllFactors() {
        // given
        float luceneScore = 2.0f;
        Set<String> docGenres = Set.of("액션", "스릴러");
        List<String> memberGenres = List.of("액션", "코미디");
        Map<String, Float> feedbackScores = Map.of("액션", 1.5f, "스릴러", 2.0f);
        Map<String, Float> contentTagGenreScores = Map.of("액션", 3.0f);

        // when
        float result = calculator.calculateRegularScore(luceneScore, docGenres, memberGenres,
                feedbackScores, contentTagGenreScores);

        // then
        // luceneScore * 3.0 = 6.0
        // genreBoost(1.0) * 2.0 = 2.0  (액션 매칭)
        // feedbackScore = 3.5  (액션 1.5 + 스릴러 2.0)
        // contentTagBoost = 3.0  (액션)
        // 총합 = 6.0 + 2.0 + 3.5 + 3.0 = 14.5
        assertThat(result).isEqualTo(14.5f);
    }
}