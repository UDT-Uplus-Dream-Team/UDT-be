package com.example.udtbe.domain.content.controller;

import com.example.udtbe.domain.content.dto.request.ContentsGetRequest;
import com.example.udtbe.domain.content.dto.response.ContentsGetResponse;
import com.example.udtbe.global.dto.CursorPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "콘텐츠 API", description = "콘텐츠 관련 API")
public interface ContentControllerApiSpec {

    @Operation(summary = "콘텐츠 필터링 목록 조회 API", description = "필터링 조건에 따른 콘텐츠 목록을 가져온다.")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/api/contents")
    public ResponseEntity<CursorPageResponse<ContentsGetResponse>> getContents(
            @ModelAttribute @Valid ContentsGetRequest request
    );
}
