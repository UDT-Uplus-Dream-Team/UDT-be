package com.example.udtbe.domain.content.dto;

import com.example.udtbe.domain.content.entity.ContentCategory;
import java.util.List;

public class ContentCategoryMapper {

    public static List<String> categoryTypes(List<ContentCategory> categories) {
        return categories.stream()
                .map(c -> c.getCategory().getCategoryType().getType())
                .toList();
    }

}
