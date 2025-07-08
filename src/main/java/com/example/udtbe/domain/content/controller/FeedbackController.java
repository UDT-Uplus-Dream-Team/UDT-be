package com.example.udtbe.domain.content.controller;

import com.example.udtbe.domain.content.dto.request.BulkFeedbackRequest;
import com.example.udtbe.domain.content.dto.response.BulkFeedbackResponseDto;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.service.FeedbackQuery;
import com.example.udtbe.domain.content.service.FeedbackService;
import com.example.udtbe.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedbackController implements FeedbackControllerApiSpec {

    private final FeedbackQuery feedbackQuery;
    private final FeedbackService feedbackService;

    @Override
    public ResponseEntity<Void> saveFeedback(BulkFeedbackRequest bulkFeedbackRequest,
            Member member) {
        feedbackService.saveFeedbacks(bulkFeedbackRequest.feedbacks(), member);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<BulkFeedbackResponseDto> getFeedbackByCursor(
            String cursor, int size, FeedbackType feedbackType, Member member) {
        BulkFeedbackResponseDto response = feedbackService.getFeedbackList(
                cursor, size, feedbackType, member);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteFeedback(Long feedbackId, Member member) {
        feedbackService.deleteFeedback(feedbackId, member);
        return ResponseEntity.ok().build();
    }
}
