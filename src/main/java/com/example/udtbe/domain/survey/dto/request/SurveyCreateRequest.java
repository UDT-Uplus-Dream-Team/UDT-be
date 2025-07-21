package com.example.udtbe.domain.survey.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SurveyCreateRequest(
        @NotNull(message = "플랫폼 리스트는 필수 값입니다.")
        @Size(min = 1, max = 7, message = "OTT 플랫폼은 최소 1개 이상 최대 7개 이하입니다.")
        List<String> platforms,

        @NotNull(message = "선호 장르는 필수 값입니다.")
        @Size(min = 1, max = 3, message = "선호 장르는 최소 1개 이상 최대 3개 이하입니다.")
        List<String> genres,

        List<Long> contentIds
) {

}
