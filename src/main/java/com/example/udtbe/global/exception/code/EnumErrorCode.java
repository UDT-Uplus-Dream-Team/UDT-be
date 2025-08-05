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
    FEEDBACK_TYPE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 피드백 타입입니다."),
    CATEGORY_TYPE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 분류 타입입니다."),
    GENRE_TYPE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 장르 타입입니다."),
    PLATFORM_TYPE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 플랫폼 타입입니다."),
    BATCH_FILTER_TYPE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 배치 목록 필터 타입입니다."),
    BATCH_JOB_TYPE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 배치 작업 타입입니다."),
    BATCH_STATUS_BAD_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 배치 상태 타입입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
