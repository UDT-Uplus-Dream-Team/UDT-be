package com.example.udtbe.domain.member.controller;

import com.example.udtbe.domain.member.dto.request.MemberCuratedContentGetsRequest;
import com.example.udtbe.domain.member.dto.request.MemberUpdateGenreRequest;
import com.example.udtbe.domain.member.dto.request.MemberUpdatePlatformRequest;
import com.example.udtbe.domain.member.dto.response.MemberCuratedContentGetResponse;
import com.example.udtbe.domain.member.dto.response.MemberInfoResponse;
import com.example.udtbe.domain.member.dto.response.MemberUpdateGenreResponse;
import com.example.udtbe.domain.member.dto.response.MemberUpdatePlatformResponse;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.dto.CursorPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Member API", description = "회원 관련 API")
@RequestMapping("/api")
public interface MemberControllerApiSpec {

    @Operation(summary = "마이페이지에서 유저 정보 조회 API")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/users/me")
    ResponseEntity<MemberInfoResponse> getMemberInfo(
            @AuthenticationPrincipal Member member
    );

    @Operation(summary = "마이페이지에서 엄선된 추천 콘텐츠 목록 조회 API")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/users/me/curated/contents")
    ResponseEntity<CursorPageResponse<MemberCuratedContentGetResponse>> getCuratedContents(
            @AuthenticationPrincipal Member member,
            @ModelAttribute @Valid MemberCuratedContentGetsRequest memberCuratedContentGetsRequest
    );

    @Operation(summary = "마이페이지에서 유저 선호 장르 수정")
    @ApiResponse(useReturnTypeSchema = true)
    @PatchMapping("/users/survey/genre")
    ResponseEntity<MemberUpdateGenreResponse> updateSurveyGenres(
            @AuthenticationPrincipal Member member,
            @Valid @RequestBody MemberUpdateGenreRequest memberUpdateGenreRequest
    );

    @Operation(summary = "마이페이지에서 유저 구독 플렛폼 수정")
    @ApiResponse(useReturnTypeSchema = true)
    @PatchMapping("/users/survey/platform")
    ResponseEntity<MemberUpdatePlatformResponse> updateSurveyPlatforms(
            @AuthenticationPrincipal Member member,
            @Valid @RequestBody MemberUpdatePlatformRequest memberUpdateGenreRequest
    );

    @Operation(summary = "CuratedContent 삭제 API", description = "저장한 엄선된 컨텐츠들을 삭제한다.")
    @ApiResponse(useReturnTypeSchema = true)
    @DeleteMapping("/users/me/curated/contents/{contentId}")
    ResponseEntity<Void> deleteCuratedContent(@PathVariable(name = "contentId") Long contentId,
            @AuthenticationPrincipal Member member);
}
