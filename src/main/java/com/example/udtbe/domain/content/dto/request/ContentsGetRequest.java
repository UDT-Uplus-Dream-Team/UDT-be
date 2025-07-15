package com.example.udtbe.domain.content.dto.request;

import com.example.udtbe.domain.content.dto.common.ContentSearchConditionDTO;
import java.util.Objects;

public record ContentsGetRequest(
        String cursor,
        Integer size,
        ContentSearchConditionDTO contentSearchConditionDTO
) {

    public ContentsGetRequest {
        if (Objects.isNull(size)) {
            size = 20;
        }
    }
}
