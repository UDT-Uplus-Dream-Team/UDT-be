package com.example.udtbe.domain.admin.dto.request;

import jakarta.validation.constraints.NotNull;

public record AdminScheduledContentResultGetsRequest(

        String cursor,

        @NotNull
        int size

) {

}
