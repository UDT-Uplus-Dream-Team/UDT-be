package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.exception.RestApiException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackQuery {

    private final ContentRepository contentRepository;
    private final FeedbackRepository feedbackRepository;

    public Content getContentById(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new RestApiException(ContentErrorCode.CONTENT_NOT_FOUND));
    }

    public Feedback getFeedbackById(Long feedbackId) {
        return feedbackRepository.getFeedbackById(feedbackId)
                .orElseThrow(() -> new RestApiException(ContentErrorCode.CONTENT_NOT_FOUND));
    }

    public List<Feedback> getFeedbacksByCursor(Member member, FeedbackType type, String cursor,
            int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        if (cursor == null) {
            return feedbackRepository.findTopByMemberAndFeedbackTypeOrderByIdDesc(member, type,
                    pageable);
        }
        return feedbackRepository.findTopByMemberAndFeedbackTypeAndIdLessThanOrderByIdDesc(
                member, type, cursor, pageable);
    }
}