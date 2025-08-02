package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.admin.dto.common.CategoryMetricDTO;
import java.util.List;

public record AdminContentCategoryMetricResponse(
        List<CategoryMetricDTO> categoryMetrics
) {

}
