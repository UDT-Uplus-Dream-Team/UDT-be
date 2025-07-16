package com.example.udtbe.domain.member.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record MemberCuratedContentGetResponse(
        Long contentId,
        String title,
        String posterUrl
) {

    @QueryProjection
    public MemberCuratedContentGetResponse(Long contentId, String title, String posterUrl) {
        this.contentId = contentId;
        this.title = title;
        this.posterUrl = posterUrl;
    }

}
