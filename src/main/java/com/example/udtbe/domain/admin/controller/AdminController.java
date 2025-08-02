package com.example.udtbe.domain.admin.controller;

import com.example.udtbe.domain.admin.dto.request.AdminCastsGetRequest;
import com.example.udtbe.domain.admin.dto.request.AdminCastsRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentGetsRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.request.AdminDirectorsGetRequest;
import com.example.udtbe.domain.admin.dto.request.AdminDirectorsRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminScheduledContentsRequest;
import com.example.udtbe.domain.admin.dto.response.AdminCastsGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminCastsRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentDeleteResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentRegJobGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentUpdateResponse;
import com.example.udtbe.domain.admin.dto.response.AdminDirectorsGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminDirectorsRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminMemberInfoGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentResponse;
import com.example.udtbe.domain.admin.service.AdminService;
import com.example.udtbe.domain.batch.scheduler.AdminScheduler;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.dto.CursorPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminControllerApiSpec {

    private final AdminService adminService;
    private final AdminScheduler adminScheduler;

    @Override
    public ResponseEntity<AdminContentRegisterResponse> registerContent(Member memeber,
            AdminContentRegisterRequest adminContentRegisterRequest) {

        AdminContentRegisterResponse contentRegisterResponse = adminService.registerBulkContent(
                memeber, adminContentRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(contentRegisterResponse);
    }

    @Override
    public ResponseEntity<AdminContentUpdateResponse> updateContent(
            Member member, Long contentId, AdminContentUpdateRequest adminContentUpdateRequest) {

        AdminContentUpdateResponse contentUpdateResponse = adminService.updateBulkContent(member,
                contentId, adminContentUpdateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(contentUpdateResponse);
    }

    @Override
    public ResponseEntity<AdminContentDeleteResponse> deleteContent(Member member, Long contentId) {

        AdminContentDeleteResponse contentDeleteResponse = adminService.deleteBulkContent(member,
                contentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(contentDeleteResponse);
    }

    @Override
    public ResponseEntity<AdminContentGetDetailResponse> getContent(Long contentId) {

        AdminContentGetDetailResponse contentGetResponse = adminService.getContent(contentId);
        return ResponseEntity.status(HttpStatus.OK).body(contentGetResponse);
    }

    @Override
    public ResponseEntity<CursorPageResponse<AdminContentGetResponse>> getContents(
            AdminContentGetsRequest adminContentGetsRequest
    ) {
        CursorPageResponse<AdminContentGetResponse> contentDTOCursorPageResponse = adminService
                .getContents(adminContentGetsRequest);
        return ResponseEntity.status(HttpStatus.OK).body(contentDTOCursorPageResponse);
    }

    @Override
    public ResponseEntity<AdminMemberInfoGetResponse> getMemberFeedbackInfo(Long memberId) {
        AdminMemberInfoGetResponse response = adminService.getMemberFeedbackInfo(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<AdminCastsRegisterResponse> registerCasts(
            AdminCastsRegisterRequest adminCastsRegisterRequest) {
        AdminCastsRegisterResponse adminCastsRegisterResponse = adminService.registerCasts(
                adminCastsRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCastsRegisterResponse);
    }

    @Override
    public ResponseEntity<CursorPageResponse<AdminCastsGetResponse>> getCasts(
            AdminCastsGetRequest adminCastsGetRequest) {
        CursorPageResponse<AdminCastsGetResponse> response =
                adminService.getCasts(adminCastsGetRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<AdminDirectorsRegisterResponse> registerDirectors(
            AdminDirectorsRegisterRequest adminDirectorsRegisterRequest) {
        AdminDirectorsRegisterResponse adminCastsRegisterRequest = adminService.registerDirectors(
                adminDirectorsRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCastsRegisterRequest);
    }

    @Override
    public ResponseEntity<Void> schedulerTestContent() {
        adminScheduler.runContentBatchJob();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<CursorPageResponse<AdminDirectorsGetResponse>> getDirectors(
            AdminDirectorsGetRequest adminDirectorsGetRequest) {
        CursorPageResponse<AdminDirectorsGetResponse> response =
                adminService.getDirectors(adminDirectorsGetRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<CursorPageResponse<AdminScheduledContentResponse>> getBatchJobs(
            AdminScheduledContentsRequest adminContentJobGetsRequest) {
        CursorPageResponse<AdminScheduledContentResponse> adminContentJobGetResponseCursorPageResponse = adminService.getBatchJobs(
                adminContentJobGetsRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(adminContentJobGetResponseCursorPageResponse);
    }

    @Override
    public ResponseEntity<AdminContentRegJobGetDetailResponse> getBatchRegJobDetails(Long jobId) {
        AdminContentRegJobGetDetailResponse response = adminService.getBatchRegJobDetail(
                jobId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
