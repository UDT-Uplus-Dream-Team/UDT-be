package com.example.udtbe.domain.batch.exception;

import com.example.udtbe.domain.content.exception.ContentErrorCode;
import com.example.udtbe.global.exception.ErrorCode;
import com.example.udtbe.global.exception.RestApiException;
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
    
    // Skip 대상 에러 (비즈니스 로직 에러)
    BUSINESS_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "비즈니스 유효성 검증 실패"),
    CONTENT_NOT_FOUND_SKIP(HttpStatus.NOT_FOUND, "콘텐츠를 찾을 수 없음 (스킵)"),
    INVALID_DATA_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 데이터 형식"),
    CATEGORY_NOT_FOUND_SKIP(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없음 (스킵)"),
    PLATFORM_NOT_FOUND_SKIP(HttpStatus.NOT_FOUND, "플랫폼을 찾을 수 없음 (스킵)"),
    CAST_NOT_FOUND_SKIP(HttpStatus.NOT_FOUND, "캐스트를 찾을 수 없음 (스킵)"),
    DIRECTOR_NOT_FOUND_SKIP(HttpStatus.NOT_FOUND, "감독을 찾을 수 없음 (스킵)"),
    DUPLICATE_CONTENT(HttpStatus.CONFLICT, "중복된 콘텐츠 (스킵)"),
    
    // Retry 대상 에러 (인프라/일시적 에러)
    DATABASE_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "데이터베이스 연결 실패 (재시도)"),
    DATABASE_TIMEOUT(HttpStatus.SERVICE_UNAVAILABLE, "데이터베이스 타임아웃 (재시도)"),
    EXTERNAL_API_TIMEOUT(HttpStatus.SERVICE_UNAVAILABLE, "외부 API 타임아웃 (재시도)"),
    NETWORK_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "네트워크 오류 (재시도)"),
    TEMPORARY_SYSTEM_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "일시적 시스템 오류 (재시도)"),
    LOCK_ACQUISITION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "락 획득 실패 (재시도)"),
    
    // 재시작 필요 에러 (심각한 시스템 에러)  
    SYSTEM_OUT_OF_MEMORY(HttpStatus.INTERNAL_SERVER_ERROR, "시스템 메모리 부족 (재시작 필요)"),
    DISK_SPACE_FULL(HttpStatus.INTERNAL_SERVER_ERROR, "디스크 공간 부족 (재시작 필요)"),
    SYSTEM_SHUTDOWN(HttpStatus.INTERNAL_SERVER_ERROR, "시스템 종료 (재시작 필요)"),
    CRITICAL_SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "심각한 시스템 오류 (재시작 필요)");


    private final HttpStatus httpStatus;
    private final String message;
    
    public boolean isSkippable() {
        return this.ordinal() >= BUSINESS_VALIDATION_FAILED.ordinal() 
               && this.ordinal() <= DUPLICATE_CONTENT.ordinal();
    }
    
    public boolean isRetryable() {
        return this.ordinal() >= DATABASE_CONNECTION_FAILED.ordinal() 
               && this.ordinal() <= LOCK_ACQUISITION_FAILED.ordinal();
    }
    
    public boolean requiresRestart() {
        return this.ordinal() >= SYSTEM_OUT_OF_MEMORY.ordinal();
    }
    
    public static BatchErrorCode fromException(Exception e) {
        String message = e.getMessage();
        String className = e.getClass().getSimpleName();
        
        // RestApiException의 ContentErrorCode 매핑
        if (e instanceof RestApiException) {
            RestApiException restApiException = (RestApiException) e;
            ErrorCode errorCode = restApiException.getErrorCode();
            
            if (errorCode == ContentErrorCode.CONTENT_NOT_FOUND) {
                return CONTENT_NOT_FOUND_SKIP;
            }
            if (errorCode == ContentErrorCode.CATEGORY_NOT_FOUND || errorCode == ContentErrorCode.GENRE_NOT_FOUND) {
                return CATEGORY_NOT_FOUND_SKIP;
            }
            if (errorCode == ContentErrorCode.PLATFORM_NOT_FOUND) {
                return PLATFORM_NOT_FOUND_SKIP;
            }
            if (errorCode == ContentErrorCode.CAST_NOT_FOUND) {
                return CAST_NOT_FOUND_SKIP;
            }
            if (errorCode == ContentErrorCode.DIRECTOR_NOT_FOUND) {
                return DIRECTOR_NOT_FOUND_SKIP;
            }
            if (errorCode == ContentErrorCode.ALREADY_CURATED_CONTENT) {
                return DUPLICATE_CONTENT;
            }
            
            // 기타 비즈니스 에러
            return BUSINESS_VALIDATION_FAILED;
        }
        
        // 일반적인 에러 매핑
        if (message != null && message.contains("not found")) {
            return CONTENT_NOT_FOUND_SKIP;
        }
        if (message != null && message.contains("duplicate")) {
            return DUPLICATE_CONTENT;
        }
        
        // 인프라 에러 매핑
        if (className.contains("Timeout") || message != null && message.contains("timeout")) {
            return DATABASE_TIMEOUT;
        }
        if (className.contains("Connection") || message != null && message.contains("connection")) {
            return DATABASE_CONNECTION_FAILED;
        }
        if (className.contains("Network") || message != null && message.contains("network")) {
            return NETWORK_ERROR;
        }
        
        // 시스템 에러 매핑
        if (className.contains("OutOfMemory") || message != null && message.contains("memory")) {
            return SYSTEM_OUT_OF_MEMORY;
        }
        
        // 기본값
        return TEMPORARY_SYSTEM_ERROR;
    }
}
