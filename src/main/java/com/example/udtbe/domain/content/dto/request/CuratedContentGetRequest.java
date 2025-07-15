package com.example.udtbe.domain.content.dto.request;

import jakarta.validation.constraints.NotNull;

public record CuratedContentGetRequest(
        Long cursor,
        @NotNull
        int size
) {

}
