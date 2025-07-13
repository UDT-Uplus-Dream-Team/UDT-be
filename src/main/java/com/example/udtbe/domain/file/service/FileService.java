package com.example.udtbe.domain.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.udtbe.domain.file.dto.FileMapper;
import com.example.udtbe.domain.file.dto.response.UploadFilesResponseDto;
import com.example.udtbe.domain.file.exception.FileException;
import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.exception.RestApiException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {

    private final AmazonS3 amazonS3;

    private static final Set<String> ALLOWED_FILE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png",
            ".webp");
    private static final String CONTENT_IMG_DIR = "content/";
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public UploadFilesResponseDto uploadFromTask(List<MultipartFile> files, Member member) {
        if (Objects.isNull(files) || files.isEmpty()) {
            throw new RestApiException(FileException.EMPTY_FILE);
        }

        return FileMapper.toUploadFilesResponseDto(
                this.uploadFiles(files, member, CONTENT_IMG_DIR));
    }

    private List<String> uploadFiles(List<MultipartFile> files, Member member, String dir) {
        List<String> uploadedFileUrls = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String fileExtension = extractFileExtension(file);

            try {
                String subDir = (i == 0) ? "poster/" : "backdrop/";
                uploadedFileUrls.add(
                        this.uploadFilesToS3(file, member, dir + subDir, fileExtension));
            } catch (Exception e) {
                throw new RestApiException(FileException.FAIL_FILE_UPLOAD);
            }
        }

        return uploadedFileUrls;
    }

    private String extractFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (Objects.isNull(fileName) || fileName.isEmpty()) {
            throw new RestApiException(FileException.EMPTY_FILE);
        }

        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) { // 확장자 미존재
            throw new RestApiException(FileException.NO_FILE_EXTENSION);
        }

        String extension = fileName.substring(lastDotIndex).toLowerCase(); // 확장자 추출 (".jpg")
        this.validateFileExtension(extension);

        return extension;
    }

    private void validateFileExtension(String extension) {
        if (!ALLOWED_FILE_EXTENSIONS.contains(extension)) {
            throw new RestApiException(FileException.INVALID_FILE_EXTENSION);
        }
    }

    private String uploadFilesToS3(MultipartFile file, Member member, String dir, String extension)
            throws IOException {
        String fileName = createFileName(dir, member, extension);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(
                new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
        );

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private String createFileName(String dir, Member member, String extension) {
        return new StringBuilder()
                .append(dir)
                .append(member.getId())
                .append("/")
                .append(UUID.randomUUID())
                .append(extension)
                .toString();
    }

}
