package com.example.udtbe.domain.admin.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Objects;

public record AdminMemberListGetRequest(
        String cursor,
        @Min(value = 1, message = "사용자는 최소 1명 이상 조회해야 합니다.")
        @Max(value = 20, message = "사용자는 최대 20명 조회할 수 있습니다.")
        Integer size,
        String keyword
) {

    public int getSizeOrDefault() {
        return Objects.nonNull(size) ? size : 10;
    }
}
