package com.example.udtbe.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentMetadataFixture;
import com.example.udtbe.common.fixture.FeedbackFixture;
import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.common.fixture.SurveyFixture;
import com.example.udtbe.domain.content.dto.response.ContentRecommendationResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.service.ContentRecommendationQuery;
import com.example.udtbe.domain.content.service.ContentRecommendationService;
import com.example.udtbe.domain.content.service.LuceneIndexService;
import com.example.udtbe.domain.content.service.LuceneSearchService;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Role;
import com.example.udtbe.domain.survey.entity.Survey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHits;
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

    @Mock
    private LuceneSearchService luceneSearchService;

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
            Survey actionThrillerSurvey = createActionThrillerSurvey();

            when(contentRecommendationQuery.findSurveyByMemberId(testMember.getId()))
                    .thenReturn(actionThrillerSurvey);
            when(contentRecommendationQuery.findContentMetadataCache())
                    .thenReturn(testMetadataCache);

            // LuceneSearchService Mock 설정
            mockLuceneSearchService(List.of(1L, 2L, 8L));
            mockFeedbackData();
            mockContentQueryWithOrder(List.of(1L, 2L, 8L));

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .recommendContents(testMember, 3);

            // then
            assertThat(result).hasSize(3);

            // 첫 번째 추천이 액션/스릴러 장르를 포함하는지 확인
            assertThat(result.get(0).genres())
                    .anyMatch(genre -> genre.contains("액션") || genre.contains("스릴러"));

            verify(contentRecommendationQuery).findSurveyByMemberId(testMember.getId());
            verify(luceneSearchService).searchRecommendations(anyList(), anyList(), anyInt());
        }

        @Test
        @DisplayName("SF/판타지 선호 사용자 - 인터스텔라, 아바타, 스파이더맨 높은 순위")
        void shouldRecommendScienceFictionContent() throws Exception {
            // given - SF/판타지 선호, 디즈니+ 사용자
            Survey sfFantasySurvey = createScienceFictionSurvey();

            when(contentRecommendationQuery.findSurveyByMemberId(testMember.getId()))
                    .thenReturn(sfFantasySurvey);
            when(contentRecommendationQuery.findContentMetadataCache())
                    .thenReturn(testMetadataCache);

            mockLuceneSearchService(List.of(3L, 4L, 10L));
            mockFeedbackData();
            mockContentQueryWithOrder(List.of(3L, 4L, 10L));

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .recommendContents(testMember, 3);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).genres())
                    .anyMatch(genre -> genre.contains("SF") || genre.contains("판타지"));

            verify(luceneSearchService).searchRecommendations(anyList(), anyList(), anyInt());
        }

        @Test
        @DisplayName("설문 없는 사용자 - 인기 콘텐츠 반환")
        void shouldReturnPopularContents_WhenSurveyNotExists() {
            // given
            when(contentRecommendationQuery.findSurveyByMemberId(testMember.getId()))
                    .thenReturn(null); // 설문이 없는 경우
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
            Survey netflixOnlySurvey = createNetflixOnlySurvey();

            when(contentRecommendationQuery.findContentMetadataCache())
                    .thenReturn(testMetadataCache);

            mockLuceneSearchService(List.of(1L, 2L, 3L, 5L, 6L));
            mockFeedbackData();
            mockContentQueryWithOrder(List.of(1L, 2L, 3L, 5L, 6L));

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .searchRecommendations(netflixOnlySurvey, testMember, 5);

            // then
            assertThat(result).hasSize(5);
            assertThat(result).allMatch(response ->
                    response.platforms().contains("넷플릭스"));

            verify(luceneSearchService).searchRecommendations(anyList(), anyList(), anyInt());
        }

        @Test
        @DisplayName("디즈니+ 전용 사용자 - 디즈니+ 콘텐츠만 추천")
        void shouldFilterByDisneyPlusOnly() throws Exception {
            // given
            Survey disneyPlusSurvey = createDisneyPlusSurvey();

            when(contentRecommendationQuery.findContentMetadataCache())
                    .thenReturn(testMetadataCache);

            mockLuceneSearchService(List.of(4L, 8L));
            mockFeedbackData();
            mockContentQueryWithOrder(List.of(4L, 8L));

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .searchRecommendations(disneyPlusSurvey, testMember, 5);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(response ->
                    response.platforms().contains("디즈니+"));

            verify(luceneSearchService).searchRecommendations(anyList(), anyList(), anyInt());
        }
    }

    @Nested
    @DisplayName("피드백 기반 추천 테스트")
    class FeedbackBasedRecommendationTest {

        @Test
        @DisplayName("스릴러 좋아요 피드백 - 다양한 장르에서 스릴러가 상위권에 위치")
        void shouldBoostThrillerGenre_WhenUserLikesThrillerContent() throws Exception {
            // given
            when(contentRecommendationQuery.findContentMetadataCache())
                    .thenReturn(testMetadataCache);

            mockPositiveFeedbackForThriller();
            mockLuceneSearchService(List.of(1L, 2L, 7L, 9L, 5L, 8L, 10L, 3L, 4L, 6L));
            mockContentQueryWithOrder(List.of(1L, 2L, 7L, 9L, 5L, 8L, 10L, 3L, 4L, 6L));

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .searchRecommendations(testSurvey, testMember, 10);

            // then
            assertThat(result).hasSize(10);
            // 상위 3개 중에 스릴러 장르가 포함된 콘텐츠가 최소 2개는 있어야 함
            long thrillerCountInTop3 = result.subList(0, 3).stream()
                    .mapToLong(response -> response.genres().stream()
                            .mapToLong(genre -> genre.contains("스릴러") ? 1L : 0L)
                            .sum())
                    .sum();
            assertThat(thrillerCountInTop3).isGreaterThanOrEqualTo(2L);

            verify(luceneSearchService).searchRecommendations(anyList(), anyList(), anyInt());
        }

        @Test
        @DisplayName("액션 싫어요 피드백 - 액션 포함한 풀에서 액션이 하위권에 위치")
        void shouldReduceActionGenreScore_WhenUserDislikesActionContent() throws Exception {
            // given
            when(contentRecommendationQuery.findContentMetadataCache())
                    .thenReturn(testMetadataCache);

            mockNegativeFeedbackForAction();
            mockLuceneSearchService(List.of(1L, 2L, 6L, 3L, 5L, 8L, 10L, 4L, 7L, 9L));
            mockContentQueryWithOrder(List.of(1L, 2L, 6L, 3L, 5L, 8L, 10L, 4L, 7L, 9L));

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .searchRecommendations(testSurvey, testMember, 10);

            // then
            assertThat(result).hasSize(10);
            // 상위 4개 중에는 액션 장르가 최대 1개만 있어야 함 (피드백으로 점수가 낮아졌으므로)
            long actionCountInTop4 = result.subList(0, 4).stream()
                    .mapToLong(response -> response.genres().stream()
                            .mapToLong(genre -> genre.contains("액션") ? 1L : 0L)
                            .sum())
                    .sum();
            assertThat(actionCountInTop4).isLessThanOrEqualTo(1L);

            // 하위 4개 중에는 액션 장르가 최소 2개는 있어야 함
            long actionCountInBottom4 = result.subList(4, 8).stream()
                    .mapToLong(response -> response.genres().stream()
                            .mapToLong(genre -> genre.contains("액션") ? 1L : 0L)
                            .sum())
                    .sum();
            assertThat(actionCountInBottom4).isGreaterThanOrEqualTo(2L);

            verify(luceneSearchService).searchRecommendations(anyList(), anyList(), anyInt());
        }
    }

    // === Helper 메서드들 ===

    private void mockLuceneSearchService(List<Long> contentIds) throws Exception {
        ScoreDoc[] scoreDocs = new ScoreDoc[contentIds.size()];
        for (int i = 0; i < contentIds.size(); i++) {
            scoreDocs[i] = new ScoreDoc(i, 1.0f + (contentIds.size() - i) * 0.1f); // 점수 내림차순
        }
        TotalHits totalHits = new TotalHits(scoreDocs.length, TotalHits.Relation.EQUAL_TO);
        TopDocs topDocs = new TopDocs(totalHits, scoreDocs);

        when(luceneSearchService.searchRecommendations(anyList(), anyList(), anyInt()))
                .thenReturn(topDocs);

        KoreanAnalyzer analyzer = new KoreanAnalyzer();
        Directory directory = new ByteBuffersDirectory();

        IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer));

        for (int i = 0; i < contentIds.size(); i++) {
            Long contentId = contentIds.get(i);
            ContentMetadata metadata = testMetadataCache.get(contentId);
            if (metadata != null) {
                Document doc = new Document();
                doc.add(new StringField("contentId", contentId.toString(), Field.Store.YES));
                doc.add(new TextField("title", metadata.getTitle(), Field.Store.YES));
                doc.add(new TextField("genreTag", String.join(",", metadata.getGenreTag()),
                        Field.Store.YES));
                doc.add(new TextField("platformTag", String.join(",", metadata.getPlatformTag()),
                        Field.Store.YES));
                writer.addDocument(doc);
            }
        }

        if (contentIds.isEmpty()) {
            Document emptyDoc = new Document();
            emptyDoc.add(new StringField("contentId", "0", Field.Store.YES));
            emptyDoc.add(new TextField("title", "empty", Field.Store.YES));
            emptyDoc.add(new TextField("genreTag", "", Field.Store.YES));
            emptyDoc.add(new TextField("platformTag", "", Field.Store.YES));
            writer.addDocument(emptyDoc);
        }

        writer.close();

        DirectoryReader reader = DirectoryReader.open(directory);
        when(luceneIndexService.getIndexReader()).thenReturn(reader);
    }

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
                .thenReturn(testSurvey);
        when(luceneSearchService.searchRecommendations(anyList(), anyList(), anyInt()))
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
        ContentMetadata metadataWithNullGenre = ContentMetadataFixture.customMetadata(
                testContents.get(0), "테스트 콘텐츠", "15세이상관람가",
                "", "넷플릭스", "테스트 감독", List.of("영화"), List.of("테스트 배우")
        );

        Map<Long, ContentMetadata> cacheWithNull = Map.of(1L, metadataWithNullGenre);

        when(contentRecommendationQuery.findContentMetadataCache())
                .thenReturn(cacheWithNull);

        mockLuceneSearchService(List.of(1L));
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
        Survey surveyWithEmptyPlatforms = SurveyFixture.emptyPlatformSurvey(testMember);

        when(contentRecommendationQuery.findContentMetadataCache())
                .thenReturn(testMetadataCache);

        List<Long> allContentIds = testMetadataList.stream()
                .map(metadata -> metadata.getContent().getId())
                .collect(Collectors.toList());

        mockLuceneSearchService(allContentIds);
        mockFeedbackData();
        mockContentQueryWithOrder(allContentIds);

        // when
        List<ContentRecommendationResponse> result = contentRecommendationService
                .searchRecommendations(surveyWithEmptyPlatforms, testMember, 10);

        // then
        assertThat(result).hasSize(10); // 모든 콘텐츠 반환
    }

    @Nested
    @DisplayName("영어→한국어 변환 로직 검증 테스트")
    class EnumConversionTest {

        @Test
        @DisplayName("GenreType.toKoreanTypes() 메서드 호출 검증")
        void shouldConvertEnglishGenresToKorean() throws Exception {
            // given
            Survey englishGenreSurvey = SurveyFixture.actionThrillerSurvey(testMember);

            when(contentRecommendationQuery.findContentMetadataCache())
                    .thenReturn(testMetadataCache);

            mockLuceneSearchService(List.of(1L, 2L, 5L));
            mockFeedbackData();
            mockContentQueryWithOrder(List.of(1L, 2L, 5L));

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .searchRecommendations(englishGenreSurvey, testMember, 3);

            // then
            assertThat(result).hasSize(3);

            assertThat(result).allSatisfy(response -> {
                assertThat(response.genres()).isNotEmpty();
            });

            verify(luceneSearchService).searchRecommendations(anyList(), anyList(), anyInt());
        }

        @Test
        @DisplayName("PlatformType.toKoreanTypes() 메서드 호출 검증")
        void shouldConvertEnglishPlatformsToKorean() throws Exception {
            // given
            Survey englishPlatformSurvey = SurveyFixture.netflixOnlySurvey(testMember);

            when(contentRecommendationQuery.findContentMetadataCache())
                    .thenReturn(testMetadataCache);

            mockLuceneSearchService(List.of(1L, 2L, 3L));
            mockFeedbackData();
            mockContentQueryWithOrder(List.of(1L, 2L, 3L));

            // when
            List<ContentRecommendationResponse> result = contentRecommendationService
                    .searchRecommendations(englishPlatformSurvey, testMember, 3);

            // then
            assertThat(result).hasSize(3);
            assertThat(result).allMatch(response ->
                    response.platforms().contains("넷플릭스"));

            verify(luceneSearchService).searchRecommendations(anyList(), anyList(), anyInt());
        }
    }
}