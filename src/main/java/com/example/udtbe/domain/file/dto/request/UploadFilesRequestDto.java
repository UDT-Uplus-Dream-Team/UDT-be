package com.example.udtbe.domain.file.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record UploadFilesRequestDto(
        @NotNull
        @Size(min = 1, max = 3)
        List<MultipartFile> files
) {

}
