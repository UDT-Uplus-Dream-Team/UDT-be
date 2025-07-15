package com.example.udtbe.domain.file.dto;

import com.example.udtbe.domain.file.dto.request.UploadFilesRequestDto;
import com.example.udtbe.domain.file.dto.response.UploadFilesResponseDto;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileMapper {

    public static List<MultipartFile> toMultipartFiles(
            UploadFilesRequestDto uploadFilesRequestDto) {
        return uploadFilesRequestDto.files();
    }

    public static UploadFilesResponseDto toUploadFilesResponseDto(List<String> uploadedFileUrls) {
        return new UploadFilesResponseDto(uploadedFileUrls);
    }

}
