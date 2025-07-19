package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.ContentRecommendationMapper;
import com.example.udtbe.domain.content.dto.common.ContentRecommendationDTO;
import com.example.udtbe.domain.content.dto.response.ContentRecommendationResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.global.exception.RestApiException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
            Survey userSurvey = contentRecommendationQuery.findSurveyByMemberId(member.getId());
            return executeRecommendationSearch(userSurvey, member, limit, isCurated);
        } catch (IOException | ParseException | RestApiException e) {
            log.error("추천 시스템 오류 발생: memberId={}, error={}", member.getId(), e.getMessage(), e);
            return getPopularContents(limit);
        }
    }

    private List<ContentRecommendationResponse> executeRecommendationSearch(
            Survey userSurvey, Member member, int limit, boolean isCurated)
            throws IOException, ParseException {

        // TODO: 모든 ContentMetadata를 한 번에 조회하여 캐시 생성 , 추후 메모리 분석 및 성능 개선의 여지가 농후
        Map<Long, ContentMetadata> metadataCache = contentRecommendationQuery.findContentMetadataCache();
        List<Long> platformFilteredContentIds = getPlatformFilteredContentIds(
                userSurvey.getPlatformTag(), metadataCache);

        if (isCurated) {
            return executeCuratedRecommendation(userSurvey, member, limit, metadataCache,
                    platformFilteredContentIds);
        }

        return executeRegularRecommendation(userSurvey, member, limit, metadataCache,
                platformFilteredContentIds);
    }

    private List<ContentRecommendationResponse> executeCuratedRecommendation(
            Survey userSurvey, Member member, int limit,
            Map<Long, ContentMetadata> metadataCache, List<Long> platformFilteredContentIds)
            throws IOException, ParseException {

        List<String> feedbackBasedGenres = extractPreferredGenresFromFeedback(member,
                metadataCache);
        List<String> surveyGenres = GenreType.toKoreanTypes(userSurvey.getGenreTag());
        TopDocs topDocs = luceneSearchService.searchCuratedRecommendations(
                platformFilteredContentIds, feedbackBasedGenres, limit);

        List<ContentRecommendationDTO> recommendations = processLuceneScoring(
                topDocs, feedbackBasedGenres, surveyGenres, member, metadataCache, true);

        return buildFinalResponse(recommendations, limit, metadataCache);
    }

    private List<ContentRecommendationResponse> executeRegularRecommendation(
            Survey userSurvey, Member member, int limit,
            Map<Long, ContentMetadata> metadataCache, List<Long> platformFilteredContentIds)
            throws IOException, ParseException {

        List<String> englishUserGenres = userSurvey.getGenreTag();
        List<String> koreanUserGenres = GenreType.toKoreanTypes(englishUserGenres);
        TopDocs topDocs = luceneSearchService.searchRecommendations(platformFilteredContentIds,
                koreanUserGenres, limit);

        List<ContentRecommendationDTO> recommendations = processLuceneScoring(
                topDocs, koreanUserGenres, null, member, metadataCache, false);

        return buildFinalResponse(recommendations, limit, metadataCache);
    }

    private List<ContentRecommendationDTO> processLuceneScoring(
            TopDocs topDocs, List<String> primaryGenres, List<String> secondaryGenres,
            Member member, Map<Long, ContentMetadata> metadataCache, boolean isCurated)
            throws IOException {

        DirectoryReader reader = luceneIndexService.getIndexReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        List<ContentRecommendationDTO> recommendations = new ArrayList<>();
        debugTopDocs(topDocs, searcher);

        Map<String, Float> feedbackScores = calculateGenreFeedbackScores(member, metadataCache);

        for (int i = 0; i < topDocs.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = topDocs.scoreDocs[i];
            Document doc = searcher.storedFields().document(scoreDoc.doc);
            Long contentId = Long.valueOf(doc.get("contentId"));

            float luceneScore = scoreDoc.score * 3.0f;
            float feedbackScore = calculateGenreFeedbackBoost(doc, feedbackScores);
            float finalScore;

            if (isCurated) {
                float feedbackGenreBoost = calculateGenreBoost(doc, primaryGenres);
                float surveyGenreBoost = calculateGenreBoost(doc, secondaryGenres);
                finalScore =
                        luceneScore + feedbackGenreBoost * 2.0f + surveyGenreBoost + feedbackScore;
            } else {
                float genreBoost = calculateGenreBoost(doc, primaryGenres);
                finalScore = luceneScore + genreBoost * 2.0f + feedbackScore;
            }

            recommendations.add(new ContentRecommendationDTO(contentId, finalScore));
        }

        return recommendations;
    }

    private List<String> extractPreferredGenresFromFeedback(Member member,
            Map<Long, ContentMetadata> metadataCache) {

        Map<String, Float> genreScores = calculateGenreFeedbackScores(member, metadataCache);

        List<String> preferredGenres = genreScores.entrySet().stream()
                .filter(entry -> entry.getValue() > 0.5f) // 긍정적 피드백만
                .sorted(Map.Entry.<String, Float>comparingByValue().reversed())
                .limit(3) // 상위 3개 장르만
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        log.info("추출된 선호 장르: {}", preferredGenres);
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

    private float calculateGenreBoost(Document doc, List<String> userGenres) {
        if (userGenres == null || userGenres.isEmpty()) {
            return 0.0f;
        }

        String genreTag = doc.get("genreTag");
        if (genreTag == null) {
            return 0.0f;
        }

        float boost = 0.0f;
        String[] docGenres = genreTag.split(",");
        for (String userGenre : userGenres) {
            if (userGenre != null && !userGenre.trim().isEmpty()) {
                for (String docGenre : docGenres) {
                    if (docGenre.trim().equals(userGenre.trim())) {
                        boost += 1.0f;
                        break;
                    }
                }
            }
        }
        return boost;
    }

    private float calculateGenreFeedbackBoost(Document doc, Map<String, Float> genreScores) {
        String docGenreTag = doc.get("genreTag");
        if (docGenreTag == null) {
            return 0.0f;
        }

        float boost = 0.0f;
        String[] docGenres = docGenreTag.split(",");
        for (String genre : docGenres) {
            genre = genre.trim();
            boost += genreScores.getOrDefault(genre, 0.0f);
        }
        return boost;
    }

    private Map<String, Float> calculateGenreFeedbackScores(Member member,
            Map<Long, ContentMetadata> metadataCache) {
        Map<String, Float> genreScores = new HashMap<>();

        List<Feedback> feedbacks = contentRecommendationQuery.findFeedbacksByMemberId(
                member.getId());
        if (feedbacks == null || feedbacks.isEmpty()) {
            return genreScores;
        }

        for (Feedback feedback : feedbacks) {
            if (!feedback.isDeleted()) {
                Long contentId = feedback.getContent().getId();
                ContentMetadata metadata = metadataCache.get(
                        contentId); // 사용자가 좋아요를 했지만 해당 플랫폼을 구독을 취소했었다면 포함이 안된다.

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

    private List<ContentRecommendationResponse> getPopularContents(int limit) {
        List<ContentMetadata> metadataList = contentRecommendationQuery.findPopularContentMetadata(
                limit);

        List<Content> contents = metadataList.stream()
                .map(ContentMetadata::getContent)
                .toList();
        return ContentRecommendationMapper.toResponseList(contents, metadataList);
    }

    private List<ContentRecommendationResponse> buildFinalResponse(
            List<ContentRecommendationDTO> recommendations, int limit,
            Map<Long, ContentMetadata> metadataCache) {

        List<ContentRecommendationDTO> sortedRecommendations = recommendations.stream()
                .sorted((r1, r2) -> Float.compare(r2.score(), r1.score()))
                .limit(limit)
                .toList();

        List<Long> sortedContentIds = sortedRecommendations.stream()
                .map(ContentRecommendationDTO::contentId)
                .toList();

        List<Content> contents = contentRecommendationQuery.findContentsByIds(sortedContentIds);
        List<ContentMetadata> metadataList = contents.stream()
                .map(content -> metadataCache.get(content.getId()))
                .filter(Objects::nonNull)
                .toList();

        for (Content content : contents) {
            log.info("추출된 순서 : {}", String.join(", ", content.getTitle()));
        }

        return ContentRecommendationMapper.toResponseList(contents, metadataList);
    }

    private void debugTopDocs(TopDocs topDocs, IndexSearcher searcher) throws IOException {
        log.info("🔍 ===== TopDocs 상세 분석 =====");
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

        log.info("🔍 ===== TopDocs 분석 완료 =====");
    }

}