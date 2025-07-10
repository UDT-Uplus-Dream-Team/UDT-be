package com.example.udtbe.domain.content.dto;

import com.example.udtbe.domain.content.dto.common.FeedbackContentDTO;
import com.example.udtbe.domain.content.dto.common.FeedbackCreateDTO;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import com.example.udtbe.domain.content.service.FeedbackQuery;
import com.example.udtbe.domain.member.entity.Member;
import java.util.List;

public class FeedbackMapper {

    public static void saveAllFeedbacks(List<FeedbackCreateDTO> requests, Member member,
            FeedbackQuery feedbackQuery, FeedbackRepository feedbackRepository) {

        List<Feedback> feedbacks = requests.stream()
                .map(req -> {
                    Content content = feedbackQuery.getContentById(req.contentId());
                    return Feedback.of(req.feedback(), false, member, content);
                })
                .toList();

        feedbackRepository.saveAll(feedbacks);
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

}
