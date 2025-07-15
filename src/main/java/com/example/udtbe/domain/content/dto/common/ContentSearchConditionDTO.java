package com.example.udtbe.domain.content.dto.common;

import java.time.LocalDateTime;
import java.util.List;

public record ContentSearchConditionDTO(

        List<String> categories,
        List<String> platforms,
        List<String> countries,
        List<LocalDateTime> openDates,
        List<String> ratings,
        List<String> genres
) {

}
