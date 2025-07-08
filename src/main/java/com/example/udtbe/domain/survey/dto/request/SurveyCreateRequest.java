package com.example.udtbe.domain.survey.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SurveyCreateRequest(
        @NotNull
        @Size(min = 1, max = 7)
        List<String> platforms,

        @NotNull
        @Size(min = 1, max = 3)
        List<String> genres
) {

}
