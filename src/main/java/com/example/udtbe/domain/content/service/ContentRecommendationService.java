package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.ContentRecommendationMapper;
import com.example.udtbe.domain.content.dto.common.ContentRecommendationDTO;
import com.example.udtbe.domain.content.dto.response.ContentRecommendationResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import com.example.udtbe.domain.content.exception.RecommendContentErrorCode;
import com.example.udtbe.domain.content.util.MemberRecommendationCache;
import com.example.udtbe.domain.content.util.RecommendationCacheManager;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.global.exception.RestApiException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
        List<Long> platformFilteredContentIds = getPlatformFilteredContentIds(
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

        List<String> feedbackBasedGenres = extractPreferredGenresFromFeedback(member,
                metadataCache);
        List<String> surveyGenres = GenreType.toKoreanTypes(memberSurvey.getGenreTag());
        TopDocs topDocs = luceneSearchService.searchCuratedRecommendations(
                platformFilteredContentIds, feedbackBasedGenres, limit);

        List<ContentRecommendationDTO> recommendations = processLuceneScoring(
                topDocs, feedbackBasedGenres, surveyGenres, member, metadataCache, true, null);

        List<ContentRecommendationDTO> sortedRecommendations = recommendations.stream()
                .sorted((r1, r2) -> Float.compare(r2.score(), r1.score()))
                .limit(limit)
                .toList();

        return buildResponseFromRecommendations(sortedRecommendations, metadataCache);
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

        return buildFinalResponse(recommendations, limit, metadataCache, member.getId(),
                false);
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

            Map<String, Float> feedbackScores = calculateGenreFeedbackScores(member, metadataCache,
                    Optional.empty());

            Map<String, Float> contentTagGenreScores = calculateContentTagGenreScores(contentTagIds,
                    metadataCache);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.storedFields().document(scoreDoc.doc);
                Long contentId = Long.valueOf(doc.get("contentId"));

                Set<String> docGenres = parseGenreTags(doc.get("genreTag"));

                float luceneScore = scoreDoc.score * 3.0f;
                float feedbackScore = calculateGenreFeedbackBoost(docGenres, feedbackScores);
                float finalScore;

                if (isCurated) {
                    float feedbackGenreBoost = calculateGenreBoost(docGenres, primaryGenres);
                    float surveyGenreBoost = calculateGenreBoost(docGenres, secondaryGenres);
                    finalScore =
                            luceneScore + feedbackGenreBoost * 2.0f + surveyGenreBoost
                                    + feedbackScore;
                } else {
                    float genreBoost = calculateGenreBoost(docGenres, primaryGenres);
                    float contentTagBoost = calculateContentTagBoost(docGenres,
                            contentTagGenreScores);
                    finalScore = luceneScore + genreBoost * 2.0f + feedbackScore + contentTagBoost;
                }

                recommendations.add(new ContentRecommendationDTO(contentId, finalScore));
            }

            return recommendations;
        }
    }

    private List<String> extractPreferredGenresFromFeedback(Member member,
            Map<Long, ContentMetadata> metadataCache) {

        Map<String, Float> genreScores = calculateGenreFeedbackScores(member, metadataCache,
                Optional.of(20));

        List<String> preferredGenres = genreScores.entrySet().stream()
                .filter(entry -> entry.getValue() > 0.0f)
                .sorted(Map.Entry.<String, Float>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (preferredGenres.isEmpty()) {
            List<Feedback> allFeedbacks = contentRecommendationQuery.findFeedbacksByMemberId(
                    member.getId());
            preferredGenres = extractFallbackGenresFromRecentLikes(allFeedbacks, metadataCache);
            log.info("선호 장르가 없어 최근 좋아요 기반 장르 사용: {}", preferredGenres);
        } else {
            log.info("추출된 선호 장르: {}", preferredGenres);
        }

        return preferredGenres;
    }

    private List<Long> getPlatformFilteredContentIds(List<String> platformTags,
            Map<Long, ContentMetadata> metadataCache) {
        if (platformTags == null || platformTags.isEmpty()) {
            return new ArrayList<>(metadataCache.keySet());
        }

        List<String> koreanPlatformTags = PlatformType.toKoreanTypes(platformTags);

        if (koreanPlatformTags.isEmpty()) {
            log.warn("플랫폼 태그 변환 실패 - 모든 콘텐츠 반환");
            return new ArrayList<>(metadataCache.keySet());
        }

        Set<Long> contentIds = new HashSet<>();
        for (String koreanPlatformTag : koreanPlatformTags) {
            if (koreanPlatformTag != null && !koreanPlatformTag.trim().isEmpty()) {
                for (Map.Entry<Long, ContentMetadata> entry : metadataCache.entrySet()) {
                    ContentMetadata metadata = entry.getValue();
                    if (metadata.getPlatformTag() != null &&
                            metadata.getPlatformTag().contains(koreanPlatformTag)) {
                        contentIds.add(entry.getKey());
                    }
                }
            }
        }

        return new ArrayList<>(contentIds);
    }

    private float calculateGenreBoost(Set<String> docGenres, List<String> memberGenres) {
        if (memberGenres == null || memberGenres.isEmpty() || docGenres.isEmpty()) {
            return 0.0f;
        }

        float boost = 0.0f;
        for (String memberGenre : memberGenres) {
            if (memberGenre != null && !memberGenre.trim().isEmpty()) {
                String targetGenre = memberGenre.trim();
                if (docGenres.contains(targetGenre)) {
                    boost += 1.0f;
                }
            }
        }
        return boost;
    }

    private float calculateGenreFeedbackBoost(Set<String> docGenres,
            Map<String, Float> genreScores) {
        if (docGenres.isEmpty()) {
            return 0.0f;
        }

        float boost = 0.0f;
        for (String genre : docGenres) {
            boost += genreScores.getOrDefault(genre, 0.0f);
        }
        return boost;
    }

    private float calculateContentTagBoost(Set<String> docGenres,
            Map<String, Float> contentTagGenreScores) {
        if (docGenres.isEmpty() || contentTagGenreScores.isEmpty()) {
            return 0.0f;
        }

        float boost = 0.0f;
        for (String docGenre : docGenres) {
            boost += contentTagGenreScores.getOrDefault(docGenre, 0.0f);
        }
        return boost;
    }

    private Map<String, Float> calculateContentTagGenreScores(List<Long> contentTagIds,
            Map<Long, ContentMetadata> metadataCache) {
        Map<String, Float> genreScores = new HashMap<>();

        if (contentTagIds == null || contentTagIds.isEmpty()) {
            return genreScores;
        }

        for (Long contentId : contentTagIds) {
            ContentMetadata metadata = metadataCache.get(contentId);
            if (metadata != null && metadata.getGenreTag() != null) {
                for (String genre : metadata.getGenreTag()) {
                    if (genre != null && !genre.trim().isEmpty()) {
                        genre = genre.trim();
                        genreScores.put(genre, genreScores.getOrDefault(genre, 0.0f) + 1.0f);
                    }
                }
            }
        }

        return genreScores;
    }

    private Set<String> parseGenreTags(String genreTag) {
        if (genreTag == null || genreTag.trim().isEmpty()) {
            return Set.of();
        }

        return Arrays.stream(genreTag.split(","))
                .map(String::trim)
                .filter(genre -> !genre.isEmpty())
                .collect(Collectors.toSet());
    }

    private Map<String, Float> calculateGenreFeedbackScores(Member member,
            Map<Long, ContentMetadata> metadataCache, Optional<Integer> recentFeedbackLimit) {
        Map<String, Float> genreScores = new HashMap<>();

        List<Feedback> feedbacks = contentRecommendationQuery.findFeedbacksByMemberId(
                member.getId());
        if (feedbacks == null || feedbacks.isEmpty()) {
            return genreScores;
        }

        List<Feedback> targetFeedbacks;
        targetFeedbacks = recentFeedbackLimit.map(integer -> feedbacks.stream()
                .sorted((f1, f2) -> f2.getUpdatedAt().compareTo(f1.getUpdatedAt()))
                .limit(integer)
                .toList()).orElse(feedbacks);

        for (Feedback feedback : targetFeedbacks) {
            if (!feedback.isDeleted()) {
                Long contentId = feedback.getContent().getId();
                ContentMetadata metadata = metadataCache.get(contentId);

                if (metadata != null && metadata.getGenreTag() != null) {
                    float score = switch (feedback.getFeedbackType()) {
                        case LIKE -> 1.0f;
                        case DISLIKE -> -1.0f;
                        case UNINTERESTED -> 0.2f;
                    };

                    for (String genre : metadata.getGenreTag()) {
                        if (genre != null && !genre.trim().isEmpty()) {
                            genre = genre.trim();
                            float oldScore = genreScores.getOrDefault(genre, 0.0f);
                            float newScore = oldScore + score;
                            genreScores.put(genre, newScore);
                        }
                    }
                }
            }
        }

        return genreScores;
    }


    private List<String> extractFallbackGenresFromRecentLikes(List<Feedback> allFeedbacks,
            Map<Long, ContentMetadata> metadataCache) {
        return allFeedbacks.stream()
                .filter(f -> !f.isDeleted() && f.getFeedbackType() == FeedbackType.LIKE)
                .sorted((f1, f2) -> f2.getCreatedAt().compareTo(f1.getCreatedAt()))
                .limit(20)
                .map(f -> metadataCache.get(f.getContent().getId()))
                .filter(Objects::nonNull)
                .flatMap(m -> m.getGenreTag().stream())
                .distinct()
                .limit(3)
                .toList();
    }


    private List<ContentRecommendationResponse> getCachedRecommendations(
            MemberRecommendationCache cache) {

        List<ContentRecommendationDTO> nextBatch = cache.getNext();
        if (nextBatch.isEmpty()) {
            return Collections.emptyList();
        }

        debugCachedRecommendations(nextBatch, cache);

        Map<Long, ContentMetadata> metadataCache = contentRecommendationQuery.findContentMetadataCache();
        return buildResponseFromRecommendations(nextBatch, metadataCache);
    }

    private List<ContentRecommendationResponse> buildFinalResponse(
            List<ContentRecommendationDTO> recommendations, int limit,
            Map<Long, ContentMetadata> metadataCache, Long memberId, boolean isCurated) {

        List<ContentRecommendationDTO> sortedRecommendations = recommendations.stream()
                .sorted((r1, r2) -> Float.compare(r2.score(), r1.score()))
                .toList();

        if (!isCurated) {
            List<ContentRecommendationDTO> firstBatch = sortedRecommendations.stream()
                    .limit(limit)
                    .toList();

            List<ContentRecommendationDTO> remainingRecommendations = sortedRecommendations.stream()
                    .skip(limit)
                    .toList();

            cacheManager.putCache(memberId, remainingRecommendations);

            return buildResponseFromRecommendations(firstBatch, metadataCache);
        }

        List<ContentRecommendationDTO> limitedRecommendations = sortedRecommendations.stream()
                .limit(limit)
                .toList();

        return buildResponseFromRecommendations(limitedRecommendations, metadataCache);
    }

    private List<ContentRecommendationResponse> buildResponseFromRecommendations(
            List<ContentRecommendationDTO> recommendations,
            Map<Long, ContentMetadata> metadataCache) {

        List<Long> recommendedContentIds = recommendations.stream()
                .map(ContentRecommendationDTO::contentId)
                .toList();

        List<Content> contents = contentRecommendationQuery.findContentsByIds(
                recommendedContentIds);
        List<ContentMetadata> metadataList = contents.stream()
                .map(content -> metadataCache.get(content.getId()))
                .filter(Objects::nonNull)
                .toList();

        for (Content content : contents) {
            log.info("추출된 순서 : {}", content.getTitle());
        }

        return ContentRecommendationMapper.toResponseList(contents, metadataList);
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

}