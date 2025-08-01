package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.FeedbackMapper;
import com.example.udtbe.domain.content.dto.common.FeedbackContentDTO;
import com.example.udtbe.domain.content.dto.common.FeedbackCreateDTO;
import com.example.udtbe.domain.content.dto.request.FeedbackContentGetRequest;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.dto.CursorPageResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackQuery feedbackQuery;
    private final FeedbackRepository feedbackRepository;
    private final ContentQuery contentQuery;
    private final FeedbackStatisticsQuery feedbackStatisticsQuery;

    @Transactional
    public void saveFeedbacks(List<FeedbackCreateDTO> requests, Member member) {
        List<Feedback> feedbacks = new ArrayList<>();

        for (FeedbackCreateDTO feedbackCreateDTO : requests) {
            Content content = feedbackQuery.findContentById(feedbackCreateDTO.contentId());

            List<GenreType> genres = contentQuery.getGenreTypeById(feedbackCreateDTO.contentId());

            FeedbackType newFeedbackType = feedbackCreateDTO.feedback();

            Optional<Feedback> findFeedback = feedbackQuery.findFeedbackByMemberIdAndContentId(
                    member.getId(),
                    content.getId());

            if (findFeedback.isEmpty()) {
                Feedback newFeedback = Feedback.of(newFeedbackType, false, member, content);
                genres.forEach(
                        genreType -> feedbackStatisticsQuery.increaseStatics(member, genreType,
                                newFeedbackType));
                feedbacks.add(newFeedback);
                continue;
            }

            Feedback prevFeedback = findFeedback.get();
            FeedbackType prevFeedbackType = prevFeedback.getFeedbackType();

            if (prevFeedback.isDeleted()) {
                prevFeedback.switchDeleted();
                genres.forEach(
                        genreType -> feedbackStatisticsQuery.increaseStatics(member, genreType,
                                newFeedbackType));
            }

            if (prevFeedbackType != newFeedbackType) {
                genres.forEach(genreType -> {
                    feedbackStatisticsQuery.decreaseStatics(member, genreType, prevFeedbackType);
                    feedbackStatisticsQuery.increaseStatics(member, genreType, newFeedbackType);
                });
                prevFeedback.updateFeedbackType(newFeedbackType);
            }
            feedbacks.add(prevFeedback);
        }

        if (!feedbacks.isEmpty()) {
            feedbackRepository.saveAll(feedbacks);
        }
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<FeedbackContentDTO> getFeedbackList(FeedbackContentGetRequest request,
            Member member) {
        List<Feedback> feedbacks = feedbackQuery.getFeedbacksByCursor(member, request);
        if (feedbacks == null) {
            feedbacks = Collections.emptyList();
        }

        boolean hasNext = feedbacks.size() > request.size();
        List<Feedback> limited = hasNext ? feedbacks.subList(0, request.size()) : feedbacks;

        List<FeedbackContentDTO> dtoList = FeedbackMapper.toResponseList(limited);

        Long nextCursorRaw = limited.isEmpty() ? null : limited.get(limited.size() - 1).getId();
        String nextCursor = nextCursorRaw == null ? null : nextCursorRaw.toString();

        return new CursorPageResponse<>(dtoList, nextCursor, hasNext);
    }

    @Transactional
    public void deleteFeedback(List<Long> feedbackIds, Member member) {
        List<Feedback> feedbacks = feedbackQuery.findFeedbackByIdList(member.getId(), feedbackIds);
        for (Feedback feedback : feedbacks) {
            feedback.switchDeleted();
            List<GenreType> genres = contentQuery.getGenreTypeById(feedback.getContent().getId());
            genres.forEach(
                    genreType -> feedbackStatisticsQuery.decreaseStatics(member, genreType,
                            feedback.getFeedbackType()));
        }
    }
}
