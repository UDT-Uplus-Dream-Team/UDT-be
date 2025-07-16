package com.example.udtbe.domain.content.dto.response;

import com.example.udtbe.domain.content.entity.Content;
import com.querydsl.core.annotations.QueryProjection;

public record WeeklyRecommendedContentsResponse(
        Long contentId,
        String posterUrl
) {

    @QueryProjection
    public WeeklyRecommendedContentsResponse(Content content) {
        this(content.getId(), content.getPosterUrl());
    }

}
