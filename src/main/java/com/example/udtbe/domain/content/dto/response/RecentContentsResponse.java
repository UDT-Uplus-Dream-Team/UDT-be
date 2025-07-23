package com.example.udtbe.domain.content.dto.response;

import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.querydsl.core.annotations.QueryProjection;
import java.util.List;

public record RecentContentsResponse(
        Long contentId,
        String title,
        String posterUrl,
        List<String> categories,
        List<String> genres
) {

    @QueryProjection
    public RecentContentsResponse(Long contentId, String title, String posterUrl,
            List<String> categories, List<String> genres) {
        this.contentId = contentId;
        this.title = title;
        this.posterUrl = posterUrl;
        this.categories = categories.stream()
                .distinct()
                .map(category ->
                        CategoryType.from(category).getType()
                ).toList();
        this.genres = genres.stream()
                .distinct()
                .map(genre ->
                        GenreType.from(genre).getType())
                .toList();
    }
}
