package com.example.udtbe.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CommonErrorCode implements ErrorCode {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not exists"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Expired token"),
    NOT_EXIST_BEARER_SUFFIX(HttpStatus.BAD_REQUEST, "Bearer prefix is missing."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "Unsupported JWT token"),
    ILLEGAL_TOKEN(HttpStatus.UNAUTHORIZED, "Illegal JWT token"),
    REFRESH_DENIED(HttpStatus.FORBIDDEN, "Refresh denied"),
    REFRESH_NOT_FOUND(HttpStatus.NOT_FOUND, "Refresh not found");

    private final HttpStatus httpStatus;
    private final String message;
}
