package com.example.udtbe.domain.content.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record PopularContentByPlatformResponse(
        Long contentId,
        String posterUrl
) {

    @QueryProjection
    public PopularContentByPlatformResponse(Long contentId, String posterUrl) {
        this.contentId = contentId;
        this.posterUrl = posterUrl;
    }
}
