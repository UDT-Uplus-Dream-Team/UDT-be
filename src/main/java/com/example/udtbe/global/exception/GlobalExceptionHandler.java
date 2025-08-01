package com.example.udtbe.global.exception;


import static com.example.udtbe.global.exception.CommonErrorCode.INVALID_PARAMETER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleCustomException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        putLogContext(errorCode); // MDC 등록
        log.warn("[API 예외] {}: {}", errorCode.name(), errorCode.getMessage());
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        ErrorCode errorCode = INVALID_PARAMETER;
        putLogContext(errorCode);
        log.warn("[잘못된 요청] {}", e.getMessage(), e);
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler({java.io.IOException.class})
    public ResponseEntity<Object> handleIOException(java.io.IOException e) {
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        putLogContext(errorCode);
        return handleExceptionInternal(errorCode, " - 검색 엔진 접근 중 오류가 발생했습니다.");
    }

    @ExceptionHandler({org.apache.lucene.queryparser.classic.ParseException.class})
    public ResponseEntity<Object> handleParseException(
            org.apache.lucene.queryparser.classic.ParseException e) {
        ErrorCode errorCode = INVALID_PARAMETER;
        putLogContext(errorCode);
        return handleExceptionInternal(errorCode, " - 검색 조건 처리 중 오류가 발생했습니다.");
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllException(Exception ex) {
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        putLogContext(errorCode);
        log.error("[예기치 못한 에러] {}", ex.getMessage(), ex);
        return handleExceptionInternal(errorCode);
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponseDto(errorCode));
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode,
            String customMessage) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponseDto.builder()
                        .code(errorCode.name())
                        .message(errorCode.getMessage() + customMessage)
                        .build());
    }

    private ErrorResponseDto makeErrorResponseDto(ErrorCode errorCode) {
        return ErrorResponseDto.builder().code(errorCode.name()).message(errorCode.getMessage())
                .build();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        String errMessage;
        if (!ex.getBindingResult().getFieldErrors().isEmpty()) {
            errMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        } else if (!ex.getBindingResult().getGlobalErrors().isEmpty()) {
            errMessage = ex.getBindingResult().getGlobalErrors().get(0).getDefaultMessage();
        } else {
            errMessage = "잘못된 요청입니다.";
        }

        putLogContext(INVALID_PARAMETER);
        log.warn("[검증 실패] {}", errMessage);

        return ResponseEntity.badRequest()
                .body(new ErrorResponseDto("404", errMessage, Collections.emptyList()));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException e,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        log.warn("handleHttpMessageNotReadable", e);
        ErrorCode errorCode = INVALID_PARAMETER;
        if (e.getCause() instanceof MismatchedInputException mismatchedInputException) {
            String fieldName = mismatchedInputException.getPath().isEmpty() ? "unknown"
                    : mismatchedInputException.getPath().get(0).getFieldName();
            return handleExceptionInternal(errorCode, " in field: " + fieldName);
        }
        return handleExceptionInternal(errorCode);
    }

    private void putLogContext(ErrorCode errorCode) {
        MDC.put("status", String.valueOf(errorCode.getHttpStatus().value()));
        MDC.put("errorCode", errorCode.name());
    }

}