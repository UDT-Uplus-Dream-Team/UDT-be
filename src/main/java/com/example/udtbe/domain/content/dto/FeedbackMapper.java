package com.example.udtbe.domain.content.dto;

import com.example.udtbe.domain.content.dto.response.FeedbackResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;

public class FeedbackMapper {

    public static FeedbackResponse toResponse(Feedback feedback) {
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
                ContentCategoryMapper.toDtoList(content.getContentCategories()),
                ContentDirectorMapper.toDtoList(content.getContentDirectors()),
                ContentPlatformMapper.toDtoList(content.getContentPlatforms())
        );
    }

}
