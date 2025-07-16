package com.example.udtbe.domain.admin.dto.common;

import java.util.List;

public record AdminCategoryDTO(
        String categoryType,
        List<String> genres
) {

}
