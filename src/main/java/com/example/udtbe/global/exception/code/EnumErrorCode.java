package com.example.udtbe.global.exception.code;

import com.example.udtbe.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EnumErrorCode implements ErrorCode {

    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "역할을 찾을 수 없습니다."),
    GENDER_NOT_FOUND(HttpStatus.NOT_FOUND, "성별을 찾을 수 없습니다."),
    FEEDBACK_TYPE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "피드백 타입을 찾을 수 없습니다."),
    CATEGORY_TYPE_BAD_REQUEST(HttpStatus.NOT_FOUND, "분류 타입을 찾을 수 없습니다."),
    GENRE_TYPE_BAD_REQUEST(HttpStatus.NOT_FOUND, "장르 타입을 찾을 수 없습니다."),
    PLATFORM_TYPE_BAD_REQUEST(HttpStatus.NOT_FOUND, "플랫폼 타입을 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
