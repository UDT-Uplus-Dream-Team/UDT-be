package com.example.udtbe.domain.survey.exception;

import org.springframework.http.HttpStatus;

import com.example.udtbe.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SurveyErrorCode implements ErrorCode {

	SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "설문조사를 찾을 수 없습니다."),
	;

	private final HttpStatus httpStatus;
	private final String message;
}
