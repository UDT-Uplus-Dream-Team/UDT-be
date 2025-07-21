package com.example.udtbe.domain.content.dto.response;

import java.util.List;

public record ContentRecommendationResponse(
        Long contentId,
        String title,
        String description,
        String posterUrl,
        String backdropUrl,
        String openDate,
        int runningTime,
        String episode,
        String rating,
        String category,
        List<String> genres,
        List<String> directors,
        List<String> casts,
        List<String> platforms
) {

}