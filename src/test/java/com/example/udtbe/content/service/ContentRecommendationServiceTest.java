package com.example.udtbe.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.udtbe.common.fixture.ContentFixture;
import com.example.udtbe.common.fixture.ContentMetadataFixture;
import com.example.udtbe.common.fixture.MemberFixture;
import com.example.udtbe.common.fixture.SurveyFixture;
import com.example.udtbe.domain.content.dto.common.ContentRecommendationDTO;
import com.example.udtbe.domain.content.dto.response.ContentRecommendationResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.service.ContentRecommendationQuery;
import com.example.udtbe.domain.content.service.ContentRecommendationService;
import com.example.udtbe.domain.content.service.GenreAnalyzer;
import com.example.udtbe.domain.content.service.LuceneIndexService;
import com.example.udtbe.domain.content.service.LuceneSearchService;
import com.example.udtbe.domain.content.service.RecommendationQueryBuilder;
import com.example.udtbe.domain.content.service.RecommendationResponseBuilder;
import com.example.udtbe.domain.content.service.RecommendationScoreCalculator;
import com.example.udtbe.domain.content.util.MemberRecommendationCache;
import com.example.udtbe.domain.content.util.RecommendationCacheManager;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.enums.Role;
import com.example.udtbe.domain.survey.entity.Survey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ContentRecommendationServiceTest {

    // === Mock: 외부 의존성만 Mock ===
    @Mock
    private ContentRecommendationQuery contentRecommendationQuery;

    @Mock
    private LuceneIndexService luceneIndexService;

    @Mock
    private LuceneSearchService luceneSearchService;

    @Mock
    private RecommendationCacheManager cacheManager;

    // === 실제 객체: 비즈니스 로직 컴포넌트 ===
    private RecommendationScoreCalculator scoreCalculator;
    private GenreAnalyzer genreAnalyzer;
    private RecommendationQueryBuilder queryBuilder;
    private RecommendationResponseBuilder responseBuilder;

    private ContentRecommendationService contentRecommendationService;

    private Member testMember;
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

        // 비즈니스 로직 컴포넌트를 실제 객체로 초기화
        scoreCalculator = new RecommendationScoreCalculator();
        genreAnalyzer = new GenreAnalyzer(scoreCalculator);
        queryBuilder = new RecommendationQueryBuilder();
        responseBuilder = new RecommendationResponseBuilder(contentRecommendationQuery);

        // ContentRecommendationService 수동 생성 (Mock + 실제 객체 조합)
        contentRecommendationService = new ContentRecommendationService(
                contentRecommendationQuery,
                luceneIndexService,
                luceneSearchService,
                cacheManager,
                scoreCalculator,
                genreAnalyzer,
                queryBuilder,
                responseBuilder
        );
    }

    @Test
    @DisplayName("캐시 히트 - 기존 캐시에서 다음 배치 반환")
    void shouldReturnCachedRecommendations_WhenCacheHit() {
        // given - 유효한 캐시 존재
        List<ContentRecommendationDTO> cachedRecommendations = List.of(
                new ContentRecommendationDTO(1L, 5.0f),
                new ContentRecommendationDTO(2L, 4.5f),
                new ContentRecommendationDTO(3L, 4.0f)
        );

        MemberRecommendationCache mockCache = mock(MemberRecommendationCache.class);
        when(mockCache.shouldRefresh()).thenReturn(false);
        when(mockCache.getNext()).thenReturn(cachedRecommendations);
        when(cacheManager.getCache(testMember.getId())).thenReturn(mockCache);
        when(contentRecommendationQuery.findContentMetadataCache())
                .thenReturn(testMetadataCache);

        // ResponseBuilder가 실제 객체이므로 findContentsByIds Mock 설정
        List<Long> ids = cachedRecommendations.stream()
                .map(ContentRecommendationDTO::contentId)
                .toList();
        List<Content> expectedContents = testContents.stream()
                .filter(c -> ids.contains(c.getId()))
                .toList();
        when(contentRecommendationQuery.findContentsByIds(ids))
                .thenReturn(expectedContents);

        // when
        List<ContentRecommendationResponse> result = contentRecommendationService
                .recommendContents(testMember, 3);

        // then
        assertThat(result).hasSize(3);
        verify(cacheManager).getCache(testMember.getId());
        verify(mockCache).getNext();
        verifyNoInteractions(luceneSearchService); // Lucene 검색이 호출되지 않아야 함
    }

    @Test
    @DisplayName("액션/스릴러 선호 사용자 - 기생충, 올드보이, 블랙팬서 높은 순위")
    void shouldRecommendActionThrillerContent() throws Exception {
        // given - 액션/스릴러 선호, 넷플릭스 사용자
        Survey actionThrillerSurvey = createActionThrillerSurvey();

        when(cacheManager.getCache(testMember.getId())).thenReturn(null);
        when(contentRecommendationQuery.findSurveyByMemberId(testMember.getId()))
                .thenReturn(actionThrillerSurvey);
        when(contentRecommendationQuery.findContentMetadataCache())
                .thenReturn(testMetadataCache);
        when(contentRecommendationQuery.findFeedbacksByMemberId(testMember.getId()))
                .thenReturn(new ArrayList<>());

        // ResponseBuilder가 실제 객체이므로 findContentsByIds Mock 설정
        when(contentRecommendationQuery.findContentsByIds(anyList()))
                .thenAnswer(invocation -> {
                    List<Long> requestedIds = invocation.getArgument(0);
                    return testContents.stream()
                            .filter(c -> requestedIds.contains(c.getId()))
                            .toList();
                });

        // LuceneSearchService Mock 설정
        mockLuceneSearchService(List.of(1L, 2L, 8L));

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
    @DisplayName("사용자 추천 캐시 삭제")
    void shouldClearUserCache_WhenCacheExists() {
        // given
        when(cacheManager.hasCache(testMember.getId())).thenReturn(true);

        // when
        contentRecommendationService.clearMyRecommendationCache(testMember);

        // then
        verify(cacheManager).hasCache(testMember.getId());
        verify(cacheManager).removeMemberCache(testMember.getId());
    }

    // === Helper 메서드들 ===

    private void mockLuceneSearchService(List<Long> contentIds) throws Exception {
        ScoreDoc[] scoreDocs = new ScoreDoc[contentIds.size()];
        for (int i = 0; i < contentIds.size(); i++) {
            scoreDocs[i] = new ScoreDoc(i, 1.0f + (contentIds.size() - i) * 0.1f);
        }
        TotalHits totalHits = new TotalHits(scoreDocs.length, TotalHits.Relation.EQUAL_TO);
        TopDocs topDocs = new TopDocs(totalHits, scoreDocs);

        when(luceneSearchService.searchRecommendations(anyList(), anyList(), anyInt()))
                .thenReturn(topDocs);

        // Lucene IndexReader Mock 설정
        KoreanAnalyzer analyzer = new KoreanAnalyzer();
        Directory directory = new ByteBuffersDirectory();
        IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer));

        for (Long contentId : contentIds) {
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

        writer.close();
        DirectoryReader reader = DirectoryReader.open(directory);
        when(luceneIndexService.getIndexReader()).thenReturn(reader);
    }

    private Member createTestMember() {
        Member member = MemberFixture.member("test@example.com", Role.ROLE_USER);
        ReflectionTestUtils.setField(member, "id", 1L);
        return member;
    }

    private Survey createActionThrillerSurvey() {
        return SurveyFixture.actionThrillerSurvey(testMember);
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

}