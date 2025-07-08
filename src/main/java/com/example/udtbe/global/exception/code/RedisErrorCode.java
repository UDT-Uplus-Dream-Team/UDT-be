package com.example.udtbe.global.exception.code;

import com.example.udtbe.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RedisErrorCode implements ErrorCode {

    REDIS_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "저장하지 못했습니다."),
    REDIS_FIND_ERROR(HttpStatus.NOT_FOUND, "값을 찾는 도중 오류가 발생했습니다."),
    REDIS_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "삭제하지 못했습니다."),
    REDIS_EXPIRE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "만료시키지하지 못했습니다."),
    REDIS_EXPIRED_ERROR(HttpStatus.GONE, "만료된 키 입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
