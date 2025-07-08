package com.example.udtbe.domain.auth.exception;

import com.example.udtbe.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    UNAUTHORIZED_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
    LOGOUT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "로그아웃에 실패했습니다."),
    FAIL_REISSUE_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 재발급에 실패했습니다."),
    MISSING_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "AT가 존재하지 않습니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
