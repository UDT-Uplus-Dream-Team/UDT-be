package com.example.udtbe.domain.admin.controller;

import com.example.udtbe.domain.admin.dto.request.AdminCastsRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentGetsRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.response.AdminCastsRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentUpdateResponse;
import com.example.udtbe.global.dto.CursorPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "관리자 API", description = "관리자 관련 API")
public interface AdminControllerApiSpec {

    @Operation(summary = "콘텐츠 등록", description = "새로운 콘텐츠를 등록하고 등록된 contenId를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록된 콘텐츠 contentId 반환"),
            @ApiResponse(responseCode = "400", description = "올바르지 않은 분류/플렛폼/장르 타입")
    })
    @PostMapping("/api/admin/contents")
    ResponseEntity<AdminContentRegisterResponse> registerContent(
            @Valid @RequestBody AdminContentRegisterRequest adminContentRegisterRequest
    );

    @Operation(summary = "콘텐츠 수정", description = "기존 콘텐츠의 필드 및 메타데이터를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 콘텐츠"),
            @ApiResponse(responseCode = "400", description = "올바르지 않은 분류/플렛폼/장르 타입")
    })
    @PatchMapping("/api/admin/contents/{contentId}")
    ResponseEntity<AdminContentUpdateResponse> updateContent(
            @PathVariable(name = "contentId") Long contentId,
            @Valid @RequestBody AdminContentUpdateRequest adminContentUpdateRequest
    );

    @Operation(summary = "콘텐츠 목록 조회", description = "커서 기반 페이지네이션으로 콘텐츠 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "콘텐츠 목록 및 페이징 정보 반환")
    @GetMapping("/api/admin/contents")
    ResponseEntity<CursorPageResponse<AdminContentGetResponse>> getContents(
            @ModelAttribute @Valid AdminContentGetsRequest adminContentGetsRequest
    );

    @Operation(summary = "콘텐츠 상세 조회", description = "지정된 ID의 콘텐츠 상세 정보를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 콘텐츠")
    })
    @GetMapping("/api/admin/contents/{contentId}")
    ResponseEntity<AdminContentGetDetailResponse> getContent(
            @PathVariable(name = "contentId") Long contentId
    );

    @Operation(summary = "콘텐츠 삭제", description = "지정된 ID의 콘텐츠를 소프트 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 콘텐츠")
    })
    @DeleteMapping("/api/admin/contents/{contentId}")
    ResponseEntity<Void> deleteContent(
            @PathVariable(name = "contentId") Long contentId
    );

    @Operation(summary = "출연진 다건 등록", description = "새로운 출연진들을 다건 등록한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록된 출연진 contentId 목록 반환"),
    })
    @PostMapping("/api/admin/casts")
    ResponseEntity<AdminCastsRegisterResponse> registerCasts(
            @Valid @RequestBody AdminCastsRegisterRequest adminCastsRegisterRequest
    );
}

