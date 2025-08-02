package com.example.udtbe.domain.admin.dto.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AdminContentJobGetsRequest(
        String cursor,
        @Max(value = 20)
        @Min(value = 1)
        int size,
        @NotNull
        String type
) {

}
