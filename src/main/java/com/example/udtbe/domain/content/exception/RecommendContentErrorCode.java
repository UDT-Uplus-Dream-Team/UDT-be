package com.example.udtbe.domain.content.exception;

import com.example.udtbe.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecommendContentErrorCode implements ErrorCode {

    RECOMMEND_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "추천 콘텐츠를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
