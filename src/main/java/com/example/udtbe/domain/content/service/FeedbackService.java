package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.FeedbackMapper;
import com.example.udtbe.domain.content.dto.common.FeedbackContentDTO;
import com.example.udtbe.domain.content.dto.common.FeedbackCreateDTO;
import com.example.udtbe.domain.content.dto.request.FeedbackContentGetRequest;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.exception.FeedbackErrorCode;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.dto.CursorPageResponse;
import com.example.udtbe.global.exception.RestApiException;
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

    @Transactional
    public void saveFeedbacks(List<FeedbackCreateDTO> requests, Member member) {
        List<Feedback> feedbacks = new ArrayList<>();

        for (FeedbackCreateDTO feedbackCreateDTO : requests) {
            Content content = feedbackQuery.findContentById(feedbackCreateDTO.contentId());

            Optional<Feedback> findFeedback = feedbackQuery.findFeedbackByMemberIdAndContentId(
                    member.getId(),
                    content.getId());

            if (findFeedback.isPresent()) {
                Feedback feedback = findFeedback.get();
                if (feedback.isDeleted()) {
                    feedback.switchDeleted();
                }
                feedback.updateFeedbackType(feedbackCreateDTO.feedback());
                feedbacks.add(feedback);
            } else {
                Feedback newFeedback = Feedback.of(feedbackCreateDTO.feedback(), false, member,
                        content);
                feedbacks.add(newFeedback);
            }
        }

        feedbackRepository.saveAll(feedbacks);
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
    public void deleteFeedback(Long feedbackId, Member member) {
        Feedback feedback = feedbackQuery.findFeedbackById(feedbackId);

        if (!feedback.getMember().getId().equals(member.getId())) {
            throw new RestApiException(FeedbackErrorCode.FEEDBACK_OWNER_MISSMATCH);
        }

        feedback.switchDeleted();
    }

}
