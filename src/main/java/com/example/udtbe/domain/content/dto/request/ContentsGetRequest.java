package com.example.udtbe.domain.content.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record ContentsGetRequest(
        String cursor,
        Integer size,
        List<String> categories,
        List<String> platforms,
        List<String> countries,
        List<LocalDateTime> openDates,
        List<String> ratings,
        List<String> genres
) {

    public ContentsGetRequest {
        if (Objects.isNull(size)) {
            size = 20;
        }
    }
}
