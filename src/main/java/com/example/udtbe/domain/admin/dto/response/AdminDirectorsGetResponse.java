package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.content.entity.Director;
import com.querydsl.core.annotations.QueryProjection;

public record AdminDirectorsGetResponse(

        Long directorId,
        String name,
        String directorImageUrl

) {

    @QueryProjection
    public AdminDirectorsGetResponse(Director director) {
        this(director.getId(), director.getDirectorName(), director.getDirectorImageUrl());
    }
}
