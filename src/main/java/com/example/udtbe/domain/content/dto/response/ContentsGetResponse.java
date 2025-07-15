package com.example.udtbe.domain.content.dto.response;

import com.example.udtbe.domain.content.entity.Content;
import com.querydsl.core.annotations.QueryProjection;

public record ContentsGetResponse(
        Long contentId,
        String title,
        String posterUrl
) {

    @QueryProjection
    public ContentsGetResponse(
            Content content
    ) {
        this(content.getId(), content.getTitle(), content.getPosterUrl());
    }
}
