package com.example.udtbe.domain.content.dto.common;

import java.util.List;

public record CategoryDTO(
        String category,
        List<String> genres
) {

}
