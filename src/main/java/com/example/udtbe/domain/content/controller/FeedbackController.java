package com.example.udtbe.domain.content.controller;

import com.example.udtbe.domain.content.dto.request.BulkFeedbackRequestDto;
import com.example.udtbe.domain.content.service.FeedbackService;
import com.example.udtbe.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/recommend/contents/feedbacks")
    public ResponseEntity<Void> saveFeedback(
            @RequestBody BulkFeedbackRequestDto bulkFeedbackRequestDto,
            @AuthenticationPrincipal Member member) {
        feedbackService.saveFeedbacks(bulkFeedbackRequestDto.feedbacks(), member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/me/feedbacks/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long feedbackId,
            @AuthenticationPrincipal Member member) {
        feedbackService.deleteFeedback(feedbackId, member);
        return ResponseEntity.ok().build();
    }
}
