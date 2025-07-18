package com.example.udtbe.domain.content.dto;

import com.example.udtbe.domain.content.dto.common.FeedbackContentDTO;
import com.example.udtbe.domain.content.dto.common.FeedbackCreateDTO;
import com.example.udtbe.domain.content.dto.response.PopularContentsResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.service.FeedbackQuery;
import com.example.udtbe.domain.member.entity.Member;
import java.util.ArrayList;
import java.util.List;

public class FeedbackMapper {

    private static Feedback createFeedback(FeedbackCreateDTO req, Member member,
            FeedbackQuery feedbackQuery) {
        Content content = feedbackQuery.getContentById(req.contentId());
        return Feedback.of(req.feedback(), false, member, content);
    }

    public static List<Feedback> mapToFeedbackList(List<FeedbackCreateDTO> requests, Member member,
            FeedbackQuery feedbackQuery) {
        return requests.stream()
                .map(req -> createFeedback(req, member, feedbackQuery))
                .toList();
    }

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

    public static List<PopularContentsResponse> toPopularContentsResponses(List<Content> contents) {
        List<PopularContentsResponse> popularContentsResponses = new ArrayList<>();

        for (Content content : contents) {
            popularContentsResponses.add(new PopularContentsResponse(
                    content.getId(), content.getPosterUrl()
            ));
        }

        return popularContentsResponses;
    }

}
