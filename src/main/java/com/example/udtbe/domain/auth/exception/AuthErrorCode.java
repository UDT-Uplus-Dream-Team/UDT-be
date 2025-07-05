package com.example.udtbe.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.example.udtbe.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	;

	private final HttpStatus httpStatus;
	private final String message;
}
