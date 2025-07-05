package com.example.udtbe.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
	REFRESH_NOT_FOUND(HttpStatus.NOT_FOUND, "Refresh not found"),
	RATEPLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "The requested rate plan does not exist."),
	RATEPLAN_DISABLED(HttpStatus.BAD_REQUEST, "The requested rate plan is not available.");

	private final HttpStatus httpStatus;
	private final String message;
}