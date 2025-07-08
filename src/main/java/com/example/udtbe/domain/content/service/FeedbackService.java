package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.FeedbackMapper;
import com.example.udtbe.domain.content.dto.request.FeedbackRequest;
import com.example.udtbe.domain.content.dto.response.FeedbackBulkResponse;
import com.example.udtbe.domain.content.dto.response.FeedbackResponse;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.exception.MemberErrorCode;
import com.example.udtbe.global.exception.RestApiException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackQuery feedbackQuery;
    private final FeedbackRepository feedbackRepository;

    @Transactional
    public void saveFeedbacks(List<FeedbackRequest> requests, Member member) {
        List<Feedback> feedbacks = requests.stream().map(req -> {
            Content content = feedbackQuery.getContentById(req.contentId());
            FeedbackType type = req.feedback() ? FeedbackType.LIKE : FeedbackType.DISLIKE;
            return Feedback.of(type, false, member, content);
        }).toList();

        feedbackRepository.saveAll(feedbacks);
    }

    public FeedbackBulkResponse getFeedbackList(String cursor, int size,
            FeedbackType feedbackType,
            Member member) {
        List<Feedback> feedbacks = feedbackQuery.getFeedbacksByCursor(member, feedbackType, cursor,
                size);

        List<FeedbackResponse> dtoList = feedbacks.stream()
                .map(FeedbackMapper::toResponse)
                .toList();

        String nextCursor =
                feedbacks.isEmpty() ? null : feedbacks.get(feedbacks.size() - 1).getId().toString();
        boolean hasNext = feedbacks.size() == size;

        return new FeedbackBulkResponse(dtoList, nextCursor, hasNext);
    }

    @Transactional
    public void deleteFeedback(Long feedbackId, Member member) {
        Feedback feedback = feedbackQuery.getFeedbackById(feedbackId);

        if (!feedback.getMember().getId().equals(member.getId())) {
            throw new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND);
        }

        feedback.softDeleted();
    }

}
