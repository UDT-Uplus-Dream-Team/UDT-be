package com.example.udtbe.global.security.exception;

import com.example.udtbe.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ErrorCode {

    UNAUTHORIZED_USER(HttpStatus.FORBIDDEN, "비인가 사용자 요청입니다."),
    FORBIDDEN_USER(HttpStatus.FORBIDDEN, "USER 권한이 필요합니다."),
    FORBIDDEN_GUEST(HttpStatus.FORBIDDEN, "GUEST 권한이 필요합니다."),
    FORBIDDEN_ADMIN(HttpStatus.FORBIDDEN, "ADMIN 권한이 필요합니다."),
    FORBIDDEN_MISMATCH(HttpStatus.FORBIDDEN, "어떤 권한도 매치되지 않습니다."),
    DENIED_ACCESS(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
