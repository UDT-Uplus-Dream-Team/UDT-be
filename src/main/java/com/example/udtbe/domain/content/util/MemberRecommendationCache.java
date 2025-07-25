package com.example.udtbe.domain.content.util;

import com.example.udtbe.domain.content.dto.common.ContentRecommendationDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MemberRecommendationCache {

    private final List<ContentRecommendationDTO> recommendations;
    private int currentIndex;
    private final LocalDateTime createdAt;

    public MemberRecommendationCache(List<ContentRecommendationDTO> recommendations) {
        this.recommendations = new ArrayList<>(recommendations);
        this.currentIndex = 0;
        this.createdAt = LocalDateTime.now();
    }

    public double getConsumptionRate() {
        if (recommendations.isEmpty()) {
            return 1.0;
        }
        return (double) currentIndex / recommendations.size();
    }

    public boolean shouldRefresh() {
        return getConsumptionRate() >= 0.7;
    }

    public List<ContentRecommendationDTO> getNext() {
        if (currentIndex >= recommendations.size()) {
            log.warn("캐시 소진 완료: currentIndex={}, total={}", currentIndex,
                    recommendations.size());
            return Collections.emptyList();
        }

        int endIndex = Math.min(currentIndex + 10, recommendations.size());
        List<ContentRecommendationDTO> nextBatch = recommendations.subList(currentIndex, endIndex);

        currentIndex = endIndex;
        return new ArrayList<>(nextBatch);
    }

    public int getRemainingCount() {
        return Math.max(0, recommendations.size() - currentIndex);
    }

    public boolean isEmpty() {
        return recommendations.isEmpty();
    }

    public boolean isExpired(int hoursToExpire) {
        return createdAt.plusHours(hoursToExpire).isBefore(LocalDateTime.now());
    }
}