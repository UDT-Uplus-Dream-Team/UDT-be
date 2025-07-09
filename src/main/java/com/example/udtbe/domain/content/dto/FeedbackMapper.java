package com.example.udtbe.domain.content.dto;

import com.example.udtbe.domain.content.dto.common.FeedbackContentDTO;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import java.util.List;

public class FeedbackMapper {

    public static FeedbackContentDTO toResponse(Feedback feedback) {
        Content content = feedback.getContent();

        return new FeedbackContentDTO(
                content.getId(),
                content.getTitle(),
                content.getPosterUrl(),
                content.getOpenDate(),
                content.getRunningTime(),
                content.getEpisode(),
                ContentCategoryMapper.categoryTypes(content.getContentCategories()),
                ContentDirectorMapper.directorNames(content.getContentDirectors())
        );
    }

    public static List<FeedbackContentDTO> toResponseList(List<Feedback> feedbacks) {
        return feedbacks.stream()
                .map(FeedbackMapper::toResponse)
                .toList();
    }

}
