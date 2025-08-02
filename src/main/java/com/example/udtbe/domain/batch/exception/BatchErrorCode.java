package com.example.udtbe.domain.batch.exception;

import com.example.udtbe.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BatchErrorCode implements ErrorCode {

    CURSOR_BAD_REQUEST(HttpStatus.BAD_REQUEST, "올바른 커서를 입력해주세요."),
    ADMIN_CONTENT_JOB_METRIC(HttpStatus.NOT_FOUND, "배치 집계를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
