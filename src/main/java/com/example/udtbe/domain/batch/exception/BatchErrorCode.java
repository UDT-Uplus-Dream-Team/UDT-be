package com.example.udtbe.domain.batch.exception;

import com.example.udtbe.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BatchErrorCode implements ErrorCode {

    CURSOR_BAD_REQUEST(HttpStatus.BAD_REQUEST, "올바른 커서를 입력해주세요."),
    ADMIN_CONTENT_JOB_METRIC(HttpStatus.NOT_FOUND, "배치 집계를 찾을 수 없습니다."),
    BATCH_ALREADY_RUNNING(HttpStatus.CONFLICT, "해당 배치 작업이 이미 실행 중입니다."),
    BATCH_RESTART_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "배치 작업 재시작에 실패했습니다."),
    BATCH_ALREADY_COMPLETED(HttpStatus.CONFLICT, "해당 배치 인스턴스가 이미 완료되어 재실행할 수 없습니다."),
    BATCH_INVALID_PARAMETERS(HttpStatus.BAD_REQUEST, "배치 작업에 잘못된 파라미터가 전달되었습니다."),
    SCHEDULER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "스케줄러를 실행할 수 없습니다."),
    ADMIN_CONTENT_REGISTER_JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "등록 배치작업을 찾을 수 없습니다."),
    ADMIN_CONTENT_UPDATE_JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "수정 배치작업을 찾을 수 없습니다."),
    ADMIN_CONTENT_DELETE_JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "삭제 배치 작업이 존재하지 않습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
