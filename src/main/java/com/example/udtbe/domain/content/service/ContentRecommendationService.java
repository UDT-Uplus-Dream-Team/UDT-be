package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.common.ContentRecommendationDTO;
import com.example.udtbe.domain.content.dto.response.ContentRecommendationResponse;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.exception.RecommendContentErrorCode;
import com.example.udtbe.domain.content.util.MemberRecommendationCache;
import com.example.udtbe.domain.content.util.RecommendationCacheManager;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.global.exception.RestApiException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentRecommendationService {

    private final ContentRecommendationQuery contentRecommendationQuery;
    private final LuceneIndexService luceneIndexService;
    private final LuceneSearchService luceneSearchService;
    private final RecommendationCacheManager cacheManager;
    private final RecommendationScoreCalculator scoreCalculator;
    private final GenreAnalyzer genreAnalyzer;
    private final RecommendationQueryBuilder queryBuilder;
    private final RecommendationResponseBuilder responseBuilder;

    @Transactional(readOnly = true)
    public List<ContentRecommendationResponse> recommendContents(Member member, int limit) {
        return performRecommendation(member, limit, false);
    }

    @Transactional(readOnly = true)
    public List<ContentRecommendationResponse> recommendCuratedContents(Member member, int limit) {
        return performRecommendation(member, limit, true);
    }

    private List<ContentRecommendationResponse> performRecommendation(Member member, int limit,
            boolean isCurated) {
        try {
            if (!isCurated) {
                MemberRecommendationCache cache = cacheManager.getCache(member.getId());
                if (cache != null && !cache.shouldRefresh()) {
                    return getCachedRecommendations(cache);
                }
            }

            Survey memberSurvey = contentRecommendationQuery.findSurveyByMemberId(member.getId());
            return executeRecommendationSearch(memberSurvey, member, limit, isCurated);
        } catch (IOException e) {
            throw new RestApiException(RecommendContentErrorCode.LUCENE_SEARCH_IO_ERROR);
        } catch (ParseException e) {
            throw new RestApiException(RecommendContentErrorCode.LUCENE_SEARCH_PARSE_ERROR);
        }
    }

    private List<ContentRecommendationResponse> executeRecommendationSearch(
            Survey memberSurvey, Member member, int limit, boolean isCurated)
            throws IOException, ParseException {

        // TODO: 모든 ContentMetadata 한 번에 조회하여 캐시 생성 , 추후 메모리 분석 및 성능 개선의 여지가 농후
        Map<Long, ContentMetadata> metadataCache = contentRecommendationQuery.findContentMetadataCache();
        List<Long> platformFilteredContentIds = queryBuilder.getPlatformFilteredContentIds(
                memberSurvey.getPlatformTag(), metadataCache);

        if (isCurated) {
            return executeCuratedRecommendation(memberSurvey, member, limit, metadataCache,
                    platformFilteredContentIds);
        }

        return executeRegularRecommendation(memberSurvey, member, limit, metadataCache,
                platformFilteredContentIds);
    }

    private List<ContentRecommendationResponse> executeCuratedRecommendation(
            Survey memberSurvey, Member member, int limit,
            Map<Long, ContentMetadata> metadataCache, List<Long> platformFilteredContentIds)
            throws IOException, ParseException {

        List<Feedback> feedbacks = contentRecommendationQuery.findFeedbacksByMemberId(
                member.getId());
        List<String> feedbackBasedGenres = genreAnalyzer.extractPreferredGenresFromFeedback(
                feedbacks, metadataCache);
        List<String> surveyGenres = GenreType.toKoreanTypes(memberSurvey.getGenreTag());
        TopDocs topDocs = luceneSearchService.searchCuratedRecommendations(
                platformFilteredContentIds, feedbackBasedGenres, limit);

        List<ContentRecommendationDTO> recommendations = processLuceneScoring(
                topDocs, feedbackBasedGenres, surveyGenres, member, metadataCache, true, null);

        List<ContentRecommendationDTO> sortedRecommendations = recommendations.stream()
                .sorted((r1, r2) -> Float.compare(r2.score(), r1.score()))
                .limit(limit)
                .toList();

        return responseBuilder.buildResponseFromRecommendations(sortedRecommendations,
                metadataCache);
    }

    private List<ContentRecommendationResponse> executeRegularRecommendation(
            Survey memberSurvey, Member member, int limit,
            Map<Long, ContentMetadata> metadataCache, List<Long> platformFilteredContentIds)
            throws IOException, ParseException {

        List<String> englishMemberGenres = memberSurvey.getGenreTag();
        List<String> koreanMemberGenres = GenreType.toKoreanTypes(englishMemberGenres);
        List<Long> contentTag = memberSurvey.getContentTag().stream()
                .map(Long::valueOf)
                .toList();
        TopDocs topDocs = luceneSearchService.searchRecommendations(platformFilteredContentIds,
                koreanMemberGenres, limit);

        List<ContentRecommendationDTO> recommendations = processLuceneScoring(
                topDocs, koreanMemberGenres, null, member, metadataCache, false, contentTag);

        return buildRegularRecommendationResponse(recommendations, limit, metadataCache,
                member.getId());
    }

    private List<ContentRecommendationDTO> processLuceneScoring(
            TopDocs topDocs, List<String> primaryGenres, List<String> secondaryGenres,
            Member member, Map<Long, ContentMetadata> metadataCache, boolean isCurated,
            List<Long> contentTagIds)
            throws IOException {

        try (DirectoryReader indexReader = luceneIndexService.getIndexReader()) {
            IndexSearcher searcher = new IndexSearcher(indexReader);
            List<ContentRecommendationDTO> recommendations = new ArrayList<>();
            debugTopDocs(topDocs, searcher);

            List<Feedback> feedbacks = contentRecommendationQuery.findFeedbacksByMemberId(
                    member.getId());
            Map<String, Float> feedbackScores = scoreCalculator.calculateGenreFeedbackScores(
                    feedbacks, metadataCache, Optional.empty());

            Map<String, Float> contentTagGenreScores = scoreCalculator.calculateContentTagGenreScores(
                    contentTagIds, metadataCache);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.storedFields().document(scoreDoc.doc);
                Long contentId = Long.valueOf(doc.get("contentId"));

                Set<String> docGenres = genreAnalyzer.parseGenreTags(doc.get("genreTag"));

                float finalScore;
                if (isCurated) {
                    finalScore = scoreCalculator.calculateCuratedScore(
                            scoreDoc.score, docGenres, primaryGenres, secondaryGenres,
                            feedbackScores);
                } else {
                    finalScore = scoreCalculator.calculateRegularScore(
                            scoreDoc.score, docGenres, primaryGenres, feedbackScores,
                            contentTagGenreScores);
                }

                recommendations.add(new ContentRecommendationDTO(contentId, finalScore));
            }

            return recommendations;
        }
    }

    private List<ContentRecommendationResponse> getCachedRecommendations(
            MemberRecommendationCache cache) {

        List<ContentRecommendationDTO> nextBatch = cache.getNext();
        if (nextBatch.isEmpty()) {
            return Collections.emptyList();
        }

        debugCachedRecommendations(nextBatch, cache);

        Map<Long, ContentMetadata> metadataCache = contentRecommendationQuery.findContentMetadataCache();
        return responseBuilder.buildResponseFromRecommendations(nextBatch, metadataCache);
    }

    private List<ContentRecommendationResponse> buildRegularRecommendationResponse(
            List<ContentRecommendationDTO> recommendations, int limit,
            Map<Long, ContentMetadata> metadataCache, Long memberId) {

        List<ContentRecommendationDTO> sortedRecommendations = recommendations.stream()
                .sorted((r1, r2) -> Float.compare(r2.score(), r1.score()))
                .toList();

        List<ContentRecommendationDTO> firstBatch = sortedRecommendations.stream()
                .limit(limit)
                .toList();

        List<ContentRecommendationDTO> remainingRecommendations = sortedRecommendations.stream()
                .skip(limit)
                .toList();

        cacheManager.putCache(memberId, remainingRecommendations);

        return responseBuilder.buildResponseFromRecommendations(firstBatch, metadataCache);
    }

    private void debugTopDocs(TopDocs topDocs, IndexSearcher searcher) throws IOException {
        log.info("===== TopDocs 상세 분석 =====");
        log.info("총 매치된 문서 수: {}", topDocs.totalHits.value);
        log.info("반환된 문서 수: {}", topDocs.scoreDocs.length);

        if (topDocs.scoreDocs.length == 0) {
            log.warn("❌ 검색 결과가 없습니다!");
            return;
        }

        log.info("📋 상위 {}개 문서 상세:", Math.min(10, topDocs.scoreDocs.length));

        for (int i = 0; i < Math.min(10, topDocs.scoreDocs.length); i++) {
            ScoreDoc scoreDoc = topDocs.scoreDocs[i];
            Document doc = searcher.storedFields().document(scoreDoc.doc);

            String contentId = doc.get("contentId");
            String title = doc.get("title");
            String genreTag = doc.get("genreTag");
            String platformTag = doc.get("platformTag");

            log.info("  {}위: contentId={}, score={}, title='{}', genres='{}', platforms='{}'",
                    i + 1, contentId, scoreDoc.score, title, genreTag, platformTag);
        }

        log.info("===== TopDocs 분석 완료 =====");
    }

    private void debugCachedRecommendations(List<ContentRecommendationDTO> recommendations,
            MemberRecommendationCache cache) {
        log.info("===== 캐싱된 추천 상세 분석 =====");
        log.info("캐시 소진율: {}% ({}/{})",
                String.format("%.2f", cache.getConsumptionRate() * 100),
                cache.getCurrentIndex(),
                cache.getRecommendations().size());
        log.info("이번 배치 추천 수: {}", recommendations.size());
        log.info("남은 추천 수: {}", cache.getRemainingCount());

        if (recommendations.isEmpty()) {
            log.warn("❌ 캐싱된 추천이 없습니다!");
            return;
        }

        log.info("📋 이번 배치 상위 {}개 추천:", Math.min(10, recommendations.size()));

        for (int i = 0; i < Math.min(10, recommendations.size()); i++) {
            ContentRecommendationDTO rec = recommendations.get(i);
            log.info("  {}위: contentId={}, score={}",
                    i + 1, rec.contentId(), rec.score());
        }

        log.info("===== 캐싱된 추천 분석 완료 =====");
    }

    public void clearMyRecommendationCache(Member member) {
        Long memberId = member.getId();
        boolean hadCache = cacheManager.hasCache(memberId);
        if (hadCache) {
            cacheManager.removeMemberCache(memberId);
        }
    }

}