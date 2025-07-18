package com.example.udtbe.domain.survey.exception;

import com.example.udtbe.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SurveyErrorCode implements ErrorCode {

    SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "설문조사를 찾을 수 없습니다."),
    SURVEY_ALREADY_EXISTS_FOR_MEMBER(HttpStatus.CONFLICT, "이미 설문조사를 완료한 회원입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
