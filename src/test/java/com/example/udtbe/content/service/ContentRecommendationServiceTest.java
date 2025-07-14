package com.example.udtbe.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentMetadataFixture;
import com.example.udtbe.common.fixture.FeedbackFixture;
import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.common.fixture.SurveyFixture;
import com.example.udtbe.domain.content.dto.ContentRecommendationResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.service.ContentRecommendationQuery;
import com.example.udtbe.domain.content.service.ContentRecommendationService;
import com.example.udtbe.domain.content.service.LuceneIndexService;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Role;
import com.example.udtbe.domain.survey.entity.Survey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ContentRecommendationServiceTest {

    @Mock
    private ContentRecommendationQuery contentRecommendationQuery;

    @Mock
    private LuceneIndexService luceneIndexService;

    @InjectMocks
    private ContentRecommendationService contentRecommendationService;

    private Member testMember;
    private Survey testSurvey;
    private List<ContentMetadata> testMetadataList;
    private Map<Long, ContentMetadata> testMetadataCache;
    private List<Content> testContents;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화 (실제 DB 데이터와 동일) 순서가 매우 중요
        testMember = createTestMember();
        testContents = createRealTestContents();
        testMetadataList = createRealTestMetadata();
        testMetadataCache = createTestMetadataCache();
        //테스트 멤버 기준으로 생성
        testSurvey = createTestSurvey();
    }

    @Nested
    @DisplayName("recommendContents(Member member, int limit) 테스트")
    class RecommendContentsTest {

        @Test
        @DisplayName("액션/스릴러 선호 사용자 - 기생충, 올드보이, 블랙팬서 높은 순위")
        void shouldRecommendActionThrillerContent() throws Exception {
            // given - 액션/스릴러 선호, 넷플릭스 사용자
            setupBasicLuceneMocks();
            Survey actionThrillerSurvey = createActionThrillerSurvey();

            when(contentRecommendationQuery.findSurveyByMemberId(testMember.getId()))
                    .thenReturn(Optional.of(actionThrillerSurvey));
            when(contentRecommendationQuery.findContentMetadataCache())
                    .thenReturn(testMetadataCache);

            mockFeedbackData();
            mockContentQueryWithOrder(List.of(1L, 2L, 8L)); // 기생충, 올드보이, 블랙팬서

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .recommendContents(testMember, 3);

            // then
            assertThat(result).hasSize(3);

            // 첫 번째 추천이 액션/스릴러 장르를 포함하는지 확인
            assertThat(result.get(0).genres())
                    .anyMatch(genre -> genre.contains("ACTION") || genre.contains("THRILLER"));

            verify(contentRecommendationQuery).findSurveyByMemberId(testMember.getId());
            verify(luceneIndexService).getIndexReader();
        }

        @Test
        @DisplayName("SF/판타지 선호 사용자 - 인터스텔라, 아바타, 스파이더맨 높은 순위")
        void shouldRecommendScienceFictionContent() throws Exception {
            // given - SF/판타지 선호, 디즈니+ 사용자
            setupBasicLuceneMocks();
            Survey sfFantasySurvey = createScienceFictionSurvey();

            when(contentRecommendationQuery.findSurveyByMemberId(testMember.getId()))
                    .thenReturn(Optional.of(sfFantasySurvey));
            when(contentRecommendationQuery.findContentMetadataCache())
                    .thenReturn(testMetadataCache);

            mockFeedbackData();
            mockContentQueryWithOrder(List.of(3L, 4L, 10L)); // 인터스텔라, 아바타, 스파이더맨

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .recommendContents(testMember, 3);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).genres())
                    .anyMatch(genre -> genre.contains("SF") || genre.contains("FANTASY"));
        }

        @Test
        @DisplayName("설문 없는 사용자 - 인기 콘텐츠 반환")
        void shouldReturnPopularContents_WhenSurveyNotExists() {
            // given
            when(contentRecommendationQuery.findSurveyByMemberId(testMember.getId()))
                    .thenReturn(Optional.empty());
            when(contentRecommendationQuery.findPopularContentMetadata(5))
                    .thenReturn(testMetadataList.subList(0, 5));

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .recommendContents(testMember, 5);

            // then
            assertThat(result).hasSize(5);
            verify(contentRecommendationQuery).findPopularContentMetadata(5);
            verifyNoInteractions(luceneIndexService);
        }
    }

    @Nested
    @DisplayName("플랫폼 필터링 테스트")
    class PlatformFilteringTest {

        @Test
        @DisplayName("넷플릭스 전용 사용자 - 넷플릭스 콘텐츠만 추천")
        void shouldFilterByNetflixOnly() throws Exception {
            // given
            setupBasicLuceneMocks();
            Survey netflixOnlySurvey = createNetflixOnlySurvey();

            when(contentRecommendationQuery.findContentMetadataCache())
                    .thenReturn(testMetadataCache);
            mockFeedbackData();
            // 넷플릭스 콘텐츠만 반환: 기생충, 올드보이, 인터스텔라, 탑건, 라라랜드 등
            mockContentQueryWithOrder(List.of(1L, 2L, 3L, 5L, 6L));

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .searchRecommendations(netflixOnlySurvey, testMember, 5);

            // then
            assertThat(result).hasSize(5);
            assertThat(result).allMatch(response ->
                    response.platforms().contains("NETFLIX"));
        }

        @Test
        @DisplayName("디즈니+ 전용 사용자 - 디즈니+ 콘텐츠만 추천")
        void shouldFilterByDisneyPlusOnly() throws Exception {
            // given
            setupBasicLuceneMocks();
            Survey disneyPlusSurvey = createDisneyPlusSurvey();

            when(contentRecommendationQuery.findContentMetadataCache())
                    .thenReturn(testMetadataCache);
            mockFeedbackData();
            // 디즈니+ 콘텐츠만 반환: 아바타, 블랙팬서
            mockContentQueryWithOrder(List.of(4L, 8L));

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .searchRecommendations(disneyPlusSurvey, testMember, 5);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(response ->
                    response.platforms().contains("DISNEY_PLUS"));
        }
    }

    @Nested
    @DisplayName("피드백 기반 추천 테스트")
    class FeedbackBasedRecommendationTest {

        @Test
        @DisplayName("스릴러 좋아요 피드백 - 다양한 장르에서 스릴러가 상위권에 위치")
        void shouldBoostThrillerGenre_WhenUserLikesThrillerContent() throws Exception {
            // given
            setupBasicLuceneMocks();
            when(contentRecommendationQuery.findContentMetadataCache())
                    .thenReturn(testMetadataCache);

            // 기생충(스릴러)에 좋아요 피드백
            mockPositiveFeedbackForThriller();
            // 모든 장르 포함: 스릴러(기생충,올드보이,겟아웃,조커), 액션(탑건,블랙팬서,스파이더맨), SF(인터스텔라,아바타), 뮤지컬(라라랜드)
            // 이때 결정된 갯수가 hasSize()랑 동일
            mockContentQueryWithOrder(List.of(1L, 2L, 7L, 9L, 5L, 8L, 10L, 3L, 4L, 6L));

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .searchRecommendations(testSurvey, testMember, 10);

            // then
            assertThat(result).hasSize(10);
            // 상위 3개 중에 스릴러 장르가 포함된 콘텐츠가 최소 2개는 있어야 함
            long thrillerCountInTop3 = result.subList(0, 3).stream()
                    .mapToLong(response -> response.genres().stream()
                            .mapToLong(genre -> genre.contains("THRILLER") ? 1L : 0L)
                            .sum())
                    .sum();
            assertThat(thrillerCountInTop3).isGreaterThanOrEqualTo(2L);
        }

        @Test
        @DisplayName("액션 싫어요 피드백 - 액션 포함한 풀에서 액션이 하위권에 위치")
        void shouldReduceActionGenreScore_WhenUserDislikesActionContent() throws Exception {
            // given
            setupBasicLuceneMocks();
            when(contentRecommendationQuery.findContentMetadataCache())
                    .thenReturn(testMetadataCache);

            // 액션 콘텐츠에 싫어요 피드백 (탑건, 블랙팬서)
            mockNegativeFeedbackForAction();
            // 비액션 우선, 액션은 하위에: 스릴러(기생충,올드보이,조커), 뮤지컬(라라랜드), SF(인터스텔라,아바타), 액션(탑건,블랙팬서,스파이더맨)
            mockContentQueryWithOrder(List.of(1L, 2L, 9L, 6L, 3L, 4L, 7L, 5L, 8L, 10L));

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .searchRecommendations(testSurvey, testMember, 8);

            // then
            assertThat(result).hasSize(8);
            // 상위 4개 중에는 액션 장르가 최대 1개만 있어야 함 (피드백으로 점수가 낮아졌으므로)
            long actionCountInTop4 = result.subList(0, 4).stream()
                    .mapToLong(response -> response.genres().stream()
                            .mapToLong(genre -> genre.contains("ACTION") ? 1L : 0L)
                            .sum())
                    .sum();
            assertThat(actionCountInTop4).isLessThanOrEqualTo(1L);

            // 하위 4개 중에는 액션 장르가 최소 2개는 있어야 함
            long actionCountInBottom4 = result.subList(4, 8).stream()
                    .mapToLong(response -> response.genres().stream()
                            .mapToLong(genre -> genre.contains("ACTION") ? 1L : 0L)
                            .sum())
                    .sum();
            assertThat(actionCountInBottom4).isGreaterThanOrEqualTo(2L);
        }
    }

    // === Helper 메서드들 ===

    private Member createTestMember() {
        Member member = MemberFixture.member("test@example.com", Role.ROLE_USER);
        ReflectionTestUtils.setField(member, "id", 1L);
        return member;
    }

    private Survey createTestSurvey() {
        return SurveyFixture.actionThrillerSurvey(testMember);
    }

    private Survey createActionThrillerSurvey() {
        return SurveyFixture.actionThrillerSurvey(testMember);
    }

    private Survey createScienceFictionSurvey() {
        return SurveyFixture.scienceFictionSurvey(testMember);
    }

    private Survey createNetflixOnlySurvey() {
        return SurveyFixture.netflixOnlySurvey(testMember);
    }

    private Survey createDisneyPlusSurvey() {
        return SurveyFixture.disneyPlusSurvey(testMember);
    }

    //10개의 초기 영화 데이터 세팅
    private List<Content> createRealTestContents() {
        return ContentFixture.allTestMovies();
    }

    private List<ContentMetadata> createRealTestMetadata() {
        return ContentMetadataFixture.allTestMetadata();
    }

    private Map<Long, ContentMetadata> createTestMetadataCache() {
        return testMetadataList.stream()
                .collect(Collectors.toMap(
                        metadata -> metadata.getContent().getId(),
                        metadata -> metadata
                ));
    }

    private void setupBasicLuceneMocks() throws Exception {
        KoreanAnalyzer analyzer = new KoreanAnalyzer();
        Directory directory = new ByteBuffersDirectory();

        // 실제 인덱스에 테스트 데이터 추가
        IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer));

        // 각 테스트 콘텐츠를 인덱스에 추가
        for (ContentMetadata metadata : testMetadataList) {
            Document doc = new Document();
            doc.add(new StringField("contentId", metadata.getContent().getId().toString(),
                    Field.Store.YES));
            doc.add(new TextField("title", metadata.getTitle(), Field.Store.YES));
            doc.add(new TextField("genreTag", String.join(",", metadata.getGenreTag()),
                    Field.Store.YES));
            doc.add(new TextField("platformTag", String.join(",", metadata.getPlatformTag()),
                    Field.Store.YES));
            writer.addDocument(doc);
        }
        writer.close();

        DirectoryReader reader = DirectoryReader.open(directory);

        when(luceneIndexService.getAnalyzer()).thenReturn(analyzer);
        when(luceneIndexService.getIndexReader()).thenReturn(reader);
    }

    private void mockContentQueryWithOrder(List<Long> contentIds) {
        List<Content> orderedContents = contentIds.stream()
                .map(id -> testContents.stream()
                        .filter(content -> content.getId().equals(id))
                        .findFirst()
                        .orElseThrow())
                .collect(Collectors.toList());

        when(contentRecommendationQuery.findContentsByIds(anyList()))
                .thenReturn(orderedContents);
    }

    private void mockFeedbackData() {
        // 기본 피드백 데이터 (필요에 따라 각 테스트에서 오버라이드)
        when(contentRecommendationQuery.findFeedbacksByMemberId(testMember.getId()))
                .thenReturn(new ArrayList<>());
    }

    private void mockPositiveFeedbackForThriller() {
        List<Feedback> feedbacks = List.of(
                createFeedback(testContents.get(0), FeedbackType.LIKE) // 기생충 좋아요
        );
        when(contentRecommendationQuery.findFeedbacksByMemberId(testMember.getId()))
                .thenReturn(feedbacks);
    }

    private void mockNegativeFeedbackForAction() {
        List<Feedback> feedbacks = List.of(
                createFeedback(testContents.get(4), FeedbackType.DISLIKE), // 탑건 싫어요
                createFeedback(testContents.get(7), FeedbackType.DISLIKE)  // 블랙팬서 싫어요
        );
        when(contentRecommendationQuery.findFeedbacksByMemberId(testMember.getId()))
                .thenReturn(feedbacks);
    }

    private Feedback createFeedback(Content content, FeedbackType type) {
        return FeedbackFixture.feedback(testMember, content, type);
    }

    @Test
    @DisplayName("예외 상황 - Lucene 인덱스 읽기 실패시 인기 콘텐츠 fallback 전략")
    void shouldFallbackToPopularContents_WhenLuceneIndexFails() throws Exception {
        // given
        when(contentRecommendationQuery.findSurveyByMemberId(testMember.getId()))
                .thenReturn(Optional.of(testSurvey));
        when(luceneIndexService.getIndexReader())
                .thenThrow(new IOException("인덱스 읽기 실패"));
        when(contentRecommendationQuery.findPopularContentMetadata(5))
                .thenReturn(testMetadataList.subList(0, 5));

        // when
        List<ContentRecommendationResponse> result = contentRecommendationService
                .recommendContents(testMember, 5);

        // then
        assertThat(result).hasSize(5);
        verify(contentRecommendationQuery).findPopularContentMetadata(5);
    }

    @Test
    @DisplayName("장르 태그가 없는 콘텐츠(이상치 데이터) 처리")
    void shouldHandleNullGenreTags() throws Exception {
        // given
        setupBasicLuceneMocks();
        ContentMetadata metadataWithNullGenre = ContentMetadataFixture.customMetadata(
                testContents.get(0), "테스트 콘텐츠", "15세이상관람가",
                "", "NETFLIX", "테스트 감독", List.of("MOVIE"), List.of("테스트 배우")
        );

        Map<Long, ContentMetadata> cacheWithNull = Map.of(1L, metadataWithNullGenre);

        when(contentRecommendationQuery.findContentMetadataCache())
                .thenReturn(cacheWithNull);

        mockFeedbackData();
        mockContentQueryWithOrder(List.of(1L));

        // when
        List<ContentRecommendationResponse> result = contentRecommendationService
                .searchRecommendations(testSurvey, testMember, 1);

        // then 예외가 발생하지 않고 정상 처리되어야..
        assertThat(result).hasSize(1);

    }

    @Test
    @DisplayName("빈 플랫폼 태그로 검색시 모든 콘텐츠 반환")
    void shouldReturnAllContents_WhenPlatformTagsEmpty() throws Exception {
        // given
        setupBasicLuceneMocks();
        Survey surveyWithEmptyPlatforms = SurveyFixture.emptyPlatformSurvey(testMember);

        when(contentRecommendationQuery.findContentMetadataCache())
                .thenReturn(testMetadataCache);

        mockFeedbackData();

        List<Long> allContentIds = testMetadataList.stream()
                .map(metadata -> metadata.getContent().getId())
                .collect(Collectors.toList());
        mockContentQueryWithOrder(allContentIds);

        // when
        List<ContentRecommendationResponse> result = contentRecommendationService
                .searchRecommendations(surveyWithEmptyPlatforms, testMember, 10);

        // then
        assertThat(result).hasSize(10); // 모든 콘텐츠 반환
    }
}