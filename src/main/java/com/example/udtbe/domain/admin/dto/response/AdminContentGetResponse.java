package com.example.udtbe.domain.admin.dto.response;

import com.example.udtbe.domain.content.entity.enums.CategoryType;
import com.example.udtbe.domain.content.entity.enums.PlatformType;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.List;

public record AdminContentGetResponse(
        Long contentId,
        String title,
        String posterUrl,
        LocalDateTime openDate,
        String rating,
        List<String> categories,
        List<String> platforms
) {

    @QueryProjection
    public AdminContentGetResponse(Long contentId, String title, String posterUrl,
            LocalDateTime openDate, String rating, List<String> categories,
            List<String> platforms) {
        this.contentId = contentId;
        this.title = title;
        this.posterUrl = posterUrl;
        this.openDate = openDate;
        this.rating = rating;
        this.categories = categories.stream()
                .distinct()
                .map(category ->
                        CategoryType.from(category).getType()
                ).toList();
        this.platforms = platforms.stream()
                .distinct()
                .map(platform ->
                        PlatformType.from(platform).getType()
                ).toList();
    }
}
