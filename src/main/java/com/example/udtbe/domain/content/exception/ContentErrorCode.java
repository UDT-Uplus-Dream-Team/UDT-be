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
    PLATFORM_NOT_FOUND(HttpStatus.NOT_FOUND, "플랫폼을 찾을 수 없습니다."),
    CURATED_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "엄선된 추천 콘텐츠를 찾을 수 없습니다."),
    ALREADY_CURATED_CONTENT(HttpStatus.CONFLICT, "이미 저장된 엄선된 콘텐츠입니다."),
    DIRECTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "감독을 찾을 수 없습니다."),
    CAST_NOT_FOUND(HttpStatus.NOT_FOUND, "출연진을 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
