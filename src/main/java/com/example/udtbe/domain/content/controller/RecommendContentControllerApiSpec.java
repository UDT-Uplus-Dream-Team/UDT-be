package com.example.udtbe.domain.content.controller;

import com.example.udtbe.domain.content.dto.request.CuratedContentRequest;
import com.example.udtbe.domain.content.dto.response.ContentRecommendationResponse;
import com.example.udtbe.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Content Recommendation API", description = "콘텐츠 추천 관련 API")
@RequestMapping("/api/v1/contents")
public interface RecommendContentControllerApiSpec {

    @Operation(
            summary = "개인화 콘텐츠 추천 API",
            description = "사용자의 설문조사 결과 기반으로 개인화된 콘텐츠를 추천합니다. " +
                    "개인화 추천이 불가능한 경우 인기 콘텐츠를 제공합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "추천 콘텐츠 목록 조회 성공",
            useReturnTypeSchema = true
    )
    @GetMapping("/recommendations")
    ResponseEntity<List<ContentRecommendationResponse>> getRecommendations(
            @Parameter(description = "인증된 사용자 정보", required = true)
            @AuthenticationPrincipal Member member,

            @Parameter(
                    description = "추천 콘텐츠 개수 (기본값: 10, 최소값: 5)",
                    example = "10"
            )
            @RequestParam(defaultValue = "10") int limit
    );

    @Operation(
            summary = "엄선된 콘텐츠 추천 API",
            description = "사용자의 설문조사보다 최근 사용자의 피드백 기반으로 개인화된 콘텐츠를 추천합니다. " +
                    "개인화 추천이 불가능한 경우 인기 콘텐츠를 제공합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "엄선된 추천 콘텐츠 목록 조회 성공",
            useReturnTypeSchema = true
    )
    @GetMapping("/recommendations/curated")
    ResponseEntity<List<ContentRecommendationResponse>> getCurated(
            @Parameter(description = "인증된 사용자 정보", required = true)
            @AuthenticationPrincipal Member member,
            @Parameter(
                    description = "추천 콘텐츠 개수 (기본값: 6)",
                    example = "6"
            )
            @RequestParam(defaultValue = "6") int limit
    );

    @Operation(summary = "엄선된 콘텐츠 저장 API", description = "엄선된 콘텐츠들을 저장한다.")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/recommendations/contents")
    ResponseEntity<Void> saveCuratedContent(
            @RequestBody @Valid CuratedContentRequest curatedContentRequest,
            @AuthenticationPrincipal Member member);
}