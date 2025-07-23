package com.example.udtbe.domain.content.controller;

import com.example.udtbe.domain.content.dto.request.CuratedContentRequest;
import com.example.udtbe.domain.content.dto.response.ContentRecommendationResponse;
import com.example.udtbe.domain.content.service.ContentRecommendationService;
import com.example.udtbe.domain.content.service.ContentService;
import com.example.udtbe.domain.member.entity.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecommendContentController implements RecommendContentControllerApiSpec {

    private final ContentRecommendationService contentRecommendationService;
    private final ContentService contentService;

    @Override
    public ResponseEntity<List<ContentRecommendationResponse>> getRecommendations(
            @AuthenticationPrincipal Member member,
            @RequestParam(defaultValue = "10") int limit) {
        List<ContentRecommendationResponse> recommendations = contentRecommendationService.recommendContents(
                member,
                limit);

        return ResponseEntity.ok(recommendations);
    }

    @Override
    public ResponseEntity<List<ContentRecommendationResponse>> getCurated(
            @AuthenticationPrincipal Member member,
            @RequestParam(defaultValue = "6") int limit) {
        List<ContentRecommendationResponse> recommendations = contentRecommendationService.recommendCuratedContents(
                member, limit);

        return ResponseEntity.ok(recommendations);
    }

    @Override
    public ResponseEntity<Void> saveCuratedContent(CuratedContentRequest curatedContentRequest,
            Member member) {
        contentService.saveCuratedContent(curatedContentRequest.contentId(), member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
