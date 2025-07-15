package com.example.udtbe.domain.content.dto.common;

import java.util.List;

public record CuratedContentDTO(
        Long contentId,
        String title,
        String posterUrl,
        String rating,
        List<String> countries,
        List<CategoryDTO> categories,
        List<String> platform
) {

}
