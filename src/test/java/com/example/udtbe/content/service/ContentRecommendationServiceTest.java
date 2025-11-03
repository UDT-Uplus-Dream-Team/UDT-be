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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock
    private RecommendationCacheManager cacheManager;

    // 실제 객체: 비즈니스 로직 컴포넌트
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
    @DisplayName("선호 장르 부스트 - GenreAnalyzer와 ScoreCalculator 협력으로 액션/스릴러 영화 우선 추천")
    void shouldBoostScoreForPreferredGenres() throws Exception {
        // given - 액션/스릴러 선호 사용자
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

        // Lucene 검색 결과: [기생충(스릴러), 블랙팬서(액션), 라라랜드(뮤지컬)]
        mockLuceneSearchService(List.of(1L, 8L, 6L));

        // when - GenreAnalyzer가 "액션", "스릴러" 추출 → ScoreCalculator가 부스트 적용
        List<ContentRecommendationResponse> result = contentRecommendationService
                .recommendContents(testMember, 3);

        // then - 장르 부스트로 인해 액션/스릴러 영화가 상위 랭크
        assertThat(result).hasSize(3);

        // 기생충(스릴러)이 최상위에 위치
        assertThat(result.get(0).title()).isEqualTo("기생충");
        assertThat(result.get(0).genres()).contains("스릴러");

        // 라라랜드(뮤지컬)는 선호 장르가 아니므로 중간 랭크
        assertThat(result.get(1).title()).isEqualTo("라라랜드");
        assertThat(result.get(1).genres()).contains("뮤지컬");

        // 블랙팬서(액션)는 선호 장르이므로 높은 랭크
        assertThat(result.get(2).title()).isEqualTo("블랙 팬서");
        assertThat(result.get(2).genres()).contains("액션");

        verify(contentRecommendationQuery).findSurveyByMemberId(testMember.getId());
        verify(luceneSearchService).searchRecommendations(anyList(), anyList(), anyInt());
    }

    @Test
    @DisplayName("플랫폼 필터링 - QueryBuilder가 디즈니+ 콘텐츠만 필터링")
    void shouldFilterByPlatform() throws Exception {
        // given - 디즈니+만 사용하는 사용자
        Survey disneyPlusSurvey = SurveyFixture.disneyPlusSurvey(testMember);

        when(cacheManager.getCache(testMember.getId())).thenReturn(null);
        when(contentRecommendationQuery.findSurveyByMemberId(testMember.getId()))
                .thenReturn(disneyPlusSurvey);
        when(contentRecommendationQuery.findContentMetadataCache())
                .thenReturn(testMetadataCache);
        when(contentRecommendationQuery.findFeedbacksByMemberId(testMember.getId()))
                .thenReturn(new ArrayList<>());

        when(contentRecommendationQuery.findContentsByIds(anyList()))
                .thenAnswer(invocation -> {
                    List<Long> requestedIds = invocation.getArgument(0);
                    return testContents.stream()
                            .filter(c -> requestedIds.contains(c.getId()))
                            .toList();
                });

        // Lucene 결과: [아바타(디즈니+), 블랙팬서(디즈니+), 기생충(넷플릭스), 조커(넷플릭스)]
        mockLuceneSearchService(List.of(4L, 8L, 1L, 9L));

        // when - QueryBuilder가 디즈니+ 플랫폼만 필터링
        List<ContentRecommendationResponse> result = contentRecommendationService
                .recommendContents(testMember, 3);

        // then - QueryBuilder의 플랫폼 필터링 동작 검증
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);

        // 디즈니+ 콘텐츠가 포함되어 있는지 검증
        List<String> resultTitles = result.stream()
                .map(ContentRecommendationResponse::title)
                .toList();
        assertThat(resultTitles).contains("아바타: 물의 길", "블랙 팬서");

        // 디즈니+ 플랫폼 콘텐츠 검증
        long disneyPlusCount = result.stream()
                .filter(r -> r.platforms().stream()
                        .anyMatch(p -> p.contains("디즈니+")))
                .count();
        assertThat(disneyPlusCount).isGreaterThanOrEqualTo(2);

        verify(contentRecommendationQuery).findSurveyByMemberId(testMember.getId());
    }

    // === Helper 메서드들 ===

    private void mockLuceneSearchService(List<Long> contentIds) throws Exception {
        // TopDocs Mock: Lucene 검색 결과
        ScoreDoc[] scoreDocs = new ScoreDoc[contentIds.size()];
        for (int i = 0; i < contentIds.size(); i++) {
            scoreDocs[i] = new ScoreDoc(i, 1.0f + (contentIds.size() - i) * 0.1f);
        }
        TopDocs topDocs = new TopDocs(
                new TotalHits(scoreDocs.length, TotalHits.Relation.EQUAL_TO),
                scoreDocs
        );

        when(luceneSearchService.searchRecommendations(anyList(), anyList(), anyInt()))
                .thenReturn(topDocs);

        // IndexReader Mock: 간소화된 Lucene Document 생성
        Directory directory = new ByteBuffersDirectory();
        IndexWriter writer = new IndexWriter(directory,
                new IndexWriterConfig(new KoreanAnalyzer()));

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