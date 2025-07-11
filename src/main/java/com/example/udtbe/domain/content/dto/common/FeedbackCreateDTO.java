package com.example.udtbe.domain.content.dto.common;

import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import jakarta.validation.constraints.NotNull;

public record FeedbackCreateDTO(
        @NotNull(message = "contentId 는 필수값입니다.")
        Long contentId,

        @NotNull(message = "feedback은 필수값입니다.")
        FeedbackType feedback
) {

}
