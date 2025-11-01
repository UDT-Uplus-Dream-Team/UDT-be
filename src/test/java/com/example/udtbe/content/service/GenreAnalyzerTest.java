package com.example.udtbe.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentMetadataFixture;
import com.example.udtbe.common.fixture.FeedbackFixture;
import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.service.GenreAnalyzer;
import com.example.udtbe.domain.content.service.RecommendationScoreCalculator;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GenreAnalyzerTest {

    @Mock
    private RecommendationScoreCalculator scoreCalculator;

    @InjectMocks
    private GenreAnalyzer genreAnalyzer;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = MemberFixture.member("test@example.com", Role.ROLE_USER);
    }

    @Test
    @DisplayName("선호 장르 추출 - 긍정적 피드백이 많은 장르 순으로 반환")
    void extractPreferredGenresFromFeedback_WithPositiveFeedback() {
        // given
        Content content1 = createContentWithId(1L, "영화1");
        Content content2 = createContentWithId(2L, "영화2");
        Content content3 = createContentWithId(3L, "영화3");

        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스", "액션,스릴러");
        ContentMetadata metadata2 = ContentMetadataFixture.metadata(content2, "넷플릭스", "액션,코미디");
        ContentMetadata metadata3 = ContentMetadataFixture.metadata(content3, "넷플릭스", "드라마");

        // 액션: 2회 LIKE, 스릴러: 1회 LIKE, 코미디: 1회 LIKE, 드라마: 1회 LIKE
        Feedback feedback1 = FeedbackFixture.feedback(testMember, content1, FeedbackType.LIKE);
        Feedback feedback2 = FeedbackFixture.feedback(testMember, content2, FeedbackType.LIKE);
        Feedback feedback3 = FeedbackFixture.feedback(testMember, content3, FeedbackType.LIKE);

        Map<Long, ContentMetadata> metadataCache = Map.of(
                1L, metadata1,
                2L, metadata2,
                3L, metadata3
        );

        // Mock: 액션이 가장 높은 점수로 반환되도록 설정
        when(scoreCalculator.calculateGenreFeedbackScores(any(), eq(metadataCache),
                eq(Optional.of(20))))
                .thenReturn(Map.of(
                        "액션", 2.0f,
                        "스릴러", 1.0f,
                        "코미디", 1.0f
                ));

        // when
        List<String> result = genreAnalyzer.extractPreferredGenresFromFeedback(
                List.of(feedback1, feedback2, feedback3), metadataCache);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo("액션"); // 2회로 가장 많음
    }

    @Test
    @DisplayName("선호 장르 추출 - 부정적 피드백은 제외")
    void extractPreferredGenresFromFeedback_ExcludesNegativeFeedback() {
        // given
        Content content1 = createContentWithId(1L, "영화1");
        Content content2 = createContentWithId(2L, "영화2");

        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스", "액션");
        ContentMetadata metadata2 = ContentMetadataFixture.metadata(content2, "넷플릭스", "공포");

        Feedback like = FeedbackFixture.feedback(testMember, content1, FeedbackType.LIKE);
        Feedback dislike = FeedbackFixture.feedback(testMember, content2, FeedbackType.DISLIKE);

        Map<Long, ContentMetadata> metadataCache = Map.of(
                1L, metadata1,
                2L, metadata2
        );

        when(scoreCalculator.calculateGenreFeedbackScores(any(), eq(metadataCache),
                eq(Optional.of(20))))
                .thenReturn(Map.of("액션", 1.0f));

        // when
        List<String> result = genreAnalyzer.extractPreferredGenresFromFeedback(
                List.of(like, dislike), metadataCache);

        // then
        assertThat(result).contains("액션");
        assertThat(result).doesNotContain("공포");
    }

    @Test
    @DisplayName("선호 장르 추출 - 선호 장르가 없으면 fallback 사용 (LIKE 없음)")
    void extractPreferredGenresFromFeedback_UsesFallbackWhenNoPreferredGenres_NoLikes() {
        // given
        Content content1 = createContentWithId(1L, "영화1");

        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스", "액션");

        // 모든 피드백이 DISLIKE여서 선호 장르 점수가 음수
        Feedback dislike = FeedbackFixture.feedback(testMember, content1, FeedbackType.DISLIKE);

        Map<Long, ContentMetadata> metadataCache = Map.of(1L, metadata1);

        // Mock: 모든 장르 점수가 음수 또는 빈 맵 반환 (선호 장르 없음)
        when(scoreCalculator.calculateGenreFeedbackScores(any(), eq(metadataCache),
                eq(Optional.of(20))))
                .thenReturn(Map.of("액션", -1.0f));

        // when
        List<String> result = genreAnalyzer.extractPreferredGenresFromFeedback(
                List.of(dislike), metadataCache);

        // then
        // fallback은 최근 LIKE 기반이지만, LIKE가 없으므로 빈 리스트
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("선호 장르 추출 - 점수가 0 이하면 fallback으로 최근 LIKE 장르 사용")
    void extractPreferredGenresFromFeedback_UsesFallbackWhenScoresAreNegative() {
        // given
        Content likedContent = createContentWithId(1L, "좋아한 영화");
        Content dislikedContent = createContentWithId(2L, "싫어한 영화");

        ContentMetadata likedMetadata = ContentMetadataFixture.metadata(likedContent, "넷플릭스",
                "코미디,로맨스");
        ContentMetadata dislikedMetadata = ContentMetadataFixture.metadata(dislikedContent,
                "넷플릭스", "공포");

        // LIKE 1개, DISLIKE 3개 → 점수 계산 결과 모두 음수
        LocalDateTime now = LocalDateTime.now();
        Feedback like = FeedbackFixture.feedbackWithTime(testMember, likedContent,
                FeedbackType.LIKE, now.minusDays(1), now.minusDays(1));
        Feedback dislike1 = FeedbackFixture.feedback(testMember, dislikedContent,
                FeedbackType.DISLIKE);
        Feedback dislike2 = FeedbackFixture.feedback(testMember, dislikedContent,
                FeedbackType.DISLIKE);
        Feedback dislike3 = FeedbackFixture.feedback(testMember, dislikedContent,
                FeedbackType.DISLIKE);

        Map<Long, ContentMetadata> metadataCache = Map.of(
                1L, likedMetadata,
                2L, dislikedMetadata
        );

        // Mock: 모든 장르 점수가 음수 (DISLIKE가 너무 많아서)
        when(scoreCalculator.calculateGenreFeedbackScores(any(), eq(metadataCache),
                eq(Optional.of(20))))
                .thenReturn(Map.of(
                        "코미디", -1.0f,
                        "로맨스", -0.5f,
                        "공포", -3.0f
                ));

        // when
        List<String> result = genreAnalyzer.extractPreferredGenresFromFeedback(
                List.of(like, dislike1, dislike2, dislike3), metadataCache);

        // then
        // fallback 동작: 최근 LIKE 기반 장르 추출
        assertThat(result).isNotEmpty();
        assertThat(result).containsAnyOf("코미디", "로맨스"); // LIKE한 영화의 장르
        assertThat(result).doesNotContain("공포"); // DISLIKE한 영화의 장르는 제외
    }

    @Test
    @DisplayName("Fallback 장르 추출 - 최근 LIKE 피드백 기반")
    void extractFallbackGenresFromRecentLikes() {
        // given
        Content content1 = createContentWithId(1L, "영화1");
        Content content2 = createContentWithId(2L, "영화2");
        Content content3 = createContentWithId(3L, "영화3");

        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스", "액션,스릴러");
        ContentMetadata metadata2 = ContentMetadataFixture.metadata(content2, "넷플릭스", "코미디");
        ContentMetadata metadata3 = ContentMetadataFixture.metadata(content3, "넷플릭스", "드라마");

        LocalDateTime now = LocalDateTime.now();
        Feedback like1 = FeedbackFixture.feedbackWithTime(testMember, content1, FeedbackType.LIKE,
                now.minusDays(1), now.minusDays(1));
        Feedback like2 = FeedbackFixture.feedbackWithTime(testMember, content2, FeedbackType.LIKE,
                now.minusDays(2), now.minusDays(2));
        Feedback like3 = FeedbackFixture.feedbackWithTime(testMember, content3, FeedbackType.LIKE,
                now.minusDays(3), now.minusDays(3));

        Map<Long, ContentMetadata> metadataCache = Map.of(
                1L, metadata1,
                2L, metadata2,
                3L, metadata3
        );

        // when
        List<String> result = genreAnalyzer.extractFallbackGenresFromRecentLikes(
                List.of(like1, like2, like3), metadataCache);

        // then
        assertThat(result).hasSize(3); // 최대 3개
        assertThat(result).contains("액션", "스릴러", "코미디");
    }

    @Test
    @DisplayName("Fallback 장르 추출 - DISLIKE는 제외")
    void extractFallbackGenresFromRecentLikes_ExcludesDislike() {
        // given
        Content content1 = createContentWithId(1L, "영화1");
        Content content2 = createContentWithId(2L, "영화2");

        ContentMetadata metadata1 = ContentMetadataFixture.metadata(content1, "넷플릭스", "액션");
        ContentMetadata metadata2 = ContentMetadataFixture.metadata(content2, "넷플릭스", "공포");

        Feedback like = FeedbackFixture.feedback(testMember, content1, FeedbackType.LIKE);
        Feedback dislike = FeedbackFixture.feedback(testMember, content2, FeedbackType.DISLIKE);

        Map<Long, ContentMetadata> metadataCache = Map.of(
                1L, metadata1,
                2L, metadata2
        );

        // when
        List<String> result = genreAnalyzer.extractFallbackGenresFromRecentLikes(
                List.of(like, dislike), metadataCache);

        // then
        assertThat(result).contains("액션");
        assertThat(result).doesNotContain("공포");
    }

    @Test
    @DisplayName("장르 태그 파싱 - 쉼표로 구분된 문자열을 Set으로 변환")
    void parseGenreTags_WithCommaSeparatedString() {
        // given
        String genreTag = "액션, 스릴러, 드라마";

        // when
        Set<String> result = genreAnalyzer.parseGenreTags(genreTag);

        // then
        assertThat(result).containsExactlyInAnyOrder("액션", "스릴러", "드라마");
    }

    @Test
    @DisplayName("장르 태그 파싱 - 빈 문자열이면 빈 Set 반환")
    void parseGenreTags_WithEmptyString() {
        // when
        Set<String> result1 = genreAnalyzer.parseGenreTags("");
        Set<String> result2 = genreAnalyzer.parseGenreTags("   ");
        Set<String> result3 = genreAnalyzer.parseGenreTags(null);

        // then
        assertThat(result1).isEmpty();
        assertThat(result2).isEmpty();
        assertThat(result3).isEmpty();
    }

    @Test
    @DisplayName("장르 태그 파싱 - 공백이 섞여있어도 정상 파싱")
    void parseGenreTags_WithWhitespace() {
        // given
        String genreTag = " 액션 ,  스릴러  , 드라마 ";

        // when
        Set<String> result = genreAnalyzer.parseGenreTags(genreTag);

        // then
        assertThat(result).containsExactlyInAnyOrder("액션", "스릴러", "드라마");
    }

    @Test
    @DisplayName("장르 태그 파싱 - 중복 제거")
    void parseGenreTags_RemovesDuplicates() {
        // given
        String genreTag = "액션,액션,스릴러";

        // when
        Set<String> result = genreAnalyzer.parseGenreTags(genreTag);

        // then
        assertThat(result).containsExactlyInAnyOrder("액션", "스릴러");
        assertThat(result).hasSize(2);
    }

    // === Helper 메서드들 ===

    private Content createContentWithId(Long id, String title) {
        Content content = ContentFixture.content(title, "설명");
        ReflectionTestUtils.setField(content, "id", id);
        return content;
    }
}