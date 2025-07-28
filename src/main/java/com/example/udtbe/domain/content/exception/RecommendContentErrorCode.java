package com.example.udtbe.domain.content.exception;

import com.example.udtbe.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecommendContentErrorCode implements ErrorCode {

    RECOMMEND_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "추천 콘텐츠를 찾을 수 없습니다."),
    SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "설문조사를 찾을 수 없습니다."),
    CONTENT_METADATA_CACHE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "콘텐츠 메타데이터 캐시 생성에 실패했습니다."),
    FEEDBACK_RETRIEVAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "피드백 조회에 실패했습니다."),
    CONTENT_BATCH_RETRIEVAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "콘텐츠 일괄 조회에 실패했습니다."),
    POPULAR_CONTENT_RETRIEVAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "인기 콘텐츠 조회에 실패했습니다."),
    INVALID_LIMIT_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 limit 파라미터입니다."),
    RECOMMENDATION_GENERATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "추천 생성에 실패했습니다."),
    CACHE_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "캐시 접근에 실패했습니다."),
    LUCENE_INDEX_NOT_BUILT(HttpStatus.INTERNAL_SERVER_ERROR, "검색 인덱스가 구축되지 않았습니다."),
    LUCENE_SEARCH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "검색 실행에 실패했습니다."),
    DATABASE_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 연결에 실패했습니다."),
    LUCENE_SEARCH_IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lucene 검색 중 입출력 오류가 발생했습니다."),
    LUCENE_SEARCH_PARSE_ERROR(HttpStatus.BAD_REQUEST, "Lucene 검색 쿼리 파싱에 실패했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
