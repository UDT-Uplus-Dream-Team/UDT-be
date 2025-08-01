package com.example.udtbe.domain.admin.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record AdminCastsGetRequest(
        @NotBlank(message = "검색할 출연진 이름은 필수입니다.")
        String name,
        String cursor,
        @Min(value = 1, message = "출연진은 최소 1명 이상 조회해야 합니다.")
        @Max(value = 20, message = "출연진은 최대 20명 조회할 수 있습니다.")
        Integer size
) {

    public int getSizeOrDefault() {
        return Objects.nonNull(size) ? size : 10;
    }
}
