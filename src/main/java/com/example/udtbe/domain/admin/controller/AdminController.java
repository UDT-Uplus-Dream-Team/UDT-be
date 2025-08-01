package com.example.udtbe.domain.admin.controller;

import com.example.udtbe.domain.admin.dto.request.AdminCastsGetRequest;
import com.example.udtbe.domain.admin.dto.request.AdminCastsRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentGetsRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.AdminContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.response.AdminCastsGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminCastsRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentGetResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.AdminContentUpdateResponse;
import com.example.udtbe.domain.admin.dto.response.AdminMemberInfoGetResponse;
import com.example.udtbe.domain.admin.service.AdminService;
import com.example.udtbe.global.dto.CursorPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminControllerApiSpec {

    private final AdminService adminService;

    @Override
    public ResponseEntity<AdminContentRegisterResponse> registerContent(
            AdminContentRegisterRequest adminContentRegisterRequest) {

        AdminContentRegisterResponse contentRegisterResponse = adminService.registerContent(
                adminContentRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(contentRegisterResponse);
    }

    @Override
    public ResponseEntity<AdminContentUpdateResponse> updateContent(
            Long contentId, AdminContentUpdateRequest adminContentUpdateRequest) {

        AdminContentUpdateResponse contentUpdateResponse = adminService.updateContent(contentId,
                adminContentUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(contentUpdateResponse);
    }

    @Override
    public ResponseEntity<AdminContentGetDetailResponse> getContent(Long contentId) {

        AdminContentGetDetailResponse contentGetResponse = adminService.getContent(contentId);
        return ResponseEntity.status(HttpStatus.OK).body(contentGetResponse);
    }

    @Override
    public ResponseEntity<Void> deleteContent(Long contentId) {

        adminService.deleteContent(contentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
}
