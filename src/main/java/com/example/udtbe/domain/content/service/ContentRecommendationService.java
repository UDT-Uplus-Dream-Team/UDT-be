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

        try {
            Survey userSurvey = contentRecommendationQuery.findSurveyByMemberId(
                    member.getId());

            return searchRecommendations(userSurvey, member, limit);

        } catch (Exception e) {
            log.error("추천 시스템 오류 발생: memberId={}, error={}", member.getId(), e.getMessage(), e);
            return getPopularContents(limit);
        }
    }

    public List<ContentRecommendationResponse> searchRecommendations(Survey userSurvey,
            Member member, int limit)
            throws Exception {

        // TODO: 모든 ContentMetadata를 한 번에 조회하여 캐시 생성 , 추후 메모리 분석 및 성능 개선의 여지가 농후
        Map<Long, ContentMetadata> metadataCache = contentRecommendationQuery.findContentMetadataCache();

        List<Long> platformFilteredContentIds = getPlatformFilteredContentIds(
                userSurvey.getPlatformTag(), metadataCache);

        List<String> userGenres = userSurvey.getGenreTag();
        TopDocs topDocs = luceneSearchService.searchRecommendations(
                platformFilteredContentIds, userGenres, limit);

        DirectoryReader reader = luceneIndexService.getIndexReader();
        IndexSearcher searcher = new IndexSearcher(reader);

        List<ContentRecommendationDTO> recommendations = new ArrayList<>();

        Map<String, Float> feedbackScores = calculateGenreFeedbackScores(member, metadataCache);

        for (int i = 0; i < topDocs.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = topDocs.scoreDocs[i];
            Document doc = searcher.storedFields().document(scoreDoc.doc);
            Long contentId = Long.valueOf(doc.get("contentId"));
            // TF-IDF 점수
            float luceneScore = scoreDoc.score;
            //설문 기반 선호도
            float genreBoost = calculateGenreBoost(doc, userGenres);
            //실제 행동 기반 선호도
            float feedbackScore = calculateGenreFeedbackBoost(doc, feedbackScores);

            float finalScore = luceneScore + genreBoost * 2.0f + feedbackScore;


            recommendations.add(new ContentRecommendationDTO(contentId, finalScore));
        }

        reader.close();

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
                .collect(Collectors.toList());


        return ContentRecommendationMapper.toResponseList(contents, metadataList);
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

        List<String> koreanUserGenres = GenreType.toKoreanTypes(userGenres);

        if (koreanUserGenres.isEmpty()) {
            log.warn("장르 태그 변환 실패");
            return 0.0f;
        }

        float boost = 0.0f;
        String[] docGenres = genreTag.split(",");
        for (String koreanUserGenre : koreanUserGenres) {
            if (koreanUserGenre != null && !koreanUserGenre.trim().isEmpty()) {
                for (String docGenre : docGenres) {
                    if (docGenre.trim().equals(koreanUserGenre.trim())) {
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
                ContentMetadata metadata = metadataCache.get(contentId);

                if (metadata != null && metadata.getGenreTag() != null) {
                    float score = switch (feedback.getFeedbackType()) {
                        case LIKE -> 1.0f;
                        case DISLIKE -> -1.0f;
                        case UNINTERESTED -> -0.5f;
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

}