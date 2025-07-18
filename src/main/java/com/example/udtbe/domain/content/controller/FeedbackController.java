package com.example.udtbe.domain.content.controller;

import com.example.udtbe.domain.content.dto.common.FeedbackContentDTO;
import com.example.udtbe.domain.content.dto.request.FeedbackContentGetRequest;
import com.example.udtbe.domain.content.dto.request.FeedbackCreateBulkRequest;
import com.example.udtbe.domain.content.service.FeedbackService;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.dto.CursorPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedbackController implements FeedbackControllerApiSpec {

    private final FeedbackService feedbackService;

    @Override
    public ResponseEntity<Void> saveFeedback(FeedbackCreateBulkRequest bulkFeedbackRequest,
            Member member) {
        feedbackService.saveFeedbacks(bulkFeedbackRequest.feedbacks(), member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<CursorPageResponse<FeedbackContentDTO>> getFeedbackByCursor(
            FeedbackContentGetRequest request, Member member) {
        // TODO: 3차 MVP 때 플랫폼별, 장르별 FeedbackSortType 반영
        CursorPageResponse<FeedbackContentDTO> response = feedbackService.getFeedbackList(
                request, member);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteFeedback(Long feedbackId, Member member) {
        feedbackService.deleteFeedback(feedbackId, member);
        return ResponseEntity.noContent().build();
    }
}
