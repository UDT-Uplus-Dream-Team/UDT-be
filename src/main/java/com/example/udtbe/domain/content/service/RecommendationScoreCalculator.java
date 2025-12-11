package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Feedback;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class RecommendationScoreCalculator {

    private static final float LUCENE_SCORE_WEIGHT = 3.0f;
    private static final float GENRE_BOOST_WEIGHT = 2.0f;
    private static final float FEEDBACK_GENRE_BOOST_WEIGHT = 2.0f;

    private static final float LIKE_SCORE = 1.0f;
    private static final float DISLIKE_SCORE = -1.0f;
    private static final float UNINTERESTED_SCORE = 0.2f;

    public float calculateGenreBoost(Set<String> docGenres, List<String> memberGenres) {
        if (memberGenres == null || memberGenres.isEmpty() || docGenres.isEmpty()) {
            return 0.0f;
        }

        float boost = 0.0f;
        for (String memberGenre : memberGenres) {
            if (StringUtils.hasText(memberGenre)) {
                String targetGenre = memberGenre.trim();
                if (docGenres.contains(targetGenre)) {
                    boost += 1.0f;
                }
            }
        }
        return boost;
    }

    public float calculateGenreFeedbackBoost(Set<String> docGenres,
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

    public float calculateContentTagBoost(Set<String> docGenres,
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

    public Map<String, Float> calculateGenreFeedbackScores(
            List<Feedback> feedbacks,
            Map<Long, ContentMetadata> metadataCache,
            Optional<Integer> recentFeedbackLimit) {

        Map<String, Float> genreScores = new HashMap<>();

        if (feedbacks == null || feedbacks.isEmpty()) {
            return genreScores;
        }

        List<Feedback> targetFeedbacks;
        targetFeedbacks = recentFeedbackLimit.map(limit -> feedbacks.stream()
                .sorted((f1, f2) -> f2.getUpdatedAt().compareTo(f1.getUpdatedAt()))
                .limit(limit)
                .toList()).orElse(feedbacks);

        for (Feedback feedback : targetFeedbacks) {
            if (!feedback.isDeleted()) {
                Long contentId = feedback.getContent().getId();
                ContentMetadata metadata = metadataCache.get(contentId);

                if (metadata != null && metadata.getGenreTag() != null) {
                    float score = switch (feedback.getFeedbackType()) {
                        case LIKE -> LIKE_SCORE;
                        case DISLIKE -> DISLIKE_SCORE;
                        case UNINTERESTED -> UNINTERESTED_SCORE;
                    };

                    for (String genre : metadata.getGenreTag()) {
                        if (StringUtils.hasText(genre)) {
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

    public Map<String, Float> calculateContentTagGenreScores(
            List<Long> contentTagIds,
            Map<Long, ContentMetadata> metadataCache) {

        Map<String, Float> genreScores = new HashMap<>();

        if (contentTagIds == null || contentTagIds.isEmpty()) {
            return genreScores;
        }

        for (Long contentId : contentTagIds) {
            ContentMetadata metadata = metadataCache.get(contentId);
            if (metadata != null && metadata.getGenreTag() != null) {
                for (String genre : metadata.getGenreTag()) {
                    if (StringUtils.hasText(genre)) {
                        genre = genre.trim();
                        genreScores.put(genre, genreScores.getOrDefault(genre, 0.0f) + 1.0f);
                    }
                }
            }
        }

        return genreScores;
    }

    public float calculateCuratedScore(
            float luceneScore,
            Set<String> docGenres,
            List<String> feedbackGenres,
            List<String> surveyGenres,
            Map<String, Float> feedbackScores) {

        float feedbackGenreBoost = calculateGenreBoost(docGenres, feedbackGenres);
        float surveyGenreBoost = calculateGenreBoost(docGenres, surveyGenres);
        float feedbackScore = calculateGenreFeedbackBoost(docGenres, feedbackScores);

        return (luceneScore * LUCENE_SCORE_WEIGHT)
                + (feedbackGenreBoost * FEEDBACK_GENRE_BOOST_WEIGHT)
                + surveyGenreBoost
                + feedbackScore;
    }

    public float calculateRegularScore(
            float luceneScore,
            Set<String> docGenres,
            List<String> memberGenres,
            Map<String, Float> feedbackScores,
            Map<String, Float> contentTagGenreScores) {

        float genreBoost = calculateGenreBoost(docGenres, memberGenres);
        float feedbackScore = calculateGenreFeedbackBoost(docGenres, feedbackScores);
        float contentTagBoost = calculateContentTagBoost(docGenres, contentTagGenreScores);

        return (luceneScore * LUCENE_SCORE_WEIGHT)
                + (genreBoost * GENRE_BOOST_WEIGHT)
                + feedbackScore
                + contentTagBoost;
    }
}