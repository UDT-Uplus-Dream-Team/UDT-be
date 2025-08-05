package com.example.udtbe.domain.admin.dto.common;

import com.example.udtbe.domain.content.entity.Category;
import com.querydsl.core.annotations.QueryProjection;

public record CategoryMetricDTO(

        Long categoryId,
        String categoryType,
        Long count
) {

    @QueryProjection
    public CategoryMetricDTO(Category category, Long count) {
        this(category.getId(), category.getCategoryType().getType(), count);
    }
}
