package com.example.udtbe.domain.content.exception;

import com.example.udtbe.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ContentErrorCode implements ErrorCode {

    CONTENT_METADATA_NOT_FOUND(HttpStatus.NOT_FOUND, "콘텐츠 메타데이터를 찾을 수 없습니다."),
    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "콘텐츠를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "분류를 찾을 수 없습니다."),
    GENRE_NOT_FOUND(HttpStatus.NOT_FOUND, "장르를 찾을 수 없습니다."),
    PLATFORM_NOT_FOUND(HttpStatus.NOT_FOUND, "플랫폼을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
