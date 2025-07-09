package com.example.udtbe.domain.content.dto.common;

import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import jakarta.validation.constraints.NotNull;

public record FeedbackCreateDTO(
        @NotNull(message = "contentId 는 null일 수 없습니다.")
        Long contentId,

        @NotNull(message = "feedback은 Null일 수 없습니다.")
        FeedbackType feedback
) {

}
