package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenreAnalyzer {

    private final RecommendationScoreCalculator scoreCalculator;

    public List<String> extractPreferredGenresFromFeedback(
            List<Feedback> feedbacks,
            Map<Long, ContentMetadata> metadataCache) {

        Map<String, Float> genreScores = scoreCalculator.calculateGenreFeedbackScores(
                feedbacks, metadataCache, java.util.Optional.of(20));

        List<String> preferredGenres = genreScores.entrySet().stream()
                .filter(entry -> entry.getValue() > 0.0f)
                .sorted(Map.Entry.<String, Float>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (preferredGenres.isEmpty()) {
            preferredGenres = extractFallbackGenresFromRecentLikes(feedbacks, metadataCache);
            log.info("선호 장르가 없어 최근 좋아요 기반 장르 사용: {}", preferredGenres);
        } else {
            log.info("추출된 선호 장르: {}", preferredGenres);
        }

        return preferredGenres;
    }

    public List<String> extractFallbackGenresFromRecentLikes(
            List<Feedback> feedbacks,
            Map<Long, ContentMetadata> metadataCache) {

        return feedbacks.stream()
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

    public Set<String> parseGenreTags(String genreTag) {
        if (genreTag == null || genreTag.trim().isEmpty()) {
            return Set.of();
        }

        return Arrays.stream(genreTag.split(","))
                .map(String::trim)
                .filter(genre -> !genre.isEmpty())
                .collect(Collectors.toSet());
    }
}