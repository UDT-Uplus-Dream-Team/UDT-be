package com.example.udtbe.domain.content.controller;

import com.example.udtbe.domain.content.dto.ContentRecommendationResponse;
import com.example.udtbe.domain.content.service.ContentRecommendationService;
import com.example.udtbe.domain.member.entity.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecommendContentController implements RecommendContentControllerApiSpec {

    private final ContentRecommendationService contentRecommendationService;

    @Override
    public ResponseEntity<List<ContentRecommendationResponse>> getRecommendations(
            @AuthenticationPrincipal Member member,
            @RequestParam(defaultValue = "10") int limit) {
        List<ContentRecommendationResponse> recommendations = contentRecommendationService.recommendContents(
                member,
                limit);

        return ResponseEntity.ok(recommendations);
    }
}
