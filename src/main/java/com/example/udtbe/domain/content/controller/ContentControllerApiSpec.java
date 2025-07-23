package com.example.udtbe.domain.content.controller;

import com.example.udtbe.domain.content.dto.request.ContentsGetRequest;
import com.example.udtbe.domain.content.dto.request.PopularContentsRequest;
import com.example.udtbe.domain.content.dto.request.RecentContentsRequest;
import com.example.udtbe.domain.content.dto.request.WeeklyRecommendationRequest;
import com.example.udtbe.domain.content.dto.response.ContentDetailsGetResponse;
import com.example.udtbe.domain.content.dto.response.ContentsGetResponse;
import com.example.udtbe.domain.content.dto.response.PopularContentsResponse;
import com.example.udtbe.domain.content.dto.response.RecentContentsResponse;
import com.example.udtbe.domain.content.dto.response.WeeklyRecommendedContentsResponse;
import com.example.udtbe.global.dto.CursorPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "콘텐츠 API", description = "콘텐츠 관련 API")
public interface ContentControllerApiSpec {

    @Operation(summary = "콘텐츠 필터링 목록 조회 API", description = "필터링 조건에 따른 콘텐츠 목록을 가져온다.")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/api/contents")
    public ResponseEntity<CursorPageResponse<ContentsGetResponse>> getContents(
            @ModelAttribute @Valid ContentsGetRequest request
    );

    @Operation(summary = "콘텐츠 상세 조회 API", description = "콘텐츠 정보를 상세 조회한다.")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/api/contents/{contentId}")
    public ResponseEntity<ContentDetailsGetResponse> getContentDetails(
            @PathVariable(name = "contentId") Long contentId
    );

    @Operation(summary = "요일별 추천 콘텐츠 조회 API", description = "요일별 추천 콘텐츠 목록을 조회한다.")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/api/contents/weekly")
    public ResponseEntity<List<WeeklyRecommendedContentsResponse>> getWeeklyRecommendedContents(
            @ModelAttribute @Valid WeeklyRecommendationRequest request
    );

    @Operation(summary = "인기 콘텐츠 목록 조회 API", description = "인기 콘텐츠 목록을 조회한다.")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/api/contents/popular")
    public ResponseEntity<List<PopularContentsResponse>> getPopularContents(
            @ModelAttribute @Valid PopularContentsRequest request
    );

    @Operation(summary = "최신 콘텐츠 목록 조회 API", description = "최신 콘텐츠 목록을 조회한다.")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/api/contents/recent")
    ResponseEntity<List<RecentContentsResponse>> getRecentContents(
            @ModelAttribute @Valid RecentContentsRequest request
    );

}
