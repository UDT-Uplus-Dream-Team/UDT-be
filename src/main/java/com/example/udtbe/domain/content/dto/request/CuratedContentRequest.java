package com.example.udtbe.domain.content.dto.request;

import jakarta.validation.constraints.NotNull;

public record CuratedContentRequest(
        @NotNull(message = "contentId 는 필수값입니다.")
        Long contentId
) {

}
