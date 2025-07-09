package com.example.udtbe.domain.content.exception;

import com.example.udtbe.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FeedbackErrorCode implements ErrorCode {
    FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "피드백을 찾을 수 없습니다."),
    FEEDBACK_OWNER_MISSMATCH(HttpStatus.NOT_FOUND, "피드백 작성자가 일치하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
