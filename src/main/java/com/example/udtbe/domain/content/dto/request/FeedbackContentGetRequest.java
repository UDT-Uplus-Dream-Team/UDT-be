package com.example.udtbe.domain.content.dto.request;

import com.example.udtbe.domain.content.entity.enums.FeedbackSortType;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record FeedbackContentGetRequest(
        Long cursor,
        @NotNull
        @Valid
        int size,
        @NotNull
        @Valid
        FeedbackType feedbackType,
        @NotNull
        @Valid
        FeedbackSortType feedbackSortType
) {

}
