package com.example.udtbe.domain.member.controller;

import com.example.udtbe.domain.content.dto.request.CuratedContentGetRequest;
import com.example.udtbe.domain.content.dto.response.CuratedContentGetListResponse;
import com.example.udtbe.domain.member.dto.request.MemberUpdateGenreRequest;
import com.example.udtbe.domain.member.dto.request.MemberUpdatePlatformRequest;
import com.example.udtbe.domain.member.dto.response.MemberInfoResponse;
import com.example.udtbe.domain.member.dto.response.MemberUpdateGenreResponse;
import com.example.udtbe.domain.member.dto.response.MemberUpdatePlatformResponse;
import com.example.udtbe.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
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


    @Operation(summary = "마이페이지에서 엄선된 추천 콘텐츠 조회 API")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/users/me/curated/contents")
    ResponseEntity<CuratedContentGetListResponse> getCuratedContents(
            @AuthenticationPrincipal Member member,
            @ModelAttribute @Valid CuratedContentGetRequest curatedContentGetRequest

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
}
