package com.example.udtbe.domain.file.controller;

import com.example.udtbe.domain.file.dto.response.UploadFilesResponseDto;
import com.example.udtbe.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "파일 API", description = "파일 업로드 관련 API")
public interface FileControllerApiSpec {

    @Operation(summary = "파일 업로드 API", description = "파일을 S3에 업로드 한다.")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping(value = "/api/files/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadFilesResponseDto> uploadFromProfile(
            @RequestPart("files") @NotNull @Size(min = 1, max = 3) List<MultipartFile> files,
            @AuthenticationPrincipal Member member
    );
}
