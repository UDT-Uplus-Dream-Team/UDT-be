package com.example.udtbe.domain.admin.controller;

import com.example.udtbe.domain.admin.dto.request.AdminCastsGetRequest;
import com.example.udtbe.domain.admin.dto.request.AdminCastsRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentGetsRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.request.AdminDirectorsGetRequest;
import com.example.udtbe.domain.admin.dto.request.AdminDirectorsRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminMemberListGetRequest;
import com.example.udtbe.domain.admin.dto.request.AdminScheduledContentsRequest;
import com.example.udtbe.domain.admin.dto.request.AdminSinginRequest;
import com.example.udtbe.domain.admin.dto.response.AdminCastsGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminCastsRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentCategoryMetricResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentDeleteResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentUpdateResponse;
import com.example.udtbe.domain.admin.dto.response.AdminDirectorsGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminDirectorsRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminMemberInfoGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminMembersGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentMetricGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentResultResponse;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.dto.CursorPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "관리자 API", description = "관리자 관련 API")
public interface AdminControllerApiSpec {

    @Operation(summary = "콘텐츠 등록", description = "등록할 콘텐츠를 등록 배치 예정 테이블이 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 예정 콘텐츠 registerJobId 반환"),
            @ApiResponse(responseCode = "400", description = "올바르지 않은 분류/플렛폼/장르 타입")
    })
    @PostMapping("/api/admin/contents/registerjob")
    ResponseEntity<AdminContentRegisterResponse> registerContent(
            @AuthenticationPrincipal Member member,
            @Valid @RequestBody AdminContentRegisterRequest adminContentRegisterRequest
    );

    @Operation(summary = "콘텐츠 수정", description = "수정할 콘텐츠를 수정 배치 예정 테이블이 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "수정 예정 콘텐츠 updateJobId 반환"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 콘텐츠"),
            @ApiResponse(responseCode = "400", description = "올바르지 않은 분류/플렛폼/장르 타입")
    })
    @PostMapping("/api/admin/contents/updatejob/{contentId}")
    ResponseEntity<AdminContentUpdateResponse> updateContent(
            @AuthenticationPrincipal Member member,
            @PathVariable(name = "contentId") Long contentId,
            @Valid @RequestBody AdminContentUpdateRequest adminContentUpdateRequest
    );

    @Operation(summary = "콘텐츠 삭제", description = "삭제할 콘텐츠를 삭제 배치 예정 테이블이 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "삭제 예정 콘텐츠 deleteJobId 반환"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 콘텐츠")
    })
    @PostMapping("/api/admin/contents/deletejob/{contentId}")
    ResponseEntity<AdminContentDeleteResponse> deleteContent(
            @AuthenticationPrincipal Member member,
            @PathVariable(name = "contentId") Long contentId
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

    @Operation(summary = "유저 목록 조회", description = "유저 목록 조회합니다.")
    @ApiResponse(responseCode = "200", description = "유저 목록 및 유저별 피드백 합계 정보 반환")
    @GetMapping("/api/admin/users")
    ResponseEntity<CursorPageResponse<AdminMembersGetResponse>> getMemberList(
            @ModelAttribute @Valid AdminMemberListGetRequest adminMemberListGetRequest
    );

    @Operation(summary = "Feedback 통계 전체 동기화", description = "풀 스캔으로 전체 Feedback 통계를 동기화 요청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "풀 스캔 동기화 요청이 정상 접수되었습니다.")
    })
    @PostMapping("/api/admin/feedback/full-scan")
    ResponseEntity<Void> triggerFullScan();

    @Operation(summary = "유저 장르별 피드백 지표 상세 조회", description = "유저의 장르별 피드백 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "유저의 장르별 피드백 정보 반환")
    @GetMapping("/api/admin/users/{userId}/metrics")
    ResponseEntity<AdminMemberInfoGetResponse> getMemberFeedbackInfo(
            @PathVariable(name = "userId") Long userId
    );

    @Operation(summary = "출연진 다건 등록", description = "새로운 출연진들을 다건 등록한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록된 출연진 updateJobId 목록 반환"),
    })
    @PostMapping("/api/admin/casts")
    ResponseEntity<AdminCastsRegisterResponse> registerCasts(
            @Valid @RequestBody AdminCastsRegisterRequest adminCastsRegisterRequest
    );

    @Operation(summary = "출연진 조회", description = "이름으로 출연진을 검색한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이름이 부분|완전 일치한 출연진 목록을 반환"),
    })
    @GetMapping("/api/admin/casts")
    ResponseEntity<CursorPageResponse<AdminCastsGetResponse>> getCasts(
            @Valid @ModelAttribute AdminCastsGetRequest adminCastsGetRequest
    );

    @Operation(summary = "감독 다건 등록", description = "새로운 감독들을 다건 등록한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록된 감독 DirectorId 목록 반환"),
    })
    @PostMapping("/api/admin/directors")
    ResponseEntity<AdminDirectorsRegisterResponse> registerDirectors(
            @Valid @RequestBody AdminDirectorsRegisterRequest adminDirectorsRegisterRequest
    );

    @Operation(summary = "배치 스케쥴러 테스트용 API", description = "콘텐츠 등록/수정/삭제를 배치처리 한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배치 성공")
    })
    @PostMapping("/api/admin/contents/scheduler-test")
    ResponseEntity<Void> schedulerTestContent();

    @Operation(summary = "감독 조회", description = "이름으로 감독을 검색한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이름이 부분|완전 일치한 감독 목록을 반환"),
    })
    @GetMapping("/api/admin/directors")
    ResponseEntity<CursorPageResponse<AdminDirectorsGetResponse>> getDirectors(
            @Valid @ModelAttribute AdminDirectorsGetRequest adminDirectorsGetRequest
    );

    @Operation(summary = "배치 예정 목록", description = "배치 예정 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배치 예정 목록반환"),
    })
    @GetMapping("/api/admin/batch")
    ResponseEntity<CursorPageResponse<AdminScheduledContentResponse>> getBatches(
            @Valid @ModelAttribute AdminScheduledContentsRequest adminContentJobGetsRequest
    );

    @Operation(summary = "배치 결과 목록 조회", description = "배치 집계 결과 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배치 결과 목록반환"),
    })
    @GetMapping("/api/admin/batch/results")
    ResponseEntity<List<AdminScheduledContentResultResponse>> getBatchResults();

    @Operation(summary = "전체 배치 집계 결과 조회", description = "전체 배치 집계 결과를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 배치 집계 조회")
    })

    @GetMapping("/api/admin/batch/metrics")
    ResponseEntity<AdminScheduledContentMetricGetResponse> getBatchMetric();

    @Operation(summary = "콘텐츠 카테고리 지표 조회", description = "콘텐츠 카테고리 별 비율 정보를 가져온다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "콘텐츠 카테고리 별 비율 정보"),
    })
    @GetMapping("/api/admin/metrics/categories")
    ResponseEntity<AdminContentCategoryMetricResponse> getContentCategoryMetric();

    @Operation(summary = "어드민 로그인", description = "백오피스에서 관리자 로그인을 한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "백오피스 관리자 로그인 성공"),
    })
    @PostMapping("/api/admin/signin")
    ResponseEntity<Void> signin(@RequestBody @Valid AdminSinginRequest request,
            HttpServletResponse response);

}
