package com.example.udtbe.domain.content.dto;

import com.example.udtbe.domain.content.dto.common.CategoryDTO;
import com.example.udtbe.domain.content.entity.ContentCategory;
import com.example.udtbe.domain.content.entity.ContentGenre;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContentCategoryMapper {

    public static List<String> categoryTypes(List<ContentCategory> categories) {
        return categories.stream()
                .map(c -> c.getCategory().getCategoryType().getType())
                .toList();
    }

    public static List<CategoryDTO> toCategoryDTOList(
            List<ContentCategory> categories, List<ContentGenre> genres) {

        Map<String, List<String>> categoryGenreMap = genres.stream()
                .collect(Collectors.groupingBy(
                        g -> g.getGenre().getCategory().getCategoryType().getType(),
                        Collectors.mapping(g -> g.getGenre().getGenreType().getType(),
                                Collectors.toList())
                ));

        return categories.stream()
                .map(c -> new CategoryDTO(
                        c.getCategory().getCategoryType().getType(),
                        categoryGenreMap.getOrDefault(c.getCategory().getCategoryType().getType(),
                                List.of())
                ))
                .toList();
    }
}
