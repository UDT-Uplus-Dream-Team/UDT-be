package com.example.udtbe.domain.file.exception;

import com.example.udtbe.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileException implements ErrorCode {

    EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 비어있습니다."),
    NO_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "파일 확장자가 존재하지 않습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원되지 않는 파일 확장자 입니다."),
    FAIL_FILE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}