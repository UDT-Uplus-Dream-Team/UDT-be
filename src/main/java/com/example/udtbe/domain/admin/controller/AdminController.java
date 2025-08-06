package com.example.udtbe.domain.admin.controller;

import com.example.udtbe.domain.admin.dto.request.AdminCastsGetRequest;
import com.example.udtbe.domain.admin.dto.request.AdminCastsRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentGetsRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.request.AdminDirectorsGetRequest;
import com.example.udtbe.domain.admin.dto.request.AdminDirectorsRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminMemberListGetRequest;
import com.example.udtbe.domain.admin.dto.request.AdminScheduledContentResultGetsRequest;
import com.example.udtbe.domain.admin.dto.request.AdminScheduledContentsRequest;
import com.example.udtbe.domain.admin.dto.request.AdminSinginRequest;
import com.example.udtbe.domain.admin.dto.response.AdminCastsGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminCastsRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentCategoryMetricResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentDelJobGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentDeleteResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentRegJobGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentUpJobGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentUpdateResponse;
import com.example.udtbe.domain.admin.dto.response.AdminDirectorsGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminDirectorsRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminMemberInfoGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminMembersGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentMetricGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledContentResultGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminScheduledResContentMetricResponse;
import com.example.udtbe.domain.admin.entity.Admin;
import com.example.udtbe.domain.admin.service.AdminAuthService;
import com.example.udtbe.domain.admin.service.AdminService;
import com.example.udtbe.domain.batch.scheduler.AdminScheduler;
import com.example.udtbe.domain.batch.scheduler.FeedbackFullScanScheduler;
import com.example.udtbe.global.dto.CursorPageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminControllerApiSpec {

    private final AdminService adminService;
    private final AdminScheduler adminScheduler;
    private final FeedbackFullScanScheduler feedbackFullScanScheduler;
    private final AdminAuthService adminAuthService;

    @Override
    public ResponseEntity<AdminContentRegisterResponse> registerContent(Admin admin,
            AdminContentRegisterRequest adminContentRegisterRequest) {

        AdminContentRegisterResponse contentRegisterResponse = adminService.registerBulkContent(
                admin, adminContentRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(contentRegisterResponse);
    }

    @Override
    public ResponseEntity<AdminContentUpdateResponse> updateContent(
            Admin admin, Long contentId, AdminContentUpdateRequest adminContentUpdateRequest) {

        AdminContentUpdateResponse contentUpdateResponse = adminService.updateBulkContent(admin,
                contentId, adminContentUpdateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(contentUpdateResponse);
    }

    @Override
    public ResponseEntity<AdminContentDeleteResponse> deleteContent(Admin admin, Long contentId) {

        AdminContentDeleteResponse contentDeleteResponse = adminService.deleteBulkContent(admin,
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
    public ResponseEntity<CursorPageResponse<AdminMembersGetResponse>> getMemberList(
            AdminMemberListGetRequest adminMemberListGetRequest) {
        CursorPageResponse<AdminMembersGetResponse> adminMemberListGetResponse = adminService.getMembers(
                adminMemberListGetRequest);
        return ResponseEntity.status(HttpStatus.OK).body(adminMemberListGetResponse);
    }

    @Override
    public ResponseEntity<Void> triggerFullScan() {
        feedbackFullScanScheduler.scheduleFullScan();
        return ResponseEntity.ok().build();
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
    public ResponseEntity<CursorPageResponse<AdminScheduledContentResponse>> getBatches(
            AdminScheduledContentsRequest adminContentJobGetsRequest) {
        CursorPageResponse<AdminScheduledContentResponse> adminContentJobGetResponseCursorPageResponse = adminService.getBatchJobs(
                adminContentJobGetsRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(adminContentJobGetResponseCursorPageResponse);
    }

    @Override
    public ResponseEntity<AdminContentCategoryMetricResponse> getContentCategoryMetric() {
        AdminContentCategoryMetricResponse response = adminService.getContentCategoryMetric();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<CursorPageResponse<AdminScheduledContentResultGetResponse>> getBatchResults(
            AdminScheduledContentResultGetsRequest request) {
        CursorPageResponse<AdminScheduledContentResultGetResponse> responses = adminService
                .getsScheduledResults(request);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @Override
    public ResponseEntity<AdminScheduledContentMetricGetResponse> getBatchMetric() {

        AdminScheduledContentMetricGetResponse response = adminService.getScheduledMetric();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<AdminContentRegJobGetDetailResponse> getBatchRegJobDetails(Long jobId) {
        AdminContentRegJobGetDetailResponse response = adminService.getBatchRegisterJobDetails(
                jobId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<AdminContentUpJobGetDetailResponse> getBatchUpJobDetails(Long jobId) {
        AdminContentUpJobGetDetailResponse response = adminService.getBatchUpJobDetails(
                jobId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<AdminContentDelJobGetDetailResponse> getBatchDelJobDetails(Long jobId) {
        AdminContentDelJobGetDetailResponse adminContentDelJobGetDetailResponse = adminService.getBatchDelJobDetails(
                jobId);

        return ResponseEntity.status(HttpStatus.OK).body(adminContentDelJobGetDetailResponse);
    }

    @Override
    public ResponseEntity<Void> signin(AdminSinginRequest request, HttpServletResponse response) {
        adminAuthService.signin(request, response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<Void> deleteInvalidBatchJobs() {
        adminService.deleteInvalidBatchJobs();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<AdminScheduledResContentMetricResponse> getScheduledResContentMetric() {
        AdminScheduledResContentMetricResponse response = adminService.getScheduledResContentMetric();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        adminAuthService.logout(request, response);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        adminAuthService.reissue(request, response);
        return ResponseEntity.noContent().build();
    }


}
