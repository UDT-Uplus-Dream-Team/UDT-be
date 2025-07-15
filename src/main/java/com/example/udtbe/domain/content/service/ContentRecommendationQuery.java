package com.example.udtbe.domain.content.service;

import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentMetadata;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.content.exception.RecommendContentErrorCode;
import com.example.udtbe.domain.content.repository.ContentMetadataRepository;
import com.example.udtbe.domain.content.repository.ContentRepository;
import com.example.udtbe.domain.content.repository.FeedbackRepository;
import com.example.udtbe.domain.survey.entity.Survey;
import com.example.udtbe.domain.survey.repository.SurveyRepository;
import com.example.udtbe.global.exception.RestApiException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentRecommendationQuery {

    private final ContentMetadataRepository contentMetadataRepository;
    private final SurveyRepository surveyRepository;
    private final FeedbackRepository feedbackRepository;
    private final ContentRepository contentRepository;

    /**
     * 회원별 설문 조회
     */
    public Survey findSurveyByMemberId(Long memberId) {
        return surveyRepository.findByMemberId(memberId).orElseThrow(
                () -> new RestApiException(RecommendContentErrorCode.SURVEY_NOT_FOUND));
    }

    /**
     * 모든 ContentMetadata 캐시 생성
     */
    public Map<Long, ContentMetadata> findContentMetadataCache() {
        try {
            return contentMetadataRepository.findByIsDeletedFalse()
                    .stream()
                    .filter(metadata -> metadata.getContent() != null)
                    .collect(Collectors.toMap(
                            metadata -> metadata.getContent().getId(),
                            metadata -> metadata
                    ));
        } catch (Exception e) {
            log.error("ContentMetadata 캐시 생성 실패: {}", e.getMessage(), e);
            throw new RestApiException(RecommendContentErrorCode.CONTENT_METADATA_CACHE_ERROR);
        }
    }

    /**
     * 회원별 피드백 조회
     */
    public List<Feedback> findFeedbacksByMemberId(Long memberId) {
        try {
            return feedbackRepository.findByMemberIdAndIsDeletedFalse(memberId);
        } catch (Exception e) {
            log.error("피드백 조회 실패: memberId={}, error={}", memberId, e.getMessage(), e);
            throw new RestApiException(RecommendContentErrorCode.FEEDBACK_RETRIEVAL_ERROR);
        }
    }

    /**
     * ID 목록으로 컨텐츠 조회
     */
    public List<Content> findContentsByIds(List<Long> contentIds) {
        if (contentIds == null || contentIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return contentRepository.findAllById(contentIds);
        } catch (Exception e) {
            log.error("컨텐츠 조회 실패: error={}", e.getMessage(), e);
            throw new RestApiException(RecommendContentErrorCode.CONTENT_BATCH_RETRIEVAL_ERROR);
        }
    }

    /**
     * 인기 컨텐츠 메타데이터 조회 (최신순)
     */
    public List<ContentMetadata> findPopularContentMetadata(int limit) {
        if (limit <= 0) {
            throw new RestApiException(RecommendContentErrorCode.INVALID_LIMIT_PARAMETER);
        }
        try {
            return contentMetadataRepository.findByIsDeletedFalse()
                    .stream()
                    .sorted((m1, m2) -> m2.getCreatedAt().compareTo(m1.getCreatedAt()))
                    .limit(limit)
                    .toList();
        } catch (Exception e) {
            log.error("인기 컨텐츠 메타데이터 조회 실패: error={}", e.getMessage(), e);
            throw new RestApiException(RecommendContentErrorCode.POPULAR_CONTENT_RETRIEVAL_ERROR);
        }
    }

    /**
     * 삭제되지 않은 모든 ContentMetadata 조회
     */
    public List<ContentMetadata> findAllContentMetadata() {
        try {
            return contentMetadataRepository.findByIsDeletedFalse();
        } catch (Exception e) {
            log.error("모든 ContentMetadata 조회 실패: error={}", e.getMessage(), e);
            throw new RestApiException(RecommendContentErrorCode.CONTENT_METADATA_CACHE_ERROR);
        }
    }
}
