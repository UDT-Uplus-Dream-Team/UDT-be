package com.example.udtbe.domain.content.dto.request;

import com.example.udtbe.domain.content.entity.enums.FeedbackSortType;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import jakarta.validation.constraints.NotNull;

public record FeedbackContentGetRequest(
        Long cursor,
        @NotNull
        int size,
        @NotNull
        FeedbackType feedbackType,
        @NotNull
        FeedbackSortType feedbackSortType
) {

}
