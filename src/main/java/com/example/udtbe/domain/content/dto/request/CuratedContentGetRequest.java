package com.example.udtbe.domain.content.dto.request;

import java.util.Objects;

public record CuratedContentGetRequest(
        Long cursor,
        Integer size
) {

    public CuratedContentGetRequest {
        if (Objects.isNull(size)) {
            size = 10;
        }
    }
}
