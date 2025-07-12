package com.example.udtbe.domain.file.controller;

import com.example.udtbe.domain.file.dto.response.UploadFilesResponseDto;
import com.example.udtbe.domain.file.service.FileService;
import com.example.udtbe.domain.member.entity.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileController implements FileControllerApiSpec {

    private final FileService fileService;

    @Override
    public ResponseEntity<UploadFilesResponseDto> uploadFromProfile(
            @RequestPart("files") @NotNull @Size(min = 1, max = 3) List<MultipartFile> files,
            @AuthenticationPrincipal Member member) {
        UploadFilesResponseDto response = fileService.uploadFromTask(files, member);
        return ResponseEntity.ok(response);
    }
}
