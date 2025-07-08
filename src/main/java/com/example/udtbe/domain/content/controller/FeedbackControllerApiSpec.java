package com.example.udtbe.domain.content.controller;

import com.example.udtbe.domain.content.dto.request.BulkFeedbackRequest;
import com.example.udtbe.domain.content.dto.response.BulkFeedbackResponse;
import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "피드백 API", description = "피드백 관련 API")
@RequestMapping("/api")
public interface FeedbackControllerApiSpec {

    @Operation(summary = "Feedback 저장 API", description = "피드백을 저장한다.")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/recommend/contents/feedbacks")
    ResponseEntity<Void> saveFeedback(
            @RequestBody BulkFeedbackRequest bulkFeedbackRequest,
            @AuthenticationPrincipal Member member);


    @Operation(summary = "Feedback한 Content 조회 API", description = "좋아요/싫어요 한 컨텐츠들을 조회한다")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/users/me/feedbacks")
    ResponseEntity<BulkFeedbackResponse> getFeedbackByCursor(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam FeedbackType feedbackType,
            @AuthenticationPrincipal Member member);

    @Operation(summary = "Feedback한 Content 삭제 API", description = "좋아요/싫어요 한 컨텐츠들을 삭제한다.")
    @ApiResponse(useReturnTypeSchema = true)
    @DeleteMapping("/users/me/feedbacks/{feedbackId}")
    ResponseEntity<Void> deleteFeedback(@PathVariable Long feedbackId,
            @AuthenticationPrincipal Member member);
}
