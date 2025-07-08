package com.example.udtbe.domain.content.dto;

import com.example.udtbe.domain.content.dto.response.ContentCategoryResponseDTO;
import com.example.udtbe.domain.content.entity.ContentCategory;
import java.util.List;

public class ContentCategoryMapper {

    public static List<ContentCategoryResponseDTO> toDtoList(List<ContentCategory> categories) {
        return categories.stream()
                .map(c -> new ContentCategoryResponseDTO(c.getId(), c.getCategory().toString()))
                .toList();
    }

}
