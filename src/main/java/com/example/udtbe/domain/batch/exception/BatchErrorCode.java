package com.example.udtbe.domain.batch.exception;

import com.example.udtbe.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BatchErrorCode implements ErrorCode {

    CURSOR_BAD_REQUEST(HttpStatus.BAD_REQUEST, "옳바른 커서를 입력해주세요."),
    ADMIN_CONTENT_DELETE_JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "삭제 배치 작업이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
