package com.example.udtbe.domain.content.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record FeedbackListDeleteRequest(
        @NotNull(message = "feedbackIds는 필수값입니다.")
        @NotEmpty(message = "feedbackIds는 최소 1개 이상이어야 합니다.")
        List<Long> feedbackIds
) {

}
