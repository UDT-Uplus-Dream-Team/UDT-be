package com.example.udtbe.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;

public record MemberCuratedContentGetsRequest(
        Long cursor,
        @NotNull
        Integer size
) {

}