package com.example.udtbe.domain.admin.controller;

import com.example.udtbe.domain.admin.dto.common.ContentDTO;
import com.example.udtbe.domain.admin.dto.request.ContentRegisterRequest;
import com.example.udtbe.domain.admin.dto.request.ContentUpdateRequest;
import com.example.udtbe.domain.admin.dto.response.ContentGetDetailResponse;
import com.example.udtbe.domain.admin.dto.response.ContentRegisterResponse;
import com.example.udtbe.domain.admin.service.AdminService;
import com.example.udtbe.global.dto.CursorPageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/api/admin/contents")
    public ResponseEntity<ContentRegisterResponse> registerContent(
            @RequestBody @Valid ContentRegisterRequest contentRegisterRequest) {

        ContentRegisterResponse contentRegisterResponse = adminService.contentRegister(contentRegisterRequest);
        return ResponseEntity.status(201).body(contentRegisterResponse);
    }

    @PatchMapping("/api/admin/contents/{contentId}")
    public ResponseEntity<Void> updateContent(
            @PathVariable Long contentId,
            @RequestBody @Valid ContentUpdateRequest contentUpdateRequest) {
        adminService.updateContent(contentId, contentUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/admin/contents/{contentId}")
    public ResponseEntity<ContentGetDetailResponse> getContent(
            @PathVariable Long contentId) {

        ContentGetDetailResponse contentGetResponse = adminService.getContent(contentId);
        return ResponseEntity.ok(contentGetResponse);
    }

    @DeleteMapping("/api/admin/contents/{contentId}")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long contentId) {
        adminService.deleteContent(contentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/admin/contents")
    public ResponseEntity<CursorPageResponse<ContentDTO>> getContents(
            @RequestParam(required = false) Long cursor, @RequestParam int size
    ){
        CursorPageResponse<ContentDTO> contentDTOCursorPageResponse = adminService.getContents(cursor, size);
        return ResponseEntity.ok(contentDTOCursorPageResponse);
    }
}
