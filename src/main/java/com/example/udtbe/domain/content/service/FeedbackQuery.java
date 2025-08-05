package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.dto.request.FeedbackContentGetRequest;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.domain.content.exception.FeedbackErrorCode;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.exception.RestApiException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackQuery {

    private final ContentRepository contentRepository;
    private final FeedbackRepository feedbackRepository;

    public Content findContentById(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new RestApiException(ContentErrorCode.CONTENT_NOT_FOUND));
    }

    public Feedback findFeedbackById(Long feedbackId) {
        return feedbackRepository.findFeedbackById(feedbackId)
                .orElseThrow(() -> new RestApiException(FeedbackErrorCode.FEEDBACK_NOT_FOUND));
    }

    public List<Feedback> getFeedbacksByCursor(Member member,
            FeedbackContentGetRequest feedbackContentGetRequest) {
        return feedbackRepository.getFeedbacksByCursor(feedbackContentGetRequest, member);
    }

    public Optional<Feedback> findFeedbackByMemberIdAndContentId(Long memberId, Long contentId) {
        return feedbackRepository.findFeedbackByMemberIdAndContentId(memberId, contentId);
    }

    public List<Feedback> findFeedbackByIdList(Long memberId,
            List<Long> feedbackIds) {
        return feedbackRepository.findByMemberIdAndIdIn(memberId, feedbackIds);
    }
}
