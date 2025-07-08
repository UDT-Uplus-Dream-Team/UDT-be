package com.example.udtbe.domain.content.dto.response;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentCategory;
import com.example.udtbe.domain.content.entity.ContentDirector;
import com.example.udtbe.domain.content.entity.ContentPlatform;
import com.example.udtbe.domain.content.entity.Feedback;
import java.time.LocalDateTime;
import java.util.List;

public record FeedbackResponse(
        Long contentId,
        String title,
        String description,
        String posterUrl,
        String backdropUrl,
        LocalDateTime openDate,
        Integer runningTime,
        String rating,
        List<ContentCategory> categories,
        List<ContentDirector> directors,
        List<ContentPlatform> platforms
) {

    public static FeedbackResponse from(Feedback feedback) {
        Content content = feedback.getContent();
        return new FeedbackResponse(
                content.getId(),
                content.getTitle(),
                content.getDescription(),
                content.getPosterUrl(),
                content.getBackdropUrl(),
                content.getOpenDate(),
                content.getRunningTime(),
                content.getRating(),
                content.getContentCategories(),
                content.getContentDirectors(),
                content.getContentPlatforms()
        );
    }
}
