package com.example.udtbe.domain.admin.controller;

import com.example.udtbe.domain.admin.dto.common.ContentDTO;
import com.example.udtbe.domain.admin.dto.request.ContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.ContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.response.ContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.ContentRegisterResponse;
import com.example.udtbe.domain.admin.dto.response.ContentUpdateResponse;
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
    public ResponseEntity<ContentRegisterResponse> registerContent(
            ContentRegisterRequest contentRegisterRequest) {

        ContentRegisterResponse contentRegisterResponse = adminService.registerContent(
                contentRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(contentRegisterResponse);
    }

    @Override
    public ResponseEntity<ContentUpdateResponse> updateContent(
            Long contentId, ContentUpdateRequest contentUpdateRequest) {

        ContentUpdateResponse contentUpdateResponse = adminService.updateContent(contentId,
                contentUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(contentUpdateResponse);
    }

    @Override
    public ResponseEntity<ContentGetDetailResponse> getContent(Long contentId) {

        ContentGetDetailResponse contentGetResponse = adminService.getContent(contentId);
        return ResponseEntity.status(HttpStatus.OK).body(contentGetResponse);
    }

    @Override
    public ResponseEntity<Void> deleteContent(Long contentId) {

        adminService.deleteContent(contentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<CursorPageResponse<ContentDTO>> getContents(Long cursor, int size) {

        CursorPageResponse<ContentDTO> contentDTOCursorPageResponse = adminService.getContents(
                cursor, size);
        return ResponseEntity.status(HttpStatus.OK).body(contentDTOCursorPageResponse);
    }
}
