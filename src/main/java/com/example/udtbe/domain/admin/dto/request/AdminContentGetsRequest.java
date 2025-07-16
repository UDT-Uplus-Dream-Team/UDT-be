package com.example.udtbe.domain.admin.dto.request;

import jakarta.validation.constraints.NotNull;

public record AdminContentGetsRequest(
        Long cursor,
        @NotNull
        int size,
        String categoryType
) {

}
