package com.example.udtbe.domain.content.dto.request;

import jakarta.validation.constraints.NotNull;

public record FeedbackContentGetRequest(
        Long cursor,
        @NotNull
        int size,
        @NotNull
        String feedbackType,
        @NotNull
        String feedbackSortType
) {

}
