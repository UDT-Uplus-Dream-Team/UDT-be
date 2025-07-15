package com.example.udtbe.domain.admin.dto.common;

import java.util.List;

public record CategoryDTO(
        String categoryType,
        List<String> genres
) {

}
