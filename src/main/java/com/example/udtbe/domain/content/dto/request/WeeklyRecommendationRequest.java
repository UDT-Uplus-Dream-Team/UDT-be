package com.example.udtbe.domain.content.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record WeeklyRecommendationRequest(
        @Min(value = 1, message = "요청 크기는 최소 1개입니다.")
        @Max(value = 10, message = "요청 크기는 최대 10개입니다.")
        int size
) {

}
