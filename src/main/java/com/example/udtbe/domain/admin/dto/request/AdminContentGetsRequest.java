package com.example.udtbe.domain.admin.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AdminContentGetsRequest(
        String cursor,
        @NotNull(message = "커서 사이즈는 필수 값 입니다.")
        @Min(value = 1, message = "커서 사이즈는 1 이상입니다.")
        int size,
        String categoryType
) {

}
