package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.content.entity.Cast;
import com.querydsl.core.annotations.QueryProjection;

public record AdminCastsGetResponse(

        Long castId,
        String castName,
        String castImageUrl

) {

    @QueryProjection
    public AdminCastsGetResponse(Cast cast) {
        this(cast.getId(), cast.getCastName(), cast.getCastImageUrl());
    }
}
